/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.printers;

import ideal.library.elements.*;
import ideal.library.resources.*;
import ideal.library.texts.*;
import ideal.library.channels.*;
import ideal.runtime.elements.*;
import ideal.runtime.reflections.*;
import ideal.runtime.logs.*;
import ideal.runtime.texts.*;
import ideal.runtime.resources.*;
import ideal.development.elements.*;
import ideal.development.names.*;
import ideal.development.comments.*;
import ideal.development.constructs.*;
import ideal.development.declarations.*;
import ideal.development.actions.*;
import ideal.development.analyzers.*;
import ideal.development.actions.*;
import ideal.development.types.*;
import ideal.development.values.*;

import javax.annotation.Nullable;

public class naming_strategy extends debuggable implements printer_assistant, immutable_data {

  public static final string INDEX = new base_string("index");
  public static final simple_name XREF_NAME = simple_name.make("xref");

  public static final boolean DEBUG_FRAGMENTS = false;

  private final immutable_list<simple_name> full_names;
  private final principal_type current_type;
  private final xref_context the_xref_context;

  private final immutable_list<simple_name> current_catalog;
  private final dictionary<construct, string> fragments;
  private final set<string> fragment_ids;

  public static function1<string, simple_name> dash_renderer =
      new function1<string, simple_name>() {
        @Override
        public string call(simple_name name) {
          readonly_list<string> segments = name.segments;
          StringBuilder s = new StringBuilder();

          for (int i = 0; i < segments.size(); ++i) {
            s.append(utilities.s(segments.get(i)));
            if (i < segments.size() - 1) {
              s.append('-');
            }
          }

          return new base_string(s.toString());
        }
      };

  public naming_strategy(principal_type current_type, xref_context the_xref_context) {
    this(type_utilities.get_full_names(current_type), current_type, the_xref_context);
  }

  // TODO: this is only exposed so it can be used by create -pretty-print
  public naming_strategy(immutable_list<simple_name> full_names, principal_type current_type,
      xref_context the_xref_context) {
    assert full_names.is_not_empty();

    this.full_names = full_names;
    this.current_type = current_type;
    this.the_xref_context = the_xref_context;

    assert full_names.is_not_empty();
    this.current_catalog = full_names.slice(0, full_names.size() - 1);

    fragments = new hash_dictionary<construct, string>();
    fragment_ids = new hash_set<string>();
  }

  public principal_type get_current_type() {
    return current_type;
  }

  public immutable_list<simple_name> get_full_names() {
    return full_names;
  }

  public immutable_list<simple_name> get_xref_names() {
    return make_xref_target(full_names).frozen_copy();
  }

  public xref_context the_xref_context() {
    return the_xref_context;
  }

  public analysis_context the_analysis_context() {
    return the_xref_context.the_analysis_context;
  }

  private readonly_list<simple_name> make_xref_target(readonly_list<simple_name> target) {
    assert target.is_not_empty();
    list<simple_name> xref_names = new base_list<simple_name>();
    xref_names.append_all(target.slice(0, target.size() - 1));

    simple_name last_name = target.last();
    xref_names.append(name_utilities.join(last_name, XREF_NAME));

    return xref_names;
  }

  // TODO: test.
  public base_string link_to_resource(readonly_list<simple_name> target_name,
      extension target_extension) {
    int shared_prefix = 0;

    while (shared_prefix < (current_catalog.size() - 1) &&
           shared_prefix < (target_name.size() - 2) &&
           current_catalog.get(shared_prefix + 1) == target_name.get(shared_prefix + 1)) {
      ++shared_prefix;
    }

    StringBuilder result = new StringBuilder();
    int parent_count = current_catalog.size() - shared_prefix;

    for (int i = 0; i < parent_count; ++i) {
      result.append(utilities.s(resource_util.PARENT_CATALOG));
      result.append(utilities.s(resource_util.PATH_SEPARATOR));
    }

    for (int i = shared_prefix; i < target_name.size(); ++i) {
      result.append(utilities.s(dash_renderer.call(target_name.get(i))));
      if (i == target_name.size() - 1) {
        result.append(utilities.s(target_extension.dot_name()));
      } else {
        result.append(utilities.s(resource_util.PATH_SEPARATOR));
      }
    }

    return new base_string(result.toString());
  }

  public @Nullable string link_to_type(@Nullable principal_type the_type, printer_mode mode) {
    if (!publish_generator.GENERATE_XREF && mode == printer_mode.XREF) {
      return null;
    }

    the_type = the_xref_context.get_output_type(the_type);

    if (the_type == null) {
      return null;
    }

    readonly_list<simple_name> target_name = type_utilities.get_full_names(the_type);
    if (target_name.is_not_empty()) {
      if (mode == printer_mode.XREF) {
        target_name = make_xref_target(target_name);
      }
      return link_to_resource(target_name, base_extension.HTML);
    } else {
      return null;
    }
  }

  @Override
  public @Nullable string link_to_construct(construct the_construct, printer_mode mode) {
    assert the_construct != null;
    @Nullable principal_type output_type = the_xref_context.get_enclosing_type(the_construct);
    if (output_type == null) {
      // Most likely, this is not_yet_implemented
      return null;
    }
    @Nullable string link = link_to_type(output_type, mode);
    if (link == null) {
      return null;
    }

    naming_strategy target_naming = the_xref_context.get_naming_strategy(output_type);
    assert target_naming != null;
    @Nullable string fragment_id = target_naming.fragment_of_construct(the_construct, mode);
    if (fragment_id != null) {
      link = new base_string(link, text_library.FRAGMENT_SEPARATOR, fragment_id);
    }

    return link;
  }

