package org.aryan.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

public class AlbumsWrapper {
    private Albums albums;

    public Albums getAlbums() {
        return albums;
    }

    public AlbumsWrapper() {
    }

    public AlbumsWrapper(Albums albums) {
        this.albums = albums;
    }

    public void setAlbums(Albums albums) {
        this.albums = albums;
    }

}