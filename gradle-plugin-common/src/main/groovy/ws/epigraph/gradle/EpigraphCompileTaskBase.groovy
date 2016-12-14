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

import org.gradle.api.GradleException
import org.gradle.api.artifacts.Configuration
import org.gradle.api.file.FileCollection
import org.gradle.api.file.FileTree
import org.gradle.api.logging.Logger
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.ParallelizableTask
import ws.epigraph.edl.compiler.*

import java.nio.charset.StandardCharsets
import java.util.jar.JarFile

import static EpigraphConstants.SCHEMA_FILE_EXTENSION
import static EpigraphConstants.SCHEMA_FILE_PATH_PATTERN

@ParallelizableTask
trait EpigraphCompileTaskBase {
  // implementations decide on their own if they want to extend `DefaultTask` or `SourceTask`

  private Configuration configuration

  void setConfiguration(Configuration configuration) {
    this.configuration = configuration
  }

  public CContext compileFiles() {
    Collection<Source> sources = getFileSources()

    Collection<Source> dependencySources = new ArrayList<>()
    dependencySources.addAll(getDependencySources())

    return compileFiles(sources, dependencySources)
  }

  @Internal
  List<FileSource> getFileSources() {
    getSource().files.collect { new FileSource(it) }
  }

  @Internal
  public abstract Logger getLogger();

  @InputFiles
  public abstract FileTree getSource();

  @InputFiles
  public FileCollection getDependencies() {
    getLogger().debug("Getting dependencies from ${configuration}")
    return configuration
  }

  @Internal
  private Collection<Source> getDependencySources() {
    def dependencies = getDependencies()
    if (dependencies == null) return Collections.emptyList()

    Collection<Source> dependencySources = new ArrayList<>()

    dependencies.files.each {
      if (it.name.endsWith('.jar')) {
        // TODO take charset from build props
        JarSource.allFiles(new JarFile(it), SCHEMA_FILE_PATH_PATTERN, StandardCharsets.UTF_8).each {
          dependencySources.add(it)
        }
      } else if (it.name.endsWith(SCHEMA_FILE_EXTENSION)) { // there may be direct dependencies on files, see examples/users/schema/build.gradle
        dependencySources.add(new FileSource(it))
      } else throw new GradleException("Don't know how to handle dependency: '$it'")
    }

    return dependencySources
  }

  public CContext compileFiles(Collection<Source> sources, Collection<Source> dependencySources) {
    try {
      SchemaCompiler compiler = new SchemaCompiler(sources, dependencySources)
      return compiler.compile()
    } catch (SchemaCompilerException e) {
      throw new GradleException('Epigraph resources compilation failed', e)
    }
  }
}
