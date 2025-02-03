package org.aryan.Resource;

import io.quarkus.scheduler.Scheduled;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.aryan.Entity.Album;
import org.aryan.Entity.AlbumsWrapper;
import org.aryan.Entity.Token;
import org.aryan.Exception.BusinessException;
import org.aryan.Proxy.SpotifyProxy;
import org.aryan.Proxy.TokenProxy;
import org.aryan.Repository.AlbumRepository;
import org.aryan.Service.ElasticsearchService;
import org.aryan.Service.KafkaServiceConsumer;
import org.aryan.Service.KafkaServiceProducer;
import org.aryan.Service.Utils.DeadLetterTopicConsumer;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Path("/album")
public class SpotifyResource {
    private Token token;

    @Inject
    KafkaServiceProducer kafkaProducer;

    @Inject
    AlbumRepository albumRepository;

    @Inject
    ElasticsearchService elasticsearchService;

    @Inject
    Validator validator;

    @RestClient
    @Inject
    TokenProxy tokenProxy;

    @RestClient
    @Inject
    SpotifyProxy spotifyProxy;

    @ConfigProperty(name = "spotify.client_id")
    private String clientId;

    @ConfigProperty(name = "spotify.client_secret")
    private String clientSecret;

    @Inject
    KafkaServiceConsumer kafkaConsumer;

    @Inject
    DeadLetterTopicConsumer deadLetterTopicConsumer;

    @PostConstruct
    public void startConsumer() {
        kafkaConsumer.consume();
        deadLetterTopicConsumer.dead();
    }

    @Scheduled(every = "55m")
    public void getToken() {
        System.out.println("Getting new token from Spotify");
        token = tokenProxy.getToken("client_credentials", clientId,
                clientSecret);
    }

    @POST
    @Path("/add")
    @Fallback(fallbackMethod = "fallbackaddAlbum")
    public Response addAlbum(Album album) throws URISyntaxException, BusinessException {
        Set<ConstraintViolation<Album>> validate=validator.validate(album);
        try {
            if(validate.isEmpty()) {
                kafkaProducer.produce(album);
                return Response.noContent().build();
            } else {
                String violations=validate.stream().map(violation->violation.getMessage()).collect(Collectors.joining(","));
                return Response.status(Response.Status.BAD_REQUEST).entity(violations).build();
            }
        } catch (Exception e) {
            throw new BusinessException(e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        }
    }
    private Response fallbackaddAlbum(Album album) {
        return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity("Fallback method reached - Could not add Album").build();
    }

    @GET
    @Path("/getOrdered")
    @Timeout(2000)
    @Fallback(fallbackMethod = "fallbackgetAlbums")
    public Response getAlbums() throws BusinessException {
        try {
            List<Album> albums = albumRepository.getOrderedByName();
//            List<Album> albums = albumRepository.getAllAlbum();
            return Response.status(Response.Status.OK).entity(albums).build();
        } catch (Exception e) {
            throw new BusinessException(e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        }
    }

    private Response fallbackgetAlbums() {
        return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity("Fallback method reached - Could not get albums").build();
    }

    @GET
    @Path("/new-releases/{addDb}")
    @Timeout(2000)
    @Fallback(fallbackMethod = "fallbackgetNewReleases")
    public Response getNewReleases(@PathParam("addDb") int addDb,@QueryParam("limit") int limit, @QueryParam("offset") int offset) throws BusinessException {
        try {
            AlbumsWrapper albums = spotifyProxy.getNewReleases("Bearer " + token.getAccess_token(),limit,offset);
            if (addDb == 1) {
                for (Album album : albums.getAlbums().getItems()) {
                    kafkaProducer.produce(album);
                }
            }
            return Response.status(Response.Status.OK).entity(albums).build();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new BusinessException(e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        }
    }
    private Response fallbackgetNewReleases(int addDb,int limit, int offset) {
        return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity("Fallback method reached - Could not get new releases and/or add them to db").build();
    }

    @GET
    @Path("/search/{term}")
    @Timeout(2000)
    @Fallback(fallbackMethod = "fallbacksearchAlbum")
    public Response searchAlbum(@PathParam("term") String term) throws BusinessException {
        try {
            System.out.println("Searching for term: " + term);
            List<Album> albums = elasticsearchService.search(term);
            return Response.status(Response.Status.OK).entity(albums).build();
        } catch (Exception e) {
            throw new BusinessException(e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        }
    }
    private Response fallbacksearchAlbum(String term) {
        return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity("Fallback method reached - Could not search for albums").build();
    }

//    @POST
//    @Path("/add/artist")
//    public Response addArtist(Artist artist) throws URISyntaxException, BusinessException {
//        try{
//            String id=albumRepository.addArtist(artist);
//            return Response.created(new URI("/album/get/"+id)).build();
//        } catch (Exception e){
//            throw new BusinessException(e.getMessage(),Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
//        }
//    }

}
