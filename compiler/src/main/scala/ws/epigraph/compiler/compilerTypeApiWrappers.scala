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

  override val supertypes: util.Collection[_ <: TypeApi] = cType.supertypes.map(_.asInstanceOf[TypeApi])

  override def isAssignableFrom(`type`: TypeApi): Boolean = cType.isAssignableFrom(`type`.asInstanceOf)
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
    case _ => throw new IllegalArgumentException("Unsupported type: " + cType.getClass.getName)
  }
}

class CDataTypeApiWrapper(dataType: CDataType) extends DataTypeApi {
  override val `type`: TypeApi = CTypeApiWrapper.wrap(dataType.typeRef.resolved)

  override val defaultTag: TagApi = dataType.defaultTag.map{ ct => new CTagWrapper(ct) }.orNull

  override val name: DataTypeName = new DataTypeName(`type`.name(), dataType.defaultTag.map(_.name).orNull)
}

class CTagWrapper(cTag: CTag) extends TagApi {
  override val name: String = cTag.name

  override val `type`: DatumTypeApi = CTypeApiWrapper.wrap(cTag.typeRef.resolved).asInstanceOf
}

trait CTypeDefApiWrapper extends CTypeApiWrapper {
  override val cType: CTypeDef
  override val name: TypeName = QualifiedTypeName.fromFqn(cType.name.fqn)
}

class CVarTypeDefApiWrapper(val cType: CVarTypeDef) extends CTypeDefApiWrapper with UnionTypeApi {
  override lazy val tags: util.Collection[_ <: TagApi] = cType.effectiveTags.map{ ct => new CTagWrapper(ct) }

  override lazy val tagsMap: util.Map[String, _ <: TagApi] =
    mapAsJavaMap(cType.effectiveTags.map{ ct => ct.name -> new CTagWrapper(ct) }.toMap)

  override val supertypes: util.Collection[_ <: UnionTypeApi] = cType.supertypes.map(_.asInstanceOf[UnionTypeApi])

  override def dataType(defaultTag: TagApi): DataTypeApi =
    new CDataTypeApiWrapper(cType.dataType(Option(defaultTag).map(_.name())))

}

trait CDatumTypeApiWrapper extends CTypeApiWrapper with DatumTypeApi {
  override val cType: CDatumType

  override lazy val tags: util.Collection[_ <: TagApi] = Collections.singletonList(new CTagWrapper(cType.impliedTag))

  override lazy val tagsMap: util.Map[String, _ <: TagApi] =
    Collections.singletonMap(CDatumType.ImpliedDefaultTagName, self)

  override val self: TagApi = new CTagWrapper(cType.impliedTag)

  override def dataType(): DataTypeApi = new CDataTypeApiWrapper(cType.dataType)
}

class CFieldApiWrapper(cField: CField) extends FieldApi {
  override def name(): String = cField.name

  override def dataType(): DataTypeApi = new CDataTypeApiWrapper(cField.valueDataType)
}

class CRecordTypeDefApiWrapper(val cType: CRecordTypeDef)
  extends CTypeDefApiWrapper with CDatumTypeApiWrapper with RecordTypeApi {

  override val supertypes: util.Collection[_ <: RecordTypeApi] = cType.supertypes.map(_.asInstanceOf[RecordTypeApi])

  override lazy val fields: util.Collection[_ <: FieldApi] = cType.effectiveFields.map{ cf => new CFieldApiWrapper(cf) }

  override lazy val fieldsMap: util.Map[String, _ <: FieldApi] =
    mapAsJavaMap(cType.effectiveFields.map{ cf => cf.name -> new CFieldApiWrapper(cf) }.toMap)
}

trait CMapTypeApiWrapper extends CTypeApiWrapper with CDatumTypeApiWrapper with MapTypeApi {
  override val cType: CMapType

  override val supertypes: util.Collection[_ <: MapTypeApi] = cType.supertypes.map(_.asInstanceOf[MapTypeApi])

  override val keyType: DatumTypeApi = CTypeApiWrapper.wrap(cType.keyTypeRef.resolved).asInstanceOf

  override val valueType: DataTypeApi = new CDataTypeApiWrapper(cType.valueDataType)
}

class CAnonMapTypeApiWrapper(val cType: CAnonMapType) extends CMapTypeApiWrapper {
  override val name: TypeName = new AnonMapTypeName(keyType.name(), valueType.name())
}

class CMapTypeDefApiWrapper(val cType: CMapTypeDef) extends CMapTypeApiWrapper with CTypeDefApiWrapper

trait CListTypeApiWrapper extends CTypeApiWrapper with CDatumTypeApiWrapper with ListTypeApi {
  override val cType: CListType

  override val supertypes: util.Collection[_ <: ListTypeApi] = cType.supertypes.map(_.asInstanceOf[ListTypeApi])

  override val elementType: DataTypeApi = new CDataTypeApiWrapper(cType.elementDataType)
}

class CAnonListTypeApiWrapper(val cType: CAnonListType) extends CListTypeApiWrapper {
  override val name: TypeName = new AnonListTypeName(elementType.name())
}

class CListTypeDefApiWrapper(val cType: CListTypeDef) extends CListTypeApiWrapper with CTypeDefApiWrapper

class CPrimitiveTypeDefApiWrapper(val cType: CPrimitiveTypeDef)
  extends CTypeDefApiWrapper with CDatumTypeApiWrapper with PrimitiveTypeApi {

  override val supertypes: util.Collection[_ <: PrimitiveTypeApi] = cType.supertypes.map(_.asInstanceOf[PrimitiveTypeApi])
}
