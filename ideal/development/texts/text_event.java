/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.texts;

import ideal.library.elements.*;

/**
 * A text fragment can be a balanced |text_fragment|, or standalone
 * |start_element| and |end_element|.
 */
public interface text_event extends deeply_immutable_data,
    stringable {
}
