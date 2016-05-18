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
    myFixture.testCompletion("CompletionWithStaticImport.es", "CompletionWithStaticImport-after.es", "zzz.es");
  }

  public void testTypeRefCompletion() {
    myFixture.configureByFiles("TypeRefCompletion.es", "foo.es", "foobar.es");
    checkCompletionVariants("bar", "Foo");
  }

  public void testNamespaceOnlyTopLevelCompletion() {
    myFixture.configureByFile("NamespaceOnlyTopLevelCompletion.es");
    checkCompletionVariants(TOP_LEVEL, "import ");
  }

  public void testCompletionAfterPolymorphic() {
    myFixture.configureByFile("CompletionAfterPolymorphic.es");
    checkCompletionVariants("record ", "map", "list",
        "string ", "double ", "integer ", "long ", "boolean ");
  }

  public void testCompletionAfterAbstract() {
    myFixture.configureByFile("CompletionAfterAbstract.es");
    checkCompletionVariants("polymorphic ", "record ", "map", "list", "string ", "double ", "integer ", "long ", "boolean ");
  }

  public void testCompletionAfterRecordName() {
    myFixture.configureByFile("CompletionAfterRecordName.es");
    checkCompletionVariants(TOP_LEVEL, "extends ", "meta ", "supplements ");
  }

  public void testCompletionAfterRecordNameBeforeBlock() {
    myFixture.configureByFile("CompletionAfterRecordNameBeforeBlock.es");
    checkCompletionVariants("extends ", "meta ", "supplements ");
  }

  public void testCompletionAfterListName() {
    myFixture.configureByFile("CompletionAfterListName.es");
    checkCompletionVariants(TOP_LEVEL, "extends ", "meta ");
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
