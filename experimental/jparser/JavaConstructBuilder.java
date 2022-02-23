/*
 * Copyright 2014-2022 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://theideal.org/license/
 */

package ideal.development.jparser;

import java.util.List;
import javax.annotation.Nullable;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.Tree;

import ideal.library.elements.*;
import ideal.library.characters.*;
import ideal.runtime.elements.*;
import ideal.development.elements.*;
import ideal.development.names.*;
import ideal.development.origins.*;
import ideal.development.kinds.*;
import ideal.development.modifiers.*;
import ideal.development.literals.*;
import ideal.development.constructs.*;

import ideal.development.jparser.JavaParser.*;

public class JavaConstructBuilder extends JavaParserBaseVisitor<Object> {
  private final boolean generate_ideal;
  private final JavaParser java_parser;
  private final origin default_origin;
  private final dictionary<Integer, modifier_kind> modifiers =
      new hash_dictionary<Integer, modifier_kind>();
  private final dictionary<string, modifier_kind> annotations =
      new hash_dictionary<string, modifier_kind>();
  private final dictionary<Integer, operator> operators =
      new hash_dictionary<Integer, operator>();

  public JavaConstructBuilder(boolean generate_ideal, JavaParser java_parser) {
    this.generate_ideal = generate_ideal;
    this.java_parser = java_parser;
    this.default_origin = new special_origin(new base_string("[jparser]"));

    modifiers.put(JavaParser.PUBLIC, access_modifier.public_modifier);
    modifiers.put(JavaParser.PROTECTED, access_modifier.protected_modifier);
    modifiers.put(JavaParser.PRIVATE, access_modifier.private_modifier);
    modifiers.put(JavaParser.STATIC, general_modifier.static_modifier);
    modifiers.put(JavaParser.ABSTRACT, general_modifier.abstract_modifier);
    modifiers.put(JavaParser.FINAL, general_modifier.final_modifier);
    // Add support for: NATIVE | SYNCHRONIZED | TRANSIENT | VOLATILE (modifier)
    // Add support for: STRICTFP | SEALED | NON_SEALED (classOrInterfaceModifier)

    annotations.put(new base_string("Override"), general_modifier.override_modifier);
    annotations.put(new base_string("Nullable"), general_modifier.nullable_modifier);
    annotations.put(new base_string("dont_display"), general_modifier.dont_display_modifier);

    operators.put(JavaParser.ASSIGN, operator.ASSIGN);
    operators.put(JavaParser.AND, operator.LOGICAL_AND);
    operators.put(JavaParser.EQUAL, operator.EQUAL_TO);
    operators.put(JavaParser.ADD, operator.ADD);
    operators.put(JavaParser.MUL, operator.MULTIPLY);
  }

  protected origin get_origin(ParseTree tree) {
    return default_origin;
  }

  protected int get_symbol_type(Tree tree) {
    return ((TerminalNode) tree).getSymbol().getType();
  }

