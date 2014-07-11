/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.showcase.coach.appengine;

import ideal.library.elements.*;
import ideal.library.elements.*;
import javax.annotation.Nullable;
import ideal.library.texts.*;
import ideal.runtime.elements.*;
import ideal.runtime.resources.*;
import ideal.runtime.channels.*;
import ideal.runtime.texts.*;
import ideal.development.elements.*;
import ideal.development.scanners.*;
import ideal.development.actions.*;
import ideal.development.analyzers.*;
import ideal.development.values.*;
import ideal.development.tools.*;
import ideal.development.declarations.*;
import ideal.showcase.coach.reflections.*;
import ideal.showcase.coach.webforms.*;
import ideal.showcase.coach.marshallers.*;

import java.text.*;
import java.util.Date;
import java.util.TimeZone;
import com.google.appengine.api.datastore.*;

/**
 * The framework for storing user state of an ideal framework application hosted on AppEngine.
 */
public class base_user_state implements user_state {

  public final string human;
  private final base_servlet servlet;
  private @Nullable datastore_state world_state_cache;
  private @Nullable datastore_schema compiled_datastore_schema;
  private @Nullable string quasi_persistent_world_state;
  private @Nullable DatastoreService datastore_cache;

  public base_user_state(string human, base_servlet servlet) {
    this.human = human;
    this.servlet = servlet;
  }

  @Override
  public string app_name() {
    return servlet.app_name;
  }

  @Override
  public source_content js_runtime() {
    return servlet.runtime_js;
  }

  private boolean persistence_enabled() {
    return servlet.persistence_enabled();
  }

  private String get_datastore_name() {
    return utilities.s(servlet.get_datastore_name());
  }

  @Override
  public void clear_world_cache() {
    world_state_cache = null;
  }

  private Key get_world_key() {
    return KeyFactory.createKey(get_datastore_name(), utilities.s(human));
  }

  private Key get_source_key() {
    return KeyFactory.createKey(get_datastore_name() + SOURCE_SUFFIX, utilities.s(human));
  }

  private DatastoreService get_datastore() {
    if (datastore_cache == null) {
      datastore_cache = DatastoreServiceFactory.getDatastoreService();
    }
    return datastore_cache;
  }

  private @Nullable string get_text_property(Key entity_key, String property_name) {
    try {
      Entity the_entity = get_datastore().get(entity_key);

      if (the_entity != null) {
        Object state = the_entity.getProperty(property_name);
        if (state instanceof Text) {
          return new base_string(((Text) state).getValue());
        }
      }
    } catch (EntityNotFoundException e) { }

    return null;
  }

  private @Nullable string do_get_world_state(datastore_schema decl) {
    if (!persistence_enabled() && quasi_persistent_world_state != null) {
      return quasi_persistent_world_state;
    }
    return get_text_property(get_world_key(), WORLD_STATE);
  }

  private void populate_world_state_cache(datastore_schema decl) {
    world_state_cache = new marshaller(decl).
        unmarshal_state(json_data.parse(do_get_world_state(decl)));
    if (world_state_cache == null) {
      world_state_cache = decl.make_new_state();
    }
  }

  private datastore_schema get_bundled_world() {
    return servlet.bundled_world;
  }

  private string get_release_version() {
    return get_bundled_world().version;
  }

  @Override
  public datastore_state get_world_state() {
    if (world_state_cache == null) {
      datastore_schema decl;
      if (compiled_datastore_schema != null) {
        decl = compiled_datastore_schema;
      } else {
        decl = get_bundled_world();
      }

      populate_world_state_cache(decl);
      if (!utilities.eq(world_state_cache.get_source_version(), decl.version)) {
        if (compiled_datastore_schema == null) {
          maybe_recompile_declaration();
        }
        world_state_cache.reset_source_version();
        set_world_state(world_state_cache);
      }
    }

    return world_state_cache;
  }

