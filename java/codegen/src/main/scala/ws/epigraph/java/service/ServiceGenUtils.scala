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

package ws.epigraph.java.service

import ws.epigraph.java.JavaGenUtils
import ws.epigraph.refs.TypeReferenceFactory
import ws.epigraph.types._

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
object ServiceGenUtils {
  val INDENT = 2 // default indent

  def genList(items: Seq[String], ctx: ServiceGenContext): String = {
    if (items.isEmpty) {
      ctx.addImport("java.util.Collections")
      "Collections.emptyList()"
    } else if (items.size == 1) {
      ctx.addImport("java.util.Collections")
      s"Collections.singletonList(${items.head})"
    } else {
      ctx.addImport("java.util.Arrays")
      val indentedItems = items.map(JavaGenUtils.indent(_, INDENT))
      indentedItems.mkString("Arrays.asList(\n", ",\n", "\n)")
    }
  }

  def genLinkedMap(
    keyType: String,
    valueType: String,
    entries: Iterable[(String, String)],
    ctx: ServiceGenContext): String = genMap("LinkedHashMap", keyType, valueType, entries, ctx)

  def genHashMap(
    keyType: String,
    valueType: String,
    entries: Iterable[(String, String)],
    ctx: ServiceGenContext): String = genMap("HashMap", keyType, valueType, entries, ctx)

  def genMap(
    mapClass: String,
    keyType: String,
    valueType: String,
    entries: Iterable[(String, String)],
    ctx: ServiceGenContext): String = {

    ctx.addImport(s"java.util.$mapClass")

    if (entries.isEmpty) s"new $mapClass<$keyType, $valueType>()"
    else {
      ctx.addImport("java.util.AbstractMap")
      ctx.addImport("java.util.Map")
      ctx.addImport("java.util.stream.Collectors")
      ctx.addImport("java.util.stream.Stream")

      val indent = JavaGenUtils.spaces(INDENT)
      val generatedEntries = entries.map{ case (k, v) => s"${indent}new AbstractMap.SimpleEntry<>($k, $v)" }
      generatedEntries.mkString(
        s"Stream.<AbstractMap.Entry<$keyType, $valueType>>of(\n",
        ",\n",
        s"\n).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (x, y) -> y, $mapClass::new))"
      )
    }
  }

  def genType(typeClass: String, t: TypeApi, ctx: ServiceGenContext): String = {
    val ref = TypeReferenceFactory.createReference(t)
    val tg = s"typesResolver.resolve(${ServiceObjectGen.gen(ref, ctx)})"
    if (typeClass == null) tg else s"($typeClass) $tg"
  }

  def genField(t: RecordTypeApi, f : FieldApi, ctx: ServiceGenContext): String = {
    ctx.addImport(classOf[RecordType].getName)
    s"""(${ServiceGenUtils.genType("RecordType", t, ctx)}).fieldsMap().get("${f.name()}")"""
  }

  def genTag(t: TypeApi, tag : TagApi, ctx: ServiceGenContext): String = {
    ctx.addImport(classOf[TypeApi].getName)
    s"""(${ServiceGenUtils.genType(null, t, ctx)}).tagsMap().get("${tag.name()}")"""
  }
}
