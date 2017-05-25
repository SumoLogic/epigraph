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

import org.apache.http.HttpHost;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import ws.epigraph.examples.library.resources.books.client.BooksClient;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class LibraryClient {
  public static final int PORT = 8888;
  public static final String HOST = "localhost";

  public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

    try (CloseableHttpAsyncClient httpClient = HttpAsyncClients.createDefault()) {
      httpClient.start();
      BooksClient client = new BooksClient(new HttpHost(HOST, PORT), httpClient);

      //                                          create a book
      BookId_List createdIds = client.create(
          null, // no input projection
          BookRecord_List.create()
              .add(BookRecord.create()
                  .setAuthor(Author.create().setId(AuthorId.create(1L)))
                  .setTitle("Quantum Physics for Dummies")
                  .setText(Text.create()
                      .setPlain(PlainText.create(
                          "One of the central problems of quantum mechanics is to calculate the energy levels of a " +
                          "system. The energy operator, called the Hamiltonian, abbreviated H, gives you the " +
                          "total energy.")
                      )
                  )
              ),
          "*" // output projection: get all list elements
      ).get();// it's a Future, so we have to get()

      BookId createdId = createdIds.datums().iterator().next(); // not checking for errors to keep things simple


      //                                          search
      BookId_BookRecord_Map booksByAllan = client.readSearchByAuthor(";author={firstName:'Allan'}[](title)").get();

      System.out.println("Books by Allan:");
      for (BookRecord bookRecord : booksByAllan.datums().values()) {
        System.out.println(bookRecord.getTitle());
      }


      //                                          update
      client.update(
          null, // no update projection
          BookId_BookRecord_Map.create()  // change created book's author id to 2
              .put(createdId, BookRecord.create().setAuthor(Author.create().setId(AuthorId.create(2L)))),
          "" // not interested in output
      );


      //                                          read
      BookId_BookRecord_Map booksMap =
          client.read("[" + createdId + "](+author:+record(+firstName,+lastName))").get(); // '+' is 'required'
      BookRecord bookRecord = booksMap.datums().get(createdId.toImmutable());
      AuthorRecord authorRecord = bookRecord.getAuthor().getRecord();
      System.out.println("\nNew author: " + authorRecord.getFirstName() + " " + authorRecord.getLastName());


      //                                          delete
      client.delete("[" + createdId + "]", "");
    }

  }
}
