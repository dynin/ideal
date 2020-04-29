-- Copyright 2014-2020 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- A token is a syntactic element such as a keyword, operator, etc. 
--- Tokens are identified by |token_type|.  Some tokens (such as |SIMPLE_NAME|)
--- carry a payload.  Payload type varies based on the token type.
--- <p>
--- Scanners generate a list of tokens.
interface token[deeply_immutable data payload_type] {
  extends deeply_immutable data, position, stringable;

  token_type type;
  payload_type payload;
}
