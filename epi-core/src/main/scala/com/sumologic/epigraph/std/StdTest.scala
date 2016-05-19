/* Created by yegor on 5/18/16. */

package com.sumologic.epigraph.std

object StdTest {


  trait PersonRecord extends RecordDatum[PersonRecord]


  object PersonRecord extends RecordType[PersonRecord] {

    override val name: QualifiedTypeName = ???

    val isPolymorphic: Boolean = false

    override val declaredSupertypes: Seq[RecordType[_ >: PersonRecord]] = Nil

    val firstName: TaggedField[PersonRecord, FirstName, FirstName.default.type, FirstName] =
      new TaggedField[PersonRecord, FirstName, FirstName.default.type, FirstName] {

        override def tag: FirstName.default.type = FirstName.default

        override def valueType: MultiType[FirstName] = FirstName

        override def as[T <: Datum[T]](varTag: VarTag[_ >: FirstName, T]): TaggedField[PersonRecord, FirstName, varTag.type, T] = ???

      }

    override val declaredFields: Seq[Field[PersonRecord, _]] = Seq(firstName)

  }


  trait FirstName extends StringDatum[FirstName]


  object FirstName extends StringType[FirstName] {

    override val isPolymorphic: Boolean = false

    override val declaredSupertypes: Seq[StringType[_ >: FirstName]] = Nil

    override val name: QualifiedTypeName = ???

  }


  trait UserRecord extends PersonRecord with RecordDatum[UserRecord]


  object UserRecord extends RecordType[UserRecord] {

    override val isPolymorphic: Boolean = false

    override val declaredSupertypes: Seq[RecordType[_ >: UserRecord]] = Seq(PersonRecord)

    override val name: QualifiedTypeName = ???

    val id: TaggedField[UserRecord, UserId, UserId.default.type, UserId] =
      new TaggedField[UserRecord, UserId, UserId.default.type, UserId] {

        override val tag: UserId.default.type = UserId.default

        override val valueType: MultiType[UserId] = UserId

        override def as[T <: Datum[T]](varTag: VarTag[_ >: UserId, T]): TaggedField[UserRecord, UserId, varTag.type, T] = ???

      }

    override val declaredFields: Seq[Field[UserRecord, _]] = Nil
  }


  trait UserId extends LongDatum[UserId]


  object UserId extends LongType[UserId] {

    override val isPolymorphic: Boolean = false

    override val name: QualifiedTypeName = ???

    override val declaredSupertypes: Seq[Super] = Nil

  }


  trait User extends MultiVar[User]


  object User extends MultiType[User] {

    override type Super = MultiType[_ >: User]

    override def listOf: ListType[Null, User] = ???

    override val declaredSupertypes: Seq[Super] = Nil

    override val name: QualifiedTypeName = ???

    val id: VarTag[User, UserId] = new VarTag[User, UserId] {

      override val name: TypeMemberName = ???

      override val declaredDataType: DataType[UserId] = UserId

    }

    val record: VarTag[User, UserRecord] = new VarTag[User, UserRecord] {

      override val name: TypeMemberName = ???

      override val declaredDataType: DataType[UserRecord] = UserRecord

    }

    override val declaredVarTags: Seq[VarTag[User, _]] = Seq(id, record)

  }


  def main(args: Array[String]) {

    val u1: UserRecord = ???
    val u1id: UserId = u1.get(UserRecord.id)
    val u1fn: FirstName = u1.get(PersonRecord.firstName)

    val p1: PersonRecord = u1
    val p1fn: FirstName = p1.get(PersonRecord.firstName)
//    val p1bf0: PersonId = p1.bestFriend
//    val p1bf0r: PersonRecord = p1.bestFriend(Person.record)
//    val p1bf1: PersonId = p1.getBestFriend
//    val p1bf2: PersonId = p1.get(PersonRecord.bestFriend)
//    val p1bf2r: PersonRecord = p1.get(PersonRecord.bestFriend.record)
//    val p1bf2r2: PersonRecord = p1.get(PersonRecord.bestFriend.asRecord)
//    val p1bf2r3: PersonRecord = p1.get(PersonRecord.bestFriend.as(Person.record))
  }

}
