package com.sumologic.epigraph.ideaplugin.schema.brains;

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import com.sumologic.epigraph.schema.parser.psi.SchemaFile;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class RenameTest extends LightCodeInsightFixtureTestCase {
  @Override
  protected String getTestDataPath() {
    return "src/test/resources/testData/rename";
  }

  public void testTypeRename() {
    myFixture.configureByFile("TypeRename.epi_schema");
    myFixture.renameElementAtCaret("GGG");
    myFixture.checkResultByFile("TypeRename-after.epi_schema", false);
    // also check manually
    //noinspection ConstantConditions
    assertEquals("GGG", ((SchemaFile) (myFixture.getFile())).getDefs().getTypeDefWrapperList().get(0).getRecordTypeDef().getName());
  }
}
