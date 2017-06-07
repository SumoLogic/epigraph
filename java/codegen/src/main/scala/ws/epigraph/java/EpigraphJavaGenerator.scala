/*
 * Copyright 2017 Sumo Logic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* Created by yegor on 7/6/16. */
package ws.epigraph.java

import java.io.{File, IOException, PrintWriter, StringWriter}
import java.nio.file.{Files, Path}
import java.util.concurrent._

import ws.epigraph.compiler._
import ws.epigraph.java.service.{AbstractResourceFactoryGen, ResourceClientGen, ResourceDeclarationGen}
import ws.epigraph.lang.Qn
import ws.epigraph.schema.ResourcesSchema

import scala.collection.JavaConversions._
import scala.collection.{JavaConversions, mutable}

class EpigraphJavaGenerator(val cctx: CContext, val outputRoot: Path, val settings: Settings) {
  private val ctx: GenContext = new GenContext(settings)

  def this(ctx: CContext, outputRoot: File, settings: Settings) {
    this(ctx, outputRoot.toPath, settings)
  }

  @throws[IOException]
  def generate() {
    val tmpRoot: Path = JavaGenUtils.rmrf(
      outputRoot.resolveSibling(outputRoot.getFileName.toString + "~tmp"),
      outputRoot.getParent
    )
//    for (CNamespace namespace : ctx.namespaces().values()) {
//      new NamespaceGen(namespace, ctx).writeUnder(tmpRoot);
//    }

    // TODO only generate request projection classes if there are any resources defined ?

    val startTime: Long = System.currentTimeMillis

    val generators: mutable.Queue[JavaGen] = mutable.Queue()

    for (schemaFile <- cctx.schemaFiles.values) {
      for (typeDef <- JavaConversions.asJavaIterable(schemaFile.typeDefs)) {

        try {
          typeDef.kind match {

            case CTypeKind.ENTITY =>
              val varTypeDef: CVarTypeDef = typeDef.asInstanceOf[CVarTypeDef]
              generators += new VarTypeGen(varTypeDef, ctx)

            case CTypeKind.RECORD =>
              val recordTypeDef: CRecordTypeDef = typeDef.asInstanceOf[CRecordTypeDef]
              generators += new RecordGen(recordTypeDef, ctx)

            case CTypeKind.MAP =>
              val mapTypeDef: CMapTypeDef = typeDef.asInstanceOf[CMapTypeDef]
              generators += new NamedMapGen(mapTypeDef, ctx)

            case CTypeKind.LIST =>
              val listTypeDef: CListTypeDef = typeDef.asInstanceOf[CListTypeDef]
              generators += new NamedListGen(listTypeDef, ctx)

            case CTypeKind.ENUM |
                 CTypeKind.STRING |
                 CTypeKind.INTEGER |
                 CTypeKind.LONG |
                 CTypeKind.DOUBLE |
                 CTypeKind.BOOLEAN =>
              generators += new PrimitiveGen(typeDef.asInstanceOf[CPrimitiveTypeDef], ctx)

            case _ =>
              throw new UnsupportedOperationException(typeDef.kind.toString)
          }
        }
        catch {
          case _: CompilerException => // keep going collecting errors
        }

      }
    }

    for (alt <- cctx.anonListTypes.values) {
      try {
        //System.out.println(alt.name().name());
        generators += new AnonListGen(alt, ctx)
      }
      catch {
        case _: CompilerException =>
      }
    }

    for (amt <- cctx.anonMapTypes.values) {
      try {
        //System.out.println(amt.name().name());
        generators += new AnonMapGen(amt, ctx)
      }
      catch {
        case _: CompilerException =>
      }
    }

    runGeneratorsAndHandleErrors(generators, _.writeUnder(tmpRoot))


//    final Set<CDataType> anonMapValueTypes = new HashSet<>();
//    for (CAnonMapType amt : ctx.anonMapTypes().values()) {
//      anonMapValueTypes.add(amt.valueDataType());
//    }
//    for (CDataType valueType : anonMapValueTypes) {
//      new AnonBaseMapGen(valueType, ctx).writeUnder(tmpRoot);
//    }

    if (cctx.schemaFiles.nonEmpty)
      generators += new IndexGen(ctx)

    // generate server/client stubs
    val serverSettings = ctx.settings.serverSettings()
    val clientSettings = ctx.settings.clientSettings()

    if (serverSettings.generate() || clientSettings.generate()) {

      val serverServicesOpt = Option(serverSettings.services())
      val clientServicesOpt = Option(clientSettings.services())

      for (entry <- cctx.resourcesSchemas.entrySet) {
        // todo: check that there are no duplicate resource declarations
        val rs: ResourcesSchema = entry.getValue
        val namespace: Qn = rs.namespace

        for (resourceDeclaration <- rs.resources.values) {
          // resource declarations (mostly op projections) are used by both server and client
          generators += new ResourceDeclarationGen(resourceDeclaration, namespace, ctx)

          val resourceName: String = namespace.append(resourceDeclaration.fieldName).toString

          // server stub
          // change them to be patters/regex?
          if (serverSettings.generate()) {
            if (serverServicesOpt.isEmpty || serverServicesOpt.exists(services => services.contains(resourceName))) {
              generators += new AbstractResourceFactoryGen(resourceDeclaration, namespace, ctx)
            }
          }

          // client
          if (clientSettings.generate()) {
            if (clientServicesOpt.isEmpty || clientServicesOpt.exists(services => services.contains(resourceName))) {
              generators += new ResourceClientGen(resourceDeclaration, namespace, ctx)
            }
          }
        }
      }

    }

    runGeneratorsAndHandleErrors(generators, _.writeUnder(tmpRoot))

    val endTime: Long = System.currentTimeMillis
    System.out.println(s"Epigraph Java code generation took ${ endTime - startTime }ms")

    if (Files.exists(tmpRoot))
      JavaGenUtils.move(tmpRoot, outputRoot, outputRoot.getParent)// move new root to final location
  }

