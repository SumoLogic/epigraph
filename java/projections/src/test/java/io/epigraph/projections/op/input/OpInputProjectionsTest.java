package io.epigraph.projections.op.input;

import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.impl.DebugUtil;
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
    // todo add custom params once there's `toString` on data
    String projectionStr = lines(
        ":(",
        "  +id,",
        "  record (",
        "    +id,",
        "    +bestFriend :record (",
        "      +id,",
        "      bestFriend: id { default: 123 }", // TODO: need better toString on Data objects
        "    ),",
        "    friends { :_ { *( :+id {} ) } }",
        "  )",
        ") ~io.epigraph.tests.User :record (profile)"
    );


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
      String psiDump = DebugUtil.psiToString(psiVarProjection, true, false).trim();
      fail(psiDump);
    }


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
      String psiDump = DebugUtil.psiToString(psiVarProjection, true, false).trim();
      fail(psiDump);
    }

    String expected = lines(
        "var io.epigraph.tests.Person (",
        "  id: +io.epigraph.tests.PersonId",
        "  record:",
        "    io.epigraph.tests.PersonRecord {",
        "      fields: {",
        "        +id:",
        "          var io.epigraph.tests.PersonId (",
        "            self: io.epigraph.tests.PersonId",
        "          )",
        "        +bestFriend:",
        "          var io.epigraph.tests.Person (",
        "            record:",
        "              io.epigraph.tests.PersonRecord {",
        "                fields: {",
        "                  +id:",
        "                    var io.epigraph.tests.PersonId (",
        "                      self: io.epigraph.tests.PersonId",
        "                    )",
        "                  bestFriend:",
        "                    var io.epigraph.tests.Person (",
        "                      id:",
        "                        io.epigraph.tests.PersonId",
        "                        default: io.epigraph.tests.PersonId@123",
        "                    )",
        "                }",
        "              }",
        "          )",
        "        friends:",
        "          var list[polymorphic io.epigraph.tests.Person] (",
        "            self:",
        "              list[polymorphic io.epigraph.tests.Person] {",
        "                items:",
        "                  var io.epigraph.tests.Person (",
        "                    id: +io.epigraph.tests.PersonId",
        "                  )",
        "              }",
        "          )",
        "      }",
        "    }",
        ")",
        "~(",
        "  var io.epigraph.tests.User (",
        "    record:",
        "      io.epigraph.tests.UserRecord {",
        "        fields: {",
        "          profile:",
        "            var io.epigraph.tests.Url (",
        "              self: io.epigraph.tests.Url",
        "            )",
        "        }",
        "      }",
        "  )",
        ")"
    );

    assertEquals(expected, varProjection.toString());
  }

  private static String lines(String... lines) {
    return Arrays.stream(lines).collect(Collectors.joining("\n"));
  }
}
