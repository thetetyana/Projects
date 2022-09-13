package A3;

/*-------------------------------------------------------------------------*
 *---									---*
 *---		TableElementInterpreter.java				---*
 *---									---*
 *---	    This file defines a class that parses scientific values	---*
 *---	from sites like "https://en.wikipedia.org/wiki/Mercury_(planet)"---*
 *---	into their values, units and error bars, and prints them with	---*
 *---	their associated attributes.					---*
 *---									---*
 *---	----	----	----	----	----	----	----	----	---*
 *---									---*
 *---	Version 1a		2022 February 6		Joseph Phillips	---*
 *---									---*
 *-------------------------------------------------------------------------*/

import java.io.*; // PrintStream, IOException, etc.
import java.util.*;
import java.util.regex.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

class TableElementInterpreter {
    // I. Constants:
    // PURPOSE: To tell the URL to analyze.
    final static String DEFAULT_URL_STR = "https://en.wikipedia.org/wiki/" + "Mercury_(planet)";

    // PURPOSE: To
    final public static String DEFAULT_CONTENT_ID_STR = "content";

    // PURPOSE: To represent the +/- sign.
    final public static char PLUS_MINUS_CHAR = '\u00B1';

    // PURPOSE: To represent the approximately equals sign.
    final public static char APPROXIMATELY_EQUALS_CHAR = '\u2248';

    // PURPOSE: To represent the degree symbol.
    final public static char DEGREES_CHAR = '\u00B0';

    // PURPOSE: To represent the minutes symbol.
    final public static char MINUTES_CHAR = '′';

    // PURPOSE: To represent the begin bracket
    final public static char BEGIN_BRACKET_CHAR = '\u005B';

    // II. Variables:
    // PURPOSE: To hold the parsing of the URL to parse.
    private Document document_ = null;

    // PURPOSE: To
    private String contentIdStr_ = DEFAULT_CONTENT_ID_STR;

    // PURPOSE: To hold the default subject.
    private String defaultSubject_ = "";

    // III. Protected methods:
    // PURPOSE: To return an AnnotatedNumber instance corresponding to the
    // value, units and error bars of 'tableElement'.
    // -------------------------------------------------------------------------------------------------------
    protected AnnotatedNumber cleanAndParseTableElement(Element tableElement) {
        Elements children = tableElement.children();

        String toParse = children.get(1).text().trim();

        String[] words = toParse.split("\\s+");
        String value = words[0];
        String units = "";

        // cleaning the code
        if (words.length > 1) {
            units = words[1];
        }

        if (value.contains("[")) {
            value = value.substring(0, value.indexOf('['));
        } else if (units.contains("[")) {
            units = units.substring(0, units.indexOf('['));
        }
        if (value.contains("°")) {
            units = "°";
            value = value.substring(0, value.length() - 1);
        }

        // negative numbers
        if (value.contains("–")) {
            value = value.replace("–", "-");
        } else if ((!Character.isDigit(value.charAt(0)))) {
            value = "-" + value.substring(1);
        }

        // exponents
        if (value.contains("×")) {
            value = value.substring(0, value.indexOf("×")) + "e" + value.substring(value.indexOf("10") + 2);
        }
        if (value.contains("′")) {
            units = "'";
            value = value.substring(0, value.length() - 1);
        } else if (value.contains("″")) {
            units = "\"";
            value = value.substring(0, value.length() - 1);
        }
        if (units.contains("(")) {
            units = "";
        }

        if (value.contains("h")) {
            value = words[0].substring(0, words[0].indexOf("h")) + " " + words[1].substring(0, words[1].indexOf("m"))
                    + " " + words[2].substring(0, words[2].indexOf("s"));
            units = "h/m/s";
        }
        
        AnnotatedNumber num = new AnnotatedNumber(value, units);
        return num;
    }

    // PURPOSE: To handle Html table 'table' with attributes (mostly) in the
    // leftmost column. No return value.
    // -------------------------------------------------------------------------------------------------------
    protected void findDataInTableWithLeftmostColumnHeadings(Element table) {
        // YOUR CODE HERE
        Elements rows = table.getElementsByTag("tr");
        String label;

        for (Element row : rows) {
            Elements children = row.children();
            AnnotatedNumber num = new AnnotatedNumber("", "");

            if (children.size() == 2 && (Character.isLetter(children.get(1).text().charAt(0)))
                    || row.text().contains("ronun")) {
            }

            else if (children.size() == 2 && children.get(0).tagName().equals("th")
                    && children.get(1).tagName().equals("td")) {

                num = cleanAndParseTableElement(row);
                label = children.get(0).text();
                System.out.print(label + " is ");
                System.out.println(num.toString());
            }
        }
    }

    // PURPOSE: To find and parse the table whose class name is "infobox".
    // No parameters. No return value.
    // -------------------------------------------------------------------------------------------------------
    protected void findAndParseTable() {
        // YOUR CODE HERE
        Element content;
        content = document_.getElementById(contentIdStr_);

        Elements tables = (content == null)
                ? document_.getElementsByTag("table")
                : content.getElementsByTag("table");

        for (Element table : tables) {
            if (table.className().equals("infobox")) {
                findDataInTableWithLeftmostColumnHeadings(table);
            }

        }
    }

    // IV. Constructor(s), factory(s), etc.
    // PURPOSE: To initialize 'this'. No parameters. No return value.
    public TableElementInterpreter(String url, String contentIdStr) {
        // I. Application validity check:

        // II. Initialize member vars:
        // II.A. Initialize 'document_':
        try {
            defaultSubject_ = url.substring(url.lastIndexOf('/') + 1);
            document_ = Jsoup.connect(url).get();
        } catch (Exception error) {
            System.err.println(error.toString());
            System.exit(1);
        }

        // II.B. Initialize 'contentIdStr':
        contentIdStr_ = contentIdStr;

    }

    // V. Accessor(s):
    // PURPOSE: To return the parsing of the URL to parse. No parameters.
    protected Document getDocument() {
        return (document_);
    }

    // PURPOSE: To
    protected String getContentIdStr() {
        return (contentIdStr_);
    }

    // PURPOSE: To return the default subject.
    protected String getDefaultSubject() {
        return (defaultSubject_);
    }

    // VI. Mutator(s):

    // VII. Method(s) that do main and misc. work of class:
    // PURPOSE:
    public static void main(String args[]) {
        String urlStr = (args.length == 0)
                ? DEFAULT_URL_STR
                : args[0];
        String contentStr = DEFAULT_CONTENT_ID_STR;
        TableElementInterpreter toAnalyze = new TableElementInterpreter(urlStr,
                contentStr);

        toAnalyze.findAndParseTable();
    }

}
