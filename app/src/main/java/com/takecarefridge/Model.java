package com.takecarefridge;

import android.net.Uri;

public class Model {
    private Uri imageUrl;
    private String link;

    public Model(Uri imageUrl, String link) {
        this.imageUrl = imageUrl;
        this.link = link;
    }

    public Uri getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(Uri imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
