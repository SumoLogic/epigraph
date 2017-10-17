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

package ws.epigraph.schema;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import ws.epigraph.projections.gen.ProjectionReferenceName;
import ws.epigraph.projections.op.OpFieldProjection;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.refs.SimpleTypesResolver;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.schema.operations.OperationDeclaration;
import ws.epigraph.schema.parser.ResourcesSchemaPsiParser;
import ws.epigraph.schema.parser.SchemaParserDefinition;
import ws.epigraph.schema.parser.psi.SchemaFile;
import ws.epigraph.tests.*;

import java.io.IOException;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static ws.epigraph.test.TestUtil.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class SchemaParserTest {
  private final TypesResolver resolver = new SimpleTypesResolver(
      PersonId.type,
      Person.type,
      PersonRecord.type,
      User.type,
      UserId.type,
      UserRecord.type,
      String_Person_Map.type,
      epigraph.String.type,
      epigraph.Boolean.type,
      epigraph.annotations.Doc.type
  );

  @Test
  public void testEmpty() throws IOException {
    testParse(
        lines(
            "namespace test",
            "import ws.epigraph.tests.Person"
        ),
        "namespace test"
    );
  }

  @Test
  public void testEmptyResource() throws IOException {
    testParse(
        lines(
            "namespace ws.epigraph.tests",
            "resource users: map[String,Person] { }"
        ),
        lines(
            "namespace ws.epigraph.tests",
            "resource users: map[epigraph.String,ws.epigraph.tests.Person] { }"
        )
    );
  }

  @Test
  public void testResource() throws IOException {
    testParse(
        lines(
            "namespace test",
            "import ws.epigraph.tests.Person",
            "import ws.epigraph.tests.UserRecord",
            "import epigraph.annotations.Doc",
            "resource users : map[String,Person] {",
            "  outputProjection defaultOutput : map[String,Person] = [forbidden](:id)",
            "  read {",
            "    @Doc \"dome doc string\"",
            "    outputProjection {",
            "      ;superUser: UserRecord { default : { id : 1337 }, @Doc \"super user account\" } (id)",
            "    } [required]( :`record` (id, firstName) )",
            "  }",
            "  read readWithPath {",
            "    path / .",
            "    outputProjection :`record` (id, firstName)",
            "  }",
            "  create {",
            "    inputProjection []( :`record` ( firstName, lastName) )",
            "    outputType Boolean",
            "    outputProjection", // empty projection
            "  }",
            "  update {",
            "    @Doc \"dome doc string\"",
            "    inputProjection []( :`record` ( firstName, lastName) )",
            "    outputProjection $defaultOutput",
            "  }",
            "  update customUpdate {",
            "    @Doc \"dome doc string\"",
            "    inputProjection []( :`record` ( firstName, lastName) )",
            "    outputProjection $defaultOutput",
            "  }",
            "  delete {",
            "    deleteProjection [forbidden]+( :`record` ( firstName ) )",
            "    outputType Boolean",
            "  }",
            "  custom customOp {",
            "    method POST",
            "    @Doc \"dome doc string\"",
            "    path / . :`record` / bestFriend",
            "    inputType map[String,Person]",
            "    inputProjection []( :`record` ( firstName, lastName) )",
            "    outputType map[String,Person]",
            "    outputProjection $defaultOutput",
            "  }",
            "}"
        ),

        lines(
            "namespace test",
            "resource users: map[epigraph.String,ws.epigraph.tests.Person] {",
            "  read {",
            "    @epigraph.annotations.Doc \"dome doc string\",",
            "    outputType map[epigraph.String,ws.epigraph.tests.Person],",
            "    outputProjection {",
            "      ;superUser: ws.epigraph.tests.UserRecord",
            "        { @epigraph.annotations.Doc \"super user account\", default: { id: 1337 } } (",
            "          id",
            "        )",
            "    } [ required ]( :`record` ( id, firstName ) )",
            "  }",
            "  read readWithPath {",
            "    path / .,",
            "    outputType ws.epigraph.tests.Person,",
            "    outputProjection :`record` ( id, firstName )",
            "  }",
            "  create {",
            "    inputType map[epigraph.String,ws.epigraph.tests.Person],",
            "    inputProjection [ ]( :`record` ( firstName, lastName ) ),",
            "    outputType epigraph.Boolean",
            "  }",
            "  update {",
            "    @epigraph.annotations.Doc \"dome doc string\",",
            "    inputType map[epigraph.String,ws.epigraph.tests.Person],",
            "    inputProjection [ ]( :`record` ( firstName, lastName ) ),",
            "    outputType map[epigraph.String,ws.epigraph.tests.Person],",
            "    outputProjection $defaultOutput",
            "  }",
            "  update customUpdate {",
            "    @epigraph.annotations.Doc \"dome doc string\",",
            "    inputType map[epigraph.String,ws.epigraph.tests.Person],",
            "    inputProjection [ ]( :`record` ( firstName, lastName ) ),",
            "    outputType map[epigraph.String,ws.epigraph.tests.Person],",
            "    outputProjection $defaultOutput",
            "  }",
            "  delete {",
            "    deleteProjection [ forbidden ]+( :`record` ( +firstName ) ),",
            "    outputType epigraph.Boolean",
            "  }",
            "  custom customOp {",
            "    method POST,",
            "    @epigraph.annotations.Doc \"dome doc string\",",
            "    path / . :`record` / bestFriend,",
            "    inputType map[epigraph.String,ws.epigraph.tests.Person],",
            "    inputProjection [ ]( :`record` ( firstName, lastName ) ),",
            "    outputType map[epigraph.String,ws.epigraph.tests.Person],",
            "    outputProjection $defaultOutput",
            "  }",
            "  outputProjection defaultOutput: map[epigraph.String,ws.epigraph.tests.Person]",
            "    = [ forbidden ]( :id )",
            "}"
        )
    );
  }

  @Test
  public void testProjectionNamespaces() throws IOException {
    ResourcesSchema schema = testParse(
        lines(
            "namespace test",
            "import ws.epigraph.tests.Person",
            "import ws.epigraph.tests.UserRecord",
            "import epigraph.annotations.Doc",
            "resource users : map[String,Person] {",
            "  outputProjection defaultOutput : map[String,Person] = [forbidden](:id)",
            "  read {",
            "    outputProjection [required]( :`record` (id, firstName) )",
            "  }",
            "  create {",
            "    inputProjection $input = []( :`record` ( firstName, lastName) )",
            "    outputType Boolean",
            "    outputProjection", // empty projection
            "  }",
            "  update {",
            "    inputProjection []( :`record` $ur = ( firstName, lastName) )",
            "    outputProjection $defaultOutput",
            "  }",
            "  delete {",
            "    deleteProjection $del = [forbidden]+( :`record` ( firstName ) )",
            "    outputType Boolean",
            "  }",
            "  custom customOp {",
            "    method POST",
            "    inputType map[String,Person]",
            "    inputProjection $input2 = []( :`record` ( firstName, lastName) )",
            "    outputType map[String,Person]",
            "    outputProjection $out = $defaultOutput",
            "  }",
            "}"
        ),

        lines(
            "namespace test",
            "resource users: map[epigraph.String,ws.epigraph.tests.Person] {",
            "  read {",
            "    outputType map[epigraph.String,ws.epigraph.tests.Person],",
            "    outputProjection [ required ]( :`record` ( id, firstName ) )",
            "  }",
            "  create {",
            "    inputType map[epigraph.String,ws.epigraph.tests.Person],",
            "    inputProjection $input = [ ]( :`record` ( firstName, lastName ) ),",
            "    outputType epigraph.Boolean",
            "  }",
            "  update {",
            "    inputType map[epigraph.String,ws.epigraph.tests.Person],",
            "    inputProjection [ ]( :`record` $ur = ( firstName, lastName ) ),",
            "    outputType map[epigraph.String,ws.epigraph.tests.Person],",
            "    outputProjection $defaultOutput",
            "  }",
            "  delete {",
            "    deleteProjection $del = [ forbidden ]+( :`record` ( +firstName ) ),",
            "    outputType epigraph.Boolean",
            "  }",
            "  custom customOp {",
            "    method POST,",
            "    inputType map[epigraph.String,ws.epigraph.tests.Person],",
            "    inputProjection $input2 = [ ]( :`record` ( firstName, lastName ) ),",
            "    outputType map[epigraph.String,ws.epigraph.tests.Person],",
            "    outputProjection $defaultOutput",
            "  }",
            "  outputProjection defaultOutput: map[epigraph.String,ws.epigraph.tests.Person]",
            "    = [ forbidden ]( :id )",
            "}"
        )
    );

    // check operation projection namespaces
    ResourceDeclaration users = schema.resources().get("users");
    for (final OperationDeclaration op : users.operations()) {
      OpFieldProjection outputProjection = op.outputProjection();
      ProjectionReferenceName referenceName = outputProjection.entityProjection().referenceName();
      if (referenceName != null) {
        assertTrue(referenceName.toString(), referenceName.toString().contains("projections.output"));
      }

      OpFieldProjection inputProjection = op.inputProjection();
      if (inputProjection != null && inputProjection.entityProjection().referenceName() != null) {
        referenceName = inputProjection.entityProjection().referenceName();
        assertNotNull(referenceName);

        switch (op.kind()) {
          case CREATE:
            assertTrue(referenceName.toString(), referenceName.toString().contains("projections.input"));
            break;
          case UPDATE:
            assertTrue(referenceName.toString(), referenceName.toString().contains("projections.input"));
            break;
          case DELETE:
            assertTrue(referenceName.toString(), referenceName.toString().contains("projections.delete"));
            break;
          case CUSTOM:
            assertTrue(referenceName.toString(), referenceName.toString().contains("projections.input"));
            break;
          default:
        }
      }
    }
  }

  @Test
  public void testParseSimpleReference() throws IOException {
    testParse(
        lines(
            "namespace ws.epigraph.tests",
            "resource users: map[String,Person] {",
            "  outputProjection out: map[String,Person] = [] (:id)",
            "  read {",
            "    outputProjection $out",
            "  }",
            "}"
        ),
        lines(
            "namespace ws.epigraph.tests",
            "resource users: map[epigraph.String,ws.epigraph.tests.Person] {",
            "  read {",
            "    outputType map[epigraph.String,ws.epigraph.tests.Person],",
            "    outputProjection $out",
            "  }",
            "  outputProjection out: map[epigraph.String,ws.epigraph.tests.Person] =",
            "    [ ]( :id )",
            "}"
        )
    );
  }

  @Test
  public void testParseStupidReference() throws IOException {
    try {
      testParse(
          lines(
              "namespace ws.epigraph.tests",
              "resource users: map[String,Person] {",
              "  outputProjection out: map[String,Person] = $out",
              "  read {",
              "    outputProjection $out",
              "  }",
              "}"
          ),
          lines(
              "namespace ws.epigraph.tests",
              "resource users: map[epigraph.String,ws.epigraph.tests.Person] {",
              "  read {",
              "    outputType map[epigraph.String,ws.epigraph.tests.Person],",
              "    outputProjection $out",
              "  }",
              "  outputProjection out: map[epigraph.String,ws.epigraph.tests.Person] =",
              "    []( :id )",
              "}"
          )
      );
      fail();
    } catch (@SuppressWarnings("ErrorNotRethrown") AssertionError e) {
      assertTrue(e.getMessage().contains("Projection 'out' is not defined"));
    }
  }

  @Test
  public void testParseCrossReference() throws IOException {
    testParse(
        lines(
            "namespace ws.epigraph.tests",
            "resource users: map[String,Person] {",
            "  outputProjection person1: Person = :`record` (id, firstName, bestFriend $person2)",
            "  outputProjection person2: Person = :`record` (id, lastName, bestFriend $person1)",
            "  read {",
            "    outputProjection [] ( $person1 )",
            "  }",
            "}"
        ),
        lines(
            "namespace ws.epigraph.tests",
            "resource users: map[epigraph.String,ws.epigraph.tests.Person] {",
            "  read {",
            "    outputType map[epigraph.String,ws.epigraph.tests.Person],",
            "    outputProjection [ ]( $person1 )",
            "  }",
            "  outputProjection person1: ws.epigraph.tests.Person =",
            "    :`record` ( id, firstName, bestFriend $person2 )",
            "  outputProjection person2: ws.epigraph.tests.Person =",
            "    :`record` ( id, lastName, bestFriend $person1 )",
            "}"
        )
    );
  }

  @Test
  public void testParseStupidCrossReference() throws IOException {
    try {
      testParse(
          lines(
              "namespace ws.epigraph.tests",
              "resource users: map[String,Person] {",
              "  outputProjection person1: Person = $person2",
              "  outputProjection person2: Person = $person1",
              "  read {",
              "    outputProjection [] ( $person1 )",
              "  }",
              "}"
          ),
          lines(
              "namespace ws.epigraph.tests",
              "resource users: map[epigraph.String,ws.epigraph.tests.Person] {",
              "  read {",
              "    outputType map[epigraph.String,ws.epigraph.tests.Person],",
              "    outputProjection []( $person1 )",
              "  }",
              "  outputProjection person1: ws.epigraph.tests.Person =",
              "    :`record` ( id, firstName, bestFriend $person2 )",
              "  outputProjection person2: ws.epigraph.tests.Person =",
              "    :`record` ( id, lastName, bestFriend $person1 )",
              "}"
          )
      );
      fail();
    } catch (@SuppressWarnings("ErrorNotRethrown") AssertionError e) {
      assertTrue(e.getMessage(), e.getMessage().contains("Projection 'person1' is not defined"));
      assertTrue(e.getMessage(), e.getMessage().contains("Projection 'person2' is not defined"));
    }
  }

  @Test
  public void testDoublePreReference() throws IOException {
    testParse(
        lines(
            "namespace ws.epigraph.tests",
            "resource users: map[String,Person] {",
            "  outputProjection prec1: PersonRecord = (firstName, worstEnemy $prec3)",
            "  outputProjection prec2: PersonRecord = (lastName, worstEnemy $prec3)",
            "  outputProjection prec3: PersonRecord = (firstName, lastName)",
            "  read {",
            "    outputProjection [] ( :`record` $prec1 )",
            "  }",
            "  read z {",
            "    outputProjection [] ( :`record` $prec2 )",
            "  }",
            "}"
        ),
        lines(
            "namespace ws.epigraph.tests",
            "resource users: map[epigraph.String,ws.epigraph.tests.Person] {",
            "  read {",
            "    outputType map[epigraph.String,ws.epigraph.tests.Person],",
            "    outputProjection [ ]( :`record` $prec1 )",
            "  }",
            "  read z {",
            "    outputType map[epigraph.String,ws.epigraph.tests.Person],",
            "    outputProjection [ ]( :`record` $prec2 )",
            "  }",
            "  outputProjection prec1: ws.epigraph.tests.PersonRecord =",
            "    ( firstName, worstEnemy $prec3 )",
            "  outputProjection prec2: ws.epigraph.tests.PersonRecord =",
            "    ( lastName, worstEnemy $prec3 )",
            "  outputProjection prec3: ws.epigraph.tests.PersonRecord =",
            "    ( firstName, lastName )",
            "}"
        )
    );
  }

  @Test
  public void testDoublePostReference() throws IOException {
    testParse(
        lines(
            "namespace ws.epigraph.tests",
            "resource users: map[String,Person] {",
            "  read {",
            "    outputProjection [] ( :`record` $prec1 )",
            "  }",
            "  read z {",
            "    outputProjection [] ( :`record` $prec2 )",
            "  }",
            "  outputProjection prec1: PersonRecord = (firstName, worstEnemy $prec3)",
            "  outputProjection prec2: PersonRecord = (lastName, worstEnemy $prec3)",
            "  outputProjection prec3: PersonRecord = (firstName, lastName)",
            "}"
        ),
        lines(
            "namespace ws.epigraph.tests",
            "resource users: map[epigraph.String,ws.epigraph.tests.Person] {",
            "  read {",
            "    outputType map[epigraph.String,ws.epigraph.tests.Person],",
            "    outputProjection [ ]( :`record` $prec1 )",
            "  }",
            "  read z {",
            "    outputType map[epigraph.String,ws.epigraph.tests.Person],",
            "    outputProjection [ ]( :`record` $prec2 )",
            "  }",
            "  outputProjection prec1: ws.epigraph.tests.PersonRecord =",
            "    ( firstName, worstEnemy $prec3 )",
            "  outputProjection prec2: ws.epigraph.tests.PersonRecord =",
            "    ( lastName, worstEnemy $prec3 )",
            "  outputProjection prec3: ws.epigraph.tests.PersonRecord =",
            "    ( firstName, lastName )",
            "}"
        )
    );
  }

  @Test
  public void testDoubleIntraReference() throws IOException {
    testParse(
        lines(
            "namespace ws.epigraph.tests",
            "resource users: map[String,Person] {",
            "  outputProjection prec1: PersonRecord = (firstName, worstEnemy $prec3)",
            "  read {",
            "    outputProjection [] ( :`record` $prec1 )",
            "  }",
            "  outputProjection prec2: PersonRecord = (lastName, worstEnemy $prec3)",
            "  read z {",
            "    outputProjection [] ( :`record` $prec2 )",
            "  }",
            "  outputProjection prec3: PersonRecord = (firstName, lastName)",
            "}"
        ),
        lines(
            "namespace ws.epigraph.tests",
            "resource users: map[epigraph.String,ws.epigraph.tests.Person] {",
            "  read {",
            "    outputType map[epigraph.String,ws.epigraph.tests.Person],",
            "    outputProjection [ ]( :`record` $prec1 )",
            "  }",
            "  read z {",
            "    outputType map[epigraph.String,ws.epigraph.tests.Person],",
            "    outputProjection [ ]( :`record` $prec2 )",
            "  }",
            "  outputProjection prec1: ws.epigraph.tests.PersonRecord =",
            "    ( firstName, worstEnemy $prec3 )",
            "  outputProjection prec2: ws.epigraph.tests.PersonRecord =",
            "    ( lastName, worstEnemy $prec3 )",
            "  outputProjection prec3: ws.epigraph.tests.PersonRecord =",
            "    ( firstName, lastName )",
            "}"
        )
    );
  }

  @Test
  public void testVarModelBackRef() throws IOException {
    // test case for parser failing with "Internal error: different references to projection 'p3'"
    testParse(
        lines(
            "namespace ws.epigraph.tests",
            "outputProjection p1: UserRecord = (worstEnemy $p3)",
            "outputProjection p2: PersonRecord = () ~ (UserRecord $p3)",
            "outputProjection p3: UserRecord = ()"
        ),
        "namespace ws.epigraph.tests"
    );
  }

  @Test
  public void testParseGlobalProjection() throws IOException {
    testParse(
        lines(
            "namespace ws.epigraph.tests",
            "outputProjection op: Person = :id",
            "resource r1: map[String,Person] {" +
            "  read {",
            "    outputProjection [] ( $op )",
            "  }",
            "}",
            "resource r2: map[String,Person] {" +
            "  read {",
            "    outputProjection [] ( $op )",
            "  }",
            "}"
        ),
        lines(
            "namespace ws.epigraph.tests",
            "resource r1: map[epigraph.String,ws.epigraph.tests.Person] {",
            "  read {",
            "    outputType map[epigraph.String,ws.epigraph.tests.Person],",
            "    outputProjection [ ]( $op )",
            "  }",
            "}",
            "resource r2: map[epigraph.String,ws.epigraph.tests.Person] {",
            "  read {",
            "    outputType map[epigraph.String,ws.epigraph.tests.Person],",
            "    outputProjection [ ]( $op )",
            "  }",
            "}",
            "outputProjection op: ws.epigraph.tests.Person = :id"
        )
    );
  }

  @Test
  public void testParseTransformer() throws IOException {
    testParse(
        lines(
            "namespace ws.epigraph.tests",
            "import epigraph.annotations.Doc",
            "transformer t : Person {" +
            "  @Doc \"test transformer\"",
            "  inputProjection :id",
            "  outputProjection :`record`(id,firstName)",
            "}"
        ),
        lines(
            "namespace ws.epigraph.tests",
            "transformer t: ws.epigraph.tests.Person {",
            "  @epigraph.annotations.Doc \"test transformer\",",
            "  inputProjection :id,",
            "  outputProjection :`record` ( id, firstName )",
            "}"
        )
    );
  }

  @Test
  public void testParseTransformerWithGlobalProjection() throws IOException {
    testParse(
        lines(
            "namespace ws.epigraph.tests",
            "import epigraph.annotations.Doc",
            "outputProjection pp : Person = :`record`(id,firstName)",
            "transformer t : Person {" +
            "  @Doc \"test transformer\"",
            "  inputProjection :id",
            "  outputProjection $pp",
            "}"
        ),
        lines(
            "namespace ws.epigraph.tests",
            "transformer t: ws.epigraph.tests.Person {",
            "  @epigraph.annotations.Doc \"test transformer\",",
            "  inputProjection :id,",
            "  outputProjection $pp",
            "}",
            "outputProjection pp: ws.epigraph.tests.Person = :`record` ( id, firstName )"
        )
    );
  }

  private ResourcesSchema testParse(String idlStr, String expected) {
    ResourcesSchema schema = parseSchema(idlStr, resolver);
    if (expected != null)
      assertEquals(expected, printSchema(schema));
    return schema;
  }

  private static @NotNull ResourcesSchema parseSchema(@NotNull String text, @NotNull TypesResolver resolver) {
    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();

    @NotNull SchemaFile psiFile =
        (SchemaFile) EpigraphPsiUtil.parseFile(
            "test.epigraph",
            text,
            SchemaParserDefinition.INSTANCE,
            errorsAccumulator
        );

    failIfHasErrors(psiFile, errorsAccumulator);

//    String psiDump = DebugUtil.psiToString(psiFile, true, false).trim();
//    System.out.println(psiDump);

    return runPsiParser(true, context -> {
      SchemasPsiProcessingContext schemasPsiProcessingContext = new SchemasPsiProcessingContext();
      ResourcesSchema schema = ResourcesSchemaPsiParser.parseResourcesSchema(
          psiFile,
          resolver,
          schemasPsiProcessingContext
      );
      schemasPsiProcessingContext.ensureAllReferencesResolved();
      context.setMessages(schemasPsiProcessingContext.messages());
      return schema;
    });
  }

}
