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

import de.uka.ilkd.pp.Layouter;
import de.uka.ilkd.pp.NoExceptions;
import de.uka.ilkd.pp.StringBackend;
import org.apache.commons.codec.net.URLCodec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.projections.req.output.ReqOutputFieldProjection;
import ws.epigraph.projections.req.output.ReqOutputProjectionsPrettyPrinter;
import ws.epigraph.projections.req.output.ReqOutputVarProjection;
import ws.epigraph.projections.req.path.ReqFieldPath;
import ws.epigraph.projections.req.path.ReqPathPrettyPrinter;

import java.nio.charset.StandardCharsets;
import java.util.BitSet;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class UriComposer {
  protected static final BitSet SAFE_CHARACTERS = new BitSet(256);

  static {
    // alpha characters
    for (int i = 'a'; i <= 'z'; i++) { SAFE_CHARACTERS.set(i); }
    for (int i = 'A'; i <= 'Z'; i++) { SAFE_CHARACTERS.set(i); }
    // numeric characters
    for (int i = '0'; i <= '9'; i++) { SAFE_CHARACTERS.set(i); }

    CharSequence specialCharacters = "-_.*()<>{}";

    for (int i = 0; i < specialCharacters.length(); i++)
      SAFE_CHARACTERS.set(specialCharacters.charAt(i));
  }

  private UriComposer() {}

  public static @NotNull String composeReadUri(
      @NotNull String fieldName,
      @Nullable ReqFieldPath path,
      @NotNull ReqOutputFieldProjection projection) {

    final String decodedUri;

    if (path == null)
      decodedUri = printReqOutputProjection(fieldName, projection);
    else {
      // see ReadRequestUrlPsiParser::parseReadRequestUrlWithPath

      String pathStr = printReqPath(fieldName, path);
      String varStr = printReqOutputProjection(projection.varProjection());

      decodedUri = pathStr + varStr;
    }

    byte[] urlBytes = URLCodec.encodeUrl(SAFE_CHARACTERS, decodedUri.getBytes(StandardCharsets.UTF_8));
    return "/" + new String(urlBytes, StandardCharsets.UTF_8);
  }

  private static @NotNull String printReqOutputProjection(
      @NotNull String fieldName,
      @NotNull ReqOutputFieldProjection projection) {

    StringBackend sb = new StringBackend(2000);
    Layouter<NoExceptions> layouter = new Layouter<>(sb, 2);

    ReqOutputProjectionsPrettyPrinter<NoExceptions> printer =
        new ReqOutputProjectionsPrettyPrinter<NoExceptions>(layouter) {
          @Override
          protected @NotNull Layouter<NoExceptions> brk() { return layouter; }

          @Override
          protected @NotNull Layouter<NoExceptions> brk(final int width, final int offset) { return layouter; }

          @Override
          protected @NotNull Layouter<NoExceptions> nbsp() { return layouter; }
        };

    printer.print(fieldName, projection, 0);

    return sb.getString();
  }

  private static @NotNull String printReqOutputProjection(
      @NotNull ReqOutputVarProjection projection) {

    StringBackend sb = new StringBackend(2000);
    Layouter<NoExceptions> layouter = new Layouter<>(sb, 2);

    ReqOutputProjectionsPrettyPrinter<NoExceptions> printer =
        new ReqOutputProjectionsPrettyPrinter<NoExceptions>(layouter) {
          @Override
          protected @NotNull Layouter<NoExceptions> brk() { return layouter; }

          @Override
          protected @NotNull Layouter<NoExceptions> brk(final int width, final int offset) { return layouter; }

          @Override
          protected @NotNull Layouter<NoExceptions> nbsp() { return layouter; }
        };

    printer.printVar(projection, 0);

    return sb.getString();
  }

  private static @NotNull String printReqPath(
      @NotNull String fieldName,
      @NotNull ReqFieldPath path) {

    StringBackend sb = new StringBackend(2000);
    Layouter<NoExceptions> layouter = new Layouter<>(sb, 2);

    ReqPathPrettyPrinter<NoExceptions> printer =
        new ReqPathPrettyPrinter<NoExceptions>(layouter) {
          @Override
          protected @NotNull Layouter<NoExceptions> brk() { return layouter; }

          @Override
          protected @NotNull Layouter<NoExceptions> brk(final int width, final int offset) { return layouter; }

          @Override
          protected @NotNull Layouter<NoExceptions> nbsp() { return layouter; }
        };

    printer.print(fieldName, path);

    return sb.getString();
  }
}
