/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.transformers;

import ideal.library.elements.*;
import javax.annotation.Nullable;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.development.elements.*;
import ideal.development.comments.*;
import ideal.development.actions.*;
import ideal.development.constructs.*;
import ideal.development.modifiers.*;
import static ideal.development.modifiers.access_modifier.*;
import static ideal.development.modifiers.general_modifier.*;
import ideal.development.flavors.*;
import static ideal.development.flavors.flavor.*;
import ideal.development.names.*;
import ideal.development.kinds.*;
import static ideal.development.kinds.type_kinds.*;
import static ideal.development.kinds.subtype_tags.*;
import ideal.development.types.*;
import ideal.development.declarations.*;
import ideal.development.scanners.*;
import ideal.development.analyzers.*;
import ideal.development.extensions.*;
import ideal.development.notifications.*;
import ideal.development.values.*;
import ideal.development.origins.*;
import ideal.development.literals.*;

public class to_java_transformer extends base_transformer {

  private static enum mapping {
    MAP_TO_PRIMITIVE_TYPE,
    MAP_TO_WRAPPER_TYPE,
    MAP_PRESERVE_ALIAS,
    NO_MAPPING;
  }

  private final java_library java_library;
  private list<construct> common_headers;
  private set<principal_type> implicit_names;
  private set<principal_type> imported_names;
  private mapping mapping_strategy;
  private principal_type package_type;
  private @Nullable principal_type enclosing_type;
  private @Nullable procedure_declaration the_enclosing_procedure;

  private static simple_name SET_NAME = simple_name.make("set");
  private static simple_name VALUE_NAME = simple_name.make("value");
  private static simple_name CALL_NAME = simple_name.make("call");

  private static simple_name OBJECTS_EQUAL_NAME = simple_name.make("values_equal");
  private static simple_name INT_TO_STRING_NAME = simple_name.make("int_to_string");
  private static simple_name CONCATENATE_NAME = simple_name.make("concatenate");

  private static simple_name BASE_STRING_NAME = simple_name.make("base_string");
  private static simple_name LIST_NAME = simple_name.make("list");
  private static simple_name STRING_NAME = simple_name.make("string");
  private static simple_name ORDINAL_NAME = simple_name.make("ordinal");
  private static simple_name TO_STRING_NAME = simple_name.make("to_string");
  private static simple_name TO_STRING_JAVA = simple_name.make("toString");
  private static simple_name START_NAME = simple_name.make("start");
  private static simple_name MAIN_NAME = simple_name.make("main");
  private static simple_name ARGS_NAME = simple_name.make("args");

  public to_java_transformer(java_library java_library) {
    this.java_library = java_library;
    this.mapping_strategy = mapping.MAP_TO_PRIMITIVE_TYPE;

    common_headers = new base_list<construct>();

    implicit_names = new hash_set<principal_type>();
    implicit_names.add(core_types.root_type());
    implicit_names.add(java_library.lang_package());
    implicit_names.add(java_library.builtins_package());

    imported_names = new hash_set<principal_type>();
  }

  public void set_type_context(principal_type main_type, readonly_list<import_declaration> imports,
      origin the_origin) {
    package_type = main_type.get_parent();

    assert package_type == core_types.root_type() ||
        package_type.short_name() instanceof simple_name;

    common_headers.append(make_newline(the_origin));

    if (package_type != core_types.root_type()) {
      common_headers.append(new package_construct(make_type(package_type, the_origin), the_origin));
      common_headers.append(make_newline(the_origin));
      implicit_names.add(package_type);
    }

    if (imports.is_not_empty()) {
      list<construct> import_headers = new base_list<construct>();

      // TODO: refactor as a filter
      for (int i = 0; i < imports.size(); ++i) {
        import_declaration the_import_declaration = imports.get(i);
        import_construct the_import = process_import(the_import_declaration);
        principal_type imported_type = (principal_type) the_import_declaration.get_type();
        if (imported_type == java_library.builtins_package() ||
            imported_type.get_parent() == java_library.builtins_package()) {
          continue;
        }
        if (the_import_declaration.is_implicit()) {
          implicit_names.add(imported_type);
        } else {
          imported_names.add(imported_type);
        }
        import_headers.append(the_import);
      }

      if (import_headers.is_not_empty()) {
        common_headers.append_all(import_headers);
        common_headers.append(make_newline(the_origin));
      }
    }
  }

  @Override
  protected modifier_kind process_modifier(modifier_kind the_modifier_kind,
      annotation_category category) {
    if (category == annotation_category.VARIABLE) {
      if (the_modifier_kind == implement_modifier || the_modifier_kind == override_modifier) {
        // Drop override on variables
        return null;
      }
    }

    if (the_modifier_kind == implement_modifier) {
      return override_modifier;
    } else if (general_modifier.supported_by_java.contains(the_modifier_kind)) {
      return the_modifier_kind;
    } else {
      return null;
    }
  }

  protected construct transform_analyzable(analyzable the_analyzable) {
    return transform_action(analyzer_utilities.to_action(the_analyzable));
  }

  protected construct transform_analyzable_or_null(analyzable the_analyzable, origin the_origin) {
    action the_action = analyzer_utilities.to_action(the_analyzable);
    if (is_nothing(the_action)) {
      return new empty_construct(the_origin);
    } else {
      return transform_action(the_action);
    }
  }

  protected type get_type(action the_action) {
    assert the_action instanceof type_action : "Action: " + the_action;
    return ((type_action) the_action).get_type();
  }

  private type result_type(action the_action) {
    type the_type = the_action.result().type_bound();
    if (library().is_reference_type(the_type)) {
      return library().get_reference_parameter(the_type);
    } else {
      return the_type;
    }
  }

  private boolean should_introduce_cast(type the_original_type, type the_narrowed_type) {
    if (the_narrowed_type == the_original_type) {
      return false;
    }

    if (the_original_type == library().immutable_integer_type()) {
      return false;
    }

    if (type_utilities.is_union(the_original_type)) {
      immutable_list<abstract_value> the_values =
          type_utilities.get_union_parameters(the_original_type);
      assert the_values.size() == 2;
      if (the_values.get(0) == the_narrowed_type ||
          the_values.get(1) == the_narrowed_type) {
        return false;
      }
    }

    return true;
  }

  private readonly_list<construct> transform_parameters_with_mapping(
      readonly_list<type_parameter_declaration> the_analyzables, mapping new_mapping) {
    mapping old_mapping_strategy = mapping_strategy;
    mapping_strategy = new_mapping;
    readonly_list<construct> result = transform_list(the_analyzables);
    mapping_strategy = old_mapping_strategy;
    return result;
  }

  public construct process_list_initializer_action(list_initializer_action initializer) {
    origin the_origin = initializer;
    type element_type = initializer.element_type;
    // Java doesn't like array creation with generic params.
    // javac complains with: "error: generic array creation".
    construct type_name_no_params = make_type(element_type, false, the_origin);
    construct alloc = new operator_construct(operator.ALLOCATE, type_name_no_params, the_origin);
    construct alloc_array = new parameter_construct(alloc, new empty<construct>(),
        grouping_type.BRACKETS, the_origin);
    // TODO: handle promotions
    construct alloc_call = new parameter_construct(alloc_array,
        transform_parameters(initializer.parameter_actions), grouping_type.BRACES, the_origin);

    construct type_name = make_type(element_type, the_origin);
    construct array_name = make_type(java_library.array_class(), the_origin);
    construct param_array = new parameter_construct(array_name, new base_list<construct>(type_name),
        grouping_type.ANGLE_BRACKETS, the_origin);
    construct alloc_array2 = new operator_construct(operator.ALLOCATE, param_array, the_origin);
    construct array_call = new parameter_construct(alloc_array2,
        new base_list<construct>(alloc_call), grouping_type.PARENS, the_origin);

    // TODO: import type if needed
    // construct list_name = make_type(java_library.base_immutable_list_class(), the_origin);
    construct list_name = new name_construct(simple_name.make("base_immutable_list"), the_origin);
    construct param_list = new parameter_construct(list_name, new base_list<construct>(type_name),
        grouping_type.ANGLE_BRACKETS, the_origin);
    construct alloc_list = new operator_construct(operator.ALLOCATE, param_list, the_origin);
    return new parameter_construct(alloc_list, new base_list<construct>(array_call),
        grouping_type.PARENS, the_origin);
  }

  public @Nullable list_construct process_parameters(
      @Nullable readonly_list<variable_declaration> the_parameters, origin the_origin) {
    if (the_parameters == null) {
      return null;
    } else {
      return new list_construct(transform_list(the_parameters), grouping_type.PARENS, false,
          the_origin);
    }
  }

  private boolean is_null_subtype(type the_type) {
    return the_type.principal() == library().missing_type();
  }

  private action_name map_name(action_name the_name) {
    if (the_name == special_name.THIS_CONSTRUCTOR) {
      return special_name.THIS;
    } else if (the_name == special_name.SUPER_CONSTRUCTOR) {
      return special_name.SUPER;
    } else {
      return the_name;
    }
  }

  private construct process_narrow_action(narrow_action the_narrow_action, construct expression,
      origin the_origin) {
    type the_original_type = result_type(the_narrow_action.expression);
    type the_narrowed_type = the_narrow_action.the_type;
    if (should_introduce_cast(the_original_type, the_narrowed_type)) {
      construct cast = new operator_construct(operator.HARD_CAST, expression,
          make_type(the_narrowed_type, the_origin), the_origin);
      return new list_construct(new base_list<construct>(cast), grouping_type.PARENS, false,
          the_origin);
    } else {
      return expression;
    }
  }

  private construct maybe_call(action the_action, construct the_construct, origin the_origin) {
    declaration the_declaration = declaration_util.get_declaration(the_action);
    if (the_declaration != null && should_call_as_procedure(the_declaration)) {
      return make_call(the_construct, new empty<construct>(), the_origin);
    } else {
      return the_construct;
    }
  }

  private boolean should_call_as_procedure(declaration the_declaration) {
    if (the_declaration instanceof variable_declaration) {
      variable_declaration the_variable = (variable_declaration) the_declaration;
      if (has_override(the_variable.annotations())) {
        return true;
      }
      kind parent_kind = the_variable.declared_in_type().get_kind();
      if (!is_concrete_kind(parent_kind) &&
          parent_kind != block_kind &&
          parent_kind != namespace_kind) {
        return true;
      }
      if (parent_kind == enum_kind && the_variable instanceof field_declaration) {
        return true;
      }
    } else if (the_declaration instanceof procedure_declaration) {
      procedure_declaration the_procedure = (procedure_declaration) the_declaration;
      return the_procedure.overrides_variable();
    }

    return false;
  }

