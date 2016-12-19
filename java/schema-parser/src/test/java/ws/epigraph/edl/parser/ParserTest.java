/*
 * Copyright 2016 Sumo Logic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ws.epigraph.edl.parser;

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
    testParse("ParsingTestData.epigraph", "ParsingTestData.txt", false);
  }

  @Test
  public void testParserRecover1() throws IOException {
    testParse("ParsingRecover1.epigraph", "ParsingRecover1.txt", true);
  }

  private void testParse(String inputFile, String expectedOutputFile, boolean withRanges) throws IOException {
    File dir = new File("src/test/resources/testData/parser");
    File input = new File(dir, inputFile);

    PsiFile psi = LightPsi.parseFile(input, new EdlParserDefinition());
    String psiDump = DebugUtil.psiToString(psi, true, withRanges).trim();
//    System.out.println("psiDump = " + psiDump);

    String expectedPsiDump = new String(Files.readAllBytes(Paths.get(dir.getAbsolutePath(), expectedOutputFile)));

    Assert.assertEquals(expectedPsiDump.trim(), psiDump.trim());
  }

}
