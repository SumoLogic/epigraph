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
import ws.epigraph.lang.Qn;
import ws.epigraph.projections.ProjectionsPrettyPrinterContext;
import ws.epigraph.projections.abs.AbstractProjectionsPrettyPrinter;
import ws.epigraph.projections.gen.GenVarProjection;
import ws.epigraph.projections.op.delete.OpDeleteProjectionsPrettyPrinter;
import ws.epigraph.projections.op.delete.OpDeleteVarProjection;
import ws.epigraph.projections.op.input.OpInputProjectionsPrettyPrinter;
import ws.epigraph.projections.op.input.OpInputVarProjection;
import ws.epigraph.projections.op.output.OpOutputProjectionsPrettyPrinter;
import ws.epigraph.projections.op.output.OpOutputVarProjection;
import ws.epigraph.schema.operations.OperationDeclaration;
import ws.epigraph.schema.operations.OperationsPrettyPrinter;
import ws.epigraph.types.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

    for (ResourceDeclaration resource : schema.resources().values())
      printResource(Qn.fromDotSeparated(namespaceString), resource);

    l.end();
  }

  private void printResource(@NotNull Qn namespace, @NotNull ResourceDeclaration resource) throws E {
    l.beginCInd(0);
    l.beginIInd(0);
    l.print("resource").brk();
    l.print(resource.fieldName()).print(":").brk();

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

    Namespaces namespaces = new Namespaces(namespace);

    List<OpOutputVarProjection> globalOutputProjections = new ArrayList<>();
    List<OpInputVarProjection> globalInputProjections = new ArrayList<>();
    List<OpDeleteVarProjection> globalDeleteProjections = new ArrayList<>();

    for (OperationDeclaration operation : resource.operations()) {

      ProjectionsPrettyPrinterContext<OpOutputVarProjection> operationOutputProjectionsContext =
          new ProjectionsPrettyPrinterContext<>(
              namespaces.operationOutputProjectionsNamespace(
                  resource.fieldName(),
                  operation.kind(),
                  operation.name()
              )
          );
      ProjectionsPrettyPrinterContext<OpInputVarProjection> operationInputProjectionsContext =
          new ProjectionsPrettyPrinterContext<>(
              namespaces.operationInputProjectionsNamespace(
                  resource.fieldName(),
                  operation.kind(),
                  operation.name()
              )
          );
      ProjectionsPrettyPrinterContext<OpDeleteVarProjection> operationDeleteProjectionsContext =
          new ProjectionsPrettyPrinterContext<>(
              namespaces.operationDeleteProjectionsNamespace(
                  resource.fieldName(),
                  operation.kind(),
                  operation.name()
              )
          );

      l.nl();
      operationsPrettyPrinter.printOperation(
          operation,
          operationOutputProjectionsContext,
          operationInputProjectionsContext,
          operationDeleteProjectionsContext
      );

      globalOutputProjections.addAll(operationOutputProjectionsContext.otherNamespaceProjections());
      globalInputProjections.addAll(operationInputProjectionsContext.otherNamespaceProjections());
      globalDeleteProjections.addAll(operationDeleteProjectionsContext.otherNamespaceProjections());
    }

    printGlobalProjections("outputProjection", globalOutputProjections, new OpOutputProjectionsPrettyPrinter<>(l));
    printGlobalProjections("inputProjection", globalInputProjections, new OpInputProjectionsPrettyPrinter<>(l));
    printGlobalProjections("deleteProjection", globalDeleteProjections, new OpDeleteProjectionsPrettyPrinter<>(l));

    l.end();
    l.brk(1, -l.getDefaultIndentation()).print("}");
    l.end();
  }

  @SuppressWarnings("unchecked")
  private <VP extends GenVarProjection<VP, ?, ?>> void printGlobalProjections(
      @NotNull String prefix,
      @NotNull List<VP> globalProjections,
      @NotNull AbstractProjectionsPrettyPrinter<VP, ?, ?, E> projectionsPrettyPrinter
  ) throws E {

    if (!globalProjections.isEmpty()) {
      projectionsPrettyPrinter.addVisitedRefs(
          globalProjections.stream()
              .map(GenVarProjection::name)
              .map(Qn::last)
              .collect(Collectors.toList())
      );

      Set<String> printedNames = new HashSet<>();
      for (final VP outputProjection : globalProjections) {
        final Qn name = outputProjection.name();
        assert name != null;
        final String shortName = name.last();
        if (!printedNames.contains(shortName)) {
          printedNames.add(shortName);
          l.nl();
          l.beginIInd();
          l.print(prefix).brk().print(shortName).print(":").brk();
          l.print(outputProjection.type().name().toString()).brk().print("=").brk();
          projectionsPrettyPrinter.printVarNoRefCheck(outputProjection, 0);
          l.end();
        }
      }
    }
  }
}
