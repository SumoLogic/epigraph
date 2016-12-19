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

package ws.epigraph.ideaplugin.schema.features.usages;

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import com.intellij.usageView.UsageInfo;

import java.util.Collection;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class FindUsagesTest extends LightCodeInsightFixtureTestCase {
  @Override
  protected String getTestDataPath() {
    return "src/test/resources/testData/features/search";
  }

  public void testFindTypeUsages() {
    Collection<UsageInfo> usageInfos = myFixture.testFindUsages("FindTypeUsagesTestData.epigraph");
    assertEquals(2, usageInfos.size());
  }

  public void testFindVarTagUsages() {
    Collection<UsageInfo> usageInfos = myFixture.testFindUsages("FindVarTagUsagesTestData.epigraph");
    assertEquals(1, usageInfos.size());
  }
}
