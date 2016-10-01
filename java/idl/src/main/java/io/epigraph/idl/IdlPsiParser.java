package io.epigraph.idl;

import com.intellij.psi.util.PsiTreeUtil;
import io.epigraph.idl.operations.Operation;
import io.epigraph.idl.operations.OperationsPsiParser;
import io.epigraph.idl.parser.psi.*;
import io.epigraph.lang.Fqn;
import io.epigraph.psi.EpigraphPsiUtil;
import io.epigraph.psi.PsiProcessingException;
import io.epigraph.types.DataType;
import io.epigraph.types.Type;
import io.epigraph.types.TypesResolver;
import io.epigraph.types.UnionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class IdlPsiParser {
  @NotNull
  public static Idl parseIdl(@NotNull IdlFile idlPsi, @NotNull TypesResolver resolver) throws PsiProcessingException {
    // todo parse imports

    @Nullable IdlResourceDef[] resourceDefsPsi = PsiTreeUtil.getChildrenOfType(idlPsi, IdlResourceDef.class);

    final List<Resource> resources;

    if (resourceDefsPsi != null) {
      resources = new ArrayList<>(resourceDefsPsi.length);
      for (IdlResourceDef resourceDefPsi : resourceDefsPsi) {
        if (resourceDefPsi != null) {
          resources.add(parseResource(resourceDefPsi, resolver));
        }
      }
    } else
      resources = Collections.emptyList();


    return new Idl(resources);
  }

  public static Resource parseResource(@NotNull IdlResourceDef psi, @NotNull TypesResolver resolver)
      throws PsiProcessingException {
    final String fieldName = psi.getQid().getCanonicalName();

    @NotNull IdlResourceType resourceTypePsi = psi.getResourceType();

    // todo take imports into account
    @NotNull Fqn typeRef = resourceTypePsi.getFqnTypeRef().getFqn().getFqn();
    @Nullable UnionType type = resolver.resolveVarType(typeRef);

    if (type == null) throw new PsiProcessingException(
        String.format("Can't resolve resource '%s' type '%s'", fieldName, typeRef),
        resourceTypePsi
    );

    Type.Tag defaultTag = null;

    @Nullable IdlDefaultOverride defaultOverridePsi = resourceTypePsi.getDefaultOverride();
    if (defaultOverridePsi != null) {
      @NotNull IdlVarTagRef varTagRef = defaultOverridePsi.getVarTagRef();
      @NotNull String defaultTagName = varTagRef.getQid().getCanonicalName();

      defaultTag = type.tagsMap().get(defaultTagName);
      if (defaultTag == null) throw new PsiProcessingException(
          String.format("Invalid tag '%s' for type '%s' of resource '%s'", defaultTagName, typeRef, fieldName),
          varTagRef
      );
    }

    final DataType dataType = new DataType(false, type, defaultTag);


    @NotNull List<IdlOperationDef> defsPsi = psi.getOperationDefList();

    final List<Operation> operations = new ArrayList<>(defsPsi.size());
    for (IdlOperationDef defPsi : defsPsi)
      operations.add(OperationsPsiParser.parseOperation(dataType, defPsi, resolver));

    return new Resource(
        fieldName, dataType, operations, EpigraphPsiUtil.getLocation(psi)
    );
  }
}
