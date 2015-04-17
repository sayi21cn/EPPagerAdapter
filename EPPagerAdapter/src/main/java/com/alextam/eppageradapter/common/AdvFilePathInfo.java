package com.alextam.eppageradapter.common;

/**
 * Created by Alex Tam on 2015/4/17.
 * @author Alex Tam
 */
public class AdvFilePathInfo {
    private String filePath;	   //本地存储路径
    private String fileName;	   //本地文件名
    private String fileDownUrl; //文件下载链接,这会是唯一识别该图片身份的key

    public AdvFilePathInfo(){
        super();
    }

    public AdvFilePathInfo(String filePath,String fileName, String fileDownUrl){
        super();
        this.filePath = filePath;
        this.fileName = fileName;
        this.fileDownUrl = fileDownUrl;
    }


    public String getFilePath() {
        return filePath;
    }


    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }


    public String getFileName() {
        return fileName;
    }


    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileDownUrl() {
        return fileDownUrl;
    }

    public void setFileDownUrl(String fileDownUrl) {
        this.fileDownUrl = fileDownUrl;
    }

}
