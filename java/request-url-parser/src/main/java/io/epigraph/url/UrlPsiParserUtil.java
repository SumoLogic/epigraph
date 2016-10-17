package io.epigraph.url;

import com.intellij.psi.PsiElement;
import io.epigraph.gdata.GDataValue;
import io.epigraph.projections.Annotation;
import io.epigraph.psi.EpigraphPsiUtil;
import io.epigraph.psi.PsiProcessingException;
import io.epigraph.refs.TypeRef;
import io.epigraph.refs.TypesResolver;
import io.epigraph.types.Type;
import io.epigraph.url.gdata.UrlGDataPsiParser;
import io.epigraph.url.parser.psi.UrlAnnotation;
import io.epigraph.url.parser.psi.UrlDataValue;
import io.epigraph.url.parser.psi.UrlTagName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class UrlPsiParserUtil {
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
          String.format("Unknown tag '%s' in type '%s', known tags: {%s}", tagName, type.name(), listTags(type)),
          location
      );
    return tag;
  }

  public static void verifyTag(@NotNull Type type, @NotNull Type.Tag tag, @NotNull PsiElement location)
      throws PsiProcessingException {
    if (!type.tags().contains(tag))
      throw new PsiProcessingException(
          String.format("Tag '%s' doesn't belong to type '%s', known tags: {%s}",
                        tag.name(),
                        type.name(),
                        listTags(type)
          ), location);
  }

  private static String listTags(@NotNull Type type) {
    return type.tags().stream().map(Type.Tag::name).collect(Collectors.joining(", "));
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
