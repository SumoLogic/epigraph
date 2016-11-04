// This is a generated file. Not intended for manual editing.
package ws.epigraph.schema.parser.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiNameIdentifierOwner;

public class SchemaVisitor extends PsiElementVisitor {

  public void visitAnnotation(@NotNull SchemaAnnotation o) {
    visitPsiNamedElement(o);
  }

  public void visitAnonList(@NotNull SchemaAnonList o) {
    visitTypeRef(o);
  }

  public void visitAnonMap(@NotNull SchemaAnonMap o) {
    visitTypeRef(o);
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

  public void visitDefaultOverride(@NotNull SchemaDefaultOverride o) {
    visitPsiElement(o);
  }

  public void visitDefs(@NotNull SchemaDefs o) {
    visitPsiElement(o);
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

  public void visitPrimitiveDatum(@NotNull SchemaPrimitiveDatum o) {
    visitDatum(o);
  }

  public void visitPrimitiveTypeBody(@NotNull SchemaPrimitiveTypeBody o) {
    visitAnnotationsHolder(o);
  }

  public void visitPrimitiveTypeDef(@NotNull SchemaPrimitiveTypeDef o) {
    visitTypeDef(o);
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

  public void visitValueTypeRef(@NotNull SchemaValueTypeRef o) {
    visitPsiElement(o);
  }

  public void visitVarTagDecl(@NotNull SchemaVarTagDecl o) {
    visitAnnotationsHolder(o);
    // visitPsiNamedElement(o);
  }

  public void visitVarTagRef(@NotNull SchemaVarTagRef o) {
    visitPsiNameIdentifierOwner(o);
  }

  public void visitVarTypeBody(@NotNull SchemaVarTypeBody o) {
    visitAnnotationsHolder(o);
  }

  public void visitVarTypeDef(@NotNull SchemaVarTypeDef o) {
    visitTypeDef(o);
  }

  public void visitAnnotationsHolder(@NotNull AnnotationsHolder o) {
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
