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

package ws.epigraph.examples.library;

import org.jetbrains.annotations.NotNull;
import ws.epigraph.examples.library._resources.books.AbstractBooksResourceFactory;
import ws.epigraph.schema.operations.CreateOperationDeclaration;
import ws.epigraph.schema.operations.DeleteOperationDeclaration;
import ws.epigraph.schema.operations.ReadOperationDeclaration;
import ws.epigraph.schema.operations.UpdateOperationDeclaration;
import ws.epigraph.service.ServiceInitializationException;
import ws.epigraph.service.operations.CreateOperation;
import ws.epigraph.service.operations.DeleteOperation;
import ws.epigraph.service.operations.ReadOperation;
import ws.epigraph.service.operations.UpdateOperation;

/**
 * Books resource factory, responsible for constructing all declared operations implementations
 */
public class BooksResourceFactory extends AbstractBooksResourceFactory {

  @Override
  protected @NotNull ReadOperation<BookId_BookRecord_Map.Data> constructReadOperation(
      @NotNull ReadOperationDeclaration operationDeclaration) throws ServiceInitializationException {
     return new BooksReadOperation(operationDeclaration);
//   return new AsmBasedBooksReadOperation(operationDeclaration);
  }

  @Override
  protected @NotNull ReadOperation<BookId_BookRecord_Map.Data> constructSearchByAuthorReadOperation(
      @NotNull ReadOperationDeclaration operationDeclaration) throws ServiceInitializationException {
    return new SearchByAuthorOperation(operationDeclaration);
  }

  @Override
  protected @NotNull CreateOperation<BookId_List.Data> constructCreateOperation(
      @NotNull CreateOperationDeclaration operationDeclaration) throws ServiceInitializationException {
    return new BooksCreateOperation(operationDeclaration);
  }

  @Override
  protected @NotNull UpdateOperation<BookId_BookRecord_Map.Data> constructUpdateOperation(
      @NotNull UpdateOperationDeclaration operationDeclaration) throws ServiceInitializationException {
    return new BooksUpdateOperation(operationDeclaration);
  }

  @Override
  protected @NotNull DeleteOperation<BookId_BookRecord_Map.Data> constructDeleteOperation(
      @NotNull DeleteOperationDeclaration operationDeclaration) throws ServiceInitializationException {
    return new BooksDeleteOperation(operationDeclaration);
  }

}
