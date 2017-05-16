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

package ws.epigraph.client.http;

import org.apache.http.nio.entity.HttpAsyncContentProducer;
import org.jetbrains.annotations.NotNull;
import ws.epigraph.http.ContentType;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class HttpContentProducer {
  private final @NotNull ContentType contentType;
  private final @NotNull HttpAsyncContentProducer contentProducer;

  public HttpContentProducer(
      final @NotNull ContentType type,
      final @NotNull HttpAsyncContentProducer producer) {
    contentType = type;
    contentProducer = producer;
  }

  public @NotNull ContentType contentType() { return contentType; }

  public @NotNull HttpAsyncContentProducer httpContentProducer() { return contentProducer; }
}
