/*
 * Copyright 2016 Sumo Logic
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

/* Created by yegor on 6/17/16. */

package ws.epigraph.edl.compiler

import java.io.File

import org.jetbrains.annotations.NotNull
import pprint.{Config, PPrint, PPrinter}

import scala.collection.GenTraversableOnce
import scala.collection.JavaConversions._
import scala.language.higherKinds

object CPrettyPrinters {

  implicit object CErrorPrinter extends PPrinter[CError] {

    override def render0(t: CError, c: Config): Iterator[String] = Iterator(
      t.filename, ":", t.position.line.toString, ":", t.position.column.toString, " ", intellijLink(t),
      "\nError: ", t.message, "\n" // TODO skip :line:colon, line text, and ^ if NA
    ) ++ t.position.lineText.iterator ++ Iterator("\n", " " * (t.position.column - 1), "^")

    private def intellijLink(t: CError): String = { // relies on '.' already rendered (as part of canonical path
      "(" + new File(t.filename).getName + ":" + t.position.line + ")"
    }

  }

  implicit val CErrorPrint: PPrint[CError] = PPrint(CErrorPrinter)


  implicit object CEdlFilePrinter extends PPrinter[CEdlFile] {

    override def render0(t: CEdlFile, c: Config): Iterator[String] = {
      pprint.Internals.handleChunks(
        t.filename, c, (c: Config) => Iterator( // TODO PPrint[CNamespace]
          implicitly[PPrint[CNamespace]].pprinter.render(t.namespace, c),
          collection("imports", t.imports, c), // TODO PPrint[CImport]
          collection("typedefs", t.typeDefs, c),
          collection("supplements", t.supplements, c) // TODO PPrint[CSupplement]
        )
      )
    }
  }

  implicit val CEdlFilePrint: PPrint[CEdlFile] = PPrint(CEdlFilePrinter)


  implicit object CTypeRefPrinter extends PPrinter[CTypeRef] {

    override def render0(t: CTypeRef, c: Config): Iterator[String] =
      if (t.isResolved) {
        Iterator(t.name.name)
      } else {
        Iterator("«", t.name.name, "»")
      }

  }

  implicit val CTypeRefPrint: PPrint[CTypeRef] = PPrint(CTypeRefPrinter)


  implicit object CTypeNamePrinter extends PPrinter[CTypeName] {

    override def render0(t: CTypeName, c: Config): Iterator[String] = Iterator(t.name)

  }

  implicit val CTypeNamePrint: PPrint[CTypeName] = PPrint(CTypeNamePrinter)


  implicit object CTypePrinter extends PPrinter[CType] {

    override def render0(t: CType, c: Config): Iterator[String] = {
      t match {
        case td: CTypeDef => CTypeDefPrinter.render(td, c)
        case amt: CAnonMapType => CAnonMapTypePrinter.render(amt, c)
        case alt: CAnonListType => CAnonListTypePrinter.render(alt, c)
        case _ => Iterator("UNKNOWN ", t.name.name)
      }
    }

  }

  implicit val CTypePrint: PPrint[CType] = PPrint(CTypePrinter)

  implicit def cTypePrint[T <: CType]: PPrint[T] = CTypePrint.asInstanceOf[PPrint[T]]


  implicit object CTypeDefPrinter extends PPrinter[CTypeDef] {

    override def render0(td: CTypeDef, c: Config): Iterator[String] = td match {
      case typeDef: CVarTypeDef => CVarTypePrinter.render(typeDef, c)
      case typeDef: CRecordTypeDef => CRecordTypePrinter.render(typeDef, c)
      case typeDef: CMapTypeDef => CMapTypePrinter.render(typeDef, c)
      case typeDef: CListTypeDef => CListTypePrinter.render(typeDef, c)
      case typeDef: CEnumTypeDef => CEnumTypePrinter.render(typeDef, c)
      case typeDef: CPrimitiveTypeDef => CPrimitiveTypePrinter.render(typeDef, c)
      case _ => Iterator("UNKNOWN ", td.name.name)
    }

    def typeDefParts(@NotNull t: CTypeDef, c: Config): Iterator[Iterator[String]] = Iterator(
      collection("extendedParents", t.extendedTypeRefs, c),
      collection("injectedParents", t.injectedTypes, c, (x: CTypeDef) => x.name),
      collection("resolvedParents", t.parents, c, (x: CType) => x.name),
      collection("linearizedParents", t.linearizedParents, c, { x: CType => x.name }),
      collection("linearization", t.linearization, c, (x: CType) => x.name),
      collection("effectiveSupertypes", t.supertypes, c, (x: CType) => x.name),
      collection("supplementedSubtypes", t.supplementedTypeRefs, c)
    )

  }

  //implicit val CTypeDefPrint: PPrint[CTypeDef] = PPrint(CTypeDefPrinter)

  private def collection[M[A, B] <: scala.collection.GenMap[A, B], K: PPrint, V: PPrint](
      name: String,
      source: M[K, V],
      c: Config
  ): Iterator[String] = if (source == null) {
    pprint.Internals.handleChunks(name, c, (c: Config) => Iterator(Iterator("?")))
  } else {
    pprint.Internals.handleChunks(
      name, c, { c =>
        source.iterator.map { case (k, v) =>
          implicitly[PPrint[K]].pprinter.render(k, c) ++ Iterator(": ") ++ implicitly[PPrint[V]].pprinter.render(v, c)
        }
      }
    )
  }

