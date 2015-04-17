package com.alextam.eppageradapter.common;

/**
 * Created by  Alex Tam on 2015/4/17.
 * @author Alex Tam
 */
public class Advertisement {
    private String name;		  //焦点图的主题名
    private String url;		  //焦点图点击后的跳转链接
    private String imageUrl;	  //焦点图的下载地址

    public Advertisement(String name, String url, String imageUrl){
        super();
        this.name = name;
        this.url = url;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}
