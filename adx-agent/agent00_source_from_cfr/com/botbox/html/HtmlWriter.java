/*
 * Decompiled with CFR 0_110.
 */
package com.botbox.html;

import com.botbox.html.TableStyle;
import com.botbox.util.ArrayUtils;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Writer;

public class HtmlWriter {
    private static final boolean DEBUG = false;
    private static final String EOL = "\r\n";
    public static final int NORMAL = 0;
    public static final int BORDERED = 1;
    public static final int LINED = 2;
    private static final int BODY = 1;
    private static final int TABLE = 2;
    private static final int COLGROUP = 3;
    private static final int FORM = 4;
    private static final int TAG_OPEN = 1024;
    private static final int CACHE_ATTR = 4096;
    private static final int PENDING = 5120;
    private static final int NEWLINE = 8192;
    private static final int TABLE_TR = 16384;
    private static final int TABLE_TD = 32768;
    private static final int TABLE_TH = 65536;
    private Writer out;
    private boolean autoflush;
    private boolean inError;
    private char[] buffer = new char[1024];
    private int size = 0;
    private String[] outerAttrCache = null;
    private int outerAttrSize = 0;
    private String outerAttributes;
    private String[] innerAttrCache = null;
    private int innerAttrSize = 0;
    private String innerAttributes;
    private int[] modes = new int[6];
    private int modeCounter = 0;
    private TableStyle currentTableStyle;
    private String heading1Start = "<p><font size='+3' face=arial><b>";
    private String heading1End = "</b></font><br><br>";
    private String heading2Start = "<p><font size='+2' face=arial><b>";
    private String heading2End = "</b></font><br><br>";
    private String heading3Start = "<p><font size='+1' face=arial><b>";
    private String heading3End = "</b></font><br><br>";
    private String heading4Start = "<p><font face=arial><b>";
    private String heading4End = "</b></font><br><br>";

    public HtmlWriter() {
    }

    public HtmlWriter(OutputStream out) {
        this(out, true);
    }

    public HtmlWriter(OutputStream out, boolean autoflush) {
        this.out = new BufferedWriter(new OutputStreamWriter(out));
        this.autoflush = autoflush;
    }

    public HtmlWriter(Writer out) {
        this(out, true);
    }

    public HtmlWriter(Writer out, boolean autoflush) {
        this.out = out;
        this.autoflush = autoflush;
    }

    public boolean checkError() {
        return this.inError;
    }

    public void ensureCapacity(int newSize) {
        if (newSize > this.buffer.length) {
            this.expandBuffer(newSize);
        }
    }

    public void write(Writer out) throws IOException {
        out.write(this.buffer, 0, this.size);
    }

    public int size() {
        return this.size;
    }

    public void flush() {
        if (this.out != null) {
            try {
                this.out.write(this.buffer, 0, this.size);
                this.size = 0;
                this.out.flush();
            }
            catch (IOException e) {
                this.inError = true;
            }
        }
    }

    public void close() {
        int mode = this.popMode();
        if (mode >= 0) {
            do {
                this.handleMode(mode);
            } while ((mode = this.popMode()) >= 0);
        }
        this.flush();
        if (this.out != null) {
            try {
                try {
                    this.out.close();
                }
                catch (IOException e) {
                    this.inError = true;
                    this.out = null;
                }
            }
            finally {
                this.out = null;
            }
        }
    }

    public String toString() {
        return new String(this.buffer, 0, this.size);
    }

    public HtmlWriter pageStart(String title) {
        return this.pageStart(title, null);
    }

    public HtmlWriter pageStart(String title, String headData) {
        this.checkMode();
        this.a("<html>").a("\r\n").a("<head>");
        if (title != null) {
            this.a("<title>").a(title).a("</title>").a("\r\n");
        }
        if (headData != null) {
            this.a(headData).a("\r\n");
        }
        this.a("</head>").a("\r\n").a("<body");
        this.pushMode(9217);
        return this;
    }

    public HtmlWriter pageEnd() {
        int mode = this.popMode(1);
        if (mode >= 0) {
            this.handleMode(mode);
        }
        return this;
    }

    private void pageEnd(int mode) {
        this.a("\r\n").a("</body>").a("\r\n").a("</html>");
    }

    public HtmlWriter table() {
        return this.table(0, null);
    }

    public HtmlWriter table(String attributes) {
        return this.table(0, attributes);
    }

