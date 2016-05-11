// This is a generated file. Not intended for manual editing.
package com.sumologic.epigraph.ideaplugin.schema.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiNameIdentifierOwner;

public class SchemaVisitor extends PsiElementVisitor {

  public void visitAnonList(@NotNull SchemaAnonList o) {
    visitPsiElement(o);
  }

  public void visitAnonMap(@NotNull SchemaAnonMap o) {
    visitPsiElement(o);
  }

  public void visitAnonUnion(@NotNull SchemaAnonUnion o) {
    visitPsiElement(o);
  }

  public void visitCombinedFqns(@NotNull SchemaCombinedFqns o) {
    visitPsiElement(o);
  }

  public void visitCustomParam(@NotNull SchemaCustomParam o) {
    visitPsiNamedElement(o);
  }

  public void visitDefaultOverride(@NotNull SchemaDefaultOverride o) {
    visitPsiElement(o);
  }

  public void visitDefs(@NotNull SchemaDefs o) {
    visitPsiElement(o);
  }

  public void visitEnumMemberDecl(@NotNull SchemaEnumMemberDecl o) {
    visitCustomParamsHolder(o);
    // visitPsiNamedElement(o);
  }

  public void visitEnumTypeBody(@NotNull SchemaEnumTypeBody o) {
    visitCustomParamsHolder(o);
  }

  public void visitEnumTypeDef(@NotNull SchemaEnumTypeDef o) {
    visitTypeDef(o);
  }

  public void visitExtendsDecl(@NotNull SchemaExtendsDecl o) {
    visitPsiElement(o);
  }

  public void visitFieldDecl(@NotNull SchemaFieldDecl o) {
    visitCustomParamsHolder(o);
    // visitPsiNamedElement(o);
  }

  public void visitFqn(@NotNull SchemaFqn o) {
    visitPsiElement(o);
  }

  public void visitFqnSegment(@NotNull SchemaFqnSegment o) {
    visitPsiNameIdentifierOwner(o);
  }

  public void visitFqnTypeRef(@NotNull SchemaFqnTypeRef o) {
    visitPsiElement(o);
  }

  public void visitImportStatement(@NotNull SchemaImportStatement o) {
    visitPsiElement(o);
  }

  public void visitImports(@NotNull SchemaImports o) {
    visitPsiElement(o);
  }

  public void visitListTypeBody(@NotNull SchemaListTypeBody o) {
    visitCustomParamsHolder(o);
  }

  public void visitListTypeDef(@NotNull SchemaListTypeDef o) {
    visitTypeDef(o);
  }

  public void visitMapTypeBody(@NotNull SchemaMapTypeBody o) {
    visitCustomParamsHolder(o);
  }

  public void visitMapTypeDef(@NotNull SchemaMapTypeDef o) {
    visitTypeDef(o);
  }

  public void visitMetaDecl(@NotNull SchemaMetaDecl o) {
    visitPsiElement(o);
  }

  public void visitMultiMemberDecl(@NotNull SchemaMultiMemberDecl o) {
    visitCustomParamsHolder(o);
    // visitPsiNamedElement(o);
  }

  public void visitMultiSupplementsDecl(@NotNull SchemaMultiSupplementsDecl o) {
    visitPsiElement(o);
  }

  public void visitMultiTypeBody(@NotNull SchemaMultiTypeBody o) {
    visitCustomParamsHolder(o);
  }

  public void visitMultiTypeDef(@NotNull SchemaMultiTypeDef o) {
    visitTypeDef(o);
  }

  public void visitNamespaceDecl(@NotNull SchemaNamespaceDecl o) {
    visitPsiElement(o);
  }

  public void visitPrimitiveKind(@NotNull SchemaPrimitiveKind o) {
    visitPsiElement(o);
  }

  public void visitPrimitiveTypeBody(@NotNull SchemaPrimitiveTypeBody o) {
    visitCustomParamsHolder(o);
  }

  public void visitPrimitiveTypeDef(@NotNull SchemaPrimitiveTypeDef o) {
    visitTypeDef(o);
  }

  public void visitRecordSupplementsDecl(@NotNull SchemaRecordSupplementsDecl o) {
    visitPsiElement(o);
  }

  public void visitRecordTypeBody(@NotNull SchemaRecordTypeBody o) {
    visitCustomParamsHolder(o);
  }

  public void visitRecordTypeDef(@NotNull SchemaRecordTypeDef o) {
    visitTypeDef(o);
  }

  public void visitStarImportSuffix(@NotNull SchemaStarImportSuffix o) {
    visitPsiElement(o);
  }

  public void visitSupplementDef(@NotNull SchemaSupplementDef o) {
    visitPsiElement(o);
  }

  public void visitTagCommonType(@NotNull SchemaTagCommonType o) {
    visitPsiElement(o);
  }

  public void visitTagDecl(@NotNull SchemaTagDecl o) {
    visitCustomParamsHolder(o);
    // visitPsiNamedElement(o);
  }

  public void visitTypeDef(@NotNull SchemaTypeDef o) {
    visitTypeDefSchemaElement(o);
  }

  public void visitTypeRef(@NotNull SchemaTypeRef o) {
    visitPsiElement(o);
  }

  public void visitUnionTypeBody(@NotNull SchemaUnionTypeBody o) {
    visitCustomParamsHolder(o);
  }

  public void visitUnionTypeDef(@NotNull SchemaUnionTypeDef o) {
    visitTypeDef(o);
  }

  public void visitCustomParamsHolder(@NotNull CustomParamsHolder o) {
    visitElement(o);
  }

  public void visitPsiNameIdentifierOwner(@NotNull PsiNameIdentifierOwner o) {
    visitElement(o);
  }

  public void visitPsiNamedElement(@NotNull PsiNamedElement o) {
    visitElement(o);
  }

  public void visitTypeDefSchemaElement(@NotNull TypeDefSchemaElement o) {
    visitElement(o);
  }

  public void visitPsiElement(@NotNull PsiElement o) {
    visitElement(o);
  }

}
