-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

import ideal.library.texts.text_fragment;
import ideal.library.messages.log_message;

--- Diagnostic messages (such as errors or warnings) generated by the ideal system.
interface notification {
  extends log_message;

  string message;
  origin origin;
  readonly list[notification] secondary;
  -- TODO: pass a render_context that contains styles?
  text_fragment render_text(boolean prefix_with_source);
  report();

  -- TODO: importance/severity level.
  -- TODO: move from string to text.
}
