package io.epigraph.psi;

import com.intellij.lang.ASTNode;
import com.intellij.lang.LanguageParserDefinitions;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lang.impl.PsiBuilderImpl;
import com.intellij.lexer.Lexer;
import com.intellij.mock.MockProjectEx;
import com.intellij.mock.MockPsiManager;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.Trinity;
import com.intellij.psi.*;
import com.intellij.psi.impl.PsiFileFactoryImpl;
import com.intellij.psi.impl.source.CharTableImpl;
import com.intellij.psi.tree.IElementType;
import io.epigraph.lang.TextLocation;
import org.intellij.grammar.LightPsi;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class EpigraphPsiUtil {
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

  public static @NotNull PsiFile parseResource(
      @NotNull String resourcePath, // absolute if starts with '/', package-prefixed otherwise
      @NotNull ParserDefinition parserDefinition,
      @Nullable ErrorProcessor errorProcessor
  ) throws IOException {
    InputStream is = Object.class.getResourceAsStream(resourcePath); // TODO use EpigraphPsiUtil.class?
    if (is == null) throw new IOException("Couldn't find '" + resourcePath + "' resource");
    PsiFile res = LightPsi.parseFile(resourcePath, inputStreamToString(is, StandardCharsets.UTF_8), parserDefinition);
    collectErrors(res, errorProcessor);
    return res;
  }

  private static String inputStreamToString(@NotNull InputStream is, @NotNull Charset charset) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    byte[] buffer = new byte[1024];
    int count;
    try {
      while ((count = is.read(buffer)) != -1) baos.write(buffer, 0, count);
      return baos.toString(charset.name());
    } finally { is.close(); }
  }

  @NotNull
  public static TextLocation getLocation(@NotNull PsiElement psi) {
    TextRange textRange = psi.getTextRange();
    PsiFile psiFile = null;
    try {
      psiFile = psi.getContainingFile();
    } catch (PsiInvalidElementAccessException ignored) {
      // any way to do this without try-catch?
    }

    // try to find the topmost psi element describing current file/contents
    final PsiElement megaParent;
    if (psiFile != null) megaParent = psiFile;
    else {
      PsiElement parent = psi;
      while (parent.getParent() != null) {
        if (parent instanceof PsiFile) break;
        if (parent.getParent() instanceof PsiDirectory) break;
        parent = parent.getParent();
      }
      megaParent = parent;
    }

    return new TextLocation(
        textRange.getStartOffset(),
        textRange.getEndOffset(),
        psiFile == null ? null : psiFile.getName(),
        megaParent.getText()
    );
  }

  public static void collectErrors(
      @NotNull PsiElement element,
      @Nullable final EpigraphPsiUtil.ErrorProcessor errorProcessor) {
    if (errorProcessor != null) {
      element.accept(new PsiRecursiveElementWalkingVisitor() {
        @Override
        public void visitErrorElement(PsiErrorElement element) {
          errorProcessor.process(element);
        }
      });
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
