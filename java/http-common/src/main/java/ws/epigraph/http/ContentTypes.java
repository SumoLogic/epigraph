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

package ws.epigraph.http;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class ContentTypes {
  public static final String TEXT_UTF8 = contentType(MimeTypes.TEXT, StandardCharsets.UTF_8);
  public static final String HTML_UTF8 = contentType(MimeTypes.HTML, StandardCharsets.UTF_8);
  public static final String JSON_UTF8 = contentType(MimeTypes.JSON, StandardCharsets.UTF_8);

  private ContentTypes() {}

  @Contract(pure = true)
  public static @NotNull String contentType(@NotNull String mimeType, @NotNull Charset charset) {
    return mimeType + ";charset=" + charset.name().toLowerCase(); // cache?
  }
}
