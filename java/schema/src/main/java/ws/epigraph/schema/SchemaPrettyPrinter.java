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
import ws.epigraph.projections.gen.GenProjection;
import ws.epigraph.projections.gen.ProjectionReferenceName;
import ws.epigraph.projections.op.OpProjection;
import ws.epigraph.projections.op.OpProjectionsPrettyPrinter;
import ws.epigraph.schema.operations.OperationDeclaration;
import ws.epigraph.schema.operations.OperationsPrettyPrinter;
import ws.epigraph.types.DataTypeApi;
import ws.epigraph.types.DatumType;
import ws.epigraph.types.TagApi;

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

    ProjectionsPrettyPrinterContext<OpProjection<?, ?>>
        globalOutputProjectionsContext = new ProjectionsPrettyPrinterContext<>(
        ProjectionReferenceName.fromQn(namespaces.outputProjectionsNamespace()),
        null
    );

    ProjectionsPrettyPrinterContext<OpProjection<?, ?>>
        globalInputProjectionsContext = new ProjectionsPrettyPrinterContext<>(
        ProjectionReferenceName.fromQn(namespaces.inputProjectionsNamespace()),
        null
    );

    ProjectionsPrettyPrinterContext<OpProjection<?, ?>>
        globalDeleteProjectionsContext = new ProjectionsPrettyPrinterContext<>(
        ProjectionReferenceName.fromQn(namespaces.deleteProjectionsNamespace()),
        null
    );

    boolean first = true;

    for (ResourceDeclaration resource : schema.resources().values()) {

      if (first) first = false;
      else l.brk();

      ProjectionsPrettyPrinterContext<OpProjection<?, ?>>
          resourceOutputProjectionsContext = new ProjectionsPrettyPrinterContext<>(
          ProjectionReferenceName.fromQn(namespaces.outputProjectionsNamespace(resource.fieldName())),
          globalOutputProjectionsContext
      );

      ProjectionsPrettyPrinterContext<OpProjection<?, ?>>
          resourceInputProjectionsContext = new ProjectionsPrettyPrinterContext<>(
          ProjectionReferenceName.fromQn(namespaces.inputProjectionsNamespace(resource.fieldName())),
          globalOutputProjectionsContext
      );

      ProjectionsPrettyPrinterContext<OpProjection<?, ?>>
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
        globalOutputProjectionsContext.projections(),
        namespaces
    );

    printInputProjections(
        null,
        globalInputProjectionsContext,
        globalInputProjectionsContext.projections(),
        namespaces
    );

    printDeleteProjections(
        null,
        globalDeleteProjectionsContext,
        globalDeleteProjectionsContext.projections(),
        namespaces
    );

    l.end();
  }

  private void printTransformer(
      @NotNull Namespaces namespaces,
      @NotNull TransformerDeclaration transformer,
      @NotNull ProjectionsPrettyPrinterContext<OpProjection<?, ?>> globalOutputProjections
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

    ProjectionsPrettyPrinterContext<OpProjection<?, ?>> ipc =
        new ProjectionsPrettyPrinterContext<>(
            ProjectionReferenceName.fromQn(namespaces.projectionsNamespace()),
            globalOutputProjections
        );

    new OpProjectionsPrettyPrinter<>(l, ipc).printProjection(transformer.inputProjection(), 0);
    l.end();


    l.print(",");
    l.brk();

    l.beginIInd();
    l.print("outputProjection").brk();
    ProjectionsPrettyPrinterContext<OpProjection<?, ?>> opc =
        new ProjectionsPrettyPrinterContext<>(
            ProjectionReferenceName.fromQn(namespaces.projectionsNamespace()),
            globalOutputProjections
        );
    new OpProjectionsPrettyPrinter<>(l, opc).printProjection(transformer.outputProjection(), 0);
    l.end();

    l.end(); //2
    l.brk(1, -l.getDefaultIndentation()).print("}");
    l.end(); //1
  }

  private void printResource(
      @NotNull Namespaces namespaces,
      @NotNull ResourceDeclaration resource,
      @NotNull ProjectionsPrettyPrinterContext<OpProjection<?, ?>> resourceOutputProjectionsPrinterContext,
      ProjectionsPrettyPrinterContext<OpProjection<?, ?>> resourceInputProjectionsPrinterContext,
      ProjectionsPrettyPrinterContext<OpProjection<?, ?>> resourceDeleteProjectionsPrinterContext
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

      ProjectionsPrettyPrinterContext<OpProjection<?, ?>>
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

      ProjectionsPrettyPrinterContext<OpProjection<?, ?>>
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

      ProjectionsPrettyPrinterContext<OpProjection<?, ?>>
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
        resourceOutputProjectionsPrinterContext.projections(),
        namespaces
    );
    printInputProjections(
        resourceName,
        resourceInputProjectionsPrinterContext,
        resourceInputProjectionsPrinterContext.projections(),
        namespaces
    );
    printDeleteProjections(
        resourceName,
        resourceDeleteProjectionsPrinterContext,
        resourceDeleteProjectionsPrinterContext.projections(),
        namespaces
    );

    l.end();
    l.brk(1, -l.getDefaultIndentation()).print("}");
    l.end();
  }

  private void printOutputProjections(
      final @Nullable String resourceName,
      final @Nullable ProjectionsPrettyPrinterContext<OpProjection<?, ?>> parent,
      final @NotNull Collection<OpProjection<?, ?>> globalOutputProjections,
      final @NotNull Namespaces namespaces) throws E {

    printGlobalProjections(
        "outputProjection",
        globalOutputProjections,
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
      final @Nullable ProjectionsPrettyPrinterContext<OpProjection<?, ?>> parent,
      final @NotNull Collection<OpProjection<?, ?>> globalInputVarProjections,
      final @NotNull Namespaces namespaces) throws E {

    printGlobalProjections(
        "inputProjection",
        globalInputVarProjections,
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
      final @Nullable ProjectionsPrettyPrinterContext<OpProjection<?, ?>> parent,
      final @NotNull Collection<OpProjection<?, ?>> globalDeleteVarProjections,
      final @NotNull Namespaces namespaces) throws E {

    printGlobalProjections(
        "deleteProjection",
        globalDeleteVarProjections,
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
  private void printGlobalProjections(
      final @NotNull String prefix,
      final @NotNull Collection<OpProjection<?, ?>> projections,
      final @NotNull Function<String, ProjectionsPrettyPrinterContext<OpProjection<?, ?>>> prettyPrinterContextFactory,
      final @NotNull Function<ProjectionsPrettyPrinterContext<OpProjection<?, ?>>, OpProjectionsPrettyPrinter<E>> prettyPrinterFactory
  ) throws E {
    List<ProjectionReferenceName.RefNameSegment> printedRefs = projections.stream()
        .map(GenProjection::referenceName)
        .filter(Objects::nonNull)
        .map(ProjectionReferenceName::last)
        .collect(Collectors.toList());

    Set<ProjectionReferenceName.RefNameSegment> printedNames = new HashSet<>();

    Collection<OpProjection<?, ?>> _projections = new ArrayList<>(projections);

    while (!_projections.isEmpty()) {

      Collection<OpProjection<?, ?>> nextProjections =
          new ArrayList<>(); // projections referenced from printed projections

      for (final OpProjection<?, ?> outputProjection : _projections) {
        final ProjectionReferenceName name = outputProjection.referenceName();
        assert name != null;
        final ProjectionReferenceName.RefNameSegment shortName = name.last();
        assert shortName != null;

        if (!printedNames.contains(shortName)) {
          printedNames.add(shortName);

          final ProjectionsPrettyPrinterContext<OpProjection<?, ?>> printerContext =
              prettyPrinterContextFactory.apply(shortName.toString());

          final OpProjectionsPrettyPrinter<E> projectionsPrettyPrinter =
              prettyPrinterFactory.apply(printerContext);

          projectionsPrettyPrinter.addVisitedRefs(printedRefs);

          l.nl();
          l.beginIInd();
          l.print(prefix).brk().print(shortName.toString()).print(":").brk();
          l.print(outputProjection.type().name().toString()).brk().print("=").brk();
          projectionsPrettyPrinter.printProjectionNoRefCheck(outputProjection, 0);
          l.end();

          ProjectionsPrettyPrinterContext<OpProjection<?, ?>> parentContext = printerContext.parent();
          if (parentContext != null) {
            for (OpProjection<?, ?> p : parentContext.projections()) {
              nextProjections.add(p);
              //noinspection ConstantConditions
              printedRefs.add(p.referenceName().last());
            }
          }

        }
      }

      _projections = nextProjections;
    }
  }
}
