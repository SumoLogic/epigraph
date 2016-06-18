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
    checkReference(element.getParent().getParent().getReference(), "Bar", "SameNsTypeRef.epi_schema");
  }

  public void testBuiltinRef() {
    myFixture.configureByFiles("BuiltinImport.epi_schema", "builtin.epi_schema");
    PsiElement element = myFixture.getFile().findElementAt(myFixture.getCaretOffset());
    //noinspection ConstantConditions
    checkReference(element.getParent().getParent().getReference(), "Long", "builtin.epi_schema");
  }

  public void testFqnImportRef() {
    myFixture.configureByFiles("FqnImportTypeRef.epi_schema", "TargetNs.epi_schema");
    PsiElement element = myFixture.getFile().findElementAt(myFixture.getCaretOffset());
    //noinspection ConstantConditions
    PsiReference reference = element.getParent().getParent().getReference();
    assertNotNull(reference);
    checkReference(reference, "ZZLong", "TargetNs.epi_schema"); // not same file!
    Object[] variants = reference.getVariants();
    assertEquals(Arrays.toString(variants), 4, variants.length); // "ZZLong", "ZZLong", "R", "target"
  }

  public void testNsSegmentInTypeRef() {
    myFixture.configureByFile("NamespaceSegmentInTypeRef.epi_schema");
    PsiElement element = myFixture.getFile().findElementAt(myFixture.getCaretOffset());
    //noinspection ConstantConditions
    checkReference(element.getParent().getParent().getReference(), "namespace foo", "NamespaceSegmentInTypeRef.epi_schema");
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

  public void testQuotedRef() {
    myFixture.configureByFile("QuotedRefTest.epi_schema");
    PsiElement element = myFixture.getFile().findElementAt(myFixture.getCaretOffset());
    //noinspection ConstantConditions
    checkReference(element.getParent().getParent().getReference(), "Bar", "QuotedRefTest.epi_schema");
  }

  public void testQuotedTargetRef() {
    myFixture.configureByFile("QuotedTargetRefTest.epi_schema");
    PsiElement element = myFixture.getFile().findElementAt(myFixture.getCaretOffset());
    //noinspection ConstantConditions
    checkReference(element.getParent().getParent().getReference(), "Bar", "QuotedTargetRefTest.epi_schema");
  }

  public void testQuotedSourceRef() {
    myFixture.configureByFile("QuotedSourceRefTest.epi_schema");
    PsiElement element = myFixture.getFile().findElementAt(myFixture.getCaretOffset());
    //noinspection ConstantConditions
    checkReference(element.getParent().getParent().getReference(), "Bar", "QuotedSourceRefTest.epi_schema");
  }

  public void testVarTagRef() {
    PsiReference reference = myFixture.getReferenceAtCaretPosition("VarTagRef.epi_schema");
    checkReference(reference, "tag1", "VarTagRef.epi_schema");

    reference = myFixture.getReferenceAtCaretPosition("VarTagRef2.epi_schema");
    checkReference(reference, "tag1", "VarTagRef2.epi_schema");
  }

  ////////////////////////////////

  private void checkReference(PsiReference reference, String text, String targetFileName) {
    assertNotNull(reference);
    PsiElement target = reference.resolve();
    assertNotNull(target);
    String fileName = target.getContainingFile().getName();
    assertEquals(targetFileName, fileName);
    String actualText = target instanceof PsiNamedElement ? ((PsiNamedElement) target).getName() : target.getText();
    assertEquals(text, actualText);
    assertTrue(reference.isReferenceTo(target));
  }
}
