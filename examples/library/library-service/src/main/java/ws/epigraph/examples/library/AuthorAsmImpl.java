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

import ws.epigraph.examples.library._resources.books.projections.output.bookprojection.author.AuthorAsm;
import ws.epigraph.examples.library._resources.books.projections.output.bookprojection.author.record.AuthorRecordAsm;
import epigraph.String;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class AuthorAsmImpl extends AuthorAsm<AuthorsBackend.AuthorData> {
  public static final AuthorAsmImpl INSTANCE = new AuthorAsmImpl();

  private AuthorAsmImpl() {
    super(
        // :id model assembler
        (dto, proj, c) -> AuthorId.create(dto.id).asValue(),

        // :record model assembler
        new AuthorRecordAsm<>(
            (dto, proj, c) -> String.type.createValueOfNullable(dto.firstName),
            (dto, proj, c) -> String.type.createValueOfNullable(dto.lastName),
            (dto, proj, c) -> String.type.createValueOfNullable(dto.middleName)
        )
    );
  }
}
