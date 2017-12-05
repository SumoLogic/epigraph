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
import ws.epigraph.annotations.Annotations;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.types.*;

import java.util.Collections;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class OpPath {
  private OpPath() {}

  public static @NotNull OpProjection<?, ?> pathEnd(@NotNull TypeApi type, @NotNull TextLocation location) {
    // todo get rid of this concept, use path steps instead?

    switch (type.kind()) {
      case ENTITY:
        return new OpEntityProjection(type, false, Collections.emptyMap(), false, null, location);
      case RECORD:
        return new OpRecordModelProjection(
            (RecordTypeApi) type,
            false,
            null,
            OpParams.EMPTY,
            Annotations.EMPTY,
            null,
            Collections.emptyMap(),
            null,
            location
        );
      case MAP:
        throw new IllegalArgumentException("Op path can't end with a map type " + type.name());
      case LIST:
        throw new IllegalArgumentException("Op path can't end with a list type " + type.name());
      case PRIMITIVE:
        return new OpPrimitiveModelProjection(
            (PrimitiveTypeApi) type,
            false,
            null,
            OpParams.EMPTY,
            Annotations.EMPTY,
            null,
            null,
            location
        );
      case ENUM:
        throw new IllegalArgumentException("Unsupported model kind");
    }

    return null;
  }

}
