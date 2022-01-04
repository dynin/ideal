-- Copyright 2014-2022 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

--- A data object that can be analyzed and converted into an action.
--- The analisys may require some context, such as the parent frame;
--- see |ideal.development.analyzers.base_analyzer| for details.
interface analyzable {
  extends origin;

  --- If true, this analyzable has errors and further processing should be skipped.
  boolean has_errors;

  --- Convert this object into an action.
  --- If there is an error this method returns an |error_signal|.
  analysis_result analyze();

  --- Get direct children in the tree of |analyzable|s.
  readonly list[analyzable] children();

  analyzable specialize(specialization_context context, principal_type new_parent) pure;
}
