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

/* Created by yegor on 10/21/16. */

package ws.epigraph.wire.json;

import ws.epigraph.http.MimeTypes;
import ws.epigraph.wire.WireFormat;
import org.jetbrains.annotations.NotNull;

public class JsonFormat implements WireFormat {
  public static final JsonFormat INSTANCE = new JsonFormat();

  public static final @NotNull String NAME = "json";

  public static final @NotNull String MIME_TYPE = MimeTypes.JSON;

  public static final @NotNull String ERROR_CODE_FIELD = "ERROR";

  public static final @NotNull String ERROR_MESSAGE_FIELD = "message";

  public static final @NotNull String REC_FIELD = "REC";

  public static final @NotNull String POLYMORPHIC_TYPE_FIELD = "type";

  public static final @NotNull String POLYMORPHIC_VALUE_FIELD = "data";

  public static final @NotNull String DATUM_META_FIELD = "meta";

  public static final @NotNull String DATUM_VALUE_FIELD = "data";

  public static final @NotNull String MAP_ENTRY_KEY_FIELD = "K";

  public static final @NotNull String MAP_ENTRY_VALUE_FIELD = "V";

  @Override
  public @NotNull String name() { return NAME; }

  @Override
  public @NotNull String mimeType() { return MIME_TYPE; }

}
