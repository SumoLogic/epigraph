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

package ws.epigraph.idl;

import ws.epigraph.lang.Qn;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class Idl {
  private final @NotNull Qn namespace;
  private final @NotNull Map<String, ResourceIdl> resources;

  public Idl(@NotNull Qn namespace, @NotNull Map<String, ResourceIdl> resources) {
    this.namespace = namespace;
    this.resources = resources;
  }

  public @NotNull Qn namespace() { return namespace; }

  public @NotNull Map<String, ResourceIdl> resources() { return resources; }
}
