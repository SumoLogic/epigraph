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
import ws.epigraph.schema.operations.DeleteOperationDeclaration;
import ws.epigraph.schema.operations.OperationDeclaration;
import ws.epigraph.tests.String_Person_Map;
import ws.epigraph.types.DataType;
import ws.epigraph.url.RequestUrl;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static ws.epigraph.test.TestUtil.*;
import static ws.epigraph.url.parser.RequestUrlPsiParserTestUtil.parseIdl;
import static ws.epigraph.url.parser.RequestUrlPsiParserTestUtil.printParameters;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class DeleteRequestUrlPsiParserTest extends NonReadRequestUrlPsiParserTest {

  private final String idlText = lines(
      "namespace test",
      "import ws.epigraph.tests.Person",
      "import ws.epigraph.tests.UserRecord",
      "resource users : map[String,Person] {",
      "  delete {",
      "    deleteProjection [required]( :`record` (id, firstName) )",
      "    outputProjection [required]( :`record` (id, firstName) )",
      "  }",
      "}"
  );

  private final DeleteOperationDeclaration deleteIdl1;
  private final DataType resourceType = String_Person_Map.type.dataType();

  {
    ResourcesSchema schema = parseIdl(idlText, resolver);
    ResourceDeclaration resourceDeclaration = schema.resources().get("users");

    final @NotNull List<OperationDeclaration> operationDeclarations = resourceDeclaration.operations();
    deleteIdl1 = (DeleteOperationDeclaration) operationDeclarations.get(0);

  }

  @Test
  public void testParsing1() throws IOException, PsiProcessingException {
    test(
        deleteIdl1,
        "/users[123](:record(id))>/123:record(id)?format='json'&verbose=true",
        "users",
        3,
        "[ '123' ]( :record ( id ) )",
        "users / '123' :record ( id )",
        "{format = \"json\", verbose = true}"
    );
  }

  private void test(
      DeleteOperationDeclaration op,
      String url,
      String expectedResource,
      int expectedSteps,
      String expectedDeleteProjection,
      String expectedOutputProjection,
      String expectedParams)
      throws PsiProcessingException {

    PsiProcessingContext context = new DefaultPsiProcessingContext();

    final @NotNull RequestUrl requestUrl = new DeleteRequestUrlPsiParser(context).parseRequestUrl(
        resourceType,
        op,
        parseUrlPsi(url),
        resolver,
        context
    );

    failIfHasErrors(true, context.messages());

    assertEquals(expectedResource, requestUrl.fieldName());

    StepsAndProjection<ReqFieldProjection> deleteStepsAndProjection = requestUrl.inputProjection();
    assertNotNull(deleteStepsAndProjection);
    assertNotNull(deleteStepsAndProjection.projection());
    final @Nullable ReqFieldProjection deleteProjection = deleteStepsAndProjection.projection();
    assertEquals(expectedDeleteProjection, printReqEntityProjection(deleteProjection.projection(), 0));

    final @NotNull StepsAndProjection<ReqFieldProjection> stepsAndProjection = requestUrl.outputProjection();
    assertEquals(expectedSteps, stepsAndProjection.pathSteps());
    assertEquals(
        expectedOutputProjection,
        printReqFieldProjection(expectedResource, stepsAndProjection.projection(), expectedSteps)
    );

    assertEquals(expectedParams, printParameters(requestUrl.parameters()));
  }

}
