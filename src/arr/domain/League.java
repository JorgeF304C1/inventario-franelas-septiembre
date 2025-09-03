package arr.domain;

public class League {
    private final String id;
    private String name;

    public League(String id, String name) {
        if (id == null || id.trim().isEmpty()) throw new IllegalArgumentException("id");
        if (name == null || name.trim().isEmpty()) throw new IllegalArgumentException("name");
        this.id = id; this.name = name;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) throw new IllegalArgumentException("name");
        this.name = name;
    }

    @Override public String toString() { return "League{id=" + id + ", name=" + name + "}"; }
}
