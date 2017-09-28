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

import org.jetbrains.annotations.NotNull;
import ws.epigraph.lang.Qn;
import ws.epigraph.schema.operations.OperationKind;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class Namespaces {
  public static final String TAILS_SEGMENT = "_t";
  public static final String NORMALIZED_TAILS_SEGMENT = "_nt";

  public static final String PROJECTIONS_SEGMENT = "projections";
  public static final String PATH_SEGMENT = "path";
  public static final String OUTPUT_SEGMENT = "output";
  public static final String INPUT_SEGMENT = "input";
  public static final String DELETE_SEGMENT = "delete";
  public static final String RESOURCES_SEGMENT = "_resources";
  public static final String OPERATIONS_SEGMENT = "operations";
  public static final String TRANSFORMERS_SEGMENT = "_transformers";
  public static final String CLIENT_SEGMENT = "client";

  private final @NotNull Qn namespace;

  public Namespaces(final @NotNull Qn namespace) {this.namespace = namespace;}

  // global stuff

  public @NotNull Qn projectionsNamespace() { return namespace.append("_" + PROJECTIONS_SEGMENT); }

  public @NotNull Qn inputProjectionsNamespace() { return projectionsNamespace().append(INPUT_SEGMENT); }

  public @NotNull Qn inputProjectionNamespace(@NotNull String projectionName) {
    return inputProjectionsNamespace().append(projectionName.toLowerCase());
  }

  public @NotNull Qn outputProjectionsNamespace() { return projectionsNamespace().append(OUTPUT_SEGMENT); }

  public @NotNull Qn outputProjectionNamespace(@NotNull String projectionName) {
    return outputProjectionsNamespace().append(projectionName.toLowerCase());
  }

  public @NotNull Qn deleteProjectionsNamespace() { return projectionsNamespace().append(DELETE_SEGMENT); }

  public @NotNull Qn deleteProjectionNamespace(@NotNull String projectionName) {
    return deleteProjectionsNamespace().append(projectionName.toLowerCase());
  }

  // transformers

  public @NotNull Qn transformersNamespace() { return namespace.append(TRANSFORMERS_SEGMENT); }

  public @NotNull Qn transformerNamespace(@NotNull String transformerName) {
    return transformersNamespace().append(transformerName);
  }

  // resource stuff

  public @NotNull Qn resourcesNamespace() { return namespace.append(RESOURCES_SEGMENT); }

  public @NotNull Qn resourceNamespace(@NotNull String resourceName) {
    return resourcesNamespace().append(resourceName);
  }

  public @NotNull Qn clientNamespace(@NotNull String resourceName) {
    return resourceNamespace(resourceName.toLowerCase()).append(CLIENT_SEGMENT);
  }

  public @NotNull Qn projectionsNamespace(@NotNull String resourceName) {
    return resourceNamespace(resourceName).append(PROJECTIONS_SEGMENT);
  }

  public @NotNull Qn inputProjectionsNamespace(@NotNull String resourceName) {
    return projectionsNamespace(resourceName).append(INPUT_SEGMENT);
  }

  public @NotNull Qn inputProjectionNamespace(@NotNull String resourceName, @NotNull String projectionName) {
    return inputProjectionsNamespace(resourceName).append(projectionName.toLowerCase());
  }

  public @NotNull Qn outputProjectionsNamespace(@NotNull String resourceName) {
    return projectionsNamespace(resourceName).append(OUTPUT_SEGMENT);
  }

  public @NotNull Qn outputProjectionNamespace(@NotNull String resourceName, @NotNull String projectionName) {
    return outputProjectionsNamespace(resourceName).append(projectionName.toLowerCase());
  }

  public @NotNull Qn deleteProjectionsNamespace(@NotNull String resourceName) {
    return projectionsNamespace(resourceName).append(DELETE_SEGMENT);
  }

  public @NotNull Qn deleteProjectionNamespace(@NotNull String resourceName, @NotNull String projectionName) {
    return deleteProjectionsNamespace(resourceName).append(projectionName.toLowerCase());
  }

  public @NotNull Qn operationsNamespace(@NotNull String resourceName) {
    return resourceNamespace(resourceName).append(OPERATIONS_SEGMENT);
  }

  public @NotNull Qn operationNamespace(
      @NotNull String resourceName,
      @NotNull OperationKind operationKind,
      @NotNull String operationNameOrDefaultName) {

    return operationsNamespace(resourceName)
        .append(operationKind.toString().toLowerCase())
        .append(operationNameOrDefaultName.toLowerCase());
  }

  public @NotNull Qn operationProjectionsNamespace(
      @NotNull String resourceName,
      @NotNull OperationKind operationKind,
      @NotNull String operationNameOrDefaultName) {

    return operationNamespace(resourceName, operationKind, operationNameOrDefaultName).append(PROJECTIONS_SEGMENT);
  }

  public @NotNull Qn operationPathProjectionsNamespace(
      @NotNull String resourceName,
      @NotNull OperationKind operationKind,
      @NotNull String operationNameOrDefaultName) {

    return operationProjectionsNamespace(resourceName, operationKind, operationNameOrDefaultName).append(PATH_SEGMENT);
  }

  public @NotNull Qn operationOutputProjectionsNamespace(
      @NotNull String resourceName,
      @NotNull OperationKind operationKind,
      @NotNull String operationNameOrDefaultName) {

    return operationProjectionsNamespace(
        resourceName,
        operationKind,
        operationNameOrDefaultName
    ).append(OUTPUT_SEGMENT);
  }

  public @NotNull Qn operationInputProjectionsNamespace(
      @NotNull String resourceName,
      @NotNull OperationKind operationKind,
      @NotNull String operationNameOrResourceName) {

    return operationProjectionsNamespace(
        resourceName,
        operationKind,
        operationNameOrResourceName
    ).append(INPUT_SEGMENT);
  }

  public @NotNull Qn operationDeleteProjectionsNamespace(
      @NotNull String resourceName,
      @NotNull OperationKind operationKind,
      @NotNull String operationNameOrResourceName) {

    return operationProjectionsNamespace(
        resourceName,
        operationKind,
        operationNameOrResourceName
    ).append(DELETE_SEGMENT);
  }
}
