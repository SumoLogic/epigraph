package io.epigraph.idl.parser.projections;

import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.openapi.project.Project;
import com.intellij.psi.tree.IElementType;
import io.epigraph.idl.parser.IdlParserDefinition;
import io.epigraph.idl.parser.IdlSubParser;

import static io.epigraph.idl.lexer.IdlElementTypes.*;

import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ProjectionParserDefinitions {
  @NotNull
  public static final ParserDefinition OP_OUTPUT_VAR_PROJECTION =
      new ProjectionParserDefinition(I_OP_OUTPUT_VAR_PROJECTION);

  public static class ProjectionParserDefinition extends IdlParserDefinition {

    @NotNull
    private final IElementType entryElementType;

    private ProjectionParserDefinition(@NotNull IElementType entryElementType) {
      this.entryElementType = entryElementType;
    }

    @Override
    public PsiParser createParser(Project project) {
      return new IdlSubParser(entryElementType);
    }
  }
}
