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
import ws.epigraph.projections.StepsAndProjection;
import ws.epigraph.projections.req.ReqEntityProjection;
import ws.epigraph.projections.req.ReqFieldProjection;
import ws.epigraph.projections.req.ReqProjectionsPrettyPrinter;
import ws.epigraph.projections.req.ReqPathPrettyPrinter;

import java.nio.charset.StandardCharsets;
import java.util.BitSet;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class UriComposer {
  protected static final BitSet SAFE_CHARACTERS = new BitSet(256);

  // todo UT
  static {
    // alpha characters
    for (int i = 'a'; i <= 'z'; i++) { SAFE_CHARACTERS.set(i); }
    for (int i = 'A'; i <= 'Z'; i++) { SAFE_CHARACTERS.set(i); }
    // numeric characters
    for (int i = '0'; i <= '9'; i++) { SAFE_CHARACTERS.set(i); }

    CharSequence specialCharacters = "-_.*()"; // <>{} are prohibited by java.net.URI

    for (int i = 0; i < specialCharacters.length(); i++)
      SAFE_CHARACTERS.set(specialCharacters.charAt(i));
  }

  private UriComposer() {}

  public static @NotNull String composeReadUri(
      @NotNull String fieldName,
      @Nullable ReqFieldProjection path,
      @NotNull StepsAndProjection<ReqFieldProjection> outputStepsAndProjection) {

    final String decodedUri;

    if (path == null)
      decodedUri = printReqProjection(fieldName, outputStepsAndProjection);
    else {
      // see ReadRequestUrlPsiParser::parseReadRequestUrlWithPath

      String pathStr = printReqPath(fieldName, path);
      String varStr = printReqProjection(outputStepsAndProjection);

      decodedUri = pathStr + varStr;
    }

    return encodeUri(decodedUri);
  }

  public static @NotNull String composeCreateUri(
      @NotNull String fieldName,
      @Nullable ReqFieldProjection path,
      @Nullable StepsAndProjection<ReqFieldProjection> inputStepsAndProjection,
      @NotNull StepsAndProjection<ReqFieldProjection> outputStepsAndProjection) {

    final StringBuilder decodedUri = new StringBuilder();

    if (path == null) {
      if (inputStepsAndProjection !=null && inputStepsAndProjection.projection().flag())
        decodedUri.append('+');
      decodedUri.append(fieldName);
    } else
      decodedUri.append(printReqPath(fieldName, path));

    if (inputStepsAndProjection != null) {
      decodedUri.append(printReqProjection(inputStepsAndProjection));
    }

    decodedUri.append('>').append(printReqProjection(outputStepsAndProjection));

    return encodeUri(decodedUri.toString());
  }

  public static @NotNull String composeUpdateUri(
      @NotNull String fieldName,
      @Nullable ReqFieldProjection path,
      @Nullable StepsAndProjection<ReqFieldProjection> updateStepsAndProjection,
      @NotNull StepsAndProjection<ReqFieldProjection> outputStepsAndProjection) {

    final StringBuilder decodedUri = new StringBuilder();

    if (path == null) {
      appendFieldName(decodedUri, fieldName, updateStepsAndProjection);
    } else
      decodedUri.append(printReqPath(fieldName, path));

    if (updateStepsAndProjection != null) {
      decodedUri.append(printReqProjection(updateStepsAndProjection));
    }

    decodedUri.append('>').append(printReqProjection(outputStepsAndProjection));

    return encodeUri(decodedUri.toString());
  }

  public static @NotNull String composeDeleteUri(
      @NotNull String fieldName,
      @Nullable ReqFieldProjection path,
      @NotNull StepsAndProjection<ReqFieldProjection> deleteStepsAndProjection,
      @NotNull StepsAndProjection<ReqFieldProjection> outputStepsAndProjection) {

    final StringBuilder decodedUri = new StringBuilder();

    if (path == null) {
      appendFieldName(decodedUri, fieldName, deleteStepsAndProjection);
    } else
      decodedUri.append(printReqPath(fieldName, path));

    decodedUri.append(printReqProjection(deleteStepsAndProjection));

    decodedUri.append('>').append(printReqProjection(outputStepsAndProjection));

    return encodeUri(decodedUri.toString());
  }

  public static @NotNull String composeCustomUri(
      @NotNull String fieldName,
      @Nullable ReqFieldProjection path,
      @Nullable StepsAndProjection<ReqFieldProjection> inputStepsAndProjection,
      @NotNull StepsAndProjection<ReqFieldProjection> outputStepsAndProjection) {

    return composeCreateUri(fieldName, path, inputStepsAndProjection, outputStepsAndProjection); // same
  }

  private static void appendFieldName(
      StringBuilder decodedUri,
      String fieldName,
      @Nullable StepsAndProjection<ReqFieldProjection> stepsAndProjection) {

    if (stepsAndProjection !=null && stepsAndProjection.projection().flag())
      decodedUri.append('+');

    decodedUri.append(fieldName);
  }

  private static @NotNull String encodeUri(@NotNull String decodedUriNoLeadSlash) {
    byte[] urlBytes = URLCodec.encodeUrl(SAFE_CHARACTERS, decodedUriNoLeadSlash.getBytes(StandardCharsets.UTF_8));
    return "/" + new String(urlBytes, StandardCharsets.UTF_8);
  }

  private static @NotNull String printReqProjection(@NotNull StepsAndProjection<ReqFieldProjection> stepsAndProjection) {
    return printReqProjection(
        stepsAndProjection.projection().projection(),
        stepsAndProjection.pathSteps() - 1
    );
  }

  private static @NotNull String printReqProjection(
      @NotNull String fieldName,
      @NotNull StepsAndProjection<ReqFieldProjection> stepsAndProjection) {

    StringBackend sb = new StringBackend(2000);
    Layouter<NoExceptions> layouter = new Layouter<>(sb, 2);

    ReqProjectionsPrettyPrinter<NoExceptions> printer =
        new ReqProjectionsPrettyPrinter<NoExceptions>(layouter) {
          @Override
          protected @NotNull Layouter<NoExceptions> brk() { return layouter; }

          @Override
          protected @NotNull Layouter<NoExceptions> brk(final int width, final int offset) { return layouter; }

          @Override
          protected @NotNull Layouter<NoExceptions> nbsp() { return layouter; }
        };

    printer.print(fieldName, stepsAndProjection.projection(), stepsAndProjection.pathSteps());

    return sb.getString();
  }

  private static @NotNull String printReqProjection(@NotNull ReqEntityProjection projection, int pathSteps) {

    StringBackend sb = new StringBackend(2000);
    Layouter<NoExceptions> layouter = new Layouter<>(sb, 2);

    ReqProjectionsPrettyPrinter<NoExceptions> printer =
        new ReqProjectionsPrettyPrinter<NoExceptions>(layouter) {
          @Override
          protected @NotNull Layouter<NoExceptions> brk() { return layouter; }

          @Override
          protected @NotNull Layouter<NoExceptions> brk(final int width, final int offset) { return layouter; }

          @Override
          protected @NotNull Layouter<NoExceptions> nbsp() { return layouter; }
        };

    printer.printEntity(projection, pathSteps);

    return sb.getString();
  }

  private static @NotNull String printReqPath(
      @NotNull String fieldName,
      @NotNull ReqFieldProjection path) {

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