  private void maybe_recompile_declaration() {
    try {
      Entity source_entity = get_datastore().get(get_source_key());
      if (source_entity == null) {
        return;
      }

      string release_version = new base_string((String) source_entity.getProperty(RELEASE_VERSION));
      if (utilities.eq(release_version, get_release_version())) {
        string source_version = new base_string((String) source_entity.getProperty(SOURCE_VERSION));
        string source = new base_string(((Text) source_entity.getProperty(SOURCE)).getValue());
        do_recompile_declaration(source_version, source);
      }
    } catch (EntityNotFoundException e) { }
  }

  private void do_recompile_declaration(string source_version, string new_source) {
    source_content source = new source_content(new name(source_version), new_source);

    translation_result result = servlet.translate_source(source, source_version, null);
    assert result.is_success();

    datastore_schema decl = result.get_new_declaration();
    decl.version = source_version;
    compiled_datastore_schema = decl;

    populate_world_state_cache(decl);
  }

  private string marshal_world(datastore_state world) {
    return new marshaller(world.get_schema()).marshal_state(world).stringify();
  }

  @Override
  public void set_world_state(datastore_state world) {
    if (persistence_enabled()) {
      do_save_world_state(new Entity(get_world_key()), world);
    } else {
      quasi_persistent_world_state = marshal_world(world);
    }
    world_state_cache = world;
  }

  @Override
  public void checkpoint_world_state(datastore_state world) {
    if (persistence_enabled()) {
      do_save_world_state(new Entity(get_datastore_name() + CHECKPOINT_SUFFIX), world);
    }
  }

  private static final String CHECKPOINT_SUFFIX = "/checkpoint";
  private static final String SOURCE_SUFFIX = "/source";

  private static final String HUMAN = "human";
  private static final String MODIFIED = "modified";
  private static final String WORLD_STATE = "world_state";
  private static final String SOURCE_VERSION = "source_version";
  private static final String RELEASE_VERSION = "release_version";
  private static final String SOURCE = "source";

  private void do_save_world_state(Entity world_entity, datastore_state world) {
    world_entity.setProperty(HUMAN, utilities.s(human));
    world_entity.setProperty(MODIFIED, new Date());
    world_entity.setProperty(WORLD_STATE, new Text(utilities.s(marshal_world(world))));

    get_datastore().put(world_entity);
  }

  private void do_save_source(datastore_schema decl) {
    Entity source_entity = new Entity(get_source_key());

    source_entity.setProperty(HUMAN, utilities.s(human));
    source_entity.setProperty(MODIFIED, new Date());
    source_entity.setProperty(SOURCE_VERSION, utilities.s(decl.version));
    source_entity.setProperty(RELEASE_VERSION, utilities.s(get_release_version()));
    source_entity.setProperty(SOURCE, new Text(utilities.s(decl.source.content)));

    get_datastore().put(source_entity);
  }

  private void delete_source() {
    get_datastore().delete(get_source_key());
  }

  @Override
  public translation_result translate_source(string new_source) {
    string timestamp = render_timestamp();
    string new_name = new base_string("compiling", timestamp);
    source_content source = new source_content(new name(new_name), new_source);

    translation_result result = servlet.translate_source(source, new_name, timestamp);
    if (result.is_success()) {
      datastore_schema decl = result.get_new_declaration();
      compiled_datastore_schema = decl;
      do_save_source(decl);
      populate_world_state_cache(decl);
      set_world_state(world_state_cache);
    }
    return result;
  }

  @Override
  public void reset_source() {
    compiled_datastore_schema = null;
    delete_source();
    populate_world_state_cache(get_bundled_world());
    set_world_state(world_state_cache);
  }

  private static @Nullable SimpleDateFormat TIMESTAMP_FORMATTER;

  private static string render_timestamp() {
    if (TIMESTAMP_FORMATTER == null) {
      TIMESTAMP_FORMATTER = new SimpleDateFormat(" @hh:mm.ssaa");
      TIMESTAMP_FORMATTER.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
    }
    return new base_string(TIMESTAMP_FORMATTER.format(new Date()));
  }
}
