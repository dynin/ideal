-- Copyright 2014-2022 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

--- All subtyping tags used in the ideal system.
-- TODO: turn into enum?
namespace subtype_tags {

  --- Generic subtyping tag, used in ideal library.
  subtype_tag subtypes_tag : base_subtype_tag.new("subtypes");

  subtype_tag extends_tag : base_subtype_tag.new("extends");

  subtype_tag implements_tag : base_subtype_tag.new("implements");

  subtype_tag refines_tag : base_subtype_tag.new("refines");

  subtype_tag aliases_tag : base_subtype_tag.new("aliases");
}
