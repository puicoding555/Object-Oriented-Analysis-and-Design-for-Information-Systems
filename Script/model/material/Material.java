package model.material;

public abstract class Material {
    protected String id;
    protected String name;
    protected MaterialType type;
    protected double unitCost;
    protected int stock;

    public Material(String id, String name, MaterialType type, double unitCost, int stock) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.unitCost = unitCost;
        this.stock = stock;
    }

    public void adjustStock(int delta) {
        this.stock += delta;
        if (this.stock < 0) this.stock = 0;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public MaterialType getType() { return type; }
    public double getUnitCost() { return unitCost; }
    public int getStock() { return stock; }

    public void setName(String name) { this.name = name; }
    public void setUnitCost(double unitCost) { this.unitCost = unitCost; }
}