  private boolean has_override(annotation_set the_annotations) {
    return the_annotations.has(override_modifier) || the_annotations.has(implement_modifier);
  }

  private construct make_parametrized_type(construct main, readonly_list<construct> parameters,
      origin the_origin) {
    return new parameter_construct(main, parameters, grouping_type.ANGLE_BRACKETS, the_origin);
  }

  private boolean should_use_wrapper_in_return(procedure_declaration the_declaration) {
    readonly_list<declaration> overriden = the_declaration.get_overriden();
    // TODO: use list.has()
    for (int i = 0; i < overriden.size(); ++i) {
      declaration overriden_declaration = overriden.get(i);
      if (overriden_declaration instanceof type_declaration) {
        assert ((type_declaration) overriden_declaration).get_kind() == procedure_kind;
        return true;
      } else if (overriden_declaration instanceof procedure_declaration) {
        procedure_declaration super_procedure = (procedure_declaration) overriden_declaration;
        if (super_procedure instanceof specialized_procedure) {
          super_procedure = ((specialized_procedure) super_procedure).master_declaration();
        }
        type return_type = super_procedure.get_return_type();
        if (return_type.principal().get_declaration() instanceof type_parameter_declaration) {
          return true;
        }
        if (should_use_wrapper_in_return(super_procedure)) {
          return true;
        }
      } else if (overriden_declaration instanceof variable_declaration) {
        variable_declaration super_variable = (variable_declaration) overriden_declaration;
        if (super_variable instanceof specialized_variable) {
          super_variable = ((specialized_variable) super_variable).get_main();
        }
        type return_type = super_variable.value_type();
        if (return_type.principal().get_declaration() instanceof type_parameter_declaration) {
          return true;
        }
      }
    }

    return false;
  }

  private boolean skip_access(principal_type declared_in_type) {
    return !is_concrete_kind(declared_in_type.get_kind());
  }

  @Override
  public construct process_procedure(procedure_declaration the_procedure) {
    origin the_origin = the_procedure;

    list<annotation_construct> annotations = to_annotations(the_procedure.annotations(),
        annotation_category.PROCEDURE, skip_access(the_procedure.declared_in_type()), the_origin);

    return process_procedure(the_procedure, annotations);
  }

  private boolean is_unreachable_result(procedure_declaration the_procedure) {
    return the_procedure.get_body_action() != null &&
        the_procedure.get_body_action().result().type_bound() == core_types.unreachable_type();
  }

  public construct process_procedure(procedure_declaration the_procedure,
      list<annotation_construct> annotations) {
    origin the_origin = the_procedure;
    action_name name = the_procedure.original_name();
    @Nullable list_construct the_list_construct = process_parameters(
        the_procedure.get_parameter_variables(), the_origin);
    if (the_list_construct == null) {
      the_list_construct = make_parens(the_origin);
    }
    readonly_list<construct> parameters = the_list_construct.the_elements;
    @Nullable readonly_list<construct> body_statements = null;

    if (the_procedure.get_body_action() != null) {
      boolean is_constructor = the_procedure.get_category() == procedure_category.CONSTRUCTOR;
      boolean void_return = the_procedure.get_return_type().principal() == library().void_type();
      boolean unreachable_result = is_unreachable_result(the_procedure);

      boolean add_return = !is_constructor && !unreachable_result && void_return &&
          should_use_wrapper_in_return(the_procedure);

      the_enclosing_procedure = the_procedure;
      list<construct> body = transform_action_list(the_procedure.get_body_action());
      the_enclosing_procedure = null;
      if (add_return) {
        body.append(new return_construct(make_null(the_origin), the_origin));
      } else if (body.size() == 1) {
        // TODO: use analyzables instead of constructs here
        construct body_construct = body.first();
        if (!is_constructor && !void_return && !unreachable_result) {
          body = new base_list<construct>(new return_construct(body_construct, the_origin));
        }
      }
      body_statements = body;
    }

    @Nullable construct ret;
    if (the_procedure.get_category() == procedure_category.CONSTRUCTOR) {
      ret = null;
    } else {
      type return_type = the_procedure.get_return_type();
      if (type_utilities.is_union(return_type)) {
        annotations.append(make_nullable(the_origin));
        ret = make_type_with_mapping(library().remove_null_type(return_type), the_origin,
            mapping.MAP_TO_WRAPPER_TYPE);
      } else if (library().is_reference_type(return_type)) {
        type_flavor ref_flavor = return_type.get_flavor();
        ret = make_type(return_type, the_origin);
        if (ref_flavor != mutable_flavor) {
          if (ret instanceof flavor_construct) {
            ret = ((flavor_construct) ret).expr;
          }
          assert ret instanceof parameter_construct;
          readonly_list<construct> ret_parameters = ((parameter_construct) ret).parameters;
          assert ret_parameters.size() == 1;
          ret = ret_parameters.first();
          if (ref_flavor == writeonly_flavor) {
            // TODO: use copy ctor.
            list<construct> new_parameters = new base_list<construct>(parameters);
            new_parameters.append(
                new variable_construct(new empty<annotation_construct>(), ret, VALUE_NAME,
                    new empty<annotation_construct>(), null, the_origin));
            parameters = new_parameters;
            ret = make_type(library().void_type(), the_origin);
          }
        }
      } else {
        if (should_use_wrapper_in_return(the_procedure)) {
          ret = make_type_with_mapping(the_procedure.get_return_type(), the_origin,
              mapping.MAP_TO_WRAPPER_TYPE);
          // Note: if Java return type is 'Void' (with the capital V),
          // then we may need to insert "return null" to keep javac happy.
        } else {
          if (the_procedure.get_return_type() == core_types.unreachable_type()) {
            ret = make_type(library().void_type(), the_origin);
          } else {
            if (is_object_type(the_procedure.get_return_type())) {
              ret = make_object_type(the_origin);
            } else {
              ret = make_type(the_procedure.get_return_type(), the_origin);
            }
          }
        }
      }
    }

    construct body = null;
    if (body_statements != null) {
      body = new block_construct(body_statements, the_origin);
    }

    // Note: the flavor is always missing.
    return new procedure_construct(annotations, ret, name, parameters,
        new empty<annotation_construct>(), body, the_origin);
  }

  modifier_construct make_nullable(origin the_origin) {
    return new modifier_construct(nullable_modifier, the_origin);
  }

  protected construct make_type_with_mapping(type the_type, origin the_origin,
      mapping new_mapping) {
    mapping old_mapping_strategy = mapping_strategy;
    mapping_strategy = new_mapping;
    construct result = make_type(the_type, the_origin);
    mapping_strategy = old_mapping_strategy;
    return result;
  }

  @Override
  protected construct make_type(type the_type, origin the_origin) {
    return make_type(the_type, true, the_origin);
  }

  protected construct make_type(type the_type, boolean include_parameters, origin the_origin) {
    principal_type principal = the_type.principal();
    type_flavor the_flavor = the_type.get_flavor();

    if (mapping_strategy != mapping.MAP_PRESERVE_ALIAS && is_aliased_type(principal)) {
      principal = substitute_aliased_type(principal);
    }

    switch (mapping_strategy) {
      case MAP_TO_PRIMITIVE_TYPE:
        if (principal == library().void_type()) {
          break;
        } else if (principal == library().boolean_type()) {
          principal = java_library.boolean_type();
          break;
        } else if (principal == library().character_type()) {
          principal = java_library.char_type();
          break;
        }
        /*
        @Nullable principal_type mapped = java_library.map_to_primitive(principal);
        if (mapped != null) {
          principal = mapped;
        }
        break;
        */
      case MAP_TO_WRAPPER_TYPE:
      case MAP_PRESERVE_ALIAS:
        @Nullable simple_name mapped_name = java_library.map_to_wrapper(principal);
        if (mapped_name != null) {
          return new name_construct(mapped_name, the_origin);
        }
        break;
      case NO_MAPPING:
        if (java_library.is_mapped(principal)) {
          utilities.panic("No mapping expected for " + principal);
        }
        break;
    }

    if (type_utilities.is_union(principal)) {
      type removed_null = library().remove_null_type(principal);
      principal = removed_null.principal();
      the_flavor = removed_null.get_flavor();
    }

    action_name the_name = make_name(get_simple_name(principal), principal, the_flavor);
    construct name = make_resolve(make_parent_name(principal, false, the_origin), the_name,
        the_origin);

    if (include_parameters && principal instanceof parametrized_type) {
      immutable_list<abstract_value> type_params =
          ((parametrized_type) principal).get_parameters().fixed_size_list();
      list<construct> params = new base_list<construct>();
      for (int i = 0; i < type_params.size(); ++i) {
        abstract_value av = type_params.get(i);
        assert av instanceof type;
        type the_param_type = (type) av;
        boolean object_parameter = is_object_type(the_param_type);
        if (object_parameter) {
          params.append(make_object_type(the_origin));
        } else {
          params.append(make_type_with_mapping(the_param_type, the_origin,
              mapping.MAP_TO_WRAPPER_TYPE));
        }
      }
      return make_parametrized_type(name, params, the_origin);
    } else {
      return name;
    }
  }

  protected construct make_object_type(origin the_origin) {
    return make_type(java_library.object_type(), the_origin);
  }

  protected construct make_flavored_and_parametrized_type(principal_type principal,
      type_flavor flavor, @Nullable readonly_list<construct> type_parameters, origin the_origin) {
    construct name = new name_construct(make_name(get_simple_name(principal), principal,
        flavor), the_origin);

    if (type_parameters != null) {
      list<construct> parameters = new base_list<construct>();
      for (int i = 0; i < type_parameters.size(); ++i) {
        construct parameter = type_parameters.get(i);
        if (parameter instanceof variable_construct) {
          parameters.append(new name_construct(((variable_construct) parameter).name, the_origin));
        } else {
          parameters.append(parameter);
        }
      }
      return make_parametrized_type(name, parameters, the_origin);
    } else {
      return name;
    }
  }

  @Override
  protected simple_name get_simple_name(principal_type the_type) {
    if (type_utilities.is_union(the_type)) {
      the_type = library().remove_null_type(the_type).principal();
    }

    if (the_type.short_name() instanceof simple_name) {
      simple_name the_name = (simple_name) the_type.short_name();
      if (the_type.get_kind() == procedure_kind && the_type instanceof parametrized_type) {
        int arity = ((parametrized_type) the_type).get_parameters().fixed_size_list().size() - 1;
        return make_procedure_name(the_name, arity);
      } else {
        return the_name;
      }
    }

    assert the_type.get_parent() != null;
    return get_simple_name(the_type.get_parent());
  }

