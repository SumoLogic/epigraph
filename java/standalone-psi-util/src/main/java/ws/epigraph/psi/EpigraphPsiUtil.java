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

package ws.epigraph.psi;

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
import ws.epigraph.lang.TextLocation;
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
public final class EpigraphPsiUtil {
  private EpigraphPsiUtil() {}

  public static @NotNull <T extends PsiElement> T parseText(
      @NotNull String text,
      @NotNull SubParserDefinition<T> parserDefinition,
      @Nullable ErrorProcessor errorProcessor) {

    return parseText(
        text,
        parserDefinition.rootElementType(),
        parserDefinition.rootElementClass(),
        parserDefinition,
        errorProcessor
    );
  }

  public static @NotNull <T extends PsiElement> T parseText(
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

  public static @NotNull PsiFile parseFile(
      @NotNull File file,
      @NotNull ParserDefinition parserDefinition,
      @Nullable ErrorProcessor errorProcessor) throws IOException {

    PsiFile res = LightPsi.parseFile(file, parserDefinition);
    collectErrors(res, errorProcessor);

    return res;
  }

  public static @NotNull PsiFile parseFile(
      @NotNull String name,
      @NotNull String text,
      @NotNull ParserDefinition parserDefinition,
      @Nullable ErrorProcessor errorProcessor) {

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

  public static @NotNull TextLocation getLocation(@NotNull PsiElement psi) {
    try {
      TextRange textRange = psi.getTextRange();
      PsiFile psiFile = getPsiFile(psi);
      final PsiElement megaParent = findTopmostParent(psi);

      return new TextLocation(
          textRange.getStartOffset(),
          textRange.getEndOffset(),
          psiFile == null ? null : psiFile.getName(),
          megaParent.getText()
      );
    } catch (PsiInvalidElementAccessException ignored) {
      return TextLocation.UNKNOWN; // thrown in unit tests, when using NULL_PSI_ELEMENT
    }
  }

  public static @NotNull PsiElement findTopmostParent(final @NotNull PsiElement psi) {
    PsiFile psiFile = getPsiFile(psi);

    final PsiElement megaParent;
    if (psiFile == null) {
      PsiElement parent = psi;
      while (parent.getParent() != null) {
        if (parent instanceof PsiFile) break;
        if (parent.getParent() instanceof PsiDirectory) break;
        parent = parent.getParent();
      }
      megaParent = parent;
    } else megaParent = psiFile;
    return megaParent;

  }

  private static @Nullable PsiFile getPsiFile(final @NotNull PsiElement psi) {
    PsiFile psiFile = null;
    try {
      psiFile = psi.getContainingFile();
    } catch (PsiInvalidElementAccessException ignored) {
      // any way to do this without try-catch?
    }
    return psiFile;
  }

  public static void collectErrors(
      @NotNull PsiElement element,
      final @Nullable EpigraphPsiUtil.ErrorProcessor errorProcessor) {
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
