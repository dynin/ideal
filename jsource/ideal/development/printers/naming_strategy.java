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
import ideal.development.types.*;
import ideal.development.values.*;

import javax.annotation.Nullable;

public class naming_strategy extends debuggable implements printer_assistant, immutable_data {

  public static final string INDEX = new base_string("index");
  public static final simple_name XREF_NAME = simple_name.make("xref");

  private final immutable_list<simple_name> full_names;
  private final principal_type current_type;
  private final analysis_context the_context;

  private final immutable_list<simple_name> current_catalog;
  private final base_printer the_printer;

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

  public naming_strategy(principal_type current_type, analysis_context the_context) {
    this(type_utilities.get_full_names(current_type), current_type, the_context);
  }

  // TODO: this is only exposed so it can be used by create -pretty-print
  public naming_strategy(immutable_list<simple_name> full_names, principal_type current_type,
      analysis_context the_context) {
    assert full_names.is_not_empty();

    this.full_names = full_names;
    this.current_type = current_type;
    this.the_context = the_context;

    assert full_names.is_not_empty();
    this.current_catalog = full_names.slice(0, full_names.size() - 1);

    this.the_printer = new base_printer(printer_mode.STYLISH, this);
  }

  public principal_type get_current_type() {
    return current_type;
  }

  public @Nullable declaration get_current_declaration() {
    return current_type.get_declaration();
  }

  public base_printer get_printer() {
    return the_printer;
  }

  public immutable_list<simple_name> get_full_names() {
    return full_names;
  }

  public immutable_list<simple_name> get_xref_names() {
    return make_xref_target(full_names).frozen_copy();
  }

  private readonly_list<simple_name> make_xref_target(readonly_list<simple_name> target) {
    assert target.is_not_empty();
    list<simple_name> xref_names = new base_list<simple_name>();
    xref_names.append_all(target.slice(0, target.size() - 1));

    simple_name last_name = full_names.last();
    xref_names.append(name_utilities.join(last_name, XREF_NAME));

    return xref_names;
  }

  // TODO: test.
  public base_string link_to(readonly_list<simple_name> target_name, extension target_extension) {
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

  public @Nullable string link_to_type(principal_type the_type, link_mode mode) {
    readonly_list<simple_name> target_name = type_utilities.get_full_names(the_type);
    if (target_name.is_not_empty()) {
      if (mode == link_mode.XREF) {
        target_name = make_xref_target(target_name);
      }
      return link_to(target_name, base_extension.HTML);
    } else {
      return null;
    }
  }

  public text_fragment print_simple_name(simple_name name) {
    return the_printer.print_simple_name(name);
  }

  private @Nullable declaration get_declaration(construct the_construct) {
    @Nullable analyzable the_analyzable = the_context.get_analyzable(the_construct);
    if (the_analyzable != null) {
      @Nullable declaration the_declaration;
      if (the_analyzable instanceof declaration) {
        return (declaration) the_analyzable;
      } else {
        return declaration_util.get_declaration(the_analyzable.analyze());
      }
    }
    return null;
  }

  @Override
  public @Nullable string make_link(construct the_construct, link_mode mode) {
    @Nullable declaration the_declaration = get_declaration(the_construct);
    if (the_declaration instanceof type_declaration &&
        !(the_declaration instanceof type_parameter_declaration)) {
      type_declaration the_type_declaration = (type_declaration) the_declaration;
      if (!the_type_declaration.has_errors()) {
        return link_to_type(the_type_declaration.get_declared_type(), mode);
      }
    }

    return null;
  }

  @Override
  public @Nullable documentation get_documentation(construct the_construct) {
    @Nullable declaration the_declaration = get_declaration(the_construct);
    if (the_declaration instanceof procedure_declaration) {
      return ((procedure_declaration) the_declaration).annotations().the_documentation();
    } else {
      return null;
    }
  }
}
