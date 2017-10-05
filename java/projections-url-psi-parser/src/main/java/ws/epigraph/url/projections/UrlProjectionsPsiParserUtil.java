/*
 * Copyright 2017 Sumo Logic
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
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.data.Datum;
import ws.epigraph.data.Val;
import ws.epigraph.gdata.GDataToData;
import ws.epigraph.gdata.GDataValue;
import ws.epigraph.gdata.GDatum;
import ws.epigraph.gdata.validation.GDataValidationError;
import ws.epigraph.gdata.validation.OpInputGDataValidator;
import ws.epigraph.lang.Qn;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.names.QualifiedTypeName;
import ws.epigraph.names.TypeName;
import ws.epigraph.projections.gen.GenModelProjection;
import ws.epigraph.projections.gen.GenTagProjectionEntry;
import ws.epigraph.projections.gen.GenVarProjection;
import ws.epigraph.projections.op.OpParam;
import ws.epigraph.projections.op.OpParams;
import ws.epigraph.projections.op.OpModelProjection;
import ws.epigraph.projections.req.Directive;
import ws.epigraph.projections.req.Directives;
import ws.epigraph.projections.req.ReqParam;
import ws.epigraph.projections.req.ReqParams;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingContext;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.ImportAwareTypesResolver;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.types.*;
import ws.epigraph.url.gdata.UrlGDataPsiParser;
import ws.epigraph.url.parser.psi.*;

import java.util.*;

import static ws.epigraph.projections.ProjectionsParsingUtil.listTags;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class UrlProjectionsPsiParserUtil {
  private UrlProjectionsPsiParserUtil() {}

//  /**
//   * Finds supported tag with a given name in type {@code type} if {@code tagNamePsi} is not null.
//   * <p>
//   * Otherwise gets {@link ProjectionsParsingUtil#findSelfOrRetroTag(TypeApi, GenVarProjection, PsiElement, PsiProcessingContext)}
//   * self tag} and, if not {@code null}, returns it; otherwise fails.
//   */
//  public static @NotNull <
//      MP extends GenModelProjection<?, ?, ?, ?>,
//      TP extends GenTagProjectionEntry<TP, MP>,
//      VP extends GenVarProjection<VP, TP, MP>>
//  TagApi getTagOrSelfTag(
//      @NotNull TypeApi type,
//      @Nullable UrlTagName tagNamePsi,
//      @NotNull VP opOutputVarProjection,
//      @NotNull PsiElement location,
//      @NotNull PsiProcessingContext context) throws PsiProcessingException {
//
//    TagApi tag = findTagOrSelfOrRetroTag(type, tagNamePsi, opOutputVarProjection, location, context);
//    if (tag == null)
//      throw new PsiProcessingException(
//          String.format(
//              "Can't build projection for type '%s': no tags specified. Supported tags: {%s}",
//              type.name(),
//              listTags(opOutputVarProjection)
//          ),
//          location,
//          context
//      );
//
//    return tag;
//  }

//  /**
//   * If {@code tagNamePsi} is not null: gets tag from the op projection. Otherwise returns
//   * retro tag if present, or self tag if it's a non-entity type, or else null.
//   * <p>
//   * Otherwise gets {@link ProjectionsParsingUtil#findSelfOrRetroTag(TypeApi, GenVarProjection, PsiElement, PsiProcessingContext)}
//   * self tag} and, if not {@code null}, returns it; otherwise returns {@code null}.
//   */
//  @Contract("_, !null, _, _, _ -> !null")
//  public static @Nullable <
//      MP extends GenModelProjection<?, ?, ?, ?>,
//      TP extends GenTagProjectionEntry<TP, MP>,
//      VP extends GenVarProjection<VP, TP, MP>>
//
//  TagApi findTagOrSelfOrRetroTag(
//      @NotNull DataTypeApi dataType,
//      @Nullable UrlTagName tagNamePsi,
//      @NotNull VP opOutputVarProjection,
//      @NotNull PsiElement location,
//      @NotNull PsiProcessingContext context) throws PsiProcessingException {
//
//    if (tagNamePsi == null)
//      return findSelfOrRetroTag(dataType, opOutputVarProjection, location, context);
//    else
//      return getTag(tagNamePsi, opOutputVarProjection, location, context);
//  }

  public static @Nullable String getTagName(@Nullable UrlTagName tagNamePsi) {
    return tagNamePsi == null ? null : tagNamePsi.getQid().getCanonicalName();
  }

  @Contract("_, _, _, _ -> fail")
  public static <
      MP extends GenModelProjection<?, ?, ?, ?>,
      TP extends GenTagProjectionEntry<TP, MP>,
      VP extends GenVarProjection<VP, TP, MP>>
  void raiseNoTagsError(
      @NotNull DataTypeApi type,
      @NotNull VP opOutputVarProjection,
      @NotNull PsiElement location,
      @NotNull PsiProcessingContext context) throws PsiProcessingException {

    throw new PsiProcessingException(
        String.format(
            "Can't build projection for type '%s': no tags specified. Supported tags: {%s}",
            type.name(),
            listTags(opOutputVarProjection)
        ),
        location,
        context
    );

  }

