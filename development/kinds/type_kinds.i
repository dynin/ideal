-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

implicit import ideal.development.flavors.flavor_profiles;

--- All type kinds used in the ideal system.
namespace type_kinds {

  block_kind : base_kind.new("block", nameonly_profile, false);

  class_kind : base_kind.new("class", mutable_profile, true);

  concept_kind : base_kind.new("concept", mutable_profile, false);

  datatype_kind : base_kind.new("datatype", mutable_profile, true);

  enum_kind : base_kind.new("enum", deeply_immutable_profile, true);

  interface_kind : base_kind.new("interface", mutable_profile, false);

  module_kind : base_kind.new("module", nameonly_profile, false);

  namespace_kind : base_kind.new("namespace", nameonly_profile, false);

  package_kind : base_kind.new("package", nameonly_profile, false);

  program_kind : base_kind.new("program", mutable_profile, false);

  html_content_kind : base_kind.new("html_content", nameonly_profile, false);

  singleton_kind : base_kind.new("singleton", deeply_immutable_profile, false);

  project_kind : base_kind.new("project", nameonly_profile, false);

  service_kind : base_kind.new("service", mutable_profile, false);

  world_kind : base_kind.new("world", mutable_profile, false);

  test_suite_kind : base_kind.new("test_suite", mutable_profile, true);

  #id:reference_kind : base_kind.new("reference_kind", mutable_profile, false);

  #id:procedure_kind : base_kind.new("procedure_kind", immutable_profile, false);

  union_kind : base_kind.new("union_kind", mutable_profile, false);

  type_alias_kind : base_kind.new("type_alias_kind", mutable_profile, false);
}
