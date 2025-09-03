package arr.io;

public class InventoryItem {
    public enum Kind { LEAGUE, TEAM, PLAYER, MATCH }

    private final Kind kind;
    private final String summary;

    public InventoryItem(Kind kind, String summary) {
        if (kind == null) throw new IllegalArgumentException("kind");
        if (summary == null || summary.trim().isEmpty()) throw new IllegalArgumentException("summary");
        this.kind = kind; this.summary = summary;
    }

    public Kind getKind() { return kind; }
    public String getSummary() { return summary; }

    @Override public String toString() { return kind + " -> " + summary; }
}
