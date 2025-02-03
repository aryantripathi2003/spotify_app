package org.aryan.Entity;

import jakarta.validation.constraints.NotBlank;

public class Album {
    private String album_type;
    private Integer total_tracks;
    private String[] available_markets;
    private Image[] images;
    @NotBlank(message = "Album Name is mandatory")
    private String name;
    private Artist[] artists;

    public String getAlbum_type() {
        return album_type;
    }

    public void setAlbum_type(String album_type) {
        this.album_type = album_type;
    }

    public Album(Integer total_tracks, String[] available_markets, Image[] images, String name, Artist[] artists) {
        this.total_tracks = total_tracks;
        this.available_markets = available_markets;
        this.images = images;
        this.name = name;
        this.artists = artists;
    }

    public Album() {
    }

    public Integer getTotal_tracks() {
        return total_tracks;
    }

    public void setTotal_tracks(Integer total_tracks) {
        this.total_tracks = total_tracks;
    }

    public String[] getAvailable_markets() {
        return available_markets;
    }

    public void setAvailable_markets(String[] available_markets) {
        this.available_markets = available_markets;
    }

    public Image[] getImages() {
        return images;
    }

    public void setImages(Image[] images) {
        this.images = images;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Artist[] getArtists() {
        return artists;
    }

    public void setArtists(Artist[] artists) {
        this.artists = artists;
    }
}
