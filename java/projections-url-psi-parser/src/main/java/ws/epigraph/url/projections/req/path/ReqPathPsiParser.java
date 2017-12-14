/*
 * Copyright 2017 Sumo Logic
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.data.Datum;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.ProjectionsParsingUtil;
import ws.epigraph.projections.op.*;
import ws.epigraph.projections.req.*;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.types.*;
import ws.epigraph.url.parser.psi.*;
import ws.epigraph.url.projections.UrlProjectionsPsiParserUtil;

import java.text.MessageFormat;
import java.util.Collections;

import static ws.epigraph.url.projections.UrlProjectionsPsiParserUtil.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class ReqPathPsiParser {

  private ReqPathPsiParser() {}

  public static ReqProjection<?, ?> parsePath(
      @NotNull OpProjection<?, ?> op,
      @NotNull DataTypeApi dataType,
      @NotNull UrlReqEntityPath psi,
      @NotNull TypesResolver typesResolver,
      @NotNull ReqPathPsiProcessingContext context) throws PsiProcessingException {

    final TypeApi type = dataType.type();

    final @Nullable UrlTagName tagNamePsi = psi.getTagName();

    final @Nullable OpTagProjectionEntry opTagPath = op.singleTagProjection();

    if (opTagPath == null) {
      if (tagNamePsi != null)
        throw new PsiProcessingException(
            String.format("'%s' tags are not supported by the operation", dataType.name()),
            tagNamePsi,
            context
        );

      return ReqEntityProjection.pathEnd(type, EpigraphPsiUtil.getLocation(psi));
    }

    @NotNull UrlReqModelPath modelPathPsi = psi.getReqModelPath();

    final @NotNull TagApi reqTag = ProjectionsParsingUtil.getTag(
        dataType,
        UrlProjectionsPsiParserUtil.getTagName(tagNamePsi),
        op,
        EpigraphPsiUtil.getLocation(psi),
        context
    );
    final TagApi opTag = opTagPath.tag();

    if (!reqTag.equals(opTag)) {
      throw new PsiProcessingException(
          String.format("'%s' tag is not supported by the operation, did you mean '%s'?", reqTag.name(), opTag.name()),
          psi,
          context
      );
    }

    final OpModelProjection<?, ?, ?, ?> opModelProjection = opTagPath.modelProjection();


    final ReqModelProjection<?, ?, ?> modelPath = parseModelProjection(
        opModelProjection,
        opTag.type(),
        modelPathPsi,
        typesResolver,
        context
    );

    return
        type.kind() == TypeKind.ENTITY ?
        ReqEntityProjection.path(
            type,
            new ReqTagProjectionEntry(opTag, modelPath, EpigraphPsiUtil.getLocation(modelPathPsi)),
            EpigraphPsiUtil.getLocation(psi)
        ) : modelPath;
  }

//  private static boolean isModelProjectionEmpty(@Nullable UrlReqModelProjection pathPsi) {
//    return pathPsi == null || (
//        pathPsi.getReqRecordModelProjection() == null &&
//        pathPsi.getReqMapModelProjection() == null
//    );
//  }

  private static @NotNull ReqModelProjection<?, ?, ?> parseModelProjection(
      @NotNull OpModelProjection<?, ?, ?, ?> op,
      @NotNull DatumTypeApi type,
      @NotNull UrlReqModelPath psi,
      @NotNull TypesResolver typesResolver,
      @NotNull ReqPathPsiProcessingContext context)
      throws PsiProcessingException {

    ReqParams params = parseReqParams(psi.getReqParamList(), op.params(), typesResolver, psi, context);
    Directives directives = parseAnnotations(psi.getReqAnnotationList(), context);

    switch (type.kind()) {
      case RECORD:
        @Nullable UrlReqRecordModelPath recordModelProjectionPsi = psi.getReqRecordModelPath();
        if (recordModelProjectionPsi == null)
          throw new PsiProcessingException("Record path must be specified", psi, context);

        ensureModelKind(psi, TypeKind.RECORD, context);
        return parseRecordModelProjection(
            (OpRecordModelProjection) op,
            (RecordTypeApi) type,
            params,
            directives,
            recordModelProjectionPsi,
            typesResolver,
            context
        );
      case MAP:
        @Nullable UrlReqMapModelPath mapModelProjectionPsi = psi.getReqMapModelPath();
        if (mapModelProjectionPsi == null)
          throw new PsiProcessingException("Map path must be specified", psi, context);

        ensureModelKind(psi, TypeKind.MAP, context);

        return parseMapModelProjection(
            (OpMapModelProjection) op,
            (MapTypeApi) type,
            params,
            directives,
            mapModelProjectionPsi,
            typesResolver,
            context
        );
      case LIST:
        throw new PsiProcessingException("Unsupported type kind: " + type.kind(), psi, context);
      case ENUM:
        throw new PsiProcessingException("Unsupported type kind: " + type.kind(), psi, context);
      case PRIMITIVE:
        return parsePrimitiveModelProjection(
            (PrimitiveTypeApi) type,
            params,
            directives,
            psi
        );
      case ENTITY:
        throw new PsiProcessingException("Unsupported type kind: " + type.kind(), psi, context);
      default:
        throw new PsiProcessingException("Unknown type kind: " + type.kind(), psi, context);
    }
  }

  private static void ensureModelKind(
      @NotNull UrlReqModelPath psi, @NotNull TypeKind expectedKind,
      @NotNull ReqPathPsiProcessingContext context) throws PsiProcessingException {

    @Nullable TypeKind actualKind = findProjectionKind(psi);
    if (expectedKind != actualKind)
      throw new PsiProcessingException(MessageFormat.format(
          "Unexpected projection kind ''{0}'', expected ''{1}''",
          actualKind,
          expectedKind
      ), psi, context);
  }

  private static @Nullable TypeKind findProjectionKind(@NotNull UrlReqModelPath psi) {
    if (psi.getReqRecordModelPath() != null) return TypeKind.RECORD;
    if (psi.getReqMapModelPath() != null) return TypeKind.MAP;
    return null;
  }

  private static @NotNull ReqRecordModelProjection parseRecordModelProjection(
      @NotNull OpRecordModelProjection op,
      @NotNull RecordTypeApi type,
      @NotNull ReqParams params,
      @NotNull Directives directives,
      @NotNull UrlReqRecordModelPath psi,
      @NotNull TypesResolver typesResolver,
      @NotNull ReqPathPsiProcessingContext context) throws PsiProcessingException {

    final @NotNull UrlReqFieldPathEntry fieldPathEntryPsi = psi.getReqFieldPathEntry();

    final String fieldName = fieldPathEntryPsi.getQid().getCanonicalName();

    final OpFieldProjectionEntry opFieldEntry = op.fieldProjections().get(fieldName);

    if (opFieldEntry == null)
      throw new PsiProcessingException(
          ProjectionsParsingUtil.unsupportedFieldMsg(fieldName, op.fieldProjections().keySet()),
          fieldPathEntryPsi.getQid(),
          context
      );

    FieldApi field = opFieldEntry.field();

    final ReqFieldProjectionEntry fieldProjection = new ReqFieldProjectionEntry(
        field,
        parseFieldPath(
            field.dataType(),
            opFieldEntry.fieldProjection(),
            fieldPathEntryPsi.getReqFieldPath(),
            typesResolver,
            context
        ),
        EpigraphPsiUtil.getLocation(fieldPathEntryPsi)
    );

    return new ReqRecordModelProjection(
        type,
        false,
        params,
        directives,
        null,
        Collections.singletonMap(fieldName, fieldProjection),
        null,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  public static @NotNull ReqFieldProjection parseFieldPath(
      final @NotNull DataTypeApi fieldType,
      final @NotNull OpFieldProjection op,
      final @NotNull UrlReqFieldPath psi,
      final @NotNull TypesResolver typesResolver,
      @NotNull ReqPathPsiProcessingContext context) throws PsiProcessingException {

//    @NotNull ReqParams fieldParams = parseReqParams(psi.getReqParamList(), op.params(), typesResolver, context);

//    @NotNull Annotations fieldAnnotations = parseAnnotations(psi.getReqAnnotationList(), context);

    @Nullable UrlReqEntityPath fieldEntityProjectionPsi = psi.getReqEntityPath();

    final ReqProjection<?, ?> projection;

    projection = parsePath(op.projection(), fieldType, fieldEntityProjectionPsi, typesResolver, context);

//    final ReadReqPathParsingResult<ReqEntityProjection> fieldVarParsingResult;

    final @NotNull TextLocation fieldLocation = EpigraphPsiUtil.getLocation(psi);

    return new ReqFieldProjection(
//        fieldParams,
//        fieldAnnotations,
        projection,
        fieldLocation
    );
  }

  private static @NotNull ReqMapModelProjection parseMapModelProjection(
      @NotNull OpMapModelProjection op,
      @NotNull MapTypeApi type,
      @NotNull ReqParams params,
      @NotNull Directives directives,
      @NotNull UrlReqMapModelPath psi,
      @NotNull TypesResolver resolver,
      @NotNull ReqPathPsiProcessingContext context)
      throws PsiProcessingException {

    @NotNull ReqKeyProjection keyProjection = parseKeyProjection(
        op.keyProjection(),
        op.type().keyType(),
        psi,
        resolver,
        context
    );

    @Nullable UrlReqEntityPath valueProjectionPsi = psi.getReqEntityPath();

    if (valueProjectionPsi == null)
      throw new PsiProcessingException("Map value projection not specified", psi, context);

    ReqProjection<?, ?> valueProjection =
        parsePath(op.itemsProjection(), type.valueType(), valueProjectionPsi, resolver, context);

    return new ReqMapModelProjection(
        type,
        false,
        params,
        directives,
        null,
        Collections.singletonList(keyProjection),
        true,
        valueProjection,
        null,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  private static @NotNull ReqKeyProjection parseKeyProjection(
      @NotNull OpKeyProjection op,
      @NotNull DatumTypeApi keyType,
      @NotNull UrlReqMapModelPath mapPathPsi,
      @NotNull TypesResolver resolver,
      @NotNull ReqPathPsiProcessingContext context) throws PsiProcessingException {

    final @NotNull ReqParams reqParams =
        parseReqParams(mapPathPsi.getReqParamList(), op.params(), resolver, mapPathPsi, context);
    final @NotNull Directives directives = parseAnnotations(mapPathPsi.getReqAnnotationList(), context);

    final @Nullable Datum keyValue =
        getDatum(mapPathPsi.getDatum(), keyType, resolver, "Error processing map key: ", context);

    if (keyValue == null)
      throw new PsiProcessingException("Null path keys not allowed", mapPathPsi.getDatum(), context);

    return new ReqKeyProjection(
        keyValue,
        reqParams,
        directives,
        EpigraphPsiUtil.getLocation(mapPathPsi)
    );
  }

  private static @NotNull ReqPrimitiveModelProjection parsePrimitiveModelProjection(
      @NotNull PrimitiveTypeApi type,
      @NotNull ReqParams params,
      @NotNull Directives directives,
      @NotNull PsiElement locationPsi) {

    return new ReqPrimitiveModelProjection(
        type,
        false,
        params,
        directives,
        null,
        null,
        EpigraphPsiUtil.getLocation(locationPsi)
    );
  }

}
