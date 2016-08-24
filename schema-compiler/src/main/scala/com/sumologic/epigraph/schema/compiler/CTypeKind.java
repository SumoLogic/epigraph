package com.sumologic.epigraph.schema.compiler;/* Created by yegor on 6/9/16. */

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
