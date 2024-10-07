// This is a generated file. Not intended for manual editing.
package com.linecorp.android.featureflag.ij.plugin.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.navigation.ItemPresentation;

public interface FeatureFlagProperty extends FeatureFlagNamedElement {

  @Nullable
  FeatureFlagModifier getModifier();

  @NotNull
  FeatureFlagValue getValue();

  @Nullable
  String getKey();

  @Nullable
  String getName();

  @NotNull
  PsiElement setName(@NotNull String newName);

  @Nullable
  PsiElement getNameIdentifier();

  @NotNull
  ItemPresentation getPresentation();

}
