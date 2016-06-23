package com.microsoft.pct.smartconversationalclient.cache.keymatch;

import android.support.v4.util.ArrayMap;

import com.microsoft.pct.smartconversationalclient.common.IQueryResult;
import com.microsoft.pct.smartconversationalclient.luis.LUISEntity;
import com.microsoft.pct.smartconversationalclient.luis.LUISIntent;
import com.microsoft.pct.smartconversationalclient.luis.LUISQueryResult;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Dictionary;
import java.util.Map;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexCacheKeyMatcher implements ICacheKeyMatcher {
    //A list of possible entites (TBD read from LUIS model configuration or external source)
    //private static final String[] ENTITES_LIST = {"kitchen","bathroom","living room","bedroom","den"};

    private static final String REGEX_TWO_WORDS = "(\\w+\\s{1}\\w+)";
    private static final double DEFAULT_CONFIDENCE = 0.9;

    private Map<String, Pattern> _keyPatternsMap;

    public RegexCacheKeyMatcher() {
        _keyPatternsMap = new ArrayMap<>();
    }

    public void addKeyMatchData(IQueryResult query) {
        // create the pattern:
        String patternStr = query.getQuery();

        for (String ent : query.getQueryEntities()) {
            patternStr = patternStr.replace(ent, REGEX_TWO_WORDS);
        }

        _keyPatternsMap.put(query.getQuery(), Pattern.compile(patternStr));
    }

    public CacheKeyMatchResult[] match(String strToMatch) {
        ArrayList<CacheKeyMatchResult> matchResults = new ArrayList<CacheKeyMatchResult>();

        for (Map.Entry<String, Pattern> entry : _keyPatternsMap.entrySet()) {
            Pattern p = entry.getValue();
            Matcher m = p.matcher(strToMatch);
            int groupCount = m.groupCount();

            //if a the query matches a known rule
            if (!m.find()) {
                continue;
            }

            double matchConfidence = DEFAULT_CONFIDENCE;

            String[] entities = new String[m.groupCount()];

            for (int groupIndex = 0; groupIndex < m.groupCount(); groupIndex++) {
                entities[groupIndex] = m.group(groupIndex + 1);
            }

            matchResults.add(new CacheKeyMatchResult(entry.getKey(), entities, matchConfidence));
        }


        CacheKeyMatchResult[] resultsArr = new CacheKeyMatchResult[matchResults.size()];
        resultsArr = matchResults.toArray(resultsArr);

        java.util.Arrays.sort(resultsArr, new Comparator<CacheKeyMatchResult>() {
            @Override
            public int compare(CacheKeyMatchResult a, CacheKeyMatchResult b) {
                return -1 * Double.compare(a.getMatchConfidence(), b.getMatchConfidence());
            }
        });

        return resultsArr;
    }
}
