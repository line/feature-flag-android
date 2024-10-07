// This is a generated file. Not intended for manual editing.
package com.linecorp.android.featureflag.ij.plugin.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiElement;

public class FeatureFlagVisitor extends PsiElementVisitor {

  public void visitEntry(@NotNull FeatureFlagEntry o) {
    visitPsiElement(o);
  }

  public void visitModifier(@NotNull FeatureFlagModifier o) {
    visitPsiElement(o);
  }

  public void visitProperty(@NotNull FeatureFlagProperty o) {
    visitNamedElement(o);
  }

  public void visitValue(@NotNull FeatureFlagValue o) {
    visitPsiElement(o);
  }

  public void visitValueReference(@NotNull FeatureFlagValueReference o) {
    visitPsiElement(o);
  }

  public void visitNamedElement(@NotNull FeatureFlagNamedElement o) {
    visitPsiElement(o);
  }

  public void visitPsiElement(@NotNull PsiElement o) {
    visitElement(o);
  }

}
