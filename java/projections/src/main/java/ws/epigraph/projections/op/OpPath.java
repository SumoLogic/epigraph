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
import ws.epigraph.lang.TextLocation;
import ws.epigraph.types.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class OpPath {
  private OpPath() {}

  public static @NotNull OpProjection<?, ?> pathEnd(@NotNull TypeApi type, @NotNull TextLocation location) {
    // todo get rid of this concept, use path steps instead?

    switch (type.kind()) {
      case ENTITY:
        return OpEntityProjection.pathEnd(type, location);
      case RECORD:
        return OpRecordModelProjection.pathEnd((RecordTypeApi) type, location);
      case MAP:
        return OpMapModelProjection.pathEnd((MapTypeApi) type, location);
      case LIST:
        return OpListModelProjection.pathEnd((ListTypeApi) type, location);
      case PRIMITIVE:
        return OpPrimitiveModelProjection.pathEnd((PrimitiveTypeApi) type, location);
      case ENUM:
        throw new IllegalArgumentException("Unsupported model kind");
    }

    return null;
  }

}
