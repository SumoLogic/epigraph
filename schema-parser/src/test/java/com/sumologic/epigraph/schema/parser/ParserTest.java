package com.sumologic.epigraph.schema.parser;

import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.DebugUtil;
import org.intellij.grammar.LightPsi;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class ParserTest {
  @Test
  public void testParserSimple() throws IOException {
    testParse("ParsingTestData.simple", "ParsingTestData.txt");
  }

  private void testParse(String inputFile, String expectedOutputFile) throws IOException {
    File dir = new File("src/test/resources/testData/parser");
    File input = new File(dir, inputFile);

    PsiFile psi = LightPsi.parseFile(input, new SchemaParserDefinition());
    String psiDump = DebugUtil.psiToString(psi, false, true).trim();

    String expectedPsiDump = new String(Files.readAllBytes(Paths.get(dir.getAbsolutePath(), expectedOutputFile)));

    Assert.assertEquals(expectedPsiDump, psiDump);
  }
}
