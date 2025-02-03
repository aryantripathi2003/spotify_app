package org.aryan.Proxy;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.aryan.Entity.Album;
import org.aryan.Entity.AlbumsWrapper;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

@RegisterRestClient
@Produces(MediaType.APPLICATION_JSON)
public interface SpotifyProxy {
    @GET
    @Path("/browse/new-releases")
    AlbumsWrapper getNewReleases(@HeaderParam("Authorization") String token, @QueryParam("limit") int limit, @QueryParam("offset") int offset);
}

