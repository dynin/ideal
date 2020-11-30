/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.printers;

/**
 * Used in |xref_context| to tag relationships between declarations.
 */
public enum xref_mode {
  SUCCESSOR,
  DECLARATION,
  USE,
  DIRECT_SUPERTYPE,
  INDIRECT_SUPERTYPE,
  DIRECT_OVERRIDE,
  INDIRECT_OVERRIDE;
}
