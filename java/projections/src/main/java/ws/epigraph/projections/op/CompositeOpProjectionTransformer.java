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

package ws.epigraph.projections.op;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.projections.gen.GenProjectionTransformer;
import ws.epigraph.types.DataTypeApi;
import ws.epigraph.util.Tuple2;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class CompositeOpProjectionTransformer extends OpProjectionTransformer {
//  // todo unused, remove?
//  private final @NotNull OpProjectionTransformer t1;
//  private final @NotNull OpProjectionTransformer t2;
//
////  private OpProjectionTransformationMap transformationMap;
//
//  public CompositeOpProjectionTransformer(
//      final @NotNull OpProjectionTransformer t1,
//      final @NotNull OpProjectionTransformer t2) {
//
////    this.t1 = t1;
//    this.t1 = new DelegatingOpProjectionTransformer(t1) {
//      // t1 has to check with t2's transformed caches
//
//      @Override
//      protected @NotNull OpEntityProjection transformEntityProjection(
//          @NotNull OpEntityProjection projection,
//          @Nullable DataTypeApi dataType) {
//
//        OpEntityProjection t2Cached = t2.cachedTransformedEntityProjection(projection);
//        if (t2Cached != null)
//          return t2Cached;
//
//        return super.transformEntityProjection(projection, dataType);
//      }
//
//      @Override
//      protected @NotNull OpModelProjection<?, ?, ?, ?> transformModelProjection(
//          @NotNull OpModelProjection<?, ?, ?, ?> projection) {
//
//        OpModelProjection<?, ?, ?, ?> t2Cached = t2.cachedTransformedModelProjection(projection);
//        if (t2Cached != null)
//          return t2Cached;
//
//        return super.transformModelProjection(projection);
//      }
//    };
//
//    this.t2 = t2;
//  }
//
//  @Override
//  public void reset() {
//    t1.reset();
//    t2.reset();
//  }
//
//  @Override
//  public @NotNull Tuple2<OpEntityProjection, OpProjectionTransformationMap> transform(
//      final @NotNull OpEntityProjection projection,
//      final @Nullable DataTypeApi dataType) {
//
//    Tuple2<OpEntityProjection, OpProjectionTransformationMap> res1 = t1.transform(projection, dataType);
//    Tuple2<OpEntityProjection, OpProjectionTransformationMap> res2 = t2.transform(res1._1, dataType);
//    return Tuple2.of(res2._1, combine(res1._2, res2._2));
//
//  }
//
//  @Override
//  public @NotNull Tuple2<OpModelProjection<?, ?, ?, ?>, OpProjectionTransformationMap> transform(final @NotNull OpModelProjection<?, ?, ?, ?> projection) {
//
//    Tuple2<OpModelProjection<?, ?, ?, ?>, OpProjectionTransformationMap> res1 = t1.transform(projection);
//    Tuple2<OpModelProjection<?, ?, ?, ?>, OpProjectionTransformationMap> res2 = t2.transform(res1._1);
//
//    return Tuple2.of(res2._1, combine(res1._2, res2._2));
//  }
//
//  @Override
//  public @NotNull Tuple2<OpFieldProjection, OpProjectionTransformationMap> transform(
//      final @NotNull OpFieldProjection projection, final @NotNull DataTypeApi dataType) {
//    return super.transform(projection, dataType);
//  }
//
//
//  @Override
//  protected @NotNull OpEntityProjection transformResolvedEntityProjection(
//      final @NotNull OpEntityProjection projection, final @Nullable DataTypeApi dataType) {
//    return super.transformResolvedEntityProjection(projection, dataType);
//  }
//
//  private OpProjectionTransformationMap combine(
//      OpProjectionTransformationMap m1,
//      OpProjectionTransformationMap m2) {
//
//    return new OpProjectionTransformationMap() {
//      @Override
//      public OpEntityProjection getEntityMapping(final OpEntityProjection old) {
//        return GenProjectionTransformer.chainTransMap(
//            m1::getEntityMapping,
//            m2::getEntityMapping,
//            old
//        );
//      }
//
//      @Override
//      public OpModelProjection<?, ?, ?, ?> getModelMapping(final OpModelProjection<?, ?, ?, ?> old) {
//        return GenProjectionTransformer.chainTransMap(
//            m1::getModelMapping,
//            m2::getModelMapping,
//            old
//        );
//      }
//
//      @Override
//      public int size() {
//        // todo this is not accurate, same object can be transformed twice
////        return m1.size() + m2.size();
//        throw new UnsupportedOperationException();
//      }
//    };
//  }
}
