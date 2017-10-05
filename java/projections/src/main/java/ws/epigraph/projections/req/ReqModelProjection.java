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

package ws.epigraph.projections.req;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.ModelNormalizationContext;
import ws.epigraph.types.*;

import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class ReqModelProjection<
    MP extends ReqModelProjection</*MP*/?, /*SMP*/?, ?>,
    SMP extends ReqModelProjection</*MP*/?, SMP, ?>,
    M extends DatumTypeApi>
    extends AbstractReqModelProjection<MP, SMP, M> {

  protected ReqModelProjection(
      @NotNull M model,
      boolean flag,
      @NotNull ReqParams params,
      @NotNull Directives directives,
      @Nullable MP metaProjection,
      @Nullable List<SMP> tails,
      @NotNull TextLocation location) {
    super(model, flag, params, metaProjection, directives, tails, location);
  }

  protected ReqModelProjection(final @NotNull M model, final @NotNull TextLocation location) {
    super(model, location);
  }

  @SuppressWarnings("unchecked")
  @Override
  protected @NotNull ModelNormalizationContext<M, SMP> newNormalizationContext() {
    return new ModelNormalizationContext<>(m -> {
      switch (m.kind()) {
        case RECORD:
          return (SMP) new ReqRecordModelProjection((RecordTypeApi) m, TextLocation.UNKNOWN);
        case MAP:
          return (SMP) new ReqMapModelProjection((MapTypeApi) m, TextLocation.UNKNOWN);
        case LIST:
          return (SMP) new ReqListModelProjection((ListTypeApi) m, TextLocation.UNKNOWN);
        case PRIMITIVE:
          return (SMP) new ReqPrimitiveModelProjection((PrimitiveTypeApi) m, TextLocation.UNKNOWN);
        default:
          throw new IllegalArgumentException("Unsupported model kind: " + m.kind());
      }
    });
  }

}