  protected boolean is_top_package(type the_type) {
    return the_type == java_library.java_package() || the_type == java_library.javax_package();
  }

  protected @Nullable construct make_parent_name(principal_type the_type, boolean is_import,
      origin the_origin) {
    if (!is_import) {
      if (imported_names.contains(the_type)) {
        return null;
      }

      if (the_type instanceof parametrized_type) {
        master_type the_master_type = ((parametrized_type) the_type).get_master();
        if (imported_names.contains(the_master_type)) {
          return null;
        }
      }
    }

    if (the_type.get_declaration() instanceof type_parameter_declaration) {
      return null;
    }

    if (is_top_package(the_type)) {
      return null;
    }

    @Nullable principal_type parent = the_type.get_parent();
    if (parent == null || parent == core_types.root_type() ||
        (!is_import && implicit_names.contains(parent))) {
      return null;
    }

    if (! (parent.short_name() instanceof simple_name)) {
      utilities.panic("Full name of " + the_type + ", parent " + parent);
    }

    return make_resolve(make_parent_name(parent, is_import, the_origin), parent.short_name(),
        the_origin);
  }

  protected construct make_imported_type(principal_type the_type, origin the_origin) {
    action_name the_name = get_simple_name(the_type);
    return make_resolve(make_parent_name(the_type, true, the_origin), the_name, the_origin);
  }

  @Override
  protected simple_name make_name(simple_name type_name, principal_type the_type,
      type_flavor flavor) {
    type_utilities.prepare(the_type, declaration_pass.FLAVOR_PROFILE);
    if (flavor == nameonly_flavor || flavor == the_type.get_flavor_profile().default_flavor()) {
      return type_name;
    } else {
      return name_utilities.join(flavor.name(), type_name);
    }
  }

  protected readonly_list<annotation_construct> make_annotations(access_modifier access,
      origin the_origin) {
    if (access != local_modifier) {
      return new base_list<annotation_construct>(new modifier_construct(access, the_origin));
    } else {
      return new empty<annotation_construct>();
    }
  }

  private void append_static(list<annotation_construct> annotations, origin the_origin) {
    // TODO: replace with collection.has()
    for (int i = 0; i < annotations.size(); ++i) {
      annotation_construct the_annotation = annotations.get(i);
      if (the_annotation instanceof modifier_construct &&
          ((modifier_construct) the_annotation).the_kind == static_modifier) {
        return;
      }
    }

    // TODO: should modifier list be sorted?...
    annotations.append(new modifier_construct(static_modifier, the_origin));
  }

  private readonly_list<construct> transform_static(readonly_list<declaration> declarations) {
    list<construct> result = new base_list<construct>();
    for (int i = 0; i < declarations.size(); ++i) {
      declaration decl = declarations.get(i);
      origin the_origin = decl;
      if (decl instanceof variable_declaration) {
        variable_declaration the_variable = (variable_declaration) decl;
        list<annotation_construct> annotations = to_annotations(the_variable.annotations(),
            annotation_category.VARIABLE, false, the_origin);
        append_static(annotations, the_origin);
        // TODO: do we need to handle null?
        result.append(process_variable(the_variable, annotations));
      } else if (decl instanceof procedure_declaration) {
        procedure_declaration the_procedure = (procedure_declaration) decl;
        list<annotation_construct> annotations = to_annotations(the_procedure.annotations(),
            annotation_category.PROCEDURE, false, the_origin);
        if (the_procedure.get_category() != procedure_category.CONSTRUCTOR) {
          append_static(annotations, the_origin);
        }
        result.append(process_procedure(the_procedure, annotations));
      } else if (decl instanceof block_declaration) {
        // TODO: make sure this is a static block...
        result.append_all(transform1(decl));
      } else if (decl instanceof import_declaration) {
        // Skip imports: they should have been declared at the top level.
      } else {
        utilities.panic("Unexpected declaration in a namespace: " + decl);
      }
    }

    return result;
  }

  private boolean skip_type_declaration(principal_type the_type) {
    if (java_library.is_mapped(the_type) || the_type.is_subtype_of(library().null_type())) {
      return true;
    }
    kind the_kind = the_type.get_kind();
    assert the_kind != procedure_kind;
    return false; // the_kind == reference_kind;
  }

  private immutable_list<type_flavor> supported_no_raw(flavor_profile profile) {
    list<type_flavor> result = new base_list<type_flavor>();
    immutable_list<type_flavor> supported_flavors = profile.supported_flavors();
    // TODO: use list.filter()
    for (int i = 0; i < supported_flavors.size(); ++i) {
      type_flavor flavor = supported_flavors.get(i);
      if (flavor != raw_flavor) {
        result.append(flavor);
      }
    }
    return result.frozen_copy();
  }

  @Override
  public Object process_type(type_declaration the_type_declaration) {
    origin the_origin = the_type_declaration;
    list<annotation_construct> annotations = to_annotations(
        the_type_declaration.annotations(), annotation_category.TYPE, false, the_origin);

    kind the_kind = the_type_declaration.get_kind();
    principal_type declared_in_type = the_type_declaration.declared_in_type();

    if (the_kind.is_namespace()) {
      assert the_type_declaration.get_parameters() == null;
      if (declared_in_type != package_type) {
        append_static(annotations, the_origin);
      }
      // TODO: add a private constructor
      return new type_declaration_construct(
        annotations,
        class_kind,
        the_type_declaration.short_name(),
        null,
        transform_static(the_type_declaration.get_signature()),
        the_origin);
    }

    if (the_kind == procedure_kind) {
      return make_procedure_declarations(annotations, the_type_declaration);
    }

    principal_type declared_type = the_type_declaration.get_declared_type();

    if (false) { // Looks like this never happens.  TODO: retire this
      if (declared_type.get_declaration() != the_type_declaration) {
        // This happens when specializes declaration, e.g. for collection[data]
        return null;
      }
    }

    if (skip_type_declaration(declared_type)) {
      return null;
    }

    @Nullable principal_type old_enclosing_type = enclosing_type;
    enclosing_type = declared_type;

    if (is_concrete_kind(declared_in_type.get_kind())) {
      // Introduce inner type.
      append_static(annotations, the_origin);
    }

    if (the_kind == test_suite_kind || the_kind == program_kind) {
      the_kind = class_kind;
    }

    boolean concrete_mode = is_concrete_kind(the_kind);
    flavor_profile profile = the_kind == class_kind ? class_profile :
        declared_type.get_flavor_profile();

    // TODO: implement namespaces
    assert profile != flavor_profiles.nameonly_profile;

    simple_name type_name = (simple_name) the_type_declaration.short_name();

    @Nullable readonly_list<construct> type_parameters = null;

    if (the_type_declaration.get_parameters() != null) {
      type_parameters = transform_parameters_with_mapping(the_type_declaration.get_parameters(),
              mapping.MAP_TO_WRAPPER_TYPE);
    }

    dictionary<type_flavor, list<construct>> flavored_bodies =
        new list_dictionary<type_flavor, list<construct>>();
    dictionary<type_flavor, list<construct>> supertype_lists =
        new list_dictionary<type_flavor, list<construct>>();
    @Nullable construct superclass = null;

    immutable_list<type_flavor> supported_flavors = supported_no_raw(profile);
    for (int i = 0; i < supported_flavors.size(); ++i) {
      type_flavor flavor = supported_flavors.get(i);
      flavored_bodies.put(flavor, new base_list<construct>());
      supertype_lists.put(flavor, new base_list<construct>());
    }

    readonly_list<declaration> body = the_type_declaration.get_signature();
    boolean generate_to_string = true;

    for (int i = 0; i < body.size(); ++i) {
      declaration decl = body.get(i);
      if (decl instanceof supertype_declaration) {
        supertype_declaration supertype_decl = (supertype_declaration) decl;
        // TODO: retire this when there is no need to match legacy output
        if (supertype_decl.annotations().has(synthetic_modifier)) {
          continue;
        }
        type supertype = supertype_decl.get_supertype();
        if (skip_supertype(the_type_declaration, supertype)) {
          continue;
        }
        kind supertype_kind = supertype.principal().get_kind();
        if (concrete_mode) {
          // TODO: use NO_MAPPING here.
          construct transformed_supertype = make_type_with_mapping(
              supertype_decl.get_supertype(), the_origin, mapping.MAP_PRESERVE_ALIAS);
          if (supertype_kind == class_kind) {
            if (superclass != null) {
              // TODO: do not panic!
              utilities.panic("Oh no. Two superclasses!");
            }
            superclass = transformed_supertype;
          } else {
            list<construct> supertype_list = supertype_lists.get(profile.default_flavor());
            assert supertype_list != null;
            supertype_list.append(transformed_supertype);
          }
        } else {
          origin the_origin2 = supertype_decl;
          if (supertype_decl.subtype_flavor() == null &&
              supertype instanceof principal_type) {
            immutable_list<type_flavor> type_flavors = flavored_bodies.keys().elements();
            for (int k = 0; k < type_flavors.size(); ++k) {
              type_flavor flavor = type_flavors.get(k);
              flavor_profile supertype_profile = supertype.principal().get_flavor_profile();
              // TODO: iterate over supertype_lists?
              if (supertype_profile.supports(flavor)) {
                construct flavored_supertype;
                if (flavor == supertype_profile.default_flavor()) {
                  flavored_supertype = make_type_with_mapping(supertype_decl.get_supertype(),
                      the_origin2, mapping.MAP_PRESERVE_ALIAS);
                } else {
                  flavored_supertype = make_type_with_mapping(supertype.get_flavored(flavor),
                      the_origin2, mapping.MAP_PRESERVE_ALIAS);
                }
                list<construct> supertype_list = supertype_lists.get(flavor);
                assert supertype_list != null;
                supertype_list.append(flavored_supertype);
              }
            }
          } else {
            type_flavor subtype_flavor = supertype_decl.subtype_flavor();
            if (subtype_flavor == null) {
              subtype_flavor = supertype.get_flavor();
            }
            construct flavored_supertype = make_type_with_mapping(supertype_decl.get_supertype(),
                the_origin2, mapping.MAP_PRESERVE_ALIAS);
            list<construct> supertype_list = supertype_lists.get(profile.map(subtype_flavor));
            assert supertype_list != null;
            supertype_list.append(flavored_supertype);
          }
        }
      } else if (decl instanceof procedure_declaration) {
        procedure_declaration proc_decl = (procedure_declaration) decl;
        if (proc_decl.short_name() == TO_STRING_NAME) {
          generate_to_string = false;
        }
        @Nullable procedure_construct proc_construct = (procedure_construct) transform(proc_decl);
        if (proc_construct == null) {
          continue;
        }
        type_flavor flavor = proc_decl.get_flavor();
        if (flavor == nameonly_flavor || flavor == raw_flavor) {
          flavor = profile.default_flavor();
        } else {
          flavor = profile.map(flavor);
        }
        flavored_bodies.get(flavor).append(proc_construct);
      } else if (decl instanceof enum_value_analyzer) {
        assert the_kind == enum_kind;
        flavored_bodies.get(profile.default_flavor()).append(
            process_enum_value((enum_value_analyzer) decl));
      } else if (decl instanceof variable_declaration) {
        variable_declaration var_decl = (variable_declaration) decl;
        @Nullable variable_construct var_construct = (variable_construct) transform(decl);
        if (var_construct == null) {
          continue;
        }
        if (concrete_mode || var_decl.annotations().has(static_modifier)) {
          flavored_bodies.get(profile.default_flavor()).append(var_construct);
        } else {
          procedure_construct proc_decl = var_to_proc(var_decl);
          type_flavor target_flavor = var_decl.reference_type().get_flavor();
          if (is_readonly_flavor(target_flavor)) {
            target_flavor = readonly_flavor;
          }
          flavored_bodies.get(profile.map(target_flavor)).append(proc_decl);
        }
      } else if (decl instanceof type_declaration) {
        flavored_bodies.get(profile.default_flavor()).append_all(transform1(decl));
      } else if (decl instanceof import_declaration) {
        // Skip imports: they should have been declared at the top level.
      } else if (decl instanceof block_declaration) {
        // TODO: make sure this is a static block...
        flavored_bodies.get(profile.default_flavor()).append_all(transform1(decl));
      } else {
        utilities.panic("Unknown declaration: " + decl);
      }
    }

    // TODO: move this into type_declaration_analyzer or enum_kind.
    if (the_kind == enum_kind && generate_to_string) {
      list<annotation_construct> modifier_list = new base_list<annotation_construct>(
          new modifier_construct(public_modifier, the_origin));
      block_construct to_string_body = new block_construct(
          new base_list<construct>(
              new return_construct(
                  base_string_wrap(
                    new parameter_construct(
                        new name_construct(TO_STRING_JAVA, the_origin),
                        new empty<construct>(),
                        grouping_type.PARENS,
                        the_origin
                    ),
                    the_origin
                  ),
                  the_origin
              )
          ),
          the_origin
      );

      procedure_construct to_string_procedure = new procedure_construct(
          modifier_list,
          new name_construct(STRING_NAME, the_origin),
          TO_STRING_NAME,
          new empty<construct>(),
          new empty<annotation_construct>(),
          to_string_body,
          the_origin);
      flavored_bodies.get(profile.default_flavor()).append(to_string_procedure);
    }

    if (the_type_declaration.get_kind() == program_kind) {
      list<annotation_construct> modifier_list = new base_list<annotation_construct>(
          new modifier_construct(public_modifier, the_origin),
          new modifier_construct(static_modifier, the_origin));
      block_construct main_body = new block_construct(
          new base_list<construct>(
              new parameter_construct(
                  new resolve_construct(
                      new parameter_construct(
                          new operator_construct(
                              operator.ALLOCATE,
                              new name_construct(
                                  type_name,
                                  the_origin
                              ),
                              the_origin
                          ),
                          new empty<construct>(),
                          grouping_type.PARENS,
                          the_origin
                      ),
                      START_NAME,
                      the_origin
                  ),
                  new empty<construct>(),
                  grouping_type.PARENS,
                  the_origin
              )
          ),
          the_origin
      );
      procedure_construct main_procedure = new procedure_construct(
          modifier_list,
          make_type(library().immutable_void_type(), the_origin),
          MAIN_NAME,
          new base_list<construct>(
              new variable_construct(
                  new empty<annotation_construct>(),
                  new parameter_construct(
                      make_type(java_library.string_type(), the_origin),
                      new empty<construct>(),
                      grouping_type.BRACKETS,
                      the_origin
                  ),
                  ARGS_NAME,
                  new empty<annotation_construct>(),
                  null,
                  the_origin
              )
          ),
          new empty<annotation_construct>(),
          main_body,
          the_origin);
      flavored_bodies.get(profile.default_flavor()).append(main_procedure);
    }

    list<construct> type_decls = new base_list<construct>();

    immutable_list<type_flavor> type_flavors = supported_no_raw(profile);
    for (int i = 0; i < type_flavors.size(); ++i) {
      type_flavor flavor = type_flavors.get(i);
      list<construct> flavored_body = flavored_bodies.get(flavor);
      assert flavored_body != null;
      list<construct> supertype_list = supertype_lists.get(flavor);
      assert supertype_list != null;

      immutable_list<type_flavor> superflavors = flavor.get_superflavors();
      for (int j = 0; j < superflavors.size(); ++j) {
        type_flavor superflavor = superflavors.get(j);
        if (profile.supports(superflavor)) {
          supertype_list.append(make_flavored_and_parametrized_type(declared_type, superflavor,
              type_parameters, the_origin));
        }
      }

      if (supertype_list.is_not_empty()) {
        subtype_tag the_subtype_tag = concrete_mode ? implements_tag : extends_tag;
        flavored_body.prepend(new supertype_construct(new empty<annotation_construct>(),
            null, the_subtype_tag, supertype_list, the_origin));
      }

      simple_name flavored_name;
      if (concrete_mode) {
        flavored_name = type_name;
        if (superclass != null) {
          flavored_body.prepend(new supertype_construct(new empty<annotation_construct>(), null,
              extends_tag, new base_list<construct>(superclass), the_origin));
        }
      } else {
        flavored_name = make_name(type_name, declared_type, flavor);
      }

      type_decls.append(new type_declaration_construct(annotations,
          concrete_mode ? the_kind : interface_kind, flavored_name, type_parameters,
          flavored_body, the_origin));
    }

    enclosing_type = old_enclosing_type;

    return type_decls;
  }

