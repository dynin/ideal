-- Copyright 2014-2020 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

package resources {
  implicit import ideal.library.elements;
  implicit import ideal.library.channels;

  interface resource[value content_type] {
    extends value;

    -- TODO: properties type; name differently
    -- dictionary[identifier, deeply_immutable data] metadata();

    content_type content mutable;
  }

  interface resource_identifier {
    extends identifier;

    -- TODO: make into an immutable reference?
    resource_identifier parent() pure;

    -- TODO: make into a readonly reference?
    boolean exists() pure;

    resource[string] access_string(access_option or null options);
    resource[resource_catalog] access_catalog();
  }

  interface access_option {
    extends deeply_immutable data, stringable;
  }

  interface resource_catalog {
    extends value;

    resource_identifier get_id() pure;
    overload resource_identifier resolve(string name);
    overload resource_identifier resolve(string name, extension ext);
  }

  interface extension {
    extends deeply_immutable data, stringable;

    string dot_name deeply_immutable;
  }
}
