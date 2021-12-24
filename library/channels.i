-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Interfaces for input/output channels and related types.
package channels {
  implicit import ideal.library.elements;

  interface closeable {
    extends value;
    not_yet_implemented readonly boolean is_closed;
    close();
    -- close(task callback);
    -- add_on_close(task callback);
    -- remove_on_close(task callback);
  }

  interface syncable {
    extends value;
    -- extensible enum sync_type {
    --   no_sync; /* NOP */
    --   default_sync;
    --   process_sync;
    --   host_sync;
    --   cluster_sync;
    --   lan_sync;
    --  wan_sync;
    --  /*  world?... */
    --}
    -- sync(sync_type type: default_sync);
    --sync(sync_type type, task callback);
    sync();
  }

  interface input[covariant value element] {
    extends closeable, syncable;
    not_yet_implemented readonly boolean is_available;
    not_yet_implemented readonly nonnegative available;
    -- TODO: use 'positive max' instead of nonnegative
    immutable list[element] read(nonnegative max);
    -- onread(task[immutable list[data]);
    -- skip(nonnegative howmany);
  }

  interface output[contravariant value element]  {
    extends closeable, syncable;
    not_yet_implemented readonly boolean is_available;
    not_yet_implemented readonly nonnegative available;
    write(element e);
    write_all(readonly list[element] c);
    -- send(input[data] copy, task done);
  }
}
