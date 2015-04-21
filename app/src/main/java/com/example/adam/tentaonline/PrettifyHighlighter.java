package com.example.adam.tentaonline;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import prettify.PrettifyParser;
import syntaxhighlight.ParseResult;
import syntaxhighlight.Parser;

/*
* Soruce code from
* Himanshu Jaju
* http://stackoverflow.com/questions/22124731/using-java-prettify-for-android-app-syntax-highlighting 18/4-15
* Modified by Adam Larsson
* */
public class PrettifyHighlighter {
    private static final Map<String, String> COLORS = buildColorsMap();

    private static final String FONT_PATTERN = "<font color=\"#%s\">%s</font>";

    private final Parser parser = new PrettifyParser();

    public String highlight(String fileExtension, String sourceCode) {
        StringBuilder highlighted = new StringBuilder();
        List<ParseResult> results = parser.parse(fileExtension, sourceCode);
        for(ParseResult result : results){
            String type = result.getStyleKeys().get(0);
            String content = sourceCode.substring(result.getOffset(), result.getOffset() + result.getLength());
            highlighted.append(String.format(FONT_PATTERN, getColor(type), content.replace(" ","&nbsp;").replace("\n","<br>").replace("\t","&emsp;")   ));
        }
        return highlighted.toString();
    }

    private String getColor(String type){
        return COLORS.containsKey(type) ? COLORS.get(type) : COLORS.get("pln");
    }

    private static Map<String, String> buildColorsMap() {
        Map<String, String> map = new HashMap<>();
        map.put("typ", "000064"); //int
        map.put("kwd", "000064"); //public,return
        map.put("lit", "ff19ff"); //Siffor
        map.put("com", "000000"); //
        map.put("str", "0000e5"); //"text"
        map.put("pun", "ff1919"); //paranteser
        map.put("pln", "000000"); //vanlig text
        return map;
    }
}