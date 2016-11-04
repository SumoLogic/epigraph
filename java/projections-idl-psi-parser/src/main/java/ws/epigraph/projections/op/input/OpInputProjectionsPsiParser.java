package ws.epigraph.projections.op.input;

import com.intellij.psi.PsiElement;
import ws.epigraph.data.*;
import ws.epigraph.gdata.*;
import ws.epigraph.idl.TypeRefs;
import ws.epigraph.idl.gdata.IdlGDataPsiParser;
import ws.epigraph.idl.parser.psi.*;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.Annotation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.ProjectionUtils;
import ws.epigraph.projections.StepsAndProjection;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.TypeRef;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.types.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.*;

import static ws.epigraph.projections.ProjectionPsiParserUtil.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpInputProjectionsPsiParser {

  public static StepsAndProjection<OpInputVarProjection> parseVarProjection(
      @NotNull DataType dataType,
      @NotNull IdlOpInputVarProjection psi,
      @NotNull TypesResolver typesResolver) throws PsiProcessingException {

    final Type type = dataType.type;
    final LinkedHashMap<String, OpInputTagProjectionEntry> tagProjections;

    @Nullable IdlOpInputSingleTagProjection singleTagProjectionPsi = psi.getOpInputSingleTagProjection();
    if (singleTagProjectionPsi != null) {
      tagProjections = new LinkedHashMap<>();
      final OpInputModelProjection<?, ?, ?> parsedModelProjection;
      final Type.Tag tag = getTag(
          type,
          singleTagProjectionPsi.getTagName(),
          dataType.defaultTag,
          singleTagProjectionPsi
      );

      @Nullable IdlOpInputModelProjection modelProjection = singleTagProjectionPsi.getOpInputModelProjection();
      assert modelProjection != null; // todo when it can be null?

      @NotNull List<IdlOpInputModelProperty> modelPropertiesPsi = singleTagProjectionPsi.getOpInputModelPropertyList();
      parsedModelProjection = parseModelProjection(
          tag.type,
          singleTagProjectionPsi.getPlus() != null,
          getModelDefaultValue(modelPropertiesPsi),
          parseModelAnnotations(modelPropertiesPsi),
          parseModelMetaProjection(tag.type, modelPropertiesPsi, typesResolver),
          modelProjection, typesResolver
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
      tagProjections = parseMultiTagProjection(dataType, multiTagProjection, typesResolver);
    }

    final List<OpInputVarProjection> tails =
        parseTails(dataType, psi.getOpInputVarPolymorphicTail(), typesResolver);

    return new StepsAndProjection<>(
        0,
        new OpInputVarProjection(type, tagProjections, tails, EpigraphPsiUtil.getLocation(psi))
    );
  }

  @NotNull
  private static LinkedHashMap<String, OpInputTagProjectionEntry> parseMultiTagProjection(
      @NotNull DataType dataType,
      @NotNull IdlOpInputMultiTagProjection multiTagProjection,
      @NotNull TypesResolver typesResolver) throws PsiProcessingException {

    final LinkedHashMap<String, OpInputTagProjectionEntry> tagProjections = new LinkedHashMap<>();

    // parse list of tags
    @NotNull List<IdlOpInputMultiTagProjectionItem> tagProjectionPsiList =
        multiTagProjection.getOpInputMultiTagProjectionItemList();

    for (IdlOpInputMultiTagProjectionItem tagProjectionPsi : tagProjectionPsiList) {
      final Type.Tag tag =
          getTag(dataType.type, tagProjectionPsi.getTagName(), dataType.defaultTag, tagProjectionPsi);

      final OpInputModelProjection<?, ?, ?> parsedModelProjection;

      @NotNull DatumType tagType = tag.type;
      @Nullable IdlOpInputModelProjection modelProjection = tagProjectionPsi.getOpInputModelProjection();
      assert modelProjection != null; // todo when it can be null?
      @NotNull List<IdlOpInputModelProperty> modelPropertiesPsi = tagProjectionPsi.getOpInputModelPropertyList();

      parsedModelProjection = parseModelProjection(
          tagType,
          tagProjectionPsi.getPlus() != null,
          getModelDefaultValue(modelPropertiesPsi),
          parseModelAnnotations(modelPropertiesPsi),
          parseModelMetaProjection(tagType, modelPropertiesPsi, typesResolver),
          modelProjection, typesResolver
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
      @NotNull TypesResolver typesResolver) throws PsiProcessingException {

    final List<OpInputVarProjection> tails;

    if (tailPsi != null) {
      tails = new ArrayList<>();

      @Nullable IdlOpInputVarSingleTail singleTail = tailPsi.getOpInputVarSingleTail();
      if (singleTail != null) {
        @NotNull IdlTypeRef typeRefPsi = singleTail.getTypeRef();
        @NotNull IdlOpInputVarProjection psiTailProjection = singleTail.getOpInputVarProjection();
        @NotNull OpInputVarProjection tailProjection =
            buildTailProjection(dataType, typeRefPsi, psiTailProjection, typesResolver);
        tails.add(tailProjection);
      } else {
        @Nullable IdlOpInputVarMultiTail multiTail = tailPsi.getOpInputVarMultiTail();
        assert multiTail != null;
        for (IdlOpInputVarMultiTailItem tailItem : multiTail.getOpInputVarMultiTailItemList()) {
          @NotNull IdlTypeRef tailTypeRef = tailItem.getTypeRef();
          @NotNull IdlOpInputVarProjection psiTailProjection = tailItem.getOpInputVarProjection();
          @NotNull OpInputVarProjection tailProjection =
              buildTailProjection(dataType, tailTypeRef, psiTailProjection, typesResolver);
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

  @NotNull
  private static Annotations parseModelAnnotations(@NotNull List<IdlOpInputModelProperty> modelProperties)
      throws PsiProcessingException {

    @Nullable Map<String, Annotation> annotationsMap = null;

    for (IdlOpInputModelProperty modelProperty : modelProperties)
      annotationsMap = parseAnnotation(annotationsMap, modelProperty.getAnnotation());

    return annotationsMap == null ? Annotations.EMPTY : new Annotations(annotationsMap);
  }

  @Nullable
  private static OpInputModelProjection<?, ?, ?> parseModelMetaProjection(
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

      @NotNull IdlOpInputModelProjection metaProjectionPsi = modelMetaPsi.getOpInputModelProjection();
      return parseModelProjection(
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
  private static OpInputVarProjection buildTailProjection(
      @NotNull DataType dataType,
      @NotNull IdlTypeRef tailTypeRefPsi,
      IdlOpInputVarProjection psiTailProjection,
      @NotNull TypesResolver typesResolver) throws PsiProcessingException {

    @NotNull TypeRef tailTypeRef = TypeRefs.fromPsi(tailTypeRefPsi);
    @NotNull Type tailType = getType(tailTypeRef, typesResolver, tailTypeRefPsi);
    return parseVarProjection(
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
            tag.name(),
            new OpInputTagProjectionEntry(
                tag,
                createDefaultModelProjection(tag.type, required, null, Annotations.EMPTY, locationPsi, null),
                EpigraphPsiUtil.getLocation(locationPsi)
            )
        ),
        null,
        EpigraphPsiUtil.getLocation(locationPsi)
    );
  }

  @NotNull
  private static OpInputVarProjection createDefaultVarProjection(
      @NotNull DatumType type,
      boolean required,
      @NotNull PsiElement locationPsi)
      throws PsiProcessingException {
    return createDefaultVarProjection(type, type.self, required, locationPsi);
  }

  @NotNull
  private static OpInputVarProjection createDefaultVarProjection(
      @NotNull DataType type,
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
  public static StepsAndProjection<? extends OpInputModelProjection<?, ?, ?>> parseModelProjection(
      @NotNull DatumType type,
      boolean required,
      @Nullable GDatum defaultValue,
      @NotNull Annotations annotations,
      @Nullable OpInputModelProjection<?, ?, ?> metaProjection,
      @NotNull IdlOpInputModelProjection psi,
      @NotNull TypesResolver typesResolver) throws PsiProcessingException {

    switch (type.kind()) {
      case RECORD:
        @Nullable IdlOpInputRecordModelProjection recordModelProjectionPsi =
            psi.getOpInputRecordModelProjection();

        if (recordModelProjectionPsi == null)
          return new StepsAndProjection<>(
              0,
              createDefaultModelProjection(type, required, defaultValue, annotations, psi, typesResolver)
          );

        ensureModelKind(psi, TypeKind.RECORD);
        GRecordDatum defaultRecordData = coerceDefault(defaultValue, GRecordDatum.class, psi);

        return parseRecordModelProjection(
            (RecordType) type,
            required,
            defaultRecordData,
            annotations,
            (OpInputRecordModelProjection) metaProjection,
            recordModelProjectionPsi,
            typesResolver
        );

      case MAP:
        @Nullable IdlOpInputMapModelProjection mapModelProjectionPsi = psi.getOpInputMapModelProjection();

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
            (OpInputMapModelProjection) metaProjection,
            mapModelProjectionPsi,
            typesResolver
        );

      case LIST:
        @Nullable IdlOpInputListModelProjection listModelProjectionPsi = psi.getOpInputListModelProjection();

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
            (OpInputListModelProjection) metaProjection,
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
            (OpInputPrimitiveModelProjection) metaProjection,
            psi,
            typesResolver
        );

      case UNION:
        throw new PsiProcessingException("Unsupported type kind: " + type.kind(), psi);

      default:
        throw new PsiProcessingException("Unknown type kind: " + type.kind(), psi);
    }
  }

  private static void ensureModelKind(@NotNull IdlOpInputModelProjection psi, @NotNull TypeKind expectedKind)
      throws PsiProcessingException {

    @Nullable TypeKind actualKind = findProjectionKind(psi);
    if (!expectedKind.equals(actualKind))
      throw new PsiProcessingException(MessageFormat.format(
          "Unexpected projection kind ''{0}'', expected ''{1}''",
          actualKind,
          expectedKind
      ), psi);
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
      @NotNull Annotations annotations,
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
        return new OpInputRecordModelProjection(
            (RecordType) type,
            required,
            (RecordDatum) defaultDatum,
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
          ), locationPsi);

        final OpInputVarProjection valueVarProjection = createDefaultVarProjection(
            valueType.type,
            defaultValuesTag,
            required,
            locationPsi
        );

        return new OpInputMapModelProjection(
            mapType,
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

        return new OpInputListModelProjection(
            listType,
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
        return new OpInputPrimitiveModelProjection(
            (PrimitiveType) type,
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
  public static StepsAndProjection<OpInputRecordModelProjection> parseRecordModelProjection(
      @NotNull RecordType type,
      boolean required,
      @Nullable GRecordDatum defaultValue,
      @NotNull Annotations annotations,
      @Nullable OpInputRecordModelProjection metaProjection,
      @NotNull IdlOpInputRecordModelProjection psi,
      @NotNull TypesResolver resolver) throws PsiProcessingException {

    RecordDatum defaultDatum = null;
    if (defaultValue != null) {
      try {
        defaultDatum = GDataToData.transform(type, defaultValue, resolver);
      } catch (GDataToData.ProcessingException e) {
        throw new PsiProcessingException(e, psi);
      }
    }

    LinkedHashMap<String, OpInputFieldProjectionEntry> fieldProjections = new LinkedHashMap<>();
    @NotNull List<IdlOpInputFieldProjection> psiFieldProjections = psi.getOpInputFieldProjectionList();

    for (IdlOpInputFieldProjection fieldProjectionPsi : psiFieldProjections) {
      final String fieldName = fieldProjectionPsi.getQid().getCanonicalName();
      RecordType.Field field = type.fieldsMap().get(fieldName);
      if (field == null)
        throw new PsiProcessingException(
            String.format("Can't build field projection for '%s', field '%s' not found", type.name(), fieldName),
            fieldProjectionPsi
        );

      final boolean fieldRequired = fieldProjectionPsi.getPlus() != null;

      @NotNull Annotations fieldAnnotations;
      @Nullable Map<String, Annotation> fieldAnnotationsMap = null;
      for (IdlOpInputFieldProjectionBodyPart fieldBodyPart : fieldProjectionPsi.getOpInputFieldProjectionBodyPartList()) {
        fieldAnnotationsMap = parseAnnotation(fieldAnnotationsMap, fieldBodyPart.getAnnotation());
      }
      fieldAnnotations = fieldAnnotationsMap == null ? Annotations.EMPTY : new Annotations(fieldAnnotationsMap);

      OpInputVarProjection varProjection;
      @Nullable IdlOpInputVarProjection psiVarProjection = fieldProjectionPsi.getOpInputVarProjection();
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
        varProjection = parseVarProjection(field.dataType(), psiVarProjection, resolver).projection();
      }

      @NotNull final TextLocation fieldLocation = EpigraphPsiUtil.getLocation(fieldProjectionPsi);

      fieldProjections.put(
          fieldName,
          new OpInputFieldProjectionEntry(
              field,
              new OpInputFieldProjection(
                  fieldAnnotations,
                  varProjection,
                  fieldRequired,
                  fieldLocation
              )
              ,
              fieldLocation
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
      @NotNull Annotations annotations,
      @Nullable OpInputMapModelProjection metaProjection,
      @NotNull IdlOpInputMapModelProjection psi,
      @NotNull TypesResolver resolver) throws PsiProcessingException {

    MapDatum defaultDatum = null;
    if (defaultValue != null) {
      try {
        defaultDatum = GDataToData.transform(type, defaultValue, resolver);
      } catch (GDataToData.ProcessingException e) {
        throw new PsiProcessingException(e, psi);
      }
    }

    @Nullable IdlOpInputVarProjection valueProjectionPsi = psi.getOpInputVarProjection();
    @NotNull OpInputVarProjection valueProjection =
        valueProjectionPsi == null ?
        createDefaultVarProjection(type.valueType(), false, psi) :
        parseVarProjection(type.valueType(), valueProjectionPsi, resolver).projection();

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
      @NotNull Annotations annotations,
      @Nullable OpInputListModelProjection metaProjection,
      @NotNull IdlOpInputListModelProjection psi,
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
    @Nullable IdlOpInputVarProjection opInputVarProjectionPsi = psi.getOpInputVarProjection();
    if (opInputVarProjectionPsi == null)
      itemsProjection = createDefaultVarProjection(type, true, psi);
    else
      itemsProjection = parseVarProjection(type.elementType(), opInputVarProjectionPsi, resolver).projection();


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
      @NotNull Annotations annotations,
      @Nullable OpInputPrimitiveModelProjection metaProjection,
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

  @SuppressWarnings("unchecked")
  @Nullable
  private static <D extends GDatum> D coerceDefault(
      @Nullable GDatum defaultValue,
      Class<D> cls,
      @NotNull PsiElement location)
      throws PsiProcessingException {

    if (defaultValue == null) return null;
    if (defaultValue instanceof GNullDatum) return null;
    if (defaultValue.getClass().equals(cls))
      return (D) defaultValue;
    throw new PsiProcessingException(
        String.format("Invalid default value '%s', expected to get '%s'", defaultValue, cls.getName()),
        location
    );
  }
}
