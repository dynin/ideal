// Autogenerated from development/constructs/supertype_construct.i

package ideal.development.constructs;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.development.elements.*;
import ideal.development.names.*;

import javax.annotation.Nullable;

public class supertype_construct extends base_construct {
  public final readonly_list<annotation_construct> annotations;
  public final @Nullable type_flavor subtype_flavor;
  public final subtype_tag tag;
  public final readonly_list<construct> types;
  public supertype_construct(final readonly_list<annotation_construct> annotations, final @Nullable type_flavor subtype_flavor, final subtype_tag tag, final readonly_list<construct> types, final origin generated_origin) {
    super(generated_origin);
    assert annotations != null;
    this.annotations = annotations;
    this.subtype_flavor = subtype_flavor;
    assert tag != null;
    this.tag = tag;
    assert types != null;
    this.types = types;
  }
  public @Override readonly_list<construct> children() {
    final base_list<construct> generated_result = new base_list<construct>();
    generated_result.append_all((readonly_list<construct>) (readonly_list) this.annotations);
    generated_result.append_all(this.types);
    return generated_result;
  }
}
