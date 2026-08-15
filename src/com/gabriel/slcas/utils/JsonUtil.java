package com.gabriel.slcas.utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Simple JSON parser and writer used for saving and loading library data.
 */
public final class JsonUtil {

    private JsonUtil() { }

    // Writing

    public static String escape(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            switch (c) {
                case '"': sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (c < 0x20) sb.append(String.format("\\u%04x", (int) c));
                    else sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String quote(String s) {
        return "\"" + escape(s) + "\"";
    }

    // Parsing

    public static Object parse(String text) {
        Parser p = new Parser(text);
        p.skipWhitespace();
        Object value = p.parseValue();
        return value;
    }

    private static class Parser {
        private final String s;
        private int pos = 0;

        Parser(String s) { this.s = s; }

        void skipWhitespace() {
            while (pos < s.length() && Character.isWhitespace(s.charAt(pos))) pos++;
        }

        char peek() { return s.charAt(pos); }

        Object parseValue() {
            skipWhitespace();
            char c = peek();
            if (c == '{') return parseObject();
            if (c == '[') return parseArray();
            if (c == '"') return parseString();
            if (c == 't' || c == 'f') return parseBoolean();
            if (c == 'n') { pos += 4; return null; }
            return parseNumber();
        }

        Map<String, Object> parseObject() {
            Map<String, Object> map = new LinkedHashMap<>();
            pos++; // {
            skipWhitespace();
            if (peek() == '}') { pos++; return map; }
            while (true) {
                skipWhitespace();
                String key = parseString();
                skipWhitespace();
                pos++; // :
                Object value = parseValue();
                map.put(key, value);
                skipWhitespace();
                if (peek() == ',') { pos++; continue; }
                if (peek() == '}') { pos++; break; }
            }
            return map;
        }

        List<Object> parseArray() {
            List<Object> list = new ArrayList<>();
            pos++; // [
            skipWhitespace();
            if (peek() == ']') { pos++; return list; }
            while (true) {
                Object value = parseValue();
                list.add(value);
                skipWhitespace();
                if (peek() == ',') { pos++; continue; }
                if (peek() == ']') { pos++; break; }
            }
            return list;
        }

        String parseString() {
            pos++; // opening quote
            StringBuilder sb = new StringBuilder();
            while (peek() != '"') {
                char c = s.charAt(pos);
                if (c == '\\') {
                    pos++;
                    char esc = s.charAt(pos);
                    switch (esc) {
                        case 'n': sb.append('\n'); break;
                        case 'r': sb.append('\r'); break;
                        case 't': sb.append('\t'); break;
                        case '"': sb.append('"'); break;
                        case '\\': sb.append('\\'); break;
                        case '/': sb.append('/'); break;
                        case 'u':
                            String hex = s.substring(pos + 1, pos + 5);
                            sb.append((char) Integer.parseInt(hex, 16));
                            pos += 4;
                            break;
                        default: sb.append(esc);
                    }
                } else {
                    sb.append(c);
                }
                pos++;
            }
            pos++; // closing quote
            return sb.toString();
        }

        Boolean parseBoolean() {
            if (s.startsWith("true", pos)) { pos += 4; return Boolean.TRUE; }
            pos += 5;
            return Boolean.FALSE;
        }

        Double parseNumber() {
            int start = pos;
            while (pos < s.length() && (Character.isDigit(peek()) || peek() == '-' || peek() == '+' || peek() == '.' || peek() == 'e' || peek() == 'E')) {
                pos++;
            }
            return Double.parseDouble(s.substring(start, pos));
        }
    }

    // Helper methods for working with parsed JSON
    @SuppressWarnings("unchecked")
    public static Map<String, Object> asMap(Object o) { return (Map<String, Object>) o; }

    @SuppressWarnings("unchecked")
    public static List<Object> asList(Object o) { return (List<Object>) o; }

    public static String asString(Object o) { return o == null ? null : o.toString(); }

    public static int asInt(Object o) { return o == null ? 0 : (int) Math.round((Double) o); }
}
