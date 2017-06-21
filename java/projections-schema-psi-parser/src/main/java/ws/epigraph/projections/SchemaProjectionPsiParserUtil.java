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

package ws.epigraph.projections;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.annotations.Annotation;
import ws.epigraph.annotations.Annotations;
import ws.epigraph.gdata.*;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.op.OpParam;
import ws.epigraph.projections.op.OpParams;
import ws.epigraph.projections.op.input.OpInputModelProjection;
import ws.epigraph.projections.op.input.OpInputProjectionsPsiParser;
import ws.epigraph.projections.op.input.OpInputPsiProcessingContext;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingContext;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.TypeRef;
import ws.epigraph.refs.TypeReferenceFactory;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.schema.TypeRefs;
import ws.epigraph.schema.gdata.SchemaGDataPsiParser;
import ws.epigraph.schema.parser.psi.*;
import ws.epigraph.types.DatumTypeApi;
import ws.epigraph.types.PrimitiveTypeApi;
import ws.epigraph.types.TagApi;
import ws.epigraph.types.TypeApi;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class SchemaProjectionPsiParserUtil {
  private SchemaProjectionPsiParserUtil() {}

  public static @NotNull TagApi getTag(
      @NotNull TypeApi type,
      @Nullable SchemaTagName tagName,
      @Nullable TagApi defaultTag,
      @NotNull PsiElement location,
      @NotNull PsiProcessingContext context) throws PsiProcessingException {

    return ProjectionsParsingUtil.getTag(type, getTagNameString(tagName), defaultTag, location, context);
  }

  public static @Nullable TagApi findTag(
      @NotNull TypeApi type,
      @Nullable SchemaTagName tagName,
      @Nullable TagApi defaultTag,
      @NotNull PsiElement location,
      @NotNull PsiProcessingContext context) throws PsiProcessingException {

    return ProjectionsParsingUtil.findTag(type, getTagNameString(tagName), defaultTag, location, context);
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

  public static @Nullable Map<DatumTypeApi, Annotation> parseAnnotation(
      @Nullable Map<DatumTypeApi, Annotation> annotationsMap,
      @Nullable SchemaAnnotation annotationPsi,
      @NotNull PsiProcessingContext context,
      @NotNull TypesResolver typeResolver) throws PsiProcessingException {

    if (annotationPsi != null) {
      if (annotationsMap == null) annotationsMap = new HashMap<>();
      @Nullable SchemaDatum annotationValuePsi = annotationPsi.getDatum();
      SchemaQnTypeRef typeRefPsi = annotationPsi.getQnTypeRef();
      if (typeRefPsi == null)
        context.addError("Malformed annotation", annotationPsi);
      else {
        TypeRef typeRef = TypeRefs.fromPsi(typeRefPsi, context);
        DatumTypeApi datumType = typeRef.resolveDatumType(typeResolver);
        if (datumType == null)
          context.addError(String.format("Can't resolve annotation type '%s'", typeRef.toString()), annotationPsi);
        else {
          annotationsMap.put(
              datumType,
              new Annotation(
                  datumType,
                  annotationValuePsi == null
                  ? createDefaultAnnotation(datumType, EpigraphPsiUtil.getLocation(annotationPsi))
                  : SchemaGDataPsiParser.parseDatum(annotationValuePsi, context),
                  EpigraphPsiUtil.getLocation(annotationPsi),
                  typeResolver
              )
          );
        }
      }
    }
    return annotationsMap;
  }

  private static @NotNull GDatum createDefaultAnnotation(@NotNull DatumTypeApi type, @NotNull TextLocation location) {
    switch (type.kind()) {
      case RECORD:
        return new GRecordDatum(TypeReferenceFactory.createReference(type), new LinkedHashMap<>(), location);
      case MAP:
        return new GMapDatum(TypeReferenceFactory.createReference(type), new LinkedHashMap<>(), location);
      case LIST:
        return new GListDatum(TypeReferenceFactory.createReference(type), Collections.emptyList(), location);
      case PRIMITIVE:
        PrimitiveTypeApi primitiveType = (PrimitiveTypeApi) type;
        final Object value;
        switch (primitiveType.primitiveKind()) {
          case BOOLEAN:
            value = true;
            break;
          case DOUBLE:
            value = 0d;
            break;
          case INTEGER:
            value = 0;
            break;
          case LONG:
            value = 0L;
            break;
          case STRING:
            value = "";
            break;
          default:
            throw new RuntimeException("Unknown primitive kind: " + primitiveType.primitiveKind());
        }
        return new GPrimitiveDatum(TypeReferenceFactory.createReference(type), value, location);

      default:
        throw new RuntimeException("Unknown type kind: " + type.kind());
    }
  }

  public static @Nullable OpInputModelProjection<?, ?, ?, ?> parseKeyProjection(
      @NotNull DatumTypeApi keyType,
      @Nullable OpInputModelProjection<?, ?, ?, ?> keyProjection,
      @Nullable SchemaOpKeyProjection projectionPsi,
      @NotNull TypesResolver typesResolver,
      @NotNull OpInputPsiProcessingContext context) throws PsiProcessingException {

    if (projectionPsi == null) return keyProjection;
    if (keyProjection == null) {
      SchemaOpInputModelProjection inputModelProjectionPsi = projectionPsi.getOpInputModelProjection();
      if (inputModelProjectionPsi == null) {
        context.addError("Missing key projection definition", projectionPsi);
        return null;
      }

      return OpInputProjectionsPsiParser.parseModelProjection(
          keyType,
          true,
          inputModelProjectionPsi,
          typesResolver,
          context
      );
    } else {
      context.addError("Key projection is already defined", projectionPsi);
      return keyProjection;
    }
  }

  public static @NotNull OpParams parseParams(
      @NotNull Stream<SchemaOpParam> paramsPsi,
      @NotNull TypesResolver resolver,
      @NotNull OpInputPsiProcessingContext context) throws PsiProcessingException {

    return parseParams(paramsPsi.collect(Collectors.toList()), resolver, context);
  }

  public static @NotNull OpParams parseParams(
      @NotNull Iterable<SchemaOpParam> paramsPsi,
      @NotNull TypesResolver resolver,
      @NotNull OpInputPsiProcessingContext context) throws PsiProcessingException {

    Collection<OpParam> params = null;

    for (final SchemaOpParam param : paramsPsi) {
      if (param != null) {
        if (params == null) params = new ArrayList<>();

        params.add(parseParameter(param, resolver, context));
      }
    }

    return OpParams.fromCollection(params);
  }

  public static @NotNull OpParam parseParameter(
      @NotNull SchemaOpParam paramPsi,
      @NotNull TypesResolver resolver,
      @NotNull OpInputPsiProcessingContext context) throws PsiProcessingException {

    @Nullable SchemaQid qid = paramPsi.getQid();
    if (qid == null) throw new PsiProcessingException("Parameter name not specified", paramPsi, context.errors());
    @NotNull String paramName = qid.getCanonicalName();

    @Nullable SchemaTypeRef typeRef = paramPsi.getTypeRef();
    if (typeRef == null)
      throw new PsiProcessingException(
          String.format("Parameter '%s' type not specified", paramName),
          paramPsi,
          context.errors()
      );
    @NotNull TypeRef paramTypeRef = TypeRefs.fromPsi(typeRef, context);
    @Nullable DatumTypeApi paramType = paramTypeRef.resolveDatumType(resolver);

    if (paramType == null)
      throw new PsiProcessingException(
          String.format("Can't resolve parameter '%s' data type '%s'", paramName, paramTypeRef),
          paramPsi,
          context.errors()
      );

    @Nullable SchemaOpInputModelProjection paramModelProjectionPsi = paramPsi.getOpInputModelProjection();

//    final @NotNull OpParams params = parseParams(paramPsi.getOpParamList(), resolver, context);
//    @NotNull Annotations annotations = parseAnnotations(paramPsi.getAnnotationList(), context);
//
//    @Nullable SchemaDatum defaultValuePsi = paramPsi.getDatum();
//    @Nullable GDatum defaultValue = defaultValuePsi == null
//                                    ? null
//                                    : SchemaGDataPsiParser.parseDatum(defaultValuePsi, context);

    final OpInputModelProjection<?, ?, ?, ?> paramModelProjection;

    if (paramModelProjectionPsi == null)
      paramModelProjection = OpInputProjectionsPsiParser.createDefaultModelProjection(
          paramType,
          paramPsi.getPlus() != null,
//          defaultValue,
//          params,
//          annotations,
          null,
          OpParams.EMPTY,
          Annotations.EMPTY,
          paramPsi,
          resolver,
          context
      );
    else paramModelProjection = OpInputProjectionsPsiParser.parseModelProjection(
        paramType,
        paramPsi.getPlus() != null,
//        defaultValue,
//        params,
//        annotations,
//        null, // TODO do we want to support metadata on parameters?
        paramModelProjectionPsi,
        resolver,
        context
    );

    return new OpParam(paramName, paramModelProjection, EpigraphPsiUtil.getLocation(paramPsi));
  }

  public static @Nullable OpInputModelProjection<?, ?, ?, ?> parseKeyProjection(
      @NotNull DatumTypeApi keyType,
      @NotNull Stream<SchemaOpKeyProjection> projectionPsi,
      @NotNull TypesResolver typesResolver,
      @NotNull OpInputPsiProcessingContext context) {

    return projectionPsi.reduce(
        null,
        (kp, psi) -> {
          try {
            return parseKeyProjection(keyType, kp, psi, typesResolver, context);
          } catch (PsiProcessingException e) {
            context.addException(e);
            return null;
          }
        },
        (kp1, kp2) -> kp1 == null ? kp2 : kp1
    );

  }

  public static @NotNull Annotations parseAnnotations(
      @NotNull Stream<SchemaAnnotation> annotationsPsi,
      @NotNull PsiProcessingContext context,
      @NotNull TypesResolver typesResolver
  ) throws PsiProcessingException {

    return parseAnnotations(annotationsPsi.collect(Collectors.toList()), context, typesResolver);
  }

  public static @NotNull Annotations parseAnnotations(
      @NotNull Iterable<SchemaAnnotation> annotationsPsi,
      @NotNull PsiProcessingContext context, @NotNull TypesResolver typesResolver
  ) throws PsiProcessingException {

    @Nullable Map<DatumTypeApi, Annotation> annotationMap = null;
    for (final SchemaAnnotation annotationPsi : annotationsPsi) {
      annotationMap = parseAnnotation(annotationMap, annotationPsi, context, typesResolver);
    }

    return Annotations.fromMap(annotationMap);
  }
}
