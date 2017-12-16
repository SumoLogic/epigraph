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
  @SuppressWarnings("ConstantConditions")
  @Test
  public void testRecursiveProjectionTransformation() {
    String projection = ":`record` ( bestFriend $bf = :`record` ( id, bestFriend $bf ) )";
    String transformedProjection = ":`record` ( bestFriend $bf = :`record` ( bestFriend $bf ) )";

    OpProjection<?, ?> p = EpigraphTestUtil.parseOpProjection(
        (DataType) Person.type.dataType(),
        projection,
        StaticTypesResolver.instance()
    );

    String s = EpigraphTestUtil.printOpProjection(p);
    assertEquals(projection, s);

    OpProjection<?, ?> oPersonProjection = p;

    OpRecordModelProjection oPersonRecordProjection =
        (OpRecordModelProjection) p.singleTagProjection().modelProjection();

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

    Tuple2<OpProjection<?, ?>, Map<OpProjection<?, ?>, OpProjection<?, ?>>> tuple  = t.transform(p, null);
    p = tuple._1;
    Map<OpProjection<?, ?>, OpProjection<?, ?>> transformationMap = tuple._2;

    s = EpigraphTestUtil.printOpProjection(p);
    assertEquals(transformedProjection, s);

    // check transformation map

    OpProjection<?, ?> nPersonProjection = p;

    OpRecordModelProjection nPersonRecordProjection =
        (OpRecordModelProjection) p.singleTagProjection().modelProjection();

    OpProjection<?, ?> nBfProjection =
        nPersonRecordProjection.fieldProjection("bestFriend").fieldProjection().projection();

    OpRecordModelProjection nBfRecordProjection =
        (OpRecordModelProjection) nBfProjection.singleTagProjection().modelProjection();

    assertEquals(5, transformationMap.size()); // 5 since $bf gets registered twice
    assertTrue(transformationMap.get(oPersonProjection) == nPersonProjection);
    assertTrue(transformationMap.get(oPersonRecordProjection) == nPersonRecordProjection);
    assertTrue(transformationMap.get(oBfProjection) == nBfProjection);
    assertTrue(transformationMap.get(oBfRecordProjection) == nBfRecordProjection);
  }
}
