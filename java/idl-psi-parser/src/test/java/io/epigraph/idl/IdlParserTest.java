package io.epigraph.idl;

import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.impl.DebugUtil;
import de.uka.ilkd.pp.Layouter;
import de.uka.ilkd.pp.NoExceptions;
import de.uka.ilkd.pp.StringBackend;
import io.epigraph.idl.parser.IdlParserDefinition;
import io.epigraph.idl.parser.psi.IdlFile;
import io.epigraph.psi.EpigraphPsiUtil;
import io.epigraph.psi.PsiProcessingError;
import io.epigraph.psi.PsiProcessingException;
import io.epigraph.refs.SimpleTypesResolver;
import io.epigraph.refs.TypesResolver;
import io.epigraph.tests.*;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class IdlParserTest {
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

  @Test
  public void testEmpty() throws Exception {
    testParse(
        lines(
            "namespace test",
            "import io.epigraph.tests.Person"
        ),
        "namespace test"
    );
  }

  @Test
  public void testEmptyResource() throws Exception {
    testParse(
        lines(
            "namespace io.epigraph.tests",
            "resource users: map[String,Person] { }"
        ),
        lines(
            "namespace io.epigraph.tests",
            "resource users: map[epigraph.String,io.epigraph.tests.Person] { }"
        )
    );
  }

  @Test
  public void testResource() throws Exception {
    testParse(
        lines(
            "namespace test",
            "import io.epigraph.tests.Person",
            "import io.epigraph.tests.UserRecord",
            "resource users : map[String,Person] {",
            "  READ {",
            "    doc = \"dome doc string\"",
            "    outputProjection {",
            "      ;superUser: UserRecord (id) = { id : 1337 } {",
            "        doc = \"super user account\"",
            "      }",
            "    } [required]( :record (id, firstName) )",
            "  }",
            "  readWithPath READ {",
            "    path / .",
            "    outputProjection :record (id, firstName)",
            "  }",
            "  CREATE {",
            "    inputProjection []( :record ( firstName, lastName) )",
            "    outputType Boolean",
            "    outputProjection", // empty projection
            "  }",
            "  UPDATE {",
            "    doc = \"dome doc string\"",
            "    inputProjection []( :record ( firstName, lastName) )",
            "    outputProjection [forbidden]( :id )",
            "  }",
            "  customUpdate UPDATE {",
            "    doc = \"dome doc string\"",
            "    inputProjection []( :record ( firstName, lastName) )",
            "    outputProjection [forbidden]( :id )",
            "  }",
            "  DELETE {",
            "    deleteProjection [forbidden]( +:record ( firstName ) )",
            "    outputType Boolean",
            "  }",
            "  customOp CUSTOM {",
            "    method POST",
            "    doc = \"dome doc string\"",
            "    path / . :record / bestFriend",
            "    inputType map[String,Person]",
            "    inputProjection []( :record ( firstName, lastName) )",
            "    outputType map[String,Person]",
            "    outputProjection [forbidden]( :id )",
            "  }",
            "}"
        ),
        lines(
            "namespace test",
            "resource users: map[epigraph.String,io.epigraph.tests.Person]",
            "{",
            "  READ",
            "  {",
            "    doc = \"dome doc string\",",
            "    outputType map[epigraph.String,io.epigraph.tests.Person],",
            "    outputProjection {",
            "      ;superUser: io.epigraph.tests.UserRecord ( id ) =",
            "        { id: 1337 } { doc = \"super user account\" }",
            "    } [ required ]( :record ( id, firstName ) )",
            "  }",
            "  readWithPath READ",
            "  {",
            "    path / .,",
            "    outputType io.epigraph.tests.Person,",
            "    outputProjection :record ( id, firstName )",
            "  }",
            "  CREATE",
            "  {",
            "    inputType map[epigraph.String,io.epigraph.tests.Person],",
            "    inputProjection []( :record ( firstName, lastName ) ),",
            "    outputType epigraph.Boolean",
            "  }",
            "  UPDATE",
            "  {",
            "    doc = \"dome doc string\",",
            "    inputType map[epigraph.String,io.epigraph.tests.Person],",
            "    inputProjection []( :record ( firstName, lastName ) ),",
            "    outputType map[epigraph.String,io.epigraph.tests.Person],",
            "    outputProjection [ forbidden ]( :id )",
            "  }",
            "  customUpdate UPDATE",
            "  {",
            "    doc = \"dome doc string\",",
            "    inputType map[epigraph.String,io.epigraph.tests.Person],",
            "    inputProjection []( :record ( firstName, lastName ) ),",
            "    outputType map[epigraph.String,io.epigraph.tests.Person],",
            "    outputProjection [ forbidden ]( :id )",
            "  }",
            "  DELETE",
            "  {",
            "    deleteProjection [ forbidden ]( +:record ( firstName ) ),",
            "    outputType epigraph.Boolean",
            "  }",
            "  customOp CUSTOM",
            "  {",
            "    method POST,",
            "    doc = \"dome doc string\",",
            "    path / . :record / bestFriend,",
            "    inputType map[epigraph.String,io.epigraph.tests.Person],",
            "    inputProjection []( :record ( firstName, lastName ) ),",
            "    outputType map[epigraph.String,io.epigraph.tests.Person],",
            "    outputProjection [ forbidden ]( :id )",
            "  } }"
        )
    );
  }

  private void testParse(String str) throws IOException {
    testParse(str, str);
  }

  private void testParse(String idlStr, String expected) throws IOException {
    Idl idl = parseFile(idlStr);

    StringBackend sb = new StringBackend(80);
    Layouter<NoExceptions> l = new Layouter<>(sb, 2);
    IdlPrettyPrinter<NoExceptions> pp = new IdlPrettyPrinter<>(l);
    pp.print(idl);
    l.close();

    String s = sb.getString();
    assertEquals(expected, s);
  }

  private Idl parseFile(String text) throws IOException {
    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();

    @NotNull IdlFile psiFile =
        (IdlFile) EpigraphPsiUtil.parseFile("idlTest.idl", text, IdlParserDefinition.INSTANCE, errorsAccumulator);

    if (errorsAccumulator.hasErrors()) {
      for (PsiErrorElement element : errorsAccumulator.errors()) {
        System.err.println(element.getErrorDescription() + " at " + EpigraphPsiUtil.getLocation(element));
      }
      fail(DebugUtil.psiTreeToString(psiFile, true));
    }

    List<PsiProcessingError> errors = new ArrayList<>();
    Idl idl = null;

    try {
      idl = IdlPsiParser.parseIdl(psiFile, resolver, errors);
    } catch (PsiProcessingException e) {
      errors = e.errors();
    }

    if (!errors.isEmpty()) {
      for (final PsiProcessingError error : errors) {
        System.err.print(error.message() + " at " + error.location());
      }
      fail();
    }

    return idl;
  }

  private static String lines(String... lines) { return Arrays.stream(lines).collect(Collectors.joining("\n")); }
}
