/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
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
import ideal.development.comments.*;
import ideal.development.constructs.*;
import ideal.development.declarations.*;
import ideal.development.actions.*;
import ideal.development.types.*;
import ideal.development.values.*;

import javax.annotation.Nullable;

public class naming_strategy extends debuggable implements printer_assistant, immutable_data {

  public static final string INDEX = new base_string("index");

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
    this.full_names = full_names;
    this.current_type = current_type;
    this.the_context = the_context;

    assert !full_names.is_empty();
    this.current_catalog = full_names.slice(0, full_names.size() - 1);

    this.the_printer = new base_printer(printer_mode.STYLISH, this);
  }

  public principal_type get_current_type() {
    return current_type;
  }

  public base_printer get_printer() {
    return the_printer;
  }

  public immutable_list<simple_name> get_full_names() {
    return full_names;
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

  public @Nullable string link_to_type(principal_type the_type) {
    readonly_list<simple_name> target_name = type_utilities.get_full_names(the_type);
    if (!target_name.is_empty()) {
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
  public @Nullable string make_link(construct the_construct) {
    @Nullable declaration the_declaration = get_declaration(the_construct);
    if (the_declaration instanceof type_declaration &&
        !(the_declaration instanceof type_parameter_declaration)) {
      return link_to_type(((type_declaration) the_declaration).get_declared_type());
    } else {
      return null;
    }
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
