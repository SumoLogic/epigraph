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
import java.nio.file.{FileAlreadyExistsException, Files, Path}
import java.util
import java.util.Collections
import java.util.concurrent._

import org.slf4s.LoggerFactory
import ws.epigraph.compiler._
import ws.epigraph.java.service._
import ws.epigraph.lang.Qn
import ws.epigraph.schema.ResourcesSchema

import scala.annotation.tailrec
import scala.collection.JavaConversions._
import scala.collection.{JavaConversions, immutable, mutable}

class EpigraphJavaGenerator private(
  val cctx: CContext,
  val javaOutputRoot: Path,
  val resourcesOutputRoot: Path,
  val settings: Settings
) {

  /**
   * Checks if java and resources output roots are nested in each other,
   * and returns true if the directories are the same.
   */
  @throws[IllegalArgumentException]
  private def validateOutputRoots(): Boolean = {
    val javaPath = javaOutputRoot.toAbsolutePath
    val resourcesPath = resourcesOutputRoot.toAbsolutePath
    val samePaths = javaPath == resourcesPath
    if (!samePaths) {
      if (javaPath.startsWith(resourcesPath) || resourcesPath.startsWith(javaPath)) {
        throw new IllegalArgumentException("One of Java or resources output paths may not be nested under the other")
      }
    }
    samePaths
  }

  private val singleOutput = validateOutputRoots()

  private val log = LoggerFactory.apply(this.getClass)

  private val ctx: GenContext = new GenContext(settings)

  // there seems to be a hard to find race in projections codegen, manifesting in
  // sometimes broken builtin-services-service code. Temporarily disabling parallel codegen to help find it
  private val parallel: Boolean = false // ! ctx.settings.debug

  def this(ctx: CContext, javaOutputRoot: File, resourcesOutputRoot: File, settings: Settings) {
    this(ctx, javaOutputRoot.toPath, resourcesOutputRoot.toPath, settings)
  }

  @throws[IOException]
  def generate() {

    def tmpSibling(path: Path): Path = JavaGenUtils.rmrf(
      path.resolveSibling(path.getFileName.toString + "~tmp"), path.getParent
    )

    val tmpJavaRoot: Path = tmpSibling(javaOutputRoot)
    val tmpResourcesRoot: Path = if (singleOutput) tmpJavaRoot else tmpSibling(resourcesOutputRoot)

    // TODO only generate request projection classes if there are any resources defined ?

    val startTime: Long = System.currentTimeMillis

    val generators: mutable.Queue[JavaGen] = mutable.Queue()

    for (schemaFile <- cctx.schemaFiles.values) {
      for (typeDef <- JavaConversions.asJavaIterable(schemaFile.typeDefs)) {

        try {
          typeDef.kind match {

            case CTypeKind.ENTITY =>
              val varTypeDef: CEntityTypeDef = typeDef.asInstanceOf[CEntityTypeDef]
              generators += new EntityTypeGen(varTypeDef, ctx)

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
        } catch {
          case _: CompilerException => // keep going collecting errors
        }

      }
    }

    for (alt <- cctx.anonListTypes.values) {
      try {
        generators += new AnonListGen(alt, ctx)
      } catch {
        case _: CompilerException =>
      }
    }

    for (amt <- cctx.anonMapTypes.values) {
      try {
        generators += new AnonMapGen(amt, ctx)
      } catch {
        case _: CompilerException =>
      }
    }

    runGeneratorsAndHandleErrors(queue(generators), _.writeUnder(tmpJavaRoot, tmpResourcesRoot))
    generators.clear()


//    final Set<CDataType> anonMapValueTypes = new HashSet<>();
//    for (CAnonMapType amt : ctx.anonMapTypes().values()) {
//      anonMapValueTypes.add(amt.valueDataType());
//    }
//    for (CDataType valueType : anonMapValueTypes) {
//      new AnonBaseMapGen(valueType, ctx).writeUnder(tmpJavaRoot, tmpResourcesRoot);
//    }

    if (cctx.schemaFiles.nonEmpty) generators += new IndexGen(ctx)

    // generate server/client stubs
    val serverSettings = ctx.settings.serverSettings()
    val clientSettings = ctx.settings.clientSettings()

    if (serverSettings.generate() || clientSettings.generate()) {

      val serverServicesOpt = Option(serverSettings.services())
      val serverTransformersOpt = Option(serverSettings.transformers())
      val clientServicesOpt = Option(clientSettings.services())

      for (entry <- cctx.resourcesSchemas.entrySet) {
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

        for (transformerDeclaration <- rs.transformers.values) {
          generators += new TransformerDeclarationGen(transformerDeclaration, namespace, ctx)

          val transformerName = namespace.append(transformerDeclaration.name()).toString

          if (serverSettings.generate()) {
            if (serverTransformersOpt.isEmpty || serverTransformersOpt.exists(
              transformers => transformers.contains(
                transformerName
              )
            )) {
              generators += new AbstractTransformerGen(transformerDeclaration, namespace, ctx)
            }
          }
        }
      }

    }

    runGeneratorsAndHandleErrors(queue(generators), _.writeUnder(tmpJavaRoot, tmpResourcesRoot))

    val endTime: Long = System.currentTimeMillis
    log.info(s"Epigraph Java code generation took ${ endTime - startTime }ms")

    if (Files.exists(tmpJavaRoot)) {
      // move new java root to the final location
      JavaGenUtils.move(tmpJavaRoot, javaOutputRoot, javaOutputRoot.getParent)
    }
    if (!singleOutput && Files.exists(tmpResourcesRoot)) {
      // move new resources root to the final location
      JavaGenUtils.move(tmpResourcesRoot, resourcesOutputRoot, resourcesOutputRoot.getParent)
    }

  }

  @tailrec
  private def runGeneratorsAndHandleErrors(
    generators: immutable.Queue[JavaGen],
    runner: JavaGen => Unit): Unit = {

    val doDebugTraces = ctx.settings.debug()
    val generatorsCopy = generators

    // mutable state, should be thread-safe
    val postponedGenerators = new java.util.concurrent.ConcurrentLinkedQueue[JavaGen]()
    val genParents = new java.util.concurrent.ConcurrentHashMap[JavaGen, JavaGen]()
    // will synchronize on this one

    if (parallel) { // run asynchronously
      val executor = Executors.newWorkStealingPool()
      val phaser = new Phaser(1) // active jobs counter + 1

      def submit(generator: JavaGen): Unit = {
        val runStrategy = generator.shouldRunStrategy
        if (runStrategy.checkAndMark) {
          phaser.register()
          executor.submit(
            new Runnable {
              override def run(): Unit = {
                try {
                  runGenerator(generator)(submit)
                } catch {
                  case e: Exception => exceptionHandler(generator, runStrategy, e)
                } finally {
                  phaser.arriveAndDeregister()
                }
              }
            }
          )
        }
      }

      generators.foreach { submit }
//      generators.clear()

      phaser.arriveAndAwaitAdvance()
      executor.shutdown()
      if (!executor.awaitTermination(10, TimeUnit.MINUTES)) throw new RuntimeException("Code generation timeout")

    } else { // run sequentially

      var _generators = generators
      while (_generators.nonEmpty) {
        val (generator, newGenerators) = _generators.dequeue
        _generators = newGenerators

        val runStrategy = generator.shouldRunStrategy
        if (runStrategy.checkAndMark) {
          try {
            runGenerator(generator) { ch => _generators :+= ch }
          } catch {
            case e: Exception => exceptionHandler(generator, runStrategy, e)
          }
        }
      }
    }

    // child -> submit subgens (child.name, child asm)
    // child -> fail, run parent first, postpone child
    // parent -> submit subgens (child!)
    // parent -> run
    // child -> submit subgens again(!!) --> clash
    // child -> run

    def runGenerator(generator: JavaGen)(scheduleGeneratorRun: JavaGen => Unit): Unit = {
      addDebugInfo(generator)

      generator.children.foreach(scheduleGeneratorRun)

      try {
        runner.apply(generator)
      } catch {
        case tle: TryLaterException =>
          val description = generator.description
          log.info(s"Postponing '$description' because: ${ tle.getMessage }")
          generator.shouldRunStrategy.unmark()
          postponedGenerators.add(generator) // run generator on next iteration

          if (tle.extraGeneratorsToRun.nonEmpty) {
            // these are the generators that have to be finished before `generator` is tried again
            // won't postpone them till next run but will include in the current one
            log.info(s"Also including ${ tle.extraGeneratorsToRun.size } generators that have to run first:")
            tle.extraGeneratorsToRun.foreach(g => log.info(g.description))
            tle.extraGeneratorsToRun.foreach(scheduleGeneratorRun)
          }

      }
    }

    def exceptionHandler(generator: JavaGen, runStrategy: ShouldRunStrategy, ex: Exception) = {
      def msg = if (doDebugTraces) {
        val sw = new StringWriter
        sw.append(ex.toString).append("\n")

        sw.append(describeGenerator(generator, showGensProducingSameFile = true))
        if (!ex.isInstanceOf[FileAlreadyExistsException]) { // not interested in these traces
          sw.append("\n")
          ex.printStackTrace(new PrintWriter(sw))
        }
        sw.toString
      } else ex.toString

      log.debug("Unhandled exception", ex)

      cctx.errors.add(CMessage.error(null, CMessagePosition.NA, msg))
    }

    //<editor-fold desc="Debugging stuff">
    def addDebugInfo(gen: JavaGen): Unit = {
      log.debug("Running: " + gen.description)
      if (doDebugTraces) {
        val children = gen.children
        children.foreach { c =>
          try {
            genParents.put(c, gen)
          } catch {
            case _: Exception => // ignore exceptions here
          }
        }
      }
    }

    def describeGenerator(g: JavaGen, showGensProducingSameFile: Boolean): String = {
      val sw = new StringWriter
      val visited = Collections.newSetFromMap(new util.IdentityHashMap[JavaGen, java.lang.Boolean]())
      var sp = ""
      var gen = g
      while (gen != null && !visited.contains(gen)) {
        sw.append(sp)
        sw.append(gen.hashCode().toString).append(" ")
        sw.append(gen.description.replace("\n", "\n" + sp))
        sw.append("\n")
        sp = sp + "  "
        visited.add(gen)
        gen = genParents.getOrElse(gen, null)
      }

      if (showGensProducingSameFile) {
        val samePathGens = genParents.keys.filter(_.relativeFilePath == g.relativeFilePath)
        sw.append("Producing: " + g.relativeFilePath)
        if (samePathGens.nonEmpty) {
          sw.append("\n  also produced by: \n")
          samePathGens.foreach(t => sw.append(describeGenerator(t, showGensProducingSameFile = false)))
        }
      }
      sw.toString
    }
    //</editor-fold>

    // run postponed generators, if any
    // some of the postponed generators may have been executed by concurrent threads, so check again here
    val postponedNotRun = postponedGenerators.toSeq.filter(gp => gp.shouldRunStrategy.check)
    postponedGenerators.clear()
    val postponedGeneratorsSize = postponedNotRun.size
    if (postponedGeneratorsSize > 0) {
      //noinspection ComparingUnrelatedTypes  (should actually be OK?)
      if (generatorsCopy == postponedNotRun) { // couldn't make any progress, abort
        val msg = new StringWriter()
        msg.append("The following generators couldn't finish:\n")
        postponedNotRun.foreach(
          sg => msg.append(describeGenerator(sg, showGensProducingSameFile = true)).append("\n")
        )
        cctx.errors.add(CMessage.error(null, CMessagePosition.NA, msg.toString))

        handleErrors()
      } else {
        log.info(s"Retrying $postponedGeneratorsSize generators")
        val postponedQueue = queue(postponedNotRun)
        // don't schedule children to run again on retries (have already been scheduled during this run)
        runGeneratorsAndHandleErrors(postponedQueue, runner)
      }
    } else {
      handleErrors()
    }

  }

  private def queue[T](i: Iterable[T]): immutable.Queue[T] = {
    val q = immutable.Queue[T]()
    q ++ i
  }

  private def handleErrors() {
    if (!cctx.errors.isEmpty) {
      EpigraphCompiler.renderErrors(cctx)
      throw new Exception("Build failed") // todo better integration with mvn/gradle/...
    }
  }

}
