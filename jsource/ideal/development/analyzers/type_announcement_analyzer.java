/*
 * Copyright 2014-2025 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://theideal.org/license/
 */

package ideal.development.analyzers;

import ideal.library.elements.*;
import javax.annotation.Nullable;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.development.elements.*;
import ideal.development.actions.*;
import ideal.development.constructs.*;
import ideal.development.notifications.*;
import ideal.development.names.*;
import ideal.development.types.*;
import ideal.development.kinds.*;
import ideal.development.modifiers.*;
import ideal.development.flavors.*;
import ideal.development.declarations.*;
import ideal.development.values.*;

public class type_announcement_analyzer extends declaration_analyzer<type_announcement_construct>
    implements type_announcement {

  private @Nullable type_declaration the_type_declaration;
  private @Nullable analyzable external_declaration;
  private @Nullable principal_type inside_type;
  private @Nullable readonly_list<declaration> declarations;
  private boolean announcement_analysis_in_progress;

  public type_announcement_analyzer(type_announcement_construct source) {
    super(source);
  }

  public kind the_kind() {
    return source.kind;
  }

  @Override
  public action_name short_name() {
    return source.name;
  }

  @Override
  public principal_type inner_type() {
    assert inside_type != null;
    return inside_type;
  }

  @Override
  public readonly_list<analyzable> children() {
    list<analyzable> result = new base_list<analyzable>();
    result.append(annotations());
    if (external_declaration != null) {
      result.append(external_declaration);
    }
    return result;
  }

  @Override
  protected signal do_multi_pass_analysis(analysis_pass pass) {
    assert !announcement_analysis_in_progress;
    announcement_analysis_in_progress = true;
    signal result = do_announcement_analysis(pass);
    announcement_analysis_in_progress = false;
    return result;
  }

  private signal do_announcement_analysis(analysis_pass pass) {
    if (pass == analysis_pass.TARGET_DECL) {
      // TODO: really handle modifiers, at least the document modifier.
      process_annotations(source.annotations, access_modifier.public_modifier);

      readonly_list<action> already_declared = get_context().lookup(declared_in_type(),
          short_name());
      if (already_declared.size() == 0) {
        action_utilities.make_type(get_context(), the_kind(), null,
            short_name(), declared_in_type(), this, this);
      } else if (already_declared.size() == 1) {
        action the_action = already_declared.first();
        principal_type the_type = the_action.result().type_bound().principal();
        declaration the_declaration = the_type.get_declaration();
        if (the_declaration != null) {
          the_type_declaration = (type_declaration) the_declaration;
        } else {
          ((base_principal_type) the_type).set_declaration(this);
        }
      } else {
        return new error_signal(new base_string("More than one type declaration"), this);
      }
    }

    if (pass == analysis_pass.TYPE_DECL) {
      if (the_type_declaration != null) {
        inside_type = make_inside_type(parent(), this);
        external_declaration = the_type_declaration;
        declarations = new base_list<declaration>(the_type_declaration);
      } else {
        readonly_list<construct> external_body = analyzer_utilities.load_resource(source);

        if (external_body == null) {
          // Assume the error has been reported.
          // TODO: load_resource() should return error_signal.
          return ok_signal.instance;
        }

        if (external_body.size() == 0) {
          return new error_signal(new base_string("External declaration expected"), this);
        }

        if (external_body.size() == 1 && external_body.first() instanceof comment_construct) {
          external_body = new base_list<construct>(
              make_comment_type((comment_construct) external_body.first()));
        }

        // TODO: there should be a cleaner way to handle subanalyzable/subdeclarations
        list<analyzable> subanalyzable = new base_list<analyzable>();
        list<declaration> subdeclarations = new base_list<declaration>();

        for (int i = 0; i < external_body.size(); ++i) {
          construct the_construct = external_body.get(i);
          // TODO: handle comments?
          if (the_construct instanceof import_construct) {
            import_analyzer the_import_analayzer =
                new import_analyzer((import_construct) the_construct);
            subanalyzable.append(the_import_analayzer);
            subdeclarations.append(the_import_analayzer);
          } else if (the_construct instanceof type_declaration_construct) {
            type_declaration_construct declaration = (type_declaration_construct) the_construct;

            if (!utilities.eq(declaration.name, short_name())) {
              return new error_signal(new base_string("Name mismatch: expected " + short_name()),
                  declaration);
            }

            if (declaration.kind != the_kind()) {
              return new error_signal(new base_string("Kind mismatch: expected " + the_kind()),
                  declaration);
            }

            assert declaration.body != null;

            analyzable converted_declaration = make(declaration);
            subanalyzable.append(converted_declaration);
            if (converted_declaration instanceof type_declaration) {
              the_type_declaration = (type_declaration) converted_declaration;
              subdeclarations.append(the_type_declaration);
            } else if (converted_declaration instanceof declaration_extension) {
              // TODO: report error if cast fails
              the_type_declaration = (type_declaration)
                  ((declaration_extension) converted_declaration).get_declaration();
              subdeclarations.append(the_type_declaration);
            } else {
              // TODO: report error.
              utilities.panic("Unrecognized declaration: " + converted_declaration);
            }
          } else {
            return new error_signal(
                new base_string("Type declaration or import expected"), the_construct);
          }
        }

        inside_type = make_inside_type(parent(), this);
        external_declaration = new list_analyzer(subanalyzable, true, this);
        declarations = subdeclarations;
        init_context(external_declaration);
      }
    }

    if (external_declaration instanceof multi_pass_analyzer) {
      return ((multi_pass_analyzer) external_declaration).multi_pass_analysis(pass);
    }

    return ok_signal.instance;
  }

  private type_declaration_construct make_comment_type(comment_construct the_comment_construct) {
    return new type_declaration_construct(
        new base_list<annotation_construct>(the_comment_construct),
        the_kind(),
        short_name(),
        null,
        new base_list<construct>(),
        this);
  }

  @Override
  public void load_resource() {
    if (!announcement_analysis_in_progress) {
      multi_pass_analysis(analysis_pass.METHOD_AND_VARIABLE_DECL);
    }
  }

  @Override
  protected analysis_result do_get_result() {
    assert external_declaration != null;
    return external_declaration.analyze();
  }

  @Override
  public type_declaration get_type_declaration() {
    assert the_type_declaration != null;
    return the_type_declaration;
  }

  @Override
  public kind get_kind() {
    return get_type_declaration().get_kind();
  }

  @Override
  public principal_type get_declared_type() {
    return the_type_declaration.get_declared_type();
  }

  @Override
  public readonly_list<declaration> external_declarations() {
    assert declarations != null;
    return declarations;
  }

  @Override
  public string to_string() {
    return utilities.describe(this, short_name());
  }
}
