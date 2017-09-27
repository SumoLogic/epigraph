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
package ws.epigraph.schema.parser.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiNameIdentifierOwner;

public class SchemaVisitor extends PsiElementVisitor {

  public void visitAnnotation(@NotNull SchemaAnnotation o) {
    visitPsiElement(o);
  }

  public void visitAnonList(@NotNull SchemaAnonList o) {
    visitTypeRef(o);
  }

  public void visitAnonMap(@NotNull SchemaAnonMap o) {
    visitTypeRef(o);
  }

  public void visitCreateOperationBodyPart(@NotNull SchemaCreateOperationBodyPart o) {
    visitPsiElement(o);
  }

  public void visitCreateOperationDef(@NotNull SchemaCreateOperationDef o) {
    visitPsiElement(o);
  }

  public void visitCustomOperationBodyPart(@NotNull SchemaCustomOperationBodyPart o) {
    visitPsiElement(o);
  }

  public void visitCustomOperationDef(@NotNull SchemaCustomOperationDef o) {
    visitPsiElement(o);
  }

  public void visitData(@NotNull SchemaData o) {
    visitPsiElement(o);
  }

  public void visitDataEntry(@NotNull SchemaDataEntry o) {
    visitPsiElement(o);
  }

  public void visitDataValue(@NotNull SchemaDataValue o) {
    visitPsiElement(o);
  }

  public void visitDatum(@NotNull SchemaDatum o) {
    visitPsiElement(o);
  }

  public void visitDefs(@NotNull SchemaDefs o) {
    visitPsiElement(o);
  }

  public void visitDeleteOperationBodyPart(@NotNull SchemaDeleteOperationBodyPart o) {
    visitPsiElement(o);
  }

  public void visitDeleteOperationDef(@NotNull SchemaDeleteOperationDef o) {
    visitPsiElement(o);
  }

  public void visitDeleteProjection(@NotNull SchemaDeleteProjection o) {
    visitPsiElement(o);
  }

  public void visitDeleteProjectionDef(@NotNull SchemaDeleteProjectionDef o) {
    visitPsiElement(o);
  }

  public void visitEntityTagDecl(@NotNull SchemaEntityTagDecl o) {
    visitAnnotationsHolder(o);
    // visitPsiNamedElement(o);
  }

  public void visitEntityTagRef(@NotNull SchemaEntityTagRef o) {
    visitPsiNameIdentifierOwner(o);
  }

  public void visitEntityTypeBody(@NotNull SchemaEntityTypeBody o) {
    visitAnnotationsHolder(o);
  }

  public void visitEntityTypeDef(@NotNull SchemaEntityTypeDef o) {
    visitTypeDef(o);
  }

  public void visitEnumDatum(@NotNull SchemaEnumDatum o) {
    visitDatum(o);
  }

  public void visitEnumMemberDecl(@NotNull SchemaEnumMemberDecl o) {
    visitAnnotationsHolder(o);
    // visitPsiNamedElement(o);
  }

  public void visitEnumTypeBody(@NotNull SchemaEnumTypeBody o) {
    visitAnnotationsHolder(o);
  }

  public void visitEnumTypeDef(@NotNull SchemaEnumTypeDef o) {
    visitTypeDef(o);
  }

  public void visitExtendsDecl(@NotNull SchemaExtendsDecl o) {
    visitPsiElement(o);
  }

  public void visitFieldDecl(@NotNull SchemaFieldDecl o) {
    visitAnnotationsHolder(o);
    // visitPsiNamedElement(o);
  }

  public void visitImportStatement(@NotNull SchemaImportStatement o) {
    visitPsiElement(o);
  }

  public void visitImports(@NotNull SchemaImports o) {
    visitPsiElement(o);
  }

  public void visitInputProjection(@NotNull SchemaInputProjection o) {
    visitPsiElement(o);
  }

  public void visitInputProjectionDef(@NotNull SchemaInputProjectionDef o) {
    visitPsiElement(o);
  }

  public void visitListDatum(@NotNull SchemaListDatum o) {
    visitDatum(o);
  }

  public void visitListTypeBody(@NotNull SchemaListTypeBody o) {
    visitAnnotationsHolder(o);
  }

  public void visitListTypeDef(@NotNull SchemaListTypeDef o) {
    visitTypeDef(o);
  }

  public void visitMapDatum(@NotNull SchemaMapDatum o) {
    visitDatum(o);
  }

  public void visitMapDatumEntry(@NotNull SchemaMapDatumEntry o) {
    visitPsiElement(o);
  }

