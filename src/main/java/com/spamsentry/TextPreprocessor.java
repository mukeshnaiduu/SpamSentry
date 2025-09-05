package com.spamsentry;

import java.util.regex.Pattern;

public final class TextPreprocessor {
    private static final Pattern URL = Pattern.compile("https?://\\S+|www\\.\\S+", Pattern.CASE_INSENSITIVE);
    private static final Pattern EMAIL = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}", Pattern.CASE_INSENSITIVE);
    private static final Pattern NUMBER = Pattern.compile("\\b\\d+[\\d.,]*\\b");
    private static final Pattern NON_WORD = Pattern.compile("[^a-z0-9_ ]+");
    private static final Pattern MULTI_SPACE = Pattern.compile("\\s+");

    private TextPreprocessor() {}

    public static String normalize(String s) {
        if (s == null) return "";
        String t = s;
        // mask URLs
        t = URL.matcher(t).replaceAll(" __URL__ ");
        // mask emails
        t = EMAIL.matcher(t).replaceAll(" __EMAIL__ ");
        // mask numbers
        t = NUMBER.matcher(t).replaceAll(" __NUM__ ");
        // lowercase
        t = t.toLowerCase();
        // remove non-word characters (keep underscores and spaces and alphanum)
        t = NON_WORD.matcher(t).replaceAll(" ");
        // collapse spaces
        t = MULTI_SPACE.matcher(t).replaceAll(" ").trim();
        return t;
    }
}