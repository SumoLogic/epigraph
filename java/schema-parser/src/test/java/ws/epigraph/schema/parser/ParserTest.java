package ws.epigraph.schema.parser;

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
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ParserTest {
  @Test
  public void testParserSimple() throws IOException {
    testParse("ParsingTestData.esc", "ParsingTestData.txt", false);
  }

  @Test
  public void testParserRecover1() throws IOException {
    testParse("ParsingRecover1.esc", "ParsingRecover1.txt", true);
  }

  private void testParse(String inputFile, String expectedOutputFile, boolean withRanges) throws IOException {
    File dir = new File("src/test/resources/testData/parser");
    File input = new File(dir, inputFile);

    PsiFile psi = LightPsi.parseFile(input, new SchemaParserDefinition());
    String psiDump = DebugUtil.psiToString(psi, true, withRanges).trim();
//    System.out.println("psiDump = " + psiDump);

    String expectedPsiDump = new String(Files.readAllBytes(Paths.get(dir.getAbsolutePath(), expectedOutputFile)));

    Assert.assertEquals(expectedPsiDump.trim(), psiDump.trim());
  }

}
