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

package ws.epigraph.client.http;

import org.jetbrains.annotations.NotNull;
import ws.epigraph.http.ContentType;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class Util {
  private static final Pattern charsetPattern = Pattern.compile(".*;charset\\s*=\\s*([^;]+).*");

  public static final Charset defaultCharset = StandardCharsets.ISO_8859_1; // https://tools.ietf.org/html/rfc2616#section-3.7.1

  private Util() {}

  public static @NotNull ContentType parseContentType(@NotNull String contentTypeStr) {
    // https://www.w3.org/Protocols/rfc1341/4_Content-Type.html

    String mimeType = contentTypeStr;
    Charset charset = defaultCharset;

    int i = contentTypeStr.indexOf(';');
    if (i != -1) {
      mimeType = contentTypeStr.substring(0, i).trim();
      String params = contentTypeStr.substring(i);
      Matcher charsetMatcher = charsetPattern.matcher(params.toLowerCase());

      if (charsetMatcher.matches()) {
        String charsetName = charsetMatcher.group(1);
        try {
          charset = Charset.forName(charsetName);
        } catch (UnsupportedCharsetException ignored) {
          // log?
        }
      }
    }

    return ContentType.get(mimeType, charset);
  }
}
