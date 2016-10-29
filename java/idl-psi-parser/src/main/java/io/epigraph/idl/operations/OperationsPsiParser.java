package io.epigraph.idl.operations;

import com.intellij.psi.PsiElement;
import io.epigraph.idl.TypeRefs;
import io.epigraph.idl.parser.psi.*;
import io.epigraph.projections.Annotation;
import io.epigraph.projections.Annotations;
import io.epigraph.projections.ProjectionPsiParserUtil;
import io.epigraph.projections.ProjectionUtils;
import io.epigraph.projections.op.OpParam;
import io.epigraph.projections.op.OpParams;
import io.epigraph.projections.op.OpParserUtil;
import io.epigraph.projections.op.delete.OpDeleteProjectionsPsiParser;
import io.epigraph.projections.op.input.OpInputProjectionsPsiParser;
import io.epigraph.projections.op.output.OpOutputProjectionsPsiParser;
import io.epigraph.projections.op.output.OpOutputVarProjection;
import io.epigraph.projections.op.path.OpPathPsiParser;
import io.epigraph.projections.op.path.OpVarPath;
import io.epigraph.psi.EpigraphPsiUtil;
import io.epigraph.psi.PsiProcessingException;
import io.epigraph.refs.TypesResolver;
import io.epigraph.refs.ValueTypeRef;
import io.epigraph.types.DataType;
import io.epigraph.types.DatumType;
import io.epigraph.types.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
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
      @NotNull TypesResolver resolver) throws PsiProcessingException {

    @Nullable IdlReadOperationDef readOperationDef = psi.getReadOperationDef();
    if (readOperationDef != null) return parseRead(resourceType, readOperationDef, resolver);

    @Nullable IdlCreateOperationDef createOperationDef = psi.getCreateOperationDef();
    if (createOperationDef != null) return parseCreate(resourceType, createOperationDef, resolver);

    @Nullable IdlUpdateOperationDef updateOperationDef = psi.getUpdateOperationDef();
    if (updateOperationDef != null) return parseUpdate(resourceType, updateOperationDef, resolver);

    @Nullable IdlDeleteOperationDef deleteOperationDef = psi.getDeleteOperationDef();
    if (deleteOperationDef != null) return parseDelete(resourceType, deleteOperationDef, resolver);

    @Nullable IdlCustomOperationDef customOperationDef = psi.getCustomOperationDef();
    if (customOperationDef != null) return parseCustom(resourceType, customOperationDef, resolver);

    throw new PsiProcessingException("Incomplete operation statement", psi);
  }

  @NotNull
  private static ReadOperationIdl parseRead(
      @NotNull DataType resourceType,
      @NotNull IdlReadOperationDef psi,
      @NotNull TypesResolver resolver) throws PsiProcessingException {

    List<OpParam> params = null;
    Map<String, Annotation> annotations = null;

    IdlOperationPath pathPsi = null;
    IdlOperationOutputProjection outputProjectionPsi = null;

    for (IdlReadOperationBodyPart part : psi.getReadOperationBodyPartList()) {
      params = parseParam(params, part.getOpParam(), resolver);
      annotations = ProjectionPsiParserUtil.parseAnnotation(annotations, part.getAnnotation());

      pathPsi = getPsiPart(pathPsi, part.getOperationPath(), "path");
      outputProjectionPsi = getPsiPart(outputProjectionPsi, part.getOperationOutputProjection(), "output projection");
    }

    OpVarPath varPath = parsePath(resourceType, pathPsi, resolver);
    DataType effectiveType = resolveOutputType(resourceType, varPath, null, resolver);

    if (outputProjectionPsi == null)
      throw new PsiProcessingException("Output projection must be specified", psi);

    OpOutputVarProjection outputProjection = OpOutputProjectionsPsiParser.parseVarProjection(
        effectiveType,
        outputProjectionPsi.getOpOutputVarProjection(),
        resolver
    );

    return new ReadOperationIdl(
        parseOperationName(psi.getOperationName()),
        OpParams.fromCollection(params),
        Annotations.fromMap(annotations),
        varPath,
        outputProjection,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  @NotNull
  private static CreateOperationIdl parseCreate(
      @NotNull DataType resourceType,
      @NotNull IdlCreateOperationDef psi,
      @NotNull TypesResolver resolver) throws PsiProcessingException {

    List<OpParam> params = null;
    Map<String, Annotation> annotations = null;

    IdlOperationPath pathPsi = null;
    IdlOperationInputType inputTypePsi = null;
    IdlOperationInputProjection inputProjectionPsi = null;
    IdlOperationOutputType outputTypePsi = null;
    IdlOperationOutputProjection outputProjectionPsi = null;

    for (IdlCreateOperationBodyPart part : psi.getCreateOperationBodyPartList()) {
      params = parseParam(params, part.getOpParam(), resolver);
      annotations = ProjectionPsiParserUtil.parseAnnotation(annotations, part.getAnnotation());

      pathPsi = getPsiPart(pathPsi, part.getOperationPath(), "path");
      inputTypePsi = getPsiPart(inputTypePsi, part.getOperationInputType(), "input type");
      inputProjectionPsi = getPsiPart(inputProjectionPsi, part.getOperationInputProjection(), "input projection");
      outputTypePsi = getPsiPart(outputTypePsi, part.getOperationOutputType(), "output type");
      outputProjectionPsi = getPsiPart(outputProjectionPsi, part.getOperationOutputProjection(), "output projection");
    }

    OpVarPath varPath = parsePath(resourceType, pathPsi, resolver);

    if (inputProjectionPsi == null)
      throw new PsiProcessingException("Input projection must be specified", psi);

    if (outputTypePsi == null)
      throw new PsiProcessingException("Output type must be specified", psi);

    if (outputProjectionPsi == null)
      throw new PsiProcessingException("Output projection must be specified", psi);

    return new CreateOperationIdl(
        parseOperationName(psi.getOperationName()),
        OpParams.fromCollection(params),
        Annotations.fromMap(annotations),
        varPath,
        OpInputProjectionsPsiParser.parseModelProjection(
            resolveInputType(resourceType, varPath, inputTypePsi, resolver, psi),
            true,
            null,
            Annotations.EMPTY,
            null,
            inputProjectionPsi.getOpInputModelProjection(),
            resolver
        ).projection(),
        OpOutputProjectionsPsiParser.parseVarProjection(
            resolveOutputType(resourceType, varPath, outputTypePsi, resolver),
            outputProjectionPsi.getOpOutputVarProjection(),
            resolver
        ),
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  @NotNull
  private static UpdateOperationIdl parseUpdate(
      @NotNull DataType resourceType,
      @NotNull IdlUpdateOperationDef psi,
      @NotNull TypesResolver resolver) throws PsiProcessingException {

    List<OpParam> params = null;
    Map<String, Annotation> annotations = null;

    IdlOperationPath pathPsi = null;
    IdlOperationInputType inputTypePsi = null;
    IdlOperationInputProjection inputProjectionPsi = null;
    IdlOperationOutputType outputTypePsi = null;
    IdlOperationOutputProjection outputProjectionPsi = null;

    for (IdlUpdateOperationBodyPart part : psi.getUpdateOperationBodyPartList()) {
      params = parseParam(params, part.getOpParam(), resolver);
      annotations = ProjectionPsiParserUtil.parseAnnotation(annotations, part.getAnnotation());

      pathPsi = getPsiPart(pathPsi, part.getOperationPath(), "path");
      inputTypePsi = getPsiPart(inputTypePsi, part.getOperationInputType(), "input type");
      inputProjectionPsi = getPsiPart(inputProjectionPsi, part.getOperationInputProjection(), "input projection");
      outputTypePsi = getPsiPart(outputTypePsi, part.getOperationOutputType(), "output type");
      outputProjectionPsi = getPsiPart(outputProjectionPsi, part.getOperationOutputProjection(), "output projection");
    }

    OpVarPath varPath = parsePath(resourceType, pathPsi, resolver);

    if (inputProjectionPsi == null)
      throw new PsiProcessingException("Input projection must be specified", psi);

    if (outputProjectionPsi == null)
      throw new PsiProcessingException("Output projection must be specified", psi);

    return new UpdateOperationIdl(
        parseOperationName(psi.getOperationName()),
        OpParams.fromCollection(params),
        Annotations.fromMap(annotations),
        varPath,
        OpInputProjectionsPsiParser.parseModelProjection(
            resolveInputType(resourceType, varPath, inputTypePsi, resolver, psi),
            true,
            null,
            Annotations.EMPTY,
            null,
            inputProjectionPsi.getOpInputModelProjection(),
            resolver
        ).projection(),
        OpOutputProjectionsPsiParser.parseVarProjection(
            resolveOutputType(resourceType, varPath, outputTypePsi, resolver),
            outputProjectionPsi.getOpOutputVarProjection(),
            resolver
        ),
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  @NotNull
  private static DeleteOperationIdl parseDelete(
      @NotNull DataType resourceType,
      @NotNull IdlDeleteOperationDef psi,
      @NotNull TypesResolver resolver) throws PsiProcessingException {

    List<OpParam> params = null;
    Map<String, Annotation> annotations = null;

    IdlOperationPath pathPsi = null;
    IdlOperationDeleteProjection deleteProjectionPsi = null;
    IdlOperationOutputType outputTypePsi = null;
    IdlOperationOutputProjection outputProjectionPsi = null;

    for (IdlDeleteOperationBodyPart part : psi.getDeleteOperationBodyPartList()) {
      params = parseParam(params, part.getOpParam(), resolver);
      annotations = ProjectionPsiParserUtil.parseAnnotation(annotations, part.getAnnotation());

      pathPsi = getPsiPart(pathPsi, part.getOperationPath(), "path");
      deleteProjectionPsi = getPsiPart(deleteProjectionPsi, part.getOperationDeleteProjection(), "delete projection");
      outputTypePsi = getPsiPart(outputTypePsi, part.getOperationOutputType(), "output type");
      outputProjectionPsi = getPsiPart(outputProjectionPsi, part.getOperationOutputProjection(), "output projection");
    }

    OpVarPath varPath = parsePath(resourceType, pathPsi, resolver);

    if (deleteProjectionPsi == null)
      throw new PsiProcessingException("Delete projection must be specified", psi);

    if (outputTypePsi == null)
      throw new PsiProcessingException("Output type must be specified", psi);

    if (outputProjectionPsi == null)
      throw new PsiProcessingException("Output projection must be specified", psi);

    return new DeleteOperationIdl(
        parseOperationName(psi.getOperationName()),
        OpParams.fromCollection(params),
        Annotations.fromMap(annotations),
        varPath,
        OpDeleteProjectionsPsiParser.parseVarProjection(
            resolveDeleteType(resourceType, varPath),
            deleteProjectionPsi.getOpDeleteVarProjection(),
            resolver
        ),
        OpOutputProjectionsPsiParser.parseVarProjection(
            resolveOutputType(resourceType, varPath, outputTypePsi, resolver),
            outputProjectionPsi.getOpOutputVarProjection(),
            resolver
        ),
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  @NotNull
  private static CustomOperationIdl parseCustom(
      @NotNull DataType resourceType,
      @NotNull IdlCustomOperationDef psi,
      @NotNull TypesResolver resolver) throws PsiProcessingException {

    List<OpParam> params = null;
    Map<String, Annotation> annotations = null;

    IdlOperationPath pathPsi = null;
    IdlOperationInputType inputTypePsi = null;
    IdlOperationInputProjection inputProjectionPsi = null;
    IdlOperationOutputType outputTypePsi = null;
    IdlOperationOutputProjection outputProjectionPsi = null;

    for (IdlCustomOperationBodyPart part : psi.getCustomOperationBodyPartList()) {
      params = parseParam(params, part.getOpParam(), resolver);
      annotations = ProjectionPsiParserUtil.parseAnnotation(annotations, part.getAnnotation());

      pathPsi = getPsiPart(pathPsi, part.getOperationPath(), "path");
      inputTypePsi = getPsiPart(inputTypePsi, part.getOperationInputType(), "input type");
      inputProjectionPsi = getPsiPart(inputProjectionPsi, part.getOperationInputProjection(), "input projection");
      outputTypePsi = getPsiPart(outputTypePsi, part.getOperationOutputType(), "output type");
      outputProjectionPsi = getPsiPart(outputProjectionPsi, part.getOperationOutputProjection(), "output projection");
    }

    OpVarPath varPath = parsePath(resourceType, pathPsi, resolver);

    if (outputProjectionPsi == null)
      throw new PsiProcessingException("Output projection must be specified", psi);

    return new CustomOperationIdl(
        parseOperationName(psi.getOperationName()),
        OpParams.fromCollection(params),
        Annotations.fromMap(annotations),
        varPath,
        inputProjectionPsi == null ? null : OpInputProjectionsPsiParser.parseModelProjection(
            resolveInputType(resourceType, varPath, inputTypePsi, resolver, psi),
            true,
            null,
            Annotations.EMPTY,
            null,
            inputProjectionPsi.getOpInputModelProjection(),
            resolver
        ).projection(),
        OpOutputProjectionsPsiParser.parseVarProjection(
            resolveOutputType(resourceType, varPath, outputTypePsi, resolver),
            outputProjectionPsi.getOpOutputVarProjection(),
            resolver
        ),
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  @Nullable
  private static List<OpParam> parseParam(
      @Nullable List<OpParam> params,
      @Nullable IdlOpParam paramPsi,
      @NotNull TypesResolver resolver) throws PsiProcessingException {

    if (paramPsi == null) return params;
    if (params == null) params = new ArrayList<>();

    params.add(OpParserUtil.parseParameter(paramPsi, resolver));

    return params;
  }

  @Nullable
  private static <T extends PsiElement> T getPsiPart(@Nullable T prev, @Nullable T cur, @NotNull String name)
      throws PsiProcessingException {

    if (cur != null) {
      if (prev != null)
        throw new PsiProcessingException("Operation " + name + " specified twice", cur);

      return cur;
    }

    return prev;
  }

  @NotNull
  private static DataType resolveOutputType(
      @NotNull DataType resourceType,
      @Nullable OpVarPath opVarPath,
      @Nullable IdlOperationOutputType outputTypePsi,
      @NotNull TypesResolver resolver) throws PsiProcessingException {

    if (outputTypePsi == null) {
      if (opVarPath == null) return resourceType;
      else return ProjectionUtils.tipType(opVarPath);
    }

    @NotNull final IdlValueTypeRef typeRefPsi = outputTypePsi.getValueTypeRef();
    @NotNull final ValueTypeRef valueTypeRef = TypeRefs.fromPsi(typeRefPsi);
    @Nullable final DataType dataType = resolver.resolve(valueTypeRef);
    if (dataType == null) throw new PsiProcessingException("Can't resolve output type", typeRefPsi);
    return dataType;
  }

  @NotNull
  private static DatumType resolveInputType(
      @NotNull DataType resourceType,
      @Nullable OpVarPath path,
      @Nullable IdlOperationInputType inputTypePsi,
      @NotNull TypesResolver resolver,
      @NotNull PsiElement location) throws PsiProcessingException {

    if (inputTypePsi == null) {
      if (path == null) {

        @NotNull Type rtt = resourceType.type;

        if (rtt instanceof DatumType) return (DatumType) rtt;

        @Nullable
        final Type.Tag defaultTag = resourceType.defaultTag;

        if (defaultTag == null)
          throw new PsiProcessingException(
              "Neither input type nor operation path is specified, and resource type has no default tag",
              location
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
              "Path tip type doesn't define default tag",
              location
          );
        else return defaultTag.type;
      }
    }
    @NotNull final IdlTypeRef typeRefPsi = inputTypePsi.getTypeRef();
    @Nullable final DatumType datumType = TypeRefs.fromPsi(typeRefPsi).resolveDatumType(resolver);
    if (datumType == null) throw new PsiProcessingException("Can't resolve input type", typeRefPsi);
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
  private static OpVarPath parsePath(
      @NotNull DataType type,
      @Nullable IdlOperationPath pathPsi,
      @NotNull TypesResolver resolver
  ) throws PsiProcessingException {
    if (pathPsi != null) {
      @Nullable final IdlOpVarPath varPathPsi = pathPsi.getOpVarPath();
      if (varPathPsi == null) throw new PsiProcessingException("Path expression missing", pathPsi);
      return OpPathPsiParser.parseVarPath(type, varPathPsi, resolver);
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
