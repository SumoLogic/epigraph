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

import ws.epigraph.compiler.{CField, CFieldApiWrapper, CRecordTypeDef}
import ws.epigraph.java.JavaGenNames.jn
import ws.epigraph.java.{JavaGen, JavaGenUtils}
import ws.epigraph.java.NewlineStringInterpolator.NewlineHelper
import ws.epigraph.lang.Qn
import ws.epigraph.projections.gen.{GenFieldProjectionEntry, GenRecordModelProjection}
import ws.epigraph.projections.op.AbstractOpModelProjection
import ws.epigraph.types.DatumTypeApi
import ws.epigraph.java.JavaGenUtils.TraversableOnceToListMapObject.TraversableOnceToListMap

import scala.collection.JavaConversions._

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
trait AbstractReqRecordModelProjectionGen extends AbstractReqModelProjectionGen {
  override type OpProjectionType <: AbstractOpModelProjection[_, _, _ <: DatumTypeApi] with GenRecordModelProjection[_, _, _, _, _, _, _ <: DatumTypeApi]
//  override protected type GenType <: ReqRecordModelProjectionGen

  /** field generators: should only include new or overridden fields, should not include inherited */
  def fieldGenerators: Map[CField, AbstractReqFieldProjectionGen]

  protected type OpFieldProjectionType <: GenFieldProjectionEntry[_, _, _, _]

  // ------

  protected val cRecordType: CRecordTypeDef = cType.asInstanceOf[CRecordTypeDef]

  /** get field projections from `g.op` if they override fields from `t` */
  def overridingFieldProjections(
    g: AbstractReqRecordModelProjectionGen,
    t: CRecordTypeDef): Map[String, (Option[AbstractReqRecordModelProjectionGen], OpFieldProjectionType)] = {

    val p = g.op

    val parentOverridingFieldProjections = g.parentClassGenOpt.map(
      pg => overridingFieldProjections(pg.asInstanceOf[AbstractReqRecordModelProjectionGen], t)
    ).getOrElse(Map())

    val gOverridingFieldProjections = p.fieldProjections().toSeq.toListMap
      .filter { case (fieldName, fieldProjection) =>
        // only keep overriden fields
        t.findEffectiveField(fieldName).exists(
          field =>
            field.valueDataType.name != fieldProjection.asInstanceOf[OpFieldProjectionType].field().dataType().name().toString
        )
      }
      .map { case (fieldName, fieldProjection) =>
        // convert overriden fields to be of proper type.
        fieldName -> (
          Some(g),
          fieldProjection.asInstanceOf[OpFieldProjectionType]
            .overridenFieldProjection(new CFieldApiWrapper(t.findEffectiveField(fieldName).get))
            .asInstanceOf[OpFieldProjectionType]
        )
      }

    parentOverridingFieldProjections ++ gOverridingFieldProjections
  }

  /**
   * field projections: should only include new or overridden fields, should not include inherited
   *
   * maps field names to (generatorOpt, projection) pairs, where generatorOpt contains generator of the
   * overriden field, or None if field is not overriding anything
   */
  lazy val fieldProjections: Map[String, (Option[AbstractReqRecordModelProjectionGen], OpFieldProjectionType)] =
    op.fieldProjections().toSeq.toListMap
      .filterKeys { !isInherited(_) }
      .mapValues(p => (None, p.asInstanceOf[OpFieldProjectionType])) ++
    parentClassGenOpt.map(
      pg => overridingFieldProjections(
        pg.asInstanceOf[AbstractReqRecordModelProjectionGen],
        cRecordType
      )
    ).getOrElse(Map())

  def isInherited(fieldName: String): Boolean = parentClassGenOpt.exists { pg =>
    val rpg = pg.asInstanceOf[AbstractReqRecordModelProjectionGen]
    rpg.fieldProjections.contains(fieldName) || rpg.isInherited(fieldName)
  }

  override def children: Iterable[JavaGen] = super.children ++ {
    // exclude (overriden) fields taken from parent generator
    if (AbstractReqFieldProjectionGen.generateFieldProjections)
      fieldGenerators.values
    else
      fieldGenerators.values.flatMap(_.children)
  }

