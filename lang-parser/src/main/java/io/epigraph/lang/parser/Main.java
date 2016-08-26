package io.epigraph.lang.parser;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ParserDefinition;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.DebugUtil;
import com.intellij.psi.search.PsiElementProcessor;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.psi.util.PsiTreeUtil;
import org.intellij.grammar.LightPsi;

import java.io.File;
import java.io.IOException;

/**
 * Mostly used to build light-psi/resources/light-psi-filelist.txt
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class Main {
  private static final String DEFAULT_SOURCE = "" +
      "namespace foo " +
      "record Bar";

  public static void main(String[] args) throws ClassNotFoundException {
    String source = args.length == 0 ? DEFAULT_SOURCE : args[0];
    PsiFile psi = LightPsi.parseFile("dummy", source, new SchemaParserDefinition());
    String psiDump = DebugUtil.psiToString(psi, true, false).trim();
    System.out.println(psiDump);

    // load file parsing related classes
    try {
      LightPsi.parseFile(new File("."), new SchemaParserDefinition());
    } catch (IOException e) {
//      e.printStackTrace();
    }

    // force some class loading
    @SuppressWarnings({"unused", "MismatchedReadAndWriteOfArray"})
    Class<?>[] classes = new Class[]{
        PsiTreeUtil.class,
        StubBasedPsiElementBase.class,
        StubBase.class,
        StubOutputStream.class,
        StubInputStream.class,
        IndexSink.class,
        ASTWrapperPsiElement.class,
        PsiElementProcessor.class,
        ParserDefinition.SpaceRequirements.class,
        PsiDirectory.class,
    };

    String[] extraClasses = new String[]{
        "com.intellij.lang.parser.GeneratedParserUtilBase$CompletionState",
        "com.intellij.lang.parser.GeneratedParserUtilBase$Hooks",
        "com.intellij.lang.parser.GeneratedParserUtilBase$DummyBlock",
        "com.intellij.psi.impl.source.SubstrateRef$StubRef",
        "com.intellij.extapi.psi.StubBasedPsiElementBase$1",
    };

    for (String extraClass : extraClasses) {
      Class.forName(extraClass);
    }

  }
}
