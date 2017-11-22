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
import ws.epigraph.lang.GenQn;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.ProjectionUtils;
import ws.epigraph.projections.ReferenceContext;
import ws.epigraph.projections.gen.ProjectionReferenceName;
import ws.epigraph.projections.op.*;
import ws.epigraph.psi.DefaultPsiProcessingContext;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingContext;
import ws.epigraph.refs.SimpleTypesResolver;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.schema.parser.SchemaSubParserDefinitions;
import ws.epigraph.schema.parser.psi.SchemaOpEntityProjection;
import ws.epigraph.tests.*;
import ws.epigraph.types.DataType;
import ws.epigraph.types.DatumType;
import ws.epigraph.types.Type;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

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
  public void testParsing() {
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
        ") :~(",
        "      ws.epigraph.tests.User :`record` (profile)",
        "        :~ws.epigraph.tests.SubUser :`record` (worstEnemy(id)),",
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
        "  :~(",
        "    ws.epigraph.tests.User :`record` ( profile ) :~ws.epigraph.tests.SubUser :`record` ( worstEnemy ( id ) ),",
        "    ws.epigraph.tests.User2 :`record` ( worstEnemy ( id ) )",
        "  )"
    );

    testParsingEntityProjection(
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
  public void testParseEmpty() {
    testParsingEntityProjection(
        dataType,
        ""
        ,
        ":id"
    );
  }

  @Test
  public void testParseParam() {
    testParsingEntityProjection(
        lines(
            ":id {",
            "  ;+param: map[epigraph.String,ws.epigraph.tests.Person]",
            "    { @epigraph.annotations.Deprecated, default: ( \"foo\": < id: 123 > ) } [ ]( :id )",
            "}"
        )
    );
  }

  @Test
  public void testParseMultipleTags() {
    testParsingEntityProjection(":( id, `record` )");
  }

  @Test
  public void testParseRecursive() {
    OpEntityProjection vp = testParsingEntityProjection("$self = :( id, `record` ( id, bestFriend $self ) )");
    final ProjectionReferenceName name = vp.referenceName();
    assertNotNull(name);
    assertEquals("self", name.toString());
  }

  @Test
  public void testParseStupidRecursive() {
    try {
      testParsingEntityProjection("$self = $self");
      fail("Expected to get an error");
    } catch (@SuppressWarnings("ErrorNotRethrown") AssertionError e) {
      assertTrue(e.getMessage().contains("is not defined"));
    }
  }

  @SuppressWarnings("ConstantConditions")
  @Test
  public void testParseModelRecursive() {
    final OpEntityProjection vp =
        testParsingEntityProjection(":`record` $rr = ( id, bestFriend :`record` $rr )");

    // check that it's actually correct

    OpRecordModelProjection rmp = (OpRecordModelProjection) vp.singleTagProjection().projection();
    assertEquals(2, rmp.fieldProjections().size());

    rmp = (OpRecordModelProjection) rmp.fieldProjection("bestFriend")
        .fieldProjection()
        .projection()
        .singleTagProjection()
        .projection();

    assertEquals(2, rmp.fieldProjections().size());
  }

  @SuppressWarnings("ConstantConditions")
  @Test
  public void testNormalizeRecursive() {
    final OpEntityProjection vp =
        testParsingEntityProjection(":`record` $rr = ( id, bestFriend :`record` $rr )")
            .normalizedForType(User.type);

    // check that it's actually correct

    OpRecordModelProjection rmp = (OpRecordModelProjection) vp.singleTagProjection().projection();
    assertEquals(2, rmp.fieldProjections().size());

    rmp = (OpRecordModelProjection) rmp.fieldProjection("bestFriend")
        .fieldProjection()
        .projection()
        .singleTagProjection()
        .projection();

    assertEquals(2, rmp.fieldProjections().size());
  }

  @SuppressWarnings("ConstantConditions")
  @Test
  public void testParseSelfVarRecursive() {
    final OpEntityProjection vp =
        testParsingEntityProjection(":`record` ( id, worstEnemy $rr = ( id, worstEnemy $rr ) )");

    // check that it's actually correct

    OpRecordModelProjection rmp = (OpRecordModelProjection) vp.singleTagProjection().projection();
    assertEquals(2, rmp.fieldProjections().size());

    rmp = (OpRecordModelProjection) rmp.fieldProjection("worstEnemy")
        .fieldProjection()
        .projection()
        .singleTagProjection()
        .projection();
    assertEquals(2, rmp.fieldProjections().size());

    rmp = (OpRecordModelProjection) rmp.fieldProjection("worstEnemy")
        .fieldProjection()
        .projection()
        .singleTagProjection()
        .projection();

    assertEquals(2, rmp.fieldProjections().size());
  }

  @SuppressWarnings("ConstantConditions")
  @Test
  public void testNormalizeSelfVarRecursive() {
    final OpEntityProjection vp =
        testParsingEntityProjection(":`record` ( id, worstEnemy $rr = ( id, worstEnemy $rr ) )")
            .normalizedForType(User.type);

    // check that it's actually correct

    OpRecordModelProjection rmp = (OpRecordModelProjection) vp.singleTagProjection().projection();
    assertEquals(2, rmp.fieldProjections().size());

    rmp = (OpRecordModelProjection) rmp.fieldProjection("worstEnemy")
        .fieldProjection()
        .projection()
        .singleTagProjection()
        .projection();
    assertEquals(2, rmp.fieldProjections().size());

    rmp = (OpRecordModelProjection) rmp.fieldProjection("worstEnemy")
        .fieldProjection()
        .projection()
        .singleTagProjection()
        .projection();

    assertEquals(2, rmp.fieldProjections().size());
  }

  @SuppressWarnings("ConstantConditions")
  @Test
  public void testNormalizeSelfVarModelRecursive() {
    final OpEntityProjection vp =
        testParsingEntityProjection(":`record` ( id, worstEnemy $rr = ( id, worstEnemy $rr ) )");


    // rmp = :record
    OpRecordModelProjection rmp = (OpRecordModelProjection) vp.singleTagProjection().projection();
    assertEquals(2, rmp.fieldProjections().size());

    // rmp = $rmp/worstEnemy:record
    rmp = (OpRecordModelProjection) rmp.fieldProjection("worstEnemy")
        .fieldProjection()
        .projection()
        .singleTagProjection()
        .projection();
    assertEquals(2, rmp.fieldProjections().size());

    rmp = rmp.normalizedForType(UserRecord.type);
    assertEquals(2, rmp.fieldProjections().size());

    // rmp = $rmp/worstEnemy:record
    rmp = (OpRecordModelProjection) rmp.fieldProjection("worstEnemy")
        .fieldProjection()
        .projection()
        .singleTagProjection()
        .projection();

    assertEquals(2, rmp.fieldProjections().size());
  }

  @Test
  public void testNormalizeDiamond() {
    final OpEntityProjection vp = testParsingEntityProjection(
        lines(
            ":`record` ( id )",
            "  ~(",
            "    ws.epigraph.tests.UserRecord ( firstName ) ~ws.epigraph.tests.UserRecord3 ( diamond ),",
            "    ws.epigraph.tests.UserRecord2 ( lastName ) ~ws.epigraph.tests.UserRecord3 ( diamond )",
            "  )"
        )
    );
    //noinspection ConstantConditions
    final OpModelProjection<?, ?, ?, ?> mp = vp.singleTagProjection().projection();
    final OpModelProjection<?, ?, ?, ?> normalized = mp.normalizedForType(UserRecord3.type);

    String p = printOpModelProjection(normalized);
    assertEquals("( diamond, firstName, id )", p); // but not `lastName`!
  }

  @Test
  public void testSuperTypeRef() {
    // todo add to other parser tests too
    OpEntityProjection personProjection = testParsingEntityProjection(":id");

    PsiProcessingContext ppc = new DefaultPsiProcessingContext();

    final OpReferenceContext referenceContext =
        new OpReferenceContext(ProjectionReferenceName.EMPTY, null, ppc);
    referenceContext.entityReference(Person.type, "ref", false, TextLocation.UNKNOWN);

    referenceContext.resolveEntityRef("ref", personProjection, TextLocation.UNKNOWN);
    failIfHasErrors(true, ppc.messages());

    TestConfig testConfig = new TestConfig() {
      @Override
      @NotNull DataType dataType() {
        return new DataType(User.type, null);
      }

      @Override
      @NotNull OpReferenceContext outputReferenceContext(PsiProcessingContext ctx) {
        return referenceContext;
      }
    };

    testParsingEntityProjection(testConfig, ":`record` ( bestFriend4 $ref )", ":`record` ( bestFriend4 $ref = :id )");
  }

  @Test
  public void testParseModelRef() {
    OpEntityProjection personProjection = testParsingEntityProjection(":`record` ( id )");

    @SuppressWarnings("ConstantConditions")
    OpModelProjection<?, ?, ?, ?> personRecordProjection = personProjection.singleTagProjection().projection();

    // like `personProjection`, but built for `PersonRecord`, not `Person` type
    OpEntityProjection personRecordVarProjection = new OpEntityProjection(
        PersonRecord.type,
        false,
        ProjectionUtils.singletonLinkedHashMap(
            DatumType.MONO_TAG_NAME,
            new OpTagProjectionEntry(
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

    final OpReferenceContext referenceContext =
        new OpReferenceContext(ProjectionReferenceName.EMPTY, null, ppc);

    referenceContext.entityReference(PersonRecord.type, "ref", false, TextLocation.UNKNOWN);
//    referenceContext.resolve("ref", personRecordVarProjection, TextLocation.UNKNOWN, ppc);
    failIfHasErrors(true, ppc.messages());

    TestConfig testConfig = new TestConfig() {
      @Override
      @NotNull DataType dataType() {
        return new DataType(Person.type, null);
      }

      @Override
      @NotNull OpReferenceContext outputReferenceContext(PsiProcessingContext ctx) {
        return referenceContext;
      }
    };

    testParsingEntityProjection(testConfig, ":`record` $ref", ":`record` <unresolved>");
//    testParsingVarProjection(testConfig, ":`record` $ref", ":`record` ( id )");
    // we don't get reference name here because it belongs to var, not model projection ( personRecordVar )
//    testParsingVarProjection(testConfig, ":`record` $ref", ":`record` $ref = ( id )");

    // should not result in class cast
    referenceContext.resolveEntityRef("ref", personRecordVarProjection, TextLocation.UNKNOWN);
  }

  @Test
  public void testParseWrongTypeRef() {
    // todo add to other parser tests too
    OpEntityProjection paginationProjection =
        testParsingEntityProjection(new DataType(PaginationInfo.type, null), "()", "");

    PsiProcessingContext ppc = new DefaultPsiProcessingContext();

    final OpReferenceContext referenceContext =
        new OpReferenceContext(ProjectionReferenceName.EMPTY, null, ppc);
    referenceContext.entityReference(PaginationInfo.type, "ref", false, TextLocation.UNKNOWN);

    referenceContext.resolveEntityRef("ref", paginationProjection, TextLocation.UNKNOWN);
    failIfHasErrors(true, ppc.messages());

    TestConfig testConfig = new TestConfig() {
      @Override
      @NotNull OpReferenceContext outputReferenceContext(PsiProcessingContext ctx) {
        return referenceContext;
      }
    };

    try {
      testParsingEntityProjection(testConfig, ":`record` ( bestFriend $ref )", ":`record` ( bestFriend )");
      fail("Var reference built for an incompatible type should not be accepted");
    } catch (@SuppressWarnings("ErrorNotRethrown") AssertionError e) {
      assertTrue(e.getMessage().contains(
          "Projection 'ref' type 'ws.epigraph.tests.PaginationInfo' is not compatible with type 'ws.epigraph.tests.Person'"
          )
      );
    }
  }

  @Test
  public void testParseTail() {
    testParsingEntityProjection(
        ":~ws.epigraph.tests.User :id"
        ,
        ":id :~ws.epigraph.tests.User :id"
    );
  }

  @Test
  public void testParseDoubleTail() {
    final OpEntityProjection projection = testParsingEntityProjection(
        dataType,
        ":~ws.epigraph.tests.User :id :~ws.epigraph.tests.SubUser :id"
        ,
        ":id :~ws.epigraph.tests.User :id :~ws.epigraph.tests.SubUser :id"
    );

    List<OpEntityProjection> tails = projection.polymorphicTails();
    assertNotNull(tails);
    assertEquals(1, tails.size());
    OpEntityProjection tail = tails.get(0);

    tails = tail.polymorphicTails();
    assertNotNull(tails);
    assertEquals(1, tails.size());
    tail = tails.get(0);

    tails = tail.polymorphicTails();
    assertNull(tails);
  }

  @Test
  public void testParseTails() {
    testParsingEntityProjection(
        ":~( ws.epigraph.tests.User :id, ws.epigraph.tests.User2 :id )"
        ,
        ":id :~( ws.epigraph.tests.User :id, ws.epigraph.tests.User2 :id )"
    );
  }

  @Test
  public void testParseModelTail() {
    testParsingEntityProjection(
        ":`record` ( worstEnemy ( id ) ~ws.epigraph.tests.UserRecord ( profile ) )"
    );
  }

  @Test
  public void testParseCustomParams() {
    testParsingEntityProjection(":id { @epigraph.annotations.Deprecated }");
  }

  @Test
  public void testParseRecordDefaultFields() {
    testParsingEntityProjection(":`record` ( id, firstName )");
  }

  @Test
  public void testParseRecordFieldsWithStructure() {
    testParsingEntityProjection(":`record` ( id, bestFriend :`record` ( id ) )");
  }

  @Test
  public void testParseRecordFieldsWithCustomParams() {
    testParsingEntityProjection(":`record` ( id, bestFriend :`record` { @epigraph.annotations.Deprecated } ( id ) )");
  }

  @Test
  public void testParseList() {
    testParsingEntityProjection(":`record` ( friends *( :id ) )");
  }

  @Test
  public void testParseList2() {
    testParsingEntityProjection(":`record` ( friends * :id )", ":`record` ( friends *( :id ) )");
  }

  @Test
  public void testParseList3() {
    testParsingEntityProjection(
        ":`record` ( friends * :`record` ( id ) )",
        ":`record` ( friends *( :`record` ( id ) ) )"
    );
  }

  @Test
  public void testParseMap() {
    testParsingEntityProjection(
        ":`record` ( friendsMap [ forbidden, ;+param: epigraph.String, @epigraph.annotations.Doc \"no keys\" ]( :id ) )");
  }

  @Test
  public void testParseMapWithKeyProjection() {
    testParsingEntityProjection(
        lines(
            ":`record` (",
            "  personRecToPersonRec [",
            "    ;param: epigraph.String { default: \"foo\" },",
            "    @epigraph.annotations.Doc \"bla\",",
            "    projection: ( firstName, lastName )",
            "  ]( ( firstName ) )",
            ")"
        )
    );
  }

  @Test
  public void testFlag() {
    testParsingEntityProjection(":+id");
    testParsingEntityProjection(":+`record` ( +id )");
    testParsingEntityProjection(":`record` ( id+ )", ":`record` ( +id )");
    testParsingEntityProjection(":`record` ( bestFriend2+ )", ":`record` ( bestFriend2 :+id )");
    testParsingEntityProjection(":`record` ( +bestFriend2 )", ":`record` ( +bestFriend2 :+id )");

    // todo: enable smarter output in pretty printer
    testParsingEntityProjection(":`record` ( friends*+:id )", ":`record` ( friends *+( :id ) )");
    testParsingEntityProjection(
        ":`record` ( friendsMap[forbidden]+:id)",
        ":`record` ( friendsMap [ forbidden ]+( :id ) )"
    );

    testParsingEntityProjection(":`record` ( friendsMap2 { meta: +( start ) } [ required ]( :id ) )");
  }

  @Test
  public void testParseDefault() {
    TestConfig cfg = new TestConfig() {
      @Override
      boolean failOnWarnings() { return false; }
    };

    testParsingEntityProjection(cfg, ":id { default: 123 }");
  }

  @Test
  public void testTailsNormalization() {
    testTailsNormalization(
        ":id :~ws.epigraph.tests.User:`record`(id)",
        Person.type,
        ":id :~ws.epigraph.tests.User :`record` ( id )"
    );

    testTailsNormalization(
        ":id :~ws.epigraph.tests.User:`record`(id)",
        SubUser.type,
        ":( `record` ( id ), id )"
    );

    testTailsNormalization(
        ":`record`(id, bestFriend:id:~ws.epigraph.tests.User:`record`(id))",
        Person.type,
        ":`record` ( id, bestFriend :id :~ws.epigraph.tests.User :`record` ( id ) )"
        // bestFriend field can still contain a User
    );

    testTailsNormalization(
        ":`record`(id):~ws.epigraph.tests.User :`record`(profile)",
        User.type,
        ":`record` ( profile, id )"
    );

    // parameters merging
    testTailsNormalization(
        ":`record`(id,firstName{;param:epigraph.String}):~ws.epigraph.tests.User :`record`(firstName{;param:epigraph.Integer})",
        User.type,
        ":`record` ( firstName { ;param: epigraph.Integer }, id )"
    );

    // annotations merging
    testTailsNormalization(
        ":`record`(id,firstName{@epigraph.annotations.Doc \"doc1\"}):~ws.epigraph.tests.User :`record`(firstName{@epigraph.annotations.Doc \"doc2\"})",
        User.type,
        ":`record` ( firstName { @epigraph.annotations.Doc \"doc2\" }, id )"
    );

    testTailsNormalization(
        ":`record`(id):~ws.epigraph.tests.User :`record`(firstName) :~ws.epigraph.tests.SubUser :`record`(lastName)",
        User.type,
        ":`record` ( firstName, id ) :~ws.epigraph.tests.SubUser :`record` ( lastName )"
    );

    testTailsNormalization(
        ":`record`(id):~ws.epigraph.tests.User :`record`(firstName) :~ws.epigraph.tests.SubUser :`record`(lastName)",
        SubUser.type,
        ":`record` ( lastName, firstName, id )"
    );

    testTailsNormalization(
        ":`record`(id):~ws.epigraph.tests.User :`record`(firstName) :~ws.epigraph.tests.SubUser :`record`(lastName)",
        User2.type,
        ":`record` ( id )"
    );

    testTailsNormalization(
        ":`record`(id):~(ws.epigraph.tests.User :`record`(firstName) :~ws.epigraph.tests.SubUser :`record`(lastName), ws.epigraph.tests.User2 :`record`(bestFriend:id))",
        User.type,
        ":`record` ( firstName, id ) :~ws.epigraph.tests.SubUser :`record` ( lastName )"
    );

    testTailsNormalization(
        ":`record`(id):~(ws.epigraph.tests.User :`record`(firstName) :~ws.epigraph.tests.SubUser :`record`(lastName), ws.epigraph.tests.User2 :`record`(bestFriend:id))",
        SubUser.type,
        ":`record` ( lastName, firstName, id )"
    );

    testTailsNormalization(
        ":`record`(id):~(ws.epigraph.tests.User :`record`(firstName) :~ws.epigraph.tests.SubUser :`record`(lastName), ws.epigraph.tests.User2 :`record`(bestFriend:id))",
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
  public void testRecursiveTailNormalization() {
    testTailsNormalization(
        "$self = :( id, `record` ( bestFriend $self ) ) :~ws.epigraph.tests.User :`record` ( id )",
        Person.type,
        "$self = :( id, `record` ( bestFriend $self ) ) :~ws.epigraph.tests.User :`record` ( id )"
    );

    // bestFriend is Person, can't apply User projection to it

//    testTailsNormalization(
//        "$self = :( id, `record` ( bestFriend $self ) ) :~ws.epigraph.tests.User :`record` ( id )",
//        User.type,
//        "$self = :( `record` ( id, bestFriend $self ), id )"
//    );
  }

  @Test
  public void testListTailsNormalization() {
    testTailsNormalization(
        ":`record`(friends*(:id)):~ws.epigraph.tests.User :`record`(friends*(:`record`(id)))",
        User.type,
        ":`record` ( friends *( :( `record` ( id ), id ) ) )"
    );
  }

  @Test
  public void testMapTailsNormalization() {
    testTailsNormalization(
        ":`record`(friendsMap[](:id)):~ws.epigraph.tests.User :`record`(friendsMap[required, ;param:epigraph.String](:`record`(id)))",
        User.type,
        ":`record` ( friendsMap [ required, ;param: epigraph.String ]( :( `record` ( id ), id ) ) )"
    );
  }

  @Test
  public void testModelTailsNormalization() {
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
  public void testNamedEntityTail() {
    PsiProcessingContext ppc = new DefaultPsiProcessingContext();

    final OpReferenceContext referenceContext =
        new OpReferenceContext(ProjectionReferenceName.EMPTY, null, ppc);

    failIfHasErrors(true, ppc.messages());

    TestConfig testConfig = new TestConfig() {
      @Override
      @NotNull DataType dataType() { return new DataType(Person.type, null); }

      @Override
      @NotNull OpReferenceContext outputReferenceContext(PsiProcessingContext ctx) {
        return referenceContext;
      }
    };

    OpEntityProjection vp = testParsingEntityProjection(
        testConfig,
        ":id :~ws.epigraph.tests.User $user = :`record` ( id, bestFriend4 $user )",
        // should we preserve original label for some reason?
        ":id :~ws.epigraph.tests.User :`record` ( id, bestFriend4 <UNRESOLVED> )"
        // $user is unresolved in parser's scope
    );

    referenceContext.ensureAllReferencesResolved();
    failIfHasErrors(true, ppc.messages());

    ReferenceContext.RefItem<OpEntityProjection> userProjection = referenceContext.lookupEntityReference("user", false);
    assertNotNull(userProjection);
    assertEquals(User.type, userProjection.apply().type());

    OpEntityProjection normalized = vp.normalizedForType(User.type);
    assertEquals(userProjection.apply(), normalized);
    assertEquals(vp, normalized.normalizedFrom());
    ProjectionReferenceName referenceName = normalized.referenceName();
    assertNotNull(referenceName);
    assertEquals("user", referenceName.toString());
  }

  @Test
  public void testNamedModelTail() {
    PsiProcessingContext ppc = new DefaultPsiProcessingContext();

    final OpReferenceContext referenceContext =
        new OpReferenceContext(ProjectionReferenceName.EMPTY, null, ppc);

    failIfHasErrors(true, ppc.messages());

    TestConfig testConfig = new TestConfig() {
      @Override
      @NotNull DataType dataType() { return new DataType(Person.type, null); }

      @Override
      @NotNull OpReferenceContext outputReferenceContext(PsiProcessingContext ctx) {
        return referenceContext;
      }
    };

    OpEntityProjection vp = testParsingEntityProjection(
        testConfig,
        ":`record` ( id ) ~ws.epigraph.tests.UserRecord $user = ( firstName )",
        // should we preserve original label for some reason?
        ":`record` ( id ) ~ws.epigraph.tests.UserRecord ( firstName )"
    );

    referenceContext.ensureAllReferencesResolved();
    failIfHasErrors(true, ppc.messages());

    @SuppressWarnings("ConstantConditions")
    OpRecordModelProjection mp = (OpRecordModelProjection) vp.singleTagProjection().projection();

    ReferenceContext.RefItem<OpModelProjection<?, ?, ?, ?>> userProjection =
        referenceContext.lookupModelReference("user", false);

    assertNotNull(userProjection);
    assertEquals(UserRecord.type, userProjection.apply().type());

    OpRecordModelProjection normalized = mp.normalizedForType(UserRecord.type);
    assertEquals(userProjection.apply(), normalized);
    assertEquals(mp, normalized.normalizedFrom());
    ProjectionReferenceName referenceName = normalized.referenceName();
    assertNotNull(referenceName);
    assertEquals("user", referenceName.toString());
  }

  @Test
  public void testDiamond() {
    testModelTailsNormalization(
        ":`record`(id)~(ws.epigraph.tests.UserRecord(firstName),ws.epigraph.tests.UserRecord2(lastName))",
        UserRecord3.type,
        "( firstName, id )"
    );
  }

  @Test
  public void testParseMeta() {
    String projection = "{ meta: ( start, count ) } [ required ]( :`record` ( id, firstName ) )";

    testParsingEntityProjection(
        new DataType(PersonMap.type, null),
        projection,
        projection
    );
  }

  @Test
  public void testSameTypeTails() {
    //noinspection ErrorNotRethrown
    try {
      testTailsNormalization(
          ":`record`(id):~(ws.epigraph.tests.User:`record`(firstName),ws.epigraph.tests.User:`record`(lastName))",
          User.type,
          ""
      );
    } catch (AssertionError e) {
      assertTrue(e.getMessage().contains("Polymorphic tail for type 'ws.epigraph.tests.User' is already defined at"));
    }
  }

  @Test
  public void testSameTypeModelTails() {
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
      assertTrue(
          error.getMessage(),
          error.getMessage().contains(
              "Tail projection type 'ws.epigraph.tests.PersonRecord' is not a subtype of tail type 'ws.epigraph.tests.UserRecord'")
      );
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
          e.getMessage().contains("Can't merge recursive projection 'p' with other projection")
      );
    }
  }

  @Test
  public void testDoubleNormalizedTailRef() {
    AtomicReference<OpReferenceContext> refRef = new AtomicReference<>();

    TestConfig cfg = new TestConfig() {
      @Override
      @NotNull OpReferenceContext outputReferenceContext(final PsiProcessingContext ctx) {
        OpReferenceContext rctx = super.outputReferenceContext(ctx);
        refRef.set(rctx);
        return rctx;
      }
    };

    OpEntityProjection entityProjection = testParsingEntityProjection(
        cfg,
        ":id :~ws.epigraph.tests.User :`record` ( firstName ) :~ws.epigraph.tests.SubUser $sub = :`record` ( lastName )",
        ":id :~ws.epigraph.tests.User :`record` ( firstName ) :~ws.epigraph.tests.SubUser :`record` ( lastName )"
    );

    OpReferenceContext referenceContext = refRef.get();
    assertNotNull(referenceContext);
    ReferenceContext.RefItem<OpEntityProjection> refItem = referenceContext.lookupEntityReference("sub", false);
    assertNotNull(refItem);
    OpEntityProjection sub = refItem.apply();
    assertNotNull(sub);
    OpEntityProjection normalizedFrom = sub.normalizedFrom();
    assertEquals(entityProjection, normalizedFrom);
    assertNotNull(sub.referenceName());
    assertEquals(Optional.of("sub"), Optional.ofNullable(sub.referenceName()).map(GenQn::toString));

    // now same, but with named User

    entityProjection = testParsingEntityProjection(
        cfg,
        ":id :~ws.epigraph.tests.User $user = :`record` ( firstName ) :~ws.epigraph.tests.SubUser $sub = :`record` ( lastName )",
        ":id :~ws.epigraph.tests.User :`record` ( firstName ) :~ws.epigraph.tests.SubUser :`record` ( lastName )"
    );

    referenceContext = refRef.get();
    assertNotNull(referenceContext);
    refItem = referenceContext.lookupEntityReference("sub", false);
    assertNotNull(refItem);
    sub = refItem.apply();
    assertNotNull(sub);
    normalizedFrom = sub.normalizedFrom();
    assertEquals(entityProjection, normalizedFrom);
    assertEquals(Optional.of("sub"), Optional.ofNullable(sub.referenceName()).map(GenQn::toString));

    OpEntityProjection norm = entityProjection.normalizedForType(sub.type());
    assertEquals(Optional.of("sub"), Optional.ofNullable(norm.referenceName()).map(GenQn::toString));
    assertEquals(sub, norm);

    refItem = referenceContext.lookupEntityReference("user", false);
    assertNotNull(refItem);
    OpEntityProjection user = refItem.apply();
    assertNotNull(user);
    normalizedFrom = user.normalizedFrom();
    assertEquals(entityProjection, normalizedFrom);
    assertEquals(Optional.of("user"), Optional.ofNullable(user.referenceName()).map(GenQn::toString));

    norm = entityProjection.normalizedForType(user.type());
    assertEquals(Optional.of("user"), Optional.ofNullable(norm.referenceName()).map(GenQn::toString));
    assertEquals(user, norm);

    norm = user.normalizedForType(sub.type());
    assertEquals(Optional.of("sub"), Optional.ofNullable(norm.referenceName()).map(GenQn::toString));
    assertEquals(sub, norm);
  }

  @Test
  public void testModelDoubleNormalizedTailRef() {
    AtomicReference<OpReferenceContext> refRef = new AtomicReference<>();

    TestConfig cfg = new TestConfig() {
      @Override
      @NotNull OpReferenceContext outputReferenceContext(final PsiProcessingContext ctx) {
        OpReferenceContext rctx = super.outputReferenceContext(ctx);
        refRef.set(rctx);
        return rctx;
      }
    };

    OpEntityProjection entityProjection = testParsingEntityProjection(
        cfg,
        ":`record` ( id ) ~ws.epigraph.tests.UserRecord $user = ( firstName ) ~ws.epigraph.tests.SubUserRecord $sub = ( lastName )",
        ":`record` ( id ) ~ws.epigraph.tests.UserRecord ( firstName ) ~ws.epigraph.tests.SubUserRecord ( lastName )"
    );

    OpTagProjectionEntry tagProjection = entityProjection.singleTagProjection();
    assertNotNull(tagProjection);
    OpModelProjection<?, ?, ?, ?> modelProjection = tagProjection.projection();

    OpReferenceContext referenceContext = refRef.get();
    assertNotNull(referenceContext);
    ReferenceContext.RefItem<OpModelProjection<?, ?, ?, ?>> refItem =
        referenceContext.lookupModelReference("sub", false);
    assertNotNull(refItem);
    OpModelProjection<?, ?, ?, ?> sub = refItem.apply();
    assertNotNull(sub);
    OpModelProjection<?, ?, ?, ?> normalizedFrom = sub.normalizedFrom();
    assertEquals(modelProjection, normalizedFrom);
    assertEquals(Optional.of("sub"), Optional.ofNullable(sub.referenceName()).map(GenQn::toString));

    OpModelProjection<?, ?, ?, ?> norm = modelProjection.normalizedForType(sub.type());
    assertEquals(Optional.of("sub"), Optional.ofNullable(norm.referenceName()).map(GenQn::toString));
    assertEquals(sub, norm);

    refItem = referenceContext.lookupModelReference("user", false);
    assertNotNull(refItem);
    OpModelProjection<?, ?, ?, ?> user = refItem.apply();
    assertNotNull(user);
    normalizedFrom = user.normalizedFrom();
    assertEquals(modelProjection, normalizedFrom);
    assertEquals(Optional.of("user"), Optional.ofNullable(user.referenceName()).map(GenQn::toString));

    norm = modelProjection.normalizedForType(user.type());
    assertEquals(Optional.of("user"), Optional.ofNullable(norm.referenceName()).map(GenQn::toString));
    assertEquals(user, norm);

    norm = user.normalizedForType(sub.type());
    assertEquals(Optional.of("sub"), Optional.ofNullable(norm.referenceName()).map(GenQn::toString));
    assertEquals(sub, norm);
  }

  @SuppressWarnings("ConstantConditions")
  @Test
  public void testTailTagType() {
    OpEntityProjection vp = testParsingEntityProjection(
        ":id :~ws.epigraph.tests.User :id"
    );

    assertEquals(PersonId.type, vp.tagProjection("id").projection().type());

    OpEntityProjection t = vp.tailByType(User.type);
    assertEquals(UserId.type, t.tagProjection("id").projection().type());

    t = vp.normalizedForType(User.type);
    assertEquals(UserId.type, t.tagProjection("id").projection().type());
  }

  @SuppressWarnings("ConstantConditions")
  @Test
  public void testTailTagType2() {
    OpEntityProjection vp = testParsingEntityProjection(
        ":`record` ( bestFriend :id :~ws.epigraph.tests.User :id )"
    );

    //noinspection OverlyStrongTypeCast
    vp = ((OpRecordModelProjection) vp.singleTagProjection().projection()).fieldProjection("bestFriend")
        .fieldProjection()
        .projection();

    assertEquals(PersonId.type, vp.tagProjection("id").projection().type());

    OpEntityProjection t = vp.tailByType(User.type);
    assertEquals(UserId.type, t.tagProjection("id").projection().type());

    t = vp.normalizedForType(User.type);
    assertEquals(UserId.type, t.tagProjection("id").projection().type());
  }

  @SuppressWarnings("ConstantConditions")
  @Test
  public void testTailFieldType() {
    OpEntityProjection vp = testParsingEntityProjection(
        ":`record` ( bestFriend :`record` ( id ) ~ws.epigraph.tests.UserRecord ( id ) )"
    );

    OpRecordModelProjection rmp = (OpRecordModelProjection) vp.singleTagProjection().projection();
    rmp = (OpRecordModelProjection) rmp.fieldProjection("bestFriend")
        .fieldProjection()
        .projection()
        .singleTagProjection()
        .projection();

    OpModelProjection<?, ?, ?, ?> idProjection =
        rmp.fieldProjection("id").fieldProjection().projection().singleTagProjection().projection();

    assertEquals(PersonId.type, idProjection.type());

    OpRecordModelProjection t = rmp.tailByType(UserRecord.type);
    assertNotNull(t);
    idProjection = t.fieldProjection("id").fieldProjection().projection().singleTagProjection().projection();
//    assertEquals(UserId.type, idProjection.type());
    assertEquals(PersonId.type, idProjection.type()); // it's not overridden

    t = rmp.normalizedForType(UserRecord.type);
    assertNotNull(t);
    idProjection = t.fieldProjection("id").fieldProjection().projection().singleTagProjection().projection();
//    assertEquals(UserId.type, idProjection.type());
    assertEquals(PersonId.type, idProjection.type()); // it's not overridden
  }

//  @Test
//  public void testEntityNormalizedClause() throws PsiProcessingException {
//    OpOutputVarProjection personProjection = testParsingVarProjection(":id :~ws.epigraph.tests.User :`record` ( id )");
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
    OpEntityProjection varProjection = parseOpOutputVarProjection(str);
    final @NotNull OpEntityProjection normalized = varProjection.normalizedForType(type);
    String actual = printOpEntityProjection(normalized);
    assertEquals(expected, actual);
  }

  private void testModelTailsNormalization(String str, DatumType type, String expected) {
    OpEntityProjection varProjection = parseOpOutputVarProjection(str);
    final OpTagProjectionEntry tagProjectionEntry = varProjection.singleTagProjection();
    assertNotNull(tagProjectionEntry);
    final OpModelProjection<?, ?, ?, ?> modelProjection = tagProjectionEntry.projection();
    assertNotNull(modelProjection);

//    final OpOutputModelProjection<?, ?, ?> normalized = modelProjection.normalizedForType(type);
    final OpEntityProjection selfVar = new OpEntityProjection(
        modelProjection.type(),
        false,
        ProjectionUtils.singletonLinkedHashMap(
            modelProjection.type().self().name(),
            new OpTagProjectionEntry(
                modelProjection.type().self(),
                modelProjection,
                modelProjection.location()
            )
        ),
        varProjection.parenthesized(),
        null,
        modelProjection.location()
    );

    OpEntityProjection normalizedVar = selfVar.normalizedForType(type);

    String actual = printOpEntityProjection(normalizedVar);
    assertEquals(expected, actual);
  }

  private OpEntityProjection testParsingEntityProjection(String str) {
    return testParsingEntityProjection(str, str);
  }

  private OpEntityProjection testParsingEntityProjection(String str, String expected) {
    return testParsingEntityProjection(DEFAULT_CONFIG, str, expected);
  }

  private OpEntityProjection testParsingEntityProjection(
      DataType dt,
      String projectionString,
      String expected) {

    return testParsingEntityProjection(
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

  private OpEntityProjection testParsingEntityProjection(TestConfig config, String str) {
    return testParsingEntityProjection(config, str, str);
  }

  private OpEntityProjection testParsingEntityProjection(
      TestConfig config,
      String projectionString,
      String expected) {

    OpEntityProjection varProjection = parseOpOutputVarProjection(config, projectionString);

    String actual = printOpEntityProjection(varProjection);

    assertEquals("\n" + actual, expected, actual);
//    assertEquals(expected.trim(), actual.trim());

    return varProjection;
  }

  private @NotNull OpEntityProjection parseOpOutputVarProjection(@NotNull String projectionString) {
    return parseOpOutputVarProjection(DEFAULT_CONFIG, projectionString);
  }

  private static @NotNull OpEntityProjection parseOpOutputVarProjection(
      @NotNull TestConfig config,
      @NotNull String projectionString) {

    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();

    SchemaOpEntityProjection psiVarProjection = EpigraphPsiUtil.parseText(
        projectionString,
        SchemaSubParserDefinitions.OP_ENTITY_PROJECTION,
        errorsAccumulator
    );

    failIfHasErrors(psiVarProjection, errorsAccumulator);

    return runPsiParser(config.failOnWarnings(), context -> {
      OpReferenceContext outputReferenceContext = config.outputReferenceContext(context);

      OpPsiProcessingContext outputPsiProcessingContext =
          new OpPsiProcessingContext(context, outputReferenceContext);

      OpEntityProjection vp = new OpOutputProjectionsPsiParser(context).parseEntityProjection(
          config.dataType(),
          false,
          psiVarProjection,
          config.resolver(),
          outputPsiProcessingContext
      );

      if (config.ensureReferencesResolved()) {
        outputReferenceContext.ensureAllReferencesResolved();
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

    @NotNull OpReferenceContext outputReferenceContext(PsiProcessingContext ctx) {
      return new OpReferenceContext(ProjectionReferenceName.EMPTY, null, ctx);
    }

    boolean ensureReferencesResolved() { return true; }

    boolean failOnWarnings() { return true; }
  }

}