//  /**
//   * Finds supported tag with a given name in type {@code type}
//   */
//  public static @NotNull <
//      MP extends GenModelProjection<?, ?, ?, ?>,
//      TP extends GenTagProjectionEntry<TP, MP>,
//      VP extends GenVarProjection<VP, TP, MP>>
//  TagApi getTag(
//      @NotNull UrlTagName idlTagName,
//      @NotNull VP varProjection,
//      @NotNull PsiElement location,
//      @NotNull PsiProcessingContext context) throws PsiProcessingException {
//
//    return getTagProjection(idlTagName.getQid().getCanonicalName(), varProjection, location, context).tag();
//  }

//  public static @NotNull TagApi getTag(
//      @NotNull TypeApi type,
//      @Nullable UrlTagName tagName,
//      @Nullable TagApi defaultTag,
//      @NotNull PsiElement location,
//      @NotNull PsiProcessingContext context) throws PsiProcessingException {
//
//    return ProjectionsParsingUtil.getTag(type, getTagNameString(tagName), defaultTag, location, context);
//  }

  @Contract("null -> null; !null -> !null")
  private static @Nullable String getTagNameString(final @Nullable UrlTagName tagName) {
    return tagName == null ? null : tagName.getQid().getCanonicalName();
  }

  public static @Nullable Map<String, Directive> parseAnnotation(
      @Nullable Map<String, Directive> annotationsMap,
      @Nullable UrlAnnotation annotationPsi,
      @NotNull PsiProcessingContext context) throws PsiProcessingException {

    if (annotationPsi != null) {
      if (annotationsMap == null) annotationsMap = new HashMap<>();
      @Nullable UrlDataValue annotationValuePsi = annotationPsi.getDataValue();
      if (annotationValuePsi != null) {
        @NotNull String annotationName = annotationPsi.getQid().getCanonicalName();
        @NotNull GDataValue annotationValue = UrlGDataPsiParser.parseValue(annotationValuePsi, context);
        annotationsMap.put(
            annotationName,
            new Directive(
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
      @NotNull DatumTypeApi model,
      @NotNull TypesResolver resolver,
      @NotNull String errorMessagePrefix,
      @NotNull PsiProcessingContext context) throws PsiProcessingException {

    @NotNull GDatum gDatum = UrlGDataPsiParser.parseDatum(datumPsi, context);

    final @Nullable Datum value;

    try {
      value = GDataToData.transform((DatumType) model, gDatum, resolver).getDatum();
    } catch (GDataToData.ProcessingException e) {
      // try to find element by offset
      int offset = e.location().startOffset() - datumPsi.getTextRange().getStartOffset();
      PsiElement element = datumPsi.findElementAt(offset);
      if (element == null) element = datumPsi;

      context.addError(errorMessagePrefix + e.getMessage(), element);
      return null;
    }
    return value;
  }

  public static @Nullable Datum getDatum(
      @NotNull UrlDatum datumPsi,
      @NotNull OpModelProjection<?, ?, ?, ?> projection,
      @NotNull TypesResolver resolver,
      @NotNull String errorMessagePrefix,
      @NotNull PsiProcessingContext context) throws PsiProcessingException {

    @NotNull GDatum gDatum = UrlGDataPsiParser.parseDatum(datumPsi, context);

    OpInputGDataValidator validator = new OpInputGDataValidator(resolver);
    validator.validateDatum(gDatum, projection);

    for (final GDataValidationError validationError : validator.errors()) {
      context.addError(
          validationError.toStringNoTextLocation(),
          validationError.textLocation()
      );
    }

    final @Nullable Datum value;

    try {
      value = GDataToData.transform((DatumType) projection.type(), gDatum, resolver).getDatum();
    } catch (GDataToData.ProcessingException e) {
      // try to find element by offset
      int offset = e.location().startOffset() - datumPsi.getTextRange().getStartOffset();
      PsiElement element = datumPsi.findElementAt(offset);
      if (element == null) element = datumPsi;

      context.addError(errorMessagePrefix + e.getMessage(), element);
      return null;
    }
    return value;
  }

  public static @NotNull Map<String, GDatum> parseRequestParams(
      @NotNull List<UrlRequestParam> requestParamList,
      @NotNull PsiProcessingContext context) throws PsiProcessingException {

    final Map<String, GDatum> requestParams;

    if (requestParamList.isEmpty()) requestParams = Collections.emptyMap();
    else {
      boolean first = true;
      requestParams = new HashMap<>();

      for (UrlRequestParam requestParamPsi : requestParamList) {
        if (first) {
          final @Nullable PsiElement amp = requestParamPsi.getAmp();
          if (amp != null) context.addError("'?' expected, got '&'", amp);
          first = false;
        } else {
          final @Nullable PsiElement qmark = requestParamPsi.getQmark();
          if (qmark != null) context.addError("'&' expected, got '?'", qmark);
        }

        final @Nullable PsiElement paramNamePsi = requestParamPsi.getParamName();
        if (paramNamePsi == null) {
          context.addError("Missing parameter name", requestParamPsi);
          continue;
        }
        String paramName = paramNamePsi.getText();

        final @Nullable UrlDatum paramValuePsi = requestParamPsi.getDatum();
        if (paramValuePsi == null) {
          context.addError(String.format("Missing parameter '%s' value", paramName), requestParamPsi);
          continue;
        }

        final @NotNull GDatum paramValue = UrlGDataPsiParser.parseDatum(paramValuePsi, context);

        requestParams.put(paramName, paramValue);
      }
    }

    return requestParams;
  }

  public static @NotNull Directives parseAnnotations(
      @NotNull List<UrlReqAnnotation> annotationsPsi,
      @NotNull PsiProcessingContext context) {
    Map<String, Directive> paramMap = null;

    for (UrlReqAnnotation annotation : annotationsPsi) {
      try {
        paramMap = parseAnnotation(paramMap, annotation.getAnnotation(), context);
      } catch (PsiProcessingException e) {
        context.setErrors(e.messages());
      }
    }

    return Directives.fromMap(paramMap);
  }

  public static @NotNull ReqParams parseReqParams(
      @NotNull List<UrlReqParam> reqParamsPsi,
      @Nullable OpParams opParams,
      @NotNull TypesResolver resolver,
      @NotNull PsiElement paramsLocation,
      @NotNull PsiProcessingContext context) throws PsiProcessingException {

    if (opParams == null) {
      if (!reqParamsPsi.isEmpty())
        context.addError("Parameters are not supported here", reqParamsPsi.iterator().next());
      return ReqParams.EMPTY;
    }

    Map<String, ReqParam> paramMap = null;

    for (UrlReqParam reqParamPsi : reqParamsPsi)
      paramMap = parseReqParam(paramMap, reqParamPsi, opParams, resolver, context);

    // check that all required params are present and fill in those with defaults
    for (final Map.Entry<String, OpParam> entry : opParams.asMap().entrySet()) {
      String paramName = entry.getKey();
      if ((paramMap == null || !paramMap.containsKey(paramName))) {
        final OpModelProjection<?, ?, ?, ?> opModelProjection = entry.getValue().projection();

        GDatum defaultValue = opModelProjection.defaultValue();
        if (defaultValue == null) {
          defaultValue = opModelProjection.defaultValue();

          if (defaultValue == null && opModelProjection.flag()) {
            context.addError(
                String.format("Required parameter '%s' is missing", paramName),
                paramsLocation
            );
          }
        } else {
          if (paramMap == null) paramMap = new LinkedHashMap<>();

          try {
            @NotNull Val val = GDataToData.transform((DatumType) opModelProjection.type(), defaultValue, resolver);
            final Datum datum = val.getDatum();
            if (datum == null)
              context.addError(
                  "Malformed default value in op projection for parameter '" + paramName + "'",
                  paramsLocation
              );
            else
              paramMap.put(paramName, new ReqParam(paramName, datum, TextLocation.UNKNOWN));
          } catch (GDataToData.ProcessingException e) {
            throw new PsiProcessingException(e, paramsLocation, context);
          }
        }
      }
    }

    return ReqParams.fromMap(paramMap);
  }

  public static @Nullable Map<String, ReqParam> parseReqParam(
      @Nullable Map<String, ReqParam> reqParamsMap,
      @Nullable UrlReqParam reqParamPsi,
      @NotNull OpParams opParams,
      @NotNull TypesResolver resolver,
      @NotNull PsiProcessingContext context) throws PsiProcessingException {

    if (reqParamPsi != null) {
      if (reqParamsMap == null) reqParamsMap = new LinkedHashMap<>();

      String name = reqParamPsi.getQid().getCanonicalName();
      OpParam opParam = opParams.asMap().get(name);

      if (opParam == null) {
        context.addError(
            String.format(
                "Unsupported parameter '%s', supported parameters: {%s}",
                name,
                String.join(", ", opParams.asMap().keySet())
            ),
            reqParamPsi.getQid()
        );
        return reqParamsMap;
      }

      final String errorMsgPrefix = String.format("Error processing parameter '%s' value: ", name);
      OpModelProjection<?, ?, ?, ?> projection = opParam.projection();
      final DatumTypeApi model = projection.type();
      final @NotNull TypesResolver subResolver = addTypeNamespace(model, resolver);

      @Nullable Datum value = getDatum(reqParamPsi.getDatum(), projection, subResolver, errorMsgPrefix, context);
      if (value == null) {
        final GDatum gDatum = projection.defaultValue();
        if (gDatum != null)
          try {
            Val val = GDataToData.transform((DatumType) projection.type(), gDatum, resolver);
            value = val.getDatum();
          } catch (GDataToData.ProcessingException e) {
            throw new PsiProcessingException(e, reqParamPsi, context);
          }
      }

      if (value == null && opParam.projection().flag())
        context.addError("Required parameter '" + opParam.name() + "' value is missing", reqParamPsi.getQid());

      reqParamsMap.put(name, new ReqParam(name, value, EpigraphPsiUtil.getLocation(reqParamPsi)));
    }
    return reqParamsMap;
  }


  public static @NotNull TypesResolver addTypeNamespace(@NotNull TypeApi type, @NotNull TypesResolver resolver) {
    final @Nullable Qn namespace = getTypeNamespace(type);

    if (namespace == null) return resolver;
    else {

      TypesResolver child = resolver;

      if (child instanceof ImportAwareTypesResolver)
        child = ((ImportAwareTypesResolver) child).childResolver();

      return new ImportAwareTypesResolver(namespace, Collections.emptyList(), child);
    }
  }

  public static @Nullable Qn getTypeNamespace(@NotNull TypeApi type) {
    final @NotNull TypeName name = type.name();

    if (name instanceof QualifiedTypeName) {
      QualifiedTypeName qualifiedTypeName = (QualifiedTypeName) name;
      return qualifiedTypeName.toFqn().removeLastSegment();
    }

    return null;
  }

  public static void ensureModelKind(
      @Nullable TypeKind actualKind, @NotNull TypeKind expectedKind, @NotNull PsiElement locationPsi,
      @NotNull PsiProcessingContext context) throws PsiProcessingException {

    if (actualKind != null && expectedKind != actualKind)
      throw new PsiProcessingException(
          String.format("Unexpected projection kind '%s', expected '%s'", actualKind, expectedKind),
          locationPsi,
          context
      );
  }
}
