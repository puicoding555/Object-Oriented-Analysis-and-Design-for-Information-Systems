package model.material;

public class DecorationMaterial extends Material {
    public DecorationMaterial(String id, String name, double unitCost, int stock) {
        super(id, name, MaterialType.DECORATION, unitCost, stock);
    }
}
