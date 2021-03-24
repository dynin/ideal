-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Annotation and documentation associated with a declaration.
interface annotation_set {
  extends analyzable, analysis_result;
  extends data;

  access_modifier access_level;
  boolean has(modifier_kind the_kind);
  documentation or null the_documentation;

  override analysis_result analyze();
  override analyzable specialize(specialization_context context,
      principal_type new_parent) pure;
}