    public HtmlWriter table(int type) {
        return this.table(type, null);
    }

    public HtmlWriter table(int type, String attributes) {
        this.checkMode();
        this.currentTableStyle = this.getTableStyle(type);
        this.innerAttributes = this.currentTableStyle.getAttributes();
        this.outerAttributes = this.currentTableStyle.getOuterAttributes();
        this.a("<table");
        if (this.currentTableStyle.isDoubleTable()) {
            this.pushMode(13314 | type << 24);
        } else {
            this.pushMode(8194 | type << 24);
            this.pushMode(this.innerAttributes != null ? 13312 : 9216);
        }
        if (attributes != null) {
            this.attr(attributes);
        }
        return this;
    }

    private TableStyle getTableStyle(int type) {
        switch (type) {
            case 0: {
                return TableStyle.getNormalTable();
            }
            case 1: {
                return TableStyle.getBorderTable();
            }
            case 2: {
                return TableStyle.getLineTable();
            }
        }
        System.err.println("HtmlWriter: could not find table style " + type);
        return TableStyle.getNormalTable();
    }

    private void updateTable() {
        int mode = this.peekMode(2);
        if (mode >= 0) {
            this.currentTableStyle = this.getTableStyle(mode >> 24 & 255);
        }
    }

    private int endData(int mode) {
        if ((mode & 65536) != 0) {
            this.a("</th>");
            mode -= 65536;
        }
        if ((mode & 32768) != 0) {
            this.a("</td>");
            mode -= 32768;
        }
        return mode;
    }

    private int endRow(int mode) {
        if (((mode = this.endData(mode)) & 16384) != 0) {
            this.a("</tr>").a("\r\n");
            mode -= 16384;
        }
        return mode;
    }

    public HtmlWriter tableEnd() {
        int mode = this.popMode(2);
        if (mode >= 0) {
            this.handleMode(mode);
        }
        return this;
    }

    private void tableEnd(int mode) {
        this.endRow(mode);
        if (this.currentTableStyle.isDoubleTable()) {
            this.a("</table></td></tr></table>");
        } else {
            this.a("</table>");
        }
        this.updateTable();
    }

    private int tableHeaderEnd(int mode) {
        if (this.currentTableStyle.isDoubleTable()) {
            this.a("><tr><td>").a("<table");
        }
        return mode | 8192;
    }

    public HtmlWriter tr() {
        int mode = this.popMode(2);
        if (mode >= 0) {
            mode = this.endRow(mode);
            this.a("<tr");
            this.pushMode(mode | 16384);
            this.innerAttributes = this.currentTableStyle.getTrAttributes();
            this.pushMode(this.innerAttributes != null ? 5120 : 1024);
        }
        return this;
    }

    public HtmlWriter th() {
        return this.th(null, null);
    }

    public HtmlWriter th(String text) {
        return this.th(text, null);
    }

    public HtmlWriter th(String text, String attributes) {
        this.td("<th", text, attributes, 65536);
        return this;
    }

    public HtmlWriter td() {
        return this.td(null, null);
    }

    public HtmlWriter td(String text) {
        return this.td(text, null);
    }

    public HtmlWriter td(String text, String attributes) {
        this.td("<td", text, attributes, 32768);
        return this;
    }

    private void td(String tag, String text, String attributes, int tagType) {
        int mode = this.popMode(2);
        if (mode >= 0) {
            String tdAtts;
            if (((mode = this.endData(mode)) & 16384) == 0) {
                String trAtts = this.currentTableStyle.getTrAttributes();
                this.a("<tr");
                if (trAtts != null) {
                    this.a(' ').a(trAtts);
                }
                this.a('>');
                mode |= 16384;
            }
            this.a(tag);
            this.pushMode(mode | tagType);
            String string = tdAtts = tagType == 65536 ? this.currentTableStyle.getThAttributes() : this.currentTableStyle.getTdAttributes();
            if (text != null && text.length() > 0) {
                if (attributes != null && attributes.length() > 0) {
                    this.pushMode(tdAtts != null ? 5120 : 1024);
                    this.innerAttributes = tdAtts;
                    this.attr(attributes);
                } else {
                    if (tdAtts != null) {
                        this.a(' ').a(tdAtts);
                    }
                    this.a('>');
                }
                this.text(text);
            } else {
                this.pushMode(tdAtts != null ? 5120 : 1024);
                this.innerAttributes = tdAtts;
                if (attributes != null && attributes.length() > 0) {
                    this.attr(attributes);
                }
            }
        }
    }