  private static simple_name STRING_TEXT_NODE = simple_name.make("string_text_node");

  private boolean is_aliased_type(principal_type the_principal_type) {
    return the_principal_type.short_name() == STRING_TEXT_NODE;
  }

  private principal_type substitute_aliased_type(principal_type the_principal_type) {
    return library().string_type();
  }

  private boolean skip_supertype(type_declaration the_type_declaration, type supertype) {
    return the_type_declaration.short_name() == STRING_TEXT_NODE &&
        supertype.principal() == library().string_type();
  }

  private static boolean is_readonly_flavor(type_flavor the_flavor) {
    return the_flavor == readonly_flavor ||
           the_flavor == immutable_flavor ||
           the_flavor == deeply_immutable_flavor;
  }

  private static boolean is_concrete_kind(kind the_kind) {
    return the_kind == class_kind || the_kind == enum_kind || the_kind == test_suite_kind ||
        the_kind == program_kind;
  }

  private procedure_construct var_to_proc(variable_declaration the_variable) {
    origin the_origin = the_variable;
    type return_type = is_readonly_flavor(the_variable.reference_type().get_flavor()) ?
        the_variable.value_type() : the_variable.reference_type();
    // TODO: should we inherit attotaions from the_variable?
    list<annotation_construct> annotations = new base_list<annotation_construct>();
    if (type_utilities.is_union(return_type)) {
      annotations.append(make_nullable(the_origin));
      // make_type() strips null from union type
    }
    // This is a hack to get gregorian_month to work.
    if (the_variable.short_name() == ORDINAL_NAME &&
        return_type == library().immutable_nonnegative_type()) {
      return_type = java_library.int_type().get_flavored(deeply_immutable_flavor);
    }
    return new procedure_construct(annotations,
        make_type(return_type, the_origin),
        the_variable.short_name(),
        new empty<construct>(),
        new empty<annotation_construct>(),
        null,
        the_origin);
  }

  private list_construct make_parens(origin the_origin) {
    return new list_construct(new base_list<construct>(), grouping_type.PARENS, false, the_origin);
  }

  private Object make_procedure_declarations(readonly_list<annotation_construct> annotations,
      type_declaration the_type_declaration) {
    action_name the_name = the_type_declaration.short_name();
    boolean is_function;
    if (utilities.eq(the_name, common_library.procedure_name)) {
      is_function = false;
    } else if (utilities.eq(the_name, common_library.function_name)) {
      is_function = true;
    } else {
      utilities.panic("Unexpected procedure type " + the_name);
      return null;
    }

    list<construct> result = new base_list<construct>();
    result.append(make_procedure_construct(annotations, the_type_declaration, is_function, 0));
    result.append(make_procedure_construct(annotations, the_type_declaration, is_function, 1));
    result.append(make_procedure_construct(annotations, the_type_declaration, is_function, 2));
    return result;
  }

  private type_declaration_construct make_procedure_construct(
      readonly_list<annotation_construct> annotations,
      type_declaration the_type_declaration,
      boolean is_function, int arity) {

    origin the_origin = the_type_declaration;

    readonly_list<annotation_construct> empty_annotations = new empty<annotation_construct>();

    simple_name return_name = simple_name.make("R");
    construct return_construct = new name_construct(return_name, the_origin);

    list<construct> type_parameters = new base_list<construct>();
    type_parameters.append(new variable_construct(empty_annotations, null, return_name,
        empty_annotations, null, the_origin));

    list<construct> call_parameters = new base_list<construct>();

    list<construct> supertype_parameters = new base_list<construct>();
    supertype_parameters.append(return_construct);

    for (int i = 0; i < arity; ++i) {
      simple_name argument_type = simple_name.make("A" + i);
      type_parameters.append(new variable_construct(empty_annotations, null, argument_type,
          empty_annotations, null, the_origin));
      supertype_parameters.append(new name_construct(argument_type, the_origin));
      simple_name argument_name = name_utilities.make_numbered_name(i);
      call_parameters.append(new variable_construct(empty_annotations,
          new name_construct(argument_type, the_origin), argument_name, empty_annotations, null,
          the_origin));
    }

    list<construct> extends_types = new base_list<construct>();
    if (is_function) {
      construct superprocedure = new parameter_construct(
          new name_construct(make_procedure_name(false, arity), the_origin),
          supertype_parameters, grouping_type.ANGLE_BRACKETS, the_origin);
      extends_types.append(superprocedure);
    }

    readonly_list<declaration> body = the_type_declaration.get_signature();
    for (int i = 0; i < body.size(); ++i) {
      declaration decl = body.get(i);
      if (decl instanceof supertype_declaration) {
        supertype_declaration supertype_decl = (supertype_declaration) decl;
        type supertype = supertype_decl.get_supertype();
        if (supertype.principal().get_kind() != procedure_kind) {
          extends_types.append(make_type(supertype_decl.get_supertype(), the_origin));
        }
      }
    }

    list<construct> type_body = new base_list<construct>();
    type_body.append(new supertype_construct(new empty<annotation_construct>(), null,
        extends_tag, extends_types, the_origin));

    if (!is_function) {
      type_body.append(new procedure_construct(empty_annotations, return_construct,
          CALL_NAME, call_parameters, empty_annotations, null, the_origin));
    }

    simple_name type_name = make_procedure_name(is_function, arity);
    return new type_declaration_construct(annotations, interface_kind, type_name, type_parameters,
        type_body, the_origin);
  }

