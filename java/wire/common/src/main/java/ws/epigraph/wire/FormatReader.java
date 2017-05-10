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

/* Created by yegor on 10/8/16. */

package ws.epigraph.wire;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.data.Data;
import ws.epigraph.data.Datum;
import ws.epigraph.data.Val;
import ws.epigraph.errors.ErrorValue;
import ws.epigraph.projections.gen.GenModelProjection;
import ws.epigraph.projections.gen.GenVarProjection;
import ws.epigraph.types.DataType;
import ws.epigraph.types.DatumType;

import java.io.IOException;
import java.io.InputStream;

public interface FormatReader<
    VP extends GenVarProjection<VP, ?, ?>,
    MP extends GenModelProjection</*MP*/?, ?, ?, ?>> {

  @Nullable Data readData(@NotNull VP projection) throws IOException, FormatException;

  @Nullable Datum readDatum(@NotNull MP projection) throws IOException, FormatException;

  @Nullable Data readData(@NotNull DataType type) throws IOException, FormatException;

  @Nullable Datum readDatum(@NotNull DatumType type) throws IOException, FormatException;

  @NotNull Val readValue(@NotNull DatumType type) throws IOException, FormatException;

  @NotNull ErrorValue readError() throws IOException, FormatException;

  interface Factory<FR extends FormatReader<?, ?>> {
    @NotNull WireFormat format();

    @NotNull FR newFormatReader(@NotNull InputStream is) throws IOException;
  }

}
