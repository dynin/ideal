// Autogenerated from isource/library/channels.i

package ideal.library.channels;

import ideal.library.elements.*;

public interface input<element> extends closeable, syncable, readonly_input<element>, writeonly_input<element> {
  immutable_list<element> read(int max);
}