  private simple_name make_procedure_name(boolean is_function, int arity) {
    simple_name base_name = is_function ? common_library.function_name :
        common_library.procedure_name;
    return make_procedure_name(base_name, arity);
  }

  private simple_name make_procedure_name(simple_name base_name, int arity) {
    return simple_name.make(new base_string(base_name.to_string(), String.valueOf(arity)));
  }

  @Override
  public construct process_variable(variable_declaration the_variable) {
    if (the_variable instanceof enum_value_analyzer) {
      return process_enum_value((enum_value_analyzer) the_variable);
    }

    origin the_origin = the_variable;

    principal_type declared_in_type = the_variable.declared_in_type();
    list<annotation_construct> annotations = to_annotations(the_variable.annotations(),
        annotation_category.VARIABLE, skip_access(declared_in_type), the_origin);

    return process_variable(the_variable, annotations);
  }

  public construct process_variable(variable_declaration the_variable,
      list<annotation_construct> annotations) {
    origin the_origin = the_variable;
    principal_type declared_in_type = the_variable.declared_in_type();

    boolean is_mutable = the_variable.reference_type().get_flavor() == mutable_flavor;
    if (!is_mutable &&
        !the_variable.annotations().has(final_modifier) &&
        !is_procedure_with_no_body(declared_in_type.get_declaration())) {
      annotations.append(new modifier_construct(final_modifier, the_origin));
    }

    type var_type = the_variable.value_type();
    construct type;
    if (type_utilities.is_union(var_type)) {
      annotations.append(make_nullable(the_origin));
      type not_null_type = library().remove_null_type(var_type);
      if (is_object_type(not_null_type)) {
        type = make_object_type(the_origin);
      } else {
        type = make_type_with_mapping(not_null_type, the_origin, mapping.MAP_TO_WRAPPER_TYPE);
      }
    } else {
      if (is_object_type(var_type)) {
        type = make_object_type(the_origin);
      } else {
        type = make_type(var_type, the_origin);
      }
    }

    @Nullable construct init = the_variable.init_action() != null ?
        transform_and_maybe_rewrite(the_variable.init_action()) : null;
    return new variable_construct(annotations, type, the_variable.short_name(),
        new empty<annotation_construct>(), init, the_origin);
  }

  private boolean is_procedure_reference(action the_action) {
    if (the_action instanceof dispatch_action) {
      return is_procedure_declaration(((dispatch_action) the_action).get_declaration());
    }

    if (the_action instanceof value_action) {
      Object the_value = ((value_action) the_action).the_value;
      if (the_value instanceof base_data_value) {
        return is_procedure_declaration(((base_data_value) the_value).get_declaration());
      }
    }

    return false;
  }

  private boolean is_procedure_declaration(declaration the_declaration) {
    return the_declaration instanceof procedure_declaration &&
        !should_call_as_procedure(the_declaration);
  }

  private construct make_procedure_class(action the_action, origin the_origin) {
    assert is_procedure_reference(the_action);
    procedure_declaration the_procedure =
        (procedure_declaration) declaration_util.get_declaration(the_action);

    @Nullable principal_type old_enclosing_type = enclosing_type;
    enclosing_type = the_procedure.get_procedure_type().principal();

    construct procedure_type = make_type(the_procedure.get_procedure_type(), the_origin);
    construct new_construct = new operator_construct(operator.ALLOCATE, procedure_type, the_origin);
    construct with_parens = new parameter_construct(new_construct, new base_list<construct>(),
        grouping_type.PARENS, the_origin);

    readonly_list<annotation_construct> annotations = new base_list<annotation_construct>(
        new modifier_construct(override_modifier, the_origin),
        new modifier_construct(public_modifier, the_origin));

    list<construct> declaration_arguments = new base_list<construct>();
    list<construct> call_arguments = new base_list<construct>();
    readonly_list<type> argument_types = the_procedure.get_argument_types();

    for (int i = 0; i < argument_types.size(); ++i) {
      simple_name argument_name = name_utilities.make_numbered_name(i);
      type argument_type = argument_types.get(i);
      declaration_arguments.append(new variable_construct(
          new empty<annotation_construct>(),
          make_type_with_mapping(argument_type, the_origin, mapping.MAP_TO_WRAPPER_TYPE),
          argument_name, new empty<annotation_construct>(), null, the_origin));
      call_arguments.append(new name_construct(argument_name, the_origin));
    }

    list<construct> body = new base_list<construct>();
    construct body_call = new parameter_construct(transform_action(the_action), call_arguments,
        grouping_type.PARENS, the_origin);
    if (the_procedure.get_return_type() == library().immutable_void_type()) {
      // We need this because the return type is Void, not void
      body.append(body_call);
      body.append(new return_construct(make_null(the_origin), the_origin));
    } else {
      body.append(new return_construct(body_call, the_origin));
    }

    construct the_call = new procedure_construct(
        annotations,
        make_type_with_mapping(the_procedure.get_return_type(), the_origin,
            mapping.MAP_TO_WRAPPER_TYPE),
        CALL_NAME,
        declaration_arguments,
        new empty<annotation_construct>(),
        new block_construct(body, the_origin),
        the_origin);

    enclosing_type = old_enclosing_type;

    return new parameter_construct(with_parens, new base_list<construct>(the_call),
        grouping_type.BRACES, the_origin);
  }

  @Override
  public construct process_block(block_declaration the_block) {
    origin the_origin = the_block;
    return new block_construct(to_annotations(the_block.annotations(),
        annotation_category.BLOCK, true, the_origin),
        transform_action_list(the_block.get_body_action()), the_origin);
  }

  private boolean is_explicit_reference(action the_action) {
    // TODO: this needs to be fixed/cleaned up.
    declaration the_declaration = get_procedure_declaration(the_action);
    if (the_declaration instanceof variable_declaration) {
      variable_declaration the_variable = (variable_declaration) the_declaration;
      if (the_variable.reference_type().get_flavor() == mutable_flavor) {
        kind the_kind = the_variable.declared_in_type().get_kind();
        return !the_kind.is_namespace() && !is_concrete_kind(the_kind);
      }
    }
    if (the_declaration instanceof procedure_declaration) {
      procedure_declaration the_procedure = (procedure_declaration) the_declaration;
      type return_type = the_procedure.get_return_type();
      return library().is_reference_type(return_type) && return_type.get_flavor() == mutable_flavor;
    }
    return false;
  }

  private construct do_explicitly_derefence(construct the_construct, origin the_origin) {
    construct get_construct = new resolve_construct(the_construct, common_library.get_name,
        the_origin);
    return new parameter_construct(get_construct, new empty<construct>(), grouping_type.PARENS,
        the_origin);
  }

  private boolean is_procedure_with_no_body(@Nullable declaration the_declaration) {
    return the_declaration instanceof procedure_declaration &&
           ((procedure_declaration) the_declaration).get_body_action() == null;
  }

  private boolean is_object_type(type the_type) {
    principal_type the_principal_type = the_type.principal();
    return the_principal_type == library().value_type() ||
           the_principal_type == library().equality_comparable_type() ||
           the_principal_type == library().data_type();
  }

  @Override
  public construct process_type_parameter(type_parameter_declaration the_type_parameter) {
    origin the_origin = the_type_parameter;
    type type_bound = the_type_parameter.variable_type();
    if (!type_bound.is_subtype_of(library().value_type().get_flavored(any_flavor))) {
      utilities.panic("Type bound is not a value but " + type_bound);
    }
    @Nullable construct type_construct;
    if (is_object_type(type_bound)) {
      type_construct = null;
    } else {
      type_construct = make_type_with_mapping(type_bound, the_origin, mapping.MAP_TO_WRAPPER_TYPE);
    }
    return new variable_construct(new empty<annotation_construct>(), type_construct,
        the_type_parameter.short_name(), new empty<annotation_construct>(), null, the_origin);
  }

  private list<construct> transform_parameters(readonly_list<action> actions) {
    list<construct> result = new base_list<construct>();
    for (int i = 0; i < actions.size(); ++i) {
      result.append(transform_and_maybe_rewrite(actions.get(i)));
    }
    return result;
  }

  // TODO: better way to detect procedure variables?
  private boolean is_procedure_variable(@Nullable declaration the_declaration) {
    return the_declaration instanceof type_declaration &&
        library().is_reference_type(((type_declaration) the_declaration).get_declared_type());
  }

  private construct make_default_return(type the_type, boolean is_constructor, origin the_origin) {
    @Nullable construct return_value = is_constructor ? null :
        make_default_value(the_type, the_origin);
    return new return_construct(return_value, the_origin);
  }

  private construct make_default_value(type the_type, origin the_origin) {
    if (the_type == library().immutable_boolean_type()) {
      return new name_construct(library().false_value().short_name(), the_origin);
    } else {
      // TODO: handle other non-Object types
      return make_null(the_origin);
    }
  }

