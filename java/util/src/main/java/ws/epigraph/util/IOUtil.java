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

package ws.epigraph.util;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.Charset;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class IOUtil {
  private IOUtil() {}

  public static @NotNull String readInputStream(@NotNull InputStream is, @NotNull Charset charset) throws IOException {
    StringBuilder textBuilder = new StringBuilder();

    try (Reader reader = new BufferedReader(new InputStreamReader(is, Charset.forName(charset.name())))) {
      for (int c = reader.read(); c != -1; c = reader.read()) { textBuilder.append((char) c); }
    }

    return textBuilder.toString();
  }

  public static long copy(@NotNull InputStream input, @NotNull OutputStream os) throws IOException {
    byte[] buffer = new byte[4096];
    long count = 0;
    int n;
    while ((n = input.read(buffer)) != -1) {
      os.write(buffer, 0, n);
      count += n;
    }
    return count;
  }
}
