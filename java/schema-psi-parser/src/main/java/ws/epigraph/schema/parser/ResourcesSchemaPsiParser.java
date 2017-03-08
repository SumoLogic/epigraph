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

package ws.epigraph.schema.parser;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.lang.Qn;
import ws.epigraph.projections.VarReferenceContext;
import ws.epigraph.projections.gen.GenVarProjection;
import ws.epigraph.projections.op.delete.OpDeleteProjectionsPsiParser;
import ws.epigraph.projections.op.delete.OpDeletePsiProcessingContext;
import ws.epigraph.projections.op.delete.OpDeleteVarProjection;
import ws.epigraph.projections.op.delete.OpDeleteVarReferenceContext;
import ws.epigraph.projections.op.input.OpInputProjectionsPsiParser;
import ws.epigraph.projections.op.input.OpInputPsiProcessingContext;
import ws.epigraph.projections.op.input.OpInputVarProjection;
import ws.epigraph.projections.op.input.OpInputVarReferenceContext;
import ws.epigraph.projections.op.output.OpOutputProjectionsPsiParser;
import ws.epigraph.projections.op.output.OpOutputPsiProcessingContext;
import ws.epigraph.projections.op.output.OpOutputVarProjection;
import ws.epigraph.projections.op.output.OpOutputVarReferenceContext;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingContext;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.ImportAwareTypesResolver;
import ws.epigraph.refs.TypeRef;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.refs.ValueTypeRef;
import ws.epigraph.schema.ResourceDeclaration;
import ws.epigraph.schema.ResourcePsiProcessingContext;
import ws.epigraph.schema.ResourcesSchema;
import ws.epigraph.schema.TypeRefs;
import ws.epigraph.schema.operations.OperationDeclaration;
import ws.epigraph.schema.operations.OperationsPsiParser;
import ws.epigraph.schema.parser.psi.*;
import ws.epigraph.types.DataTypeApi;
import ws.epigraph.types.DatumTypeApi;
import ws.epigraph.types.TypeApi;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class ResourcesSchemaPsiParser { // todo this must be ported to scala/ctypes
  private ResourcesSchemaPsiParser() {}

  public static @NotNull ResourcesSchema parseResourcesSchema(
      @NotNull SchemaFile psi,
      @NotNull TypesResolver basicResolver,
      @NotNull PsiProcessingContext context) throws PsiProcessingException {

    @Nullable SchemaNamespaceDecl namespaceDeclPsi = PsiTreeUtil.getChildOfType(psi, SchemaNamespaceDecl.class);
    if (namespaceDeclPsi == null)
      throw new PsiProcessingException("namespace not specified", psi, context);
    @Nullable SchemaQn namespaceFqnPsi = namespaceDeclPsi.getQn();
    if (namespaceFqnPsi == null)
      throw new PsiProcessingException("namespace not specified", psi, context);

    Qn namespace = namespaceFqnPsi.getQn();

    TypesResolver resolver = new ImportAwareTypesResolver(namespace, parseImports(psi), basicResolver);

    final SchemaDefs defs = psi.getDefs();

    final Map<String, ResourceDeclaration> resources;
    if (defs == null) resources = Collections.emptyMap();
    else {
      resources = new HashMap<>();
      for (SchemaResourceDef resourceDefPsi : defs.getResourceDefList()) {
        if (resourceDefPsi != null) {
          try {
            ResourceDeclaration resource = parseResource(namespace, resourceDefPsi, resolver, context);
            String fieldName = resource.fieldName();
            if (resources.containsKey(fieldName))
              context.addError("Resource '" + fieldName + "' is already defined", resourceDefPsi);
            else
              resources.put(fieldName, resource);
          } catch (PsiProcessingException e) {
            context.addException(e);
          }
        }
      }
    }

    return new ResourcesSchema(namespace, resources);
  }

  private static @NotNull List<Qn> parseImports(@NotNull SchemaFile idlPsi) {
    final @Nullable SchemaImports importsPsi = PsiTreeUtil.getChildOfType(idlPsi, SchemaImports.class);
    if (importsPsi == null) return Collections.emptyList();

    final @NotNull List<SchemaImportStatement> importStatementsPsi = importsPsi.getImportStatementList();

    if (importStatementsPsi.isEmpty()) return Collections.emptyList();

    return importStatementsPsi
        .stream()
        .filter(Objects::nonNull)
        .map(SchemaImportStatement::getQn)
        .filter(Objects::nonNull)
        .map(SchemaQn::getQn)
        .collect(Collectors.toList());
  }

  public static ResourceDeclaration parseResource(
      @NotNull Qn namespace,
      @NotNull SchemaResourceDef psi,
      @NotNull TypesResolver resolver,
      @NotNull PsiProcessingContext context) throws PsiProcessingException {

    final SchemaResourceName resourceName = psi.getResourceName();
    if (resourceName == null) throw new PsiProcessingException(
        "Resource name not specified", psi, context
    );

    final String fieldName = resourceName.getQid().getCanonicalName();

    SchemaResourceType resourceTypePsi = psi.getResourceType();
    if (resourceTypePsi == null) throw new PsiProcessingException(
        String.format("Resource '%s' type not specified", fieldName),
        psi, context
    );

    @NotNull SchemaValueTypeRef valueTypeRefPsi = resourceTypePsi.getValueTypeRef();
    @NotNull ValueTypeRef valueTypeRef = TypeRefs.fromPsi(valueTypeRefPsi, context);
    @Nullable DataTypeApi resourceType = resolver.resolve(valueTypeRef);

    if (resourceType == null) throw new PsiProcessingException(
        String.format("Can't resolve resource '%s' type '%s'", fieldName, valueTypeRef),
        resourceTypePsi,
        context
    );

    // convert datum kind to samovar
    @NotNull TypeApi type = resourceType.type();
    if (resourceType.defaultTag() == null && valueTypeRef.defaultOverride() == null && type instanceof DatumTypeApi) {
      resourceType = type.dataType();
    }

    ResourcePsiProcessingContext resourcePsiProcessingContext = new ResourcePsiProcessingContext(
        context, namespace, fieldName
    );

    final List<SchemaProjectionDef> projectionDefsPsi = psi.getProjectionDefList();
    for (final SchemaProjectionDef projectionDef : projectionDefsPsi) {
      try {
        parseProjectionDef(projectionDef, resolver, resourcePsiProcessingContext);
      } catch (PsiProcessingException e) {
        context.addException(e);
      }
    }

    @NotNull List<SchemaOperationDef> operationDefsPsi = psi.getOperationDefList();

    final List<OperationDeclaration> operations = new ArrayList<>(operationDefsPsi.size());
    for (SchemaOperationDef defPsi : operationDefsPsi)
      try {
        operations.add(
            OperationsPsiParser.parseOperation(
                resourceType,
                defPsi,
                resolver,
                resourcePsiProcessingContext
            )
        );
      } catch (PsiProcessingException e) {
        context.addException(e);
      }

    resourcePsiProcessingContext.inputVarReferenceContext().ensureAllReferencesResolved(context);
    resourcePsiProcessingContext.outputVarReferenceContext().ensureAllReferencesResolved(context);
    resourcePsiProcessingContext.deleteVarReferenceContext().ensureAllReferencesResolved(context);

    return new ResourceDeclaration(
        fieldName, resourceType, operations, EpigraphPsiUtil.getLocation(psi)
    );
  }

  private static void parseProjectionDef(
      @NotNull SchemaProjectionDef projectionDefPsi,
      @NotNull TypesResolver resolver,
      @NotNull ResourcePsiProcessingContext context) throws PsiProcessingException {

    final SchemaOutputProjectionDef outputProjectionDef = projectionDefPsi.getOutputProjectionDef();
    final SchemaInputProjectionDef inputProjectionDef = projectionDefPsi.getInputProjectionDef();
    final SchemaDeleteProjectionDef deleteProjectionDef = projectionDefPsi.getDeleteProjectionDef();

    if (outputProjectionDef != null) {
      parseOutputProjectionDef(outputProjectionDef, resolver, context);
    } else if (inputProjectionDef != null) {
      parseInputProjectionDef(inputProjectionDef, resolver, context);
    } else if (deleteProjectionDef != null) {
      parseDeleteProjectionDef(deleteProjectionDef, resolver, context);
    } else {
      context.addError("Incomplete projection definition", projectionDefPsi);
    }
  }

  private static void parseOutputProjectionDef(
      @NotNull SchemaOutputProjectionDef projectionDefPsi,
      @NotNull TypesResolver resolver,
      @NotNull ResourcePsiProcessingContext context) throws PsiProcessingException {
    
    parseGenProjectionDef(
        projectionDefPsi, 
        projectionDefPsi.getQid(),
        projectionDefPsi.getTypeRef(),
        projectionDefPsi.getOpOutputUnnamedVarProjection(),
        resolver, 
        context.outputVarReferenceContext(),
        context, 
        OutputUnnamedVarReferenceParser.INSTANCE
    );

  }
  
  private static void parseInputProjectionDef(
      @NotNull SchemaInputProjectionDef projectionDefPsi,
      @NotNull TypesResolver resolver,
      @NotNull ResourcePsiProcessingContext context) throws PsiProcessingException {

    parseGenProjectionDef(
        projectionDefPsi,
        projectionDefPsi.getQid(),
        projectionDefPsi.getTypeRef(),
        projectionDefPsi.getOpInputUnnamedVarProjection(),
        resolver,
        context.inputVarReferenceContext(),
        context,
        InputUnnamedVarReferenceParser.INSTANCE
    );

  }
  
  private static void parseDeleteProjectionDef(
      @NotNull SchemaDeleteProjectionDef projectionDefPsi,
      @NotNull TypesResolver resolver,
      @NotNull ResourcePsiProcessingContext context) throws PsiProcessingException {

    parseGenProjectionDef(
        projectionDefPsi,
        projectionDefPsi.getQid(),
        projectionDefPsi.getTypeRef(),
        projectionDefPsi.getOpDeleteUnnamedVarProjection(),
        resolver,
        context.deleteVarReferenceContext(),
        context,
        DeleteUnnamedVarReferenceParser.INSTANCE
    );

  }

  private static <
      VP extends GenVarProjection<VP, ?, ?>,
      RC extends VarReferenceContext<VP>,
      UP extends PsiElement>
  void parseGenProjectionDef(
      @NotNull PsiElement projectionDefPsi,
      @Nullable SchemaQid projectionNamePsi,
      @Nullable SchemaTypeRef typeRefPsi,
      @Nullable UP unnamedPsi,
      @NotNull TypesResolver resolver,
      @NotNull RC referenceContext,
      @NotNull ResourcePsiProcessingContext context,
      @NotNull UnnamedVarParser<VP, RC, UP> psiParser) throws PsiProcessingException {

    if (projectionNamePsi == null)
      context.addError("Incomplete output projection definition: name not specified", projectionDefPsi);
    else {
      final String projectionName = projectionNamePsi.getCanonicalName();
      if (referenceContext.exists(projectionName))
        context.addError(String.format("Output projection '%s' is already defined", projectionName), projectionDefPsi);
      else {
        if (typeRefPsi == null || unnamedPsi == null)
          context.addError(
              String.format("Incomplete output projection '%s' definition", projectionName),
              projectionDefPsi
          );
        else {
          final TypeRef typeRef = TypeRefs.fromPsi(typeRefPsi, context);
          final TypeApi type = typeRef.resolve(resolver);
          if (type == null)
            context.addError(
                String.format("Output projection '%s' type '%s' not defined", projectionName, typeRef.toString()),
                projectionDefPsi
            );
          else {
            final VP reference =
                referenceContext.reference(type, projectionName, false, EpigraphPsiUtil.getLocation(projectionDefPsi));
            
            final VP value = psiParser.parse(
                type.dataType(),
                unnamedPsi,
                resolver,
                referenceContext, 
                context
            );

            referenceContext.resolve(projectionName, value, EpigraphPsiUtil.getLocation(unnamedPsi), context);
            //noinspection unchecked
            assert reference.name() != null;
          }
        }
      }
    }
  }

  private interface UnnamedVarParser<
      VP extends GenVarProjection<VP, ?, ?>,
      RC extends VarReferenceContext<VP>,
      UP extends PsiElement
      > {
    VP parse(
        @NotNull DataTypeApi type,
        @NotNull UP psi,
        @NotNull TypesResolver resolver,
        @NotNull RC referenceContext,
        @NotNull ResourcePsiProcessingContext context
    ) throws PsiProcessingException;
  }

  private static class OutputUnnamedVarReferenceParser 
      implements UnnamedVarParser<OpOutputVarProjection, OpOutputVarReferenceContext, SchemaOpOutputUnnamedVarProjection> {
    
    static final OutputUnnamedVarReferenceParser INSTANCE = new OutputUnnamedVarReferenceParser();

    @Override
    public OpOutputVarProjection parse(
        final @NotNull DataTypeApi type,
        final @NotNull SchemaOpOutputUnnamedVarProjection psi,
        final @NotNull TypesResolver resolver,
        final @NotNull OpOutputVarReferenceContext referenceContext,
        final @NotNull ResourcePsiProcessingContext context) throws PsiProcessingException {

      OpInputPsiProcessingContext inputPsiProcessingContext = new OpInputPsiProcessingContext(
          context,
          context.inputVarReferenceContext()
      );
      OpOutputPsiProcessingContext outputPsiProcessingContext = new OpOutputPsiProcessingContext(
          context,
          inputPsiProcessingContext,
          referenceContext
      );

      return OpOutputProjectionsPsiParser.parseUnnamedVarProjection(
          type,
          psi,
          resolver,
          outputPsiProcessingContext
      );
    }
  }
  
  private static class InputUnnamedVarReferenceParser
      implements UnnamedVarParser<OpInputVarProjection, OpInputVarReferenceContext, SchemaOpInputUnnamedVarProjection> {

    static final InputUnnamedVarReferenceParser INSTANCE = new InputUnnamedVarReferenceParser();

    @Override
    public OpInputVarProjection parse(
        final @NotNull DataTypeApi type,
        final @NotNull SchemaOpInputUnnamedVarProjection psi,
        final @NotNull TypesResolver resolver,
        final @NotNull OpInputVarReferenceContext referenceContext,
        final @NotNull ResourcePsiProcessingContext context) throws PsiProcessingException {

      OpInputPsiProcessingContext inputPsiProcessingContext = new OpInputPsiProcessingContext(
          context,
          referenceContext
      );

      return OpInputProjectionsPsiParser.parseUnnamedVarProjection(
          type,
          psi,
          resolver,
          inputPsiProcessingContext
      );
    }
  }
  
  private static class DeleteUnnamedVarReferenceParser
      implements UnnamedVarParser<OpDeleteVarProjection, OpDeleteVarReferenceContext, SchemaOpDeleteUnnamedVarProjection> {

    static final DeleteUnnamedVarReferenceParser INSTANCE = new DeleteUnnamedVarReferenceParser();

    @Override
    public OpDeleteVarProjection parse(
        final @NotNull DataTypeApi type,
        final @NotNull SchemaOpDeleteUnnamedVarProjection psi,
        final @NotNull TypesResolver resolver,
        final @NotNull OpDeleteVarReferenceContext referenceContext,
        final @NotNull ResourcePsiProcessingContext context) throws PsiProcessingException {

      OpInputPsiProcessingContext inputPsiProcessingContext = new OpInputPsiProcessingContext(
          context,
          context.inputVarReferenceContext()
      );
      OpDeletePsiProcessingContext deletePsiProcessingContext = new OpDeletePsiProcessingContext(
          context,
          inputPsiProcessingContext,
          referenceContext
      );

      return OpDeleteProjectionsPsiParser.parseUnnamedVarProjection(
          type,
          psi,
          resolver,
          deletePsiProcessingContext
      );
    }
  }

}
