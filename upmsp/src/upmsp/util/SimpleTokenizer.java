package upmsp.util;

import java.util.*;

/**
 * This class is a Simple Tokenizer implementation. Simple but yet very useful
 * to quickly parse Strings and CSV files.
 *
 * @author Tulio Toffolo
 */
public class SimpleTokenizer {

    public final String str;
    public final char[] separators;

    private int start, end = -1;

    /**
     * Instantiates a new SimpleTokenizer.
     *
     * @param str the String to parse.
     */
    public SimpleTokenizer(String str) {
        this.str = str;
        this.separators = new char[]{ ' ', '\t', '\n' };
    }

    /**
     * Instantiates a new SimpleTokenizer.
     *
     * @param str        the String to parse.
     * @param separators the separators to be considered.
     */
    public SimpleTokenizer(String str, String separators) {
        this.str = str;
        this.separators = separators.toCharArray();
    }


    /**
     * Returns if there is more token.
     *
     * @return true if there is more token or false otherwise.
     */
    public boolean hasToken() {
        start = end + 1;
        while (start < str.length() && isSeparator(str.charAt(start))) start++;
        if (start < str.length()) {
            end = start - 1;
            return true;
        }
        return false;
    }

    /**
     * Gets the next token as an integer.
     *
     * @return the next token.
     */
    public int nextInt() {
        while (start < str.length()) {
            try {
                String token = nextToken().trim();
                return Integer.parseInt(token);
            }
            catch (Exception ignored) {}
        }
        throw new NoSuchElementException();
    }

    /**
     * Gets the next token as a String.
     *
     * @return the next token.
     */
    public String nextToken() {
        skipToken();
        return str.substring(start, end);
    }

    /**
     * Skip the next token.
     */
    public void skipToken() {
        start = end + 1;
        while (start < str.length() && isSeparator(str.charAt(start))) start++;
        end = start + 1;
        while (end < str.length() && !isSeparator(str.charAt(end))) end++;
    }


    private boolean isSeparator(char c) {
        for (char separator : separators)
            if (c == separator)
                return true;
        return false;
    }
}
