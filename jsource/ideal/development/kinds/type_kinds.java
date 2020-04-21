/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.kinds;

import ideal.development.elements.*;
import ideal.development.flavors.*;
import static ideal.development.flavors.flavor_profiles.*;

public class type_kinds {

  public static kind block_kind = new base_kind("block", nameonly_profile);

  public static kind class_kind = new base_kind("class", mutable_profile);

  public static kind concept_kind = new base_kind("concept", mutable_profile);

  public static kind datatype_kind = new base_kind("datatype", mutable_profile);

  public static kind enum_kind = new base_kind("enum", deeply_immutable_profile);

  public static kind interface_kind = new base_kind("interface", mutable_profile);

  public static kind module_kind = new base_kind("module", nameonly_profile);

  public static kind namespace_kind = new base_kind("namespace", nameonly_profile);

  public static kind package_kind = new base_kind("package", nameonly_profile);

  public static kind singleton_kind = new base_kind("singleton", deeply_immutable_profile);

  // TODO: where should we put this?
  public static simple_name INSTANCE_NAME = simple_name.make("instance");

  public static kind project_kind = new base_kind("project", nameonly_profile);

  public static kind service_kind = new base_kind("service", mutable_profile);

  public static kind world_kind = new base_kind("world", mutable_profile);

  public static kind reference_kind = new base_kind("reference_kind", mutable_profile);

  public static kind procedure_kind = new base_kind("procedure_kind", immutable_profile);

  public static kind union_kind = new base_kind("union_kind", mutable_profile);

  public static kind type_alias_kind = new base_kind("type_alias_kind", mutable_profile);
}
