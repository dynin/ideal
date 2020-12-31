/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
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

public class analyze_target extends type_processor_target {

  base_value_printer printer;

  public analyze_target(simple_name the_name, target_manager the_manager) {
    super(the_name, the_manager);
    printer = new base_value_printer(null);
  }

  @Override
  public void setup(analysis_context the_context) { }

  @Override
  public void process_type(principal_type the_type) {
    declaration the_declaration = the_type.get_declaration();
    assert the_declaration instanceof analyzable;
    ((analyzable) the_declaration).analyze();

    if (!the_manager.has_errors()) {
      // TODO: only do this in verbose mode
      log.info(new base_string(printer.print_type(the_type), " looking good."));
    }
  }

  @Override
  public void finish_processing() { }
}
