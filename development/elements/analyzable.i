-- Copyright 2014-2020 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- A data object that can be analyzed and convrted into an action.
--- The analisys may require some context, such as the parent frame;
--- see |ideal.development.analyzers.base_analyzer| for details.
interface analyzable {
  extends origin;

  --- Convert this object into an action.
  --- If there is an error this method returns an |error_signal|.
  analysis_result analyze();

  analyzable specialize(specialization_context context, principal_type new_parent) pure;
}
