package com.ebixcash.aayu.util;

import java.io.UnsupportedEncodingException;
import java.util.BitSet;
import java.util.StringTokenizer;

public class Encoder {
  static final int caseDiff = 32;
  
  static BitSet dontNeedEncoding = new BitSet(256);
  
  static {
    for (int i = 97; i <= 122; i++)
      dontNeedEncoding.set(i); 
    for (int j = 65; j <= 90; j++)
      dontNeedEncoding.set(j); 
    for (int k = 48; k <= 57; k++)
      dontNeedEncoding.set(k); 
    dontNeedEncoding.set(32);
    dontNeedEncoding.set(45);
    dontNeedEncoding.set(95);
    dontNeedEncoding.set(46);
    dontNeedEncoding.set(42);
  }
  
  private static String a(char[] ac) {
    StringBuffer stringbuffer = new StringBuffer(ac.length);
    for (int i = 0; i < ac.length; i++) {
      int j = ac[i];
      if (dontNeedEncoding.get(j)) {
        if (j == 32)
          j = 43; 
        stringbuffer.append((char)j);
      } else {
        stringbuffer.append('%');
        byte byte0 = 1;
        if (j > 255) {
          byte0 = 3;
          stringbuffer.append('u');
        } 
        for (int k = byte0; k >= 0; k--) {
          int l = ((j & 61440 >> (3 - k) * 4) >> k * 4) + 48;
          if (l > 57)
            l += 7; 
          stringbuffer.append((char)l);
        } 
      } 
    } 
    return stringbuffer.toString();
  }
  
  public static String decodeCookie(String s) {
    if (s == null)
      return null; 
    StringBuffer stringbuffer = new StringBuffer();
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      switch (c) {
        case '+':
          stringbuffer.append(' ');
          break;
        case '%':
          try {
            if (s.charAt(i + 1) == 'u') {
              stringbuffer.append(
                  (char)Integer.parseInt(s
                    .substring(i + 2, i + 6), 16));
              i += 5;
              break;
            } 
            stringbuffer.append(
                (char)Integer.parseInt(s
                  .substring(i + 1, i + 3), 16));
            i += 2;
          } catch (NumberFormatException numberformatexception) {
            throw new IllegalArgumentException();
          } 
          break;
        default:
          stringbuffer.append(c);
          break;
      } 
    } 
    String s1 = stringbuffer.toString();
    return s1;
  }
  
  public static String encodeCookie(String s) {
    if (s == null)
      return null; 
    return a(s.toCharArray());
  }
  
  public static String encodeHTML(String s) {
    if (s == null)
      return null; 
    char[] ac = new char[s.length()];
    s.getChars(0, s.length(), ac, 0);
    StringBuffer stringbuffer = new StringBuffer(ac.length + 50);
    for (int i = 0; i < ac.length; i++) {
      switch (ac[i]) {
        case '<':
          stringbuffer.append("&lt;");
          break;
        case '>':
          stringbuffer.append("&gt;");
          break;
        case '&':
          stringbuffer.append("&amp;");
          break;
        case '"':
          stringbuffer.append("&quot;");
          break;
        default:
          stringbuffer.append(ac[i]);
          break;
      } 
    } 
    return stringbuffer.toString();
  }
  
  public static String encodeJS(String s) {
    if (s == null)
      return null; 
    char[] ac = new char[s.length()];
    s.getChars(0, s.length(), ac, 0);
    StringBuffer stringbuffer = new StringBuffer(ac.length + 50);
    for (int i = 0; i < ac.length; i++) {
      switch (ac[i]) {
        case '\r':
          stringbuffer.append("\\n");
          if (i < ac.length - 1 && ac[i + 1] == '\n')
            i++; 
          break;
        case '\n':
          stringbuffer.append("\\n");
          break;
        case '"':
        case '\'':
        case '\\':
          stringbuffer.append('\\');
          stringbuffer.append(ac[i]);
          break;
        default:
          stringbuffer.append(ac[i]);
          break;
      } 
    } 
    return stringbuffer.toString();
  }
  
  public static String encodeURL(String s) {
    char[] ac;
    if (s == null)
      return null; 
    try {
      byte[] abyte0 = s.getBytes("UTF-8");
      ac = new char[abyte0.length];
      for (int i = abyte0.length - 1; i >= 0; i--)
        ac[i] = (char)(abyte0[i] & 0xFF); 
    } catch (UnsupportedEncodingException unsupportedencodingexception) {
      ac = s.toCharArray();
    } 
    return a(ac);
  }
  
  public static String quoteObjectProp(String s) {
    String s1 = s;
    StringTokenizer stringtokenizer = new StringTokenizer(s, "'");
    boolean flag = true;
    while (stringtokenizer.hasMoreTokens()) {
      if (!flag) {
        s1 = s1 + "''" + stringtokenizer.nextToken();
        continue;
      } 
      flag = false;
      s1 = stringtokenizer.nextToken();
    } 
    s1 = "'" + s1 + "'";
    return s1;
  }
}
