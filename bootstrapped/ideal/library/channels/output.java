// Autogenerated from isource/library/channels.i

package ideal.library.channels;

import ideal.library.elements.*;

public interface output<element> extends closeable, syncable, readonly_output<element>, writeonly_output<element> {
  void write(element e);
  void write_all(readonly_list<element> c);
}
