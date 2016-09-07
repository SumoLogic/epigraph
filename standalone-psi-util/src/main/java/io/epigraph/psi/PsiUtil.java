package io.epigraph.psi;

import com.intellij.lang.ASTNode;
import com.intellij.lang.LanguageParserDefinitions;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lang.impl.PsiBuilderImpl;
import com.intellij.lexer.Lexer;
import com.intellij.mock.MockProjectEx;
import com.intellij.mock.MockPsiManager;
import com.intellij.openapi.util.Trinity;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import com.intellij.psi.impl.PsiFileFactoryImpl;
import com.intellij.psi.impl.source.CharTableImpl;
import com.intellij.psi.tree.IElementType;
import org.intellij.grammar.LightPsi;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class PsiUtil {
  @NotNull
  public static <T extends PsiElement> T parseText(
      @NotNull String text,
      @NotNull IElementType rootElementType,
      @NotNull Class<T> rootElementClass,
      @NotNull ParserDefinition parserDefinition,
      @Nullable ErrorProcessor errorProcessor) {

    final Trinity<MockProjectEx, MockPsiManager, PsiFileFactoryImpl>
        model = LightPsi.Init.initPsiFileFactory(() -> { });

    LanguageParserDefinitions.INSTANCE.addExplicitExtension(
        parserDefinition.getFileNodeType().getLanguage(),
        parserDefinition
    );

    PsiParser parser = parserDefinition.createParser(model.first);
    Lexer lexer = parserDefinition.createLexer(model.first);
    PsiBuilderImpl psiBuilder = new PsiBuilderImpl(
        model.first,
        null,
        parserDefinition,
        lexer,
        new CharTableImpl(),
        text,
        null,
        null
    );

    ASTNode astNode = parser.parse(rootElementType, psiBuilder);
    T res = astNode.getPsi(rootElementClass);
    collectErrors(res, errorProcessor);

    return res;
  }

  @NotNull
  public static PsiFile parseFile(
      @NotNull File file,
      @NotNull ParserDefinition parserDefinition,
      @Nullable ErrorProcessor errorProcessor) throws IOException {

    PsiFile res = LightPsi.parseFile(file, parserDefinition);
    collectErrors(res, errorProcessor);

    return res;
  }

  @NotNull
  public static PsiFile parseFile(
      @NotNull String name,
      @NotNull String text,
      @NotNull ParserDefinition parserDefinition,
      @Nullable ErrorProcessor errorProcessor) throws IOException {

    PsiFile res = LightPsi.parseFile(name, text, parserDefinition);
    collectErrors(res, errorProcessor);

    return res;
  }

  private static void collectErrors(@NotNull PsiElement element,
                                    @Nullable final PsiUtil.ErrorProcessor errorProcessor) {
    if (errorProcessor != null) {
      element.accept(new PsiRecursiveElementWalkingVisitor() {
        @Override
        public void visitErrorElement(PsiErrorElement element) {
          errorProcessor.process(element);
        }
      });
    }
  }

  public static Location getLocation(@NotNull PsiElement element, @Nullable String text) {
    if (text == null) text = element.getContainingFile().getText();
    int offset = element.getTextRange().getStartOffset();
    // todo port LineNumberUtil to Java
    int line = text.substring(0, offset).split("\r\n|\r|\n").length;
    return new Location(line, -1);
  }

  public static class Location {
    public final int line;
    public final int column;

    public Location(int line, int column) {
      this.line = line;
      this.column = column;
    }

    @Override
    public String toString() {
      if (column == -1) return "line " + line;
      else return String.format("(%d:%d)", line, column);
    }
  }

  public interface ErrorProcessor {
    void process(PsiErrorElement error);
  }

  public static class ErrorsAccumulator implements ErrorProcessor {
    private final List<PsiErrorElement> errors = new ArrayList<>();

    @Override
    public void process(PsiErrorElement error) { errors.add(error); }

    public List<PsiErrorElement> errors() { return errors; }

    public boolean hasErrors() { return !errors.isEmpty(); }
  }

}
