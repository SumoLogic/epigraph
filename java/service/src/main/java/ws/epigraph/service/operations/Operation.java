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

package ws.epigraph.service.operations;

import ws.epigraph.idl.operations.OperationIdl;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class Operation<
    Decl extends OperationIdl,
    Req extends OperationRequest,
    Rsp extends OperationResponse> {

  private final @NotNull Decl declaration;

  protected Operation(@NotNull Decl declaration) {this.declaration = declaration;}

  public @NotNull Decl declaration() { return declaration; }

  public abstract @NotNull CompletableFuture<Rsp> process(@NotNull Req request);
}
