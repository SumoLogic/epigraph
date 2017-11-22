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

import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.abs.AbstractFieldProjectionEntry;
import ws.epigraph.types.FieldApi;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpFieldProjectionEntry extends AbstractFieldProjectionEntry<
    OpEntityProjection,
    OpTagProjectionEntry,
    OpModelProjection<?, ?, ?, ?>,
    OpFieldProjection
    > {

  public OpFieldProjectionEntry(
      @NotNull FieldApi field,
      @NotNull OpFieldProjection projection,
      @NotNull TextLocation location) {
    super(field, projection, location);
  }

  @Override
  public @NotNull OpFieldProjectionEntry overridenFieldProjection(@NotNull FieldApi overridingField) {
    return new OpFieldProjectionEntry(
        overridingField,
        new OpFieldProjection(
            fieldProjection().projection().normalizedForType(overridingField.dataType().type()),
            TextLocation.UNKNOWN
        ),
        TextLocation.UNKNOWN
    );
  }
}
