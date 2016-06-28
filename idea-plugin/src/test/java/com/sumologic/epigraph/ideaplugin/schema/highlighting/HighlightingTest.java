package com.sumologic.epigraph.ideaplugin.schema.highlighting;

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import com.sumologic.epigraph.ideaplugin.schema.features.inspections.ConflictingImportInspection;
import com.sumologic.epigraph.ideaplugin.schema.features.inspections.DuplicateImportInspection;
import com.sumologic.epigraph.ideaplugin.schema.features.inspections.UnnecessaryImportInspection;
import com.sumologic.epigraph.ideaplugin.schema.features.inspections.UnusedImportInspection;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class HighlightingTest extends LightCodeInsightFixtureTestCase {
  @Override
  protected String getTestDataPath() {
    return "src/test/resources/testData/highlighting";
  }

  public void testInvalid1() {
    myFixture.configureByFiles("Invalid1.esc", "other.esc", "builtin.esc",
        "foo.esc", "bar.esc");
    myFixture.enableInspections(new DuplicateImportInspection());
    myFixture.enableInspections(new UnnecessaryImportInspection());
    myFixture.enableInspections(new UnusedImportInspection());
    myFixture.enableInspections(new ConflictingImportInspection());
    myFixture.checkHighlighting(true, false, true);
  }
}
