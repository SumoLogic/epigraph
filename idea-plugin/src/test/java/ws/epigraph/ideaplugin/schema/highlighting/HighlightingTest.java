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

package ws.epigraph.ideaplugin.schema.highlighting;

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import ws.epigraph.ideaplugin.schema.features.inspections.ConflictingImportInspection;
import ws.epigraph.ideaplugin.schema.features.inspections.DuplicateImportInspection;
import ws.epigraph.ideaplugin.schema.features.inspections.UnnecessaryImportInspection;
import ws.epigraph.ideaplugin.schema.features.inspections.UnusedImportInspection;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class HighlightingTest extends LightCodeInsightFixtureTestCase {
  @Override
  protected String getTestDataPath() {
    return "src/test/resources/testData/highlighting";
  }

  public void testInvalid1() {
    myFixture.configureByFiles("Invalid1.epigraph", "other.epigraph", "builtin.epigraph", "foo.epigraph", "bar.epigraph");
    myFixture.enableInspections(new DuplicateImportInspection());
    myFixture.enableInspections(new UnnecessaryImportInspection());
    myFixture.enableInspections(new UnusedImportInspection());
    myFixture.enableInspections(new ConflictingImportInspection());
    myFixture.checkHighlighting(true, false, true);
  }

  public void testAmbiguousReference() {
    myFixture.configureByFiles("stringfoo.epigraph", "builtin.epigraph", "string.epigraph");
    myFixture.checkHighlighting(true, false, true);
  }
}
