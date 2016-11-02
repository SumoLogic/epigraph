package io.epigraph.projections.op.delete;

import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.impl.DebugUtil;
import de.uka.ilkd.pp.Layouter;
import de.uka.ilkd.pp.NoExceptions;
import de.uka.ilkd.pp.StringBackend;
import io.epigraph.idl.parser.projections.IdlSubParserDefinitions;
import io.epigraph.idl.parser.psi.IdlOpDeleteVarProjection;
import io.epigraph.psi.EpigraphPsiUtil;
import io.epigraph.psi.PsiProcessingException;
import io.epigraph.refs.SimpleTypesResolver;
import io.epigraph.refs.TypesResolver;
import io.epigraph.tests.*;
import io.epigraph.types.DataType;
import org.junit.Test;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpDeleteProjectionsTest {
  @Test
  public void testParsing() throws PsiProcessingException {
    // Make pretty-printed result consistent with grammar?
    String projectionStr = lines(
        ":(",
        "  id,",
        "  record (",
        "    id {",
        "      ; +param1 : epigraph.String = \"hello world\" { doc = \"some doc\" },",
        "    },",
        "    bestFriend +:record (",
        "      id,",
        "      bestFriend: id",
        // todo get default tag from Person.type, once available
        "    ),",
        "    friends *( +:id )",
        "  )",
        ")"
    );


    String expected = lines(
        ":(",
        "  id,",
        "  record",
        "    (",
        "      id { ;+param1: epigraph.String = \"hello world\" { doc = \"some doc\" } },",
        "      bestFriend +:record ( id, bestFriend :id ),",
        "      friends *( +:id )",
        "    )",
        ")"
    );

    testParsingVarProjection(
        new DataType(Person.type, Person.id),
        projectionStr,
        expected
    );
  }

  @Test
  public void testParseEmpty() throws PsiProcessingException {
    testParsingVarProjection(
        new DataType(Person.type, Person.id),
        ""
        ,
        ":id"
    );
  }

  @Test
  public void testParseEmptyPlus() throws PsiProcessingException {
    testParsingVarProjection(
        new DataType(Person.type, Person.id),
        "+"
        ,
        "+:id"
    );
  }

  @Test
  public void testParseParam() throws PsiProcessingException {
    testParsingVarProjection(
        ":id { ;+param: map[epigraph.String,io.epigraph.tests.Person] = ( \"foo\": < id: 123 > ) { deprecated = true []( :id ) } }"
    );
  }

  @Test
  public void testParseParam2() throws PsiProcessingException {
    testParsingVarProjection(
        ":id { ;+param: io.epigraph.tests.UserRecord = { id: 1 } }"
    );
  }

  @Test
  public void testParseMultipleTags() throws PsiProcessingException {
    testParsingVarProjection(
        ":( id, record )"
    );
  }

  @Test
  public void testParseCustomParams() throws PsiProcessingException {
    testParsingVarProjection(":id { deprecated = true }");
  }

  @Test
  public void testParseRecordDefaultFields() throws PsiProcessingException {
    testParsingVarProjection(":record ( id, firstName + )");
  }

  @Test
  public void testParseRecordFieldsWithStructure() throws PsiProcessingException {
    testParsingVarProjection(":record ( id, bestFriend +:record ( id ) )");
  }

  @Test
  public void testParseRecordFieldsWithCustomParams() throws PsiProcessingException {
    testParsingVarProjection(":record ( id, bestFriend { deprecated = true :record ( id ) } )");
  }

  @Test
  public void testParseList() throws PsiProcessingException {
    testParsingVarProjection(":record ( friends *( :id ) )");
  }

  @Test
  public void testParseMap() throws PsiProcessingException {
    testParsingVarProjection(":record ( friendsMap [ forbidden, ;+param: epigraph.String, doc = \"no keys\" ]( :id ) )");
  }

  private void testParsingVarProjection(String str) throws PsiProcessingException {
    testParsingVarProjection(
        new DataType(Person.type, Person.id),
        str
        ,
        str
    );
  }

  private void testParsingVarProjection(DataType varDataType, String projectionString, String expected)
      throws PsiProcessingException {

    OpDeleteVarProjection varProjection = parseOpDeleteVarProjection(varDataType, projectionString);

    String actual = print(varProjection);

    assertEquals("\n" + actual, expected, actual);
//    assertEquals(expected.trim(), actual.trim());
  }

  private OpDeleteVarProjection parseOpDeleteVarProjection(DataType varDataType, String projectionString) {
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

    IdlOpDeleteVarProjection psiVarProjection = EpigraphPsiUtil.parseText(
        projectionString,
        IdlSubParserDefinitions.OP_DELETE_VAR_PROJECTION.rootElementType(),
        IdlOpDeleteVarProjection.class,
        IdlSubParserDefinitions.OP_DELETE_VAR_PROJECTION,
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

    OpDeleteVarProjection varProjection = null;
    try {
      varProjection = OpDeleteProjectionsPsiParser.parseVarProjection(
          varDataType,
          psiVarProjection,
          resolver
      );

    } catch (PsiProcessingException e) {
      e.printStackTrace();
      System.err.println(e.getMessage() + " at " + e.location());
      String psiDump = DebugUtil.psiToString(psiVarProjection, true, false).trim();
      fail(psiDump);
    }
    return varProjection;
  }

  private String print(OpDeleteVarProjection projection) {
    StringBackend sb = new StringBackend(120);
    Layouter<NoExceptions> layouter = new Layouter<>(sb, 2);
    OpDeleteProjectionsPrettyPrinter<NoExceptions> printer = new OpDeleteProjectionsPrettyPrinter<>(layouter);
    printer.print(projection, 0);
    layouter.close();
    return sb.getString();
  }

  private static String lines(String... lines) {
    return Arrays.stream(lines).collect(Collectors.joining("\n"));
  }

  // todo fix constructors some day..

//  @Test
//  public void testRecursiveFieldProjection() {
//    OpDeleteRecordModelProjection recursivePersonRecordProjection = new OpDeleteRecordModelProjection(
//        PersonRecord.type,
//        false,
//        null,
//        null,
//        null,
//        OpDeleteRecordModelProjection.fields(
//            new OpDeleteFieldProjection(
//                PersonRecord.id,
//                null,
//                null,
//                new OpDeleteVarProjection(
//                    Person.type,
//                    TextLocation.UNKNOWN,
//                    new OpDeleteTagProjection(
//                        Person.id,
//                        new OpDeletePrimitiveModelProjection(PersonId.type,
//                                                             false,
//                                                             null,
//                                                             null,
//                                                             null,
//                                                             TextLocation.UNKNOWN
//                        ),
//                        TextLocation.UNKNOWN
//                    )
//                ), true, TextLocation.UNKNOWN
//            )
//        ),
//        TextLocation.UNKNOWN
//    );
//
//    OpDeleteFieldProjection recursiveBestFriendProjection = new OpDeleteFieldProjection(
//        PersonRecord.bestFriend,
//        null,
//        null,
//        new OpDeleteVarProjection(
//            Person.type,
//            TextLocation.UNKNOWN,
//            new OpDeleteTagProjection(
//                Person.record,
//                recursivePersonRecordProjection,
//                TextLocation.UNKNOWN
//            )
//        ), true, TextLocation.UNKNOWN
//    );
//    recursivePersonRecordProjection.addFieldProjection(
//        recursiveBestFriendProjection
//    );
//
//    OpDeleteRecordModelProjection recursivePersonRecordProjection2 = new OpDeleteRecordModelProjection(
//        PersonRecord.type,
//        false,
//        null,
//        null,
//        null,
//        OpDeleteRecordModelProjection.fields(
//            new OpDeleteFieldProjection(
//                PersonRecord.id,
//                null,
//                null,
//                new OpDeleteVarProjection(
//                    Person.type,
//                    TextLocation.UNKNOWN,
//                    new OpDeleteTagProjection(
//                        Person.id,
//                        new OpDeletePrimitiveModelProjection(PersonId.type,
//                                                             false,
//                                                             null,
//                                                             null,
//                                                             null,
//                                                             TextLocation.UNKNOWN
//                        ),
//                        TextLocation.UNKNOWN
//                    )
//                ), true, TextLocation.UNKNOWN
//            )
//        ),
//        TextLocation.UNKNOWN
//    );
//    OpDeleteFieldProjection recursiveBestFriendProjection2 = new OpDeleteFieldProjection(
//        PersonRecord.bestFriend,
//        null,
//        null,
//        new OpDeleteVarProjection(
//            Person.type,
//            TextLocation.UNKNOWN,
//            new OpDeleteTagProjection(
//                Person.record,
//                recursivePersonRecordProjection2,
//                TextLocation.UNKNOWN
//            )
//        ), true, TextLocation.UNKNOWN
//    );
//    recursivePersonRecordProjection2.addFieldProjection(
//        recursiveBestFriendProjection2
//    );
//
//    assertEquals(recursiveBestFriendProjection.hashCode(), recursiveBestFriendProjection2.hashCode());
//    assertEquals(recursiveBestFriendProjection, recursiveBestFriendProjection2);
//  }
//
//  @Test
//  public void testRecursiveRecordProjection() {
//    OpDeleteRecordModelProjection recursivePersonRecordProjection = new OpDeleteRecordModelProjection(
//        PersonRecord.type,
//        false,
//        null,
//        null,
//        null,
//        OpDeleteRecordModelProjection.fields(
//            new OpDeleteFieldProjection(
//                PersonRecord.id,
//                null,
//                null,
//                new OpDeleteVarProjection(
//                    Person.type,
//                    TextLocation.UNKNOWN,
//                    new OpDeleteTagProjection(
//                        Person.id,
//                        new OpDeletePrimitiveModelProjection(PersonId.type,
//                                                             false,
//                                                             null,
//                                                             null,
//                                                             null,
//                                                             TextLocation.UNKNOWN
//                        ),
//                        TextLocation.UNKNOWN
//                    )
//                ), true, TextLocation.UNKNOWN
//            )
//        ), TextLocation.UNKNOWN
//    );
//    recursivePersonRecordProjection.addFieldProjection(
//        new OpDeleteFieldProjection(
//            PersonRecord.bestFriend,
//            null,
//            null,
//            new OpDeleteVarProjection(
//                Person.type,
//                TextLocation.UNKNOWN,
//                new OpDeleteTagProjection(
//                    Person.record,
//                    recursivePersonRecordProjection,
//                    TextLocation.UNKNOWN
//                )
//            ), true, TextLocation.UNKNOWN
//        )
//    );
//
//    OpDeleteRecordModelProjection recursivePersonRecordProjection2 = new OpDeleteRecordModelProjection(
//        PersonRecord.type,
//        false,
//        null,
//        null,
//        null,
//        OpDeleteRecordModelProjection.fields(
//            new OpDeleteFieldProjection(
//                PersonRecord.id,
//                null,
//                null,
//                new OpDeleteVarProjection(
//                    Person.type,
//                    TextLocation.UNKNOWN,
//                    new OpDeleteTagProjection(
//                        Person.id,
//                        new OpDeletePrimitiveModelProjection(PersonId.type,
//                                                             false,
//                                                             null,
//                                                             null,
//                                                             null,
//                                                             TextLocation.UNKNOWN
//                        ),
//                        TextLocation.UNKNOWN
//                    )
//                ), true, TextLocation.UNKNOWN
//            )
//        ),
//        TextLocation.UNKNOWN
//    );
//    recursivePersonRecordProjection2.addFieldProjection(
//        new OpDeleteFieldProjection(
//            PersonRecord.bestFriend,
//            null,
//            null,
//            new OpDeleteVarProjection(
//                Person.type,
//                TextLocation.UNKNOWN,
//                new OpDeleteTagProjection(
//                    Person.record,
//                    recursivePersonRecordProjection2,
//                    TextLocation.UNKNOWN
//                )
//            ), true, TextLocation.UNKNOWN
//        )
//    );
//
//    assertEquals(recursivePersonRecordProjection.hashCode(), recursivePersonRecordProjection2.hashCode());
//    assertEquals(recursivePersonRecordProjection, recursivePersonRecordProjection2);
//  }
//  @Test
//  public void testBuildSampleProjection() {
//    OpDeleteRecordModelProjection recursivePersonRecordProjection = new OpDeleteRecordModelProjection(
//        PersonRecord.type,
//        false,
//        null,
//        null,
//        null,
//        OpDeleteRecordModelProjection.fields(
//            new OpDeleteFieldProjection(
//                PersonRecord.id,
//                null,
//                null,
//                new OpDeleteVarProjection(
//                    Person.type,
//                    TextLocation.UNKNOWN,
//                    new OpDeleteTagProjection(
//                        Person.id,
//                        new OpDeletePrimitiveModelProjection(PersonId.type,
//                                                             false,
//                                                             null,
//                                                             null,
//                                                             null,
//                                                             TextLocation.UNKNOWN
//                        ),
//                        TextLocation.UNKNOWN
//                    )
//                ), true, TextLocation.UNKNOWN
//            )
//        ), TextLocation.UNKNOWN
//    );
//    recursivePersonRecordProjection.addFieldProjection(
//        new OpDeleteFieldProjection(
//            PersonRecord.bestFriend,
//            null,
//            null,
//            new OpDeleteVarProjection(
//                Person.type,
//                TextLocation.UNKNOWN,
//                new OpDeleteTagProjection(
//                    Person.record,
//                    recursivePersonRecordProjection,
//                    TextLocation.UNKNOWN
//                )
//            ), true, TextLocation.UNKNOWN
//        )
//    );
//
//    OpDeleteVarProjection personVarProjection = new OpDeleteVarProjection(
//        Person.type,
//        new LinkedHashSet<>(
//            Arrays.asList(
//                new OpDeleteTagProjection(
//                    Person.id,
//                    new OpDeletePrimitiveModelProjection(
//                        PersonId.type,
//                        true,
//                        new OpParams(
//                            // todo string
//                            new OpParam("token",
//                                        new OpInputPrimitiveModelProjection(UserId.type,
//                                                                            false,
//                                                                            null,
//                                                                            null,
//                                                                            null,
//                                                                            TextLocation.UNKNOWN
//                                        ),
//                                        TextLocation.UNKNOWN
//                            )
//                        ),
//                        null,
//                        null,
//                        TextLocation.UNKNOWN
//                    ), TextLocation.UNKNOWN
//                ),
//                new OpDeleteTagProjection(
//                    Person.record,
//                    new OpDeleteRecordModelProjection(
//                        PersonRecord.type,
//                        false,
//                        null,
//                        null,
//                        null,
//                        OpDeleteRecordModelProjection.fields(
//                            new OpDeleteFieldProjection(
//                                PersonRecord.bestFriend,
//                                null, null,
//                                // todo make it recursive?
//                                new OpDeleteVarProjection(
//                                    Person.type,
//                                    TextLocation.UNKNOWN,
//                                    new OpDeleteTagProjection(
//                                        Person.id,
//                                        new OpDeletePrimitiveModelProjection(PersonId.type,
//                                                                             false,
//                                                                             null,
//                                                                             null,
//                                                                             null,
//                                                                             TextLocation.UNKNOWN
//                                        ),
//                                        TextLocation.UNKNOWN
//                                    )
//                                ),
//                                true,
//                                TextLocation.UNKNOWN
//                            )
//                        ),
//                        TextLocation.UNKNOWN
//                    ),
//                    TextLocation.UNKNOWN
//                )
//            )
//        ),
//        Collections.singletonList(
//            new OpDeleteVarProjection(
//                UserRecord.type,
//                TextLocation.UNKNOWN,
//                new OpDeleteTagProjection(
//                    User.record,
//                    new OpDeleteRecordModelProjection(
//                        UserRecord.type,
//                        false, null, null, null,
//                        OpDeleteRecordModelProjection.fields(
//                            new OpDeleteFieldProjection(
//                                UserRecord.bestFriend,
//                                new OpParams(new OpParam("maxAge",
//                                                         new OpInputPrimitiveModelProjection(
//                                                             PersonId.type,
//                                                             false,
//                                                             null,
//                                                             null,
//                                                             null,
//                                                             TextLocation.UNKNOWN
//                                                         ),
//                                                         TextLocation.UNKNOWN
//                                )),
//                                null,
//                                new OpDeleteVarProjection(
//                                    Person.type, // todo ??
//                                    TextLocation.UNKNOWN,
//                                    new OpDeleteTagProjection(
//                                        Person.record, // todo ??
//                                        recursivePersonRecordProjection,
//                                        TextLocation.UNKNOWN
//                                    )
//                                ),
//                                true,
//                                TextLocation.UNKNOWN
//                            )
//                        ),
//                        TextLocation.UNKNOWN
//                    ),
//                    TextLocation.UNKNOWN
//                )
//            )
//        ),
//        TextLocation.UNKNOWN
//    );
//
//    // shouldn't blow up with stack overflow
//    String recursivePrint = print(personVarProjection);
////    System.out.println(recursivePrint);
//  }

}
