/* Created by yegor on 6/10/16. */

package com.sumologic.epigraph.schema

import org.jetbrains.annotations.NotNull
import pprint.{Config, PPrint, PPrinter}

package object compiler {


  implicit object CErrorPrinter extends PPrinter[CError] {

    override def render0(t: CError, c: Config): Iterator[String] = Iterator( // TODO add " (filename.ext:linenum)"?
      t.filename, ":", t.position.line.toString, ":", t.position.column.toString, "\nError: ", t.message, "\n"
    ) ++ t.position.lineText.iterator ++ Iterator("\n", " " * (t.position.column - 1), "^")

  }

  implicit val CErrorPrint: PPrint[CError] = PPrint(CErrorPrinter)


//  implicit object CSchemaFilePrinter extends PPrinter[CSchemaFile] {
//
//    override def render0(t: CSchemaFile, c: Config): Iterator[String] = {
//      def body = (c: Config) => CTypePrinter.typeParts(t, c) ++ Iterator(
//        pprint.Internals.handleChunks(
//          "tags", c, (c: Config) => t.declaredTags.toIterator.map(CTagPrinter.render(_, c))
//        )
//      )
//      pprint.Internals.handleChunks("var " + t.name.name, c, body)
//
//    }
//  }


  implicit object CTypeRefPrinter extends PPrinter[CTypeRef] {

    override def render0(t: CTypeRef, c: Config): Iterator[String] = CTypeNamePrinter.render(t.name, c)

  }

  implicit val CTypeRefPrint: PPrint[CTypeRef] = PPrint(CTypeRefPrinter)


  implicit object CTypeNamePrinter extends PPrinter[CTypeName] {

    override def render0(t: CTypeName, c: Config): Iterator[String] = Iterator("«", t.name, "»")

  }

  implicit val CTypeNamePrint: PPrint[CTypeName] = PPrint(CTypeNamePrinter)


  implicit object CTypePrinter extends PPrinter[CTypeDef] {

    override def render0(t: CTypeDef, c: Config): Iterator[String] = {
      t match {
        case vt: CVarTypeDef => CVarTypePrinter.render(vt, c)
        case rt: CRecordTypeDef => CRecordTypePrinter.render(rt, c)
        case mt: CMapTypeDef => CMapTypePrinter.render(mt, c)
        case lt: CListTypeDef => CListTypePrinter.render(lt, c)
        case et: CEnumTypeDef => CEnumTypePrinter.render(et, c)
        case pt: CPrimitiveTypeDef => CPrimitiveTypePrinter.render(pt, c)
        case _ => Iterator("UNKNOWN ", t.name.name)
      }
    }

    def typeParts(@NotNull t: CTypeDef, c: Config): Iterator[Iterator[String]] = Iterator(
      pprint.Internals.handleChunks(
        "extends", c,
        (c: Config) => t.declaredSupertypeRefs.toIterator.map(implicitly[PPrint[CTypeRef]].pprinter.render(_, c))
      ),
      pprint.Internals.handleChunks(
        "supplements", c,
        (c: Config) => t.declaredSupplementees.toIterator.map(implicitly[PPrint[CTypeRef]].pprinter.render(_, c))
      )
    )

  }

  implicit object CVarTypePrinter extends PPrinter[CVarTypeDef] {

    override def render0(@NotNull t: CVarTypeDef, c: Config): Iterator[String] = {
      def body = (c: Config) => CTypePrinter.typeParts(t, c) ++ Iterator(
        pprint.Internals.handleChunks(
          "tags", c, (c: Config) => t.declaredTags.toIterator.map(CTagPrinter.render(_, c))
        )
      )
      pprint.Internals.handleChunks("var " + t.name.name, c, body)
    }

  }

  implicit val CVarTypePrint: PPrint[CVarTypeDef] = PPrint(CVarTypePrinter)

  implicit object CTagPrinter extends PPrinter[CTag] {

    override def render0(t: CTag, c: Config): Iterator[String] = {
      pprint.Internals.handleChunks(
        t.name + ": " + CTypeRefPrint.render(t.typeRef, c).mkString, c, (c: Config) => Iterator(
          // TODO tag attributes etc.
        )
      )
    }

  }

  implicit val CTagPrint: PPrint[CTag] = PPrint(CTagPrinter)


  implicit object CRecordTypePrinter extends PPrinter[CRecordTypeDef] {

    override def render0(@NotNull t: CRecordTypeDef, c: Config): Iterator[String] = {
      def body = (c: Config) => CTypePrinter.typeParts(t, c) ++ Iterator(
        pprint.Internals.handleChunks(
          "fields", c, (c: Config) => t.declaredFields.toIterator.map(CFieldPrint.pprinter.render(_, c))
        )
      )
      pprint.Internals.handleChunks("record " + t.name.name, c, body)
    }

  }

  implicit val CRecordTypePrint: PPrint[CRecordTypeDef] = PPrint(CRecordTypePrinter)

  implicit object CFieldPrinter extends PPrinter[CField] {

    override def render0(t: CField, c: Config): Iterator[String] = {
      pprint.Internals.handleChunks(
        "" + t.name + ": " + CTypeRefPrinter.render(t.typeRef, c).mkString, c, (c: Config) => Iterator(
          // TODO field attributes etc.
        )
      )
    }

  }

  implicit val CFieldPrint: PPrint[CField] = PPrint(CFieldPrinter)


  implicit object CMapTypePrinter extends PPrinter[CMapTypeDef] {

    override def render0(@NotNull t: CMapTypeDef, c: Config): Iterator[String] = {
      pprint.Internals.handleChunks(
        "map " + t.name.name, c, (c: Config) => CTypePrinter.typeParts(t, c) ++ Iterator(
          Iterator("keyType: ") ++ CTypeRefPrinter.render(t.keyTypeRef, c),
          Iterator("valueType: ") ++ CTypeRefPrinter.render(t.valueTypeRef, c)
        )
      )
    }
  }

  implicit val CMapTypePrint: PPrint[CMapTypeDef] = PPrint(CMapTypePrinter)


  implicit object CListTypePrinter extends PPrinter[CListTypeDef] {

    override def render0(@NotNull t: CListTypeDef, c: Config): Iterator[String] = {
      pprint.Internals.handleChunks(
        "list " + t.name.name, c, (c: Config) => CTypePrinter.typeParts(t, c) ++ Iterator(
          Iterator("valueType", t.elementTypeRef.name.name)
        )
      )
    }
  }

  implicit val CListTypePrint: PPrint[CListTypeDef] = PPrint(CListTypePrinter)


  implicit object CEnumTypePrinter extends PPrinter[CEnumTypeDef] {

    override def render0(@NotNull t: CEnumTypeDef, c: Config): Iterator[String] = {
      def body = (c: Config) => CTypePrinter.typeParts(t, c) ++ Iterator(
        pprint.Internals.handleChunks(
          "values", c, (c: Config) => t.values.toIterator.map(CEnumValuePrint.pprinter.render(_, c))
        )
      )
      pprint.Internals.handleChunks("enum " + t.name.name, c, body)
    }

  }

  implicit val CEnumTypePrint: PPrint[CEnumTypeDef] = PPrint(CEnumTypePrinter)

  implicit object CEnumValuePrinter extends PPrinter[CEnumValue] {

    override def render0(t: CEnumValue, c: Config): Iterator[String] = {
      Iterator(t.name)
    }

  }

  implicit val CEnumValuePrint: PPrint[CEnumValue] = PPrint(CEnumValuePrinter)


  implicit object CPrimitiveTypePrinter extends PPrinter[CPrimitiveTypeDef] {

    override def render0(@NotNull t: CPrimitiveTypeDef, c: Config): Iterator[String] = {
      pprint.Internals.handleChunks(
        t.kind.keyword + " " + t.name.name, c, (c: Config) => CTypePrinter.typeParts(t, c) ++ Iterator(
          // TODO enum attributes
        )
      )
    }
  }

  implicit val CPrimitiveTypePrint: PPrint[CPrimitiveTypeDef] = PPrint(CPrimitiveTypePrinter)

}
