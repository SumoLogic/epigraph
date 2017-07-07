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

package ws.epigraph.java.service.projections.req

import ws.epigraph.compiler.CDatumType
import ws.epigraph.java.JavaGenNames.lqn2
import ws.epigraph.java.NewlineStringInterpolator.NewlineHelper
import ws.epigraph.java.service.projections.req.ReqTypeProjectionGen._
import ws.epigraph.java.{JavaGen, JavaGenUtils}
import ws.epigraph.java.JavaGenUtils.{lo, toCType}
import ws.epigraph.lang.Qn
import ws.epigraph.projections.op.AbstractOpModelProjection
import ws.epigraph.types.DatumTypeApi

import scala.collection.JavaConversions._

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
trait ReqModelProjectionGen extends ReqTypeProjectionGen {
  override type OpProjectionType <: AbstractOpModelProjection[_, _, _ <: DatumTypeApi]
  type OpMetaProjectionType <: AbstractOpModelProjection[_, _, _ <: DatumTypeApi]

  override protected val cType: CDatumType = JavaGenUtils.toCType(op.`type`())

  protected def reqVarProjectionFqn: Qn

  protected def reqModelProjectionFqn: Qn

  protected def reqModelProjectionParams: String

  protected def metaGenerator(metaOp: OpMetaProjectionType): ReqProjectionGen =
    throw new RuntimeException("meta projections not supported")

  protected def tailGenerator(parentGen: ReqModelProjectionGen, op: OpProjectionType, normalized: Boolean): ReqModelProjectionGen =
    throw new RuntimeException("tail projections not supported")

  // -----------

  protected def genShortClassName(prefix: String, suffix: String): String = genShortClassName(prefix, suffix, cType)

  override def children: Iterable[JavaGen] =
    super.children ++ metaGeneratorOpt.iterator ++ tailGenerators.values ++ normalizedTailGenerators.values

  protected lazy val params: CodeChunk =
    ReqProjectionGen.generateParams(op.params(), namespace.toString, "raw.params()")

  protected lazy val metaGeneratorOpt: Option[ReqProjectionGen] = {
    val metaOp: OpMetaProjectionType = op.metaProjection().asInstanceOf[OpMetaProjectionType]
    Option(metaOp).map(metaGenerator)
  }

  protected lazy val meta: CodeChunk = metaGeneratorOpt match {
    case Some(g) => CodeChunk(/*@formatter:off*/sn"""\
  public @Nullable ${g.fullClassName} meta() {
    return raw.metaProjection() == null ? null : new ${g.fullClassName} (raw.metaProjection());
  }
"""/*@formatter:on*/ , Set("org.jetbrains.annotations.Nullable")
    )
    case None => CodeChunk.empty
  }

  protected val buildTails = true

  protected val buildNormalizedTails = true

  protected lazy val tails: CodeChunk = if (!buildTails) CodeChunk.empty else tailGenerators
    .map { case (tail, gen) => genTail(tail, gen) }
    .foldLeft(CodeChunk.empty)(_ + _)

  protected lazy val normalizedTails: CodeChunk = if (!buildNormalizedTails) CodeChunk.empty else normalizedTailGenerators
    .map { case (tail, gen) => genNormalizedTail(tail, gen) }
    .foldLeft(CodeChunk.empty)(_ + _)

  protected lazy val tailGenerators: Map[OpProjectionType, ReqModelProjectionGen] =
    Option(op.polymorphicTails()).map(
      _.asInstanceOf[java.util.List[OpProjectionType]] // can't set SMP to OpProjectionType, Scala doesn't allow cyclic types
        .map { t: OpProjectionType => t -> tailGenerator(this, t, normalized = false) }.toMap
    ).getOrElse(Map())

  lazy val normalizedTailGenerators: Map[OpProjectionType, ReqModelProjectionGen] =
    Option(op.polymorphicTails()).map(
      _.asInstanceOf[java.util.List[OpProjectionType]].map { t: OpProjectionType =>
        t -> tailGenerator(this, op.normalizedForType(t.`type`()).asInstanceOf[OpProjectionType], normalized = true)
      }.toMap
    ).getOrElse(Map())

