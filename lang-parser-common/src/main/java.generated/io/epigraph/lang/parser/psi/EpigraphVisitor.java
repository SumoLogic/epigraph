// This is a generated file. Not intended for manual editing.
package io.epigraph.lang.parser.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiNameIdentifierOwner;

public class EpigraphVisitor extends PsiElementVisitor {

  public void visitAnonList(@NotNull EpigraphAnonList o) {
    visitTypeRef(o);
  }

  public void visitAnonMap(@NotNull EpigraphAnonMap o) {
    visitTypeRef(o);
  }

  public void visitCustomParam(@NotNull EpigraphCustomParam o) {
    visitPsiNamedElement(o);
  }

  public void visitDataEnum(@NotNull EpigraphDataEnum o) {
    visitDataValue(o);
  }

  public void visitDataList(@NotNull EpigraphDataList o) {
    visitDataValue(o);
  }

  public void visitDataMap(@NotNull EpigraphDataMap o) {
    visitDataValue(o);
  }

  public void visitDataMapEntry(@NotNull EpigraphDataMapEntry o) {
    visitPsiElement(o);
  }

  public void visitDataPrimitive(@NotNull EpigraphDataPrimitive o) {
    visitPsiElement(o);
  }

  public void visitDataRecord(@NotNull EpigraphDataRecord o) {
    visitDataValue(o);
  }

  public void visitDataRecordEntry(@NotNull EpigraphDataRecordEntry o) {
    visitPsiElement(o);
  }

  public void visitDataValue(@NotNull EpigraphDataValue o) {
    visitPsiElement(o);
  }

  public void visitDataVar(@NotNull EpigraphDataVar o) {
    visitDataValue(o);
  }

  public void visitDataVarEntry(@NotNull EpigraphDataVarEntry o) {
    visitPsiElement(o);
  }

  public void visitDefaultOverride(@NotNull EpigraphDefaultOverride o) {
    visitPsiElement(o);
  }

  public void visitDefs(@NotNull EpigraphDefs o) {
    visitPsiElement(o);
  }

  public void visitEnumMemberDecl(@NotNull EpigraphEnumMemberDecl o) {
    visitCustomParamsHolder(o);
    // visitPsiNamedElement(o);
  }

  public void visitEnumTypeBody(@NotNull EpigraphEnumTypeBody o) {
    visitCustomParamsHolder(o);
  }

  public void visitEnumTypeDef(@NotNull EpigraphEnumTypeDef o) {
    visitTypeDef(o);
  }

  public void visitExtendsDecl(@NotNull EpigraphExtendsDecl o) {
    visitPsiElement(o);
  }

  public void visitFieldDecl(@NotNull EpigraphFieldDecl o) {
    visitCustomParamsHolder(o);
    // visitPsiNamedElement(o);
  }

  public void visitFqn(@NotNull EpigraphFqn o) {
    visitPsiElement(o);
  }

  public void visitFqnSegment(@NotNull EpigraphFqnSegment o) {
    visitPsiNameIdentifierOwner(o);
  }

  public void visitFqnTypeRef(@NotNull EpigraphFqnTypeRef o) {
    visitTypeRef(o);
  }

  public void visitImportStatement(@NotNull EpigraphImportStatement o) {
    visitPsiElement(o);
  }

  public void visitImports(@NotNull EpigraphImports o) {
    visitPsiElement(o);
  }

  public void visitListTypeBody(@NotNull EpigraphListTypeBody o) {
    visitCustomParamsHolder(o);
  }

  public void visitListTypeDef(@NotNull EpigraphListTypeDef o) {
    visitTypeDef(o);
  }

  public void visitMapTypeBody(@NotNull EpigraphMapTypeBody o) {
    visitCustomParamsHolder(o);
  }

  public void visitMapTypeDef(@NotNull EpigraphMapTypeDef o) {
    visitTypeDef(o);
  }

  public void visitMetaDecl(@NotNull EpigraphMetaDecl o) {
    visitPsiElement(o);
  }

  public void visitNamespaceDecl(@NotNull EpigraphNamespaceDecl o) {
    visitPsiElement(o);
  }

  public void visitOpInputModelProjection(@NotNull EpigraphOpInputModelProjection o) {
    visitPsiElement(o);
  }

  public void visitOpOutputEnumModelProjection(@NotNull EpigraphOpOutputEnumModelProjection o) {
    visitPsiElement(o);
  }

  public void visitOpOutputFieldProjection(@NotNull EpigraphOpOutputFieldProjection o) {
    visitPsiElement(o);
  }

  public void visitOpOutputFieldProjectionBody(@NotNull EpigraphOpOutputFieldProjectionBody o) {
    visitPsiElement(o);
  }

  public void visitOpOutputFieldProjectionBodyPart(@NotNull EpigraphOpOutputFieldProjectionBodyPart o) {
    visitPsiElement(o);
  }

