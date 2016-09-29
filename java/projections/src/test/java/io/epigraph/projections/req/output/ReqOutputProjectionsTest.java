package io.epigraph.projections.req.output;

import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.impl.DebugUtil;
import de.uka.ilkd.pp.Layouter;
import de.uka.ilkd.pp.NoExceptions;
import de.uka.ilkd.pp.StringBackend;
import io.epigraph.idl.parser.projections.IdlSubParserDefinitions;
import io.epigraph.idl.parser.psi.IdlOpOutputVarProjection;
import io.epigraph.idl.parser.psi.IdlReqOutputTrunkVarProjection;
import io.epigraph.projections.StepsAndProjection;
import io.epigraph.projections.op.output.OpOutputProjectionsPsiParser;
import io.epigraph.projections.op.output.OpOutputVarProjection;
import io.epigraph.psi.EpigraphPsiUtil;
import io.epigraph.psi.PsiProcessingException;
import io.epigraph.tests.*;
import io.epigraph.types.DataType;
import io.epigraph.types.SimpleTypesResolver;
import io.epigraph.types.TypesResolver;
import org.junit.Test;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqOutputProjectionsTest {
  private DataType dataType = new DataType(false, Person.type, Person.id);
  private TypesResolver resolver = new SimpleTypesResolver(
      PersonId.type,
      Person.type,
      User.type,
      UserId.type,
      UserRecord.type,
      epigraph.String.type
  );

  private OpOutputVarProjection personOpProjection = parsePersonOpOutputVarProjection(
      lines(
          ":(",
          "  id,",
          "  record (",
          "    id {",
          "      ; param1 : epigraph.String = \"hello world\" { doc = \"some doc\" },",
          "    },",
          "    bestFriend :record (",
          "      id,",
          "      bestFriend :record (",
          "        id,",
          "        firstName",
          "      ),",
          "    ),",
          "    friends *( :+id )",
          "  )",
          ") ~io.epigraph.tests.User :record (profile)"
      )
  );

  @Test
  public void testParse1() {
    testParse(":record / bestFriend :record / bestFriend :record ( id, firstName )", 5);
  }

  private void testParse(String expr, int steps) {
    IdlReqOutputTrunkVarProjection psi = getPsi(expr);
    try {
      StepsAndProjection<ReqOutputVarProjection> stepsAndProjection =
          ReqOutputProjectionsPsiParser.parseTrunkVarProjection(
              dataType, personOpProjection, psi, resolver
          );
      assertEquals(steps, stepsAndProjection.pathSteps());

      String s = print(stepsAndProjection.projection(), steps);
      assertEquals(expr, s);

    } catch (PsiProcessingException e) {
//      String psiDump = DebugUtil.psiToString(psi, true, false).trim();
//      System.err.println(psiDump);
      e.printStackTrace();
      fail(e.getMessage() + " at " + EpigraphPsiUtil.getLocation(e.psi()));
    }
  }

  private IdlReqOutputTrunkVarProjection getPsi(String projectionString) {
    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();

    IdlReqOutputTrunkVarProjection psiVarProjection = EpigraphPsiUtil.parseText(
        projectionString,
        IdlSubParserDefinitions.REQ_OUTPUT_VAR_PROJECTION.rootElementType(),
        IdlReqOutputTrunkVarProjection.class,
        IdlSubParserDefinitions.REQ_OUTPUT_VAR_PROJECTION,
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

//    String psiDump = DebugUtil.psiToString(psiVarProjection, true, false).trim();
//    System.out.println(psiDump);

    return psiVarProjection;
  }

  private OpOutputVarProjection parsePersonOpOutputVarProjection(String projectionString) {
    return parseOpOutputVarProjection(dataType, projectionString);
  }

  private OpOutputVarProjection parseOpOutputVarProjection(DataType varDataType, String projectionString) {
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
    return varProjection;
  }

  private String print(ReqOutputVarProjection projection, int pathSteps) {
    StringBackend sb = new StringBackend(120);
    Layouter<NoExceptions> layouter = new Layouter<>(sb, 2);
    ReqOutputProjectionsPrettyPrinter<NoExceptions> printer = new ReqOutputProjectionsPrettyPrinter<>(layouter);
    printer.print(projection, pathSteps);
    layouter.close();
    return sb.getString();
  }

  private static String lines(String... lines) {
    return Arrays.stream(lines).collect(Collectors.joining("\n"));
  }
}
