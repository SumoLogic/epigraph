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

package ws.epigraph.gradle

import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.internal.file.SourceDirectorySetFactory
import org.gradle.util.ConfigureUtil

class DefaultEpigraphSourceSet implements EpigraphSourceSet {
  private final SourceDirectorySet epigraph

  DefaultEpigraphSourceSet(String displayName, SourceDirectorySetFactory sourceDirectorySetFactory) {
    epigraph = sourceDirectorySetFactory.create(displayName + ' Epigraph Schema source')
    epigraph.getFilter().include('**/*.' + EpigraphConstants.SCHEMA_FILE_EXTENSION)
  }

  @Override
  SourceDirectorySet getEpigraph() {
    return epigraph
  }

  @Override
  EpigraphSourceSet epigraph(Closure closure) {
    ConfigureUtil.configure(closure, getEpigraph())
    return this
  }
}
