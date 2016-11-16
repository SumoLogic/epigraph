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

package ws.epigraph.lang;

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
    // todo capture columns if text doesn't contain tabs and special symbols
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
    return text.substring(0, offset).split("\r\n|\r|\n").length; // todo test
  }

  @Override
  public String toString() {
    String file = fileName() == null ? "<unknown>" : "file '" + fileName() + "'";
    String lines = startLine() == endLine() ?
                   "line " + startLine() :
                   "lines " + startLine() + "-" + endLine();
    String offset = startOffset() == endOffset() ?
                    "offset " + startOffset() :
                    "offset range " + startOffset() + "-" + endOffset();

    return String.format("%s %s (%s)", file, lines, offset);
  }
}
