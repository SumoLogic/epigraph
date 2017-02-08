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

package ws.epigraph.url.projections.req.path;

import com.intellij.psi.PsiElement;
import ws.epigraph.data.Datum;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.ProjectionUtils;
import ws.epigraph.projections.op.path.*;
import ws.epigraph.projections.req.ReqParams;
import ws.epigraph.projections.req.path.*;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingError;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.types.*;
import ws.epigraph.url.parser.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.List;

import static ws.epigraph.url.projections.UrlProjectionsPsiParserUtil.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class ReqPathPsiParser {

  private ReqPathPsiParser() {}

  public static ReqVarPath parseVarPath(
      @NotNull OpVarPath op,
      @NotNull DataTypeApi dataType,
      @NotNull UrlReqVarPath psi,
      @NotNull TypesResolver typesResolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    final TypeApi type = dataType.type();

    final @Nullable UrlTagName tagNamePsi = psi.getTagName();

    final @Nullable OpTagPath opTagPath = op.singleTagProjection();

    if (opTagPath == null) {
      if (tagNamePsi != null)
        throw new PsiProcessingException(
            String.format("'%s' tags are not supported by the operation", dataType.name()),
            tagNamePsi,
            errors
        );

      return new ReqVarPath(
          type,
          null, // no tags = end of path
          EpigraphPsiUtil.getLocation(psi)
      );
    }

    @NotNull UrlReqModelPath modelPathPsi = psi.getReqModelPath();

    final @NotNull TagApi reqTag = getTag(type, tagNamePsi, dataType.defaultTag(), psi, errors);
    final TagApi opTag = opTagPath.tag();

    if (!reqTag.equals(opTag)) {
      throw new PsiProcessingException(
          String.format("'%s' tag is not supported by the operation, did you mean '%s'?", reqTag.name(), opTag.name()),
          psi,
          errors
      );
    }

    final OpModelPath<?, ?, ?> opModelPath = opTagPath.projection();


    final ReqModelPath<?, ?, ?> parsedModelProjection = parseModelPath(
        opModelPath,
        opTag.type(),
        parseReqParams(psi.getReqParamList(), opModelPath.params(), typesResolver, errors),
        parseAnnotations(psi.getReqAnnotationList(), errors),
        modelPathPsi,
        typesResolver,
        errors
    );

    try {
      return new ReqVarPath(
          type,
          new ReqTagPath(
              opTag, parsedModelProjection, EpigraphPsiUtil.getLocation(modelPathPsi)
          ),
          EpigraphPsiUtil.getLocation(psi)
      );
    } catch (Exception e) {
      throw new PsiProcessingException(e, psi, errors);
    }
  }

//  private static boolean isModelPathEmpty(@Nullable UrlReqModelPath pathPsi) {
//    return pathPsi == null || (
//        pathPsi.getReqRecordModelPath() == null &&
//        pathPsi.getReqMapModelPath() == null
//    );
//  }

  public static @NotNull ReqModelPath<?, ?, ?> parseModelPath(
      @NotNull OpModelPath<?, ?, ?> op,
      @NotNull DatumTypeApi type,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      @NotNull UrlReqModelPath psi,
      @NotNull TypesResolver typesResolver,
      @NotNull List<PsiProcessingError> errors)
      throws PsiProcessingException {

    switch (type.kind()) {
      case RECORD:
        @Nullable UrlReqRecordModelPath recordModelProjectionPsi = psi.getReqRecordModelPath();
        if (recordModelProjectionPsi == null)
          throw new PsiProcessingException("Record path must be specified", psi, errors);

        ensureModelKind(psi, TypeKind.RECORD, errors);
        return parseRecordModelPath(
            (OpRecordModelPath) op,
            (RecordTypeApi) type,
            params,
            annotations,
            recordModelProjectionPsi,
            typesResolver,
            errors
        );
      case MAP:
        @Nullable UrlReqMapModelPath mapModelProjectionPsi = psi.getReqMapModelPath();
        if (mapModelProjectionPsi == null)
          throw new PsiProcessingException("Map path must be specified", psi, errors);

        ensureModelKind(psi, TypeKind.MAP, errors);

        return parseMapModelPath(
            (OpMapModelPath) op,
            (MapTypeApi) type,
            params,
            annotations,
            mapModelProjectionPsi,
            typesResolver,
            errors
        );
      case LIST:
        throw new PsiProcessingException("Unsupported type kind: " + type.kind(), psi, errors);
      case ENUM:
        throw new PsiProcessingException("Unsupported type kind: " + type.kind(), psi, errors);
      case PRIMITIVE:
        return parsePrimitiveModelPath(
            (PrimitiveTypeApi) type,
            params,
            annotations,
            psi
        );
      case UNION:
        throw new PsiProcessingException("Unsupported type kind: " + type.kind(), psi, errors);
      default:
        throw new PsiProcessingException("Unknown type kind: " + type.kind(), psi, errors);
    }
  }

  private static void ensureModelKind(
      @NotNull UrlReqModelPath psi, @NotNull TypeKind expectedKind,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    @Nullable TypeKind actualKind = findProjectionKind(psi);
    if (expectedKind != actualKind)
      throw new PsiProcessingException(MessageFormat.format(
          "Unexpected projection kind ''{0}'', expected ''{1}''",
          actualKind,
          expectedKind
      ), psi, errors);
  }

  private static @Nullable TypeKind findProjectionKind(@NotNull UrlReqModelPath psi) {
    if (psi.getReqRecordModelPath() != null) return TypeKind.RECORD;
    if (psi.getReqMapModelPath() != null) return TypeKind.MAP;
    return null;
  }

  public static @NotNull ReqRecordModelPath parseRecordModelPath(
      @NotNull OpRecordModelPath op,
      @NotNull RecordTypeApi type,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      @NotNull UrlReqRecordModelPath psi,
      @NotNull TypesResolver typesResolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    final @NotNull UrlReqFieldPathEntry fieldPathEntryPsi = psi.getReqFieldPathEntry();

    final String fieldName = fieldPathEntryPsi.getQid().getCanonicalName();

    final OpFieldPathEntry opFieldEntry = op.fieldProjections().get(fieldName);

    if (opFieldEntry == null)
      throw new PsiProcessingException(
          String.format(
              "Field '%s' is not supported by operation, supported fields: {%s}",
              fieldName,
              ProjectionUtils.listFields(op.fieldProjections().keySet())
          ),
          fieldPathEntryPsi.getQid(),
          errors
      );

    FieldApi field = opFieldEntry.field();

    final ReqFieldPathEntry fieldProjection = new ReqFieldPathEntry(
        field,
        parseFieldPath(
            field.dataType(),
            opFieldEntry.fieldProjection(),
            fieldPathEntryPsi.getReqFieldPath(),
            typesResolver,
            errors
        ),
        EpigraphPsiUtil.getLocation(fieldPathEntryPsi)
    );

    return new ReqRecordModelPath(
        type,
        params,
        annotations,
        fieldProjection,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  public static @NotNull ReqFieldPath parseFieldPath(
      final @NotNull DataTypeApi fieldType,
      final @NotNull OpFieldPath op,
      final @NotNull UrlReqFieldPath psi,
      final @NotNull TypesResolver typesResolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

//    @NotNull ReqParams fieldParams = parseReqParams(psi.getReqParamList(), op.params(), typesResolver, errors);

//    @NotNull Annotations fieldAnnotations = parseAnnotations(psi.getReqAnnotationList(), errors);

    @Nullable UrlReqVarPath fieldVarPathPsi = psi.getReqVarPath();

    final ReqVarPath varProjection;

    varProjection = parseVarPath(op.varProjection(), fieldType, fieldVarPathPsi, typesResolver, errors);

//    final ReadReqPathParsingResult<ReqVarPath> fieldVarParsingResult;

    final @NotNull TextLocation fieldLocation = EpigraphPsiUtil.getLocation(psi);

    return new ReqFieldPath(
//        fieldParams,
//        fieldAnnotations,
        varProjection,
        fieldLocation
    );
  }

  public static @NotNull ReqMapModelPath parseMapModelPath(
      @NotNull OpMapModelPath op,
      @NotNull MapTypeApi type,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      @NotNull UrlReqMapModelPath psi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors)
      throws PsiProcessingException {

    @NotNull ReqPathKeyProjection keyProjection = parseKeyProjection(
        op.keyProjection(),
        op.model().keyType(),
        psi,
        resolver,
        errors
    );

    @Nullable UrlReqVarPath valueProjectionPsi = psi.getReqVarPath();

    if (valueProjectionPsi == null)
      throw new PsiProcessingException("Map value projection not specified", psi, errors);

    @NotNull ReqVarPath valueProjection =
        parseVarPath(op.itemsProjection(), type.valueType(), valueProjectionPsi, resolver, errors);

    return new ReqMapModelPath(
        type,
        params,
        annotations,
        keyProjection,
        valueProjection,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  private static @NotNull ReqPathKeyProjection parseKeyProjection(
      @NotNull OpPathKeyProjection op,
      @NotNull DatumTypeApi keyType,
      @NotNull UrlReqMapModelPath mapPathPsi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    final @NotNull ReqParams reqParams = parseReqParams(mapPathPsi.getReqParamList(), op.params(), resolver, errors);
    final @NotNull Annotations annotations = parseAnnotations(mapPathPsi.getReqAnnotationList(), errors);

    final @Nullable Datum keyValue =
        getDatum(mapPathPsi.getDatum(), keyType, resolver, "Error processing map key: ", errors);

    if (keyValue == null) throw new PsiProcessingException("Null path keys not allowed", mapPathPsi.getDatum(), errors);

    return new ReqPathKeyProjection(
        keyValue,
        reqParams,
        annotations,
        EpigraphPsiUtil.getLocation(mapPathPsi)
    );
  }

  public static @NotNull ReqPrimitiveModelPath parsePrimitiveModelPath(
      @NotNull PrimitiveTypeApi type,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      @NotNull PsiElement locationPsi) {

    return new ReqPrimitiveModelPath(
        type,
        params,
        annotations,
        EpigraphPsiUtil.getLocation(locationPsi)
    );
  }

}