  public construct process_operator(bound_procedure the_bound_procedure, operator the_operator) {
    origin the_origin = the_bound_procedure;
    readonly_list<action> arguments = the_bound_procedure.parameters.params();

    // TODO: handle other assignment operators.
    if (the_operator == operator.ASSIGN) {
      action lhs = arguments.get(0);
      construct rhs = transform_and_maybe_rewrite(arguments.get(1));
      if (lhs instanceof bound_procedure) {
        bound_procedure lhs2 = (bound_procedure) lhs;
        readonly_list<action> plhs = lhs2.parameters.params();
        @Nullable declaration the_declaration = declaration_util.get_declaration(lhs2);
        if (the_declaration instanceof procedure_declaration) {
          procedure_declaration proc_decl = (procedure_declaration) the_declaration;
          if (proc_decl.annotations().has(implicit_modifier) && plhs.size() == 1) {
            construct main = transform_action(lhs2.the_procedure_action);
            readonly_list<construct> set_params =
                new base_list<construct>(transform_action(plhs.first()), rhs);
            construct set = new resolve_construct(main, SET_NAME, the_origin);
            return make_call(set, set_params, the_origin);
          }
        }
      }
      if (is_explicit_reference(lhs)) {
        construct main = transform_action(lhs);
        readonly_list<construct> set_params = new base_list<construct>(rhs);
        construct set = new resolve_construct(main, SET_NAME, the_origin);
        return make_call(set, set_params, the_origin);
      }
      // TODO: this should be generic...
      base_list<construct> arguments_constructs =
          new base_list<construct>(transform_action(lhs), rhs);
      return new operator_construct(operator.ASSIGN, arguments_constructs, the_origin);
    } else if (the_operator instanceof cast_type) {
      return transform_cast(arguments.get(0), get_type(arguments.get(1)), (cast_type) the_operator,
          the_origin);
    } else if (the_operator == operator.IS_OPERATOR) {
      return transform_is(false, arguments.get(0), get_type(arguments.get(1)), the_origin);
    } else if (the_operator == operator.IS_NOT_OPERATOR) {
      return transform_is(true, arguments.get(0), get_type(arguments.get(1)), the_origin);
    } else if (the_operator == operator.EQUAL_TO) {
      if (use_objects_equal(arguments)) {
        return objects_equal(arguments, the_origin);
      }
    } else if (the_operator == operator.NOT_EQUAL_TO) {
      if (use_objects_equal(arguments)) {
        return new operator_construct(operator.LOGICAL_NOT, objects_equal(arguments, the_origin),
            the_origin);
      }
      // TODO: convert into an equivalence function call if the argument is not a primitive.
    } else if (the_operator == operator.CONCATENATE) {
      if (!is_string_type(arguments.get(0)) || !is_string_type(arguments.get(1))) {
        construct concatenation = new resolve_construct(make_type(java_library.runtime_util_class(),
            the_origin), CONCATENATE_NAME, the_origin);
        return make_call(concatenation, transform_parameters(arguments), the_origin);
      }
    }
    /*else if (the_operator == operator.GENERAL_OR) {
      action the_action = get_action(the_parameter);
      if (the_action instanceof type_action && mapping_strategy == mapping.MAP_TO_WRAPPER_TYPE) {
        return make_type(remove_null_type(((type_action) the_action).get_type()), the_origin);
      }
      utilities.panic("Unexpected 'or' operator " + the_parameter);
    }*/

    return new operator_construct(map_operator(the_operator), transform_parameters(arguments),
        the_origin);
  }

  private boolean use_objects_equal(readonly_list<action> arguments) {
    assert arguments.size() == 2;
    boolean is_primitive =
        is_java_primitive(arguments.get(0)) || is_java_primitive(arguments.get(1));
    boolean is_reference_equality =
        is_reference_equality(arguments.get(0)) || is_reference_equality(arguments.get(1));

    return !is_primitive && !is_reference_equality;
  }

  private construct objects_equal(readonly_list<action> arguments, origin the_origin) {
    construct values_equal = new resolve_construct(make_type(java_library.runtime_util_class(),
        the_origin), OBJECTS_EQUAL_NAME, the_origin);
    return make_call(values_equal, transform_parameters(arguments), the_origin);
  }

  private construct int_to_string(construct int_value, origin the_origin) {
    construct to_string = new resolve_construct(make_type(java_library.runtime_util_class(),
        the_origin), INT_TO_STRING_NAME, the_origin);
    return make_call(to_string, new base_list<construct>(int_value), the_origin);
  }

  public construct transform_cast(action expression, type the_type, cast_type the_cast_type,
      origin the_origin) {
    type expression_type = result_type(expression);

    construct transformed_expression = transform_action(expression);
    construct transformed_type = make_type(the_type, the_origin);

    if (the_type == library().nonnegative_type() &&
        expression_type == library().immutable_integer_type()) {
      // we drop the integer -> nonnegative cast
      return transformed_expression;
    }

    principal_type expression_principal = expression_type.principal();
    principal_type type_principal = the_type.principal();

    if (expression_principal instanceof parametrized_type &&
        type_principal instanceof parametrized_type) {
      // && ((parametrized_type) expression_principal).get_master() ==
      //    ((parametrized_type) type_principal).get_master()
      // Note that with the above constraints, some Java generic casts
      // do not work. Rather than trying to figure out when exactly double casts
      // are necessary with generic types, we always introduce double casts.
      // There shouldn't be semantic problems with it, just minor overhead.
      master_type the_master = ((parametrized_type) type_principal).get_master();
      type intermediate_type = the_master.get_flavored(the_type.get_flavor());
      transformed_expression = new operator_construct(operator.HARD_CAST,
        new base_list<construct>(transformed_expression, make_type(intermediate_type, the_origin)),
            the_origin);
    }

    return new operator_construct(the_cast_type,
        new base_list<construct>(transformed_expression, transformed_type), the_origin);
  }

  public operator map_operator(operator the_operator) {
    if (the_operator == operator.CONCATENATE) {
      return operator.ADD;
    } else if (the_operator == operator.CONCATENATE_ASSIGN) {
      return operator.ADD_ASSIGN;
    } else {
      return the_operator;
    }
  }

  // TODO: make three methods below into a generic function.
  private boolean is_string_type(action the_action) {
    boolean result = result_type(the_action).principal() == java_library.string_type();
    if (result) {
      return true;
    }

    if (the_action instanceof promotion_action) {
      the_action = ((promotion_action) the_action).get_action();
      return result_type(the_action).principal() == java_library.string_type();
    }

    return false;
  }

  private boolean is_reference_equality(action the_action) {
    type reference_equality = library().reference_equality_type().get_flavored(any_flavor);
    boolean result = result_type(the_action).is_subtype_of(reference_equality);
    if (result) {
      return true;
    }

    if (the_action instanceof promotion_action) {
      the_action = ((promotion_action) the_action).get_action();
      return result_type(the_action).is_subtype_of(reference_equality);
    }

    return false;
  }

  public boolean is_mapped(principal_type the_type) {
    return the_type == library().boolean_type() ||
           the_type == library().character_type() ||
           the_type == library().void_type();
  }

  private boolean is_java_primitive(action the_action) {
    boolean result = is_mapped(result_type(the_action).principal());
    if (result) {
      return true;
    }

    if (the_action instanceof promotion_action) {
      the_action = ((promotion_action) the_action).get_action();
      return is_mapped(result_type(the_action).principal());
    }

    return false;
  }

  @Override
  public import_construct process_import(import_declaration the_import) {
    origin the_origin = the_import;
    list<annotation_construct> annotations = new base_list<annotation_construct>();
    if (the_import.is_implicit())  {
      annotations.append(new modifier_construct(implicit_modifier, the_origin));

      principal_type the_principal = the_import.get_type().principal();
      kind the_kind = the_principal.get_kind();
      if ((the_kind.is_namespace() || the_kind == type_kinds.class_kind) &&
          (the_principal.get_parent().get_kind() == type_kinds.package_kind)) {
        annotations.append(new modifier_construct(static_modifier, the_origin));
      }
    }

    return new import_construct(annotations,
        make_imported_type((principal_type) the_import.get_type(), the_origin), the_origin);
  }

  public construct process_loop_action(loop_action the_loop_action) {
    origin the_origin = the_loop_action;
    construct true_construct = new name_construct(library().true_value().short_name(), the_origin);
    return new while_construct(true_construct, transform_action(the_loop_action.get_body()),
        the_origin);
  }

  public construct process_return_action(return_action the_return) {
    origin the_origin = the_return;

    if (the_return.return_type.principal() == library().void_type()) {
      @Nullable procedure_declaration the_procedure = the_return.the_procedure;
      // We rewrite return constructs of procedures that return 'Void' with capital 'V'
      if (should_use_wrapper_in_return(the_procedure)) {
        return new return_construct(make_null(the_origin), the_origin);
      }
      return new return_construct(null, the_origin);
    }

    construct the_expression;
    if (library().is_reference_type(the_return.return_type)) {
      the_expression = transform_action(the_return.expression);
    } else {
      the_expression = transform_and_maybe_rewrite(the_return.expression);
    }

    return new return_construct(the_expression, the_origin);
  }

  public construct process_extension_action(extension_action the_extension_action) {
    extension_analyzer the_extension = the_extension_action.get_extension();

    if (the_extension instanceof grouping_analyzer) {
      return process_grouping((grouping_analyzer) the_extension);
    } else if (the_extension instanceof while_analyzer) {
      return process_while((while_analyzer) the_extension);
    } else if (the_extension instanceof for_analyzer) {
      return process_for((for_analyzer) the_extension);
    }

    return transform_action(the_extension_action.extended_action);
  }

  public construct process_variable_initializer(variable_initializer the_variable_initializer) {
    variable_declaration the_declaration =
        the_variable_initializer.the_variable_action.the_declaration;
    return process_variable(the_declaration);
  }

  public boolean is_nothing(action the_action) {
    if (the_action instanceof value_action) {
      Object value = ((value_action) the_action).the_value;
      return value == library().void_instance();
    }
    return false;
  }

  public @Nullable construct transform_or_null(action the_action) {
    if (is_nothing(the_action)) {
      return null;
    } else {
      return transform_action(the_action);
    }
  }

  protected boolean is_value(action the_action, value the_value) {
    return the_action instanceof base_value_action &&
        ((base_value_action) the_action).the_value == the_value;
  }

  public construct process_conditional_action(conditional_action the_conditional) {
    origin the_origin = the_conditional;
    if (is_value(the_conditional.else_action, library().false_value())) {
      return new operator_construct(operator.LOGICAL_AND,
          transform_action(the_conditional.condition),
          transform_action(the_conditional.then_action), the_origin);
    } else if (is_value(the_conditional.then_action, library().true_value())) {
      return new operator_construct(operator.LOGICAL_OR,
          transform_action(the_conditional.condition),
          transform_action(the_conditional.else_action), the_origin);
    }

    // TODO: infer is_statement from conditional_action
    boolean is_statement = true;
    construct the_construct = get_construct(the_conditional);
    if (the_construct instanceof conditional_construct) {
      is_statement = ((conditional_construct) the_construct).is_statement;
    }

    return new conditional_construct(transform_action(the_conditional.condition),
        transform_action(the_conditional.then_action),
        transform_or_null(the_conditional.else_action),
        is_statement, the_origin);
  }

  public construct process_list_action(list_action the_list_action) {
    utilities.panic("list_action: " + the_list_action);
    origin the_origin = the_list_action;
    return new block_construct(transform_parameters(the_list_action.elements()), the_origin);
  }

  public construct process_constraint_action(constraint_action the_constraint_action) {
    origin the_origin = the_constraint_action;
    return new constraint_construct(constraint_category.ASSERT_CONSTRAINT,
        transform_action(the_constraint_action.expression), the_origin);
  }

