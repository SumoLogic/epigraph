package com.sumologic.epigraph.ideaplugin.schema.features.completion;

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;

import java.util.*;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class CompletionTest extends LightCodeInsightFixtureTestCase {
  private final List<String> TOP_LEVEL = Arrays.asList(
      "polymorphic ", "abstract ", "vartype ", "record ", "map", "list", "enum ",
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

  public void testCompletionAfterPolymorphic() {
    myFixture.configureByFile("CompletionAfterPolymorphic.esc");
    checkCompletionVariants("record ", "map", "list",
        "string ", "double ", "integer ", "long ", "boolean ");
  }

  public void testCompletionAfterAbstract() {
    myFixture.configureByFile("CompletionAfterAbstract.esc");
    checkCompletionVariants("polymorphic ", "record ", "map", "list", "string ", "double ", "integer ", "long ", "boolean ");
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

  /*
  public void testProperKindCompletionSups1() {
    myFixture.configureByFile("TypeRefKindCompletionSups1.esc");
    checkCompletionVariants("LL1", "LL2"); // but not LL3
  }

  public void testProperKindCompletionSups2() {
    myFixture.configureByFile("TypeRefKindCompletionSups2.esc");
    checkCompletionVariants("LL1", "LL4");
  }

//  public void testProperKindCompletionSups3() {
//    myFixture.configureByFile("TypeRefKindCompletionSups3.esc");
//    checkCompletionVariants("S1", "S2");
//  }
*/

  private void checkCompletionVariants(String... variants) {
    myFixture.completeBasic();
    List<String> actual = myFixture.getLookupElementStrings();
    assertNotNull(actual);
    List<String> expected = Arrays.asList(variants);
    Collections.sort(actual);
    Collections.sort(expected);
    assertEquals(expected, actual);
  }

  private void checkCompletionVariants(List<String> variants, String... moreVariants) {
    List<String> allVariants = new ArrayList<>(variants);
    allVariants.addAll(Arrays.asList(moreVariants));
    checkCompletionVariants(allVariants.toArray(new String[allVariants.size()]));
  }
}
