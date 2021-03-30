-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

implicit import ideal.development.flavors.flavor_profiles;

--- All type kinds used in the ideal system.
namespace type_kinds {

  block_kind : base_kind.new("block", nameonly_profile);

  class_kind : base_kind.new("class", mutable_profile);

  concept_kind : base_kind.new("concept", mutable_profile);

  datatype_kind : base_kind.new("datatype", mutable_profile);

  enum_kind : base_kind.new("enum", deeply_immutable_profile);

  interface_kind : base_kind.new("interface", mutable_profile);

  module_kind : base_kind.new("module", nameonly_profile);

  namespace_kind : base_kind.new("namespace", nameonly_profile);

  package_kind : base_kind.new("package", nameonly_profile);

  html_content_kind : base_kind.new("html_content", nameonly_profile);

  singleton_kind : base_kind.new("singleton", deeply_immutable_profile);

  -- TODO: where should we put this?
  INSTANCE_NAME : simple_name.make("instance");

  project_kind : base_kind.new("project", nameonly_profile);

  service_kind : base_kind.new("service", mutable_profile);

  world_kind : base_kind.new("world", mutable_profile);

  #id:reference_kind : base_kind.new("reference_kind", mutable_profile);

  #id:procedure_kind : base_kind.new("procedure_kind", immutable_profile);

  union_kind : base_kind.new("union_kind", mutable_profile);

  type_alias_kind : base_kind.new("type_alias_kind", mutable_profile);
}
