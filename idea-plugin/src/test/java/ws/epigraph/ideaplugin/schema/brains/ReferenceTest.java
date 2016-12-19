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

package ws.epigraph.ideaplugin.schema.brains;

import com.intellij.psi.*;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import ws.epigraph.schema.parser.psi.EdlFile;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReferenceTest extends LightCodeInsightFixtureTestCase {
  @Override
  protected String getTestDataPath() {
    return "src/test/resources/testData/brains/reference";
  }

  public void testSameNamespaceRef() {
    myFixture.configureByFile("SameNsTypeRef.epigraph");
    PsiElement element = myFixture.getFile().findElementAt(myFixture.getCaretOffset());
    //noinspection ConstantConditions
    checkReference(element.getParent().getParent().getReference(), "Bar", "SameNsTypeRef.epigraph");
  }

  public void testBuiltinRef() {
    myFixture.configureByFiles("BuiltinImport.epigraph", "builtin.epigraph");
    PsiElement element = myFixture.getFile().findElementAt(myFixture.getCaretOffset());
    //noinspection ConstantConditions
    checkReference(element.getParent().getParent().getReference(), "Long", "builtin.epigraph");
  }

  public void testFqnImportRef() {
    myFixture.configureByFiles("FqnImportTypeRef.epigraph", "TargetNs.epigraph");
    PsiElement element = myFixture.getFile().findElementAt(myFixture.getCaretOffset());
    //noinspection ConstantConditions
    PsiReference reference = element.getParent().getParent().getReference();
    assertNotNull(reference);
    checkReference(reference, "ZZLong", "TargetNs.epigraph"); // not same file!
    Object[] variants = reference.getVariants();
    assertEquals(Arrays.toString(variants), 4, variants.length); // "ZZLong", "ZZLong", "R", "target"
  }

  public void testNsSegmentInTypeRef() {
    myFixture.configureByFile("NamespaceSegmentInTypeRef.epigraph");
    PsiElement element = myFixture.getFile().findElementAt(myFixture.getCaretOffset());
    //noinspection ConstantConditions
    checkReference(element.getParent().getParent().getReference(), "namespace foo", "NamespaceSegmentInTypeRef.epigraph");
  }

  @SuppressWarnings("ConstantConditions")
  public void testMultiNsRef() {
    myFixture.configureByFiles("MultiNamespaceRef.epigraph", "foobar.epigraph", "foobaz.epigraph");
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
    myFixture.configureByFile("QuotedRefTest.epigraph");
    PsiElement element = myFixture.getFile().findElementAt(myFixture.getCaretOffset());
    //noinspection ConstantConditions
    checkReference(element.getParent().getParent().getReference(), "Bar", "QuotedRefTest.epigraph");
  }

  public void testQuotedTargetRef() {
    myFixture.configureByFile("QuotedTargetRefTest.epigraph");
    PsiElement element = myFixture.getFile().findElementAt(myFixture.getCaretOffset());
    //noinspection ConstantConditions
    checkReference(element.getParent().getParent().getReference(), "Bar", "QuotedTargetRefTest.epigraph");
  }

  public void testQuotedSourceRef() {
    myFixture.configureByFile("QuotedSourceRefTest.epigraph");
    PsiElement element = myFixture.getFile().findElementAt(myFixture.getCaretOffset());
    //noinspection ConstantConditions
    checkReference(element.getParent().getParent().getReference(), "Bar", "QuotedSourceRefTest.epigraph");
  }

  public void testVarTagRef() {
    PsiReference reference = myFixture.getReferenceAtCaretPosition("VarTagRef.epigraph");
    checkReference(reference, "tag1", "VarTagRef.epigraph");

    reference = myFixture.getReferenceAtCaretPosition("VarTagRef2.epigraph");
    checkReference(reference, "tag1", "VarTagRef2.epigraph");
  }

  public void testQuotedVarTag() {
    PsiReference reference = myFixture.getReferenceAtCaretPosition("VarTagRef3.epigraph");
    checkReference(reference, "`string`", "VarTagRef3.epigraph");
  }

  @SuppressWarnings("ConstantConditions")
  public void testReferenceAfterRename() {
    myFixture.configureByFile("SameNsTypeRef.epigraph");
    PsiElement element = myFixture.getFile().findElementAt(myFixture.getCaretOffset());
    PsiElement parent = element.getParent().getParent();

    checkReference(parent.getReference(), "Bar", "SameNsTypeRef.epigraph");

    myFixture.renameElementAtCaret("Baz");
    // first check that it actually got renamed
    assertEquals(
        "Baz",
        ((EdlFile) (myFixture.getFile())).getDefs().getTypeDefWrapperList().get(0).getRecordTypeDef().getName()
    );

    checkReference(parent.getReference(), "Baz", "SameNsTypeRef.epigraph");
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
