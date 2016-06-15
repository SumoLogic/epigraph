// This is a generated file. Not intended for manual editing.
package com.sumologic.epigraph.schema.parser.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiNameIdentifierOwner;

public class SchemaVisitor extends PsiElementVisitor {

  public void visitAnonList(@NotNull SchemaAnonList o) {
    visitTypeRef(o);
  }

  public void visitAnonMap(@NotNull SchemaAnonMap o) {
    visitTypeRef(o);
  }

  public void visitCustomParam(@NotNull SchemaCustomParam o) {
    visitPsiNamedElement(o);
  }

  public void visitDataEnum(@NotNull SchemaDataEnum o) {
    visitDataValue(o);
  }

  public void visitDataList(@NotNull SchemaDataList o) {
    visitDataValue(o);
  }

  public void visitDataMap(@NotNull SchemaDataMap o) {
    visitDataValue(o);
  }

  public void visitDataMapEntry(@NotNull SchemaDataMapEntry o) {
    visitPsiElement(o);
  }

  public void visitDataPrimitiveValue(@NotNull SchemaDataPrimitiveValue o) {
    visitDataValue(o);
  }

  public void visitDataRecord(@NotNull SchemaDataRecord o) {
    visitDataValue(o);
  }

  public void visitDataRecordEntry(@NotNull SchemaDataRecordEntry o) {
    visitPsiElement(o);
  }

  public void visitDataValue(@NotNull SchemaDataValue o) {
    visitPsiElement(o);
  }

  public void visitDataVar(@NotNull SchemaDataVar o) {
    visitDataValue(o);
  }

  public void visitDataVarEntry(@NotNull SchemaDataVarEntry o) {
    visitPsiElement(o);
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
    visitTypeRef(o);
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

  public void visitNamespaceDecl(@NotNull SchemaNamespaceDecl o) {
    visitPsiElement(o);
  }

  public void visitPrimitiveTypeBody(@NotNull SchemaPrimitiveTypeBody o) {
    visitCustomParamsHolder(o);
  }

  public void visitPrimitiveTypeDef(@NotNull SchemaPrimitiveTypeDef o) {
    visitTypeDef(o);
  }

  public void visitQid(@NotNull SchemaQid o) {
    visitPsiElement(o);
  }

  public void visitRecordTypeBody(@NotNull SchemaRecordTypeBody o) {
    visitCustomParamsHolder(o);
  }

  public void visitRecordTypeDef(@NotNull SchemaRecordTypeDef o) {
    visitTypeDef(o);
  }

  public void visitSupplementDef(@NotNull SchemaSupplementDef o) {
    visitPsiElement(o);
  }

  public void visitSupplementsDecl(@NotNull SchemaSupplementsDecl o) {
    visitPsiElement(o);
  }

  public void visitTypeDefWrapper(@NotNull SchemaTypeDefWrapper o) {
    visitPsiElement(o);
  }

  public void visitTypeRef(@NotNull SchemaTypeRef o) {
    visitPsiElement(o);
  }

  public void visitVarTagDecl(@NotNull SchemaVarTagDecl o) {
    visitCustomParamsHolder(o);
    // visitPsiNamedElement(o);
  }

  public void visitVarTagRef(@NotNull SchemaVarTagRef o) {
    visitPsiNameIdentifierOwner(o);
  }

  public void visitVarTypeBody(@NotNull SchemaVarTypeBody o) {
    visitCustomParamsHolder(o);
  }

  public void visitVarTypeDef(@NotNull SchemaVarTypeDef o) {
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

  public void visitTypeDef(@NotNull SchemaTypeDef o) {
    visitPsiElement(o);
  }

  public void visitPsiElement(@NotNull PsiElement o) {
    visitElement(o);
  }

}
