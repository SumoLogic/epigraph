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

package ws.epigraph.projections.op;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.annotations.Annotations;
import ws.epigraph.gdata.*;
import ws.epigraph.gdata.validation.OpInputGDataValidator;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.ProjectionUtils;
import ws.epigraph.projections.ProjectionsParsingUtil;
import ws.epigraph.projections.ReferenceContext;
import ws.epigraph.projections.SchemaProjectionPsiParserUtil;
import ws.epigraph.projections.gen.ProjectionReferenceName;
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
import java.util.*;

import static ws.epigraph.projections.ProjectionsParsingUtil.*;
import static ws.epigraph.projections.SchemaProjectionPsiParserUtil.findTag;
import static ws.epigraph.projections.SchemaProjectionPsiParserUtil.getTag;
import static ws.epigraph.projections.SchemaProjectionPsiParserUtil.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class OpBasicProjectionPsiParser {

  private OpBasicProjectionPsiParser() {}

  public static OpProjection<?, ?> parseProjection(
      @NotNull DataTypeApi dataType,
      boolean flagged,
      @NotNull SchemaOpEntityProjection psi,
      @NotNull TypesResolver typesResolver,
      @NotNull OpPsiProcessingContext context) throws PsiProcessingException {

    return parseProjection(dataType, flagged, psi, null, typesResolver, context);
  }

  static OpProjection<?, ?> parseProjection(
      @NotNull DataTypeApi dataType,
      boolean flagged,
      @NotNull SchemaOpEntityProjection psi,
      @Nullable OpProjection<?, ?> rootProjection,
      // if parsing tail: root projection this tail is transitively attached to
      @NotNull TypesResolver typesResolver,
      @NotNull OpPsiProcessingContext context) throws PsiProcessingException {

    final SchemaOpNamedEntityProjection namedEntityProjection = psi.getOpNamedEntityProjection();
    if (namedEntityProjection == null) {
      final SchemaOpUnnamedOrRefEntityProjection unnamedOrRefEntityProjection =
          psi.getOpUnnamedOrRefEntityProjection();

      if (unnamedOrRefEntityProjection == null)
        throw new PsiProcessingException(
            "Incomplete projection definition",
            psi,
            context.messages()
        );

      return parseUnnamedOrRefProjection(
          dataType,
          flagged,
          unnamedOrRefEntityProjection,
          rootProjection,
          typesResolver,
          context
      );
    } else {
      // named entity projection
      final @NotNull String projectionName = namedEntityProjection.getQid().getCanonicalName();

      final @Nullable SchemaOpUnnamedOrRefEntityProjection unnamedOrRefEntityProjectionPsi =
          namedEntityProjection.getOpUnnamedOrRefEntityProjection();

      if (unnamedOrRefEntityProjectionPsi == null)
        throw new PsiProcessingException(
            String.format("Incomplete projection '%s' definition", projectionName),
            psi,
            context.messages()
        );

      TypeApi type = dataType.type();

      ReferenceContext<OpProjection<?, ?>, OpEntityProjection, OpModelProjection<?, ?, ?, ?>>
          referenceContext = rootProjection == null
                             ? context.referenceContext()                  // not tail: usual context
                             : context.referenceContext().parentOrThis();  // tail: global context

      OpProjection<?, ?> reference =
          referenceContext.reference(type, projectionName, false, EpigraphPsiUtil.getLocation(psi));
//      final OpEntityProjection reference = referenceContext
//          .entityReference(type, projectionName, false, EpigraphPsiUtil.getLocation(psi));

      final OpProjection<?, ?> value = parseUnnamedOrRefProjection(
          dataType,
          flagged,
          unnamedOrRefEntityProjectionPsi,
          rootProjection,
          typesResolver,
          context
      );

      if (rootProjection == null) {
        referenceContext.resolveRef(
            projectionName,
            value,
            EpigraphPsiUtil.getLocation(unnamedOrRefEntityProjectionPsi)
        );

        return reference;
      } else {
        // special case:
        // someProjection = ( ... ) :~ SubType $subProjection = ( ... )
        // must result in `subProjection` created in the same namespace as `someProjection`
        // (that's why we have to access parent context here)
        // and it's value will be `rootProjection.normalizedForType(SubType)

        // `reference` belongs to parent context and will contain normalized tail

        ProjectionReferenceName referenceName = referenceContext.projectionReferenceName(projectionName);
        rootProjection.setNormalizedTailReferenceName(type, referenceName);
        // add parent reference that will call `normalizedForType` when dereferenced
        referenceContext.addReference(
            projectionName,
            new ReferenceContext.RefItem<>(
                rootProjection,
                p -> p.normalizedForType(type),
                EpigraphPsiUtil.getLocation(unnamedOrRefEntityProjectionPsi)
            )
        );

        return value; // return non-normalized tail
      }
    }
  }

  public static OpProjection<?, ?> parseUnnamedOrRefProjection(
      @NotNull DataTypeApi dataType,
      boolean flagged,
      @NotNull SchemaOpUnnamedOrRefEntityProjection psi,
      @Nullable OpProjection<?, ?> rootProjection,
      @NotNull TypesResolver typesResolver,
      @NotNull OpPsiProcessingContext context)
      throws PsiProcessingException {

    final SchemaOpEntityProjectionRef entityProjectionRefPsi = psi.getOpEntityProjectionRef();
    if (entityProjectionRefPsi == null) {
      // usual entity projection
      final SchemaOpUnnamedEntityProjection unnamedEntityProjection = psi.getOpUnnamedEntityProjection();
      if (unnamedEntityProjection == null)
        throw new PsiProcessingException("Incomplete projection definition", psi, context.messages());
      else return parseUnnamedProjection(
          dataType,
          flagged,
          unnamedEntityProjection,
          rootProjection,
          typesResolver,
          context
      );
    } else {
      // entity projection reference
      final SchemaQid refNamePsi = entityProjectionRefPsi.getQid();
      if (refNamePsi == null)
        throw new PsiProcessingException(
            "Incomplete projection reference: name not specified",
            psi,
            context.messages()
        );

      final String projectionName = refNamePsi.getCanonicalName();

      return context.referenceContext()
          .entityReference(dataType.type(), projectionName, true, EpigraphPsiUtil.getLocation(refNamePsi));
    }
  }

  static @NotNull OpProjection<?, ?> parseUnnamedProjection(
      @NotNull DataTypeApi dataType,
      boolean flagged,
      @NotNull SchemaOpUnnamedEntityProjection psi,
      @Nullable OpProjection<?, ?> rootProjection,
      @NotNull TypesResolver typesResolver,
      @NotNull OpPsiProcessingContext context)
      throws PsiProcessingException {

    final TypeApi type = dataType.type();
    final LinkedHashMap<String, OpTagProjectionEntry> tagProjections;

    @Nullable SchemaOpSingleTagProjection singleTagProjectionPsi = psi.getOpSingleTagProjection();

    if (singleTagProjectionPsi == null) {
      @Nullable SchemaOpMultiTagProjection multiTagProjection = psi.getOpMultiTagProjection();
      assert multiTagProjection != null;
      tagProjections = parseMultiTagProjection(dataType, multiTagProjection, typesResolver, context);
    } else {
      tagProjections = new LinkedHashMap<>();

      TagApi tag = findTag(
          dataType,
          singleTagProjectionPsi.getTagName(),
          EpigraphPsiUtil.getLocation(singleTagProjectionPsi),
          context
      );

      if (tag == null && !singleTagProjectionPsi.getText().isEmpty()) {
        // can't deduce the tag but there's a projection specified for it
        raiseNoTagsError(dataType, singleTagProjectionPsi, context);
      }

      if (tag != null) {

        tagProjections.put(
            tag.name(),
            new OpTagProjectionEntry(
                tag,
                parseModelProjection(
                    tag.type(),
                    singleTagProjectionPsi.getPlus() != null,
                    singleTagProjectionPsi.getOpModelProjection(),
                    typesResolver,
                    context
                ),
                EpigraphPsiUtil.getLocation(singleTagProjectionPsi)
            )
        );
      }
    }

    if (type.kind() == TypeKind.ENTITY) {
      OpEntityProjection result = new OpEntityProjection(type, EpigraphPsiUtil.getLocation(psi));

      final List<OpEntityProjection> tails =
          parseEntityTails(
              dataType,
              psi.getOpEntityPolymorphicTail(),
              rootProjection == null ? result : rootProjection.asEntityProjection(),
              typesResolver,
              context
          );

      try {
        OpEntityProjection tmp = new OpEntityProjection(
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
    } else {
      if (tagProjections.size() == 1) {
        return tagProjections.values().iterator().next().modelProjection();
      } else {
        throw new PsiProcessingException(
            String.format("Projection for model type '%s' should have exactly one tag defined, %d found",
                type.name(), tagProjections.size()
            ),
            EpigraphPsiUtil.getLocation(psi),
            context
        );
      }
    }
  }

  static @NotNull LinkedHashMap<String, OpTagProjectionEntry> parseMultiTagProjection(
      final @NotNull DataTypeApi dataType,
      final @NotNull SchemaOpMultiTagProjection multiTagProjection,
      final @NotNull TypesResolver typesResolver,
      final @NotNull OpPsiProcessingContext context) throws PsiProcessingException {

    final LinkedHashMap<String, OpTagProjectionEntry> tagProjections = new LinkedHashMap<>();

    @NotNull List<SchemaOpMultiTagProjectionItem> tagProjectionPsiList =
        multiTagProjection.getOpMultiTagProjectionItemList();

    for (SchemaOpMultiTagProjectionItem tagProjectionPsi : tagProjectionPsiList) {
      @NotNull TagApi tag = getTag(
          dataType,
          tagProjectionPsi.getTagName(),
          EpigraphPsiUtil.getLocation(tagProjectionPsi),
          context
      );

      tagProjections.put(
          tag.name(),
          new OpTagProjectionEntry(
              tag,
              parseModelProjection(
                  tag.type(),
                  tagProjectionPsi.getPlus() != null,
                  tagProjectionPsi.getOpModelProjection(),
                  typesResolver,
                  context
              ),
              EpigraphPsiUtil.getLocation(tagProjectionPsi)
          )
      );
    }

    return tagProjections;
  }

  private static List<OpEntityProjection> parseEntityTails(
      final @NotNull DataTypeApi dataType,
      final @Nullable SchemaOpEntityPolymorphicTail tailPsi,
      final @NotNull OpEntityProjection rootProjection,
      final @NotNull TypesResolver typesResolver,
      final @NotNull OpPsiProcessingContext context)
      throws PsiProcessingException {

    final List<OpEntityProjection> tails;

    if (tailPsi == null) tails = null;
    else {
      tails = new ArrayList<>();
      @Nullable SchemaOpEntityTailItem singleTail = tailPsi.getOpEntityTailItem();
      if (singleTail == null) {
        @Nullable SchemaOpEntityMultiTail multiTail = tailPsi.getOpEntityMultiTail();
        assert multiTail != null;
        for (SchemaOpEntityTailItem tailItem : multiTail.getOpEntityTailItemList()) {
          tails.add(parseEntityTailItem(tailItem, dataType, rootProjection, typesResolver, context));
        }
      } else {
        tails.add(parseEntityTailItem(singleTail, dataType, rootProjection, typesResolver, context));
      }

      SchemaProjectionPsiParserUtil.checkDuplicatingEntityTails(tails, context);

    }
    return tails;
  }

  private static @NotNull OpEntityProjection parseEntityTailItem(
      final @NotNull SchemaOpEntityTailItem tailItem,
      final @NotNull DataTypeApi dataType,
      final @NotNull OpEntityProjection rootProjection,
      final @NotNull TypesResolver typesResolver,
      final @NotNull OpPsiProcessingContext context) throws PsiProcessingException {

    @NotNull SchemaTypeRef tailTypeRef = tailItem.getTypeRef();
    @NotNull SchemaOpEntityProjection psiTailProjection = tailItem.getOpEntityProjection();
    return buildEntityTailProjection(
        dataType,
        tailItem.getPlus() != null,
        tailTypeRef,
        psiTailProjection,
        rootProjection,
        typesResolver,
        context
    );
  }

  private static @NotNull OpEntityProjection buildEntityTailProjection(
      @NotNull DataTypeApi dataType,
      boolean flagged,
      @NotNull SchemaTypeRef tailTypeRefPsi,
      @NotNull SchemaOpEntityProjection psiTailProjection,
      @NotNull OpEntityProjection rootProjection,
      @NotNull TypesResolver typesResolver,
      @NotNull OpPsiProcessingContext context) throws PsiProcessingException {

    @NotNull TypeRef tailTypeRef = TypeRefs.fromPsi(tailTypeRefPsi, context);
    EntityTypeApi tailType =
        getEntityType(tailTypeRef, false, typesResolver, EpigraphPsiUtil.getLocation(tailTypeRefPsi), context);
    assert tailType != null;
    checkEntityTailType(tailType, dataType, tailTypeRefPsi, context);

    OpEntityProjection ep = parseProjection(
        tailType.dataType(dataType.retroTag()),
        flagged, // todo allow flags on tails
        psiTailProjection,
        rootProjection,
        typesResolver,
        context
    ).asEntityProjection();

    checkEntityTailType(rootProjection.type(), tailType, ep.type(), psiTailProjection, context);

    return ep;
  }

  private static @Nullable GDatum getModelDefaultValue(
      @NotNull List<SchemaOpModelProperty> modelProperties,
      @NotNull OpPsiProcessingContext context) throws PsiProcessingException {

    GDatum result = null;
    for (SchemaOpModelProperty property : modelProperties) {
      @Nullable SchemaOpDefaultValue defaultValuePsi = property.getOpDefaultValue();
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
      @NotNull List<SchemaOpModelProperty> modelProperties,
      @NotNull TypesResolver resolver,
      @NotNull OpPsiProcessingContext context) throws PsiProcessingException {

    return parseParams(
        modelProperties.stream().map(SchemaOpModelProperty::getOpParam),
        resolver,
        context
    );
  }

  private static @NotNull Annotations parseModelAnnotations(
      @NotNull List<SchemaOpModelProperty> modelProperties,
      @NotNull OpPsiProcessingContext context,
      @NotNull TypesResolver resolver) throws PsiProcessingException {

    return SchemaPsiParserUtil.parseAnnotations(
        modelProperties.stream().map(SchemaOpModelProperty::getAnnotation),
        context,
        resolver
    );
  }

  private static @Nullable OpModelProjection<?, ?, ?, ?> parseModelMetaProjection(
      @NotNull DatumTypeApi type,
      @NotNull List<SchemaOpModelProperty> modelProperties,
      @NotNull TypesResolver resolver,
      @NotNull OpPsiProcessingContext context
  ) throws PsiProcessingException {

    @Nullable SchemaOpModelMeta modelMetaPsi = null;

    for (SchemaOpModelProperty modelProperty : modelProperties) {
      if (modelMetaPsi != null)
        context.addError("Metadata projection should be specified only once", modelProperty);

      modelMetaPsi = modelProperty.getOpModelMeta();
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

        @NotNull SchemaOpModelProjection metaProjectionPsi = modelMetaPsi.getOpModelProjection();
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

  private static @NotNull OpEntityProjection createDefaultEntityProjection(
      @NotNull TypeApi type,
      @NotNull TagApi tag,
      @NotNull PsiElement locationPsi,
      @NotNull OpPsiProcessingContext context) throws PsiProcessingException {
    return new OpEntityProjection(
        type,
        false,
        ProjectionUtils.singletonLinkedHashMap(
            tag.name(),
            new OpTagProjectionEntry(
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

  private static @NotNull OpProjection<?, ?> createDefaultProjection(
      @NotNull DatumTypeApi type,
      @NotNull PsiElement locationPsi,
      @NotNull OpPsiProcessingContext context) throws PsiProcessingException {

    return createDefaultModelProjection(
        type,
        false,
        null,
        OpParams.EMPTY,
        Annotations.EMPTY,
        locationPsi,
        context
    );
  }

  public static @NotNull OpProjection<?, ?> createDefaultProjection(
      @NotNull DataTypeApi type,
      @NotNull PsiElement locationPsi,
      @NotNull OpPsiProcessingContext context)
      throws PsiProcessingException {

    if (type.type().kind() == TypeKind.ENTITY) {

      @Nullable TagApi defaultTag = type.retroTag();
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

      return createDefaultEntityProjection(type.type(), defaultTag, locationPsi, context);
    } else {
      return createDefaultProjection((DatumTypeApi) (type.type()), locationPsi, context);
    }
  }

  public static @NotNull OpModelProjection<?, ?, ?, ?> parseModelProjection(
      @NotNull DatumTypeApi type,
      boolean flagged,
      @NotNull SchemaOpModelProjection psi,
      @NotNull TypesResolver typesResolver,
      @NotNull OpPsiProcessingContext context)
      throws PsiProcessingException {

    return parseModelProjection(
        OpModelProjection.class,
        type,
        flagged,
        psi,
        null,
        typesResolver,
        context
    );

  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private static @NotNull <MP extends OpModelProjection<?, ?, ?, ?>>
    /*@NotNull*/ MP parseModelProjection(
      @NotNull Class<MP> modelClass,
      @NotNull DatumTypeApi type,
      boolean flagged,
      @NotNull SchemaOpModelProjection psi,
      @Nullable MP rootProjection,
      @NotNull TypesResolver typesResolver,
      @NotNull OpPsiProcessingContext context)
      throws PsiProcessingException {

    // this follows `parseEntityProjection` logic

    final SchemaOpNamedModelProjection namedModelProjection = psi.getOpNamedModelProjection();
    if (namedModelProjection == null) {
      final SchemaOpUnnamedOrRefModelProjection unnamedOrRefModelProjection =
          psi.getOpUnnamedOrRefModelProjection();

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
          rootProjection,
          typesResolver,
          context
      );
    } else {
      // named model projection
      final String projectionName = namedModelProjection.getQid().getCanonicalName();

      final @Nullable SchemaOpUnnamedOrRefModelProjection unnamedOrRefModelProjectionPsi =
          namedModelProjection.getOpUnnamedOrRefModelProjection();

      if (unnamedOrRefModelProjectionPsi == null)
        throw new PsiProcessingException(
            String.format("Incomplete model projection '%s' definition", projectionName),
            psi,
            context.messages()
        );

      ReferenceContext<OpProjection<?, ?>, OpEntityProjection, OpModelProjection<?, ?, ?, ?>>
          referenceContext = rootProjection == null
                             ? context.referenceContext()                  // not tail: usual context
                             : context.referenceContext().parentOrThis();  // tail: global context

      final MP reference = (MP) referenceContext
          .modelReference(type, projectionName, false, EpigraphPsiUtil.getLocation(psi));

      final MP value = parseUnnamedOrRefModelProjection(
          modelClass,
          type,
          flagged,
          unnamedOrRefModelProjectionPsi,
          rootProjection,
          typesResolver,
          context
      );

      if (rootProjection == null) {
        referenceContext.resolveRef(
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
        // and it's value will be `rootProjection.normalizedForType(SubType)

        // `reference` belongs to parent context and will contain normalized tail

        ProjectionReferenceName referenceName = referenceContext.projectionReferenceName(projectionName);

        rootProjection.setNormalizedTailReferenceName(type, referenceName);
        // add parent reference that will call `normalizedForType` when dereferenced
        referenceContext.addReference(
            projectionName,
            new ReferenceContext.RefItem<>(
                rootProjection,
                p -> p.normalizedForType(type),
                EpigraphPsiUtil.getLocation(unnamedOrRefModelProjectionPsi)
            )
        );

        return value; // return non-normalized tail
      }

    }
  }

  @SuppressWarnings("unchecked")
  private static @NotNull <MP extends OpModelProjection<?, ?, ?, ?>>
    /*@NotNull*/ MP parseUnnamedOrRefModelProjection(
      @NotNull Class<MP> modelClass,
      @NotNull DatumTypeApi type,
      boolean flagged,
      @NotNull SchemaOpUnnamedOrRefModelProjection psi,
      @Nullable MP rootProjection,
      @NotNull TypesResolver typesResolver,
      @NotNull OpPsiProcessingContext context)
      throws PsiProcessingException {

    // this follows `parseUnnamedOrRefProjection` logic

    final SchemaOpModelProjectionRef modelProjectionRefPsi = psi.getOpModelProjectionRef();
    if (modelProjectionRefPsi == null) {
      // usual model projection
      final SchemaOpUnnamedModelProjection unnamedModelProjection = psi.getOpUnnamedModelProjection();
      if (unnamedModelProjection == null)
        throw new PsiProcessingException("Incomplete model projection definition", psi, context.messages());
      else return parseUnnamedModelProjection(
          modelClass,
          type,
          flagged,
          unnamedModelProjection,
          rootProjection,
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
  private static @NotNull <MP extends OpModelProjection<?, ?, ?, ?>>
    /*@NotNull*/ MP parseUnnamedModelProjection(
      @NotNull Class<MP> modelClass,
      @NotNull DatumTypeApi type,
      boolean flagged,
      @NotNull SchemaOpUnnamedModelProjection psi,
      @Nullable MP rootProjection,
      @NotNull TypesResolver typesResolver,
      @NotNull OpPsiProcessingContext context)
      throws PsiProcessingException {

    final List<SchemaOpModelProperty> modelProperties = psi.getOpModelPropertyList();

    final GDatum defaultValue = getModelDefaultValue(modelProperties, context);
    final OpParams params = parseModelParams(modelProperties, typesResolver, context);
    final Annotations annotations = parseModelAnnotations(modelProperties, context, typesResolver);
    final OpModelProjection<?, ?, ?, ?> metaProjection =
        parseModelMetaProjection(type, modelProperties, typesResolver, context);

    final MP result;

    switch (type.kind()) {
      case RECORD:
        assert modelClass.isAssignableFrom(OpRecordModelProjection.class);
        ensureModelKind(psi, TypeKind.RECORD, context);

        @Nullable SchemaOpRecordModelProjection recordModelProjectionPsi =
            psi.getOpRecordModelProjection();

        if (recordModelProjectionPsi == null)
          result = (MP) createDefaultModelProjection(type, flagged, defaultValue, params, annotations, psi, context);
        else {
          GRecordDatum defaultRecordData = coerceDefault(defaultValue, GRecordDatum.class, psi, context);

          OpRecordModelProjection recordModelProjection =
              new OpRecordModelProjection((RecordTypeApi) type, EpigraphPsiUtil.getLocation(psi));

          OpRecordModelProjection recordModelTemp = parseRecordModelProjection(
              (RecordTypeApi) type,
              flagged,
              defaultRecordData,
              params,
              annotations,
              metaProjection,
              parseModelTails(
                  OpRecordModelProjection.class,
                  psi.getOpModelPolymorphicTail(),
                  rootProjection == null ? recordModelProjection : (OpRecordModelProjection) rootProjection,
                  typesResolver,
                  context
              ),
              recordModelProjectionPsi,
              typesResolver,
              context
          );

          recordModelProjection.resolve(null, recordModelTemp);
          result = (MP) recordModelProjection;
        }
        break;

      case MAP:
        assert modelClass.isAssignableFrom(OpMapModelProjection.class);
        ensureModelKind(psi, TypeKind.MAP, context);

        @Nullable SchemaOpMapModelProjection mapModelProjectionPsi =
            psi.getOpMapModelProjection();

        if (mapModelProjectionPsi == null)
          result = (MP) createDefaultModelProjection(type, flagged, defaultValue, params, annotations, psi, context);
        else {
          GMapDatum defaultMapData = coerceDefault(defaultValue, GMapDatum.class, psi, context);

          OpMapModelProjection mapModelProjection =
              new OpMapModelProjection((MapTypeApi) type, EpigraphPsiUtil.getLocation(psi));

          OpMapModelProjection mapModelTmp = parseMapModelProjection(
              (MapTypeApi) type,
              flagged,
              defaultMapData,
              params,
              annotations,
              metaProjection,
              parseModelTails(
                  OpMapModelProjection.class,
                  psi.getOpModelPolymorphicTail(),
                  rootProjection == null ? mapModelProjection : (OpMapModelProjection) rootProjection,
                  typesResolver,
                  context
              ),
              mapModelProjectionPsi,
              typesResolver,
              context
          );

          mapModelProjection.resolve(null, mapModelTmp);
          result = (MP) mapModelProjection;
        }
        break;

      case LIST:
        assert modelClass.isAssignableFrom(OpListModelProjection.class);
        ensureModelKind(psi, TypeKind.LIST, context);

        @Nullable SchemaOpListModelProjection listModelProjectionPsi =
            psi.getOpListModelProjection();

        if (listModelProjectionPsi == null)
          result = (MP) createDefaultModelProjection(type, flagged, defaultValue, params, annotations, psi, context);
        else {

          GListDatum defaultListData = coerceDefault(defaultValue, GListDatum.class, psi, context);

          OpListModelProjection listModelProjection =
              new OpListModelProjection((ListTypeApi) type, EpigraphPsiUtil.getLocation(psi));

          OpListModelProjection listModelTmp = parseListModelProjection(
              (ListTypeApi) type,
              flagged,
              defaultListData,
              params,
              annotations,
              metaProjection,
              parseModelTails(
                  OpListModelProjection.class,
                  psi.getOpModelPolymorphicTail(),
                  rootProjection == null ? listModelProjection : (OpListModelProjection) rootProjection,
                  typesResolver,
                  context
              ),
              listModelProjectionPsi,
              typesResolver,
              context
          );

          listModelProjection.resolve(null, listModelTmp);
          result = (MP) listModelProjection;
        }
        break;

      case ENUM:
        throw new PsiProcessingException("Unsupported type kind: " + type.kind(), psi, context);

      case PRIMITIVE:
        assert modelClass.isAssignableFrom(OpPrimitiveModelProjection.class);

        GPrimitiveDatum defaultPrimitiveData = coerceDefault(defaultValue, GPrimitiveDatum.class, psi, context);

        OpPrimitiveModelProjection primitiveModelProjection =
            new OpPrimitiveModelProjection((PrimitiveTypeApi) type, EpigraphPsiUtil.getLocation(psi));

        OpPrimitiveModelProjection primitiveModelTmp = parsePrimitiveModelProjection(
            (PrimitiveTypeApi) type,
            flagged,
            defaultPrimitiveData,
            params,
            annotations,
            metaProjection,
            parseModelTails(
                OpPrimitiveModelProjection.class,
                psi.getOpModelPolymorphicTail(),
                rootProjection == null ? primitiveModelProjection : (OpPrimitiveModelProjection) rootProjection,
                typesResolver,
                context
            ),
            psi
        );

        primitiveModelProjection.resolve(null, primitiveModelTmp);
        result = (MP) primitiveModelProjection;
        break;

      case ENTITY:
        throw new PsiProcessingException("Unsupported type kind: " + type.kind(), psi, context);

      default:
        throw new PsiProcessingException("Unknown type kind: " + type.kind(), psi, context);
    }

    if (defaultValue != null) {
      OpInputGDataValidator validator = new OpInputGDataValidator(typesResolver);
      validator.validateDatum(defaultValue, result);
      validator.errors().forEach(ve -> context.addError(ve.message(), ve.textLocation()));
    }

    return result;
  }

  @Contract("_, null, _, _, _ -> null")
  private static @Nullable <MP extends OpModelProjection<?, ?, ?, ?>>
    /*@Nullable*/ List<MP> parseModelTails(
      @NotNull Class<MP> modelClass,
      @Nullable SchemaOpModelPolymorphicTail tailPsi,
      @NotNull MP rootProjection,
      @NotNull TypesResolver typesResolver,
      @NotNull OpPsiProcessingContext context) throws PsiProcessingException {

    if (tailPsi == null) return null;
    else {
      List<MP> tails = new ArrayList<>();

      final @Nullable SchemaOpModelTailItem singleTailPsi = tailPsi.getOpModelTailItem();
      if (singleTailPsi == null) {
        final SchemaOpModelMultiTail multiTailPsi = tailPsi.getOpModelMultiTail();
        assert multiTailPsi != null;
        for (SchemaOpModelTailItem tailItemPsi : multiTailPsi.getOpModelTailItemList()) {
          tails.add(
              buildModelTailProjection(
                  modelClass,
                  tailItemPsi.getTypeRef(),
                  tailItemPsi.getOpModelProjection(),
                  rootProjection,
                  tailItemPsi.getPlus() != null,
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
                singleTailPsi.getOpModelProjection(),
                rootProjection,
                singleTailPsi.getPlus() != null,
                typesResolver,
                context
            )
        );
      }

      SchemaProjectionPsiParserUtil.checkDuplicatingModelTails(tails, context);

      return tails;
    }
  }

  private static @NotNull <MP extends OpModelProjection<?, ?, ?, ?>>
    /*@NotNull*/ MP buildModelTailProjection(
      @NotNull Class<MP> modelClass,
      @NotNull SchemaTypeRef tailTypeRefPsi,
      @NotNull SchemaOpModelProjection modelProjectionPsi,
      @NotNull MP rootProjection,
      boolean flagged,
      @NotNull TypesResolver typesResolver,
      @NotNull OpPsiProcessingContext context) throws PsiProcessingException {

    @NotNull TypeRef tailTypeRef = TypeRefs.fromPsi(tailTypeRefPsi, context);
    DatumTypeApi tailType =
        getDatumType(tailTypeRef, false, typesResolver, EpigraphPsiUtil.getLocation(tailTypeRefPsi), context);
    assert tailType != null;

    MP mp = parseModelProjection(
        modelClass,
        tailType,
        flagged,
        modelProjectionPsi,
        rootProjection,
        typesResolver,
        context
    );

    checkModelTailType(
        rootProjection.type(),
        tailType,
        mp.type(),
        EpigraphPsiUtil.getLocation(modelProjectionPsi),
        context
    );

    return mp;
  }

  private static void ensureModelKind(
      @NotNull SchemaOpUnnamedModelProjection psi,
      @NotNull TypeKind expectedKind,
      @NotNull OpPsiProcessingContext context) throws PsiProcessingException {

    @Nullable TypeKind actualKind = findProjectionKind(psi);
    if (actualKind != null && expectedKind != actualKind)
      throw new PsiProcessingException(MessageFormat.format(
          "Unexpected projection kind ''{0}'', expected ''{1}''",
          actualKind,
          expectedKind
      ), psi, context);
  }

  private static @Nullable TypeKind findProjectionKind(@NotNull SchemaOpUnnamedModelProjection psi) {
    if (psi.getOpRecordModelProjection() != null) return TypeKind.RECORD;
    if (psi.getOpMapModelProjection() != null) return TypeKind.MAP;
    if (psi.getOpListModelProjection() != null) return TypeKind.LIST;
    return null;
  }

  public static @NotNull OpModelProjection<?, ?, ?, ?> createDefaultModelProjection(
      @NotNull DatumTypeApi type,
      boolean flagged,
      @Nullable GDatum defaultValue,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @NotNull PsiElement locationPsi,
      @NotNull OpPsiProcessingContext context)
      throws PsiProcessingException {

    TextLocation location = EpigraphPsiUtil.getLocation(locationPsi);

    switch (type.kind()) {
      case RECORD:
        return new OpRecordModelProjection(
            (RecordTypeApi) type,
            flagged,
            (GRecordDatum) defaultValue,
            params,
            annotations,
            null,
            Collections.emptyMap(),
            null,
            location
        );
      case MAP:
        MapTypeApi mapType = (MapTypeApi) type;

        final OpKeyProjection keyProjection =
            new OpKeyProjection(
                OpKeyPresence.OPTIONAL,
                location,
                OpParams.EMPTY,
                Annotations.EMPTY,
                null,
                location
            );

        @NotNull DataTypeApi valueType = mapType.valueType();
        @Nullable TagApi defaultValuesTag = valueType.retroTag();

        if (defaultValuesTag == null)
          throw new PsiProcessingException(String.format(
              "Can't create default projection for map type '%s, as it's value type '%s' doesn't have a retro tag",
              type.name(),
              valueType.name()
          ), locationPsi, context);

        final OpEntityProjection valueEntityProjection = createDefaultEntityProjection(
            valueType.type(),
            defaultValuesTag,
            locationPsi,
            context
        );

        return new OpMapModelProjection(
            mapType,
            flagged,
            (GMapDatum) defaultValue,
            params,
            annotations,
            null,
            keyProjection,
            valueEntityProjection,
            null,
            location
        );
      case LIST:
        ListTypeApi listType = (ListTypeApi) type;
        @NotNull DataTypeApi elementType = listType.elementType();
        @Nullable TagApi defaultElementsTag = elementType.retroTag();

        if (defaultElementsTag == null)
          throw new PsiProcessingException(String.format(
              "Can't create default projection for list type '%s, as it's element type '%s' doesn't have a default tag",
              type.name(),
              elementType.name()
          ), locationPsi, context);

        final OpEntityProjection itemEntityProjection = createDefaultEntityProjection(
            elementType.type(),
            defaultElementsTag,
            locationPsi,
            context
        );

        return new OpListModelProjection(
            listType,
            flagged,
            (GListDatum) defaultValue,
            params,
            annotations,
            null,
            itemEntityProjection,
            null,
            location
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
        return new OpPrimitiveModelProjection(
            (PrimitiveTypeApi) type,
            flagged,
            (GPrimitiveDatum) defaultValue,
            params,
            annotations,
            null,
            null,
            location
        );
      default:
        throw new PsiProcessingException("Unknown type kind: " + type.kind(), locationPsi, context);
    }
  }

  static @NotNull OpRecordModelProjection parseRecordModelProjection(
      @NotNull RecordTypeApi type,
      boolean flagged,
      @Nullable GRecordDatum defaultValue,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @Nullable OpModelProjection<?, ?, ?, ?> metaProjection,
      @Nullable List<OpRecordModelProjection> tails,
      @NotNull SchemaOpRecordModelProjection psi,
      @NotNull TypesResolver typesResolver,
      @NotNull OpPsiProcessingContext context) {

    LinkedHashMap<String, OpFieldProjectionEntry> fieldProjections = new LinkedHashMap<>();
    @NotNull List<SchemaOpFieldProjectionEntry> fieldProjectionEntriesPsi =
        psi.getOpFieldProjectionEntryList();

    for (SchemaOpFieldProjectionEntry fieldProjectionEntryPsi : fieldProjectionEntriesPsi) {
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

        final @Nullable SchemaOpFieldProjection fieldProjectionPsi =
            fieldProjectionEntryPsi.getOpFieldProjection();

        if (fieldProjectionPsi == null)
          context.addError("Incomplete definition for field '" + fieldName + "'", fieldProjectionEntryPsi);
        else {
          final OpFieldProjection opFieldProjection = parseFieldProjection(
              field.dataType(),
              fieldProjectionEntryPsi.getPlus() != null,
              fieldProjectionPsi,
              typesResolver,
              context
          );

          fieldProjections.put(
              fieldName,
              new OpFieldProjectionEntry(
                  field,
                  opFieldProjection,
                  EpigraphPsiUtil.getLocation(fieldProjectionPsi)
              )
          );
        }
      } catch (PsiProcessingException e) {
        context.addException(e);
      }
    }

    return new OpRecordModelProjection(
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

  public static @NotNull OpFieldProjection parseFieldProjection(
      @NotNull DataTypeApi fieldType,
      boolean flagged,
      @NotNull SchemaOpFieldProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull OpPsiProcessingContext context) throws PsiProcessingException {

//    List<OpParam> fieldParamsList = null;
//    @Nullable Map<String, Annotation> fieldAnnotationsMap = null;
//    for (SchemaOpFieldProjectionBodyPart fieldBodyPart : psi.getOpFieldProjectionBodyPartList()) {
//      @Nullable SchemaOpParam fieldParamPsi = fieldBodyPart.getOpParam();
//      if (fieldParamPsi != null) {
//        if (fieldParamsList == null) fieldParamsList = new ArrayList<>(3);
//        fieldParamsList.add(parseParameter(fieldParamPsi, resolver, context));
//      }
//
//      fieldAnnotationsMap = parseAnnotation(fieldAnnotationsMap, fieldBodyPart.getAnnotation(), context);
//    }

    final OpProjection<?, ?> fieldProjection =
        parseProjection(fieldType, flagged, psi.getOpEntityProjection(), resolver, context);

    ProjectionsParsingUtil.verifyData(fieldType, fieldProjection, psi, context);

    return new OpFieldProjection(
//        OpParams.fromCollection(fieldParamsList),
//        Annotations.fromMap(fieldAnnotationsMap),
        fieldProjection,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  static @NotNull OpMapModelProjection parseMapModelProjection(
      @NotNull MapTypeApi type,
      boolean flagged,
      @Nullable GMapDatum defaultValue,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @Nullable OpModelProjection<?, ?, ?, ?> metaProjection,
      @Nullable List<OpMapModelProjection> tails,
      @NotNull SchemaOpMapModelProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull OpPsiProcessingContext context)
      throws PsiProcessingException {

    @NotNull OpKeyProjection keyProjection =
        parseKeyProjection(type.keyType(), psi.getOpKeySpec(), resolver, context);

    @Nullable SchemaOpEntityProjection valueProjectionPsi = psi.getOpEntityProjection();
    @NotNull OpProjection<?, ?> valueProjection =
        valueProjectionPsi == null
        ? createDefaultProjection(
            type.valueType(),
            psi,
            context
        )
        : parseProjection(type.valueType(), psi.getPlus() != null, valueProjectionPsi, resolver, context);

    return new OpMapModelProjection(
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

  private static @NotNull OpKeyProjection parseKeyProjection(
      @NotNull DatumTypeApi keyType,
      @NotNull SchemaOpKeySpec keySpecPsi,
      @NotNull TypesResolver resolver,
      @NotNull OpPsiProcessingContext context) throws PsiProcessingException {

    final OpKeyPresence presence;
    final TextLocation presenceLocation;

    if (keySpecPsi.getForbidden() != null) {
      presence = OpKeyPresence.FORBIDDEN;
      presenceLocation = EpigraphPsiUtil.getLocation(keySpecPsi.getForbidden());
    } else if (keySpecPsi.getRequired() != null) {
      presence = OpKeyPresence.REQUIRED;
      presenceLocation = EpigraphPsiUtil.getLocation(keySpecPsi.getRequired());
    } else {
      presence = OpKeyPresence.OPTIONAL;
      presenceLocation = EpigraphPsiUtil.getLocation(keySpecPsi);
    }

    final @NotNull List<SchemaOpKeySpecPart> keyPartsPsi =
        keySpecPsi.getOpKeySpecPartList();

    final @NotNull OpParams keyParams =
        parseParams(
            keyPartsPsi.stream().map(SchemaOpKeySpecPart::getOpParam),
            resolver,
            context
        );
    final @NotNull Annotations keyAnnotations =
        SchemaPsiParserUtil.parseAnnotations(
            keyPartsPsi.stream().map(SchemaOpKeySpecPart::getAnnotation),
            context,
            resolver
        );

    OpModelProjection<?, ?, ?, ?> keyProjection = SchemaProjectionPsiParserUtil.parseKeyProjection(
        keyType,
        keyPartsPsi.stream().map(SchemaOpKeySpecPart::getOpKeyProjection),
        resolver,
        context // should be separate one for keys?
    );

    return new OpKeyProjection(
        presence,
        presenceLocation,
        keyParams,
        keyAnnotations,
        keyProjection,
        EpigraphPsiUtil.getLocation(keySpecPsi)
    );
  }

  static @NotNull OpListModelProjection parseListModelProjection(
      @NotNull ListTypeApi type,
      boolean flagged,
      @Nullable GListDatum defaultValue,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @Nullable OpModelProjection<?, ?, ?, ?> metaProjection,
      @Nullable List<OpListModelProjection> tails,
      @NotNull SchemaOpListModelProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull OpPsiProcessingContext context)
      throws PsiProcessingException {

    OpProjection<?, ?> itemsProjection;
    @Nullable SchemaOpEntityProjection opOutputEntityProjectionPsi = psi.getOpEntityProjection();
    if (opOutputEntityProjectionPsi == null)
      itemsProjection = createDefaultProjection(type, psi, context);
    else
      itemsProjection =
          parseProjection(
              type.elementType(),
              psi.getPlus() != null,
              opOutputEntityProjectionPsi,
              resolver,
              context
          );

    return new OpListModelProjection(
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

  private static @NotNull OpPrimitiveModelProjection parsePrimitiveModelProjection(
      @NotNull PrimitiveTypeApi type,
      boolean flagged,
      @Nullable GPrimitiveDatum defaultValue,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @Nullable OpModelProjection<?, ?, ?, ?> metaProjection,
      @Nullable List<OpPrimitiveModelProjection> tails,
      @NotNull PsiElement locationPsi) {

    return new OpPrimitiveModelProjection(
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
      @NotNull OpPsiProcessingContext context) throws PsiProcessingException {

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
