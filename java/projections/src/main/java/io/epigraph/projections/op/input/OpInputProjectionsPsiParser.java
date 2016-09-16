package io.epigraph.projections.op.input;

import com.intellij.psi.PsiElement;
import io.epigraph.data.PrimitiveDatum;
import io.epigraph.data.RecordDatum;
import io.epigraph.gdata.*;
import io.epigraph.idl.gdata.IdlGDataPsiParser;
import io.epigraph.idl.parser.psi.*;
import io.epigraph.idl.parser.psi.impl.IdlOpInputModelProjectionImpl;
import io.epigraph.lang.Fqn;
import io.epigraph.psi.PsiProcessingException;
import io.epigraph.types.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpInputProjectionsPsiParser {
  // todo custom parameters support

  public static OpInputVarProjection parseVarProjection(@NotNull DataType dataType,
                                                        @NotNull IdlOpInputVarProjection psi,
                                                        @NotNull TypesResolver typesResolver)
      throws PsiProcessingException {

    final Type type = dataType.type;
    final LinkedHashSet<OpInputTagProjection> tagProjections = new LinkedHashSet<>();

    @Nullable IdlOpInputSingleTagProjection singleTagProjection = psi.getOpInputSingleTagProjection();
    if (singleTagProjection != null) {
      final OpInputModelProjection<?, ?> parsedModelProjection;
      final String tagName = singleTagProjection.getQid() == null
                             ? null
                             : singleTagProjection.getQid().getCanonicalName();
      final Type.Tag tag;

      if (tagName == null) {
        // get default tag
        if (dataType.defaultTag == null)
          throw new PsiProcessingException(
              String.format("Can't parse default tag projection for '%s', default tag not specified", type.name()),
              singleTagProjection
          );

        tag = dataType.defaultTag;
        verifyTag(type, tag, singleTagProjection);
      } else tag = getTag(type, tagName, singleTagProjection);

      @Nullable IdlOpInputModelProjection modelProjection = singleTagProjection.getOpInputModelProjection();

      parsedModelProjection = parseModelProjection(tag.type, modelProjection, typesResolver);

      tagProjections.add(new OpInputTagProjection(tag, parsedModelProjection));
    } else {
      @Nullable IdlOpInputMultiTagProjection multiTagProjection = psi.getOpInputMultiTagProjection();
      assert multiTagProjection != null;
      // parse list of tags
      @NotNull List<IdlOpInputMultiTagProjectionItem> psiTagProjections =
          multiTagProjection.getOpInputMultiTagProjectionItemList();

      for (IdlOpInputMultiTagProjectionItem psiTagProjection : psiTagProjections) {
        String tagName = psiTagProjection.getQid().getCanonicalName();
        Type.Tag tag = getTag(type, tagName, psiTagProjection);

        final OpInputModelProjection<?, ?> parsedModelProjection;

        @NotNull DatumType tagType = tag.type;
        @Nullable IdlOpInputModelProjection modelProjection = psiTagProjection.getOpInputModelProjection();
        parsedModelProjection = parseModelProjection(tagType, modelProjection, typesResolver);

        tagProjections.add(new OpInputTagProjection(tag, parsedModelProjection));
      }
    }

    // parse tails
    final LinkedHashSet<OpInputVarProjection> tails;
    @Nullable IdlOpInputVarPolymorphicTail psiTail = psi.getOpInputVarPolymorphicTail();
    if (psiTail != null) {
      tails = new LinkedHashSet<>();

      @Nullable IdlOpInputVarSingleTail singleTail = psiTail.getOpInputVarSingleTail();
      if (singleTail != null) {
        @NotNull IdlFqnTypeRef tailTypeRef = singleTail.getFqnTypeRef();
        @NotNull IdlOpInputVarProjection psiTailProjection = singleTail.getOpInputVarProjection();
        @NotNull OpInputVarProjection tailProjection =
            buildTailProjection(dataType, tailTypeRef, psiTailProjection, typesResolver, singleTail);
        tails.add(tailProjection);
      } else {
        @Nullable IdlOpInputVarMultiTail multiTail = psiTail.getOpInputVarMultiTail();
        assert multiTail != null;
        for (IdlOpInputVarMultiTailItem tailItem : multiTail.getOpInputVarMultiTailItemList()) {
          @NotNull IdlFqnTypeRef tailTypeRef = tailItem.getFqnTypeRef();
          @NotNull IdlOpInputVarProjection psiTailProjection = tailItem.getOpInputVarProjection();
          @NotNull OpInputVarProjection tailProjection =
              buildTailProjection(dataType, tailTypeRef, psiTailProjection, typesResolver, tailItem);
          tails.add(tailProjection);
        }
      }

    } else tails = null;

    return new OpInputVarProjection(type, tagProjections, tails);
  }

  @NotNull
  private static OpInputVarProjection buildTailProjection(@NotNull DataType dataType,
                                                          IdlFqnTypeRef tailTypeRef,
                                                          IdlOpInputVarProjection psiTailProjection,
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

  private static OpInputVarProjection createDefaultVarProjection(@NotNull Type type,
                                                                 @NotNull Type.Tag tag,
                                                                 boolean required,
                                                                 @NotNull PsiElement location)
      throws PsiProcessingException {
    return new OpInputVarProjection(type, new OpInputTagProjection(
        tag,
        createDefaultModelProjection(tag.type, required, location)
    ));
  }

  private static OpInputVarProjection createDefaultVarProjection(@NotNull DatumType type,
                                                                 boolean required,
                                                                 @NotNull PsiElement location)
      throws PsiProcessingException {
    return createDefaultVarProjection(type, type.self, required, location);
  }

//  private static boolean required(@NotNull IdlOpInputModelProjection modelProjection) {
//    @Nullable
//    IdlOpInputModelProjectionBody modelProjectionBody = modelProjection.getOpInputModelProjectionBody();
//
//    if (modelProjectionBody != null) {
//      @NotNull
//      List<IdlOpInputModelProjectionBodyPart> bodyParts = modelProjectionBody.getOpInputModelProjectionBodyPartList();
//
//      for (IdlOpInputModelProjectionBodyPart part : bodyParts) {
//        if (part.getRequired() != null) return true;
//      }
//    }
//
//    return false;
//  }

  public static OpInputModelProjection<?, ?> parseModelProjection(@NotNull DatumType type,
                                                                  @NotNull IdlOpInputModelProjection psi,
                                                                  @NotNull TypesResolver typesResolver)
      throws PsiProcessingException {

    @NotNull OpInputModelProjectionBodyContents body = parseModelBody(psi.getOpInputModelProjectionBody());
    body.required = psi.getPlus() != null;

    final boolean noSpecificKindProjection = psi.getClass().equals(IdlOpInputModelProjectionImpl.class);
    switch (type.kind()) {
      case RECORD:
        if (noSpecificKindProjection)
          return createDefaultModelProjection(type, body.required, psi);
        ensureModelKind(psi, IdlOpInputRecordModelProjection.class, TypeKind.RECORD);
        GDataRecord defaultRecordData = body.coerceDefault(GDataRecord.class, psi);

        return parseRecordModelProjection((RecordType) type,
                                          body.required,
                                          defaultRecordData,
                                          (IdlOpInputRecordModelProjection) psi,
                                          typesResolver
        );
      case LIST:
        if (noSpecificKindProjection)
          return createDefaultModelProjection(type, body.required, psi);
        ensureModelKind(psi, IdlOpInputListModelProjection.class, TypeKind.LIST);
        throw new PsiProcessingException("Unsupported type kind: " + type.kind(), psi);
      case MAP:
        if (noSpecificKindProjection)
          return createDefaultModelProjection(type, body.required, psi);
        ensureModelKind(psi, IdlOpInputMapModelProjection.class, TypeKind.MAP);
        throw new PsiProcessingException("Unsupported type kind: " + type.kind(), psi);
      case ENUM:
        if (!noSpecificKindProjection)
          wrongProjectionKind(psi, TypeKind.ENUM);
        throw new PsiProcessingException("Unsupported type kind: " + type.kind(), psi);
      case PRIMITIVE:
        if (!noSpecificKindProjection)
          wrongProjectionKind(psi, TypeKind.PRIMITIVE);
        GDataPrimitive defaultPrimitiveData = body.coerceDefault(GDataPrimitive.class, psi);
        return parsePrimitiveModelProjection((PrimitiveType) type,
                                             body.required,
                                             defaultPrimitiveData,
                                             psi,
                                             typesResolver
        );
      case UNION:
        throw new PsiProcessingException("Unsupported type kind: " + type.kind(), psi);
      default:
        throw new PsiProcessingException("Unknown type kind: " + type.kind(), psi);
    }
  }

  private static void ensureModelKind(@NotNull IdlOpInputModelProjection psi,
                                      @NotNull Class<? extends IdlOpInputModelProjection> expectedClass,
                                      @NotNull TypeKind expectedKind) throws PsiProcessingException {
    if (!(expectedClass.isAssignableFrom(psi.getClass())))
      wrongProjectionKind(psi, expectedKind);
  }

  private static void wrongProjectionKind(@NotNull IdlOpInputModelProjection psi, @NotNull TypeKind expectedKind)
      throws PsiProcessingException {
    String actualKind = "Unknown (" + psi.getClass().getName() + ")";
    if (psi instanceof IdlOpInputRecordModelProjection)
      actualKind = TypeKind.RECORD.toString();
    else if (psi instanceof IdlOpInputMapModelProjection)
      actualKind = TypeKind.MAP.toString();
    else if (psi instanceof IdlOpInputListModelProjection)
      actualKind = TypeKind.LIST.toString();
    // TODO rest

    throw new PsiProcessingException(MessageFormat.format("Unexpected projection kind ''{0}'', expected ''{1}''",
                                                          actualKind,
                                                          expectedKind
    ), psi);
  }

  @NotNull
  private static OpInputModelProjectionBodyContents parseModelBody(@Nullable IdlOpInputModelProjectionBody body)
      throws PsiProcessingException {
    final OpInputModelProjectionBodyContents res = new OpInputModelProjectionBodyContents();
    if (body != null) {
      @NotNull List<IdlOpInputModelProjectionBodyPart> parts = body.getOpInputModelProjectionBodyPartList();
      for (IdlOpInputModelProjectionBodyPart part : parts) {

        if (part.getOpInputDefaultValue() != null) {
          @Nullable IdlVarValue idlVarValue = part.getOpInputDefaultValue().getVarValue();
          res.defaultValue = idlVarValue == null
                             ? null
                             : IdlGDataPsiParser.parseVarValue(idlVarValue);
        }

        // todo custom params
      }

    }

    return res;
  }

  private static OpInputModelProjection<?, ?> createDefaultModelProjection(@NotNull DatumType type,
                                                                           boolean required,
                                                                           @NotNull PsiElement location)
      throws PsiProcessingException {

    switch (type.kind()) {
      case RECORD:
        return new OpInputRecordModelProjection((RecordType) type,
                                                required,
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

        final OpInputVarProjection itemVarProjection = createDefaultVarProjection(
            elementType.type,
            defaultTag,
            required,
            location
        );

        return new OpInputListModelProjection(listType,
                                              required,
                                              null,
                                              itemVarProjection
        );
      case MAP:
        // todo
        throw new PsiProcessingException("Unsupported type kind: " + type.kind(), location);
      case UNION:
        throw new PsiProcessingException("Was expecting to get datum model kind, got: " + type.kind(), location);
      case ENUM:
        // todo
        throw new PsiProcessingException("Unsupported type kind: " + type.kind(), location);
      case PRIMITIVE:
        return new OpInputPrimitiveModelProjection((PrimitiveType) type, required, null);
      default:
        throw new PsiProcessingException("Unknown type kind: " + type.kind(), location);
    }
  }

  public static OpInputRecordModelProjection parseRecordModelProjection(@NotNull RecordType type,
                                                                        boolean required,
                                                                        @Nullable GDataRecord defaultValue,
                                                                        @NotNull IdlOpInputRecordModelProjection psi,
                                                                        @NotNull TypesResolver resolver)
      throws PsiProcessingException {

    RecordDatum defaultDatum = null;
    if (defaultValue != null) {
      try {
        defaultDatum = GDataToData.transform(type, defaultValue, resolver);
      } catch (GDataToData.ProcessingException e) {
        throw new PsiProcessingException(e, psi);
      }
    }

    LinkedHashSet<OpInputFieldProjection> fieldProjections = new LinkedHashSet<>();
    @NotNull List<IdlOpInputFieldProjection> psiFieldProjections = psi.getOpInputFieldProjectionList();

    for (IdlOpInputFieldProjection psiFieldProjection : psiFieldProjections) {
      final String fieldName = psiFieldProjection.getQid().getCanonicalName();
      RecordType.Field field = type.fieldsMap().get(fieldName);
      if (field == null)
        throw new PsiProcessingException(
            String.format("Can't field projection for '%s', field '%s' not found", type.name(), fieldName),
            psiFieldProjection
        );

      final boolean requried = psiFieldProjection.getPlus() != null;
      @Nullable IdlOpInputFieldProjectionBody fieldBody = psiFieldProjection.getOpInputFieldProjectionBody();
      if (fieldBody != null) {
        for (IdlOpInputFieldProjectionBodyPart fieldBodyPart : fieldBody.getOpInputFieldProjectionBodyPartList()) {
          //todo parse field custom params
        }
      }

      OpInputVarProjection varProjection;

      @Nullable IdlOpInputVarProjection psiVarProjection = psiFieldProjection.getOpInputVarProjection();
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
            createDefaultVarProjection(fieldDataType.type, defaultFieldTag, required, psiFieldProjection);
      } else {
        varProjection = parseVarProjection(field.dataType(), psiVarProjection, resolver);
      }

      fieldProjections.add(new OpInputFieldProjection(field, varProjection, requried));
    }

    final LinkedHashSet<OpInputRecordModelProjection> tail;

    return new OpInputRecordModelProjection(type, required, defaultDatum, fieldProjections);
  }


  public static OpInputPrimitiveModelProjection parsePrimitiveModelProjection(@NotNull PrimitiveType type,
                                                                              boolean required,
                                                                              @Nullable GDataPrimitive defaultValue,
                                                                              @NotNull PsiElement location,
                                                                              @NotNull TypesResolver resolver)
      throws PsiProcessingException {

    PrimitiveDatum<?> defaultDatum = null;
    if (defaultValue != null) {
      try {
        defaultDatum = GDataToData.transform(type, defaultValue, resolver);
      } catch (GDataToData.ProcessingException e) {
        throw new PsiProcessingException(e, location);
      }
    }
    return new OpInputPrimitiveModelProjection(type, required, defaultDatum);
  }

  private static class OpInputModelProjectionBodyContents {
    boolean required = false;
    @Nullable GDataVarValue defaultValue;
    // todo custom params

    @Nullable
    public <D extends GDataVarValue> D coerceDefault(Class<D> cls, @NotNull PsiElement location)
        throws PsiProcessingException {
      if (defaultValue == null) return null;
      if (defaultValue instanceof GDataNull) return null;
      if (defaultValue.getClass().equals(cls)) //noinspection unchecked
        return (D) defaultValue;
      throw new PsiProcessingException(
          String.format("Invalid default value '%s', expected to get '%s'", defaultValue, cls.getName()),
          location
      );
    }
  }
}