  public void visitMapTypeBody(@NotNull SchemaMapTypeBody o) {
    visitAnnotationsHolder(o);
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

  public void visitNullDatum(@NotNull SchemaNullDatum o) {
    visitDatum(o);
  }

  public void visitOpDefaultValue(@NotNull SchemaOpDefaultValue o) {
    visitPsiElement(o);
  }

  public void visitOpEntityMultiTail(@NotNull SchemaOpEntityMultiTail o) {
    visitPsiElement(o);
  }

  public void visitOpEntityPolymorphicTail(@NotNull SchemaOpEntityPolymorphicTail o) {
    visitPsiElement(o);
  }

  public void visitOpEntityProjection(@NotNull SchemaOpEntityProjection o) {
    visitPsiElement(o);
  }

  public void visitOpEntityProjectionRef(@NotNull SchemaOpEntityProjectionRef o) {
    visitPsiElement(o);
  }

  public void visitOpEntityTailItem(@NotNull SchemaOpEntityTailItem o) {
    visitPsiElement(o);
  }

  public void visitOpFieldPath(@NotNull SchemaOpFieldPath o) {
    visitPsiElement(o);
  }

  public void visitOpFieldPathEntry(@NotNull SchemaOpFieldPathEntry o) {
    visitPsiElement(o);
  }

  public void visitOpFieldProjection(@NotNull SchemaOpFieldProjection o) {
    visitPsiElement(o);
  }

  public void visitOpFieldProjectionEntry(@NotNull SchemaOpFieldProjectionEntry o) {
    visitPsiElement(o);
  }

  public void visitOpKeyProjection(@NotNull SchemaOpKeyProjection o) {
    visitPsiElement(o);
  }

  public void visitOpKeySpec(@NotNull SchemaOpKeySpec o) {
    visitPsiElement(o);
  }

  public void visitOpKeySpecPart(@NotNull SchemaOpKeySpecPart o) {
    visitPsiElement(o);
  }

  public void visitOpListModelProjection(@NotNull SchemaOpListModelProjection o) {
    visitPsiElement(o);
  }

  public void visitOpMapModelPath(@NotNull SchemaOpMapModelPath o) {
    visitPsiElement(o);
  }

  public void visitOpMapModelProjection(@NotNull SchemaOpMapModelProjection o) {
    visitPsiElement(o);
  }

  public void visitOpModelMeta(@NotNull SchemaOpModelMeta o) {
    visitPsiElement(o);
  }

  public void visitOpModelMultiTail(@NotNull SchemaOpModelMultiTail o) {
    visitPsiElement(o);
  }

  public void visitOpModelPath(@NotNull SchemaOpModelPath o) {
    visitPsiElement(o);
  }

  public void visitOpModelPathProperty(@NotNull SchemaOpModelPathProperty o) {
    visitPsiElement(o);
  }

  public void visitOpModelPolymorphicTail(@NotNull SchemaOpModelPolymorphicTail o) {
    visitPsiElement(o);
  }

  public void visitOpModelProjection(@NotNull SchemaOpModelProjection o) {
    visitPsiElement(o);
  }

  public void visitOpModelProjectionRef(@NotNull SchemaOpModelProjectionRef o) {
    visitPsiElement(o);
  }

  public void visitOpModelProperty(@NotNull SchemaOpModelProperty o) {
    visitPsiElement(o);
  }

  public void visitOpModelTailItem(@NotNull SchemaOpModelTailItem o) {
    visitPsiElement(o);
  }

  public void visitOpMultiTagProjection(@NotNull SchemaOpMultiTagProjection o) {
    visitPsiElement(o);
  }

  public void visitOpMultiTagProjectionItem(@NotNull SchemaOpMultiTagProjectionItem o) {
    visitPsiElement(o);
  }

  public void visitOpNamedEntityProjection(@NotNull SchemaOpNamedEntityProjection o) {
    visitPsiElement(o);
  }

  public void visitOpNamedModelProjection(@NotNull SchemaOpNamedModelProjection o) {
    visitPsiElement(o);
  }

  public void visitOpParam(@NotNull SchemaOpParam o) {
    visitPsiElement(o);
  }

  public void visitOpPathKeyProjection(@NotNull SchemaOpPathKeyProjection o) {
    visitPsiElement(o);
  }

  public void visitOpPathKeyProjectionBody(@NotNull SchemaOpPathKeyProjectionBody o) {
    visitPsiElement(o);
  }

  public void visitOpPathKeyProjectionPart(@NotNull SchemaOpPathKeyProjectionPart o) {
    visitPsiElement(o);
  }

  public void visitOpRecordModelPath(@NotNull SchemaOpRecordModelPath o) {
    visitPsiElement(o);
  }

  public void visitOpRecordModelProjection(@NotNull SchemaOpRecordModelProjection o) {
    visitPsiElement(o);
  }

  public void visitOpSingleTagProjection(@NotNull SchemaOpSingleTagProjection o) {
    visitPsiElement(o);
  }

  public void visitOpUnnamedEntityProjection(@NotNull SchemaOpUnnamedEntityProjection o) {
    visitPsiElement(o);
  }

  public void visitOpUnnamedModelProjection(@NotNull SchemaOpUnnamedModelProjection o) {
    visitPsiElement(o);
  }

  public void visitOpUnnamedOrRefEntityProjection(@NotNull SchemaOpUnnamedOrRefEntityProjection o) {
    visitPsiElement(o);
  }

  public void visitOpUnnamedOrRefModelProjection(@NotNull SchemaOpUnnamedOrRefModelProjection o) {
    visitPsiElement(o);
  }

  public void visitOpVarPath(@NotNull SchemaOpVarPath o) {
    visitPsiElement(o);
  }

  public void visitOperationDef(@NotNull SchemaOperationDef o) {
    visitPsiElement(o);
  }

  public void visitOperationInputType(@NotNull SchemaOperationInputType o) {
    visitPsiElement(o);
  }

  public void visitOperationMethod(@NotNull SchemaOperationMethod o) {
    visitPsiElement(o);
  }

  public void visitOperationName(@NotNull SchemaOperationName o) {
    visitPsiElement(o);
  }

  public void visitOperationOutputType(@NotNull SchemaOperationOutputType o) {
    visitPsiElement(o);
  }

  public void visitOperationPath(@NotNull SchemaOperationPath o) {
    visitPsiElement(o);
  }

  public void visitOutputProjection(@NotNull SchemaOutputProjection o) {
    visitPsiElement(o);
  }

  public void visitOutputProjectionDef(@NotNull SchemaOutputProjectionDef o) {
    visitPsiElement(o);
  }

  public void visitPrimitiveDatum(@NotNull SchemaPrimitiveDatum o) {
    visitDatum(o);
  }

  public void visitPrimitiveTypeBody(@NotNull SchemaPrimitiveTypeBody o) {
    visitAnnotationsHolder(o);
  }

  public void visitPrimitiveTypeDef(@NotNull SchemaPrimitiveTypeDef o) {
    visitTypeDef(o);
  }

  public void visitProjectionDef(@NotNull SchemaProjectionDef o) {
    visitPsiElement(o);
  }

  public void visitQid(@NotNull SchemaQid o) {
    visitPsiElement(o);
  }

  public void visitQn(@NotNull SchemaQn o) {
    visitPsiElement(o);
  }

  public void visitQnSegment(@NotNull SchemaQnSegment o) {
    visitPsiNameIdentifierOwner(o);
  }

  public void visitQnTypeRef(@NotNull SchemaQnTypeRef o) {
    visitTypeRef(o);
  }

  public void visitReadOperationBodyPart(@NotNull SchemaReadOperationBodyPart o) {
    visitPsiElement(o);
  }

  public void visitReadOperationDef(@NotNull SchemaReadOperationDef o) {
    visitPsiElement(o);
  }

  public void visitRecordDatum(@NotNull SchemaRecordDatum o) {
    visitDatum(o);
  }

  public void visitRecordDatumEntry(@NotNull SchemaRecordDatumEntry o) {
    visitPsiElement(o);
  }

  public void visitRecordTypeBody(@NotNull SchemaRecordTypeBody o) {
    visitAnnotationsHolder(o);
  }

  public void visitRecordTypeDef(@NotNull SchemaRecordTypeDef o) {
    visitTypeDef(o);
  }

  public void visitResourceDef(@NotNull SchemaResourceDef o) {
    visitPsiElement(o);
  }

  public void visitResourceName(@NotNull SchemaResourceName o) {
    visitPsiElement(o);
  }

  public void visitResourceType(@NotNull SchemaResourceType o) {
    visitPsiElement(o);
  }

  public void visitRetroDecl(@NotNull SchemaRetroDecl o) {
    visitPsiElement(o);
  }

  public void visitSupplementDef(@NotNull SchemaSupplementDef o) {
    visitPsiElement(o);
  }

  public void visitSupplementsDecl(@NotNull SchemaSupplementsDecl o) {
    visitPsiElement(o);
  }

  public void visitTagName(@NotNull SchemaTagName o) {
    visitPsiElement(o);
  }

  public void visitTransformerBodyPart(@NotNull SchemaTransformerBodyPart o) {
    visitPsiElement(o);
  }

  public void visitTransformerDef(@NotNull SchemaTransformerDef o) {
    visitPsiElement(o);
  }

  public void visitTransformerName(@NotNull SchemaTransformerName o) {
    visitPsiElement(o);
  }

  public void visitTransformerType(@NotNull SchemaTransformerType o) {
    visitPsiElement(o);
  }

  public void visitTypeDefWrapper(@NotNull SchemaTypeDefWrapper o) {
    visitPsiElement(o);
  }

  public void visitTypeRef(@NotNull SchemaTypeRef o) {
    visitPsiElement(o);
  }

  public void visitUpdateOperationBodyPart(@NotNull SchemaUpdateOperationBodyPart o) {
    visitPsiElement(o);
  }

  public void visitUpdateOperationDef(@NotNull SchemaUpdateOperationDef o) {
    visitPsiElement(o);
  }

  public void visitValueTypeRef(@NotNull SchemaValueTypeRef o) {
    visitPsiElement(o);
  }

  public void visitAnnotationsHolder(@NotNull AnnotationsHolder o) {
    visitElement(o);
  }

  public void visitPsiNameIdentifierOwner(@NotNull PsiNameIdentifierOwner o) {
    visitElement(o);
  }

  public void visitTypeDef(@NotNull SchemaTypeDef o) {
    visitPsiElement(o);
  }

  public void visitPsiElement(@NotNull PsiElement o) {
    visitElement(o);
  }

}
