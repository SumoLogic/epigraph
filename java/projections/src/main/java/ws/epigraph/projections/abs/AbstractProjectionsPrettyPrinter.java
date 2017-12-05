/*
 * Copyright 2017 Sumo Logic
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.gdata.GDataPrettyPrinter;
import ws.epigraph.projections.ProjectionsPrettyPrinterContext;
import ws.epigraph.projections.gen.*;
import ws.epigraph.types.TypeKind;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class AbstractProjectionsPrettyPrinter<
    P extends GenProjection<?, TP, EP, ? extends MP>,
    EP extends GenEntityProjection<EP, TP, MP>,
    TP extends GenTagProjectionEntry<TP, MP>,
    MP extends GenModelProjection<EP, TP, /*MP*/?, ?, ?>,
    E extends Exception> {

  protected final @NotNull Layouter<E> l;
  protected final @NotNull GDataPrettyPrinter<E> gdataPrettyPrinter;
  protected final @NotNull ProjectionsPrettyPrinterContext<P> context;

  private final Collection<ProjectionReferenceName.RefNameSegment> visitedRefs = new HashSet<>();

  protected AbstractProjectionsPrettyPrinter(
      final @NotNull Layouter<E> layouter,
      final @NotNull ProjectionsPrettyPrinterContext<P> context) {
    l = layouter;
    this.context = context;
    gdataPrettyPrinter = new GDataPrettyPrinter<>(l);
  }

  protected @NotNull Layouter<E> brk() throws E {
    l.brk();
    return l;
  }

  protected @NotNull Layouter<E> brk(int width, int offset) throws E {
    l.brk(width, offset);
    return l;
  }

  protected @NotNull Layouter<E> nbsp() throws E {
    l.print(" ");
    return l;
  }

  public void addVisitedRefs(@NotNull Collection<ProjectionReferenceName.RefNameSegment> names) {
    visitedRefs.addAll(names);
  }

  private @Nullable ProjectionReferenceName.RefNameSegment shortName(@NotNull P p) {
    ProjectionReferenceName referenceName = p.referenceName();
    return referenceName == null ? null : referenceName.last();
  }

  public final void printProjection(@NotNull P p, int pathSteps) throws E {
    final ProjectionReferenceName name = p.referenceName();

    @Nullable ProjectionReferenceName.RefNameSegment shortName = shortName(p);
    boolean shouldPrint = true;

    if (shortName != null) {
      context.addProjection(p);
      if (!context.inNamespace(name) || visitedRefs.contains(shortName)) {
        l.print("$").print(shortName.toString());
        shouldPrint = false;
      } else {
        visitedRefs.add(shortName);
        l.print("$").print(shortName.toString());
        nbsp();
        l.print("=");
        nbsp();
      }
    }

    if (shouldPrint)
      printProjectionNoRefCheck(p, pathSteps);
  }

  public final void printProjectionNoRefCheck(@NotNull P p, int pathSteps) throws E {
    if (p.isResolved()) {
      printProjectionWithoutTails(p, pathSteps);
      printTailsOnly(p);
    } else {
      l.print("<UNRESOLVED>");
    }
  }

  protected void printProjectionWithoutTails(@NotNull P p, int pathSteps) throws E {
    Map<String, TP> tagProjections = p.tagProjections();
    if (p.type().kind() != TypeKind.ENTITY) {
      // samovar

      TP tp = p.singleTagProjection();
      if (tp == null)
        l.print("< invalid " + p.type().name() + " data, 1 tag expected but " + tagProjections.size() + " found >");
      else
        printTag(null, tp, false, false, decSteps(pathSteps));

    } else if (!p.asEntityProjection().parenthesized() && !tagProjections.isEmpty()) {
      Map.Entry<String, TP> entry = tagProjections.entrySet().iterator().next();
      l.print(":");
      printTag(entry.getKey(), entry.getValue(), true, true, decSteps(pathSteps));
    } else if (tagProjections.isEmpty()) {
      l.print(":()");
    } else {
      if (pathSteps > 0) throw new IllegalArgumentException(
          String.format(
              "found %d entity tags (%s) and parenthesized = %b while path still contains %d steps",
              tagProjections.size(),
              String.join(", ", tagProjections.keySet()),
              p.asEntityProjection().parenthesized(),
              pathSteps
          )
      );
      l.beginCInd();
      l.print(":(");
      boolean first = true;
      for (Map.Entry<String, TP> entry : tagProjections.entrySet()) {
        if (first) first = false;
        else l.print(",");
        brk();
        printTag(entry.getKey(), entry.getValue(), true, true, 0);
      }
      brk(1, -l.getDefaultIndentation()).end().print(")");
    }
  }

  private void printTailsOnly(@NotNull P p) throws E {
    @SuppressWarnings("unchecked")
    Collection<P> polymorphicTails = (Collection<P>) p.polymorphicTails();

    String tailSign = p.isEntityProjection() ? ":~" : "~";

    if (polymorphicTails != null && !polymorphicTails.isEmpty()) {
      l.beginIInd();
      brk();
      if (polymorphicTails.size() == 1) {
        l.print(tailSign);
        P tail = polymorphicTails.iterator().next();
        l.print(tailTypeNamePrefix(tail));
        l.print(tail.type().name().toString());
        brk();
        printProjection(tail, 0);
      } else {
        l.beginCInd();
        l.print(tailSign);
        l.print("(");
        boolean first = true;
        for (P tail : polymorphicTails) {
          if (first) first = false;
          else l.print(",");
          brk();
          l.beginIInd(0);
          l.print(tailTypeNamePrefix(tail));
          l.print(tail.type().name().toString());
          brk();
          printProjection(tail, 0);
          l.end();
        }
        brk(1, -l.getDefaultIndentation()).end().print(")");
      }
      l.end();
    }
  }

  protected String tailTypeNamePrefix(@NotNull P tail) { return ""; }

  private void printTag(@Nullable String tagName, @NotNull TP tp, boolean checkRefs, boolean printTails, int pathSteps)
      throws E {
    final MP projection = tp.modelProjection();

    l.beginIInd(0);

    if (tagName != null)
      printTagName(tagName, projection);

    if (!isPrintoutEmpty(projection)) {
      if (tagName != null) // name was printed
        nbsp();
//      l.print(" ");
//      brk();
      printModel(projection, checkRefs, printTails, pathSteps);
    }

    l.end();
  }

  protected void printTagName(@NotNull String tagName, @NotNull MP mp) throws E { l.print(tagName); }

  public void printModel(@NotNull MP mp, int pathSteps) throws E {
    printModel(mp, true, true, pathSteps);
  }

  @SuppressWarnings("unchecked")
  private void printModel(@NotNull MP mp, boolean checkRefs, boolean printTails, int pathSteps) throws E {
    final ProjectionReferenceName name = mp.referenceName();

    boolean shouldPrint = true;

    if (checkRefs && name != null && !name.isEmpty()) {
      ProjectionReferenceName.RefNameSegment shortName = name.last();
      assert shortName != null;

      context.addProjection((P) mp);
      if (!context.inNamespace(name) || visitedRefs.contains(shortName)) {
        l.print("$").print(shortName.toString());
        shouldPrint = false;
      } else {
        visitedRefs.add(shortName);
        l.print("$").print(shortName.toString()).print(" = ");
      }
    }

    if (shouldPrint)
      printModelNoRefCheck(mp, printTails, pathSteps);
  }

  @SuppressWarnings("unchecked")
  private void printModelNoRefCheck(@NotNull MP mp, boolean printTails, int pathSteps) throws E {
    if (mp.isResolved()) {
      l.beginIInd(0);

      boolean empty = true;
      final MP meta = (MP) mp.metaProjection();
      if (meta != null) {
        empty = printModelMeta(meta);
      }

      if (!empty && !modelParamsEmpty(mp))
        nbsp();

      empty &= printModelParams(mp);

      if (!empty && !isPrintoutNoParamsEmpty(mp))
        nbsp();
      //l.brk();

      if (!isPrintoutNoParamsEmpty(mp))
        printModelOnly(mp, pathSteps);

      if (printTails)
        printTailsOnly((P) mp);

      l.end();
    } else
      l.print("<unresolved>");
  }

  protected abstract boolean printModelParams(@NotNull MP mp) throws E;

  protected abstract void printModelOnly(@NotNull MP mp, int pathSteps) throws E;

  protected boolean printModelMeta(@NotNull MP meta) throws E { return true; }

//  public void printDirectives(@NotNull Directives cp) throws E {
//    printDirectives(cp, false, true);
//  }
//
//  public boolean printDirectives(@NotNull Directives cp, boolean needCommas, boolean first) throws E {
//    for (Map.Entry<String, Directive> entry : cp.asMap().entrySet()) {
//      if (first) {
//        first = false;
//      } else {
//        if (needCommas) l.print(",");
//        brk();
//      }
//      l.beginCInd(0);
//      l.print(entry.getKey());
//      brk().print("=");
//      brk();
//      gdataPrettyPrinter.print(entry.getValue().value());
//      l.end();
//    }
//
//    return first;
//  }

  protected boolean isPrintoutEmpty(@NotNull P p) {
    return p.isEntityProjection() ? isPrintoutEmpty(p.asEntityProjection()) : isPrintoutEmpty(p.asModelProjection());
  }

  protected boolean isPrintoutEmpty(@NotNull EP EP) {

    if (!EP.isResolved()) return false;
    Collection<EP> tails = EP.polymorphicTails();
    if (tails != null && !tails.isEmpty()) return false;
    if (EP.type().kind() == TypeKind.ENTITY) return false; // non-samovar always prints something

    for (TP tagProjection : EP.tagProjections().values()) {
      final MP modelProjection = tagProjection.modelProjection();
      if (!isPrintoutEmpty(modelProjection)) return false;
      if (!modelParamsEmpty(modelProjection)) return false;
      if (!isPrintoutNoParamsEmpty(modelProjection)) return false;
    }

    return true;
  }

  protected boolean isPrintoutEmpty(@NotNull MP mp) {
    return isPrintoutNoParamsEmpty(mp) && modelParamsEmpty(mp);
  }

  @SuppressWarnings("unchecked")
  public boolean isPrintoutNoParamsEmpty(@NotNull MP mp) {
    if (!mp.isResolved()) return false;
    switch (mp.type().kind()) {
      case RECORD:
        return ((GenRecordModelProjection<?, ?, ?, ?, ?, ?, ?, ?>) mp).fieldProjections().isEmpty();
      case MAP:
        return isPrintoutEmpty((P) ((GenMapModelProjection<?, ?, ?, ?, ?, ?>) mp).itemsProjection());
      case LIST:
        return isPrintoutEmpty((P) ((GenListModelProjection<?, ?, ?, ?, ?, ?>) mp).itemsProjection());
      default:
        return true;
    }
  }

  public boolean modelParamsEmpty(@NotNull MP mp) { return true; }

  protected int decSteps(int pathSteps) {
    return pathSteps == 0 ? 0 : pathSteps - 1;
  }
}
