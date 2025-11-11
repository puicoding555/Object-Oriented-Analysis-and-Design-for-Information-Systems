package model.material;

public class WrappingPaperType extends Material {
    private String finish;      // ด้าน/มัน ฯลฯ
    private String paperColor;

    public WrappingPaperType(String id, String name, double unitCost, int stock, String finish, String paperColor) {
        super(id, name, MaterialType.WRAP_PAPER, unitCost, stock);
        this.finish = finish;
        this.paperColor = paperColor;
    }

    public String getFinish() { return finish; }
    public String getPaperColor() { return paperColor; }
    public void setFinish(String finish) { this.finish = finish; }
    public void setPaperColor(String paperColor) { this.paperColor = paperColor; }
}