    public HtmlWriter colgroup(int span) {
        return this.colgroup(span, null);
    }

    public HtmlWriter colgroup(int span, String attributes) {
        int mode = this.popMode(2);
        if (mode >= 0) {
            mode = this.endRow(mode);
            this.a("<colgroup span=").a(span);
            if (attributes != null && attributes.length() > 0) {
                this.a(' ').a(attributes);
            }
            this.pushMode(mode);
            this.pushMode(9219);
        }
        return this;
    }

    private int colgroupHeaderEnd(int mode) {
        this.a("></colgroup");
        return 0;
    }

    public HtmlWriter form() {
        return this.form(null, null, null);
    }

    public HtmlWriter form(String action) {
        return this.form(action, null, null);
    }

    public HtmlWriter form(String action, String method) {
        return this.form(action, method, null);
    }

    public HtmlWriter form(String action, String method, String attributes) {
        this.checkMode();
        this.a("<form");
        if (action != null && action.length() > 0) {
            this.a(" action='").a(action).a('\'');
        }
        if (method != null && method.length() > 0) {
            this.a(" method='").a(method).a('\'');
        }
        if (attributes != null && attributes.length() > 0) {
            this.a(' ').a(attributes);
        }
        this.pushMode(8196);
        this.pushMode(9216);
        return this;
    }

    public HtmlWriter formEnd() {
        int mode = this.popMode(4);
        if (mode >= 0) {
            this.handleMode(mode);
        }
        return this;
    }

    private void formEnd(int mode) {
        this.a("</form>").a("\r\n");
    }

    public HtmlWriter h1(String text) {
        this.checkMode();
        return this.a("\r\n").a(this.heading1Start).a(text).a(this.heading1End).a("\r\n").addNewLine();
    }

    public HtmlWriter h2(String text) {
        this.checkMode();
        return this.a("\r\n").a(this.heading2Start).a(text).a(this.heading2End).a("\r\n").addNewLine();
    }

    public HtmlWriter h3(String text) {
        this.checkMode();
        return this.a("\r\n").a(this.heading3Start).a(text).a(this.heading3End).a("\r\n").addNewLine();
    }

    public HtmlWriter h4(String text) {
        this.checkMode();
        return this.a("\r\n").a(this.heading4Start).a(text).a(this.heading4End).a("\r\n").addNewLine();
    }

    public HtmlWriter tag(String name) {
        return this.tag(name, null);
    }

    public HtmlWriter tag(String name, String attributes) {
        this.checkMode();
        this.a('<').a(name);
        this.pushMode(1024);
        if (attributes != null && attributes.length() > 0) {
            this.attr(attributes);
        }
        return this;
    }

    public HtmlWriter tag(char name) {
        return this.tag(name, null);
    }

    public HtmlWriter tag(char name, String attributes) {
        this.checkMode();
        this.a('<').a(name);
        this.pushMode(1024);
        if (attributes != null && attributes.length() > 0) {
            this.attr(attributes);
        }
        return this;
    }

    public HtmlWriter tagEnd(String name) {
        this.checkMode();
        return this.a('<').a('/').a(name).a('>').a("\r\n");
    }

    public HtmlWriter tagEnd(char name) {
        this.checkMode();
        return this.a('<').a('/').a(name).a('>').a("\r\n");
    }

    public HtmlWriter comment(String comment) {
        this.checkMode();
        return this.a("\r\n").a("<!-- ").a(comment).a(" -->").a("\r\n");
    }

    public HtmlWriter p() {
        this.checkMode();
        this.a("<p");
        this.pushMode(9216);
        return this;
    }

    public HtmlWriter attr(String name, int value) {
        return this.attr(name, Integer.toString(value));
    }

    public HtmlWriter attr(String name, long value) {
        return this.attr(name, Long.toString(value));
    }

