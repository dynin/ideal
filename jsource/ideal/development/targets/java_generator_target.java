/*
 * Copyright 2014-2022 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://theideal.org/license/
 */

package ideal.development.targets;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import javax.annotation.Nullable;
import ideal.runtime.reflections.*;
import ideal.runtime.logs.*;
import ideal.development.elements.*;
import ideal.development.actions.*;
import ideal.development.types.*;
import ideal.development.values.*;
import ideal.development.transformers.*;

public class java_generator_target extends type_processor_target {

  private @Nullable java_generator generator;

  public java_generator_target(simple_name the_name, target_manager the_manager) {
    super(the_name, the_manager);
  }

  @Override
  public void setup(action_context the_context) {
    generator = new java_generator(java_library.get_instance(),
        new content_writer(the_manager.output_catalog(), null));
  }

  @Override
  public void process_type(principal_type the_type) {
    declaration the_declaration = the_type.get_declaration();
    the_declaration.analyze();
    generator.generate_for_type(the_type);
  }

  @Override
  public void finish_processing() { }
}
