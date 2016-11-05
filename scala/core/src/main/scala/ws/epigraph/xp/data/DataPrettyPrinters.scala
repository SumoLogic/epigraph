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

package ws.epigraph.xp.data

import ws.epigraph.names.FieldName
import pprint.{Config, PPrint, PPrinter}

import scala.util.{Failure, Success}

object DataPrettyPrinters {
  private val DatumPrettyPrint = PPrint(DatumPrettyPrinter)

  implicit def datumPrettyPrint[D <: Datum[_]]: PPrint[D] = DatumPrettyPrint.asInstanceOf[PPrint[D]]

  private object DatumPrettyPrinter extends PPrinter[Datum[_]] {
    override def render0(t: Datum[_], c: Config): Iterator[String] = t match {
      case rd: RecordDatum[_] => RecordPrettyPrinter.render0(rd, c)
      case md: MapDatum[_, _] => MapPrettyPrinter.render0(md, c)
      case ld: ListDatum[_] => ListPrettyPrinter.render0(ld, c)
      case ed: EnumDatum[_] => EnumPrettyPrinter.render0(ed, c)
      case pd: PrimitiveDatum[_] => PrimitivePrettyPrinter.render0(pd, c)
      case _ => throw new IllegalArgumentException(t.getClass.toString)
    }
  }

  private object VarPrettyPrinter extends PPrinter[Var[_]] {
    // todo separate handling of MonoVar ?
    override def render0(t: Var[_], c: Config): Iterator[String] =
      pprint.Internals.handleChunks(
        "var",
        c,
        (c: Config) => t.varEntriesIterator.map {
          case (tn, ve) => Iterator(
            tn.string,
            "="
          ) ++ VarEntryPrettyPrinter.render(ve, c)
        }
      )
  }

  implicit def varPrinter[V <: Var[_]]: PPrint[V] = PPrint(VarPrettyPrinter)

  private object VarEntryPrettyPrinter extends PPrinter[VarEntry[_]] {
    override def render0(t: VarEntry[_], c: Config): Iterator[String] = t.value match {
      case Success(v) => DatumPrettyPrinter.render(v.asInstanceOf[Datum[_]], c)
      case Failure(exc) => Iterator(exc.toString)
    }
  }

  private object RecordPrettyPrinter extends PPrinter[RecordDatum[_]] {
    override def render0(t: RecordDatum[_], c: Config): Iterator[String] =
      pprint.Internals.handleChunks(
        t.dataType.name.string,
        c,
        (c: Config) => t.toIterator.map {
          case (fn: FieldName, v: Var[_]) => Iterator(
            fn.string,
            "="
          ) ++ VarPrettyPrinter.render(v, c)
        }
      )
  }

  private object MapPrettyPrinter extends PPrinter[MapDatum[_, _]] {
    override def render0(t: MapDatum[_, _], c: Config): Iterator[String] = ???
  }

  private object ListPrettyPrinter extends PPrinter[ListDatum[_]] {
    override def render0(t: ListDatum[_], c: Config): Iterator[String] = ???
  }

  private object EnumPrettyPrinter extends PPrinter[EnumDatum[_]] {
    override def render0(t: EnumDatum[_], c: Config): Iterator[String] =
      Iterator(t.name.string)
  }

  private object PrimitivePrettyPrinter extends PPrinter[PrimitiveDatum[_]] {
    override def render0(t: PrimitiveDatum[_], c: Config): Iterator[String] =
      Iterator(String.valueOf(t.native))
  }

}