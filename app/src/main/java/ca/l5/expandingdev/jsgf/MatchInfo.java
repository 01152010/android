package ca.l5.expandingdev.jsgf;

public class MatchInfo {
    private final Expansion expansion;
    private final String matchingSection;
    public boolean actualMatch;

    public Expansion getMatchedExpansion() {
        return expansion;
    }

    public String getMatchingStringSection() {
        return matchingSection;
    }

    public MatchInfo(Expansion e, String matchingStringPart) {
        expansion = e;
        matchingSection = matchingStringPart;
    }
}
