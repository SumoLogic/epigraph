package io.epigraph.projections.op.input;

import com.intellij.psi.PsiErrorElement;
import io.epigraph.idl.parser.projections.IdlSubParserDefinitions;
import io.epigraph.idl.parser.psi.IdlOpInputVarProjection;
import io.epigraph.psi.EpigraphPsiUtil;
import io.epigraph.psi.PsiProcessingException;
import io.epigraph.tests.*;
import io.epigraph.types.DataType;
import io.epigraph.types.SimpleTypesResolver;
import io.epigraph.types.TypesResolver;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpInputProjectionsTest {
  @Test
  public void testParsing() throws PsiProcessingException {
    TypesResolver resolver = new SimpleTypesResolver(
        PersonId.type,
        Person.type,
        User.type,
        UserId.type,
        UserRecord.type
    );

    // todo add params
    String projectionStr = ":( \n" +
                           "  id +, \n" +
                           "  record (\n" +
                           "    +id, \n" +
                           "    +bestFriend :record (\n" +
                           "      +id, \n" +
                           "      bestFriend: id \n" +
//                           "      bestFriend: id { default: 123 }\n" + // TODO: need better toString on Data objects
                           "    ) \n" +
                           "  \n) " +
                           ") ~io.epigraph.tests.User :record (profile)";


    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();

    IdlOpInputVarProjection psiVarProjection = EpigraphPsiUtil.parseText(
        projectionStr,
        IdlSubParserDefinitions.OP_INPUT_VAR_PROJECTION.rootElementType(),
        IdlOpInputVarProjection.class,
        IdlSubParserDefinitions.OP_INPUT_VAR_PROJECTION,
        errorsAccumulator
    );

    if (errorsAccumulator.hasErrors()) {
      for (PsiErrorElement element : errorsAccumulator.errors()) {
        System.err.println(element.getErrorDescription() + " at " +
                           EpigraphPsiUtil.getLocation(element, projectionStr));
      }
      fail();
    }

//    String psiDump = DebugUtil.psiToString(psiVarProjection, true, false).trim();
//    System.out.println(psiDump);

    OpInputVarProjection varProjection = null;
    try {
      varProjection = OpInputProjectionsPsiParser.parseVarProjection(
          new DataType(false, Person.type, Person.id),
          psiVarProjection,
          resolver
      );

    } catch (PsiProcessingException e) {
      e.printStackTrace();
      System.err.println(e.getMessage() + " at " +
                         EpigraphPsiUtil.getLocation(e.psi(), projectionStr));
      fail();
    }

    String expected = "var io.epigraph.tests.Person (\n" +
                      "  id: +io.epigraph.tests.PersonId\n" +
                      "  record:\n" +
                      "    io.epigraph.tests.PersonRecord {\n" +
                      "      fields: {\n" +
                      "        +id:\n" +
                      "          var io.epigraph.tests.PersonId (\n" +
                      "            self: io.epigraph.tests.PersonId\n" +
                      "          )\n" +
                      "        +bestFriend:\n" +
                      "          var io.epigraph.tests.Person (\n" +
                      "            record:\n" +
                      "              io.epigraph.tests.PersonRecord {\n" +
                      "                fields: {\n" +
                      "                  +id:\n" +
                      "                    var io.epigraph.tests.PersonId (\n" +
                      "                      self: io.epigraph.tests.PersonId\n" +
                      "                    )\n" +
                      "                  bestFriend:\n" +
                      "                    var io.epigraph.tests.Person (\n" +
                      "                      id: io.epigraph.tests.PersonId\n" +
                      "                    )\n" +
                      "                }\n" +
                      "              }\n" +
                      "          )\n" +
                      "      }\n" +
                      "    }\n" +
                      ")\n" +
                      "~(\n" +
                      "  var io.epigraph.tests.User (\n" +
                      "    record:\n" +
                      "      io.epigraph.tests.UserRecord {\n" +
                      "        fields: {\n" +
                      "          profile:\n" +
                      "            var io.epigraph.tests.Url (\n" +
                      "              self: io.epigraph.tests.Url\n" +
                      "            )\n" +
                      "        }\n" +
                      "      }\n" +
                      "  )\n" +
                      ")";

    assertEquals(expected, varProjection.toString());
  }
}