  public @Nullable string link_to_type_declaration(type_declaration the_type_declaration,
      type_declaration_construct the_declaration_construct, printer_mode mode) {
    principal_type output_type = the_type_declaration.get_declared_type();
    assert the_xref_context.has_output_type(output_type);

    @Nullable string link = link_to_type(output_type, mode);
    if (link == null) {
      return null;
    }

    naming_strategy target_naming = the_xref_context.get_naming_strategy(output_type);
    assert target_naming != null;
    @Nullable string fragment_id = target_naming.fragment_of_construct(the_declaration_construct,
        mode);
    if (fragment_id != null) {
      link = new base_string(link, text_library.FRAGMENT_SEPARATOR, fragment_id);
    }

    return link;
  }

  @Override
  public @Nullable string link_to_declaration(construct the_construct, printer_mode mode) {
    if (the_construct instanceof type_announcement_construct) {
      type_declaration the_type_declaration = declaration_util.to_type_declaration(
          the_xref_context.origin_to_declaration(the_construct));
      assert the_type_declaration != null;
      return link_to_type(the_type_declaration.get_declared_type(), mode);
    } else {
      return declaration_link(the_xref_context.origin_to_declaration(the_construct), mode);
    }
  }

  public @Nullable string declaration_link(@Nullable declaration the_declaration,
      printer_mode mode) {
    if (the_declaration == null || the_declaration instanceof builtin_declaration) {
      return null;
    }

    if (the_declaration instanceof type_declaration) {
      type_declaration the_type_declaration = (type_declaration) the_declaration;
      if (the_xref_context.has_output_type(the_type_declaration.get_declared_type())) {
        return link_to_type(the_type_declaration.get_declared_type(), mode);
      }
    }

    if (the_declaration instanceof type_announcement) {
      type_announcement the_type_announcement = (type_announcement) the_declaration;
      if (!the_xref_context.has_output_type(the_type_announcement.declared_in_type())) {
        return null;
      }
    }

    if (the_declaration instanceof variable_declaration) {
      if (((variable_declaration) the_declaration).get_category() == variable_category.LOCAL) {
        return null;
      }
    }

    if (the_declaration instanceof procedure_declaration) {
      // generate procedure link
    }

    construct declaration_construct = printer_util.find_construct(the_declaration);
    if (declaration_construct instanceof parameter_construct) {
      declaration_construct = ((parameter_construct) declaration_construct).main;
    }
    return link_to_construct(declaration_construct, mode);
  }

  @Override
  public @Nullable string fragment_of_construct(construct the_construct, printer_mode mode) {
    if (!publish_generator.GENERATE_XREF) {
      return null;
    }

    @Nullable string fragment = fragments.get(the_construct);
    if (fragment == null) {
      @Nullable analyzable the_analyzable = the_analysis_context().get_analyzable(the_construct);
      if (the_analyzable == null || the_analyzable.has_errors()) {
        // Most likely, this is not_yet_implemented
        return null;
      }

      if (DEBUG_FRAGMENTS) {
        System.out.println("NOFRAG " + current_type + " C " + the_construct +
            " A " + the_analyzable + " AA " + the_analyzable.analyze());
      }

      utilities.panic("No fragment found for " + the_construct);
      return null;
    }

    return fragment;
  }

  private action_name name_of_construct(construct the_construct) {
    if (the_construct instanceof name_construct) {
      return ((name_construct) the_construct).the_name;
    } else if (the_construct instanceof type_declaration_construct) {
      return ((type_declaration_construct) the_construct).name;
    } else if (the_construct instanceof type_announcement_construct) {
      return ((type_announcement_construct) the_construct).name;
    } else if (the_construct instanceof variable_construct) {
      return ((variable_construct) the_construct).name;
    } else if (the_construct instanceof procedure_construct) {
      return ((procedure_construct) the_construct).name;
    } else {
      utilities.panic("Unknown construct " + the_construct);
      return null;
    }
  }

  public string add_fragment(construct the_construct) {
    @Nullable string fragment = fragments.get(the_construct);
    assert fragment == null;

    if (DEBUG_FRAGMENTS) {
      System.out.println("FRAG " + current_type + " C " + the_construct);
    }

    action_name name = name_of_construct(the_construct);

    if (name instanceof special_name) {
      // TODO: handle special names such as super and new
      return null;
    }

    fragment = name_to_id(name);

    string result = fragment;
    int index = 1;

    while (fragment_ids.contains(result)) {
      index += 1;
      result = new base_string(fragment, String.valueOf(index));
    }

    if (DEBUG_FRAGMENTS) {
      System.out.println("PUTFRAG " + current_type + " C " + the_construct + " R " + result);
    }

    fragments.put(the_construct, result);
    fragment_ids.add(result);

    return result;
  }

  public void add_fragment_alias(construct the_construct, string fragment) {
    @Nullable string old_fragment = fragments.get(the_construct);
    assert old_fragment == null;
    fragments.put(the_construct, fragment);
  }

  private string name_to_id(action_name the_action_name) {
    return dash_renderer.call((simple_name) the_action_name);
  }

  @Override
  public @Nullable documentation get_documentation(construct the_construct) {
    @Nullable analyzable the_analyzable = the_analysis_context().get_analyzable(the_construct);
    if (the_analyzable == null) {
      return null;
    }

    declaration the_declaration = (declaration) the_analyzable;
    if (the_declaration instanceof procedure_declaration) {
      return ((procedure_declaration) the_declaration).annotations().the_documentation();
    } else if (the_declaration instanceof type_announcement) {
      type_declaration the_type_declaration =
          ((type_announcement) the_declaration).get_type_declaration();
      return printer_util.extract_summary(the_type_declaration.annotations(), the_declaration);
    } else {
      return null;
    }
  }
}
