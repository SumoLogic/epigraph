package io.epigraph.lang;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class TextLocation {
  public static final int UNKNOWN_OFFSET = -1;
  public static final TextLocation UNKNOWN = new TextLocation(UNKNOWN_OFFSET, UNKNOWN_OFFSET, null);

  private final int startOffset;
  private final int endOffset;
  @Nullable
  private final String fileName;

  public TextLocation(int startOffset, int endOffset, @Nullable String fileName) {
    this.startOffset = startOffset;
    this.endOffset = endOffset;
    this.fileName = fileName;
  }

  public int startOffset() {
    return startOffset;
  }

  public int endOffset() {
    return endOffset;
  }

  @Nullable
  public String fileName() {
    return fileName;
  }

  // todo helper methods to convert offsets to lines/columns
  // todo port LineNumberUtil to Java

  public static int line(@NotNull String text, int offset) {
    return text.substring(0, offset).split("\r\n|\r|\n").length;
  }

  @Override
  public String toString() {
    return String.format("%s@(%d-%d)", fileName(), startOffset(), endOffset());
  }
}
