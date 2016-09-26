package io.epigraph.idl.gdata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.impl.DebugUtil;
import io.epigraph.idl.parser.projections.IdlSubParserDefinitions;
import io.epigraph.idl.parser.psi.IdlDataValue;
import io.epigraph.gdata.GDataValue;
import io.epigraph.psi.EpigraphPsiUtil;
import io.epigraph.psi.PsiProcessingException;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class IdlGDataPsiParserTest {
  @Test
  public void testGDataVarParsing() {
    String expression = "fooType < a: { a1 : \"a\", a2 : <>}, \n" +
                        "b: mapType( 1 : 1, 2: <tag: true>), \n" +
                        "c: listType[1,2,false,\"33\", <q:w>], \n" +
                        "d: enum, \n" +
                        "e: q.w.someType@3.14, \n" +
                        "f: false,\n" +
                        "g: \"foobar\">";
    String expected =
        "fooType<a: {a1: \"a\", a2: <>}, " +
        "b: mapType(1: 1, 2: <tag: true>), " +
        "c: listType[1, 2, false, \"33\", <q: w>], " +
        "d: enum, " +
        "e: q.w.someType@3.14, " +
        "f: false, " +
        "g: \"foobar\">";
    testParse(expression, expected);
  }

  private void testParse(String dataStr, String expectedToString) {
    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();

    @NotNull IdlDataValue dataValue = EpigraphPsiUtil.parseText(
        dataStr,
        IdlSubParserDefinitions.DATA_VALUE.rootElementType(),
        IdlDataValue.class,
        IdlSubParserDefinitions.DATA_VALUE,
        errorsAccumulator
    );

    if (errorsAccumulator.hasErrors()) {
      for (PsiErrorElement element : errorsAccumulator.errors()) {
        System.err.println(element.getErrorDescription() + " at " + EpigraphPsiUtil.getLocation(element));
      }
      fail(DebugUtil.psiTreeToString(dataValue, true));
    }

    GDataValue gDataValue = null;
    try {
      gDataValue = IdlGDataPsiParser.parseValue(dataValue);
    } catch (PsiProcessingException e) {
      e.printStackTrace();
      System.err.println(e.getMessage() + " at " + EpigraphPsiUtil.getLocation(e.psi()));
      fail();
    }

    assertEquals(expectedToString, gDataValue.toString());
  }
}
