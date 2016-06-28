package com.microsoft.pct.smartconversationalclient.cache.keymatch;

/**
 * A Cache Key Match that can be returned by an ICacheKeyMathcer interface
 */
public class CacheKeyMatchResult {

    private double _matchConfidence;
    private String _keyMatch;
    private String[] _entities;

    public CacheKeyMatchResult(String keyMatch, String[] entities, double matchConfidence) {
        _matchConfidence = matchConfidence;
        _keyMatch = keyMatch;
        _entities = entities;
    }

    public double getMatchConfidence() {
        return _matchConfidence;
    }

    public String getKeyMatch() {
        return _keyMatch;
    }

    public String[] getEntities() {
        return _entities;
    }
}
