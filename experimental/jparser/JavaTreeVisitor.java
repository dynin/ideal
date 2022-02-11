/*
 * Copyright 2014-2022 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://theideal.org/license/
 */

package ideal.development.jparser;

import java.util.List;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.development.elements.*;
import ideal.development.names.*;
import ideal.development.origins.*;
import ideal.development.kinds.*;
import ideal.development.modifiers.*;
import ideal.development.constructs.*;

import ideal.development.jparser.JavaParser.*;

public class JavaTreeVisitor extends JavaParserBaseVisitor<Object> {
  private final JavaParser java_parser;
  private final origin default_origin;

  public JavaTreeVisitor(JavaParser java_parser) {
    this.java_parser = java_parser;
    this.default_origin = new special_origin(new base_string("[jparser]"));
  }

  protected origin get_origin(ParseTree tree) {
    return default_origin;
  }

  protected readonly_list<construct> to_constructs(List<? extends ParseTree> elements) {
    list<construct> result = new base_list<construct>();
    for (ParseTree element : elements) {
      result.append((construct) visit(element));
    }
    return result;
  }

  protected readonly_list<annotation_construct> to_annotations(List<? extends ParseTree> elements) {
    list<annotation_construct> result = new base_list<annotation_construct>();
    for (ParseTree element : elements) {
      result.append((annotation_construct) visit(element));
    }
    return result;
  }

  @Override
  public readonly_list<construct> visitCompilationUnit(CompilationUnitContext ctx) {
    return to_constructs(ctx.typeDeclaration());
  }

