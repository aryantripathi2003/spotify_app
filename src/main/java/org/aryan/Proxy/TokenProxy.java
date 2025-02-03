package org.aryan.Proxy;

import jakarta.json.JsonArray;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.aryan.Entity.Token;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
public interface TokenProxy {
    @POST
    @Path("/token")
    Token getToken(@FormParam("grant_type") String grant_type, @FormParam("client_id") String client_id, @FormParam("client_secret") String client_secret);
}

