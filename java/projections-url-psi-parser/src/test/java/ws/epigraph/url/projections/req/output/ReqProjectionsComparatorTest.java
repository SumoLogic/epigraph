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
import ws.epigraph.projections.op.OpProjection;
import ws.epigraph.projections.req.ReqProjection;
import ws.epigraph.projections.req.ReqProjectionsComparator;
import ws.epigraph.projections.req.ReqRecordModelProjection;
import ws.epigraph.refs.SimpleTypesResolver;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.tests.*;
import ws.epigraph.types.DataType;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static ws.epigraph.url.projections.req.ReqTestUtil.parseOpOutputProjection;
import static ws.epigraph.url.projections.req.ReqTestUtil.parseReqOutputProjection;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqProjectionsComparatorTest {
  // todo introduce separate projections-test module

  private final DataType dataType = new DataType(Person.type, Person.id);
  private final TypesResolver resolver = new SimpleTypesResolver(
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
      epigraph.String.type
  );

  @Test
  public void testRec1() {
    OpProjection<?, ?> op = parsePersonOpProjection(
        "$rec = :`record`(firstName, lastName, bestFriend $rec)"
    );

    ReqProjection<?, ?> p1 = parseReqProjection(
        op,
        "$rec = :record( firstName, bestFriend:record( lastName,bestFriend $rec ) )"
    );

    ReqProjection<?, ?> p2 = parseReqProjection(
        op,
        "$rec = :record( lastName, bestFriend:record( firstName,bestFriend $rec ) )"
    );

    ReqProjectionsComparator comparator = new ReqProjectionsComparator(false, false);
    assertFalse(comparator.equals(p1, p2));

    assertTrue(p1.isEntityProjection());

    final ReqProjection<?, ?> vp1_1 =
        ((ReqRecordModelProjection) p1.asEntityProjection().tagProjection("record").modelProjection()).fieldProjection("bestFriend")
            .fieldProjection()
            .projection();

    assertTrue(comparator.equals(vp1_1, p2));
  }

  private @NotNull ReqProjection<?, ?> parseReqProjection(@NotNull OpProjection<?, ?> op, String s) {
    return parseReqOutputProjection(dataType, op, s, resolver).projection();
  }

  private @NotNull OpProjection<?, ?> parsePersonOpProjection(@NotNull String projectionString) {
    return parseOpOutputProjection(dataType, projectionString, resolver);
  }

  // todo more tests

}
