package com.sumologic.epigraph.ideaplugin.schema.brains;

import com.intellij.psi.*;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import io.epigraph.lang.parser.psi.SchemaFile;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class ReferenceTest extends LightCodeInsightFixtureTestCase {
  @Override
  protected String getTestDataPath() {
    return "src/test/resources/testData/brains/reference";
  }

  public void testSameNamespaceRef() {
    myFixture.configureByFile("SameNsTypeRef.esc");
    PsiElement element = myFixture.getFile().findElementAt(myFixture.getCaretOffset());
    //noinspection ConstantConditions
    checkReference(element.getParent().getParent().getReference(), "Bar", "SameNsTypeRef.esc");
  }

  public void testBuiltinRef() {
    myFixture.configureByFiles("BuiltinImport.esc", "builtin.esc");
    PsiElement element = myFixture.getFile().findElementAt(myFixture.getCaretOffset());
    //noinspection ConstantConditions
    checkReference(element.getParent().getParent().getReference(), "Long", "builtin.esc");
  }

  public void testFqnImportRef() {
    myFixture.configureByFiles("FqnImportTypeRef.esc", "TargetNs.esc");
    PsiElement element = myFixture.getFile().findElementAt(myFixture.getCaretOffset());
    //noinspection ConstantConditions
    PsiReference reference = element.getParent().getParent().getReference();
    assertNotNull(reference);
    checkReference(reference, "ZZLong", "TargetNs.esc"); // not same file!
    Object[] variants = reference.getVariants();
    assertEquals(Arrays.toString(variants), 4, variants.length); // "ZZLong", "ZZLong", "R", "target"
  }

  public void testNsSegmentInTypeRef() {
    myFixture.configureByFile("NamespaceSegmentInTypeRef.esc");
    PsiElement element = myFixture.getFile().findElementAt(myFixture.getCaretOffset());
    //noinspection ConstantConditions
    checkReference(element.getParent().getParent().getReference(), "namespace foo", "NamespaceSegmentInTypeRef.esc");
  }

  @SuppressWarnings("ConstantConditions")
  public void testMultiNsRef() {
    myFixture.configureByFiles("MultiNamespaceRef.esc", "foobar.esc", "foobaz.esc");
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
    myFixture.configureByFile("QuotedRefTest.esc");
    PsiElement element = myFixture.getFile().findElementAt(myFixture.getCaretOffset());
    //noinspection ConstantConditions
    checkReference(element.getParent().getParent().getReference(), "Bar", "QuotedRefTest.esc");
  }

  public void testQuotedTargetRef() {
    myFixture.configureByFile("QuotedTargetRefTest.esc");
    PsiElement element = myFixture.getFile().findElementAt(myFixture.getCaretOffset());
    //noinspection ConstantConditions
    checkReference(element.getParent().getParent().getReference(), "Bar", "QuotedTargetRefTest.esc");
  }

  public void testQuotedSourceRef() {
    myFixture.configureByFile("QuotedSourceRefTest.esc");
    PsiElement element = myFixture.getFile().findElementAt(myFixture.getCaretOffset());
    //noinspection ConstantConditions
    checkReference(element.getParent().getParent().getReference(), "Bar", "QuotedSourceRefTest.esc");
  }

  public void testVarTagRef() {
    PsiReference reference = myFixture.getReferenceAtCaretPosition("VarTagRef.esc");
    checkReference(reference, "tag1", "VarTagRef.esc");

    reference = myFixture.getReferenceAtCaretPosition("VarTagRef2.esc");
    checkReference(reference, "tag1", "VarTagRef2.esc");
  }

  public void testQuotedVarTag() {
    PsiReference reference = myFixture.getReferenceAtCaretPosition("VarTagRef3.esc");
    checkReference(reference, "`string`", "VarTagRef3.esc");
  }

  @SuppressWarnings("ConstantConditions")
  public void testReferenceAfterRename() {
    myFixture.configureByFile("SameNsTypeRef.esc");
    PsiElement element = myFixture.getFile().findElementAt(myFixture.getCaretOffset());
    PsiElement parent = element.getParent().getParent();

    checkReference(parent.getReference(), "Bar", "SameNsTypeRef.esc");

    myFixture.renameElementAtCaret("Baz");
    // first check that it actually got renamed
    assertEquals(
        "Baz",
        ((SchemaFile) (myFixture.getFile())).getDefs().getTypeDefWrapperList().get(0).getRecordTypeDef().getName()
    );

    checkReference(parent.getReference(), "Baz", "SameNsTypeRef.esc");
  }

  ////////////////////////////////

  private void checkReference(PsiReference reference, String text, String targetFileName) {
    assertNotNull(reference);
    PsiElement target = reference.resolve();
    assertNotNull("reference didn't resolve", target);
    String fileName = target.getContainingFile().getName();
    assertEquals(targetFileName, fileName);
    String actualText = target instanceof PsiNamedElement ? ((PsiNamedElement) target).getName() : target.getText();
    assertEquals(text, actualText);
    assertTrue(reference.isReferenceTo(target));
  }
}
