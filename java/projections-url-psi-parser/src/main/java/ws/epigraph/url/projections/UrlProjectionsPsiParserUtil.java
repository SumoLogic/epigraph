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

package ws.epigraph.url.projections;

import com.intellij.psi.PsiElement;
import ws.epigraph.gdata.GDataValue;
import ws.epigraph.projections.Annotation;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.TypeRef;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.types.Type;
import ws.epigraph.url.gdata.UrlGDataPsiParser;
import ws.epigraph.url.parser.psi.UrlAnnotation;
import ws.epigraph.url.parser.psi.UrlDataValue;
import ws.epigraph.url.parser.psi.UrlTagName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class UrlProjectionsPsiParserUtil {
  @Nullable
  public static String getTagName(@Nullable UrlTagName tagNamePsi) {
    if (tagNamePsi == null) return null;
    return tagNamePsi.getQid().getCanonicalName();
  }

  @NotNull
  public static Type.Tag getTag(
      @NotNull Type type,
      @Nullable UrlTagName tagName,
      @Nullable Type.Tag defaultTag,
      @NotNull PsiElement location) throws PsiProcessingException {

    String tagNameStr = null;

    if (tagName != null)
      tagNameStr = tagName.getQid().getCanonicalName();

    return getTag(type, tagNameStr, defaultTag, location);
  }

  @NotNull
  public static Type.Tag getTag(
      @NotNull Type type,
      @Nullable String tagName,
      @Nullable Type.Tag defaultTag,
      @NotNull PsiElement location) throws PsiProcessingException {

    final Type.Tag tag;

    if (tagName == null) {
      // get default tag
      if (defaultTag == null)
        throw new PsiProcessingException(
            String.format("Can't parse default tag projection for '%s', default tag not specified", type.name()),
            location
        );

      tag = defaultTag;
      verifyTag(type, tag, location);
    } else tag = getTag(type, tagName, location);
    return tag;
  }

  @NotNull
  public static Type.Tag getTag(@NotNull Type type, @NotNull String tagName, @NotNull PsiElement location)
      throws PsiProcessingException {
    Type.Tag tag = type.tagsMap().get(tagName);
    if (tag == null)
      throw new PsiProcessingException(
          String.format("Unknown tag '%s' in type '%s', known tags: (%s)", tagName, type.name(), listTags(type)),
          location
      );
    return tag;
  }

  public static void verifyTag(@NotNull Type type, @NotNull Type.Tag tag, @NotNull PsiElement location)
      throws PsiProcessingException {
    if (!type.tags().contains(tag))
      throw new PsiProcessingException(
          String.format("Tag '%s' doesn't belong to type '%s', known tags: (%s)",
                        tag.name(),
                        type.name(),
                        listTags(type)
          ), location);
  }

  private static String listTags(@NotNull Type type) {
    return type.tags().stream().map(Type.Tag::name).collect(Collectors.joining(","));
  }

  @NotNull
  public static Type getType(@NotNull TypeRef typeRef, @NotNull TypesResolver resolver, @NotNull PsiElement location)
      throws PsiProcessingException {
    @Nullable Type type = typeRef.resolve(resolver);
    if (type == null)
      throw new PsiProcessingException(String.format("Can't find type '%s'", typeRef.toString()), location);
    return type;
  }

  @Nullable
  public static Map<String, Annotation> parseAnnotation(
      @Nullable Map<String, Annotation> annotationsMap,
      @Nullable UrlAnnotation annotationPsi)
      throws PsiProcessingException {

    if (annotationPsi != null) {
      if (annotationsMap == null) annotationsMap = new HashMap<>();
      @Nullable UrlDataValue annotationValuePsi = annotationPsi.getDataValue();
      if (annotationValuePsi != null) {
        @NotNull String annotationName = annotationPsi.getQid().getCanonicalName();
        @NotNull GDataValue annotationValue = UrlGDataPsiParser.parseValue(annotationValuePsi);
        annotationsMap.put(annotationName,
                           new Annotation(annotationName,
                                          annotationValue,
                                          EpigraphPsiUtil.getLocation(annotationPsi)
                           )
        );
      }
    }
    return annotationsMap;
  }
}
