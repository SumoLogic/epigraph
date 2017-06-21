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

package ws.epigraph.server.http.routing;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;
import ws.epigraph.projections.StepsAndProjection;
import ws.epigraph.projections.req.output.ReqOutputFieldProjection;
import ws.epigraph.projections.req.path.ReqFieldPath;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.SimpleTypesResolver;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.schema.ResourceDeclaration;
import ws.epigraph.schema.ResourcesSchema;
import ws.epigraph.schema.operations.OperationDeclaration;
import ws.epigraph.schema.operations.ReadOperationDeclaration;
import ws.epigraph.service.Resource;
import ws.epigraph.service.ServiceInitializationException;
import ws.epigraph.service.operations.ReadOperation;
import ws.epigraph.service.operations.ReadOperationRequest;
import ws.epigraph.service.operations.ReadOperationResponse;
import ws.epigraph.test.TestUtil;
import ws.epigraph.tests.*;
import ws.epigraph.url.ReadRequestUrl;
import ws.epigraph.url.parser.UrlSubParserDefinitions;
import ws.epigraph.url.parser.psi.UrlReadUrl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.*;
import static ws.epigraph.server.http.routing.RoutingTestUtil.parseIdl;
import static ws.epigraph.test.TestUtil.failIfHasErrors;
import static ws.epigraph.test.TestUtil.lines;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReadOperationRouterTest {
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
      "  read {",
      "    @String \"pathless.1\"",
      "    outputProjection [required]( :`record` (id, firstName) )",
      "  }",
      "  read pathless2 {",
      "    @String \"pathless.2\"",
      "    outputProjection [required]( :`record` (id, firstName, lastName) )",
      "  }",
      "  read path1 {",
      "    @String \"path.1\"",
      "    path /.",
      "    outputProjection :`record` (id, firstName, bestFriend :`record` (id, firstName) )",
      "  }",
      "  read path2 {",
      "    @String \"path.2\"",
      "    path /.:`record`/bestFriend",
      "    outputProjection :`record` (id, firstName)",
      "  }",
      "}"
  );

  private final Resource resource;

  {
    try {
      ResourcesSchema schema = parseIdl(idlText, resolver);
      ResourceDeclaration resourceDeclaration = schema.resources().get("users");
      assertNotNull(resourceDeclaration);

      final List<OpImpl> readOps = new ArrayList<>();

      for (final OperationDeclaration operationDeclaration : resourceDeclaration.operations())
        readOps.add(new OpImpl((ReadOperationDeclaration) operationDeclaration));

      resource = new Resource(
          resourceDeclaration,
          readOps,
          Collections.emptyList(),
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
    testRouting("/users[1](:record(id))", "pathless.1", null, 1, "[ '1' ]( :record ( id ) )");
  }

  @Test
  public void testPathless2() throws PsiProcessingException {
    testRouting("/users[1](:record(id,lastName))", "pathless.2", null, 1, "[ '1' ]( :record ( id, lastName ) )");
  }

  @Test
  public void testPath1() throws PsiProcessingException {
    testRouting("/users/1:record(id)", "path.1", "/ '1'", 1, ":record ( id )");
  }

  @Test
  public void testPathless3() throws PsiProcessingException {
    testRouting("/users/1:record(id,lastName)", "pathless.2", null, 3, "/ '1' :record ( id, lastName )");
  }

  @Test
  public void testPath2() throws PsiProcessingException {
    testRouting("/users/1:record/bestFriend:record(id)", "path.2", "/ '1' :record / bestFriend", 1, ":record ( id )");
  }

  // todo test routing based on input projection

  private void testRouting(
      @NotNull String url,
      @NotNull String expectedId,
      @Nullable String expectedPath,
      int expectedSteps,
      @NotNull String expectedProjection) throws PsiProcessingException {

    final OperationSearchSuccess<?, ReadRequestUrl> s = getTargetOpId(url);
    final OpImpl op = (OpImpl) s.operation();
    assertEquals(expectedId, op.getId());

    final @NotNull ReadRequestUrl readRequestUrl = s.requestUrl();
    final ReqFieldPath path = readRequestUrl.path();

    if (expectedPath == null)
      assertNull(path);
    else {
      assertNotNull(path);
      assertEquals(expectedPath, TestUtil.printReqVarPath(path.varProjection()));
    }

    final StepsAndProjection<ReqOutputFieldProjection> stepsAndProjection = readRequestUrl.outputProjection();

    assertEquals(expectedSteps, stepsAndProjection.pathSteps());
    assertEquals(
        expectedProjection,
        TestUtil.printReqOutputVarProjection(stepsAndProjection.projection().varProjection(), expectedSteps)
    );
  }

  @SuppressWarnings("unchecked")
  private OperationSearchSuccess<?, ReadRequestUrl>
  getTargetOpId(final @NotNull String url) throws PsiProcessingException {
    final @NotNull OperationSearchResult<ReadOperation<?>> oss = ReadOperationRouter.INSTANCE.findOperation(
        null,
        parseReadUrl(url),
        resource, resolver
    );
    assertTrue(oss instanceof OperationSearchSuccess);
    return (OperationSearchSuccess<?, ReadRequestUrl>) oss;
  }

  private class OpImpl extends ReadOperation<PersonId_Person_Map.Data> {

    OpImpl(final ReadOperationDeclaration declaration) {
      super(declaration);
    }

    @Nullable String getId() {
      epigraph.String.Imm id = declaration().annotations().get(epigraph.String.type);
      return id == null ? null : id.getVal();
    }

    @Override
    public @NotNull CompletableFuture<ReadOperationResponse<PersonId_Person_Map.Data>> process(
        final @NotNull ReadOperationRequest request) {
      throw new RuntimeException("unreachable");
    }
  }

  private static @NotNull UrlReadUrl parseReadUrl(@NotNull String url) {
    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();

    UrlReadUrl urlPsi = EpigraphPsiUtil.parseText(
        url,
        UrlSubParserDefinitions.READ_URL,
        errorsAccumulator
    );

    failIfHasErrors(urlPsi, errorsAccumulator);

    return urlPsi;
  }
}
