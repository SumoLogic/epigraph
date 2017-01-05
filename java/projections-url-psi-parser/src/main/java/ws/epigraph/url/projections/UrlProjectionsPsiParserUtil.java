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
import ws.epigraph.data.Datum;
import ws.epigraph.gdata.GDataToData;
import ws.epigraph.gdata.GDataValue;
import ws.epigraph.gdata.GDatum;
import ws.epigraph.lang.Qn;
import ws.epigraph.names.QualifiedTypeName;
import ws.epigraph.names.TypeName;
import ws.epigraph.projections.Annotation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.ProjectionsParsingUtil;
import ws.epigraph.projections.gen.GenModelProjection;
import ws.epigraph.projections.gen.GenTagProjectionEntry;
import ws.epigraph.projections.gen.GenVarProjection;
import ws.epigraph.projections.op.OpParam;
import ws.epigraph.projections.op.OpParams;
import ws.epigraph.projections.op.input.OpInputModelProjection;
import ws.epigraph.projections.req.ReqParam;
import ws.epigraph.projections.req.ReqParams;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingError;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.ImportAwareTypesResolver;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.types.DatumType;
import ws.epigraph.types.Type;
import ws.epigraph.types.TypeKind;
import ws.epigraph.url.gdata.UrlGDataPsiParser;
import ws.epigraph.url.parser.psi.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ws.epigraph.projections.ProjectionsParsingUtil.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class UrlProjectionsPsiParserUtil {
  private UrlProjectionsPsiParserUtil() {}
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
  public static @NotNull <
      MP extends GenModelProjection<?, ?>,
      TP extends GenTagProjectionEntry<TP, MP>,
      VP extends GenVarProjection<VP, TP, MP>>
  Type.Tag getTagOrDefaultTag(
      @NotNull Type type,
      @Nullable UrlTagName idlTagName,
      @NotNull VP opOutputVarProjection,
      @NotNull PsiElement location,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    Type.Tag tag = findTagOrDefaultTag(type, idlTagName, opOutputVarProjection, location, errors);
    if (tag == null)
      throw new PsiProcessingException(
          String.format(
              "Can't build projection for type '%s': no tags specified. Supported tags: {%s}",
              type.name(),
              listTags(opOutputVarProjection)
          ),
          location,
          errors
      );

    return tag;
  }

  /**
   * Finds supported tag with a given name in type {@code type} if {@code idlTagName} is not null.
   * <p>
   * Otherwise gets {@link ProjectionsParsingUtil#findDefaultTag(Type, GenVarProjection, PsiElement, List)}
   * default tag} and, if not {@code null}, returns it; otherwise returns {@code null}.
   */
  public static @Nullable <
      MP extends GenModelProjection<?, ?>,
      TP extends GenTagProjectionEntry<TP, MP>,
      VP extends GenVarProjection<VP, TP, MP>>

  Type.Tag findTagOrDefaultTag(
      @NotNull Type type,
      @Nullable UrlTagName idlTagName,
      @NotNull VP opOutputVarProjection,
      @NotNull PsiElement location,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    if (idlTagName == null) {
      final Type.@Nullable Tag defaultTag = findDefaultTag(type, opOutputVarProjection, location, errors);

      return defaultTag == null ? null : defaultTag;
    } else return getTag(idlTagName, opOutputVarProjection, location, errors);
  }


  /**
   * Finds supported tag with a given name in type {@code type}
   */
  public static @NotNull <
      MP extends GenModelProjection<?, ?>,
      TP extends GenTagProjectionEntry<TP, MP>,
      VP extends GenVarProjection<VP, TP, MP>>
  Type.Tag getTag(
      @NotNull UrlTagName idlTagName,
      @NotNull VP varProjection,
      @NotNull PsiElement location,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    return getTagProjection(idlTagName.getQid().getCanonicalName(), varProjection, location, errors).tag();
  }

  public static @NotNull Type.Tag getTag(
      @NotNull Type type,
      @Nullable UrlTagName tagName,
      @Nullable Type.Tag defaultTag,
      @NotNull PsiElement location,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    return ProjectionsParsingUtil.getTag(type, getTagNameString(tagName), defaultTag, location, errors);
  }

  private static @Nullable String getTagNameString(final @Nullable UrlTagName tagName) {
    String tagNameStr = null;

    if (tagName != null) {
      final @Nullable UrlQid qid = tagName.getQid();
      tagNameStr = qid.getCanonicalName();
    }
    return tagNameStr;
  }


  public static @Nullable Map<String, Annotation> parseAnnotation(
      @Nullable Map<String, Annotation> annotationsMap,
      @Nullable UrlAnnotation annotationPsi,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    if (annotationPsi != null) {
      if (annotationsMap == null) annotationsMap = new HashMap<>();
      @Nullable UrlDataValue annotationValuePsi = annotationPsi.getDataValue();
      if (annotationValuePsi != null) {
        @NotNull String annotationName = annotationPsi.getQid().getCanonicalName();
        @NotNull GDataValue annotationValue = UrlGDataPsiParser.parseValue(annotationValuePsi, errors);
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

  public static @Nullable Datum getDatum(
      @NotNull UrlDatum datumPsi,
      @NotNull DatumType model,
      @NotNull TypesResolver resolver,
      @NotNull String errorMessagePrefix,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    @NotNull GDatum gDatum = UrlGDataPsiParser.parseDatum(datumPsi, errors);
    @Nullable Datum value;

    try {
      value = GDataToData.transform(model, gDatum, resolver).getDatum();
    } catch (GDataToData.ProcessingException e) {
      // try to find element by offset
      int offset = e.location().startOffset() - datumPsi.getTextRange().getStartOffset();
      PsiElement element = datumPsi.findElementAt(offset);
      if (element == null) element = datumPsi;

      errors.add(new PsiProcessingError(errorMessagePrefix + e.getMessage(), element));
      return null;
    }
    return value;
  }

  public static @NotNull Map<String, GDatum> parseRequestParams(
      @NotNull List<UrlRequestParam> requestParamList,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    final Map<String, GDatum> requestParams;

    if (requestParamList.isEmpty()) requestParams = Collections.emptyMap();
    else {
      boolean first = true;
      requestParams = new HashMap<>();

      for (UrlRequestParam requestParamPsi : requestParamList) {
        if (first) {
          final @Nullable PsiElement amp = requestParamPsi.getAmp();
          if (amp != null) errors.add(new PsiProcessingError("'?' expected, got '&'", amp));
          first = false;
        } else {
          final @Nullable PsiElement qmark = requestParamPsi.getQmark();
          if (qmark != null) errors.add(new PsiProcessingError("'&' expected, got '?'", qmark));
        }

        final @Nullable PsiElement paramNamePsi = requestParamPsi.getParamName();
        if (paramNamePsi == null) {
          errors.add(new PsiProcessingError("Missing parameter name", requestParamPsi));
          continue;
        }
        String paramName = paramNamePsi.getText();

        final @Nullable UrlDatum paramValuePsi = requestParamPsi.getDatum();
        if (paramValuePsi == null) {
          errors.add(new PsiProcessingError(String.format("Missing parameter '%s' value", paramName), requestParamPsi));
          continue;
        }

        final @NotNull GDatum paramValue = UrlGDataPsiParser.parseDatum(paramValuePsi, errors);

        requestParams.put(paramName, paramValue);
      }
    }

    return requestParams;
  }

  public static @NotNull Annotations parseAnnotations(
      @NotNull List<UrlReqAnnotation> annotationsPsi,
      @NotNull List<PsiProcessingError> errors) {
    Map<String, Annotation> paramMap = null;

    for (UrlReqAnnotation annotation : annotationsPsi) {
      try {
        paramMap = parseAnnotation(paramMap, annotation.getAnnotation(), errors);
      } catch (PsiProcessingException e) {
        errors = e.errors();
      }
    }

    return Annotations.fromMap(paramMap);
  }

  public static @NotNull ReqParams parseReqParams(
      @NotNull List<UrlReqParam> reqParamsPsi,
      @Nullable OpParams opParams,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    if (reqParamsPsi.isEmpty()) return ReqParams.EMPTY;

    if (opParams == null) {
      errors.add(new PsiProcessingError("Parameters are not supported here", reqParamsPsi.iterator().next()));
      return ReqParams.EMPTY;
    }

    Map<String, ReqParam> paramMap = null;

    for (UrlReqParam reqParamPsi : reqParamsPsi)
      paramMap = parseReqParam(paramMap, reqParamPsi, opParams, resolver, errors);

    return ReqParams.fromMap(paramMap);
  }

  public static @Nullable Map<String, ReqParam> parseReqParam(
      @Nullable Map<String, ReqParam> reqParamsMap,
      @Nullable UrlReqParam reqParamPsi,
      @NotNull OpParams opParams,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    if (reqParamPsi != null) {
      if (reqParamsMap == null) reqParamsMap = new HashMap<>();

      String name = reqParamPsi.getQid().getCanonicalName();
      OpParam opParam = opParams.asMap().get(name);

      if (opParam == null) {
        errors.add(new PsiProcessingError(
            String.format(
                "Unsupported parameter '%s', supported parameters: {%s}",
                name,
                String.join(", ", opParams.asMap().keySet())
            ),
            reqParamPsi.getQid()
        ));
        return reqParamsMap;
      }

      final String errorMsgPrefix = String.format("Error processing parameter '%s' value: ", name);
      OpInputModelProjection<?, ?, ?> projection = opParam.projection();
      final DatumType model = projection.model();
      final @NotNull TypesResolver subResolver = addTypeNamespace(model, resolver);

      @Nullable Datum value = getDatum(reqParamPsi.getDatum(), model, subResolver, errorMsgPrefix, errors);
      if (value == null) {
        final GDatum gDatum = projection.defaultValue();
        if (gDatum != null)
          try {
            value = (Datum) GDataToData.transform(projection.model(), gDatum, resolver);
          } catch (GDataToData.ProcessingException e) {
            throw new PsiProcessingException(e, reqParamPsi, errors);
          }
      }

      // todo validate value against input projection

      reqParamsMap.put(name, new ReqParam(name, value, EpigraphPsiUtil.getLocation(reqParamPsi)));
    }
    return reqParamsMap;
  }


  public static @NotNull TypesResolver addTypeNamespace(@NotNull Type type, @NotNull TypesResolver resolver) {
    final @Nullable Qn namespace = getTypeNamespace(type);

    if (namespace == null) return resolver;
    else {

      TypesResolver child = resolver;

      if (child instanceof ImportAwareTypesResolver)
        child = ((ImportAwareTypesResolver) child).childResolver();

      return new ImportAwareTypesResolver(namespace, Collections.emptyList(), child);
    }
  }

  public static @Nullable Qn getTypeNamespace(@NotNull Type type) {
    final @NotNull TypeName name = type.name();

    if (name instanceof QualifiedTypeName) {
      QualifiedTypeName qualifiedTypeName = (QualifiedTypeName) name;
      return qualifiedTypeName.toFqn().removeLastSegment();
    }

    return null;
  }

  public static void ensureModelKind(
      @Nullable TypeKind actualKind, @NotNull TypeKind expectedKind, @NotNull PsiElement locationPsi,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    if (expectedKind != actualKind)
      throw new PsiProcessingException(
          String.format("Unexpected projection kind '%s', expected '%s'", actualKind, expectedKind),
          locationPsi,
          errors
      );
  }
}
