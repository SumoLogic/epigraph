package ws.epigraph.ideaplugin.schema.features.usages;

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import com.intellij.usageView.UsageInfo;

import java.util.Collection;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class FindUsagesTest extends LightCodeInsightFixtureTestCase {
  @Override
  protected String getTestDataPath() {
    return "src/test/resources/testData/features/search";
  }

  public void testFindTypeUsages() {
    Collection<UsageInfo> usageInfos = myFixture.testFindUsages("FindTypeUsagesTestData.esc");
    assertEquals(2, usageInfos.size());
  }

  public void testFindVarTagUsages() {
    Collection<UsageInfo> usageInfos = myFixture.testFindUsages("FindVarTagUsagesTestData.esc");
    assertEquals(1, usageInfos.size());
  }
}
