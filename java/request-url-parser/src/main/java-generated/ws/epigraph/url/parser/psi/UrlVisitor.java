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

// This is a generated file. Not intended for manual editing.
package ws.epigraph.url.parser.psi;

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

  public void visitInputProjection(@NotNull UrlInputProjection o) {
    visitPsiElement(o);
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

  public void visitNonReadUrl(@NotNull UrlNonReadUrl o) {
    visitUrl(o);
  }

  public void visitNullDatum(@NotNull UrlNullDatum o) {
    visitDatum(o);
  }

  public void visitOutputProjection(@NotNull UrlOutputProjection o) {
    visitPsiElement(o);
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

  public void visitReadUrl(@NotNull UrlReadUrl o) {
    visitUrl(o);
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

  public void visitReqFieldPath(@NotNull UrlReqFieldPath o) {
    visitPsiElement(o);
  }

  public void visitReqFieldPathEntry(@NotNull UrlReqFieldPathEntry o) {
    visitPsiElement(o);
  }

  public void visitReqMapModelPath(@NotNull UrlReqMapModelPath o) {
    visitPsiElement(o);
  }

  public void visitReqModelPath(@NotNull UrlReqModelPath o) {
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

  public void visitReqOutputComaVarProjectionRef(@NotNull UrlReqOutputComaVarProjectionRef o) {
    visitPsiElement(o);
  }

  public void visitReqOutputModelMeta(@NotNull UrlReqOutputModelMeta o) {
    visitPsiElement(o);
  }

  public void visitReqOutputModelMultiTail(@NotNull UrlReqOutputModelMultiTail o) {
    visitPsiElement(o);
  }

  public void visitReqOutputModelMultiTailItem(@NotNull UrlReqOutputModelMultiTailItem o) {
    visitPsiElement(o);
  }

  public void visitReqOutputModelPolymorphicTail(@NotNull UrlReqOutputModelPolymorphicTail o) {
    visitPsiElement(o);
  }

  public void visitReqOutputModelSingleTail(@NotNull UrlReqOutputModelSingleTail o) {
    visitPsiElement(o);
  }

  public void visitReqOutputNamedComaVarProjection(@NotNull UrlReqOutputNamedComaVarProjection o) {
    visitPsiElement(o);
  }

  public void visitReqOutputNamedTrunkVarProjection(@NotNull UrlReqOutputNamedTrunkVarProjection o) {
    visitPsiElement(o);
  }

  public void visitReqOutputStarTagProjection(@NotNull UrlReqOutputStarTagProjection o) {
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

  public void visitReqOutputTrunkVarProjectionRef(@NotNull UrlReqOutputTrunkVarProjectionRef o) {
    visitPsiElement(o);
  }

  public void visitReqOutputUnnamedComaVarProjection(@NotNull UrlReqOutputUnnamedComaVarProjection o) {
    visitPsiElement(o);
  }

  public void visitReqOutputUnnamedOrRefComaVarProjection(@NotNull UrlReqOutputUnnamedOrRefComaVarProjection o) {
    visitPsiElement(o);
  }

  public void visitReqOutputUnnamedOrRefTrunkVarProjection(@NotNull UrlReqOutputUnnamedOrRefTrunkVarProjection o) {
    visitPsiElement(o);
  }

  public void visitReqOutputUnnamedTrunkVarProjection(@NotNull UrlReqOutputUnnamedTrunkVarProjection o) {
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

  public void visitReqRecordModelPath(@NotNull UrlReqRecordModelPath o) {
    visitPsiElement(o);
  }

  public void visitReqVarPath(@NotNull UrlReqVarPath o) {
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
