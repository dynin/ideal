-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

namespace enum_util {
  boolean can_be_enum_value(construct the_construct) {
    if (the_construct is name_construct || the_construct is parameter_construct) {
      return true;
    }

    if (the_construct is variable_construct) {
      the_variable_construct : the_construct;
      if (the_variable_construct.annotations.is_empty &&
          the_variable_construct.variable_type is null &&
          the_variable_construct.name is_not null &&
          the_variable_construct.post_annotations.is_empty &&
          the_variable_construct.init is parameter_construct) {
        main_initializer : (the_variable_construct.init !> parameter_construct).main;
        return main_initializer is name_construct &&
               main_initializer.the_name == special_name.NEW;
      }
    }

    return false;
  }
}
