// This is a generated file. Not intended for manual editing.
package com.linecorp.android.featureflag.ij.plugin.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import com.linecorp.android.featureflag.ij.plugin.psi.impl.*;

public interface FeatureFlagTypes {

  IElementType ENTRY = new FeatureFlagElementType("ENTRY");
  IElementType MODIFIER = new FeatureFlagElementType("MODIFIER");
  IElementType PROPERTY = new FeatureFlagElementType("PROPERTY");
  IElementType VALUE = new FeatureFlagElementType("VALUE");
  IElementType VALUE_REFERENCE = new FeatureFlagElementType("VALUE_REFERENCE");

  IElementType AND = new FeatureFlagTokenType("AND");
  IElementType COLON = new FeatureFlagTokenType("COLON");
  IElementType COMMA = new FeatureFlagTokenType("COMMA");
  IElementType COMMENT = new FeatureFlagTokenType("COMMENT");
  IElementType CRLF = new FeatureFlagTokenType("CRLF");
  IElementType KEY = new FeatureFlagTokenType("KEY");
  IElementType MODIFIER_LITERALIZE = new FeatureFlagTokenType("MODIFIER_LITERALIZE");
  IElementType MODIFIER_OVERRIDABLE = new FeatureFlagTokenType("MODIFIER_OVERRIDABLE");
  IElementType MODIFIER_PRIVATE = new FeatureFlagTokenType("MODIFIER_PRIVATE");
  IElementType SEPARATOR = new FeatureFlagTokenType("SEPARATOR");
  IElementType VALUE_REFERENCE_NAME = new FeatureFlagTokenType("VALUE_REFERENCE_NAME");
  IElementType VALUE_REFERENCE_PACKAGE = new FeatureFlagTokenType("VALUE_REFERENCE_PACKAGE");
  IElementType VALUE_STRING = new FeatureFlagTokenType("VALUE_STRING");
  IElementType VALUE_USERNAME = new FeatureFlagTokenType("VALUE_USERNAME");
  IElementType VALUE_VERSION = new FeatureFlagTokenType("VALUE_VERSION");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == ENTRY) {
        return new FeatureFlagEntryImpl(node);
      }
      else if (type == MODIFIER) {
        return new FeatureFlagModifierImpl(node);
      }
      else if (type == PROPERTY) {
        return new FeatureFlagPropertyImpl(node);
      }
      else if (type == VALUE) {
        return new FeatureFlagValueImpl(node);
      }
      else if (type == VALUE_REFERENCE) {
        return new FeatureFlagValueReferenceImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
