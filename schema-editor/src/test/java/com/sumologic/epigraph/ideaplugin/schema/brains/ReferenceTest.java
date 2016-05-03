package com.sumologic.epigraph.ideaplugin.schema.brains;

import com.intellij.psi.PsiElement;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaTypeDef;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class ReferenceTest extends LightCodeInsightFixtureTestCase {
  @Override
  protected String getTestDataPath() {
    return "src/test/resources/testData/reference";
  }

  public void testSameNamespaceRef() {
    myFixture.configureByFile("SameNsTypeRef.es");
    PsiElement element = myFixture.getFile().findElementAt(myFixture.getCaretOffset());
    //noinspection ConstantConditions
    assertEquals("Bar", ((SchemaTypeDef) element.getParent().getReference().resolve()).getName());
  }

  public void testFqnImportRef() {
    myFixture.configureByFiles("FqnImportTypeRef.es", "TargetNs.es");
    PsiElement element = myFixture.getFile().findElementAt(myFixture.getCaretOffset());
    //noinspection ConstantConditions
    assertEquals("ZZLong", ((SchemaTypeDef) element.getParent().getReference().resolve()).getName());
  }
}
