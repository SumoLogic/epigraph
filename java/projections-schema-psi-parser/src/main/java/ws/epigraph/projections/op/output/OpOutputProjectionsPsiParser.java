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

package ws.epigraph.projections.op.output;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.annotations.Annotations;
import ws.epigraph.projections.ProjectionUtils;
import ws.epigraph.projections.ProjectionsParsingUtil;
import ws.epigraph.projections.ReferenceContext;
import ws.epigraph.projections.SchemaProjectionPsiParserUtil;
import ws.epigraph.projections.gen.GenProjectionReference;
import ws.epigraph.projections.op.OpKeyPresence;
import ws.epigraph.projections.op.OpParams;
import ws.epigraph.projections.op.input.OpInputModelProjection;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.TypeRef;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.schema.TypeRefs;
import ws.epigraph.schema.parser.SchemaPsiParserUtil;
import ws.epigraph.schema.parser.psi.*;
import ws.epigraph.types.*;
import ws.epigraph.types.TypeKind;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import static ws.epigraph.projections.ProjectionsParsingUtil.*;
import static ws.epigraph.projections.SchemaProjectionPsiParserUtil.findTag;
import static ws.epigraph.projections.SchemaProjectionPsiParserUtil.getTag;
import static ws.epigraph.projections.SchemaProjectionPsiParserUtil.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class OpOutputProjectionsPsiParser {

  private OpOutputProjectionsPsiParser() {}

  public static OpOutputVarProjection parseVarProjection(
      @NotNull DataTypeApi dataType,
      @NotNull SchemaOpOutputVarProjection psi,
      @NotNull TypesResolver typesResolver,
      @NotNull OpOutputPsiProcessingContext context) throws PsiProcessingException {

    return parseVarProjection(dataType, psi, null, typesResolver, context);
  }

  public static OpOutputVarProjection parseVarProjection(
      @NotNull DataTypeApi dataType,
      @NotNull SchemaOpOutputVarProjection psi,
      @Nullable OpOutputVarProjection parentProjection,
      @NotNull TypesResolver typesResolver,
      @NotNull OpOutputPsiProcessingContext context) throws PsiProcessingException {

    final SchemaOpOutputNamedVarProjection namedVarProjection = psi.getOpOutputNamedVarProjection();
    if (namedVarProjection == null) {
      final SchemaOpOutputUnnamedOrRefVarProjection unnamedOrRefVarProjection =
          psi.getOpOutputUnnamedOrRefVarProjection();

      if (unnamedOrRefVarProjection == null)
        throw new PsiProcessingException(
            "Incomplete var projection definition",
            psi,
            context.messages()
        );

      return parseUnnamedOrRefVarProjection(
          dataType,
          unnamedOrRefVarProjection,
          typesResolver,
          context
      );
    } else {
      // named var projection
      final String projectionName = namedVarProjection.getQid().getCanonicalName();

      final @Nullable SchemaOpOutputUnnamedOrRefVarProjection unnamedOrRefVarProjectionPsi =
          namedVarProjection.getOpOutputUnnamedOrRefVarProjection();

      if (unnamedOrRefVarProjectionPsi == null)
        throw new PsiProcessingException(
            String.format("Incomplete entity projection '%s' definition", projectionName),
            psi,
            context.messages()
        );

      TypeApi type = dataType.type();

      ReferenceContext<OpOutputVarProjection, OpOutputModelProjection<?, ?, ?>>
          referenceContext = parentProjection == null
                             ? context.referenceContext()                  // not tail: usual context
                             : context.referenceContext().parentOrThis();  // tail: global context

      final OpOutputVarProjection reference = referenceContext
          .varReference(type, projectionName, false, EpigraphPsiUtil.getLocation(psi));

      final OpOutputVarProjection value = parseUnnamedOrRefVarProjection(
          dataType,
          unnamedOrRefVarProjectionPsi,
          typesResolver,
          context
      );

      if (parentProjection == null) {
        referenceContext.resolveEntityRef(
            projectionName,
            value,
            EpigraphPsiUtil.getLocation(unnamedOrRefVarProjectionPsi)
        );

        return reference;
      } else {
        // special case:
        // someProjection = ( ... ) ~~ SubType $subProjection = ( ... )
        // must result in `subProjection` created in the same namespace as `someProjection`
        // (that's why we have to access parent context here)
        // and it's value will be `parentProjection.normalizedForType(SubType)

        // `reference` belongs to parent context and will contain normalized tail

        GenProjectionReference.runOnResolved(
            parentProjection,
            () -> {
              OpOutputVarProjection normalizedTail = parentProjection.normalizedForType(
                  type,
                  referenceContext.projectionReferenceName(projectionName)
              );

              referenceContext.resolveEntityRef( //resolve normalizedTailRef to normalizedTail
                  projectionName,
                  normalizedTail,
                  EpigraphPsiUtil.getLocation(unnamedOrRefVarProjectionPsi)
              );
              // give normalized tail proper alias
//              parentProjection.setNormalizedForType(type, reference);
            }
        );

        return value; // return non-normalized tail
      }
    }
  }

  public static OpOutputVarProjection parseUnnamedOrRefVarProjection(
      @NotNull DataTypeApi dataType,
      @NotNull SchemaOpOutputUnnamedOrRefVarProjection psi,
      @NotNull TypesResolver typesResolver,
      @NotNull OpOutputPsiProcessingContext context)
      throws PsiProcessingException {

    final SchemaOpOutputVarProjectionRef varProjectionRefPsi = psi.getOpOutputVarProjectionRef();
    if (varProjectionRefPsi == null) {
      // usual var projection
      final SchemaOpOutputUnnamedVarProjection unnamedVarProjection = psi.getOpOutputUnnamedVarProjection();
      if (unnamedVarProjection == null)
        throw new PsiProcessingException("Incomplete var projection definition", psi, context.messages());
      else return parseUnnamedVarProjection(
          dataType,
          unnamedVarProjection,
          typesResolver,
          context
      );
    } else {
      // var projection reference
      final SchemaQid refNamePsi = varProjectionRefPsi.getQid();
      if (refNamePsi == null)
        throw new PsiProcessingException(
            "Incomplete entity projection reference: name not specified",
            psi,
            context.messages()
        );

      final String projectionName = refNamePsi.getCanonicalName();

      return context.referenceContext()
          .varReference(dataType.type(), projectionName, true, EpigraphPsiUtil.getLocation(psi));
    }
  }

  public static @NotNull OpOutputVarProjection parseUnnamedVarProjection(
      @NotNull DataTypeApi dataType,
      @NotNull SchemaOpOutputUnnamedVarProjection psi,
      @NotNull TypesResolver typesResolver,
      @NotNull OpOutputPsiProcessingContext context)
      throws PsiProcessingException {

    final TypeApi type = dataType.type();
    final LinkedHashMap<String, OpOutputTagProjectionEntry> tagProjections = new LinkedHashMap<>();

    @Nullable SchemaOpOutputSingleTagProjection singleTagProjectionPsi = psi.getOpOutputSingleTagProjection();

    if (singleTagProjectionPsi == null) {
      @Nullable SchemaOpOutputMultiTagProjection multiTagProjection = psi.getOpOutputMultiTagProjection();
      assert multiTagProjection != null;
      // parse list of tags
      @NotNull List<SchemaOpOutputMultiTagProjectionItem> tagProjectionPsiList =
          multiTagProjection.getOpOutputMultiTagProjectionItemList();

      for (SchemaOpOutputMultiTagProjectionItem tagProjectionPsi : tagProjectionPsiList) {
        final TagApi tag =
            getTag(type, tagProjectionPsi.getTagName(), dataType.defaultTag(), tagProjectionPsi, context);

        tagProjections.put(
            tag.name(),
            new OpOutputTagProjectionEntry(
                tag,
                parseModelProjection(
                    tag.type(),
                    tagProjectionPsi.getOpOutputModelProjection(),
                    typesResolver,
                    context
                ),
                EpigraphPsiUtil.getLocation(tagProjectionPsi)
            )
        );
      }
    } else {
      // todo (here and other parsers): simplify this tag logic
      TagApi tag = findTag(
          type,
          singleTagProjectionPsi.getTagName(),
          dataType.defaultTag(),
          singleTagProjectionPsi,
          context
      );
      if (tag != null || !singleTagProjectionPsi.getText().isEmpty()) {
        if (tag == null) // will throw proper error
          tag = getTag(
              type,
              singleTagProjectionPsi.getTagName(),
              dataType.defaultTag(),
              singleTagProjectionPsi,
              context
          );

        tagProjections.put(
            tag.name(),
            new OpOutputTagProjectionEntry(
                tag,
                parseModelProjection(
                    tag.type(),
                    singleTagProjectionPsi.getOpOutputModelProjection(),
                    typesResolver,
                    context
                ),
                EpigraphPsiUtil.getLocation(singleTagProjectionPsi)
            )
        );
      }
    }

    OpOutputVarProjection result = new OpOutputVarProjection(type, EpigraphPsiUtil.getLocation(psi));

    // parse tails
    final List<OpOutputVarProjection> tails;
    @Nullable SchemaOpOutputVarPolymorphicTail psiTail = psi.getOpOutputVarPolymorphicTail();
    if (psiTail == null) tails = null;
    else {
      tails = new ArrayList<>();

      @Nullable SchemaOpOutputVarTailItem singleTail = psiTail.getOpOutputVarTailItem();
      if (singleTail == null) {
        @Nullable SchemaOpOutputVarMultiTail multiTail = psiTail.getOpOutputVarMultiTail();
        assert multiTail != null;
        for (SchemaOpOutputVarTailItem tailItem : multiTail.getOpOutputVarTailItemList()) {
          tails.add(parseEntityTailItem(tailItem, dataType, result, typesResolver, context));
        }
      } else {
        tails.add(parseEntityTailItem(singleTail, dataType, result, typesResolver, context));
      }

      SchemaProjectionPsiParserUtil.checkDuplicatingEntityTails(tails, context);

    }

    try {
      OpOutputVarProjection tmp = new OpOutputVarProjection(
          type,
          tagProjections,
          singleTagProjectionPsi == null || tagProjections.size() != 1,
          tails,
          EpigraphPsiUtil.getLocation(psi)
      );

      result.resolve(null, tmp);
      return result;
    } catch (Exception e) {
      throw new PsiProcessingException(e, psi, context);
    }
  }

  private static @NotNull OpOutputVarProjection parseEntityTailItem(
      final SchemaOpOutputVarTailItem tailItem,
      final @NotNull DataTypeApi dataType,
      final @NotNull OpOutputVarProjection parentProjection,
      final @NotNull TypesResolver typesResolver,
      final @NotNull OpOutputPsiProcessingContext context) throws PsiProcessingException {

    @NotNull SchemaTypeRef tailTypeRef = tailItem.getTypeRef();
    @NotNull SchemaOpOutputVarProjection psiTailProjection = tailItem.getOpOutputVarProjection();
    return buildTailProjection(dataType, tailTypeRef, psiTailProjection, parentProjection, typesResolver, context);
  }

  private static @NotNull OpParams parseModelParams(
      @NotNull List<SchemaOpOutputModelProperty> modelProperties,
      @NotNull TypesResolver resolver,
      @NotNull OpOutputPsiProcessingContext context) throws PsiProcessingException {


    return parseParams(
        modelProperties.stream().map(SchemaOpOutputModelProperty::getOpParam),
        resolver,
        context.inputPsiProcessingContext()
    );
  }

  private static @NotNull Annotations parseModelAnnotations(
      @NotNull List<SchemaOpOutputModelProperty> modelProperties,
      @NotNull OpOutputPsiProcessingContext context,
      @NotNull TypesResolver resolver) throws PsiProcessingException {

    return SchemaPsiParserUtil.parseAnnotations(
        modelProperties.stream().map(SchemaOpOutputModelProperty::getAnnotation),
        context,
        resolver
    );
  }

  private static @Nullable OpOutputModelProjection<?, ?, ?> parseModelMetaProjection(
      @NotNull DatumTypeApi type,
      @NotNull List<SchemaOpOutputModelProperty> modelProperties,
      @NotNull TypesResolver resolver,
      @NotNull OpOutputPsiProcessingContext context
  ) throws PsiProcessingException {

    @Nullable SchemaOpOutputModelMeta modelMetaPsi = null;

    for (SchemaOpOutputModelProperty modelProperty : modelProperties) {
      if (modelMetaPsi != null)
        context.addError("Metadata projection should be specified only once", modelProperty);

      modelMetaPsi = modelProperty.getOpOutputModelMeta();
    }

    if (modelMetaPsi == null) return null;
    else {
      @Nullable DatumTypeApi metaType = type.metaType();
      if (metaType == null) {
        context.addError(
            String.format("Type '%s' doesn't have a metadata, metadata projection can't be specified", type.name()),
            modelMetaPsi
        );
        return null;
      } else {

        @NotNull SchemaOpOutputModelProjection metaProjectionPsi = modelMetaPsi.getOpOutputModelProjection();
        return parseModelProjection(
            metaType,
            metaProjectionPsi,
            resolver,
            context
        );
      }
    }
  }

  private static @NotNull OpOutputVarProjection buildTailProjection(
      @NotNull DataTypeApi dataType,
      @NotNull SchemaTypeRef tailTypeRefPsi,
      @NotNull SchemaOpOutputVarProjection psiTailProjection,
      @NotNull OpOutputVarProjection parentProjection,
      @NotNull TypesResolver typesResolver,
      @NotNull OpOutputPsiProcessingContext context) throws PsiProcessingException {

    @NotNull TypeRef tailTypeRef = TypeRefs.fromPsi(tailTypeRefPsi, context);
    @NotNull EntityTypeApi tailType = getEntityType(tailTypeRef, typesResolver, tailTypeRefPsi, context);
    checkEntityTailType(tailType, dataType, tailTypeRefPsi, context);

    OpOutputVarProjection ep = parseVarProjection(
        tailType.dataType(dataType.defaultTag()),
        psiTailProjection,
        parentProjection,
        typesResolver,
        context
    );

    checkEntityTailType(tailType, ep, psiTailProjection, context);

    return ep;
  }


  private static @NotNull OpOutputVarProjection createDefaultVarProjection(
      @NotNull TypeApi type,
      @NotNull TagApi tag,
      @NotNull PsiElement locationPsi,
      @NotNull OpOutputPsiProcessingContext context) throws PsiProcessingException {
    return new OpOutputVarProjection(
        type,
        ProjectionUtils.singletonLinkedHashMap(
            tag.name(),
            new OpOutputTagProjectionEntry(
                tag,
                createDefaultModelProjection(
                    tag.type(),
                    OpParams.EMPTY,
                    Annotations.EMPTY,
                    locationPsi,
                    context
                ),
                EpigraphPsiUtil.getLocation(locationPsi)
            )
        ),
        false,
        null,
        EpigraphPsiUtil.getLocation(locationPsi)
    );
  }

  private static @NotNull OpOutputVarProjection createDefaultVarProjection(
      @NotNull DatumTypeApi type,
      @NotNull PsiElement locationPsi,
      @NotNull OpOutputPsiProcessingContext context) throws PsiProcessingException {
    return createDefaultVarProjection(type, type.self(), locationPsi, context);
  }

  public static @NotNull OpOutputVarProjection createDefaultVarProjection(
      @NotNull DataTypeApi type,
      @NotNull PsiElement locationPsi,
      @NotNull OpOutputPsiProcessingContext context)
      throws PsiProcessingException {

    @Nullable TagApi defaultTag = type.defaultTag();
    if (defaultTag == null) {

      if (type.type() instanceof DatumType) {
        DatumTypeApi datumType = (DatumTypeApi) type.type();
        defaultTag = datumType.self();
      } else {
        throw new PsiProcessingException(
            String.format("Can't build default projection for '%s', default tag not specified", type.name()),
            locationPsi,
            context
        );
      }

    }

    return createDefaultVarProjection(type.type(), defaultTag, locationPsi, context);
  }

  public static @NotNull OpOutputModelProjection<?, ?, ?> parseModelProjection(
      @NotNull DatumTypeApi type,
      @NotNull SchemaOpOutputModelProjection psi,
      @NotNull TypesResolver typesResolver,
      @NotNull OpOutputPsiProcessingContext context)
      throws PsiProcessingException {

    return parseModelProjection(
        OpOutputModelProjection.class,
        type,
        psi,
        null,
        typesResolver,
        context
    );

  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private static @NotNull <MP extends OpOutputModelProjection<?, ?, ?>, SMP extends OpOutputModelProjection<SMP, SMP, ?>>
  /*@NotNull*/ MP parseModelProjection(
      @NotNull Class<MP> modelClass,
      @NotNull DatumTypeApi type,
      @NotNull SchemaOpOutputModelProjection psi,
      @Nullable MP parentProjection,
      @NotNull TypesResolver typesResolver,
      @NotNull OpOutputPsiProcessingContext context)
      throws PsiProcessingException {

    // this follows `parseVarProjection` logic

    final SchemaOpOutputNamedModelProjection namedModelProjection = psi.getOpOutputNamedModelProjection();
    if (namedModelProjection == null) {
      final SchemaOpOutputUnnamedOrRefModelProjection unnamedOrRefModelProjection =
          psi.getOpOutputUnnamedOrRefModelProjection();

      if (unnamedOrRefModelProjection == null)
        throw new PsiProcessingException(
            "Incomplete model projection definition",
            psi,
            context.messages()
        );

      return parseUnnamedOrRefModelProjection(
          modelClass,
          type,
          unnamedOrRefModelProjection,
          typesResolver,
          context
      );
    } else {
      // named model projection
      final String projectionName = namedModelProjection.getQid().getCanonicalName();

      final @Nullable SchemaOpOutputUnnamedOrRefModelProjection unnamedOrRefModelProjectionPsi =
          namedModelProjection.getOpOutputUnnamedOrRefModelProjection();

      if (unnamedOrRefModelProjectionPsi == null)
        throw new PsiProcessingException(
            String.format("Incomplete model projection '%s' definition", projectionName),
            psi,
            context.messages()
        );

      ReferenceContext<OpOutputVarProjection, OpOutputModelProjection<?, ?, ?>>
          referenceContext = parentProjection == null
                             ? context.referenceContext()                  // not tail: usual context
                             : context.referenceContext().parentOrThis();  // tail: global context

      final MP reference = (MP) referenceContext
          .modelReference(type, projectionName, false, EpigraphPsiUtil.getLocation(psi));

      final MP value = parseUnnamedOrRefModelProjection(
          modelClass,
          type,
          unnamedOrRefModelProjectionPsi,
          typesResolver,
          context
      );

      if (parentProjection == null) {
        referenceContext.<OpOutputModelProjection>resolveModelRef(
            projectionName,
            value,
            EpigraphPsiUtil.getLocation(unnamedOrRefModelProjectionPsi)
        );

        return reference;
      } else {
        // special case:
        // someProjection = ( ... ) ~~ SubType $subProjection = ( ... )
        // must result in `subProjection` created in the same namespace as `someProjection`
        // (that's why we have to access parent context here)
        // and it's value will be `parentProjection.normalizedForType(SubType)

        // `reference` belongs to parent context and will contain normalized tail

        GenProjectionReference.runOnResolved(
            (OpOutputModelProjection) parentProjection,
            () -> {
              OpOutputModelProjection<?, ?, ?> normalizedTail = parentProjection.normalizedForType(
                  type,
                  referenceContext.projectionReferenceName(projectionName)
              );

              referenceContext.<OpOutputModelProjection>resolveModelRef( //resolve normalizedTailRef to normalizedTail
                  projectionName,
                  normalizedTail,
                  EpigraphPsiUtil.getLocation(unnamedOrRefModelProjectionPsi)
              );

              // give normalized tail proper alias
//              ((OpOutputModelProjection<?, SMP, ?>) parentProjection).setNormalizedForType(
//                  type,
//                  (SMP) reference
//              );
            }
        );

        return value; // return non-normalized tail
      }

    }
  }

  @SuppressWarnings("unchecked")
  private static @NotNull <MP extends OpOutputModelProjection<?, ?, ?>>
  /*@NotNull*/ MP parseUnnamedOrRefModelProjection(
      @NotNull Class<MP> modelClass,
      @NotNull DatumTypeApi type,
      @NotNull SchemaOpOutputUnnamedOrRefModelProjection psi,
      @NotNull TypesResolver typesResolver,
      @NotNull OpOutputPsiProcessingContext context)
      throws PsiProcessingException {

    // this follows `parseUnnamedOrRefVarProjection` logic

    final SchemaOpOutputModelProjectionRef modelProjectionRefPsi = psi.getOpOutputModelProjectionRef();
    if (modelProjectionRefPsi == null) {
      // usual model projection
      final SchemaOpOutputUnnamedModelProjection unnamedModelProjection = psi.getOpOutputUnnamedModelProjection();
      if (unnamedModelProjection == null)
        throw new PsiProcessingException("Incomplete model projection definition", psi, context.messages());
      else return parseUnnamedModelProjection(
          modelClass,
          type,
          unnamedModelProjection,
          typesResolver,
          context
      );
    } else {
      // model projection reference
      final SchemaQid refNamePsi = modelProjectionRefPsi.getQid();
      if (refNamePsi == null)
        throw new PsiProcessingException(
            "Incomplete model projection definition: name not specified",
            psi,
            context.messages()
        );

      final String projectionName = refNamePsi.getCanonicalName();
      return (MP) context.referenceContext().modelReference(
          type,
          projectionName,
          true,
          EpigraphPsiUtil.getLocation(psi)
      );

    }
  }

  @SuppressWarnings("unchecked")
  private static @NotNull <MP extends OpOutputModelProjection<?, ?, ?>>
  /*@NotNull*/ MP parseUnnamedModelProjection(
      @NotNull Class<MP> modelClass,
      @NotNull DatumTypeApi type,
      @NotNull SchemaOpOutputUnnamedModelProjection psi,
      @NotNull TypesResolver typesResolver,
      @NotNull OpOutputPsiProcessingContext context)
      throws PsiProcessingException {

    final List<SchemaOpOutputModelProperty> modelProperties = psi.getOpOutputModelPropertyList();

    final OpParams params = parseModelParams(modelProperties, typesResolver, context);
    final Annotations annotations = parseModelAnnotations(modelProperties, context, typesResolver);
    final OpOutputModelProjection<?, ?, ?> metaProjection =
        parseModelMetaProjection(type, modelProperties, typesResolver, context);

    switch (type.kind()) {
      case RECORD:
        assert modelClass.isAssignableFrom(OpOutputRecordModelProjection.class);
        ensureModelKind(psi, TypeKind.RECORD, context);

        @Nullable SchemaOpOutputRecordModelProjection recordModelProjectionPsi =
            psi.getOpOutputRecordModelProjection();
        if (recordModelProjectionPsi == null)
          return (MP) createDefaultModelProjection(type, params, annotations, psi, context);

        OpOutputRecordModelProjection recordModel =
            new OpOutputRecordModelProjection((RecordTypeApi) type, EpigraphPsiUtil.getLocation(psi));

        OpOutputRecordModelProjection recordModelTemp = parseRecordModelProjection(
            (RecordTypeApi) type,
            params,
            annotations,
            metaProjection,
            parseModelTails(
                OpOutputRecordModelProjection.class,
                psi.getOpOutputModelPolymorphicTail(),
                recordModel,
                typesResolver,
                context
            ),
            recordModelProjectionPsi,
            typesResolver,
            context
        );

        recordModel.resolve(null, recordModelTemp);
        return (MP) recordModel;

      case MAP:
        assert modelClass.isAssignableFrom(OpOutputMapModelProjection.class);
        ensureModelKind(psi, TypeKind.MAP, context);

        @Nullable SchemaOpOutputMapModelProjection mapModelProjectionPsi =
            psi.getOpOutputMapModelProjection();
        if (mapModelProjectionPsi == null)
          return (MP) createDefaultModelProjection(type, params, annotations, psi, context);

        OpOutputMapModelProjection mapModel =
            new OpOutputMapModelProjection((MapTypeApi) type, EpigraphPsiUtil.getLocation(psi));

        OpOutputMapModelProjection mapModelTmp = parseMapModelProjection(
            (MapTypeApi) type,
            params,
            annotations,
            metaProjection,
            parseModelTails(
                OpOutputMapModelProjection.class,
                psi.getOpOutputModelPolymorphicTail(),
                mapModel,
                typesResolver,
                context
            ),
            mapModelProjectionPsi,
            typesResolver,
            context
        );

        mapModel.resolve(null, mapModelTmp);
        return (MP) mapModel;

      case LIST:
        assert modelClass.isAssignableFrom(OpOutputListModelProjection.class);
        ensureModelKind(psi, TypeKind.LIST, context);

        @Nullable SchemaOpOutputListModelProjection listModelProjectionPsi =
            psi.getOpOutputListModelProjection();

        if (listModelProjectionPsi == null)
          return (MP) createDefaultModelProjection(type, params, annotations, psi, context);

        OpOutputListModelProjection listModel =
            new OpOutputListModelProjection((ListTypeApi) type, EpigraphPsiUtil.getLocation(psi));

        OpOutputListModelProjection listModelTmp = parseListModelProjection(
            (ListTypeApi) type,
            params,
            annotations,
            metaProjection,
            parseModelTails(
                OpOutputListModelProjection.class,
                psi.getOpOutputModelPolymorphicTail(),
                listModel,
                typesResolver,
                context
            ),
            listModelProjectionPsi,
            typesResolver,
            context
        );

        listModel.resolve(null, listModelTmp);
        return (MP) listModel;

      case ENUM:
        throw new PsiProcessingException("Unsupported type kind: " + type.kind(), psi, context);

      case PRIMITIVE:
        assert modelClass.isAssignableFrom(OpOutputPrimitiveModelProjection.class);

        OpOutputPrimitiveModelProjection primitiveModel =
            new OpOutputPrimitiveModelProjection((PrimitiveTypeApi) type, EpigraphPsiUtil.getLocation(psi));

        OpOutputPrimitiveModelProjection primitiveModelTmp = parsePrimitiveModelProjection(
            (PrimitiveTypeApi) type,
            params,
            annotations,
            metaProjection,
            parseModelTails(
                OpOutputPrimitiveModelProjection.class,
                psi.getOpOutputModelPolymorphicTail(),
                primitiveModel,
                typesResolver,
                context
            ),
            psi
        );

        primitiveModel.resolve(null, primitiveModelTmp);
        return (MP) primitiveModel;

      case ENTITY:
        throw new PsiProcessingException("Unsupported type kind: " + type.kind(), psi, context);

      default:
        throw new PsiProcessingException("Unknown type kind: " + type.kind(), psi, context);
    }
  }

  @Contract("_, null, _, _, _ -> null")
  private static @Nullable <MP extends OpOutputModelProjection<?, ?, ?>>
  /*@Nullable*/ List<MP> parseModelTails(
      @NotNull Class<MP> modelClass,
      @Nullable SchemaOpOutputModelPolymorphicTail tailPsi,
      @NotNull MP parentProjection,
      @NotNull TypesResolver typesResolver,
      @NotNull OpOutputPsiProcessingContext context) throws PsiProcessingException {

    if (tailPsi == null) return null;
    else {
      List<MP> tails = new ArrayList<>();

      final @Nullable SchemaOpOutputModelTailItem singleTailPsi = tailPsi.getOpOutputModelTailItem();
      if (singleTailPsi == null) {
        final SchemaOpOutputModelMultiTail multiTailPsi = tailPsi.getOpOutputModelMultiTail();
        assert multiTailPsi != null;
        for (SchemaOpOutputModelTailItem tailItemPsi : multiTailPsi.getOpOutputModelTailItemList()) {
          tails.add(
              buildModelTailProjection(
                  modelClass,
                  tailItemPsi.getTypeRef(),
                  tailItemPsi.getOpOutputModelProjection(),
                  parentProjection,
                  typesResolver,
                  context
              )
          );
        }
      } else {
        tails.add(
            buildModelTailProjection(
                modelClass,
                singleTailPsi.getTypeRef(),
                singleTailPsi.getOpOutputModelProjection(),
                parentProjection,
                typesResolver,
                context
            )
        );
      }

      SchemaProjectionPsiParserUtil.checkDuplicatingModelTails(tails, context);

      return tails;
    }
  }

  private static @NotNull <MP extends OpOutputModelProjection<?, ?, ?>>
  /*@NotNull*/ MP buildModelTailProjection(
      @NotNull Class<MP> modelClass,
      @NotNull SchemaTypeRef tailTypeRefPsi,
      @NotNull SchemaOpOutputModelProjection modelProjectionPsi,
      @NotNull MP parentProjection,
      @NotNull TypesResolver typesResolver,
      @NotNull OpOutputPsiProcessingContext context) throws PsiProcessingException {

    @NotNull TypeRef tailTypeRef = TypeRefs.fromPsi(tailTypeRefPsi, context);
    @NotNull DatumTypeApi tailType = getDatumType(tailTypeRef, typesResolver, tailTypeRefPsi, context);

    MP mp = parseModelProjection(
        modelClass,
        tailType,
        modelProjectionPsi,
        parentProjection,
        typesResolver,
        context
    );

    checkModelTailType(tailType, mp, modelProjectionPsi, context);

    return mp;
  }

  private static void ensureModelKind(
      @NotNull SchemaOpOutputUnnamedModelProjection psi,
      @NotNull TypeKind expectedKind,
      @NotNull OpOutputPsiProcessingContext context) throws PsiProcessingException {

    @Nullable TypeKind actualKind = findProjectionKind(psi);
    if (actualKind != null && expectedKind != actualKind)
      throw new PsiProcessingException(MessageFormat.format(
          "Unexpected projection kind ''{0}'', expected ''{1}''",
          actualKind,
          expectedKind
      ), psi, context);
  }

  private static @Nullable TypeKind findProjectionKind(@NotNull SchemaOpOutputUnnamedModelProjection psi) {
    if (psi.getOpOutputRecordModelProjection() != null) return TypeKind.RECORD;
    if (psi.getOpOutputMapModelProjection() != null) return TypeKind.MAP;
    if (psi.getOpOutputListModelProjection() != null) return TypeKind.LIST;
    return null;
  }

  private static @NotNull OpOutputModelProjection<?, ?, ?> createDefaultModelProjection(
      @NotNull DatumTypeApi type,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @NotNull PsiElement locationPsi,
      @NotNull OpOutputPsiProcessingContext context)
      throws PsiProcessingException {

    switch (type.kind()) {
      case RECORD:
        return new OpOutputRecordModelProjection(
            (RecordTypeApi) type,
            params,
            annotations,
            null,
            Collections.emptyMap(),
            null,
            EpigraphPsiUtil.getLocation(locationPsi)
        );
      case MAP:
        MapTypeApi mapType = (MapTypeApi) type;

        final OpOutputKeyProjection keyProjection =
            new OpOutputKeyProjection(
                OpKeyPresence.OPTIONAL,
                OpParams.EMPTY,
                Annotations.EMPTY,
                null,
                EpigraphPsiUtil.getLocation(locationPsi)
            );

        @NotNull DataTypeApi valueType = mapType.valueType();
        @Nullable TagApi defaultValuesTag = valueType.defaultTag();

        if (defaultValuesTag == null)
          throw new PsiProcessingException(String.format(
              "Can't create default projection for map type '%s, as it's value type '%s' doesn't have a default tag",
              type.name(),
              valueType.name()
          ), locationPsi, context);

        final OpOutputVarProjection valueVarProjection = createDefaultVarProjection(
            valueType.type(),
            defaultValuesTag,
            locationPsi,
            context
        );

        return new OpOutputMapModelProjection(
            mapType,
            params,
            annotations,
            null,
            keyProjection,
            valueVarProjection,
            null,
            EpigraphPsiUtil.getLocation(locationPsi)
        );
      case LIST:
        ListTypeApi listType = (ListTypeApi) type;
        @NotNull DataTypeApi elementType = listType.elementType();
        @Nullable TagApi defaultElementsTag = elementType.defaultTag();

        if (defaultElementsTag == null)
          throw new PsiProcessingException(String.format(
              "Can't create default projection for list type '%s, as it's element type '%s' doesn't have a default tag",
              type.name(),
              elementType.name()
          ), locationPsi, context);

        final OpOutputVarProjection itemVarProjection = createDefaultVarProjection(
            elementType.type(),
            defaultElementsTag,
            locationPsi,
            context
        );

        return new OpOutputListModelProjection(
            listType,
            params,
            annotations,
            null,
            itemVarProjection,
            null,
            EpigraphPsiUtil.getLocation(locationPsi)
        );
      case ENTITY:
        throw new PsiProcessingException(
            "Was expecting to get datum model kind, got: " + type.kind(),
            locationPsi,
            context
        );
      case ENUM:
        throw new PsiProcessingException("Unsupported type kind: " + type.kind(), locationPsi, context);
      case PRIMITIVE:
        return new OpOutputPrimitiveModelProjection(
            (PrimitiveTypeApi) type,
            params,
            annotations,
            null,
            null,
            EpigraphPsiUtil.getLocation(locationPsi)
        );
      default:
        throw new PsiProcessingException("Unknown type kind: " + type.kind(), locationPsi, context);
    }
  }

  public static @NotNull OpOutputRecordModelProjection parseRecordModelProjection(
      @NotNull RecordTypeApi type,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @Nullable OpOutputModelProjection<?, ?, ?> metaProjection,
      @Nullable List<OpOutputRecordModelProjection> tails,
      @NotNull SchemaOpOutputRecordModelProjection psi,
      @NotNull TypesResolver typesResolver,
      @NotNull OpOutputPsiProcessingContext context) {

    LinkedHashMap<String, OpOutputFieldProjectionEntry> fieldProjections = new LinkedHashMap<>();
    @NotNull List<SchemaOpOutputFieldProjectionEntry> fieldProjectionEntriesPsi =
        psi.getOpOutputFieldProjectionEntryList();

    for (SchemaOpOutputFieldProjectionEntry fieldProjectionEntryPsi : fieldProjectionEntriesPsi) {
      try {
        final String fieldName = fieldProjectionEntryPsi.getQid().getCanonicalName();
        FieldApi field = type.fieldsMap().get(fieldName);

        if (field == null) {
          context.addError(
              String.format(
                  "Unknown field '%s' in type '%s'; known fields: {%s}",
                  fieldName,
                  type.name(),
                  String.join(", ", type.fieldsMap().keySet())
              ),
              fieldProjectionEntryPsi
          );
          continue;
        }

        final @Nullable SchemaOpOutputFieldProjection fieldProjectionPsi =
            fieldProjectionEntryPsi.getOpOutputFieldProjection();

        if (fieldProjectionPsi == null)
          context.addError("Incomplete definition for field '" + fieldName + "'", psi);
        else {
          final OpOutputFieldProjection opOutputFieldProjection = parseFieldProjection(
              field.dataType(),
              fieldProjectionPsi,
              typesResolver,
              context
          );

          fieldProjections.put(
              fieldName,
              new OpOutputFieldProjectionEntry(
                  field,
                  opOutputFieldProjection,
                  EpigraphPsiUtil.getLocation(fieldProjectionPsi)
              )
          );
        }
      } catch (PsiProcessingException e) {
        context.addException(e);
      }
    }

    return new OpOutputRecordModelProjection(
        type,
        params,
        annotations,
        metaProjection,
        fieldProjections,
        tails,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  public static @NotNull OpOutputFieldProjection parseFieldProjection(
      @NotNull DataTypeApi fieldType,
      @NotNull SchemaOpOutputFieldProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull OpOutputPsiProcessingContext context) throws PsiProcessingException {

//    List<OpParam> fieldParamsList = null;
//    @Nullable Map<String, Annotation> fieldAnnotationsMap = null;
//    for (SchemaOpOutputFieldProjectionBodyPart fieldBodyPart : psi.getOpOutputFieldProjectionBodyPartList()) {
//      @Nullable SchemaOpParam fieldParamPsi = fieldBodyPart.getOpParam();
//      if (fieldParamPsi != null) {
//        if (fieldParamsList == null) fieldParamsList = new ArrayList<>(3);
//        fieldParamsList.add(parseParameter(fieldParamPsi, resolver, context));
//      }
//
//      fieldAnnotationsMap = parseAnnotation(fieldAnnotationsMap, fieldBodyPart.getAnnotation(), context);
//    }

    final OpOutputVarProjection varProjection =
        parseVarProjection(fieldType, psi.getOpOutputVarProjection(), resolver, context);

    ProjectionsParsingUtil.verifyData(fieldType, varProjection, psi, context);

    return new OpOutputFieldProjection(
//        OpParams.fromCollection(fieldParamsList),
//        Annotations.fromMap(fieldAnnotationsMap),
        varProjection,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  public static @NotNull OpOutputMapModelProjection parseMapModelProjection(
      @NotNull MapTypeApi type,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @Nullable OpOutputModelProjection<?, ?, ?> metaProjection,
      @Nullable List<OpOutputMapModelProjection> tails,
      @NotNull SchemaOpOutputMapModelProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull OpOutputPsiProcessingContext context)
      throws PsiProcessingException {

    @NotNull OpOutputKeyProjection keyProjection =
        parseKeyProjection(type.keyType(), psi.getOpOutputKeyProjection(), resolver, context);

    @Nullable SchemaOpOutputVarProjection valueProjectionPsi = psi.getOpOutputVarProjection();
    @NotNull OpOutputVarProjection valueProjection =
        valueProjectionPsi == null
        ? createDefaultVarProjection(
            type.valueType(),
            psi,
            context
        )
        : parseVarProjection(type.valueType(), valueProjectionPsi, resolver, context);

    return new OpOutputMapModelProjection(
        type,
        params,
        annotations,
        metaProjection,
        keyProjection,
        valueProjection,
        tails,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  private static @NotNull OpOutputKeyProjection parseKeyProjection(
      @NotNull DatumTypeApi keyType,
      @NotNull SchemaOpOutputKeyProjection keyProjectionPsi,
      @NotNull TypesResolver resolver,
      @NotNull OpOutputPsiProcessingContext context) throws PsiProcessingException {

    final OpKeyPresence presence;

    if (keyProjectionPsi.getForbidden() != null)
      presence = OpKeyPresence.FORBIDDEN;
    else if (keyProjectionPsi.getRequired() != null)
      presence = OpKeyPresence.REQUIRED;
    else
      presence = OpKeyPresence.OPTIONAL;

    final @NotNull List<SchemaOpOutputKeyProjectionPart> keyPartsPsi =
        keyProjectionPsi.getOpOutputKeyProjectionPartList();

    final @NotNull OpParams keyParams =
        parseParams(
            keyPartsPsi.stream().map(SchemaOpOutputKeyProjectionPart::getOpParam),
            resolver,
            context.inputPsiProcessingContext()
        );
    final @NotNull Annotations keyAnnotations =
        SchemaPsiParserUtil.parseAnnotations(
            keyPartsPsi.stream().map(SchemaOpOutputKeyProjectionPart::getAnnotation),
            context,
            resolver
        );

    OpInputModelProjection<?, ?, ?, ?> keyProjection = SchemaProjectionPsiParserUtil.parseKeyProjection(
        keyType,
        keyPartsPsi.stream().map(SchemaOpOutputKeyProjectionPart::getOpKeyProjection),
        resolver,
        context.inputPsiProcessingContext()
    );

    return new OpOutputKeyProjection(
        presence,
        keyParams,
        keyAnnotations,
        keyProjection,
        EpigraphPsiUtil.getLocation(keyProjectionPsi)
    );
  }

  public static @NotNull OpOutputListModelProjection parseListModelProjection(
      @NotNull ListTypeApi type,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @Nullable OpOutputModelProjection<?, ?, ?> metaProjection,
      @Nullable List<OpOutputListModelProjection> tails,
      @NotNull SchemaOpOutputListModelProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull OpOutputPsiProcessingContext context)
      throws PsiProcessingException {

    OpOutputVarProjection itemsProjection;
    @Nullable SchemaOpOutputVarProjection opOutputVarProjectionPsi = psi.getOpOutputVarProjection();
    if (opOutputVarProjectionPsi == null)
      itemsProjection = createDefaultVarProjection(type, psi, context);
    else
      itemsProjection = parseVarProjection(type.elementType(), opOutputVarProjectionPsi, resolver, context);


    return new OpOutputListModelProjection(
        type,
        params,
        annotations,
        metaProjection,
        itemsProjection,
        tails,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  public static @NotNull OpOutputPrimitiveModelProjection parsePrimitiveModelProjection(
      @NotNull PrimitiveTypeApi type,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @Nullable OpOutputModelProjection<?, ?, ?> metaProjection,
      @Nullable List<OpOutputPrimitiveModelProjection> tails,
      @NotNull PsiElement locationPsi) {

    return new OpOutputPrimitiveModelProjection(
        type,
        params,
        annotations,
        metaProjection,
        tails,
        EpigraphPsiUtil.getLocation(locationPsi)
    );
  }

}
