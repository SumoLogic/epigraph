package io.epigraph.idl;

import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.impl.DebugUtil;
import de.uka.ilkd.pp.Layouter;
import de.uka.ilkd.pp.NoExceptions;
import de.uka.ilkd.pp.StringBackend;
import io.epigraph.idl.parser.IdlParserDefinition;
import io.epigraph.idl.parser.psi.IdlFile;
import io.epigraph.lang.TextLocation;
import io.epigraph.psi.EpigraphPsiUtil;
import io.epigraph.psi.PsiProcessingException;
import io.epigraph.refs.SimpleTypesResolver;
import io.epigraph.refs.TypesResolver;
import io.epigraph.tests.*;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class IdlTest {
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
            "resource users : map[String,Person] {",
            "  READ {",
            "    doc = \"dome doc string\"",
            "    ; authToken : String",
            "    outputProjection [required]( :record (id, firstName) )",
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
            "    ; authToken : String",
            "    inputProjection []( :record ( firstName, lastName) )",
            "    outputProjection [forbidden]( :id )",
            "  }",
            "  customUpdate UPDATE {",
            "    doc = \"dome doc string\"",
            "    ; authToken : String",
            "    inputProjection []( :record ( firstName, lastName) )",
            "    outputProjection [forbidden]( :id )",
            "  }",
            "  DELETE {",
            "    deleteProjection [forbidden]( +:record ( firstName ) )",
            "    outputType Boolean",
            "    outputProjection", // empty projection
            "  }",
            "  customOp CUSTOM {",
            "    doc = \"dome doc string\"",
            "    ; authToken : String",
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
            "    ;authToken: epigraph.String,",
            "    doc = \"dome doc string\",",
            "    outputType map[epigraph.String,io.epigraph.tests.Person],",
            "    outputProjection [ required ]( :record ( id, firstName ) )",
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
            "    outputType epigraph.Boolean,",
            "    outputProjection ", // empty projection
            "  }",
            "  UPDATE",
            "  {",
            "    ;authToken: epigraph.String,",
            "    doc = \"dome doc string\",",
            "    inputType map[epigraph.String,io.epigraph.tests.Person],",
            "    inputProjection []( :record ( firstName, lastName ) ),",
            "    outputType map[epigraph.String,io.epigraph.tests.Person],",
            "    outputProjection [ forbidden ]( :id )",
            "  }",
            "  customUpdate UPDATE",
            "  {",
            "    ;authToken: epigraph.String,",
            "    doc = \"dome doc string\",",
            "    inputType map[epigraph.String,io.epigraph.tests.Person],",
            "    inputProjection []( :record ( firstName, lastName ) ),",
            "    outputType map[epigraph.String,io.epigraph.tests.Person],",
            "    outputProjection [ forbidden ]( :id )",
            "  }",
            "  DELETE",
            "  {",
            "    deleteProjection [ forbidden ]( +:record ( firstName ) ),",
            "    outputType epigraph.Boolean,",
            "    outputProjection ", // empty projection
            "  }",
            "  customOp CUSTOM",
            "  {",
            "    ;authToken: epigraph.String,",
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

    try {
      return IdlPsiParser.parseIdl(psiFile, resolver);
    } catch (PsiProcessingException e) {
      e.printStackTrace();
      @NotNull final TextLocation location = e.location();
      System.err.println(e.getMessage() + " at " + location);

      System.err.print(text.substring(location.startOffset()));
      System.err.print(">>>");
      System.err.print(text.substring(location.startOffset(), location.endOffset()));
      System.err.println("<<<");
      fail();
    }

    throw new RuntimeException("Unreachable");
  }

  private static String lines(String... lines) { return Arrays.stream(lines).collect(Collectors.joining("\n")); }
}
