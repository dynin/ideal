-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Constants and a helper function for resources runtime.
namespace resource_util {

  PATH_SEPARATOR : "/";

  ROOT_CATALOG : "/";
  CURRENT_CATALOG : ".";
  PARENT_CATALOG : "..";

  UTF_8 : "UTF-8";

  TEXT_HTML : "text/html";
  TEXT_PLAIN : "text/plain";

  FILE_SCHEME : "file";
  HTTPS_SCHEME : "https";

  -- TODO: return status.
  copy(resource_identifier source, resource_identifier destination) {
    -- TODO: use bytes; fixed buffer size; handle errors.
    content : source.access_string(missing.instance).content;
    destination.access_string(missing.instance).content = content;
  }
}
