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

package ws.epigraph.url.parser;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;
import ws.epigraph.schema.Edl;
import ws.epigraph.schema.ResourceDeclaration;
import ws.epigraph.schema.operations.OperationDeclaration;
import ws.epigraph.schema.operations.UpdateOperationDeclaration;
import ws.epigraph.projections.StepsAndProjection;
import ws.epigraph.projections.req.output.ReqOutputFieldProjection;
import ws.epigraph.projections.req.update.ReqUpdateFieldProjection;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingError;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.SimpleTypesResolver;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.tests.*;
import ws.epigraph.types.DataType;
import ws.epigraph.url.UpdateRequestUrl;
import ws.epigraph.url.parser.psi.UrlUpdateUrl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static ws.epigraph.test.TestUtil.*;
import static ws.epigraph.url.parser.RequestUrlPsiParserTestUtil.parseIdl;
import static ws.epigraph.url.parser.RequestUrlPsiParserTestUtil.printParameters;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class UpdateRequestUrlPsiParserTest {
  private final TypesResolver resolver = new SimpleTypesResolver(
      PersonId.type,
      Person.type,
      User.type,
      UserId.type,
      UserRecord.type,
      String_Person_Map.type,
      epigraph.String.type,
      epigraph.Boolean.type
  );

  private final String idlText = lines(
      "namespace test",
      "import ws.epigraph.tests.Person",
      "import ws.epigraph.tests.UserRecord",
      "resource users : map[String,Person] {",
      "  update {",
      "    inputType UserRecord",
      "    inputProjection (id, firstName)",
      "    outputProjection [required]( :`record` (id, firstName) )",
      "  }",
      "}"
  );

  private final UpdateOperationDeclaration updateIdl1;
  private final DataType resourceType = String_Person_Map.type.dataType();

  {
    Edl edl = parseIdl(idlText, resolver);
    ResourceDeclaration resourceDeclaration = edl.resources().get("users");

    final @NotNull List<OperationDeclaration> operationDeclarations = resourceDeclaration.operations();
    updateIdl1 = (UpdateOperationDeclaration) operationDeclarations.get(0);

  }

  @Test
  public void testParsing1() throws IOException, PsiProcessingException {
    test(
        updateIdl1,
        "/users<+(+id)>/123:record(id)?format='json'&verbose=true",
        "users",
        3,
        "( +id )",
        "+users / \"123\" :record ( id )",
        "{format = \"json\", verbose = true}"
    );
  }

  private void test(
      UpdateOperationDeclaration op,
      String url,
      String expectedResource,
      int expectedSteps,
      String expectedInputProjection,
      String expectedOutputProjection,
      String expectedParams)
      throws PsiProcessingException {

    List<PsiProcessingError> errors = new ArrayList<>();
    final @NotNull UpdateRequestUrl requestUrl = UpdateRequestUrlPsiParser.parseUpdateRequestUrl(
        resourceType,
        op,
        parseUrlPsi(url),
        resolver,
        errors
    );

    failIfHasErrors(errors);

    assertEquals(expectedResource, requestUrl.fieldName());

    final @Nullable ReqUpdateFieldProjection inputProjection = requestUrl.updateProjection();
    if (inputProjection == null) assertNull(expectedInputProjection);
    else assertEquals(expectedInputProjection, printReqUpdateVarProjection(inputProjection.varProjection()));

    final @NotNull StepsAndProjection<ReqOutputFieldProjection> stepsAndProjection = requestUrl.outputProjection();
    assertEquals(expectedSteps, stepsAndProjection.pathSteps());
    assertEquals(
        expectedOutputProjection,
        printReqOutputFieldProjection(expectedResource, stepsAndProjection.projection(), expectedSteps)
    );

    assertEquals(expectedParams, printParameters(requestUrl.parameters()));
  }


  private static UrlUpdateUrl parseUrlPsi(@NotNull String text) {
    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();

    @NotNull UrlUpdateUrl urlPsi =
        EpigraphPsiUtil.parseText(text, UrlSubParserDefinitions.UPDATE_URL, errorsAccumulator);

    failIfHasErrors(urlPsi, errorsAccumulator);

    return urlPsi;
  }

}
