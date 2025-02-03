package org.aryan.Entity;

import jakarta.validation.constraints.NotBlank;

public class Artist {
    private String href;
    private String id;
    @NotBlank(message = "Artist Name is mandatory")
    private String name;
    private String type;
    private String uri;
    private ExternalUrls external_urls;

    public Artist(String href, String id, String name, String type, String uri, ExternalUrls external_urls) {
        this.href = href;
        this.id = id;
        this.name = name;
        this.type = type;
        this.uri = uri;
        this.external_urls = external_urls;
    }

    public Artist() {
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public ExternalUrls getExternal_urls() {
        return external_urls;
    }

    public void setExternal_urls(ExternalUrls external_urls) {
        this.external_urls = external_urls;
    }
}
