// This is a generated file. Not intended for manual editing.
package ws.epigraph.edl.parser.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiNameIdentifierOwner;

public class EdlVisitor extends PsiElementVisitor {

  public void visitAnnotation(@NotNull EdlAnnotation o) {
    visitPsiNamedElement(o);
  }

  public void visitAnonList(@NotNull EdlAnonList o) {
    visitTypeRef(o);
  }

  public void visitAnonMap(@NotNull EdlAnonMap o) {
    visitTypeRef(o);
  }

  public void visitCreateOperationBodyPart(@NotNull EdlCreateOperationBodyPart o) {
    visitPsiElement(o);
  }

  public void visitCreateOperationDef(@NotNull EdlCreateOperationDef o) {
    visitPsiElement(o);
  }

  public void visitCustomOperationBodyPart(@NotNull EdlCustomOperationBodyPart o) {
    visitPsiElement(o);
  }

  public void visitCustomOperationDef(@NotNull EdlCustomOperationDef o) {
    visitPsiElement(o);
  }

  public void visitData(@NotNull EdlData o) {
    visitPsiElement(o);
  }

  public void visitDataEntry(@NotNull EdlDataEntry o) {
    visitPsiElement(o);
  }

  public void visitDataValue(@NotNull EdlDataValue o) {
    visitPsiElement(o);
  }

  public void visitDatum(@NotNull EdlDatum o) {
    visitPsiElement(o);
  }

  public void visitDefaultOverride(@NotNull EdlDefaultOverride o) {
    visitPsiElement(o);
  }

  public void visitDefs(@NotNull EdlDefs o) {
    visitPsiElement(o);
  }

  public void visitDeleteOperationBodyPart(@NotNull EdlDeleteOperationBodyPart o) {
    visitPsiElement(o);
  }

  public void visitDeleteOperationDef(@NotNull EdlDeleteOperationDef o) {
    visitPsiElement(o);
  }

  public void visitEnumDatum(@NotNull EdlEnumDatum o) {
    visitDatum(o);
  }

  public void visitEnumMemberDecl(@NotNull EdlEnumMemberDecl o) {
    visitAnnotationsHolder(o);
    // visitPsiNamedElement(o);
  }

  public void visitEnumTypeBody(@NotNull EdlEnumTypeBody o) {
    visitAnnotationsHolder(o);
  }

  public void visitEnumTypeDef(@NotNull EdlEnumTypeDef o) {
    visitTypeDef(o);
  }

  public void visitExtendsDecl(@NotNull EdlExtendsDecl o) {
    visitPsiElement(o);
  }

  public void visitFieldDecl(@NotNull EdlFieldDecl o) {
    visitAnnotationsHolder(o);
    // visitPsiNamedElement(o);
  }

  public void visitImportStatement(@NotNull EdlImportStatement o) {
    visitPsiElement(o);
  }

  public void visitImports(@NotNull EdlImports o) {
    visitPsiElement(o);
  }

  public void visitListDatum(@NotNull EdlListDatum o) {
    visitDatum(o);
  }

  public void visitListTypeBody(@NotNull EdlListTypeBody o) {
    visitAnnotationsHolder(o);
  }

  public void visitListTypeDef(@NotNull EdlListTypeDef o) {
    visitTypeDef(o);
  }

  public void visitMapDatum(@NotNull EdlMapDatum o) {
    visitDatum(o);
  }

  public void visitMapDatumEntry(@NotNull EdlMapDatumEntry o) {
    visitPsiElement(o);
  }

  public void visitMapTypeBody(@NotNull EdlMapTypeBody o) {
    visitAnnotationsHolder(o);
  }

  public void visitMapTypeDef(@NotNull EdlMapTypeDef o) {
    visitTypeDef(o);
  }

  public void visitMetaDecl(@NotNull EdlMetaDecl o) {
    visitPsiElement(o);
  }

  public void visitNamespaceDecl(@NotNull EdlNamespaceDecl o) {
    visitPsiElement(o);
  }

  public void visitNullDatum(@NotNull EdlNullDatum o) {
    visitDatum(o);
  }

  public void visitOpDeleteFieldProjection(@NotNull EdlOpDeleteFieldProjection o) {
    visitPsiElement(o);
  }

  public void visitOpDeleteFieldProjectionBodyPart(@NotNull EdlOpDeleteFieldProjectionBodyPart o) {
    visitPsiElement(o);
  }

  public void visitOpDeleteFieldProjectionEntry(@NotNull EdlOpDeleteFieldProjectionEntry o) {
    visitPsiElement(o);
  }

  public void visitOpDeleteKeyProjection(@NotNull EdlOpDeleteKeyProjection o) {
    visitPsiElement(o);
  }

