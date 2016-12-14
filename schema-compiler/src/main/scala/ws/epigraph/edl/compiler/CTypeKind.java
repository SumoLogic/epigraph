/*
 * Copyright 2016 Sumo Logic
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

package ws.epigraph.edl.compiler;/* Created by yegor on 6/9/16. */

import com.intellij.util.containers.HashMap;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public enum CTypeKind {

  VARTYPE("vartype"),
  RECORD("record"),
  MAP("map"),
  LIST("list"),
  ENUM("enum"),
  STRING("string"),
  INTEGER("integer"),
  LONG("long"),
  DOUBLE("double"),
  BOOLEAN("boolean");

  private static final Map<String, CTypeKind> map = new HashMap<>();

  public final String keyword;

  CTypeKind(String keyword) { this.keyword = keyword; }

  static { for (CTypeKind value : values()) map.put(value.keyword, value); }

  /**
   * @param keyword
   * @throws IllegalArgumentException
   */
  public static CTypeKind forKeyword(@NotNull String keyword) throws IllegalArgumentException {
    CTypeKind value = map.get(keyword);
    if (value == null) throw new IllegalArgumentException(keyword);
    return value;
  }

}
