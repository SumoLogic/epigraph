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

import ws.epigraph.compiler.{CEntityTypeDef, CTag, CTagApiWrapper}
import ws.epigraph.java.JavaGenNames.{jn, ln, lqn2, ttr}
import ws.epigraph.java.JavaGenUtils.TraversableOnceToListMapObject.TraversableOnceToListMap
import ws.epigraph.java.JavaGenUtils.{lo, toCType}
import ws.epigraph.java.NewlineStringInterpolator.NewlineHelper
import ws.epigraph.java.{JavaGen, JavaGenUtils}
import ws.epigraph.lang.Qn
import ws.epigraph.projections.gen.{GenTagProjectionEntry, GenEntityProjection}

import scala.collection.JavaConversions._

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
trait ReqEntityProjectionGen extends ReqTypeProjectionGen {
  override type OpProjectionType <: GenEntityProjection[OpProjectionType, OpTagProjectionEntryType, _]
  type OpTagProjectionEntryType <: GenTagProjectionEntry[OpTagProjectionEntryType, _]
  override protected type GenType <: ReqEntityProjectionGen

  protected def tagGenerator(pgo: Option[ReqEntityProjectionGen], tpe: OpTagProjectionEntryType): ReqProjectionGen

  protected def tailGenerator(op: OpProjectionType, normalized: Boolean): ReqProjectionGen

  // -----------

  protected def genShortClassName(prefix: String, suffix: String): String = genShortClassName(prefix, suffix, cType)

  override def children: Iterable[JavaGen]
  = {
      if (invalidParentClassGenerator)
        Iterable()
      else
        tagGenerators.values
    } ++
    /*tailGenerators.values ++*/
    normalizedTailGenerators/*.filterKeys(p => p.referenceName() == null)*/ .values // filter out named generators

  override protected val cType: CEntityTypeDef = toCType(op.`type`()).asInstanceOf[CEntityTypeDef]

  /**
   * Tag projections: should only include new or overridden tags, should not include inherited.
   * Maps tag names to (generatorOpt, projection) pairs, where generatorOpt contains generator of the
   * overriden tag, or None if field is not overriding anything
   *
   * @param t overriding type
   */
  private def tagProjections(
    g: ReqEntityProjectionGen,
    t: CEntityTypeDef): Map[String, (Option[ReqEntityProjectionGen], OpTagProjectionEntryType)] = {
    val op = g.op

//    val parentOverridingTagProjections = g.parentClassGenOpt.map(
//      pg => tagProjections(
//        pg.asInstanceOf[ReqEntityProjectionGen], t
//      )
//    ).getOrElse(Map())

    val gOverridingTagProjections = op.tagProjections().toSeq.toListMap
        .filter { case (tn, tp) =>
          // only keep overriden tags of compatible types
          t.findEffectiveTag(tn).exists { tag =>
            val tagType = new CTagApiWrapper(tag).`type`
            tagType != tp.tag().`type`() && tp.tag().`type`().isAssignableFrom(tagType)
          }
        }
        .map { case (tn, tp) =>
          // convert overriden tags to be of proper type.
          tn -> (
              Some(g),
              tp.overridenTagProjection(new CTagApiWrapper(t.findEffectiveTag(tn).get)).asInstanceOf[OpTagProjectionEntryType]
          )
        }

//    parentOverridingTagProjections ++ gOverridingTagProjections
    gOverridingTagProjections
  }

  /** tag projections: should only include new or overridden tags, should not include inherited */
  lazy val tagProjections: Map[String, (Option[ReqEntityProjectionGen], OpTagProjectionEntryType)] =
    op.tagProjections().toSeq.toListMap
        .filterKeys(tn => !parentClassGenOpt.exists(_.tagProjections.contains(tn)))
        .mapValues(p => (None, p)) ++
    parentClassGenOpt.map(
      pg => tagProjections(
        pg, cType
      )
    ).getOrElse(Map())

  /** tag generators: should only include new or overridden tags, should not include inherited */
  lazy val tagGenerators: Map[CTag, ReqProjectionGen] =
    if (invalidParentClassGenerator) // don't produce (invalid) tags if generator is broken, will be recreated & retried
      Map()
    else
      tagProjections.values.map { case (pgo, tpe) =>
        findTag(tpe.tag().name()) -> tagGenerator(pgo, tpe)
      }.toListMap

  protected def findTag(name: String): CTag = cType.effectiveTags.find(_.name == name).getOrElse {
    throw new RuntimeException(s"Can't find tag '$name' in type '${ cType.name.toString }'")
  }

  def findTagGenerator(name: String): Option[ReqProjectionGen] = tagGenerators.find(_._1.name == name).map(_._2)

  def isInherited(tagName: String): Boolean = parentClassGenOpt.exists { pg =>
    pg.tagProjections.contains(tagName) || pg.isInherited(tagName)
  }

//  protected lazy val tailGenerators: Map[OpProjectionType, ReqProjectionGen] =
//    Option(op.polymorphicTails()).map(
//      _.map { t: OpProjectionType => t -> tailGenerator(t, normalized = false) }.toMap
//    ).getOrElse(Map())

  lazy val normalizedTailGenerators: Map[OpProjectionType, ReqProjectionGen] =
    Option(op.polymorphicTails()).map(
      _.map { t: OpProjectionType =>
        t -> tailGenerator(op.normalizedForType(t.`type`()), normalized = true)
      }.toListMap
    ).getOrElse(Map())


  override def description: String = "[E] " + super.description

