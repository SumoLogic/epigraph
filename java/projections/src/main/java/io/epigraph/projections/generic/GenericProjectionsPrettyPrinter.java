package io.epigraph.projections.generic;

import de.uka.ilkd.pp.Layouter;
import io.epigraph.gdata.GDataPrettyPrinter;
import io.epigraph.printers.DataPrinter;
import io.epigraph.projections.op.OpCustomParam;
import io.epigraph.projections.op.OpCustomParams;
import io.epigraph.types.TypeKind;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Stack;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class GenericProjectionsPrettyPrinter<
    S extends GenericVarProjection<T, S>,
    T extends GenericTagProjection<MP>,
    MP extends GenericModelProjection<?>,
    E extends Exception> {

  @NotNull
  protected final Layouter<E> l;
  @NotNull
  protected DataPrinter<E> dataPrinter;
  @NotNull
  protected GDataPrettyPrinter<E> gdataPrettyPrinter;

  private int nextRefNumber = 1;
  private Map<GenericVarProjection<T, S>, Integer> varRefs = new HashMap<>();
  private Stack<GenericVarProjection<T, S>> varsStack = new Stack<>();

  protected GenericProjectionsPrettyPrinter(@NotNull Layouter<E> layouter) {
    l = layouter;
    dataPrinter = new DataPrinter<>(l);
    gdataPrettyPrinter = new GDataPrettyPrinter<>(l);
  }

  public void print(@NotNull GenericVarProjection<T, S> p) throws E {
    if (varsStack.contains(p)) {
      int ref = nextRefNumber++;
      varRefs.put(p, ref);
      l.print("... = @").print(Integer.toString(ref));
      return;
    }

    try {
      varsStack.push(p);

      LinkedHashSet<T> tagProjections = p.tagProjections();

      if (p.type().kind() != TypeKind.UNION) {
        // samovar
        print(tagProjections.iterator().next().projection());
      } else if (tagProjections.size() == 1) {
        T tagProjection = tagProjections.iterator().next();
        l.print(":");
        print(tagProjection);
      } else if (tagProjections.isEmpty()) {
        l.print(":()");
      } else {
        l.beginCInd();
        l.print(":(");
        boolean first = true;
        for (T tagProjection : tagProjections) {
          if (first) first = false;
          else l.print(",");
          l.brk();
          print(tagProjection);
        }
        l.brk(1, -l.getDefaultIndentation()).end().print(")");
      }

      LinkedHashSet<S> polymorphicTails = p.polymorphicTails();

      if (polymorphicTails != null && !polymorphicTails.isEmpty()) {
        l.beginIInd();
        l.brk();
        if (polymorphicTails.size() == 1) {
          l.print("~");
          S tail = polymorphicTails.iterator().next();
          l.print(tail.type().name().toString());
          l.brk();
          print(tail);
        } else {
          l.beginCInd();
          l.print("~(");
          boolean first = true;
          for (GenericVarProjection<T, S> tail : polymorphicTails) {
            if (first) first = false;
            else l.print(",");
            l.brk().print(tail.type().name().toString()).brk();
            print(tail);
          }
          l.brk(1, -l.getDefaultIndentation()).end().print(")");
        }
        l.end();
      }
    } finally {
      varsStack.pop();
      Integer ref = varRefs.remove(p);
      if (ref != null) l.print("@").print(Integer.toString(ref));
    }

  }

  public abstract void print(@NotNull T tp) throws E;

  public abstract void print(@NotNull MP mp) throws E;

  protected void print(@NotNull OpCustomParams cp) throws E {
    l.beginCInd(0);
    for (Map.Entry<String, OpCustomParam> entry : cp.params().entrySet()) {
      l.brk().print(entry.getKey()).brk().print("=").brk();
      gdataPrettyPrinter.print(entry.getValue().value());
    }
    l.end();
  }

  protected boolean isPrintoutEmpty(@NotNull GenericVarProjection<T, S> vp) {
    LinkedHashSet<S> tails = vp.polymorphicTails();
    if (tails != null && !tails.isEmpty()) return false;
    if (vp.type().kind() == TypeKind.UNION) return false; // non-samovar always prints something

    for (T tagProjection : vp.tagProjections()) {
      if (!isPrintoutEmpty(tagProjection.projection())) return false;
    }

    return true;
  }

  public abstract boolean isPrintoutEmpty(@NotNull MP mp);
}
