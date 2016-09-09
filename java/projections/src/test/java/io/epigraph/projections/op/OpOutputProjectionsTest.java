package io.epigraph.projections.op;

import com.example.*;
import com.intellij.psi.PsiErrorElement;
import io.epigraph.idl.parser.projections.ProjectionParserDefinitions;
import io.epigraph.idl.parser.psi.IdlOpOutputVarProjection;
import io.epigraph.psi.EpigraphPsiUtil;
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
                        true,
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
                false,
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
                        true,
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
                false,
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
                        true,
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
                    false,
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
                        true,
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
                    false,
                    recursivePersonRecordProjection2
                )
            ), true
        )
    );

    assertEquals(recursivePersonRecordProjection.hashCode(), recursivePersonRecordProjection2.hashCode());
    assertEquals(recursivePersonRecordProjection, recursivePersonRecordProjection2);
  }

  @Test
  public void testParsing() throws OpOutputProjectionsParser.ProjectionParsingException {
    TypesResolver resolver = new SimpleTypesResolver(
        PersonId.type,
        Person.type,
        UserId.type,
        UserRecord.type
    );

    // todo add params
    String projectionStr = ":( \n" +
                           "  +id, \n" +
                           "  record (\n" +
                           "    +id, \n" +
                           "    +bestFriend (" +
                           "      +id, " +
                           "      bestFriend" +
                           "    ) \n" +
                           "  \n) " +
                           ") ~com.example.Person :record (bestFriend)";


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

    try {
      OpOutputVarProjection varProjection = OpOutputProjectionsParser.parseVarProjection(
          new DataType(false, Person.type, Person.id),
          psiVarProjection,
          resolver
      );

      System.out.println(varProjection);
    } catch (OpOutputProjectionsParser.ProjectionParsingException e) {
      e.printStackTrace();
      System.err.println(e.getMessage() + " at " +
                         EpigraphPsiUtil.getLocation(e.psi(), projectionStr));
      fail();
    }
  }

  public static void main(String[] args) {

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
                        true,
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
                    false,
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
                    true,
                    new OpOutputPrimitiveModelProjection(
                        PersonId.type,
                        true,
                        OpParam.params(
                            new OpParam("token", UserId.type) // todo string
                        )
                    )
                ),
                new OpOutputTagProjection(
                    Person.record,
                    true,
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
                                        true,
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
                        Person.record,
                        true,
                        new OpOutputRecordModelProjection(
                            UserRecord.type,
                            false, null,
                            OpOutputRecordModelProjection.fields(
                                new OpOutputFieldProjection(
                                    UserRecord.bestFriend,
                                    OpParam.params(new OpParam("maxAge", PersonId.type)),
                                    new OpOutputVarProjection(
                                        Person.type, // todo ??
                                        new OpOutputTagProjection(
                                            Person.record, // todo ??
                                            false,
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

    System.out.println(personVarProjection);
  }
}
