package com.sumologic.epigraph.ideaplugin.schema.brains;

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;

import java.util.Arrays;
import java.util.List;

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
    myFixture.completeBasic();
    List<String> completionVariants = myFixture.getLookupElementStrings();
    assertNotNull(completionVariants);
    assertEquals(2, completionVariants.size());
    assertTrue(completionVariants.containsAll(Arrays.asList("bar", "Foo")));
  }
}
