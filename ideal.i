-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

use clean_slate;

namespace ideal {
  namespace library;
  namespace runtime;
  namespace machine;
  namespace development;
  namespace documentation;
}

target analyze_library: analyze(ideal.library);

target analyze_runtime: analyze(ideal.runtime);

target analyze_all: analyze(ideal);

target print_elements: print_source(ideal.library.elements);

target generate_elements: generate_java(ideal.library.elements);

target generate_library: generate_java(ideal.library);

target generate_runtime: generate_java(ideal.runtime);

target generate_test: generate_java(ideal.development.futures.test_futures);
--target generate_test: generate_java(ideal.runtime.elements.test_list);

target generate_array: generate_java(ideal.development.flavors.base_flavor_profile);

target generate_texts: generate_java(ideal.runtime.texts);

target generate_reflections: generate_java(ideal.runtime.reflections);

target generate_development: generate_java(ideal.development);

target generate_cache: generate_java(ideal.development.flavors.base_flavor_profile);

target generate_all: generate_java(ideal.library, ideal.runtime, ideal.development);

target print_declarations: generate_java(ideal.development.declarations.type_declaration);

target document_elements: print_documentation(ideal.library.elements);

target document_library: print_documentation(ideal.library);

target document_runtime: print_documentation(ideal.runtime);

target document_librun: print_documentation(ideal.library, ideal.runtime);

target document_development: print_documentation(ideal.development);

target document_all: print_documentation(ideal.library, ideal.runtime, ideal.development,
    ideal.documentation);

target document_site: print_documentation(ideal.documentation, ideal.library,
    ideal.runtime);
