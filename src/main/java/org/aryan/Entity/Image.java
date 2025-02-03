package org.aryan.Entity;

import java.net.URL;
import jakarta.validation.constraints.NotBlank;

public class Image {
    @NotBlank(message = "URL is mandatory")
    private URL url;
    private Integer height;
    private Integer width;

    public Image(URL url, Integer height, Integer width) {
        this.url = url;
        this.height = height;
        this.width = width;
    }
    public Image(){
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }
}
