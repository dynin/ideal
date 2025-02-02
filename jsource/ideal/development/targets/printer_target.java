/*
 * Copyright 2014-2025 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://theideal.org/license/
 */

package ideal.development.targets;

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
import ideal.development.declarations.*;
import ideal.development.constructs.*;
import ideal.development.actions.*;
import ideal.development.types.*;
import ideal.development.values.*;
import ideal.development.printers.*;
import ideal.development.transformers.*;

import ideal.machine.elements.runtime_util;
import ideal.machine.channels.standard_channels;

import javax.annotation.Nullable;

public class printer_target extends type_processor_target {

  private content_writer the_writer;

  public printer_target(simple_name the_name, target_manager the_manager) {
    super(the_name, the_manager);
  }

  @Override
  public void setup(action_context the_context) {
    the_writer = new content_writer(the_manager.output_catalog(), printer_util.dash_renderer);
  }

  @Override
  public void process_type(principal_type the_type) {
    construct declaration_construct = (construct) the_type.get_declaration().deeper_origin();
    if (declaration_construct instanceof type_announcement_construct) {
      declaration_construct =
          (construct) ((type_announcement) the_type.get_declaration()).get_type_declaration().
              deeper_origin();
    }

    if (false) {
      output<text_fragment> out = new plain_formatter(standard_channels.stdout);
      out.write(runtime_util.display(declaration_construct));
    }

    print_constructs(new base_list<construct>(declaration_construct),
        type_utilities.get_full_names(the_type));
  }

  public void print_constructs(readonly_list<construct> constructs,
      readonly_list<simple_name> full_name) {

    assert full_name.is_not_empty();

    text_fragment result = new base_printer(printer_mode.CURLY).print_statements(constructs);
    string string_result = text_utilities.to_plain_text(result);
    the_writer.write(string_result, full_name, base_extension.TEXT);
  }

  @Override
  public void finish_processing() { }
}
