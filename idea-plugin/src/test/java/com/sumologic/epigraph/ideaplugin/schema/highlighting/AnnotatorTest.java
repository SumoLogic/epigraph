package com.sumologic.epigraph.ideaplugin.schema.highlighting;

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class AnnotatorTest extends LightCodeInsightFixtureTestCase {
  @Override
  protected String getTestDataPath() {
    return "src/test/resources/testData/highlighting";
  }

  public void testInvalid1() {
    myFixture.configureByFile("Invalid1.epi_schema");
    myFixture.checkHighlighting(true, false, true);
  }
}
