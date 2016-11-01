package io.epigraph.lang;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class TextLocation {
  public static final int UNKNOWN_POSITION = -1;

  public static final TextLocation UNKNOWN =
      new TextLocation(UNKNOWN_POSITION, UNKNOWN_POSITION, UNKNOWN_POSITION, UNKNOWN_POSITION, null);

  private final int startOffset;
  private final int endOffset;

  // have columns too? they depend on tab width..
  private final int startLine;
  private final int endLine;

  @Nullable
  private final String fileName;

  public TextLocation(int startOffset, int startLine, int endOffset, int endLine, @Nullable String fileName) {
    this.startOffset = startOffset;
    this.startLine = startLine;
    this.endOffset = endOffset;
    this.endLine = endLine;
    this.fileName = fileName;
  }

  public TextLocation(int startOffset, int endOffset, @Nullable String fileName, @NotNull String text) {
    this(
        startOffset,
        line(text, startOffset),
        endOffset,
        line(text, endOffset),
        fileName
    );
  }

  public int startOffset() { return startOffset; }

  public int endOffset() { return endOffset; }

  public int startLine() { return startLine; }

  public int endLine() { return endLine; }

  @Nullable
  public String fileName() { return fileName; }

  // todo helper methods to convert offsets to lines/columns
  // todo port LineNumberUtil to Java

  private static int line(@NotNull String text, int offset) {
    return text.substring(0, offset).split("\r\n|\r|\n").length;
  }

  @Override
  public String toString() {
    String file = fileName() == null ? "unknown" : fileName();
    return String.format(
        "file '%s' lines %d:%d (offset %d:%d)",
        file,
        startLine(),
        endLine(),
        startOffset(),
        endOffset()
    );
  }
}
