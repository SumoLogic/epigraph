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

package ws.epigraph.url.projections.req.update;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.data.Datum;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.op.input.*;
import ws.epigraph.projections.req.ReqParams;
import ws.epigraph.projections.req.update.*;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingError;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.TypeRef;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.types.*;
import ws.epigraph.url.TypeRefs;
import ws.epigraph.url.parser.psi.*;

import java.util.*;

import static ws.epigraph.projections.ProjectionsParsingUtil.*;
import static ws.epigraph.url.projections.UrlProjectionsPsiParserUtil.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqUpdateProjectionsPsiParser {

  @NotNull
  public static ReqUpdateVarProjection parseVarProjection(
      @NotNull DataType dataType,
      @NotNull OpInputVarProjection op,
      @NotNull UrlReqUpdateVarProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {


    final Type type = dataType.type;
    final LinkedHashMap<String, ReqUpdateTagProjectionEntry> tagProjections;

    @Nullable UrlReqUpdateSingleTagProjection singleTagProjectionPsi = psi.getReqUpdateSingleTagProjection();
    @NotNull final TypesResolver subResolver = addTypeNamespace(dataType.type, resolver);

    if (singleTagProjectionPsi != null) {
      // try to improve error reporting: singleTagProjectionPsi may be empty
      PsiElement tagLocation = getSingleTagLocation(singleTagProjectionPsi);

      tagProjections = new LinkedHashMap<>();

      final ReqUpdateModelProjection<?, ?> parsedModelProjection;
      @Nullable final UrlTagName tagNamePsi = singleTagProjectionPsi.getTagName();

      @NotNull final Type.Tag tag;

      tag = findTagOrDefaultTag(type, tagNamePsi, op, tagLocation, errors);
      @NotNull OpInputTagProjectionEntry opTagProjection =
          findTagProjection(tag.name(), op, tagLocation, errors);

      @NotNull OpInputModelProjection<?, ?, ?> opModelProjection = opTagProjection.projection();
      @NotNull UrlReqUpdateModelProjection modelProjectionPsi = singleTagProjectionPsi.getReqUpdateModelProjection();

      parsedModelProjection = parseModelProjection(
          opModelProjection,
          singleTagProjectionPsi.getPlus() != null,
          parseReqParams(singleTagProjectionPsi.getReqParamList(), opModelProjection.params(), subResolver, errors),
          parseAnnotations(singleTagProjectionPsi.getReqAnnotationList()),
          modelProjectionPsi,
          subResolver,
          errors
      );

      tagProjections.put(
          tag.name(),
          new ReqUpdateTagProjectionEntry(
              tag,
              parsedModelProjection,
              EpigraphPsiUtil.getLocation(tagLocation)
          )
      );

    } else {
      @Nullable UrlReqUpdateMultiTagProjection multiTagProjection = psi.getReqUpdateMultiTagProjection();
      assert multiTagProjection != null;
      tagProjections = parseMultiTagProjection(dataType, op, multiTagProjection, subResolver, errors);
    }

    // check that all required tags are present
    for (final Map.Entry<String, OpInputTagProjectionEntry> entry : op.tagProjections().entrySet()) {
      if (entry.getValue().projection().required() && !tagProjections.containsKey(entry.getKey()))
        errors.add(
            new PsiProcessingError(String.format("Required tag '%s' is missing", entry.getKey()), psi)
        );
    }

    final List<ReqUpdateVarProjection> tails =
        parseTails(dataType, op, psi.getReqUpdateVarPolymorphicTail(), subResolver, errors);

    return
        new ReqUpdateVarProjection(type, tagProjections, tails, EpigraphPsiUtil.getLocation(psi));
  }

  @NotNull
  private static PsiElement getSingleTagLocation(@NotNull final UrlReqUpdateSingleTagProjection singleTagProjectionPsi) {
    PsiElement tagLocation = singleTagProjectionPsi;
    if (tagLocation.getText().length() == 0) {
      @Nullable final UrlReqUpdateFieldProjectionEntry fieldProjectionPsi =
          PsiTreeUtil.getParentOfType(tagLocation, UrlReqUpdateFieldProjectionEntry.class);
      if (fieldProjectionPsi != null) {
        tagLocation = fieldProjectionPsi.getQid();
      }
    }
    return tagLocation;
  }

  @NotNull
  private static LinkedHashMap<String, ReqUpdateTagProjectionEntry> parseMultiTagProjection(
      @NotNull DataType dataType,
      @NotNull OpInputVarProjection op,
      @NotNull UrlReqUpdateMultiTagProjection psi,
      @NotNull TypesResolver typesResolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    if (!(dataType.type.equals(op.type())))
      throw new PsiProcessingException(
          String.format("Inconsistent arguments. data type: '%s', op type: '%s'", dataType.name, op.type().name()),
          psi,
          errors
      );

    @NotNull final TypesResolver subResolver = addTypeNamespace(dataType.type, typesResolver);

    final LinkedHashMap<String, ReqUpdateTagProjectionEntry> tagProjections = new LinkedHashMap<>();

    // parse list of tags
    @NotNull List<UrlReqUpdateMultiTagProjectionItem> tagProjectionPsiList =
        psi.getReqUpdateMultiTagProjectionItemList();

    for (UrlReqUpdateMultiTagProjectionItem tagProjectionPsi : tagProjectionPsiList) {
      try {
        @NotNull Type.Tag tag = findTag(tagProjectionPsi.getTagName(), op, tagProjectionPsi, errors);
        @NotNull OpInputTagProjectionEntry opTag = findTagProjection(tag.name(), op, tagProjectionPsi, errors);

        OpInputModelProjection<?, ?, ?> opTagProjection = opTag.projection();

        final ReqUpdateModelProjection<?, ?> parsedModelProjection;

        @NotNull UrlReqUpdateModelProjection modelProjection = tagProjectionPsi.getReqUpdateModelProjection();

        parsedModelProjection = parseModelProjection(
            opTagProjection,
            tagProjectionPsi.getPlus() != null,
            parseReqParams(tagProjectionPsi.getReqParamList(), opTagProjection.params(), subResolver, errors),
            parseAnnotations(tagProjectionPsi.getReqAnnotationList()),
            modelProjection, subResolver, errors
        );

        tagProjections.put(
            tag.name(),
            new ReqUpdateTagProjectionEntry(
                tag,
                parsedModelProjection,
                EpigraphPsiUtil.getLocation(tagProjectionPsi)
            )
        );
      } catch (PsiProcessingException e) {
        errors.add(e.toError());
      }
    }

    return tagProjections;
  }

  @NotNull
  private static ReqUpdateVarProjection getDefaultVarProjection(
      @NotNull final Type type,
      final @NotNull PsiElement psi) {
    return new ReqUpdateVarProjection(
        type,
        Collections.emptyMap(),
        null,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  @Nullable
  private static List<ReqUpdateVarProjection> parseTails(
      @NotNull DataType dataType,
      @NotNull OpInputVarProjection op,
      @Nullable UrlReqUpdateVarPolymorphicTail tailPsi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    final List<ReqUpdateVarProjection> tails;

    @NotNull TypesResolver subResolver = addTypeNamespace(dataType.type, resolver);

    if (tailPsi != null) {

      tails = new ArrayList<>();

      @Nullable UrlReqUpdateVarSingleTail singleTail = tailPsi.getReqUpdateVarSingleTail();
      if (singleTail != null) {
        @NotNull UrlTypeRef tailTypeRef = singleTail.getTypeRef();
        @NotNull UrlReqUpdateVarProjection psiTailProjection = singleTail.getReqUpdateVarProjection();
        @NotNull ReqUpdateVarProjection tailProjection =
            buildTailProjection(dataType, op, tailTypeRef, psiTailProjection, subResolver, errors);
        tails.add(tailProjection);
      } else {
        @Nullable UrlReqUpdateVarMultiTail multiTail = tailPsi.getReqUpdateVarMultiTail();
        assert multiTail != null;
        Type prevTailType = null;

        for (UrlReqUpdateVarMultiTailItem tailItem : multiTail.getReqUpdateVarMultiTailItemList()) {
          try {
            if (prevTailType != null)
              subResolver = addTypeNamespace(prevTailType, resolver);

            @NotNull UrlTypeRef tailTypeRef = tailItem.getTypeRef();
            @NotNull UrlReqUpdateVarProjection psiTailProjection = tailItem.getReqUpdateVarProjection();
            @NotNull ReqUpdateVarProjection tailProjection =
                buildTailProjection(dataType, op, tailTypeRef, psiTailProjection, subResolver, errors);
            tails.add(tailProjection);

            prevTailType = tailProjection.type();
          } catch (PsiProcessingException e) {
            errors.add(e.toError());
          }
        }
      }

    } else tails = null;

    return tails;
  }

  @NotNull
  private static ReqUpdateVarProjection buildTailProjection(
      @NotNull DataType dataType,
      @NotNull OpInputVarProjection op,
      @NotNull UrlTypeRef tailTypeRefPsi,
      @NotNull UrlReqUpdateVarProjection tailProjectionPsi,
      @NotNull TypesResolver typesResolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    @NotNull TypeRef tailTypeRef = TypeRefs.fromPsi(tailTypeRefPsi);
    @NotNull Type tailType = getType(tailTypeRef, typesResolver, tailTypeRefPsi, errors);

    @Nullable OpInputVarProjection opTail = mergeOpTails(op, tailType);
    if (opTail == null)
      throw new PsiProcessingException(
          String.format("Polymorphic tail for type '%s' is not supported", tailType.name()),
          tailProjectionPsi,
          errors
      );

    return parseVarProjection(
        new DataType(tailType, dataType.defaultTag),
        opTail,
        tailProjectionPsi,
        typesResolver,
        errors
    );
  }

  @Nullable
  private static OpInputVarProjection mergeOpTails(@NotNull OpInputVarProjection op, @NotNull Type tailType) {
    List<OpInputVarProjection> opTails = op.polymorphicTails();
    if (opTails == null) return null;
    // TODO a deep merge of op projections wrt to tailType is needed here, probably moved into a separate class
    // we simply look for the first fully matching tail for now
    // algo should be: DFS on tails, look for exact match on tailType
    // if found: merge all op tails up the stack into one mega-op-var-projection: squash all tags/fields/params together. Should be OK since they all are supertypes of tailType
    // else null

    for (OpInputVarProjection opTail : opTails) {
      if (opTail.type().equals(tailType)) return opTail;
    }

    return null;
  }

  @NotNull
  public static ReqUpdateModelProjection<?, ?> parseModelProjection(
      @NotNull OpInputModelProjection<?, ?, ?> op,
      boolean update,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      @NotNull UrlReqUpdateModelProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    DatumType model = op.model();
    @NotNull final TypesResolver subResolver = addTypeNamespace(model, resolver);

    switch (model.kind()) {
      case RECORD:
        final OpInputRecordModelProjection opRecord = (OpInputRecordModelProjection) op;

        @Nullable UrlReqUpdateRecordModelProjection recordModelProjectionPsi =
            psi.getReqUpdateRecordModelProjection();

        if (recordModelProjectionPsi == null)
          return createDefaultModelProjection(model, update, opRecord, params, annotations, psi, errors);

        ensureModelKind(findProjectionKind(psi), TypeKind.RECORD, psi, errors);

        return parseRecordModelProjection(
            opRecord,
            update,
            params,
            annotations,
            recordModelProjectionPsi,
            subResolver,
            errors
        );

      case MAP:
        final OpInputMapModelProjection opMap = (OpInputMapModelProjection) op;
        @Nullable UrlReqUpdateMapModelProjection mapModelProjectionPsi = psi.getReqUpdateMapModelProjection();

        if (mapModelProjectionPsi == null)
          return createDefaultModelProjection(model, update, opMap, params, annotations, psi, errors);

        ensureModelKind(findProjectionKind(psi), TypeKind.MAP, psi, errors);

        return parseMapModelProjection(
            opMap,
            update,
            params,
            annotations,
            mapModelProjectionPsi,
            subResolver,
            errors
        );

      case LIST:
        final OpInputListModelProjection opList = (OpInputListModelProjection) op;
        @Nullable UrlReqUpdateListModelProjection listModelProjectionPsi =
            psi.getReqUpdateListModelProjection();

        if (listModelProjectionPsi == null)
          return createDefaultModelProjection(model, update, opList, params, annotations, psi, errors);

        ensureModelKind(findProjectionKind(psi), TypeKind.LIST, psi, errors);

        return parseListModelProjection(
            opList,
            update,
            params,
            annotations,
            listModelProjectionPsi,
            subResolver,
            errors
        );

      case ENUM:
        throw new PsiProcessingException("Unsupported type kind: " + model.kind(), psi, errors);

      case PRIMITIVE:
        return parsePrimitiveModelProjection(
            (OpInputPrimitiveModelProjection) op,
            params,
            annotations,
            psi
        );

      case UNION:
        throw new PsiProcessingException("Unsupported type kind: " + model.kind(), psi, errors);

      default:
        throw new PsiProcessingException("Unknown type kind: " + model.kind(), psi, errors);
    }

  }

  @NotNull
  private static ReqUpdateModelProjection<?, ?> createDefaultModelProjection(
      @NotNull DatumType type,
      boolean update,
      @NotNull OpInputModelProjection<?, ?, ?> op,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      @NotNull PsiElement locationPsi,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    @NotNull TextLocation location = EpigraphPsiUtil.getLocation(locationPsi);

    switch (type.kind()) {
      case RECORD:
        OpInputRecordModelProjection opRecord = (OpInputRecordModelProjection) op;
        final Map<String, OpInputFieldProjectionEntry> opFields = opRecord.fieldProjections();

        @NotNull final Map<String, ReqUpdateFieldProjectionEntry> fields;

        if (opFields.isEmpty()) {
          fields = Collections.emptyMap();
        } else {
          fields = new LinkedHashMap<>();

        }

        return new ReqUpdateRecordModelProjection(
            (RecordType) type,
            update,
            params,
            annotations,
            fields,
            location
        );
      case MAP:
        OpInputMapModelProjection opMap = (OpInputMapModelProjection) op;

        MapType mapType = (MapType) type;
        final ReqUpdateVarProjection valueVarProjection = createDefaultVarProjection(
            mapType.valueType(),
            opMap.itemsProjection(),
            update,
            locationPsi,
            errors
        );

        return new ReqUpdateMapModelProjection(
            mapType,
            update,
            params,
            annotations,
            false,
            Collections.emptyList(),
            valueVarProjection,
            location
        );
      case LIST:
        OpInputListModelProjection opList = (OpInputListModelProjection) op;
        ListType listType = (ListType) type;

        final ReqUpdateVarProjection itemVarProjection = createDefaultVarProjection(
            listType.elementType(),
            opList.itemsProjection(),
            update,
            locationPsi,
            errors
        );

        return new ReqUpdateListModelProjection(
            listType,
            update,
            params,
            annotations,
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
        return new ReqUpdatePrimitiveModelProjection(
            (PrimitiveType) type,
            params,
            annotations,
            location
        );
      default:
        throw new PsiProcessingException("Unknown type kind: " + type.kind(), locationPsi, errors);
    }
  }

  @NotNull
  private static ReqUpdateVarProjection createDefaultVarProjection(
      @NotNull DataType type,
      @NotNull OpInputVarProjection op,
      boolean required,
      @NotNull PsiElement locationPsi,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    return createDefaultVarProjection(type.type, op, required, locationPsi, errors);
  }

  @NotNull
  private static ReqUpdateVarProjection createDefaultVarProjection(
      @NotNull Type type,
      @NotNull OpInputVarProjection op,
      boolean required,
      @NotNull PsiElement locationPsi,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    Type.@Nullable Tag defaultTag = findDefaultTag(type, op, locationPsi, errors);
    List<Type.Tag> tags = defaultTag == null ?
                          Collections.emptyList() :
                          Collections.singletonList(defaultTag);
    return createDefaultVarProjection(type, tags, op, required, locationPsi, errors);
  }

  /**
   * Creates default var projection with explicitly specified list of tags
   */
  private static ReqUpdateVarProjection createDefaultVarProjection(
      @NotNull Type type,
      @NotNull List<Type.Tag> tags,
      @NotNull OpInputVarProjection op, boolean required,
      @NotNull PsiElement locationPsi,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    LinkedHashMap<String, ReqUpdateTagProjectionEntry> tagProjections = new LinkedHashMap<>();

    for (Type.Tag tag : tags) {
      final OpInputTagProjectionEntry opInputTagProjection = op.tagProjections().get(tag.name());
      if (opInputTagProjection != null) {
        tagProjections.put(
            tag.name(),
            new ReqUpdateTagProjectionEntry(
                tag,
                createDefaultModelProjection(
                    tag.type,
                    required,
                    opInputTagProjection.projection(),
                    ReqParams.EMPTY,
                    Annotations.EMPTY,
                    locationPsi,
                    errors
                ),
                EpigraphPsiUtil.getLocation(locationPsi)
            )
        );
      }
    }

    return new ReqUpdateVarProjection(
        type,
        tagProjections,
        null,
        EpigraphPsiUtil.getLocation(locationPsi)
    );
  }

  @Nullable
  private static TypeKind findProjectionKind(@NotNull UrlReqUpdateModelProjection psi) {
    if (psi.getReqUpdateRecordModelProjection() != null) return TypeKind.RECORD;
    if (psi.getReqUpdateMapModelProjection() != null) return TypeKind.MAP;
    if (psi.getReqUpdateListModelProjection() != null) return TypeKind.LIST;
    return null;
  }

  @NotNull
  public static ReqUpdateRecordModelProjection parseRecordModelProjection(
      @NotNull OpInputRecordModelProjection op,
      boolean update,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      @NotNull UrlReqUpdateRecordModelProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    final Map<String, ReqUpdateFieldProjectionEntry> fieldProjections = new LinkedHashMap<>();

    for (final UrlReqUpdateFieldProjectionEntry entryPsi : psi.getReqUpdateFieldProjectionEntryList()) {
      @NotNull final String fieldName = entryPsi.getQid().getCanonicalName();

      @Nullable final OpInputFieldProjectionEntry opFieldProjectionEntry = op.fieldProjection(fieldName);
      if (opFieldProjectionEntry == null) {
        errors.add(
            new PsiProcessingError(
                String.format(
                    "Field '%s' is not supported by the operation. Supported fields: {%s}",
                    fieldName,
                    String.join(", ", op.fieldProjections().keySet())
                ), entryPsi.getQid()
            )
        );
      } else {
        try {
          final RecordType.Field field = opFieldProjectionEntry.field();
          @NotNull final OpInputFieldProjection opFieldProjection = opFieldProjectionEntry.projection();
          @NotNull final UrlReqUpdateFieldProjection fieldProjectionPsi = entryPsi.getReqUpdateFieldProjection();
          @NotNull final DataType fieldType = field.dataType();

          final ReqUpdateFieldProjection fieldProjection =
              parseFieldProjection(
                  fieldType,
                  entryPsi.getPlus() != null,
                  opFieldProjection,
                  fieldProjectionPsi,
                  resolver,
                  errors
              );

          fieldProjections.put(
              fieldName,
              new ReqUpdateFieldProjectionEntry(
                  field,
                  fieldProjection,
                  EpigraphPsiUtil.getLocation(fieldProjectionPsi)
              )
          );
        } catch (PsiProcessingException e) {
          errors.add(e.toError());
        }
      }
    }

    // check that all required fields are specified
    for (final Map.Entry<String, OpInputFieldProjectionEntry> entry : op.fieldProjections().entrySet()) {
      if (entry.getValue().projection().required() && !fieldProjections.containsKey(entry.getKey())) {
        errors.add(
            new PsiProcessingError(String.format("Required field '%s' is missing", entry.getKey()), psi)
        );
      }
    }

    return new ReqUpdateRecordModelProjection(
        op.model(),
        update,
        params,
        annotations,
        fieldProjections,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  @NotNull
  public static ReqUpdateFieldProjection parseFieldProjection(
      final DataType fieldType,
      boolean update,
      final @NotNull OpInputFieldProjection op,
      final @NotNull UrlReqUpdateFieldProjection psi,
      final @NotNull TypesResolver resolver, final @NotNull List<PsiProcessingError> errors)
      throws PsiProcessingException {

    ReqParams fieldParams = parseReqParams(psi.getReqParamList(), op.params(), resolver, errors);

    Annotations fieldAnnotations = parseAnnotations(psi.getReqAnnotationList());

    @NotNull UrlReqUpdateVarProjection psiVarProjection = psi.getReqUpdateVarProjection();
    @NotNull ReqUpdateVarProjection varProjection =
        parseVarProjection(
            fieldType,
            op.projection(),
            psiVarProjection,
            resolver,
            errors
        );

    return new ReqUpdateFieldProjection(
        fieldParams,
        fieldAnnotations,
        varProjection,
        update,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  @NotNull
  public static ReqUpdateMapModelProjection parseMapModelProjection(
      @NotNull OpInputMapModelProjection op,
      boolean update,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      @NotNull UrlReqUpdateMapModelProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    @NotNull final MapType model = op.model();

    @NotNull final List<UrlReqUpdateKeyProjection> keysPsi =
        psi.getReqUpdateKeysProjection().getReqUpdateKeyProjectionList();

    List<ReqUpdateKeyProjection> keys = new ArrayList<>(keysPsi.size());

    for (final UrlReqUpdateKeyProjection keyPsi : keysPsi) {
      try {
        final @Nullable Datum keyValue =
            getDatum(keyPsi.getDatum(), model.keyType(), resolver, "Error processing map key: ", errors);

        if (keyValue == null) {
          errors.add(new PsiProcessingError("Null keys are not allowed", keyPsi));
        } else {
          keys.add(
              new ReqUpdateKeyProjection(
                  keyValue,
                  parseReqParams(keyPsi.getReqParamList(), op.keyProjection().params(), resolver, errors),
                  parseAnnotations(keyPsi.getReqAnnotationList()),
                  EpigraphPsiUtil.getLocation(keyPsi)
              )
          );
        }
      } catch (PsiProcessingException e) {
        errors.add(e.toError());
      }
    }

    @Nullable final UrlReqUpdateVarProjection elementsVarProjectionPsi = psi.getReqUpdateVarProjection();
    @NotNull final ReqUpdateVarProjection elementsVarProjection;

    if (elementsVarProjectionPsi == null) {
      @NotNull final Type type = model.valueType.type;
      elementsVarProjection = getDefaultVarProjection(type, psi);
    } else {
      elementsVarProjection = parseVarProjection(
          model.valueType,
          op.itemsProjection(),
          elementsVarProjectionPsi,
          resolver,
          errors
      );
    }

    return new ReqUpdateMapModelProjection(
        model,
        update,
        params,
        annotations,
        psi.getReqUpdateKeysProjection().getPlus() != null,
        keys,
        elementsVarProjection,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  @NotNull
  public static ReqUpdateListModelProjection parseListModelProjection(
      @NotNull OpInputListModelProjection op,
      boolean update,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      @NotNull UrlReqUpdateListModelProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    @Nullable final UrlReqUpdateVarProjection elementsVarProjectionPsi = psi.getReqUpdateVarProjection();

    @NotNull final ReqUpdateVarProjection elementsVarProjection;
    if (elementsVarProjectionPsi == null) {
      @NotNull final Type type = op.model().elementType().type;
      elementsVarProjection = getDefaultVarProjection(type, psi);
    } else {
      elementsVarProjection = parseVarProjection(
          op.model().elementType,
          op.itemsProjection(),
          elementsVarProjectionPsi,
          resolver,
          errors
      );
    }

    return new ReqUpdateListModelProjection(
        op.model(),
        update,
        params,
        annotations,
        elementsVarProjection,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  @NotNull
  public static ReqUpdatePrimitiveModelProjection parsePrimitiveModelProjection(
      @NotNull OpInputPrimitiveModelProjection op,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      @NotNull PsiElement locationPsi) throws PsiProcessingException {

    return new ReqUpdatePrimitiveModelProjection(
        op.model(),
        params,
        annotations,
        EpigraphPsiUtil.getLocation(locationPsi)
    );

  }
}
