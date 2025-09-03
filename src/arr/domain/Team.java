package arr.domain;

public class Team {
    private final String id;
    private final String leagueId;
    private String name;

    public Team(String id, String leagueId, String name) {
        if (id == null || id.trim().isEmpty()) throw new IllegalArgumentException("id");
        if (leagueId == null || leagueId.trim().isEmpty()) throw new IllegalArgumentException("leagueId");
        if (name == null || name.trim().isEmpty()) throw new IllegalArgumentException("name");
        this.id = id; this.leagueId = leagueId; this.name = name;
    }

    public String getId() { return id; }
    public String getLeagueId() { return leagueId; }
    public String getName() { return name; }
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) throw new IllegalArgumentException("name");
        this.name = name;
    }

    @Override public String toString() { return "Team{id=" + id + ", leagueId=" + leagueId + ", name=" + name + "}"; }
}
