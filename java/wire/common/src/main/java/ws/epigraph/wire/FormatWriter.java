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

/* Created by yegor on 10/8/16. */

package ws.epigraph.wire;

import ws.epigraph.data.Data;
import ws.epigraph.data.Datum;
import ws.epigraph.data.Val;
import ws.epigraph.errors.ErrorValue;
import ws.epigraph.projections.req.output.ReqOutputModelProjection;
import ws.epigraph.projections.req.output.ReqOutputVarProjection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface FormatWriter<Exc extends Exception> {

  void writeData(@NotNull ReqOutputVarProjection projection, @Nullable Data data) throws Exc;

  void writeDatum(@NotNull ReqOutputModelProjection<?, ?, ?> projection, @Nullable Datum datum) throws Exc;

  // FIXME take explicit type for all projectionless writes below (or add another set of methods that does):

  void writeData(@Nullable Data data) throws Exc;

  void writeValue(@NotNull Val value) throws Exc;

  void writeDatum(@Nullable Datum datum) throws Exc;

  void writeError(@NotNull ErrorValue error) throws Exc;

}
