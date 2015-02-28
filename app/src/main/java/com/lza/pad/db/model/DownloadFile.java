package com.lza.pad.db.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 11/26/14.
 */
@DatabaseTable(tableName = "download_file")
public class DownloadFile {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private String fileName;

    @DatabaseField
    private String filePath;

    /**
     * 0 - 期刊
     * 1 - 文献
     * 2 - 电子书
     */
    @DatabaseField
    private int type;

    @DatabaseField
    private String artId;

    @DatabaseField
    private String artTitle;

    @DatabaseField
    private String artDescription;

    @DatabaseField
    private String imgPath;

    public DownloadFile() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getArtId() {
        return artId;
    }

    public void setArtId(String artId) {
        this.artId = artId;
    }

    public String getArtTitle() {
        return artTitle;
    }

    public void setArtTitle(String artTitle) {
        this.artTitle = artTitle;
    }

    public String getArtDescription() {
        return artDescription;
    }

    public void setArtDescription(String artDescription) {
        this.artDescription = artDescription;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    @Override
    public boolean equals(Object o) {
        return o != null &&
                o instanceof DownloadFile &&
                ((DownloadFile) o).getFileName().equals(fileName);
    }
}
