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

package ws.epigraph.url.parser;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;
import ws.epigraph.projections.StepsAndProjection;
import ws.epigraph.projections.req.ReqFieldProjection;
import ws.epigraph.psi.DefaultPsiProcessingContext;
import ws.epigraph.psi.PsiProcessingContext;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.schema.ResourceDeclaration;
import ws.epigraph.schema.ResourcesSchema;
import ws.epigraph.schema.operations.CreateOperationDeclaration;
import ws.epigraph.schema.operations.OperationDeclaration;
import ws.epigraph.url.RequestUrl;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static ws.epigraph.test.TestUtil.*;
import static ws.epigraph.url.parser.RequestUrlPsiParserTestUtil.parseIdl;
import static ws.epigraph.url.parser.RequestUrlPsiParserTestUtil.printParameters;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class CreateRequestUrlPsiParserTest extends NonReadRequestUrlPsiParserTest {

  private final String idlText = lines(
      "namespace test",
      "import ws.epigraph.tests.Person",
      "import ws.epigraph.tests.UserRecord",
      "resource users : map[String,Person] {",
      "  create {",
      "    inputType UserRecord",
      "    inputProjection (id, firstName)",
      "    outputProjection [required]( :`record` (id, firstName) )",
      "  }",
      "  create withPath {",
      "    path /.",
      "    inputType UserRecord",
      "    inputProjection (id, firstName)",
      "    outputProjection :`record` (id, firstName)",
      "  }",
      "}"
  );

  private final CreateOperationDeclaration createIdl1;
  private final CreateOperationDeclaration createIdl2;

  {
    ResourcesSchema schema = parseIdl(idlText, resolver);
    ResourceDeclaration resourceDeclaration = schema.resources().get("users");

    final @NotNull List<OperationDeclaration> operationDeclarations = resourceDeclaration.operations();
    createIdl1 = (CreateOperationDeclaration) operationDeclarations.get(0);
    createIdl2 = (CreateOperationDeclaration) operationDeclarations.get(1);

  }

  @Test
  public void testParsing1() throws IOException, PsiProcessingException {
    test(
        createIdl1,
        "/users(id)>/123:record(id)?format='json'&verbose=true",
        "users",
        null,
        3,
        "( id )",
        "users / '123' :record ( id )",
        "{format = \"json\", verbose = true}"
    );
  }

  @Test
  public void testParsingWithPath() throws IOException, PsiProcessingException {
    test(
        createIdl2,
        "/users/123(id)>:record(id)?format='json'&verbose=true",
        "users",
        "/ '123'",
        1,
        "( id )",
        "users :record ( id )",
        "{format = \"json\", verbose = true}"
    );
  }

  @Test
  public void testParsingWithPathOnly() throws IOException, PsiProcessingException {
    test(
        createIdl2,
        "/users/123>:record(id)?format='json'&verbose=true",
        "users",
        "/ '123'",
        1,
        null,
        "users :record ( id )",
        "{format = \"json\", verbose = true}"
    );
  }

  private void test(
      CreateOperationDeclaration op,
      String url,
      String expectedResource,
      @Nullable String expectedPath,
      int expectedOutputSteps,
      @Nullable String expectedInputProjection,
      String expectedOutputProjection,
      String expectedParams)
      throws PsiProcessingException {

    PsiProcessingContext context = new DefaultPsiProcessingContext();

    final @NotNull RequestUrl requestUrl = new CustomRequestUrlPsiParser(context).parseRequestUrl(
        resourceType,
        op,
        parseUrlPsi(url),
        resolver,
        context
    );

    failIfHasErrors(true, context.messages());

    assertEquals(expectedResource, requestUrl.fieldName());

    @Nullable ReqFieldProjection path = requestUrl.path();
    if (expectedPath == null) {
      assertNull(path);
    } else {
      assertNotNull(path);
      assertEquals(expectedPath, printReqEntityPath(path.entityProjection()));
    }

    final @Nullable StepsAndProjection<ReqFieldProjection> inputProjection = requestUrl.inputProjection();
    if (inputProjection == null) assertNull(expectedInputProjection);
    else
      assertEquals(
          expectedInputProjection,
          printReqEntityProjection(inputProjection.projection().entityProjection(), 0)
      );

    final @NotNull StepsAndProjection<ReqFieldProjection> outputStepsAndProjection = requestUrl.outputProjection();
    assertEquals(expectedOutputSteps, outputStepsAndProjection.pathSteps());
    assertEquals(
        expectedOutputProjection,
        printReqFieldProjection(expectedResource, outputStepsAndProjection.projection(), expectedOutputSteps)
    );

    assertEquals(expectedParams, printParameters(requestUrl.parameters()));
  }


}
