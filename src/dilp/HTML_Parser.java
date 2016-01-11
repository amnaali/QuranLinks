/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dilp;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;

import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;

public class HTML_Parser {
  public static void main(String[] args) {
    ParserGetter kit = new ParserGetter();
    HTMLEditorKit.Parser parser = kit.getParser();
    HTMLEditorKit.ParserCallback callback = new ReportAttributes();

    try {
      URL u = new URL("http://www.java2s.com");
      InputStream in = u.openStream();
      InputStreamReader r = new InputStreamReader(in);
      parser.parse(r, callback, false);
    } catch (IOException e) {
      System.err.println(e);
    }
  }
}

class ReportAttributes extends HTMLEditorKit.ParserCallback {

  public void handleStartTag(HTML.Tag tag, MutableAttributeSet attributes, int position) {
    this.listAttributes(attributes);
  }

  private void listAttributes(AttributeSet attributes) {
    Enumeration e = attributes.getAttributeNames();
    while (e.hasMoreElements()) {
      Object name = e.nextElement();
      Object value = attributes.getAttribute(name);
      if (!attributes.containsAttribute(name.toString(), value)) {
        System.out.println("containsAttribute() fails");
      }
      if (!attributes.isDefined(name.toString())) {
        System.out.println("isDefined() fails");
      }
      System.out.println(name + "=" + value);
    }
  }

  public void handleSimpleTag(HTML.Tag tag, MutableAttributeSet attributes, int position) {
    this.listAttributes(attributes);
  }
}

class ParserGetter extends HTMLEditorKit {
  public HTMLEditorKit.Parser getParser() {
    return super.getParser();
  }
}