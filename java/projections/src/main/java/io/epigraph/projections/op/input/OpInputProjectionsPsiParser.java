package io.epigraph.projections.op.input;

import com.intellij.psi.PsiElement;
import io.epigraph.data.*;
import io.epigraph.gdata.*;
import io.epigraph.idl.TypeRefs;
import io.epigraph.idl.parser.psi.*;
import io.epigraph.lang.TextLocation;
import io.epigraph.projections.Annotation;
import io.epigraph.projections.Annotations;
import io.epigraph.projections.ProjectionUtils;
import io.epigraph.projections.StepsAndProjection;
import io.epigraph.psi.EpigraphPsiUtil;
import io.epigraph.psi.PsiProcessingException;
import io.epigraph.refs.TypeRef;
import io.epigraph.refs.TypesResolver;
import io.epigraph.types.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.*;

import static io.epigraph.projections.ProjectionPsiParserUtil.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpInputProjectionsPsiParser {
  public static StepsAndProjection<OpInputVarProjection> parseTrunkVarProjection(
      @NotNull DataType dataType,
      @NotNull IdlOpInputTrunkVarProjection psi,
      @NotNull TypesResolver typesResolver) throws PsiProcessingException {

    final Type type = dataType.type;
    final LinkedHashMap<Type.Tag, OpInputTagProjection> tagProjections;
    final int steps;

    @Nullable IdlOpInputTrunkSingleTagProjection singleTagProjectionPsi = psi.getOpInputTrunkSingleTagProjection();
    if (singleTagProjectionPsi != null) {
      tagProjections = new LinkedHashMap<>();
      final OpInputModelProjection<?, ?> parsedModelProjection;
      final Type.Tag tag = getTag(
          type,
          singleTagProjectionPsi.getTagName(),
          dataType.defaultTag,
          singleTagProjectionPsi
      );

      @Nullable IdlOpInputTrunkModelProjection modelProjection =
          singleTagProjectionPsi.getOpInputTrunkModelProjection();
      assert modelProjection != null; // todo when it can be null?

      @NotNull List<IdlOpInputModelProperty> modelPropertiesPsi = singleTagProjectionPsi.getOpInputModelPropertyList();

      StepsAndProjection<? extends OpInputModelProjection<?, ?>> stepsAndProjection = parseTrunkModelProjection(
          tag.type,
          singleTagProjectionPsi.getPlus() != null,
          getModelDefaultValue(modelPropertiesPsi),
          parseModelAnnotations(modelPropertiesPsi),
          parseModelMetaProjection(tag.type, modelPropertiesPsi, typesResolver),
          modelProjection, typesResolver
      );

      parsedModelProjection = stepsAndProjection.projection();
      steps = stepsAndProjection.pathSteps() + 1;

      tagProjections.put(
          tag,
          new OpInputTagProjection(
              parsedModelProjection,
              EpigraphPsiUtil.getLocation(singleTagProjectionPsi)
          )
      );

    } else {
      @Nullable IdlOpInputComaMultiTagProjection multiTagProjection = psi.getOpInputComaMultiTagProjection();
      assert multiTagProjection != null;
      tagProjections = parseComaMultiTagProjection(dataType, multiTagProjection, typesResolver);
      steps = 0;
    }

    final List<OpInputVarProjection> tails =
        parseTails(dataType, psi.getOpInputVarPolymorphicTail(), typesResolver);

    return new StepsAndProjection<>(
        steps,
        new OpInputVarProjection(type, tagProjections, tails, EpigraphPsiUtil.getLocation(psi))
    );
  }

  public static StepsAndProjection<OpInputVarProjection> parseComaVarProjection(
      @NotNull DataType dataType,
      @NotNull IdlOpInputComaVarProjection psi,
      @NotNull TypesResolver typesResolver) throws PsiProcessingException {

    final Type type = dataType.type;
    final LinkedHashMap<Type.Tag, OpInputTagProjection> tagProjections;

    @Nullable IdlOpInputComaSingleTagProjection singleTagProjectionPsi = psi.getOpInputComaSingleTagProjection();
    if (singleTagProjectionPsi != null) {
      tagProjections = new LinkedHashMap<>();
      final OpInputModelProjection<?, ?> parsedModelProjection;
      final Type.Tag tag = getTag(
          type,
          singleTagProjectionPsi.getTagName(),
          dataType.defaultTag,
          singleTagProjectionPsi
      );

      @Nullable IdlOpInputComaModelProjection modelProjection = singleTagProjectionPsi.getOpInputComaModelProjection();
      assert modelProjection != null; // todo when it can be null?

      @NotNull List<IdlOpInputModelProperty> modelPropertiesPsi = singleTagProjectionPsi.getOpInputModelPropertyList();
      parsedModelProjection = parseComaModelProjection(
          tag.type,
          singleTagProjectionPsi.getPlus() != null,
          getModelDefaultValue(modelPropertiesPsi),
          parseModelAnnotations(modelPropertiesPsi),
          parseModelMetaProjection(tag.type, modelPropertiesPsi, typesResolver),
          modelProjection, typesResolver
      ).projection();

      tagProjections.put(
          tag,
          new OpInputTagProjection(
              parsedModelProjection,
              EpigraphPsiUtil.getLocation(singleTagProjectionPsi)
          )
      );

    } else {
      @Nullable IdlOpInputComaMultiTagProjection multiTagProjection = psi.getOpInputComaMultiTagProjection();
      assert multiTagProjection != null;
      tagProjections = parseComaMultiTagProjection(dataType, multiTagProjection, typesResolver);
    }

    final List<OpInputVarProjection> tails =
        parseTails(dataType, psi.getOpInputVarPolymorphicTail(), typesResolver);

    return new StepsAndProjection<>(
        0,
        new OpInputVarProjection(type, tagProjections, tails, EpigraphPsiUtil.getLocation(psi))
    );
  }

  @NotNull
  private static LinkedHashMap<Type.Tag, OpInputTagProjection> parseComaMultiTagProjection(
      @NotNull DataType dataType,
      @NotNull IdlOpInputComaMultiTagProjection multiTagProjection,
      @NotNull TypesResolver typesResolver) throws PsiProcessingException {

    final LinkedHashMap<Type.Tag, OpInputTagProjection> tagProjections = new LinkedHashMap<>();

    // parse list of tags
    @NotNull List<IdlOpInputComaMultiTagProjectionItem> tagProjectionPsiList =
        multiTagProjection.getOpInputComaMultiTagProjectionItemList();

    for (IdlOpInputComaMultiTagProjectionItem tagProjectionPsi : tagProjectionPsiList) {
      final Type.Tag tag =
          getTag(dataType.type, tagProjectionPsi.getTagName(), dataType.defaultTag, tagProjectionPsi);

      final OpInputModelProjection<?, ?> parsedModelProjection;

      @NotNull DatumType tagType = tag.type;
      @Nullable IdlOpInputComaModelProjection modelProjection = tagProjectionPsi.getOpInputComaModelProjection();
      assert modelProjection != null; // todo when it can be null?
      @NotNull List<IdlOpInputModelProperty> modelPropertiesPsi = tagProjectionPsi.getOpInputModelPropertyList();

      parsedModelProjection = parseComaModelProjection(
          tagType,
          tagProjectionPsi.getPlus() != null,
          getModelDefaultValue(modelPropertiesPsi),
          parseModelAnnotations(modelPropertiesPsi),
          parseModelMetaProjection(tagType, modelPropertiesPsi, typesResolver),
          modelProjection, typesResolver
      ).projection();

      tagProjections.put(
          tag,
          new OpInputTagProjection(
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
      @NotNull TypesResolver typesResolver) throws PsiProcessingException {

    final List<OpInputVarProjection> tails;

    if (tailPsi != null) {
      tails = new ArrayList<>();

      @Nullable IdlOpInputVarSingleTail singleTail = tailPsi.getOpInputVarSingleTail();
      if (singleTail != null) {
        @NotNull IdlTypeRef typeRefPsi = singleTail.getTypeRef();
        @NotNull IdlOpInputComaVarProjection psiTailProjection = singleTail.getOpInputComaVarProjection();
        @NotNull OpInputVarProjection tailProjection =
            buildTailProjection(dataType, typeRefPsi, psiTailProjection, typesResolver, singleTail);
        tails.add(tailProjection);
      } else {
        @Nullable IdlOpInputVarMultiTail multiTail = tailPsi.getOpInputVarMultiTail();
        assert multiTail != null;
        for (IdlOpInputVarMultiTailItem tailItem : multiTail.getOpInputVarMultiTailItemList()) {
          @NotNull IdlTypeRef tailTypeRef = tailItem.getTypeRef();
          @NotNull IdlOpInputComaVarProjection psiTailProjection = tailItem.getOpInputComaVarProjection();
          @NotNull OpInputVarProjection tailProjection =
              buildTailProjection(dataType, tailTypeRef, psiTailProjection, typesResolver, tailItem);
          tails.add(tailProjection);
        }
      }

    } else tails = null;

    return tails;
  }

  @Nullable
  private static GDatum getModelDefaultValue(@NotNull List<IdlOpInputModelProperty> modelProperties)
      throws PsiProcessingException {

    GDatum result = null;
    for (IdlOpInputModelProperty property : modelProperties) {
      @Nullable IdlOpInputDefaultValue defaultValuePsi = property.getOpInputDefaultValue();
      if (defaultValuePsi != null) {
        if (result != null)
          throw new PsiProcessingException("Default value should only be specified once", defaultValuePsi);

        @Nullable IdlDatum varValuePsi = defaultValuePsi.getDatum();
        if (varValuePsi != null)
          result = IdlGDataPsiParser.parseDatum(varValuePsi);
      }
    }

    return result;
  }

  @Nullable
  private static Annotations parseModelAnnotations(@NotNull List<IdlOpInputModelProperty> modelProperties)
      throws PsiProcessingException {

    @Nullable Map<String, Annotation> annotationsMap = null;

    for (IdlOpInputModelProperty modelProperty : modelProperties)
      annotationsMap = parseAnnotation(annotationsMap, modelProperty.getAnnotation());

    return annotationsMap == null ? null : new Annotations(annotationsMap);
  }

  @Nullable
  private static OpInputModelProjection<?, ?> parseModelMetaProjection(
      @NotNull DatumType type,
      @NotNull List<IdlOpInputModelProperty> modelProperties,
      @NotNull TypesResolver resolver
  ) throws PsiProcessingException {

    @Nullable IdlOpInputModelMeta modelMetaPsi = null;

    for (IdlOpInputModelProperty modelProperty : modelProperties) {
      if (modelMetaPsi != null)
        throw new PsiProcessingException("Metadata projection should only be specified once", modelProperty);

      modelMetaPsi = modelProperty.getOpInputModelMeta();
    }

    if (modelMetaPsi != null) {
      @Nullable DatumType metaType = null; // TODO need a way to extract it from 'type'
      if (metaType == null) throw new PsiProcessingException(
          String.format("Type '%s' doesn't have a metadata, metadata projection can't be specified", type.name()),
          modelMetaPsi
      );

      @NotNull IdlOpInputComaModelProjection metaProjectionPsi = modelMetaPsi.getOpInputComaModelProjection();
      return parseComaModelProjection(
          metaType,
          modelMetaPsi.getPlus() != null,
          null, // TODO do we want to specify defaults for meta?
          null,
          null, // TODO what if meta-type has it's own meta-type? meta-meta-type projection should go here
          metaProjectionPsi,
          resolver
      ).projection();
    } else return null;
  }

  @NotNull
  private static OpInputVarProjection buildTailProjection(@NotNull DataType dataType,
                                                          @NotNull IdlTypeRef tailTypeRefPsi,
                                                          IdlOpInputComaVarProjection psiTailProjection,
                                                          @NotNull TypesResolver typesResolver,
                                                          PsiElement locationPsi)
      throws PsiProcessingException {

    @NotNull TypeRef tailTypeRef = TypeRefs.fromPsi(tailTypeRefPsi);
    @NotNull Type tailType = getType(tailTypeRef, typesResolver, locationPsi);
    return parseComaVarProjection(
        new DataType(tailType, dataType.defaultTag),
        psiTailProjection,
        typesResolver
    ).projection();
  }

  @NotNull
  private static OpInputVarProjection createDefaultVarProjection(
      @NotNull Type type,
      @NotNull Type.Tag tag,
      boolean required,
      @NotNull PsiElement locationPsi) throws PsiProcessingException {
    return new OpInputVarProjection(
        type,
        ProjectionUtils.singletonLinkedHashMap(
            tag,
            new OpInputTagProjection(
                createDefaultModelProjection(tag.type, required, null, null, locationPsi, null),
                EpigraphPsiUtil.getLocation(locationPsi)
            )
        ),
        null,
        EpigraphPsiUtil.getLocation(locationPsi)
    );
  }

  @NotNull
  private static OpInputVarProjection createDefaultVarProjection(@NotNull DatumType type,
                                                                 boolean required,
                                                                 @NotNull PsiElement locationPsi)
      throws PsiProcessingException {
    return createDefaultVarProjection(type, type.self, required, locationPsi);
  }

  @NotNull
  private static OpInputVarProjection createDefaultVarProjection(@NotNull DataType type,
                                                                 boolean required,
                                                                 @NotNull PsiElement locationPsi)
      throws PsiProcessingException {

    @Nullable Type.Tag defaultTag = type.defaultTag;
    if (defaultTag == null)
      throw new PsiProcessingException(
          String.format("Can't build default projection for '%s', default tag not specified", type.name), locationPsi
      );

    return createDefaultVarProjection(type.type, defaultTag, required, locationPsi);
  }

  @NotNull
  public static StepsAndProjection<? extends OpInputModelProjection<?, ?>> parseTrunkModelProjection(
      @NotNull DatumType type,
      boolean required,
      @Nullable GDatum defaultValue,
      @Nullable Annotations annotations,
      @Nullable OpInputModelProjection<?, ?> metaProjection,
      @NotNull IdlOpInputTrunkModelProjection psi,
      @NotNull TypesResolver typesResolver) throws PsiProcessingException {

    if (type.kind() == TypeKind.RECORD) {
      @Nullable IdlOpInputTrunkRecordModelProjection trunkRecordModelProjectionPsi =
          psi.getOpInputTrunkRecordModelProjection();

      if (trunkRecordModelProjectionPsi != null) {

        GRecordDatum defaultRecordData = coerceDefault(defaultValue, GRecordDatum.class, psi);

        return parseTrunkRecordModelProjection(
            (RecordType) type,
            required,
            defaultRecordData,
            annotations,
            metaProjection,
            trunkRecordModelProjectionPsi,
            typesResolver
        );
      }
    }

    // else end of path

    return parseComaModelProjection(
        type,
        required,
        defaultValue,
        annotations,
        metaProjection,
        psi,
        typesResolver
    );
  }

  @NotNull
  public static StepsAndProjection<? extends OpInputModelProjection<?, ?>> parseComaModelProjection(
      @NotNull DatumType type,
      boolean required,
      @Nullable GDatum defaultValue,
      @Nullable Annotations annotations,
      @Nullable OpInputModelProjection<?, ?> metaProjection,
      @NotNull IdlOpInputComaModelProjection psi,
      @NotNull TypesResolver typesResolver) throws PsiProcessingException {

    switch (type.kind()) {
      case RECORD:
        @Nullable IdlOpInputComaRecordModelProjection recordModelProjectionPsi =
            psi.getOpInputComaRecordModelProjection();

        if (recordModelProjectionPsi == null)
          return new StepsAndProjection<>(
              0,
              createDefaultModelProjection(type, required, defaultValue, annotations, psi, typesResolver)
          );

        ensureModelKind(psi, TypeKind.RECORD);
        GRecordDatum defaultRecordData = coerceDefault(defaultValue, GRecordDatum.class, psi);

        return parseComaRecordModelProjection(
            (RecordType) type,
            required,
            defaultRecordData,
            annotations,
            metaProjection,
            recordModelProjectionPsi,
            typesResolver
        );

      case MAP:
        @Nullable IdlOpInputComaMapModelProjection mapModelProjectionPsi = psi.getOpInputComaMapModelProjection();

        if (mapModelProjectionPsi == null)
          return new StepsAndProjection<>(
              0,
              createDefaultModelProjection(type, required, defaultValue, annotations, psi, typesResolver)
          );

        ensureModelKind(psi, TypeKind.MAP);
        GMapDatum defaultMapData = coerceDefault(defaultValue, GMapDatum.class, psi);

        return parseMapModelProjection(
            (MapType) type,
            required,
            defaultMapData,
            annotations,
            metaProjection,
            mapModelProjectionPsi,
            typesResolver
        );

      case LIST:
        @Nullable IdlOpInputComaListModelProjection listModelProjectionPsi = psi.getOpInputComaListModelProjection();

        if (listModelProjectionPsi == null)
          return new StepsAndProjection<>(
              0,
              createDefaultModelProjection(type, required, defaultValue, annotations, psi, typesResolver)
          );

        ensureModelKind(psi, TypeKind.LIST);
        GListDatum defaultListData = coerceDefault(defaultValue, GListDatum.class, psi);

        return parseListModelProjection(
            (ListType) type,
            required,
            defaultListData,
            annotations,
            metaProjection,
            listModelProjectionPsi,
            typesResolver
        );

      case ENUM:
        throw new PsiProcessingException("Unsupported type kind: " + type.kind(), psi);

      case PRIMITIVE:
        GPrimitiveDatum defaultPrimitiveData = coerceDefault(defaultValue, GPrimitiveDatum.class, psi);

        return parsePrimitiveModelProjection(
            (PrimitiveType) type,
            required,
            defaultPrimitiveData,
            annotations,
            metaProjection,
            psi,
            typesResolver
        );

      case UNION:
        throw new PsiProcessingException("Unsupported type kind: " + type.kind(), psi);

      default:
        throw new PsiProcessingException("Unknown type kind: " + type.kind(), psi);
    }
  }

  private static void ensureModelKind(@NotNull IdlOpInputComaModelProjection psi, @NotNull TypeKind expectedKind)
      throws PsiProcessingException {

    @Nullable TypeKind actualKind = findProjectionKind(psi);
    if (!expectedKind.equals(actualKind))
      throw new PsiProcessingException(MessageFormat.format("Unexpected projection kind ''{0}'', expected ''{1}''",
                                                            actualKind,
                                                            expectedKind
      ), psi);
  }

  @Nullable
  private static TypeKind findProjectionKind(@NotNull IdlOpInputComaModelProjection psi) {
    if (psi.getOpInputComaRecordModelProjection() != null) return TypeKind.RECORD;
    if (psi.getOpInputComaMapModelProjection() != null) return TypeKind.MAP;
    if (psi.getOpInputComaListModelProjection() != null) return TypeKind.LIST;
    return null;
  }

  @NotNull
  private static OpInputModelProjection<?, ?> createDefaultModelProjection(@NotNull DatumType type,
                                                                           boolean required,
                                                                           @Nullable GDatum defaultValue,
                                                                           @Nullable Annotations annotations,
                                                                           @NotNull PsiElement locationPsi,
                                                                           @Nullable TypesResolver resolver)
      throws PsiProcessingException {

    @NotNull TextLocation location = EpigraphPsiUtil.getLocation(locationPsi);

    @Nullable Datum defaultDatum = null;
    if (defaultValue != null)
      try {
        assert resolver != null;
        defaultDatum = GDataToData.transformDatum(type, defaultValue, resolver);
      } catch (GDataToData.ProcessingException e) {
        throw new PsiProcessingException(e, locationPsi);
      }

    switch (type.kind()) {
      case RECORD:
        return new OpInputRecordModelProjection((RecordType) type,
                                                required,
                                                (RecordDatum) defaultDatum,
                                                annotations,
                                                null,
                                                null,
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
          ), locationPsi);

        final OpInputVarProjection valueVarProjection = createDefaultVarProjection(
            valueType.type,
            defaultValuesTag,
            required,
            locationPsi
        );

        return new OpInputMapModelProjection(mapType,
                                             required,
                                             (MapDatum) defaultDatum,
                                             annotations,
                                             null,
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
          ), locationPsi);

        final OpInputVarProjection itemVarProjection = createDefaultVarProjection(
            elementType.type,
            defaultElementsTag,
            required,
            locationPsi
        );

        return new OpInputListModelProjection(listType,
                                              required,
                                              (ListDatum) defaultDatum,
                                              annotations,
                                              null,
                                              itemVarProjection,
                                              location
        );
      case UNION:
        throw new PsiProcessingException("Was expecting to get datum model kind, got: " + type.kind(), locationPsi);
      case ENUM:
        // todo
        throw new PsiProcessingException("Unsupported type kind: " + type.kind(), locationPsi);
      case PRIMITIVE:
        return new OpInputPrimitiveModelProjection((PrimitiveType) type,
                                                   required,
                                                   (PrimitiveDatum<?>) defaultDatum,
                                                   annotations,
                                                   null,
                                                   location
        );
      default:
        throw new PsiProcessingException("Unknown type kind: " + type.kind(), locationPsi);
    }
  }

  @NotNull
  public static StepsAndProjection<OpInputRecordModelProjection> parseTrunkRecordModelProjection(
      @NotNull RecordType type,
      boolean required,
      @Nullable GRecordDatum defaultValue,
      @Nullable Annotations annotations,
      @Nullable OpInputModelProjection<?, ?> metaProjection,
      @NotNull IdlOpInputTrunkRecordModelProjection psi,
      @NotNull TypesResolver resolver) throws PsiProcessingException {

    RecordDatum defaultDatum = null;
    if (defaultValue != null) {
      try {
        defaultDatum = GDataToData.transform(type, defaultValue, resolver);
      } catch (GDataToData.ProcessingException e) {
        throw new PsiProcessingException(e, psi);
      }
    }

    LinkedHashMap<RecordType.Field, OpInputFieldProjection> fieldProjections = new LinkedHashMap<>();
    @Nullable IdlOpInputTrunkFieldProjection fieldProjectionPsi = psi.getOpInputTrunkFieldProjection();

    if (fieldProjectionPsi == null)
      throw new PsiProcessingException("Field not specified", psi);

    final String fieldName = fieldProjectionPsi.getQid().getCanonicalName();
    RecordType.Field field = type.fieldsMap().get(fieldName);
    if (field == null)
      throw new PsiProcessingException(
          String.format("Can't field projection for '%s', field '%s' not found", type.name(), fieldName),
          fieldProjectionPsi
      );

    final boolean fieldRequired = fieldProjectionPsi.getPlus() != null;

    Annotations fieldAnnotations;
    @Nullable Map<String, Annotation> fieldAnnotationsMap = null;
    for (IdlOpInputFieldProjectionBodyPart fieldBodyPart : fieldProjectionPsi.getOpInputFieldProjectionBodyPartList()) {
      fieldAnnotationsMap = parseAnnotation(fieldAnnotationsMap, fieldBodyPart.getAnnotation());
    }
    fieldAnnotations = fieldAnnotationsMap == null ? null : new Annotations(fieldAnnotationsMap);

    final int steps;

    OpInputVarProjection varProjection;
    @Nullable IdlOpInputTrunkVarProjection psiVarProjection = fieldProjectionPsi.getOpInputTrunkVarProjection();
    if (psiVarProjection == null) {
      @NotNull DataType fieldDataType = field.dataType();
      @Nullable Type.Tag defaultFieldTag = fieldDataType.defaultTag;
      if (defaultFieldTag == null)
        throw new PsiProcessingException(String.format(
            "Can't construct default projection for field '%s', as it's type '%s' has no default tag",
            fieldName,
            fieldDataType.name
        ), fieldProjectionPsi);

      varProjection =
          createDefaultVarProjection(fieldDataType.type, defaultFieldTag, required, fieldProjectionPsi);
      steps =
          2; // first step = our field, second step = default var. default var projection is a trunk projection, default model projection is a coma projection
    } else {
      StepsAndProjection<OpInputVarProjection> stepsAndProjection =
          parseTrunkVarProjection(field.dataType(), psiVarProjection, resolver);

      varProjection = stepsAndProjection.projection();
      steps = stepsAndProjection.pathSteps() + 1;
    }

    fieldProjections.put(
        field,
        new OpInputFieldProjection(
            fieldAnnotations,
            varProjection,
            fieldRequired,
            EpigraphPsiUtil.getLocation(fieldProjectionPsi)
        )
    );

    return new StepsAndProjection<>(
        steps,
        new OpInputRecordModelProjection(
            type,
            required,
            defaultDatum,
            annotations,
            metaProjection,
            fieldProjections,
            EpigraphPsiUtil.getLocation(psi)
        )
    );
  }

  @NotNull
  public static StepsAndProjection<OpInputRecordModelProjection> parseComaRecordModelProjection(
      @NotNull RecordType type,
      boolean required,
      @Nullable GRecordDatum defaultValue,
      @Nullable Annotations annotations,
      @Nullable OpInputModelProjection<?, ?> metaProjection,
      @NotNull IdlOpInputComaRecordModelProjection psi,
      @NotNull TypesResolver resolver) throws PsiProcessingException {

    RecordDatum defaultDatum = null;
    if (defaultValue != null) {
      try {
        defaultDatum = GDataToData.transform(type, defaultValue, resolver);
      } catch (GDataToData.ProcessingException e) {
        throw new PsiProcessingException(e, psi);
      }
    }

    LinkedHashMap<RecordType.Field, OpInputFieldProjection> fieldProjections = new LinkedHashMap<>();
    @NotNull List<IdlOpInputComaFieldProjection> psiFieldProjections = psi.getOpInputComaFieldProjectionList();

    for (IdlOpInputComaFieldProjection fieldProjectionPsi : psiFieldProjections) {
      final String fieldName = fieldProjectionPsi.getQid().getCanonicalName();
      RecordType.Field field = type.fieldsMap().get(fieldName);
      if (field == null)
        throw new PsiProcessingException(
            String.format("Can't field projection for '%s', field '%s' not found", type.name(), fieldName),
            fieldProjectionPsi
        );

      final boolean fieldRequired = fieldProjectionPsi.getPlus() != null;

      Annotations fieldAnnotations;
      @Nullable Map<String, Annotation> fieldAnnotationsMap = null;
      for (IdlOpInputFieldProjectionBodyPart fieldBodyPart : fieldProjectionPsi.getOpInputFieldProjectionBodyPartList()) {
        fieldAnnotationsMap = parseAnnotation(fieldAnnotationsMap, fieldBodyPart.getAnnotation());
      }
      fieldAnnotations = fieldAnnotationsMap == null ? null : new Annotations(fieldAnnotationsMap);

      OpInputVarProjection varProjection;
      @Nullable IdlOpInputComaVarProjection psiVarProjection = fieldProjectionPsi.getOpInputComaVarProjection();
      if (psiVarProjection == null) {
        @NotNull DataType fieldDataType = field.dataType();
        @Nullable Type.Tag defaultFieldTag = fieldDataType.defaultTag;
        if (defaultFieldTag == null)
          throw new PsiProcessingException(String.format(
              "Can't construct default projection for field '%s', as it's type '%s' has no default tag",
              fieldName,
              fieldDataType.name
          ), fieldProjectionPsi);

        varProjection =
            createDefaultVarProjection(fieldDataType.type, defaultFieldTag, required, fieldProjectionPsi);
      } else {
        varProjection = parseComaVarProjection(field.dataType(), psiVarProjection, resolver).projection();
      }

      fieldProjections.put(
          field,
          new OpInputFieldProjection(
              fieldAnnotations,
              varProjection,
              fieldRequired,
              EpigraphPsiUtil.getLocation(fieldProjectionPsi)
          )
      );
    }

    return new StepsAndProjection<>(
        0,
        new OpInputRecordModelProjection(
            type,
            required,
            defaultDatum,
            annotations,
            metaProjection,
            fieldProjections,
            EpigraphPsiUtil.getLocation(psi)
        )
    );
  }

  @NotNull
  public static StepsAndProjection<OpInputMapModelProjection> parseMapModelProjection(
      @NotNull MapType type,
      boolean required,
      @Nullable GMapDatum defaultValue,
      @Nullable Annotations annotations,
      @Nullable OpInputModelProjection<?, ?> metaProjection,
      @NotNull IdlOpInputComaMapModelProjection psi,
      @NotNull TypesResolver resolver) throws PsiProcessingException {

    MapDatum defaultDatum = null;
    if (defaultValue != null) {
      try {
        defaultDatum = GDataToData.transform(type, defaultValue, resolver);
      } catch (GDataToData.ProcessingException e) {
        throw new PsiProcessingException(e, psi);
      }
    }

    @Nullable IdlOpInputComaVarProjection valueProjectionPsi = psi.getOpInputComaVarProjection();
    @NotNull OpInputVarProjection valueProjection =
        parseComaVarProjection(type.valueType(), valueProjectionPsi, resolver).projection();

    return new StepsAndProjection<>(
        0,
        new OpInputMapModelProjection(
            type,
            required,
            defaultDatum,
            annotations,
            metaProjection,
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
      @Nullable Annotations annotations,
      @Nullable OpInputModelProjection<?, ?> metaProjection,
      @NotNull IdlOpInputComaListModelProjection psi,
      @NotNull TypesResolver resolver) throws PsiProcessingException {

    ListDatum defaultDatum = null;
    if (defaultValue != null) {
      try {
        defaultDatum = GDataToData.transform(type, defaultValue, resolver);
      } catch (GDataToData.ProcessingException e) {
        throw new PsiProcessingException(e, psi);
      }
    }

    OpInputVarProjection itemsProjection;
    @Nullable IdlOpInputComaVarProjection opInputVarProjectionPsi = psi.getOpInputComaVarProjection();
    if (opInputVarProjectionPsi == null)
      itemsProjection = createDefaultVarProjection(type, true, psi);
    else
      itemsProjection = parseComaVarProjection(type.elementType(), opInputVarProjectionPsi, resolver).projection();


    return new StepsAndProjection<>(
        0,
        new OpInputListModelProjection(
            type,
            required,
            defaultDatum,
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
      @Nullable Annotations annotations,
      @Nullable OpInputModelProjection<?, ?> metaProjection,
      @NotNull PsiElement locationPsi,
      @NotNull TypesResolver resolver) throws PsiProcessingException {

    PrimitiveDatum<?> defaultDatum = null;
    if (defaultValue != null) {
      try {
        defaultDatum = GDataToData.transform(type, defaultValue, resolver);
      } catch (GDataToData.ProcessingException e) {
        throw new PsiProcessingException(e, locationPsi);
      }
    }

    return new StepsAndProjection<>(
        0,
        new OpInputPrimitiveModelProjection(
            type,
            required,
            defaultDatum,
            annotations,
            metaProjection,
            EpigraphPsiUtil.getLocation(locationPsi)
        )
    );
  }

  @Nullable
  private static <D extends GDatum> D coerceDefault(@Nullable GDatum defaultValue,
                                                    Class<D> cls,
                                                    @NotNull PsiElement location)
      throws PsiProcessingException {

    if (defaultValue == null) return null;
    if (defaultValue instanceof GNullDatum) return null;
    if (defaultValue.getClass().equals(cls)) //noinspection unchecked
      return (D) defaultValue;
    throw new PsiProcessingException(
        String.format("Invalid default value '%s', expected to get '%s'", defaultValue, cls.getName()),
        location
    );
  }
}
