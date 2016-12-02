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

package ws.epigraph.projections.op.input;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.data.*;
import ws.epigraph.gdata.*;
import ws.epigraph.idl.TypeRefs;
import ws.epigraph.idl.gdata.IdlGDataPsiParser;
import ws.epigraph.idl.parser.psi.*;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.ProjectionUtils;
import ws.epigraph.projections.StepsAndProjection;
import ws.epigraph.projections.op.OpParams;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingError;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.TypeRef;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.types.*;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import static ws.epigraph.projections.IdlProjectionPsiParserUtil.getTag;
import static ws.epigraph.projections.IdlProjectionPsiParserUtil.parseAnnotations;
import static ws.epigraph.projections.IdlProjectionPsiParserUtil.parseParams;
import static ws.epigraph.projections.ProjectionsParsingUtil.getType;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpInputProjectionsPsiParser {

  public static StepsAndProjection<OpInputVarProjection> parseVarProjection(
      @NotNull DataType dataType,
      @NotNull IdlOpInputVarProjection psi,
      @NotNull TypesResolver typesResolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    final Type type = dataType.type;
    final LinkedHashMap<String, OpInputTagProjectionEntry> tagProjections;

    boolean isDatumType = type.kind() != TypeKind.UNION;

    @Nullable IdlOpInputSingleTagProjection singleTagProjectionPsi = psi.getOpInputSingleTagProjection();
    if (singleTagProjectionPsi != null) {
      tagProjections = new LinkedHashMap<>();
      final OpInputModelProjection<?, ?, ?> parsedModelProjection;
      final Type.Tag tag = getTag(
          type,
          singleTagProjectionPsi.getTagName(),
          dataType.defaultTag,
          singleTagProjectionPsi,
          errors
      );

      @Nullable IdlOpInputModelProjection modelProjection = singleTagProjectionPsi.getOpInputModelProjection();
      assert modelProjection != null; // can never be null

      @NotNull List<IdlOpInputModelProperty> modelPropertiesPsi = singleTagProjectionPsi.getOpInputModelPropertyList();
      parsedModelProjection = parseModelProjection(
          tag.type,
          singleTagProjectionPsi.getPlus() != null || isDatumType, // 'self 'tags on datum projections are required
          getModelDefaultValue(modelPropertiesPsi, errors),
          parseModelParams(modelPropertiesPsi, typesResolver, errors),
          parseModelAnnotations(modelPropertiesPsi, errors),
          parseModelMetaProjection(tag.type, modelPropertiesPsi, typesResolver, errors),
          modelProjection, typesResolver, errors
      ).projection();

      tagProjections.put(
          tag.name(),
          new OpInputTagProjectionEntry(
              tag,
              parsedModelProjection,
              EpigraphPsiUtil.getLocation(singleTagProjectionPsi)
          )
      );

    } else {
      @Nullable IdlOpInputMultiTagProjection multiTagProjection = psi.getOpInputMultiTagProjection();
      assert multiTagProjection != null;
      tagProjections = parseMultiTagProjection(dataType, multiTagProjection, typesResolver, errors);
    }

    final List<OpInputVarProjection> tails =
        parseTails(dataType, psi.getOpInputVarPolymorphicTail(), typesResolver, errors);

    try {
      return new StepsAndProjection<>(
          0,
          new OpInputVarProjection(
              type,
              tagProjections,
              singleTagProjectionPsi == null,
              tails,
              EpigraphPsiUtil.getLocation(psi)
          )
      );
    } catch (Exception e) {
      throw new PsiProcessingException(e, psi, errors);
    }
  }

  @NotNull
  private static LinkedHashMap<String, OpInputTagProjectionEntry> parseMultiTagProjection(
      @NotNull DataType dataType,
      @NotNull IdlOpInputMultiTagProjection multiTagProjection,
      @NotNull TypesResolver typesResolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    final LinkedHashMap<String, OpInputTagProjectionEntry> tagProjections = new LinkedHashMap<>();

    // parse list of tags
    @NotNull List<IdlOpInputMultiTagProjectionItem> tagProjectionPsiList =
        multiTagProjection.getOpInputMultiTagProjectionItemList();

    for (IdlOpInputMultiTagProjectionItem tagProjectionPsi : tagProjectionPsiList) {
      final Type.Tag tag =
          getTag(dataType.type, tagProjectionPsi.getTagName(), dataType.defaultTag, tagProjectionPsi, errors);

      final OpInputModelProjection<?, ?, ?> parsedModelProjection;

      @NotNull DatumType tagType = tag.type;
      @Nullable IdlOpInputModelProjection modelProjection = tagProjectionPsi.getOpInputModelProjection();
      assert modelProjection != null; // todo when it can be null?
      @NotNull List<IdlOpInputModelProperty> modelPropertiesPsi = tagProjectionPsi.getOpInputModelPropertyList();

      parsedModelProjection = parseModelProjection(
          tagType,
          tagProjectionPsi.getPlus() != null,
          getModelDefaultValue(modelPropertiesPsi, errors),
          parseModelParams(modelPropertiesPsi, typesResolver, errors),
          parseModelAnnotations(modelPropertiesPsi, errors),
          parseModelMetaProjection(tagType, modelPropertiesPsi, typesResolver, errors),
          modelProjection, typesResolver, errors
      ).projection();

      tagProjections.put(
          tag.name(),
          new OpInputTagProjectionEntry(
              tag,
              parsedModelProjection,
              EpigraphPsiUtil.getLocation(tagProjectionPsi)
          )
      );
    }

    return tagProjections;
  }

  @Nullable
  private static List<OpInputVarProjection> parseTails(
      @NotNull DataType dataType,
      @Nullable IdlOpInputVarPolymorphicTail tailPsi,
      @NotNull TypesResolver typesResolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    final List<OpInputVarProjection> tails;

    if (tailPsi != null) {
      tails = new ArrayList<>();

      @Nullable IdlOpInputVarSingleTail singleTail = tailPsi.getOpInputVarSingleTail();
      if (singleTail != null) {
        @NotNull IdlTypeRef typeRefPsi = singleTail.getTypeRef();
        @NotNull IdlOpInputVarProjection psiTailProjection = singleTail.getOpInputVarProjection();
        @NotNull OpInputVarProjection tailProjection =
            buildTailProjection(dataType, typeRefPsi, psiTailProjection, typesResolver, errors);
        tails.add(tailProjection);
      } else {
        @Nullable IdlOpInputVarMultiTail multiTail = tailPsi.getOpInputVarMultiTail();
        assert multiTail != null;
        for (IdlOpInputVarMultiTailItem tailItem : multiTail.getOpInputVarMultiTailItemList()) {
          @NotNull IdlTypeRef tailTypeRef = tailItem.getTypeRef();
          @NotNull IdlOpInputVarProjection psiTailProjection = tailItem.getOpInputVarProjection();
          @NotNull OpInputVarProjection tailProjection =
              buildTailProjection(
                  dataType,
                  tailTypeRef,
                  psiTailProjection,
                  typesResolver,
                  errors
              );
          tails.add(tailProjection);
        }
      }

    } else tails = null;

    return tails;
  }

  @Nullable
  private static GDatum getModelDefaultValue(
      @NotNull List<IdlOpInputModelProperty> modelProperties,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    GDatum result = null;
    for (IdlOpInputModelProperty property : modelProperties) {
      @Nullable IdlOpInputDefaultValue defaultValuePsi = property.getOpInputDefaultValue();
      if (defaultValuePsi != null) {
        if (result != null) {
          errors.add(new PsiProcessingError("Default value should only be specified once", defaultValuePsi));
        } else {
          @Nullable IdlDatum varValuePsi = defaultValuePsi.getDatum();
          if (varValuePsi != null)
            result = IdlGDataPsiParser.parseDatum(varValuePsi, errors);
        }
      }
    }

    return result;
  }

  @NotNull
  private static OpParams parseModelParams(
      @NotNull List<IdlOpInputModelProperty> modelProperties,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {


    return parseParams(
        modelProperties.stream().map(IdlOpInputModelProperty::getOpParam),
        resolver,
        errors
    );
  }

  @NotNull
  private static Annotations parseModelAnnotations(
      @NotNull List<IdlOpInputModelProperty> modelProperties,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    return parseAnnotations(
        modelProperties.stream().map(IdlOpInputModelProperty::getAnnotation),
        errors
    );
  }

  @Nullable
  private static OpInputModelProjection<?, ?, ?> parseModelMetaProjection(
      @NotNull DatumType type,
      @NotNull List<IdlOpInputModelProperty> modelProperties,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors
  ) throws PsiProcessingException {

    @Nullable IdlOpInputModelMeta modelMetaPsi = null;

    for (IdlOpInputModelProperty modelProperty : modelProperties) {
      if (modelMetaPsi != null) {
        errors.add(new PsiProcessingError("Metadata projection should only be specified once", modelProperty));
      } else {
        modelMetaPsi = modelProperty.getOpInputModelMeta();
      }
    }

    if (modelMetaPsi != null) {
      @Nullable DatumType metaType = null; // TODO need a way to extract it from 'type'
      if (metaType == null) {
        errors.add(new PsiProcessingError(
            String.format("Type '%s' doesn't have a metadata, metadata projection can't be specified", type.name()),
            modelMetaPsi
        ));
      } else {
        @NotNull IdlOpInputModelProjection metaProjectionPsi = modelMetaPsi.getOpInputModelProjection();
        return parseModelProjection(
            metaType,
            modelMetaPsi.getPlus() != null,
            null, // TODO do we want to specify defaults for meta?
            OpParams.EMPTY,
            Annotations.EMPTY,
            null, // TODO what if meta-type has it's own meta-type? meta-meta-type projection should go here
            metaProjectionPsi,
            resolver,
            errors
        ).projection();
      }
    }

    return null;
  }

  @NotNull
  private static OpInputVarProjection buildTailProjection(
      @NotNull DataType dataType,
      @NotNull IdlTypeRef tailTypeRefPsi,
      IdlOpInputVarProjection psiTailProjection,
      @NotNull TypesResolver typesResolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    @NotNull TypeRef tailTypeRef = TypeRefs.fromPsi(tailTypeRefPsi, errors);
    @NotNull Type tailType = getType(tailTypeRef, typesResolver, tailTypeRefPsi, errors);
    return parseVarProjection(
        new DataType(tailType, dataType.defaultTag),
        psiTailProjection,
        typesResolver,
        errors
    ).projection();
  }

  @NotNull
  private static OpInputVarProjection createDefaultVarProjection(
      @NotNull Type type,
      @NotNull Type.Tag tag,
      boolean required,
      @NotNull PsiElement locationPsi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {
    return new OpInputVarProjection(
        type,
        ProjectionUtils.singletonLinkedHashMap(
            tag.name(),
            new OpInputTagProjectionEntry(
                tag,
                createDefaultModelProjection(
                    tag.type,
                    required,
                    null,
                    OpParams.EMPTY,
                    Annotations.EMPTY,
                    locationPsi,
                    resolver,
                    errors
                ),
                EpigraphPsiUtil.getLocation(locationPsi)
            )
        ),
        false,
        null,
        EpigraphPsiUtil.getLocation(locationPsi)
    );
  }

  @NotNull
  private static OpInputVarProjection createDefaultVarProjection(
      @NotNull DatumType type,
      boolean required,
      @NotNull PsiElement locationPsi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors)
      throws PsiProcessingException {
    return createDefaultVarProjection(type, type.self, required, locationPsi, resolver, errors);
  }

  @NotNull
  private static OpInputVarProjection createDefaultVarProjection(
      @NotNull DataType type,
      boolean required,
      @NotNull PsiElement locationPsi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors)
      throws PsiProcessingException {

    @Nullable Type.Tag defaultTag = type.defaultTag;
    if (defaultTag == null)
      throw new PsiProcessingException(
          String.format("Can't build default projection for '%s', default tag not specified", type.name),
          locationPsi,
          errors
      );

    return createDefaultVarProjection(type.type, defaultTag, required, locationPsi, resolver, errors);
  }

  @NotNull
  public static StepsAndProjection<? extends OpInputModelProjection<?, ?, ?>> parseModelProjection(
      @NotNull DatumType type,
      boolean required,
      @Nullable GDatum defaultValue,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @Nullable OpInputModelProjection<?, ?, ?> metaProjection,
      @NotNull IdlOpInputModelProjection psi,
      @NotNull TypesResolver typesResolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    switch (type.kind()) {
      case RECORD:
        @Nullable IdlOpInputRecordModelProjection recordModelProjectionPsi =
            psi.getOpInputRecordModelProjection();

        if (recordModelProjectionPsi == null)
          return new StepsAndProjection<>(
              0,
              createDefaultModelProjection(
                  type,
                  required,
                  defaultValue,
                  params,
                  annotations,
                  psi,
                  typesResolver,
                  errors
              )
          );

        ensureModelKind(psi, TypeKind.RECORD, errors);
        GRecordDatum defaultRecordData = coerceDefault(defaultValue, GRecordDatum.class, psi, errors);

        return parseRecordModelProjection(
            (RecordType) type,
            required,
            defaultRecordData,
            params,
            annotations,
            (OpInputRecordModelProjection) metaProjection,
            recordModelProjectionPsi,
            typesResolver,
            errors
        );

      case MAP:
        @Nullable IdlOpInputMapModelProjection mapModelProjectionPsi = psi.getOpInputMapModelProjection();

        if (mapModelProjectionPsi == null)
          return new StepsAndProjection<>(
              0,
              createDefaultModelProjection(
                  type,
                  required,
                  defaultValue,
                  params,
                  annotations,
                  psi,
                  typesResolver,
                  errors
              )
          );

        ensureModelKind(psi, TypeKind.MAP, errors);
        GMapDatum defaultMapData = coerceDefault(defaultValue, GMapDatum.class, psi, errors);

        return parseMapModelProjection(
            (MapType) type,
            required,
            defaultMapData,
            params,
            annotations,
            (OpInputMapModelProjection) metaProjection,
            mapModelProjectionPsi,
            typesResolver,
            errors
        );

      case LIST:
        @Nullable IdlOpInputListModelProjection listModelProjectionPsi = psi.getOpInputListModelProjection();

        if (listModelProjectionPsi == null)
          return new StepsAndProjection<>(
              0,
              createDefaultModelProjection(
                  type,
                  required,
                  defaultValue,
                  params,
                  annotations,
                  psi,
                  typesResolver,
                  errors
              )
          );

        ensureModelKind(psi, TypeKind.LIST, errors);
        GListDatum defaultListData = coerceDefault(defaultValue, GListDatum.class, psi, errors);

        return parseListModelProjection(
            (ListType) type,
            required,
            defaultListData,
            params,
            annotations,
            (OpInputListModelProjection) metaProjection,
            listModelProjectionPsi,
            typesResolver,
            errors
        );

      case ENUM:
        throw new PsiProcessingException("Unsupported type kind: " + type.kind(), psi, errors);

      case PRIMITIVE:
        GPrimitiveDatum defaultPrimitiveData = coerceDefault(defaultValue, GPrimitiveDatum.class, psi, errors);

        return parsePrimitiveModelProjection(
            (PrimitiveType) type,
            required,
            defaultPrimitiveData,
            params,
            annotations,
            (OpInputPrimitiveModelProjection) metaProjection,
            psi,
            typesResolver,
            errors
        );

      case UNION:
        throw new PsiProcessingException("Unsupported type kind: " + type.kind(), psi, errors);

      default:
        throw new PsiProcessingException("Unknown type kind: " + type.kind(), psi, errors);
    }
  }

  private static void ensureModelKind(
      @NotNull IdlOpInputModelProjection psi,
      @NotNull TypeKind expectedKind,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    @Nullable TypeKind actualKind = findProjectionKind(psi);
    if (!expectedKind.equals(actualKind))
      throw new PsiProcessingException(MessageFormat.format(
          "Unexpected projection kind ''{0}'', expected ''{1}''",
          actualKind,
          expectedKind
      ), psi, errors);
  }

  @Nullable
  private static TypeKind findProjectionKind(@NotNull IdlOpInputModelProjection psi) {
    if (psi.getOpInputRecordModelProjection() != null) return TypeKind.RECORD;
    if (psi.getOpInputMapModelProjection() != null) return TypeKind.MAP;
    if (psi.getOpInputListModelProjection() != null) return TypeKind.LIST;
    return null;
  }

  @NotNull
  public static OpInputModelProjection<?, ?, ?> createDefaultModelProjection(
      @NotNull DatumType type,
      boolean required,
      @Nullable GDatum defaultValue,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @NotNull PsiElement locationPsi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    @NotNull TextLocation location = EpigraphPsiUtil.getLocation(locationPsi);

    @Nullable Datum defaultDatum = null;
    if (defaultValue != null)
      try {
        defaultDatum = GDataToData.transformDatum(type, defaultValue, resolver);
      } catch (GDataToData.ProcessingException e) {
        throw new PsiProcessingException(e, locationPsi, errors);
      }

    switch (type.kind()) {
      case RECORD:
        return new OpInputRecordModelProjection(
            (RecordType) type,
            required,
            (RecordDatum) defaultDatum,
            params,
            annotations,
            null,
            Collections.emptyMap(),
            location
        );
      case MAP:
        MapType mapType = (MapType) type;

        @NotNull DataType valueType = mapType.valueType();
        Type.@Nullable Tag defaultValuesTag = valueType.defaultTag;

        if (defaultValuesTag == null)
          throw new PsiProcessingException(String.format(
              "Can't create default projection for map type '%s, as it's value type '%s' doesn't have a default tag",
              type.name(),
              valueType.name
          ), locationPsi, errors);

        final OpInputVarProjection valueVarProjection = createDefaultVarProjection(
            valueType.type,
            defaultValuesTag,
            required,
            locationPsi,
            resolver,
            errors
        );

        return new OpInputMapModelProjection(
            mapType,
            required,
            (MapDatum) defaultDatum,
            params,
            annotations,
            null,
            new OpInputKeyProjection(
                OpParams.EMPTY,
                Annotations.EMPTY,
                location
            ),
            valueVarProjection,
            location
        );
      case LIST:
        ListType listType = (ListType) type;
        @NotNull DataType elementType = listType.elementType();
        Type.@Nullable Tag defaultElementsTag = elementType.defaultTag;

        if (defaultElementsTag == null)
          throw new PsiProcessingException(String.format(
              "Can't create default projection for list type '%s, as it's element type '%s' doesn't have a default tag",
              type.name(),
              elementType.name
          ), locationPsi, errors);

        final OpInputVarProjection itemVarProjection = createDefaultVarProjection(
            elementType.type,
            defaultElementsTag,
            required,
            locationPsi,
            resolver,
            errors
        );

        return new OpInputListModelProjection(
            listType,
            required,
            (ListDatum) defaultDatum,
            params,
            annotations,
            null,
            itemVarProjection,
            location
        );
      case UNION:
        throw new PsiProcessingException(
            "Was expecting to get datum model kind, got: " + type.kind(),
            locationPsi,
            errors
        );
      case ENUM:
        // todo
        throw new PsiProcessingException("Unsupported type kind: " + type.kind(), locationPsi, errors);
      case PRIMITIVE:
        return new OpInputPrimitiveModelProjection(
            (PrimitiveType) type,
            required,
            (PrimitiveDatum<?>) defaultDatum,
            params,
            annotations,
            null,
            location
        );
      default:
        throw new PsiProcessingException("Unknown type kind: " + type.kind(), locationPsi, errors);
    }
  }

  @NotNull
  public static StepsAndProjection<OpInputRecordModelProjection> parseRecordModelProjection(
      @NotNull RecordType type,
      boolean required,
      @Nullable GRecordDatum defaultValue,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @Nullable OpInputRecordModelProjection metaProjection,
      @NotNull IdlOpInputRecordModelProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    RecordDatum defaultDatum = null;
    if (defaultValue != null) {
      try {
        defaultDatum = GDataToData.transform(type, defaultValue, resolver);
      } catch (GDataToData.ProcessingException e) {
        throw new PsiProcessingException(e, psi, errors);
      }
    }

    LinkedHashMap<String, OpInputFieldProjectionEntry> fieldProjections = new LinkedHashMap<>();
    @NotNull List<IdlOpInputFieldProjectionEntry> psiFieldProjections = psi.getOpInputFieldProjectionEntryList();

    for (IdlOpInputFieldProjectionEntry fieldProjectionPsi : psiFieldProjections) {
      try {
        final String fieldName = fieldProjectionPsi.getQid().getCanonicalName();
        RecordType.Field field = type.fieldsMap().get(fieldName);
        if (field == null)
          throw new PsiProcessingException(
              String.format("Can't build field projection for '%s', field '%s' not found", type.name(), fieldName),
              fieldProjectionPsi,
              errors
          );

        @NotNull final DataType fieldType = field.dataType();
        final boolean fieldRequired = fieldProjectionPsi.getPlus() != null;

        final OpInputFieldProjection fieldProjection =
            parseFieldProjection(
                fieldType,
                fieldRequired,
                fieldProjectionPsi.getOpInputFieldProjection(),
                resolver,
                errors
            );

        fieldProjections.put(
            fieldName,
            new OpInputFieldProjectionEntry(
                field,
                fieldProjection
                ,
                EpigraphPsiUtil.getLocation(fieldProjectionPsi)
            )
        );
      } catch (PsiProcessingException e) {
        errors.add(e.toError());
      }
    }

    return new StepsAndProjection<>(
        0,
        new OpInputRecordModelProjection(
            type,
            required,
            defaultDatum,
            params,
            annotations,
            metaProjection,
            fieldProjections,
            EpigraphPsiUtil.getLocation(psi)
        )
    );
  }

  @NotNull
  public static OpInputFieldProjection parseFieldProjection(
      final DataType fieldType,
      final boolean required,
      final @NotNull IdlOpInputFieldProjection psi,
      final @NotNull TypesResolver resolver,
      final @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    @NotNull OpParams fieldParams = parseParams(
        psi.getOpInputFieldProjectionBodyPartList()
            .stream()
            .map(IdlOpInputFieldProjectionBodyPart::getOpParam),
        resolver,
        errors
    );

    @NotNull Annotations fieldAnnotations = parseAnnotations(
        psi.getOpInputFieldProjectionBodyPartList()
            .stream()
            .map(IdlOpInputFieldProjectionBodyPart::getAnnotation),
        errors
    );

    @NotNull IdlOpInputVarProjection psiVarProjection = psi.getOpInputVarProjection();
    OpInputVarProjection varProjection =
        parseVarProjection(fieldType, psiVarProjection, resolver, errors).projection();

    return new OpInputFieldProjection(
        fieldParams,
        fieldAnnotations,
        varProjection,
        required,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  @NotNull
  public static StepsAndProjection<OpInputMapModelProjection> parseMapModelProjection(
      @NotNull MapType type,
      boolean required,
      @Nullable GMapDatum defaultValue,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @Nullable OpInputMapModelProjection metaProjection,
      @NotNull IdlOpInputMapModelProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    MapDatum defaultDatum = null;
    if (defaultValue != null) {
      try {
        defaultDatum = GDataToData.transform(type, defaultValue, resolver);
      } catch (GDataToData.ProcessingException e) {
        throw new PsiProcessingException(e, psi, errors);
      }
    }

    final @NotNull List<IdlOpInputKeyProjectionPart> keyPartsPsi =
        psi.getOpInputKeyProjection().getOpInputKeyProjectionPartList();

    @NotNull final OpParams keyParams =
        parseParams(keyPartsPsi.stream().map(IdlOpInputKeyProjectionPart::getOpParam), resolver, errors);
    @NotNull final Annotations keyAnnotations =
        parseAnnotations(keyPartsPsi.stream().map(IdlOpInputKeyProjectionPart::getAnnotation), errors);

    @Nullable IdlOpInputVarProjection valueProjectionPsi = psi.getOpInputVarProjection();
    @NotNull OpInputVarProjection valueProjection =
        valueProjectionPsi == null ?
        createDefaultVarProjection(type.valueType(), false, psi, resolver, errors) :
        parseVarProjection(type.valueType(), valueProjectionPsi, resolver, errors).projection();

    return new StepsAndProjection<>(
        0,
        new OpInputMapModelProjection(
            type,
            required,
            defaultDatum,
            params,
            annotations,
            metaProjection,
            new OpInputKeyProjection(
                keyParams,
                keyAnnotations,
                EpigraphPsiUtil.getLocation(psi.getOpInputKeyProjection())
            ),
            valueProjection,
            EpigraphPsiUtil.getLocation(psi)
        )
    );
  }

  @NotNull
  public static StepsAndProjection<OpInputListModelProjection> parseListModelProjection(
      @NotNull ListType type,
      boolean required,
      @Nullable GListDatum defaultValue,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @Nullable OpInputListModelProjection metaProjection,
      @NotNull IdlOpInputListModelProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    ListDatum defaultDatum = null;
    if (defaultValue != null) {
      try {
        defaultDatum = GDataToData.transform(type, defaultValue, resolver);
      } catch (GDataToData.ProcessingException e) {
        throw new PsiProcessingException(e, psi, errors);
      }
    }

    OpInputVarProjection itemsProjection;
    @Nullable IdlOpInputVarProjection opInputVarProjectionPsi = psi.getOpInputVarProjection();
    if (opInputVarProjectionPsi == null)
      itemsProjection = createDefaultVarProjection(type, true, psi, resolver, errors);
    else
      itemsProjection = parseVarProjection(type.elementType(), opInputVarProjectionPsi, resolver, errors).projection();

    return new StepsAndProjection<>(
        0,
        new OpInputListModelProjection(
            type,
            required,
            defaultDatum,
            params,
            annotations,
            metaProjection,
            itemsProjection,
            EpigraphPsiUtil.getLocation(psi)
        )
    );
  }

  @NotNull
  public static StepsAndProjection<OpInputPrimitiveModelProjection> parsePrimitiveModelProjection(
      @NotNull PrimitiveType type,
      boolean required,
      @Nullable GPrimitiveDatum defaultValue,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @Nullable OpInputPrimitiveModelProjection metaProjection,
      @NotNull PsiElement locationPsi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    PrimitiveDatum<?> defaultDatum = null;
    if (defaultValue != null) {
      try {
        defaultDatum = GDataToData.transform(type, defaultValue, resolver);
      } catch (GDataToData.ProcessingException e) {
        throw new PsiProcessingException(e, locationPsi, errors);
      }
    }

    return new StepsAndProjection<>(
        0,
        new OpInputPrimitiveModelProjection(
            type,
            required,
            defaultDatum,
            params,
            annotations,
            metaProjection,
            EpigraphPsiUtil.getLocation(locationPsi)
        )
    );
  }

  @SuppressWarnings("unchecked")
  @Nullable
  private static <D extends GDatum> D coerceDefault(
      @Nullable GDatum defaultValue,
      Class<D> cls,
      @NotNull PsiElement location,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    if (defaultValue == null) return null;
    if (defaultValue instanceof GNullDatum) return null;
    if (defaultValue.getClass().equals(cls))
      return (D) defaultValue;
    throw new PsiProcessingException(
        String.format("Invalid default value '%s', expected to get '%s'", defaultValue, cls.getName()),
        location,
        errors
    );
  }
}
