-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Declarations for resources, resource catalogs and resource identifiers.
package resources {
  implicit import ideal.library.elements;
  import ideal.library.formats.json_data;

  interface resource[value content_type] {
    extends value, stringable;

    -- TODO: properties type; name differently
    -- dictionary[identifier, deeply_immutable data] metadata();

    var reference[content_type] content;
  }

  interface resource_identifier {
    extends identifier;

    string scheme;
    -- TODO: should be a hierarchical name instead of string?
    string or null host;

    -- TODO: make into an immutable reference?
    resource_identifier parent;

    -- TODO: make into a readonly reference?
    boolean exists;

    resource[string] access_string(access_option or null options);
    resource[readonly json_data] access_json_data(access_option or null options);
    resource_catalog access_catalog();
  }

  interface access_option {
    extends deeply_immutable data, stringable;
  }

  interface resource_catalog {
    extends resource[dictionary[string, resource_identifier] or null];

    resource_identifier id pure;
    overload resource_identifier resolve(string name) pure;
    overload resource_identifier resolve(string name, extension ext) pure;
  }

  interface extension {
    extends deeply_immutable data, stringable;

    string dot_name;
  }
}
