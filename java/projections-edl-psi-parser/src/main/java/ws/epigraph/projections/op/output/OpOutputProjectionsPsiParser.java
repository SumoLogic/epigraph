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

package ws.epigraph.projections.op.output;

import com.intellij.psi.PsiElement;
import ws.epigraph.edl.TypeRefs;
import ws.epigraph.edl.parser.psi.*;
import ws.epigraph.projections.Annotation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.ProjectionUtils;
import ws.epigraph.projections.op.OpKeyPresence;
import ws.epigraph.projections.op.OpParam;
import ws.epigraph.projections.op.OpParams;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingError;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.TypeRef;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.types.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.types.TypeKind;

import java.text.MessageFormat;
import java.util.*;

import static ws.epigraph.projections.EdlProjectionPsiParserUtil.*;
import static ws.epigraph.projections.ProjectionsParsingUtil.getType;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class OpOutputProjectionsPsiParser {

  private OpOutputProjectionsPsiParser() {}

  public static OpOutputVarProjection parseVarProjection(
      @NotNull DataType dataType,
      @NotNull EdlOpOutputVarProjection psi,
      @NotNull TypesResolver typesResolver,
      @NotNull List<PsiProcessingError> errors)
      throws PsiProcessingException {

    final Type type = dataType.type;
    final LinkedHashMap<String, OpOutputTagProjectionEntry> tagProjections = new LinkedHashMap<>();

    @Nullable EdlOpOutputSingleTagProjection singleTagProjectionPsi = psi.getOpOutputSingleTagProjection();

    if (singleTagProjectionPsi == null) {
      @Nullable EdlOpOutputMultiTagProjection multiTagProjection = psi.getOpOutputMultiTagProjection();
      assert multiTagProjection != null;
      // parse list of tags
      @NotNull List<EdlOpOutputMultiTagProjectionItem> tagProjectionPsiList =
          multiTagProjection.getOpOutputMultiTagProjectionItemList();

      for (EdlOpOutputMultiTagProjectionItem tagProjectionPsi : tagProjectionPsiList) {
        final Type.Tag tag = getTag(type, tagProjectionPsi.getTagName(), dataType.defaultTag, tagProjectionPsi, errors);

        final OpOutputModelProjection<?, ?> parsedModelProjection;

        @NotNull DatumType tagType = tag.type;
        @Nullable EdlOpOutputModelProjection modelProjection = tagProjectionPsi.getOpOutputModelProjection();
        assert modelProjection != null; // todo when it can be null?

        @NotNull List<EdlOpOutputModelProperty> modelPropertiesPsi = tagProjectionPsi.getOpOutputModelPropertyList();

        parsedModelProjection = parseModelProjection(
            tagType,
            parseModelParams(modelPropertiesPsi, typesResolver, errors),
            parseModelAnnotations(modelPropertiesPsi, errors),
            parseModelMetaProjection(tagType, modelPropertiesPsi, typesResolver, errors),
            modelProjection,
            typesResolver,
            errors
        );

        tagProjections.put(
            tag.name(),
            new OpOutputTagProjectionEntry(
                tag,
                parsedModelProjection,
                EpigraphPsiUtil.getLocation(tagProjectionPsi)
            )
        );
      }
    } else {
      Type.Tag tag = findTag(
          type,
          singleTagProjectionPsi.getTagName(),
          dataType.defaultTag,
          singleTagProjectionPsi,
          errors
      );
      if (tag != null || !singleTagProjectionPsi.getText().isEmpty()) {
        final OpOutputModelProjection<?, ?> parsedModelProjection;
        if (tag == null) // will throw proper error
          tag = getTag(
              type,
              singleTagProjectionPsi.getTagName(),
              dataType.defaultTag,
              singleTagProjectionPsi,
              errors
          );

        @Nullable EdlOpOutputModelProjection modelProjection = singleTagProjectionPsi.getOpOutputModelProjection();
        assert modelProjection != null; // todo when it can be null?

        @NotNull List<EdlOpOutputModelProperty> modelPropertiesPsi =
            singleTagProjectionPsi.getOpOutputModelPropertyList();

        parsedModelProjection = parseModelProjection(
            tag.type,
            parseModelParams(modelPropertiesPsi, typesResolver, errors),
            parseModelAnnotations(modelPropertiesPsi, errors),
            parseModelMetaProjection(tag.type, modelPropertiesPsi, typesResolver, errors),
            modelProjection,
            typesResolver,
            errors
        );

        tagProjections.put(
            tag.name(),
            new OpOutputTagProjectionEntry(
                tag,
                parsedModelProjection,
                EpigraphPsiUtil.getLocation(singleTagProjectionPsi)
            )
        );
      }
    }

    // parse tails
    final List<OpOutputVarProjection> tails;
    @Nullable EdlOpOutputVarPolymorphicTail psiTail = psi.getOpOutputVarPolymorphicTail();
    if (psiTail == null) tails = null;
    else {
      tails = new ArrayList<>();

      @Nullable EdlOpOutputVarSingleTail singleTail = psiTail.getOpOutputVarSingleTail();
      if (singleTail == null) {
        @Nullable EdlOpOutputVarMultiTail multiTail = psiTail.getOpOutputVarMultiTail();
        assert multiTail != null;
        for (EdlOpOutputVarMultiTailItem tailItem : multiTail.getOpOutputVarMultiTailItemList()) {
          @NotNull EdlTypeRef tailTypeRef = tailItem.getTypeRef();
          @NotNull EdlOpOutputVarProjection psiTailProjection = tailItem.getOpOutputVarProjection();
          @NotNull OpOutputVarProjection tailProjection =
              buildTailProjection(dataType, tailTypeRef, psiTailProjection, typesResolver, errors);
          tails.add(tailProjection);
        }
      } else {
        @NotNull EdlTypeRef tailTypeRef = singleTail.getTypeRef();
        @NotNull EdlOpOutputVarProjection psiTailProjection = singleTail.getOpOutputVarProjection();
        @NotNull OpOutputVarProjection tailProjection =
            buildTailProjection(dataType, tailTypeRef, psiTailProjection, typesResolver, errors);
        tails.add(tailProjection);
      }

    }

    try {
      return new OpOutputVarProjection(
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


  private static @NotNull OpParams parseModelParams(
      @NotNull List<EdlOpOutputModelProperty> modelProperties,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {


    return parseParams(
        modelProperties.stream().map(EdlOpOutputModelProperty::getOpParam),
        resolver,
        errors
    );
  }

  private static @NotNull Annotations parseModelAnnotations(
      @NotNull List<EdlOpOutputModelProperty> modelProperties,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    return parseAnnotations(
        modelProperties.stream().map(EdlOpOutputModelProperty::getAnnotation),
        errors
    );
  }

  private static @Nullable OpOutputModelProjection<?, ?> parseModelMetaProjection(
      @NotNull DatumType type,
      @NotNull List<EdlOpOutputModelProperty> modelProperties,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors
  ) throws PsiProcessingException {

    @Nullable EdlOpOutputModelMeta modelMetaPsi = null;

    for (EdlOpOutputModelProperty modelProperty : modelProperties) {
      if (modelMetaPsi != null)
        errors.add(new PsiProcessingError("Metadata projection should be specified only once", modelProperty));

      modelMetaPsi = modelProperty.getOpOutputModelMeta();
    }

    if (modelMetaPsi == null) return null;
    else {
      @Nullable DatumType metaType = null; // TODO need a way to extract it from 'type'
      if (metaType == null) {
        errors.add(new PsiProcessingError(
            String.format("Type '%s' doesn't have a metadata, metadata projection can't be specified", type.name()),
            modelMetaPsi
        ));
        return null;
      } else {

        @NotNull EdlOpOutputModelProjection metaProjectionPsi = modelMetaPsi.getOpOutputModelProjection();
        return parseModelProjection(
            metaType,
            OpParams.EMPTY,
            Annotations.EMPTY,
            null, // TODO what if meta-type has it's own meta-type? meta-meta-type projection should go here
            metaProjectionPsi,
            resolver,
            errors
        );
      }
    }
  }

  private static @NotNull OpOutputVarProjection buildTailProjection(
      @NotNull DataType dataType,
      @NotNull EdlTypeRef tailTypeRefPsi,
      @NotNull EdlOpOutputVarProjection psiTailProjection,
      @NotNull TypesResolver typesResolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    @NotNull TypeRef tailTypeRef = TypeRefs.fromPsi(tailTypeRefPsi, errors);
    @NotNull Type tailType = getType(tailTypeRef, typesResolver, tailTypeRefPsi, errors);
    return parseVarProjection(
        new DataType(tailType, dataType.defaultTag),
        psiTailProjection,
        typesResolver,
        errors
    );
  }


  private static @NotNull OpOutputVarProjection createDefaultVarProjection(
      @NotNull Type type,
      @NotNull Type.Tag tag,
      @NotNull PsiElement locationPsi,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {
    return new OpOutputVarProjection(
        type,
        ProjectionUtils.singletonLinkedHashMap(
            tag.name(),
            new OpOutputTagProjectionEntry(
                tag,
                createDefaultModelProjection(
                    tag.type,
                    OpParams.EMPTY,
                    Annotations.EMPTY,
                    locationPsi,
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

  private static @NotNull OpOutputVarProjection createDefaultVarProjection(
      @NotNull DatumType type,
      @NotNull PsiElement locationPsi,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {
    return createDefaultVarProjection(type, type.self, locationPsi, errors);
  }

  public static @NotNull OpOutputVarProjection createDefaultVarProjection(
      @NotNull DataType type,
      @NotNull PsiElement locationPsi,
      @NotNull List<PsiProcessingError> errors)
      throws PsiProcessingException {

    @Nullable Type.Tag defaultTag = type.defaultTag;
    if (defaultTag == null) {

      if (type.type instanceof DatumType) {
        DatumType datumType = (DatumType) type.type;
        defaultTag = datumType.self;
      } else {
        throw new PsiProcessingException(
            String.format("Can't build default projection for '%s', default tag not specified", type.name), locationPsi,
            errors
        );
      }

    }

    return createDefaultVarProjection(type.type, defaultTag, locationPsi, errors);
  }

  public static @NotNull OpOutputModelProjection<?, ?> parseModelProjection(
      @NotNull DatumType type,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @Nullable OpOutputModelProjection<?, ?> metaProjection,
      @NotNull EdlOpOutputModelProjection psi,
      @NotNull TypesResolver typesResolver,
      @NotNull List<PsiProcessingError> errors)
      throws PsiProcessingException {

    switch (type.kind()) {
      case RECORD:
        @Nullable EdlOpOutputRecordModelProjection recordModelProjectionPsi = psi.getOpOutputRecordModelProjection();
        if (recordModelProjectionPsi == null)
          return createDefaultModelProjection(type, params, annotations, psi, errors);
        ensureModelKind(psi, TypeKind.RECORD, errors);
        return parseRecordModelProjection(
            (RecordType) type,
            params,
            annotations,
            (OpOutputRecordModelProjection) metaProjection,
            recordModelProjectionPsi,
            typesResolver,
            errors
        );
      case MAP:
        @Nullable EdlOpOutputMapModelProjection mapModelProjectionPsi = psi.getOpOutputMapModelProjection();
        if (mapModelProjectionPsi == null)
          return createDefaultModelProjection(type, params, annotations, psi, errors);
        ensureModelKind(psi, TypeKind.MAP, errors);

        return parseMapModelProjection(
            (MapType) type,
            params,
            annotations,
            (OpOutputMapModelProjection) metaProjection,
            mapModelProjectionPsi,
            typesResolver,
            errors
        );
      case LIST:
        @Nullable EdlOpOutputListModelProjection listModelProjectionPsi = psi.getOpOutputListModelProjection();
        if (listModelProjectionPsi == null)
          return createDefaultModelProjection(type, params, annotations, psi, errors);
        ensureModelKind(psi, TypeKind.LIST, errors);

        return parseListModelProjection(
            (ListType) type,
            params,
            annotations,
            (OpOutputListModelProjection) metaProjection,
            listModelProjectionPsi,
            typesResolver,
            errors
        );
      case ENUM:
        throw new PsiProcessingException("Unsupported type kind: " + type.kind(), psi, errors);
      case PRIMITIVE:
        return parsePrimitiveModelProjection(
            (PrimitiveType<?>) type,
            params,
            annotations,
            (OpOutputPrimitiveModelProjection) metaProjection,
            psi
        );
      case UNION:
        throw new PsiProcessingException("Unsupported type kind: " + type.kind(), psi, errors);
      default:
        throw new PsiProcessingException("Unknown type kind: " + type.kind(), psi, errors);
    }
  }

  private static void ensureModelKind(
      @NotNull EdlOpOutputModelProjection psi,
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

  private static @Nullable TypeKind findProjectionKind(@NotNull EdlOpOutputModelProjection psi) {
    if (psi.getOpOutputRecordModelProjection() != null) return TypeKind.RECORD;
    if (psi.getOpOutputMapModelProjection() != null) return TypeKind.MAP;
    if (psi.getOpOutputListModelProjection() != null) return TypeKind.LIST;
    return null;
  }

  private static @NotNull OpOutputModelProjection<?, ?> createDefaultModelProjection(
      @NotNull DatumType type,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @NotNull PsiElement locationPsi,
      @NotNull List<PsiProcessingError> errors)
      throws PsiProcessingException {

    switch (type.kind()) {
      case RECORD:
        return new OpOutputRecordModelProjection(
            (RecordType) type,
            params,
            annotations,
            null,
            Collections.emptyMap(),
            EpigraphPsiUtil.getLocation(locationPsi)
        );
      case MAP:
        MapType mapType = (MapType) type;

        final OpOutputKeyProjection keyProjection =
            new OpOutputKeyProjection(
                OpKeyPresence.OPTIONAL,
                OpParams.EMPTY,
                Annotations.EMPTY,
                EpigraphPsiUtil.getLocation(locationPsi)
            );

        @NotNull DataType valueType = mapType.valueType();
        Type.@Nullable Tag defaultValuesTag = valueType.defaultTag;

        if (defaultValuesTag == null)
          throw new PsiProcessingException(String.format(
              "Can't create default projection for map type '%s, as it's value type '%s' doesn't have a default tag",
              type.name(),
              valueType.name
          ), locationPsi, errors);

        final OpOutputVarProjection valueVarProjection = createDefaultVarProjection(
            valueType.type,
            defaultValuesTag,
            locationPsi,
            errors
        );

        return new OpOutputMapModelProjection(
            mapType,
            params,
            annotations,
            null,
            keyProjection,
            valueVarProjection,
            EpigraphPsiUtil.getLocation(locationPsi)
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

        final OpOutputVarProjection itemVarProjection = createDefaultVarProjection(
            elementType.type,
            defaultElementsTag,
            locationPsi,
            errors
        );

        return new OpOutputListModelProjection(
            listType,
            params,
            annotations,
            null,
            itemVarProjection,
            EpigraphPsiUtil.getLocation(locationPsi)
        );
      case UNION:
        throw new PsiProcessingException(
            "Was expecting to get datum model kind, got: " + type.kind(),
            locationPsi,
            errors
        );
      case ENUM:
        throw new PsiProcessingException("Unsupported type kind: " + type.kind(), locationPsi, errors);
      case PRIMITIVE:
        return new OpOutputPrimitiveModelProjection(
            (PrimitiveType<?>) type,
            params,
            annotations,
            null,
            EpigraphPsiUtil.getLocation(locationPsi)
        );
      default:
        throw new PsiProcessingException("Unknown type kind: " + type.kind(), locationPsi, errors);
    }
  }

  public static @NotNull OpOutputRecordModelProjection parseRecordModelProjection(
      @NotNull RecordType type,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @Nullable OpOutputRecordModelProjection metaProjection,
      @NotNull EdlOpOutputRecordModelProjection psi,
      @NotNull TypesResolver typesResolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    LinkedHashMap<String, OpOutputFieldProjectionEntry> fieldProjections = new LinkedHashMap<>();
    @NotNull List<EdlOpOutputFieldProjectionEntry> fieldProjectionEntriesPsi =
        psi.getOpOutputFieldProjectionEntryList();

    for (EdlOpOutputFieldProjectionEntry fieldProjectionEntryPsi : fieldProjectionEntriesPsi) {
      final String fieldName = fieldProjectionEntryPsi.getQid().getCanonicalName();
      RecordType.Field field = type.fieldsMap().get(fieldName);

      if (field == null) {
        errors.add(new PsiProcessingError(
            String.format(
                "Unknown field '%s' in type '%s'; known fields: {%s}",
                fieldName,
                type.name(),
                String.join(", ", type.fieldsMap().keySet())
            ),
            fieldProjectionEntryPsi
        ));
        continue;
      }

      final @NotNull EdlOpOutputFieldProjection fieldProjectionPsi =
          fieldProjectionEntryPsi.getOpOutputFieldProjection();

      final OpOutputFieldProjection opOutputFieldProjection = parseFieldProjection(
          field.dataType(),
          fieldProjectionPsi,
          typesResolver,
          errors
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

    return new OpOutputRecordModelProjection(
        type,
        params,
        annotations,
        metaProjection,
        fieldProjections,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  public static @NotNull OpOutputFieldProjection parseFieldProjection(
      @NotNull DataType fieldType,
      @NotNull EdlOpOutputFieldProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    List<OpParam> fieldParamsList = null;
    @Nullable Map<String, Annotation> fieldAnnotationsMap = null;
    for (EdlOpOutputFieldProjectionBodyPart fieldBodyPart : psi.getOpOutputFieldProjectionBodyPartList()) {
      @Nullable EdlOpParam fieldParamPsi = fieldBodyPart.getOpParam();
      if (fieldParamPsi != null) {
        if (fieldParamsList == null) fieldParamsList = new ArrayList<>(3);
        fieldParamsList.add(parseParameter(fieldParamPsi, resolver, errors));
      }

      fieldAnnotationsMap = parseAnnotation(fieldAnnotationsMap, fieldBodyPart.getAnnotation(), errors);
    }

    return new OpOutputFieldProjection(
        OpParams.fromCollection(fieldParamsList),
        Annotations.fromMap(fieldAnnotationsMap),
        parseVarProjection(fieldType, psi.getOpOutputVarProjection(), resolver, errors),
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  public static @NotNull OpOutputMapModelProjection parseMapModelProjection(
      @NotNull MapType type,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @Nullable OpOutputMapModelProjection metaProjection,
      @NotNull EdlOpOutputMapModelProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors)
      throws PsiProcessingException {

    @NotNull OpOutputKeyProjection keyProjection = parseKeyProjection(psi.getOpOutputKeyProjection(), resolver, errors);

    @Nullable EdlOpOutputVarProjection valueProjectionPsi = psi.getOpOutputVarProjection();
    @NotNull OpOutputVarProjection valueProjection =
        valueProjectionPsi == null
        ? createDefaultVarProjection(
            type.valueType(),
            psi,
            errors
        )
        : parseVarProjection(type.valueType(), valueProjectionPsi, resolver, errors);

    return new OpOutputMapModelProjection(
        type,
        params,
        annotations,
        metaProjection,
        keyProjection,
        valueProjection,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  private static @NotNull OpOutputKeyProjection parseKeyProjection(
      @NotNull EdlOpOutputKeyProjection keyProjectionPsi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    final OpKeyPresence presence;

    if (keyProjectionPsi.getForbidden() != null)
      presence = OpKeyPresence.FORBIDDEN;
    else if (keyProjectionPsi.getRequired() != null)
      presence = OpKeyPresence.REQUIRED;
    else
      presence = OpKeyPresence.OPTIONAL;

    final @NotNull List<EdlOpOutputKeyProjectionPart> keyPartsPsi =
        keyProjectionPsi.getOpOutputKeyProjectionPartList();

    final @NotNull OpParams keyParams =
        parseParams(keyPartsPsi.stream().map(EdlOpOutputKeyProjectionPart::getOpParam), resolver, errors);
    final @NotNull Annotations keyAnnotations =
        parseAnnotations(keyPartsPsi.stream().map(EdlOpOutputKeyProjectionPart::getAnnotation), errors);

    return new OpOutputKeyProjection(
        presence,
        keyParams,
        keyAnnotations,
        EpigraphPsiUtil.getLocation(keyProjectionPsi)
    );
  }

  public static @NotNull OpOutputListModelProjection parseListModelProjection(
      @NotNull ListType type,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @Nullable OpOutputListModelProjection metaProjection,
      @NotNull EdlOpOutputListModelProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors)
      throws PsiProcessingException {

    OpOutputVarProjection itemsProjection;
    @Nullable EdlOpOutputVarProjection opOutputVarProjectionPsi = psi.getOpOutputVarProjection();
    if (opOutputVarProjectionPsi == null)
      itemsProjection = createDefaultVarProjection(type, psi, errors);
    else
      itemsProjection = parseVarProjection(type.elementType(), opOutputVarProjectionPsi, resolver, errors);


    return new OpOutputListModelProjection(
        type,
        params,
        annotations,
        metaProjection,
        itemsProjection,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  public static @NotNull OpOutputPrimitiveModelProjection parsePrimitiveModelProjection(
      @NotNull PrimitiveType<?> type,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @Nullable OpOutputPrimitiveModelProjection metaProjection,
      @NotNull PsiElement locationPsi) {

    return new OpOutputPrimitiveModelProjection(
        type,
        params,
        annotations,
        metaProjection,
        EpigraphPsiUtil.getLocation(locationPsi)
    );
  }

}
