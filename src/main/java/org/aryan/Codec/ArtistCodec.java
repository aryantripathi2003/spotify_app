package org.aryan.Codec;

import org.aryan.Entity.Artist;
import org.aryan.Entity.ExternalUrls;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;

import java.net.URL;

public class ArtistCodec implements Codec<Artist> {

    private Codec<ExternalUrls> externalUrlsCodec;

    public ArtistCodec(CodecRegistry registry) {
        this.externalUrlsCodec = registry.get(ExternalUrls.class);
    }

        @Override
        public void encode(BsonWriter writer, Artist value, EncoderContext encoderContext) {
            if (value != null) {
                writer.writeStartDocument();
                if(value.getName()!=null){
                    writer.writeName("name");
                    writer.writeString(value.getName());
                }
                if(value.getHref()!=null){
                    writer.writeName("href");
                    writer.writeString(value.getHref());
                }
                if(value.getId()!=null){
                    writer.writeName("id");
                    writer.writeString(value.getId());
                }
                if(value.getType()!=null){
                    writer.writeName("type");
                    writer.writeString(value.getType());
                }
                if(value.getUri()!=null){
                    writer.writeName("uri");
                    writer.writeString(value.getUri());
                }
                if(value.getExternal_urls()!=null){
                    writer.writeName("externalUrls");
                    externalUrlsCodec.encode(writer, value.getExternal_urls(), encoderContext);
                }
                writer.writeEndDocument();
            }
        }
        @Override
        public Artist decode(BsonReader reader, DecoderContext decoderContext) {
            Artist artist = new Artist();
            reader.readStartDocument();
            while(reader.readBsonType()!= org.bson.BsonType.END_OF_DOCUMENT){
                switch (reader.readName()){
                    case "name":
                        artist.setName(reader.readString());
                        break;
                    case "href":
                        artist.setHref(reader.readString());
                        break;
                    case "id":
                        artist.setId(reader.readString());
                        break;
                    case "type":
                        artist.setType(reader.readString());
                        break;
                    case "uri":
                        artist.setUri(reader.readString());
                        break;
                    case "externalUrls":
                        artist.setExternal_urls(externalUrlsCodec.decode(reader, decoderContext));
                        break;
                }
            }
            reader.readEndDocument();
            return artist;
        }
        @Override
        public Class<Artist> getEncoderClass() {
            return Artist.class;
        }
}
