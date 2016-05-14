package com.sumologic.epigraph.ideaplugin.schema.brains;

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;

import java.util.*;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class CompletionTest extends LightCodeInsightFixtureTestCase {
  @Override
  protected String getTestDataPath() {
    return "src/test/resources/testData/completion";
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
    checkCompletionVariants("import ", "polymorphic ", "abstract ", "vartype ", "record ", "map", "list", "enum ",
        "string ", "double ", "integer ", "long ", "boolean ", "supplement ");
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

  private void checkCompletionVariants(String... variants) {
    myFixture.completeBasic();
    List<String> actual = myFixture.getLookupElementStrings();
    assertNotNull(actual);
    List<String> expected = Arrays.asList(variants);
    Collections.sort(actual);
    Collections.sort(expected);
    assertEquals(expected, actual);
  }
}
