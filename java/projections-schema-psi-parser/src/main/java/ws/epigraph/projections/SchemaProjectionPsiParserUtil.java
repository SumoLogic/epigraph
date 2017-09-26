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
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.annotations.Annotations;
import ws.epigraph.names.TypeName;
import ws.epigraph.projections.gen.GenModelProjection;
import ws.epigraph.projections.gen.GenVarProjection;
import ws.epigraph.projections.op.OpParam;
import ws.epigraph.projections.op.OpParams;
import ws.epigraph.projections.op.input.OpInputProjectionsPsiParser;
import ws.epigraph.projections.op.OpModelProjection;
import ws.epigraph.projections.op.output.OpPsiProcessingContext;
import ws.epigraph.projections.op.output.OpBasicProjectionPsiParser;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingContext;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.TypeRef;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.schema.TypeRefs;
import ws.epigraph.schema.parser.psi.*;
import ws.epigraph.types.DataTypeApi;
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

  public static
  @NotNull TagApi getTag(
      @NotNull DataTypeApi type,
      @Nullable SchemaTagName tagNamePsi,
      @NotNull PsiElement location,
      @NotNull PsiProcessingContext context) throws PsiProcessingException {

    return ProjectionsParsingUtil.getTag(type, getTagName(tagNamePsi), null, location, context);
  }

  public static
  @Nullable TagApi findTag(
      @NotNull DataTypeApi type,
      @Nullable SchemaTagName tagNamePsi,
      @NotNull PsiElement location,
      @NotNull PsiProcessingContext context) throws PsiProcessingException {

    return ProjectionsParsingUtil.findTag(type, getTagName(tagNamePsi), null, location, context);
  }

  private static @Nullable String getTagName(final @Nullable SchemaTagName tagNamePsi) {
    String tagNameStr = null;

    if (tagNamePsi != null) {
      final @Nullable SchemaQid qid = tagNamePsi.getQid();
      if (qid != null)
        tagNameStr = qid.getCanonicalName();
    }
    return tagNameStr;
  }

  @Contract("_, _, _ -> fail")
  public static
  void raiseNoTagsError(
      @NotNull DataTypeApi type,
      @NotNull PsiElement location,
      @NotNull PsiProcessingContext context) throws PsiProcessingException {

    throw new PsiProcessingException(
        String.format(
            "Can't build projection for type '%s': no tags specified. Supported tags: {%s}",
            type.name(),
            ProjectionsParsingUtil.listTags(type.type())
        ),
        location,
        context
    );

  }

  public static @Nullable OpModelProjection<?, ?, ?, ?> parseKeyProjection(
      @NotNull DatumTypeApi keyType,
      @Nullable OpModelProjection<?, ?, ?, ?> keyProjection,
      @Nullable SchemaOpKeyProjection projectionPsi,
      @NotNull TypesResolver typesResolver,
      @NotNull OpPsiProcessingContext context) throws PsiProcessingException {

    if (projectionPsi == null) return keyProjection;

    if (keyProjection == null) {
      @Nullable SchemaOpOutputModelProjection inputModelProjectionPsi = projectionPsi.getOpOutputModelProjection();
      if (inputModelProjectionPsi == null) {
        context.addError("Missing key projection definition", projectionPsi);
        return null;
      }

      return OpInputProjectionsPsiParser.INSTANCE.parseModelProjection(
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
      @NotNull OpPsiProcessingContext context) throws PsiProcessingException {

    return parseParams(paramsPsi.collect(Collectors.toList()), resolver, context);
  }

  public static @NotNull OpParams parseParams(
      @NotNull Iterable<SchemaOpParam> paramsPsi,
      @NotNull TypesResolver resolver,
      @NotNull OpPsiProcessingContext context) throws PsiProcessingException {

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
      @NotNull OpPsiProcessingContext context) throws PsiProcessingException {

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

    @Nullable SchemaOpOutputModelProjection paramModelProjectionPsi = paramPsi.getOpOutputModelProjection();

//    final @NotNull OpParams params = parseParams(paramPsi.getOpParamList(), resolver, context);
//    @NotNull Annotations annotations = parseAnnotations(paramPsi.getAnnotationList(), context);
//
//    @Nullable SchemaDatum defaultValuePsi = paramPsi.getDatum();
//    @Nullable GDatum defaultValue = defaultValuePsi == null
//                                    ? null
//                                    : SchemaGDataPsiParser.parseDatum(defaultValuePsi, context);

    final OpModelProjection<?, ?, ?, ?> paramModelProjection;

    if (paramModelProjectionPsi == null)
      paramModelProjection = OpBasicProjectionPsiParser.createDefaultModelProjection(
          paramType,
          paramPsi.getPlus() != null,
          null,
          OpParams.EMPTY,
          Annotations.EMPTY,
          paramPsi,
          context
      );
    else paramModelProjection = OpInputProjectionsPsiParser.INSTANCE.parseModelProjection(
        paramType,
        paramPsi.getPlus() != null,
        paramModelProjectionPsi,
        resolver,
        context
    );

    return new OpParam(paramName, paramModelProjection, EpigraphPsiUtil.getLocation(paramPsi));
  }

  public static @Nullable OpModelProjection<?, ?, ?, ?> parseKeyProjection(
      @NotNull DatumTypeApi keyType,
      @NotNull Stream<SchemaOpKeyProjection> projectionPsi,
      @NotNull TypesResolver typesResolver,
      @NotNull OpPsiProcessingContext context) {

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

//  @SuppressWarnings("unchecked")
//  public static <VP extends GenVarProjection<VP, ?, MP>, MP extends GenModelProjection<?, ?, ?, ?>>
//  VP parseNormalizedClause(
//      VP reference,
//      String referenceName,
//      SchemaOpNormalized normalizedPsi,
//      PsiProcessingContext context,
//      ReferenceContext<VP, MP> referenceContext,
//      PsiElement psi,
//      TypesResolver typesResolver
//  ) throws PsiProcessingException {
//
//    if (normalizedPsi == null)
//      return reference;
//    else {
//      SchemaTypeRef typeRefPsi = normalizedPsi.getTypeRef();
//
//      if (typeRefPsi == null)
//        throw new PsiProcessingException("Missing type reference", psi, context.messages());
//
//      TypeRef typeRef = TypeRefs.fromPsi(typeRefPsi, context);
//      EntityTypeApi type = typeRef.resolveEntityType(typesResolver);
//
//      if (type == null) {
//        GenTagProjectionEntry<?, ?> tagProjection = reference.singleTagProjection();
//        if (tagProjection == null)
//          throw new PsiProcessingException(
//              "internal error",
//              typeRefPsi, context
//          );
//
//        MP modelProjection = (MP) tagProjection.projection();
//        MP normalizedModelProjection = parseNormalizedClause(
//            modelProjection,
//            referenceName,
//            normalizedPsi,
//            context,
//            referenceContext,
//            psi,
//            typesResolver
//        );
//
//        return referenceContext.toSelfVar(normalizedModelProjection);
//
//      } else {
//        String normalizedProjectionName = referenceName + "$" + type.name().toString().replace(".", "_");
//        VP result =
//            referenceContext.varReference(type, normalizedProjectionName, true, EpigraphPsiUtil.getLocation(psi));
//
//        final Runnable[] r = new Runnable[1];
//        Runnable runnable = () -> {
//          try {
//            referenceContext.resolveEntityRef(
//                normalizedProjectionName,
//                reference.normalizedForType(type),
//                EpigraphPsiUtil.getLocation(psi)
//            );
//          } catch (UnresolvedReferenceException e) {
//            e.reference().runOnResolved(r[0]);
//          }
//        };
//        reference.runOnResolved(runnable);
//        r[0] = runnable;
//
//        return result;
//      }
//    }
//  }
//
//  @SuppressWarnings("unchecked")
//  public static <MP extends GenModelProjection<?, ?, ?, ?>> MP parseNormalizedClause(
//      MP reference,
//      String referenceName,
//      SchemaOpNormalized normalizedPsi,
//      PsiProcessingContext context,
//      ReferenceContext<?, MP> referenceContext,
//      PsiElement psi,
//      TypesResolver typesResolver
//  ) throws PsiProcessingException {
//
//    if (normalizedPsi == null)
//      return reference;
//    else {
//      SchemaTypeRef typeRefPsi = normalizedPsi.getTypeRef();
//
//      if (typeRefPsi == null)
//        throw new PsiProcessingException("Missing type reference", psi, context.messages());
//
//      TypeRef typeRef = TypeRefs.fromPsi(typeRefPsi, context);
//      DatumTypeApi type = typeRef.resolveDatumType(typesResolver);
//
//      if (type == null)
//        throw new PsiProcessingException(
//            String.format("Can't resolve model type '%s'", typeRefPsi.getText()),
//            typeRefPsi, context
//        );
//
//      String normalizedProjectionName = referenceName + "$" + type.name().toString().replace(".", "_");
//
//      MP result =
//          referenceContext.modelReference(type, normalizedProjectionName, true, EpigraphPsiUtil.getLocation(psi));
//
//      final Runnable[] r = new Runnable[1];
//      final Runnable runnable = () -> {
//        try {
//          referenceContext.resolveModelRef(
//              normalizedProjectionName,
//              (MP) reference.normalizedForType(type),
//              EpigraphPsiUtil.getLocation(psi)
//          );
//        } catch (UnresolvedReferenceException e) {
//          e.reference().runOnResolved(r[0]);
//        }
//      };
//      r[0] = runnable;
//
//      reference.runOnResolved(runnable);
//
//      return result;
//    }
//  }

}
