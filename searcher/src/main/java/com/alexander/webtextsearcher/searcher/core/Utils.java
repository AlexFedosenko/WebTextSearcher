package com.alexander.webtextsearcher.searcher.core;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static List<String> findUrls(String inputText) {
        List<String> resultList = new ArrayList<String>();
        String VALID_CHARS = "-\\w+&@#/%=~()|";
        String VALID_NON_TERMINAL = "?!:,.;";
        Pattern URI_FINDER_PATTERN = Pattern.compile("\\(*https?://["+ VALID_CHARS + VALID_NON_TERMINAL + "]*[" +VALID_CHARS + "]", Pattern.CASE_INSENSITIVE );
        Matcher m = URI_FINDER_PATTERN.matcher(inputText);
        while (m.find()) {
            String item = m.group();
            try {
                URL url = new URL(item);
                resultList.add(url.toString());
            } catch (MalformedURLException e) {
                // If there was an URL that was not it!...
                System.out.print(item + " ");
            }
        }
//        String [] parts = inputText.split("\\s+");
//
//        // Attempt to convert each item into an URL.
//        for( String item : parts ) {
//            if (item.startsWith(Consts.URL)) {
//                try {
//                    URL url = new URL(item);
//                    resultList.add(url.toString());
//                } catch (MalformedURLException e) {
//                    // If there was an URL that was not it!...
//                    System.out.print(item + " ");
//                }
//            }
//        }
        return resultList;
    }

    public static List<String> findTargetText(String inputText, String targetText) {
        List<String> resultList = new ArrayList<String>();
        Pattern pattern = Pattern.compile(
                "# Match a sentence ending in punctuation or EOS.\n" +
                        "[^.!?\\s]    # First char is non-punct, non-ws\n" +
                        "[^.!?]*      # Greedily consume up to punctuation.\n" +
                        "(?:          # Group for unrolling the loop.\n" +
                        "  [.!?]      # (special) inner punctuation ok if\n" +
                        "  (?!['\"]?\\s|$)  # not followed by ws or EOS.\n" +
                        "  [^.!?]*    # Greedily consume up to punctuation.\n" +
                        ")*           # Zero or more (special normal*)\n" +
                        "[.!?]?       # Optional ending punctuation.\n" +
                        "['\"]?       # Optional closing quote.\n" +
                        "(?=\\s|$)",
                Pattern.MULTILINE | Pattern.COMMENTS);
        Matcher m = pattern.matcher(inputText);
        while (m.find()) {
            String sentence = m.group();
            if (sentence.contains(targetText)) {
                resultList.add(sentence);
            }
        }
        return resultList;
    }

    public static void setListViewHeightBasedOnChildren(final ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
}
