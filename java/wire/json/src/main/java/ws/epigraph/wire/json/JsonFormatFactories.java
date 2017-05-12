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

package ws.epigraph.wire.json;

import org.jetbrains.annotations.NotNull;
import ws.epigraph.wire.*;
import ws.epigraph.wire.json.reader.OpInputJsonFormatReader;
import ws.epigraph.wire.json.reader.ReqInputJsonFormatReader;
import ws.epigraph.wire.json.reader.ReqOutputJsonFormatReader;
import ws.epigraph.wire.json.reader.ReqUpdateJsonFormatReader;
import ws.epigraph.wire.json.writer.ReqOutputJsonFormatWriter;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class JsonFormatFactories implements FormatFactories {
  public static final JsonFormatFactories INSTANCE = new JsonFormatFactories();

  @Override
  public @NotNull FormatReader.Factory<? extends OpInputFormatReader> opInputReaderFactory() {
    return new OpInputJsonFormatReader.Factory();
  }

  @Override
  public @NotNull FormatReader.Factory<? extends ReqInputFormatReader> reqInputReaderFactory() {
    return new ReqInputJsonFormatReader.Factory();
  }

  @Override
  public @NotNull FormatReader.Factory<? extends ReqUpdateFormatReader> reqUpdateReaderFactory() {
    return new ReqUpdateJsonFormatReader.Factory();
  }

  @Override
  public @NotNull FormatReader.Factory<? extends ReqOutputFormatReader> reqOutputReaderFactory() {
    return new ReqOutputJsonFormatReader.Factory();
  }

  @Override
  public @NotNull FormatWriter.Factory<? extends ReqOutputFormatWriter> reqOutputWriterFactory() {
    return new ReqOutputJsonFormatWriter.ReqOutputJsonFormatWriterFactory();
  }
}
