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

package ws.epigraph.compiler

import java.util
import java.util.Collections

import ws.epigraph.names._
import ws.epigraph.types._

import scala.collection.JavaConversions._

// todo: this is a temporary bridge, must be removed once op projections + idl + parsers are ported to scala/ctypes

trait CTypeApiWrapper extends TypeApi {
  val cType: CType

  override def kind(): TypeKind = cType.kind match {
    case CTypeKind.VARTYPE => TypeKind.UNION
    case CTypeKind.RECORD => TypeKind.RECORD
    case CTypeKind.MAP => TypeKind.MAP
    case CTypeKind.LIST => TypeKind.LIST
    case CTypeKind.ENUM => TypeKind.ENUM
    case CTypeKind.STRING => TypeKind.PRIMITIVE
    case CTypeKind.INTEGER => TypeKind.PRIMITIVE
    case CTypeKind.LONG => TypeKind.PRIMITIVE
    case CTypeKind.DOUBLE => TypeKind.PRIMITIVE
    case CTypeKind.BOOLEAN => TypeKind.PRIMITIVE
    case _ => throw new IllegalArgumentException("Unsupported kind: " + cType.kind)
  }

  override lazy val supertypes: util.Collection[_ <: TypeApi] = cType.supertypes.map{s => CTypeApiWrapper.wrap(s)}

  override def isAssignableFrom(`type`: TypeApi): Boolean = `type` match {
    case wrapper: CTypeApiWrapper => cType.isAssignableFrom(wrapper.cType)
    case ct: CType => cType.isAssignableFrom(ct)
    case other => throw new IllegalArgumentException("Unsupported type class: " + other.getClass.getName)
  }

  override def dataType(): DataTypeApi = new CDataTypeApiWrapper(cType.dataType)
}

object CTypeApiWrapper {
  def wrap(cType: CType): TypeApi = cType match {
    case t: CVarTypeDef => new CVarTypeDefApiWrapper(t)
    case t: CRecordTypeDef => new CRecordTypeDefApiWrapper(t)
    case t: CMapTypeDef => new CMapTypeDefApiWrapper(t)
    case t: CAnonMapType => new CAnonMapTypeApiWrapper(t)
    case t: CListTypeDef => new CListTypeDefApiWrapper(t)
    case t: CAnonListType => new CAnonListTypeApiWrapper(t)
    case t: CPrimitiveTypeDef => new CPrimitiveTypeDefApiWrapper(t)
    case _ => throw new IllegalArgumentException("Unsupported type: " + Option(cType).map(_.getClass.getName))
  }
}

class CDataTypeApiWrapper(private val dataType: CDataType) extends DataTypeApi {
  override val `type`: TypeApi = CTypeApiWrapper.wrap(dataType.typeRef.resolved)

  override val defaultTag: TagApi = dataType.defaultTag.map{ ct => new CTagWrapper(ct) }.orNull

  override val name: DataTypeName = new DataTypeName(`type`.name(), dataType.defaultTag.map(_.name).orNull)

  def canEqual(other: Any): Boolean = other.isInstanceOf[CDataTypeApiWrapper]

  override def equals(other: Any): Boolean = other match {
    case that: CDataTypeApiWrapper => (that canEqual this) && dataType == that.dataType
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(dataType)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }
}

class CTagWrapper(private val cTag: CTag) extends TagApi {
  override val name: String = cTag.name

  override val `type`: DatumTypeApi = CTypeApiWrapper.wrap(cTag.typeRef.resolved).asInstanceOf[DatumTypeApi]

  def canEqual(other: Any): Boolean = other.isInstanceOf[CTagWrapper]

  override def equals(other: Any): Boolean = other match {
    case that: CTagWrapper => (that canEqual this) && cTag == that.cTag
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(cTag)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }
}

trait CTypeDefApiWrapper extends CTypeApiWrapper {
  override val cType: CTypeDef
  override val name: QualifiedTypeName = QualifiedTypeName.fromFqn(cType.name.fqn)
}

class CVarTypeDefApiWrapper(val cType: CVarTypeDef) extends CTypeDefApiWrapper with UnionTypeApi {
  override lazy val tags: util.Collection[_ <: TagApi] = cType.effectiveTags.map{ ct => new CTagWrapper(ct) }

  override lazy val tagsMap: util.Map[String, _ <: TagApi] =
    mapAsJavaMap(cType.effectiveTags.map{ ct => ct.name -> new CTagWrapper(ct) }.toMap)

  override lazy val supertypes: util.Collection[_ <: UnionTypeApi] = cType.supertypes.map{s => CTypeApiWrapper.wrap(s).asInstanceOf[UnionTypeApi]}

  override def dataType(defaultTag: TagApi): DataTypeApi =
    new CDataTypeApiWrapper(cType.dataType(Option(defaultTag).map(_.name())))


  def canEqual(other: Any): Boolean = other.isInstanceOf[CVarTypeDefApiWrapper]

  override def equals(other: Any): Boolean = other match {
    case that: CVarTypeDefApiWrapper => (that canEqual this) && cType == that.cType
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(cType)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }
}

trait CDatumTypeApiWrapper extends CTypeApiWrapper with DatumTypeApi {
  override val cType: CDatumType

  override lazy val tags: util.Collection[_ <: TagApi] = Collections.singletonList(new CTagWrapper(cType.impliedTag))

