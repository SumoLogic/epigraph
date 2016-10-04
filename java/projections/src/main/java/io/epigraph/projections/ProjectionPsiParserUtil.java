package io.epigraph.projections;

import com.intellij.psi.PsiElement;
import io.epigraph.gdata.GDataValue;
import io.epigraph.gdata.IdlGDataPsiParser;
import io.epigraph.idl.parser.psi.*;
import io.epigraph.psi.EpigraphPsiUtil;
import io.epigraph.psi.PsiProcessingException;
import io.epigraph.refs.TypeRef;
import io.epigraph.types.Type;
import io.epigraph.refs.TypesResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ProjectionPsiParserUtil {
  @Nullable
  public static String getTagName(@Nullable IdlTagName tagNamePsi) {
    if (tagNamePsi == null) return null;
    @Nullable IdlQid qid = tagNamePsi.getQid();
    if (qid == null) return null;
    return qid.getCanonicalName();
  }

  @NotNull
  public static Type.Tag getTag(
      @NotNull Type type,
      @Nullable IdlTagName tagName,
      @Nullable Type.Tag defaultTag,
      @NotNull PsiElement location) throws PsiProcessingException {

    String tagNameStr = null;
    if (tagName != null) {
      @Nullable IdlQid qid = tagName.getQid();
      tagNameStr = qid == null ? null : qid.getCanonicalName();
    }

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
          String.format("Can't find tag '%s' in '%s'", tagName, type.name()),
          location
      );
    return tag;
  }

  public static void verifyTag(@NotNull Type type, @NotNull Type.Tag tag, @NotNull PsiElement location)
      throws PsiProcessingException {
    if (!type.tags().contains(tag))
      throw new PsiProcessingException(String.format("Tag '%s' doesn't belong to type '%s'",
                                                     tag.name(),
                                                     type.name()
      ), location);
  }

  @NotNull
  public static Type getType(@NotNull TypeRef typeRef, @NotNull TypesResolver resolver, @NotNull PsiElement location)
      throws PsiProcessingException {
    @Nullable Type type = typeRef.resolve(resolver);
    if (type == null) throw new PsiProcessingException(String.format("Can't find type '%s'", typeRef.toString()), location);
    return type;
  }

  @Nullable
  public static Map<String, Annotation> parseAnnotation(
      @Nullable Map<String, Annotation> annotationsMap,
      @Nullable IdlAnnotation annotationPsi)
      throws PsiProcessingException {

    if (annotationPsi != null) {
      if (annotationsMap == null) annotationsMap = new HashMap<>();
      @Nullable IdlDataValue annotationValuePsi = annotationPsi.getDataValue();
      if (annotationValuePsi != null) {
        @NotNull String annotationName = annotationPsi.getQid().getCanonicalName();
        @NotNull GDataValue annotationValue = IdlGDataPsiParser.parseValue(annotationValuePsi);
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
