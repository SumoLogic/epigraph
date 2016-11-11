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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.gdata.GDataValue;
import ws.epigraph.projections.Annotation;
import ws.epigraph.projections.ProjectionsParsingUtil;
import ws.epigraph.projections.gen.GenModelProjection;
import ws.epigraph.projections.gen.GenTagProjectionEntry;
import ws.epigraph.projections.gen.GenVarProjection;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingError;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.types.Type;
import ws.epigraph.url.gdata.UrlGDataPsiParser;
import ws.epigraph.url.parser.psi.UrlAnnotation;
import ws.epigraph.url.parser.psi.UrlDataValue;
import ws.epigraph.url.parser.psi.UrlTagName;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ws.epigraph.projections.ProjectionsParsingUtil.findDefaultTag;
import static ws.epigraph.projections.ProjectionsParsingUtil.findTagProjection;
import static ws.epigraph.projections.ProjectionsParsingUtil.listTags;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class UrlProjectionsPsiParserUtil {
//  @Contract("null -> null; !null -> !null")
//  @Nullable
//  public static String getTagName(@Nullable UrlTagName tagNamePsi) {
//    if (tagNamePsi == null) return null;
//    return tagNamePsi.getQid().getCanonicalName();
//  }

  /**
   * Finds supported tag with a given name in type {@code type} if {@code idlTagName} is not null.
   * <p>
   * Otherwise gets {@link ProjectionsParsingUtil#findDefaultTag(Type, GenVarProjection, PsiElement, List)}
   * default tag} and, if not {@code null}, returns it; otherwise fails.
   */
  @NotNull
  public static <
      MP extends GenModelProjection<?, ?>,
      TP extends GenTagProjectionEntry<MP>,
      VP extends GenVarProjection<VP, TP, MP>>
  Type.Tag findTagOrDefaultTag(
      @NotNull Type type,
      @Nullable UrlTagName idlTagName,
      @NotNull VP opOutputVarProjection,
      @NotNull PsiElement location,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    if (idlTagName != null) return findTag(idlTagName, opOutputVarProjection, location, errors);
    else {
      final Type.@Nullable Tag defaultTag = findDefaultTag(type, opOutputVarProjection, location, errors);

      if (defaultTag != null) return defaultTag;

      throw new PsiProcessingException(
          String.format(
              "Can't build projection for type '%s': no tags specified. Supported tags: {%s}",
              type.name(),
              listTags(opOutputVarProjection)
          ),
          location,
          errors
      );
    }
  }


  /**
   * Finds supported tag with a given name in type {@code type}
   */
  @NotNull
  public static <
      MP extends GenModelProjection<?, ?>,
      TP extends GenTagProjectionEntry<MP>,
      VP extends GenVarProjection<VP, TP, MP>>
  Type.Tag findTag(
      @NotNull UrlTagName idlTagName,
      @NotNull VP varProjection,
      @NotNull PsiElement location,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    return findTagProjection(idlTagName.getQid().getCanonicalName(), varProjection, location, errors).tag();
  }

  @NotNull
  public static Type.Tag getTag(
      @NotNull Type type,
      @Nullable UrlTagName tagName,
      @Nullable Type.Tag defaultTag,
      @NotNull PsiElement location,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    String tagNameStr = null;

    if (tagName != null)
      tagNameStr = tagName.getQid().getCanonicalName();

    return ProjectionsParsingUtil.getTag(type, tagNameStr, defaultTag, location, errors);
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
        annotationsMap.put(
            annotationName,
            new Annotation(
                annotationName,
                annotationValue,
                EpigraphPsiUtil.getLocation(annotationPsi)
            )
        );
      }
    }
    return annotationsMap;
  }
}
