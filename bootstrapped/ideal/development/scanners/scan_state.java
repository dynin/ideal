// Autogenerated from development/scanners/scan_state.i

package ideal.development.scanners;

import ideal.library.elements.*;
import ideal.library.characters.*;
import ideal.runtime.elements.*;
import ideal.runtime.characters.*;
import ideal.runtime.logs.*;
import ideal.machine.characters.*;
import ideal.machine.channels.string_writer;
import ideal.development.elements.*;
import ideal.development.names.*;
import ideal.development.notifications.*;
import ideal.development.origins.*;
import ideal.development.comments.*;
import ideal.development.literals.*;
import ideal.development.modifiers.*;
import ideal.development.constructs.constraint_category;
import ideal.development.jumps.jump_category;

public class scan_state implements deeply_immutable_data {
  public final token token;
  public final Integer prefix_end;
  public final Integer end;
  public Integer compare_to(final scan_state other) {
    Integer result = this.prefix_end - other.prefix_end;
    if (ideal.machine.elements.runtime_util.values_equal(result, 0)) {
      result = this.end - other.end;
    }
    return result;
  }
  public scan_state(final token token, final Integer prefix_end, final Integer end) {
    this.token = token;
    this.prefix_end = prefix_end;
    this.end = end;
  }
}