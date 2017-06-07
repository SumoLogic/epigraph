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
import ws.epigraph.lang.Qn;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.ProjectionsPrettyPrinterContext;
import ws.epigraph.projections.abs.AbstractProjectionsPrettyPrinter;
import ws.epigraph.projections.gen.GenModelProjection;
import ws.epigraph.projections.gen.GenTagProjectionEntry;
import ws.epigraph.projections.gen.GenVarProjection;
import ws.epigraph.projections.gen.ProjectionReferenceName;
import ws.epigraph.projections.op.delete.OpDeleteModelProjection;
import ws.epigraph.projections.op.delete.OpDeleteProjectionsPrettyPrinter;
import ws.epigraph.projections.op.delete.OpDeleteVarProjection;
import ws.epigraph.projections.op.input.OpInputModelProjection;
import ws.epigraph.projections.op.input.OpInputProjectionsPrettyPrinter;
import ws.epigraph.projections.op.input.OpInputVarProjection;
import ws.epigraph.projections.op.output.OpOutputModelProjection;
import ws.epigraph.projections.op.output.OpOutputProjectionsPrettyPrinter;
import ws.epigraph.projections.op.output.OpOutputVarProjection;
import ws.epigraph.schema.operations.OperationDeclaration;
import ws.epigraph.schema.operations.OperationsPrettyPrinter;
import ws.epigraph.types.DataTypeApi;
import ws.epigraph.types.DatumType;
import ws.epigraph.types.TagApi;
import ws.epigraph.types.TypeKind;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
    if (!schema.resources().isEmpty()) l.nl();

    Namespaces namespaces = new Namespaces(Qn.fromDotSeparated(namespaceString));

    Collection<OpOutputVarProjection> globalOutputProjections = new ArrayList<>();
    Collection<OpInputVarProjection> globalInputProjections = new ArrayList<>();
    Collection<OpDeleteVarProjection> globalDeleteProjections = new ArrayList<>();

    for (ResourceDeclaration resource : schema.resources().values()) {

      ProjectionsPrettyPrinterContext<OpOutputVarProjection, OpOutputModelProjection<?, ?, ?>>
          resourceOutputProjectionsContext = new ProjectionsPrettyPrinterContext<>(
          ProjectionReferenceName.fromQn(namespaces.outputProjectionsNamespace(resource.fieldName()))
      );
      ProjectionsPrettyPrinterContext<OpInputVarProjection, OpInputModelProjection<?, ?, ?, ?>>
          resourceInputProjectionsContext = new ProjectionsPrettyPrinterContext<>(
          ProjectionReferenceName.fromQn(namespaces.inputProjectionsNamespace(resource.fieldName()))
      );
      ProjectionsPrettyPrinterContext<OpDeleteVarProjection, OpDeleteModelProjection<?, ?, ?>>
          resourceDeleteProjectionsContext = new ProjectionsPrettyPrinterContext<>(
          ProjectionReferenceName.fromQn(namespaces.deleteProjectionsNamespace(resource.fieldName()))
      );

      printResource(
          namespaces,
          resource,
          resourceOutputProjectionsContext,
          resourceInputProjectionsContext,
          resourceDeleteProjectionsContext
      );

      globalOutputProjections.addAll(resourceOutputProjectionsContext.otherNamespaceVarProjections());
      globalInputProjections.addAll(resourceInputProjectionsContext.otherNamespaceVarProjections());
      globalDeleteProjections.addAll(resourceDeleteProjectionsContext.otherNamespaceVarProjections());
    }

    for (final TransformerDeclaration transformer : schema.transformers().values()) {
      printTransformer(
          namespaces,
          transformer,
          globalOutputProjections,
          globalInputProjections
      );
    }

    ProjectionReferenceName globalProjectionsNamespace =
        ProjectionReferenceName.fromQn(namespaces.projectionsNamespace());

    checkProjectionsInNamespace(globalOutputProjections, globalProjectionsNamespace);
    checkProjectionsInNamespace(globalInputProjections, globalProjectionsNamespace);
    checkProjectionsInNamespace(globalDeleteProjections, globalProjectionsNamespace);

    printGlobalOutputProjections(null, globalOutputProjections, namespaces);
    printGlobalInputProjections(null, globalInputProjections, namespaces);
    printGlobalDeleteProjections(null, globalDeleteProjections, namespaces);

    l.end();
  }

  private void checkProjectionsInNamespace(
      @NotNull Collection<? extends GenVarProjection<?, ?, ?>> projections,
      @NotNull ProjectionReferenceName namespace) {

    for (final GenVarProjection<?, ?, ?> projection : projections) {
      if (!namespace.equals(projection.referenceName().removeLastSegment())) {
        throw new IllegalStateException(
            "Projection " + projection.referenceName() + " was expected to belong to " + namespace);
      }
    }
  }

  private void printTransformer(
      @NotNull Namespaces namespaces,
      @NotNull TransformerDeclaration transformer,
      @NotNull Collection<OpOutputVarProjection> globalOutputProjections,
      @NotNull Collection<OpInputVarProjection> globalInputProjections
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
      first = new OpOutputProjectionsPrettyPrinter<>(l).printAnnotations(annotations, true, true);
    }

    if (!first) l.print(",");
    l.brk();

    l.beginIInd();
    l.print("inputProjection").brk();

    ProjectionsPrettyPrinterContext<OpInputVarProjection, OpInputModelProjection<?, ?, ?, ?>> ipc =
        new ProjectionsPrettyPrinterContext<>(ProjectionReferenceName.fromQn(namespaces.projectionsNamespace()));
    new OpInputProjectionsPrettyPrinter<>(l, ipc).printVar(transformer.inputProjection(), 0);
    globalInputProjections.addAll(ipc.otherNamespaceVarProjections());
    l.end();


    l.print(",");
    l.brk();

    l.beginIInd();
    l.print("outputProjection").brk();
    ProjectionsPrettyPrinterContext<OpOutputVarProjection, OpOutputModelProjection<?, ?, ?>> opc =
        new ProjectionsPrettyPrinterContext<>(ProjectionReferenceName.fromQn(namespaces.projectionsNamespace()));
    new OpOutputProjectionsPrettyPrinter<>(l, opc).printVar(transformer.outputProjection(), 0);
    globalOutputProjections.addAll(opc.otherNamespaceVarProjections());
    l.end();

    l.end(); //2
    l.end(); //1
  }

  private void printResource(
      @NotNull Namespaces namespaces,
      @NotNull ResourceDeclaration resource,
      @NotNull ProjectionsPrettyPrinterContext<OpOutputVarProjection, OpOutputModelProjection<?, ?, ?>> resourceOutputProjectionsPrinterContext,
      @NotNull ProjectionsPrettyPrinterContext<OpInputVarProjection, OpInputModelProjection<?, ?, ?, ?>> resourceInputProjectionsPrinterContext,
      @NotNull ProjectionsPrettyPrinterContext<OpDeleteVarProjection, OpDeleteModelProjection<?, ?, ?>> resourceDeleteProjectionsPrinterContext
  ) throws E {
    l.beginCInd(0);
    l.beginIInd(0);
    l.print("resource").brk();

    final String resourceName = resource.fieldName();
    l.print(resourceName).print(":").brk();

    @NotNull DataTypeApi fieldType = resource.fieldType();
    l.print(fieldType.type().name().toString()).brk();

    @Nullable TagApi defaultTag = fieldType.defaultTag();
    if (defaultTag != null && !(fieldType.type() instanceof DatumType)) {
      l.print("default").brk();
      l.print(defaultTag.name()).brk();
    }

    l.print("{");
    l.end();
    l.beginCInd();

    Collection<OpOutputVarProjection> resourceOutputProjections = new ArrayList<>();
    Collection<OpInputVarProjection> resourceInputProjections = new ArrayList<>();
    Collection<OpDeleteVarProjection> resourceDeleteProjections = new ArrayList<>();

    for (OperationDeclaration operation : resource.operations()) {

      ProjectionsPrettyPrinterContext<OpOutputVarProjection, OpOutputModelProjection<?, ?, ?>>
          operationOutputProjectionsContext =
          new ProjectionsPrettyPrinterContext<>(
              ProjectionReferenceName.fromQn(
                  namespaces.operationOutputProjectionsNamespace(
                      resourceName,
                      operation.kind(),
                      operation.nameOrDefaultName()
                  )
              )
          );

      ProjectionsPrettyPrinterContext<OpInputVarProjection, OpInputModelProjection<?, ?, ?, ?>>
          operationInputProjectionsContext =
          new ProjectionsPrettyPrinterContext<>(
              ProjectionReferenceName.fromQn(
                  namespaces.operationInputProjectionsNamespace(
                      resourceName,
                      operation.kind(),
                      operation.nameOrDefaultName()
                  )
              )
          );

      ProjectionsPrettyPrinterContext<OpDeleteVarProjection, OpDeleteModelProjection<?, ?, ?>>
          operationDeleteProjectionsContext =
          new ProjectionsPrettyPrinterContext<>(
              ProjectionReferenceName.fromQn(
                  namespaces.operationDeleteProjectionsNamespace(
                      resourceName,
                      operation.kind(),
                      operation.nameOrDefaultName()
                  )
              )
          );

      l.nl();
      operationsPrettyPrinter.printOperation(
          operation,
          operationOutputProjectionsContext,
          operationInputProjectionsContext,
          operationDeleteProjectionsContext
      );

      resourceOutputProjections.addAll(
          resourceOutputProjectionsPrinterContext.filterEntityProjections(
              operationOutputProjectionsContext.otherNamespaceVarProjections()
          )
      );
      resourceInputProjections.addAll(
          resourceInputProjectionsPrinterContext.filterEntityProjections(
              operationInputProjectionsContext.otherNamespaceVarProjections()
          )
      );
      resourceDeleteProjections.addAll(
          resourceDeleteProjectionsPrinterContext.filterEntityProjections(
              operationDeleteProjectionsContext.otherNamespaceVarProjections()
          )
      );
    }

    printGlobalOutputProjections(resourceName, resourceOutputProjections, namespaces);
    printGlobalInputProjections(resourceName, resourceInputProjections, namespaces);
    printGlobalDeleteProjections(resourceName, resourceDeleteProjections, namespaces);

    l.end();
    l.brk(1, -l.getDefaultIndentation()).print("}");
    l.end();
  }

  private void printGlobalOutputProjections(
      final @Nullable String resourceName,
      final @NotNull Collection<OpOutputVarProjection> globalOutputProjections,
      final @NotNull Namespaces namespaces) throws E {

    printGlobalProjections(
        "outputProjection",
        globalOutputProjections,
        Collections.emptyList(),
        projectionName -> new ProjectionsPrettyPrinterContext<>(
            ProjectionReferenceName.fromQn(
                resourceName == null
                ? namespaces.outputProjectionNamespace(projectionName)
                : namespaces.outputProjectionNamespace(resourceName, projectionName)
            )
        ),
        context -> new OpOutputProjectionsPrettyPrinter<>(l, context)
    );
  }

  private void printGlobalInputProjections(
      final @Nullable String resourceName,
      final @NotNull Collection<OpInputVarProjection> globalInputProjections,
      final @NotNull Namespaces namespaces) throws E {

    printGlobalProjections(
        "inputProjection",
        globalInputProjections,
        Collections.emptyList(),
        projectionName -> new ProjectionsPrettyPrinterContext<>(
            ProjectionReferenceName.fromQn(
                resourceName == null
                ? namespaces.inputProjectionNamespace(projectionName)
                : namespaces.inputProjectionNamespace(resourceName, projectionName)
            )
        ),
        context -> new OpInputProjectionsPrettyPrinter<>(l, context)
    );
  }

  private void printGlobalDeleteProjections(
      final @Nullable String resourceName,
      final @NotNull Collection<OpDeleteVarProjection> globalDeleteProjections,
      final @NotNull Namespaces namespaces) throws E {

    printGlobalProjections(
        "deleteProjection",
        globalDeleteProjections,
        Collections.emptyList(),
        projectionName -> new ProjectionsPrettyPrinterContext<>(
            ProjectionReferenceName.fromQn(
                resourceName == null
                ? namespaces.deleteProjectionNamespace(projectionName)
                : namespaces.deleteProjectionNamespace(resourceName, projectionName)
            )
        ),
        context -> new OpDeleteProjectionsPrettyPrinter<>(l, context)
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
            projectionsPrettyPrinter.printVarNoRefCheck(outputProjection, 0);
            l.end();

            for (VP vp : printerContext.otherNamespaceVarProjections()) {
              nextVarProjections.add(vp);
              //noinspection ConstantConditions
              printedVarRefs.add(vp.referenceName().last());
            }

            for (MP mp : printerContext.otherNamespaceModelProjections()) {
              nextModelProjections.add(mp);
              //noinspection ConstantConditions
              printedModelRefs.add(mp.referenceName().last());
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

          for (VP vp : printerContext.otherNamespaceVarProjections()) {
            nextVarProjections.add(vp);
            //noinspection ConstantConditions
            printedVarRefs.add(vp.referenceName().last());
          }

          for (MP mp : printerContext.otherNamespaceModelProjections()) {
            nextModelProjections.add(mp);
            //noinspection ConstantConditions
            printedModelRefs.add(mp.referenceName().last());
          }
        }
      }

      varProjections = nextVarProjections;
      modelProjections = nextModelProjections;
    }
  }
}
