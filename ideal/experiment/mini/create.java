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

  public static class text_position implements source {
    public final source_text the_source_text;
    public final int character_index;

    public text_position(source_text the_source_text, int character_index) {
      this.the_source_text = the_source_text;
      this.character_index = character_index;
    }

    public source deeper() {
      return the_source_text;
    }
  }

  public static enum notification_type {
    UNRECOGNIZED_CHARACTER("Unrecognized character");

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
        if (deep_source instanceof text_position) {
          text_position position = (text_position) deep_source;
          String content = position.the_source_text.content;
          int index = position.character_index;
          int line_number = 1;
          for (int i = index - 1; i >= 0; --i) {
            if (content.charAt(i) == '\n') {
              line_number += 1;
            }
          }
          StringBuilder detailed = new StringBuilder();
          detailed.append(position.the_source_text.name).append(":");
          detailed.append(line_number).append(": ");
          detailed.append(message).append('\n');

          int start_of_line = index;
          while (start_of_line > 0 && content.charAt(start_of_line - 1) != '\n') {
            start_of_line -= 1;
          }
          int spaces;
          if (index >= content.length() || content.charAt(index) == '\n') {
            detailed.append(content.substring(start_of_line, index));
            detailed.append('\n');
          } else {
            int end_of_line = index;
            while (end_of_line < (content.length() - 1) && content.charAt(end_of_line + 1) != '\n') {
              end_of_line += 1;
            }
            detailed.append(content.substring(start_of_line, end_of_line + 1));
            detailed.append('\n');
          }
          for (int i = 0; i < (index - start_of_line); ++i) {
            detailed.append(' ');
          }
          detailed.append('^');
          // Last newline is added by println().
          message = detailed.toString();
          break;
        }
        if (deep_source instanceof source_text) {
          message = ((source_text) deep_source).name + ": " + message;
          break;
        }
        deep_source = the_source.deeper();
      }

      System.err.println(message);
    }
  }

  public static enum token_type {
    WHITESPACE,
    OPEN,
    CLOSE,
    IDENTIFIER;
  }

  public static interface token extends source {
    token_type type();
  }

  public static class simple_token implements token {
    public final token_type type;
    public final source the_source;

    public simple_token(token_type type, source the_source) {
      this.type = type;
      this.the_source = the_source;
    }

    @Override
    public token_type type() {
      return type;
    }

    @Override
    public source deeper() {
      return the_source;
    }

    @Override
    public String toString() {
      return "<" + type.toString() + ">";
    }
  }

  public static class identifier_token implements token {
    public final String name;
    public final source the_source;

    public identifier_token(String name, source the_source) {
      this.name = name;
      this.the_source = the_source;
    }

    @Override
    public token_type type() {
      return token_type.IDENTIFIER;
    }

    @Override
    public source deeper() {
      return the_source;
    }

    @Override
    public String toString() {
      return "<" + type().toString() + ":" + name + ">";
    }
  }

  public static List<token> tokenize(source_text the_source_text) {
    String content = the_source_text.content;
    int index = 0;
    List<token> result = new ArrayList<token>();
    while (index < content.length()) {
      int start = index;
      char prefix = content.charAt(index);
      index += 1;
      source position = new text_position(the_source_text, start);
      if (fn_is_letter(prefix)) {
        while (index < content.length() && fn_is_letter(content.charAt(index))) {
          index += 1;
        }
        result.add(new identifier_token(content.substring(start, index), position));
      } else if (fn_is_whitespace(prefix)) {
        while (index < content.length() && fn_is_whitespace(content.charAt(index))) {
          index += 1;
        }
        result.add(new simple_token(token_type.WHITESPACE, position));
      } else if (prefix == '(') {
        result.add(new simple_token(token_type.OPEN, position));
      } else if (prefix == ')') {
        result.add(new simple_token(token_type.CLOSE, position));
      } else {
        new notification(notification_type.UNRECOGNIZED_CHARACTER, position).report();
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
      System.out.println(the_token.toString());
    }
  }

  private static String read_file(String file_name) throws IOException {
    byte[] encoded = Files.readAllBytes(Paths.get(file_name));
    return new String(encoded, StandardCharsets.UTF_8);
  }
}
