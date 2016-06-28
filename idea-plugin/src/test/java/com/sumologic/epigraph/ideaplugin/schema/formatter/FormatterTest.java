package com.sumologic.epigraph.ideaplugin.schema.formatter;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class FormatterTest extends LightCodeInsightFixtureTestCase {
  @Override
  protected String getTestDataPath() {
    return "src/test/resources/testData/formatting";
  }

  public void testFormatter() {
    myFixture.configureByFile("FormatterTest.esc");
    new WriteCommandAction.Simple<Void>(getProject()) {
      @Override
      protected void run() throws Throwable {
        CodeStyleManager.getInstance(getProject()).reformat(myFixture.getFile());
      }
    }.execute();

    myFixture.checkResultByFile("FormatterTest-out.esc");
  }
}
