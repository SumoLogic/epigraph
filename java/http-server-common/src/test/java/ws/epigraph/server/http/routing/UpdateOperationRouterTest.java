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
import ws.epigraph.idl.operations.OperationIdl;
import ws.epigraph.idl.operations.UpdateOperationIdl;
import ws.epigraph.projections.StepsAndProjection;
import ws.epigraph.projections.req.output.ReqOutputFieldProjection;
import ws.epigraph.projections.req.path.ReqFieldPath;
import ws.epigraph.projections.req.update.ReqUpdateFieldProjection;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.SimpleTypesResolver;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.service.Resource;
import ws.epigraph.service.ServiceInitializationException;
import ws.epigraph.service.operations.ReadOperationResponse;
import ws.epigraph.service.operations.UpdateOperation;
import ws.epigraph.service.operations.UpdateOperationRequest;
import ws.epigraph.test.TestUtil;
import ws.epigraph.tests.*;
import ws.epigraph.url.UpdateRequestUrl;
import ws.epigraph.url.parser.UrlSubParserDefinitions;
import ws.epigraph.url.parser.psi.UrlUpdateUrl;

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
public class UpdateOperationRouterTest {
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
      "  UPDATE {",
      "    id = \"pathless.1\"",
      "    inputProjection []( :record (id, firstName) )",
      "    outputProjection [required]( :record (id, firstName) )",
      "  }",
      "  UPDATE {",
      "    id = \"pathless.2\"",
      "    inputProjection []( :record (id, firstName, lastName) )",
      "    outputProjection [required]( :record (id, firstName, lastName) )",
      "  }",
      "  UPDATE {",
      "    id = \"path.1\"",
      "    path /.",
      "    inputType UserRecord",
      "    inputProjection (id, firstName )",
      "    outputProjection :record (id, firstName, bestFriend :record (id, firstName) )",
      "  }",
      "  UPDATE {",
      "    id = \"path.2\"",
      "    path /.:record/bestFriend",
      "    inputType UserRecord",
      "    inputProjection (id, +firstName )",
      "    outputProjection :record (id, firstName)",
      "  }",
      "  UPDATE {",
      "    id = \"path.3\"",
      "    path /.:record/bestFriend",
      "    inputType UserRecord",
      "    inputProjection (id, firstName )",
      "    outputProjection :record (id, firstName)",
      "  }",
      "}"
  );

  private final Resource resource;

  {
    try {
      Idl idl = parseIdl(idlText, resolver);
      ResourceIdl resourceIdl = idl.resources().get("users");
      assertNotNull(resourceIdl);

      final List<OpImpl> updateOps = new ArrayList<>();

      for (final OperationIdl operationIdl : resourceIdl.operations())
        updateOps.add(new OpImpl((UpdateOperationIdl) operationIdl));

      resource = new Resource(
          resourceIdl,
          Collections.emptyList(),
          Collections.emptyList(),
          updateOps,
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
  public void testPathlessWithUpdate() throws PsiProcessingException {
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
  public void testPathless2WithUpdate() throws PsiProcessingException {
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
  public void testPathless3WithUpdate() throws PsiProcessingException {
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
  public void testPath1WithUpdate() throws PsiProcessingException {
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
  public void testPath2WithUpdate() throws PsiProcessingException {
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
  public void testPath3WithUpdate() throws PsiProcessingException {
    testRouting(
        "/users/1:record/bestFriend<(id)>:record(id)",
        "path.3", // should not select path.2 because required field is missing from update projection
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

    final OperationSearchSuccess<? extends UpdateOperation<?>, UpdateRequestUrl> s = getTargetOpId(url);
    final OpImpl op = (OpImpl) s.operation();
    assertEquals(expectedId, op.getId());

    final @NotNull UpdateRequestUrl updateRequestUrl = s.requestUrl();
    final ReqFieldPath path = updateRequestUrl.path();

    if (expectedPath == null)
      assertNull(path);
    else {
      assertNotNull(path);
      assertEquals(expectedPath, TestUtil.printReqVarPath(path.varProjection()));
    }

    final @Nullable ReqUpdateFieldProjection inputProjection = updateRequestUrl.updateProjection();
    if (expectedInputProjection == null)
      assertNull(inputProjection);
    else {
      assertNotNull(inputProjection);
      assertEquals(expectedInputProjection, TestUtil.printReqUpdateVarProjection(inputProjection.varProjection()));
    }

    final StepsAndProjection<ReqOutputFieldProjection> stepsAndProjection = updateRequestUrl.outputProjection();

    assertEquals(expectedOutputSteps, stepsAndProjection.pathSteps());
    assertEquals(
        expectedOutputProjection,
        TestUtil.printReqOutputVarProjection(stepsAndProjection.projection().varProjection(), expectedOutputSteps)
    );
  }

  @SuppressWarnings("unchecked")
  private OperationSearchSuccess<? extends UpdateOperation<?>, UpdateRequestUrl>
  getTargetOpId(final @NotNull String url) throws PsiProcessingException {
    final @NotNull OperationSearchResult<UpdateOperation<?>> oss = UpdateOperationRouter.INSTANCE.findOperation(
        null,
        parseUpdateUrl(url),
        resource, resolver
    );

    failIfSearchFailure(oss);
    assertTrue(oss instanceof OperationSearchSuccess);
    return (OperationSearchSuccess<? extends UpdateOperation<?>, UpdateRequestUrl>) oss;
  }

  private class OpImpl extends UpdateOperation<PersonId_Person_Map.Data> {

    protected OpImpl(final UpdateOperationIdl declaration) {
      super(declaration);
    }

    public @Nullable String getId() {
      final @Nullable GDataValue value = declaration().annotations().get("id");
      if (value instanceof GPrimitiveDatum)
        return ((GPrimitiveDatum) value).value().toString();
      return null;
    }

    @Override
    public @NotNull CompletableFuture<ReadOperationResponse<PersonId_Person_Map.Data>> process(
        final @NotNull UpdateOperationRequest request) {
      throw new RuntimeException("unreachable");
    }
  }

  private static @NotNull UrlUpdateUrl parseUpdateUrl(@NotNull String url) {
    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();

    UrlUpdateUrl urlPsi = EpigraphPsiUtil.parseText(
        url,
        UrlSubParserDefinitions.UPDATE_URL,
        errorsAccumulator
    );

    failIfHasErrors(urlPsi, errorsAccumulator);

    return urlPsi;
  }
}
