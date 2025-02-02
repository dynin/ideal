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
import ideal.development.constructs.*;
import ideal.development.actions.*;
import ideal.development.types.*;
import ideal.development.values.*;
import ideal.development.printers.*;
import ideal.development.transformers.*;

import javax.annotation.Nullable;

public class publish_target extends type_processor_target {

  private final publish_mode mode;
  private publish_generator the_generator;

  public publish_target(simple_name the_name, target_manager the_manager, publish_mode mode) {
    super(the_name, the_manager);
    this.mode = mode;
  }

  @Override
  public void setup(action_context the_context) {

    content_writer the_writer = new content_writer(the_manager.output_catalog(),
        printer_util.dash_renderer);
    the_generator = new publish_generator(mode, the_context, the_writer);

    if (the_manager.output_catalog() != null) {
      resource_catalog output_catalog = the_manager.output_catalog();
      string assets = printer_util.dash_renderer.call(publish_generator.ASSETS_NAME);
      string ideal_style = printer_util.dash_renderer.call(publish_generator.IDEAL_STYLE_NAME);
      resource_catalog assets_catalog = the_manager.top_catalog().resolve(assets).access_catalog();
      resource_identifier css_source = assets_catalog.resolve(ideal_style, base_extension.CSS);
      string stylesheet_content = css_source.access_string(null).content().get();
      the_writer.write(stylesheet_content,
          new base_list<simple_name>(publish_generator.ASSETS_NAME,
              publish_generator.IDEAL_STYLE_NAME), base_extension.CSS);
    }
  }

  @Override
  public void process_type(principal_type the_type) {
    the_generator.add_type(the_type);
  }

  @Override
  public void finish_processing() {
    the_generator.generate_all();
  }
}