  @Override
  public Object visitPackageDeclaration(PackageDeclarationContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitImportDeclaration(ImportDeclarationContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public type_declaration_construct visitTypeDeclaration(TypeDeclarationContext ctx) {
    readonly_list<annotation_construct> annotations =
        to_annotations(ctx.classOrInterfaceModifier());
    type_declaration_construct the_declaration = visitClassDeclaration(ctx.classDeclaration());
    return new type_declaration_construct(
        annotations,
        the_declaration.kind,
        the_declaration.name,
        the_declaration.parameters,
        the_declaration.body,
        get_origin(ctx)
    );
  }

  @Override
  public Object visitModifier(ModifierContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public annotation_construct visitClassOrInterfaceModifier(ClassOrInterfaceModifierContext ctx) {
    origin the_origin = get_origin(ctx);
    switch (((TerminalNode) ctx.getChild(0)).getSymbol().getType()) {
      case JavaParser.PUBLIC:
        return new modifier_construct(access_modifier.public_modifier, the_origin);
      case JavaParser.PROTECTED:
        return new modifier_construct(access_modifier.protected_modifier, the_origin);
      case JavaParser.PRIVATE:
        return new modifier_construct(access_modifier.private_modifier, the_origin);
      case JavaParser.STATIC:
        return new modifier_construct(general_modifier.static_modifier, the_origin);
      case JavaParser.ABSTRACT:
        return new modifier_construct(general_modifier.abstract_modifier, the_origin);
      case JavaParser.FINAL:
        return new modifier_construct(general_modifier.final_modifier, the_origin);
    }
    unsupported(ctx);
    return null;
  }

  @Override
  public Object visitVariableModifier(VariableModifierContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public type_declaration_construct visitClassDeclaration(ClassDeclarationContext ctx) {
    return new type_declaration_construct(
        new empty<annotation_construct>(),
        type_kinds.class_kind,
        visitIdentifier(ctx.identifier()).the_name,
        null,
        new base_list<construct>(),
        get_origin(ctx)
    );
  }

  @Override
  public Object visitTypeParameters(TypeParametersContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitTypeParameter(TypeParameterContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitTypeBound(TypeBoundContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitEnumDeclaration(EnumDeclarationContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitEnumConstants(EnumConstantsContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitEnumConstant(EnumConstantContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitEnumBodyDeclarations(EnumBodyDeclarationsContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitInterfaceDeclaration(InterfaceDeclarationContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitClassBody(ClassBodyContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitInterfaceBody(InterfaceBodyContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitClassBodyDeclaration(ClassBodyDeclarationContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitMemberDeclaration(MemberDeclarationContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitMethodDeclaration(MethodDeclarationContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitMethodBody(MethodBodyContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitTypeTypeOrVoid(TypeTypeOrVoidContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitGenericMethodDeclaration(GenericMethodDeclarationContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitGenericConstructorDeclaration(GenericConstructorDeclarationContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitConstructorDeclaration(ConstructorDeclarationContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitFieldDeclaration(FieldDeclarationContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitInterfaceBodyDeclaration(InterfaceBodyDeclarationContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitInterfaceMemberDeclaration(InterfaceMemberDeclarationContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitConstDeclaration(ConstDeclarationContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitConstantDeclarator(ConstantDeclaratorContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitInterfaceMethodDeclaration(InterfaceMethodDeclarationContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitInterfaceMethodModifier(InterfaceMethodModifierContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitGenericInterfaceMethodDeclaration(GenericInterfaceMethodDeclarationContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitInterfaceCommonBodyDeclaration(InterfaceCommonBodyDeclarationContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitVariableDeclarators(VariableDeclaratorsContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitVariableDeclarator(VariableDeclaratorContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitVariableDeclaratorId(VariableDeclaratorIdContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitVariableInitializer(VariableInitializerContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitArrayInitializer(ArrayInitializerContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitClassOrInterfaceType(ClassOrInterfaceTypeContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitTypeArgument(TypeArgumentContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitQualifiedNameList(QualifiedNameListContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitFormalParameters(FormalParametersContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitReceiverParameter(ReceiverParameterContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitFormalParameterList(FormalParameterListContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitFormalParameter(FormalParameterContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitLastFormalParameter(LastFormalParameterContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitLambdaLVTIList(LambdaLVTIListContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitLambdaLVTIParameter(LambdaLVTIParameterContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitQualifiedName(QualifiedNameContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitLiteral(LiteralContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitIntegerLiteral(IntegerLiteralContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitFloatLiteral(FloatLiteralContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitAltAnnotationQualifiedName(AltAnnotationQualifiedNameContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitAnnotation(AnnotationContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitElementValuePairs(ElementValuePairsContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitElementValuePair(ElementValuePairContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitElementValue(ElementValueContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitElementValueArrayInitializer(ElementValueArrayInitializerContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitAnnotationTypeDeclaration(AnnotationTypeDeclarationContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitAnnotationTypeBody(AnnotationTypeBodyContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitAnnotationTypeElementDeclaration(AnnotationTypeElementDeclarationContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitAnnotationTypeElementRest(AnnotationTypeElementRestContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitAnnotationMethodOrConstantRest(AnnotationMethodOrConstantRestContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitAnnotationMethodRest(AnnotationMethodRestContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitAnnotationConstantRest(AnnotationConstantRestContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitDefaultValue(DefaultValueContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitModuleDeclaration(ModuleDeclarationContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitModuleBody(ModuleBodyContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitModuleDirective(ModuleDirectiveContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitRequiresModifier(RequiresModifierContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitRecordDeclaration(RecordDeclarationContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitRecordHeader(RecordHeaderContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitRecordComponentList(RecordComponentListContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitRecordComponent(RecordComponentContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitRecordBody(RecordBodyContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitBlock(BlockContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitBlockStatement(BlockStatementContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitLocalVariableDeclaration(LocalVariableDeclarationContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public name_construct visitIdentifier(IdentifierContext ctx) {
    simple_name name = simple_name.make(ctx.IDENTIFIER().getText());
    return new name_construct(name, get_origin(ctx));
  }

  @Override
  public Object visitLocalTypeDeclaration(LocalTypeDeclarationContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitStatement(StatementContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitCatchClause(CatchClauseContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitCatchType(CatchTypeContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitFinallyBlock(FinallyBlockContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitResourceSpecification(ResourceSpecificationContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitResources(ResourcesContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitResource(ResourceContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitSwitchBlockStatementGroup(SwitchBlockStatementGroupContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitSwitchLabel(SwitchLabelContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitForControl(ForControlContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitForInit(ForInitContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitEnhancedForControl(EnhancedForControlContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitParExpression(ParExpressionContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitExpressionList(ExpressionListContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitMethodCall(MethodCallContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitExpression(ExpressionContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitPattern(PatternContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitLambdaExpression(LambdaExpressionContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitLambdaParameters(LambdaParametersContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitLambdaBody(LambdaBodyContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitPrimary(PrimaryContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitSwitchExpression(SwitchExpressionContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitSwitchLabeledRule(SwitchLabeledRuleContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitGuardedPattern(GuardedPatternContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitSwitchRuleOutcome(SwitchRuleOutcomeContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitClassType(ClassTypeContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitCreator(CreatorContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitCreatedName(CreatedNameContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitInnerCreator(InnerCreatorContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitArrayCreatorRest(ArrayCreatorRestContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitClassCreatorRest(ClassCreatorRestContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitExplicitGenericInvocation(ExplicitGenericInvocationContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitTypeArgumentsOrDiamond(TypeArgumentsOrDiamondContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitNonWildcardTypeArgumentsOrDiamond(NonWildcardTypeArgumentsOrDiamondContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitNonWildcardTypeArguments(NonWildcardTypeArgumentsContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitTypeList(TypeListContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitTypeType(TypeTypeContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitPrimitiveType(PrimitiveTypeContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitTypeArguments(TypeArgumentsContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitSuperSuffix(SuperSuffixContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitExplicitGenericInvocationSuffix(ExplicitGenericInvocationSuffixContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitArguments(ArgumentsContext ctx) {
    return unsupported(ctx);
  }

  public Object unsupported(ParseTree parseTree) {
    utilities.panic("Unsupported " + parseTree.toStringTree(java_parser));
    return null;
  }
}
