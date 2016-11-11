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

package ws.epigraph.projections;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.projections.gen.GenModelProjection;
import ws.epigraph.projections.gen.GenTagProjectionEntry;
import ws.epigraph.projections.gen.GenVarProjection;
import ws.epigraph.psi.PsiProcessingError;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.TypeRef;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.types.DatumType;
import ws.epigraph.types.Type;
import ws.epigraph.types.TypeKind;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ProjectionsParsingUtil {

  @NotNull
  public static Type.Tag getTag(
      @NotNull Type type,
      @Nullable String tagName,
      @Nullable Type.Tag defaultTag,
      @NotNull PsiElement location,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    final Type.Tag tag;

    if (tagName == null) {
      // get default tag
      if (defaultTag == null) {
        defaultTag = findDefaultTag(type, null, location, errors);

        if (defaultTag == null)
          throw new PsiProcessingException(
              String.format("Can't parse default tag projection for '%s', default tag not specified", type.name()),
              location,
              errors
          );
      }

      tag = defaultTag;
      verifyTag(type, tag, location, errors);
    } else tag = getTag(type, tagName, location, errors);
    return tag;
  }

  @NotNull
  public static Type.Tag getTag(
      @NotNull Type type,
      @NotNull String tagName,
      @NotNull PsiElement location,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    Type.Tag tag = type.tagsMap().get(tagName);
    if (tag == null)
      throw new PsiProcessingException(
          String.format("Unknown tag '%s' in type '%s', known tags: (%s)", tagName, type.name(), listTags(type)),
          location, errors
      );
    return tag;
  }

  public static void verifyTag(
      @NotNull Type type,
      @NotNull Type.Tag tag,
      @NotNull PsiElement location,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    if (!type.tags().contains(tag))
      throw new PsiProcessingException(
          String.format(
              "Tag '%s' doesn't belong to type '%s', known tags: (%s)",
              tag.name(),
              type.name(),
              listTags(type)
          ), location, errors);
  }

  private static String listTags(@NotNull Type type) {
    return type.tags().stream().map(Type.Tag::name).collect(Collectors.joining(","));
  }

  public static <VP extends GenVarProjection<?, ?, ?>> String listTags(@NotNull VP op) {
    return String.join(", ", op.tagProjections().keySet());
  }

  @NotNull
  public static Type getType(
      @NotNull TypeRef typeRef,
      @NotNull TypesResolver resolver,
      @NotNull PsiElement location,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    @Nullable Type type = typeRef.resolve(resolver);
    if (type == null)
      throw new PsiProcessingException(String.format("Can't find type '%s'", typeRef.toString()), location, errors);
    return type;
  }

  /**
   * Finds tag projection by tag name
   */
  @NotNull
  public static <
      MP extends GenModelProjection<?, ?>,
      TP extends GenTagProjectionEntry<MP>,
      VP extends GenVarProjection<VP, TP, MP>
      > TP findTagProjection(
      @NotNull String tagName,
      @NotNull VP op,
      @NotNull PsiElement location,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {
    final TP tagProjection = op.tagProjections().get(tagName);
    if (tagProjection == null) {
      throw new PsiProcessingException(
          String.format("Tag '%s' is unsupported, supported tags: {%s}", tagName, listTags(op)), location, errors);
    }
    return tagProjection;
  }

  /**
   * Finds default tags for a given {@code type}
   * <p>
   * If it's a {@code DatumType}, then default tag is {@code self}, provided that {@code op} contains it.
   * If it's a {@code UnionType}, then all default tags from {@code op} are included.
   */
  @Nullable
  public static <
      MP extends GenModelProjection<?, ?>,
      TP extends GenTagProjectionEntry<MP>,
      VP extends GenVarProjection<VP, TP, MP>
      > Type.Tag findDefaultTag(
      @NotNull Type type,
      @Nullable VP op,
      @NotNull PsiElement locationPsi,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    if (type.kind() != TypeKind.UNION) {
      DatumType datumType = (DatumType) type;
      final Type.@NotNull Tag self = datumType.self;
      if (op != null) findTagProjection(self.name(), op, locationPsi, errors); // check that op contains it
      return self;
    }

    return null;
  }
}
