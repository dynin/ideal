/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
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
import ideal.development.constructs.*;
import ideal.development.actions.*;
import ideal.development.types.*;
import ideal.development.values.*;
import ideal.development.printers.*;
import ideal.development.transformers.*;

import javax.annotation.Nullable;

public class printer_target extends type_processor_target {

  private content_writer the_writer;

  public printer_target(simple_name the_name, target_manager the_manager) {
    super(the_name, the_manager);
  }

  @Override
  public void setup(analysis_context the_context) {
    the_writer = new content_writer(the_manager.output_catalog(), naming_strategy.dash_renderer);
  }

  @Override
  public void process_type(principal_type the_type) {
    type_declaration_construct the_declaration =
        (type_declaration_construct) the_type.get_declaration().deeper_origin();
    print_constructs(new base_list<construct>(the_declaration),
        type_utilities.get_full_names(the_type));
  }

  public void print_constructs(readonly_list<construct> constructs,
      readonly_list<simple_name> full_name) {

    assert full_name.is_not_empty();

    text_fragment result = new base_printer(printer_mode.CURLY).print_statements(constructs);
    string string_result = text_util.to_plain_text(result);
    the_writer.write(string_result, full_name, base_extension.TEXT);
  }
}
