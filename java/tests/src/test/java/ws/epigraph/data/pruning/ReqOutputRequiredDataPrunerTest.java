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

package ws.epigraph.data.pruning;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import ws.epigraph.EpigraphTestUtil;
import ws.epigraph.data.Data;
import ws.epigraph.data.Datum;
import ws.epigraph.errors.ErrorValue;
import ws.epigraph.projections.StepsAndProjection;
import ws.epigraph.projections.op.output.OpOutputVarProjection;
import ws.epigraph.projections.req.output.ReqOutputVarProjection;
import ws.epigraph.refs.StaticTypesResolver;
import ws.epigraph.tests.Person;
import ws.epigraph.tests.PersonId;
import ws.epigraph.tests.PersonRecord;
import ws.epigraph.tests.Person_List;
import ws.epigraph.tests.String_PersonRecord_Map;
import ws.epigraph.types.DataType;
import ws.epigraph.util.HttpStatusCode;

import static junit.framework.TestCase.assertTrue;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqOutputRequiredDataPrunerTest {
  private final ReqOutputRequiredDataPruner pruner = new ReqOutputRequiredDataPruner();

  @Test
  public void testNonRequiredField() {
    PersonRecord.Builder record = PersonRecord.create().setId_Error(new ErrorValue(111, "xxx"));

    checkEquals(
        record,
        prune(record, "(id)", "(id)")
    );
  }

  @Test
  public void testRequiredFieldError() {
    assertUseError(
        prune(PersonRecord.create().setId_Error(new ErrorValue(111, "xxx")), "(id)", "(+id)"),
        "/id : Required data is"
    );
  }

  @Test
  public void testRequiredFieldMissing() {
    assertFail(
        prune(PersonRecord.create(), "(id)", "(+id)"),
        "Required field 'id' is missing"
    );
  }

  @Test
  public void testRequiredFieldErrorInSelfVar() {
    assertRemove(
        prune(
            PersonRecord.type.createDataBuilder().set(
                PersonRecord.create().setId_Error(new ErrorValue(111, "xxx"))
            ),
            "(id)",
            "(+id)"
        ),
        "/id : Required data is a [111] error: xxx"
    );
  }

  @Test
  public void testRequiredFieldInDataIsError() {
    checkEquals(
        Person.create()
            .setRecord_Error(new ErrorValue(
                HttpStatusCode.PRECONDITION_FAILED,
                ":record/id : Required data is a [111] error: xxx"
            )),

        prune(
            Person.create().setRecord(PersonRecord.create().setId_Error(new ErrorValue(111, "xxx"))),
            ":`record`(id)",
            ":record(+id)"
        )
    );
  }

  @Test
  public void testRequiredFieldInDataIsMissing() {
    assertFail(
        prune(
            Person.create().setRecord(PersonRecord.create()),
            ":`record`(id)",
            ":record(+id)"
        ),

        ":record : Required field 'id' is missing"
    );
  }

  @Test
  public void testRequiredFieldInDataIsError2() {
    assertRemove(
        prune(
            Person.create().setRecord(PersonRecord.create().setId_Error(new ErrorValue(111, "xxx"))),
            ":`record`(id)",
            ":+record(+id)"
        ),

        "record/id : Required data is a [111] error: xxx"
    );
  }

  @Test
  public void testRequiredFieldInsideMapWithOptKeysIsError() {
    checkEquals(
        String_PersonRecord_Map.create()
            .put("1", PersonRecord.create().setId(PersonId.create(1)))
            .put("3", null),

        prune(
            String_PersonRecord_Map.create()
                .put("1", PersonRecord.create().setId(PersonId.create(1)))
                .put("2", PersonRecord.create().setId_Error(new ErrorValue(111, "xxx")))
                .put("3", null),

            "[](id)",
            "[1,2,3](+id)"
        )
    );
  }

  @Test
  public void testRequiredFieldInsideMapWithReqKeysIsError() {
    assertUseError(
        prune(
            String_PersonRecord_Map.create()
                .put("1", PersonRecord.create().setId(PersonId.create(1)))
                .put("2", PersonRecord.create().setId_Error(new ErrorValue(111, "xxx")))
                .put("3", null),

            "[](id)",
            "[1,2,3]+(+id)"
        ),

        "['2']/id : Required data is a [111] error: xxx"
    );
  }

  @Test
  public void testRequiredDataInsideMapIsError() {
    checkEquals(
        Person_List.create()
            .add(Person.create().setId(PersonId.create(1)))

        ,

        prune(
            Person_List.create()
                .add(Person.create().setId(PersonId.create(1)))
                .add(Person.create().setId_Error(new ErrorValue(HttpStatusCode.NOT_FOUND, "not found")))
            ,
            "*:id",
            "*:+id"
        )
    );
  }

  private void assertFail(@NotNull ReqOutputRequiredDataPruner.DataPruningResult pruningResult, String s) {
    assertTrue(pruningResult.getClass().getName(), pruningResult instanceof ReqOutputRequiredDataPruner.Fail);
    ReqOutputRequiredDataPruner.Fail ue = (ReqOutputRequiredDataPruner.Fail) pruningResult;

    assertTrue(ue.toString(), ue.toString().contains(s));
  }

  private void assertRemove(@NotNull ReqOutputRequiredDataPruner.DataPruningResult pruningResult, String s) {
    assertTrue(pruningResult.getClass().getName(), pruningResult instanceof ReqOutputRequiredDataPruner.RemoveData);
    ReqOutputRequiredDataPruner.RemoveData ue = (ReqOutputRequiredDataPruner.RemoveData) pruningResult;

    assertTrue(ue.toString(), ue.toString().contains(s));
  }

  private void assertUseError(@NotNull ReqOutputRequiredDataPruner.DatumPruningResult pruningResult, String s) {
    assertTrue(pruningResult.getClass().getName(), pruningResult instanceof ReqOutputRequiredDataPruner.UseError);
    ReqOutputRequiredDataPruner.UseError ue = (ReqOutputRequiredDataPruner.UseError) pruningResult;

    assertTrue(ue.toString(), ue.toString().contains(s));
  }

  private void assertFail(@NotNull ReqOutputRequiredDataPruner.DatumPruningResult pruningResult, String s) {
    assertTrue(pruningResult.getClass().getName(), pruningResult instanceof ReqOutputRequiredDataPruner.Fail);
    ReqOutputRequiredDataPruner.Fail ue = (ReqOutputRequiredDataPruner.Fail) pruningResult;

    assertTrue(ue.toString(), ue.toString().contains(s));
  }

  private void checkEquals(
      @NotNull Data expected,
      @NotNull ReqOutputRequiredDataPruner.DataPruningResult dataPruningResult) {

    if (dataPruningResult instanceof ReqOutputRequiredDataPruner.ReplaceData) {
      EpigraphTestUtil.checkEquals(expected, ((ReqOutputRequiredDataPruner.ReplaceData) dataPruningResult).newData);
    } else {
      assertTrue(dataPruningResult.getClass().getName(), dataPruningResult instanceof ReqOutputRequiredDataPruner.Keep);
    }
  }

  private void checkEquals(
      @NotNull Datum expected,
      @NotNull ReqOutputRequiredDataPruner.DatumPruningResult datumPruningResult) {

    if (datumPruningResult instanceof ReqOutputRequiredDataPruner.ReplaceDatum) {
      EpigraphTestUtil.checkEquals(expected, ((ReqOutputRequiredDataPruner.ReplaceDatum) datumPruningResult).newDatum);
    } else {
      assertTrue(
          datumPruningResult.getClass().getName(),
          datumPruningResult instanceof ReqOutputRequiredDataPruner.Keep
      );
    }
  }

  private @NotNull ReqOutputRequiredDataPruner.DataPruningResult prune(
      @NotNull Data data,
      @NotNull String opProjection,
      @NotNull String reqProjection) {

    OpOutputVarProjection op = EpigraphTestUtil.parseOpOutputVarProjection(
        (DataType) data.type().dataType(),
        opProjection,
        StaticTypesResolver.instance()
    );

    StepsAndProjection<ReqOutputVarProjection> req =
        EpigraphTestUtil.parseReqOutputVarProjection(
            (DataType) data.type().dataType(),
            op,
            reqProjection,
            StaticTypesResolver.instance()
        );

    return pruner.pruneData(data, req.projection());
  }

  private @NotNull ReqOutputRequiredDataPruner.DatumPruningResult prune(
      @NotNull Datum datum,
      @NotNull String opProjection,
      @NotNull String reqProjection) {

    OpOutputVarProjection op = EpigraphTestUtil.parseOpOutputVarProjection(
        datum.type().dataType(),
        opProjection,
        StaticTypesResolver.instance()
    );

    StepsAndProjection<ReqOutputVarProjection> req =
        EpigraphTestUtil.parseReqOutputVarProjection(
            datum.type().dataType(),
            op,
            reqProjection,
            StaticTypesResolver.instance()
        );

    //noinspection ConstantConditions
    return pruner.pruneDatum(
        datum,
        req.projection().singleTagProjection().projection()
    );
  }
}
