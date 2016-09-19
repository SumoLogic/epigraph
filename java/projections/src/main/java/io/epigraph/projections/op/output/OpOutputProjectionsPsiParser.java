package io.epigraph.projections.op.output;

import com.intellij.psi.PsiElement;
import io.epigraph.idl.parser.psi.*;
import io.epigraph.lang.Fqn;
import io.epigraph.projections.op.input.OpInputModelProjection;
import io.epigraph.projections.op.input.OpInputProjectionsPsiParser;
import io.epigraph.psi.PsiProcessingException;
import io.epigraph.types.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpOutputProjectionsPsiParser {
  // todo custom parameters support

  public static OpOutputVarProjection parseVarProjection(@NotNull DataType dataType,
                                                         @NotNull IdlOpOutputVarProjection psi,
                                                         @NotNull TypesResolver typesResolver)
      throws PsiProcessingException {

    final Type type = dataType.type;
    final LinkedHashSet<OpOutputTagProjection> tagProjections = new LinkedHashSet<>();

    @Nullable IdlOpOutputSingleTagProjection singleTagProjectionPsi = psi.getOpOutputSingleTagProjection();

    if (singleTagProjectionPsi != null) {
      final OpOutputModelProjection<?> parsedModelProjection;
      final Type.Tag tag = getTag(
          type,
          getTagName(singleTagProjectionPsi.getOpTagName()),
          dataType.defaultTag,
          singleTagProjectionPsi
      );

      @Nullable IdlOpOutputModelProjection modelProjection = singleTagProjectionPsi.getOpOutputModelProjection();
      assert modelProjection != null; // todo when it can be null?

      parsedModelProjection = parseModelProjection(
          tag.type,
          singleTagProjectionPsi.getPlus() != null,
          parseModelParams(singleTagProjectionPsi.getOpOutputModelPropertyList(), typesResolver),
          modelProjection,
          typesResolver
      );

      tagProjections.add(new OpOutputTagProjection(tag, parsedModelProjection));
    } else {
      @Nullable IdlOpOutputMultiTagProjection multiTagProjection = psi.getOpOutputMultiTagProjection();
      assert multiTagProjection != null;
      // parse list of tags
      @NotNull List<IdlOpOutputMultiTagProjectionItem> tagProjectionPsiList =
          multiTagProjection.getOpOutputMultiTagProjectionItemList();

      for (IdlOpOutputMultiTagProjectionItem tagProjectionPsi : tagProjectionPsiList) {
        final String tagName = getTagName(tagProjectionPsi.getOpTagName());
        final Type.Tag tag = getTag(type, tagName, dataType.defaultTag, tagProjectionPsi);

        final OpOutputModelProjection<?> parsedModelProjection;

        @NotNull DatumType tagType = tag.type;
        @Nullable IdlOpOutputModelProjection modelProjection = tagProjectionPsi.getOpOutputModelProjection();
        assert modelProjection != null; // todo when it can be null?

        parsedModelProjection = parseModelProjection(
            tagType,
            tagProjectionPsi.getPlus() != null,
            parseModelParams(tagProjectionPsi.getOpOutputModelPropertyList(), typesResolver),
            modelProjection,
            typesResolver
        );

        tagProjections.add(new OpOutputTagProjection(tag, parsedModelProjection));
      }
    }

    // parse tails
    final LinkedHashSet<OpOutputVarProjection> tails;
    @Nullable IdlOpOutputVarPolymorphicTail psiTail = psi.getOpOutputVarPolymorphicTail();
    if (psiTail != null) {
      tails = new LinkedHashSet<>();

      @Nullable IdlOpOutputVarSingleTail singleTail = psiTail.getOpOutputVarSingleTail();
      if (singleTail != null) {
        @NotNull IdlFqnTypeRef tailTypeRef = singleTail.getFqnTypeRef();
        @NotNull IdlOpOutputVarProjection psiTailProjection = singleTail.getOpOutputVarProjection();
        @NotNull OpOutputVarProjection tailProjection =
            buildTailProjection(dataType, tailTypeRef, psiTailProjection, typesResolver, singleTail);
        tails.add(tailProjection);
      } else {
        @Nullable IdlOpOutputVarMultiTail multiTail = psiTail.getOpOutputVarMultiTail();
        assert multiTail != null;
        for (IdlOpOutputVarMultiTailItem tailItem : multiTail.getOpOutputVarMultiTailItemList()) {
          @NotNull IdlFqnTypeRef tailTypeRef = tailItem.getFqnTypeRef();
          @NotNull IdlOpOutputVarProjection psiTailProjection = tailItem.getOpOutputVarProjection();
          @NotNull OpOutputVarProjection tailProjection =
              buildTailProjection(dataType, tailTypeRef, psiTailProjection, typesResolver, tailItem);
          tails.add(tailProjection);
        }
      }

    } else tails = null;

    return new OpOutputVarProjection(type, tagProjections, tails);
  }

  @Nullable
  private static String getTagName(@Nullable IdlOpTagName tagNamePsi) {
    if (tagNamePsi == null) return null;
    @Nullable IdlQid qid = tagNamePsi.getQid();
    if (qid == null) return null;
    return qid.getCanonicalName();
  }

  @NotNull
  private static Type.Tag getTag(
      @NotNull Type type,
      @Nullable String tagName,
      @Nullable Type.Tag defaultTag,
      @NotNull PsiElement location) throws PsiProcessingException {

    final Type.Tag tag;

    if (tagName == null) {
      // get default tag
      if (defaultTag == null)
        throw new PsiProcessingException(
            String.format("Can't parse default tag projection for '%s', default tag not specified", type.name()),
            location
        );

      tag = defaultTag;
      verifyTag(type, tag, location);
    } else tag = getTag(type, tagName, location);
    return tag;
  }

  @Nullable
  public static Set<OpParam> parseModelParams(
      @NotNull List<IdlOpOutputModelProperty> modelProperties,
      @NotNull TypesResolver resolver) throws PsiProcessingException {

    Set<OpParam> res = null;

    for (IdlOpOutputModelProperty modelProperty : modelProperties) {
      @Nullable IdlOpParam paramPsi = modelProperty.getOpParam();
      if (paramPsi != null) {
        if (res == null) res = new HashSet<>();
        res.add(parseParameter(paramPsi, resolver));
      }
    }

    return res;
  }

  @NotNull
  private static OpOutputVarProjection buildTailProjection(@NotNull DataType dataType,
                                                           IdlFqnTypeRef tailTypeRef,
                                                           IdlOpOutputVarProjection psiTailProjection,
                                                           @NotNull TypesResolver typesResolver,
                                                           PsiElement location)
      throws PsiProcessingException {

    @NotNull Fqn typeFqn = tailTypeRef.getFqn().getFqn();
    @NotNull Type tailType = getType(typesResolver, typeFqn, location);
    return parseVarProjection(
        new DataType(dataType.polymorphic, tailType, dataType.defaultTag),
        psiTailProjection,
        typesResolver
    );
  }

  @NotNull
  private static Type.Tag getTag(@NotNull Type type, @NotNull String tagName, @NotNull PsiElement location)
      throws PsiProcessingException {
    Type.Tag tag = type.tagsMap().get(tagName);
    if (tag == null)
      throw new PsiProcessingException(
          String.format("Can't find tag '%s' in '%s'", tagName, type.name()),
          location
      );
    return tag;
  }

  private static void verifyTag(@NotNull Type type, @NotNull Type.Tag tag, @NotNull PsiElement location)
      throws PsiProcessingException {
    if (!type.tags().contains(tag))
      throw new PsiProcessingException(String.format("Tag '%s' doesn't belong to type '%s'",
                                                     tag.name(),
                                                     type.name()
      ), location);
  }

  @NotNull
  private static Type getType(@NotNull TypesResolver resolver, @NotNull Fqn fqn, @NotNull PsiElement location)
      throws PsiProcessingException {
    @Nullable Type type = resolver.resolve(fqn);
    if (type == null) throw new PsiProcessingException(String.format("Can't find type '%s'", fqn), location);
    return type;
  }

  @NotNull
  private static OpOutputVarProjection createDefaultVarProjection(@NotNull Type type,
                                                                  @NotNull Type.Tag tag,
                                                                  boolean includeInDefault,
                                                                  @NotNull PsiElement location)
      throws PsiProcessingException {
    return new OpOutputVarProjection(type, new OpOutputTagProjection(
        tag,
        createDefaultModelProjection(tag.type, includeInDefault, location)
    ));
  }

  @NotNull
  private static OpOutputVarProjection createDefaultVarProjection(@NotNull DatumType type,
                                                                  boolean includeInDefault,
                                                                  @NotNull PsiElement location)
      throws PsiProcessingException {
    return createDefaultVarProjection(type, type.self, includeInDefault, location);
  }

  @NotNull
  private static OpOutputVarProjection createDefaultVarProjection(@NotNull DataType type,
                                                                  boolean includeInDefault,
                                                                  @NotNull PsiElement location)
      throws PsiProcessingException {

    @Nullable Type.Tag defaultTag = type.defaultTag;
    if (defaultTag == null)
      throw new PsiProcessingException(
          String.format("Can't build default projection for '%s', default tag not specified", type.name), location
      );

    return createDefaultVarProjection(type.type, defaultTag, includeInDefault, location);
  }

  @NotNull
  public static OpOutputModelProjection<?> parseModelProjection(@NotNull DatumType type,
                                                                boolean includeInDefault,
                                                                @Nullable Set<OpParam> params,
                                                                @NotNull IdlOpOutputModelProjection psi,
                                                                @NotNull TypesResolver typesResolver)
      throws PsiProcessingException {

    switch (type.kind()) {
      case RECORD:
        @Nullable IdlOpOutputRecordModelProjection recordModelProjectionPsi = psi.getOpOutputRecordModelProjection();
        if (recordModelProjectionPsi == null)
          return createDefaultModelProjection(type, includeInDefault, psi);
        ensureModelKind(psi, TypeKind.RECORD);
        return parseRecordModelProjection(
            (RecordType) type,
            includeInDefault,
            params,
            recordModelProjectionPsi,
            typesResolver
        );
      case LIST:
        @Nullable IdlOpOutputListModelProjection listModelProjectionPsi = psi.getOpOutputListModelProjection();
        if (listModelProjectionPsi == null)
          return createDefaultModelProjection(type, includeInDefault, psi);
        ensureModelKind(psi, TypeKind.LIST);
        return parseListModelProjection(
            (ListType) type,
            includeInDefault,
            params,
            listModelProjectionPsi,
            typesResolver
        );
      case MAP:
        @Nullable IdlOpOutputMapModelProjection mapModelProjectionPsi = psi.getOpOutputMapModelProjection();
        if (mapModelProjectionPsi == null)
          return createDefaultModelProjection(type, includeInDefault, psi);
        ensureModelKind(psi, TypeKind.MAP);
        throw new PsiProcessingException("Unsupported type kind: " + type.kind(), psi);
      case ENUM:
        throw new PsiProcessingException("Unsupported type kind: " + type.kind(), psi);
      case PRIMITIVE:
        return parsePrimitiveModelProjection(
            (PrimitiveType) type,
            includeInDefault,
            params
        );
      case UNION:
        throw new PsiProcessingException("Unsupported type kind: " + type.kind(), psi);
      default:
        throw new PsiProcessingException("Unknown type kind: " + type.kind(), psi);
    }
  }

  private static void ensureModelKind(@NotNull IdlOpOutputModelProjection psi, @NotNull TypeKind expectedKind)
      throws PsiProcessingException {

    @Nullable TypeKind actualKind = findProjectionKind(psi);
    if (!expectedKind.equals(actualKind))
      throw new PsiProcessingException(MessageFormat.format("Unexpected projection kind ''{0}'', expected ''{1}''",
                                                            actualKind,
                                                            expectedKind
      ), psi);
  }

  @Nullable
  private static TypeKind findProjectionKind(@NotNull IdlOpOutputModelProjection psi) {
    if (psi.getOpOutputRecordModelProjection() != null) return TypeKind.RECORD;
    if (psi.getOpOutputMapModelProjection() != null) return TypeKind.MAP;
    if (psi.getOpOutputListModelProjection() != null) return TypeKind.LIST;
    return null;
  }

  @NotNull
  private static OpOutputModelProjection<?> createDefaultModelProjection(@NotNull DatumType type,
                                                                         boolean includeInDefault,
                                                                         @NotNull PsiElement location)
      throws PsiProcessingException {

    switch (type.kind()) {
      case RECORD:
        return new OpOutputRecordModelProjection((RecordType) type,
                                                 includeInDefault,
                                                 null,
                                                 null
        );
      case LIST:
        ListType listType = (ListType) type;
        @NotNull DataType elementType = listType.elementType();
        Type.@Nullable Tag defaultTag = elementType.defaultTag;

        if (defaultTag == null)
          throw new PsiProcessingException(String.format(
              "Can't create default projection for list type '%s, as it's element type '%s' doesn't have a default tag",
              type.name(),
              elementType.name
          ), location);

        final OpOutputVarProjection itemVarProjection = createDefaultVarProjection(
            elementType.type,
            defaultTag,
            includeInDefault,
            location
        );

        return new OpOutputListModelProjection(listType,
                                               includeInDefault,
                                               null,
                                               itemVarProjection
        );
      case MAP:
        throw new PsiProcessingException("Unsupported type kind: " + type.kind(), location);
      case UNION:
        throw new PsiProcessingException("Was expecting to get datum model kind, got: " + type.kind(), location);
      case ENUM:
        throw new PsiProcessingException("Unsupported type kind: " + type.kind(), location);
      case PRIMITIVE:
        return new OpOutputPrimitiveModelProjection((PrimitiveType) type, includeInDefault, null);
      default:
        throw new PsiProcessingException("Unknown type kind: " + type.kind(), location);
    }
  }

  @NotNull
  public static OpOutputRecordModelProjection parseRecordModelProjection(@NotNull RecordType type,
                                                                         boolean includeInDefault,
                                                                         @Nullable Set<OpParam> params,
                                                                         @NotNull IdlOpOutputRecordModelProjection psi,
                                                                         @NotNull TypesResolver typesResolver)
      throws PsiProcessingException {
    LinkedHashSet<OpOutputFieldProjection> fieldProjections = new LinkedHashSet<>();
    @NotNull List<IdlOpOutputFieldProjection> psiFieldProjections = psi.getOpOutputFieldProjectionList();

    for (IdlOpOutputFieldProjection psiFieldProjection : psiFieldProjections) {
      final String fieldName = psiFieldProjection.getQid().getCanonicalName();
      RecordType.Field field = type.fieldsMap().get(fieldName);
      if (field == null)
        throw new PsiProcessingException(
            String.format("Can't field projection for '%s', field '%s' not found", type.name(), fieldName),
            psiFieldProjection
        );

      final boolean includeFieldInDefault = psiFieldProjection.getPlus() != null;

      Set<OpParam> fieldParams = null;
      for (IdlOpOutputFieldProjectionBodyPart fieldBodyPart : psiFieldProjection.getOpOutputFieldProjectionBodyPartList()) {
        @Nullable IdlOpParam opParam = fieldBodyPart.getOpParam();
        if (opParam != null) {
          if (fieldParams == null) fieldParams = new HashSet<>();
          fieldParams.add(parseParameter(opParam, typesResolver));
        }

        //todo parse field custom params
      }

      OpOutputVarProjection varProjection;

      @Nullable IdlOpOutputVarProjection psiVarProjection = psiFieldProjection.getOpOutputVarProjection();
      if (psiVarProjection == null) {
        @NotNull DataType fieldDataType = field.dataType();
        @Nullable Type.Tag defaultFieldTag = fieldDataType.defaultTag;
        if (defaultFieldTag == null)
          throw new PsiProcessingException(String.format(
              "Can't construct default projection for field '%s', as it's type '%s' has no default tag",
              fieldName,
              fieldDataType.name
          ), psiFieldProjection);

        varProjection =
            createDefaultVarProjection(fieldDataType.type, defaultFieldTag, includeInDefault, psiFieldProjection);
      } else {
        varProjection = parseVarProjection(field.dataType(), psiVarProjection, typesResolver);
      }

      fieldProjections.add(new OpOutputFieldProjection(field, fieldParams, varProjection, includeFieldInDefault));
    }

    final LinkedHashSet<OpOutputRecordModelProjection> tail;

    return new OpOutputRecordModelProjection(type, includeInDefault, params, fieldProjections);
  }

  @NotNull
  public static OpOutputListModelProjection parseListModelProjection(@NotNull ListType type,
                                                                     boolean includeInDefault,
                                                                     @Nullable Set<OpParam> params,
                                                                     @NotNull IdlOpOutputListModelProjection psi,
                                                                     @NotNull TypesResolver resolver)
      throws PsiProcessingException {

    OpOutputVarProjection itemsProjection;
    @Nullable IdlOpOutputVarProjection opOutputVarProjectionPsi = psi.getOpOutputVarProjection();
    if (opOutputVarProjectionPsi == null)
      itemsProjection = createDefaultVarProjection(type, true, psi);
    else
      itemsProjection = parseVarProjection(type.elementType(), opOutputVarProjectionPsi, resolver);


    return new OpOutputListModelProjection(
        type,
        includeInDefault,
        params,
        itemsProjection
    );
  }

  @NotNull
  public static OpOutputPrimitiveModelProjection parsePrimitiveModelProjection(@NotNull PrimitiveType type,
                                                                               boolean includeInDefault,
                                                                               @Nullable Set<OpParam> params) {
    // todo custom params, tails
    return new OpOutputPrimitiveModelProjection(type, includeInDefault, params);
  }

  @NotNull
  private static OpParam parseParameter(@NotNull IdlOpParam paramPsi,
                                        @NotNull TypesResolver resolver) throws PsiProcessingException {
    @NotNull String paramName = paramPsi.getQid().getCanonicalName();
    @NotNull Fqn paramTypeName = paramPsi.getFqnTypeRef().getFqn().getFqn();
    @NotNull IdlOpInputModelProjection paramModelProjectionPsi = paramPsi.getOpInputModelProjection();

    @Nullable DatumType paramType = resolver.resolveDatumType(paramTypeName);
    if (paramType == null)
      throw new PsiProcessingException(
          String.format("Can't resolve parameter '%s' data type '%s'", paramName, paramTypeName), paramPsi
      );

    OpInputModelProjection<?, ?> paramModelProjection = OpInputProjectionsPsiParser.parseModelProjection(
        paramType, paramPsi.getPlus() != null, paramModelProjectionPsi, resolver
    );

    return new OpParam(paramName, paramModelProjection);
  }
}
