// This is a generated file. Not intended for manual editing.
package io.epigraph.idl.parser.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiElement;

public class IdlVisitor extends PsiElementVisitor {

  public void visitCustomParam(@NotNull IdlCustomParam o) {
    visitPsiElement(o);
  }

  public void visitDataEnum(@NotNull IdlDataEnum o) {
    visitDataValue(o);
  }

  public void visitDataList(@NotNull IdlDataList o) {
    visitDataValue(o);
  }

  public void visitDataMap(@NotNull IdlDataMap o) {
    visitDataValue(o);
  }

  public void visitDataMapEntry(@NotNull IdlDataMapEntry o) {
    visitPsiElement(o);
  }

  public void visitDataPrimitive(@NotNull IdlDataPrimitive o) {
    visitPsiElement(o);
  }

  public void visitDataRecord(@NotNull IdlDataRecord o) {
    visitDataValue(o);
  }

  public void visitDataRecordEntry(@NotNull IdlDataRecordEntry o) {
    visitPsiElement(o);
  }

  public void visitDataValue(@NotNull IdlDataValue o) {
    visitPsiElement(o);
  }

  public void visitDataVar(@NotNull IdlDataVar o) {
    visitDataValue(o);
  }

  public void visitDataVarEntry(@NotNull IdlDataVarEntry o) {
    visitPsiElement(o);
  }

  public void visitFqn(@NotNull IdlFqn o) {
    visitPsiElement(o);
  }

  public void visitFqnSegment(@NotNull IdlFqnSegment o) {
    visitPsiElement(o);
  }

  public void visitFqnTypeRef(@NotNull IdlFqnTypeRef o) {
    visitPsiElement(o);
  }

  public void visitImportStatement(@NotNull IdlImportStatement o) {
    visitPsiElement(o);
  }

  public void visitImports(@NotNull IdlImports o) {
    visitPsiElement(o);
  }

  public void visitNamespaceDecl(@NotNull IdlNamespaceDecl o) {
    visitPsiElement(o);
  }

  public void visitOpInputModelProjection(@NotNull IdlOpInputModelProjection o) {
    visitPsiElement(o);
  }

  public void visitOpOutputEnumModelProjection(@NotNull IdlOpOutputEnumModelProjection o) {
    visitPsiElement(o);
  }

  public void visitOpOutputFieldProjection(@NotNull IdlOpOutputFieldProjection o) {
    visitPsiElement(o);
  }

  public void visitOpOutputFieldProjectionBody(@NotNull IdlOpOutputFieldProjectionBody o) {
    visitPsiElement(o);
  }

  public void visitOpOutputFieldProjectionBodyPart(@NotNull IdlOpOutputFieldProjectionBodyPart o) {
    visitPsiElement(o);
  }

  public void visitOpOutputKeyProjection(@NotNull IdlOpOutputKeyProjection o) {
    visitPsiElement(o);
  }

  public void visitOpOutputKeyProjectionPart(@NotNull IdlOpOutputKeyProjectionPart o) {
    visitPsiElement(o);
  }

  public void visitOpOutputListModelProjection(@NotNull IdlOpOutputListModelProjection o) {
    visitOpOutputModelProjection(o);
  }

  public void visitOpOutputListPolyBranch(@NotNull IdlOpOutputListPolyBranch o) {
    visitPsiElement(o);
  }

  public void visitOpOutputMapModelProjection(@NotNull IdlOpOutputMapModelProjection o) {
    visitOpOutputModelProjection(o);
  }

  public void visitOpOutputMapPolyBranch(@NotNull IdlOpOutputMapPolyBranch o) {
    visitPsiElement(o);
  }

  public void visitOpOutputModelProjection(@NotNull IdlOpOutputModelProjection o) {
    visitPsiElement(o);
  }

  public void visitOpOutputModelProjectionBody(@NotNull IdlOpOutputModelProjectionBody o) {
    visitPsiElement(o);
  }

  public void visitOpOutputModelProjectionBodyPart(@NotNull IdlOpOutputModelProjectionBodyPart o) {
    visitPsiElement(o);
  }

  public void visitOpOutputPrimitiveModelProjection(@NotNull IdlOpOutputPrimitiveModelProjection o) {
    visitPsiElement(o);
  }

  public void visitOpOutputRecordModelProjection(@NotNull IdlOpOutputRecordModelProjection o) {
    visitOpOutputModelProjection(o);
  }

  public void visitOpOutputRecordPolyBranch(@NotNull IdlOpOutputRecordPolyBranch o) {
    visitPsiElement(o);
  }

  public void visitOpOutputTagProjection(@NotNull IdlOpOutputTagProjection o) {
    visitPsiElement(o);
  }

  public void visitOpOutputVarProjection(@NotNull IdlOpOutputVarProjection o) {
    visitPsiElement(o);
  }

  public void visitOpParamProjection(@NotNull IdlOpParamProjection o) {
    visitPsiElement(o);
  }

  public void visitOpParameters(@NotNull IdlOpParameters o) {
    visitPsiElement(o);
  }

  public void visitQid(@NotNull IdlQid o) {
    visitPsiElement(o);
  }

  public void visitPsiElement(@NotNull PsiElement o) {
    visitElement(o);
  }

}
