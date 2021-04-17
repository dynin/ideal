// Autogenerated from runtime/texts/text_library.i

package ideal.runtime.texts;

import ideal.library.elements.*;
import ideal.library.characters.*;
import ideal.library.texts.*;
import ideal.runtime.elements.*;
import ideal.library.channels.output;

public class text_library {
  public static final base_namespace HTML_NS = new base_namespace(new base_string("html"));
  public static final base_element_id HTML = new base_element_id(text_library.HTML_NS, new base_string("html"));
  public static final base_element_id HEAD = new base_element_id(text_library.HTML_NS, new base_string("head"));
  public static final base_element_id TITLE = new base_element_id(text_library.HTML_NS, new base_string("title"));
  public static final base_element_id LINK = new base_element_id(text_library.HTML_NS, new base_string("link"));
  public static final base_element_id BODY = new base_element_id(text_library.HTML_NS, new base_string("body"));
  public static final base_element_id P = new base_element_id(text_library.HTML_NS, new base_string("p"));
  public static final base_element_id DIV = new base_element_id(text_library.HTML_NS, new base_string("div"));
  public static final base_element_id H1 = new base_element_id(text_library.HTML_NS, new base_string("h1"));
  public static final base_element_id H2 = new base_element_id(text_library.HTML_NS, new base_string("h2"));
  public static final base_element_id PRE = new base_element_id(text_library.HTML_NS, new base_string("pre"));
  public static final base_element_id UL = new base_element_id(text_library.HTML_NS, new base_string("ul"));
  public static final base_element_id LI = new base_element_id(text_library.HTML_NS, new base_string("li"));
  public static final base_element_id TABLE = new base_element_id(text_library.HTML_NS, new base_string("table"));
  public static final base_element_id TR = new base_element_id(text_library.HTML_NS, new base_string("tr"));
  public static final base_element_id TH = new base_element_id(text_library.HTML_NS, new base_string("th"));
  public static final base_element_id TD = new base_element_id(text_library.HTML_NS, new base_string("td"));
  public static final base_element_id SPAN = new base_element_id(text_library.HTML_NS, new base_string("span"));
  public static final base_element_id BR = new base_element_id(text_library.HTML_NS, new base_string("br"));
  public static final base_element_id EM = new base_element_id(text_library.HTML_NS, new base_string("em"));
  public static final base_element_id A = new base_element_id(text_library.HTML_NS, new base_string("a"));
  public static final base_element_id B = new base_element_id(text_library.HTML_NS, new base_string("b"));
  public static final base_element_id U = new base_element_id(text_library.HTML_NS, new base_string("u"));
  public static final base_element_id HR = new base_element_id(text_library.HTML_NS, new base_string("hr"));
  public static final base_element_id U2 = new base_element_id(text_library.HTML_NS, new base_string("u2"));
  public static final base_attribute_id ID = new base_attribute_id(text_library.HTML_NS, new base_string("id"));
  public static final base_attribute_id NAME = new base_attribute_id(text_library.HTML_NS, new base_string("name"));
  public static final base_attribute_id CLEAR = new base_attribute_id(text_library.HTML_NS, new base_string("clear"));
  public static final base_attribute_id CLASS = new base_attribute_id(text_library.HTML_NS, new base_string("class"));
  public static final base_attribute_id STYLE = new base_attribute_id(text_library.HTML_NS, new base_string("style"));
  public static final base_attribute_id HREF = new base_attribute_id(text_library.HTML_NS, new base_string("href"));
  public static final base_attribute_id REL = new base_attribute_id(text_library.HTML_NS, new base_string("rel"));
  public static final base_attribute_id TYPE = new base_attribute_id(text_library.HTML_NS, new base_string("type"));
  public static final text_entity AMP = new text_entity(text_library.HTML_NS, new base_string("&"), new base_string("amp"));
  public static final text_entity LT = new text_entity(text_library.HTML_NS, new base_string("<"), new base_string("lt"));
  public static final text_entity GT = new text_entity(text_library.HTML_NS, new base_string(">"), new base_string("gt"));
  public static final text_entity APOS = new text_entity(text_library.HTML_NS, new base_string("\'"), new base_string("apos"));
  public static final text_entity QUOT = new text_entity(text_library.HTML_NS, new base_string("\""), new base_string("quot"));
  public static final text_entity BULL = new text_entity(text_library.HTML_NS, new base_string("*"), new base_string("bull"));
  public static final text_entity MIDDOT = new text_entity(text_library.HTML_NS, new base_string("."), new base_string("middot"));
  public static final text_entity MDASH = new text_entity(text_library.HTML_NS, new base_string("--"), new base_string("mdash"));
  public static final text_entity NBSP = new text_entity(text_library.HTML_NS, new base_string(" "), new base_string("nbsp"));
  public static final text_entity THINSP = new text_entity(text_library.HTML_NS, new base_string(" "), new base_string("thinsp;"));
  public static final text_entity LARR = new text_entity(text_library.HTML_NS, new base_string("<-"), new base_string("larr"));
  public static final text_entity UARR = new text_entity(text_library.HTML_NS, new base_string("^"), new base_string("uarr"));
  public static final text_entity RARR = new text_entity(text_library.HTML_NS, new base_string("->"), new base_string("rarr"));
  public static final text_entity DARR = new text_entity(text_library.HTML_NS, new base_string("V"), new base_string("darr"));
  public static final immutable_list<element_id> HTML_ELEMENTS = (immutable_list<element_id>) (immutable_list) new base_immutable_list<base_element_id>(new ideal.machine.elements.array<base_element_id>(new base_element_id[]{ text_library.HTML, text_library.HEAD, text_library.TITLE, text_library.LINK, text_library.BODY, text_library.P, text_library.DIV, text_library.H1, text_library.H2, text_library.PRE, text_library.UL, text_library.LI, text_library.TABLE, text_library.TR, text_library.TH, text_library.TD, text_library.SPAN, text_library.BR, text_library.EM, text_library.A, text_library.B, text_library.U, text_library.HR }));
  public static final immutable_list<attribute_id> HTML_ATTRIBUTES = (immutable_list<attribute_id>) (immutable_list) new base_immutable_list<base_attribute_id>(new ideal.machine.elements.array<base_attribute_id>(new base_attribute_id[]{ text_library.ID, text_library.NAME, text_library.CLEAR, text_library.CLASS, text_library.STYLE, text_library.HREF, text_library.REL, text_library.TYPE }));
  public static final immutable_list<special_text> HTML_ENTITIES = (immutable_list<special_text>) (immutable_list) new base_immutable_list<text_entity>(new ideal.machine.elements.array<text_entity>(new text_entity[]{ text_library.AMP, text_library.LT, text_library.GT, text_library.APOS, text_library.QUOT, text_library.BULL, text_library.MIDDOT, text_library.MDASH, text_library.NBSP, text_library.THINSP, text_library.LARR, text_library.UARR, text_library.RARR, text_library.DARR }));
  public static final string FRAGMENT_SEPARATOR = new base_string("#");
  public static final base_namespace IDEAL_TEXT = new base_namespace(new base_string("itext"));
  public static final base_element_id INDENT = new base_element_id(text_library.IDEAL_TEXT, new base_string("indent"));
  public static final base_element_id ERROR_ELEMENT = new base_element_id(text_library.IDEAL_TEXT, new base_string("_error_"));
  public static final base_attribute_id ERROR_ATTRIBUTE = new base_attribute_id(text_library.IDEAL_TEXT, new base_string("_error_"));
  public static final text_entity ERROR_ENTITY = new text_entity(text_library.IDEAL_TEXT, new base_string("_error_"), new base_string("_error_"));
}
