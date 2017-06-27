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
import ws.epigraph.annotations.Annotations;
import ws.epigraph.names.TypeName;
import ws.epigraph.projections.gen.GenModelProjection;
import ws.epigraph.projections.gen.GenVarProjection;
import ws.epigraph.projections.op.OpParam;
import ws.epigraph.projections.op.OpParams;
import ws.epigraph.projections.op.input.OpInputModelProjection;
import ws.epigraph.projections.op.input.OpInputProjectionsPsiParser;
import ws.epigraph.projections.op.input.OpInputPsiProcessingContext;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingContext;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.TypeRef;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.schema.TypeRefs;
import ws.epigraph.schema.parser.psi.*;
import ws.epigraph.types.DatumTypeApi;
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
    if (qid == null) throw new PsiProcessingException("Parameter name not specified", paramPsi, context.messages());
    @NotNull String paramName = qid.getCanonicalName();

    @Nullable SchemaTypeRef typeRef = paramPsi.getTypeRef();
    if (typeRef == null)
      throw new PsiProcessingException(
          String.format("Parameter '%s' type not specified", paramName),
          paramPsi,
          context.messages()
      );
    @NotNull TypeRef paramTypeRef = TypeRefs.fromPsi(typeRef, context);
    @Nullable DatumTypeApi paramType = paramTypeRef.resolveDatumType(resolver);

    if (paramType == null)
      throw new PsiProcessingException(
          String.format("Can't resolve parameter '%s' data type '%s'", paramName, paramTypeRef),
          paramPsi,
          context.messages()
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

  public static void checkDuplicatingEntityTails(
      @NotNull List<? extends GenVarProjection<?, ?, ?>> tails,
      @NotNull PsiProcessingContext context) {

    Set<TypeName> reportedTypes = new HashSet<>();

    for (int i = 0; i < tails.size(); i++) {
      GenVarProjection<?, ?, ?> tail = tails.get(i);
      TypeApi type = tail.type();
      TypeName typeName = type.name();

      if (!reportedTypes.contains(typeName)) {

        for (int j = i + 1; j < tails.size(); j++) {
          GenVarProjection<?, ?, ?> tail2 = tails.get(j);
          TypeApi type2 = tail2.type();
          TypeName typeName2 = type2.name();

          if (typeName.equals(typeName2)) {
            reportedTypes.add(typeName);
            context.addWarning(
                String.format(
                    "Polymorphic tail for type '%s' is already defined at %s",
                    typeName,
                    tail.location()
                ),
                tail2.location()
            );
          }

        }
      }
    }
  }

  public static void checkDuplicatingModelTails(
      @NotNull List<? extends GenModelProjection<?, ?, ?, ?>> tails,
      @NotNull PsiProcessingContext context) {

    Set<TypeName> reportedTypes = new HashSet<>();

    for (int i = 0; i < tails.size(); i++) {
      GenModelProjection<?, ?, ?, ?> tail = tails.get(i);
      DatumTypeApi type = tail.type();
      TypeName typeName = type.name();

      if (!reportedTypes.contains(typeName)) {

        for (int j = i + 1; j < tails.size(); j++) {
          GenModelProjection<?, ?, ?, ?> tail2 = tails.get(j);
          DatumTypeApi type2 = tail2.type();
          TypeName typeName2 = type2.name();

          if (typeName.equals(typeName2)) {
            reportedTypes.add(typeName);
            context.addWarning(
                String.format(
                    "Polymorphic tail for type '%s' is already defined at %s",
                    typeName,
                    tail.location()
                ),
                tail2.location()
            );
          }

        }
      }
    }
  }

}
