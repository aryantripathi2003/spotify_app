package org.aryan.Entity;

import jakarta.validation.constraints.NotBlank;

public class ExternalUrls {
    @NotBlank(message = "External URL is mandatory")
    private String spotify;

    public ExternalUrls(String spotify) {
        this.spotify = spotify;
    }

    public ExternalUrls() {
    }

    public String getSpotify() {
        return spotify;
    }

    public void setSpotify(String spotify) {
        this.spotify = spotify;
    }
}
