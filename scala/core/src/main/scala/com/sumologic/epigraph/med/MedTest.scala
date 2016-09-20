/* Created by yegor on 5/4/16. */

package com.sumologic.epigraph.med

import com.sumologic.epigraph.raw.RawNames

object MedTest extends RawNames with MedTypes with MedData {

  val ns: QualifiedNamespaceName = QualifiedNamespaceName(None, "org") / LocalNamespaceName("example")

  trait PersonRecord extends RecordDatum[PersonRecord]


  object PersonRecord extends StaticRecordType[PersonRecord](ns \ "PersonRecord", Nil) {

    val bestFriend: Field[PersonRecord, PersonRecord] = field("bestFriend", PersonRecord)

    val company: Field[PersonRecord, CompanyRecord] = field("company", CompanyRecord)

    // TODO collect these from field(...) invocations
    override val declaredFields: Seq[Field[PersonRecord, _]] = Seq[Field[PersonRecord, _]](bestFriend, company)

  }


  trait UserRecord extends PersonRecord with RecordDatum[UserRecord]


  object UserRecord extends StaticRecordType[UserRecord](ns \ "UserRecord", Seq(PersonRecord)) {

    val id: StaticField[UserRecord, User] = field("id", User)

    val url: StaticField[UserRecord, UserUrl] = field("url", UserUrl)

    override val declaredFields: Seq[Field[UserRecord, _]] = Seq[Field[UserRecord, _]](id, url)

  }


  trait UserId extends StringDatum[UserId]


  object UserId extends StaticStringType[UserId](ns \ "UserId", Nil)


  trait User extends Var[User]


  object User extends StaticVarType[User](ns \ "User", Nil) with VarTypeWithDefault[User, UserId] {

    val id: TypeMember[UserId] = new TypeMember[UserId]("id", UserId)

    val record: TypeMember[UserRecord] = new TypeMember[UserRecord]("record", UserRecord)

    override val default: TypeMember[UserId] = id

    override val members: Seq[TypeMember[_]] = Seq[TypeMember[_]](id, record)

  }


  trait Url extends StringDatum[Url]


  object Url extends StaticStringType[Url](ns \ "Url", Nil)

  trait UserUrl extends Var[UserUrl]

  object UserUrl extends StaticVarType[UserUrl](ns \ "UserUrl", Nil) {

    val fb: TypeMember[Url] = new TypeMember[Url]("fb", Url)

    val li: TypeMember[Url] = new TypeMember[Url]("li", Url)

    override def defaultMember: Option[Nothing] = None

    override def members: Seq[TypeMember[_]] = Seq[TypeMember[_]](fb, li)

  }


  trait CompanyRecord extends RecordDatum[CompanyRecord]


  object CompanyRecord extends StaticRecordType[CompanyRecord](ns \ "CompanyRecord", Nil) {

    val manager: Field[CompanyRecord, User] = field("manager", User)

    override val declaredFields: Seq[Field[CompanyRecord, _]] = Seq(manager)

  }


  def main(args: Array[String]) {

    println(UserRecord)
    println(PersonRecord)
    println(CompanyRecord)

    val p1: PersonRecord = ???

    val p1bf: PersonRecord = p1.get(PersonRecord.bestFriend)
//    val p1c = p1.get(PersonRecord.company)//(CompanyRecord.default.get)

//    import PersonRecord._, CompanyRecord._, UserRecord._, User.record, UserUrl._
//    val p1bf2 = p1 get bestFriend
//    val p1bf3: PersonRecord = p1bf2
//    val p1c2 = p1.get(company).get(manager)(record) get company
//    val p1c3 = (p1 / company)./(manager)(record) / bestFriend / company
//
//    val u1: UserRecord = ???
//    val u1id: UserId = u1.get(id)
//    val u1id2: UserId = u1.get(id)(record) / bestFriend / company / manager
//
//    val u1fb: Url = u1.get(url)(fb)
//    val u1li: Url = u1.get(url)(li)

  }

}
