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
import ws.epigraph.gdata.*;
import ws.epigraph.projections.ProjectionUtils;
import ws.epigraph.projections.ProjectionsParsingUtil;
import ws.epigraph.projections.ReferenceContext;
import ws.epigraph.projections.SchemaProjectionPsiParserUtil;
import ws.epigraph.projections.gen.ProjectionReferenceName;
import ws.epigraph.projections.op.OpKeyPresence;
import ws.epigraph.projections.op.OpParams;
import ws.epigraph.projections.op.input.OpInputModelProjection;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.TypeRef;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.schema.TypeRefs;
import ws.epigraph.schema.gdata.SchemaGDataPsiParser;
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
public final class OpProjectionsPsiParser {

  private OpProjectionsPsiParser() {}

  public static OpOutputVarProjection parseVarProjection(
      @NotNull DataTypeApi dataType,
      boolean flagged,
      @NotNull SchemaOpOutputVarProjection psi,
      @NotNull TypesResolver typesResolver,
      @NotNull OpOutputPsiProcessingContext context) throws PsiProcessingException {

    return parseVarProjection(dataType, flagged, psi, null, typesResolver, context);
  }

  static OpOutputVarProjection parseVarProjection(
      @NotNull DataTypeApi dataType,
      boolean flagged,
      @NotNull SchemaOpOutputVarProjection psi,
      @Nullable OpOutputVarProjection parentProjection, // if parsing tail: projection this tail is attached to
      @NotNull TypesResolver typesResolver,
      @NotNull OpOutputPsiProcessingContext context) throws PsiProcessingException {

    final SchemaOpOutputNamedVarProjection namedVarProjection = psi.getOpOutputNamedVarProjection();
    if (namedVarProjection == null) {
      final SchemaOpOutputUnnamedOrRefVarProjection unnamedOrRefVarProjection =
          psi.getOpOutputUnnamedOrRefVarProjection();

      if (unnamedOrRefVarProjection == null)
        throw new PsiProcessingException(
            "Incomplete entity projection definition",
            psi,
            context.messages()
        );

      return parseUnnamedOrRefVarProjection(
          dataType,
          flagged,
          unnamedOrRefVarProjection,
          typesResolver,
          context
      );
    } else {
      // named var projection
      final @NotNull String projectionName = namedVarProjection.getQid().getCanonicalName();

      final @Nullable SchemaOpOutputUnnamedOrRefVarProjection unnamedOrRefVarProjectionPsi =
          namedVarProjection.getOpOutputUnnamedOrRefVarProjection();

      if (unnamedOrRefVarProjectionPsi == null)
        throw new PsiProcessingException(
            String.format("Incomplete entity projection '%s' definition", projectionName),
            psi,
            context.messages()
        );

      TypeApi type = dataType.type();

      ReferenceContext<OpOutputVarProjection, OpOutputModelProjection<?, ?, ?, ?>>
          referenceContext = parentProjection == null
                             ? context.referenceContext()                  // not tail: usual context
                             : context.referenceContext().parentOrThis();  // tail: global context

      final OpOutputVarProjection reference = referenceContext
          .entityReference(type, projectionName, false, EpigraphPsiUtil.getLocation(psi));

      final OpOutputVarProjection value = parseUnnamedOrRefVarProjection(
          dataType,
          flagged,
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
        // someProjection = ( ... ) :~ SubType $subProjection = ( ... )
        // must result in `subProjection` created in the same namespace as `someProjection`
        // (that's why we have to access parent context here)
        // and it's value will be `parentProjection.normalizedForType(SubType)

        // `reference` belongs to parent context and will contain normalized tail

        ProjectionReferenceName referenceName = referenceContext.projectionReferenceName(projectionName);
        parentProjection.setNormalizedTailReferenceName(type, referenceName);
        // add parent reference that will call `normalizedForType` when dereferenced
        referenceContext.addEntityReference(
            projectionName,
            new ReferenceContext.RefItem<>(
                parentProjection,
                p -> p.normalizedForType(type),
                EpigraphPsiUtil.getLocation(unnamedOrRefVarProjectionPsi)
            )
        );

//        GenProjectionReference.runOnResolved(
//            parentProjection,
//            () -> {
//              OpOutputVarProjection normalizedTail = parentProjection.normalizedForType(
//                  type,
//                  referenceName
//              );
//
//              referenceContext.resolveEntityRef( //resolve normalizedTailRef to normalizedTail
//                  projectionName,
//                  normalizedTail,
//                  EpigraphPsiUtil.getLocation(unnamedOrRefVarProjectionPsi)
//              );
//            }
//        );

        return value; // return non-normalized tail
      }
    }
  }

