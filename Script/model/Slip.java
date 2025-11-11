package model;

import java.time.LocalDateTime;

public class Slip {
    private int id;
    private String filename;
    private LocalDateTime uploadedAt;

    public Slip(int id, String filename, LocalDateTime uploadedAt) {
        this.id = id;
        this.filename = filename;
        this.uploadedAt = uploadedAt;
    }

    public int getId() { return id; }
    public String getFilename() { return filename; }
    public LocalDateTime getUploadedAt() { return uploadedAt; }

    public void setFilename(String filename) { this.filename = filename; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
}
