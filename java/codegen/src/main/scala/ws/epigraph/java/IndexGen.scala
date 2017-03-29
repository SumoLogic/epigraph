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

package ws.epigraph.java

import java.nio.file.{Path, Paths}

import ws.epigraph.gen.Constants
import ws.epigraph.java.NewlineStringInterpolator.NewlineHelper

import scala.collection.JavaConverters._

/**
 * @author yegor 2016-12-15.
 */
class IndexGen(protected val ctx: GenContext) extends JavaGen {

    private val IndexClassName: String = Constants.TypesIndex.className

    override protected def relativeFilePath: Path =
      Paths.get(s"${Constants.TypesIndex.namespace.replaceAll("\\.", "/")}/$IndexClassName.java")

    override protected def generate: String = /*@formatter:off*/sn"""\
package epigraph.java;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.types.Type;
import ws.epigraph.util.Unmodifiable;

import java.util.LinkedHashMap;
import java.util.Map;

public final class $IndexClassName {

  public static final @NotNull Map<@NotNull String, @NotNull Type> types = types();

  private $IndexClassName() {}

  private static @NotNull Map<@NotNull String, @NotNull Type> types() {
    Map<@NotNull String, @NotNull Type> types = new LinkedHashMap<>();

${ctx.generatedTypes.asScala.toSeq./*TODO better*/sortWith((a, b) => a._1.name < b._1.name).map { entry => sn"""\
    types.put("${entry._1.name}", ${entry._2}.Type.instance());
"""
  }.mkString
}\

    return Unmodifiable.map_(types);
  }

}
"""/*@formatter:on*/

}
