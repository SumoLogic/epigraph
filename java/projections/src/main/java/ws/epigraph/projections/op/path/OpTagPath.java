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

package ws.epigraph.projections.op.path;

import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.abs.AbstractTagProjectionEntry;
import ws.epigraph.types.TagApi;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpTagPath extends AbstractTagProjectionEntry<OpTagPath, OpModelPath<?, ?>> {
  public OpTagPath(
      @NotNull TagApi tag,
      @NotNull OpModelPath<?, ?> projection,
      @NotNull TextLocation location) {
    super(tag, projection, location);
  }
}
