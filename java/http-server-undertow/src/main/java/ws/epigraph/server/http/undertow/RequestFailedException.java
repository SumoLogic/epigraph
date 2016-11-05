package ws.epigraph.server.http.undertow;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class RequestFailedException extends Exception {
  @SuppressWarnings("ThrowableInstanceNeverThrown")
  static final RequestFailedException INSTANCE = new RequestFailedException();
}