  public static OpOutputVarProjection parseUnnamedOrRefVarProjection(
      @NotNull DataTypeApi dataType,
      boolean flagged,
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
          flagged,
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
          .entityReference(dataType.type(), projectionName, true, EpigraphPsiUtil.getLocation(refNamePsi));
    }
  }

  static @NotNull OpOutputVarProjection parseUnnamedVarProjection(
      @NotNull DataTypeApi dataType,
      boolean flagged,
      @NotNull SchemaOpOutputUnnamedVarProjection psi,
      @NotNull TypesResolver typesResolver,
      @NotNull OpOutputPsiProcessingContext context)
      throws PsiProcessingException {

    final TypeApi type = dataType.type();
    final LinkedHashMap<String, OpOutputTagProjectionEntry> tagProjections;

    @Nullable SchemaOpOutputSingleTagProjection singleTagProjectionPsi = psi.getOpOutputSingleTagProjection();

    if (singleTagProjectionPsi == null) {
      @Nullable SchemaOpOutputMultiTagProjection multiTagProjection = psi.getOpOutputMultiTagProjection();
      assert multiTagProjection != null;
      tagProjections = parseMultiTagProjection(dataType, multiTagProjection, typesResolver, context);
    } else {
      // todo (here and other parsers): simplify this tag logic
      tagProjections = new LinkedHashMap<>();
      TagApi defaultTag = dataType.defaultTag();

      TagApi tag = findTag(
          type,
          singleTagProjectionPsi.getTagName(),
          defaultTag,
          singleTagProjectionPsi,
          context
      );
      if (tag != null || !singleTagProjectionPsi.getText().isEmpty()) {
        if (tag == null) // will throw proper error
          tag = getTag(
              type,
              singleTagProjectionPsi.getTagName(),
              defaultTag,
              singleTagProjectionPsi,
              context
          );

        tagProjections.put(
            tag.name(),
            new OpOutputTagProjectionEntry(
                tag,
                parseModelProjection(
                    tag.type(),
                    singleTagProjectionPsi.getPlus() != null,
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

    final List<OpOutputVarProjection> tails =
        parseTails(result, dataType, psi.getOpOutputVarPolymorphicTail(), typesResolver, context);

    try {
      OpOutputVarProjection tmp = new OpOutputVarProjection(
          type,
          flagged,
          tagProjections,
          singleTagProjectionPsi == null || tagProjections.size() != 1,
          tails,
          EpigraphPsiUtil.getLocation(psi)
      );

      result.resolve(null, tmp);
      return result;
    } catch (RuntimeException e) {
      throw new PsiProcessingException(e, psi, context);
    }
  }

  static @NotNull LinkedHashMap<String, OpOutputTagProjectionEntry> parseMultiTagProjection(
      final @NotNull DataTypeApi dataType,
      final @NotNull SchemaOpOutputMultiTagProjection multiTagProjection,
      final @NotNull TypesResolver typesResolver,
      final @NotNull OpOutputPsiProcessingContext context) throws PsiProcessingException {

    final LinkedHashMap<String, OpOutputTagProjectionEntry> tagProjections = new LinkedHashMap<>();

    @NotNull List<SchemaOpOutputMultiTagProjectionItem> tagProjectionPsiList =
        multiTagProjection.getOpOutputMultiTagProjectionItemList();

    for (SchemaOpOutputMultiTagProjectionItem tagProjectionPsi : tagProjectionPsiList) {
      final TagApi tag =
          getTag(dataType.type(), tagProjectionPsi.getTagName(), dataType.defaultTag(), tagProjectionPsi, context);

      tagProjections.put(
          tag.name(),
          new OpOutputTagProjectionEntry(
              tag,
              parseModelProjection(
                  tag.type(),
                  tagProjectionPsi.getPlus() != null,
                  tagProjectionPsi.getOpOutputModelProjection(),
                  typesResolver,
                  context
              ),
              EpigraphPsiUtil.getLocation(tagProjectionPsi)
          )
      );
    }

    return tagProjections;
  }

  static @Nullable List<OpOutputVarProjection> parseTails(
      final @NotNull OpOutputVarProjection parentProjection,
      final @NotNull DataTypeApi dataType,
      final @Nullable SchemaOpOutputVarPolymorphicTail tailPsi,
      final @NotNull TypesResolver typesResolver,
      final @NotNull OpOutputPsiProcessingContext context)
      throws PsiProcessingException {

    final List<OpOutputVarProjection> tails;

    if (tailPsi == null) tails = null;
    else {
      tails = new ArrayList<>();

      @Nullable SchemaOpOutputVarTailItem singleTail = tailPsi.getOpOutputVarTailItem();
      if (singleTail == null) {
        @Nullable SchemaOpOutputVarMultiTail multiTail = tailPsi.getOpOutputVarMultiTail();
        assert multiTail != null;
        for (SchemaOpOutputVarTailItem tailItem : multiTail.getOpOutputVarTailItemList()) {
          tails.add(parseEntityTailItem(tailItem, dataType, parentProjection, typesResolver, context));
        }
      } else {
        tails.add(parseEntityTailItem(singleTail, dataType, parentProjection, typesResolver, context));
      }

      SchemaProjectionPsiParserUtil.checkDuplicatingEntityTails(tails, context);

    }
    return tails;
  }

  private static @NotNull OpOutputVarProjection parseEntityTailItem(
      final @NotNull SchemaOpOutputVarTailItem tailItem,
      final @NotNull DataTypeApi dataType,
      final @NotNull OpOutputVarProjection parentProjection,
      final @NotNull TypesResolver typesResolver,
      final @NotNull OpOutputPsiProcessingContext context) throws PsiProcessingException {

    @NotNull SchemaTypeRef tailTypeRef = tailItem.getTypeRef();
    @NotNull SchemaOpOutputVarProjection psiTailProjection = tailItem.getOpOutputVarProjection();
    return buildTailProjection(dataType, tailTypeRef, psiTailProjection, parentProjection, typesResolver, context);
  }

  private static @Nullable GDatum getModelDefaultValue(
      @NotNull List<SchemaOpOutputModelProperty> modelProperties,
      @NotNull OpOutputPsiProcessingContext context) throws PsiProcessingException {

    GDatum result = null;
    for (SchemaOpOutputModelProperty property : modelProperties) {
      @Nullable SchemaOpOutputDefaultValue defaultValuePsi = property.getOpOutputDefaultValue();
      if (defaultValuePsi != null) {
        if (result == null) {
          @Nullable SchemaDatum varValuePsi = defaultValuePsi.getDatum();
          if (varValuePsi != null)
            result = SchemaGDataPsiParser.parseDatum(varValuePsi, context);
        } else {
          context.addError("Default value should only be specified once", defaultValuePsi);
        }
      }
    }

    return result;
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

  private static @Nullable OpOutputModelProjection<?, ?, ?, ?> parseModelMetaProjection(
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
            modelMetaPsi.getPlus() != null,
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
        false, // todo allow flags on tails
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
        false,
        ProjectionUtils.singletonLinkedHashMap(
            tag.name(),
            new OpOutputTagProjectionEntry(
                tag,
                createDefaultModelProjection(
                    tag.type(),
                    false,
                    null,
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
            String.format("Can't build default projection for '%s', retro tag not specified", type.name()),
            locationPsi,
            context
        );
      }

    }

    return createDefaultVarProjection(type.type(), defaultTag, locationPsi, context);
  }

  static @NotNull OpOutputModelProjection<?, ?, ?, ?> parseModelProjection(
      @NotNull DatumTypeApi type,
      boolean flagged,
      @NotNull SchemaOpOutputModelProjection psi,
      @NotNull TypesResolver typesResolver,
      @NotNull OpOutputPsiProcessingContext context)
      throws PsiProcessingException {

    return parseModelProjection(
        OpOutputModelProjection.class,
        type,
        flagged,
        psi,
        null,
        typesResolver,
        context
    );

  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private static @NotNull <MP extends OpOutputModelProjection<?, ?, ?, ?>>
  /*@NotNull*/ MP parseModelProjection(
      @NotNull Class<MP> modelClass,
      @NotNull DatumTypeApi type,
      boolean flagged,
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
          flagged,
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

      ReferenceContext<OpOutputVarProjection, OpOutputModelProjection<?, ?, ?, ?>>
          referenceContext = parentProjection == null
                             ? context.referenceContext()                  // not tail: usual context
                             : context.referenceContext().parentOrThis();  // tail: global context

      final MP reference = (MP) referenceContext
          .modelReference(type, projectionName, false, EpigraphPsiUtil.getLocation(psi));

      final MP value = parseUnnamedOrRefModelProjection(
          modelClass,
          type,
          flagged,
          unnamedOrRefModelProjectionPsi,
          typesResolver,
          context
      );

      if (parentProjection == null) {
        referenceContext.resolveModelRef(
            projectionName,
            value,
            EpigraphPsiUtil.getLocation(unnamedOrRefModelProjectionPsi)
        );

        return reference;
      } else {
        // special case:
        // someProjection = ( ... ) :~ SubType $subProjection = ( ... )
        // must result in `subProjection` created in the same namespace as `someProjection`
        // (that's why we have to access parent context here)
        // and it's value will be `parentProjection.normalizedForType(SubType)

        // `reference` belongs to parent context and will contain normalized tail

        ProjectionReferenceName referenceName = referenceContext.projectionReferenceName(projectionName);

        parentProjection.setNormalizedTailReferenceName(type, referenceName);
        // add parent reference that will call `normalizedForType` when dereferenced
        referenceContext.addModelReference(
            projectionName,
            new ReferenceContext.RefItem<>(
                parentProjection,
                p -> p.normalizedForType(type),
                EpigraphPsiUtil.getLocation(unnamedOrRefModelProjectionPsi)
            )
        );

//        GenProjectionReference.runOnResolved(
//            (OpOutputModelProjection) parentProjection,
//            () -> {
//              OpOutputModelProjection<?, ?, ?, ?> normalizedTail = parentProjection.normalizedForType(
//                  type,
//                  referenceName
//              );
//
//              referenceContext.<OpOutputModelProjection>resolveModelRef( //resolve normalizedTailRef to normalizedTail
//                  projectionName,
//                  normalizedTail,
//                  EpigraphPsiUtil.getLocation(unnamedOrRefModelProjectionPsi)
//              );
//            }
//        );

        return value; // return non-normalized tail
      }

    }
  }

  @SuppressWarnings("unchecked")
  private static @NotNull <MP extends OpOutputModelProjection<?, ?, ?, ?>>
  /*@NotNull*/ MP parseUnnamedOrRefModelProjection(
      @NotNull Class<MP> modelClass,
      @NotNull DatumTypeApi type,
      boolean flagged,
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
          flagged,
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
  private static @NotNull <MP extends OpOutputModelProjection<?, ?, ?, ?>>
  /*@NotNull*/ MP parseUnnamedModelProjection(
      @NotNull Class<MP> modelClass,
      @NotNull DatumTypeApi type,
      boolean flagged,
      @NotNull SchemaOpOutputUnnamedModelProjection psi,
      @NotNull TypesResolver typesResolver,
      @NotNull OpOutputPsiProcessingContext context)
      throws PsiProcessingException {

    final List<SchemaOpOutputModelProperty> modelProperties = psi.getOpOutputModelPropertyList();

    final GDatum defaultValue = getModelDefaultValue(modelProperties, context);
    final OpParams params = parseModelParams(modelProperties, typesResolver, context);
    final Annotations annotations = parseModelAnnotations(modelProperties, context, typesResolver);
    final OpOutputModelProjection<?, ?, ?, ?> metaProjection =
        parseModelMetaProjection(type, modelProperties, typesResolver, context);

    switch (type.kind()) {
      case RECORD:
        assert modelClass.isAssignableFrom(OpOutputRecordModelProjection.class);
        ensureModelKind(psi, TypeKind.RECORD, context);

        @Nullable SchemaOpOutputRecordModelProjection recordModelProjectionPsi =
            psi.getOpOutputRecordModelProjection();

        if (recordModelProjectionPsi == null)
          return (MP) createDefaultModelProjection(type, flagged, defaultValue, params, annotations, psi, context);

        GRecordDatum defaultRecordData = coerceDefault(defaultValue, GRecordDatum.class, psi, context);

        OpOutputRecordModelProjection recordModel =
            new OpOutputRecordModelProjection((RecordTypeApi) type, EpigraphPsiUtil.getLocation(psi));

        OpOutputRecordModelProjection recordModelTemp = parseRecordModelProjection(
            (RecordTypeApi) type,
            flagged,
            defaultRecordData,
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
          return (MP) createDefaultModelProjection(type, flagged, defaultValue, params, annotations, psi, context);

        GMapDatum defaultMapData = coerceDefault(defaultValue, GMapDatum.class, psi, context);

        OpOutputMapModelProjection mapModel =
            new OpOutputMapModelProjection((MapTypeApi) type, EpigraphPsiUtil.getLocation(psi));

        OpOutputMapModelProjection mapModelTmp = parseMapModelProjection(
            (MapTypeApi) type,
            flagged,
            defaultMapData,
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
          return (MP) createDefaultModelProjection(type, flagged, defaultValue, params, annotations, psi, context);

        GListDatum defaultListData = coerceDefault(defaultValue, GListDatum.class, psi, context);

        OpOutputListModelProjection listModel =
            new OpOutputListModelProjection((ListTypeApi) type, EpigraphPsiUtil.getLocation(psi));

        OpOutputListModelProjection listModelTmp = parseListModelProjection(
            (ListTypeApi) type,
            flagged,
            defaultListData,
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

        GPrimitiveDatum defaultPrimitiveData = coerceDefault(defaultValue, GPrimitiveDatum.class, psi, context);

        OpOutputPrimitiveModelProjection primitiveModel =
            new OpOutputPrimitiveModelProjection((PrimitiveTypeApi) type, EpigraphPsiUtil.getLocation(psi));

        OpOutputPrimitiveModelProjection primitiveModelTmp = parsePrimitiveModelProjection(
            (PrimitiveTypeApi) type,
            flagged,
            defaultPrimitiveData,
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
  private static @Nullable <MP extends OpOutputModelProjection<?, ?, ?, ?>>
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

  private static @NotNull <MP extends OpOutputModelProjection<?, ?, ?, ?>>
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
        false, // todo add flags to tails?
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

  private static @NotNull OpOutputModelProjection<?, ?, ?, ?> createDefaultModelProjection(
      @NotNull DatumTypeApi type,
      boolean flagged,
      @Nullable GDatum defaultValue,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @NotNull PsiElement locationPsi,
      @NotNull OpOutputPsiProcessingContext context)
      throws PsiProcessingException {

    switch (type.kind()) {
      case RECORD:
        return new OpOutputRecordModelProjection(
            (RecordTypeApi) type,
            flagged,
            (GRecordDatum) defaultValue,
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
              "Can't create default projection for map type '%s, as it's value type '%s' doesn't have a retro tag",
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
            flagged,
            (GMapDatum) defaultValue,
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
            flagged,
            (GListDatum) defaultValue,
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
            flagged,
            (GPrimitiveDatum) defaultValue,
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

  static @NotNull OpOutputRecordModelProjection parseRecordModelProjection(
      @NotNull RecordTypeApi type,
      boolean flagged,
      @Nullable GRecordDatum defaultValue,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @Nullable OpOutputModelProjection<?, ?, ?, ?> metaProjection,
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
          context.addError("Incomplete definition for field '" + fieldName + "'", fieldProjectionEntryPsi);
        else {
          final OpOutputFieldProjection opOutputFieldProjection = parseFieldProjection(
              field.dataType(),
              fieldProjectionEntryPsi.getPlus() != null,
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
        flagged,
        defaultValue,
        params,
        annotations,
        metaProjection,
        fieldProjections,
        tails,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  static @NotNull OpOutputFieldProjection parseFieldProjection(
      @NotNull DataTypeApi fieldType,
      boolean flagged,
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
        parseVarProjection(fieldType, flagged, psi.getOpOutputVarProjection(), resolver, context);

    ProjectionsParsingUtil.verifyData(fieldType, varProjection, psi, context);

    return new OpOutputFieldProjection(
//        OpParams.fromCollection(fieldParamsList),
//        Annotations.fromMap(fieldAnnotationsMap),
        varProjection,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  static @NotNull OpOutputMapModelProjection parseMapModelProjection(
      @NotNull MapTypeApi type,
      boolean flagged,
      @Nullable GMapDatum defaultValue,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @Nullable OpOutputModelProjection<?, ?, ?, ?> metaProjection,
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
        : parseVarProjection(type.valueType(), psi.getPlus() != null, valueProjectionPsi, resolver, context);

    return new OpOutputMapModelProjection(
        type,
        flagged,
        defaultValue,
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

  static @NotNull OpOutputListModelProjection parseListModelProjection(
      @NotNull ListTypeApi type,
      boolean flagged,
      @Nullable GListDatum defaultValue,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @Nullable OpOutputModelProjection<?, ?, ?, ?> metaProjection,
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
      itemsProjection =
          parseVarProjection(type.elementType(), psi.getPlus() != null, opOutputVarProjectionPsi, resolver, context);

    return new OpOutputListModelProjection(
        type,
        flagged,
        defaultValue,
        params,
        annotations,
        metaProjection,
        itemsProjection,
        tails,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  private static @NotNull OpOutputPrimitiveModelProjection parsePrimitiveModelProjection(
      @NotNull PrimitiveTypeApi type,
      boolean flagged,
      @Nullable GPrimitiveDatum defaultValue,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @Nullable OpOutputModelProjection<?, ?, ?, ?> metaProjection,
      @Nullable List<OpOutputPrimitiveModelProjection> tails,
      @NotNull PsiElement locationPsi) {

    return new OpOutputPrimitiveModelProjection(
        type,
        flagged,
        defaultValue,
        params,
        annotations,
        metaProjection,
        tails,
        EpigraphPsiUtil.getLocation(locationPsi)
    );
  }

  @SuppressWarnings("unchecked")
  private static @Nullable <D extends GDatum> D coerceDefault(
      @Nullable GDatum defaultValue,
      Class<D> cls,
      @NotNull PsiElement location,
      @NotNull OpOutputPsiProcessingContext context) throws PsiProcessingException {

    if (defaultValue == null) return null;
    if (defaultValue instanceof GNullDatum) return null;
    if (defaultValue.getClass().equals(cls))
      return (D) defaultValue;
    throw new PsiProcessingException(
        String.format("Invalid default value '%s', expected to get '%s'", defaultValue, cls.getName()),
        location,
        context
    );
  }
}