  public construct process_block_action(block_action the_block_action) {
    origin the_origin = the_block_action;
    return process_block(the_block_action.get_declaration());
  }

  private simple_name get_simple_name(construct c) {
    name_construct identifier = (name_construct) c;
    return (simple_name) identifier.the_name;
  }

  public readonly_list<construct> make_headers(type_declaration_construct the_declaration) {
    origin the_origin = the_declaration;
    list<construct> headers = new base_list<construct>();

    headers.append(make_comment(the_origin));
    headers.append_all(common_headers);
    boolean add_newline = false;

    if (deep_has(the_declaration, modifier_of_type(nullable_modifier))) {
      // TODO: kill empty line after common imports but before nullable import?
      headers.append(make_import(java_library.nullable_type(), the_origin));
      add_newline = true;
    }

    if (deep_has(the_declaration, modifier_of_type(dont_display_modifier))) {
      headers.append(make_import(java_library.dont_display_type(), the_origin));
      add_newline = true;
    }

    if (add_newline) {
      headers.append(make_newline(the_origin));
    }

    return headers;
  }

  private boolean deep_has(type_declaration_construct the_declaration,
      predicate<construct> the_predicate) {
    predicate<construct> deep_traverse = new predicate<construct>() {
      public @Override Boolean call(construct the_construct) {
        if (the_predicate.call(the_construct)) {
          return true;
        }

        return the_construct.children().has(this);
      }
    };

    return deep_traverse.call(the_declaration);
  }

  private predicate<construct> modifier_of_type(modifier_kind the_modifier_kind) {
    return new predicate<construct>() {
      public @Override Boolean call(construct the_construct) {
        return the_construct instanceof modifier_construct &&
            ((modifier_construct) the_construct).the_kind == the_modifier_kind;
      }
    };
  }

  private import_construct make_import(principal_type the_type, origin the_origin) {
    return new import_construct(new empty<annotation_construct>(), make_type(the_type, the_origin),
        the_origin);
  }

  private static construct make_call(construct main, readonly_list<construct> parameters,
      origin the_origin) {
    return new parameter_construct(main, parameters, grouping_type.PARENS, the_origin);
  }

  private static construct make_null(origin the_origin) {
    // TODO: use common_library.null_type
    return new name_construct(simple_name.make("null"), the_origin);
  }

  private static construct make_zero(origin the_origin) {
    return new literal_construct(new integer_literal(0), the_origin);
  }

  private static comment_construct make_comment(origin the_origin) {
    source_content src = origin_utilities.get_source(the_origin);
    string comment;
    if (src != null) {
      comment = new base_string("Autogenerated from ", src.name.to_string());
    } else {
      comment = new base_string("Autogenerated");
    }

    return new comment_construct(new comment(comment_type.LINE_COMMENT, comment,
        new base_string("// ", comment)), null, the_origin);
  }

  private static comment_construct make_newline(origin the_origin) {
    string newline = new base_string("\n");
    return new comment_construct(new comment(comment_type.NEWLINE, newline, newline), null,
        the_origin);
  }

  public final static base_flavor_profile class_profile =
    new base_flavor_profile(new base_string("class_profile"),
        new function1<type_flavor, type_flavor>() {
      @Override public type_flavor call(type_flavor first) {
        if (first == flavor.nameonly_flavor) {
          return first;
        } else {
          return flavor.DEFAULT_FLAVOR;
        }
      }
    });

  public construct process_analyzable_action(analyzable_action the_analyzable_action) {
    return transform_action(the_analyzable_action.get_action());
  }

  public construct base_string_wrap(construct the_construct, origin the_origin) {
    // TODO: handle both string and character literals correctly.
    // TODO: also, convert inline literals into constants.
    // TODO: use fully qualified type name?
    principal_type runtime_elements = java_library.runtime_elements_namespace();
    construct base_string_name;
    if (implicit_names.contains(runtime_elements)) {
      base_string_name = new name_construct(BASE_STRING_NAME, the_origin);
    } else {
      base_string_name = new resolve_construct(make_type(runtime_elements, the_origin),
          BASE_STRING_NAME, the_origin);
    }
    construct alloc = new operator_construct(operator.ALLOCATE, base_string_name, the_origin);
    return make_call(alloc, new base_list<construct>(the_construct), the_origin);
  }

  public construct process_value_action(base_value_action the_value_action) {
    //System.out.println("PV: " + the_value);
    origin the_origin = the_value_action;
    Object the_value = the_value_action.the_value;
    if (the_value instanceof singleton_value) {
      principal_type singleton_type = ((singleton_value) the_value).type_bound().principal();
      // Convert missing.instance to null literal
      if (is_null_subtype(singleton_type)) {
        return make_null(the_origin);
      }
      construct type_construct = make_type(singleton_type, the_origin);
      return new resolve_construct(type_construct, type_kinds.INSTANCE_NAME, the_origin);
    } else if (the_value instanceof integer_value) {
      integer_value the_integer_value = (integer_value) the_value;
      return new literal_construct(new integer_literal(the_integer_value.unwrap()), the_origin);
    } else if (the_value instanceof enum_value) {
      enum_value the_enum_value = (enum_value) the_value;
      if (the_enum_value.type_bound() == library().immutable_boolean_type()) {
        return new name_construct(the_enum_value.short_name(), the_origin);
      } else {
        return new resolve_construct(make_type(the_enum_value.type_bound(), the_origin),
            the_enum_value.short_name(), the_origin);
      }
    } else if (the_value instanceof string_value) {
      string_value the_string_value = (string_value) the_value;
      type the_type = the_value_action.result().type_bound();
      quote_type literal_type = (the_type == library().immutable_character_type()) ?
          punctuation.SINGLE_QUOTE : punctuation.DOUBLE_QUOTE;
      construct result = new literal_construct(new quoted_literal(the_string_value.unwrap(),
          literal_type), the_origin);
      if (the_type == library().immutable_string_type()) {
        result = base_string_wrap(result, the_origin);
      }
      return result;
    } else if (the_value instanceof base_procedure) {
      base_procedure the_procedure = (base_procedure) the_value;
      action_name the_name = map_name(the_procedure.name());
      procedure_declaration the_procedure_declaration =
          (procedure_declaration) the_procedure.get_declaration();
      if (the_procedure_declaration == null) {
        return new name_construct(the_name, the_origin);
      } else {
        return new resolve_construct(
            make_type(the_procedure_declaration.declared_in_type(), the_origin),
            the_name, the_origin);
      }
    } else if (the_value instanceof procedure_with_this) {
      procedure_with_this the_procedure_with_this = (procedure_with_this) the_value;
      action_name the_action_name = the_procedure_with_this.name();
      assert the_procedure_with_this.this_action != null;
      construct this_construct = process_action(the_procedure_with_this.this_action,
          the_origin);
      if (the_action_name == special_name.IMPLICIT_CALL) {
        return this_construct;
      } else {
        return new resolve_construct(this_construct, map_name(the_action_name), the_origin);
      }
    } else if (the_value instanceof loop_jump_wrapper) {
      loop_jump_wrapper the_loop_jump_wrapper = (loop_jump_wrapper) the_value;
      return new jump_construct(the_loop_jump_wrapper.the_jump_category, the_origin);
    }

    utilities.panic("processing value " + the_value.getClass() + ": " + the_value);
    return null;
  }

  @Override
  public construct transform_action(action the_action) {
    origin the_origin = the_action;
    return process_action(the_action, the_origin);
  }

  private construct transform_and_maybe_rewrite(action the_action) {
    origin the_origin = the_action;
    construct transformed = transform_action(the_action);
    if (is_explicit_reference(the_action)) {
      return do_explicitly_derefence(transformed, the_origin);
    } else if (is_procedure_reference(the_action)) {
      return make_procedure_class(the_action, the_origin);
    } else {
      return transformed;
    }
  }

  // This cleans up grouping_analyzers generated when processing please_construct.
  private @Nullable construct strip_grouping(action the_action) {
    if (the_action instanceof extension_action) {
      analyzable extension_analyzable = ((extension_action) the_action).get_extension();
      analyzable the_analyzable = extension_analyzable;
      while (the_analyzable instanceof grouping_analyzer) {
        the_analyzable = ((grouping_analyzer) the_analyzable).expression;
      }
      if (the_analyzable != extension_analyzable) {
        return transform_analyzable(the_analyzable);
      }
    }
    return null;
  }

  private void transform_and_append(action the_action, list<construct> result) {
    if (the_action instanceof list_action) {
      readonly_list<action> actions = ((list_action) the_action).elements();
      for (int i = 0; i < actions.size(); ++i) {
        action element_action = actions.get(i);
        @Nullable construct stripped_grouping = strip_grouping(element_action);
        if (stripped_grouping != null) {
          result.append(stripped_grouping);
        } else {
          transform_and_append(element_action, result);
        }
      }
    } else {
      result.append(transform_action(the_action));
    }
  }

  public list<construct> transform_action_list(action the_action) {
    list<construct> result = new base_list<construct>();
    transform_and_append(the_action, result);
    return result;
  }

