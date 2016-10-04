package io.epigraph.schema.parser.psi.impl;

import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import io.epigraph.schema.parser.psi.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

import static io.epigraph.schema.parser.psi.impl.SchemaPsiImplUtil.*;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaPsiImplUtilExt {

  // record --------------------------------------------

  @Contract(pure = true)
  @NotNull
  public static List<SchemaTypeDef> supplemented(@NotNull SchemaRecordTypeDef recordTypeDef) {
    return Collections.emptyList();
  }
  
  // var --------------------------------------------

  @Contract(pure = true)
  @NotNull
  public static List<SchemaTypeDef> supplemented(@NotNull SchemaVarTypeDef varTypeDef) {
    return Collections.emptyList();
  }

  // fqn type ref --------------------------------------------

  // not exposed through PSI
  @Contract(pure = true)
  @Nullable
  public static PsiReference getReference(@NotNull SchemaQnTypeRef typeRef) {
    List<SchemaQnSegment> fqnSegmentList = typeRef.getQn().getQnSegmentList();
    if (fqnSegmentList.isEmpty()) return null;
    return fqnSegmentList.get(fqnSegmentList.size() - 1).getReference();
  }

  @Contract(pure = true)
  @Nullable
  public static SchemaTypeDef resolve(@NotNull SchemaQnTypeRef typeRef) {
    PsiReference reference = getReference(typeRef);
    if (reference == null) return null;
    PsiElement element = reference.resolve();
    if (element instanceof SchemaTypeDef) return (SchemaTypeDef) element;
    return null;
  }

  // supplement --------------------------------------------

  // can't use SchemaSupplementDef::getQnTypeRefList as it will include both source and all supplemented

  @Contract(pure = true)
  @Nullable
  public static SchemaTypeDef source(@NotNull SchemaSupplementDef supplementDef) {
    SchemaQnTypeRef ref = sourceRef(supplementDef);
    if (ref == null) return null;
    return ref.resolve();
  }

  @Contract(pure = true)
  @NotNull
  public static List<SchemaTypeDef> supplemented(@NotNull SchemaSupplementDef supplementDef) {
    throw new UnsupportedOperationException();
  }

  @Contract(pure = true)
  @NotNull
  public static ItemPresentation getPresentation(@NotNull SchemaSupplementDef supplementDef) {
    throw new UnsupportedOperationException();
  }

  // member decls --------------------------------------------
  // field decl

  @Contract(pure = true)
  @NotNull
  public static ItemPresentation getPresentation(@NotNull SchemaFieldDecl fieldDecl) {
    throw new UnsupportedOperationException();
  }

  // varTypeMember decl

  @Contract(pure = true)
  @NotNull
  public static ItemPresentation getPresentation(@NotNull SchemaVarTagDecl varTagDecl) {
    throw new UnsupportedOperationException();
  }

}
