package io.epigraph.projections.op.input;

import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.impl.DebugUtil;
import de.uka.ilkd.pp.Layouter;
import de.uka.ilkd.pp.NoExceptions;
import de.uka.ilkd.pp.StringBackend;
import io.epigraph.idl.parser.projections.IdlSubParserDefinitions;
import io.epigraph.idl.parser.psi.IdlOpInputVarProjection;
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
public class OpInputProjectionsTest {
  // todo add map tests when codegen is ready
  @Test
  public void testParsing1() throws PsiProcessingException {
    String projectionStr = lines(
        ":(",
        "  +id,",
        "  record (",
        "    +id,",
        "    +bestFriend :record (",
        "      +id,",
        "      bestFriend: id { default: 123 }",
        "    ),",
        "    friends { :_ { *( :+id {} ) } }",
        "  )",
        ") ~io.epigraph.tests.User :record (profile)"
    );

    String expected = lines(
        ":( +id, record ( +id, +bestFriend :record ( +id, bestFriend :id { default: 123 } ), friends *( :+id ) ) )",
        "  ~io.epigraph.tests.User :record ( profile )"
    );

    testParsingVarProjection(
        new DataType(false, Person.type, Person.id),
        projectionStr, expected
    );
  }

  @Test
  public void testParseEmpty() throws PsiProcessingException {
    testParsingVarProjection(
        new DataType(false, Person.type, Person.id),
        ""
        ,
        ":id"
    );
  }

  @Test
  public void testParseDefault() throws PsiProcessingException {
    testParsingVarProjection(":id { default: 123 }");
  }

  @Test
  public void testParseMultipleTags() throws PsiProcessingException {
    testParsingVarProjection(":( id, record )");
  }

  @Test
  public void testParseTail() throws PsiProcessingException {
    testParsingVarProjection(
        new DataType(false, Person.type, Person.id),
        "~io.epigraph.tests.User :id"
        ,
        ":id ~io.epigraph.tests.User :id"
    );
  }

  @Test
  public void testParseTails() throws PsiProcessingException {
    testParsingVarProjection(
        new DataType(false, Person.type, Person.id),
        "~( io.epigraph.tests.User :id, io.epigraph.tests.Person :id )"
        ,
        ":id ~( io.epigraph.tests.User :id, io.epigraph.tests.Person :id )"
    );
  }

  @Test
  public void testParseCustomParams() throws PsiProcessingException {
    testParsingVarProjection(":id { deprecated = true }");
  }

  @Test
  public void testParseRecordDefaultFields() throws PsiProcessingException {
    testParsingVarProjection(":record ( id, firstName )");
  }

  @Test
  public void testParseRecordFieldsWithStructure() throws PsiProcessingException {
    testParsingVarProjection(":record ( id, bestFriend :record ( id ) )");
  }

  @Test
  public void testParseRecordFieldsWithCustomParams() throws PsiProcessingException {
    testParsingVarProjection(":record ( id, bestFriend { deprecated = true :record ( id ) } )");
  }

  @Test
  public void testParseList() throws PsiProcessingException {
    testParsingVarProjection(":record ( friends *( :id ) )");
  }

  private void testParsingVarProjection(String str)
      throws PsiProcessingException {
    testParsingVarProjection(
        new DataType(false, Person.type, Person.id),
        str
        ,
        str
    );

  }

  private void testParsingVarProjection(DataType varDataType, String projectionString, String expected)
      throws PsiProcessingException {

    TypesResolver resolver = new SimpleTypesResolver(
        PersonId.type,
        Person.type,
        User.type,
        UserId.type,
        UserRecord.type
    );

    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();

    IdlOpInputVarProjection psiVarProjection = EpigraphPsiUtil.parseText(
        projectionString,
        IdlSubParserDefinitions.OP_INPUT_VAR_PROJECTION.rootElementType(),
        IdlOpInputVarProjection.class,
        IdlSubParserDefinitions.OP_INPUT_VAR_PROJECTION,
        errorsAccumulator
    );

    if (errorsAccumulator.hasErrors()) {
      for (PsiErrorElement element : errorsAccumulator.errors()) {
        System.err.println(element.getErrorDescription() + " at " +
                           EpigraphPsiUtil.getLocation(element, projectionString));
      }
      String psiDump = DebugUtil.psiToString(psiVarProjection, true, false).trim();
      fail(psiDump);
    }


    OpInputVarProjection varProjection = null;
    try {
      varProjection = OpInputProjectionsPsiParser.parseVarProjection(
          varDataType,
          psiVarProjection,
          resolver
      );

    } catch (PsiProcessingException e) {
      e.printStackTrace();
      System.err.println(e.getMessage() + " at " +
                         EpigraphPsiUtil.getLocation(e.psi(), projectionString));
      String psiDump = DebugUtil.psiToString(psiVarProjection, true, false).trim();
      fail(psiDump);
    }

    StringBackend sb = new StringBackend(120);
    Layouter<NoExceptions> layouter = new Layouter<>(sb, 2);
    OpInputProjectionsPrettyPrinter<NoExceptions> printer = new OpInputProjectionsPrettyPrinter<>(layouter);
    printer.print(varProjection);
    layouter.close();
    String actual = sb.getString();

    assertEquals("\n" + actual, expected, actual);
//    assertEquals(expected.trim(), actual.trim());
  }

  private static String lines(String... lines) {
    return Arrays.stream(lines).collect(Collectors.joining("\n"));
  }
}
