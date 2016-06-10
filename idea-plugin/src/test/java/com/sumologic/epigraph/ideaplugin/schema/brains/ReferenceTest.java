package com.sumologic.epigraph.ideaplugin.schema.brains;

import com.intellij.psi.*;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
    checkReference(element.getParent().getReference(), "Bar");
  }

  public void testFqnImportRef() {
    myFixture.configureByFiles("FqnImportTypeRef.es", "TargetNs.es");
    PsiElement element = myFixture.getFile().findElementAt(myFixture.getCaretOffset());
    //noinspection ConstantConditions
    checkReference(element.getParent().getReference(), "ZZLong");
  }

  public void testNsSegmentInTypeRef() {
    myFixture.configureByFile("NamespaceSegmentInTypeRef.es");
    PsiElement element = myFixture.getFile().findElementAt(myFixture.getCaretOffset());
    //noinspection ConstantConditions
    checkReference(element.getParent().getReference(), "namespace foo");
  }

  private void checkReference(PsiReference reference, String text) {
    assertNotNull(reference);
    PsiElement target = reference.resolve();
    assertNotNull(target);
    String actualText = target instanceof PsiNamedElement ? ((PsiNamedElement) target).getName() : target.getText();
    assertEquals(text, actualText);
    assertTrue(reference.isReferenceTo(target));
  }

  @SuppressWarnings("ConstantConditions")
  public void testMultiNsRef() {
    myFixture.configureByFiles("MultiNamespaceRef.es", "foobar.es", "foobaz.es");
    PsiElement element = myFixture.getFile().findElementAt(myFixture.getCaretOffset());
    PsiPolyVariantReference reference = (PsiPolyVariantReference) element.getParent().getReference();
    assertEquals(null, reference.resolve());

    List<ResolveResult> resolveResults = Arrays.asList(reference.multiResolve(true));
    List<String> variants = resolveResults.stream()
        .map(e -> e.getElement().getParent().getText())
//        .map(e -> e.getElement().getParent().getParent().getText())
        .collect(Collectors.toList());

    assertEquals(2, variants.size());
    assertTrue(variants.contains("namespace foo.bar"));
    assertTrue(variants.contains("namespace foo.baz"));
  }
}
