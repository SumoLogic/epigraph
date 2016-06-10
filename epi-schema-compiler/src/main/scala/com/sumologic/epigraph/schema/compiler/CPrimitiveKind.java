package com.sumologic.epigraph.schema.compiler;/* Created by yegor on 6/9/16. */

import com.intellij.util.containers.HashMap;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public enum CPrimitiveKind {

  STRING("string"),
  INTEGER("integer"),
  LONG("long"),
  DOUBLE("double"),
  BOOLEAN("boolean");

  private static final Map<String, CPrimitiveKind> map = new HashMap<>();

  final String keyword;

  CPrimitiveKind(String keyword) {
    this.keyword = keyword;
  }

  static {
    for (CPrimitiveKind value : values()) map.put(value.keyword, value);
  }

  /**
   * @param keyword
   * @throws IllegalArgumentException
   */
  public static CPrimitiveKind forKeyword(@NotNull String keyword) throws IllegalArgumentException {
    CPrimitiveKind value = map.get(keyword);
    if (value == null) throw new IllegalArgumentException(keyword);
    return value;
  }

}