  def tagMethodName(tagName: String): String = jn(tagName)

  protected def generate(
    reqVarProjectionFqn: Qn,
    reqTagProjectionEntryFqn: Qn,
    extra: CodeChunk = CodeChunk.empty
  ): String = {

    def genTag(tag: CTag, tagGenerator: ReqProjectionGen): CodeChunk = CodeChunk(
      /*@formatter:off*/sn"""\
  /**
   * @return {@code ${tag.name}} tag projection
   */
  public @Nullable ${tagGenerator.shortClassName} ${tagMethodName(tag.name)}() {
    ${reqTagProjectionEntryFqn.last()} tpe = raw.tagProjections().get(${ttr(cType, tag.name, namespace.toString)}.name());
    return tpe == null ? null : new ${tagGenerator.shortClassName}(tpe.projection());
  }
"""/*@formatter:on*/ ,
      Set(
        tagGenerator.fullClassName,
        "org.jetbrains.annotations.Nullable",
        reqTagProjectionEntryFqn.toString
      )
    )

//    def genTail(tail: OpProjectionType, tailGenerator: ReqProjectionGen): CodeChunk = {
//      val tailCtype = toCType(tail.`type`())
//      CodeChunk(
//        /*@formatter:off*/sn"""\
//  /**
//   * @return ${JavaGenUtils.javadocLink(tailCtype, namespace)} tail projection
//   */
//  public @Nullable ${tailGenerator.fullClassName} ${tailMethodPrefix(false)}${typeNameToMethodName(tailCtype)}${tailMethodSuffix(false)}() {
//    ${reqVarProjectionFqn.last()} tail = raw.tailByType(${lqn2(tailCtype, namespace.toString)}.Type.instance());
//    return tail == null ? null : new ${tailGenerator.fullClassName}(tail);
//  }
//"""/*@formatter:on*/ ,
//        Set(
//          "org.jetbrains.annotations.Nullable"
//        )
//      )
//    }
//
//    def genNormalizedTail(tail: OpProjectionType, tailGenerator: ReqProjectionGen): CodeChunk = {
//      val tailCtype = toCType(tail.`type`())
//      val tailTypeExpr = lqn2(tailCtype, namespace.toString)
//      CodeChunk(
//        /*@formatter:off*/sn"""\
//  /**
//   * @return var projection normalized for ${JavaGenUtils.javadocLink(tailCtype, namespace)} type
//   *
//   * @see <a href="https://github.com/SumoLogic/epigraph/wiki/polymorphic-tails#normalized-projections">normalized projections</a>
//   */
//  public @NotNull ${tailGenerator.fullClassName} ${tailMethodPrefix(true)}${typeNameToMethodName(tailCtype)}${tailMethodSuffix(true)}() {
//    return new ${tailGenerator.fullClassName}(raw.normalizedForType($tailTypeExpr.Type.instance()));
//  }
//"""/*@formatter:on*/ ,
//        Set()
//      )
//    }

    val tags = tagGenerators.map { case (tag, gen) => genTag(tag, gen) }.foldLeft(CodeChunk.empty)(_ + _)
    val tails = CodeChunk.empty //tailGenerators.map { case (tail, gen) => genTail(tail, gen) }.foldLeft(CodeChunk.empty)(_ + _)
    val normalizedTails = CodeChunk.empty
//      normalizedTailGenerators
//      .map { case (tail, gen) => genNormalizedTail(tail, gen) }
//      .foldLeft(CodeChunk.empty)(_ + _)

    //                                                                                                   dispatcher code
    val dispatcher = if (normalizedTailGenerators.isEmpty) CodeChunk.empty else new CodeChunk(
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
      Set(
        "ws.epigraph.types.Type",
        "java.util.function.Function",
        "java.util.function.Supplier",
        "java.util.function.Consumer"
      )
    )

    val imports: Set[String] = Set(
      "org.jetbrains.annotations.NotNull",
      reqVarProjectionFqn.toString
    ) ++ tags.imports ++ tails.imports ++ normalizedTails.imports ++ dispatcher.imports ++ extra.imports

    /*@formatter:off*/sn"""\
${JavaGenUtils.topLevelComment}
package $namespace;

${JavaGenUtils.generateImports(imports)}

/**
 * Request projection for {@code ${ln(cType)}} type
 */
${JavaGenUtils.generatedAnnotation(this)}
public class $shortClassName $extendsClause{
${if (parentClassGenOpt.isEmpty) s"  protected final @NotNull ${reqVarProjectionFqn.last()} raw;\n" else ""}\

  public $shortClassName(@NotNull ${reqVarProjectionFqn.last()} raw) { ${if (parentClassGenOpt.isEmpty) "this.raw = raw" else "super(raw)" }; }\
\s${(tags + tails + normalizedTails + extra + dispatcher).code}\
${if (parentClassGenOpt.isEmpty) s"\n  public @NotNull ${reqVarProjectionFqn.last()} _raw() { return raw; };\n\n" else ""}\
}"""/*@formatter:on*/
  }

  protected lazy val flag: CodeChunk = CodeChunk(/*@formatter:off*/sn"""\
  public boolean flag() { return raw.flag(); }
"""/*@formatter:on*/
  )

  protected def generate0: String = generate(
    Qn.fromDotSeparated("ws.epigraph.projections.req.ReqEntityProjection"),
    Qn.fromDotSeparated("ws.epigraph.projections.req.ReqTagProjectionEntry"),
    flag
  )
}