    public HtmlWriter attr(String name, String value) {
        int mode = this.peekMode();
        if (mode <= 0 || (mode & 5120) == 0) {
            System.err.println("HtmlWriter: could not add attribute outside tag: " + name + "='" + value + '\'');
            return this;
        }
        if ((mode & 4096) != 0) {
            if (this.outerAttributes != null) {
                this.parseAttributes(this.outerAttributes, 0);
                this.outerAttributes = null;
            }
            if (this.innerAttributes != null) {
                this.parseAttributes(this.innerAttributes, 1);
                this.innerAttributes = null;
            }
            if ((mode & 2) != 0 && "width".equals(name)) {
                this.addInnerAttr("width", value);
                this.addOuterAttr("width", "100%");
                return this;
            }
            this.addInnerAttr(name, value);
        } else {
            this.addAttr(name, value);
        }
        return this;
    }

    private void addOuterAttr(String name, String value) {
        int size = this.outerAttrSize * 2;
        int index = ArrayUtils.keyValuesIndexOf(this.outerAttrCache, 2, 0, size, name);
        if (index >= 0) {
            this.outerAttrCache[index + 1] = value;
        } else {
            if (this.outerAttrCache == null) {
                this.outerAttrCache = new String[10];
            } else if (size == this.outerAttrCache.length) {
                this.outerAttrCache = (String[])ArrayUtils.setSize(this.outerAttrCache, size + 10);
            }
            this.outerAttrCache[size] = name;
            this.outerAttrCache[size + 1] = value;
            ++this.outerAttrSize;
        }
    }

    private void flushOuterAttributes() {
        if (this.outerAttrSize > 0) {
            int index = 0;
            int n = this.outerAttrSize * 2;
            while (index < n) {
                this.addAttr(this.outerAttrCache[index], this.outerAttrCache[index + 1]);
                this.outerAttrCache[index + 1] = null;
                this.outerAttrCache[index] = null;
                index += 2;
            }
            this.outerAttrSize = 0;
        }
    }

    private void addInnerAttr(String name, String value) {
        int size = this.innerAttrSize * 2;
        int index = ArrayUtils.keyValuesIndexOf(this.innerAttrCache, 2, 0, size, name);
        if (index >= 0) {
            this.innerAttrCache[index + 1] = value;
        } else {
            if (this.innerAttrCache == null) {
                this.innerAttrCache = new String[10];
            } else if (size == this.innerAttrCache.length) {
                this.innerAttrCache = (String[])ArrayUtils.setSize(this.innerAttrCache, size + 10);
            }
            this.innerAttrCache[size] = name;
            this.innerAttrCache[size + 1] = value;
            ++this.innerAttrSize;
        }
    }

    private void flushInnerAttributes() {
        if (this.innerAttrSize > 0) {
            int index = 0;
            int n = this.innerAttrSize * 2;
            while (index < n) {
                this.addAttr(this.innerAttrCache[index], this.innerAttrCache[index + 1]);
                this.innerAttrCache[index + 1] = null;
                this.innerAttrCache[index] = null;
                index += 2;
            }
            this.innerAttrSize = 0;
        }
    }

    private void addAttr(String name, String value, int type) {
        if (type == 2) {
            this.attr(name, value);
        } else if (type == 0) {
            this.addOuterAttr(name, value);
        } else {
            this.addInnerAttr(name, value);
        }
    }

    private void addAttr(String name, String value) {
        int len;
        int valLen;
        int nameLen = name.length();
        if (value == null || value.length() == 0) {
            valLen = 0;
            len = 1 + name.length();
        } else {
            valLen = value.length();
            len = 1 + name.length() + 1 + 1 + valLen * 2 + 1;
        }
        if ((len += this.size) > this.buffer.length) {
            this.expandBuffer(len);
        }
        this.buffer[this.size++] = 32;
        name.getChars(0, nameLen, this.buffer, this.size);
        this.size += nameLen;
        if (valLen > 0) {
            this.buffer[this.size++] = 61;
            if (value.indexOf(39) < 0) {
                this.buffer[this.size++] = 39;
                value.getChars(0, valLen, this.buffer, this.size);
                this.size += valLen;
                this.buffer[this.size++] = 39;
            } else if (value.indexOf(34) < 0) {
                this.buffer[this.size++] = 34;
                value.getChars(0, valLen, this.buffer, this.size);
                this.size += valLen;
                this.buffer[this.size++] = 34;
            } else {
                this.buffer[this.size++] = 34;
                int i = 0;
                while (i < valLen) {
                    char c = value.charAt(i);
                    if (c == '\"') {
                        this.buffer[this.size++] = 92;
                    }
                    this.buffer[this.size++] = c;
                    ++i;
                }
                this.buffer[this.size++] = 34;
            }
        }
    }