  public void visitOpDeleteKeyProjectionPart(@NotNull EdlOpDeleteKeyProjectionPart o) {
    visitPsiElement(o);
  }

  public void visitOpDeleteListModelProjection(@NotNull EdlOpDeleteListModelProjection o) {
    visitPsiElement(o);
  }

  public void visitOpDeleteMapModelProjection(@NotNull EdlOpDeleteMapModelProjection o) {
    visitPsiElement(o);
  }

  public void visitOpDeleteModelProjection(@NotNull EdlOpDeleteModelProjection o) {
    visitPsiElement(o);
  }

  public void visitOpDeleteModelProperty(@NotNull EdlOpDeleteModelProperty o) {
    visitPsiElement(o);
  }

  public void visitOpDeleteMultiTagProjection(@NotNull EdlOpDeleteMultiTagProjection o) {
    visitPsiElement(o);
  }

  public void visitOpDeleteMultiTagProjectionItem(@NotNull EdlOpDeleteMultiTagProjectionItem o) {
    visitPsiElement(o);
  }

  public void visitOpDeleteRecordModelProjection(@NotNull EdlOpDeleteRecordModelProjection o) {
    visitPsiElement(o);
  }

  public void visitOpDeleteSingleTagProjection(@NotNull EdlOpDeleteSingleTagProjection o) {
    visitPsiElement(o);
  }

  public void visitOpDeleteVarMultiTail(@NotNull EdlOpDeleteVarMultiTail o) {
    visitPsiElement(o);
  }

  public void visitOpDeleteVarMultiTailItem(@NotNull EdlOpDeleteVarMultiTailItem o) {
    visitPsiElement(o);
  }

  public void visitOpDeleteVarPolymorphicTail(@NotNull EdlOpDeleteVarPolymorphicTail o) {
    visitPsiElement(o);
  }

  public void visitOpDeleteVarProjection(@NotNull EdlOpDeleteVarProjection o) {
    visitPsiElement(o);
  }

  public void visitOpDeleteVarSingleTail(@NotNull EdlOpDeleteVarSingleTail o) {
    visitPsiElement(o);
  }

  public void visitOpFieldPath(@NotNull EdlOpFieldPath o) {
    visitPsiElement(o);
  }

  public void visitOpFieldPathBodyPart(@NotNull EdlOpFieldPathBodyPart o) {
    visitPsiElement(o);
  }

  public void visitOpFieldPathEntry(@NotNull EdlOpFieldPathEntry o) {
    visitPsiElement(o);
  }

  public void visitOpInputDefaultValue(@NotNull EdlOpInputDefaultValue o) {
    visitPsiElement(o);
  }

  public void visitOpInputFieldProjection(@NotNull EdlOpInputFieldProjection o) {
    visitPsiElement(o);
  }

  public void visitOpInputFieldProjectionBodyPart(@NotNull EdlOpInputFieldProjectionBodyPart o) {
    visitPsiElement(o);
  }

  public void visitOpInputFieldProjectionEntry(@NotNull EdlOpInputFieldProjectionEntry o) {
    visitPsiElement(o);
  }

  public void visitOpInputKeyProjection(@NotNull EdlOpInputKeyProjection o) {
    visitPsiElement(o);
  }

  public void visitOpInputKeyProjectionPart(@NotNull EdlOpInputKeyProjectionPart o) {
    visitPsiElement(o);
  }

  public void visitOpInputListModelProjection(@NotNull EdlOpInputListModelProjection o) {
    visitPsiElement(o);
  }

  public void visitOpInputMapModelProjection(@NotNull EdlOpInputMapModelProjection o) {
    visitPsiElement(o);
  }

  public void visitOpInputModelMeta(@NotNull EdlOpInputModelMeta o) {
    visitPsiElement(o);
  }

  public void visitOpInputModelProjection(@NotNull EdlOpInputModelProjection o) {
    visitPsiElement(o);
  }

  public void visitOpInputModelProperty(@NotNull EdlOpInputModelProperty o) {
    visitPsiElement(o);
  }

  public void visitOpInputMultiTagProjection(@NotNull EdlOpInputMultiTagProjection o) {
    visitPsiElement(o);
  }

  public void visitOpInputMultiTagProjectionItem(@NotNull EdlOpInputMultiTagProjectionItem o) {
    visitPsiElement(o);
  }

  public void visitOpInputRecordModelProjection(@NotNull EdlOpInputRecordModelProjection o) {
    visitPsiElement(o);
  }

  public void visitOpInputSingleTagProjection(@NotNull EdlOpInputSingleTagProjection o) {
    visitPsiElement(o);
  }

