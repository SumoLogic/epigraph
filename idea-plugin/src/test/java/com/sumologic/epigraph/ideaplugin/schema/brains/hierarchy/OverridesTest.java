package com.sumologic.epigraph.ideaplugin.schema.brains.hierarchy;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import io.epigraph.lang.parser.psi.EpigraphFieldDecl;
import io.epigraph.lang.parser.psi.EpigraphVarTagDecl;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class OverridesTest extends LightCodeInsightFixtureTestCase {
  @Override
  protected String getTestDataPath() {
    return "src/test/resources/testData/brains/hierarchy";
  }

  public void testQuotedVarTagOverride() {
    myFixture.configureByFile("QuotedTagOverride.esc");
    PsiElement element = myFixture.getFile().findElementAt(myFixture.getCaretOffset());
    EpigraphVarTagDecl tagDecl = PsiTreeUtil.getParentOfType(element, EpigraphVarTagDecl.class);
    assertNotNull(tagDecl);
    assertEquals(1, TypeMembers.getOverridenTags(tagDecl).size());
  }

  public void testQuotedFieldOverride() {
    myFixture.configureByFile("QuotedFieldOverride.esc");
    PsiElement element = myFixture.getFile().findElementAt(myFixture.getCaretOffset());
    EpigraphFieldDecl fieldDecl = PsiTreeUtil.getParentOfType(element, EpigraphFieldDecl.class);
    assertNotNull(fieldDecl);
    assertEquals(1, TypeMembers.getOverridenFields(fieldDecl).size());
  }
}
