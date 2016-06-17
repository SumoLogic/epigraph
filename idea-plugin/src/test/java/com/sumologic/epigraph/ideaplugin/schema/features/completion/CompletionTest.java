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
    myFixture.testCompletion("CompletionWithStaticImport.epi_schema", "CompletionWithStaticImport-after.epi_schema", "zzz.epi_schema");
  }

  public void testImportCompletion() {
    myFixture.configureByFiles("ImportCompletion.epi_schema", "foo.epi_schema", "foobar.epi_schema");
    checkCompletionVariants("Foo", "bar");

    myFixture.configureByFiles("ImportCompletion2.epi_schema", "foo.epi_schema", "foobar.epi_schema");
    checkCompletionVariants("bar"); // but not "*" or "Foo", same namespace

    myFixture.configureByFiles("ImportCompletion3.epi_schema", "foo.epi_schema", "foobar.epi_schema", "zzz.epi_schema", "builtin.epi_schema");
    checkCompletionVariants("foo", "zzz", "epigraph"); // but not "foo.bar" or "some" or "epigraph.Boolean"
  }

  public void testTypeRefCompletion() {
    myFixture.configureByFiles("TypeRefCompletion.epi_schema", "foo.epi_schema", "foobar.epi_schema");
    checkCompletionVariants("bar", "Foo"); // foo.bar has not been imported but foo.bar.Baz still makes a valid reference

    myFixture.testCompletion("TypeRefCompletion2.epi_schema", "TypeRefCompletion2-after.epi_schema", "foo.epi_schema", "foobar.epi_schema");
  }

  public void testTopLevelCompletion() {
    myFixture.configureByFile("TopLevelCompletion.epi_schema");
    checkCompletionVariants(TOP_LEVEL);
  }

  public void testNamespaceOnlyTopLevelCompletion() {
    myFixture.configureByFile("NamespaceOnlyTopLevelCompletion.epi_schema");
    checkCompletionVariants(TOP_LEVEL, "import ");
  }

  public void testBuiltinTypeCompletion() {
    myFixture.testCompletion("BuiltinTypeCompletion.epi_schema", "BuiltinTypeCompletion-after.epi_schema", "builtin.epi_schema");
  }

  public void testCompletionAfterPolymorphic() {
    myFixture.configureByFile("CompletionAfterPolymorphic.epi_schema");
    checkCompletionVariants("record ", "map", "list",
        "string ", "double ", "integer ", "long ", "boolean ");
  }

  public void testCompletionAfterAbstract() {
    myFixture.configureByFile("CompletionAfterAbstract.epi_schema");
    checkCompletionVariants("polymorphic ", "record ", "map", "list", "string ", "double ", "integer ", "long ", "boolean ");
  }

  public void testCompletionAfterRecordName() {
    myFixture.configureByFile("CompletionAfterRecordName.epi_schema");
    checkCompletionVariants(TOP_LEVEL, "extends ", "meta ", "supplements ");
  }

  public void testCompletionAfterRecordNameBeforeBlock() {
    myFixture.configureByFile("CompletionAfterRecordNameBeforeBlock.epi_schema");
    checkCompletionVariants("extends ", "meta ", "supplements ");
  }

  public void testCompletionAfterListName() {
    myFixture.configureByFile("CompletionAfterListName.epi_schema");
    checkCompletionVariants(TOP_LEVEL, "extends ", "meta ");
  }

  public void testUndefinedTypeNameCompletion() {
    myFixture.configureByFile("UndefinedTypeNameCompletion.epi_schema");
    checkCompletionVariants("Bar", "Baz");
  }

  // TODO(low) add later, too advanced for now
//  public void testUndefinedTypeNameCompletion2() {
//    myFixture.configureByFile("UndefinedTypeNameCompletion2.epi_schema");
//    checkCompletionVariants("Bar", "Baz"); // but not Baq, incomplete kinds
//  }

  public void testUndefinedTypeNameCompletion3() {
    myFixture.configureByFile("UndefinedTypeNameCompletion2.epi_schema");
    checkCompletionVariants("Bar", "Baz", "Baq");
  }

  public void testFieldTypeCompletion() {
    myFixture.configureByFile("FieldTypeCompletion.epi_schema");
    checkCompletionVariants("Bar", "Baz");
  }

  public void testWithCompletion() {
    myFixture.testCompletion("CompleteWith.epi_schema", "CompleteWith-after.epi_schema");
  }

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
