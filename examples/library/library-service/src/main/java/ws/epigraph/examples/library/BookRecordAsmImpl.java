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


import ws.epigraph.examples.library._resources.books.projections.output.bookprojection.BookRecordAsm;
import ws.epigraph.examples.library._resources.books.projections.output.bookprojection.text.TextAsm;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class BookRecordAsmImpl extends BookRecordAsm<BooksBackend.BookData> {
  public static final BookRecordAsmImpl INSTANCE = new BookRecordAsmImpl();

  private BookRecordAsmImpl() {
    super(
        // author
        AuthorAsmImpl.INSTANCE.on(bd -> AuthorsBackend.get(bd.authorId)),

        // text
        new TextAsm<>(PlainTextAsm.INSTANCE),

        // title
        (d, p, c) -> epigraph.String.type.createValueOfNullable(d.title)
    );
  }

}
