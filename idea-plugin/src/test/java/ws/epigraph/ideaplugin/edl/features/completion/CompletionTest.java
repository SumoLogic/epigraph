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

package ws.epigraph.ideaplugin.edl.features.completion;

import com.intellij.psi.impl.DebugUtil;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import ws.epigraph.edl.parser.SchemaFileType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class CompletionTest extends LightCodeInsightFixtureTestCase {
  private final List<String> TOP_LEVEL = Arrays.asList(
      "abstract ", "vartype ", "record ", "map", "list", "enum ",
      "string ", "double ", "integer ", "long ", "boolean ", "supplement "
  );

  @Override
  protected String getTestDataPath() {
    return "src/test/resources/testData/features/completion";
  }

  public void testStaticImportCompletion() {
    myFixture.testCompletion("CompletionWithStaticImport.esc", "CompletionWithStaticImport-after.esc", "zzz.esc");
  }

  public void testImportCompletion() {
    myFixture.configureByFiles("ImportCompletion.esc", "foo.esc", "foobar.esc");
    checkCompletionVariants("Foo", "bar");

    myFixture.configureByFiles("ImportCompletion2.esc", "foo.esc", "foobar.esc");
    checkCompletionVariants("bar"); // but not "*" or "Foo", same namespace

    myFixture.configureByFiles("ImportCompletion3.esc", "foo.esc", "foobar.esc", "zzz.esc", "builtin.esc");
    checkCompletionVariants("foo", "zzz", "epigraph"); // but not "foo.bar" or "some" or "epigraph.Boolean"
  }

  public void testTypeRefCompletion() {
    myFixture.configureByFiles("TypeRefCompletion.esc", "foo.esc", "foobar.esc");
    checkCompletionVariants("bar", "Foo"); // foo.bar has not been imported but foo.bar.Baz still makes a valid reference

    myFixture.testCompletion("TypeRefCompletion2.esc", "TypeRefCompletion2-after.esc", "foo.esc", "foobar.esc");
  }

  public void testTopLevelCompletion() {
    myFixture.configureByFile("TopLevelCompletion.esc");
    checkCompletionVariants(TOP_LEVEL);
  }

  public void testNamespaceOnlyTopLevelCompletion() {
    myFixture.configureByFile("NamespaceOnlyTopLevelCompletion.esc");
    checkCompletionVariants(TOP_LEVEL, "import ");
  }

  public void testBuiltinTypeCompletion() {
    myFixture.testCompletion("BuiltinTypeCompletion.esc", "BuiltinTypeCompletion-after.esc", "builtin.esc");
  }

  public void testCompletionAfterAbstract() {
    myFixture.configureByFile("CompletionAfterAbstract.esc");
    checkCompletionVariants("record ", "map", "list", "string ", "double ", "integer ", "long ", "boolean ");
  }

  public void testCompletionAfterRecordName() {
    myFixture.configureByFile("CompletionAfterRecordName.esc");
    checkCompletionVariants(TOP_LEVEL, "extends ", "meta ", "supplements ");
  }

  public void testCompletionAfterRecordNameBeforeBlock() {
    myFixture.configureByFile("CompletionAfterRecordNameBeforeBlock.esc");
    checkCompletionVariants("extends ", "meta ", "supplements ");
  }

  public void testCompletionAfterListName() {
    myFixture.configureByFile("CompletionAfterListName.esc");
    checkCompletionVariants(TOP_LEVEL, "extends ", "meta ");
  }

  public void testUndefinedTypeNameCompletion() {
    myFixture.configureByFile("UndefinedTypeNameCompletion.esc");
    checkCompletionVariants("Bar", "Baz");
  }

  // TODO(low) add later, too advanced for now
//  public void testUndefinedTypeNameCompletion2() {
//    myFixture.configureByFile("UndefinedTypeNameCompletion2.esc");
//    checkCompletionVariants("Bar", "Baz"); // but not Baq, incompatible kinds
//  }

  public void testUndefinedTypeNameCompletion3() {
    myFixture.configureByFile("UndefinedTypeNameCompletion2.esc");
    checkCompletionVariants("Bar", "Baz", "Baq");
  }

  public void testFieldTypeCompletion() {
    myFixture.configureByFile("FieldTypeCompletion.esc");
    checkCompletionVariants("Bar", "Baz");
  }

  public void testWithCompletion() {
    myFixture.testCompletion("CompleteWith.esc", "CompleteWith-after.esc");
  }

  public void testVarTagCompletion() {
    myFixture.configureByFile("VarTagCompletion.esc");
    checkCompletionVariants("`string`", "foo");
  }

  public void testNamespaceCompletionDoesntIncludeTypes() {
    myFixture.configureByFiles("NamespaceCompletion.esc", "foo.esc", "foobar.esc");
    checkCompletionVariants("bar");
  }

  // ------------- extends

  public void testProperKindCompletionExt1() {
    myFixture.configureByFile("TypeRefKindCompletionExt1.esc");
    checkCompletionVariants("LL1", "LL2"); // but not LL3
  }

  public void testProperKindCompletionExt2() {
    myFixture.configureByFile("TypeRefKindCompletionExt2.esc");
    checkCompletionVariants("LL3", "LL4");
  }

  public void testProperKindCompletionExt3() {
    myFixture.configureByFile("TypeRefKindCompletionExt3.esc");
    checkCompletionVariants("S1", "S2");
  }

  // ------------- supplements

  public void testProperKindCompletionSup1() {
    myFixture.configureByFile("TypeRefKindCompletionSup1.esc");
    checkCompletionVariants("LL1", "LL2"); // but not LL3
  }

  public void testProperKindCompletionSup2() {
    myFixture.configureByFile("TypeRefKindCompletionSup2.esc");
    checkCompletionVariants("LL1", "LL4");
  }

  public void testProperKindCompletionSup3() {
    myFixture.configureByFile("TypeRefKindCompletionSup3.esc");
    checkCompletionVariants("S1", "S2");

  }
  // ------------- supplement target

  // target, no `with`

  public void testProperKindCompletionSupT1() {
    myFixture.configureByFile("TypeRefKindCompletionSupT1.esc");
    checkCompletionVariants("LL2", "LL3");
  }

  public void testProperKindCompletionSupT2() {
    myFixture.configureByFile("TypeRefKindCompletionSupT2.esc");
    checkCompletionVariants("LL1", "LL4");
  }

  public void testProperKindCompletionSupT3() {
    myFixture.configureByFile("TypeRefKindCompletionSupT3.esc");
    checkCompletionVariants("S2", "S3");
  }

  // target, with `with`

  public void testProperKindCompletionSupT4() {
    myFixture.configureByFile("TypeRefKindCompletionSupT4.esc");
    checkCompletionVariants("LL1", "LL2");
  }

  public void testProperKindCompletionSupT5() {
    myFixture.configureByFile("TypeRefKindCompletionSupT5.esc");
    checkCompletionVariants("LL4", "LL5"); // LL1 would create circular ref, L2 already there, L3 extends L2
  }

  public void testProperKindCompletionSupT6() {
    myFixture.configureByFile("TypeRefKindCompletionSupT6.esc");
    checkCompletionVariants("S2", "S3");
  }

  // source

  public void testProperKindCompletionSupS1() {
    myFixture.configureByFile("TypeRefKindCompletionSupS1.esc");
    checkCompletionVariants("LL1", "LL2");
  }

  public void testProperKindCompletionSupS2() {
    myFixture.configureByFile("TypeRefKindCompletionSupS2.esc");
    checkCompletionVariants("LL4", "LL5"); // LL1 would create circular ref, L2 already there, L3 extends L2
  }

  public void testProperKindCompletionSupS3() {
    myFixture.configureByFile("TypeRefKindCompletionSupS3.esc");
    checkCompletionVariants("S2", "S3");
  }

  public void testProperKindCompletionSupS4() {
    myFixture.configureByFile("TypeRefKindCompletionSupS4.esc");
    checkCompletionVariants("R2"); // R3 extends R2, but R1 doesn't
  }

  public void testProperKindCompletionSupS5() {
    myFixture.configureByFile("TypeRefKindCompletionSupS5.esc");
    checkCompletionVariants();
  }

  // --------------- record fields

  public void testNoCompletionInEmptyRecord() {
    configureByText("namespace foo record Bar { <caret> }");
    checkCompletionVariants();
  }

  public void testOverrideCompletionInRecord() {
    configureByText("namespace foo record Bar { bar: Bar } record Baz extends Bar { <caret> }");
    checkCompletionVariants("override ");

    configureByText("namespace foo record Bar { bar: Bar } record Baz extends Bar { qux: Baz <caret> }");
    checkCompletionVariants("override "); // Baz is not a vartype, so no 'default'

    configureByText("namespace foo vartype V { v: Baz } record Baz { qux: <caret> }");
    checkCompletionVariants("V", "Baz");

    configureByText("namespace foo vartype V { v: Baz } record Baz { qux: V <caret> }");
    checkCompletionVariants("override ", "default ");

    configureByText("namespace foo record A { a: A } record Baz extends A { qux: V de<caret> abstract zz: Baz} vartype V { v: Baz }");
    checkCompletionVariants("override ", "default ");

    configureByText("namespace foo vartype V { v: Baz } record A { a: A} record Baz extends A { qux: Baz <caret> }");
    checkCompletionVariants("override ");

    configureByText("namespace foo vartype V { v: Baz } record Baz { q: Baz qux: V <caret> }");
    checkCompletionVariants("default ");

    configureByText("namespace foo vartype V { v: Baz } record Baz { q: Baz qux: V <caret> w: Baz}");
    checkCompletionVariants("default ");

    configureByText("namespace foo vartype V { v: Baz } record Baz { qux: V default <caret> }");
    checkCompletionVariants("v");
  }

  public void testOverrideFieldCompletionInRecord() {
    configureByText("namespace foo record Bar { } record Baz extends Bar { override <caret> }");
    checkCompletionVariants();

    configureByText("namespace foo record Bar { `bar`: Bar } record Baz extends Bar { override <caret> }");
    checkCompletionVariants("`bar`");

    configureByText("namespace foo record Bar { bar: Bar } record Baz extends Bar { override bar override <caret> }");
    checkCompletionVariants();

    configureByText("namespace foo record Bar { bar: Bar } record Baz extends Bar { override <caret> foo : Bar { doc = \"xx\" } }");
    checkCompletionVariants("bar");
  }

  // todo: complete field name after override

  // --------------- vartype tags

  public void testNoCompletionInEmptyVartype() {
    configureByText("namespace foo vartype Bar { <caret> }");
    checkCompletionVariants();
  }

  public void testOverrideCompletionInVartype() {
    configureByText("namespace foo vartype Bar { bar: Bar } vartype Baz extends Bar { <caret> }");
    checkCompletionVariants("override ");

    configureByText("namespace foo vartype Bar { bar: Bar } vartype Baz extends Bar { qux: Baz <caret> }");
    checkCompletionVariants("override ");
  }

  public void testOverrideTagCompletionInVartype() {
    configureByText("namespace foo vartype Bar { } vartype Baz extends Bar { override <caret> }");
    checkCompletionVariants();

    configureByText("namespace foo vartype Bar { `bar`: Bar } vartype Baz extends Bar { override <caret> }");
    checkCompletionVariants("`bar`");

    configureByText("namespace foo vartype Bar { bar: Bar } vartype Baz extends Bar { override bar override <caret> }");
    checkCompletionVariants();

    configureByText("namespace foo vartype Bar { bar: Bar } vartype Baz extends Bar { override <caret> foo : Bar { doc = \"xx\" } }");
    checkCompletionVariants("bar");
  }

  // todo test 'default' override in anon lists/maps

  // ----------------------------------

  private void checkCompletionVariants(String... variants) {
    myFixture.completeBasic();
    List<String> actual = myFixture.getLookupElementStrings();
    assertNotNull(dumpPsi(), actual);
    List<String> expected = Arrays.asList(variants);
    Collections.sort(actual);
    Collections.sort(expected);
    assertEquals(dumpPsi(), expected, actual);
  }

  private void checkCompletionVariants(List<String> variants, String... moreVariants) {
    List<String> allVariants = new ArrayList<>(variants);
    allVariants.addAll(Arrays.asList(moreVariants));
    checkCompletionVariants(allVariants.toArray(new String[allVariants.size()]));
  }

  private void configureByText(@NotNull String text) {
    myFixture.configureByText(SchemaFileType.INSTANCE, text);
  }

  @NotNull
  private String dumpPsi() {
    return DebugUtil.psiToString(getFile(), true, false).trim();
  }
}
