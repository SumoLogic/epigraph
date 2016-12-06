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

/* Created by yegor on 10/27/16. */

package ws.epigraph.server.http;

import org.jetbrains.annotations.NotNull;
import ws.epigraph.idl.ResourceIdl;
import ws.epigraph.idl.operations.ReadOperationIdl;
import ws.epigraph.service.Resource;
import ws.epigraph.service.ServiceInitializationException;
import ws.epigraph.service.operations.ReadOperation;
import ws.epigraph.service.operations.ReadOperationRequest;
import ws.epigraph.service.operations.ReadOperationResponse;
import ws.epigraph.tests.PersonId_Person_Map;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;

public class UsersResource extends Resource {
  public UsersResource(@NotNull ResourceIdl resourceIdl, @NotNull UsersStorage storage)
      throws ServiceInitializationException {

    super(
        resourceIdl,
        Collections.singletonList(
            new ReadOp(((ReadOperationIdl) resourceIdl.operations().get(0)), storage)
        ),
        Collections.emptyList(),
        Collections.emptyList(),
        Collections.emptyList(),
        Collections.emptyList()
    );

  }


  private static final class ReadOp extends ReadOperation<PersonId_Person_Map.Data> {
    private final @NotNull UsersStorage storage;

    ReadOp(@NotNull ReadOperationIdl declaration, @NotNull UsersStorage storage) {
      super(declaration);
      this.storage = storage;
    }

    @Override
    public @NotNull CompletableFuture<ReadOperationResponse<PersonId_Person_Map.Data>>
    process(@NotNull ReadOperationRequest request) {
      CompletableFuture<ReadOperationResponse<PersonId_Person_Map.Data>> future = new CompletableFuture<>();
      future.complete(
          new ReadOperationResponse<>(
              PersonId_Person_Map.type.createDataBuilder().set(storage.users())
          )
      );
      return future;
    }


  }

}
