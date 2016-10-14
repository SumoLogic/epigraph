// This is a generated file. Not intended for manual editing.
package io.epigraph.url.parser.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiElement;

public class UrlVisitor extends PsiElementVisitor {

  public void visitAnnotation(@NotNull UrlAnnotation o) {
    visitPsiElement(o);
  }

  public void visitAnonList(@NotNull UrlAnonList o) {
    visitTypeRef(o);
  }

  public void visitAnonMap(@NotNull UrlAnonMap o) {
    visitTypeRef(o);
  }

  public void visitData(@NotNull UrlData o) {
    visitPsiElement(o);
  }

  public void visitDataEntry(@NotNull UrlDataEntry o) {
    visitPsiElement(o);
  }

  public void visitDataValue(@NotNull UrlDataValue o) {
    visitPsiElement(o);
  }

  public void visitDatum(@NotNull UrlDatum o) {
    visitPsiElement(o);
  }

  public void visitDefaultOverride(@NotNull UrlDefaultOverride o) {
    visitPsiElement(o);
  }

  public void visitEnumDatum(@NotNull UrlEnumDatum o) {
    visitDatum(o);
  }

  public void visitListDatum(@NotNull UrlListDatum o) {
    visitDatum(o);
  }

  public void visitMapDatum(@NotNull UrlMapDatum o) {
    visitDatum(o);
  }

  public void visitMapDatumEntry(@NotNull UrlMapDatumEntry o) {
    visitPsiElement(o);
  }

  public void visitNullDatum(@NotNull UrlNullDatum o) {
    visitDatum(o);
  }

  public void visitPrimitiveDatum(@NotNull UrlPrimitiveDatum o) {
    visitDatum(o);
  }

  public void visitQid(@NotNull UrlQid o) {
    visitPsiElement(o);
  }

  public void visitQn(@NotNull UrlQn o) {
    visitPsiElement(o);
  }

  public void visitQnSegment(@NotNull UrlQnSegment o) {
    visitPsiElement(o);
  }

  public void visitQnTypeRef(@NotNull UrlQnTypeRef o) {
    visitTypeRef(o);
  }

  public void visitRecordDatum(@NotNull UrlRecordDatum o) {
    visitDatum(o);
  }

  public void visitRecordDatumEntry(@NotNull UrlRecordDatumEntry o) {
    visitPsiElement(o);
  }

  public void visitReqAnnotation(@NotNull UrlReqAnnotation o) {
    visitPsiElement(o);
  }

  public void visitReqOutputComaFieldProjection(@NotNull UrlReqOutputComaFieldProjection o) {
    visitPsiElement(o);
  }

  public void visitReqOutputComaKeyProjection(@NotNull UrlReqOutputComaKeyProjection o) {
    visitPsiElement(o);
  }

  public void visitReqOutputComaKeysProjection(@NotNull UrlReqOutputComaKeysProjection o) {
    visitPsiElement(o);
  }

  public void visitReqOutputComaListModelProjection(@NotNull UrlReqOutputComaListModelProjection o) {
    visitPsiElement(o);
  }

  public void visitReqOutputComaMapModelProjection(@NotNull UrlReqOutputComaMapModelProjection o) {
    visitPsiElement(o);
  }

  public void visitReqOutputComaModelProjection(@NotNull UrlReqOutputComaModelProjection o) {
    visitPsiElement(o);
  }

  public void visitReqOutputComaMultiTagProjection(@NotNull UrlReqOutputComaMultiTagProjection o) {
    visitPsiElement(o);
  }

  public void visitReqOutputComaMultiTagProjectionItem(@NotNull UrlReqOutputComaMultiTagProjectionItem o) {
    visitPsiElement(o);
  }

  public void visitReqOutputComaRecordModelProjection(@NotNull UrlReqOutputComaRecordModelProjection o) {
    visitPsiElement(o);
  }

  public void visitReqOutputComaSingleTagProjection(@NotNull UrlReqOutputComaSingleTagProjection o) {
    visitPsiElement(o);
  }

  public void visitReqOutputComaVarProjection(@NotNull UrlReqOutputComaVarProjection o) {
    visitPsiElement(o);
  }

  public void visitReqOutputModelMeta(@NotNull UrlReqOutputModelMeta o) {
    visitPsiElement(o);
  }

  public void visitReqOutputTrunkFieldProjection(@NotNull UrlReqOutputTrunkFieldProjection o) {
    visitPsiElement(o);
  }

  public void visitReqOutputTrunkMapModelProjection(@NotNull UrlReqOutputTrunkMapModelProjection o) {
    visitPsiElement(o);
  }

  public void visitReqOutputTrunkModelProjection(@NotNull UrlReqOutputTrunkModelProjection o) {
    visitReqOutputComaModelProjection(o);
  }

  public void visitReqOutputTrunkRecordModelProjection(@NotNull UrlReqOutputTrunkRecordModelProjection o) {
    visitPsiElement(o);
  }

  public void visitReqOutputTrunkSingleTagProjection(@NotNull UrlReqOutputTrunkSingleTagProjection o) {
    visitPsiElement(o);
  }

  public void visitReqOutputTrunkVarProjection(@NotNull UrlReqOutputTrunkVarProjection o) {
    visitPsiElement(o);
  }

  public void visitReqOutputVarMultiTail(@NotNull UrlReqOutputVarMultiTail o) {
    visitPsiElement(o);
  }

  public void visitReqOutputVarMultiTailItem(@NotNull UrlReqOutputVarMultiTailItem o) {
    visitPsiElement(o);
  }

  public void visitReqOutputVarPolymorphicTail(@NotNull UrlReqOutputVarPolymorphicTail o) {
    visitPsiElement(o);
  }

  public void visitReqOutputVarSingleTail(@NotNull UrlReqOutputVarSingleTail o) {
    visitPsiElement(o);
  }

  public void visitReqParam(@NotNull UrlReqParam o) {
    visitPsiElement(o);
  }

  public void visitRequestParam(@NotNull UrlRequestParam o) {
    visitPsiElement(o);
  }

  public void visitTagName(@NotNull UrlTagName o) {
    visitPsiElement(o);
  }

  public void visitTypeRef(@NotNull UrlTypeRef o) {
    visitPsiElement(o);
  }

  public void visitUrl(@NotNull UrlUrl o) {
    visitPsiElement(o);
  }

  public void visitValueTypeRef(@NotNull UrlValueTypeRef o) {
    visitPsiElement(o);
  }

  public void visitVarTagRef(@NotNull UrlVarTagRef o) {
    visitPsiElement(o);
  }

  public void visitPsiElement(@NotNull PsiElement o) {
    visitElement(o);
  }

}