  protected def findField(name: String): CField = cRecordType.effectiveFields.find(_.name == name).getOrElse {
    throw new RuntimeException(s"Can't find field '$name' in type '${ cType.name.toString }'")
  }

  def findFieldGenerator(fieldName: String): Option[AbstractReqFieldProjectionGen] =
    fieldGenerators.find(_._1.name == fieldName).map(_._2)

  protected def generate(
    reqRecordModelProjectionFqn: Qn,
    reqFieldProjectionEntryFqn: Qn,
    extra: CodeChunk = CodeChunk.empty): String = {

    def genField(field: CField, fieldGenerator: AbstractReqFieldProjectionGen): CodeChunk = {

      lazy val fieldProjection = /*@formatter:off*/sn"""\
  /**
   * @return {@code ${field.name}} field projection
   */
  public @Nullable ${fieldGenerator.shortClassName} ${jn(field.name)}FieldProjection() {
    ${reqFieldProjectionEntryFqn.last()} fpe = raw.fieldProjection("${field.name}");
    return fpe == null ? null : new ${fieldGenerator.shortClassName}(fpe.fieldProjection());
  }
"""/*@formatter:on*/

      val dataGenerator = fieldGenerator.dataProjectionGen
      val modelProjection = /*@formatter:off*/sn"""\
  /**
   * @return {@code ${field.name}} field data projection
   */
  public @Nullable ${dataGenerator.fullClassName} ${jn(field.name)}() {
    ${reqFieldProjectionEntryFqn.last()} fpe = raw.fieldProjection("${field.name}");
    return fpe == null ? null : new ${dataGenerator.fullClassName}(fpe.fieldProjection().varProjection());
  }
"""/*@formatter:on*/

      if (AbstractReqFieldProjectionGen.generateFieldProjections)
        CodeChunk(
          fieldProjection ++ modelProjection,
          Set(
            fieldGenerator.fullClassName,
            "org.jetbrains.annotations.Nullable",
            reqFieldProjectionEntryFqn.toString
          )
        )
      else
        CodeChunk(
          modelProjection,
          Set(
            "org.jetbrains.annotations.Nullable",
            reqFieldProjectionEntryFqn.toString
          )
        )
    }

    val fields = fieldGenerators.map { case (field, gen) => genField(field, gen) }.foldLeft(CodeChunk.empty)(_ + _)

    val imports: Set[String] = Set(
      "org.jetbrains.annotations.NotNull",
      reqVarProjectionFqn.toString,
      reqModelProjectionFqn.toString,
      reqRecordModelProjectionFqn.toString
    ) ++ fields.imports ++ params.imports ++ meta.imports ++ tails.imports ++ normalizedTails.imports ++ dispatcher.imports ++ extra.imports

    /*@formatter:off*/sn"""\
${JavaGenUtils.topLevelComment}
$packageStatement

${JavaGenUtils.generateImports(imports)}

$classJavadoc\
${JavaGenUtils.generatedAnnotation(this)}
public class $shortClassName $extendsClause{
${if (parentClassGenOpt.isEmpty) s"  protected final @NotNull ${reqRecordModelProjectionFqn.last()} raw;\n" else ""}\

  public $shortClassName(@NotNull ${reqModelProjectionFqn.last()}$reqModelProjectionParams raw) {
    ${if (parentClassGenOpt.isEmpty) s"this.raw = (${reqRecordModelProjectionFqn.last()}) raw" else "super(raw)"};
  }

  public $shortClassName(@NotNull ${reqVarProjectionFqn.last()} selfVar) {
    this(selfVar.singleTagProjection().projection());
  }\
\s${(extra + fields + params + meta + tails + normalizedTails + dispatcher).code}\
${if (parentClassGenOpt.isEmpty) s"\n  public @NotNull ${reqRecordModelProjectionFqn.last()} _raw() { return raw; };\n\n" else ""}\
}"""/*@formatter:on*/
  }
}