  private def collection[A: PPrint](name: String, source: GenTraversableOnce[A], c: Config): Iterator[String] =
    collection(name, source, c, identity[A])

  private def collection[A, B: PPrint](
      name: String,
      source: GenTraversableOnce[A],
      c: Config,
      trans: (A) => B
  ): Iterator[String] = if (source == null) {
    pprint.Internals.handleChunks(name, c, (c: Config) => Iterator(Iterator("?")))
  } else {
    pprint.Internals.handleChunks(
      name, c, { c => source.toIterator.map { a: A => implicitly[PPrint[B]].pprinter.render(trans(a), c) } }
    )
  }


  implicit object CVarTypePrinter extends PPrinter[CVarTypeDef] {

    override def render0(@NotNull t: CVarTypeDef, c: Config): Iterator[String] = {
      def body = (c: Config) => CTypeDefPrinter.typeDefParts(t, c) ++ Iterator(
        collection("declaredTags", t.declaredTags, c),
        collection("effectiveTags", t.effectiveTags, c)
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
      def body = (c: Config) => CTypeDefPrinter.typeDefParts(t, c) ++ Iterator(
        collection("declaredFields", t.declaredFields, c),
        collection("effectiveFields", t.effectiveFields, c)
      )
      pprint.Internals.handleChunks("record " + t.name.name, c, body)
    }

  }

  implicit val CRecordTypePrint: PPrint[CRecordTypeDef] = PPrint(CRecordTypePrinter)

  implicit object CFieldPrinter extends PPrinter[CField] {

    override def render0(t: CField, c: Config): Iterator[String] = {
      pprint.Internals.handleChunks(
        t.name + ": " + CTypeRefPrinter.render(t.typeRef, c).mkString, c, (c: Config) => Iterator(
          // TODO field attributes etc.
        )
      )
    }

  }

  implicit val CFieldPrint: PPrint[CField] = PPrint(CFieldPrinter)


  implicit object CAnonMapTypePrinter extends PPrinter[CAnonMapType] {

    override def render0(@NotNull t: CAnonMapType, c: Config): Iterator[String] = {
      pprint.Internals.handleChunks(
        t.name.name, c, (c: Config) => Iterator(
          Iterator("keyType: ") ++ CTypeRefPrinter.render(t.keyTypeRef, c),
          Iterator("valueType: ") ++ CTypeRefPrinter.render(t.valueTypeRef, c)
        )
      )
    }
  }

  implicit val CAnonMapTypePrint: PPrint[CAnonMapType] = PPrint(CAnonMapTypePrinter)

  implicit object CMapTypePrinter extends PPrinter[CMapTypeDef] {

    override def render0(@NotNull t: CMapTypeDef, c: Config): Iterator[String] = {
      pprint.Internals.handleChunks(
        "map " + CTypeNamePrinter.render(t.name, c).mkString, c,
        (c: Config) => CTypeDefPrinter.typeDefParts(t, c) ++ Iterator(
          Iterator("keyType: ") ++ CTypeRefPrinter.render(t.keyTypeRef, c),
          Iterator("valueType: ") ++ CTypeRefPrinter.render(t.valueTypeRef, c)
        )
      )
    }
  }

  implicit val CMapTypePrint: PPrint[CMapTypeDef] = PPrint(CMapTypePrinter)


  implicit object CAnonListTypePrinter extends PPrinter[CAnonListType] {

    override def render0(@NotNull t: CAnonListType, c: Config): Iterator[String] = {
      pprint.Internals.handleChunks(
        t.name.name, c, (c: Config) => Iterator(
          Iterator("elementType: ") ++ CTypeRefPrinter.render(t.elementTypeRef, c)
        )
      )
    }
  }

  implicit val CAnonListTypePrint: PPrint[CAnonListType] = PPrint(CAnonListTypePrinter)


  implicit object CListTypePrinter extends PPrinter[CListTypeDef] {

    override def render0(@NotNull t: CListTypeDef, c: Config): Iterator[String] = {
      pprint.Internals.handleChunks(
        "list " + t.name.name, c, (c: Config) => CTypeDefPrinter.typeDefParts(t, c) ++ Iterator(
          Iterator("elementType: ") ++ CTypeRefPrinter.render(t.elementTypeRef, c)
        )
      )
    }
  }

  implicit val CListTypePrint: PPrint[CListTypeDef] = PPrint(CListTypePrinter)


  implicit object CEnumTypePrinter extends PPrinter[CEnumTypeDef] {

    override def render0(@NotNull t: CEnumTypeDef, c: Config): Iterator[String] = {
      def body = (c: Config) => CTypeDefPrinter.typeDefParts(t, c) ++ Iterator(
        collection("values", t.values, c)
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
        t.kind.keyword + " " + t.name.name, c, (c: Config) => CTypeDefPrinter.typeDefParts(t, c) ++ Iterator(
          // TODO enum attributes
        )
      )
    }
  }

  implicit val CPrimitiveTypePrint: PPrint[CPrimitiveTypeDef] = PPrint(CPrimitiveTypePrinter)

}