  public void visitOpInputVarMultiTail(@NotNull EdlOpInputVarMultiTail o) {
    visitPsiElement(o);
  }

  public void visitOpInputVarMultiTailItem(@NotNull EdlOpInputVarMultiTailItem o) {
    visitPsiElement(o);
  }

  public void visitOpInputVarPolymorphicTail(@NotNull EdlOpInputVarPolymorphicTail o) {
    visitPsiElement(o);
  }

  public void visitOpInputVarProjection(@NotNull EdlOpInputVarProjection o) {
    visitPsiElement(o);
  }

  public void visitOpInputVarSingleTail(@NotNull EdlOpInputVarSingleTail o) {
    visitPsiElement(o);
  }

  public void visitOpMapModelPath(@NotNull EdlOpMapModelPath o) {
    visitPsiElement(o);
  }

  public void visitOpModelPath(@NotNull EdlOpModelPath o) {
    visitPsiElement(o);
  }

  public void visitOpModelPathProperty(@NotNull EdlOpModelPathProperty o) {
    visitPsiElement(o);
  }

  public void visitOpOutputFieldProjection(@NotNull EdlOpOutputFieldProjection o) {
    visitPsiElement(o);
  }

  public void visitOpOutputFieldProjectionBodyPart(@NotNull EdlOpOutputFieldProjectionBodyPart o) {
    visitPsiElement(o);
  }

  public void visitOpOutputFieldProjectionEntry(@NotNull EdlOpOutputFieldProjectionEntry o) {
    visitPsiElement(o);
  }

  public void visitOpOutputKeyProjection(@NotNull EdlOpOutputKeyProjection o) {
    visitPsiElement(o);
  }

  public void visitOpOutputKeyProjectionPart(@NotNull EdlOpOutputKeyProjectionPart o) {
    visitPsiElement(o);
  }

  public void visitOpOutputListModelProjection(@NotNull EdlOpOutputListModelProjection o) {
    visitPsiElement(o);
  }

  public void visitOpOutputMapModelProjection(@NotNull EdlOpOutputMapModelProjection o) {
    visitPsiElement(o);
  }

  public void visitOpOutputModelMeta(@NotNull EdlOpOutputModelMeta o) {
    visitPsiElement(o);
  }

  public void visitOpOutputModelProjection(@NotNull EdlOpOutputModelProjection o) {
    visitPsiElement(o);
  }

  public void visitOpOutputModelProperty(@NotNull EdlOpOutputModelProperty o) {
    visitPsiElement(o);
  }

  public void visitOpOutputMultiTagProjection(@NotNull EdlOpOutputMultiTagProjection o) {
    visitPsiElement(o);
  }

  public void visitOpOutputMultiTagProjectionItem(@NotNull EdlOpOutputMultiTagProjectionItem o) {
    visitPsiElement(o);
  }

  public void visitOpOutputRecordModelProjection(@NotNull EdlOpOutputRecordModelProjection o) {
    visitPsiElement(o);
  }

  public void visitOpOutputSingleTagProjection(@NotNull EdlOpOutputSingleTagProjection o) {
    visitPsiElement(o);
  }

  public void visitOpOutputVarMultiTail(@NotNull EdlOpOutputVarMultiTail o) {
    visitPsiElement(o);
  }

  public void visitOpOutputVarMultiTailItem(@NotNull EdlOpOutputVarMultiTailItem o) {
    visitPsiElement(o);
  }

  public void visitOpOutputVarPolymorphicTail(@NotNull EdlOpOutputVarPolymorphicTail o) {
    visitPsiElement(o);
  }

  public void visitOpOutputVarProjection(@NotNull EdlOpOutputVarProjection o) {
    visitPsiElement(o);
  }

  public void visitOpOutputVarSingleTail(@NotNull EdlOpOutputVarSingleTail o) {
    visitPsiElement(o);
  }

  public void visitOpParam(@NotNull EdlOpParam o) {
    visitPsiElement(o);
  }

  public void visitOpPathKeyProjection(@NotNull EdlOpPathKeyProjection o) {
    visitPsiElement(o);
  }

  public void visitOpPathKeyProjectionBody(@NotNull EdlOpPathKeyProjectionBody o) {
    visitPsiElement(o);
  }

  public void visitOpPathKeyProjectionPart(@NotNull EdlOpPathKeyProjectionPart o) {
    visitPsiElement(o);
  }

  public void visitOpRecordModelPath(@NotNull EdlOpRecordModelPath o) {
    visitPsiElement(o);
  }

  public void visitOpVarPath(@NotNull EdlOpVarPath o) {
    visitPsiElement(o);
  }

