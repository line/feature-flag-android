// This is a generated file. Not intended for manual editing.
package com.linecorp.android.featureflag.ij.plugin.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.linecorp.android.featureflag.ij.plugin.psi.FeatureFlagTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.linecorp.android.featureflag.ij.plugin.psi.*;

public class FeatureFlagModifierImpl extends ASTWrapperPsiElement implements FeatureFlagModifier {

  public FeatureFlagModifierImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull FeatureFlagVisitor visitor) {
    visitor.visitModifier(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof FeatureFlagVisitor) accept((FeatureFlagVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public FeatureFlagModifierType getModifierType() {
    return FeatureFlagPsiImplUtil.getModifierType(this);
  }

}
