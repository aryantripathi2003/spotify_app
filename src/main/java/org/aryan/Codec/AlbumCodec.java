package org.aryan.Codec;

import org.aryan.Entity.Artist;
import org.aryan.Entity.Image;
import org.aryan.Entity.Album;
import org.aryan.Entity.ExternalUrls;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.types.ObjectId;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AlbumCodec implements Codec<Album> {
    private Codec<Artist> artistCodec;
    private Codec<Image> imageCodec;
    private Codec<ExternalUrls> externalUrlsCodec;

    public AlbumCodec(CodecRegistry registry) {
        this.artistCodec = registry.get(Artist.class);
        this.imageCodec = registry.get(Image.class);
    }

    public void encode(BsonWriter writer, Album value, EncoderContext encoderContext) {
        if (value != null) {
            writer.writeStartDocument();
            if(value.getAlbum_type()!=null){
                writer.writeName("album_type");
                writer.writeString(value.getAlbum_type());
            }
            if(value.getArtists()!=null){
                writer.writeName("artists");
                writer.writeStartArray();
                for (Artist artist : value.getArtists()) {
                    artistCodec.encode(writer, artist, encoderContext);
                }
                writer.writeEndArray();
            }
            if(value.getImages()!=null){
                writer.writeName("images");
                writer.writeStartArray();
                for (Image image : value.getImages()) {
                    imageCodec.encode(writer, image, encoderContext);
                }
                writer.writeEndArray();
            }
            if(value.getName()!=null) {
                writer.writeName("name");
                writer.writeString(value.getName());
            }
            if(value.getTotal_tracks()!=null){
                writer.writeName("total_tracks");
                writer.writeInt32(value.getTotal_tracks());
            }
            if(value.getAvailable_markets()!=null){
                writer.writeName("available_markets");
                writer.writeStartArray();
                for (String available_market : value.getAvailable_markets()) {
                    writer.writeString(available_market);
                }
                writer.writeEndArray();
            }
            writer.writeEndDocument();
        }
    }

    public Album decode(BsonReader reader, DecoderContext decoderContext) {
        Album album = new Album();
        reader.readStartDocument();
        while(reader.readBsonType()!=BsonType.END_OF_DOCUMENT){
            switch (reader.readName()){
                case "_id":
                    reader.skipValue();
                    break;
                case "album_type":
                    album.setAlbum_type(reader.readString());
                    break;
                case "artists":
//                    reader.skipValue();
                    reader.readStartArray();
                    List<Artist> artists = new ArrayList<>();
                    while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                        artists.add(artistCodec.decode(reader, decoderContext));
                    }
                    album.setArtists(artists.toArray(new Artist[0]));
                    reader.readEndArray();
                    break;
                case "images":
//                    reader.skipValue();
                    reader.readStartArray();
                    List<Image> images = new ArrayList<>();
                    while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                        images.add(imageCodec.decode(reader, decoderContext));
                    }
                    album.setImages(images.toArray(new Image[0]));
                    reader.readEndArray();
                    break;
                case "name":
                    album.setName(reader.readString());
                    break;
                case "total_tracks":
                    album.setTotal_tracks(reader.readInt32());
                    break;
                case "available_markets":
                    reader.readStartArray();
                    List<String> available_markets = new ArrayList<>();
                    while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                        available_markets.add(reader.readString());
                    }
                    album.setAvailable_markets(available_markets.toArray(new String[0]));
                    reader.readEndArray();
                    break;
            }
        }
        reader.readEndDocument();
        return album;
    }

    public Class<Album> getEncoderClass() {
        return Album.class;
    }

}
