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

package ws.epigraph.idl.operations;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.idl.TypeRefs;
import ws.epigraph.idl.parser.psi.*;
import ws.epigraph.projections.Annotation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.IdlProjectionPsiParserUtil;
import ws.epigraph.projections.ProjectionUtils;
import ws.epigraph.projections.op.OpParams;
import ws.epigraph.projections.op.delete.OpDeleteProjectionsPsiParser;
import ws.epigraph.projections.op.input.OpInputProjectionsPsiParser;
import ws.epigraph.projections.op.output.OpOutputFieldProjection;
import ws.epigraph.projections.op.output.OpOutputProjectionsPsiParser;
import ws.epigraph.projections.op.output.OpOutputVarProjection;
import ws.epigraph.projections.op.path.OpFieldPath;
import ws.epigraph.projections.op.path.OpPathPsiParser;
import ws.epigraph.projections.op.path.OpVarPath;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingError;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.refs.ValueTypeRef;
import ws.epigraph.types.DataType;
import ws.epigraph.types.DatumType;
import ws.epigraph.types.Type;

import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OperationsPsiParser {
  @NotNull
  public static OperationIdl parseOperation(
      @NotNull DataType resourceType,
      @NotNull IdlOperationDef psi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    @Nullable IdlReadOperationDef readOperationDef = psi.getReadOperationDef();
    if (readOperationDef != null) return parseRead(resourceType, readOperationDef, resolver, errors);

    @Nullable IdlCreateOperationDef createOperationDef = psi.getCreateOperationDef();
    if (createOperationDef != null) return parseCreate(resourceType, createOperationDef, resolver, errors);

    @Nullable IdlUpdateOperationDef updateOperationDef = psi.getUpdateOperationDef();
    if (updateOperationDef != null) return parseUpdate(resourceType, updateOperationDef, resolver, errors);

    @Nullable IdlDeleteOperationDef deleteOperationDef = psi.getDeleteOperationDef();
    if (deleteOperationDef != null) return parseDelete(resourceType, deleteOperationDef, resolver, errors);

    @Nullable IdlCustomOperationDef customOperationDef = psi.getCustomOperationDef();
    if (customOperationDef != null) return parseCustom(resourceType, customOperationDef, resolver, errors);

    throw new PsiProcessingException("Incomplete operation statement", psi, errors);
  }

  @NotNull
  private static ReadOperationIdl parseRead(
      @NotNull DataType resourceType,
      @NotNull IdlReadOperationDef psi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    Map<String, Annotation> annotations = null;

    IdlOperationPath pathPsi = null;
    IdlOperationOutputProjection outputProjectionPsi = null;

    for (IdlReadOperationBodyPart part : psi.getReadOperationBodyPartList()) {
      annotations = IdlProjectionPsiParserUtil.parseAnnotation(annotations, part.getAnnotation(), errors);

      pathPsi = getPsiPart(pathPsi, part.getOperationPath(), "path", errors);
      outputProjectionPsi =
          getPsiPart(outputProjectionPsi, part.getOperationOutputProjection(), "output projection", errors);
    }

    @Nullable OpFieldPath fieldPath = parsePath(resourceType, pathPsi, resolver, errors);
//    @Nullable OpVarPath varPath = fieldPath == null ? null : fieldPath.projection();

    final OpOutputFieldProjection outputProjection = parseOutputProjection(
        resolveOutputType(
            resourceType,
            fieldPath == null ? null : fieldPath.projection(),
            null,
            resolver,
            errors
        ), outputProjectionPsi, resolver, psi, errors
    );

//    if (outputProjectionPsi == null)
//      throw new PsiProcessingException("Output projection must be specified", psi, errors);
//
//    OpOutputFieldProjection outputProjection = OpOutputProjectionsPsiParser.parseFieldProjection(
//        resolveOutputType(resourceType, varPath, null, resolver, errors),
//        true,
//        outputProjectionPsi.getOpOutputFieldProjection(),
//        resolver,
//        errors
//    );

    return new ReadOperationIdl(
        parseOperationName(psi.getOperationName()),
        Annotations.fromMap(annotations),
        fieldPath,
        outputProjection,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  @NotNull
  private static CreateOperationIdl parseCreate(
      @NotNull DataType resourceType,
      @NotNull IdlCreateOperationDef psi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    Map<String, Annotation> annotations = null;

    IdlOperationPath pathPsi = null;
    IdlOperationInputType inputTypePsi = null;
    IdlOperationInputProjection inputProjectionPsi = null;
    IdlOperationOutputType outputTypePsi = null;
    IdlOperationOutputProjection outputProjectionPsi = null;

    for (IdlCreateOperationBodyPart part : psi.getCreateOperationBodyPartList()) {
      annotations = IdlProjectionPsiParserUtil.parseAnnotation(annotations, part.getAnnotation(), errors);

      pathPsi = getPsiPart(pathPsi, part.getOperationPath(), "path", errors);
      inputTypePsi = getPsiPart(inputTypePsi, part.getOperationInputType(), "input type", errors);
      inputProjectionPsi =
          getPsiPart(inputProjectionPsi, part.getOperationInputProjection(), "input projection", errors);
      outputTypePsi = getPsiPart(outputTypePsi, part.getOperationOutputType(), "output type", errors);
      outputProjectionPsi =
          getPsiPart(outputProjectionPsi, part.getOperationOutputProjection(), "output projection", errors);
    }

    @Nullable OpFieldPath fieldPath = parsePath(resourceType, pathPsi, resolver, errors);
    OpVarPath varPath = fieldPath == null ? null : fieldPath.projection();

    if (inputProjectionPsi == null)
      throw new PsiProcessingException("Input projection must be specified", psi, errors);

    @Nullable final IdlOpInputFieldProjection inputFieldProjectionPsi = inputProjectionPsi.getOpInputFieldProjection();
    if (inputFieldProjectionPsi == null)
      throw new PsiProcessingException("Input projection must be specified", inputProjectionPsi, errors);

    return new CreateOperationIdl(
        parseOperationName(psi.getOperationName()),
        Annotations.fromMap(annotations),
        fieldPath,
        OpInputProjectionsPsiParser.parseFieldProjection(
            resolveInputType(resourceType, varPath, inputTypePsi, resolver, errors),
            true,
            inputFieldProjectionPsi,
            resolver,
            errors
        ),
        parseOutputProjection(
            resolveOutputType(
                resourceType,
                fieldPath == null ? null : fieldPath.projection(),
                outputTypePsi,
                resolver,
                errors
            ), outputProjectionPsi, resolver, psi, errors
        ),
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  @NotNull
  private static UpdateOperationIdl parseUpdate(
      @NotNull DataType resourceType,
      @NotNull IdlUpdateOperationDef psi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    Map<String, Annotation> annotations = null;

    IdlOperationPath pathPsi = null;
    IdlOperationInputType inputTypePsi = null;
    IdlOperationInputProjection inputProjectionPsi = null;
    IdlOperationOutputType outputTypePsi = null;
    IdlOperationOutputProjection outputProjectionPsi = null;

    for (IdlUpdateOperationBodyPart part : psi.getUpdateOperationBodyPartList()) {
      annotations = IdlProjectionPsiParserUtil.parseAnnotation(annotations, part.getAnnotation(), errors);

      pathPsi = getPsiPart(pathPsi, part.getOperationPath(), "path", errors);
      inputTypePsi = getPsiPart(inputTypePsi, part.getOperationInputType(), "input type", errors);
      inputProjectionPsi =
          getPsiPart(inputProjectionPsi, part.getOperationInputProjection(), "input projection", errors);
      outputTypePsi = getPsiPart(outputTypePsi, part.getOperationOutputType(), "output type", errors);
      outputProjectionPsi =
          getPsiPart(outputProjectionPsi, part.getOperationOutputProjection(), "output projection", errors);
    }

    @Nullable OpFieldPath fieldPath = parsePath(resourceType, pathPsi, resolver, errors);

    if (inputProjectionPsi == null)
      throw new PsiProcessingException("Input projection must be specified", psi, errors);

    @Nullable final IdlOpInputFieldProjection inputFieldProjectionPsi = inputProjectionPsi.getOpInputFieldProjection();
    if (inputFieldProjectionPsi == null)
      throw new PsiProcessingException("Input projection must be specified", inputProjectionPsi, errors);

    @Nullable final OpVarPath varPath = fieldPath == null ? null : fieldPath.projection();

    return new UpdateOperationIdl(
        parseOperationName(psi.getOperationName()),
        Annotations.fromMap(annotations),
        fieldPath,
        OpInputProjectionsPsiParser.parseFieldProjection(
            resolveInputType(resourceType, varPath, inputTypePsi, resolver, errors),
            true,
            inputFieldProjectionPsi,
            resolver,
            errors
        ),
        parseOutputProjection(
            resolveOutputType(
                resourceType,
                fieldPath == null ? null : fieldPath.projection(),
                outputTypePsi,
                resolver,
                errors
            ), outputProjectionPsi, resolver, psi, errors
        ),
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  @NotNull
  private static DeleteOperationIdl parseDelete(
      @NotNull DataType resourceType,
      @NotNull IdlDeleteOperationDef psi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    Map<String, Annotation> annotations = null;

    IdlOperationPath pathPsi = null;
    IdlOperationDeleteProjection deleteProjectionPsi = null;
    IdlOperationOutputType outputTypePsi = null;
    IdlOperationOutputProjection outputProjectionPsi = null;

    for (IdlDeleteOperationBodyPart part : psi.getDeleteOperationBodyPartList()) {
      annotations = IdlProjectionPsiParserUtil.parseAnnotation(annotations, part.getAnnotation(), errors);

      pathPsi = getPsiPart(pathPsi, part.getOperationPath(), "path", errors);
      deleteProjectionPsi =
          getPsiPart(deleteProjectionPsi, part.getOperationDeleteProjection(), "delete projection", errors);
      outputTypePsi = getPsiPart(outputTypePsi, part.getOperationOutputType(), "output type", errors);
      outputProjectionPsi =
          getPsiPart(outputProjectionPsi, part.getOperationOutputProjection(), "output projection", errors);
    }

    @Nullable OpFieldPath fieldPath = parsePath(resourceType, pathPsi, resolver, errors);

    if (deleteProjectionPsi == null)
      throw new PsiProcessingException("Delete projection must be specified", psi, errors);

    @Nullable final IdlOpDeleteFieldProjection deleteFieldProjectionPsi =
        deleteProjectionPsi.getOpDeleteFieldProjection();
    if (deleteFieldProjectionPsi == null)
      throw new PsiProcessingException("Delete projection must be specified", deleteProjectionPsi, errors);

    return new DeleteOperationIdl(
        parseOperationName(psi.getOperationName()),
        Annotations.fromMap(annotations),
        fieldPath,
        OpDeleteProjectionsPsiParser.parseFieldProjection(
            resolveDeleteType(resourceType, fieldPath == null ? null : fieldPath.projection()),
            deleteFieldProjectionPsi,
            resolver,
            errors
        ),
        parseOutputProjection(
            resolveOutputType(
                resourceType,
                fieldPath == null ? null : fieldPath.projection(),
                outputTypePsi,
                resolver,
                errors
            ), outputProjectionPsi, resolver, psi, errors
        ),
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  @NotNull
  private static CustomOperationIdl parseCustom(
      @NotNull DataType resourceType,
      @NotNull IdlCustomOperationDef psi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    Map<String, Annotation> annotations = null;

    IdlOperationMethod methodPsi = null;
    IdlOperationPath pathPsi = null;
    IdlOperationInputType inputTypePsi = null;
    IdlOperationInputProjection inputProjectionPsi = null;
    IdlOperationOutputType outputTypePsi = null;
    IdlOperationOutputProjection outputProjectionPsi = null;

    for (IdlCustomOperationBodyPart part : psi.getCustomOperationBodyPartList()) {
      annotations = IdlProjectionPsiParserUtil.parseAnnotation(annotations, part.getAnnotation(), errors);

      methodPsi = getPsiPart(methodPsi, part.getOperationMethod(), "HTTP method", errors);
      pathPsi = getPsiPart(pathPsi, part.getOperationPath(), "path", errors);
      inputTypePsi = getPsiPart(inputTypePsi, part.getOperationInputType(), "input type", errors);
      inputProjectionPsi =
          getPsiPart(inputProjectionPsi, part.getOperationInputProjection(), "input projection", errors);
      outputTypePsi = getPsiPart(outputTypePsi, part.getOperationOutputType(), "output type", errors);
      outputProjectionPsi =
          getPsiPart(outputProjectionPsi, part.getOperationOutputProjection(), "output projection", errors);
    }

    final HttpMethod method;
    if (methodPsi == null)
      throw new PsiProcessingException("HTTP method must be specified", psi, errors);
    else {
      if (methodPsi.getGet() != null) method = HttpMethod.GET;
      else if (methodPsi.getPost() != null) method = HttpMethod.POST;
      else if (methodPsi.getPut() != null) method = HttpMethod.PUT;
      else if (methodPsi.getDelete() != null) method = HttpMethod.DELETE;
      else throw new PsiProcessingException("HTTP method must be specified", methodPsi, errors);
    }

    @Nullable final IdlOpInputFieldProjection inputFieldProjectionPsi =
        inputProjectionPsi == null ? null : inputProjectionPsi.getOpInputFieldProjection();

    @Nullable OpFieldPath opPath = parsePath(resourceType, pathPsi, resolver, errors);

    return new CustomOperationIdl(
        method,
        parseOperationName(psi.getOperationName()),
        Annotations.fromMap(annotations),
        opPath,
        inputFieldProjectionPsi == null ? null : OpInputProjectionsPsiParser.parseFieldProjection(
            resolveInputType(
                resourceType,
                opPath == null ? null : opPath.projection(),
                inputTypePsi,
                resolver,
                errors
            ),
            true,
            inputFieldProjectionPsi,
            resolver,
            errors
        ),
        parseOutputProjection(
            resolveOutputType(
                resourceType,
                opPath == null ? null : opPath.projection(),
                outputTypePsi,
                resolver,
                errors
            ), outputProjectionPsi, resolver, psi, errors
        ),
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  @NotNull
  private static OpOutputFieldProjection parseOutputProjection(
      final @NotNull DataType outputType,
      final @Nullable IdlOperationOutputProjection outputProjectionPsi,
      final @NotNull TypesResolver resolver,
      final @NotNull PsiElement location,
      final @NotNull List<PsiProcessingError> errors)
      throws PsiProcessingException {

    @Nullable final IdlOpOutputFieldProjection outputFieldProjectionPsi =
        outputProjectionPsi == null ? null : outputProjectionPsi.getOpOutputFieldProjection();

    if (outputProjectionPsi == null || outputFieldProjectionPsi == null) {
      final @NotNull OpOutputVarProjection varProjection =
          OpOutputProjectionsPsiParser.createDefaultVarProjection(outputType, location, errors);

      return new OpOutputFieldProjection(
          OpParams.EMPTY,
          Annotations.EMPTY,
          varProjection,
          EpigraphPsiUtil.getLocation(location)
      );

//      throw new PsiProcessingException("Output projection must be specified", location, errors);
    }

    return OpOutputProjectionsPsiParser.parseFieldProjection(
        outputType,
        outputFieldProjectionPsi,
        resolver,
        errors
    );
  }

  @Contract("null, !null, _, _ -> !null")
  @Nullable
  private static <T extends PsiElement> T getPsiPart(
      @Nullable T prev,
      @Nullable T cur,
      @NotNull String name,
      @NotNull List<PsiProcessingError> errors)
      throws PsiProcessingException {

    if (cur != null) {
      if (prev != null) {
        errors.add(new PsiProcessingError("Operation " + name + " specified twice", cur));
      }

      return cur;
    }

    return prev;
  }

  @NotNull
  private static DataType resolveOutputType(
      @NotNull DataType resourceType,
      @Nullable OpVarPath opVarPath,
      @Nullable IdlOperationOutputType declaredOutputTypePsi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    @Nullable final IdlValueTypeRef typeRefPsi =
        declaredOutputTypePsi == null ? null : declaredOutputTypePsi.getValueTypeRef();

    if (declaredOutputTypePsi == null || typeRefPsi == null) {
      if (opVarPath == null) return resourceType;
      else return ProjectionUtils.tipType(opVarPath);
    }

    @NotNull final ValueTypeRef valueTypeRef = TypeRefs.fromPsi(typeRefPsi, errors);
    @Nullable final DataType dataType = resolver.resolve(valueTypeRef);
    if (dataType == null) throw new PsiProcessingException("Can't resolve output type", typeRefPsi, errors);
    return dataType;
  }

  @NotNull
  private static DataType resolveInputType(
      @NotNull DataType resourceType,
      @Nullable OpVarPath path,
      @Nullable IdlOperationInputType inputTypePsi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    @Nullable final IdlTypeRef typeRefPsi = inputTypePsi == null ? null : inputTypePsi.getTypeRef();
    if (inputTypePsi == null || typeRefPsi == null) {
      if (path == null) {

        @NotNull Type rtt = resourceType.type;

        if (rtt instanceof DatumType) return ((DatumType) rtt).dataType();

        return resourceType;

      } else {

        @NotNull final DataType tipType = ProjectionUtils.tipType(path);

        @NotNull final Type ttt = tipType.type;

        if (ttt instanceof DatumType) return ((DatumType) ttt).dataType();

        return tipType;
      }
    }
    @Nullable final DatumType datumType = TypeRefs.fromPsi(typeRefPsi, errors).resolveDatumType(resolver);
    if (datumType == null)
      throw new PsiProcessingException("Can't resolve input type", typeRefPsi, errors);
    return datumType.dataType();
  }

  @NotNull
  private static DataType resolveDeleteType(
      @NotNull DataType resourceType,
      @Nullable OpVarPath opVarPath) throws PsiProcessingException {

    if (opVarPath == null) return resourceType;
    else return ProjectionUtils.tipType(opVarPath);
  }

  @Nullable
  private static OpFieldPath parsePath(
      @NotNull DataType type,
      @Nullable IdlOperationPath pathPsi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    if (pathPsi != null) {
      final @Nullable IdlOpFieldPath varPathPsi = pathPsi.getOpFieldPath();
      if (varPathPsi == null) {
        errors.add(new PsiProcessingError("Path expression missing", pathPsi));
        return null;
      }
      return OpPathPsiParser.parseFieldPath(type, varPathPsi, resolver, errors);
    } else return null;
  }

  @Nullable
  private static String parseOperationName(@Nullable IdlOperationName namePsi) {
    if (namePsi == null) return null;
    @Nullable IdlQid qid = namePsi.getQid();
    if (qid == null) return null;
    return qid.getCanonicalName();
  }
}
