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

package ws.epigraph.projections;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;
import ws.epigraph.EpigraphTestUtil;
import ws.epigraph.projections.op.OpProjectionTransformationMap;
import ws.epigraph.projections.op.OpProjectionTransformer;
import ws.epigraph.projections.op.output.OpOutputFieldProjectionEntry;
import ws.epigraph.projections.op.output.OpOutputModelProjection;
import ws.epigraph.projections.op.output.OpOutputRecordModelProjection;
import ws.epigraph.projections.op.output.OpOutputVarProjection;
import ws.epigraph.refs.StaticTypesResolver;
import ws.epigraph.tests.Person;
import ws.epigraph.types.DataType;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ProjectionsTransformerTest {
  @Test
  public void testRecursiveProjectionTransformation() {
    String projection = ":`record` ( bestFriend $bf = :`record` ( id, bestFriend $bf ) )";
    String transformedProjection = ":`record` ( bestFriend $bf = :`record` ( bestFriend $bf ) )";

    OpProjectionTransformationMap transformationMap = new OpProjectionTransformationMap();

    OpOutputVarProjection vp = EpigraphTestUtil.parseOpOutputVarProjection(
        (DataType) Person.type.dataType(),
        projection,
        StaticTypesResolver.instance()
    );

    String s = EpigraphTestUtil.printOpOutputVarProjection(vp);
    assertEquals(projection, s);

    OpOutputVarProjection oPersonProjection = vp;

    OpOutputRecordModelProjection oPersonRecordProjection =
        (OpOutputRecordModelProjection) vp.singleTagProjection().projection();

    OpOutputVarProjection oBfProjection =
        oPersonRecordProjection.fieldProjection("bestFriend").fieldProjection().varProjection();

    OpOutputRecordModelProjection oBfRecordProjection =
        (OpOutputRecordModelProjection) oBfProjection.singleTagProjection().projection();

    OpProjectionTransformer t = new OpProjectionTransformer() {
      @Override
      protected @NotNull OpOutputRecordModelProjection transformRecordModelProjection(
          final @NotNull OpOutputRecordModelProjection recordModelProjection,
          final @NotNull Map<String, OpOutputFieldProjectionEntry> transformedFields,
          final @Nullable List<OpOutputRecordModelProjection> transformedTails,
          final @Nullable OpOutputModelProjection<?, ?, ?, ?> transformedMeta,
          final boolean mustRebuild) {

        transformedFields.remove("id");

        return super.transformRecordModelProjection(
            recordModelProjection,
            transformedFields,
            transformedTails,
            transformedMeta,
            true
        );
      }
    };

    vp = t.transform(transformationMap, vp, null);

    s = EpigraphTestUtil.printOpOutputVarProjection(vp);
    assertEquals(transformedProjection, s);

    // check transformation map

    OpOutputVarProjection nPersonProjection = vp;

    OpOutputRecordModelProjection nPersonRecordProjection =
        (OpOutputRecordModelProjection) vp.singleTagProjection().projection();

    OpOutputVarProjection nBfProjection =
        nPersonRecordProjection.fieldProjection("bestFriend").fieldProjection().varProjection();

    OpOutputRecordModelProjection nBfRecordProjection =
        (OpOutputRecordModelProjection) nBfProjection.singleTagProjection().projection();

    assertEquals(4, transformationMap.size());
    assertTrue(transformationMap.getEntityMapping(oPersonProjection) == nPersonProjection);
    assertTrue(transformationMap.getModelMapping(oPersonRecordProjection) == nPersonRecordProjection);
    assertTrue(transformationMap.getEntityMapping(oBfProjection) == nBfProjection);
    assertTrue(transformationMap.getModelMapping(oBfRecordProjection) == nBfRecordProjection);
  }
}
