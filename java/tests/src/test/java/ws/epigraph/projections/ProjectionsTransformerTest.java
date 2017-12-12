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
import ws.epigraph.util.Tuple2;

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

    OpProjection<?, ?> vp = EpigraphTestUtil.parseOpProjection(
        (DataType) Person.type.dataType(),
        projection,
        StaticTypesResolver.instance()
    );

    String s = EpigraphTestUtil.printOpProjection(vp);
    assertEquals(projection, s);

    OpProjection<?, ?> oPersonProjection = vp;

    OpRecordModelProjection oPersonRecordProjection =
        (OpRecordModelProjection) vp.singleTagProjection().modelProjection();

    OpProjection<?, ?> oBfProjection =
        oPersonRecordProjection.fieldProjection("bestFriend").fieldProjection().projection();

    OpRecordModelProjection oBfRecordProjection =
        (OpRecordModelProjection) oBfProjection.singleTagProjection().modelProjection();

    OpProjectionTransformer t = new OpProjectionTransformer() {
      @Override
      protected @NotNull OpRecordModelProjection transformRecordProjection(
          final @NotNull OpRecordModelProjection recordModelProjection,
          final @NotNull Map<String, OpFieldProjectionEntry> transformedFields,
          final @Nullable List<OpRecordModelProjection> transformedTails,
          final @Nullable OpModelProjection<?, ?, ?, ?> transformedMeta,
          final boolean mustRebuild) {

        transformedFields.remove("id");

        return super.transformRecordProjection(
            recordModelProjection,
            transformedFields,
            transformedTails,
            transformedMeta,
            true
        );
      }
    };

    Tuple2<OpEntityProjection, OpProjectionTransformationMap> tuple  = t.transform(vp, null);
    vp = tuple._1;
    OpProjectionTransformationMap transformationMap = tuple._2;

    s = EpigraphTestUtil.printOpProjection(vp);
    assertEquals(transformedProjection, s);

    // check transformation map

    OpEntityProjection nPersonProjection = vp;

    OpRecordModelProjection nPersonRecordProjection =
        (OpRecordModelProjection) vp.singleTagProjection().modelProjection();

    OpEntityProjection nBfProjection =
        nPersonRecordProjection.fieldProjection("bestFriend").fieldProjection().projection();

    OpRecordModelProjection nBfRecordProjection =
        (OpRecordModelProjection) nBfProjection.singleTagProjection().modelProjection();

    assertEquals(4, transformationMap.size());
    assertTrue(transformationMap.getEntityMapping(oPersonProjection) == nPersonProjection);
    assertTrue(transformationMap.getModelMapping(oPersonRecordProjection) == nPersonRecordProjection);
    assertTrue(transformationMap.getEntityMapping(oBfProjection) == nBfProjection);
    assertTrue(transformationMap.getModelMapping(oBfRecordProjection) == nBfRecordProjection);
  }
}
