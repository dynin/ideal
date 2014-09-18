/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.experiment.mini;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class create {

  public interface source {
    @Nullable source deeper();
  }

  public static class source_text implements source {
    public final String name;
    public final String content;

    public source_text(String name, String content) {
      this.name = name;
      this.content = content;
    }

    public @Nullable source deeper() {
      return null;
    }
  }

  public static enum token_type {
    WHITESPACE,
    OPEN,
    CLOSE,
    IDENTIFIER;
  }

  public static enum notification_type {
    UNKNOWN_CHARACTER("Unknown character");

    public final String message;

    notification_type(String message) {
      this.message = message;
    }
  }

  public static class notification {
    public final notification_type type;
    public final source the_source;

    public notification(notification_type type, source the_source) {
      this.type = type;
      this.the_source = the_source;
    }

    public void report() {
      String message = type.message;

      @Nullable source deep_source = the_source;
      while(deep_source != null) {
        if (deep_source instanceof source_text) {
          message = ((source_text) deep_source).name + ": " + message;
          break;
        }
        deep_source = the_source.deeper();
      }

      System.err.println(message);
    }
  }

  public static class token {
    public final token_type type;

    public token(token_type type) {
      this.type = type;
    }
  }

  public static List<token> tokenize(source_text source) {
    String content = source.content;
    int index = 0;
    List<token> result = new ArrayList<token>();
    while (index < content.length()) {
      int start = index;
      char prefix = content.charAt(index);
      index += 1;
      if (fn_is_letter(prefix)) {
        while (index < content.length() && fn_is_letter(content.charAt(index))) {
          index += 1;
        }
        result.add(new token(token_type.IDENTIFIER));
      } else if (fn_is_whitespace(prefix)) {
        while (index < content.length() && fn_is_whitespace(content.charAt(index))) {
          index += 1;
        }
        result.add(new token(token_type.WHITESPACE));
      } else if (prefix == '(') {
        result.add(new token(token_type.OPEN));
      } else if (prefix == ')') {
        result.add(new token(token_type.CLOSE));
      } else {
        new notification(notification_type.UNKNOWN_CHARACTER, source).report();
      }
    }
    return result;
  }

  public static boolean fn_is_letter(char c) {
    return Character.isLetter(c);
  }

  public static boolean fn_is_whitespace(char c) {
    return Character.isWhitespace(c);
  }

  public static void main(String[] args) {
    String file_name = args[0];
    String file_content = "";

    try {
      file_content = read_file(file_name);
    } catch (IOException e) {
      System.err.println("Can't read " + file_name);
      System.exit(1);
    }

    source_text the_source = new source_text(file_name, file_content);
    List<token> tokens = tokenize(the_source);
    for (token the_token : tokens) {
      System.out.println(the_token.type);
    }
  }

  private static String read_file(String file_name) throws IOException {
    byte[] encoded = Files.readAllBytes(Paths.get(file_name));
    return new String(encoded, StandardCharsets.UTF_8);
  }
}