  public void visitOpOutputKeyProjection(@NotNull EpigraphOpOutputKeyProjection o) {
    visitPsiElement(o);
  }

  public void visitOpOutputKeyProjectionPart(@NotNull EpigraphOpOutputKeyProjectionPart o) {
    visitPsiElement(o);
  }

  public void visitOpOutputListModelProjection(@NotNull EpigraphOpOutputListModelProjection o) {
    visitPsiElement(o);
  }

  public void visitOpOutputListPolyBranch(@NotNull EpigraphOpOutputListPolyBranch o) {
    visitPsiElement(o);
  }

  public void visitOpOutputMapModelProjection(@NotNull EpigraphOpOutputMapModelProjection o) {
    visitPsiElement(o);
  }

  public void visitOpOutputMapPolyBranch(@NotNull EpigraphOpOutputMapPolyBranch o) {
    visitPsiElement(o);
  }

  public void visitOpOutputModelProjection(@NotNull EpigraphOpOutputModelProjection o) {
    visitPsiElement(o);
  }

  public void visitOpOutputModelProjectionBody(@NotNull EpigraphOpOutputModelProjectionBody o) {
    visitPsiElement(o);
  }

  public void visitOpOutputModelProjectionBodyPart(@NotNull EpigraphOpOutputModelProjectionBodyPart o) {
    visitPsiElement(o);
  }

  public void visitOpOutputPrimitiveModelProjection(@NotNull EpigraphOpOutputPrimitiveModelProjection o) {
    visitPsiElement(o);
  }

  public void visitOpOutputRecordModelProjection(@NotNull EpigraphOpOutputRecordModelProjection o) {
    visitPsiElement(o);
  }

  public void visitOpOutputRecordPolyBranch(@NotNull EpigraphOpOutputRecordPolyBranch o) {
    visitPsiElement(o);
  }

  public void visitOpOutputTagProjection(@NotNull EpigraphOpOutputTagProjection o) {
    visitPsiElement(o);
  }

  public void visitOpOutputVarProjection(@NotNull EpigraphOpOutputVarProjection o) {
    visitPsiElement(o);
  }

  public void visitOpParamProjection(@NotNull EpigraphOpParamProjection o) {
    visitPsiElement(o);
  }

  public void visitOpParameters(@NotNull EpigraphOpParameters o) {
    visitPsiElement(o);
  }

  public void visitPrimitiveTypeBody(@NotNull EpigraphPrimitiveTypeBody o) {
    visitCustomParamsHolder(o);
  }

  public void visitPrimitiveTypeDef(@NotNull EpigraphPrimitiveTypeDef o) {
    visitTypeDef(o);
  }

  public void visitQid(@NotNull EpigraphQid o) {
    visitPsiElement(o);
  }

  public void visitRecordTypeBody(@NotNull EpigraphRecordTypeBody o) {
    visitCustomParamsHolder(o);
  }

  public void visitRecordTypeDef(@NotNull EpigraphRecordTypeDef o) {
    visitTypeDef(o);
  }

  public void visitSupplementDef(@NotNull EpigraphSupplementDef o) {
    visitPsiElement(o);
  }

  public void visitSupplementsDecl(@NotNull EpigraphSupplementsDecl o) {
    visitPsiElement(o);
  }

  public void visitTypeDefWrapper(@NotNull EpigraphTypeDefWrapper o) {
    visitPsiElement(o);
  }

  public void visitTypeRef(@NotNull EpigraphTypeRef o) {
    visitPsiElement(o);
  }

  public void visitValueTypeRef(@NotNull EpigraphValueTypeRef o) {
    visitPsiElement(o);
  }

  public void visitVarTagDecl(@NotNull EpigraphVarTagDecl o) {
    visitCustomParamsHolder(o);
    // visitPsiNamedElement(o);
  }

  public void visitVarTagRef(@NotNull EpigraphVarTagRef o) {
    visitPsiNameIdentifierOwner(o);
  }

  public void visitVarTypeBody(@NotNull EpigraphVarTypeBody o) {
    visitCustomParamsHolder(o);
  }

  public void visitVarTypeDef(@NotNull EpigraphVarTypeDef o) {
    visitTypeDef(o);
  }

  public void visitCustomParamsHolder(@NotNull CustomParamsHolder o) {
    visitElement(o);
  }

  public void visitTypeDef(@NotNull EpigraphTypeDef o) {
    visitPsiElement(o);
  }

  public void visitPsiNameIdentifierOwner(@NotNull PsiNameIdentifierOwner o) {
    visitElement(o);
  }

  public void visitPsiNamedElement(@NotNull PsiNamedElement o) {
    visitElement(o);
  }

  public void visitPsiElement(@NotNull PsiElement o) {
    visitElement(o);
  }

}