  private def genTail(tail: OpProjectionType, tailGenerator: ReqModelProjectionGen): CodeChunk = {
    val tailCtype = JavaGenUtils.toCType(tail.`type`())
    CodeChunk(
      /*@formatter:off*/sn"""\
  /**
   * @return ${JavaGenUtils.javadocLink(tailCtype, namespace)} tail projection
   */
  public @Nullable ${tailGenerator.fullClassName} ${tailMethodPrefix(false)}${typeNameToMethodName(tailCtype)}${tailMethodSuffix(false)}() {
    ${reqModelProjectionFqn.last()} tail = raw.tailByType(${lqn2(tailCtype, namespace.toString)}.Type.instance());
    return tail == null ? null : new ${tailGenerator.fullClassName}(tail);
  }
"""/*@formatter:on*/ ,
      Set(
        "org.jetbrains.annotations.Nullable"
      )
    )
  }

  def genNormalizedTail(tail: OpProjectionType, tailGenerator: ReqModelProjectionGen): CodeChunk = {
    val tailCtype = JavaGenUtils.toCType(tail.`type`())
    val tailTypeExpr = lqn2(tailCtype, namespace.toString)
    CodeChunk(
      /*@formatter:off*/sn"""\
  /**
   * @return model projection normalized for ${JavaGenUtils.javadocLink(tailCtype, namespace)} type
   *
   * @see <a href="https://github.com/SumoLogic/epigraph/wiki/polymorphic-tails#normalized-projections">normalized projections</a>
   */
  public @NotNull ${tailGenerator.fullClassName} ${tailMethodPrefix(true)}${typeNameToMethodName(tailCtype)}${tailMethodSuffix(true)}() {
    return new ${tailGenerator.fullClassName}(raw.normalizedForType($tailTypeExpr.Type.instance()));
  }
"""/*@formatter:on*/ ,
      Set()
    )
  }


  //                                                                                                   dispatcher code
  protected lazy val dispatcher: CodeChunk = if (normalizedTailGenerators.isEmpty) CodeChunk.empty else new CodeChunk(
    /*@formatter:off*/sn"""\
  public static final @NotNull Dispatcher dispatcher = Dispatcher.INSTANCE;

  public static final class Dispatcher {
    static final Dispatcher INSTANCE = new Dispatcher();

    private Dispatcher() {}

    public <T> T dispatch(
      @NotNull $shortClassName projection,
      @NotNull Type actualType,
${normalizedTailGenerators.map{
  case (t,g) => s"      @NotNull Function<${g.fullClassName}, T> ${lo(typeNameToMethodName(toCType(t.`type`())))}Producer,"
}.mkString("\n")}
      @NotNull Supplier<T> _default) {

${normalizedTailGenerators.map{ case (t,g) =>
  s"if (actualType.equals(${lqn2(toCType(t.`type`()), namespace.toString)}.Type.instance()))\n" +
  s"        return ${lo(typeNameToMethodName(toCType(t.`type`())))}Producer.apply(new ${g.fullClassName}(projection.raw.normalizedForType(${lqn2(toCType(t.`type`()), namespace.toString)}.Type.instance())));"
}.mkString("      ","\n      else ","")}
      else
        return _default.get();
    }

    public void dispatch(
      @NotNull $shortClassName projection,
      @NotNull Type actualType,
${normalizedTailGenerators.map{
  case (t,g) => s"      @NotNull Consumer<${g.fullClassName}> ${lo(typeNameToMethodName(toCType(t.`type`())))}Consumer,"
}.mkString("\n")}
      @NotNull Runnable _default) {

${normalizedTailGenerators.map{ case (t,g) =>
  s"if (actualType.equals(${lqn2(toCType(t.`type`()), namespace.toString)}.Type.instance()))\n" +
  s"        ${lo(typeNameToMethodName(toCType(t.`type`())))}Consumer.accept(new ${g.fullClassName}(projection.raw.normalizedForType(${lqn2(toCType(t.`type`()), namespace.toString)}.Type.instance())));"
}.mkString("      ","\n      else ", "")}
      else
        _default.run();
    }
  }\n"""/*@formatter:on*/ ,
    Set("ws.epigraph.types.Type", "java.util.function.Function", "java.util.function.Supplier", "java.util.function.Consumer")
  )

  protected def classJavadoc =/*@formatter:off*/sn"""\
/**
 * Request output projection for ${JavaGenUtils.javadocLink(cType, namespace)} type
 */
"""/*@formatter:on*/

}
