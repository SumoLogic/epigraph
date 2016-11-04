package ws.epigraph.ideaplugin.schema.brains.hierarchy;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import ws.epigraph.schema.parser.psi.SchemaFieldDecl;
import ws.epigraph.schema.parser.psi.SchemaVarTagDecl;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OverridesTest extends LightCodeInsightFixtureTestCase {
  @Override
  protected String getTestDataPath() {
    return "src/test/resources/testData/brains/hierarchy";
  }

  public void testQuotedVarTagOverride() {
    myFixture.configureByFile("QuotedTagOverride.esc");
    PsiElement element = myFixture.getFile().findElementAt(myFixture.getCaretOffset());
    SchemaVarTagDecl tagDecl = PsiTreeUtil.getParentOfType(element, SchemaVarTagDecl.class);
    assertNotNull(tagDecl);
    assertEquals(1, TypeMembers.getOverridenTags(tagDecl).size());
  }

  public void testQuotedFieldOverride() {
    myFixture.configureByFile("QuotedFieldOverride.esc");
    PsiElement element = myFixture.getFile().findElementAt(myFixture.getCaretOffset());
    SchemaFieldDecl fieldDecl = PsiTreeUtil.getParentOfType(element, SchemaFieldDecl.class);
    assertNotNull(fieldDecl);
    assertEquals(1, TypeMembers.getOverridenFields(fieldDecl).size());
  }
}
