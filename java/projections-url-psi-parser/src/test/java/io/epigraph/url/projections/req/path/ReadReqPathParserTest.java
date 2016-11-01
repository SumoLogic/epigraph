package io.epigraph.url.projections.req.path;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.impl.DebugUtil;
import de.uka.ilkd.pp.Layouter;
import de.uka.ilkd.pp.NoExceptions;
import de.uka.ilkd.pp.StringBackend;
import io.epigraph.idl.parser.projections.IdlSubParserDefinitions;
import io.epigraph.idl.parser.psi.IdlOpVarPath;
import io.epigraph.projections.ProjectionUtils;
import io.epigraph.projections.op.path.OpPathPsiParser;
import io.epigraph.projections.op.path.OpVarPath;
import io.epigraph.projections.req.path.ReqPathPrettyPrinter;
import io.epigraph.projections.req.path.ReqVarPath;
import io.epigraph.psi.EpigraphPsiUtil;
import io.epigraph.psi.PsiProcessingError;
import io.epigraph.psi.PsiProcessingException;
import io.epigraph.refs.SimpleTypesResolver;
import io.epigraph.refs.TypesResolver;
import io.epigraph.tests.*;
import io.epigraph.types.DataType;
import io.epigraph.url.parser.projections.UrlSubParserDefinitions;
import io.epigraph.url.parser.psi.UrlReqOutputComaVarProjection;
import io.epigraph.url.parser.psi.UrlReqOutputTrunkVarProjection;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReadReqPathParserTest {
  private DataType dataType = new DataType(Person.type, Person.id);
  private TypesResolver resolver = new SimpleTypesResolver(
      PersonId.type,
      Person.type,
      User.type,
      UserId.type,
      UserRecord.type,
      epigraph.String.type
  );

  private OpVarPath personOpPath = parseOpVarPath(
      lines(
          ":record { ;p1:epigraph.String }",
          "  / friendsMap { ;p2:epigraph.String }",
          "    / . { ;p3:epigraph.String }",
          "      :record { ;p4:epigraph.String }",
          "        / bestFriend { ;p5:epigraph.String }"
      )
  );

  @Test
  public void testParsePath() {
    testParse(personOpPath, ":record / friendsMap / 'John' ;p3 = 'foo' :record / bestFriend");
  }

  @Test
  public void testParseParam() {
    testParse(
        personOpPath,
        ":record ;p1 = 'a' / friendsMap ;p2 = 'b' / 'John' ;p3 = 'c' :record ;p4 = 'd' / bestFriend ;p5 = 'e'"
    );
  }

  @Test
  public void testComaFail1() {
    testPathNotMatched(personOpPath, ":record / friendsMap / 'John' :record ( bestFriend )");
  }

  @Test
  public void testComaFail2() {
    testPathNotMatched(personOpPath, ":record / friendsMap / 'John' :(record ( bestFriend ))");
  }

  @Test
  public void testComaFail3() {
    testPathNotMatched(personOpPath, ":record / friendsMap");
  }

  @Test
  public void testComaFail4() {
    testPathNotMatched(personOpPath, ":record / friendsMap / 'John'");
  }

  @Test
  public void testComaFail5() {
    testPathNotMatched(personOpPath, "");
  }

  @Test
  public void testComa1() {
    testParse(
        personOpPath,
        ":record / friendsMap / 'John' :record / bestFriend :record (id)",
        ":record / friendsMap / 'John' :record / bestFriend",
        ":record (id)"
    );
  }

  @Test
  public void testComa2() {
    testParse(
        personOpPath,
        ":record / friendsMap / 'John' :record / bestFriend :(record (id))",
        ":record / friendsMap / 'John' :record / bestFriend",
        ":(record (id))"
    );
  }

  private void testParse(OpVarPath opPath, String expr) {
    testParse(opPath, expr, expr, null);
  }

  private void testPathNotMatched(OpVarPath opPath, String expr) {
    try {
      UrlReqOutputTrunkVarProjection psi = getPsi(expr);
      final ReadReqPathParsingResult<ReqVarPath> result =
          ReadReqPathPsiParser.parseVarPath(opPath, Person.type.dataType(null), psi, resolver, new ArrayList<>());

      fail("Expected to get 'path not matched' error");
    } catch (PathNotMatchedException ignored) {
    } catch (PsiProcessingException e) {
      e.printStackTrace();
      fail(e.getMessage() + " at " + e.location());
    }
  }

  private void testParse(OpVarPath opPath, String expr, String expectedPath, @Nullable String expectedPsiRemainder) {
    try {

      UrlReqOutputTrunkVarProjection psi = getPsi(expr);
      final ReadReqPathParsingResult<ReqVarPath> result =
          ReadReqPathPsiParser.parseVarPath(opPath, Person.type.dataType(null), psi, resolver, new ArrayList<>());

      if (!result.errors().isEmpty()) {
        for (PsiProcessingError error : result.errors()) {
          System.out.println(error.message() + " at " + error.location());
        }
        fail();
      }

      String s = print(result.path());

      final String actual =
          s.replaceAll("\"", "'"); // pretty printer outputs double quotes, we use single quotes in URLs
      assertEquals(expectedPath, actual);

      final UrlReqOutputTrunkVarProjection trunkProjectionPsi = result.trunkProjectionPsi();
      final UrlReqOutputComaVarProjection comaProjectionPsi = result.comaProjectionPsi();

      if (expectedPsiRemainder == null) {
        if (trunkProjectionPsi != null) {
          // should be empty
          assertEquals("", trunkProjectionPsi.getText());
        }

        if (comaProjectionPsi != null) {
          assertEquals("", comaProjectionPsi.getText());
        }

      } else {
        PsiElement remPsi = trunkProjectionPsi;
        if (remPsi == null) remPsi = comaProjectionPsi;
        assertNotNull(remPsi);

        assertEquals(expectedPsiRemainder, remPsi.getText());
      }

    } catch (PsiProcessingException e) {
//      String psiDump = DebugUtil.psiToString(psi, true, false).trim();
//      System.err.println(psiDump);
      e.printStackTrace();
      fail(e.getMessage() + " at " + e.location());
    }
  }

  private UrlReqOutputTrunkVarProjection getPsi(String projectionString) {
    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();

    UrlReqOutputTrunkVarProjection psiVarPath = EpigraphPsiUtil.parseText(
        projectionString,
        UrlSubParserDefinitions.REQ_OUTPUT_VAR_PROJECTION.rootElementType(),
        UrlReqOutputTrunkVarProjection.class,
        UrlSubParserDefinitions.REQ_OUTPUT_VAR_PROJECTION,
        errorsAccumulator
    );

    if (errorsAccumulator.hasErrors()) {
      for (PsiErrorElement element : errorsAccumulator.errors()) {
        System.err.println(element.getErrorDescription() + " at " +
                           EpigraphPsiUtil.getLocation(element));
      }
      String psiDump = DebugUtil.psiToString(psiVarPath, true, false).trim();
      fail(psiDump);
    }

//    String psiDump = DebugUtil.psiToString(psiVarProjection, true, false).trim();
//    System.out.println(psiDump);

    return psiVarPath;
  }

  private OpVarPath parseOpVarPath(String projectionString) {
    return parseOpVarPath(dataType, projectionString);
  }

  private OpVarPath parseOpVarPath(DataType varDataType, String projectionString) {
    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();

    IdlOpVarPath psiVarProjection = EpigraphPsiUtil.parseText(
        projectionString,
        IdlSubParserDefinitions.OP_VAR_PATH.rootElementType(),
        IdlOpVarPath.class,
        IdlSubParserDefinitions.OP_VAR_PATH,
        errorsAccumulator
    );

    if (errorsAccumulator.hasErrors()) {
      for (PsiErrorElement element : errorsAccumulator.errors()) {
        System.err.println(element.getErrorDescription() + " at " +
                           EpigraphPsiUtil.getLocation(element));
      }
      String psiDump = DebugUtil.psiToString(psiVarProjection, true, false).trim();
      fail(psiDump);
    }

    OpVarPath varPath = null;
    try {
      varPath = OpPathPsiParser.parseVarPath(varDataType, psiVarProjection, resolver);

    } catch (PsiProcessingException e) {
      e.printStackTrace();
      System.err.println(e.getMessage() + " at " + e.location());
      String psiDump = DebugUtil.psiToString(psiVarProjection, true, false).trim();
      fail(psiDump);
    }

    return varPath;
  }

  private String print(ReqVarPath path) {
    StringBackend sb = new StringBackend(120);
    Layouter<NoExceptions> layouter = new Layouter<>(sb, 2);
    ReqPathPrettyPrinter<NoExceptions> printer = new ReqPathPrettyPrinter<>(layouter);
    int len = ProjectionUtils.pathLength(path);
    printer.print(path, len);
    layouter.close();
    return sb.getString();
  }

  private static String lines(String... lines) {
    return Arrays.stream(lines).collect(Collectors.joining("\n"));
  }
}