    public HtmlWriter attr(String attributes) {
        int mode = this.peekMode();
        if (mode <= 0 || (mode & 5120) == 0) {
            System.err.println("HtmlWriter: could not add attribute outside tag: " + attributes);
            return this;
        }
        if ((mode & 4096) != 0) {
            this.parseAttributes(attributes, 2);
        } else {
            this.a(' ').a(attributes);
        }
        return this;
    }

    private void parseAttributes(String attributes, int type) {
        int len = attributes.length();
        int mode = 0;
        boolean inValue = false;
        boolean isStuffed = false;
        char quoteDelimiter = ' ';
        int start = 0;
        String name = null;
        int i = 0;
        int n = attributes.length();
        while (i < n) {
            char c = attributes.charAt(i);
            if (c == '\\') {
                if (mode == 1 || mode == 2) {
                    ++i;
                    isStuffed = true;
                }
            } else {
                switch (mode) {
                    String value;
                    case 0: {
                        if (c <= ' ') break;
                        if (c == '\"' || c == '\'') {
                            quoteDelimiter = c;
                            mode = 2;
                            start = i + 1;
                            break;
                        }
                        mode = 1;
                        start = i;
                        break;
                    }
                    case 1: {
                        if (c <= ' ') {
                            value = isStuffed ? this.destuff(attributes, start, i) : attributes.substring(start, i);
                            isStuffed = false;
                            if (inValue) {
                                this.addAttr(name, value, type);
                                mode = 0;
                                inValue = false;
                                break;
                            }
                            mode = 3;
                            name = value;
                            break;
                        }
                        if (c != '=') break;
                        value = isStuffed ? this.destuff(attributes, start, i) : attributes.substring(start, i);
                        isStuffed = false;
                        if (inValue) {
                            this.addAttr(name, null, type);
                        } else {
                            inValue = true;
                        }
                        name = value;
                        mode = 0;
                        break;
                    }
                    case 2: {
                        if (c != quoteDelimiter) break;
                        value = isStuffed ? this.destuff(attributes, start, i) : attributes.substring(start, i);
                        isStuffed = false;
                        if (inValue) {
                            this.addAttr(name, value, type);
                            mode = 0;
                            inValue = false;
                            break;
                        }
                        mode = 3;
                        name = value;
                        break;
                    }
                    case 3: {
                        if (c <= ' ') break;
                        if (c == '=') {
                            inValue = true;
                            mode = 0;
                            break;
                        }
                        this.addAttr(name, null, type);
                        mode = 0;
                        --i;
                    }
                }
            }
            ++i;
        }
        if (mode > 0) {
            String value;
            String string = value = isStuffed ? this.destuff(attributes, start, attributes.length()) : attributes.substring(start);
            if (inValue) {
                this.addAttr(name, value, type);
            } else {
                this.addAttr(value, null, type);
            }
        } else if (inValue) {
            this.addAttr(name, null, type);
        }
    }

    private String destuff(String text, int start, int end) {
        char[] buf = new char[end - start];
        int bufLen = 0;
        int i = start;
        while (i < end) {
            char c = text.charAt(i);
            if (c != '\\') {
                buf[bufLen++] = c;
            }
            ++i;
        }
        return new String(buf, 0, bufLen);
    }

    private int wss(String text, int start, int len) {
        while (start < len && text.charAt(start) <= ' ') {
            ++start;
        }
        return start;
    }

    public HtmlWriter text(char c) {
        this.checkMode();
        return this.a(c);
    }

    public HtmlWriter text(int value) {
        return this.text(Integer.toString(value));
    }

    public HtmlWriter text(long value) {
        return this.text(Long.toString(value));
    }

    public HtmlWriter text(String text) {
        this.checkMode();
        return this.a(text);
    }

    private void expandBuffer(int minSize) {
        int newSize = this.buffer.length * 2;
        if (minSize > newSize) {
            newSize = minSize + 5;
        }
        this.buffer = ArrayUtils.setSize(this.buffer, newSize);
    }

    private HtmlWriter a(char c) {
        int len = this.size + 1;
        if (len > this.buffer.length) {
            this.expandBuffer(len);
        }
        this.buffer[this.size++] = c;
        return this;
    }

    private HtmlWriter a(int value) {
        return this.a(Integer.toString(value));
    }

