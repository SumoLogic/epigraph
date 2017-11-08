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

package ws.epigraph.schema;

import de.uka.ilkd.pp.Layouter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.annotations.Annotations;
import ws.epigraph.lang.Qn;
import ws.epigraph.projections.ProjectionsPrettyPrinterContext;
import ws.epigraph.projections.abs.AbstractProjectionsPrettyPrinter;
import ws.epigraph.projections.gen.GenModelProjection;
import ws.epigraph.projections.gen.GenTagProjectionEntry;
import ws.epigraph.projections.gen.GenVarProjection;
import ws.epigraph.projections.gen.ProjectionReferenceName;
import ws.epigraph.projections.op.OpEntityProjection;
import ws.epigraph.projections.op.OpModelProjection;
import ws.epigraph.projections.op.OpProjectionsPrettyPrinter;
import ws.epigraph.schema.operations.OperationDeclaration;
import ws.epigraph.schema.operations.OperationsPrettyPrinter;
import ws.epigraph.types.DataTypeApi;
import ws.epigraph.types.DatumType;
import ws.epigraph.types.TagApi;
import ws.epigraph.types.TypeKind;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class SchemaPrettyPrinter<E extends Exception> {
  private final @NotNull Layouter<E> l;

  private final OperationsPrettyPrinter<E> operationsPrettyPrinter;

  public SchemaPrettyPrinter(@NotNull Layouter<E> l) {
    this.l = l;
    operationsPrettyPrinter = new OperationsPrettyPrinter<>(l);
  }

  public void print(@NotNull ResourcesSchema schema) throws E {
    // should we try to preserve imports?

    l.beginCInd(0);
    final String namespaceString = schema.namespace().toString();
    l.print("namespace ").print(namespaceString);
    if (!(schema.resources().isEmpty() && schema.transformers().isEmpty())) l.nl();

    Namespaces namespaces = new Namespaces(Qn.fromDotSeparated(namespaceString));

    ProjectionsPrettyPrinterContext<OpEntityProjection, OpModelProjection<?, ?, ?, ?>>
        globalOutputProjectionsContext = new ProjectionsPrettyPrinterContext<>(
        ProjectionReferenceName.fromQn(namespaces.outputProjectionsNamespace()),
        null
    );

    ProjectionsPrettyPrinterContext<OpEntityProjection, OpModelProjection<?, ?, ?, ?>>
        globalInputProjectionsContext = new ProjectionsPrettyPrinterContext<>(
        ProjectionReferenceName.fromQn(namespaces.inputProjectionsNamespace()),
        null
    );

    ProjectionsPrettyPrinterContext<OpEntityProjection, OpModelProjection<?, ?, ?, ?>>
        globalDeleteProjectionsContext = new ProjectionsPrettyPrinterContext<>(
        ProjectionReferenceName.fromQn(namespaces.deleteProjectionsNamespace()),
        null
    );

    boolean first = true;

    for (ResourceDeclaration resource : schema.resources().values()) {

      if (first) first = false;
      else l.brk();

      ProjectionsPrettyPrinterContext<OpEntityProjection, OpModelProjection<?, ?, ?, ?>>
          resourceOutputProjectionsContext = new ProjectionsPrettyPrinterContext<>(
          ProjectionReferenceName.fromQn(namespaces.outputProjectionsNamespace(resource.fieldName())),
          globalOutputProjectionsContext
      );

      ProjectionsPrettyPrinterContext<OpEntityProjection, OpModelProjection<?, ?, ?, ?>>
          resourceInputProjectionsContext = new ProjectionsPrettyPrinterContext<>(
          ProjectionReferenceName.fromQn(namespaces.inputProjectionsNamespace(resource.fieldName())),
          globalOutputProjectionsContext
      );

      ProjectionsPrettyPrinterContext<OpEntityProjection, OpModelProjection<?, ?, ?, ?>>
          resourceDeleteProjectionsContext = new ProjectionsPrettyPrinterContext<>(
          ProjectionReferenceName.fromQn(namespaces.deleteProjectionsNamespace(resource.fieldName())),
          globalOutputProjectionsContext
      );

      printResource(
          namespaces,
          resource,
          resourceOutputProjectionsContext,
          resourceInputProjectionsContext,
          resourceDeleteProjectionsContext
      );
    }

    for (final TransformerDeclaration transformer : schema.transformers().values()) {

      if (first) first = false;
      else l.brk();

      printTransformer(
          namespaces,
          transformer,
          globalOutputProjectionsContext
      );
    }

    printOutputProjections(
        null,
        globalOutputProjectionsContext,
        globalOutputProjectionsContext.entityProjections(),
        globalOutputProjectionsContext.modelProjections(),
        namespaces
    );

    printInputProjections(
        null,
        globalInputProjectionsContext,
        globalInputProjectionsContext.entityProjections(),
        globalInputProjectionsContext.modelProjections(),
        namespaces
    );

    printDeleteProjections(
        null,
        globalDeleteProjectionsContext,
        globalDeleteProjectionsContext.entityProjections(),
        globalDeleteProjectionsContext.modelProjections(),
        namespaces
    );

    l.end();
  }

  private void printTransformer(
      @NotNull Namespaces namespaces,
      @NotNull TransformerDeclaration transformer,
      @NotNull ProjectionsPrettyPrinterContext<OpEntityProjection, OpModelProjection<?, ?, ?, ?>> globalOutputProjections
  ) throws E {

    l.beginCInd(0); //1

    l.beginIInd();
    l.print("transformer").brk();
    l.print(transformer.name()).print(":").brk();
    l.print(transformer.type().name().toString()).brk();
    l.print("{");
    l.end();

    l.beginCInd(); //2

    boolean first = true;

    Annotations annotations = transformer.annotations();
    if (!annotations.isEmpty()) {
      l.brk();
      first = new OpProjectionsPrettyPrinter<>(l).printAnnotations(annotations, true, true);
    }

    if (!first) l.print(",");
    l.brk();

    l.beginIInd();
    l.print("inputProjection").brk();

    ProjectionsPrettyPrinterContext<OpEntityProjection, OpModelProjection<?, ?, ?, ?>> ipc =
        new ProjectionsPrettyPrinterContext<>(
            ProjectionReferenceName.fromQn(namespaces.projectionsNamespace()),
            globalOutputProjections
        );

    new OpProjectionsPrettyPrinter<>(l, ipc).printEntity(transformer.inputProjection(), 0);
    l.end();


    l.print(",");
    l.brk();

    l.beginIInd();
    l.print("outputProjection").brk();
    ProjectionsPrettyPrinterContext<OpEntityProjection, OpModelProjection<?, ?, ?, ?>> opc =
        new ProjectionsPrettyPrinterContext<>(
            ProjectionReferenceName.fromQn(namespaces.projectionsNamespace()),
            globalOutputProjections
        );
    new OpProjectionsPrettyPrinter<>(l, opc).printEntity(transformer.outputProjection(), 0);
    l.end();

    l.end(); //2
    l.brk(1, -l.getDefaultIndentation()).print("}");
    l.end(); //1
  }

  private void printResource(
      @NotNull Namespaces namespaces,
      @NotNull ResourceDeclaration resource,
      @NotNull ProjectionsPrettyPrinterContext<OpEntityProjection, OpModelProjection<?, ?, ?, ?>> resourceOutputProjectionsPrinterContext,
      ProjectionsPrettyPrinterContext<OpEntityProjection, OpModelProjection<?, ?, ?, ?>> resourceInputProjectionsPrinterContext,
      ProjectionsPrettyPrinterContext<OpEntityProjection, OpModelProjection<?, ?, ?, ?>> resourceDeleteProjectionsPrinterContext
  ) throws E {
    l.beginCInd(0);
    l.beginIInd(0);
    l.print("resource").brk();

    final String resourceName = resource.fieldName();
    l.print(resourceName).print(":").brk();

    @NotNull DataTypeApi fieldType = resource.fieldType();
    l.print(fieldType.type().name().toString()).brk();

    @Nullable TagApi defaultTag = fieldType.retroTag();
    if (defaultTag != null && !(fieldType.type() instanceof DatumType)) {
      l.print("retro").brk();
      l.print(defaultTag.name()).brk();
    }

    l.print("{");
    l.end();
    l.beginCInd();

    for (OperationDeclaration operation : resource.operations()) {

      ProjectionsPrettyPrinterContext<OpEntityProjection, OpModelProjection<?, ?, ?, ?>>
          operationOutputProjectionsContext =
          new ProjectionsPrettyPrinterContext<>(
              ProjectionReferenceName.fromQn(
                  namespaces.operationOutputProjectionsNamespace(
                      resourceName,
                      operation.kind(),
                      operation.nameOrDefaultName()
                  )
              ),
              resourceOutputProjectionsPrinterContext
          );

      ProjectionsPrettyPrinterContext<OpEntityProjection, OpModelProjection<?, ?, ?, ?>>
          operationInputProjectionsContext =
          new ProjectionsPrettyPrinterContext<>(
              ProjectionReferenceName.fromQn(
                  namespaces.operationInputProjectionsNamespace(
                      resourceName,
                      operation.kind(),
                      operation.nameOrDefaultName()
                  )
              ),
              resourceInputProjectionsPrinterContext
          );

      ProjectionsPrettyPrinterContext<OpEntityProjection, OpModelProjection<?, ?, ?, ?>>
          operationDeleteProjectionsContext =
          new ProjectionsPrettyPrinterContext<>(
              ProjectionReferenceName.fromQn(
                  namespaces.operationDeleteProjectionsNamespace(
                      resourceName,
                      operation.kind(),
                      operation.nameOrDefaultName()
                  )
              ),
              resourceDeleteProjectionsPrinterContext
          );

      l.nl();
      operationsPrettyPrinter.printOperation(
          operation,
          operationOutputProjectionsContext,
          operationInputProjectionsContext,
          operationDeleteProjectionsContext
      );

    }

    printOutputProjections(
        resourceName,
        resourceOutputProjectionsPrinterContext,
        resourceOutputProjectionsPrinterContext.entityProjections(),
        resourceOutputProjectionsPrinterContext.modelProjections(),
        namespaces
    );
    printInputProjections(
        resourceName,
        resourceInputProjectionsPrinterContext,
        resourceInputProjectionsPrinterContext.entityProjections(),
        resourceInputProjectionsPrinterContext.modelProjections(),
        namespaces
    );
    printDeleteProjections(
        resourceName,
        resourceDeleteProjectionsPrinterContext,
        resourceDeleteProjectionsPrinterContext.entityProjections(),
        resourceDeleteProjectionsPrinterContext.modelProjections(),
        namespaces
    );

    l.end();
    l.brk(1, -l.getDefaultIndentation()).print("}");
    l.end();
  }

  private void printOutputProjections(
      final @Nullable String resourceName,
      final @Nullable ProjectionsPrettyPrinterContext<OpEntityProjection, OpModelProjection<?, ?, ?, ?>> parent,
      final @NotNull Collection<OpEntityProjection> globalOutputVarProjections,
      final @NotNull Collection<OpModelProjection<?, ?, ?, ?>> globalOutputModelProjections,
      final @NotNull Namespaces namespaces) throws E {

    printGlobalProjections(
        "outputProjection",
        globalOutputVarProjections,
        globalOutputModelProjections,
        projectionName -> new ProjectionsPrettyPrinterContext<>(
            ProjectionReferenceName.fromQn(
                resourceName == null
                ? namespaces.outputProjectionNamespace(projectionName)
                : namespaces.outputProjectionNamespace(resourceName, projectionName)
            ),
            parent
        ),
        context -> new OpProjectionsPrettyPrinter<>(l, context)
    );
  }

  private void printInputProjections(
      final @Nullable String resourceName,
      final @Nullable ProjectionsPrettyPrinterContext<OpEntityProjection, OpModelProjection<?, ?, ?, ?>> parent,
      final @NotNull Collection<OpEntityProjection> globalInputVarProjections,
      final @NotNull Collection<OpModelProjection<?, ?, ?, ?>> globalInputModelProjections,
      final @NotNull Namespaces namespaces) throws E {

    printGlobalProjections(
        "inputProjection",
        globalInputVarProjections,
        globalInputModelProjections,
        projectionName -> new ProjectionsPrettyPrinterContext<>(
            ProjectionReferenceName.fromQn(
                resourceName == null
                ? namespaces.inputProjectionNamespace(projectionName)
                : namespaces.inputProjectionNamespace(resourceName, projectionName)
            ),
            parent
        ),
        context -> new OpProjectionsPrettyPrinter<>(l, context)
    );
  }

  private void printDeleteProjections(
      final @Nullable String resourceName,
      final @Nullable ProjectionsPrettyPrinterContext<OpEntityProjection, OpModelProjection<?, ?, ?, ?>> parent,
      final @NotNull Collection<OpEntityProjection> globalDeleteVarProjections,
      final @NotNull Collection<OpModelProjection<?, ?, ?, ?>> globalDeleteModelProjections,
      final @NotNull Namespaces namespaces) throws E {

    printGlobalProjections(
        "deleteProjection",
        globalDeleteVarProjections,
        globalDeleteModelProjections,
        projectionName -> new ProjectionsPrettyPrinterContext<>(
            ProjectionReferenceName.fromQn(
                resourceName == null
                ? namespaces.deleteProjectionNamespace(projectionName)
                : namespaces.deleteProjectionNamespace(resourceName, projectionName)
            ),
            parent
        ),
        context -> new OpProjectionsPrettyPrinter<>(l, context)
    );
  }

  @SuppressWarnings("unchecked")
  private <VP extends GenVarProjection<VP, ?, MP>, MP extends GenModelProjection<?, ?, ?, ?>> void printGlobalProjections(
      @NotNull String prefix,
      @NotNull Collection<VP> varProjections,
      @NotNull Collection<MP> modelProjections,
      @NotNull Function<String, ProjectionsPrettyPrinterContext<VP, MP>> prettyPrinterContextFactory,
      @NotNull Function<ProjectionsPrettyPrinterContext<VP, MP>, AbstractProjectionsPrettyPrinter<VP, ?, MP, E>> prettyPrinterFactory
  ) throws E {

    List<ProjectionReferenceName.RefNameSegment> printedVarRefs = varProjections.stream()
        .map(GenVarProjection::referenceName)
        .map(ProjectionReferenceName::last)
        .collect(Collectors.toList());

    List<ProjectionReferenceName.RefNameSegment> printedModelRefs = modelProjections.stream()
        .map(GenModelProjection::referenceName)
        .map(ProjectionReferenceName::last)
        .collect(Collectors.toList());

    Set<ProjectionReferenceName.RefNameSegment> printedVarNames = new HashSet<>();
    Set<ProjectionReferenceName.RefNameSegment> printedModelNames = new HashSet<>();

    while (!varProjections.isEmpty() || !modelProjections.isEmpty()) {

      Collection<VP> nextVarProjections = new ArrayList<>(); // projections referenced from printed projections
      Collection<MP> nextModelProjections = new ArrayList<>(); // projections referenced from printed projections

      for (final VP outputProjection : varProjections) {
        final ProjectionReferenceName name = outputProjection.referenceName();
        assert name != null;
        final ProjectionReferenceName.RefNameSegment shortName = name.last();
        assert shortName != null;

        if (!printedVarNames.contains(shortName)) {
          printedVarNames.add(shortName);

          if (outputProjection.type().kind() == TypeKind.ENTITY) { // real entity

            final ProjectionsPrettyPrinterContext<VP, MP> printerContext =
                prettyPrinterContextFactory.apply(shortName.toString());
            final AbstractProjectionsPrettyPrinter<VP, ?, ?, E> projectionsPrettyPrinter =
                prettyPrinterFactory.apply(printerContext);

            projectionsPrettyPrinter.addVisitedRefs(printedVarRefs);

            l.nl();
            l.beginIInd();
            l.print(prefix).brk().print(shortName.toString()).print(":").brk();
            l.print(outputProjection.type().name().toString()).brk().print("=").brk();
            projectionsPrettyPrinter.printEntityNoRefCheck(outputProjection, 0);
            l.end();

            ProjectionsPrettyPrinterContext<VP, MP> parentContext = printerContext.parent();
            if (parentContext != null) {
              for (VP vp : parentContext.entityProjections()) {
                nextVarProjections.add(vp);
                //noinspection ConstantConditions
                printedVarRefs.add(vp.referenceName().last());
              }

              for (MP mp : parentContext.modelProjections()) {
                nextModelProjections.add(mp);
                //noinspection ConstantConditions
                printedModelRefs.add(mp.referenceName().last());
              }
            }

          } else { // self-var
            final GenTagProjectionEntry<?, MP> tp = outputProjection.singleTagProjection();

            if (tp != null) {
              final MP mp = tp.projection();
              if (mp.referenceName() != null)
                nextModelProjections.add(mp);

            }
          }
        }
      }

      for (final MP outputProjection : modelProjections) {
        final ProjectionReferenceName name = outputProjection.referenceName();
        assert name != null;
        final ProjectionReferenceName.RefNameSegment shortName = name.last();
        assert shortName != null;

        if (!printedModelNames.contains(shortName)) {
          printedModelNames.add(shortName);

          final ProjectionsPrettyPrinterContext<VP, MP> printerContext =
              prettyPrinterContextFactory.apply(shortName.toString());
          final AbstractProjectionsPrettyPrinter<VP, ?, MP, E> projectionsPrettyPrinter =
              prettyPrinterFactory.apply(printerContext);

          projectionsPrettyPrinter.addVisitedRefs(printedModelRefs);

          l.nl();
          l.beginIInd();
          l.print(prefix).brk().print(shortName.toString()).print(":").brk();
          l.print(outputProjection.type().name().toString()).brk().print("=").brk();
          projectionsPrettyPrinter.printModelNoRefCheck(outputProjection, 0);
          l.end();

          ProjectionsPrettyPrinterContext<VP, MP> parentContext = printerContext.parent();
          if (parentContext != null) {
            for (VP vp : parentContext.entityProjections()) {
              nextVarProjections.add(vp);
              //noinspection ConstantConditions
              printedVarRefs.add(vp.referenceName().last());
            }

            for (MP mp : parentContext.modelProjections()) {
              nextModelProjections.add(mp);
              //noinspection ConstantConditions
              printedModelRefs.add(mp.referenceName().last());
            }
          }
        }
      }

      varProjections = nextVarProjections;
      modelProjections = nextModelProjections;
    }
  }
}
