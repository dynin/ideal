/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
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
import ideal.development.literals.*;

public class to_java_transformer extends base_transformer {

  private static enum mapping {
    MAP_TO_PRIMITIVE_TYPE,
    MAP_TO_WRAPPER_TYPE,
    NO_MAPPING;
  }

  private final java_library java_library;
  private final analysis_context context;
  private list<construct> common_headers;
  private set<principal_type> implicit_names;
  private set<principal_type> imported_names;
  private mapping mapping_strategy;
  private principal_type package_type;

  private static simple_name SET_NAME = simple_name.make("set");
  private static simple_name VALUE_NAME = simple_name.make("value");
  private static simple_name CALL_NAME = simple_name.make("call");

  private static simple_name OBJECTS_EQUAL_NAME = simple_name.make("values_equal");
  private static simple_name CONCATENATE_NAME = simple_name.make("concatenate");

  private static simple_name BASE_STRING_NAME = simple_name.make("base_string");
  private static simple_name LIST_NAME = simple_name.make("list");

  public to_java_transformer(java_library java_library, analysis_context context) {
    this.java_library = java_library;
    this.context = context;
    this.mapping_strategy = mapping.MAP_TO_PRIMITIVE_TYPE;

    common_headers = new base_list<construct>();

    implicit_names = new hash_set<principal_type>();
    implicit_names.add(core_types.root_type());
    implicit_names.add(java_library.lang_package());
    implicit_names.add(java_library.builtins_package());

    imported_names = new hash_set<principal_type>();
  }

