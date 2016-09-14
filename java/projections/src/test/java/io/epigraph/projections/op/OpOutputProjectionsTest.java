package io.epigraph.projections.op;

import com.intellij.psi.PsiErrorElement;
import io.epigraph.idl.parser.projections.ProjectionParserDefinitions;
import io.epigraph.idl.parser.psi.IdlOpOutputVarProjection;
import io.epigraph.projections.ProjectionParsingException;
import io.epigraph.projections.op.input.OpInputPrimitiveModelProjection;
import io.epigraph.projections.op.output.*;
import io.epigraph.psi.EpigraphPsiUtil;
import io.epigraph.tests.*;
import io.epigraph.types.DataType;
import io.epigraph.types.SimpleTypesResolver;
import io.epigraph.types.TypesResolver;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpOutputProjectionsTest {
  @Test
  public void testRecursiveFieldProjection() {
    OpOutputRecordModelProjection recursivePersonRecordProjection = new OpOutputRecordModelProjection(
        PersonRecord.type,
        false,
        null,
        OpOutputRecordModelProjection.fields(
            new OpOutputFieldProjection(
                PersonRecord.id,
                null,
                new OpOutputVarProjection(
                    Person.type,
                    new OpOutputTagProjection(
                        Person.id,
                        new OpOutputPrimitiveModelProjection(PersonId.type, false, null)
                    )
                ), true
            )
        )
    );

    OpOutputFieldProjection recursiveBestFriendProjection = new OpOutputFieldProjection(
        PersonRecord.bestFriend,
        null,
        new OpOutputVarProjection(
            Person.type,
            new OpOutputTagProjection(
                Person.record,
                recursivePersonRecordProjection
            )
        ), true
    );
    recursivePersonRecordProjection.addFieldProjection(
        recursiveBestFriendProjection
    );

    OpOutputRecordModelProjection recursivePersonRecordProjection2 = new OpOutputRecordModelProjection(
        PersonRecord.type,
        false,
        null,
        OpOutputRecordModelProjection.fields(
            new OpOutputFieldProjection(
                PersonRecord.id,
                null,
                new OpOutputVarProjection(
                    Person.type,
                    new OpOutputTagProjection(
                        Person.id,
                        new OpOutputPrimitiveModelProjection(PersonId.type, false, null)
                    )
                ), true
            )
        )
    );
    OpOutputFieldProjection recursiveBestFriendProjection2 = new OpOutputFieldProjection(
        PersonRecord.bestFriend,
        null,
        new OpOutputVarProjection(
            Person.type,
            new OpOutputTagProjection(
                Person.record,
                recursivePersonRecordProjection2
            )
        ), true
    );
    recursivePersonRecordProjection2.addFieldProjection(
        recursiveBestFriendProjection2
    );

    assertEquals(recursiveBestFriendProjection.hashCode(), recursiveBestFriendProjection2.hashCode());
    assertEquals(recursiveBestFriendProjection, recursiveBestFriendProjection2);
  }

  @Test
  public void testRecursiveRecordProjection() {
    OpOutputRecordModelProjection recursivePersonRecordProjection = new OpOutputRecordModelProjection(
        PersonRecord.type,
        false,
        null,
        OpOutputRecordModelProjection.fields(
            new OpOutputFieldProjection(
                PersonRecord.id,
                null,
                new OpOutputVarProjection(
                    Person.type,
                    new OpOutputTagProjection(
                        Person.id,
                        new OpOutputPrimitiveModelProjection(PersonId.type, false, null)
                    )
                ), true
            )
        )
    );
    recursivePersonRecordProjection.addFieldProjection(
        new OpOutputFieldProjection(
            PersonRecord.bestFriend,
            null,
            new OpOutputVarProjection(
                Person.type,
                new OpOutputTagProjection(
                    Person.record,
                    recursivePersonRecordProjection
                )
            ), true
        )
    );

    OpOutputRecordModelProjection recursivePersonRecordProjection2 = new OpOutputRecordModelProjection(
        PersonRecord.type,
        false,
        null,
        OpOutputRecordModelProjection.fields(
            new OpOutputFieldProjection(
                PersonRecord.id,
                null,
                new OpOutputVarProjection(
                    Person.type,
                    new OpOutputTagProjection(
                        Person.id,
                        new OpOutputPrimitiveModelProjection(PersonId.type, false, null)
                    )
                ), true
            )
        )
    );
    recursivePersonRecordProjection2.addFieldProjection(
        new OpOutputFieldProjection(
            PersonRecord.bestFriend,
            null,
            new OpOutputVarProjection(
                Person.type,
                new OpOutputTagProjection(
                    Person.record,
                    recursivePersonRecordProjection2
                )
            ), true
        )
    );

    assertEquals(recursivePersonRecordProjection.hashCode(), recursivePersonRecordProjection2.hashCode());
    assertEquals(recursivePersonRecordProjection, recursivePersonRecordProjection2);
  }

  @Test
  public void testParsing() throws ProjectionParsingException {
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
                           "    ) \n" +
                           "  \n) " +
                           ") ~io.epigraph.tests.User :record (profile)";


    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();

    IdlOpOutputVarProjection psiVarProjection = EpigraphPsiUtil.parseText(
        projectionStr,
        ProjectionParserDefinitions.OP_OUTPUT_VAR_PROJECTION.rootElementType(),
        IdlOpOutputVarProjection.class,
        ProjectionParserDefinitions.OP_OUTPUT_VAR_PROJECTION,
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

    OpOutputVarProjection varProjection = null;
    try {
      varProjection = OpOutputProjectionsParser.parseVarProjection(
          new DataType(false, Person.type, Person.id),
          psiVarProjection,
          resolver
      );

    } catch (ProjectionParsingException e) {
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

  @Test
  public void testBuildSampleProjection() {
    OpOutputRecordModelProjection recursivePersonRecordProjection = new OpOutputRecordModelProjection(
        PersonRecord.type,
        false,
        null,
        OpOutputRecordModelProjection.fields(
            new OpOutputFieldProjection(
                PersonRecord.id,
                null,
                new OpOutputVarProjection(
                    Person.type,
                    new OpOutputTagProjection(
                        Person.id,
                        new OpOutputPrimitiveModelProjection(PersonId.type, false, null)
                    )
                ), true
            )
        )
    );
    recursivePersonRecordProjection.addFieldProjection(
        new OpOutputFieldProjection(
            PersonRecord.bestFriend,
            null,
            new OpOutputVarProjection(
                Person.type,
                new OpOutputTagProjection(
                    Person.record,
                    recursivePersonRecordProjection
                )
            ), true
        )
    );

    OpOutputVarProjection personVarProjection = new OpOutputVarProjection(
        Person.type,
        new LinkedHashSet<>(
            Arrays.asList(
                new OpOutputTagProjection(
                    Person.id,
                    new OpOutputPrimitiveModelProjection(
                        PersonId.type,
                        true,
                        OpParam.params(
                            // todo string
                            new OpParam("token", new OpInputPrimitiveModelProjection(UserId.type, false, null))
                        )
                    )
                ),
                new OpOutputTagProjection(
                    Person.record,
                    new OpOutputRecordModelProjection(
                        PersonRecord.type,
                        false,
                        null,
                        OpOutputRecordModelProjection.fields(
                            new OpOutputFieldProjection(
                                PersonRecord.bestFriend,
                                null,
                                // todo make it recursive?
                                new OpOutputVarProjection(
                                    Person.type,
                                    new OpOutputTagProjection(
                                        Person.id,
                                        new OpOutputPrimitiveModelProjection(PersonId.type, false, null)
                                    )
                                ),
                                true
                            )
                        )
                    )
                )
            )
        ),
        new LinkedHashSet<>(
            Collections.singletonList(
                new OpOutputVarProjection(
                    UserRecord.type,
                    new OpOutputTagProjection(
                        User.record,
                        new OpOutputRecordModelProjection(
                            UserRecord.type,
                            false, null,
                            OpOutputRecordModelProjection.fields(
                                new OpOutputFieldProjection(
                                    UserRecord.bestFriend,
                                    OpParam.params(new OpParam("maxAge",
                                                               new OpInputPrimitiveModelProjection(
                                                                   PersonId.type,
                                                                   false,
                                                                   null
                                                               )
                                    )),
                                    new OpOutputVarProjection(
                                        Person.type, // todo ??
                                        new OpOutputTagProjection(
                                            Person.record, // todo ??
                                            recursivePersonRecordProjection
                                        )
                                    ),
                                    true
                                )
                            )
                        )
                    )
                )
            )
        )
    );

//    System.out.println(personVarProjection);
  }
}
