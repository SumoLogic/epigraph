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

import java.util.Map;

/**
 * "Resources" part of the schema: describes resources and transformers
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ResourcesSchema {
  private final @NotNull Qn namespace;
  private final @NotNull Map<String, ResourceDeclaration> resources;
  private final @NotNull Map<String, TransformerDeclaration> transformers;

  public ResourcesSchema(
      @NotNull Qn namespace,
      @NotNull Map<String, ResourceDeclaration> resources,
      @NotNull Map<String, TransformerDeclaration> transformers) {
    this.namespace = namespace;
    this.resources = resources;
    this.transformers = transformers;
  }

  public @NotNull Qn namespace() { return namespace; }

  public @NotNull Map<String, ResourceDeclaration> resources() { return resources; }

  public @NotNull Map<String, TransformerDeclaration> transformers() { return transformers; }
}
