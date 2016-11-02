package io.epigraph.idl.operations;

import com.intellij.psi.PsiElement;
import io.epigraph.idl.TypeRefs;
import io.epigraph.idl.parser.psi.*;
import io.epigraph.projections.Annotation;
import io.epigraph.projections.Annotations;
import io.epigraph.projections.ProjectionPsiParserUtil;
import io.epigraph.projections.ProjectionUtils;
import io.epigraph.projections.op.delete.OpDeleteProjectionsPsiParser;
import io.epigraph.projections.op.input.OpInputProjectionsPsiParser;
import io.epigraph.projections.op.output.OpOutputFieldProjection;
import io.epigraph.projections.op.output.OpOutputProjectionsPsiParser;
import io.epigraph.projections.op.path.OpFieldPath;
import io.epigraph.projections.op.path.OpPathPsiParser;
import io.epigraph.projections.op.path.OpVarPath;
import io.epigraph.psi.EpigraphPsiUtil;
import io.epigraph.psi.PsiProcessingError;
import io.epigraph.psi.PsiProcessingException;
import io.epigraph.refs.TypesResolver;
import io.epigraph.refs.ValueTypeRef;
import io.epigraph.types.DataType;
import io.epigraph.types.DatumType;
import io.epigraph.types.Type;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
      annotations = ProjectionPsiParserUtil.parseAnnotation(annotations, part.getAnnotation());

      pathPsi = getPsiPart(pathPsi, part.getOperationPath(), "path", errors);
      outputProjectionPsi =
          getPsiPart(outputProjectionPsi, part.getOperationOutputProjection(), "output projection", errors);
    }

    @Nullable OpFieldPath fieldPath = parsePath(resourceType, pathPsi, resolver, errors);
    @Nullable OpVarPath varPath = fieldPath == null ? null : fieldPath.projection();

    if (outputProjectionPsi == null)
      throw new PsiProcessingException("Output projection must be specified", psi, errors);

    OpOutputFieldProjection outputProjection = OpOutputProjectionsPsiParser.parseFieldProjection(
        resolveOutputType(resourceType, varPath, null, resolver, errors),
        true,
        outputProjectionPsi.getOpOutputFieldProjection(),
        resolver,
        errors
    );

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
      annotations = ProjectionPsiParserUtil.parseAnnotation(annotations, part.getAnnotation());

      pathPsi = getPsiPart(pathPsi, part.getOperationPath(), "path", errors);
      inputTypePsi = getPsiPart(inputTypePsi, part.getOperationInputType(), "input kind", errors);
      inputProjectionPsi =
          getPsiPart(inputProjectionPsi, part.getOperationInputProjection(), "input projection", errors);
      outputTypePsi = getPsiPart(outputTypePsi, part.getOperationOutputType(), "output kind", errors);
      outputProjectionPsi =
          getPsiPart(outputProjectionPsi, part.getOperationOutputProjection(), "output projection", errors);
    }

    @Nullable OpFieldPath fieldPath = parsePath(resourceType, pathPsi, resolver, errors);
    OpVarPath varPath = fieldPath == null ? null : fieldPath.projection();

    if (inputProjectionPsi == null)
      throw new PsiProcessingException("Input projection must be specified", psi, errors);

    if (outputTypePsi == null)
      throw new PsiProcessingException("Output kind must be specified", psi, errors);

    if (outputProjectionPsi == null)
      throw new PsiProcessingException("Output projection must be specified", psi, errors);

    return new CreateOperationIdl(
        parseOperationName(psi.getOperationName()),
        Annotations.fromMap(annotations),
        fieldPath,
        OpInputProjectionsPsiParser.parseModelProjection(
            resolveInputType(resourceType, varPath, inputTypePsi, resolver, psi, errors),
            true,
            null,
            Annotations.EMPTY,
            null,
            inputProjectionPsi.getOpInputModelProjection(),
            resolver
        ).projection(),
        OpOutputProjectionsPsiParser.parseFieldProjection(
            resolveOutputType(resourceType, varPath, outputTypePsi, resolver, errors),
            true,
            outputProjectionPsi.getOpOutputFieldProjection(),
            resolver,
            errors
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
      annotations = ProjectionPsiParserUtil.parseAnnotation(annotations, part.getAnnotation());

      pathPsi = getPsiPart(pathPsi, part.getOperationPath(), "path", errors);
      inputTypePsi = getPsiPart(inputTypePsi, part.getOperationInputType(), "input kind", errors);
      inputProjectionPsi =
          getPsiPart(inputProjectionPsi, part.getOperationInputProjection(), "input projection", errors);
      outputTypePsi = getPsiPart(outputTypePsi, part.getOperationOutputType(), "output kind", errors);
      outputProjectionPsi =
          getPsiPart(outputProjectionPsi, part.getOperationOutputProjection(), "output projection", errors);
    }

    @Nullable OpFieldPath fieldPath = parsePath(resourceType, pathPsi, resolver, errors);

    if (inputProjectionPsi == null)
      throw new PsiProcessingException("Input projection must be specified", psi, errors);

    if (outputProjectionPsi == null)
      throw new PsiProcessingException("Output projection must be specified", psi, errors);

    @Nullable final OpVarPath varPath = fieldPath == null ? null : fieldPath.projection();

    return new UpdateOperationIdl(
        parseOperationName(psi.getOperationName()),
        Annotations.fromMap(annotations),
        fieldPath,
        OpInputProjectionsPsiParser.parseModelProjection(
            resolveInputType(resourceType, varPath, inputTypePsi, resolver, psi, errors),
            true,
            null,
            Annotations.EMPTY,
            null,
            inputProjectionPsi.getOpInputModelProjection(),
            resolver
        ).projection(),
        OpOutputProjectionsPsiParser.parseFieldProjection(
            resolveOutputType(resourceType, varPath, outputTypePsi, resolver, errors),
            true,
            outputProjectionPsi.getOpOutputFieldProjection(),
            resolver,
            errors
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
      annotations = ProjectionPsiParserUtil.parseAnnotation(annotations, part.getAnnotation());

      pathPsi = getPsiPart(pathPsi, part.getOperationPath(), "path", errors);
      deleteProjectionPsi =
          getPsiPart(deleteProjectionPsi, part.getOperationDeleteProjection(), "delete projection", errors);
      outputTypePsi = getPsiPart(outputTypePsi, part.getOperationOutputType(), "output kind", errors);
      outputProjectionPsi =
          getPsiPart(outputProjectionPsi, part.getOperationOutputProjection(), "output projection", errors);
    }

    @Nullable OpFieldPath fieldPath = parsePath(resourceType, pathPsi, resolver, errors);

    if (deleteProjectionPsi == null)
      throw new PsiProcessingException("Delete projection must be specified", psi, errors);

    if (outputTypePsi == null)
      throw new PsiProcessingException("Output kind must be specified", psi, errors);

    if (outputProjectionPsi == null)
      throw new PsiProcessingException("Output projection must be specified", psi, errors);

    return new DeleteOperationIdl(
        parseOperationName(psi.getOperationName()),
        Annotations.fromMap(annotations),
        fieldPath,
        OpDeleteProjectionsPsiParser.parseFieldProjection(
            resolveDeleteType(resourceType, fieldPath == null ? null : fieldPath.projection()),
            deleteProjectionPsi.getOpDeleteFieldProjection(),
            resolver
        ),
        OpOutputProjectionsPsiParser.parseFieldProjection(
            resolveOutputType(
                resourceType,
                fieldPath == null ? null : fieldPath.projection(),
                outputTypePsi,
                resolver,
                errors
            ),
            true,
            outputProjectionPsi.getOpOutputFieldProjection(),
            resolver,
            errors
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
      annotations = ProjectionPsiParserUtil.parseAnnotation(annotations, part.getAnnotation());

      methodPsi = getPsiPart(methodPsi, part.getOperationMethod(), "HTTP method", errors);
      pathPsi = getPsiPart(pathPsi, part.getOperationPath(), "path", errors);
      inputTypePsi = getPsiPart(inputTypePsi, part.getOperationInputType(), "input kind", errors);
      inputProjectionPsi =
          getPsiPart(inputProjectionPsi, part.getOperationInputProjection(), "input projection", errors);
      outputTypePsi = getPsiPart(outputTypePsi, part.getOperationOutputType(), "output kind", errors);
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

    if (outputProjectionPsi == null)
      throw new PsiProcessingException("Output projection must be specified", psi, errors);

    @Nullable OpFieldPath fieldPath = parsePath(resourceType, pathPsi, resolver, errors);

    return new CustomOperationIdl(
        method,
        parseOperationName(psi.getOperationName()),
        Annotations.fromMap(annotations),
        fieldPath,
        inputProjectionPsi == null ? null : OpInputProjectionsPsiParser.parseModelProjection(
            resolveInputType(
                resourceType,
                fieldPath == null ? null : fieldPath.projection(),
                inputTypePsi,
                resolver,
                psi,
                errors
            ),
            true,
            null,
            Annotations.EMPTY,
            null,
            inputProjectionPsi.getOpInputModelProjection(),
            resolver
        ).projection(),
        OpOutputProjectionsPsiParser.parseFieldProjection(
            resolveOutputType(
                resourceType,
                fieldPath == null ? null : fieldPath.projection(),
                outputTypePsi,
                resolver,
                errors
            ),
            true,
            outputProjectionPsi.getOpOutputFieldProjection(),
            resolver,
            errors
        ),
        EpigraphPsiUtil.getLocation(psi)
    );
  }

//  @Nullable
//  private static List<OpParam> parseParam(
//      @Nullable List<OpParam> params,
//      @Nullable IdlOpParam paramPsi,
//      @NotNull TypesResolver resolver) throws PsiProcessingException {
//
//    if (paramPsi == null) return params;
//    if (params == null) params = new ArrayList<>();
//
//    params.add(OpParserUtil.parseParameter(paramPsi, resolver));
//
//    return params;
//  }

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
      @Nullable IdlOperationOutputType outputTypePsi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    if (outputTypePsi == null) {
      if (opVarPath == null) return resourceType;
      else return ProjectionUtils.tipType(opVarPath);
    }

    @NotNull final IdlValueTypeRef typeRefPsi = outputTypePsi.getValueTypeRef();
    @NotNull final ValueTypeRef valueTypeRef = TypeRefs.fromPsi(typeRefPsi);
    @Nullable final DataType dataType = resolver.resolve(valueTypeRef);
    if (dataType == null) throw new PsiProcessingException("Can't resolve output kind", typeRefPsi, errors);
    return dataType;
  }

  @NotNull
  private static DatumType resolveInputType(
      @NotNull DataType resourceType,
      @Nullable OpVarPath path,
      @Nullable IdlOperationInputType inputTypePsi,
      @NotNull TypesResolver resolver,
      @NotNull PsiElement location,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    if (inputTypePsi == null) {
      if (path == null) {

        @NotNull Type rtt = resourceType.type;

        if (rtt instanceof DatumType) return (DatumType) rtt;

        @Nullable
        final Type.Tag defaultTag = resourceType.defaultTag;

        if (defaultTag == null)
          throw new PsiProcessingException(
              "Neither input kind nor operation path is specified, and resource kind has no default tag",
              location,
              errors
          );
        else return defaultTag.type;
      } else {
        @NotNull final DataType tipType = ProjectionUtils.tipType(path);

        @NotNull final Type ttt = tipType.type;

        if (ttt instanceof DatumType) return (DatumType) ttt;

        @Nullable
        final Type.Tag defaultTag = tipType.defaultTag;

        if (defaultTag == null)
          throw new PsiProcessingException(
              "Path tip kind doesn't define default tag",
              location,
              errors
          );
        else return defaultTag.type;
      }
    }
    @NotNull final IdlTypeRef typeRefPsi = inputTypePsi.getTypeRef();
    @Nullable final DatumType datumType = TypeRefs.fromPsi(typeRefPsi).resolveDatumType(resolver);
    if (datumType == null)
      throw new PsiProcessingException("Can't resolve input kind", typeRefPsi, errors);
    return datumType;
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
      return OpPathPsiParser.parseFieldPath(type, varPathPsi, resolver);
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
