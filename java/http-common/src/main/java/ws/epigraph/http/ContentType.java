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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ContentType {
  private final @NotNull String mimeType;
  private final @Nullable Charset charset;

  private final String toString;

  ContentType(final @NotNull String mimeType, final @Nullable Charset charset) {
    this.mimeType = mimeType;
    this.charset = charset;
    toString = _toString();
  }

  public static @NotNull ContentType get(final @NotNull String mimeType, final @Nullable Charset charset) {
    if (StandardCharsets.UTF_8.equals(charset)) {
      switch (mimeType) {
        case MimeTypes.HTML:
          return ContentTypes.HTML_UTF8;
        case MimeTypes.JSON:
          return ContentTypes.JSON_UTF8;
        case MimeTypes.TEXT:
          return ContentTypes.TEXT_UTF8;
      }
    }

    return new ContentType(mimeType, charset);
  }

  public @NotNull String mimeType() { return mimeType; }

  public @Nullable Charset charset() { return charset; }

  @Override
  public String toString() { return toString; }

  private String _toString() {
    return charset == null ? mimeType : mimeType + ";charset=" + charset.name().toLowerCase();
  }
}
