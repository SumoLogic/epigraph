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

  public void visitRecordDatum(@NotNull UrlRecordDatum o) {
    visitDatum(o);
  }

  public void visitRecordDatumEntry(@NotNull UrlRecordDatumEntry o) {
    visitPsiElement(o);
  }

  public void visitReqAll(@NotNull UrlReqAll o) {
    visitPsiElement(o);
  }

  public void visitReqAnnotation(@NotNull UrlReqAnnotation o) {
    visitPsiElement(o);
  }

  public void visitReqComaEntityProjection(@NotNull UrlReqComaEntityProjection o) {
    visitPsiElement(o);
  }

  public void visitReqComaEntityProjectionRef(@NotNull UrlReqComaEntityProjectionRef o) {
    visitPsiElement(o);
  }

  public void visitReqComaFieldProjection(@NotNull UrlReqComaFieldProjection o) {
    visitPsiElement(o);
  }

  public void visitReqComaKeyProjection(@NotNull UrlReqComaKeyProjection o) {
    visitPsiElement(o);
  }

  public void visitReqComaKeysProjection(@NotNull UrlReqComaKeysProjection o) {
    visitPsiElement(o);
  }

  public void visitReqComaListModelProjection(@NotNull UrlReqComaListModelProjection o) {
    visitPsiElement(o);
  }

  public void visitReqComaMapModelProjection(@NotNull UrlReqComaMapModelProjection o) {
    visitPsiElement(o);
  }

  public void visitReqComaModelProjection(@NotNull UrlReqComaModelProjection o) {
    visitPsiElement(o);
  }

  public void visitReqComaModelProjectionRef(@NotNull UrlReqComaModelProjectionRef o) {
    visitPsiElement(o);
  }

  public void visitReqComaMultiTagProjection(@NotNull UrlReqComaMultiTagProjection o) {
    visitPsiElement(o);
  }

  public void visitReqComaMultiTagProjectionItem(@NotNull UrlReqComaMultiTagProjectionItem o) {
    visitPsiElement(o);
  }

  public void visitReqComaRecordModelProjection(@NotNull UrlReqComaRecordModelProjection o) {
    visitPsiElement(o);
  }

  public void visitReqComaSingleTagProjection(@NotNull UrlReqComaSingleTagProjection o) {
    visitPsiElement(o);
  }

  public void visitReqEntityMultiTail(@NotNull UrlReqEntityMultiTail o) {
    visitPsiElement(o);
  }

  public void visitReqEntityMultiTailItem(@NotNull UrlReqEntityMultiTailItem o) {
    visitPsiElement(o);
  }

  public void visitReqEntityPath(@NotNull UrlReqEntityPath o) {
    visitPsiElement(o);
  }

  public void visitReqEntityPolymorphicTail(@NotNull UrlReqEntityPolymorphicTail o) {
    visitPsiElement(o);
  }

  public void visitReqEntitySingleTail(@NotNull UrlReqEntitySingleTail o) {
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

  public void visitReqModelMeta(@NotNull UrlReqModelMeta o) {
    visitPsiElement(o);
  }

  public void visitReqModelMultiTail(@NotNull UrlReqModelMultiTail o) {
    visitPsiElement(o);
  }

  public void visitReqModelMultiTailItem(@NotNull UrlReqModelMultiTailItem o) {
    visitPsiElement(o);
  }

  public void visitReqModelPath(@NotNull UrlReqModelPath o) {
    visitPsiElement(o);
  }

  public void visitReqModelPolymorphicTail(@NotNull UrlReqModelPolymorphicTail o) {
    visitPsiElement(o);
  }

  public void visitReqModelSingleTail(@NotNull UrlReqModelSingleTail o) {
    visitPsiElement(o);
  }

  public void visitReqNamedComaEntityProjection(@NotNull UrlReqNamedComaEntityProjection o) {
    visitPsiElement(o);
  }

  public void visitReqNamedTrunkEntityProjection(@NotNull UrlReqNamedTrunkEntityProjection o) {
    visitPsiElement(o);
  }

  public void visitReqParam(@NotNull UrlReqParam o) {
    visitPsiElement(o);
  }

  public void visitReqRecordModelPath(@NotNull UrlReqRecordModelPath o) {
    visitPsiElement(o);
  }

  public void visitReqStarTagProjection(@NotNull UrlReqStarTagProjection o) {
    visitPsiElement(o);
  }

  public void visitReqTrunkEntityProjection(@NotNull UrlReqTrunkEntityProjection o) {
    visitPsiElement(o);
  }

  public void visitReqTrunkEntityProjectionRef(@NotNull UrlReqTrunkEntityProjectionRef o) {
    visitPsiElement(o);
  }

  public void visitReqTrunkFieldProjection(@NotNull UrlReqTrunkFieldProjection o) {
    visitPsiElement(o);
  }

  public void visitReqTrunkMapModelProjection(@NotNull UrlReqTrunkMapModelProjection o) {
    visitPsiElement(o);
  }

  public void visitReqTrunkModelProjection(@NotNull UrlReqTrunkModelProjection o) {
    visitReqComaModelProjection(o);
  }

  public void visitReqTrunkRecordModelProjection(@NotNull UrlReqTrunkRecordModelProjection o) {
    visitPsiElement(o);
  }

  public void visitReqTrunkSingleTagProjection(@NotNull UrlReqTrunkSingleTagProjection o) {
    visitPsiElement(o);
  }

  public void visitReqUnnamedComaEntityProjection(@NotNull UrlReqUnnamedComaEntityProjection o) {
    visitPsiElement(o);
  }

  public void visitReqUnnamedOrRefComaEntityProjection(@NotNull UrlReqUnnamedOrRefComaEntityProjection o) {
    visitPsiElement(o);
  }

  public void visitReqUnnamedOrRefComaModelProjection(@NotNull UrlReqUnnamedOrRefComaModelProjection o) {
    visitPsiElement(o);
  }

  public void visitReqUnnamedOrRefTrunkEntityProjection(@NotNull UrlReqUnnamedOrRefTrunkEntityProjection o) {
    visitPsiElement(o);
  }

  public void visitReqUnnamedTrunkEntityProjection(@NotNull UrlReqUnnamedTrunkEntityProjection o) {
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
