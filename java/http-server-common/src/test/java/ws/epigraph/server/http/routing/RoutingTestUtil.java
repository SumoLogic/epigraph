/*
 * Copyright 2016 Sumo Logic
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

package ws.epigraph.server.http.routing;

import org.jetbrains.annotations.NotNull;
import ws.epigraph.edl.Edl;
import ws.epigraph.edl.parser.EdlPsiParser;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingError;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.edl.parser.EdlParserDefinition;
import ws.epigraph.edl.parser.psi.EdlFile;
import ws.epigraph.service.operations.Operation;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.fail;
import static ws.epigraph.test.TestUtil.failIfHasErrors;
import static ws.epigraph.test.TestUtil.runPsiParser;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class RoutingTestUtil {

  private RoutingTestUtil() {}

  static @NotNull Edl parseIdl(@NotNull String text, @NotNull TypesResolver resolver) {
    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();

    @NotNull EdlFile psiFile =
        (EdlFile) EpigraphPsiUtil.parseFile(
            "test.epigraph",
            text,
            EdlParserDefinition.INSTANCE,
            errorsAccumulator
        );

    failIfHasErrors(psiFile, errorsAccumulator);

    return runPsiParser(errors -> EdlPsiParser.parseEdl(psiFile, resolver, errors));
  }

  static void failIfSearchFailure(final OperationSearchResult<? extends Operation<?, ?, ?>> oss) {
    if (oss instanceof OperationSearchFailure) {
      StringBuilder msg = new StringBuilder("Operation matching failed.\n");

      OperationSearchFailure<? extends Operation<?, ?, ?>> failure =
          (OperationSearchFailure<? extends Operation<?, ?, ?>>) oss;
      for (final Map.Entry<? extends Operation<?, ?, ?>, List<PsiProcessingError>> entry : failure.errors()
          .entrySet()) {
        final Operation<?, ?, ?> op = entry.getKey();
        msg.append("\nOperation defined at ").append(op.declaration().location()).append(" errors:\n");
        for (final PsiProcessingError error : entry.getValue()) {
          msg.append(error).append("\n");
        }
      }

      fail(msg.toString());
    }
  }
}
