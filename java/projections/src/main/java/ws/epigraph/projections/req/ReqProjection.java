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

import ws.epigraph.projections.gen.GenProjection;

/**
 * Request projection - either {@link ReqEntityProjection} or {@link ReqModelProjection}
 *
 * @param <P>  this projection type, either {@code ReqEntityProjection} or
 *             one of {@code ReqModelProjection} subclasses
 * @param <MP> model projection type, either {@code ReqModelProjection}
 *             if this is an entity projection, or specific
 *             {@code ReqModelProjection} subtype if this is a model projection
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public interface ReqProjection<P extends ReqProjection<?, ?>, MP extends ReqModelProjection<?, ?, ?>>
    extends GenProjection<P, ReqTagProjectionEntry, ReqEntityProjection, MP> {
}
