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

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import static groovy.util.GroovyTestCase.assertEquals

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class IdeaOutputPathTest extends Specification {
  @Rule
  final TemporaryFolder testProjectDir = new TemporaryFolder()
  File buildFile

  def setup() {
    buildFile = testProjectDir.newFile('build.gradle')
  }

  def "idea module main and test output paths are defined and are not the same"() {
    given:
    buildFile << """
plugins {
  id 'idea'
  id 'ws.epigraph.schema' version '0.0'
}

task q << {
  def module = project.idea.module
  if (module == null) throw new Exception('IDEA module not found')

  if (module.inheritOutputDirs != false)
    throw new Exception('IDEA module `inheritOutputDirs` is not `false`')

  def outputDir = module.outputDir
  if (outputDir == null) throw new Exception('IDEA module output dir is null')

  def testOutputDir = module.testOutputDir
  if (testOutputDir == null) throw new Exception('IDEA module test output dir is null')

  if (outputDir == testOutputDir)
    throw new Exception('IDEA module output dir == test output dir')
}

"""

    when:
    def build = GradleRunner.create()
        .withProjectDir(testProjectDir.root)
        .withPluginClasspath()
        .withArguments('q')
        .build()

    then:
    assertEquals(TaskOutcome.SUCCESS, build.task(':q').outcome)
  }
}
