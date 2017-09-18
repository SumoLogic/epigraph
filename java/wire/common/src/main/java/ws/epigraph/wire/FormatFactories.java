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

package ws.epigraph.wire;

import org.jetbrains.annotations.NotNull;

/**
 * A set of input/output format factories for some format
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public interface FormatFactories {
  @NotNull FormatReader.Factory<? extends OpFormatReader> opReaderFactory();

  @NotNull FormatWriter.Factory<? extends OpFormatWriter> opWriterFactory();

  @NotNull FormatReader.Factory<? extends ReqFormatReader> reqReaderFactory();

  @NotNull FormatReader.Factory<? extends ReqUpdateFormatReader> reqUpdateReaderFactory();

  @NotNull FormatReader.Factory<? extends ReqFormatReader> reqOutputReaderFactory();

  @NotNull FormatWriter.Factory<? extends ReqFormatWriter> reqOutputWriterFactory();

  @NotNull FormatWriter.Factory<? extends ReqFormatWriter> reqInputWriterFactory();

  @NotNull FormatWriter.Factory<? extends ReqUpdateFormatWriter> reqUpdateWriterFactory();

  @NotNull FormatWriter.Factory<? extends OpInputFormatWriter> opInputWriterFactory();

}
