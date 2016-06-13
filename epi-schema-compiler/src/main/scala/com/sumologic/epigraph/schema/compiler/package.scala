/* Created by yegor on 6/10/16. */

package com.sumologic.epigraph.schema

import org.jetbrains.annotations.NotNull
import pprint.{Config, PPrint, PPrinter}

package object compiler {

  implicit object CTypeRefPrinter extends PPrinter[CTypeRef] {

    override def render0(t: CTypeRef, c: Config): Iterator[String] = Iterator(t.name.name)

  }

  implicit val CTypeRefPrint: PPrint[CTypeRef] = PPrint(CTypeRefPrinter)


  implicit object CTypePrinter extends PPrinter[CType] {

    override def render0(t: CType, c: Config): Iterator[String] = {
      t match {
        case vt: CVarType => CVarTypePrinter.render(vt, c)
        case rt: CRecordType => CRecordTypePrinter.render(rt, c)
        case mt: CMapType => CMapTypePrinter.render(mt, c)
        case lt: CListType => CListTypePrinter.render(lt, c)
        case et: CEnumType => CEnumTypePrinter.render(et, c)
        case pt: CPrimitiveType => CPrimitiveTypePrinter.render(pt, c)
        case _ => Iterator("UNKNOWN ", t.name.name)
      }
    }

    def typeParts(@NotNull t: CType, cfg: Config): Iterator[Iterator[String]] = Iterator(
      pprint.Internals.handleChunks(
        "extends: ", cfg,
        (c0: Config) => t.declaredSupertypeRefs.toIterator.map(implicitly[PPrint[CTypeRef]].pprinter.render(_, c0))
      ),
      pprint.Internals.handleChunks(
        "supplements: ", cfg,
        (c0: Config) => t.declaredSupplementees.toIterator.map(implicitly[PPrint[CTypeRef]].pprinter.render(_, c0))
      )
    )

  }

  implicit object CVarTypePrinter extends PPrinter[CVarType] {

    override def render0(@NotNull t: CVarType, c: Config): Iterator[String] = {
      def body = (cfg: Config) => CTypePrinter.typeParts(t, cfg) ++ Iterator(
        pprint.Internals.handleChunks(
          "tags: ", cfg, (c0: Config) => t.declaredTags.toIterator.map(CTagPrinter.render(_, c0))
        )
      )
      pprint.Internals.handleChunks("var " + t.name.name, c, body)
    }

  }

  implicit val CVarTypePrint: PPrint[CVarType] = PPrint(CVarTypePrinter)

  implicit object CTagPrinter extends PPrinter[CTag] {

    override def render0(t: CTag, c: Config): Iterator[String] = {
      pprint.Internals.handleChunks(
        t.name + ": " + t.typeRef.name.name, c, (c: Config) => Iterator(
          // TODO tag attributes etc.
        )
      )
    }

  }

  implicit val CTagPrint: PPrint[CTag] = PPrint(CTagPrinter)


  implicit object CRecordTypePrinter extends PPrinter[CRecordType] {

    override def render0(@NotNull t: CRecordType, c: Config): Iterator[String] = {
      def body = (cfg: Config) => CTypePrinter.typeParts(t, cfg) ++ Iterator(
        pprint.Internals.handleChunks(
          "fields: ", cfg, (c0: Config) => t.declaredFields.toIterator.map(CFieldPrint.pprinter.render(_, c0))
        )
      )
      pprint.Internals.handleChunks("record " + t.name.name, c, body)
    }

  }

  implicit val CRecordTypePrint: PPrint[CRecordType] = PPrint(CRecordTypePrinter)

  implicit object CFieldPrinter extends PPrinter[CField] {

    override def render0(t: CField, c: Config): Iterator[String] = {
      pprint.Internals.handleChunks(
        t.name + ": " + t.typeRef.name.name, c, (c: Config) => Iterator(
          // TODO field attributes etc.
        )
      )
    }

  }

  implicit val CFieldPrint: PPrint[CField] = PPrint(CFieldPrinter)


  implicit object CMapTypePrinter extends PPrinter[CMapType] {

    override def render0(@NotNull t: CMapType, c: Config): Iterator[String] = {
      pprint.Internals.handleChunks(
        "map " + t.name.name, c, (c: Config) => CTypePrinter.typeParts(t, c) ++ Iterator(
          Iterator("keyType: ", t.keyTypeRef.name.name),
          Iterator("valueType: ", t.valueTypeRef.name.name)
        )
      )
    }
  }

  implicit val CMapTypePrint: PPrint[CMapType] = PPrint(CMapTypePrinter)


  implicit object CListTypePrinter extends PPrinter[CListType] {

    override def render0(@NotNull t: CListType, c: Config): Iterator[String] = {
      pprint.Internals.handleChunks(
        "list " + t.name.name, c, (c: Config) => CTypePrinter.typeParts(t, c) ++ Iterator(
          Iterator("valueType: ", t.elementTypeRef.name.name)
        )
      )
    }
  }

  implicit val CListTypePrint: PPrint[CListType] = PPrint(CListTypePrinter)


  implicit object CEnumTypePrinter extends PPrinter[CEnumType] {

    override def render0(@NotNull t: CEnumType, c: Config): Iterator[String] = {
      def body = (cfg: Config) => CTypePrinter.typeParts(t, cfg) ++ Iterator(
        pprint.Internals.handleChunks(
          "values: ", cfg, (c0: Config) => t.values.toIterator.map(CEnumValuePrint.pprinter.render(_, c0))
        )
      )
      pprint.Internals.handleChunks("enum " + t.name.name, c, body)
    }

  }

  implicit val CEnumTypePrint: PPrint[CEnumType] = PPrint(CEnumTypePrinter)

  implicit object CEnumValuePrinter extends PPrinter[CEnumValue] {

    override def render0(t: CEnumValue, c: Config): Iterator[String] = {
      Iterator(t.name)
    }

  }

  implicit val CEnumValuePrint: PPrint[CEnumValue] = PPrint(CEnumValuePrinter)


  implicit object CPrimitiveTypePrinter extends PPrinter[CPrimitiveType] {

    override def render0(@NotNull t: CPrimitiveType, c: Config): Iterator[String] = {
      pprint.Internals.handleChunks(
        t.kind.keyword + " " + t.name.name, c, (c: Config) => CTypePrinter.typeParts(t, c) ++ Iterator(
          // TODO enum attributes
        )
      )
    }
  }

  implicit val CPrimitiveTypePrint: PPrint[CPrimitiveType] = PPrint(CPrimitiveTypePrinter)

}
