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
import ws.epigraph.projections.op.*;
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

    OpEntityProjection vp = EpigraphTestUtil.parseOpEntityProjection(
        (DataType) Person.type.dataType(),
        projection,
        StaticTypesResolver.instance()
    );

    String s = EpigraphTestUtil.printOpEntityProjection(vp);
    assertEquals(projection, s);

    OpEntityProjection oPersonProjection = vp;

    OpRecordModelProjection oPersonRecordProjection =
        (OpRecordModelProjection) vp.singleTagProjection().projection();

    OpEntityProjection oBfProjection =
        oPersonRecordProjection.fieldProjection("bestFriend").fieldProjection().entityProjection();

    OpRecordModelProjection oBfRecordProjection =
        (OpRecordModelProjection) oBfProjection.singleTagProjection().projection();

    OpProjectionTransformer t = new OpProjectionTransformer() {
      @Override
      protected @NotNull OpRecordModelProjection transformRecordModelProjection(
          final @NotNull OpRecordModelProjection recordModelProjection,
          final @NotNull Map<String, OpFieldProjectionEntry> transformedFields,
          final @Nullable List<OpRecordModelProjection> transformedTails,
          final @Nullable OpModelProjection<?, ?, ?, ?> transformedMeta,
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

    s = EpigraphTestUtil.printOpEntityProjection(vp);
    assertEquals(transformedProjection, s);

    // check transformation map

    OpEntityProjection nPersonProjection = vp;

    OpRecordModelProjection nPersonRecordProjection =
        (OpRecordModelProjection) vp.singleTagProjection().projection();

    OpEntityProjection nBfProjection =
        nPersonRecordProjection.fieldProjection("bestFriend").fieldProjection().entityProjection();

    OpRecordModelProjection nBfRecordProjection =
        (OpRecordModelProjection) nBfProjection.singleTagProjection().projection();

    assertEquals(4, transformationMap.size());
    assertTrue(transformationMap.getEntityMapping(oPersonProjection) == nPersonProjection);
    assertTrue(transformationMap.getModelMapping(oPersonRecordProjection) == nPersonRecordProjection);
    assertTrue(transformationMap.getEntityMapping(oBfProjection) == nBfProjection);
    assertTrue(transformationMap.getModelMapping(oBfRecordProjection) == nBfRecordProjection);
  }
}