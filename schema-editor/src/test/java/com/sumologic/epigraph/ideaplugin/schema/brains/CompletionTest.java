package com.sumologic.epigraph.ideaplugin.schema.brains;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;

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
    myFixture.configureByFiles("CompletionWithStaticImport.es", "zzz.es");
    LookupElement[] complete = myFixture.complete(CompletionType.BASIC, 1);
    List<String> strings = myFixture.getLookupElementStrings();
    // doesn't work: https://intellij-support.jetbrains.com/hc/en-us/community/posts/206665049-Completion-Unit-Test-complete-returns-null
//    assertNotNull(strings);
//    assertEquals(1, strings.size());
//    assertContainsElements(strings, "ZZLong");
  }
}
