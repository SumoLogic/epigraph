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

import java.io.{File, IOException}
import java.nio.file.Path
import java.util.concurrent._

import ws.epigraph.compiler._
import ws.epigraph.java.service.projections.req.OperationInfo
import ws.epigraph.java.service.projections.req.delete.ReqDeleteFieldProjectionGen
import ws.epigraph.java.service.projections.req.input.ReqInputFieldProjectionGen
import ws.epigraph.java.service.projections.req.output.ReqOutputFieldProjectionGen
import ws.epigraph.java.service.projections.req.path.ReqPathFieldProjectionGen
import ws.epigraph.java.service.projections.req.update.ReqUpdateFieldProjectionGen
import ws.epigraph.java.service.{AbstractReadOperationGen, AbstractResourceFactoryGen, ResourceDeclarationGen}
import ws.epigraph.lang.Qn
import ws.epigraph.schema.ResourcesSchema
import ws.epigraph.schema.operations.{DeleteOperationDeclaration, OperationKind, ReadOperationDeclaration}

import scala.collection.JavaConversions._
import scala.collection.{JavaConversions, mutable}

class EpigraphJavaGenerator(val cctx: CContext, val outputRoot: Path, val settings: GenSettings) {

  private val ctx: GenContext = new GenContext(settings)

  def this(ctx: CContext, outputRoot: File, settings: GenSettings) {
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

            case CTypeKind.VARTYPE =>
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

    runGenerators(generators, _.writeUnder(tmpRoot))
    handleErrors()


//    final Set<CDataType> anonMapValueTypes = new HashSet<>();
//    for (CAnonMapType amt : ctx.anonMapTypes().values()) {
//      anonMapValueTypes.add(amt.valueDataType());
//    }
//    for (CDataType valueType : anonMapValueTypes) {
//      new AnonBaseMapGen(valueType, ctx).writeUnder(tmpRoot);
//    }

    generators += new IndexGen(ctx)

    val settings: GenSettings = ctx.settings

    for (entry <- cctx.resourcesSchemas.entrySet) {
      val rs: ResourcesSchema = entry.getValue
      val namespace: Qn = rs.namespace

      for (resourceDeclaration <- rs.resources.values) {
        generators += new ResourceDeclarationGen(resourceDeclaration, namespace, ctx)

        val resourceName: String = namespace.append(JavaGenUtils.up(resourceDeclaration.fieldName)).toString

        // change them to be patters/regex?
        if (settings.generateImplementationStubs == null ||
            settings.generateImplementationStubs.contains(resourceName)) {

          generators += new AbstractResourceFactoryGen(resourceDeclaration, namespace, ctx)

//          for (operationDeclaration <- resourceDeclaration.operations) {
//
//            val operationInfo: OperationInfo = OperationInfo(
//              namespace,
//              resourceDeclaration.fieldName,
//              operationDeclaration
//            )
//
////            val outputFieldProjectionGen = new ReqOutputFieldProjectionGen(
////              operationInfo,
////              resourceDeclaration.fieldName,
////              operationDeclaration.outputProjection,
////              Qn.EMPTY,
////              ctx
////            )
////
////            generators += outputFieldProjectionGen
//
//
//            operationDeclaration.kind match {
//
//              case OperationKind.READ =>
//                generators += new AbstractReadOperationGen(
//                  operationInfo.resourceNamespace,
//                  resourceDeclaration,
//                  operationDeclaration.asInstanceOf[ReadOperationDeclaration],
//                  ctx
//                )
//
//              case OperationKind.CREATE =>
//                generators += new ReqInputFieldProjectionGen(
//                  operationInfo,
//                  resourceDeclaration.fieldName,
//                  operationDeclaration.inputProjection,
//                  Qn.EMPTY,
//                  ctx
//                )
//
//              case OperationKind.UPDATE =>
//                generators += new ReqUpdateFieldProjectionGen(
//                  operationInfo,
//                  resourceDeclaration.fieldName,
//                  operationDeclaration.inputProjection,
//                  Qn.EMPTY,
//                  ctx
//                )
//
//              case OperationKind.DELETE =>
//                generators += new ReqDeleteFieldProjectionGen(
//                  operationInfo,
//                  resourceDeclaration.fieldName,
//                  operationDeclaration.asInstanceOf[DeleteOperationDeclaration].deleteProjection(),
//                  Qn.EMPTY,
//                  ctx
//                )
//
//              case OperationKind.CUSTOM =>
//                generators += new ReqInputFieldProjectionGen(
//                  operationInfo,
//                  resourceDeclaration.fieldName,
//                  operationDeclaration.inputProjection,
//                  Qn.EMPTY,
//                  ctx
//                )
//
//              case _ =>
//                throw new RuntimeException(
//                  s"Unknown operation '${operationDeclaration.name}' kind '${operationDeclaration.kind}'"
//                )
//            }
//          }

        }
      }
    }

    runGenerators(generators, _.writeUnder(tmpRoot))
    handleErrors()

    val endTime: Long = System.currentTimeMillis

    System.out.println(s"Epigraph Java code generation took ${endTime - startTime}ms")


    JavaGenUtils.move(tmpRoot, outputRoot, outputRoot.getParent)// move new root to final location
  }

  private def runGenerators(generators: mutable.Queue[JavaGen], runner: JavaGen => Unit): Unit = {
    if (generators.size() < 5) { // todo find correct break-even point
      // run sequentially
      while (generators.nonEmpty) {
        val generator: JavaGen = generators.dequeue()
        generators ++= generator.children
        runner.apply(generator)
      }
    } else {
      // run asynchronously
      val executor = Executors.newWorkStealingPool()
      val phaser = new Phaser(1)

      def submit(generator: JavaGen): Unit = {
        phaser.register()
        executor.submit(
          new Runnable {
            override def run(): Unit = {
              try {
                generator.children.foreach(submit)
                runner.apply(generator)
              } catch {
                case e: Exception => cctx.errors.add(CError(null, CErrorPosition.NA, e.toString))
              } finally {
                phaser.arriveAndDeregister()
              }
            }
          }
        )
      }

      generators.foreach{ submit }
      generators.clear()

      phaser.arriveAndAwaitAdvance()
      executor.shutdown()
      if (!executor.awaitTermination(10, TimeUnit.MINUTES)) throw new RuntimeException("Code generation timeout")
    }
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
      System.exit(10)
    }
  }

}