    private HtmlWriter a(String text) {
        int newSize;
        int len;
        if (text == null) {
            text = String.valueOf(text);
        }
        if ((newSize = this.size + (len = text.length())) > this.buffer.length) {
            this.expandBuffer(newSize);
        }
        text.getChars(0, len, this.buffer, this.size);
        this.size = newSize;
        return this;
    }

    public HtmlWriter newLine() {
        if (this.modeCounter > 0 && (this.modes[this.modeCounter - 1] & 5120) != 0) {
            int[] arrn = this.modes;
            int n = this.modeCounter - 1;
            arrn[n] = arrn[n] | 8192;
            return this;
        }
        return this.addNewLine();
    }

    private HtmlWriter addNewLine() {
        this.a("\r\n");
        if (this.out != null) {
            try {
                this.out.write(this.buffer, 0, this.size);
                this.size = 0;
                if (this.autoflush) {
                    this.out.flush();
                }
            }
            catch (IOException e) {
                this.inError = true;
            }
        }
        return this;
    }

    private void checkMode() {
        while (this.modeCounter > 0 && (this.modes[this.modeCounter - 1] & 5120) != 0) {
            this.handleMode(this.modes[--this.modeCounter]);
        }
        int mode = this.peekMode(2);
        if (mode >= 0 && (mode & 98304) == 0) {
            this.td();
            while (this.modeCounter > 0 && (this.modes[this.modeCounter - 1] & 5120) != 0) {
                this.handleMode(this.modes[--this.modeCounter]);
            }
        }
    }

    private void pushMode(int mode) {
        if (this.modeCounter == this.modes.length) {
            this.modes = ArrayUtils.setSize(this.modes, this.modeCounter + 10);
        }
        this.modes[this.modeCounter++] = mode;
    }

    private int peekMode() {
        return this.modeCounter > 0 ? this.modes[this.modeCounter - 1] : -1;
    }

    private int peekMode(int type) {
        int len = this.modeCounter;
        while (len > 0) {
            int mode;
            if (((mode = this.modes[--len]) & 255) != type) continue;
            return mode;
        }
        return -1;
    }

    private int popMode() {
        while (this.modeCounter > 0 && (this.modes[this.modeCounter - 1] & 5120) != 0) {
            this.handleMode(this.modes[--this.modeCounter]);
        }
        int n = this.modeCounter > 0 ? this.modes[--this.modeCounter] : -1;
        return n;
    }

    private int popMode(int type) {
        while (this.modeCounter > 0 && (this.modes[this.modeCounter - 1] & 5120) != 0) {
            this.handleMode(this.modes[--this.modeCounter]);
        }
        while (this.modeCounter > 0) {
            int mode;
            if (((mode = this.modes[--this.modeCounter]) & 255) == type) {
                return mode;
            }
            this.handleMode(mode);
        }
        System.err.println("HtmlWriter: could not find tag " + type);
        return -1;
    }

    private int handleMode(int mode) {
        int type = mode & 255;
        if ((mode & 5120) != 0) {
            boolean newline;
            if ((mode & 8192) != 0) {
                newline = true;
                mode -= 8192;
            } else {
                newline = false;
            }
            if (this.outerAttrSize > 0) {
                this.flushOuterAttributes();
            } else if (this.outerAttributes != null) {
                this.a(' ').a(this.outerAttributes);
                this.outerAttributes = null;
            }
            if (type == 2) {
                mode = this.tableHeaderEnd(mode);
                type = mode & 255;
            } else if (type == 3) {
                mode = this.colgroupHeaderEnd(mode);
                type = mode & 255;
            }
            if (this.innerAttrSize > 0) {
                this.flushInnerAttributes();
            } else if (this.innerAttributes != null) {
                this.a(' ').a(this.innerAttributes);
                this.innerAttributes = null;
            }
            this.a('>');
            if (newline) {
                this.addNewLine();
            }
            if (type > 0) {
                this.pushMode(mode & -5121);
            }
            return mode;
        }
        switch (type) {
            case 1: {
                this.pageEnd(mode);
                break;
            }
            case 2: {
                this.tableEnd(mode);
                break;
            }
            case 4: {
                this.formEnd(mode);
                break;
            }
            default: {
                System.err.println("HtmlWriter: unhandled type=" + type + " mode=" + mode);
            }
        }
        if ((mode & 8192) != 0) {
            this.addNewLine();
        }
        return type;
    }
}

