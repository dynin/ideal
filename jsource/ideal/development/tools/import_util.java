/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.tools;

import ideal.library.elements.*;
import ideal.library.resources.*;
import ideal.library.texts.*;
import ideal.library.channels.*;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.runtime.resources.*;
import ideal.runtime.texts.*;
import ideal.machine.channels.standard_channels;
import ideal.machine.resources.filesystem;
import ideal.development.elements.*;
import ideal.development.types.*;
import ideal.development.names.*;
import ideal.development.values.*;
import ideal.development.constructs.*;
import ideal.development.modifiers.*;
import static ideal.development.modifiers.access_modifier.*;
import static ideal.development.modifiers.general_modifier.*;
import ideal.development.names.*;
import ideal.development.kinds.*;
import ideal.development.printers.*;
import ideal.development.origins.*;
import ideal.development.notifications.*;

import java.lang.reflect.*;

public class import_util {

  private import_util() { }

  public static void start_import() {
    output<text_fragment> out = new plain_formatter(standard_channels.stdout);

    create_manager cm = new create_manager(filesystem.CURRENT_CATALOG);
    cm.process_bootstrap(true);

    String name = "java.lang.Object";
    //String name = "java.lang.String";

    origin import_pos = new special_origin(new base_string("[import]"));
    type_declaration_construct tc = new import_util().import_type(name, import_pos);

    out.write(new base_printer(printer_mode.CURLY).print(tc));
  }

  public type_declaration_construct import_type(String class_name, origin pos) {
    Class cl;
    try {
      return import_type(Class.forName(class_name), pos);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public type_declaration_construct import_type(Class cl, origin pos) {
    list<annotation_construct> annotations = modifiers_as_list(cl.getModifiers(),
        pos);
    kind kind = cl.isInterface() ? type_kinds.interface_kind:
        type_kinds.class_kind;
    simple_name name = name_utilities.parse_camel_case(new base_string(cl.getSimpleName()));

    list<construct> declarations = new base_list<construct>();
    for (Method method : cl.getDeclaredMethods()) {
      declarations.append(import_method(method, pos));
    }
    type_declaration_construct result = new type_declaration_construct(annotations, kind, name,
        null, declarations, pos);

    return result;
  }

  public procedure_construct import_method(Method method, origin pos) {
    list<annotation_construct> annotations = modifiers_as_list(
        method.getModifiers(), pos);
    construct ret = get_name(method.getReturnType(), pos);
    simple_name name = name_utilities.parse_camel_case(new base_string(method.getName()));
    list<construct> params = new base_list<construct>();
    int index = 0;
    for (Class param : method.getParameterTypes()) {
      params.append(get_var(param, index++, pos));
    }

    procedure_construct result = new procedure_construct(annotations, ret, name, params,
        new empty<annotation_construct>(), null, pos);

    return result;
  }

  private construct get_name(Class type, origin pos) {
    return new name_construct(name_utilities.parse_camel_case(
        new base_string(type.getSimpleName())), pos);
  }

  private variable_construct get_var(Class type, int index, origin pos) {
    list<annotation_construct> annotations = new base_list<annotation_construct>();
    construct var_type = get_name(type, pos);
    simple_name name = simple_name.make(new base_string("val" + index));
    construct init = null;

    return new variable_construct(annotations, var_type, name, new empty<annotation_construct>(),
        init, pos);
  }

  public static class modifier_mapping {
    public final int value;
    public final modifier_kind kind;

    public modifier_mapping(int value, modifier_kind kind) {
      this.value = value;
      this.kind = kind;
    }
  }

  public static final modifier_mapping[] all_modifiers = {
    new modifier_mapping(Modifier.PUBLIC, public_modifier),
    new modifier_mapping(Modifier.PROTECTED, protected_modifier),
    new modifier_mapping(Modifier.PRIVATE, private_modifier),

    new modifier_mapping(Modifier.ABSTRACT, abstract_modifier),
    new modifier_mapping(Modifier.STATIC, static_modifier),
    new modifier_mapping(Modifier.FINAL, final_modifier),
    new modifier_mapping(Modifier.TRANSIENT, transient_modifier),
    new modifier_mapping(Modifier.VOLATILE, volatile_modifier),
    new modifier_mapping(Modifier.SYNCHRONIZED, synchronized_modifier),
    new modifier_mapping(Modifier.NATIVE, native_modifier),
    // TODO: missing: STRICT, INTERFACE
  };

  public static list<annotation_construct> modifiers_as_list(int modifiers,
      origin pos) {
    list<annotation_construct> result = new base_list<annotation_construct>();

    for (modifier_mapping mod : all_modifiers) {
      if ((mod.value & modifiers) != 0) {
        result.append(new modifier_construct(mod.kind, pos));
      }
    }

    return result;
  }
}
