package ws.epigraph.service;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ServiceInitializationException extends Exception {
  public ServiceInitializationException(String message) {
    super(message);
  }

  public ServiceInitializationException(String message, Throwable cause) {
    super(message, cause);
  }
}
