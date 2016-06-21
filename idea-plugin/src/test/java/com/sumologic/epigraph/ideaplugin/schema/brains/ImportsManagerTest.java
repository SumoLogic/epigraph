package com.sumologic.epigraph.ideaplugin.schema.brains;

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import com.sumologic.epigraph.schema.parser.psi.SchemaFile;
import com.sumologic.epigraph.schema.parser.psi.SchemaImportStatement;

import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class ImportsManagerTest extends LightCodeInsightFixtureTestCase {
  @Override
  protected String getTestDataPath() {
    return "src/test/resources/testData/brains";
  }

  public void testFindUnusedImports() {
    myFixture.configureByFiles("UnusedImports.epi_schema", "foo.epi_schema", "foobar.epi_schema");
    List<SchemaImportStatement> unusedImports = ImportsManager.findUnusedImports((SchemaFile) myFixture.getFile());

    assertEquals(2, unusedImports.size());
    Iterator<SchemaImportStatement> iterator = unusedImports.iterator();
    assertEquals("import foo.Y", iterator.next().getText());
    assertEquals("import foo", iterator.next().getText());
  }
}
