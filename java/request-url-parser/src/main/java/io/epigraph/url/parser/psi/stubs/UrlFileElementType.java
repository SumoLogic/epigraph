package io.epigraph.url.parser.psi.stubs;

import com.intellij.psi.tree.IFileElementType;
import io.epigraph.url.parser.UrlLanguage;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class UrlFileElementType extends IFileElementType {
  public UrlFileElementType() {
    super("epigraph_url.FILE", UrlLanguage.INSTANCE);
  }
}
