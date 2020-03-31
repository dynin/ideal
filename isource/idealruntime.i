-- Copyright 2014 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

use clean_slate;

namespace ideal {
  namespace library;
  namespace runtime;
  namespace machine;
}

generate_library: generate_java(ideal.library);

print_elements: print_source(ideal.library.elements);

document_elements: print_documentation(ideal.library.elements);

document_library: print_documentation(ideal.library);

generate_runtime : generate_java(ideal.runtime);

generate_all: generate_java(ideal.library, ideal.runtime);

document_all: print_documentation(ideal.library, ideal.runtime);

document_runtime: print_documentation(ideal.runtime);

generate_texts : generate_java(ideal.runtime.texts);

generate_reflections : generate_java(ideal.runtime.reflections);
