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

import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.NormalizationContext;
import ws.epigraph.types.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class ReqNormalizationContext extends NormalizationContext<TypeApi, ReqProjection<?, ?>> {
  ReqNormalizationContext() {
    super(t -> {
      switch (t.kind()) {
        case ENTITY:
          return new ReqEntityProjection(t, TextLocation.UNKNOWN);
        case RECORD:
          return new ReqRecordModelProjection((RecordTypeApi) t, TextLocation.UNKNOWN);
        case MAP:
          return new ReqMapModelProjection((MapTypeApi) t, TextLocation.UNKNOWN);
        case LIST:
          return new ReqListModelProjection((ListTypeApi) t, TextLocation.UNKNOWN);
        case PRIMITIVE:
          return new ReqPrimitiveModelProjection((PrimitiveTypeApi) t, TextLocation.UNKNOWN);
        default:
          throw new IllegalArgumentException("Unsupported model kind: " + t.kind());
      }
    });
  }
}