  private def runGeneratorsAndHandleErrors(generators: mutable.Queue[JavaGen], runner: JavaGen => Unit): Unit = {
    def exceptionHandler(e: Exception) = {
      def msg = if (ctx.settings.debug) {
        val sw = new StringWriter
        e.printStackTrace(new PrintWriter(sw))
        sw.toString
      } else e.toString

      cctx.errors.add(CError(null, CErrorPosition.NA, msg))
    }

    if (ctx.settings.debug) {
      // run sequentially
      while (generators.nonEmpty) {
        val generator: JavaGen = generators.dequeue()
        if (generator.shouldRun) {
          try {
            generators ++= generator.children
            runner.apply(generator)
          } catch {
            case e: Exception => exceptionHandler(e)
          }
        }
      }
    } else {
      // run asynchronously
      val executor = Executors.newWorkStealingPool()
      val phaser = new Phaser(1) // active jobs counter + 1

      def submit(generator: JavaGen): Unit = {
        if (generator.shouldRun) {
          phaser.register()
          executor.submit(
            new Runnable {
              override def run(): Unit = {
                try {
                  generator.children.foreach(submit)
                  runner.apply(generator)
                } catch {
                  case e: Exception => exceptionHandler(e)
                } finally {
                  phaser.arriveAndDeregister()
                }
              }
            }
          )
        }
      }

      generators.foreach { submit }
      generators.clear()

      phaser.arriveAndAwaitAdvance()
      executor.shutdown()
      if (!executor.awaitTermination(10, TimeUnit.MINUTES)) throw new RuntimeException("Code generation timeout")
    }

    handleErrors()
  }

//  public static void main(String... args) throws IOException {
//    new EpigraphJavaGenerator(
//        SchemaCompiler.testcompile(),
//        Paths.get("java/codegen-test/src/main/java")
//    ).generate();
//  }

  private def handleErrors() {
    if (!cctx.errors.isEmpty) {
      EpigraphCompiler.renderErrors(cctx)
      throw new Exception("Build failed") // todo better integration with mvn/gradle/...
      //System.exit(10)
    }
  }

}