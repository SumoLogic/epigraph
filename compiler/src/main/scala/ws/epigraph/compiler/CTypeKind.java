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

package ws.epigraph.compiler;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/* Created by yegor on 6/9/16. */
public enum CTypeKind {

  ENTITY("entity"),
  RECORD("record"),
  MAP("map"),
  LIST("list"),
  ENUM("enum"),
  STRING("string"),
  INTEGER("integer"),
  LONG("long"),
  DOUBLE("double"),
  BOOLEAN("boolean");

  private static final @NotNull Map<String, CTypeKind> map = new HashMap<>();

  public final String keyword;

  CTypeKind(String keyword) { this.keyword = keyword; }

  static { for (CTypeKind value : values()) map.put(value.keyword, value); }

  public static CTypeKind forKeyword(@NotNull String keyword) throws IllegalArgumentException {
    CTypeKind value = map.get(keyword);
    if (value == null) throw new IllegalArgumentException(keyword);
    return value;
  }

  @Contract(pure = true)
  public boolean isPrimitive() {
    switch (this) {
      case STRING:
      case INTEGER:
      case LONG:
      case DOUBLE:
      case BOOLEAN:
        return true;
      default:
        return false;
    }
  }

}
