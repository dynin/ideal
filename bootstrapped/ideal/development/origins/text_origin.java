// Autogenerated from development/origins/text_origin.i

package ideal.development.origins;

import ideal.library.elements.*;
import ideal.library.texts.*;
import ideal.library.channels.*;
import ideal.library.resources.*;
import ideal.library.patterns.*;
import ideal.runtime.elements.*;
import ideal.runtime.texts.*;
import ideal.runtime.patterns.*;
import ideal.runtime.logs.*;
import ideal.development.elements.*;
import ideal.development.names.*;

import ideal.machine.annotations.dont_display;

public class text_origin extends debuggable implements deeply_immutable_data, origin, stringable {
  public @dont_display final source_content source;
  public @dont_display final Integer begin;
  public @dont_display final Integer end;
  public text_origin(final source_content source, final Integer begin, final Integer end) {
    this.source = source;
    this.begin = begin;
    this.end = end;
    assert begin >= 0 && end >= begin && source.content.size() >= end;
  }
  public @Override origin deeper_origin() {
    return this.source;
  }
  public string image() {
    return this.source.content.slice(this.begin, this.end);
  }
  public @Override string to_string() {
    return ideal.machine.elements.runtime_util.concatenate(ideal.machine.elements.runtime_util.concatenate(ideal.machine.elements.runtime_util.concatenate(ideal.machine.elements.runtime_util.concatenate(new base_string("("), this.begin), new base_string("-")), this.end), new base_string(")"));
  }
}
