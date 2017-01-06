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

package ws.epigraph.schema.parser;

import com.intellij.psi.util.PsiTreeUtil;
import ws.epigraph.schema.ResourcesSchema;
import ws.epigraph.schema.ResourceDeclaration;
import ws.epigraph.schema.TypeRefs;
import ws.epigraph.schema.operations.OperationDeclaration;
import ws.epigraph.schema.operations.OperationsPsiParser;
import ws.epigraph.schema.parser.psi.*;
import ws.epigraph.lang.Qn;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingError;
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
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    @Nullable SchemaNamespaceDecl namespaceDeclPsi = PsiTreeUtil.getChildOfType(psi, SchemaNamespaceDecl.class);
    if (namespaceDeclPsi == null)
      throw new PsiProcessingException("namespace not specified", psi, errors);
    @Nullable SchemaQn namespaceFqnPsi = namespaceDeclPsi.getQn();
    if (namespaceFqnPsi == null)
      throw new PsiProcessingException("namespace not specified", psi, errors);

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
            ResourceDeclaration resource = parseResource(resourceDefPsi, resolver, errors);
            String fieldName = resource.fieldName();
            if (resources.containsKey(fieldName))
              errors.add(new PsiProcessingError("Resource '" + fieldName + "' is already defined", resourceDefPsi));
            else
              resources.put(fieldName, resource);
          } catch (PsiProcessingException e) {
            errors.add(e.toError());
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
      @NotNull SchemaResourceDef psi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    final SchemaResourceName resourceName = psi.getResourceName();
    if (resourceName == null) throw new PsiProcessingException(
        "Resource name not specified", psi, errors
    );

    final String fieldName = resourceName.getQid().getCanonicalName();

    SchemaResourceType resourceTypePsi = psi.getResourceType();
    if (resourceTypePsi == null) throw new PsiProcessingException(
        String.format("Resource '%s' type not specified", fieldName),
        psi, errors
    );

    @NotNull SchemaValueTypeRef valueTypeRefPsi = resourceTypePsi.getValueTypeRef();
    @NotNull ValueTypeRef valueTypeRef = TypeRefs.fromPsi(valueTypeRefPsi, errors);
    @Nullable DataTypeApi resourceType = resolver.resolve(valueTypeRef);

    if (resourceType == null) throw new PsiProcessingException(
        String.format("Can't resolve resource '%s' type '%s'", fieldName, valueTypeRef),
        resourceTypePsi,
        errors
    );

    // convert datum kind to samovar
    @NotNull TypeApi type = resourceType.type();
    if (resourceType.defaultTag() == null && valueTypeRef.defaultOverride() == null && type instanceof DatumTypeApi) {
      resourceType = type.dataType();
    }

    @NotNull List<SchemaOperationDef> defsPsi = psi.getOperationDefList();

    final List<OperationDeclaration> operations = new ArrayList<>(defsPsi.size());
    for (SchemaOperationDef defPsi : defsPsi)
      try {
        operations.add(OperationsPsiParser.parseOperation(resourceType, defPsi, resolver, errors));
      } catch (PsiProcessingException e) {
        errors.add(e.toError());
      }

    return new ResourceDeclaration(
        fieldName, resourceType, operations, EpigraphPsiUtil.getLocation(psi)
    );
  }
}
