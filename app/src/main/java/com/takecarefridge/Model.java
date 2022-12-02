package com.takecarefridge;

public class Model {
    private String 이미지;
    private String 링크;

    public Model(String 이미지, String 링크) {
        this.이미지 = 이미지;
        this.링크 = 링크;
    }

    public String getImage() {
        return 이미지;
    }

    public void setImage(String 이미지) {
        this.이미지 = 이미지;
    }

    public String getLink() {
        return 링크;
    }

    public void setLink(String 링크) {
        this.링크 = 링크;
    }
}
