package io.epigraph.projections.op.output;

import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.impl.DebugUtil;
import de.uka.ilkd.pp.Layouter;
import de.uka.ilkd.pp.NoExceptions;
import de.uka.ilkd.pp.StringBackend;
import io.epigraph.idl.parser.projections.IdlSubParserDefinitions;
import io.epigraph.idl.parser.psi.IdlOpOutputVarProjection;
import io.epigraph.lang.TextLocation;
import io.epigraph.projections.op.OpParam;
import io.epigraph.projections.op.OpParams;
import io.epigraph.projections.op.input.OpInputPrimitiveModelProjection;
import io.epigraph.psi.EpigraphPsiUtil;
import io.epigraph.psi.PsiProcessingException;
import io.epigraph.tests.*;
import io.epigraph.types.DataType;
import io.epigraph.refs.SimpleTypesResolver;
import io.epigraph.refs.TypesResolver;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpOutputProjectionsTest {
  // todo add map tests when codegen is ready
  @Test
  public void testRecursiveFieldProjection() {
    OpOutputRecordModelProjection recursivePersonRecordProjection = new OpOutputRecordModelProjection(
        PersonRecord.type,
        false,
        null,
        null,
        null,
        OpOutputRecordModelProjection.fields(
            new OpOutputFieldProjection(
                PersonRecord.id,
                null,
                null,
                new OpOutputVarProjection(
                    Person.type,
                    TextLocation.UNKNOWN,
                    new OpOutputTagProjection(
                        Person.id,
                        new OpOutputPrimitiveModelProjection(PersonId.type,
                                                             false,
                                                             null,
                                                             null,
                                                             null,
                                                             TextLocation.UNKNOWN
                        ),
                        TextLocation.UNKNOWN
                    )
                ), true, TextLocation.UNKNOWN
            )
        ),
        TextLocation.UNKNOWN
    );

    OpOutputFieldProjection recursiveBestFriendProjection = new OpOutputFieldProjection(
        PersonRecord.bestFriend,
        null,
        null,
        new OpOutputVarProjection(
            Person.type,
            TextLocation.UNKNOWN,
            new OpOutputTagProjection(
                Person.record,
                recursivePersonRecordProjection,
                TextLocation.UNKNOWN
            )
        ), true, TextLocation.UNKNOWN
    );
    recursivePersonRecordProjection.addFieldProjection(
        recursiveBestFriendProjection
    );

    OpOutputRecordModelProjection recursivePersonRecordProjection2 = new OpOutputRecordModelProjection(
        PersonRecord.type,
        false,
        null,
        null,
        null,
        OpOutputRecordModelProjection.fields(
            new OpOutputFieldProjection(
                PersonRecord.id,
                null,
                null,
                new OpOutputVarProjection(
                    Person.type,
                    TextLocation.UNKNOWN,
                    new OpOutputTagProjection(
                        Person.id,
                        new OpOutputPrimitiveModelProjection(PersonId.type,
                                                             false,
                                                             null,
                                                             null,
                                                             null,
                                                             TextLocation.UNKNOWN
                        ),
                        TextLocation.UNKNOWN
                    )
                ), true, TextLocation.UNKNOWN
            )
        ),
        TextLocation.UNKNOWN
    );
    OpOutputFieldProjection recursiveBestFriendProjection2 = new OpOutputFieldProjection(
        PersonRecord.bestFriend,
        null,
        null,
        new OpOutputVarProjection(
            Person.type,
            TextLocation.UNKNOWN,
            new OpOutputTagProjection(
                Person.record,
                recursivePersonRecordProjection2,
                TextLocation.UNKNOWN
            )
        ), true, TextLocation.UNKNOWN
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
        null,
        null,
        OpOutputRecordModelProjection.fields(
            new OpOutputFieldProjection(
                PersonRecord.id,
                null,
                null,
                new OpOutputVarProjection(
                    Person.type,
                    TextLocation.UNKNOWN,
                    new OpOutputTagProjection(
                        Person.id,
                        new OpOutputPrimitiveModelProjection(PersonId.type,
                                                             false,
                                                             null,
                                                             null,
                                                             null,
                                                             TextLocation.UNKNOWN
                        ),
                        TextLocation.UNKNOWN
                    )
                ), true, TextLocation.UNKNOWN
            )
        ), TextLocation.UNKNOWN
    );
    recursivePersonRecordProjection.addFieldProjection(
        new OpOutputFieldProjection(
            PersonRecord.bestFriend,
            null,
            null,
            new OpOutputVarProjection(
                Person.type,
                TextLocation.UNKNOWN,
                new OpOutputTagProjection(
                    Person.record,
                    recursivePersonRecordProjection,
                    TextLocation.UNKNOWN
                )
            ), true, TextLocation.UNKNOWN
        )
    );

    OpOutputRecordModelProjection recursivePersonRecordProjection2 = new OpOutputRecordModelProjection(
        PersonRecord.type,
        false,
        null,
        null,
        null,
        OpOutputRecordModelProjection.fields(
            new OpOutputFieldProjection(
                PersonRecord.id,
                null,
                null,
                new OpOutputVarProjection(
                    Person.type,
                    TextLocation.UNKNOWN,
                    new OpOutputTagProjection(
                        Person.id,
                        new OpOutputPrimitiveModelProjection(PersonId.type,
                                                             false,
                                                             null,
                                                             null,
                                                             null,
                                                             TextLocation.UNKNOWN
                        ),
                        TextLocation.UNKNOWN
                    )
                ), true, TextLocation.UNKNOWN
            )
        ),
        TextLocation.UNKNOWN
    );
    recursivePersonRecordProjection2.addFieldProjection(
        new OpOutputFieldProjection(
            PersonRecord.bestFriend,
            null,
            null,
            new OpOutputVarProjection(
                Person.type,
                TextLocation.UNKNOWN,
                new OpOutputTagProjection(
                    Person.record,
                    recursivePersonRecordProjection2,
                    TextLocation.UNKNOWN
                )
            ), true, TextLocation.UNKNOWN
        )
    );

    assertEquals(recursivePersonRecordProjection.hashCode(), recursivePersonRecordProjection2.hashCode());
    assertEquals(recursivePersonRecordProjection, recursivePersonRecordProjection2);
  }

  @Test
  public void testParsing() throws PsiProcessingException {
    // todo more elaborate example (multiple tails!).
    // Make pretty-printed result consistent with grammar?
    String projectionStr = lines(
        ":(",
        "  +id,",
        "  record (",
        "    +id {",
        "      ; +param1 : epigraph.String = \"hello world\" { doc = \"some doc\" },",
        "    },",
        "    +bestFriend :record (",
        "      +id,",
        "      bestFriend: id",
        // todo get default tag from Person.type, once available
        "    ),",
        "    friends *( :+id )",
//        "    friends *( :+id )",
//        "    friends { *( :+id ) }",
//        "    friends { { *( :+id {} ) } }",
//        "    friends { :_ { *( :+id {} ) } }", // same as above
        // :record (....) {params}
        "  )",
        ") ~io.epigraph.tests.User :record (profile)"
    );


    String expected = lines(
        ":(",
        "  +id,",
        "  record",
        "    (",
        "      +id { ;+param1: epigraph.String = \"hello world\" { doc = \"some doc\" } },",
        "      +bestFriend :record ( +id, bestFriend :id ),",
        "      friends *( :+id )",
        "    )",
        ") ~io.epigraph.tests.User :record ( profile )"
    );

    testParsingVarProjection(
        new DataType(false, Person.type, Person.id),
        projectionStr,
        expected
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
  public void testParseParam() throws PsiProcessingException {
    testParsingVarProjection(
        ":id { ;+param: map[epigraph.String,io.epigraph.tests.Person] []( :id ) = ( \"foo\": < id: 123 > ) { deprecated = true } }"
    );
  }

  @Test
  public void testParseMultipleTags() throws PsiProcessingException {
    testParsingVarProjection(
        ":( id, record )"
    );
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
  public void testParseDoubleTail() throws PsiProcessingException {
    testParsingVarProjection(
        new DataType(false, Person.type, Person.id),
        "~io.epigraph.tests.User :id ~io.epigraph.tests.Person :id"
        ,
        ":id ~io.epigraph.tests.User :id ~io.epigraph.tests.Person :id"
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
    testParsingVarProjection( ":id { deprecated = true }" );
  }

  @Test
  public void testParseRecordDefaultFields() throws PsiProcessingException {
    testParsingVarProjection( ":record ( id, firstName )" );
  }

  @Test
  public void testParseRecordFieldsWithStructure() throws PsiProcessingException {
    testParsingVarProjection( ":record ( id, bestFriend :record ( id ) )" );
  }

  @Test
  public void testParseRecordFieldsWithCustomParams() throws PsiProcessingException {
    testParsingVarProjection( ":record ( id, bestFriend { deprecated = true :record ( id ) } )" );
  }

  @Test
  public void testParseList() throws PsiProcessingException {
    testParsingVarProjection( ":record ( friends *( :id ) )" );
  }

  @Test
  public void testParseMap() throws PsiProcessingException {
    testParsingVarProjection(":record ( friendsMap [ forbidden, ;+param: epigraph.String, doc = \"no keys\" ]( :id ) )");
  }

  @Test
  public void testBuildSampleProjection() {
    OpOutputRecordModelProjection recursivePersonRecordProjection = new OpOutputRecordModelProjection(
        PersonRecord.type,
        false,
        null,
        null,
        null,
        OpOutputRecordModelProjection.fields(
            new OpOutputFieldProjection(
                PersonRecord.id,
                null,
                null,
                new OpOutputVarProjection(
                    Person.type,
                    TextLocation.UNKNOWN,
                    new OpOutputTagProjection(
                        Person.id,
                        new OpOutputPrimitiveModelProjection(PersonId.type,
                                                             false,
                                                             null,
                                                             null,
                                                             null,
                                                             TextLocation.UNKNOWN
                        ),
                        TextLocation.UNKNOWN
                    )
                ), true, TextLocation.UNKNOWN
            )
        ), TextLocation.UNKNOWN
    );
    recursivePersonRecordProjection.addFieldProjection(
        new OpOutputFieldProjection(
            PersonRecord.bestFriend,
            null,
            null,
            new OpOutputVarProjection(
                Person.type,
                TextLocation.UNKNOWN,
                new OpOutputTagProjection(
                    Person.record,
                    recursivePersonRecordProjection,
                    TextLocation.UNKNOWN
                )
            ), true, TextLocation.UNKNOWN
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
                        new OpParams(
                            // todo string
                            new OpParam("token",
                                        new OpInputPrimitiveModelProjection(UserId.type,
                                                                            false,
                                                                            null,
                                                                            null,
                                                                            null,
                                                                            TextLocation.UNKNOWN
                                        ),
                                        TextLocation.UNKNOWN
                            )
                        ),
                        null,
                        null,
                        TextLocation.UNKNOWN
                    ), TextLocation.UNKNOWN
                ),
                new OpOutputTagProjection(
                    Person.record,
                    new OpOutputRecordModelProjection(
                        PersonRecord.type,
                        false,
                        null,
                        null,
                        null,
                        OpOutputRecordModelProjection.fields(
                            new OpOutputFieldProjection(
                                PersonRecord.bestFriend,
                                null, null,
                                // todo make it recursive?
                                new OpOutputVarProjection(
                                    Person.type,
                                    TextLocation.UNKNOWN,
                                    new OpOutputTagProjection(
                                        Person.id,
                                        new OpOutputPrimitiveModelProjection(PersonId.type,
                                                                             false,
                                                                             null,
                                                                             null,
                                                                             null,
                                                                             TextLocation.UNKNOWN
                                        ),
                                        TextLocation.UNKNOWN
                                    )
                                ),
                                true,
                                TextLocation.UNKNOWN
                            )
                        ),
                        TextLocation.UNKNOWN
                    ),
                    TextLocation.UNKNOWN
                )
            )
        ),
        new LinkedHashSet<>(
            Collections.singletonList(
                new OpOutputVarProjection(
                    UserRecord.type,
                    TextLocation.UNKNOWN,
                    new OpOutputTagProjection(
                        User.record,
                        new OpOutputRecordModelProjection(
                            UserRecord.type,
                            false, null, null, null,
                            OpOutputRecordModelProjection.fields(
                                new OpOutputFieldProjection(
                                    UserRecord.bestFriend,
                                    new OpParams(new OpParam("maxAge",
                                                             new OpInputPrimitiveModelProjection(
                                                                 PersonId.type,
                                                                 false,
                                                                 null,
                                                                 null,
                                                                 null,
                                                                 TextLocation.UNKNOWN
                                                             ),
                                                             TextLocation.UNKNOWN
                                    )),
                                    null,
                                    new OpOutputVarProjection(
                                        Person.type, // todo ??
                                        TextLocation.UNKNOWN,
                                        new OpOutputTagProjection(
                                            Person.record, // todo ??
                                            recursivePersonRecordProjection,
                                            TextLocation.UNKNOWN
                                        )
                                    ),
                                    true,
                                    TextLocation.UNKNOWN
                                )
                            ),
                            TextLocation.UNKNOWN
                        ),
                        TextLocation.UNKNOWN
                    )
                )
            )
        ),
        TextLocation.UNKNOWN
    );

    // shouldn't blow up with stack overflow
    String recursivePrint = print(personVarProjection);
//    System.out.println(recursivePrint);
  }

  private void testParsingVarProjection(String str) throws PsiProcessingException {
    testParsingVarProjection(
        new DataType(false, Person.type, Person.id),
        str
        ,
        str
    );
  }

  private void testParsingVarProjection(DataType varDataType, String projectionString, String expected)
      throws PsiProcessingException {

    OpOutputVarProjection varProjection = parseOpOutputVarProjection(varDataType, projectionString);

    String actual = print(varProjection);

    assertEquals("\n" + actual, expected, actual);
//    assertEquals(expected.trim(), actual.trim());
  }

  private OpOutputVarProjection parseOpOutputVarProjection(DataType varDataType, String projectionString) {
    TypesResolver
        resolver = new SimpleTypesResolver(
        PersonId.type,
        Person.type,
        User.type,
        UserId.type,
        UserRecord.type,
        String_Person_Map.type,
        epigraph.String.type
    );

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

  private String print(OpOutputVarProjection projection) {
    StringBackend sb = new StringBackend(120);
    Layouter<NoExceptions> layouter = new Layouter<>(sb, 2);
    OpOutputProjectionsPrettyPrinter<NoExceptions> printer = new OpOutputProjectionsPrettyPrinter<>(layouter);
    printer.print(projection, 0);
    layouter.close();
    return sb.getString();
  }

  private static String lines(String... lines) {
    return Arrays.stream(lines).collect(Collectors.joining("\n"));
  }
}
