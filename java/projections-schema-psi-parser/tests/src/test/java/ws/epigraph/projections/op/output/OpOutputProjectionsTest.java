/*
 * Copyright 2017 Sumo Logic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ws.epigraph.projections.op.output;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.ProjectionUtils;
import ws.epigraph.projections.gen.ProjectionReferenceName;
import ws.epigraph.projections.op.input.OpInputPsiProcessingContext;
import ws.epigraph.projections.op.input.OpInputReferenceContext;
import ws.epigraph.psi.DefaultPsiProcessingContext;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingContext;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.SimpleTypesResolver;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.schema.parser.SchemaSubParserDefinitions;
import ws.epigraph.schema.parser.psi.SchemaOpOutputVarProjection;
import ws.epigraph.tests.*;
import ws.epigraph.types.DataType;
import ws.epigraph.types.DatumType;
import ws.epigraph.types.Type;

import java.util.List;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.*;
import static ws.epigraph.test.TestUtil.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpOutputProjectionsTest {
  private final TestConfig DEFAULT_CONFIG = new TestConfig();

  private final DataType dataType = new DataType(Person.type, Person.id);

  @Test
  public void testParsing() throws PsiProcessingException {
    // todo more elaborate example (multiple tails!).
    // Make pretty-printed result consistent with grammar?
    String projectionStr = lines(
        ":(",
        "  id,",
        "  `record` (",
        "    id {",
        "      ; +param1 : epigraph.String { @epigraph.annotations.Doc \"some doc\", default : \"hello world\" },",
        "    },",
        "    bestFriend :`record` (",
        "      id,",
        "      bestFriend: id",
        // todo get default tag from Person.type, once available
        "    )",
        "    friends *( :id )",
//        "    friends *( :+id )",
//        "    friends { *( :+id ) }",
//        "    friends { { *( :+id {} ) } }",
//        "    friends { :_ { *( :+id {} ) } }", // same as above
        // :`record` (....) {params}
        "  ) ~ws.epigraph.tests.UserRecord (profile)",
        ") ~~(",
        "      ws.epigraph.tests.User :`record` (profile)",
        "        ~~ws.epigraph.tests.SubUser :`record` (worstEnemy(id)),",
        "      ws.epigraph.tests.User2 :`record` (worstEnemy(id))",
        ")"
    );

    String expected = lines(
        ":(",
        "  id,",
        "  `record` (",
        "    id { ;+param1: epigraph.String { @epigraph.annotations.Doc \"some doc\", default: \"hello world\" } },",
        "    bestFriend :`record` ( id, bestFriend :id ),",
        "    friends *( :id )",
        "  ) ~ws.epigraph.tests.UserRecord ( profile )",
        ")",
        "  ~~(",
        "    ws.epigraph.tests.User :`record` ( profile ) ~~ws.epigraph.tests.SubUser :`record` ( worstEnemy ( id ) ),",
        "    ws.epigraph.tests.User2 :`record` ( worstEnemy ( id ) )",
        "  )"
    );

    testParsingVarProjection(
        dataType,
        projectionStr,
        expected
    );

    testTailsNormalization(
        projectionStr,
        SubUser.type,
        lines(
            ":(",
            "  `record` (",
            "    worstEnemy ( id ),",
            "    profile,",
            "    id { ;+param1: epigraph.String { @epigraph.annotations.Doc \"some doc\", default: \"hello world\" } },",
            "    bestFriend :`record` ( id, bestFriend :id ),",
            "    friends *( :id )",
            "  ),",
            "  id",
            ")"
        )
    );
  }

  @Test
  public void testParseEmpty() throws PsiProcessingException {
    testParsingVarProjection(
        dataType,
        ""
        ,
        ":id"
    );
  }

  @Test
  public void testParseParam() throws PsiProcessingException {
    testParsingVarProjection(
        lines(
            ":id {",
            "  ;+param: map[epigraph.String,ws.epigraph.tests.Person]",
            "    { @epigraph.annotations.Deprecated, default: ( \"foo\": < id: 123 > ) } [ ]( :id )",
            "}"
        )
    );
  }

  @Test
  public void testParseMultipleTags() throws PsiProcessingException {
    testParsingVarProjection(":( id, `record` )");
  }

  @Test
  public void testParseRecursive() throws PsiProcessingException {
    OpOutputVarProjection vp = testParsingVarProjection("$self = :( id, `record` ( id, bestFriend $self ) )");
    final ProjectionReferenceName name = vp.referenceName();
    assertNotNull(name);
    assertEquals("self", name.toString());
  }

  @Test
  public void testParseStupidRecursive() throws PsiProcessingException {
    try {
      testParsingVarProjection("$self = $self");
      fail("Expected to get an error");
    } catch (@SuppressWarnings("ErrorNotRethrown") AssertionError e) {
      assertTrue(e.getMessage().contains("is not defined"));
    }
  }

  @SuppressWarnings("ConstantConditions")
  @Test
  public void testParseModelRecursive() throws PsiProcessingException {
    final OpOutputVarProjection vp =
        testParsingVarProjection(":`record` $rr = ( id, bestFriend :`record` $rr )");

    // check that it's actually correct

    OpOutputRecordModelProjection rmp = (OpOutputRecordModelProjection) vp.singleTagProjection().projection();
    assertEquals(2, rmp.fieldProjections().size());

    rmp = (OpOutputRecordModelProjection) rmp.fieldProjection("bestFriend")
        .fieldProjection()
        .varProjection()
        .singleTagProjection()
        .projection();

    assertEquals(2, rmp.fieldProjections().size());
  }

  @SuppressWarnings("ConstantConditions")
  @Test
  public void testNormalizeRecursive() throws PsiProcessingException {
    final OpOutputVarProjection vp =
        testParsingVarProjection(":`record` $rr = ( id, bestFriend :`record` $rr )")
            .normalizedForType(User.type);

    // check that it's actually correct

    OpOutputRecordModelProjection rmp = (OpOutputRecordModelProjection) vp.singleTagProjection().projection();
    assertEquals(2, rmp.fieldProjections().size());

    rmp = (OpOutputRecordModelProjection) rmp.fieldProjection("bestFriend")
        .fieldProjection()
        .varProjection()
        .singleTagProjection()
        .projection();

    assertEquals(2, rmp.fieldProjections().size());
  }

  @SuppressWarnings("ConstantConditions")
  @Test
  public void testParseSelfVarRecursive() throws PsiProcessingException {
    final OpOutputVarProjection vp =
        testParsingVarProjection(":`record` ( id, worstEnemy $rr = ( id, worstEnemy $rr ) )");

    // check that it's actually correct

    OpOutputRecordModelProjection rmp = (OpOutputRecordModelProjection) vp.singleTagProjection().projection();
    assertEquals(2, rmp.fieldProjections().size());

    rmp = (OpOutputRecordModelProjection) rmp.fieldProjection("worstEnemy")
        .fieldProjection()
        .varProjection()
        .singleTagProjection()
        .projection();
    assertEquals(2, rmp.fieldProjections().size());

    rmp = (OpOutputRecordModelProjection) rmp.fieldProjection("worstEnemy")
        .fieldProjection()
        .varProjection()
        .singleTagProjection()
        .projection();

    assertEquals(2, rmp.fieldProjections().size());
  }

  @SuppressWarnings("ConstantConditions")
  @Test
  public void testNormalizeSelfVarRecursive() throws PsiProcessingException {
    final OpOutputVarProjection vp =
        testParsingVarProjection(":`record` ( id, worstEnemy $rr = ( id, worstEnemy $rr ) )")
            .normalizedForType(User.type);

    // check that it's actually correct

    OpOutputRecordModelProjection rmp = (OpOutputRecordModelProjection) vp.singleTagProjection().projection();
    assertEquals(2, rmp.fieldProjections().size());

    rmp = (OpOutputRecordModelProjection) rmp.fieldProjection("worstEnemy")
        .fieldProjection()
        .varProjection()
        .singleTagProjection()
        .projection();
    assertEquals(2, rmp.fieldProjections().size());

    rmp = (OpOutputRecordModelProjection) rmp.fieldProjection("worstEnemy")
        .fieldProjection()
        .varProjection()
        .singleTagProjection()
        .projection();

    assertEquals(2, rmp.fieldProjections().size());
  }

  @SuppressWarnings("ConstantConditions")
  @Test
  public void testNormalizeSelfVarModelRecursive() throws PsiProcessingException {
    final OpOutputVarProjection vp =
        testParsingVarProjection(":`record` ( id, worstEnemy $rr = ( id, worstEnemy $rr ) )");


    // rmp = :record
    OpOutputRecordModelProjection rmp = (OpOutputRecordModelProjection) vp.singleTagProjection().projection();
    assertEquals(2, rmp.fieldProjections().size());

    // rmp = $rmp/worstEnemy:record
    rmp = (OpOutputRecordModelProjection) rmp.fieldProjection("worstEnemy")
        .fieldProjection()
        .varProjection()
        .singleTagProjection()
        .projection();
    assertEquals(2, rmp.fieldProjections().size());

    rmp = rmp.normalizedForType(UserRecord.type);
    assertEquals(2, rmp.fieldProjections().size());

    // rmp = $rmp/worstEnemy:record
    rmp = (OpOutputRecordModelProjection) rmp.fieldProjection("worstEnemy")
        .fieldProjection()
        .varProjection()
        .singleTagProjection()
        .projection();

    assertEquals(2, rmp.fieldProjections().size());
  }

  @Test
  public void testNormalizeDiamond() throws PsiProcessingException {
    final OpOutputVarProjection vp = testParsingVarProjection(
        lines(
            ":`record` ( id )",
            "  ~(",
            "    ws.epigraph.tests.UserRecord ( firstName ) ~ws.epigraph.tests.UserRecord3 ( diamond ),",
            "    ws.epigraph.tests.UserRecord2 ( lastName ) ~ws.epigraph.tests.UserRecord3 ( diamond )",
            "  )"
        )
    );
    //noinspection ConstantConditions
    final OpOutputModelProjection<?, ?, ?> mp = vp.singleTagProjection().projection();
    final OpOutputModelProjection<?, ?, ?> normalized = mp.normalizedForType(UserRecord3.type);

    String p = printOpOutputModelProjection(normalized);
    assertEquals("( diamond, firstName, id )", p); // but not `lastName`!
  }

  @Test
  public void testSuperTypeRef() throws PsiProcessingException {
    // todo add to other parser tests too
    OpOutputVarProjection personProjection = testParsingVarProjection(":id");

    PsiProcessingContext ppc = new DefaultPsiProcessingContext();

    final OpOutputReferenceContext referenceContext =
        new OpOutputReferenceContext(ProjectionReferenceName.EMPTY, null, ppc);
    referenceContext.varReference(Person.type, "ref", false, TextLocation.UNKNOWN);

    referenceContext.resolveEntityRef("ref", personProjection, TextLocation.UNKNOWN);
    failIfHasErrors(ppc.messages());

    TestConfig testConfig = new TestConfig() {
      @Override
      @NotNull DataType dataType() {
        return new DataType(User.type, null);
      }

      @Override
      @NotNull OpOutputReferenceContext outputReferenceContext(PsiProcessingContext ctx) {
        return referenceContext;
      }
    };

    testParsingVarProjection(testConfig, ":`record` ( bestFriend4 $ref )", ":`record` ( bestFriend4 $ref = :id )");
  }

  @Test
  public void testParseModelRef() throws PsiProcessingException {
    OpOutputVarProjection personProjection = testParsingVarProjection(":`record` ( id )");

    @SuppressWarnings("ConstantConditions")
    OpOutputModelProjection<?, ?, ?> personRecordProjection = personProjection.singleTagProjection().projection();

    OpOutputVarProjection personRecordVarProjection = new OpOutputVarProjection(
        PersonRecord.type,
        ProjectionUtils.singletonLinkedHashMap(
            DatumType.MONO_TAG_NAME,
            new OpOutputTagProjectionEntry(
                PersonRecord.type.self(),
                personRecordProjection,
                TextLocation.UNKNOWN
            )
        ),
        false,
        null,
        TextLocation.UNKNOWN
    );

    PsiProcessingContext ppc = new DefaultPsiProcessingContext();

    final OpOutputReferenceContext referenceContext =
        new OpOutputReferenceContext(ProjectionReferenceName.EMPTY, null, ppc);
    referenceContext.varReference(PersonRecord.type, "ref", false, TextLocation.UNKNOWN);
//    referenceContext.resolve("ref", personRecordVarProjection, TextLocation.UNKNOWN, ppc);
    failIfHasErrors(ppc.messages());

    TestConfig testConfig = new TestConfig() {
      @Override
      @NotNull DataType dataType() {
        return new DataType(Person.type, null);
      }

      @Override
      @NotNull OpOutputReferenceContext outputReferenceContext(PsiProcessingContext ctx) {
        return referenceContext;
      }
    };

    testParsingVarProjection(testConfig, ":`record` $ref", ":`record` <unresolved>");
//    testParsingVarProjection(testConfig, ":`record` $ref", ":`record` ( id )");
    // we don't get reference name here because it belongs to var, not model projection ( personRecordVar )
//    testParsingVarProjection(testConfig, ":`record` $ref", ":`record` $ref = ( id )");

    // should not result in class cast
    referenceContext.resolveEntityRef("ref", personRecordVarProjection, TextLocation.UNKNOWN);
  }

  @Test
  public void testParseWrongTypeRef() throws PsiProcessingException {
    // todo add to other parser tests too
    OpOutputVarProjection paginationProjection =
        testParsingVarProjection(new DataType(PaginationInfo.type, null), "()", "");

    PsiProcessingContext ppc = new DefaultPsiProcessingContext();

    final OpOutputReferenceContext referenceContext =
        new OpOutputReferenceContext(ProjectionReferenceName.EMPTY, null, ppc);
    referenceContext.varReference(PaginationInfo.type, "ref", false, TextLocation.UNKNOWN);

    referenceContext.resolveEntityRef("ref", paginationProjection, TextLocation.UNKNOWN);
    failIfHasErrors(ppc.messages());

    TestConfig testConfig = new TestConfig() {
      @Override
      @NotNull OpOutputReferenceContext outputReferenceContext(PsiProcessingContext ctx) {
        return referenceContext;
      }
    };

    try {
      testParsingVarProjection(testConfig, ":`record` ( bestFriend $ref )", ":`record` ( bestFriend )");
      fail("Var reference built for an incompatible type should not be accepted");
    } catch (@SuppressWarnings("ErrorNotRethrown") AssertionError e) {
      assertTrue(e.getMessage().contains(
          "Projection 'ref' type 'ws.epigraph.tests.PaginationInfo' is not compatible with type 'ws.epigraph.tests.Person'"
          )
      );
    }
  }

  @Test
  public void testParseTail() throws PsiProcessingException {
    testParsingVarProjection(
        "~~ws.epigraph.tests.User :id"
        ,
        ":id ~~ws.epigraph.tests.User :id"
    );
  }

  @Test
  public void testParseDoubleTail() throws PsiProcessingException {
    final OpOutputVarProjection projection = testParsingVarProjection(
        dataType,
        "~~ws.epigraph.tests.User :id ~~ws.epigraph.tests.SubUser :id"
        ,
        ":id ~~ws.epigraph.tests.User :id ~~ws.epigraph.tests.SubUser :id"
    );

    List<OpOutputVarProjection> tails = projection.polymorphicTails();
    assertNotNull(tails);
    assertEquals(1, tails.size());
    OpOutputVarProjection tail = tails.get(0);

    tails = tail.polymorphicTails();
    assertNotNull(tails);
    assertEquals(1, tails.size());
    tail = tails.get(0);

    tails = tail.polymorphicTails();
    assertNull(tails);
  }

  @Test
  public void testParseTails() throws PsiProcessingException {
    testParsingVarProjection(
        "~~( ws.epigraph.tests.User :id, ws.epigraph.tests.User2 :id )"
        ,
        ":id ~~( ws.epigraph.tests.User :id, ws.epigraph.tests.User2 :id )"
    );
  }

  @Test
  public void testParseModelTail() throws PsiProcessingException {
    testParsingVarProjection(
        ":`record` ( worstEnemy ( id ) ~ws.epigraph.tests.UserRecord ( profile ) )"
    );
  }

  @Test
  public void testParseCustomParams() throws PsiProcessingException {
    testParsingVarProjection(":id { @epigraph.annotations.Deprecated }");
  }

  @Test
  public void testParseRecordDefaultFields() throws PsiProcessingException {
    testParsingVarProjection(":`record` ( id, firstName )");
  }

  @Test
  public void testParseRecordFieldsWithStructure() throws PsiProcessingException {
    testParsingVarProjection(":`record` ( id, bestFriend :`record` ( id ) )");
  }

  @Test
  public void testParseRecordFieldsWithCustomParams() throws PsiProcessingException {
    testParsingVarProjection(":`record` ( id, bestFriend :`record` { @epigraph.annotations.Deprecated } ( id ) )");
  }

  @Test
  public void testParseList() throws PsiProcessingException {
    testParsingVarProjection(":`record` ( friends *( :id ) )");
  }

  @Test
  public void testParseList2() throws PsiProcessingException {
    testParsingVarProjection(":`record` ( friends * :id )", ":`record` ( friends *( :id ) )");
  }

  @Test
  public void testParseList3() throws PsiProcessingException {
    testParsingVarProjection(":`record` ( friends * :`record` ( id ) )", ":`record` ( friends *( :`record` ( id ) ) )");
  }

  @Test
  public void testParseMap() throws PsiProcessingException {
    testParsingVarProjection(
        ":`record` ( friendsMap [ forbidden, ;+param: epigraph.String, @epigraph.annotations.Doc \"no keys\" ]( :id ) )");
  }

  @Test
  public void testParseMapWithKeyProjection() throws PsiProcessingException {
    testParsingVarProjection(
        lines(
            ":`record` (",
            "  personRecToPersonRec [ ;param: epigraph.String, @epigraph.annotations.Doc \"bla\", projection: ( firstName, lastName ) ](",
            "    ( firstName ) )",
            ")"
        )
    );
  }

  @Test
  public void testTailsNormalization() throws PsiProcessingException {
    testTailsNormalization(
        ":id ~~ws.epigraph.tests.User:`record`(id)",
        Person.type,
        ":id ~~ws.epigraph.tests.User :`record` ( id )"
    );

    testTailsNormalization(
        ":id ~~ws.epigraph.tests.User:`record`(id)",
        SubUser.type,
        ":( `record` ( id ), id )"
    );

    testTailsNormalization(
        ":`record`(id, bestFriend:id~~ws.epigraph.tests.User:`record`(id))",
        Person.type,
        ":`record` ( id, bestFriend :id ~~ws.epigraph.tests.User :`record` ( id ) )"
        // bestFriend field can still contain a User
    );

    testTailsNormalization(
        ":`record`(id)~~ws.epigraph.tests.User :`record`(profile)",
        User.type,
        ":`record` ( profile, id )"
    );

    // parameters merging
    testTailsNormalization(
        ":`record`(id,firstName{;param:epigraph.String})~~ws.epigraph.tests.User :`record`(firstName{;param:epigraph.Integer})",
        User.type,
        ":`record` ( firstName { ;param: epigraph.Integer }, id )"
    );

    // annotations merging
    testTailsNormalization(
        ":`record`(id,firstName{@epigraph.annotations.Doc \"doc1\"})~~ws.epigraph.tests.User :`record`(firstName{@epigraph.annotations.Doc \"doc2\"})",
        User.type,
        ":`record` ( firstName { @epigraph.annotations.Doc \"doc2\" }, id )"
    );

    testTailsNormalization(
        ":`record`(id)~~ws.epigraph.tests.User :`record`(firstName) ~~ws.epigraph.tests.SubUser :`record`(lastName)",
        User.type,
        ":`record` ( firstName, id ) ~~ws.epigraph.tests.SubUser :`record` ( lastName )"
    );

    testTailsNormalization(
        ":`record`(id)~~ws.epigraph.tests.User :`record`(firstName) ~~ws.epigraph.tests.SubUser :`record`(lastName)",
        SubUser.type,
        ":`record` ( lastName, firstName, id )"
    );

    testTailsNormalization(
        ":`record`(id)~~ws.epigraph.tests.User :`record`(firstName) ~~ws.epigraph.tests.SubUser :`record`(lastName)",
        User2.type,
        ":`record` ( id )"
    );

    testTailsNormalization(
        ":`record`(id)~~(ws.epigraph.tests.User :`record`(firstName) ~~ws.epigraph.tests.SubUser :`record`(lastName), ws.epigraph.tests.User2 :`record`(bestFriend:id))",
        User.type,
        ":`record` ( firstName, id ) ~~ws.epigraph.tests.SubUser :`record` ( lastName )"
    );

    testTailsNormalization(
        ":`record`(id)~~(ws.epigraph.tests.User :`record`(firstName) ~~ws.epigraph.tests.SubUser :`record`(lastName), ws.epigraph.tests.User2 :`record`(bestFriend:id))",
        SubUser.type,
        ":`record` ( lastName, firstName, id )"
    );

    testTailsNormalization(
        ":`record`(id)~~(ws.epigraph.tests.User :`record`(firstName) ~~ws.epigraph.tests.SubUser :`record`(lastName), ws.epigraph.tests.User2 :`record`(bestFriend:id))",
        User2.type,
        ":`record` ( bestFriend :id, id )"
    );

    testTailsNormalization(
        ":`record`( worstEnemy(id)~ws.epigraph.tests.UserRecord(firstName))",
        User.type,
        ":`record` ( worstEnemy ( firstName, id ) )"
    );

    testTailsNormalization(
        ":`record`( worstEnemy(id)~(ws.epigraph.tests.UserRecord(firstName),ws.epigraph.tests.UserRecord2(lastName)))",
        User.type,
        ":`record` ( worstEnemy ( firstName, id ) )"
    );
  }

  @Test
  public void testRecursiveTailNormalization() throws PsiProcessingException {
    testTailsNormalization(
        "$self = :( id, `record` ( bestFriend $self ) ) ~~ws.epigraph.tests.User :`record` ( id )",
        Person.type,
        "$self = :( id, `record` ( bestFriend $self ) ) ~~ws.epigraph.tests.User :`record` ( id )"
    );

    // bestFriend is Person, can't apply User projection to it

//    testTailsNormalization(
//        "$self = :( id, `record` ( bestFriend $self ) ) ~~ws.epigraph.tests.User :`record` ( id )",
//        User.type,
//        "$self = :( `record` ( id, bestFriend $self ), id )"
//    );
  }

  @Test
  public void testListTailsNormalization() throws PsiProcessingException {
    testTailsNormalization(
        ":`record`(friends*(:id))~~ws.epigraph.tests.User :`record`(friends*(:`record`(id)))",
        User.type,
        ":`record` ( friends *( :( `record` ( id ), id ) ) )"
    );
  }

  @Test
  public void testMapTailsNormalization() throws PsiProcessingException {
    testTailsNormalization(
        ":`record`(friendsMap[](:id))~~ws.epigraph.tests.User :`record`(friendsMap[required, ;param:epigraph.String](:`record`(id)))",
        User.type,
        ":`record` ( friendsMap [ required, ;param: epigraph.String ]( :( `record` ( id ), id ) ) )"
    );
  }

  @Test
  public void testModelTailsNormalization() throws PsiProcessingException {
    testModelTailsNormalization(
        ":`record`(id, lastName)~ws.epigraph.tests.UserRecord(firstName)~ws.epigraph.tests.UserRecord2(worstEnemy)",
        UserRecord.type,
        "( firstName, id, lastName )"
    );

    testModelTailsNormalization(
        ":`record`( worstEnemy(id)~ws.epigraph.tests.UserRecord(firstName))",
        UserRecord.type,
        "( worstEnemy ( firstName, id ) )"
    );
  }

  @Test
  public void testDiamond() throws PsiProcessingException {
    testModelTailsNormalization(
        ":`record`(id)~(ws.epigraph.tests.UserRecord(firstName),ws.epigraph.tests.UserRecord2(lastName))",
        UserRecord3.type,
        "( firstName, id )"
    );
  }

  @Test
  public void testParseMeta() throws PsiProcessingException {
    String projection = "{ meta: ( start, count ) } [ required ]( :`record` ( id, firstName ) )";

    testParsingVarProjection(
        new DataType(PersonMap.type, null),
        projection,
        projection
    );
  }

  @Test
  public void testSameTypeTails() throws PsiProcessingException {
    //noinspection ErrorNotRethrown
    try {
      testTailsNormalization(
          ":`record`(id)~~(ws.epigraph.tests.User:`record`(firstName),ws.epigraph.tests.User:`record`(lastName))",
          User.type,
          ""
      );
    } catch (AssertionError e) {
      assertTrue(e.getMessage().contains("Polymorphic tail for type 'ws.epigraph.tests.User' is already defined at"));
    }
  }

  @Test
  public void testSameTypeModelTails() throws PsiProcessingException {
    //noinspection ErrorNotRethrown
    try {
      testModelTailsNormalization(
          ":`record`(id)~(ws.epigraph.tests.UserRecord (profile),ws.epigraph.tests.UserRecord (lastName))",
          UserRecord.type,
          ""
      );
    } catch (AssertionError error) {
      assertNotNull(error.getMessage());
      assertTrue(
          error.getMessage(),
          error.getMessage().contains("Polymorphic tail for type 'ws.epigraph.tests.UserRecord' is already defined at")
      );
    }
  }

  // todo fixme
//  @Test
//  public void testSameTypeModelTailRefs() throws PsiProcessingException {
//    //noinspection ErrorNotRethrown
//    try {
//      testModelTailsNormalization(
//          ":`record`(id)~(ws.epigraph.tests.UserRecord $p=(firstName),ws.epigraph.tests.UserRecord $p)",
//          UserRecord.type,
//          ""
//      );
//    } catch (AssertionError error) {
//      error.printStackTrace();
//      assertNotNull(error.getMessage());
//      assertTrue(
//          error.getMessage(),
//          error.getMessage().contains("Polymorphic tail for type 'ws.epigraph.tests.UserRecord' is already defined at")
//      );
//    }
//  }

  @Test
  public void testWrongTailType() {
    //noinspection ErrorNotRethrown
    try {
      testModelTailsNormalization(
          ":`record` $rp=(firstName) ~ws.epigraph.tests.UserRecord $rp",
          UserRecord.type,
          ""
      );
    } catch (AssertionError error) {
      assertTrue(error.getMessage()
          .contains(
              "Tail projection type 'ws.epigraph.tests.PersonRecord' is not a subtype of 'ws.epigraph.tests.UserRecord'"));
    }
  }

  @Test
  public void testMergeRefs() {
    testModelTailsNormalization(
        ":`record` ( worstEnemy $we1 = ( firstName ) ) ~ws.epigraph.tests.UserRecord ( worstEnemy $we2 = ( lastName ) )",
        UserRecord.type,
        "( worstEnemy ( lastName, firstName ) )"
    );
  }

  @Test
  public void testMergeRecursiveRefs() {
    testModelTailsNormalization(
        // todo this cases StackOverflow. Possible fix: combine we1 and we2 references into "we1+we2" reference, see ProjectionUtils.buildReferenceName
//        ":`record` ( worstEnemy $we1 = ( firstName, worstEnemy $we1 ) ) ~ws.epigraph.tests.UserRecord ( worstEnemy $we2 = ( lastName, worstEnemy $we2 ) )",

        ":`record` ( worstEnemy $we1 = ( firstName, worstEnemy $we1 ) ) ~ws.epigraph.tests.UserRecord ( worstEnemy $we2 = ( lastName ) )",
        UserRecord.type,
        "( worstEnemy ( lastName, firstName, worstEnemy $we1 = ( firstName, worstEnemy $we1 ) ) )"
    );
  }

  @Test
  public void testNormalizeRecursiveList() {
    testModelTailsNormalization(
        ":`record` $p = ( friendRecords * $p ) ~ws.epigraph.tests.UserRecord ( firstName ) ",
        UserRecord.type,
        "$UserRecord = ( firstName, friendRecords *( $UserRecord ) )"
    );
  }

  @Test
  public void testNormalizeRecursiveList2() {
    try {
      // todo any way to handle it better?
      // see also a comment in AbstractVarProjection::mergeTags
      testModelTailsNormalization(
          ":`record` $p = ( friendRecords * $p ) ~ws.epigraph.tests.UserRecord ( friendRecords * ( firstName) ) ",

          // dereferencing won't help here though, unfolding once gives this which still can't be merged:
          // ":`record` $p = ( friendRecords * ( friendRecords * $p ) ~ws.epigraph.tests.UserRecord ( friendRecords * ( firstName) ) ) ~ws.epigraph.tests.UserRecord ( friendRecords * ( firstName) ) ",

          UserRecord.type,
          ""
      );
    } catch (IllegalArgumentException e) {
      assertTrue(
          e.getMessage(),
          e.getMessage().contains("Can't merge recursive projection [p._nt.UserRecord] with other projection")
      );
    }
  }

  @SuppressWarnings("ConstantConditions")
  @Test
  public void testTailTagType() {
    OpOutputVarProjection vp = testParsingVarProjection(
        ":id ~~ws.epigraph.tests.User :id"
    );

    assertEquals(PersonId.type, vp.tagProjection("id").projection().type());

    OpOutputVarProjection t = vp.tailByType(User.type);
    assertEquals(UserId.type, t.tagProjection("id").projection().type());

    t = vp.normalizedForType(User.type);
    assertEquals(UserId.type, t.tagProjection("id").projection().type());
  }

  @SuppressWarnings("ConstantConditions")
  @Test
  public void testTailTagType2() {
    OpOutputVarProjection vp = testParsingVarProjection(
        ":`record` ( bestFriend :id ~~ws.epigraph.tests.User :id )"
    );

    //noinspection OverlyStrongTypeCast
    vp = ((OpOutputRecordModelProjection) vp.singleTagProjection().projection()).fieldProjection("bestFriend")
        .fieldProjection()
        .varProjection();

    assertEquals(PersonId.type, vp.tagProjection("id").projection().type());

    OpOutputVarProjection t = vp.tailByType(User.type);
    assertEquals(UserId.type, t.tagProjection("id").projection().type());

    t = vp.normalizedForType(User.type);
    assertEquals(UserId.type, t.tagProjection("id").projection().type());
  }

  @SuppressWarnings("ConstantConditions")
  @Test
  public void testTailFieldType() {
    OpOutputVarProjection vp = testParsingVarProjection(
        ":`record` ( bestFriend :`record` ( id ) ~ws.epigraph.tests.UserRecord ( id ) )"
    );

    OpOutputRecordModelProjection rmp = (OpOutputRecordModelProjection) vp.singleTagProjection().projection();
    rmp = (OpOutputRecordModelProjection) rmp.fieldProjection("bestFriend")
        .fieldProjection()
        .varProjection()
        .singleTagProjection()
        .projection();

    OpOutputModelProjection<?, ?, ?> idProjection =
        rmp.fieldProjection("id").fieldProjection().varProjection().singleTagProjection().projection();

    assertEquals(PersonId.type, idProjection.type());

    OpOutputRecordModelProjection t = rmp.tailByType(UserRecord.type);
    assertNotNull(t);
    idProjection = t.fieldProjection("id").fieldProjection().varProjection().singleTagProjection().projection();
//    assertEquals(UserId.type, idProjection.type());
    assertEquals(PersonId.type, idProjection.type()); // it's not overridden

    t = rmp.normalizedForType(UserRecord.type);
    assertNotNull(t);
    idProjection = t.fieldProjection("id").fieldProjection().varProjection().singleTagProjection().projection();
//    assertEquals(UserId.type, idProjection.type());
    assertEquals(PersonId.type, idProjection.type()); // it's not overridden
  }

//  @Test
//  public void testEntityNormalizedClause() throws PsiProcessingException {
//    OpOutputVarProjection personProjection = testParsingVarProjection(":id ~~ws.epigraph.tests.User :`record` ( id )");
//
//    PsiProcessingContext ppc = new DefaultPsiProcessingContext();
//
//    final OpOutputReferenceContext referenceContext =
//        new OpOutputReferenceContext(ProjectionReferenceName.EMPTY, null, ppc);
//    referenceContext.varReference(Person.type, "ref", false, TextLocation.UNKNOWN);
//
//    referenceContext.resolveEntityRef("ref", personProjection, TextLocation.UNKNOWN);
//    failIfHasErrors(ppc.messages());
//
//    TestConfig testConfig = new TestConfig() {
//      @Override
//      @NotNull DataType dataType() {
//        return new DataType(User.type, null);
//      }
//
//      @Override
//      @NotNull OpOutputReferenceContext outputReferenceContext(PsiProcessingContext ctx) {
//        return referenceContext;
//      }
//    };
//
//    testParsingVarProjection(
//        testConfig,
//        "$ref | ws.epigraph.tests.User",
//        ":( `record` ( id ), id )"
//    );
//  }

//  @Test
//  public void testModelNormalizedClause() throws PsiProcessingException {
//    OpOutputVarProjection personProjection = testParsingVarProjection(":`record` ( id ) ~ws.epigraph.tests.UserRecord ( firstName )");
//    @SuppressWarnings("ConstantConditions")
//    OpOutputRecordModelProjection personRecordProjection = (OpOutputRecordModelProjection) personProjection.singleTagProjection().projection();
//
//    PsiProcessingContext ppc = new DefaultPsiProcessingContext();
//
//    final OpOutputReferenceContext referenceContext =
//        new OpOutputReferenceContext(ProjectionReferenceName.EMPTY, null, ppc);
//    referenceContext.modelReference(PersonRecord.type, "ref", false, TextLocation.UNKNOWN);
//
//    referenceContext.resolveModelRef("ref", personRecordProjection, TextLocation.UNKNOWN);
//    failIfHasErrors(ppc.messages());
//
//    TestConfig testConfig = new TestConfig() {
//      @Override
//      @NotNull DataType dataType() {
//        return new DataType(User.type, null);
//      }
//
//      @Override
//      @NotNull OpOutputReferenceContext outputReferenceContext(PsiProcessingContext ctx) {
//        return referenceContext;
//      }
//    };
//
//    testParsingVarProjection(
//        testConfig,
//        "$ref | ws.epigraph.tests.UserRecord",
//        "$ref$ws_epigraph_tests_UserRecord = ( firstName, id )"
//    );
//  }

  private void testTailsNormalization(String str, Type type, String expected) {
    OpOutputVarProjection varProjection = parseOpOutputVarProjection(str);
    final @NotNull OpOutputVarProjection normalized = varProjection.normalizedForType(type);
    String actual = printOpOutputVarProjection(normalized);
    assertEquals(expected, actual);
  }

  private void testModelTailsNormalization(String str, DatumType type, String expected) {
    OpOutputVarProjection varProjection = parseOpOutputVarProjection(str);
    final OpOutputTagProjectionEntry tagProjectionEntry = varProjection.singleTagProjection();
    assertNotNull(tagProjectionEntry);
    final OpOutputModelProjection<?, ?, ?> modelProjection = tagProjectionEntry.projection();
    assertNotNull(modelProjection);

//    final OpOutputModelProjection<?, ?, ?> normalized = modelProjection.normalizedForType(type);
    final OpOutputVarProjection selfVar = new OpOutputVarProjection(
        modelProjection.type(),
        ProjectionUtils.singletonLinkedHashMap(
            modelProjection.type().self().name(),
            new OpOutputTagProjectionEntry(
                modelProjection.type().self(),
                modelProjection,
                modelProjection.location()
            )
        ),
        varProjection.parenthesized(),
        null,
        modelProjection.location()
    );

    OpOutputVarProjection normalizedVar = selfVar.normalizedForType(type);

    String actual = printOpOutputVarProjection(normalizedVar);
    assertEquals(expected, actual);
  }

  private OpOutputVarProjection testParsingVarProjection(String str) {
    return testParsingVarProjection(str, str);
  }

  private OpOutputVarProjection testParsingVarProjection(String str, String expected) {
    return testParsingVarProjection(DEFAULT_CONFIG, str, expected);
  }

  private OpOutputVarProjection testParsingVarProjection(
      DataType dt,
      String projectionString,
      String expected) {

    return testParsingVarProjection(
        new TestConfig() {
          @Override
          @NotNull DataType dataType() {
            return dt;
          }
        },
        projectionString,
        expected
    );
  }

  private OpOutputVarProjection testParsingVarProjection(
      TestConfig config,
      String projectionString,
      String expected) {

    OpOutputVarProjection varProjection = parseOpOutputVarProjection(config, projectionString);

    String actual = printOpOutputVarProjection(varProjection);

    assertEquals("\n" + actual, expected, actual);
//    assertEquals(expected.trim(), actual.trim());

    return varProjection;
  }

  private @NotNull OpOutputVarProjection parseOpOutputVarProjection(@NotNull String projectionString) {
    return parseOpOutputVarProjection(DEFAULT_CONFIG, projectionString);
  }

  private static @NotNull OpOutputVarProjection parseOpOutputVarProjection(
      @NotNull TestConfig config,
      @NotNull String projectionString) {

    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();

    SchemaOpOutputVarProjection psiVarProjection = EpigraphPsiUtil.parseText(
        projectionString,
        SchemaSubParserDefinitions.OP_OUTPUT_VAR_PROJECTION,
        errorsAccumulator
    );

    failIfHasErrors(psiVarProjection, errorsAccumulator);

    return runPsiParser(context -> {
      OpInputReferenceContext inputReferenceContext = config.inputReferenceContext(context);
      OpOutputReferenceContext outputReferenceContext = config.outputReferenceContext(context);

      OpInputPsiProcessingContext inputPsiProcessingContext =
          new OpInputPsiProcessingContext(context, inputReferenceContext);

      OpOutputPsiProcessingContext outputPsiProcessingContext =
          new OpOutputPsiProcessingContext(context, inputPsiProcessingContext, outputReferenceContext);

      OpOutputVarProjection vp = OpOutputProjectionsPsiParser.parseVarProjection(
          config.dataType(),
          psiVarProjection,
          config.resolver(),
          outputPsiProcessingContext
      );

      if (config.ensureReferencesResolved()) {
        outputReferenceContext.ensureAllReferencesResolved();
        inputReferenceContext.ensureAllReferencesResolved();
      }

      return vp;
    });
  }

  private class TestConfig {
    @NotNull DataType dataType() { return dataType; }

    @NotNull TypesResolver resolver() {
      return new SimpleTypesResolver(
          PersonId.type,
          Person.type,
          User.type,
          UserId.type,
          UserRecord.type,
          User2.type,
          UserId2.type,
          UserRecord2.type,
          UserRecord3.type,
          SubUser.type,
          SubUserId.type,
          SubUserRecord.type,
          String_Person_Map.type,
          PersonMap.type,
          PaginationInfo.type,
          epigraph.String.type,
          epigraph.Integer.type,
          epigraph.annotations.Deprecated.type,
          epigraph.annotations.Doc.type
      );
    }

    @NotNull OpInputReferenceContext inputReferenceContext(PsiProcessingContext ctx) {
      return new OpInputReferenceContext(ProjectionReferenceName.EMPTY, null, ctx);
    }

    @NotNull OpOutputReferenceContext outputReferenceContext(PsiProcessingContext ctx) {
      return new OpOutputReferenceContext(ProjectionReferenceName.EMPTY, null, ctx);
    }

    boolean ensureReferencesResolved() { return true; }
  }

}
