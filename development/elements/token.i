-- Copyright 2014-2022 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

--- A token is a syntactic element such as a keyword, operator, etc. 
--- Tokens are identified by |token_type|.  Some tokens (such as |SIMPLE_NAME|)
--- carry a payload.  Payload type varies based on the token type.
--- <p>
--- Scanners generate a list of tokens.
--- </p>
interface token[covariant deeply_immutable data payload_type] {
  extends deeply_immutable data, mutable origin, stringable;

  token_type type;
  payload_type payload;
}
