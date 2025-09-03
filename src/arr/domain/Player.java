package arr.domain;

public class Player {
    private final String id;
    private final String teamId;
    private String name;
    private int jerseyStock;

    public Player(String id, String teamId, String name, int jerseyStock) {
        if (id == null || id.trim().isEmpty()) throw new IllegalArgumentException("id");
        if (teamId == null || teamId.trim().isEmpty()) throw new IllegalArgumentException("teamId");
        if (name == null || name.trim().isEmpty()) throw new IllegalArgumentException("name");
        if (jerseyStock < 0) throw new IllegalArgumentException("stock");
        this.id = id; this.teamId = teamId; this.name = name; this.jerseyStock = jerseyStock;
    }

    public String getId() { return id; }
    public String getTeamId() { return teamId; }
    public String getName() { return name; }
    public void setName(String n) { if (n == null || n.trim().isEmpty()) throw new IllegalArgumentException("name"); this.name = n; }
    public int getJerseyStock() { return jerseyStock; }
    public void setJerseyStock(int s) { if (s < 0) throw new IllegalArgumentException("stock"); this.jerseyStock = s; }

    @Override public String toString() { return "Player{id=" + id + ", teamId=" + teamId + ", name=" + name + ", stock=" + jerseyStock + "}"; }
}
