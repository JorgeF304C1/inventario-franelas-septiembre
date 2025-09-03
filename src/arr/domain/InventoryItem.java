package arr.domain;

/**
 * Item homogéneo para la cola/pila del pipeline de búsqueda.
 * Separa el "tipo" del resumen de texto para poder clasificar.
 */
public class InventoryItem {
    public enum Kind {
        FILE_HIT,   // archivo que hizo match por nombre
        LINE_HIT,   // línea dentro de un archivo que hizo match
        LEAGUE,
        TEAM,
        PLAYER,
        MATCH
    }

    private final Kind kind;
    private final String summary;

    public InventoryItem(Kind kind, String summary) {
        if (kind == null) throw new IllegalArgumentException("kind");
        if (summary == null || summary.trim().isEmpty()) throw new IllegalArgumentException("summary");
        this.kind = kind;
        this.summary = summary;
    }

    public Kind getKind() { return kind; }
    public String getSummary() { return summary; }

    @Override
    public String toString() {
        return kind + " -> " + summary;
    }
}
