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

package ws.epigraph.ideaplugin.schema.brains;

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import ws.epigraph.lang.Qn;
import ws.epigraph.schema.parser.psi.EdlFile;
import ws.epigraph.schema.parser.psi.EdlImportStatement;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ImportsManagerTest extends LightCodeInsightFixtureTestCase {
  @Override
  protected String getTestDataPath() {
    return "src/test/resources/testData/brains";
  }

  public void testFindUnusedImports() {
    myFixture.configureByFiles("UnusedImports.epigraph", "foo.epigraph", "foobar.epigraph");
    Set<EdlImportStatement> unusedImports = ImportsManager.findUnusedImports((EdlFile) myFixture.getFile());

    Set<String> strings = unusedImports.stream().map(EdlImportStatement::getText).collect(Collectors.toSet());
    assertEquals(new HashSet<>(Arrays.asList("import foo", "import foo.Y", "import foo.Z")), strings);
  }

  public void testOptimizeImports() {
    myFixture.configureByFiles("UnusedImports.epigraph", "foo.epigraph", "foobar.epigraph");
    List<Qn> optimizedImports = ImportsManager.getOptimizedImports((EdlFile) myFixture.getFile());

    assertEquals(Arrays.asList(
        new Qn[]{
            Qn.fromDotSeparated("foo.X"),
            Qn.fromDotSeparated("foo.bar")
        }
    ), optimizedImports);

  }
}
