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
import org.jetbrains.annotations.Nullable;
import org.junit.Test;
import ws.epigraph.gdata.GDataValue;
import ws.epigraph.gdata.GPrimitiveDatum;
import ws.epigraph.schema.Schema;
import ws.epigraph.schema.ResourceDeclaration;
import ws.epigraph.schema.operations.CreateOperationDeclaration;
import ws.epigraph.schema.operations.OperationDeclaration;
import ws.epigraph.projections.StepsAndProjection;
import ws.epigraph.projections.req.input.ReqInputFieldProjection;
import ws.epigraph.projections.req.output.ReqOutputFieldProjection;
import ws.epigraph.projections.req.path.ReqFieldPath;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.SimpleTypesResolver;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.service.Resource;
import ws.epigraph.service.ServiceInitializationException;
import ws.epigraph.service.operations.CreateOperation;
import ws.epigraph.service.operations.CreateOperationRequest;
import ws.epigraph.service.operations.ReadOperationResponse;
import ws.epigraph.test.TestUtil;
import ws.epigraph.tests.*;
import ws.epigraph.url.CreateRequestUrl;
import ws.epigraph.url.parser.UrlSubParserDefinitions;
import ws.epigraph.url.parser.psi.UrlCreateUrl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.*;
import static ws.epigraph.server.http.routing.RoutingTestUtil.failIfSearchFailure;
import static ws.epigraph.server.http.routing.RoutingTestUtil.parseIdl;
import static ws.epigraph.test.TestUtil.failIfHasErrors;
import static ws.epigraph.test.TestUtil.lines;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class CreateOperationRouterTest {
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
      "  create {",
      "    id = \"pathless.1\"",
      "    inputProjection []( :`record` (id, firstName) )",
      "    outputProjection [required]( :`record` (id, firstName) )",
      "  }",
      "  create {",
      "    id = \"pathless.2\"",
      "    inputProjection []( :`record` (id, firstName, lastName) )",
      "    outputProjection [required]( :`record` (id, firstName, lastName) )",
      "  }",
      "  create {",
      "    id = \"path.1\"",
      "    path /.",
      "    inputType UserRecord",
      "    inputProjection (id, firstName )",
      "    outputProjection :`record` (id, firstName, bestFriend :`record` (id, firstName) )",
      "  }",
      "  create {",
      "    id = \"path.2\"",
      "    path /.:`record`/bestFriend",
      "    inputType UserRecord",
      "    inputProjection (id, +firstName )",
      "    outputProjection :`record` (id, firstName)",
      "  }",
      "  create {",
      "    id = \"path.3\"",
      "    path /.:`record`/bestFriend",
      "    inputType UserRecord",
      "    inputProjection (id, firstName )",
      "    outputProjection :`record` (id, firstName)",
      "  }",
      "}"
  );

  private final Resource resource;

  {
    try {
      Schema schema = parseIdl(idlText, resolver);
      ResourceDeclaration resourceDeclaration = schema.resources().get("users");
      assertNotNull(resourceDeclaration);

      final List<OpImpl> createOps = new ArrayList<>();

      for (final OperationDeclaration operationDeclaration : resourceDeclaration.operations())
        createOps.add(new OpImpl((CreateOperationDeclaration) operationDeclaration));

      resource = new Resource(
          resourceDeclaration,
          Collections.emptyList(),
          createOps,
          Collections.emptyList(),
          Collections.emptyList(),
          Collections.emptyList()

      );
    } catch (ServiceInitializationException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  public void testPathless() throws PsiProcessingException {
    testRouting("/users>[1](:record(id))", "pathless.1", null, null, 1, "[ \"1\" ]( :record ( id ) )");
  }

  @Test
  public void testPathlessWithInput() throws PsiProcessingException {
    testRouting(
        "/users<[](:record(id))>[1](:record(firstName))",
        "pathless.1",
        null,
        "[]( :record ( id ) )",
        1,
        "[ \"1\" ]( :record ( firstName ) )"
    );
  }

  @Test
  public void testPathless2() throws PsiProcessingException {
    testRouting(
        "/users>[1](:record(id,lastName))",
        "pathless.2",
        null,
        null,
        1,
        "[ \"1\" ]( :record ( id, lastName ) )"
    );
  }

  @Test
  public void testPathless2WithInput() throws PsiProcessingException {
    testRouting(
        "/users<[](:record(id))>[1](:record(id,lastName))",
        "pathless.2",
        null,
        "[]( :record ( id ) )",
        1,
        "[ \"1\" ]( :record ( id, lastName ) )"
    );
  }

  @Test
  public void testPathless3() throws PsiProcessingException {
    testRouting("/users>/1:record(id,lastName)", "pathless.2", null, null, 3, "/ \"1\" :record ( id, lastName )");
  }

  @Test
  public void testPathless3WithInput() throws PsiProcessingException {
    testRouting(
        "/users<[](:record(id))>/1:record(id,lastName)",
        "pathless.2",
        null,
        "[]( :record ( id ) )",
        3,
        "/ \"1\" :record ( id, lastName )"
    );
  }

  @Test
  public void testPath1() throws PsiProcessingException {
    testRouting("/users/1>:record(id)", "path.1", "/ \"1\"", null, 1, ":record ( id )");
  }

  @Test
  public void testPath1WithInput() throws PsiProcessingException {
    testRouting("/users/1<(id)>:record(id)", "path.1", "/ \"1\"", "( id )", 1, ":record ( id )");
  }

  @Test
  public void testPath2() throws PsiProcessingException {
    testRouting(
        "/users/1:record/bestFriend>:record(id)",
        "path.2",
        "/ \"1\" :record / bestFriend",
        null,
        1,
        ":record ( id )"
    );
  }

  @Test
  public void testPath2WithInput() throws PsiProcessingException {
    testRouting(
        "/users/1:record/bestFriend<(id,firstName)>:record(id)",
        "path.2",
        "/ \"1\" :record / bestFriend",
        "( id, firstName )",
        1,
        ":record ( id )"
    );
  }

  @Test
  public void testPath3WithInput() throws PsiProcessingException {
    testRouting(
        "/users/1:record/bestFriend<(id)>:record(id)",
        "path.3", // should not select path.2 because required field is missing from input projection
        "/ \"1\" :record / bestFriend",
        "( id )",
        1,
        ":record ( id )"
    );
  }

  private void testRouting(
      @NotNull String url,
      @NotNull String expectedId,
      @Nullable String expectedPath,
      @Nullable String expectedInputProjection,
      int expectedOutputSteps,
      @NotNull String expectedOutputProjection) throws PsiProcessingException {

    final OperationSearchSuccess<? extends CreateOperation<?>, CreateRequestUrl> s = getTargetOpId(url);
    final OpImpl op = (OpImpl) s.operation();
    assertEquals(expectedId, op.getId());

    final @NotNull CreateRequestUrl createRequestUrl = s.requestUrl();
    final ReqFieldPath path = createRequestUrl.path();

    if (expectedPath == null)
      assertNull(path);
    else {
      assertNotNull(path);
      assertEquals(expectedPath, TestUtil.printReqVarPath(path.varProjection()));
    }

    final @Nullable ReqInputFieldProjection inputProjection = createRequestUrl.inputProjection();
    if (expectedInputProjection == null)
      assertNull(inputProjection);
    else {
      assertNotNull(inputProjection);
      assertEquals(expectedInputProjection, TestUtil.printReqInputVarProjection(inputProjection.varProjection()));
    }

    final StepsAndProjection<ReqOutputFieldProjection> stepsAndProjection = createRequestUrl.outputProjection();

    assertEquals(expectedOutputSteps, stepsAndProjection.pathSteps());
    assertEquals(
        expectedOutputProjection,
        TestUtil.printReqOutputVarProjection(stepsAndProjection.projection().varProjection(), expectedOutputSteps)
    );
  }

  @SuppressWarnings("unchecked")
  private OperationSearchSuccess<? extends CreateOperation<?>, CreateRequestUrl>
  getTargetOpId(final @NotNull String url) throws PsiProcessingException {
    final @NotNull OperationSearchResult<CreateOperation<?>> oss = CreateOperationRouter.INSTANCE.findOperation(
        null,
        parseCreateUrl(url),
        resource, resolver
    );

    failIfSearchFailure(oss);
    assertTrue(oss instanceof OperationSearchSuccess);
    return (OperationSearchSuccess<? extends CreateOperation<?>, CreateRequestUrl>) oss;
  }

  private class OpImpl extends CreateOperation<PersonId_Person_Map.Data> {

    OpImpl(final CreateOperationDeclaration declaration) {
      super(declaration);
    }

    @Nullable String getId() {
      final @Nullable GDataValue value = declaration().annotations().get("id");
      if (value instanceof GPrimitiveDatum)
        return ((GPrimitiveDatum) value).value().toString();
      return null;
    }

    @Override
    public @NotNull CompletableFuture<ReadOperationResponse<PersonId_Person_Map.Data>> process(
        final @NotNull CreateOperationRequest request) {
      throw new RuntimeException("unreachable");
    }
  }

  private static @NotNull UrlCreateUrl parseCreateUrl(@NotNull String url) {
    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();

    UrlCreateUrl urlPsi = EpigraphPsiUtil.parseText(
        url,
        UrlSubParserDefinitions.CREATE_URL,
        errorsAccumulator
    );

    failIfHasErrors(urlPsi, errorsAccumulator);

    return urlPsi;
  }
}
