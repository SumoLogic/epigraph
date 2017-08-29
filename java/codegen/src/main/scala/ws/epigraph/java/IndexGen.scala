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

import java.io.{PrintWriter, StringWriter}
import java.nio.file.{Path, Paths}
import java.util.Properties

import ws.epigraph.gen.Constants
import ws.epigraph.java.NewlineStringInterpolator.NewlineHelper

import scala.collection.JavaConverters._

/**
 * @author yegor 2016-12-15.
 */
class IndexGen(protected val ctx: GenContext) extends JavaGen {

  /** Index should be generated under generated resources root folder. */
  final override protected def pickRoot(sourcesRoot: Path, resourcesRoot: Path): Path = resourcesRoot

  override def relativeFilePath: Path = Paths.get(Constants.TypesIndex.resourcePath)

  // TODO this needs to be refactored to produce structured json/yaml instead of .properties

  override protected def generate: String = {
    val properties = new Properties
    ctx.generatedTypes.asScala.foreach(kv => properties.setProperty(kv._1.name, kv._2 + "$Type"))
    val writer = new StringWriter // doesn't need to be closed
    properties.store(new PrintWriter(writer), null)
    /*@formatter:off*/sn"""\
# Mappings from canonical fully-qualified Epigraph type names to generated Java class names for the types.
#
# ${JavaGenUtils.generatedAnnotation(this)}
#
$writer\
"""/*@formatter:on*/
  }

}
