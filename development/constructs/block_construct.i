-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

construct_data class block_construct {
  readonly list[annotation_construct] annotations;
  readonly list[construct] body;

  overload block_construct(readonly list[annotation_construct] annotations,
      readonly list[construct] body, origin the_origin) {
    super(the_origin);
    this.annotations = annotations;
    assert body is_not null;
    this.body = body;
  }

  overload block_construct(readonly list[construct] body, origin the_origin) {
    this(empty[annotation_construct].new(), body, the_origin);
  }

  readonly list[construct] children() {
    result : base_list[construct].new();
    result.append_all(annotations !> readonly list[construct]);
    result.append_all(body);
    return result;
  }
}
