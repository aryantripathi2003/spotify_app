package org.aryan.Codec;

import org.aryan.Entity.ExternalUrls;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;


public class ExternalUrlsCodec implements Codec<ExternalUrls> {

        @Override
        public void encode(BsonWriter writer, ExternalUrls value, EncoderContext encoderContext) {
            if (value != null) {
                writer.writeStartDocument();
                if(value.getSpotify()!=null){
                    writer.writeName("spotify");
                    writer.writeString(value.getSpotify());
                }
                writer.writeEndDocument();
            }
        }
        @Override
        public ExternalUrls decode(BsonReader reader, DecoderContext decoderContext) {
            ExternalUrls externalUrls = new ExternalUrls();
            reader.readStartDocument();
            while(reader.readBsonType()!= BsonType.END_OF_DOCUMENT){
                if(reader.readName().equals("spotify"))
                    externalUrls.setSpotify(reader.readString());
            }
            reader.readEndDocument();
            return externalUrls;
        }
        @Override
        public Class<ExternalUrls> getEncoderClass() {
            return ExternalUrls.class;
        }
}
