package com.sumologic.epigraph.ideaplugin.schema.parser;

import com.intellij.testFramework.ParsingTestCase;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SimpleParsingTest extends ParsingTestCase {
  public SimpleParsingTest() {
    super("", "simple", new SchemaParserDefinition());
  }

  @Override
  protected String getTestDataPath() {
    return "src/test/resources/testData/parser";
  }

  public void testParsingTestData() {
    doTest(true);
  }

  @Override
  protected boolean skipSpaces() {
    return false;
  }

  @Override
  protected boolean includeRanges() {
    return true;
  }
}
