package io.epigraph.url;

import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.impl.DebugUtil;
import de.uka.ilkd.pp.Layouter;
import de.uka.ilkd.pp.NoExceptions;
import de.uka.ilkd.pp.StringBackend;
import io.epigraph.idl.parser.projections.IdlSubParserDefinitions;
import io.epigraph.idl.parser.psi.IdlOpOutputVarProjection;
import io.epigraph.lang.TextLocation;
import io.epigraph.projections.StepsAndProjection;
import io.epigraph.projections.op.output.OpOutputFieldProjection;
import io.epigraph.projections.op.output.OpOutputProjectionsPsiParser;
import io.epigraph.projections.op.output.OpOutputVarProjection;
import io.epigraph.projections.req.output.ReqOutputFieldProjection;
import io.epigraph.projections.req.output.ReqOutputProjectionsPrettyPrinter;
import io.epigraph.psi.EpigraphPsiUtil;
import io.epigraph.psi.PsiProcessingException;
import io.epigraph.refs.SimpleTypesResolver;
import io.epigraph.refs.TypesResolver;
import io.epigraph.tests.*;
import io.epigraph.types.DataType;
import io.epigraph.url.parser.UrlParserDefinition;
import io.epigraph.url.parser.psi.UrlFile;
import org.intellij.grammar.LightPsi;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class UrlReqOutputProjectionsTest {
  private DataType dataType = new DataType(Person.type, Person.id);
  private TypesResolver resolver = new SimpleTypesResolver(
      PersonId.type,
      Person.type,
      User.type,
      UserId.type,
      UserRecord.type,
      epigraph.String.type
  );

  private OpOutputFieldProjection personOpProjection = parsePersonOpOutputFieldProjection(
      lines(
          ":(",
          "  id,",
          "  record (",
          "    id {",
          "      ;param1 : epigraph.String = \"hello world\" { doc = \"some doc\" },",
          "    },",
          "    bestFriend :record (",
          "      id,",
          "      bestFriend :record (",
          "        id,",
          "        firstName",
          "      ),",
          "    ),",
          "    friends *( :+id ),",
          "    friendsMap [;keyParam:epigraph.String]( :(id, record (id, firstName) ) )",
          "  )",
          ") ~io.epigraph.tests.User :record (profile)"
      )
  );

//  private OpOutputFieldProjection personFieldProjection =

  @Test
  public void testParsePath() {
    testParse(":record / bestFriend :+record / bestFriend :record ( id, firstName )", 5);
  }

  @Test
  public void testParsePathMap() {
    testParse(":record / friendsMap / +'John' ;keyParam = 'foo' :record ( +firstName )", 5);
  }

  @Test
  public void testParseMap() {
    testParse(":record / friendsMap +[ 'Alice', 'Bob' !sla = 100 ]( :id )", 3);
  }

  @Test
  public void testParseMapStar() {
    testParse(":record / friendsMap [ * ]( :id )", 3);
  }

  @Test
  public void testParseList() {
    testParse(":record / friends *( :id )", 3);
  }

  @Test
  public void testParseParam() {
    testParse(":( id, record ( id ;param1 = 'foo' ) )", 0);
  }

  @Test
  public void testParseParamDefault() {
    testParse(":( id, record ( id ) )", 0); // defaults are not substituted!
  }

  @Test
  public void testParseRequestParam() {
    testParse(
        "/user :( id )?param1='foo'&param2='bar'",
        "user",
        "+user :id", // todo: fix pretty printer to output ":( id )" (take 'parenthesized' into account)
        0,
        "param1", "param2"
    );
  }

  @Test
  public void testParseTail() {
    testParse(
        "/user :id ~User :record ( profile )",
        "user",
        "+user :id ~io.epigraph.tests.User :record ( profile )",
        1
    );
  }

  // todo negative test cases too

  private void testParse(String expr, int steps, String... paramNames) {
    testParse("/user " + expr, "user", steps, paramNames);
  }

  private void testParse(String expr, String field, int steps, String... paramNames) {
    final String expected = expr.replaceFirst("/", "+");
    testParse(expr, field, expected, steps, paramNames);
  }

  private void testParse(String expr, String field, String expectedProjection, int steps, String... paramNames) {
    UrlFile psi = getPsi(expr);
    try {
      @Nullable final RequestUrl requestUrl =
          RequestUrlPsiParser.parseRequestUrl(dataType, personOpProjection, psi, resolver);

      assertNotNull(requestUrl);
      assertEquals(field, requestUrl.fieldName());
      assertEquals(new HashSet<>(Arrays.asList(paramNames)), requestUrl.parameters().keySet());

      @NotNull final StepsAndProjection<ReqOutputFieldProjection> stepsAndProjection =
          requestUrl.fieldProjection();

      assertEquals(steps, stepsAndProjection.pathSteps());

      String s = print(field, stepsAndProjection.projection(), steps);

      final String actual =
          s.replaceAll("\"", "'"); // pretty printer outputs double quotes, we use single quotes in URLs
      assertEquals(expectedProjection, actual);

    } catch (PsiProcessingException e) {
//      String psiDump = DebugUtil.psiToString(psi, true, false).trim();
//      System.err.println(psiDump);
      e.printStackTrace();
      fail(e.getMessage() + " at " + EpigraphPsiUtil.getLocation(e.psi()));
    }
  }

  private UrlFile getPsi(String urlString) {
    final UrlFile urlFile = (UrlFile) LightPsi.parseFile("test.eidl", urlString, UrlParserDefinition.INSTANCE);

    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();
    EpigraphPsiUtil.collectErrors(urlFile, errorsAccumulator);

//    UrlFile urlFile = EpigraphPsiUtil.parseText(
//        urlString,
//        UrlParserDefinition.INSTANCE.getFileNodeType(),
//        UrlFile.class,
//        UrlParserDefinition.INSTANCE,
//        errorsAccumulator
//    );

    if (errorsAccumulator.hasErrors()) {
      for (PsiErrorElement element : errorsAccumulator.errors()) {
        System.err.println(element.getErrorDescription() + " at " +
                           EpigraphPsiUtil.getLocation(element));
      }
      String psiDump = DebugUtil.psiToString(urlFile, true, false).trim();
      fail(psiDump);
    }

//    String psiDump = DebugUtil.psiToString(psiVarProjection, true, false).trim();
//    System.out.println(psiDump);

    return urlFile;
  }

  private OpOutputFieldProjection parsePersonOpOutputFieldProjection(String projectionString) {
    return parseOpOutputFieldProjection(dataType, projectionString);
  }

  private OpOutputFieldProjection parseOpOutputFieldProjection(DataType varDataType, String projectionString) {
    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();

    IdlOpOutputVarProjection psiVarProjection = EpigraphPsiUtil.parseText(
        projectionString,
        IdlSubParserDefinitions.OP_OUTPUT_VAR_PROJECTION.rootElementType(),
        IdlOpOutputVarProjection.class,
        IdlSubParserDefinitions.OP_OUTPUT_VAR_PROJECTION,
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

    OpOutputVarProjection varProjection = null;
    try {
      varProjection = OpOutputProjectionsPsiParser.parseVarProjection(
          varDataType,
          psiVarProjection,
          resolver
      );

    } catch (PsiProcessingException e) {
      e.printStackTrace();
      System.err.println(e.getMessage() + " at " +
                         EpigraphPsiUtil.getLocation(e.psi()));
      String psiDump = DebugUtil.psiToString(psiVarProjection, true, false).trim();
      fail(psiDump);
    }
    return new OpOutputFieldProjection(
        null,
        null,
        varProjection,
        true,
        TextLocation.UNKNOWN
    );
  }

  private String print(String fieldName, ReqOutputFieldProjection projection, int pathSteps) {
    StringBackend sb = new StringBackend(120);
    Layouter<NoExceptions> layouter = new Layouter<>(sb, 2);
    ReqOutputProjectionsPrettyPrinter<NoExceptions> printer = new ReqOutputProjectionsPrettyPrinter<>(layouter);
    printer.print(fieldName, projection, pathSteps);
    layouter.close();
    return sb.getString();
  }

  private static String lines(String... lines) {
    return Arrays.stream(lines).collect(Collectors.joining("\n"));
  }
}
