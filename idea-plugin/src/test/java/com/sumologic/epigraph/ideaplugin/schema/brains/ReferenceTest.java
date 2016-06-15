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
    myFixture.configureByFile("SameNsTypeRef.epi_schema");
    PsiElement element = myFixture.getFile().findElementAt(myFixture.getCaretOffset());
    //noinspection ConstantConditions
    checkReference(element.getParent().getParent().getReference(), "Bar");
  }

  public void testFqnImportRef() {
    myFixture.configureByFiles("FqnImportTypeRef.epi_schema", "TargetNs.epi_schema");
    PsiElement element = myFixture.getFile().findElementAt(myFixture.getCaretOffset());
    //noinspection ConstantConditions
    PsiReference reference = element.getParent().getParent().getReference();
    assertNotNull(reference);
    checkReference(reference, "ZZLong");
    Object[] variants = reference.getVariants();
    assertEquals(3, variants.length); // "ZZLong", "R", "target"
  }

  public void testNsSegmentInTypeRef() {
    myFixture.configureByFile("NamespaceSegmentInTypeRef.epi_schema");
    PsiElement element = myFixture.getFile().findElementAt(myFixture.getCaretOffset());
    //noinspection ConstantConditions
    checkReference(element.getParent().getParent().getReference(), "namespace foo");
  }

  @SuppressWarnings("ConstantConditions")
  public void testMultiNsRef() {
    myFixture.configureByFiles("MultiNamespaceRef.epi_schema", "foobar.epi_schema", "foobaz.epi_schema");
    PsiElement element = myFixture.getFile().findElementAt(myFixture.getCaretOffset());
    PsiPolyVariantReference reference = (PsiPolyVariantReference) element.getParent().getParent().getReference();
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

  public void testVarTagRef() {
    PsiReference reference = myFixture.getReferenceAtCaretPosition("VarTagRef.epi_schema");
    checkReference(reference, "tag1");

    reference = myFixture.getReferenceAtCaretPosition("VarTagRef2.epi_schema");
    checkReference(reference, "tag1");
  }

  ////////////////////////////////

  private void checkReference(PsiReference reference, String text) {
    assertNotNull(reference);
    PsiElement target = reference.resolve();
    assertNotNull(target);
    String actualText = target instanceof PsiNamedElement ? ((PsiNamedElement) target).getName() : target.getText();
    assertEquals(text, actualText);
    assertTrue(reference.isReferenceTo(target));
  }
}