  public void visitOperationDef(@NotNull EdlOperationDef o) {
    visitPsiElement(o);
  }

  public void visitOperationDeleteProjection(@NotNull EdlOperationDeleteProjection o) {
    visitPsiElement(o);
  }

  public void visitOperationInputProjection(@NotNull EdlOperationInputProjection o) {
    visitPsiElement(o);
  }

  public void visitOperationInputType(@NotNull EdlOperationInputType o) {
    visitPsiElement(o);
  }

  public void visitOperationMethod(@NotNull EdlOperationMethod o) {
    visitPsiElement(o);
  }

  public void visitOperationName(@NotNull EdlOperationName o) {
    visitPsiElement(o);
  }

  public void visitOperationOutputProjection(@NotNull EdlOperationOutputProjection o) {
    visitPsiElement(o);
  }

  public void visitOperationOutputType(@NotNull EdlOperationOutputType o) {
    visitPsiElement(o);
  }

  public void visitOperationPath(@NotNull EdlOperationPath o) {
    visitPsiElement(o);
  }

  public void visitPrimitiveDatum(@NotNull EdlPrimitiveDatum o) {
    visitDatum(o);
  }

  public void visitPrimitiveTypeBody(@NotNull EdlPrimitiveTypeBody o) {
    visitAnnotationsHolder(o);
  }

  public void visitPrimitiveTypeDef(@NotNull EdlPrimitiveTypeDef o) {
    visitTypeDef(o);
  }

  public void visitQid(@NotNull EdlQid o) {
    visitPsiElement(o);
  }

  public void visitQn(@NotNull EdlQn o) {
    visitPsiElement(o);
  }

  public void visitQnSegment(@NotNull EdlQnSegment o) {
    visitPsiNameIdentifierOwner(o);
  }

  public void visitQnTypeRef(@NotNull EdlQnTypeRef o) {
    visitTypeRef(o);
  }

  public void visitReadOperationBodyPart(@NotNull EdlReadOperationBodyPart o) {
    visitPsiElement(o);
  }

  public void visitReadOperationDef(@NotNull EdlReadOperationDef o) {
    visitPsiElement(o);
  }

  public void visitRecordDatum(@NotNull EdlRecordDatum o) {
    visitDatum(o);
  }

  public void visitRecordDatumEntry(@NotNull EdlRecordDatumEntry o) {
    visitPsiElement(o);
  }

  public void visitRecordTypeBody(@NotNull EdlRecordTypeBody o) {
    visitAnnotationsHolder(o);
  }

  public void visitRecordTypeDef(@NotNull EdlRecordTypeDef o) {
    visitTypeDef(o);
  }

  public void visitResourceDef(@NotNull EdlResourceDef o) {
    visitPsiElement(o);
  }

  public void visitResourceName(@NotNull EdlResourceName o) {
    visitPsiElement(o);
  }

  public void visitResourceType(@NotNull EdlResourceType o) {
    visitPsiElement(o);
  }

  public void visitSupplementDef(@NotNull EdlSupplementDef o) {
    visitPsiElement(o);
  }

  public void visitSupplementsDecl(@NotNull EdlSupplementsDecl o) {
    visitPsiElement(o);
  }

  public void visitTagName(@NotNull EdlTagName o) {
    visitPsiElement(o);
  }

  public void visitTypeDefWrapper(@NotNull EdlTypeDefWrapper o) {
    visitPsiElement(o);
  }

  public void visitTypeRef(@NotNull EdlTypeRef o) {
    visitPsiElement(o);
  }

  public void visitUpdateOperationBodyPart(@NotNull EdlUpdateOperationBodyPart o) {
    visitPsiElement(o);
  }

  public void visitUpdateOperationDef(@NotNull EdlUpdateOperationDef o) {
    visitPsiElement(o);
  }

  public void visitValueTypeRef(@NotNull EdlValueTypeRef o) {
    visitPsiElement(o);
  }

  public void visitVarTagDecl(@NotNull EdlVarTagDecl o) {
    visitAnnotationsHolder(o);
    // visitPsiNamedElement(o);
  }

  public void visitVarTagRef(@NotNull EdlVarTagRef o) {
    visitPsiNameIdentifierOwner(o);
  }

  public void visitVarTypeBody(@NotNull EdlVarTypeBody o) {
    visitAnnotationsHolder(o);
  }

  public void visitVarTypeDef(@NotNull EdlVarTypeDef o) {
    visitTypeDef(o);
  }

  public void visitAnnotationsHolder(@NotNull AnnotationsHolder o) {
    visitElement(o);
  }

  public void visitTypeDef(@NotNull EdlTypeDef o) {
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
