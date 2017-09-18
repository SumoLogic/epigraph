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
import ws.epigraph.wire.json.reader.OpJsonFormatReader;
import ws.epigraph.wire.json.reader.ReqJsonFormatReader;
import ws.epigraph.wire.json.reader.ReqUpdateJsonFormatReader;
import ws.epigraph.wire.json.writer.OpInputJsonFormatWriter;
import ws.epigraph.wire.json.writer.OpJsonFormatWriter;
import ws.epigraph.wire.json.writer.ReqJsonFormatWriter;
import ws.epigraph.wire.json.writer.ReqUpdateJsonFormatWriter;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class JsonFormatFactories implements FormatFactories {
  public static final JsonFormatFactories INSTANCE = new JsonFormatFactories();

  @Override
  public @NotNull FormatReader.Factory<? extends OpFormatReader> opReaderFactory() {
    return new OpJsonFormatReader.Factory();
  }

  @Override
  public @NotNull FormatWriter.Factory<? extends OpFormatWriter> opWriterFactory() {
    return new OpJsonFormatWriter.OpOutputJsonFormatWriterFactory();
  }

  @Override
  public @NotNull FormatReader.Factory<? extends ReqFormatReader> reqReaderFactory() {
    return new ReqJsonFormatReader.Factory();
  }

  @Override
  public @NotNull FormatReader.Factory<? extends ReqUpdateFormatReader> reqUpdateReaderFactory() {
    return new ReqUpdateJsonFormatReader.Factory();
  }

  @Override
  public @NotNull FormatReader.Factory<? extends ReqFormatReader> reqOutputReaderFactory() {
    return new ReqJsonFormatReader.Factory();
  }

  @Override
  public @NotNull FormatWriter.Factory<? extends ReqFormatWriter> reqOutputWriterFactory() {
    return new ReqJsonFormatWriter.ReqJsonFormatWriterFactory();
  }

  @Override
  public @NotNull FormatWriter.Factory<? extends ReqFormatWriter> reqInputWriterFactory() {
    return new ReqJsonFormatWriter.ReqJsonFormatWriterFactory();
  }

  @Override
  public @NotNull FormatWriter.Factory<? extends ReqUpdateFormatWriter> reqUpdateWriterFactory() {
    return new ReqUpdateJsonFormatWriter.ReqUpdateJsonFormatWriterFactory();
  }

  @Override
  @Deprecated
  public @NotNull FormatWriter.Factory<? extends OpInputFormatWriter> opInputWriterFactory() {
    return new OpInputJsonFormatWriter.OpInputJsonFormatWriterFactory();
  }
}
