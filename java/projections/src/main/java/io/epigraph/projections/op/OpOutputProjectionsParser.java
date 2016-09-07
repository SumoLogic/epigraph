package io.epigraph.projections.op;

import com.intellij.psi.PsiErrorElement;
import io.epigraph.idl.parser.psi.*;
import io.epigraph.types.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    if (psi.getDefault() != null) {
      final OpOutputModelProjection<?, ?> parsedModelProjection;
      final boolean includeInDefault;
      // parse default tag
      if (dataType.defaultTag == null)
        throw new ProjectionParsingException(
            "Can't parse default tag projection for '" + type.name() + "', default tag not specified");

      @Nullable IdlOpOutputModelProjection modelProjection = psi.getOpOutputModelProjection();
      if (modelProjection == null) {
//        throw new ProjectionParsingException(
//            "Can't parse default tag projection for '" + type.name() + "', model projection not specified");
        parsedModelProjection = createDefaultModelProjection(dataType.defaultTag.type);
        includeInDefault = false;
      } else {

        @NotNull DatumType tagType = dataType.defaultTag.type;
        includeInDefault = parseModelBody(modelProjection.getOpOutputModelProjectionBody()).includeInDefault;

        parsedModelProjection = parseModelProjection(tagType, modelProjection, typesResolver);
      }

      tagProjections.add(new OpOutputTagProjection(dataType.defaultTag, includeInDefault, parsedModelProjection));
    } else {
      // parse list of tags
      @NotNull List<IdlOpOutputTagProjection> psiTagProjections = psi.getOpOutputTagProjectionList();
      for (IdlOpOutputTagProjection psiTagProjection : psiTagProjections) {
        String tagName = psiTagProjection.getQid().getText(); // TODO get canonical name
        Type.Tag tag = type.tagsMap().get(tagName);

        if (tag == null)
          throw new ProjectionParsingException(
              String.format("Can't parse tag projection for '%s', tag '%s' not found", type.name(), tagName)
          );

        final OpOutputModelProjection<?, ?> parsedModelProjection;
        final boolean includeInDefault;

        @NotNull DatumType tagType = tag.type;
        @Nullable IdlOpOutputModelProjection modelProjection = psiTagProjection.getOpOutputModelProjection();
        if (modelProjection == null) {
//          throw new ProjectionParsingException(
//              String.format("Can't parse tag projection for '%s', model projection not specified", type.name())
//          );
          parsedModelProjection = createDefaultModelProjection(tagType);
          includeInDefault = false;
        } else {
          parsedModelProjection = parseModelProjection(tagType, modelProjection, typesResolver);
          includeInDefault = parseModelBody(modelProjection.getOpOutputModelProjectionBody()).includeInDefault;
        }

        tagProjections.add(new OpOutputTagProjection(tag, includeInDefault, parsedModelProjection));
      }
    }

    return new OpOutputVarProjection(type, tagProjections);
  }

  private static OpOutputVarProjection createDefaultVarProjection(@NotNull Type type, @NotNull Type.Tag tag)
      throws ProjectionParsingException {
    return new OpOutputVarProjection(type, new OpOutputTagProjection(
        tag,
        true,
        createDefaultModelProjection(tag.type)
    ));
  }

  private static OpOutputVarProjection createDefaultVarProjection(@NotNull DatumType type)
      throws ProjectionParsingException {
    return createDefaultVarProjection(type, type.self);
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

  public static OpOutputModelProjection<?, ?> parseModelProjection(@NotNull DatumType type,
                                                                   @NotNull IdlOpOutputModelProjection psi,
                                                                   @NotNull TypesResolver typesResolver)
      throws ProjectionParsingException {

    @NotNull OpOutputModelProjectionBodyContents body = parseModelBody(psi.getOpOutputModelProjectionBody());

    switch (type.kind()) {
      case RECORD:
        return parseRecordModelProjection((RecordType) type,
                                          body.includeInDefault,
                                          body.params,
                                          (IdlOpOutputRecordModelProjection) psi,
                                          typesResolver
        );
      case LIST:
        throw new ProjectionParsingException("Unsupported type kind: " + type.kind());
      case MAP:
        throw new ProjectionParsingException("Unsupported type kind: " + type.kind());
      case UNION:
        throw new ProjectionParsingException("Unsupported type kind: " + type.kind());
      case ENUM:
        throw new ProjectionParsingException("Unsupported type kind: " + type.kind());
      case PRIMITIVE:
        return parsePrimitiveModelProjection((PrimitiveType) type,
                                             body.includeInDefault,
                                             body.params
        );
      default:
        throw new ProjectionParsingException("Unknown type kind: " + type.kind());
    }
  }

  @NotNull
  private static OpOutputModelProjectionBodyContents parseModelBody(@Nullable IdlOpOutputModelProjectionBody body) {
    final OpOutputModelProjectionBodyContents res = new OpOutputModelProjectionBodyContents();
    if (body != null) {
      @NotNull List<IdlOpOutputModelProjectionBodyPart> parts = body.getOpOutputModelProjectionBodyPartList();
      for (IdlOpOutputModelProjectionBodyPart part : parts) {
        if (part.getIncludeInDefault() != null) res.includeInDefault = true;

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

  private static OpOutputModelProjection<?, ?> createDefaultModelProjection(@NotNull DatumType type)
      throws ProjectionParsingException {

    switch (type.kind()) {
      case RECORD:
        return new OpOutputRecordModelProjection((RecordType) type,
                                                 true,
                                                 null,
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
          ));

        OpOutputVarProjection itemVarProjection = createDefaultVarProjection(elementType.type, defaultTag);
        return new OpOutputListModelProjection(listType,
                                               true,
                                               null,
                                               itemVarProjection,
                                               null
        );
      case MAP:
        throw new ProjectionParsingException("Unsupported type kind: " + type.kind());
      case UNION:
        throw new ProjectionParsingException("Was expecting to get datum model kind, got: " + type.kind());
      case ENUM:
        throw new ProjectionParsingException("Unsupported type kind: " + type.kind());
      case PRIMITIVE:
        return new OpOutputPrimitiveModelProjection((PrimitiveType) type, true, null, null);
      default:
        throw new ProjectionParsingException("Unknown type kind: " + type.kind());
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
      final String fieldName = psiFieldProjection.getQid().getText(); // todo get canonical name
      RecordType.Field field = type.fieldsMap().get(fieldName);
      if (field == null)
        throw new ProjectionParsingException(
            String.format("Can't field projection for '%s', field '%s' not found", type.name(), fieldName)
        );

      boolean includeFieldInDefault = false;
      Set<OpParam> fieldParams = null;
      @Nullable IdlOpOutputFieldProjectionBody fieldBody = psiFieldProjection.getOpOutputFieldProjectionBody();
      if (fieldBody != null) {
        for (IdlOpOutputFieldProjectionBodyPart fieldBodyPart : fieldBody.getOpOutputFieldProjectionBodyPartList()) {
          if (fieldBodyPart.getIncludeInDefault() != null) includeFieldInDefault = true;

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
          ));

        varProjection = createDefaultVarProjection(fieldDataType.type, defaultFieldTag);
      } else {
        varProjection = parseVarProjection(field.dataType(), psiVarProjection, typesResolver);
      }

      fieldProjections.add(new OpOutputFieldProjection(field, fieldParams, varProjection, includeFieldInDefault));
    }

    final LinkedHashSet<OpOutputRecordModelProjection> tail;

    @NotNull List<IdlOpOutputRecordPolyBranch> branches = psi.getOpOutputRecordPolyBranchList();
    if (branches.isEmpty()) tail = null;
    else {
      tail = new LinkedHashSet<>();
      for (IdlOpOutputRecordPolyBranch branch : branches) {
        @Nullable IdlFqnTypeRef fqnTypeRef = branch.getFqnTypeRef();

        if (fqnTypeRef == null)
          throw new ProjectionParsingException(String.format(
              "Can't build polymorphic branch for '%s', branch type not specified", type
          ));

        @Nullable DatumType tailType = typesResolver.resolveDatumType(fqnTypeRef.getFqn().getFqn());
        if (tailType == null)
          throw new ProjectionParsingException(String.format(
              "Can't build polymorphic branch for '%s', tail type '%s' not found", type, fqnTypeRef.getFqn().getFqn()
          ));

        if (!(tailType instanceof RecordType))
          throw new ProjectionParsingException(String.format(
              "Can't build polymorphic branch for '%s', tail type '%s' is not a record type", type, tailType.name()
          ));

        @Nullable
        IdlOpOutputRecordModelProjection psiBranchProjection = branch.getOpOutputRecordModelProjection();
        if (psiBranchProjection == null) // build default one?
          throw new ProjectionParsingException(String.format(
              "Can't build polymorphic branch for '%s', tail type '%s' doesn't have a projection",
              type,
              tailType.name()
          ));

        @NotNull OpOutputModelProjectionBodyContents body = parseModelBody(branch.getOpOutputModelProjectionBody());

        parseRecordModelProjection((RecordType) tailType,
                                   body.includeInDefault,
                                   body.params,
                                   psiBranchProjection,
                                   typesResolver
        );
      }
    }

    return new OpOutputRecordModelProjection(type, includeInDefault, params, fieldProjections, tail);
  }


  public static OpOutputPrimitiveModelProjection parsePrimitiveModelProjection(@NotNull PrimitiveType type,
                                                                               boolean includeInDefault,
                                                                               @Nullable Set<OpParam> params) {
    // todo custom params, tails
    return new OpOutputPrimitiveModelProjection(type, includeInDefault, params, null);
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
    public ProjectionParsingException(@NotNull PsiErrorElement psiErrorElement) {
      // todo extract line numbers, similar to CError/CErrorPosition
      super(psiErrorElement.getErrorDescription());
    }

    public ProjectionParsingException(@NotNull String message) {
      super(message);
    }
  }

}
