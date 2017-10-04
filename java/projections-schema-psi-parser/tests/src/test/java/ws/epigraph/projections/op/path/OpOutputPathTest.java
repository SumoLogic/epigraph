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

package ws.epigraph.projections.op.path;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import ws.epigraph.projections.gen.ProjectionReferenceName;
import ws.epigraph.projections.op.OpEntityProjection;
import ws.epigraph.projections.op.OpPsiProcessingContext;
import ws.epigraph.projections.op.OpReferenceContext;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.SimpleTypesResolver;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.schema.parser.SchemaSubParserDefinitions;
import ws.epigraph.schema.parser.psi.SchemaOpEntityPath;
import ws.epigraph.test.TestUtil;
import ws.epigraph.tests.*;
import ws.epigraph.types.DataType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static ws.epigraph.test.TestUtil.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpOutputPathTest {
  @Test
  public void testParseEmpty() throws PsiProcessingException {
    testEntityPathParsing("");
  }

  @Test
  public void testParseTag() throws PsiProcessingException {
    testVarPathParsingErr(":id");
  }

  @Test
  public void testParseParam() throws PsiProcessingException {
    testEntityPathParsing(
        ":`record` { ;foo: epigraph.String } / id { ;+param: map[epigraph.String,ws.epigraph.tests.Person] { @epigraph.annotations.Deprecated, default : ( \"foo\": < id: 123 > ) } []( :id ) }"
        ,
        lines(
            ":`record` { ;foo: epigraph.String } /",
            "  id {",
            "    ;+param: map[epigraph.String,ws.epigraph.tests.Person]",
            "      { @epigraph.annotations.Deprecated, default: ( \"foo\": < id: 123 > ) } [ ]( :id )",
            "  }"
        )
    );
  }


  @Test
  public void testParseRecordFieldsWithStructure() throws PsiProcessingException {
    testEntityPathParsing(":`record` / bestFriend :`record` / id");
  }

  @Test
  public void testParseRecordFieldsWithCustomParams() throws PsiProcessingException {
    testEntityPathParsing(":`record` / bestFriend :`record` { @epigraph.annotations.Deprecated } / id");
  }

  @Test
  public void testParseMap() throws PsiProcessingException {
    testEntityPathParsing(":`record` / friendsMap { ;param: epigraph.String } / . :`record` / id");
  }

  private void testVarPathParsingErr(String str) {
    try {
      testEntityPathParsing(
          new DataType(Person.type, Person.id),
          str
          ,
          str
      );
      fail();
    } catch (PsiProcessingException ignored) {
    }
  }

  private void testEntityPathParsing(String str) throws PsiProcessingException {
    testEntityPathParsing(
        str
        ,
        str
    );
  }

  private void testEntityPathParsing(String str, String expected) throws PsiProcessingException {
    testEntityPathParsing(
        new DataType(Person.type, Person.id),
        str
        ,
        expected
    );
  }

  private void testEntityPathParsing(DataType varDataType, String projectionString, String expected)
      throws PsiProcessingException {

    OpEntityProjection varPath = parseOpEntityProjection(varDataType, projectionString);

    String actual = TestUtil.printOpEntityPath(varPath);

    assertEquals("\n" + actual, expected, actual);
//    assertEquals(expected.trim(), actual.trim());
  }

  private OpEntityProjection parseOpEntityProjection(DataType varDataType, String projectionString) throws PsiProcessingException {

    TypesResolver
        resolver = new SimpleTypesResolver(
        PersonId.type,
        Person.type,
        User.type,
        UserId.type,
        UserRecord.type,
        String_Person_Map.type,
        epigraph.String.type,
        epigraph.annotations.Deprecated.type
    );

    return parseOpEntityProjection(varDataType, projectionString, false, resolver);
  }

  public static @NotNull OpEntityProjection parseOpEntityProjection(
      @NotNull DataType varDataType,
      @NotNull String projectionString,
      boolean catchPsiErrors,
      @NotNull TypesResolver resolver) throws PsiProcessingException {

    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();

    SchemaOpEntityPath entityPathPsi = EpigraphPsiUtil.parseText(
        projectionString,
        SchemaSubParserDefinitions.OP_ENTITY_PATH,
        errorsAccumulator
    );

    failIfHasErrors(entityPathPsi, errorsAccumulator);

    final TestUtil.PsiParserClosure<OpEntityProjection> closure = context -> {
      OpReferenceContext referenceContext =
          new OpReferenceContext(ProjectionReferenceName.EMPTY, null, context);

      OpPathPsiProcessingContext pathPsiProcessingContext =
          new OpPathPsiProcessingContext(context, new OpPsiProcessingContext(context, referenceContext));

      @NotNull OpEntityProjection
          vp = OpPathPsiParser.parseEntityPath(varDataType, entityPathPsi, resolver, pathPsiProcessingContext);

      referenceContext.ensureAllReferencesResolved();

      return vp;
    };

    return catchPsiErrors ? runPsiParser(true, closure) : runPsiParserNotCatchingErrors(closure);
  }

}
