// This is a generated file. Not intended for manual editing.
package com.linecorp.android.featureflag.ij.plugin.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.linecorp.android.featureflag.ij.plugin.psi.FeatureFlagTypes.*;
import com.linecorp.android.featureflag.ij.plugin.psi.*;
import com.intellij.navigation.ItemPresentation;

public class FeatureFlagPropertyImpl extends FeatureFlagNamedElementImpl implements FeatureFlagProperty {

  public FeatureFlagPropertyImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull FeatureFlagVisitor visitor) {
    visitor.visitProperty(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof FeatureFlagVisitor) accept((FeatureFlagVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public FeatureFlagModifier getModifier() {
    return findChildByClass(FeatureFlagModifier.class);
  }

  @Override
  @NotNull
  public FeatureFlagValue getValue() {
    return findNotNullChildByClass(FeatureFlagValue.class);
  }

  @Override
  @Nullable
  public String getKey() {
    return FeatureFlagPsiImplUtil.getKey(this);
  }

  @Override
  @Nullable
  public String getName() {
    return FeatureFlagPsiImplUtil.getName(this);
  }

  @Override
  @NotNull
  public PsiElement setName(@NotNull String newName) {
    return FeatureFlagPsiImplUtil.setName(this, newName);
  }

  @Override
  @Nullable
  public PsiElement getNameIdentifier() {
    return FeatureFlagPsiImplUtil.getNameIdentifier(this);
  }

  @Override
  @NotNull
  public ItemPresentation getPresentation() {
    return FeatureFlagPsiImplUtil.getPresentation(this);
  }

}