  protected readonly_list<construct> to_constructs(List<? extends ParseTree> elements) {
    list<construct> result = new base_list<construct>();
    for (ParseTree element : elements) {
      Object object_element = visit(element);
      if (object_element instanceof construct) {
        result.append((construct) object_element);
      } else {
        result.append_all((readonly_list<construct>) object_element);
      }
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

  protected construct make_array(construct the_construct, int depth, origin the_origin) {
    while (depth > 0) {
      if (generate_ideal) {
        the_construct = new parameter_construct(
            new name_construct(simple_name.make("array"), the_origin),
            new base_list<construct>(the_construct), grouping_type.BRACKETS, the_origin);
      } else {
        the_construct = new parameter_construct(the_construct, new empty<construct>(),
            grouping_type.BRACKETS, the_origin);
      }
      depth -= 1;
    }
    return the_construct;
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

  protected type_declaration_construct make_type_declaration(
      readonly_list<annotation_construct> annotations,
      type_declaration_construct the_declaration,
      origin the_origin) {
    assert the_declaration.annotations.is_empty();
    return new type_declaration_construct(
        annotations,
        the_declaration.kind,
        the_declaration.name,
        the_declaration.parameters,
        the_declaration.body,
        the_origin
    );
  }

  protected procedure_construct make_procedure(readonly_list<annotation_construct> annotations,
      procedure_construct the_declaration,
      origin the_origin) {
    assert the_declaration.annotations.is_empty();
    return new procedure_construct(
        annotations,
        the_declaration.ret,
        the_declaration.name,
        the_declaration.parameters,
        the_declaration.post_annotations,
        the_declaration.body,
        the_origin
    );
  }

  protected variable_construct make_variable(readonly_list<annotation_construct> annotations,
      variable_construct the_declaration,
      origin the_origin) {
    assert the_declaration.annotations.is_empty();
    return new variable_construct(
        annotations,
        the_declaration.variable_type,
        the_declaration.name,
        the_declaration.post_annotations,
        the_declaration.init,
        the_origin
    );
  }

  @Override
  public type_declaration_construct visitTypeDeclaration(TypeDeclarationContext ctx) {
    readonly_list<annotation_construct> annotations =
        to_annotations(ctx.classOrInterfaceModifier());
    type_declaration_construct the_declaration = visitClassDeclaration(ctx.classDeclaration());
    return make_type_declaration(annotations, the_declaration, get_origin(ctx));
  }

  @Override
  public annotation_construct visitModifier(ModifierContext ctx) {
    // TODO: handle NATIVE | SYNCHRONIZED | TRANSIENT | VOLATILE
    assert ctx.classOrInterfaceModifier() != null;
    return visitClassOrInterfaceModifier(ctx.classOrInterfaceModifier());
  }

  @Override
  public annotation_construct visitClassOrInterfaceModifier(ClassOrInterfaceModifierContext ctx) {
    if (ctx.annotation() != null) {
      return visitAnnotation(ctx.annotation());
    }
    int modifier_symbol_type = get_symbol_type(ctx.getChild(0));
    @Nullable modifier_kind modifier = modifiers.get(modifier_symbol_type);
    if (modifier != null) {
      return new modifier_construct(modifier, get_origin(ctx));
    }
    return (annotation_construct) unsupported(ctx);
  }

  @Override
  public Object visitVariableModifier(VariableModifierContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public type_declaration_construct visitClassDeclaration(ClassDeclarationContext ctx) {
    list<construct> body = new base_list<construct>();
    if (ctx.typeParameters() != null) {
      return (type_declaration_construct) unsupported(ctx.typeParameters());
    }
    if (ctx.EXTENDS() != null) {
      body.append(new supertype_construct(
          new empty<annotation_construct>(),
          null,
          subtype_tags.extends_tag,
          new base_list<construct>(visitTypeType(ctx.typeType())),
          get_origin(ctx)));
    }
    if (ctx.IMPLEMENTS() != null) {
      body.append(new supertype_construct(
          new empty<annotation_construct>(),
          null,
          subtype_tags.implements_tag,
          visitTypeList(ctx.typeList(0)),
          get_origin(ctx)));
    }
    body.append_all(visitClassBody(ctx.classBody()));
    return new type_declaration_construct(
        new empty<annotation_construct>(),
        type_kinds.class_kind,
        visitIdentifier(ctx.identifier()).the_name,
        null,
        body,
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
  public readonly_list<construct> visitClassBody(ClassBodyContext ctx) {
    return to_constructs(ctx.classBodyDeclaration());
  }

  @Override
  public Object visitInterfaceBody(InterfaceBodyContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public Object visitClassBodyDeclaration(ClassBodyDeclarationContext ctx) {
    if (ctx.SEMI() != null) {
      return new empty_construct(get_origin(ctx));
    } else if (ctx.block() != null) {
      return visitBlock(ctx.block());
    }

    readonly_list<annotation_construct> annotations = to_annotations(ctx.modifier());
    Object member_declaration = visitMemberDeclaration(ctx.memberDeclaration());
    if (annotations.is_not_empty()) {
      if (member_declaration instanceof type_declaration_construct) {
        member_declaration = make_type_declaration(annotations,
            (type_declaration_construct) member_declaration, get_origin(ctx));
      } else if (member_declaration instanceof procedure_construct) {
        member_declaration = make_procedure(annotations,
            (procedure_construct) member_declaration, get_origin(ctx));
      } else if (member_declaration instanceof readonly_list) {
        readonly_list<construct> declarations = (readonly_list<construct>) member_declaration;
        list<construct> result = new base_list<construct>();
        for (int i = 0; i < declarations.size(); ++i) {
          result.append(make_variable(annotations,
              (variable_construct) declarations.get(i), get_origin(ctx)));
        }
        return result;
      } else {
        unsupported(ctx);
      }
    }
    return member_declaration;
  }

  @Override
  public Object visitMemberDeclaration(MemberDeclarationContext ctx) {
    return visit(ctx.getChild(0));
  }

  @Override
  public procedure_construct visitMethodDeclaration(MethodDeclarationContext ctx) {
    // TODO: handle thrown exception
    return new procedure_construct(
        new empty<annotation_construct>(),
        visitTypeTypeOrVoid(ctx.typeTypeOrVoid()),
        visitIdentifier(ctx.identifier()).the_name,
        visitFormalParameters(ctx.formalParameters()),
        new empty<annotation_construct>(),
        visitMethodBody(ctx.methodBody()),
        get_origin(ctx)
    );
  }

  @Override
  public @Nullable construct visitMethodBody(MethodBodyContext ctx) {
    if (ctx.block() != null) {
      return visitBlock(ctx.block());
    } else {
      return null;
    }
  }

  @Override
  public construct visitTypeTypeOrVoid(TypeTypeOrVoidContext ctx) {
    if (ctx.typeType() != null) {
      return visitTypeType(ctx.typeType());
    } else {
      assert ctx.VOID() != null;
      return new name_construct(common_names.void_name, get_origin(ctx));
    }
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
  public procedure_construct visitConstructorDeclaration(ConstructorDeclarationContext ctx) {
    // TODO: handle thrown exception
    return new procedure_construct(
        new empty<annotation_construct>(),
        null,
        visitIdentifier(ctx.identifier()).the_name,
        visitFormalParameters(ctx.formalParameters()),
        new empty<annotation_construct>(),
        visitBlock(ctx.block()),
        get_origin(ctx)
    );
  }

  @Override
  public readonly_list<variable_construct> visitFieldDeclaration(FieldDeclarationContext ctx) {
    construct type_type = visitTypeType(ctx.typeType());
    list<variable_construct> constructs = new base_list<variable_construct>();
    readonly_list<variable_construct> variables =
        visitVariableDeclarators(ctx.variableDeclarators());
    for (int i = 0; i < variables.size(); ++i) {
      variable_construct the_variable = variables.get(i);
      constructs.append(new variable_construct(
          the_variable.annotations,
          type_type,
          the_variable.name,
          the_variable.post_annotations,
          the_variable.init,
          get_origin(ctx)
      ));
    }
    return constructs;
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
  public readonly_list<variable_construct> visitVariableDeclarators(
      VariableDeclaratorsContext ctx) {
    list<variable_construct> result = new base_list<variable_construct>();
    for (ParseTree element : ctx.variableDeclarator()) {
      result.append((variable_construct) visit(element));
    }
    return result;
  }

  @Override
  public variable_construct visitVariableDeclarator(VariableDeclaratorContext ctx) {
    return new variable_construct(
        new empty<annotation_construct>(),
        null,
        visitVariableDeclaratorId(ctx.variableDeclaratorId()).the_name,
        new empty<annotation_construct>(),
        ctx.variableInitializer() != null ?
            visitVariableInitializer(ctx.variableInitializer()) : null,
        get_origin(ctx)
    );
  }

  @Override
  public name_construct visitVariableDeclaratorId(VariableDeclaratorIdContext ctx) {
    // Brackets after identifier is an obsolete artefact of grammar.  Prohibit it.
    assert ctx.getChildCount() == 1;
    return visitIdentifier(ctx.identifier());
  }

  @Override
  public construct visitVariableInitializer(VariableInitializerContext ctx) {
    return (construct) visit(ctx.getChild(0));
  }

  @Override
  public Object visitArrayInitializer(ArrayInitializerContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public construct visitClassOrInterfaceType(ClassOrInterfaceTypeContext ctx) {
    // TODO: handle typeArguments, name resolution
    if (ctx.DOT(0) != null) {
      unsupported(ctx);
    }
    construct result = visitIdentifier(ctx.identifier(0));
    if (ctx.typeArguments(0) != null) {
      grouping_type grouping = generate_ideal ?
          grouping_type.BRACKETS : grouping_type.ANGLE_BRACKETS;
      result = new parameter_construct(result, visitTypeArguments(ctx.typeArguments(0)),
          grouping, get_origin(ctx));
    }
    return result;
  }

  @Override
  public construct visitTypeArgument(TypeArgumentContext ctx) {
    // TODO: handle extends/super
    assert ctx.typeType() != null;
    return visitTypeType(ctx.typeType());
  }

  @Override
  public Object visitQualifiedNameList(QualifiedNameListContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public readonly_list<construct> visitFormalParameters(FormalParametersContext ctx) {
    if (ctx.formalParameterList() != null) {
      return visitFormalParameterList(ctx.formalParameterList());
    } else {
      return new empty<construct>();
    }
  }

  @Override
  public Object visitReceiverParameter(ReceiverParameterContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public readonly_list<construct> visitFormalParameterList(FormalParameterListContext ctx) {
    return to_constructs(ctx.formalParameter());
  }

  @Override
  public variable_construct visitFormalParameter(FormalParameterContext ctx) {
    return new variable_construct(
        // TODO: handle variableModifier
        new empty<annotation_construct>(),
        visitTypeType(ctx.typeType()),
        visitVariableDeclaratorId(ctx.variableDeclaratorId()).the_name,
        new empty<annotation_construct>(),
        null,
        get_origin(ctx)
    );
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
  public name_construct visitQualifiedName(QualifiedNameContext ctx) {
    // TODO: handle identifiers separated by dots
    assert ctx.identifier().size() == 1;
    return visitIdentifier(ctx.identifier(0));
  }

  protected string strip_quotes(string quoted_string, quote_type quote) {
    assert quoted_string.first() == quote.quote_character;
    assert quoted_string.last() == quote.quote_character;
    return quoted_string.slice(1, quoted_string.size() - 1);
  }

  @Override
  public literal_construct visitLiteral(LiteralContext ctx) {
    // TODO: handle non-string literals
    if (ctx.STRING_LITERAL() != null) {
      quote_type quote = punctuation.DOUBLE_QUOTE;
      string_literal the_string_literal = new string_literal(
          strip_quotes(new base_string(ctx.STRING_LITERAL().getText()), quote), quote);
      return new literal_construct(the_string_literal, get_origin(ctx));
    } else if (ctx.integerLiteral() != null) {
      return new literal_construct(visitIntegerLiteral(ctx.integerLiteral()), get_origin(ctx));
    }

    return (literal_construct) unsupported(ctx);
  }

  @Override
  public integer_literal visitIntegerLiteral(IntegerLiteralContext ctx) {
    // TODO: handle non-decimal integers
    assert ctx.DECIMAL_LITERAL() != null;
    String image = ctx.DECIMAL_LITERAL().getText();
    int value = Integer.parseInt(image);
    return new integer_literal(value, new base_string(image), radixes.DEFAULT_RADIX);
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
  public annotation_construct visitAnnotation(AnnotationContext ctx) {
    assert ctx.qualifiedName() != null;
    String annotation_name = visitQualifiedName(ctx.qualifiedName()).the_name.toString();
    @Nullable modifier_kind annotation = annotations.get(new base_string(annotation_name));
    if (annotation != null) {
      return new modifier_construct(annotation, get_origin(ctx));
    }
    return (annotation_construct) unsupported(ctx);
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
  public block_construct visitBlock(BlockContext ctx) {
    return new block_construct(to_constructs(ctx.blockStatement()), get_origin(ctx));
  }

  @Override
  public construct visitBlockStatement(BlockStatementContext ctx) {
    return (construct) visit(ctx.getChild(0));
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
  public construct visitStatement(StatementContext ctx) {
    // TODO: handle other statements
    if (ctx.statementExpression != null) {
      return visitExpression(ctx.statementExpression);
    }
    int statement_type = get_symbol_type(ctx.getChild(0));
    if (statement_type == JavaParser.RETURN) {
      @Nullable construct the_expression = null;
      if (ctx.expression(0) != null) {
        the_expression = visitExpression(ctx.expression(0));
      }
      return new return_construct(the_expression, get_origin(ctx));
    }
    return (construct) unsupported(ctx);
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
  public readonly_list<construct> visitExpressionList(ExpressionListContext ctx) {
    return to_constructs(ctx.expression());
  }

  @Override
  public parameter_construct visitMethodCall(MethodCallContext ctx) {
    name_construct name;
    if (ctx.identifier() != null) {
      name = visitIdentifier(ctx.identifier());
    } else if (ctx.THIS() != null) {
      name = new name_construct(special_name.THIS, get_origin(ctx));
    } else if (ctx.SUPER() != null) {
      name = new name_construct(special_name.SUPER, get_origin(ctx));
    } else {
      // Should never happen
      return (parameter_construct) unsupported(ctx);
    }
    readonly_list<construct> expressions;
    if (ctx.expressionList() != null) {
      expressions = visitExpressionList(ctx.expressionList());
    } else {
      expressions = new empty<construct>();
    }
    return new parameter_construct(name, expressions, grouping_type.PARENS, get_origin(ctx));
  }

  @Override
  public construct visitExpression(ExpressionContext ctx) {
    // TODO: handle other expressions
    if (ctx.primary() != null) {
      return visitPrimary(ctx.primary());
    }

    if (ctx.bop != null) {
      if (ctx.bop.getType() == JavaParser.DOT) {
        construct qualifier = visitExpression(ctx.expression(0));
        if (ctx.identifier() != null) {
          return new resolve_construct(qualifier, visitIdentifier(ctx.identifier()).the_name,
              get_origin(ctx));
        } else if (ctx.methodCall() != null) {
          parameter_construct method_construct = visitMethodCall(ctx.methodCall());
          name_construct name = (name_construct) method_construct.main;
          construct main = new resolve_construct(qualifier, name.the_name, get_origin(ctx));
          return new parameter_construct(main, method_construct.parameters,
              grouping_type.PARENS, get_origin(ctx));
        } else {
          // TODO: handle THIS, etc.
          return (construct) unsupported(ctx.getChild(2));
        }
      } else {
        @Nullable operator the_operator = operators.get(ctx.bop.getType());
        if (the_operator != null) {
          construct left_expression = visitExpression(ctx.expression(0));
          construct right_expression = visitExpression(ctx.expression(1));
          return new operator_construct(the_operator, left_expression, right_expression,
              get_origin(ctx));
        } else {
          System.out.println("BINARY OP " + ctx.bop.getType());
          return (construct) unsupported(ctx);
        }
      }
    }

    if (ctx.methodCall() != null) {
      return visitMethodCall(ctx.methodCall());
    }

    if (ctx.NEW() != null) {
      // TODO: generate different code based on generate_ideal
      parameter_construct creator = visitCreator(ctx.creator());
      return new parameter_construct(
          new resolve_construct(creator.main, special_name.NEW, get_origin(ctx)),
          creator.parameters, grouping_type.PARENS, get_origin(ctx));
    }

    return (construct) unsupported(ctx);
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
  public construct visitPrimary(PrimaryContext ctx) {
    // TODO: handle more primaries
    // TODO: convert to switch
    if (ctx.THIS() != null) {
      return new name_construct(special_name.THIS, get_origin(ctx));
    } else if (ctx.SUPER() != null) {
      return new name_construct(special_name.SUPER, get_origin(ctx));
    } else if (ctx.identifier() != null) {
      return visitIdentifier(ctx.identifier());
    } else if (ctx.literal() != null) {
      return visitLiteral(ctx.literal());
    }
    return (construct) unsupported(ctx);
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
  public parameter_construct visitCreator(CreatorContext ctx) {
    // TODO: handle arrayCreatorRest
    assert ctx.createdName() != null;
    assert ctx.classCreatorRest() != null;
    return new parameter_construct(visitCreatedName(ctx.createdName()),
        visitClassCreatorRest(ctx.classCreatorRest()), grouping_type.PARENS, get_origin(ctx));
  }

  @Override
  public construct visitCreatedName(CreatedNameContext ctx) {
    // TODO: handle dot-separated identifiers, typeArguments, primitiveType
    assert ctx.identifier(0) != null;
    construct result = visitIdentifier(ctx.identifier(0));
    if (ctx.typeArgumentsOrDiamond(0) != null) {
      result = new parameter_construct(result,
          visitTypeArgumentsOrDiamond(ctx.typeArgumentsOrDiamond(0)), grouping_type.BRACKETS,
          get_origin(ctx));
    }
    return result;
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
  public readonly_list<construct> visitClassCreatorRest(ClassCreatorRestContext ctx) {
    // TODO: handle classBody
    assert ctx.classBody() == null;
    return visitArguments(ctx.arguments());
  }

  @Override
  public Object visitExplicitGenericInvocation(ExplicitGenericInvocationContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public readonly_list<construct> visitTypeArgumentsOrDiamond(TypeArgumentsOrDiamondContext ctx) {
    if (ctx.typeArguments() != null) {
      return visitTypeArguments(ctx.typeArguments());
    } else {
      return new empty<construct>();
    }
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
  public readonly_list<construct> visitTypeList(TypeListContext ctx) {
    return to_constructs(ctx.typeType());
  }

  @Override
  public construct visitTypeType(TypeTypeContext ctx) {
    // TODO: handle annotations, primitiveTypes
    assert ctx.classOrInterfaceType() != null;
    construct result = visitClassOrInterfaceType(ctx.classOrInterfaceType());
    return make_array(result, ctx.LBRACK().size(), get_origin(ctx));
  }

  @Override
  public Object visitPrimitiveType(PrimitiveTypeContext ctx) {
    return unsupported(ctx);
  }

  @Override
  public readonly_list<construct> visitTypeArguments(TypeArgumentsContext ctx) {
    return to_constructs(ctx.typeArgument());
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
  public readonly_list<construct> visitArguments(ArgumentsContext ctx) {
    if (ctx.expressionList() != null) {
      return visitExpressionList(ctx.expressionList());
    } else {
      return new empty<construct>();
    }
  }

  protected String to_string(@Nullable ParseTree parseTree) {
    if (parseTree == null) {
      return "[null]";
    }

    return parseTree.toStringTree(java_parser);
  }

  public Object unsupported(ParseTree parseTree) {
    utilities.panic("Unsupported " + to_string(parseTree));
    return null;
  }
}
