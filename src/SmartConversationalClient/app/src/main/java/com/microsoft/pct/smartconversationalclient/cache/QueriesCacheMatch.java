package com.microsoft.pct.smartconversationalclient.cache;

import com.microsoft.pct.smartconversationalclient.common.IQueryResult;

public class QueriesCacheMatch {
    private IQueryResult _queryResult;
    private double _matchConfidence;

    public QueriesCacheMatch(IQueryResult queryResult, double matchConfidence) {
        _queryResult = queryResult;
        _matchConfidence = matchConfidence;
    }

    public IQueryResult getQueryResult() {
        return _queryResult;
    }

    public double getMatchConfidence() {
        return _matchConfidence;
    }
}
