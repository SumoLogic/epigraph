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

import com.intellij.psi.util.PsiTreeUtil;
import ws.epigraph.projections.op.input.OpInputPsiProcessingContext;
import ws.epigraph.projections.op.input.OpInputVarReferenceContext;
import ws.epigraph.projections.op.output.OpOutputProjectionsPsiParser;
import ws.epigraph.projections.op.output.OpOutputPsiProcessingContext;
import ws.epigraph.projections.op.output.OpOutputVarProjection;
import ws.epigraph.projections.op.output.OpOutputVarReferenceContext;
import ws.epigraph.psi.PsiProcessingContext;
import ws.epigraph.refs.TypeRef;
import ws.epigraph.schema.ResourcePsiProcessingContext;
import ws.epigraph.schema.ResourcesSchema;
import ws.epigraph.schema.ResourceDeclaration;
import ws.epigraph.schema.TypeRefs;
import ws.epigraph.schema.operations.OperationDeclaration;
import ws.epigraph.schema.operations.OperationsPsiParser;
import ws.epigraph.schema.parser.psi.*;
import ws.epigraph.lang.Qn;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.ImportAwareTypesResolver;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.refs.ValueTypeRef;
import ws.epigraph.types.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    if (outputProjectionDef != null) {
      parseOutputProjectionDef(outputProjectionDef, resolver, context);
    } else { // todo rest
      context.addError("Incomplete projection definition", projectionDefPsi);
    }
  }

  private static void parseOutputProjectionDef(
      @NotNull SchemaOutputProjectionDef projectionDefPsi,
      @NotNull TypesResolver resolver,
      @NotNull ResourcePsiProcessingContext context) throws PsiProcessingException {

    final OpOutputVarReferenceContext referenceContext = context.outputVarReferenceContext();

    final String projectionName = projectionDefPsi.getQid().getCanonicalName();
    if (referenceContext.exists(projectionName))
      context.addError(String.format("Output projection '%s' is already defined", projectionName), projectionDefPsi);
    else {
      final SchemaTypeRef typeRefPsi = projectionDefPsi.getTypeRef();
      final SchemaOpOutputUnnamedVarProjection unnamedPsi = projectionDefPsi.getOpOutputUnnamedVarProjection();

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
          final OpOutputVarProjection reference =
              referenceContext.reference(type, projectionName, false, EpigraphPsiUtil.getLocation(projectionDefPsi));

          OpInputPsiProcessingContext inputPsiProcessingContext = new OpInputPsiProcessingContext(
              context,
              context.inputVarReferenceContext()
          );
          OpOutputPsiProcessingContext outputPsiProcessingContext = new OpOutputPsiProcessingContext(
              context,
              inputPsiProcessingContext,
              referenceContext
          );

          final OpOutputVarProjection value = OpOutputProjectionsPsiParser.parseUnnamedVarProjection(
              type.dataType(),
              unnamedPsi,
              resolver,
              outputPsiProcessingContext
          );

          referenceContext.resolve(projectionName, value, EpigraphPsiUtil.getLocation(unnamedPsi), context);
        }
      }
    }
  }
}
