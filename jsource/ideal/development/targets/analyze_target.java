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
import ideal.development.notifications.*;
import ideal.development.analyzers.*;

import javax.annotation.Nullable;

public class analyze_target extends type_processor_target {

  action_context the_context;

  public analyze_target(simple_name the_name, target_manager the_manager) {
    super(the_name, the_manager);
  }

  @Override
  public void setup(action_context the_context) {
    this.the_context = the_context;
  }

  @Override
  public void process_type(principal_type the_type) {
    declaration the_declaration = the_type.get_declaration();
    the_declaration.analyze();

    if (the_manager.has_errors()) {
      return;
    }

    ensure_is_analyzed((construct) the_declaration.deeper_origin());

    if (the_manager.has_errors()) {
      return;
    }

    // TODO: only do this in verbose mode
    new base_notification(
          new base_string(base_value_printer.instance.print_type(the_type), " looking good."),
          the_declaration, null, notification_level.INFORMATIONAL).report();
  }

  private void ensure_is_analyzed(construct the_construct) {
    @Nullable analyzable the_analyzable = ((base_construct) the_construct).the_analyzable;

    if (the_analyzable == null) {
      if (!(the_construct instanceof empty_construct) &&
          !(the_construct instanceof modifier_construct) &&
          !(the_construct instanceof comment_construct)) {
        new base_notification(
            new base_string("Not analyzed " + the_construct), the_construct).report();
        return;
      }
    }

    readonly_list<construct> children;
    if (the_analyzable instanceof declaration_extension) {
      children = ((declaration_extension) the_analyzable).construct_children();
    } else {
      children = the_construct.children();
    }
    for (int i = 0; i < children.size(); ++i) {
      ensure_is_analyzed(children.get(i));
    }

    if (the_construct instanceof type_announcement_construct) {
      type_announcement the_type_announcement = (type_announcement) the_analyzable;
      readonly_list<declaration> declarations = the_type_announcement.external_declarations();
      for (int i = 0; i < declarations.size(); ++i) {
        ensure_is_analyzed((construct) declarations.get(i).deeper_origin());
      }
    }
  }

  @Override
  public void finish_processing() { }
}
