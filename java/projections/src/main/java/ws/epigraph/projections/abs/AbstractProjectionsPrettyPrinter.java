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
import ws.epigraph.projections.Annotation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.ProjectionsPrettyPrinterContext;
import ws.epigraph.projections.gen.*;
import ws.epigraph.types.TypeKind;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class AbstractProjectionsPrettyPrinter<
    VP extends GenVarProjection<VP, TP, MP>,
    TP extends GenTagProjectionEntry<TP, MP>,
    MP extends GenModelProjection</*MP*/?, ?, ?, ?>,
    E extends Exception> {

  protected final @NotNull Layouter<E> l;
  protected final @NotNull GDataPrettyPrinter<E> gdataPrettyPrinter;
  protected final @NotNull ProjectionsPrettyPrinterContext<VP, MP> context;

  private final Collection<ProjectionReferenceName.RefNameSegment> visitedVarRefs = new HashSet<>();
  private final Collection<ProjectionReferenceName.RefNameSegment> visitedModelRefs = new HashSet<>();

  protected AbstractProjectionsPrettyPrinter(
      final @NotNull Layouter<E> layouter,
      final @NotNull ProjectionsPrettyPrinterContext<VP, MP> context) {
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
    visitedVarRefs.addAll(names);
  }

  public final void printVar(@NotNull VP p, int pathSteps) throws E {
    final ProjectionReferenceName name = p.referenceName();

    boolean shouldPrint = true;

    if (name != null && !name.isEmpty()) {
      ProjectionReferenceName.@Nullable RefNameSegment shortName = name.last();
      assert shortName != null;

      if (!context.inNamespace(name)) {
        context.addOtherNamespaceVarProjection(p);
        l.print("$").print(shortName.toString());
        shouldPrint = false;
      } else if (visitedVarRefs.contains(shortName)) {
        l.print("$").print(shortName.toString());
        shouldPrint = false;
      } else {
        visitedVarRefs.add(shortName);
        if (p.type().kind() == TypeKind.UNION) { // otherwise label will be printed by model
          l.print("$").print(shortName.toString()); //.print(" = ");
          nbsp();
          l.print("=");
          nbsp();
        }
      }
    }

    if (shouldPrint)
      printVarNoRefCheck(p, pathSteps);
  }

  public final void printVarNoRefCheck(@NotNull VP p, int pathSteps) throws E {
    printVarOnly(p, pathSteps);
    printTailsOnly(p);
  }

  protected void printVarOnly(@NotNull VP p, int pathSteps) throws E {
    printVarDecoration(p);
    Map<String, TP> tagProjections = p.tagProjections();
    if (p.type().kind() != TypeKind.UNION) {
      // samovar
      TP tp = tagProjections.values().iterator().next();
      printTag(null, tp, decSteps(pathSteps));
    } else if (!p.parenthesized()) {
      Map.Entry<String, TP> entry = tagProjections.entrySet().iterator().next();
      l.print(":");
      printTag(entry.getKey(), entry.getValue(), decSteps(pathSteps));
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
        brk();
        printTag(entry.getKey(), entry.getValue(), 0);
      }
      brk(1, -l.getDefaultIndentation()).end().print(")");
    }
  }

  protected void printVarDecoration(@NotNull VP p) throws E { }

  private void printTailsOnly(@NotNull VP p) throws E {
    Collection<VP> polymorphicTails = p.polymorphicTails();

    if (polymorphicTails != null && !polymorphicTails.isEmpty()) {
      l.beginIInd();
      brk();
      if (polymorphicTails.size() == 1) {
        l.print("~~");
        VP tail = polymorphicTails.iterator().next();
        l.print(tail.type().name().toString());
        brk();
        printVar(tail, 0);
      } else {
        l.beginCInd();
        l.print("~~(");
        boolean first = true;
        for (VP tail : polymorphicTails) {
          if (first) first = false;
          else l.print(",");
          brk();
          l.beginIInd(0);
          l.print(tail.type().name().toString());
          brk();
          printVar(tail, 0);
          l.end();
        }
        brk(1, -l.getDefaultIndentation()).end().print(")");
      }
      l.end();
    }
  }

  public void printTag(final @Nullable String tagName, final @NotNull TP tp, final int pathSteps) throws E {
    final MP projection = tp.projection();

    l.beginIInd(0);

    if (tagName != null)
      printTagName(tagName, projection);

    if (!isPrintoutEmpty(projection)) {
      if (tagName != null) // name was printed
        nbsp();
//      l.print(" ");
//      brk();
      printModel(projection, pathSteps);
    }

    l.end();
  }

  protected void printTagName(@NotNull String tagName, @NotNull MP mp) throws E { l.print(tagName); }

  public void printModel(@NotNull MP mp, int pathSteps) throws E {
    final ProjectionReferenceName name = mp.referenceName();

    boolean shouldPrint = true;

    if (name != null && !name.isEmpty()) {
      ProjectionReferenceName.RefNameSegment shortName = name.last();
      assert shortName != null;

      if (!context.inNamespace(name)) {
        context.addOtherNamespaceModelProjection(mp);
        l.print("$").print(shortName.toString());
        shouldPrint = false;
      } else if (visitedModelRefs.contains(shortName)) {
        l.print("$").print(shortName.toString());
        shouldPrint = false;
      } else {
        visitedModelRefs.add(shortName);
        l.print("$").print(shortName.toString()).print(" = ");
      }
    }

    if (shouldPrint)
      printModelNoRefCheck(mp, pathSteps);
  }

  @SuppressWarnings("unchecked")
  public void printModelNoRefCheck(@NotNull MP mp, int pathSteps) throws E {
    if (mp.isResolved()) {
      l.beginIInd(0);

      boolean empty = printModelParams(mp);
      if (!empty && !isPrintoutNoParamsEmpty(mp))
        l.print(" ");
      //l.brk();

      if (!isPrintoutNoParamsEmpty(mp))
        printModelOnly(mp, pathSteps);

      printModelTailsOnly(mp);

      final MP meta = (MP) mp.metaProjection();
      if (meta != null)
        printModelMeta(meta);

      l.end();
    } else
      l.print("<unresolved>");
  }

  protected abstract boolean printModelParams(@NotNull MP mp) throws E;

  protected abstract void printModelOnly(@NotNull MP mp, int pathSteps) throws E;

  protected void printModelMeta(@NotNull MP meta) throws E { }

  @SuppressWarnings("unchecked") // todo introduce TMP extends MP
  private void printModelTailsOnly(@NotNull MP p) throws E {
    List<MP> polymorphicTails = (List<MP>) p.polymorphicTails();

    if (polymorphicTails != null && !polymorphicTails.isEmpty()) {
      l.beginIInd();
      brk();
      if (polymorphicTails.size() == 1) {
        l.print("~");
        MP tail = polymorphicTails.iterator().next();
        l.print(modelTailTypeNamePrefix(tail));
        l.print(tail.type().name().toString());
        brk();
        printModel(tail, 0);
      } else {
        l.beginCInd();
        l.print("~(");
        boolean first = true;
        for (MP tail : polymorphicTails) {
          if (first) first = false;
          else l.print(",");
          brk();
          l.beginIInd(0);
          l.print(modelTailTypeNamePrefix(tail));
          l.print(tail.type().name().toString());
          brk();
          printModel(tail, 0);
          l.end();
        }
        brk(1, -l.getDefaultIndentation()).end().print(")");
      }
      l.end();
    }
  }

  protected String modelTailTypeNamePrefix(@NotNull MP mp) {
    return "";
  }

  public void printAnnotations(@NotNull Annotations cp) throws E {
    printAnnotations(cp, false, true);
  }

  public boolean printAnnotations(@NotNull Annotations cp, boolean needCommas, boolean first) throws E {
    for (Map.Entry<String, Annotation> entry : cp.asMap().entrySet()) {
      if (first) {
        first = false;
      } else {
        if (needCommas) l.print(",");
        brk();
      }
      l.beginCInd(0);
      l.print(entry.getKey());
      brk().print("=");
      brk();
      gdataPrettyPrinter.print(entry.getValue().value());
      l.end();
    }

    return first;
  }

  protected boolean isPrintoutEmpty(@NotNull VP vp) {

    Collection<VP> tails = vp.polymorphicTails();
    if (tails != null && !tails.isEmpty()) return false;
    if (vp.type().kind() == TypeKind.UNION) return false; // non-samovar always prints something

    for (TP tagProjection : vp.tagProjections().values()) {
      final MP modelProjection = tagProjection.projection();
      if (!modelParamsEmpty(modelProjection)) return false;
      if (!isPrintoutNoParamsEmpty(modelProjection)) return false;
      if (!modelProjection.annotations().isEmpty()) return false;
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
        return ((GenRecordModelProjection<?, ?, ?, ?, ?, ?, ?>) mp).fieldProjections().isEmpty();
      case MAP:
        return isPrintoutEmpty((VP) ((GenMapModelProjection<?, ?, ?, ?, ?>) mp).itemsProjection());
      case LIST:
        return isPrintoutEmpty((VP) ((GenListModelProjection<?, ?, ?, ?, ?>) mp).itemsProjection());
      default:
        return true;
    }
  }

  public boolean modelParamsEmpty(@NotNull MP mp) {
    GenModelProjection<?, ?, ?, ?> metaProjection = mp.metaProjection();
    Annotations annotations = mp.annotations();

    return metaProjection == null && annotations.isEmpty();
  }

  protected int decSteps(int pathSteps) {
    return pathSteps == 0 ? 0 : pathSteps - 1;
  }
}