  public void set_type_context(principal_type main_type, readonly_list<import_construct> imports,
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
        import_analyzer the_analyzable = (import_analyzer) context.get_analyzable(imports.get(i));
        import_construct the_import = process_import(the_analyzable);
        principal_type imported_type = (principal_type) the_analyzable.get_type();
        if (imported_type == java_library.builtins_package() ||
            imported_type.get_parent() == java_library.builtins_package()) {
          continue;
        }
        if (the_analyzable.is_implicit()) {
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

  protected common_library library() {
    return context.language().library();
  }

  @Override
  protected modifier_kind process_modifier(modifier_kind the_modifier_kind) {
    if (the_modifier_kind == implement_modifier) {
      return override_modifier;
    } else if (general_modifier.supported_by_java.contains(the_modifier_kind)) {
      return the_modifier_kind;
    } else {
      return null;
    }
  }

  protected action get_action(analyzable the_analyzable) {
    return analyzer_utilities.to_action(the_analyzable);
  }

  // TODO: detailed errors instead of asserts?
  protected type get_type(analyzable the_analyzable) {
    action the_action = get_action(the_analyzable);
    assert the_action instanceof type_action : "Action: " + the_action;
    return ((type_action) the_action).get_type();
  }

  private @Nullable declaration get_declaration(analyzable the_analyzable) {
    return declaration_util.get_declaration(get_action(the_analyzable));
  }

  /*
  @Override
  public Object process_name(name_construct c) {
    action the_action = get_action(c);
    origin the_origin = c;

    if (the_action instanceof type_action) {
      if (the_action.deeper_origin() instanceof type_parameter_analyzer) {
        type_parameter_analyzer type_parameter =
            (type_parameter_analyzer) the_action.deeper_origin();
        return new name_construct(type_parameter.short_name(), the_origin);
      }
      if (the_action.result() instanceof principal_type) {
        principal_type the_type = (principal_type) the_action.result();
        if (java_library.is_mapped(the_type)) {
          return make_type(the_type, the_origin);
        }
      }
    }

    construct result = maybe_call(c, c);

    if (the_action instanceof narrow_action) {
      narrow_action the_narrow_action = (narrow_action) the_action;
      type the_original_type = result_type(the_narrow_action.expression);
      type the_narrowed_type = the_narrow_action.the_type;
      if (should_introduce_cast(the_original_type, the_narrowed_type)) {
        result = new operator_construct(operator.AS_OPERATOR, result,
            make_type(the_narrowed_type, result_type), the_origin);
        result = new list_construct(new base_list<construct>(result), grouping_type.PARENS, false,
            the_origin);
      }
    }

    return result;
  }
  */

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

  private construct transform_with_mapping(analyzable_or_declaration the_analyzable,
      mapping new_mapping) {
    mapping old_mapping_strategy = mapping_strategy;
    mapping_strategy = new_mapping;
    construct result = transform(the_analyzable);
    mapping_strategy = old_mapping_strategy;
    return result;
  }

  private readonly_list<construct> transform_list_with_mapping(
      readonly_list<? extends analyzable_or_declaration> the_analyzables, mapping new_mapping) {
    mapping old_mapping_strategy = mapping_strategy;
    mapping_strategy = new_mapping;
    readonly_list<construct> result = transform_list(the_analyzables);
    mapping_strategy = old_mapping_strategy;
    return result;
  }

  @Override
  public construct process_list_initializer(list_initializer_analyzer initializer) {
    origin the_origin = initializer;
    type element_type = initializer.element_type;
    construct type_name = make_type(element_type, the_origin);
    construct alloc = new operator_construct(operator.ALLOCATE, type_name, the_origin);
    list_construct empty_brackets = new list_construct(new empty<construct>(),
        grouping_type.BRACKETS, false, the_origin);
    construct alloc_array = new parameter_construct(alloc, empty_brackets, the_origin);
    // TODO: handle promotions
    list_construct elements = new list_construct(transform_list(initializer.analyzable_parameters),
       grouping_type.BRACES, false, the_origin);
    construct alloc_call = new parameter_construct(alloc_array, elements, the_origin);

    construct array_name = make_type(java_library.array_class(), the_origin);
    list_construct array_params = new list_construct(new base_list<construct>(type_name),
        grouping_type.ANGLE_BRACKETS, false, the_origin);
    construct param_array = new parameter_construct(array_name, array_params, the_origin);
    construct alloc_array2 = new operator_construct(operator.ALLOCATE, param_array, the_origin);
    list_construct new_array_params = new list_construct(new base_list<construct>(alloc_call),
        grouping_type.PARENS, false, the_origin);
    construct array_call = new parameter_construct(alloc_array2, new_array_params, the_origin);

    // TODO: import type if needed
    // construct list_name = make_type(java_library.base_immutable_list_class(), the_origin);
    construct list_name = new name_construct(simple_name.make("base_immutable_list"), the_origin);
    list_construct list_params = new list_construct(new base_list<construct>(type_name),
        grouping_type.ANGLE_BRACKETS, false, the_origin);
    construct param_list = new parameter_construct(list_name, list_params, the_origin);
    construct alloc_list = new operator_construct(operator.ALLOCATE, param_list, the_origin);
    list_construct new_params = new list_construct(new base_list<construct>(array_call),
        grouping_type.PARENS, false, the_origin);
    return new parameter_construct(alloc_list, new_params, the_origin);
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

  @Override
  public construct process_resolve(resolve_analyzer the_resolve) {
    origin the_origin = the_resolve;

    action the_action = get_action(the_resolve);
    if (the_action instanceof type_action) {
      return make_type(((type_action) the_action).get_type(), the_origin);
    }

    type result_type = the_action.result().type_bound();

    // Convert missing.instance to null literal
    if (is_null_subtype(result_type)) {
      return make_null(the_origin);
    }

    action_name the_name = the_resolve.short_name();
    if (the_name == special_name.THIS_CONSTRUCTOR) {
      the_name = special_name.THIS;
    } else if (the_name == special_name.SUPER_CONSTRUCTOR) {
      the_name = special_name.SUPER;
    }

    construct result = new name_construct(the_name, the_origin);

    if (is_top_package(result_type)) {
      return result;
    }

    if (!the_resolve.has_from()) {
      result = maybe_call(the_resolve, result, the_origin);

      if (the_action instanceof narrow_action) {
        narrow_action the_narrow_action = (narrow_action) the_action;
        type the_original_type = result_type(the_narrow_action.expression);
        type the_narrowed_type = the_narrow_action.the_type;
        if (should_introduce_cast(the_original_type, the_narrowed_type)) {
          result = new operator_construct(operator.AS_OPERATOR, result,
              make_type(the_narrowed_type, the_origin), the_origin);
          result = new list_construct(new base_list<construct>(result), grouping_type.PARENS, false,
              the_origin);
        }
      }

      return result;
    }

    // Note: we do not transform the name!
    construct qualifier = transform_and_maybe_rewrite(the_resolve.get_from());

    if (the_resolve.short_name() == special_name.NEW) {
      return new operator_construct(operator.ALLOCATE, qualifier, the_origin);
    }

    return maybe_call(the_resolve, new resolve_construct(qualifier, result, the_origin),
        the_origin);
  }

  // TODO: should analyzable be resolve_analyzer?
  private construct maybe_call(analyzable the_analyzable, construct new_construct,
      origin the_origin) {
    @Nullable declaration the_declaration;
    if (the_analyzable instanceof resolve_analyzer) {
      resolve_analyzer the_resolve_analyzer = (resolve_analyzer) the_analyzable;
      the_declaration = declaration_util.get_declaration(the_resolve_analyzer.get_main_candidate());
    } else {
      the_declaration = get_declaration(the_analyzable);
    }
    if (the_declaration != null && should_call_as_procedure(the_declaration)) {
      return make_call(new_construct, new empty<construct>(), the_origin);
    } else {
      return new_construct;
    }
  }

  private boolean should_call_as_procedure(declaration the_declaration) {
    if (the_declaration instanceof variable_declaration) {
      variable_declaration the_variable = (variable_declaration) the_declaration;
      if (has_override(the_variable.annotations())) {
        return true;
      }
      kind parent_kind = the_variable.declared_in_type().get_kind();
      if (parent_kind != class_kind &&
          parent_kind != block_kind &&
          parent_kind != enum_kind &&
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

  @Override
  public construct process_flavor(flavor_analyzer the_flavor) {
    origin the_origin = the_flavor;
    type flavored_type = the_flavor.flavored_type;
    if (flavored_type.get_flavor() !=
        flavored_type.principal().get_flavor_profile().default_flavor()) {
      return make_type(flavored_type, the_origin);
    } else {
      return transform(the_flavor.expression);
    }
  }

  private construct make_parametrized_type(construct main, readonly_list<construct> parameters,
      origin the_origin) {
    return new parameter_construct(main,
        new list_construct(parameters, grouping_type.ANGLE_BRACKETS, false, the_origin),
        the_origin);
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
          super_procedure = ((specialized_procedure) super_procedure).get_main();
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
    kind the_kind = declared_in_type.get_kind();
    return the_kind != class_kind && the_kind != enum_kind;
  }

  @Override
  public construct process_procedure(procedure_declaration the_procedure) {
    origin the_origin = the_procedure;

    list<annotation_construct> annotations = to_annotations(the_procedure.annotations(),
        skip_access(the_procedure.declared_in_type()), the_origin);

    return process_procedure(the_procedure, annotations);
  }

  public construct process_procedure(procedure_declaration the_procedure,
      list<annotation_construct> annotations) {
    origin the_origin = the_procedure;

    if (the_procedure.annotations().has(not_yet_implemented_modifier)) {
      return null;
    }

    action_name name = the_procedure.original_name();
    @Nullable list_construct the_list_construct = process_parameters(
        the_procedure.get_parameter_variables(), the_origin);
    if (the_list_construct == null) {
      the_list_construct = make_parens(the_origin);
    }
    readonly_list<construct> parameters = the_list_construct.elements;
    @Nullable readonly_list<construct> body_statements = null;

    if (the_procedure.get_body() != null) {
      boolean is_constructor = the_procedure.get_category() == procedure_category.CONSTRUCTOR;
      boolean void_return = the_procedure.get_return_type().principal() == library().void_type();
      boolean unreachable_result =
          the_procedure.get_body_action().result().type_bound() == core_types.unreachable_type();

      boolean add_return = !is_constructor && !unreachable_result && void_return &&
          should_use_wrapper_in_return(the_procedure);

      list<construct> body = transform1(the_procedure.get_body());
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
    if (the_procedure.get_return() == null) {
      if (the_procedure.get_category() == procedure_category.CONSTRUCTOR) {
        ret = null;
      } else {
        ret = make_type(the_procedure.get_return_type(), the_origin);
      }
    } else {
      type return_type = the_procedure.get_return_type();
      if (type_utilities.is_union(return_type)) {
        annotations.append(make_nullable(the_origin));
        ret = make_type(remove_null_type(return_type), the_origin);
      } else if (library().is_reference_type(return_type)) {
        type_flavor ref_flavor = return_type.get_flavor();
        ret = transform(the_procedure.get_return());
        if (ref_flavor != mutable_flavor) {
          if (ret instanceof flavor_construct) {
            ret = ((flavor_construct) ret).expr;
          }
          assert ret instanceof parameter_construct;
          readonly_list<construct> ret_parameters = ((parameter_construct) ret).parameters.elements;
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
          ret = transform_with_mapping(the_procedure.get_return(), mapping.MAP_TO_WRAPPER_TYPE);
          // Note: if Java return type is 'Void' (with the capital V),
          // then we may need to insert "return null" to keep javac happy.
        } else {
          ret = transform(the_procedure.get_return());
        }
      }
    }

    construct body = null;
    if (body_statements != null) {
      body = new block_construct(body_statements, the_origin);
    }

    // Note: the flavor is always missing.
    return new procedure_construct(annotations, ret, name,
        new list_construct(parameters, grouping_type.PARENS, false, the_origin),
        new empty<annotation_construct>(), body, the_origin);
  }

  modifier_construct make_nullable(origin the_origin) {
    return new modifier_construct(nullable_modifier, the_origin);
  }

  protected type remove_null_type(type the_type) {
    assert type_utilities.is_union(the_type);
    type_flavor union_flavor = the_type.get_flavor();
    immutable_list<abstract_value> the_values = type_utilities.get_union_parameters(the_type);
    assert the_values.size() == 2;
    type null_type = (type) the_values.get(1);
    assert null_type.principal() == library().null_type();
    type result = (type) the_values.first();
    if (union_flavor != flavor.nameonly_flavor) {
      result = result.get_flavored(union_flavor);
    }
    return result;
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
    principal_type principal = the_type.principal();
    type_flavor the_flavor = the_type.get_flavor();

    switch (mapping_strategy) {
      case MAP_TO_PRIMITIVE_TYPE:
        @Nullable principal_type mapped = java_library.map_to_primitive(principal);
        if (mapped != null) {
          principal = mapped;
        }
        break;
      case MAP_TO_WRAPPER_TYPE:
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
      type removed_null = remove_null_type(principal);
      principal = removed_null.principal();
      the_flavor = removed_null.get_flavor();
    }

    construct name = new name_construct(make_name(get_simple_name(principal), principal,
        the_flavor), the_origin);
    name = make_full_name(name, principal, the_origin);

    if (principal instanceof parametrized_type) {
      immutable_list<abstract_value> type_params =
          ((parametrized_type) principal).get_parameters().internal_access();
      list<construct> params = new base_list<construct>();
      for (int i = 0; i < type_params.size(); ++i) {
        abstract_value av = type_params.get(i);
        assert av instanceof type;
        type the_param_type = (type) av;
        params.append(make_type_with_mapping(the_param_type, the_origin,
            mapping.MAP_TO_WRAPPER_TYPE));
      }
      return make_parametrized_type(name, params, the_origin);
    } else {
      return name;
    }
  }

  protected construct make_flavored_and_parametrized_type(principal_type principal,
      type_flavor flavor, @Nullable list_construct type_parameters, origin the_origin) {
    construct name = new name_construct(make_name(get_simple_name(principal), principal,
        flavor), the_origin);

    if (type_parameters != null) {
      list<construct> parameters = new base_list<construct>();
      for (int i = 0; i < type_parameters.elements.size(); ++i) {
        construct parameter = type_parameters.elements.get(i);
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
      the_type = remove_null_type(the_type).principal();
    }

    if (the_type.short_name() instanceof simple_name) {
      simple_name the_name = (simple_name) the_type.short_name();
      if (the_type.get_kind() == procedure_kind && the_type instanceof parametrized_type) {
        int arity = ((parametrized_type) the_type).get_parameters().internal_access().size() - 1;
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

  protected construct make_full_name(construct name, principal_type the_type, origin the_origin) {
    if (imported_names.contains(the_type)) {
      return name;
    }

    if (the_type instanceof parametrized_type) {
      master_type the_master_type = ((parametrized_type) the_type).get_master();
      if (imported_names.contains(the_master_type)) {
        return name;
      }
    }

    if (the_type.get_declaration() instanceof type_parameter_declaration) {
      return name;
    }

    if (is_top_package(the_type)) {
      return name;
    }

    @Nullable principal_type parent = the_type.get_parent();
    if (parent == null || implicit_names.contains(parent)) {
      return name;
    }

    if (! (parent.short_name() instanceof simple_name)) {
      utilities.panic("Full name of " + the_type + ", parent " + parent);
    }

    construct parent_name = new name_construct(parent.short_name(), the_origin);
    return make_full_name(new resolve_construct(parent_name, name, the_origin), parent, the_origin);
  }

  protected construct make_imported_type(principal_type the_type, origin the_origin) {
    construct name = new name_construct(get_simple_name(the_type), the_origin);
    name = make_imported_full_name(name, the_type, the_origin);

    return name;
  }

  protected construct make_imported_full_name(construct name, principal_type the_type,
      origin the_origin) {
    @Nullable principal_type parent = the_type.get_parent();
    if (parent == null) {
      return name;
    }

    if (! (parent.short_name() instanceof simple_name)) {
      utilities.panic("Full name of " + the_type + ", parent " + parent);
    }

    construct parent_name = new name_construct(parent.short_name(), the_origin);
    return make_full_name(new resolve_construct(parent_name, name, the_origin), parent, the_origin);
  }

  @Override
  protected simple_name make_name(simple_name type_name, principal_type the_type,
      type_flavor flavor) {
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
            false, the_origin);
        append_static(annotations, the_origin);
        // TODO: do we need to handle null?
        result.append(process_variable(the_variable, annotations));
      } else if (decl instanceof procedure_declaration) {
        procedure_declaration the_procedure = (procedure_declaration) decl;
        list<annotation_construct> annotations = to_annotations(the_procedure.annotations(),
            false, the_origin);
        if (the_procedure.get_category() != procedure_category.CONSTRUCTOR) {
          append_static(annotations, the_origin);
        }
        result.append(process_procedure(the_procedure, annotations));
      } else if (decl instanceof block_analyzer) {
        // TODO: make sure this is a static block...
        result.append_all(transform1(decl));
      } else if (decl instanceof import_analyzer) {
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

  @Override
  public Object process_type(type_declaration the_type_declaration) {
    origin the_origin = the_type_declaration;
    list<annotation_construct> annotations = to_annotations(
        the_type_declaration.annotations(), false, the_origin);

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

    if (declared_in_type.get_kind() == class_kind) {
      // Introduce inner type.
      append_static(annotations, the_origin);
    }

    boolean concrete_mode = is_concrete_kind(the_kind);
    flavor_profile profile = the_kind == class_kind ? class_profile :
        declared_type.get_flavor_profile();

    // TODO: implement namespaces
    assert profile != flavor_profiles.nameonly_profile;

    simple_name type_name = (simple_name) the_type_declaration.short_name();

    @Nullable list_construct type_parameters = null;

    if (the_type_declaration.get_parameters() != null) {
      type_parameters = new list_construct(
          transform_list_with_mapping(the_type_declaration.get_parameters(),
              mapping.MAP_TO_WRAPPER_TYPE),
          grouping_type.ANGLE_BRACKETS, false, the_origin);
    }

    dictionary<type_flavor, list<construct>> flavored_bodies =
        new list_dictionary<type_flavor, list<construct>>();
    dictionary<type_flavor, list<construct>> supertype_lists =
        new list_dictionary<type_flavor, list<construct>>();
    @Nullable construct superclass = null;

    immutable_list<type_flavor> supported_flavors = profile.supported_flavors();
    for (int i = 0; i < supported_flavors.size(); ++i) {
      type_flavor flavor = supported_flavors.get(i);
      flavored_bodies.put(flavor, new base_list<construct>());
      supertype_lists.put(flavor, new base_list<construct>());
    }

    if (concrete_mode) {
      @Nullable procedure_construct run_tests =
          testcase_generator.process_testcases(the_type_declaration);
      if (run_tests != null) {
        flavored_bodies.get(profile.default_flavor()).append(run_tests);
      }
    }

    readonly_list<declaration> body = the_type_declaration.get_signature();

    for (int i = 0; i < body.size(); ++i) {
      declaration decl = body.get(i);
      if (decl instanceof supertype_declaration) {
        supertype_declaration supertype_decl = (supertype_declaration) decl;
        // TODO: retire this when there is no need to match legacy output
        if (supertype_decl.annotations().has(synthetic_modifier)) {
          continue;
        }
        type supertype = supertype_decl.get_supertype();
        kind supertype_kind = supertype.principal().get_kind();
        if (concrete_mode) {
          construct transformed_supertype = transform_with_mapping(
              supertype_decl.supertype_analyzable(), mapping.NO_MAPPING);
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
                  flavored_supertype = transform_with_mapping(supertype_decl.supertype_analyzable(),
                      mapping.MAP_TO_WRAPPER_TYPE);
                } else {
                  flavored_supertype = make_type_with_mapping(supertype.get_flavored(flavor),
                      the_origin2, mapping.MAP_TO_WRAPPER_TYPE);
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
            construct flavored_supertype = transform_with_mapping(
                supertype_decl.supertype_analyzable(), mapping.MAP_TO_WRAPPER_TYPE);
            list<construct> supertype_list = supertype_lists.get(profile.map(subtype_flavor));
            assert supertype_list != null;
            supertype_list.append(flavored_supertype);
          }
        }
      } else if (decl instanceof procedure_declaration) {
        procedure_declaration proc_decl = (procedure_declaration) decl;
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
      } else if (decl instanceof import_analyzer) {
        // Skip imports: they should have been declared at the top level.
      } else if (decl instanceof block_declaration) {
        // TODO: make sure this is a static block...
        flavored_bodies.get(profile.default_flavor()).append_all(transform1(decl));
      } else {
        utilities.panic("Unknown declaration: " + decl);
      }
    }

    list<construct> type_decls = new base_list<construct>();

    immutable_list<type_flavor> type_flavors = profile.supported_flavors();
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

    return type_decls;
  }

  private static boolean is_readonly_flavor(type_flavor the_flavor) {
    return the_flavor == readonly_flavor ||
           the_flavor == immutable_flavor ||
           the_flavor == deeply_immutable_flavor;
  }

  private static boolean is_concrete_kind(kind the_kind) {
    return the_kind == class_kind || the_kind == enum_kind;
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
    return new procedure_construct(annotations,
        make_type(return_type, the_origin),
        the_variable.short_name(),
        make_parens(the_origin),
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
          new list_construct(supertype_parameters, grouping_type.ANGLE_BRACKETS, false, the_origin),
          the_origin);
      extends_types.append(superprocedure);
    }

    readonly_list<declaration> body = the_type_declaration.get_signature();
    for (int i = 0; i < body.size(); ++i) {
      declaration decl = body.get(i);
      if (decl instanceof supertype_declaration) {
        supertype_declaration supertype_decl = (supertype_declaration) decl;
        type supertype = supertype_decl.get_supertype();
        if (supertype.principal().get_kind() != procedure_kind) {
          extends_types.append(transform(supertype_decl.supertype_analyzable()));
        }
      }
    }

    list<construct> type_body = new base_list<construct>();
    type_body.append(new supertype_construct(new empty<annotation_construct>(), null,
        extends_tag, extends_types, the_origin));

    if (!is_function) {
      type_body.append(new procedure_construct(empty_annotations, return_construct,
          CALL_NAME, new list_construct(call_parameters, grouping_type.PARENS, false, the_origin),
          empty_annotations, null, the_origin));
    }

    simple_name type_name = make_procedure_name(is_function, arity);
    return new type_declaration_construct(annotations, interface_kind, type_name,
        new list_construct(type_parameters, grouping_type.ANGLE_BRACKETS, false, the_origin),
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
    origin the_origin = the_variable;

    principal_type declared_in_type = the_variable.declared_in_type();
    list<annotation_construct> annotations = to_annotations(the_variable.annotations(),
        skip_access(declared_in_type), the_origin);

    return process_variable(the_variable, annotations);
  }

  public construct process_variable(variable_declaration the_variable,
      list<annotation_construct> annotations) {
    origin the_origin = the_variable;

    if (the_variable.annotations().has(not_yet_implemented_modifier)) {
      return null;
    }

    principal_type declared_in_type = the_variable.declared_in_type();

    boolean is_mutable = the_variable.reference_type().get_flavor() == mutable_flavor;
    if (!is_mutable &&
        !the_variable.annotations().has(final_modifier) &&
        !is_procedure_with_no_body(declared_in_type.get_declaration())) {
      annotations.append(new modifier_construct(final_modifier, the_origin));
    }

    type var_type = the_variable.value_type();
    construct type;
    if (the_variable.get_type_analyzable() == null) {
      if (type_utilities.is_union(var_type)) {
        annotations.append(make_nullable(the_origin));
        type not_null_type = remove_null_type(var_type);
        type = make_type_with_mapping(not_null_type, the_origin, mapping.MAP_TO_WRAPPER_TYPE);
      } else {
        type = make_type(var_type, the_origin);
      }
    } else if (type_utilities.is_union(var_type)) {
      annotations.append(make_nullable(the_origin));
      type = make_type(remove_null_type(var_type), the_origin);
    } else {
      type = transform(the_variable.get_type_analyzable());
    }

    @Nullable construct init = the_variable.initializer() != null ?
        transform_and_maybe_rewrite(the_variable.initializer()) : null;
    return new variable_construct(annotations, type, the_variable.short_name(),
        new empty<annotation_construct>(), init, the_origin);
  }

  private boolean is_procedure_reference(analyzable the_analyzable) {
    if (the_analyzable instanceof resolve_analyzer) {
      declaration the_declaration = get_declaration(the_analyzable);
      return the_declaration instanceof procedure_declaration &&
          !should_call_as_procedure(the_declaration);
    } else {
      return false;
    }
  }

  private construct make_procedure_class(analyzable the_procedure_analyzable, origin the_origin) {
    assert is_procedure_reference(the_procedure_analyzable);
    procedure_declaration the_procedure =
        (procedure_declaration) get_declaration(the_procedure_analyzable);

    construct procedure_type = make_type(the_procedure.get_procedure_type(), the_origin);
    construct new_construct = new operator_construct(operator.ALLOCATE, procedure_type, the_origin);
    construct with_parens = new parameter_construct(new_construct, make_parens(the_origin),
        the_origin);

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
          new empty<annotation_construct>(), make_type(argument_type, the_origin),
          argument_name, new empty<annotation_construct>(), null, the_origin));
      call_arguments.append(new name_construct(argument_name, the_origin));
    }

    construct the_call_body = new return_construct(
        new parameter_construct(
            transform(the_procedure_analyzable),
            new list_construct(call_arguments, grouping_type.PARENS, false, the_origin),
            the_origin),
        the_origin);

    construct the_call = new procedure_construct(
        annotations,
        make_type(the_procedure.get_return_type(), the_origin),
        CALL_NAME,
        new list_construct(declaration_arguments, grouping_type.PARENS, false, the_origin),
        new empty<annotation_construct>(),
        new block_construct(new base_list<construct>(the_call_body), the_origin),
        the_origin);

    return new parameter_construct(with_parens,
        new list_construct(new base_list<construct>(the_call), grouping_type.BRACES, false,
            the_origin), the_origin);
  }

  private boolean is_explicit_reference(analyzable the_analyzable) {
    // TODO: this needs to be fixed/cleaned up.
    declaration the_declaration = get_declaration(the_analyzable);
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
    construct get_construct = new resolve_construct(the_construct,
        new name_construct(common_library.get_name, the_origin), the_origin);
    return new parameter_construct(get_construct,
        new list_construct(new empty<construct>(), grouping_type.PARENS, false, the_origin),
        the_origin);
  }

  private boolean is_procedure_with_no_body(@Nullable declaration the_declaration) {
    return the_declaration instanceof procedure_declaration &&
           ((procedure_declaration) the_declaration).get_body_action() == null;
  }

  private boolean should_omit_type_bound(principal_type the_type) {
    return the_type == library().value_type() || the_type == library().equality_comparable_type();
  }

  @Override
  public construct process_type_parameter(type_parameter_declaration the_type_parameter) {
    origin the_origin = the_type_parameter;
    type type_bound = the_type_parameter.variable_type();
    if (!type_bound.is_subtype_of(library().value_type().get_flavored(any_flavor))) {
      utilities.panic("Type bound is not a value but " + type_bound);
    }
    @Nullable construct type_construct;
    if (should_omit_type_bound(type_bound.principal()) ||
        the_type_parameter.get_type_analyzable() == null) {
      type_construct = null;
    } else {
      type_construct = transform_with_mapping(the_type_parameter.get_type_analyzable(),
          mapping.MAP_TO_WRAPPER_TYPE);
    }
    return new variable_construct(new empty<annotation_construct>(), type_construct,
        the_type_parameter.short_name(), new empty<annotation_construct>(), null, the_origin);
  }

  private construct transform_and_maybe_rewrite(analyzable the_analyzable) {
    origin the_origin = the_analyzable;
    construct transformed = transform(the_analyzable);
    if (is_explicit_reference(the_analyzable)) {
      return do_explicitly_derefence(transformed, the_origin);
    } else if (is_procedure_reference(the_analyzable)) {
      return make_procedure_class(the_analyzable, the_origin);
    } else {
      return transformed;
    }
  }

  private list<construct> transform_parameters(readonly_list<analyzable> analyzables) {
    list<construct> result = new base_list<construct>();
    for (int i = 0; i < analyzables.size(); ++i) {
      result.append(transform_and_maybe_rewrite(analyzables.get(i)));
    }
    return result;
  }

  @Override
  public Object process_parameter(parameter_analyzer the_parameter) {
    origin the_origin = the_parameter;

    analyzable main_analyzable = the_parameter.main_analyzable;
    if (main_analyzable instanceof resolve_analyzer) {
      resolve_analyzer the_resolve_analyzer = (resolve_analyzer) main_analyzable;
      if (the_resolve_analyzer.short_name() instanceof operator) {
        return process_operator(the_parameter, (operator) the_resolve_analyzer.short_name(),
            the_parameter.analyzable_parameters);
      }
    }

    action the_action = get_action(the_parameter);
    boolean is_type = the_action instanceof type_action;
    construct main = transform(main_analyzable);
    readonly_list<construct> parameters;

    if (is_type) {
      parameters = transform_list_with_mapping(the_parameter.analyzable_parameters,
          mapping.MAP_TO_WRAPPER_TYPE);
      action main_action = get_action(main_analyzable);
      if (main_action.result().type_bound().principal().get_kind() == procedure_kind) {
        // TODO: handle resolve_construct here.
        name_construct procedure_type_name = (name_construct) get_construct(main_analyzable);
        simple_name base_name = (simple_name) procedure_type_name.the_name;
        int arity = parameters.size() - 1;
        main = new name_construct(make_procedure_name(base_name, arity), the_origin);
      }
    } else {
      parameters = transform_parameters(the_parameter.analyzable_parameters);
      @Nullable declaration the_declaration = get_declaration(the_parameter);
      if (the_declaration instanceof procedure_declaration) {
        procedure_declaration proc_decl = (procedure_declaration) the_declaration;
        if (proc_decl.get_category() != procedure_category.CONSTRUCTOR &&
            proc_decl.annotations().has(implicit_modifier) && parameters.size() == 1) {
          main = new resolve_construct(main, new name_construct(proc_decl.original_name(),
              the_origin), the_origin);
        }
      }
      // TODO: better way to detect procedure variables?
      if (is_procedure_variable(the_declaration)) {
        main = new resolve_construct(main, new name_construct(CALL_NAME, the_origin), the_origin);
      }
    }

    parameter_construct transformed = new parameter_construct(main,
        new list_construct(parameters,
            is_type ? grouping_type.ANGLE_BRACKETS : grouping_type.PARENS, false, the_origin),
            the_origin);
    if (the_action.result() == core_types.unreachable_type()) {
      @Nullable procedure_declaration the_procedure =
          analyzer_utilities.get_enclosing_procedure(the_parameter);
      assert the_procedure != null;
      type return_type = the_procedure.get_return_type();
      if (return_type != core_types.unreachable_type() &&
          return_type != library().immutable_void_type()) {
        list<construct> result = new base_list<construct>();
        result.append(transformed);
        result.append(make_default_return(return_type, the_origin));
        return result;
      }
    }

    return transformed;
  }

  // TODO: better way to detect procedure variables?
  private boolean is_procedure_variable(@Nullable declaration the_declaration) {
    return the_declaration instanceof type_declaration &&
        library().is_reference_type(((type_declaration) the_declaration).get_declared_type());
  }

  private construct make_default_return(type the_type, origin the_origin) {
    return new return_construct(make_default_value(the_type, the_origin), the_origin);
  }

  private construct make_default_value(type the_type, origin the_origin) {
    if (the_type == library().immutable_boolean_type()) {
      return new name_construct(library().false_value().short_name(), the_origin);
    } else {
      // TODO: handle other non-Object types
      return make_null(the_origin);
    }
  }

  public construct process_operator(parameter_analyzer the_parameter,
      operator the_operator, readonly_list<analyzable> arguments) {
    origin the_origin = the_parameter;

    // TODO: handle other assignment operators.
    if (the_operator == operator.ASSIGN) {
      analyzable lhs = arguments.get(0);
      construct rhs = transform_and_maybe_rewrite(arguments.get(1));
      if (lhs instanceof parameter_analyzer) {
        parameter_analyzer lhs2 = (parameter_analyzer) lhs;
        readonly_list<analyzable> plhs = lhs2.analyzable_parameters;
        @Nullable declaration the_declaration = get_declaration(lhs2);
        if (the_declaration instanceof procedure_declaration) {
          procedure_declaration proc_decl = (procedure_declaration) the_declaration;
          if (proc_decl.annotations().has(implicit_modifier) && plhs.size() == 1) {
            construct main = transform(lhs2.main_analyzable);
            readonly_list<construct> set_params =
                new base_list<construct>(transform(plhs.first()), rhs);
            construct set = new resolve_construct(main, new name_construct(SET_NAME, the_origin),
                the_origin);
            return make_call(set, set_params, the_origin);
          }
        }
      }
      if (is_explicit_reference(lhs)) {
        construct main = transform(lhs);
        readonly_list<construct> set_params = new base_list<construct>(rhs);
        construct set = new resolve_construct(main, new name_construct(SET_NAME, the_origin),
            the_origin);
        return make_call(set, set_params, the_origin);
      }
      // TODO: this should be generic...
      base_list<construct> arguments_constructs = new base_list<construct>(transform(lhs), rhs);
      return new operator_construct(operator.ASSIGN, arguments_constructs, the_origin);
    } else if (the_operator == operator.AS_OPERATOR) {
      return transform_cast(arguments.get(0), arguments.get(1), the_origin);
    } else if (the_operator == operator.IS_OPERATOR) {
      construct expression = transform(arguments.get(0));
      type the_type = get_type(arguments.get(1));
      if (the_type.principal() == library().null_type()) {
        return new operator_construct(operator.EQUAL_TO, expression, make_null(the_origin),
            the_origin);
      } else if (the_type.principal() == library().nonnegative_type()) {
        return new operator_construct(operator.GREATER_EQUAL, expression, make_zero(the_origin),
            the_origin);
      }
    } else if (the_operator == operator.IS_NOT_OPERATOR) {
      construct expression = transform(arguments.get(0));
      type the_type = get_type(arguments.get(1));

      if (the_type.principal() == library().null_type()) {
        return new operator_construct(operator.NOT_EQUAL_TO, expression, make_null(the_origin),
            the_origin);
      }
      // TODO: convert is_not to instanceof
    } else if (the_operator == operator.EQUAL_TO) {
      type first_type = result_type(get_action(arguments.get(0)));
      type second_type = result_type(get_action(arguments.get(1)));

      boolean is_primitive = java_library.is_mapped(first_type.principal()) ||
          java_library.is_mapped(second_type.principal());
      boolean is_reference_equality =
          first_type.is_subtype_of(library().reference_equality_type().get_flavored(any_flavor)) ||
          second_type.is_subtype_of(library().reference_equality_type().get_flavored(any_flavor));

      if (!is_primitive && !is_reference_equality) {
        construct values_equal = new resolve_construct(make_type(java_library.runtime_util_class(),
            the_origin), new name_construct(OBJECTS_EQUAL_NAME, the_origin), the_origin);
        return make_call(values_equal, transform_parameters(arguments), the_origin);
      }
    } else if (the_operator == operator.NOT_EQUAL_TO) {
      // TODO: convert into an equivalence function call if the argument is not a primitive.
    } else if (the_operator == operator.CONCATENATE) {
      if (!is_string_type(arguments.get(0)) || !is_string_type(arguments.get(1))) {
        construct concatenation = new resolve_construct(make_type(java_library.runtime_util_class(),
            the_origin), new name_construct(CONCATENATE_NAME, the_origin), the_origin);
        return make_call(concatenation, transform_list(arguments), the_origin);
      }
    }
    else if (the_operator == operator.GENERAL_OR) {
      action the_action = get_action(the_parameter);
      if (the_action instanceof type_action && mapping_strategy == mapping.MAP_TO_WRAPPER_TYPE) {
        return make_type(remove_null_type(((type_action) the_action).get_type()), the_origin);
      }
      utilities.panic("Unexpected 'or' operator " + the_parameter);
    }

    return new operator_construct(map_operator(the_operator), transform_list(arguments),
        the_origin);
  }

  public construct transform_cast(analyzable expression, analyzable type_analyzable,
      origin the_origin) {
    type expression_type = result_type(get_action(expression));
    type the_type = get_type(type_analyzable);

    construct transformed_expression = transform(expression);
    construct transformed_type = transform(type_analyzable);

    if (the_type == library().nonnegative_type() &&
        expression_type == library().immutable_integer_type()) {
      // we drop the integer -> nonnegative cast
      return transformed_expression;
    }

    principal_type expression_principal = expression_type.principal();
    principal_type type_principal = the_type.principal();

    if (expression_principal instanceof parametrized_type &&
        type_principal instanceof parametrized_type &&
        ((parametrized_type) expression_principal).get_master() ==
            ((parametrized_type) type_principal).get_master()) {
      master_type the_master = ((parametrized_type) type_principal).get_master();
      type intermediate_type = the_master.get_flavored(the_type.get_flavor());
      transformed_expression = new operator_construct(operator.AS_OPERATOR,
        new base_list<construct>(transformed_expression, make_type(intermediate_type, the_origin)),
            the_origin);
    }

    return new operator_construct(operator.AS_OPERATOR,
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

  private boolean is_string_type(analyzable the_analyzable) {
    type the_type = result_type(get_action(the_analyzable));
    return the_type.principal() == java_library.string_type();
  }

  @Override
  public import_construct process_import(import_analyzer the_import) {
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

  @Override
  public construct process_loop(loop_analyzer the_loop) {
    origin the_origin = the_loop;
    construct true_construct = new name_construct(library().true_value().short_name(), the_origin);
    return new while_construct(true_construct, transform(the_loop.body), the_origin);
  }

  @Override
  public construct process_literal(literal_analyzer the_literal_analyzer) {
    origin the_origin = the_literal_analyzer;
    literal the_literal = the_literal_analyzer.the_literal;
    literal_construct the_literal_construct = new literal_construct(the_literal, the_origin);

    if (the_literal instanceof quoted_literal &&
        get_action(the_literal_analyzer).result().type_bound() == 
            library().immutable_string_type()) {
      // TODO: handle both string and character literals correctly.
      // TODO: also, convert inline literals into constants.
      // TODO: use fully qualified type name?
      construct type_name = new name_construct(BASE_STRING_NAME, the_origin);
      construct alloc = new operator_construct(operator.ALLOCATE, type_name, the_origin);
      return make_call(alloc, new base_list<construct>(the_literal_construct), the_origin);
    }

    return the_literal_construct;
  }

  @Override
  public construct process_return(return_analyzer the_return) {
    origin the_origin = the_return;

    if (the_return.the_expression == null ||
        the_return.return_type().principal() == library().void_type()) {
      @Nullable procedure_declaration the_procedure = the_return.the_procedure;
      // We rewrite return constructs of procedures that return 'Void' with capital 'V'
      if (the_procedure != null && should_use_wrapper_in_return(the_procedure)) {
        return new return_construct(make_null(the_origin), the_origin);
      }
      return new return_construct(null, the_origin);
    }

    construct the_expression;
    if (library().is_reference_type(the_return.return_type())) {
      the_expression = transform(the_return.the_expression);
    } else {
      the_expression = transform_and_maybe_rewrite(the_return.the_expression);
    }

    return new return_construct(the_expression, the_origin);
  }

  /*
  @Override
  public Object process_extension(extension_construct the_construct) {
    if (the_construct instanceof please_construct) {
      // Java doesn't support good manners.
      return transform(((please_construct) the_construct).the_statement);
    }

    if (the_construct instanceof list_iteration_construct) {
      return rewrite_list_iteration((list_iteration_construct) the_construct);
    }

    return super.process_extension(the_construct);
  }
  */

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

    if (the_declaration.has(modifier_of_type(nullable_modifier))) {
      // TODO: kill empty line after common imports but before nullable import?
      headers.append(make_import(java_library.nullable_type(), the_origin));
      add_newline = true;
    }

    if (the_declaration.has(modifier_of_type(dont_display_modifier))) {
      headers.append(make_import(java_library.dont_display_type(), the_origin));
      add_newline = true;
    }

    if (add_newline) {
      headers.append(make_newline(the_origin));
    }

    return headers;
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
    return new parameter_construct(main,
        new list_construct(parameters, grouping_type.PARENS, false, the_origin), the_origin);
  }

  private static construct make_null(origin the_origin) {
    return new name_construct(simple_name.make("null"), the_origin);
  }

  private static construct make_zero(origin the_origin) {
    return new literal_construct(new integer_literal(0), the_origin);
  }

  private static comment_construct make_comment(origin the_origin) {
    source_content src = position_util.get_source(the_origin);
    string comment;
    if (src != null) {
      comment = new base_string("Autogenerated from ", src.name.to_string());
    } else {
      comment = new base_string("Autogenerated");
    }

    return new comment_construct(new comment(comment_type.LINE_COMMENT, comment,
        new base_string("// ", comment)), the_origin);
  }

  private static comment_construct make_newline(origin the_origin) {
    string newline = new base_string("\n");
    return new comment_construct(new comment(comment_type.NEWLINE, newline, newline), the_origin);
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
    origin the_origin = the_analyzable_action;
    action the_action = the_analyzable_action.the_action;
    return process_action(the_action, the_origin);
  }

  public construct process_action(action the_action, origin the_origin) {
    if (the_action instanceof type_action) {
      return make_type(((type_action) the_action).get_type(), the_origin);
    }

    if (the_action instanceof value_action) {
      base_data_value the_value = (base_data_value) ((value_action) the_action).the_value;
      if (the_value instanceof singleton_value) {
        principal_type singleton_type = the_value.type_bound().principal();
        // Convert missing.instance to null literal
        if (is_null_subtype(singleton_type)) {
          return make_null(the_origin);
        }
        construct type_construct = make_type(singleton_type, the_origin);
        construct name_construct = new name_construct(type_kinds.INSTANCE_NAME, the_origin);
        return new resolve_construct(type_construct, name_construct, the_origin);
      } else if (the_value instanceof integer_value) {
        integer_value the_integer_value = (integer_value) the_value;
        return new literal_construct(new integer_literal(the_integer_value.unwrap()), the_origin);
      } else if (the_value instanceof base_procedure) {
        base_procedure the_procedure = (base_procedure) the_value;
        return new name_construct(the_procedure.name(), the_origin);
      }
      utilities.panic("processing action value " + the_value);
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
      return process_action(the_narrow_action.expression, the_origin);
    }

    if (the_action instanceof dispatch_action) {
      dispatch_action the_dispatch_action = (dispatch_action) the_action;
      action primary = the_dispatch_action.get_primary();
      if (the_dispatch_action.get_from() != null) {
        action from = the_dispatch_action.get_from();
        construct from_construct = process_action(from, the_origin);
        if (from instanceof type_action) {
          // TODO: handle type_action
        } else if (primary instanceof variable_action) {
          variable_action the_variable_action = (variable_action) primary;
          construct result = new resolve_construct(from_construct,
              new name_construct(the_variable_action.short_name(), the_origin), the_origin);
          declaration the_declaration = declaration_util.get_declaration(the_variable_action);
          if (the_declaration != null && should_call_as_procedure(the_declaration)) {
            result = make_call(result, new empty<construct>(), the_origin);
          }
          return result;
        } else {
          utilities.panic("processing dispatch action " + the_dispatch_action.get_from());
        }
      }
      return process_action(primary, the_origin);
    }

    if (the_action instanceof variable_action) {
      variable_action the_variable_action = (variable_action) the_action;
      // TODO: handle from
      name_construct name = new name_construct(the_variable_action.short_name(), the_origin);
      if (the_variable_action instanceof static_variable) {
        return new resolve_construct(
            make_type(the_variable_action.the_declaration.declared_in_type(), the_origin),
            name, the_origin);
      }
      return name;
    }

    if (the_action instanceof bound_procedure) {
      bound_procedure the_bound_procedure = (bound_procedure) the_action;
      readonly_list<construct> parameters =
          process_action_parameters(the_bound_procedure.parameters, the_origin);
      return make_call(process_action(the_bound_procedure.the_procedure_action, the_origin),
          parameters, the_origin);
    }

    utilities.panic("processing action " + the_action);
    return null;
  }

  public readonly_list<construct> process_action_parameters(action_parameters the_parameters,
      origin the_origin) {
    list<construct> result = new base_list<construct>();
    readonly_list<action> parameters_list = the_parameters.params();
    for (int i = 0; i < parameters_list.size(); ++i) {
      result.append(process_action(parameters_list.get(i), the_origin));
    }
    return result;
  }

  public construct process_enum_value(enum_value_analyzer the_enum_value) {
    origin the_origin = the_enum_value;
    name_construct the_name = new name_construct(the_enum_value.short_name(), the_origin);
    if (the_enum_value.parameters != null) {
      grouping_type grouping = grouping_type.PARENS;
      construct the_construct = get_construct(the_enum_value);
      if (the_construct instanceof parameter_construct) {
        grouping = ((parameter_construct) the_construct).parameters.grouping;
      }
      return new parameter_construct(the_name, new list_construct(
          transform_list(the_enum_value.constructor_parameters()),
              grouping, false, the_origin), the_origin);
    } else {
      return the_name;
    }
  }

  public construct process_grouping(grouping_analyzer the_grouping) {
    origin the_origin = the_grouping;
    return new list_construct(new base_list<construct>(transform(the_grouping.expression)),
        grouping_type.PARENS, false, the_origin);
  }

  public construct process_while(while_analyzer the_while) {
    origin the_origin = the_while;
    return new while_construct(
        transform(the_while.condition),
        transform(the_while.body),
        the_origin);
  }

  public construct process_for(for_analyzer the_for) {
    origin the_origin = the_for;
    return new for_construct(
        transform(the_for.init),
        transform(the_for.condition),
        transform(the_for.update),
        transform(the_for.body),
        the_origin);
  }

  @Override
  public construct process_extension(extension_analyzer the_extension) {
    if (the_extension instanceof grouping_analyzer) {
      return process_grouping((grouping_analyzer) the_extension);
    } else if (the_extension instanceof while_analyzer) {
      return process_while((while_analyzer) the_extension);
    } else if (the_extension instanceof for_analyzer) {
      return process_for((for_analyzer) the_extension);
    } else {
      return transform(the_extension.expand());
    }
  }

  public construct process_supertype(supertype_declaration the_supertype) {
    utilities.panic("Unexpected supertype_declaration");
    return null;
  }
}
