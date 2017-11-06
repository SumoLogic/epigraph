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
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.ProjectionUtils;
import ws.epigraph.projections.StepsAndProjection;
import ws.epigraph.projections.op.OpEntityProjection;
import ws.epigraph.projections.req.*;
import ws.epigraph.psi.PsiProcessingException;
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
import static ws.epigraph.url.projections.req.ReqTestUtil.parseOpOutputEntityProjection;
import static ws.epigraph.url.projections.req.ReqTestUtil.parseReqOutputEntityProjection;

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

  private final OpEntityProjection personOpProjection = parsePersonOpEntityProjection(
      lines(
          ":(",
          "  id,",
          "  `record` (",
          "    id {",
          "      ;param1 : epigraph.String { default: \"p1\" },",
          "      ;param2 : ws.epigraph.tests.UserRecord",
          "    },",
          "    +firstName{;param:epigraph.String},",
          "    middleName{",
          "      ;+param1:epigraph.String,",
          "      ;+param2:epigraph.String { default: \"p2\" },",
          "      ;param3:ws.epigraph.tests.PersonRecord (+firstName, bestFriend:`record`(+lastName))",
          "    },",
          "    bestFriend :( id, `record` (",
          "      id,",
          "      bestFriend :`record` (",
          "        id,",
          "        firstName",
          "      ),",
          "    ) ) :~ws.epigraph.tests.User : profile ,",
          "    bestFriend2 $bf2 = :( id, `record` ( id, bestFriend2 $bf2 ) ),",
          "    bestFriend3 :( id, `record` ( id, firstName, bestFriend3 :`record` ( id, lastName, bestFriend3 : `record` ( id, bestFriend3 $bf3 = :`record` ( id, bestFriend3 $bf3 ) ) ) ) ),",
          "    worstEnemy ( firstName ) ~ws.epigraph.tests.UserRecord $wu",
          "    worstUser $wu = ( id )",
          "    friends *( :(id,`record`(id)) ),",
          "    friendRecords * (id),",
          "    friendsMap [;keyParam:epigraph.String]( :(id, `record` (id, firstName) ) )",
          "    friendRecordMap [ forbidden ] (id, firstName)",
          "  ) ~ws.epigraph.tests.UserRecord (profile)",
          ") :~(",
          "      ws.epigraph.tests.User :`record` (profile)",
          "        :~ws.epigraph.tests.SubUser :`record` (worstEnemy(id)),",
          "      ws.epigraph.tests.User2 :`record` (worstEnemy(id))",
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
  public void testRetro() throws PsiProcessingException {
    testParse(":record ( bestFriend :() )", 1); // no retro - no tags
//    testParse(":record ( bestFriend2 )", ":record ( bestFriend2:id )", 1); // bestFriend2: Person retro id
  }

  @Test
  public void testParsePath() {
    testParse(":record / +bestFriend :+record / bestFriend :record ( id, firstName )", 5);
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
    ReqEntityProjection
        vp =
        testParse(":( id, record ( id ;param2 = ws.epigraph.tests.UserRecord{firstName: null};param1 = 'p1' ) )", 0);

    ReqRecordModelProjection recordProjection =
        (ReqRecordModelProjection) vp.tagProjection("record").projection();
    ReqModelProjection<?, ?, ?> idProjection =
        recordProjection.fieldProjection("id").fieldProjection().entityProjection().singleTagProjection().projection();

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
    ReqEntityProjection vp = testParse(":record ( +id ;param1 = 'p1' )", 1);
    ReqRecordModelProjection rmp = (ReqRecordModelProjection) vp.tagProjection("record").projection();
    assertTrue(rmp.fieldProjection("id").fieldProjection().flag());
  }

  @Test
  public void testRequiredFieldWithTails() {
    testParse(
        ":record ( +bestFriend :( id ) :~ws.epigraph.tests.User :( profile ) )",
//        ":record ( +bestFriend :( +id ) :~ws.epigraph.tests.User :( +profile ) )",
        1
    );
  }

  @Test
  public void testRequiredFieldMultiModel() {
    // + on field implies + on models
//    testParse(":record ( +bestFriend :( id, record ) )", ":record ( +bestFriend :( +id, +record ) )", 1);
    testParse(":record ( +bestFriend :( id, record ) )", 1);
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

    String opProjectionStr = ":( id, `record` ( +firstName ) )";
    OpEntityProjection opProjection = parseOpOutputEntityProjection(dataType, opProjectionStr, resolver);

    testParse(
        dataType,
        opProjection,
        ":( record ( firstName ) )",
        0
    );

    // tails
    opProjectionStr = ":( id, `record` ( +firstName ) ) :~ws.epigraph.tests.User :`record` ( +lastName )";
    opProjection = parseOpOutputEntityProjection(dataType, opProjectionStr, resolver);

    testParse(
        dataType,
        opProjection,
        "",
        ":record ( firstName ) :~ws.epigraph.tests.User :record ( lastName )",
        0
    );
  }

  @Test
  public void testTailsNormalization() throws PsiProcessingException {
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
  public void testListTailsNormalization() throws PsiProcessingException {
    testTailsNormalization(
        ":record(friends*(:id)):~ws.epigraph.tests.User :record(friends*(:record(id)))",
        User.type,
        ":record ( friends *( :( record ( id ), id ) ) )"
    );
  }

  @Test
  public void testMapTailsNormalization() throws PsiProcessingException {
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
    ReqEntityProjection n = testTailsNormalization(
        ":id :~User :record ( profile ) :~SubUser $su = :record (worstEnemy(id))",
        SubUser.type,
        ":( record ( worstEnemy ( id ), profile ), id )"
    );

    ReqEntityProjection normalizedFrom = n.normalizedFrom();
    assertNotNull(normalizedFrom);
    assertEquals(Person.type, normalizedFrom.type());
  }

  @Test
  public void testModelTailsNormalization() throws PsiProcessingException {

    testModelTailsNormalization(
        parseOpOutputEntityProjection(
            new DataType(Person.type, null),
            ":`record`(id)~ws.epigraph.tests.UserRecord(firstName)",
            resolver
        ),
        ":record(id)~ws.epigraph.tests.UserRecord(firstName)",
        UserRecord.type,
        ":record ( firstName, id )"
    );

    testModelTailsNormalization(
        parseOpOutputEntityProjection(
            new DataType(Person.type, null),
            ":`record`( worstEnemy(id)~ws.epigraph.tests.UserRecord(firstName))",
            resolver
        ),
        ":record( worstEnemy(id)~ws.epigraph.tests.UserRecord(firstName))",
        UserRecord.type,
        ":record ( worstEnemy ( firstName, id ) )"
    );

    testModelTailsNormalization(
        parseOpOutputEntityProjection(
            new DataType(Person.type, null),
            ":`record`( worstEnemy(id)~ws.epigraph.tests.UserRecord(lastName)~ws.epigraph.tests.SubUserRecord(firstName))",
            resolver
        ),
        ":record( worstEnemy(id)~ws.epigraph.tests.SubUserRecord(firstName))",
        SubUserRecord.type,
        ":record ( worstEnemy ( firstName, id ) )"
    );

  }

  @Test
  public void testDiamond() throws PsiProcessingException {

    testModelTailsNormalization(
        parseOpOutputEntityProjection(
            new DataType(Person.type, null),
            ":`record`(id)~(ws.epigraph.tests.UserRecord(firstName),ws.epigraph.tests.UserRecord2(lastName))",
            resolver
        ),
        ":record(id)~(ws.epigraph.tests.UserRecord(firstName),ws.epigraph.tests.UserRecord2(lastName))",
        UserRecord3.type,
        ":record ( firstName, id )"
    );

  }

  @Test
  public void testParseMeta() throws PsiProcessingException {
    final DataType dataType = new DataType(PersonMap.type, null);

    String opProjectionStr =
        "{ ;param: epigraph.String, meta: ( start, count ) } [ required ] :`record` ( id, firstName )";
    @NotNull OpEntityProjection opProjection = parseOpOutputEntityProjection(dataType, opProjectionStr, resolver);

    String projection = "@+( start, count ) ;param = 'foo' [ 2 ]( :record ( id, firstName ) )";

    testParse(
        dataType,
        opProjection,
        projection,
        1
    );
  }

  @Test
  public void testParseMissingRequiredParam() throws PsiProcessingException {
    final DataType dataType = new DataType(PersonMap.type, null);

    String opProjectionStr = "{ ;+param: epigraph.String } [ ]( :id )";
    @NotNull OpEntityProjection opProjection = parseOpOutputEntityProjection(dataType, opProjectionStr, resolver);

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
  public void testNonExistingEntityTailErrorMsg() {
    try {
      testParse(":id:~X:id", 1);
      fail();
    } catch (@SuppressWarnings("ErrorNotRethrown") AssertionError e) {
      assertTrue(e.getMessage(), e.getMessage()
          .contains("Unknown tail type 'X'. Supported tail types: {ws.epigraph.tests.User, ws.epigraph.tests.User2}"));
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
              "Polymorphic tail for type 'ws.epigraph.tests.SingleTagEntity' is not supported. Supported tail types: {ws.epigraph.tests.User, ws.epigraph.tests.User2}")
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
          .contains("Unknown tail type 'X'. Supported tail types: {ws.epigraph.tests.UserRecord}"));
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
  public void testPostProcessor() throws PsiProcessingException {
    // check that model gets flag when field/var is flag
    ReqEntityProjection p = testParse(":record ( +id ;param1 = 'p1' )", 1);
    ReqRecordModelProjection rmp = (ReqRecordModelProjection) p.singleTagProjection().projection();
    @NotNull ReqFieldProjection fp = rmp.fieldProjection("id").fieldProjection();
    assertTrue(fp.flag());
    ReqEntityProjection ep = fp.entityProjection();
    assertTrue(ep.flag());
    ReqModelProjection<?, ?, ?> mp = ep.singleTagProjection().projection();
    assertTrue(mp.flag());
  }

  @SuppressWarnings("ConstantConditions")
  @Test
  public void testPostProcessorWithRetro() throws PsiProcessingException {
    // bestFriend2 has retro :id
    ReqEntityProjection p = testParse(
        ":record( +bestFriend2 )",
        ":record ( +bestFriend2 :+id )",
        1
    );
    ReqRecordModelProjection rmp = (ReqRecordModelProjection) p.singleTagProjection().projection();
    @NotNull ReqFieldProjection fp = rmp.fieldProjection("bestFriend2").fieldProjection();
    assertTrue(fp.flag());
    @NotNull ReqEntityProjection ep = fp.entityProjection();
    assertTrue(ep.flag());
    ReqTagProjectionEntry tpe = ep.tagProjection("id");
    assertNotNull(tpe);
    ReqModelProjection<?, ?, ?> mp = tpe.projection();
    assertTrue(mp.flag());
  }

  @SuppressWarnings("ConstantConditions")
  @Test
  public void testPostProcessorNoTags() throws PsiProcessingException {
    // no retro and no tags
    testParse(":record ( +bestFriend :() )", 1); // should be an error?
  }

  // todo fix (implement) this
//  @Test
//  public void testDoubleNormalizedTailRef() {
//    Tuple2<StepsAndProjection<ReqEntityProjection>, ReqReferenceContext> tuple2 = ReqTestUtil.parseReqEntityProjection2(
//        ReqOutputProjectionPsiParser::new,
//        dataType,
//        personOpProjection,
//        ":id :~ws.epigraph.tests.User $user = :record(firstName) :~ws.epigraph.tests.SubUser $sub = :record(middleName;param1 = 'foo')",
//        resolver
//    );
//
//    ReqEntityProjection projection = tuple2._1.projection();
//    ReqReferenceContext referenceContext = tuple2._2;
//
//    ReferenceContext.RefItem<ReqEntityProjection> refItem = referenceContext.lookupEntityReference("sub", false);
//    assertNotNull(refItem);
//    ReqEntityProjection s = refItem.apply();
//    assertNotNull(s);
//    ReqEntityProjection nf = s.normalizedFrom();
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

  private ReqEntityProjection testTailsNormalization(String str, Type type, String expected) {
    final @NotNull StepsAndProjection<ReqEntityProjection> stepsAndProjection =
        parseReqOutputEntityProjection(dataType, personOpProjection, str, resolver);

    ReqEntityProjection varProjection = stepsAndProjection.projection();
    final @NotNull ReqEntityProjection normalized = varProjection.normalizedForType(type);

    String actual = TestUtil.printReqEntityProjection(normalized, 0);
    assertEquals(expected, actual);

    return normalized;
  }

  private void testModelTailsNormalization(OpEntityProjection op, String str, DatumType type, String expected) {
    ReqEntityProjection varProjection = parseReqOutputEntityProjection(dataType, op, str, resolver).projection();
    final ReqTagProjectionEntry tagProjectionEntry = varProjection.singleTagProjection();
    assertNotNull(tagProjectionEntry);
    final ReqModelProjection<?, ?, ?> modelProjection = tagProjectionEntry.projection();
    assertNotNull(modelProjection);

    final ReqModelProjection<?, ?, ?> normalized = modelProjection.normalizedForType(type);
    final ReqEntityProjection normalizedVar = new ReqEntityProjection(
        varProjection.type(),
        false,
        ProjectionUtils.singletonLinkedHashMap(
            tagProjectionEntry.tag().name(),
            new ReqTagProjectionEntry(
                tagProjectionEntry.tag(),
                normalized,
                TextLocation.UNKNOWN
            )
        ),
        varProjection.parenthesized(), null,
        TextLocation.UNKNOWN
    );
    String actual = TestUtil.printReqEntityProjection(normalizedVar, 0);
    assertEquals(expected, actual);
  }

  private ReqEntityProjection testParse(String expr, int steps) { return testParse(expr, expr, steps); }

  private void testParse(DataType dataType, OpEntityProjection opProjection, String expr, int steps) {
    testParse(dataType, opProjection, expr, expr, steps);
  }

  private ReqEntityProjection testParse(String expr, String expectedProjection, int steps) {
    return testParse(dataType, personOpProjection, expr, expectedProjection, steps);
  }

  private ReqEntityProjection testParse(
      DataType dataType,
      OpEntityProjection opProjection,
      String expr,
      String expectedProjection,
      int steps) {

    final @NotNull StepsAndProjection<ReqEntityProjection> stepsAndProjection =
        parseReqOutputEntityProjection(dataType, opProjection, expr, resolver);

    assertEquals(steps, stepsAndProjection.pathSteps());

    String s = TestUtil.printReqEntityProjection(stepsAndProjection.projection(), steps);

    final String actual =
        s.replaceAll("\"", "'"); // pretty printer outputs double quotes, we use single quotes in URLs
    assertEquals(expectedProjection, actual);

    return stepsAndProjection.projection();
  }

  private @NotNull OpEntityProjection parsePersonOpEntityProjection(@NotNull String projectionString) {
    return parseOpOutputEntityProjection(dataType, projectionString, resolver);
  }

}
