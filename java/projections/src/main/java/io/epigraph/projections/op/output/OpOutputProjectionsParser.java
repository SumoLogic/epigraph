package io.epigraph.projections.op.output;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiErrorElement;
import io.epigraph.idl.parser.psi.*;
import io.epigraph.idl.parser.psi.impl.IdlOpOutputModelProjectionImpl;
import io.epigraph.lang.Fqn;
import io.epigraph.types.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpOutputProjectionsParser {
  // todo custom parameters support

  public static OpOutputVarProjection parseVarProjection(@NotNull DataType dataType,
                                                         @NotNull IdlOpOutputVarProjection psi,
                                                         @NotNull TypesResolver typesResolver)
      throws ProjectionParsingException {

    final Type type = dataType.type;
    final LinkedHashSet<OpOutputTagProjection> tagProjections = new LinkedHashSet<>();

    @Nullable IdlOpOutputSingleTagProjection singleTagProjection = psi.getOpOutputSingleTagProjection();
    if (singleTagProjection != null) {
      final OpOutputModelProjection<?> parsedModelProjection;
      final boolean includeInDefault = singleTagProjection.getPlus() != null;
      final String tagName = singleTagProjection.getQid() == null
                             ? null
                             : singleTagProjection.getQid().getCanonicalName();
      final Type.Tag tag;

      if (tagName == null) {
        // get default tag
        if (dataType.defaultTag == null)
          throw new ProjectionParsingException(
              String.format("Can't parse default tag projection for '%s', default tag not specified", type.name()),
              singleTagProjection
          );

        tag = dataType.defaultTag;
        verifyTag(type, tag, singleTagProjection);
      } else tag = getTag(type, tagName, singleTagProjection);

      @Nullable IdlOpOutputModelProjection modelProjection = singleTagProjection.getOpOutputModelProjection();

      parsedModelProjection = parseModelProjection(tag.type, modelProjection, typesResolver);

      tagProjections.add(new OpOutputTagProjection(tag, includeInDefault, parsedModelProjection));
    } else {
      @Nullable IdlOpOutputMultiTagProjection multiTagProjection = psi.getOpOutputMultiTagProjection();
      assert multiTagProjection != null;
      // parse list of tags
      @NotNull List<IdlOpOutputMultiTagProjectionItem> psiTagProjections =
          multiTagProjection.getOpOutputMultiTagProjectionItemList();

      for (IdlOpOutputMultiTagProjectionItem psiTagProjection : psiTagProjections) {
        String tagName = psiTagProjection.getQid().getCanonicalName();
        Type.Tag tag = getTag(type, tagName, psiTagProjection);

        final boolean includeInDefault = psiTagProjection.getPlus() != null;
        final OpOutputModelProjection<?> parsedModelProjection;

        @NotNull DatumType tagType = tag.type;
        @Nullable IdlOpOutputModelProjection modelProjection = psiTagProjection.getOpOutputModelProjection();
        parsedModelProjection = parseModelProjection(tagType, modelProjection, typesResolver);

        tagProjections.add(new OpOutputTagProjection(tag, includeInDefault, parsedModelProjection));
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

  @NotNull
  private static OpOutputVarProjection buildTailProjection(@NotNull DataType dataType,
                                                           IdlFqnTypeRef tailTypeRef,
                                                           IdlOpOutputVarProjection psiTailProjection,
                                                           @NotNull TypesResolver typesResolver,
                                                           PsiElement location)
      throws ProjectionParsingException {

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
      throws ProjectionParsingException {
    Type.Tag tag = type.tagsMap().get(tagName);
    if (tag == null)
      throw new ProjectionParsingException(
          String.format("Can't find tag '%s' in '%s'", tagName, type.name()),
          location
      );
    return tag;
  }

  private static void verifyTag(@NotNull Type type, @NotNull Type.Tag tag, @NotNull PsiElement location)
      throws ProjectionParsingException {
    if (!type.tags().contains(tag))
      throw new ProjectionParsingException(String.format("Tag '%s' doesn't belong to type '%s'",
                                                         tag.name(),
                                                         type.name()
      ), location);
  }

  @NotNull
  private static Type getType(@NotNull TypesResolver resolver, @NotNull Fqn fqn, @NotNull PsiElement location)
      throws ProjectionParsingException {
    @Nullable Type type = resolver.resolve(fqn);
    if (type == null) throw new ProjectionParsingException(String.format("Can't find type '%s'", fqn), location);
    return type;
  }

  private static OpOutputVarProjection createDefaultVarProjection(@NotNull Type type,
                                                                  @NotNull Type.Tag tag,
                                                                  boolean includeInDefault,
                                                                  @NotNull PsiElement location)
      throws ProjectionParsingException {
    return new OpOutputVarProjection(type, new OpOutputTagProjection(
        tag,
        true,
        createDefaultModelProjection(tag.type, includeInDefault, location)
    ));
  }

  private static OpOutputVarProjection createDefaultVarProjection(@NotNull DatumType type,
                                                                  boolean includeInDefault,
                                                                  @NotNull PsiElement location)
      throws ProjectionParsingException {
    return createDefaultVarProjection(type, type.self, includeInDefault, location);
  }

//  private static boolean includeInDefault(@NotNull IdlOpOutputModelProjection modelProjection) {
//    @Nullable
//    IdlOpOutputModelProjectionBody modelProjectionBody = modelProjection.getOpOutputModelProjectionBody();
//
//    if (modelProjectionBody != null) {
//      @NotNull
//      List<IdlOpOutputModelProjectionBodyPart> bodyParts = modelProjectionBody.getOpOutputModelProjectionBodyPartList();
//
//      for (IdlOpOutputModelProjectionBodyPart part : bodyParts) {
//        if (part.getIncludeInDefault() != null) return true;
//      }
//    }
//
//    return false;
//  }

  public static OpOutputModelProjection<?> parseModelProjection(@NotNull DatumType type,
                                                                   @NotNull IdlOpOutputModelProjection psi,
                                                                   @NotNull TypesResolver typesResolver)
      throws ProjectionParsingException {

    @NotNull OpOutputModelProjectionBodyContents body = parseModelBody(psi.getOpOutputModelProjectionBody());
    body.includeInDefault = psi.getPlus() != null;

    final boolean noSpecificKindProjection = psi.getClass().equals(IdlOpOutputModelProjectionImpl.class);
    switch (type.kind()) {
      case RECORD:
        if (noSpecificKindProjection)
          return createDefaultModelProjection(type, body.includeInDefault, psi);
        ensureModelKind(psi, IdlOpOutputRecordModelProjection.class, TypeKind.RECORD);
        return parseRecordModelProjection((RecordType) type,
                                          body.includeInDefault,
                                          body.params,
                                          (IdlOpOutputRecordModelProjection) psi,
                                          typesResolver
        );
      case LIST:
        if (noSpecificKindProjection)
          return createDefaultModelProjection(type, body.includeInDefault, psi);
        ensureModelKind(psi, IdlOpOutputListModelProjection.class, TypeKind.LIST);
        throw new ProjectionParsingException("Unsupported type kind: " + type.kind(), psi);
      case MAP:
        if (noSpecificKindProjection)
          return createDefaultModelProjection(type, body.includeInDefault, psi);
        ensureModelKind(psi, IdlOpOutputMapModelProjection.class, TypeKind.MAP);
        throw new ProjectionParsingException("Unsupported type kind: " + type.kind(), psi);
      case ENUM:
        if (!noSpecificKindProjection)
          wrongProjectionKind(psi, TypeKind.ENUM);
        throw new ProjectionParsingException("Unsupported type kind: " + type.kind(), psi);
      case PRIMITIVE:
        if (!noSpecificKindProjection)
          wrongProjectionKind(psi, TypeKind.PRIMITIVE);
        return parsePrimitiveModelProjection((PrimitiveType) type,
                                             body.includeInDefault,
                                             body.params
        );
      case UNION:
        throw new ProjectionParsingException("Unsupported type kind: " + type.kind(), psi);
      default:
        throw new ProjectionParsingException("Unknown type kind: " + type.kind(), psi);
    }
  }

  private static void ensureModelKind(@NotNull IdlOpOutputModelProjection psi,
                                      @NotNull Class<? extends IdlOpOutputModelProjection> expectedClass,
                                      @NotNull TypeKind expectedKind) throws ProjectionParsingException {
    if (!(expectedClass.isAssignableFrom(psi.getClass())))
      wrongProjectionKind(psi, expectedKind);
  }

  private static void wrongProjectionKind(@NotNull IdlOpOutputModelProjection psi, @NotNull TypeKind expectedKind)
      throws ProjectionParsingException {
    String actualKind = "Unknown (" + psi.getClass().getName() + ")";
    if (psi instanceof IdlOpOutputRecordModelProjection)
      actualKind = TypeKind.RECORD.toString();
    else if (psi instanceof IdlOpOutputMapModelProjection)
      actualKind = TypeKind.MAP.toString();
    else if (psi instanceof IdlOpOutputListModelProjection)
      actualKind = TypeKind.LIST.toString();
    // TODO rest

    throw new ProjectionParsingException(MessageFormat.format("Unexpected projection kind ''{0}'', expected ''{1}''",
                                                              actualKind,
                                                              expectedKind
    ), psi);
  }

  @NotNull
  private static OpOutputModelProjectionBodyContents parseModelBody(@Nullable IdlOpOutputModelProjectionBody body) {
    final OpOutputModelProjectionBodyContents res = new OpOutputModelProjectionBodyContents();
    if (body != null) {
      @NotNull List<IdlOpOutputModelProjectionBodyPart> parts = body.getOpOutputModelProjectionBodyPartList();
      for (IdlOpOutputModelProjectionBodyPart part : parts) {
        @Nullable IdlOpParameters opParameters = part.getOpParameters();
        if (opParameters != null) {
          @NotNull List<IdlOpParamProjection> paramProjections = opParameters.getOpParamProjectionList();

          // todo convert input projection, put it into param
        }

        // todo custom params
      }

    }

    return res;
  }

  private static OpOutputModelProjection<?> createDefaultModelProjection(@NotNull DatumType type,
                                                                            boolean includeInDefault,
                                                                            @NotNull PsiElement location)
      throws ProjectionParsingException {

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
          throw new ProjectionParsingException(String.format(
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
        throw new ProjectionParsingException("Unsupported type kind: " + type.kind(), location);
      case UNION:
        throw new ProjectionParsingException("Was expecting to get datum model kind, got: " + type.kind(), location);
      case ENUM:
        throw new ProjectionParsingException("Unsupported type kind: " + type.kind(), location);
      case PRIMITIVE:
        return new OpOutputPrimitiveModelProjection((PrimitiveType) type, includeInDefault, null);
      default:
        throw new ProjectionParsingException("Unknown type kind: " + type.kind(), location);
    }
  }

  public static OpOutputRecordModelProjection parseRecordModelProjection(@NotNull RecordType type,
                                                                         boolean includeInDefault,
                                                                         @Nullable Set<OpParam> params,
                                                                         @NotNull IdlOpOutputRecordModelProjection psi,
                                                                         @NotNull TypesResolver typesResolver)
      throws ProjectionParsingException {
    LinkedHashSet<OpOutputFieldProjection> fieldProjections = new LinkedHashSet<>();
    @NotNull List<IdlOpOutputFieldProjection> psiFieldProjections = psi.getOpOutputFieldProjectionList();

    for (IdlOpOutputFieldProjection psiFieldProjection : psiFieldProjections) {
      final String fieldName = psiFieldProjection.getQid().getCanonicalName();
      RecordType.Field field = type.fieldsMap().get(fieldName);
      if (field == null)
        throw new ProjectionParsingException(
            String.format("Can't field projection for '%s', field '%s' not found", type.name(), fieldName),
            psiFieldProjection
        );

      final boolean includeFieldInDefault = psiFieldProjection.getPlus() != null;
      Set<OpParam> fieldParams = null;
      @Nullable IdlOpOutputFieldProjectionBody fieldBody = psiFieldProjection.getOpOutputFieldProjectionBody();
      if (fieldBody != null) {
        for (IdlOpOutputFieldProjectionBodyPart fieldBodyPart : fieldBody.getOpOutputFieldProjectionBodyPartList()) {
          // todo parse fieldParams
          //todo parse field custom params
        }
      }

      OpOutputVarProjection varProjection;

      @Nullable IdlOpOutputVarProjection psiVarProjection = psiFieldProjection.getOpOutputVarProjection();
      if (psiVarProjection == null) {
        @NotNull DataType fieldDataType = field.dataType();
        @Nullable Type.Tag defaultFieldTag = fieldDataType.defaultTag;
        if (defaultFieldTag == null)
          throw new ProjectionParsingException(String.format(
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


  public static OpOutputPrimitiveModelProjection parsePrimitiveModelProjection(@NotNull PrimitiveType type,
                                                                               boolean includeInDefault,
                                                                               @Nullable Set<OpParam> params) {
    // todo custom params, tails
    return new OpOutputPrimitiveModelProjection(type, includeInDefault, params);
  }

  private static class OpOutputModelProjectionBodyContents {
    boolean includeInDefault = false;
    Set<OpParam> params = null;
    // todo custom params

    public void addParam(@NotNull OpParam param) {
      if (params == null) params = new HashSet<>();
      params.add(param);
    }
  }

  // TODO move to appropriate place
  public static class ProjectionParsingException extends Exception {
    @NotNull
    private final PsiElement psi;

    public ProjectionParsingException(@NotNull PsiErrorElement psiErrorElement) {
      // todo extract line numbers, similar to CError/CErrorPosition
      super(psiErrorElement.getErrorDescription());
      psi = psiErrorElement;
    }

    public ProjectionParsingException(@NotNull String message, @NotNull PsiElement location) {
      super(message);
      psi = location;
    }

    @NotNull
    public PsiElement psi() {
      return psi;
    }
  }

}
