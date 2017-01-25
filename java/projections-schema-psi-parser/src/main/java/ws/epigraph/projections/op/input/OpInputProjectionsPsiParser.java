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
import ws.epigraph.gdata.*;
import ws.epigraph.schema.TypeRefs;
import ws.epigraph.schema.gdata.SchemaGDataPsiParser;
import ws.epigraph.schema.parser.psi.*;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.ProjectionUtils;
import ws.epigraph.projections.op.OpKeyPresence;
import ws.epigraph.projections.op.OpParams;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingError;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.TypeRef;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.types.*;
import ws.epigraph.types.TypeKind;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import static ws.epigraph.projections.ProjectionsParsingUtil.getUnionType;
import static ws.epigraph.projections.SchemaProjectionPsiParserUtil.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class OpInputProjectionsPsiParser {

  private OpInputProjectionsPsiParser() {}

  public static OpInputVarProjection parseVarProjection(
      @NotNull DataTypeApi dataType,
      @NotNull SchemaOpInputVarProjection psi,
      @NotNull TypesResolver typesResolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    final TypeApi type = dataType.type();
    final LinkedHashMap<String, OpInputTagProjectionEntry> tagProjections;

    boolean isDatumType = type.kind() != TypeKind.UNION;

    @Nullable SchemaOpInputSingleTagProjection singleTagProjectionPsi = psi.getOpInputSingleTagProjection();
    if (singleTagProjectionPsi == null) {
      @Nullable SchemaOpInputMultiTagProjection multiTagProjection = psi.getOpInputMultiTagProjection();
      assert multiTagProjection != null;
      tagProjections = parseMultiTagProjection(dataType, multiTagProjection, typesResolver, errors);
    } else {
      tagProjections = new LinkedHashMap<>();
      TagApi tag = findTag(
          type,
          singleTagProjectionPsi.getTagName(),
          dataType.defaultTag(),
          singleTagProjectionPsi,
          errors
      );
      if (tag != null || !singleTagProjectionPsi.getText().isEmpty()) {
        final OpInputModelProjection<?, ?, ?, ?> parsedModelProjection;
        if (tag == null) // will throw proper error
          tag = getTag(
              type,
              singleTagProjectionPsi.getTagName(),
              dataType.defaultTag(),
              singleTagProjectionPsi,
              errors
          );

        @Nullable SchemaOpInputModelProjection modelProjection = singleTagProjectionPsi.getOpInputModelProjection();
        assert modelProjection != null; // can never be null

        @NotNull List<SchemaOpInputModelProperty> modelPropertiesPsi =
            singleTagProjectionPsi.getOpInputModelPropertyList();
        parsedModelProjection = parseModelProjection(
            tag.type(),
            singleTagProjectionPsi.getPlus() != null || isDatumType, // 'self 'tags on datum projections are required
            getModelDefaultValue(modelPropertiesPsi, errors),
            parseModelParams(modelPropertiesPsi, typesResolver, errors),
            parseModelAnnotations(modelPropertiesPsi, errors),
            parseModelMetaProjection(tag.type(), modelPropertiesPsi, typesResolver, errors),
            modelProjection, typesResolver, errors
        );

        tagProjections.put(
            tag.name(),
            new OpInputTagProjectionEntry(
                tag,
                parsedModelProjection,
                EpigraphPsiUtil.getLocation(singleTagProjectionPsi)
            )
        );
      }
    }

    final List<OpInputVarProjection> tails =
        parseTails(dataType, psi.getOpInputVarPolymorphicTail(), typesResolver, errors);

    try {
      return new OpInputVarProjection(
          type,
          tagProjections,
          singleTagProjectionPsi == null || tagProjections.size() != 1,
          tails,
          EpigraphPsiUtil.getLocation(psi)
      );
    } catch (Exception e) {
      throw new PsiProcessingException(e, psi, errors);
    }
  }

  private static @NotNull LinkedHashMap<String, OpInputTagProjectionEntry> parseMultiTagProjection(
      @NotNull DataTypeApi dataType,
      @NotNull SchemaOpInputMultiTagProjection multiTagProjection,
      @NotNull TypesResolver typesResolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    final LinkedHashMap<String, OpInputTagProjectionEntry> tagProjections = new LinkedHashMap<>();

    // parse list of tags
    @NotNull List<SchemaOpInputMultiTagProjectionItem> tagProjectionPsiList =
        multiTagProjection.getOpInputMultiTagProjectionItemList();

    for (SchemaOpInputMultiTagProjectionItem tagProjectionPsi : tagProjectionPsiList) {
      final TagApi tag =
          getTag(dataType.type(), tagProjectionPsi.getTagName(), dataType.defaultTag(), tagProjectionPsi, errors);

      final OpInputModelProjection<?, ?, ?, ?> parsedModelProjection;

      @NotNull DatumTypeApi tagType = tag.type();
      @Nullable SchemaOpInputModelProjection modelProjection = tagProjectionPsi.getOpInputModelProjection();
      assert modelProjection != null; // todo when it can be null?
      @NotNull List<SchemaOpInputModelProperty> modelPropertiesPsi = tagProjectionPsi.getOpInputModelPropertyList();

      parsedModelProjection = parseModelProjection(
          tagType,
          tagProjectionPsi.getPlus() != null,
          getModelDefaultValue(modelPropertiesPsi, errors),
          parseModelParams(modelPropertiesPsi, typesResolver, errors),
          parseModelAnnotations(modelPropertiesPsi, errors),
          parseModelMetaProjection(tagType, modelPropertiesPsi, typesResolver, errors),
          modelProjection, typesResolver, errors
      );

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

  private static @Nullable List<OpInputVarProjection> parseTails(
      @NotNull DataTypeApi dataType,
      @Nullable SchemaOpInputVarPolymorphicTail tailPsi,
      @NotNull TypesResolver typesResolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    final List<OpInputVarProjection> tails;

    if (tailPsi == null) tails = null;
    else {
      tails = new ArrayList<>();

      @Nullable SchemaOpInputVarSingleTail singleTail = tailPsi.getOpInputVarSingleTail();
      if (singleTail == null) {
        @Nullable SchemaOpInputVarMultiTail multiTail = tailPsi.getOpInputVarMultiTail();
        assert multiTail != null;
        for (SchemaOpInputVarMultiTailItem tailItem : multiTail.getOpInputVarMultiTailItemList()) {
          @NotNull SchemaTypeRef tailTypeRef = tailItem.getTypeRef();
          @NotNull SchemaOpInputVarProjection psiTailProjection = tailItem.getOpInputVarProjection();
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
      } else {
        @NotNull SchemaTypeRef typeRefPsi = singleTail.getTypeRef();
        @NotNull SchemaOpInputVarProjection psiTailProjection = singleTail.getOpInputVarProjection();
        @NotNull OpInputVarProjection tailProjection =
            buildTailProjection(dataType, typeRefPsi, psiTailProjection, typesResolver, errors);
        tails.add(tailProjection);
      }

    }

    return tails;
  }

  private static @Nullable GDatum getModelDefaultValue(
      @NotNull List<SchemaOpInputModelProperty> modelProperties,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    GDatum result = null;
    for (SchemaOpInputModelProperty property : modelProperties) {
      @Nullable SchemaOpInputDefaultValue defaultValuePsi = property.getOpInputDefaultValue();
      if (defaultValuePsi != null) {
        if (result == null) {
          @Nullable SchemaDatum varValuePsi = defaultValuePsi.getDatum();
          if (varValuePsi != null)
            result = SchemaGDataPsiParser.parseDatum(varValuePsi, errors);
        } else {
          errors.add(new PsiProcessingError("Default value should only be specified once", defaultValuePsi));
        }
      }
    }

    return result;
  }

  private static @NotNull OpParams parseModelParams(
      @NotNull List<SchemaOpInputModelProperty> modelProperties,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {


    return parseParams(
        modelProperties.stream().map(SchemaOpInputModelProperty::getOpParam),
        resolver,
        errors
    );
  }

  private static @NotNull Annotations parseModelAnnotations(
      @NotNull List<SchemaOpInputModelProperty> modelProperties,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    return parseAnnotations(
        modelProperties.stream().map(SchemaOpInputModelProperty::getAnnotation),
        errors
    );
  }

  private static @Nullable OpInputModelProjection<?, ?, ?, ?> parseModelMetaProjection(
      @NotNull DatumTypeApi type,
      @NotNull List<SchemaOpInputModelProperty> modelProperties,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors
  ) throws PsiProcessingException {

    @Nullable SchemaOpInputModelMeta modelMetaPsi = null;

    for (SchemaOpInputModelProperty modelProperty : modelProperties) {
      if (modelMetaPsi == null) {
        modelMetaPsi = modelProperty.getOpInputModelMeta();
      } else {
        errors.add(new PsiProcessingError("Metadata projection should only be specified once", modelProperty));
      }
    }

    if (modelMetaPsi != null) {
      @Nullable DatumTypeApi metaType = type.metaType();
      if (metaType == null) {
        errors.add(new PsiProcessingError(
            String.format("Type '%s' doesn't have a metadata, metadata projection can't be specified", type.name()),
            modelMetaPsi
        ));
      } else {
        @NotNull SchemaOpInputModelProjection metaProjectionPsi = modelMetaPsi.getOpInputModelProjection();
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
        );
      }
    }

    return null;
  }

  private static @NotNull OpInputVarProjection buildTailProjection(
      @NotNull DataTypeApi dataType,
      @NotNull SchemaTypeRef tailTypeRefPsi,
      SchemaOpInputVarProjection psiTailProjection,
      @NotNull TypesResolver typesResolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    @NotNull TypeRef tailTypeRef = TypeRefs.fromPsi(tailTypeRefPsi, errors);
    @NotNull UnionTypeApi tailType = getUnionType(tailTypeRef, typesResolver, tailTypeRefPsi, errors);
    return parseVarProjection(
        tailType.dataType(dataType.defaultTag()),
        psiTailProjection,
        typesResolver,
        errors
    );
  }

  private static @NotNull OpInputVarProjection createDefaultVarProjection(
      @NotNull TypeApi type,
      @NotNull TagApi tag,
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
                    tag.type(),
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

  private static @NotNull OpInputVarProjection createDefaultVarProjection(
      @NotNull DatumTypeApi type,
      boolean required,
      @NotNull PsiElement locationPsi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors)
      throws PsiProcessingException {
    return createDefaultVarProjection(type, type.self(), required, locationPsi, resolver, errors);
  }

  private static @NotNull OpInputVarProjection createDefaultVarProjection(
      @NotNull DataTypeApi type,
      boolean required,
      @NotNull PsiElement locationPsi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors)
      throws PsiProcessingException {

    @Nullable TagApi defaultTag = type.defaultTag();
    if (defaultTag == null)
      throw new PsiProcessingException(
          String.format("Can't build default projection for '%s', default tag not specified", type.name()),
          locationPsi,
          errors
      );

    return createDefaultVarProjection(type.type(), defaultTag, required, locationPsi, resolver, errors);
  }

  public static @NotNull OpInputModelProjection<?, ?, ?, ?> parseModelProjection(
      @NotNull DatumTypeApi type,
      boolean required,
      @Nullable GDatum defaultValue,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @Nullable OpInputModelProjection<?, ?, ?, ?> metaProjection,
      @NotNull SchemaOpInputModelProjection psi,
      @NotNull TypesResolver typesResolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    switch (type.kind()) {
      case RECORD:
        @Nullable SchemaOpInputRecordModelProjection recordModelProjectionPsi =
            psi.getOpInputRecordModelProjection();

        if (recordModelProjectionPsi == null)
          return createDefaultModelProjection(
              type,
              required,
              defaultValue,
              params,
              annotations,
              psi,
              typesResolver,
              errors
          );

        ensureModelKind(psi, TypeKind.RECORD, errors);
        GRecordDatum defaultRecordData = coerceDefault(defaultValue, GRecordDatum.class, psi, errors);

        return parseRecordModelProjection(
            (RecordTypeApi) type,
            required,
            defaultRecordData,
            params,
            annotations,
            metaProjection,
            recordModelProjectionPsi,
            typesResolver,
            errors
        );

      case MAP:
        @Nullable SchemaOpInputMapModelProjection mapModelProjectionPsi = psi.getOpInputMapModelProjection();

        if (mapModelProjectionPsi == null)
          return createDefaultModelProjection(
              type,
              required,
              defaultValue,
              params,
              annotations,
              psi,
              typesResolver,
              errors
          );

        ensureModelKind(psi, TypeKind.MAP, errors);
        GMapDatum defaultMapData = coerceDefault(defaultValue, GMapDatum.class, psi, errors);

        return parseMapModelProjection(
            (MapTypeApi) type,
            required,
            defaultMapData,
            params,
            annotations,
            metaProjection,
            mapModelProjectionPsi,
            typesResolver,
            errors
        );

      case LIST:
        @Nullable SchemaOpInputListModelProjection listModelProjectionPsi = psi.getOpInputListModelProjection();

        if (listModelProjectionPsi == null)
          return createDefaultModelProjection(
              type,
              required,
              defaultValue,
              params,
              annotations,
              psi,
              typesResolver,
              errors
          );

        ensureModelKind(psi, TypeKind.LIST, errors);
        GListDatum defaultListData = coerceDefault(defaultValue, GListDatum.class, psi, errors);

        return parseListModelProjection(
            (ListTypeApi) type,
            required,
            defaultListData,
            params,
            annotations,
            metaProjection,
            listModelProjectionPsi,
            typesResolver,
            errors
        );

      case ENUM:
        throw new PsiProcessingException("Unsupported type kind: " + type.kind(), psi, errors);

      case PRIMITIVE:
        GPrimitiveDatum defaultPrimitiveData = coerceDefault(defaultValue, GPrimitiveDatum.class, psi, errors);

        return parsePrimitiveModelProjection(
            (PrimitiveTypeApi) type,
            required,
            defaultPrimitiveData,
            params,
            annotations,
            metaProjection,
            psi
        );

      case UNION:
        throw new PsiProcessingException("Unsupported type kind: " + type.kind(), psi, errors);

      default:
        throw new PsiProcessingException("Unknown type kind: " + type.kind(), psi, errors);
    }
  }

  private static void ensureModelKind(
      @NotNull SchemaOpInputModelProjection psi,
      @NotNull TypeKind expectedKind,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    @Nullable TypeKind actualKind = findProjectionKind(psi);
    if (expectedKind != actualKind)
      throw new PsiProcessingException(MessageFormat.format(
          "Unexpected projection kind ''{0}'', expected ''{1}''",
          actualKind,
          expectedKind
      ), psi, errors);
  }

  private static @Nullable TypeKind findProjectionKind(@NotNull SchemaOpInputModelProjection psi) {
    if (psi.getOpInputRecordModelProjection() != null) return TypeKind.RECORD;
    if (psi.getOpInputMapModelProjection() != null) return TypeKind.MAP;
    if (psi.getOpInputListModelProjection() != null) return TypeKind.LIST;
    return null;
  }

  public static @NotNull OpInputModelProjection<?, ?, ?, ?> createDefaultModelProjection(
      @NotNull DatumTypeApi type,
      boolean required,
      @Nullable GDatum defaultValue,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @NotNull PsiElement locationPsi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    @NotNull TextLocation location = EpigraphPsiUtil.getLocation(locationPsi);

    switch (type.kind()) {
      case RECORD:
        return new OpInputRecordModelProjection(
            (RecordTypeApi) type,
            required,
            (GRecordDatum) defaultValue,
            params,
            annotations,
            null,
            Collections.emptyMap(),
            location
        );
      case MAP:
        MapTypeApi mapType = (MapTypeApi) type;

        @NotNull DataTypeApi valueType = mapType.valueType();
        @Nullable TagApi defaultValuesTag = valueType.defaultTag();

        if (defaultValuesTag == null)
          throw new PsiProcessingException(String.format(
              "Can't create default projection for map type '%s, as it's value type '%s' doesn't have a default tag",
              type.name(),
              valueType.name()
          ), locationPsi, errors);

        final OpInputVarProjection valueVarProjection = createDefaultVarProjection(
            valueType.type(),
            defaultValuesTag,
            required,
            locationPsi,
            resolver,
            errors
        );

        return new OpInputMapModelProjection(
            mapType,
            required,
            (GMapDatum) defaultValue,
            params,
            annotations,
            null,
            new OpInputKeyProjection(
                OpKeyPresence.OPTIONAL,
                OpParams.EMPTY,
                Annotations.EMPTY,
                location
            ),
            valueVarProjection,
            location
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
          ), locationPsi, errors);

        final OpInputVarProjection itemVarProjection = createDefaultVarProjection(
            elementType.type(),
            defaultElementsTag,
            required,
            locationPsi,
            resolver,
            errors
        );

        return new OpInputListModelProjection(
            listType,
            required,
            (GListDatum) defaultValue,
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
            (PrimitiveTypeApi) type,
            required,
            (GPrimitiveDatum) defaultValue,
            params,
            annotations,
            null,
            location
        );
      default:
        throw new PsiProcessingException("Unknown type kind: " + type.kind(), locationPsi, errors);
    }
  }

  public static @NotNull OpInputRecordModelProjection parseRecordModelProjection(
      @NotNull RecordTypeApi type,
      boolean required,
      @Nullable GRecordDatum defaultValue,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @Nullable OpInputModelProjection<?, ?, ?, ?> metaProjection,
      @NotNull SchemaOpInputRecordModelProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) {

    LinkedHashMap<String, OpInputFieldProjectionEntry> fieldProjections = new LinkedHashMap<>();
    @NotNull List<SchemaOpInputFieldProjectionEntry> psiFieldProjections = psi.getOpInputFieldProjectionEntryList();

    for (SchemaOpInputFieldProjectionEntry fieldProjectionPsi : psiFieldProjections) {
      try {
        final String fieldName = fieldProjectionPsi.getQid().getCanonicalName();
        FieldApi field = type.fieldsMap().get(fieldName);
        if (field == null) {
          errors.add(new PsiProcessingError(
                  String.format("Can't build field projection for '%s', field '%s' not found", type.name(), fieldName),
                  fieldProjectionPsi
              )
          );
          continue;
        }

        final @NotNull DataTypeApi fieldType = field.dataType();
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

    return new OpInputRecordModelProjection(
        type,
        required,
        defaultValue,
        params,
        annotations,
        metaProjection,
        fieldProjections,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  public static @NotNull OpInputFieldProjection parseFieldProjection(
      final DataTypeApi fieldType,
      final boolean required,
      final @NotNull SchemaOpInputFieldProjection psi,
      final @NotNull TypesResolver resolver,
      final @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    @NotNull OpParams fieldParams = parseParams(
        psi.getOpInputFieldProjectionBodyPartList()
            .stream()
            .map(SchemaOpInputFieldProjectionBodyPart::getOpParam),
        resolver,
        errors
    );

    @NotNull Annotations fieldAnnotations = parseAnnotations(
        psi.getOpInputFieldProjectionBodyPartList()
            .stream()
            .map(SchemaOpInputFieldProjectionBodyPart::getAnnotation),
        errors
    );

    @NotNull SchemaOpInputVarProjection psiVarProjection = psi.getOpInputVarProjection();
    OpInputVarProjection varProjection = parseVarProjection(fieldType, psiVarProjection, resolver, errors);

    return new OpInputFieldProjection(
        fieldParams,
        fieldAnnotations,
        varProjection,
        required,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  public static @NotNull OpInputMapModelProjection parseMapModelProjection(
      @NotNull MapTypeApi type,
      boolean required,
      @Nullable GMapDatum defaultValue,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @Nullable OpInputModelProjection<?, ?, ?, ?> metaProjection,
      @NotNull SchemaOpInputMapModelProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    @NotNull OpInputKeyProjection keyProjection = parseKeyProjection(psi.getOpInputKeyProjection(), resolver, errors);

    @Nullable SchemaOpInputVarProjection valueProjectionPsi = psi.getOpInputVarProjection();
    @NotNull OpInputVarProjection valueProjection =
        valueProjectionPsi == null ?
        createDefaultVarProjection(type.valueType(), false, psi, resolver, errors) :
        parseVarProjection(type.valueType(), valueProjectionPsi, resolver, errors);

    return new OpInputMapModelProjection(
        type,
        required,
        defaultValue,
        params,
        annotations,
        metaProjection,
        keyProjection,
        valueProjection,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  private static @NotNull OpInputKeyProjection parseKeyProjection(
      @NotNull SchemaOpInputKeyProjection keyProjectionPsi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    final OpKeyPresence presence;

    if (keyProjectionPsi.getForbidden() != null)
      presence = OpKeyPresence.FORBIDDEN;
    else if (keyProjectionPsi.getRequired() != null)
      presence = OpKeyPresence.REQUIRED;
    else
      presence = OpKeyPresence.OPTIONAL;

    final @NotNull List<SchemaOpInputKeyProjectionPart> keyPartsPsi =
        keyProjectionPsi.getOpInputKeyProjectionPartList();

    final @NotNull OpParams keyParams =
        parseParams(keyPartsPsi.stream().map(SchemaOpInputKeyProjectionPart::getOpParam), resolver, errors);
    final @NotNull Annotations keyAnnotations =
        parseAnnotations(keyPartsPsi.stream().map(SchemaOpInputKeyProjectionPart::getAnnotation), errors);

    return new OpInputKeyProjection(
        presence,
        keyParams,
        keyAnnotations,
        EpigraphPsiUtil.getLocation(keyProjectionPsi)
    );
  }

  public static @NotNull OpInputListModelProjection parseListModelProjection(
      @NotNull ListTypeApi type,
      boolean required,
      @Nullable GListDatum defaultValue,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @Nullable OpInputModelProjection<?, ?, ?, ?> metaProjection,
      @NotNull SchemaOpInputListModelProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    OpInputVarProjection itemsProjection;
    @Nullable SchemaOpInputVarProjection opInputVarProjectionPsi = psi.getOpInputVarProjection();
    if (opInputVarProjectionPsi == null)
      itemsProjection = createDefaultVarProjection(type, true, psi, resolver, errors);
    else
      itemsProjection = parseVarProjection(type.elementType(), opInputVarProjectionPsi, resolver, errors);

    return new OpInputListModelProjection(
        type,
        required,
        defaultValue,
        params,
        annotations,
        metaProjection,
        itemsProjection,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  public static @NotNull OpInputPrimitiveModelProjection parsePrimitiveModelProjection(
      @NotNull PrimitiveTypeApi type,
      boolean required,
      @Nullable GPrimitiveDatum defaultValue,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @Nullable OpInputModelProjection<?, ?, ?, ?> metaProjection,
      @NotNull PsiElement locationPsi) {

    return new OpInputPrimitiveModelProjection(
        type,
        required,
        defaultValue,
        params,
        annotations,
        metaProjection,
        EpigraphPsiUtil.getLocation(locationPsi)
    );
  }

  @SuppressWarnings("unchecked")
  private static @Nullable <D extends GDatum> D coerceDefault(
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
