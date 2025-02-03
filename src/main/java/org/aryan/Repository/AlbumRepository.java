package org.aryan.Repository;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoTimeoutException;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.ws.rs.core.Response;
import org.aryan.Codec.*;
import org.aryan.Entity.Album;
import org.aryan.Entity.Artist;
import org.aryan.Exception.BusinessException;
import org.bson.BsonDocument;
import org.bson.BsonInt64;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

@ApplicationScoped
public class AlbumRepository {

    private MongoCollection<Album> albumCollection;
//    private MongoCollection<Artist> artistCollection;
    private MongoClient mongoClient;

    @ConfigProperty(name="quarkus.mongodb.connection-string")
    private String uri;

    @PostConstruct
    public void init(){
        System.out.println("Connecting to MongoClient");
        System.out.println("Initialising CodecProvider and CodecRegistry");
        MongoClient mongoClient = MongoClients.create(uri);
            CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
                    CodecRegistries.fromCodecs(new ExternalUrlsCodec(), new ImageCodec()),
                    CodecRegistries.fromProviders(new ArtistCodecProvider(),new AlbumCodecProvider()),
                    MongoClientSettings.getDefaultCodecRegistry());
            MongoDatabase database = mongoClient.getDatabase("Albums").withCodecRegistry(codecRegistry);
//            MongoDatabase database=mongoClient.getDatabase("Artists").withCodecRegistry(codecRegistry);
            MongoCollection<Album> collection = database.getCollection("Albums", Album.class);
//            MongoCollection<Artist> collection=database.getCollection("Artists", Artist.class);
            this.mongoClient=mongoClient;
            this.albumCollection= database.getCollection("Albums", Album.class);
//            this.artistCollection=database.getCollection("Artists", Artist.class);
            if(!isConnected()){
                System.out.println("MongoDB is not connected");
            } else{
                System.out.println("Completed Initialisation");
            }

    }

    public boolean isConnected() {
        MongoDatabase database=mongoClient.getDatabase("Albums");
        Bson command = new BsonDocument("ping", new BsonInt64(1));
        try {
            database.runCommand(command);
        } catch (MongoTimeoutException e) {
            return false;
        }
        return true;

    }

    public void close(@Observes ShutdownEvent ev){
        System.out.println("Disconnecting from MongoClient");
        mongoClient.close();
    }

    public Album getAlbumById(String id) throws BusinessException {
        Album album=albumCollection.find(Filters.eq("_id", new ObjectId(id))).first();
        if(album==null){
            throw new BusinessException("No album found with id "+id, Response.Status.NOT_FOUND.getStatusCode());
        }
        return album;
    }

    public List<Album> getAllAlbum(){
        return albumCollection.find().into(new ArrayList<>());
    }

    public Album getAlbumByName(String name) throws BusinessException{
        Album pojo=albumCollection.find(Filters.eq("name", name)).first();
        if(pojo==null){
            throw new BusinessException("No album found with name "+name,Response.Status.NOT_FOUND.getStatusCode());
        }
        return pojo;
    }

    public String addAlbum(Album album) throws BusinessException{
        try{
            BsonValue val=albumCollection.insertOne(album).getInsertedId();
            return val.asObjectId().getValue().toString();
        } catch (Exception e){
            System.out.println(e.getMessage());
            throw new BusinessException("Error in adding album",Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        }
    }

//    public String addArtist(Artist artist) throws BusinessException{
//        try{
//            BsonValue val=artistCollection.insertOne(artist).getInsertedId();
//            System.out.println(val);
//            return val.asObjectId().getValue().toString();
//        } catch (Exception e){
//            System.out.println(e.getMessage());
//            throw new BusinessException("Error in adding artist",Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
//        }
//    }

    public void updateAlbum(String id, Album pojo) throws BusinessException{
        try{
            albumCollection.replaceOne(Filters.eq("_id", new ObjectId(id)), pojo);
        } catch (Exception e){
            throw new BusinessException("No album found with id "+id,Response.Status.NOT_FOUND.getStatusCode());
        }
    }

    public void deleteAlbum(String id) throws BusinessException{
        try{
            albumCollection.deleteOne(Filters.eq("_id", new ObjectId(id)));
        } catch (Exception e){
            throw new BusinessException("No db found with id "+id,Response.Status.NOT_FOUND.getStatusCode());
        }
    }

    public List<Album> getOrderedByName(){
        MongoCursor<Album> cursor = albumCollection.find().sort(new Document("name",1)).iterator();
        List<Album> albums = new ArrayList<>();
        try {
            while (cursor.hasNext()) {
                albums.add(cursor.next());
            }
        } finally {
            cursor.close();
        }
        return albums;
//        return albumCollection.find().sort(new Document("name",1)).into(new ArrayList<>());
    }


}
