package io.epigraph.idl.parser.projections;

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
public class IdlSubParserDefinitions {
  @NotNull
  public static final IdlSubParserDefinitions.IdlSubParserDefinition OP_OUTPUT_VAR_PROJECTION =
      new IdlSubParserDefinition(I_OP_OUTPUT_VAR_PROJECTION);
  @NotNull
  public static final IdlSubParserDefinitions.IdlSubParserDefinition OP_INPUT_VAR_PROJECTION =
      new IdlSubParserDefinition(I_OP_INPUT_MODEL_PROJECTION);
  @NotNull
  public static final IdlSubParserDefinitions.IdlSubParserDefinition DATA_VALUE =
      new IdlSubParserDefinition(I_DATA_VALUE);

  public static class IdlSubParserDefinition extends IdlParserDefinition {

    @NotNull
    private final IElementType rootElementType;

    private IdlSubParserDefinition(@NotNull IElementType rootElementType) {
      this.rootElementType = rootElementType;
    }

    @Override
    public PsiParser createParser(Project project) {
      return new IdlSubParser(rootElementType);
    }

    @NotNull
    public IElementType rootElementType() {
      return rootElementType;
    }
  }
}
