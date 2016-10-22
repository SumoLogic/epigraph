/* Created by yegor on 10/21/16. */

package io.epigraph.wire.json;

import io.epigraph.wire.WireFormat;
import org.jetbrains.annotations.NotNull;

public class JsonFormat implements WireFormat {

  public static final @NotNull String NAME = "json";

  public static final @NotNull String MIME_TYPE = "application/json";

  public static final @NotNull String ERROR_CODE_FIELD = "ERROR";

  public static final @NotNull String ERROR_MESSAGE_FIELD = "message";

  public static final @NotNull String POLYMORPHIC_TYPE_FIELD = "type";

  public static final @NotNull String POLYMORPHIC_VALUE_FIELD = "data";

  public static final @NotNull String MAP_ENTRY_KEY_FIELD = "key";

  public static final @NotNull String MAP_ENTRY_VALUE_FIELD = "value";

  @Override
  public @NotNull String name() { return NAME; }

  @Override
  public @NotNull String mimeType() { return MIME_TYPE; }

}
