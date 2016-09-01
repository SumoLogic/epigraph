package io.epigraph.util.pp;

import de.uka.ilkd.pp.StringBackend;
import org.jetbrains.annotations.Nullable;

import java.util.IdentityHashMap;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class MarkingStringBackend extends StringBackend {
  private IdentityHashMap<Object, Mark> marks = new IdentityHashMap<>();

  private int currentLineNo = 1;
  private int currentColumnNo = 1;

  public MarkingStringBackend(StringBuilder sb, int lineWidth) {
    super(sb, lineWidth);
  }

  public MarkingStringBackend(int lineWidth) {
    super(lineWidth);
  }

  @Override
  public void print(String s) {
    super.print(s);
    currentColumnNo += s.length();
  }

  @Override
  public void newLine() {
    super.newLine();
    currentLineNo++;
    currentColumnNo = 1;
  }

  @Override
  public void mark(Object o) {
    marks.put(o, new Mark());
  }

  @Nullable
  public Mark getMark(Object o) {
    return marks.get(o);
  }

  public class Mark {
    public final int line;
    public final int column;

    private Mark() {
      line = currentLineNo;
      column = currentColumnNo;
    }

    @Override
    public String toString() {
      return "@(" + line + ":" + column + ")";
    }
  }
}
