package model.material;

public class FlowerMaterial extends Material {
    private String color;
    private String size;

    public FlowerMaterial(String id, String name, double unitCost, int stock, String color, String size) {
        super(id, name, MaterialType.FLOWER, unitCost, stock);
        this.color = color;
        this.size = size;
    }

    public String getColor() { return color; }
    public String getSize() { return size; }
    public void setColor(String color) { this.color = color; }
    public void setSize(String size) { this.size = size; }
}
