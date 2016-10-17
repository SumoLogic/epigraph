package io.epigraph.idl.operations;

import io.epigraph.idl.parser.psi.*;
import io.epigraph.projections.Annotation;
import io.epigraph.projections.Annotations;
import io.epigraph.projections.ProjectionPsiParserUtil;
import io.epigraph.projections.StepsAndProjection;
import io.epigraph.projections.op.OpParam;
import io.epigraph.projections.op.OpParams;
import io.epigraph.projections.op.input.OpInputProjectionsPsiParser;
import io.epigraph.projections.op.input.OpInputVarProjection;
import io.epigraph.projections.op.output.OpOutputProjectionsPsiParser;
import io.epigraph.projections.op.output.OpOutputVarProjection;
import io.epigraph.psi.EpigraphPsiUtil;
import io.epigraph.psi.PsiProcessingException;
import io.epigraph.refs.TypesResolver;
import io.epigraph.types.DataType;
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
      @NotNull DataType type,
      @NotNull IdlOperationDef psi,
      @NotNull TypesResolver resolver) throws PsiProcessingException {

    @Nullable IdlReadOperationDef readOperationDef = psi.getReadOperationDef();
    if (readOperationDef != null) return parseRead(type, readOperationDef, resolver);

    @Nullable IdlCreateOperationDef createOperationDef = psi.getCreateOperationDef();
    if (createOperationDef != null) return parseCreate(type, createOperationDef, resolver);

    @Nullable IdlUpdateOperationDef updateOperationDef = psi.getUpdateOperationDef();
    if (updateOperationDef != null) return parseUpdate(type, updateOperationDef, resolver);

    @Nullable IdlCustomOperationDef customOperationDef = psi.getCustomOperationDef();
    if (customOperationDef != null) return parseCustom(type, customOperationDef, resolver);

    throw new PsiProcessingException("Incomplete operation statement", psi);
  }

  @NotNull
  private static ReadOperationIdl parseRead(
      @NotNull DataType type,
      @NotNull IdlReadOperationDef psi,
      @NotNull TypesResolver resolver) throws PsiProcessingException {

    List<OpParam> params = null;
    Map<String, Annotation> annotations = null;

    OpOutputVarProjection outputProjection = null;

    for (IdlReadOperationBodyPart part : psi.getReadOperationBodyPartList()) {
      params = parseParam(params, part.getOpParam(), resolver);
      annotations = ProjectionPsiParserUtil.parseAnnotation(annotations, part.getAnnotation());
      outputProjection = parseOutputProjection(type, outputProjection, part.getOperationOutput(), resolver);
    }

    if (outputProjection == null)
      throw new PsiProcessingException("Output projection must be specified", psi);

    return new ReadOperationIdl(
        parseOperationName(psi.getOperationName()),
        params == null ? null : new OpParams(params),
        annotations == null ? null : new Annotations(annotations),
        outputProjection,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  @NotNull
  private static CreateOperationIdl parseCreate(
      @NotNull DataType type,
      @NotNull IdlCreateOperationDef psi,
      @NotNull TypesResolver resolver) throws PsiProcessingException {

    List<OpParam> params = null;
    Map<String, Annotation> annotations = null;

    StepsAndProjection<OpInputVarProjection> inputProjection = null;
    OpOutputVarProjection outputProjection = null;

    for (IdlCreateOperationBodyPart part : psi.getCreateOperationBodyPartList()) {
      params = parseParam(params, part.getOpParam(), resolver);
      annotations = ProjectionPsiParserUtil.parseAnnotation(annotations, part.getAnnotation());
      inputProjection = parseInputProjection(type, inputProjection, part.getOperationInput(), resolver);
      outputProjection = parseOutputProjection(type, outputProjection, part.getOperationOutput(), resolver);
    }

    if (inputProjection == null)
      throw new PsiProcessingException("Input projection must be specified", psi);

    if (outputProjection == null)
      throw new PsiProcessingException("Output projection must be specified", psi);

    return new CreateOperationIdl(
        parseOperationName(psi.getOperationName()),
        params == null ? null : new OpParams(params),
        annotations == null ? null : new Annotations(annotations),
        outputProjection,
        inputProjection,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  @NotNull
  private static UpdateOperationIdl parseUpdate(
      @NotNull DataType type,
      @NotNull IdlUpdateOperationDef psi,
      @NotNull TypesResolver resolver) throws PsiProcessingException {

    List<OpParam> params = null;
    Map<String, Annotation> annotations = null;

    StepsAndProjection<OpInputVarProjection> inputProjection = null;
    OpOutputVarProjection outputProjection = null;

    for (IdlUpdateOperationBodyPart part : psi.getUpdateOperationBodyPartList()) {
      params = parseParam(params, part.getOpParam(), resolver);
      annotations = ProjectionPsiParserUtil.parseAnnotation(annotations, part.getAnnotation());
      inputProjection = parseInputProjection(type, inputProjection, part.getOperationInput(), resolver);
      outputProjection = parseOutputProjection(type, outputProjection, part.getOperationOutput(), resolver);
    }

    if (inputProjection == null)
      throw new PsiProcessingException("Input projection must be specified", psi);

    if (outputProjection == null)
      throw new PsiProcessingException("Output projection must be specified", psi);

    return new UpdateOperationIdl(
        parseOperationName(psi.getOperationName()),
        params == null ? null : new OpParams(params),
        annotations == null ? null : new Annotations(annotations),
        outputProjection,
        inputProjection,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  @NotNull
  private static CustomOperationIdl parseCustom(
      @NotNull DataType type,
      @NotNull IdlCustomOperationDef psi,
      @NotNull TypesResolver resolver) throws PsiProcessingException {

    List<OpParam> params = null;
    Map<String, Annotation> annotations = null;

    StepsAndProjection<OpInputVarProjection> inputProjection = null;
    OpOutputVarProjection outputProjection = null;

    for (IdlCustomOperationBodyPart part : psi.getCustomOperationBodyPartList()) {
      params = parseParam(params, part.getOpParam(), resolver);
      annotations = ProjectionPsiParserUtil.parseAnnotation(annotations, part.getAnnotation());
      inputProjection = parseInputProjection(type, inputProjection, part.getOperationInput(), resolver);
      outputProjection = parseOutputProjection(type, outputProjection, part.getOperationOutput(), resolver);
    }

    if (inputProjection == null)
      throw new PsiProcessingException("Input projection must be specified", psi);

    if (outputProjection == null)
      throw new PsiProcessingException("Output projection must be specified", psi);

    return new CustomOperationIdl(
        psi.getQid().getCanonicalName(),
        params == null ? null : new OpParams(params),
        annotations == null ? null : new Annotations(annotations),
        outputProjection,
        inputProjection,
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

    params.add(OpOutputProjectionsPsiParser.parseParameter(paramPsi, resolver));

    return params;
  }

  @Nullable
  private static StepsAndProjection<OpInputVarProjection> parseInputProjection(
      @NotNull DataType type,
      @Nullable StepsAndProjection<OpInputVarProjection> inputProjection,
      @Nullable IdlOperationInput inputPsi,
      @NotNull TypesResolver resolver) throws PsiProcessingException {

    if (inputPsi != null) {
      if (inputProjection == null) {
        @Nullable IdlOpInputTrunkVarProjection inputProjectionPsi = inputPsi.getOpInputTrunkVarProjection();
        if (inputProjectionPsi != null) {
          return OpInputProjectionsPsiParser.parseTrunkVarProjection(type, inputProjectionPsi, resolver);
        }
      } else {
        throw new PsiProcessingException("Operation input projection should only be specified once", inputPsi);
      }
    }

    return inputProjection;
  }

  @Nullable
  private static OpOutputVarProjection parseOutputProjection(
      @NotNull DataType type,
      @Nullable OpOutputVarProjection outputProjection,
      @Nullable IdlOperationOutput outputPsi,
      @NotNull TypesResolver resolver) throws PsiProcessingException {

    if (outputPsi != null) {
      if (outputProjection == null) {
        @Nullable IdlOpOutputVarProjection outputProjectionPsi = outputPsi.getOpOutputVarProjection();
        if (outputProjectionPsi != null) {
          return OpOutputProjectionsPsiParser.parseVarProjection(type, outputProjectionPsi, resolver);
        }
      } else {
        throw new PsiProcessingException("Operation output projection should only be specified once", outputPsi);
      }
    }

    return outputProjection;
  }

  @Nullable
  private static String parseOperationName(@Nullable IdlOperationName namePsi) {
    if (namePsi == null) return null;
    @Nullable IdlQid qid = namePsi.getQid();
    if (qid == null) return null;
    return qid.getCanonicalName();
  }
}
