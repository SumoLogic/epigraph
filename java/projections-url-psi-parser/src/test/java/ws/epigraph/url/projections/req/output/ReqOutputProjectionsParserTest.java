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

package ws.epigraph.url.projections.req.output;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import ws.epigraph.projections.StepsAndProjection;
import ws.epigraph.projections.op.OpProjection;
import ws.epigraph.projections.req.*;
import ws.epigraph.refs.SimpleTypesResolver;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.test.TestUtil;
import ws.epigraph.tests.*;
import ws.epigraph.types.DataType;
import ws.epigraph.types.DatumType;
import ws.epigraph.types.Type;

import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;
import static org.junit.Assert.*;
import static ws.epigraph.test.TestUtil.lines;
import static ws.epigraph.url.projections.req.ReqTestUtil.parseOpOutputProjection;
import static ws.epigraph.url.projections.req.ReqTestUtil.parseReqOutputProjection;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqOutputProjectionsParserTest {
  private final DataType dataType = new DataType(Person.type, Person.id);
  private final TypesResolver resolver = new SimpleTypesResolver(
      PersonId.type,
      Person.type,
      PersonRecord.type,
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
      epigraph.String.type,
      SingleTagEntity.type
  );

  private final OpProjection<?, ?> personOpProjection = parsePersonOpProjection(
      lines(
          ":(",
          "  +id,",
          "  `record` (",
          "    +id {",
          "      ;param1 : epigraph.String { default: \"p1\" },",
          "      ;param2 : ws.epigraph.tests.UserRecord",
          "    },",
          "    firstName{;param:epigraph.String},",
          "    +middleName{",
          "      ;+param1:epigraph.String,",
          "      ;+param2:epigraph.String { default: \"p2\" },",
          "      ;param3:ws.epigraph.tests.PersonRecord (+firstName, bestFriend:`record`(+lastName))",
          "    },",
          "    +bestFriend :( id, `record` (",
          "      id,",
          "      bestFriend :`record` (",
          "        id,",
          "        firstName",
          "      ),",
          "    ) ) :~ws.epigraph.tests.User : profile ,",
          "    +bestFriend2 $bf2 = :( id, `record` ( id, bestFriend2 $bf2 ) ),",
          "    +bestFriend3 :( id, `record` ( id, firstName, bestFriend3 :`record` ( id, lastName, bestFriend3 : `record` ( id, bestFriend3 $bf3 = :`record` ( id, bestFriend3 $bf3 ) ) ) ) ),",
          "    +worstEnemy ( firstName ) ~ws.epigraph.tests.UserRecord $wu",
          "    +worstUser $wu = ( id )",
          "    +friends *( :(id,`record`(id)) ),",
          "    +friendRecords * (id),",
          "    +friendsMap [;keyParam:epigraph.String]( :(id, `record` (id, firstName) ) )",
          "    +friendRecordMap [ forbidden ] (id, firstName)",
          "  ) ~ws.epigraph.tests.UserRecord +(profile)",
          ") :~(",
          "      ws.epigraph.tests.User +:`record` (profile)",
          "        :~ws.epigraph.tests.SubUser :`record` (worstEnemy(id)),",
          "      ws.epigraph.tests.User2 +:`record` (worstEnemy(id))",
          ")"
      )
  );

//  private OpOutputFieldProjection personFieldProjection =


  @Test
  public void testRequiredParam() {
    try {
      testParse(":record / middleName", 3);
      fail();
    } catch (@SuppressWarnings("ErrorNotRethrown") AssertionError e) {
      assertTrue(e.getMessage().contains("Required parameter 'param1' is missing"));
      assertFalse(e.getMessage().contains("Required parameter 'param2' is missing"));
    }

    testParse(
        ":record / middleName ;param1 = 'p1'",
        ":record / middleName ;param1 = 'p1';param2 = 'p2'",
        3
    );

    testParse(":record / middleName ;param1 = 'p1';param3 = {firstName: 'Alfred'};param2 = 'p2'", 3);

    try {
      testParse(":record / middleName ;param1 = 'p1';param3 = { lastName: 'Hitchcock' };param2 = 'p2'", 3);
      fail();
    } catch (@SuppressWarnings("ErrorNotRethrown") AssertionError e) {
      assertTrue(e.getMessage().contains("Required field 'firstName' is missing"));
    }

    testParse(
        lines(
            ":record /",
            "  middleName ;param1 = 'p1';param3 = {firstName: 'Hitchcock',bestFriend: <record: {lastName: 'Lee'}>};param2 = 'p2'"
        ), 3
    );

    try {
      testParse(
          lines(
              ":record /",
              "  middleName ;param1 = 'p1';param3 = {firstName: 'Hitchcock',bestFriend: <record: {firstName: 'Bruce'}>};param2 = 'p2'"
          ), 3
      );
      fail();
    } catch (@SuppressWarnings("ErrorNotRethrown") AssertionError e) {
      assertTrue(e.getMessage().contains("Required field 'lastName' is missing"));
    }
  }

  @Test
  public void testRetro() {
    testParse(":record ( bestFriend :() )", 1); // no retro - no tags
//    testParse(":record ( bestFriend2 )", ":record ( bestFriend2:id )", 1); // bestFriend2: Person retro id
  }

  @Test
  public void testParsePath() {
    testParse(":record / bestFriend :+record / bestFriend :record ( id, firstName )", 5);
  }

  @Test
  public void testParsePathMap() {
    testParse(":record / friendsMap / 'John'[;keyParam = 'foo'] :record ( +firstName )", 5);
  }

  @Test
  public void testParseMap() {
    testParse(":record / friendsMap [ 'Alice', 'Bob'!sla = 100 ]( :id )", 3);
    testParse(
        ":record / friendsMap [ 'Alice', 'Bob'!sla = 100 ] :id",
        ":record / friendsMap [ 'Alice', 'Bob'!sla = 100 ]( :id )",
        3
    );
    testParse(
        ":record / friendRecordMap [ ] ( id )",
        ":record / friendRecordMap [ * ]( ( id ) )", // convert to * since map keys are forbidden
        3
    );
  }

  @Test
  public void testParseMapStar() {
    testParse(":record / friendsMap [ * ]( :id )", 3);
  }

  @Test
  public void testParseList() {
    testParse(":record / friends *( :id )", 3);
    testParse(":record / friends * :id", ":record / friends *( :id )", 3);
    testParse(":record / friendRecords * (id)", ":record / friendRecords *( ( id ) )", 3);
  }

  @Test
  public void testParseEmptyRecordParam() {
    testParse(":( id, record ( id ;param2 = ws.epigraph.tests.UserRecord{};param1 = 'p1' ) )", 0);
  }

  @SuppressWarnings("ConstantConditions")
  @Test
  public void testParseEmptyRecordFieldParam() {
    ReqProjection<?, ?> p =
        testParse(":( id, record ( id ;param2 = ws.epigraph.tests.UserRecord{firstName: null};param1 = 'p1' ) )", 0);

    assertTrue(p.isEntityProjection());

    ReqRecordModelProjection recordProjection =
        (ReqRecordModelProjection) p.asEntityProjection().tagProjection("record").modelProjection();
    ReqModelProjection<?, ?, ?> idProjection =
        recordProjection.fieldProjection("id").fieldProjection().projection().singleTagProjection().modelProjection();

    ReqParam param = idProjection.params().get("param2");
    UserRecord paramValue = (UserRecord) param.value();

    assertNotNull(paramValue.getFirstName_());
    assertNull(paramValue.getFirstName());
  }

  @Test
  public void testParseParamDefault() {
    testParse(":( id, record ( id ;param1 = 'p1' ) )", 0); // defaults are not substituted!
  }

  @Test
  public void testParseRecursiveWrongOp() {
    //noinspection ErrorNotRethrown
    try {
      testParse("$self = :( id, record ( id, bestFriend $self ) )", 0);
      fail();
    } catch (AssertionError e) {
      assertTrue(e.getMessage(), e.getMessage().contains("Tag 'id' is not supported"));
      assertTrue(e.getMessage(), e.getMessage().contains("Field 'bestFriend' is not supported"));
    }
  }

  @Test
  public void testParseRecursive() {
    testParse(":( id, record ( id ;param1 = 'p', bestFriend2 $bf2 = :record ( id, bestFriend2 $bf2 ) ) )", 0);
  }

  @Test
  public void testParseRecursiveDifferentOpRecursion() {
    testParse(":( id, record ( id ;param1 = 'p', bestFriend3 $bf3 = :record ( id, bestFriend3 $bf3 ) ) )", 0);
  }

  @Test
  public void testParseTail() {
    testParse(
        ":id :~User :record ( profile )",
        ":id :~ws.epigraph.tests.User :record ( profile )",
        1
    );
  }

  @Test
  public void testParseDoubleTail() {
    testParse(
        ":id :~User :record ( profile ) :~SubUser :record (worstEnemy(id))",
        ":id :~ws.epigraph.tests.User :record ( profile ) :~ws.epigraph.tests.SubUser :record ( worstEnemy ( id ) )",
        1
    );
  }

  @Test
  public void testParseModelTail() {
    testParse(
        ":record (id) ~+UserRecord (profile)",
        ":record ( id ;param1 = 'p1' ) ~+ws.epigraph.tests.UserRecord ( profile )",
        1
    );
  }

  @Test
  public void testParseModelTailRef() {
    testParse(
        ":record ( worstUser $wu = ( id ), worstEnemy ( firstName ) ~ws.epigraph.tests.UserRecord $wu )",
        1
    );
  }

  @Test
  public void testStarTags() {
    testParse(
        ":...",
        ":( id, record )",
        0
    );

    testParse(
        ":(...)",
        ":( id, record )",
        0
    );
  }

  @Test
  public void testStarTags2() {
    testParse(
        ":record(bestFriend:...)",
        ":record ( bestFriend :( id, record ) )",
        1
    );
  }

  @Test
  public void testStarFields() {
    testParse(
        ":record(...)",
        lines(
            ":record (",
            "  id ;param1 = 'p1',",
            "  firstName,",
//            "  middleName ;param2 = 'p2',", // required param1 missing => field not included
            "  bestFriend :(),",
            "  bestFriend2 $bf2 = :id,",
            "  bestFriend3 :(),",
            "  worstEnemy,",
            "  worstUser,",
            "  friends *( :() ),",
            "  friendRecords,",
            "  friendsMap [ * ]( :() ),",
            "  friendRecordMap",
            ")"
        ),
        1
    );
  }

  @Test
  public void testStarFieldsInMap() {
    testParse(
        ":record(friendRecordMap[*](...))",
        ":record ( friendRecordMap [ * ]( ( id, firstName ) ) )",
        1
    );
  }

  @SuppressWarnings("ConstantConditions")
  @Test
  public void testRequiredField() {
    ReqProjection<?, ?> p = testParse(":record ( +id ;param1 = 'p1' )", 1);
    assertTrue(p.isEntityProjection());
    ReqRecordModelProjection rmp =
        (ReqRecordModelProjection) p.asEntityProjection().tagProjection("record").modelProjection();
    assertTrue(rmp.fieldProjection("id").fieldProjection().flag());
  }

  @Test
  public void testRequiredFieldWithTails() {
    testParse(
        ":record ( bestFriend :( +id ) :~ws.epigraph.tests.User :( +profile ) )",
//        ":record ( +bestFriend :( +id ) :~ws.epigraph.tests.User :( +profile ) )",
        1
    );
  }

  @Test
  public void testRequiredFieldMultiModel() {
    // + on field implies + on models
//    testParse(":record ( +bestFriend :( id, record ) )", ":record ( +bestFriend :( +id, +record ) )", 1);
    testParse(":record ( bestFriend :( +id, +record ( id, bestFriend :record ( id, firstName ) ) ) )", 1);
  }

  @Test
  public void testModelRequired() {
    testParse(":( +id, +record ( firstName ) )", 0);
  }

  @Test
  public void testDefaultProjection() {
    testParse("", ":id", 1); // default tag takes over
    testParse(":record", ":record ( firstName )", 1);

    // now without default tag
    DataType dataType = new DataType(Person.type, null);

    String opProjectionStr = ":( +id, `record` ( firstName ) )";
    OpProjection<?, ?> opProjection = parseOpOutputProjection(dataType, opProjectionStr, resolver);

    testParse(
        dataType,
        opProjection,
        ":( record ( firstName ) )",
        0
    );

    // tails
    opProjectionStr = ":( +id, `record` ( firstName ) ) :~ws.epigraph.tests.User :`record` ( lastName )";
    opProjection = parseOpOutputProjection(dataType, opProjectionStr, resolver);

    testParse(
        dataType,
        opProjection,
        "",
        ":record ( firstName ) :~ws.epigraph.tests.User :record ( lastName )",
        0
    );
  }

  @Test
  public void testTailsNormalization() {
    testTailsNormalization(
        ":record(id):~ws.epigraph.tests.User :record(firstName)",
        User.type,
        ":record ( firstName, id ;param1 = 'p1' )"
    );

    testTailsNormalization(
        ":record(id):~ws.epigraph.tests.SubUser :record(firstName)",
        SubUser.type,
        ":record ( firstName, id ;param1 = 'p1' )"
    );

    // parameters merging
    testTailsNormalization(
        ":record(id,firstName;param='foo'):~ws.epigraph.tests.User :record(firstName;param='bar')",
        User.type,
        ":record ( firstName ;param = 'bar', id ;param1 = 'p1' )"
    );

    // annotations merging
    testTailsNormalization(
        ":record(id,firstName!doc='doc1'):~ws.epigraph.tests.User :record(firstName!doc='doc2')",
        User.type,
        ":record ( firstName !doc = \"doc2\", id ;param1 = 'p1' )"
    );
  }

  @Test
  public void testListTailsNormalization() {
    testTailsNormalization(
        ":record(friends*(:id)):~ws.epigraph.tests.User :record(friends*(:record(id)))",
        User.type,
        ":record ( friends *( :( record ( id ), id ) ) )"
    );
  }

  @Test
  public void testMapTailsNormalization() {
    testTailsNormalization(
        ":record(friendsMap[1](:id)):~ws.epigraph.tests.User :record(friendsMap[2 ;keyParam='foo'](:record(id)))",
        User.type,
        ":record ( friendsMap [ '2';keyParam = 'foo', '1' ]( :( record ( id ), id ) ) )"
    );

    testTailsNormalization(
        ":record(friendsMap[2 ;keyParam='bar'](:id)):~ws.epigraph.tests.User :record(friendsMap[2 ;keyParam='foo'](:record(id)))",
        User.type,
        ":record ( friendsMap [ '2';keyParam = 'foo' ]( :( record ( id ), id ) ) )"
    );
  }

  @Test
  public void testDoubleTailNormalized() {
    ReqProjection<?, ?> n = testTailsNormalization(
        ":id :~User :record ( profile ) :~SubUser $su = :record (worstEnemy(id))",
        SubUser.type,
        ":( record ( worstEnemy ( id ), profile ), id )"
    );

    ReqProjection<?, ?> normalizedFrom = n.normalizedFrom();
    assertNotNull(normalizedFrom);
    assertEquals(Person.type, normalizedFrom.type());
  }

  @Test
  public void testModelTailsNormalization() {

    testModelTailsNormalization(
        parseOpOutputProjection(
            new DataType(Person.type, null),
            ":`record`(id)~ws.epigraph.tests.UserRecord(firstName)",
            resolver
        ),
        ":record(id)~ws.epigraph.tests.UserRecord(firstName)",
        UserRecord.type,
        "( firstName, id )"
    );

    testModelTailsNormalization(
        parseOpOutputProjection(
            new DataType(Person.type, null),
            ":`record`( worstEnemy(id)~ws.epigraph.tests.UserRecord(firstName))",
            resolver
        ),
        ":record( worstEnemy(id)~ws.epigraph.tests.UserRecord(firstName))",
        UserRecord.type,
        "( worstEnemy ( firstName, id ) )"
    );

    testModelTailsNormalization(
        parseOpOutputProjection(
            new DataType(Person.type, null),
            ":`record`( worstEnemy(id)~ws.epigraph.tests.UserRecord(lastName)~ws.epigraph.tests.SubUserRecord(firstName))",
            resolver
        ),
        ":record( worstEnemy(id)~ws.epigraph.tests.SubUserRecord(firstName))",
        SubUserRecord.type,
        "( worstEnemy ( firstName, id ) )"
    );

  }

  @Test
  public void testDiamond() {

    testModelTailsNormalization(
        parseOpOutputProjection(
            new DataType(Person.type, null),
            ":`record`(id)~(ws.epigraph.tests.UserRecord(firstName),ws.epigraph.tests.UserRecord2(lastName))",
            resolver
        ),
        ":record(id)~(ws.epigraph.tests.UserRecord(firstName),ws.epigraph.tests.UserRecord2(lastName))",
        UserRecord3.type,
        "( firstName, id )"
    );

  }

  @Test
  public void testParseMeta() {
    final DataType dataType = new DataType(PersonMap.type, null);

    String opProjectionStr =
        "{ ;param: epigraph.String, meta: ( start, count ) } [ required ] :`record` ( id, firstName )";
    @NotNull OpProjection<?, ?> opProjection = parseOpOutputProjection(dataType, opProjectionStr, resolver);

    String projection = "@+( start, count ) ;param = 'foo' [ 2 ]( :record ( id, firstName ) )";

    testParse(
        dataType,
        opProjection,
        projection,
        1
    );
  }

  @Test
  public void testParseMissingRequiredParam() {
    final DataType dataType = new DataType(PersonMap.type, null);

    String opProjectionStr = "{ ;+param: epigraph.String } [ ]( :id )";
    @NotNull OpProjection<?, ?> opProjection = parseOpOutputProjection(dataType, opProjectionStr, resolver);

    try {
      testParse(
          dataType,
          opProjection,
          "[ 2 ]( :id )",
          1
      );
    } catch (@SuppressWarnings("ErrorNotRethrown") AssertionError e) {
      assertTrue(e.getMessage().contains("Required parameter 'param' is missing"));
    }
  }

  @Test
  public void testNonExistingFieldErrorMsg() {
    try {
      testParse(":record(xx)", 1);
      fail();
    } catch (@SuppressWarnings("ErrorNotRethrown") AssertionError e) {
      assertTrue(e.getMessage(), e.getMessage()
          .contains(
              "Field 'xx' is not supported, supported fields: (id,firstName,middleName,bestFriend,bestFriend2,bestFriend3,worstEnemy,worstUser,friends,friendRecords,friendsMap,friendRecordMap)"));
    }
  }

  @Test
  public void testNonExistingEntityTailErrorMsg() {
    try {
      testParse(":id:~X:id", 1);
      fail();
    } catch (@SuppressWarnings("ErrorNotRethrown") AssertionError e) {
      assertTrue(e.getMessage(), e.getMessage()
          .contains("Tag 'X' is not supported, supported tags: (ws.epigraph.tests.User,ws.epigraph.tests.User2)"));
    }
  }

  @Test
  public void testWrongEntityTailErrorMsg() {
    try {
      testParse(":record(id):~ws.epigraph.tests.SingleTagEntity(tag)", 1);
      fail();
    } catch (@SuppressWarnings("ErrorNotRethrown") AssertionError e) {
      String message = e.getMessage();
      assertNotNull(message);
      assertTrue(
          message,
          message.contains(
              "Tag 'ws.epigraph.tests.SingleTagEntity' is not supported, did you mean 'ws.epigraph.tests.User'? Supported tags: (ws.epigraph.tests.User,ws.epigraph.tests.User2)")
      );
    }
  }

  @Test
  public void testNonExistingModelTailErrorMsg() {
    try {
      testParse(":record(id)~X(lastName)", 1);
      fail();
    } catch (@SuppressWarnings("ErrorNotRethrown") AssertionError e) {
      assertTrue(e.getMessage(), e.getMessage()
          .contains("Tail type 'X' is not supported, supported tail types: (ws.epigraph.tests.UserRecord)"));
    }
  }

  @Test
  public void testWrongModelTailErrorMsg() {
    try {
      testParse(":record(id)~epigraph.String(lastName)", 1);
      fail();
    } catch (@SuppressWarnings("ErrorNotRethrown") AssertionError e) {
      String message = e.getMessage();
      assertNotNull(message);
      assertTrue(
          message,
          message.contains(
              "Polymorphic tail for type 'epigraph.String' is not supported. Supported tail types: {ws.epigraph.tests.UserRecord}")
      );
    }
  }


  @SuppressWarnings("ConstantConditions")
  @Test
  public void testPostProcessor() {
    // check that model gets flag when field/var is flag
    ReqProjection<?, ?> p = testParse(":record ( +id ;param1 = 'p1' )", 1);
    ReqRecordModelProjection rmp = (ReqRecordModelProjection) p.singleTagProjection().modelProjection();
    @NotNull ReqFieldProjection fp = rmp.fieldProjection("id").fieldProjection();
    assertTrue(fp.flag());
    ReqProjection<?, ?> ep = fp.projection();
    assertTrue(ep.flag());
    ReqModelProjection<?, ?, ?> mp = ep.singleTagProjection().modelProjection();
    assertTrue(mp.flag());
  }

  @SuppressWarnings("ConstantConditions")
  @Test
  public void testPostProcessorWithRetro() {
    // bestFriend2 has retro :id
    ReqProjection<?, ?> p = testParse(
        ":record( +bestFriend2 )",
        ":record ( +bestFriend2 :+id )",
        1
    );
    ReqRecordModelProjection rmp = (ReqRecordModelProjection) p.singleTagProjection().modelProjection();
    @NotNull ReqFieldProjection fp = rmp.fieldProjection("bestFriend2").fieldProjection();
    assertTrue(fp.flag());
    @NotNull ReqProjection<?, ?> ep = fp.projection();
    assertTrue(ep.flag());
    assertTrue(ep.isEntityProjection());
    ReqTagProjectionEntry tpe = ep.asEntityProjection().tagProjection("id");
    assertNotNull(tpe);
    ReqModelProjection<?, ?, ?> mp = tpe.modelProjection();
    assertTrue(mp.flag());
  }

  @SuppressWarnings("ConstantConditions")
  @Test
  public void testPostProcessorNoTags() {
    // no retro and no tags
    try {
      testParse(":record ( +bestFriend :() )", 1);
      fail();
    } catch (@SuppressWarnings("ErrorNotRethrown") AssertionError e) {
      assertTrue(
          e.getMessage(),
          e.getMessage()
              .contains(
                  "Entity projection is marked as required, but 'ws.epigraph.tests.Person' type has no retro tag defined")
      );
    }
  }

  // todo fix (implement) this
//  @Test
//  public void testDoubleNormalizedTailRef() {
//    Tuple2<StepsAndProjection<ReqProjection<?,?>>, ReqReferenceContext> tuple2 = ReqTestUtil.parseReqEntityProjection2(
//        ReqOutputProjectionPsiParser::new,
//        dataType,
//        personOpProjection,
//        ":id :~ws.epigraph.tests.User $user = :record(firstName) :~ws.epigraph.tests.SubUser $sub = :record(middleName;param1 = 'foo')",
//        resolver
//    );
//
//    ReqProjection<?,?> projection = tuple2._1.projection();
//    ReqReferenceContext referenceContext = tuple2._2;
//
//    ReferenceContext.RefItem<ReqProjection<?,?>> refItem = referenceContext.lookupEntityReference("sub", false);
//    assertNotNull(refItem);
//    ReqProjection<?,?> s = refItem.apply();
//    assertNotNull(s);
//    ReqProjection<?,?> nf = s.normalizedFrom();
//    assertEquals(projection, nf);
//
//    refItem = referenceContext.lookupEntityReference("user", false);
//    assertNotNull(refItem);
//    s = refItem.apply();
//    assertNotNull(s);
//    nf = s.normalizedFrom();
//    assertEquals(projection, nf);
//  }

  // todo negative test cases too

  private ReqProjection<?, ?> testTailsNormalization(String str, Type type, String expected) {
    final @NotNull StepsAndProjection<ReqProjection<?, ?>> stepsAndProjection =
        parseReqOutputProjection(dataType, personOpProjection, str, resolver);

    ReqProjection<?, ?> varProjection = stepsAndProjection.projection();
    final @NotNull ReqProjection<?, ?> normalized = varProjection.normalizedForType(type);

    String actual = TestUtil.printReqProjection(normalized, 0);
    assertEquals(expected, actual);

    return normalized;
  }

  private void testModelTailsNormalization(OpProjection<?, ?> op, String str, DatumType type, String expected) {
    ReqProjection<?, ?> projection = parseReqOutputProjection(dataType, op, str, resolver).projection();
    assertTrue(projection.isEntityProjection());
    final ReqTagProjectionEntry tpe = projection.singleTagProjection();
    assertNotNull(tpe);
    final ReqModelProjection<?, ?, ?> modelProjection = tpe.modelProjection();
    assertNotNull(modelProjection);

    final ReqModelProjection<?, ?, ?> normalized = modelProjection.normalizedForType(type);

    String actual = TestUtil.printReqProjection(normalized, 0);
    assertEquals(expected, actual);
  }

  private @NotNull ReqProjection<?, ?> testParse(String expr, int steps) { return testParse(expr, expr, steps); }

  private void testParse(DataType dataType, OpProjection<?, ?> opProjection, String expr, int steps) {
    testParse(dataType, opProjection, expr, expr, steps);
  }

  private @NotNull ReqProjection<?, ?> testParse(String expr, String expectedProjection, int steps) {
    return testParse(dataType, personOpProjection, expr, expectedProjection, steps);
  }

  private @NotNull ReqProjection<?, ?> testParse(
      DataType dataType,
      OpProjection<?, ?> opProjection,
      String expr,
      String expectedProjection,
      int steps) {

    final StepsAndProjection<ReqProjection<?, ?>> stepsAndProjection =
        parseReqOutputProjection(dataType, opProjection, expr, resolver);

    assertEquals(steps, stepsAndProjection.pathSteps());

    String s = TestUtil.printReqProjection(stepsAndProjection.projection(), steps);

    final String actual =
        s.replaceAll("\"", "'"); // pretty printer outputs double quotes, we use single quotes in URLs
    assertEquals(expectedProjection, actual);

    return stepsAndProjection.projection();
  }

  private @NotNull OpProjection<?, ?> parsePersonOpProjection(@NotNull String projectionString) {
    return parseOpOutputProjection(dataType, projectionString, resolver);
  }

}
