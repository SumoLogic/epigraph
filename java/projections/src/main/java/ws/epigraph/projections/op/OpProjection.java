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

import ws.epigraph.projections.gen.GenProjection;

/**
 * Operation projection - either {@link OpEntityProjection} or {@link OpModelProjection}
 *
 * @param <P>  this projection type, either {@code OpEntityProjection} or
 *             one of {@code OpModelProjection} subclasses
 * @param <MP> model projection type, either {@code OpModelProjection}
 *             if this is an entity projection, or specific
 *             {@code OpModelProjection} subtype if this is a model projection
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public interface OpProjection<P extends OpProjection<?, ?>, MP extends OpModelProjection<?, ?, ?, ?>>
    extends GenProjection<P, OpTagProjectionEntry, OpEntityProjection, MP> {


}
