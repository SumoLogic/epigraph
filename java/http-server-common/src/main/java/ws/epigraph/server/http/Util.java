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

package ws.epigraph.server.http;

import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.impl.DebugUtil;
import org.jetbrains.annotations.NotNull;
import ws.epigraph.psi.PsiProcessingError;
import ws.epigraph.service.Service;
import ws.epigraph.url.parser.psi.UrlUrl;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class Util {
  private Util() {}

  static @NotNull String dumpUrl(@NotNull UrlUrl url) {
    return DebugUtil.psiToString(url, true, false);
  }

  static @NotNull List<PsiProcessingError> psiErrorsToPsiProcessingErrors(@NotNull List<PsiErrorElement> errors) {
    return errors.stream().map(e -> new PsiProcessingError(e.getErrorDescription(), e)).collect(Collectors.toList());
  }

  static @NotNull String listSupportedResources(@NotNull Service service) {
    return service.resources().keySet().stream().map(n -> "/" + n).collect(Collectors.joining(", "));
  }

  public static @NotNull String decodeUri(@NotNull String uri) throws URISyntaxException {
    final URI _uri = new URI(uri);

    final String decodedPath = _uri.getPath();
    final String decodedQuery = _uri.getQuery();

    return decodedQuery == null ? decodedPath : decodedPath + "?" + decodedQuery;
  }

  // async timeouts support. Use `onTimeout` instead once on JDK9
  private static final ThreadFactory threadFactory = new ThreadFactory() {
    private final AtomicLong counter = new AtomicLong();

    @Override
    public Thread newThread(final @NotNull Runnable r) {
      Thread t = new Thread(r, "failAfter-" + (counter.incrementAndGet()));
      t.setDaemon(true);
      return t;
    }
  };

  private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1, threadFactory);

  static <T> CompletableFuture<T> failAfter(Duration duration) {
    final CompletableFuture<T> promise = new CompletableFuture<>();
    scheduler.schedule(() -> {
      final TimeoutException ex = new TimeoutException("Timeout after " + duration);
      return promise.completeExceptionally(ex);
    }, duration.toMillis(), MILLISECONDS);
    return promise;
  }
}
