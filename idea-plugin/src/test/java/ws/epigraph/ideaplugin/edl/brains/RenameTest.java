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

package ws.epigraph.ideaplugin.edl.brains;

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import ws.epigraph.edl.parser.psi.SchemaFile;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class RenameTest extends LightCodeInsightFixtureTestCase {
  @Override
  protected String getTestDataPath() {
    return "src/test/resources/testData/rename";
  }

  public void testTypeRename() {
    myFixture.configureByFile("TypeRename.esc");
    myFixture.renameElementAtCaret("GGG");
    myFixture.checkResultByFile("TypeRename-after.esc", false);
    // also check manually
    //noinspection ConstantConditions
    assertEquals("GGG", ((SchemaFile) (myFixture.getFile())).getDefs().getTypeDefWrapperList().get(0).getRecordTypeDef().getName());
  }
}
