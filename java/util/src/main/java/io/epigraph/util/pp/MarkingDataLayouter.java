package io.epigraph.util.pp;

import de.uka.ilkd.pp.Backend;
import de.uka.ilkd.pp.DataLayouter;
import de.uka.ilkd.pp.NoExceptions;
import de.uka.ilkd.pp.PrettyPrintable;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class MarkingDataLayouter extends DataLayouter<NoExceptions> {
  private MarkingStringBackend back;

  /**
   * Construts a newly allocated DataLayouter which will send output to
   * the given {@link Backend} and has the given default indentation.
   *
   * @param back        the Backend
   * @param indentation the default indentation
   */
  public MarkingDataLayouter(MarkingStringBackend back, int indentation) {
    super(back, indentation);
    this.back = back;
  }

  @Override
  public DataLayouter<NoExceptions> print(Object o) throws NoExceptions {
    if (o instanceof PrettyPrintable) {
      MarkingStringBackend.Mark mark = back.getMark(o);
      if (mark != null) {
        return super.print(mark);
      } else {
        back.mark(o);
      }
    }
    return super.print(o);
  }
}
