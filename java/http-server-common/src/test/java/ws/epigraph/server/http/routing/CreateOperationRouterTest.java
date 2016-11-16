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
import ws.epigraph.idl.Idl;
import ws.epigraph.idl.ResourceIdl;
import ws.epigraph.idl.operations.CreateOperationIdl;
import ws.epigraph.idl.operations.OperationIdl;
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

import java.io.IOException;
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
  private CreateOperationRouter router = new CreateOperationRouter();

  private TypesResolver resolver = new SimpleTypesResolver(
      PersonId.type,
      Person.type,
      User.type,
      UserId.type,
      UserRecord.type,
      String_Person_Map.type,
      epigraph.String.type,
      epigraph.Boolean.type
  );

  private String idlText = lines(
      "namespace test",
      "import ws.epigraph.tests.Person",
      "import ws.epigraph.tests.UserRecord",
      "resource users : map[String,Person] {",
      "  CREATE {",
      "    id = \"pathless.1\"",
      "    inputProjection []( :record (id, firstName) )",
      "    outputProjection [required]( :record (id, firstName) )",
      "  }",
      "  CREATE {",
      "    id = \"pathless.2\"",
      "    inputProjection []( :record (id, firstName, lastName) )",
      "    outputProjection [required]( :record (id, firstName, lastName) )",
      "  }",
      "  CREATE {",
      "    id = \"path.1\"",
      "    path /.",
      "    inputType UserRecord",
      "    inputProjection (id, firstName )",
      "    outputProjection :record (id, firstName, bestFriend :record (id, firstName) )",
      "  }",
      "  CREATE {",
      "    id = \"path.2\"",
      "    path /.:record/bestFriend",
      "    inputType UserRecord",
      "    inputProjection (id, firstName )",
      "    outputProjection :record (id, firstName)",
      "  }",
      "}"
  );

  private Resource resource;

  {
    try {
      Idl idl = parseIdl(idlText, resolver);
      ResourceIdl resourceIdl = idl.resources().get("users");
      assertNotNull(resourceIdl);

      final List<OpImpl> createOps = new ArrayList<>();

      for (final OperationIdl operationIdl : resourceIdl.operations())
        createOps.add(new OpImpl((CreateOperationIdl) operationIdl));

      resource = new Resource(
          resourceIdl,
          Collections.emptyList(),
          createOps,
          Collections.emptyList(),
          Collections.emptyList(),
          Collections.emptyList()

      );
    } catch (IOException | ServiceInitializationException e) {
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
        "/users/1:record/bestFriend<(id)>:record(id)",
        "path.2",
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

    @NotNull final CreateRequestUrl createRequestUrl = s.requestUrl();
    final ReqFieldPath path = createRequestUrl.path();

    if (expectedPath == null)
      assertNull(path);
    else {
      assertNotNull(path);
      assertEquals(expectedPath, TestUtil.printReqVarPath(path.projection()));
    }

    final @Nullable ReqInputFieldProjection inputProjection = createRequestUrl.inputProjection();
    if (expectedInputProjection == null)
      assertNull(inputProjection);
    else {
      assertNotNull(inputProjection);
      assertEquals(expectedInputProjection, TestUtil.printReqInputVarProjection(inputProjection.projection()));
    }

    final StepsAndProjection<ReqOutputFieldProjection> stepsAndProjection = createRequestUrl.outputProjection();

    assertEquals(expectedOutputSteps, stepsAndProjection.pathSteps());
    assertEquals(
        expectedOutputProjection,
        TestUtil.printReqOutputVarProjection(stepsAndProjection.projection().projection(), expectedOutputSteps)
    );
  }

  private OperationSearchSuccess<? extends CreateOperation<?>, CreateRequestUrl> getTargetOpId(@NotNull final String url)
      throws PsiProcessingException {
    @NotNull final OperationSearchResult<CreateOperation<?>> oss = router.findOperation(
        null,
        parseCreateUrl(url),
        resource, resolver
    );

    failIfSearchFailure(oss);
    assertTrue(oss instanceof OperationSearchSuccess);
    return (OperationSearchSuccess<? extends CreateOperation<?>, CreateRequestUrl>) oss;
  }

  private class OpImpl extends CreateOperation<PersonId_Person_Map.Data> {

    protected OpImpl(final CreateOperationIdl declaration) {
      super(declaration);
    }

    @Nullable
    public String getId() {
      final @Nullable GDataValue value = declaration().annotations().get("id");
      if (value instanceof GPrimitiveDatum)
        return ((GPrimitiveDatum) value).value().toString();
      return null;
    }

    @NotNull
    @Override
    public CompletableFuture<? extends ReadOperationResponse<PersonId_Person_Map.Data>> process(
        @NotNull final CreateOperationRequest request) {
      throw new RuntimeException("unreachable");
    }
  }

  @NotNull
  private static UrlCreateUrl parseCreateUrl(@NotNull String url) {
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
