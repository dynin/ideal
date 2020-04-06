-- Copyright 2014 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

use clean_slate;

namespace ideal {
  namespace library;
}

target generate_library: generate_java(ideal.library);

target print_elements: print_source(ideal.library.elements);

target document_elements: print_documentation(ideal.library.elements);

target document_library: print_documentation(ideal.library);
