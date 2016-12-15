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

package ws.epigraph.ideaplugin.edl.brains.hierarchy;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import ws.epigraph.edl.parser.psi.EdlFieldDecl;
import ws.epigraph.edl.parser.psi.EdlVarTagDecl;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OverridesTest extends LightCodeInsightFixtureTestCase {
  @Override
  protected String getTestDataPath() {
    return "src/test/resources/testData/brains/hierarchy";
  }

  public void testQuotedVarTagOverride() {
    myFixture.configureByFile("QuotedTagOverride.epigraph");
    PsiElement element = myFixture.getFile().findElementAt(myFixture.getCaretOffset());
    EdlVarTagDecl tagDecl = PsiTreeUtil.getParentOfType(element, EdlVarTagDecl.class);
    assertNotNull(tagDecl);
    assertEquals(1, TypeMembers.getOverridenTags(tagDecl).size());
  }

  public void testQuotedFieldOverride() {
    myFixture.configureByFile("QuotedFieldOverride.epigraph");
    PsiElement element = myFixture.getFile().findElementAt(myFixture.getCaretOffset());
    EdlFieldDecl fieldDecl = PsiTreeUtil.getParentOfType(element, EdlFieldDecl.class);
    assertNotNull(fieldDecl);
    assertEquals(1, TypeMembers.getOverridenFields(fieldDecl).size());
  }
}