  public construct process_action(action the_action, origin the_origin) {
    if (the_action instanceof type_action) {
      return make_type(((type_action) the_action).get_type(), the_origin);
    }

    if (the_action instanceof base_value_action) {
      return process_value_action((base_value_action) the_action);
    }

    if (the_action instanceof dereference_action) {
      dereference_action deref_action = (dereference_action) the_action;
      return process_action(deref_action.from, the_origin);
    }

    if (the_action instanceof promotion_action) {
      promotion_action the_promotion_action = (promotion_action) the_action;
      return process_action(the_promotion_action.get_action(), the_origin);
    }

    if (the_action instanceof narrow_action) {
      narrow_action the_narrow_action = (narrow_action) the_action;
      return process_narrow_action(the_narrow_action,
          process_action(the_narrow_action.expression, the_origin), the_origin);
    }

    if (the_action instanceof dispatch_action) {
      dispatch_action the_dispatch_action = (dispatch_action) the_action;
      action from_action = the_dispatch_action.get_from();
      assert from_action != null;
      construct from_construct = transform_and_maybe_rewrite(from_action);
      action primary = the_dispatch_action.get_primary();
      if (primary instanceof variable_action) {
        variable_action the_variable_action = (variable_action) primary;
        action_name the_name = map_name(the_variable_action.short_name());
        if (the_name == TO_STRING_NAME && from_action instanceof promotion_action) {
          principal_type from_type = ((promotion_action) from_action).get_action().result()
              .type_bound().principal();
          // TODO: implement less hacky way to implement Integer.to_string
          if (from_type == library().integer_type() || from_type == library().nonnegative_type()) {
            return int_to_string(from_construct, the_origin);
          }
        }
        construct result;
        if (from_construct == null) {
          result = new name_construct(the_name, the_origin);
        } else {
          result = new resolve_construct(from_construct, the_name, the_origin);
        }
        return maybe_call(the_variable_action, result, the_origin);
      } else if (primary instanceof value_action) {
        base_procedure the_procedure = (base_procedure) ((value_action) primary).the_value;
        action_name the_name = map_name(the_procedure.name());
        if (the_name != special_name.IMPLICIT_CALL) {
          if (from_construct == null) {
            return new name_construct(the_name, the_origin);
          } else {
            return new resolve_construct(from_construct, the_name, the_origin);
          }
        } else {
          return from_construct;
        }
      } else {
        utilities.panic("processing dispatch action, primary " + primary);
        return null;
      }
    }

    if (the_action instanceof variable_action) {
      variable_action the_variable_action = (variable_action) the_action;
      // TODO: handle from
      action_name the_name = map_name(the_variable_action.short_name());
      if (the_variable_action instanceof static_variable) {
        return new resolve_construct(
            make_type(the_variable_action.the_declaration.declared_in_type(), the_origin),
            the_name, the_origin);
      } else if (the_variable_action instanceof instance_variable) {
        assert the_variable_action.from != null;
        construct from = transform_and_maybe_rewrite(the_variable_action.from);
        construct result = new resolve_construct(from, the_name, the_origin);
        return maybe_call(the_variable_action, result, the_origin);
      } else if (the_variable_action instanceof local_variable &&
          the_variable_action.short_name() == special_name.THIS) {
        principal_type this_type = the_variable_action.value_type().principal();
        if (this_type != enclosing_type) {
          name_construct from = new name_construct(this_type.short_name(), the_origin);
          return new resolve_construct(from, the_name, the_origin);
        }
      }
      return new name_construct(the_name, the_origin);
    }

    if (the_action instanceof bound_procedure) {
      return process_bound_procedure((bound_procedure) the_action, the_origin);
    }

    if (the_action instanceof allocate_action) {
      allocate_action the_allocate_action = (allocate_action) the_action;
      return new operator_construct(operator.ALLOCATE,
          make_type(the_allocate_action.the_type, the_origin), the_origin);
    }

    if (the_action instanceof extension_action) {
      return process_extension_action((extension_action) the_action);
    }

    if (the_action instanceof cast_action) {
      return process_cast((cast_action) the_action, the_origin);
    }

    if (the_action instanceof list_initializer_action) {
      return process_list_initializer_action((list_initializer_action) the_action);
    }

    if (the_action instanceof is_action) {
      return process_is_operator((is_action) the_action, the_origin);
    }

    if (the_action instanceof return_action) {
      return process_return_action((return_action) the_action);
    }

    if (the_action instanceof variable_initializer) {
      return process_variable_initializer((variable_initializer) the_action);
    }

    if (the_action instanceof conditional_action) {
      return process_conditional_action((conditional_action) the_action);
    }

    if (the_action instanceof list_action) {
      return process_list_action((list_action) the_action);
    }

    if (the_action instanceof constraint_action) {
      return process_constraint_action((constraint_action) the_action);
    }

    if (the_action instanceof block_action) {
      return process_block_action((block_action) the_action);
    }

    if (the_action instanceof loop_action) {
      return process_loop_action((loop_action) the_action);
    }

    utilities.panic("processing action " + the_action.getClass() + ": " + the_action);
    return null;
  }

  private construct process_cast(cast_action the_cast_action, origin the_origin) {
    return transform_cast(the_cast_action.expression, the_cast_action.the_type,
        the_cast_action.the_cast_type, the_origin);
  }

  private construct process_is_operator(is_action the_is_action, origin the_origin) {
    return transform_is(the_is_action.negated, the_is_action.expression, the_is_action.the_type,
        the_origin);
  }

  private construct transform_is(boolean negated, action the_action, type the_type,
      origin the_origin) {
    construct expression = transform_action(the_action);

    if (the_type.principal() == library().null_type()) {
      return new operator_construct(negated ? operator.NOT_EQUAL_TO : operator.EQUAL_TO,
          expression, make_null(the_origin), the_origin);
    }

    if (the_type.principal() == library().nonnegative_type()) {
      // TODO: handle is_not
      assert !negated;
      return new operator_construct(operator.GREATER_EQUAL, expression, make_zero(the_origin),
          the_origin);
    }

    principal_type type_principal = the_type.principal();
    if (type_principal instanceof parametrized_type) {
      master_type the_master = ((parametrized_type) type_principal).get_master();
      the_type = the_master.get_flavored(the_type.get_flavor());
    }

    construct result = new operator_construct(operator.IS_OPERATOR, expression,
        make_type_with_mapping(the_type, the_origin, mapping.MAP_TO_WRAPPER_TYPE), the_origin);
    if (negated) {
      return new operator_construct(operator.LOGICAL_NOT,
          new list_construct(new base_list<construct>(result), grouping_type.PARENS, false,
              the_origin),
          the_origin);
    } else {
      return result;
    }
  }

  private declaration get_procedure_declaration(action the_procedure_action) {
    if (the_procedure_action instanceof value_action) {
      base_data_value the_value =
          (base_data_value) ((value_action) the_procedure_action).the_value;
      return the_value.get_declaration();
    } else if (the_procedure_action instanceof dispatch_action) {
      return get_procedure_declaration(((dispatch_action) the_procedure_action).get_primary());
    } else if (the_procedure_action instanceof promotion_action) {
      return get_procedure_declaration(((promotion_action) the_procedure_action).get_action());
    } else if (the_procedure_action instanceof dereference_action) {
      return get_procedure_declaration(((dereference_action) the_procedure_action).from);
    } else if (the_procedure_action instanceof variable_action) {
      return ((variable_action) the_procedure_action).get_declaration();
    } else if (the_procedure_action instanceof bound_procedure) {
      return ((bound_procedure) the_procedure_action).get_declaration();
    } else {
      //System.out.println("get_procedure_declaration: " + the_procedure_action.getClass());
      return null;
      //utilities.panic("get_procedure_declaration failure: " + the_procedure_action);
    }
  }

  private construct process_bound_procedure(bound_procedure the_bound_procedure,
      origin the_origin) {
    //System.out.println("\n===TBP: " + the_bound_procedure);
    action the_procedure_action = the_bound_procedure.the_procedure_action;
    construct main;
    procedure_value the_procedure = null;

    if (the_procedure_action instanceof value_action) {
      base_data_value the_value =
          (base_data_value) ((value_action) the_procedure_action).the_value;
      assert the_value instanceof procedure_value;
      the_procedure = (procedure_value) the_value;
      action_name the_name = the_procedure.name();
      if (the_name instanceof operator) {
        operator the_operator = (operator) the_name;
        return process_operator(the_bound_procedure, the_operator);
      }
      main = process_value_action((value_action) the_procedure_action);
    } else {
      main = process_action(the_procedure_action, the_origin);
    }

    readonly_list<construct> parameters =
        transform_parameters(the_bound_procedure.parameters.params());

    @Nullable declaration the_declaration = get_procedure_declaration(the_procedure_action);

    if (the_declaration instanceof procedure_declaration) {
      procedure_declaration proc_decl = (procedure_declaration) the_declaration;
      if (proc_decl.get_category() != procedure_category.CONSTRUCTOR &&
          proc_decl.annotations().has(implicit_modifier) &&
          parameters.size() == 1) {
        main = new resolve_construct(main, proc_decl.original_name(), the_origin);
      } else if (proc_decl.short_name() == special_name.IMPLICIT_CALL) {
        procedure_with_this the_procedure_with_this = (procedure_with_this) the_procedure;
        assert the_procedure_with_this != null;
        assert the_procedure_with_this.this_action != null;
        main = process_action(the_procedure_with_this.this_action, the_origin);
      }
    }

    // TODO: better way to detect procedure variables?
    if (is_procedure_variable(declaration_util.get_declaration(the_procedure_action))) {
      main = new resolve_construct(main, CALL_NAME, the_origin);
    }

    construct transformed = make_call(main, parameters, the_origin);

    type procedure_return_type = the_procedure_action.result().type_bound();
    if (action_utilities.is_procedure_type(procedure_return_type) &&
        action_utilities.get_procedure_return(procedure_return_type) ==
            core_types.unreachable_type()) {
      assert the_enclosing_procedure != null;
      type return_type = the_enclosing_procedure.get_return_type();
      if (return_type != core_types.unreachable_type() &&
          return_type != library().immutable_void_type()) {
        list<construct> statements = new base_list<construct>();
        statements.append(transformed);
        boolean is_constructor =
            the_enclosing_procedure.get_category() == procedure_category.CONSTRUCTOR;
        statements.append(make_default_return(return_type, is_constructor, the_origin));
        return new block_construct(statements, the_origin);
      }
    }

    return transformed;
  }

  public construct process_enum_value(enum_value_analyzer the_enum_value) {
    origin the_origin = the_enum_value;
    name_construct the_name = new name_construct(the_enum_value.short_name(), the_origin);
    if (the_enum_value.has_parameters()) {
      grouping_type grouping = grouping_type.PARENS;
      construct the_construct = get_construct(the_enum_value);
      if (the_construct instanceof parameter_construct) {
        grouping = ((parameter_construct) the_construct).grouping;
      }
      return new parameter_construct(the_name, 
          transform_parameters(the_enum_value.get_parameters().params()), grouping, the_origin);
    } else {
      return the_name;
    }
  }

  public construct process_grouping(grouping_analyzer the_grouping) {
    origin the_origin = the_grouping;
    return new list_construct(new base_list<construct>(
        transform_analyzable(the_grouping.expression)),
        grouping_type.PARENS, false, the_origin);
  }

  public construct process_while(while_analyzer the_while) {
    origin the_origin = the_while;
    return new while_construct(
        transform_analyzable(the_while.condition),
        transform_analyzable(the_while.body),
        the_origin);
  }

  public construct process_for(for_analyzer the_for) {
    origin the_origin = the_for;
    return new for_construct(
        transform_analyzable_or_null(the_for.init, the_origin),
        transform_analyzable_or_null(the_for.condition, the_origin),
        transform_analyzable_or_null(the_for.update, the_origin),
        transform_analyzable_or_null(the_for.body, the_origin),
        the_origin);
  }

  public construct process_supertype(supertype_declaration the_supertype) {
    utilities.panic("Unexpected supertype_declaration");
    return null;
  }
}
