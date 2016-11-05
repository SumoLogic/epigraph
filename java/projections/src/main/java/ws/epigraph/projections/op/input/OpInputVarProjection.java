/*
 * Copyright 2016 Sumo Logic
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

package ws.epigraph.projections.op.input;

import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.abs.AbstractVarProjection;
import ws.epigraph.types.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpInputVarProjection extends AbstractVarProjection<
    OpInputVarProjection,
    OpInputTagProjectionEntry,
    OpInputModelProjection<?, ?, ?>
    > {
  public OpInputVarProjection(
      @NotNull Type type,
      @NotNull LinkedHashMap<String, OpInputTagProjectionEntry> tagProjections,
      @Nullable List<OpInputVarProjection> polymorphicTails,
      @NotNull TextLocation location) {
    super(type, tagProjections, polymorphicTails, location);
  }
}
