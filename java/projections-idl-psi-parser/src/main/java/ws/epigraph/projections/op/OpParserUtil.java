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

package ws.epigraph.projections.op;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.gdata.GDatum;
import ws.epigraph.idl.TypeRefs;
import ws.epigraph.idl.gdata.IdlGDataPsiParser;
import ws.epigraph.idl.parser.psi.*;
import ws.epigraph.projections.Annotation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.op.input.OpInputModelProjection;
import ws.epigraph.projections.op.input.OpInputProjectionsPsiParser;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingError;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.TypeRef;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.types.DatumType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ws.epigraph.projections.IdlProjectionPsiParserUtil.parseAnnotation;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpParserUtil {
  @NotNull
  public static OpParams parseParams(
      @NotNull Stream<IdlOpParam> paramsPsi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    return parseParams(paramsPsi.collect(Collectors.toList()), resolver, errors);
  }

  @NotNull
  public static OpParams parseParams(
      @NotNull Collection<IdlOpParam> paramsPsi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    List<OpParam> params = null;

    for (final IdlOpParam param : paramsPsi) {
      if (param != null) {
        if (params == null ) params = new ArrayList<>();

        params.add(parseParameter(param, resolver, errors));
      }
    }

    return OpParams.fromCollection(params);
  }

  @NotNull
  public static OpParam parseParameter(
      @NotNull IdlOpParam paramPsi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {
    @Nullable IdlQid qid = paramPsi.getQid();
    if (qid == null) throw new PsiProcessingException("Parameter name not specified", paramPsi, errors);
    @NotNull String paramName = qid.getCanonicalName();

    @Nullable IdlTypeRef typeRef = paramPsi.getTypeRef();
    if (typeRef == null)
      throw new PsiProcessingException(String.format("Parameter '%s' type not specified", paramName), paramPsi, errors);
    @NotNull TypeRef paramTypeRef = TypeRefs.fromPsi(typeRef);
    @Nullable DatumType paramType = paramTypeRef.resolveDatumType(resolver);

    if (paramType == null)
      throw new PsiProcessingException(
          String.format("Can't resolve parameter '%s' data type '%s'", paramName, paramTypeRef), paramPsi, errors
      );

    @Nullable IdlOpInputModelProjection paramModelProjectionPsi = paramPsi.getOpInputModelProjection();

    @NotNull final OpParams params = parseParams(paramPsi.getOpParamList(), resolver, errors);
    @NotNull Annotations annotations = parseAnnotations(paramPsi.getAnnotationList(), errors);

    @Nullable IdlDatum defaultValuePsi = paramPsi.getDatum();
    @Nullable GDatum defaultValue = defaultValuePsi == null
                                    ? null
                                    : IdlGDataPsiParser.parseDatum(defaultValuePsi, errors);

    final OpInputModelProjection<?, ?, ?> paramModelProjection;

    if (paramModelProjectionPsi != null)
      paramModelProjection = OpInputProjectionsPsiParser.parseModelProjection(
          paramType,
          paramPsi.getPlus() != null,
          defaultValue,
          params,
          annotations,
          null, // TODO do we want to support metadata on parameters?
          paramModelProjectionPsi,
          resolver,
          errors
      ).projection();
    else
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

    return new OpParam(paramName, paramModelProjection, EpigraphPsiUtil.getLocation(paramPsi));
  }

  @NotNull
  public static Annotations parseAnnotations(
      @NotNull Stream<IdlAnnotation> annotationsPsi,
      @NotNull List<PsiProcessingError> errors
  ) throws PsiProcessingException {
    return parseAnnotations(annotationsPsi.collect(Collectors.toList()), errors);
  }

  @NotNull
  public static Annotations parseAnnotations(
      @NotNull Collection<IdlAnnotation> annotationsPsi,
      @NotNull List<PsiProcessingError> errors
  ) throws PsiProcessingException {
    @Nullable Map<String, Annotation> annotationMap = null;
    for (final IdlAnnotation annotationPsi : annotationsPsi) {
      annotationMap = parseAnnotation(annotationMap, annotationPsi, errors);
    }

    return Annotations.fromMap(annotationMap);
  }
}
