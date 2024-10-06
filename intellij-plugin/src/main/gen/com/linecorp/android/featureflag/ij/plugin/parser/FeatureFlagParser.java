// This is a generated file. Not intended for manual editing.
package com.linecorp.android.featureflag.ij.plugin.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static com.linecorp.android.featureflag.ij.plugin.psi.FeatureFlagTypes.*;
import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;
import com.intellij.lang.LightPsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class FeatureFlagParser implements PsiParser, LightPsiParser {

  public ASTNode parse(IElementType t, PsiBuilder b) {
    parseLight(t, b);
    return b.getTreeBuilt();
  }

  public void parseLight(IElementType t, PsiBuilder b) {
    boolean r;
    b = adapt_builder_(t, b, this, null);
    Marker m = enter_section_(b, 0, _COLLAPSE_, null);
    r = parse_root_(t, b);
    exit_section_(b, 0, m, t, r, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(IElementType t, PsiBuilder b) {
    return parse_root_(t, b, 0);
  }

  static boolean parse_root_(IElementType t, PsiBuilder b, int l) {
    return featureFlagFile(b, l + 1);
  }

  /* ********************************************************** */
  // VALUE_VERSION | VALUE_USERNAME | valueReference | VALUE_STRING
  public static boolean entry(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "entry")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ENTRY, "<entry>");
    r = consumeToken(b, VALUE_VERSION);
    if (!r) r = consumeToken(b, VALUE_USERNAME);
    if (!r) r = valueReference(b, l + 1);
    if (!r) r = consumeToken(b, VALUE_STRING);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // item_*
  static boolean featureFlagFile(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "featureFlagFile")) return false;
    while (true) {
      int c = current_position_(b);
      if (!item_(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "featureFlagFile", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // property|COMMENT|CRLF
  static boolean item_(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "item_")) return false;
    boolean r;
    r = property(b, l + 1);
    if (!r) r = consumeToken(b, COMMENT);
    if (!r) r = consumeToken(b, CRLF);
    return r;
  }

  /* ********************************************************** */
  // MODIFIER_OVERRIDABLE | MODIFIER_PRIVATE | MODIFIER_LITERALIZE
  public static boolean modifier(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "modifier")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, MODIFIER, "<modifier>");
    r = consumeToken(b, MODIFIER_OVERRIDABLE);
    if (!r) r = consumeToken(b, MODIFIER_PRIVATE);
    if (!r) r = consumeToken(b, MODIFIER_LITERALIZE);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // [modifier] KEY SEPARATOR value COMMENT?
  public static boolean property(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "property")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PROPERTY, "<property>");
    r = property_0(b, l + 1);
    r = r && consumeTokens(b, 0, KEY, SEPARATOR);
    r = r && value(b, l + 1);
    r = r && property_4(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // [modifier]
  private static boolean property_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "property_0")) return false;
    modifier(b, l + 1);
    return true;
  }

  // COMMENT?
  private static boolean property_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "property_4")) return false;
    consumeToken(b, COMMENT);
    return true;
  }

  /* ********************************************************** */
  // (entry (COMMA | AND))* entry
  public static boolean value(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "value")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, VALUE, "<value>");
    r = value_0(b, l + 1);
    r = r && entry(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (entry (COMMA | AND))*
  private static boolean value_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "value_0")) return false;
    while (true) {
      int c = current_position_(b);
      if (!value_0_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "value_0", c)) break;
    }
    return true;
  }

  // entry (COMMA | AND)
  private static boolean value_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "value_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = entry(b, l + 1);
    r = r && value_0_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // COMMA | AND
  private static boolean value_0_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "value_0_0_1")) return false;
    boolean r;
    r = consumeToken(b, COMMA);
    if (!r) r = consumeToken(b, AND);
    return r;
  }

  /* ********************************************************** */
  // VALUE_REFERENCE_PACKAGE COLON VALUE_REFERENCE_NAME
  public static boolean valueReference(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "valueReference")) return false;
    if (!nextTokenIs(b, VALUE_REFERENCE_PACKAGE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, VALUE_REFERENCE_PACKAGE, COLON, VALUE_REFERENCE_NAME);
    exit_section_(b, m, VALUE_REFERENCE, r);
    return r;
  }

}