  override lazy val tagsMap: util.Map[String, _ <: TagApi] =
    Collections.singletonMap(CDatumType.ImpliedDefaultTagName, self())

  override def self(): TagApi = new CTagWrapper(cType.impliedTag)

  override def dataType(): DataTypeApi = new CDataTypeApiWrapper(cType.dataType)

  override def metaType(): DatumTypeApi = cType.meta.map{ ct => CTypeApiWrapper.wrap(ct).asInstanceOf[DatumTypeApi] }.orNull
}

class CFieldApiWrapper(private val cField: CField) extends FieldApi {
  override def name(): String = cField.name

  override def dataType(): DataTypeApi = new CDataTypeApiWrapper(cField.valueDataType)

  def canEqual(other: Any): Boolean = other.isInstanceOf[CFieldApiWrapper]

  override def equals(other: Any): Boolean = other match {
    case that: CFieldApiWrapper => (that canEqual this) && cField == that.cField
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(cField)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }
}

class CRecordTypeDefApiWrapper(val cType: CRecordTypeDef)
  extends CTypeDefApiWrapper with CDatumTypeApiWrapper with RecordTypeApi {

  override lazy val supertypes: util.Collection[_ <: RecordTypeApi] = cType.supertypes.map{s => CTypeApiWrapper.wrap(s).asInstanceOf[RecordTypeApi]}

  override lazy val fields: util.Collection[_ <: FieldApi] = cType.effectiveFields.map{ cf => new CFieldApiWrapper(cf) }

  override lazy val immediateFields: util.Collection[_ <: FieldApi] = cType.declaredFields.map{ cf => new CFieldApiWrapper(cf) }

  override lazy val fieldsMap: util.Map[String, _ <: FieldApi] =
    mapAsJavaMap(cType.effectiveFields.map{ cf => cf.name -> new CFieldApiWrapper(cf) }.toMap)


  def canEqual(other: Any): Boolean = other.isInstanceOf[CRecordTypeDefApiWrapper]

  override def equals(other: Any): Boolean = other match {
    case that: CRecordTypeDefApiWrapper => (that canEqual this) && cType == that.cType
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(cType)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }
}

trait CMapTypeApiWrapper extends CTypeApiWrapper with CDatumTypeApiWrapper with MapTypeApi {
  override val cType: CMapType

  override lazy val supertypes: util.Collection[_ <: MapTypeApi] = cType.supertypes.map{s => CTypeApiWrapper.wrap(s).asInstanceOf[MapTypeApi]}

  override val keyType: DatumTypeApi = CTypeApiWrapper.wrap(cType.keyTypeRef.resolved).asInstanceOf[DatumTypeApi]

  override val valueType: DataTypeApi = new CDataTypeApiWrapper(cType.valueDataType)
}

class CAnonMapTypeApiWrapper(val cType: CAnonMapType) extends CMapTypeApiWrapper {
  override val name: TypeName = new AnonMapTypeName(keyType.name(), valueType.name())

  def canEqual(other: Any): Boolean = other.isInstanceOf[CAnonMapTypeApiWrapper]

  override def equals(other: Any): Boolean = other match {
    case that: CAnonMapTypeApiWrapper => (that canEqual this) && cType == that.cType
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(cType)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }
}

class CMapTypeDefApiWrapper(val cType: CMapTypeDef) extends CMapTypeApiWrapper with CTypeDefApiWrapper

trait CListTypeApiWrapper extends CTypeApiWrapper with CDatumTypeApiWrapper with ListTypeApi {
  override val cType: CListType

  override lazy val supertypes: util.Collection[_ <: ListTypeApi] = cType.supertypes.map{s => CTypeApiWrapper.wrap(s).asInstanceOf[ListTypeApi]}

  override val elementType: DataTypeApi = new CDataTypeApiWrapper(cType.elementDataType)
}

class CAnonListTypeApiWrapper(val cType: CAnonListType) extends CListTypeApiWrapper {
  override val name: TypeName = new AnonListTypeName(elementType.name())

  def canEqual(other: Any): Boolean = other.isInstanceOf[CAnonListTypeApiWrapper]

  override def equals(other: Any): Boolean = other match {
    case that: CAnonListTypeApiWrapper => (that canEqual this) && cType == that.cType
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(cType)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }
}

class CListTypeDefApiWrapper(val cType: CListTypeDef) extends CListTypeApiWrapper with CTypeDefApiWrapper {

  def canEqual(other: Any): Boolean = other.isInstanceOf[CListTypeDefApiWrapper]

  override def equals(other: Any): Boolean = other match {
    case that: CListTypeDefApiWrapper => (that canEqual this) && cType == that.cType
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(cType)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }
}

class CPrimitiveTypeDefApiWrapper(val cType: CPrimitiveTypeDef)
  extends CTypeDefApiWrapper with CDatumTypeApiWrapper with PrimitiveTypeApi {

  override lazy val supertypes: util.Collection[_ <: PrimitiveTypeApi] = cType.supertypes.map{s => CTypeApiWrapper.wrap(s).asInstanceOf[PrimitiveTypeApi]}

  def canEqual(other: Any): Boolean = other.isInstanceOf[CPrimitiveTypeDefApiWrapper]

  override def equals(other: Any): Boolean = other match {
    case that: CPrimitiveTypeDefApiWrapper => (that canEqual this) && cType == that.cType
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(cType)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }
}
