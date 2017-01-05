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
import ws.epigraph.gdata.GDataValue;
import ws.epigraph.gdata.GDatum;
import ws.epigraph.schema.TypeRefs;
import ws.epigraph.schema.gdata.SchemaGDataPsiParser;
import ws.epigraph.schema.parser.psi.*;
import ws.epigraph.projections.op.OpParam;
import ws.epigraph.projections.op.OpParams;
import ws.epigraph.projections.op.input.OpInputModelProjection;
import ws.epigraph.projections.op.input.OpInputProjectionsPsiParser;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingError;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.TypeRef;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.types.DatumType;
import ws.epigraph.types.Type;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class SchemaProjectionPsiParserUtil {
  private SchemaProjectionPsiParserUtil() {}
//  @Nullable
//  public static String getTagName(@Nullable SchemaTagName tagNamePsi) {
//    if (tagNamePsi == null) return null;
//    @Nullable SchemaQid qid = tagNamePsi.getQid();
//    if (qid == null) return null;
//    return qid.getCanonicalName();
//  }

  public static @NotNull Type.Tag getTag(
      @NotNull Type type,
      @Nullable SchemaTagName tagName,
      @Nullable Type.Tag defaultTag,
      @NotNull PsiElement location,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    return ProjectionsParsingUtil.getTag(type, getTagNameString(tagName), defaultTag, location, errors);
  }

  public static @Nullable Type.Tag findTag(
      @NotNull Type type,
      @Nullable SchemaTagName tagName,
      @Nullable Type.Tag defaultTag,
      @NotNull PsiElement location,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    return ProjectionsParsingUtil.findTag(type, getTagNameString(tagName), defaultTag, location, errors);
  }

  private static @Nullable String getTagNameString(final @Nullable SchemaTagName tagName) {
    String tagNameStr = null;

    if (tagName != null) {
      final @Nullable SchemaQid qid = tagName.getQid();
      if (qid != null)
        tagNameStr = qid.getCanonicalName();
    }
    return tagNameStr;
  }

  public static @Nullable Map<String, Annotation> parseAnnotation(
      @Nullable Map<String, Annotation> annotationsMap,
      @Nullable SchemaAnnotation annotationPsi,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    if (annotationPsi != null) {
      if (annotationsMap == null) annotationsMap = new HashMap<>();
      @Nullable SchemaDataValue annotationValuePsi = annotationPsi.getDataValue();
      if (annotationValuePsi != null) {
        @NotNull String annotationName = annotationPsi.getQid().getCanonicalName();
        @NotNull GDataValue annotationValue = SchemaGDataPsiParser.parseValue(annotationValuePsi, errors);
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

  public static @NotNull OpParams parseParams(
      @NotNull Stream<SchemaOpParam> paramsPsi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    return parseParams(paramsPsi.collect(Collectors.toList()), resolver, errors);
  }

  public static @NotNull OpParams parseParams(
      @NotNull Iterable<SchemaOpParam> paramsPsi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    Collection<OpParam> params = null;

    for (final SchemaOpParam param : paramsPsi) {
      if (param != null) {
        if (params == null) params = new ArrayList<>();

        params.add(parseParameter(param, resolver, errors));
      }
    }

    return OpParams.fromCollection(params);
  }

  public static @NotNull OpParam parseParameter(
      @NotNull SchemaOpParam paramPsi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {
    @Nullable SchemaQid qid = paramPsi.getQid();
    if (qid == null) throw new PsiProcessingException("Parameter name not specified", paramPsi, errors);
    @NotNull String paramName = qid.getCanonicalName();

    @Nullable SchemaTypeRef typeRef = paramPsi.getTypeRef();
    if (typeRef == null)
      throw new PsiProcessingException(String.format("Parameter '%s' type not specified", paramName), paramPsi, errors);
    @NotNull TypeRef paramTypeRef = TypeRefs.fromPsi(typeRef, errors);
    @Nullable DatumType paramType = paramTypeRef.resolveDatumType(resolver);

    if (paramType == null)
      throw new PsiProcessingException(
          String.format("Can't resolve parameter '%s' data type '%s'", paramName, paramTypeRef), paramPsi, errors
      );

    @Nullable SchemaOpInputModelProjection paramModelProjectionPsi = paramPsi.getOpInputModelProjection();

    final @NotNull OpParams params = parseParams(paramPsi.getOpParamList(), resolver, errors);
    @NotNull Annotations annotations = parseAnnotations(paramPsi.getAnnotationList(), errors);

    @Nullable SchemaDatum defaultValuePsi = paramPsi.getDatum();
    @Nullable GDatum defaultValue = defaultValuePsi == null
                                    ? null
                                    : SchemaGDataPsiParser.parseDatum(defaultValuePsi, errors);

    final OpInputModelProjection<?, ?, ?> paramModelProjection;

    if (paramModelProjectionPsi == null)
      paramModelProjection = OpInputProjectionsPsiParser.createDefaultModelProjection(
          paramType,
          paramPsi.getPlus() != null,
          defaultValue,
          params,
          annotations,
          paramPsi,
          resolver,
          errors
      );
    else paramModelProjection = OpInputProjectionsPsiParser.parseModelProjection(
        paramType,
        paramPsi.getPlus() != null,
        defaultValue,
        params,
        annotations,
        null, // TODO do we want to support metadata on parameters?
        paramModelProjectionPsi,
        resolver,
        errors
    );

    return new OpParam(paramName, paramModelProjection, EpigraphPsiUtil.getLocation(paramPsi));
  }

  public static @NotNull Annotations parseAnnotations(
      @NotNull Stream<SchemaAnnotation> annotationsPsi,
      @NotNull List<PsiProcessingError> errors
  ) throws PsiProcessingException {
    return parseAnnotations(annotationsPsi.collect(Collectors.toList()), errors);
  }

  public static @NotNull Annotations parseAnnotations(
      @NotNull Iterable<SchemaAnnotation> annotationsPsi,
      @NotNull List<PsiProcessingError> errors
  ) throws PsiProcessingException {
    @Nullable Map<String, Annotation> annotationMap = null;
    for (final SchemaAnnotation annotationPsi : annotationsPsi) {
      annotationMap = parseAnnotation(annotationMap, annotationPsi, errors);
    }

    return Annotations.fromMap(annotationMap);
  }
}
