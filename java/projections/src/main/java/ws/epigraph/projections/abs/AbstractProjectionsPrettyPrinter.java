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

package ws.epigraph.projections.abs;

import de.uka.ilkd.pp.Layouter;
import ws.epigraph.gdata.GDataPrettyPrinter;
import ws.epigraph.lang.Keywords;
import ws.epigraph.printers.DataPrinter;
import ws.epigraph.projections.Annotation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.gen.GenModelProjection;
import ws.epigraph.projections.gen.GenTagProjectionEntry;
import ws.epigraph.projections.gen.GenVarProjection;
import ws.epigraph.types.TypeKind;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class AbstractProjectionsPrettyPrinter<
    VP extends GenVarProjection<VP, TP, MP>,
    TP extends GenTagProjectionEntry<TP, MP>,
    MP extends GenModelProjection</*MP*/?, ?>,
    E extends Exception> {

  protected final @NotNull Layouter<E> l;
  protected @NotNull DataPrinter<E> dataPrinter;
  protected @NotNull GDataPrettyPrinter<E> gdataPrettyPrinter;

  private int nextRefNumber = 1;
  private final Map<VP, Integer> varRefs = new HashMap<>();
  private final Stack<VP> varsStack = new Stack<>();

  protected AbstractProjectionsPrettyPrinter(@NotNull Layouter<E> layouter) {
    l = layouter;
    dataPrinter = new DataPrinter<>(l);
    gdataPrettyPrinter = new GDataPrettyPrinter<>(l);
  }

  // todo figure out recursive projections syntax
  public final void print(@NotNull VP p, int pathSteps) throws E {
    if (varsStack.contains(p)) {
      // handle recursive projections
      int ref = nextRefNumber++;
      varRefs.put(p, ref);
      l.print("... = @").print(Integer.toString(ref));
      return;
    }

    try {
      varsStack.push(p);

      printVarOnly(p, pathSteps);
      printTailsOnly(p);
    } finally {
      varsStack.pop();
      Integer ref = varRefs.remove(p);
      if (ref != null) l.print("@").print(Integer.toString(ref));
    }

  }

  protected void printVarOnly(@NotNull VP p, int pathSteps) throws E {
    printVarDecoration(p);
    Map<String, TP> tagProjections = p.tagProjections();
    if (p.type().kind() != TypeKind.UNION) {
      // samovar
      print(tagProjections.values().iterator().next().projection(), decSteps(pathSteps));
    } else if (!p.parenthesized()) {
      Map.Entry<String, TP> entry = tagProjections.entrySet().iterator().next();
      l.print(":");
      print(entry.getKey(), entry.getValue(), decSteps(pathSteps));
    } else if (tagProjections.isEmpty()) {
      l.print(":()");
    } else {
      if (pathSteps > 0) throw new IllegalArgumentException(
          String.format(
              "found %d var tags and parenthesized = %b while path still contains %d steps",
              tagProjections.size(),
              p.parenthesized(),
              pathSteps
          )
      );
      l.beginCInd();
      l.print(":(");
      boolean first = true;
      for (Map.Entry<String, TP> entry : tagProjections.entrySet()) {
        if (first) first = false;
        else l.print(",");
        l.brk();
        print(entry.getKey(), entry.getValue(), 0);
      }
      l.brk(1, -l.getDefaultIndentation()).end().print(")");
    }
  }

  protected void printVarDecoration(@NotNull VP p) throws E { }

  private void printTailsOnly(@NotNull VP p) throws E {
    Collection<VP> polymorphicTails = p.polymorphicTails();

    if (polymorphicTails != null && !polymorphicTails.isEmpty()) {
      l.beginIInd();
      l.brk();
      if (polymorphicTails.size() == 1) {
        l.print("~");
        VP tail = polymorphicTails.iterator().next();
        l.print(tail.type().name().toString());
        l.brk();
        print(tail, 0);
      } else {
        l.beginCInd();
        l.print("~(");
        boolean first = true;
        for (VP tail : polymorphicTails) {
          if (first) first = false;
          else l.print(",");
          l.brk().print(tail.type().name().toString()).brk();
          print(tail, 0);
        }
        l.brk(1, -l.getDefaultIndentation()).end().print(")");
      }
      l.end();
    }
  }

  public abstract void print(@NotNull String tagName, @NotNull TP tp, int pathSteps) throws E;

  public abstract void print(@NotNull MP mp, int pathSteps) throws E;

  public void print(@NotNull Annotations cp) throws E {
    print(cp, false, true);
  }

  public boolean print(@NotNull Annotations cp, boolean needCommas, boolean first) throws E {
    for (Map.Entry<String, Annotation> entry : cp.asMap().entrySet()) {
      if (needCommas) {
        if (first) first = false;
        else l.print(",");
      }
      l.brk();
      l.beginCInd(0);
      l.print(entry.getKey()).brk().print("=").brk();
      gdataPrettyPrinter.print(entry.getValue().value());
      l.end();
    }

    return first;
  }

  protected boolean isPrintoutEmpty(@NotNull VP vp) {

    Collection<VP> tails = vp.polymorphicTails();
    if (tails != null && !tails.isEmpty()) return false;
    if (vp.type().kind() == TypeKind.UNION) return false; // non-samovar always prints something

    for (TP tagProjection : vp.tagProjections().values())
      if (!isPrintoutEmpty(tagProjection.projection())) return false;

    return true;
  }

  protected @NotNull String escape(@NotNull String s) { return Keywords.schema.escape(s); }

  public abstract boolean isPrintoutEmpty(@NotNull MP mp);

  protected int decSteps(int pathSteps) {
    return pathSteps == 0 ? 0 : pathSteps - 1;
  }
}
