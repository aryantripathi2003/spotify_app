package org.aryan.Codec;

import org.aryan.Entity.Image;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import java.net.URL;

public class ImageCodec implements Codec<Image> {

    @Override
    public void encode(BsonWriter writer, Image value, EncoderContext encoderContext) {
        if (value != null) {
            writer.writeStartDocument();
            if(value.getUrl()!=null){
                writer.writeName("url");
                writer.writeString(value.getUrl().toString());
            }
            if(value.getHeight()!=null){
                writer.writeName("height");
                writer.writeInt32(value.getHeight());
            }
            if(value.getWidth()!=null){
                writer.writeName("width");
                writer.writeInt32(value.getWidth());
            }
            writer.writeEndDocument();
        }
    }
    @Override
    public Image decode(BsonReader reader, DecoderContext decoderContext) {
        Image image = new Image();
        reader.readStartDocument();
        while(reader.readBsonType()!= org.bson.BsonType.END_OF_DOCUMENT){
            switch (reader.readName()){
                case "url":
                    try{
                        image.setUrl(new URL(reader.readString()));
                    } catch (Exception e){
                        System.out.println("Exception "+e);
                    }
                    break;
                case "height":
                    image.setHeight(reader.readInt32());
                    break;
                case "width":
                    image.setWidth(reader.readInt32());
                    break;
            }
        }
        reader.readEndDocument();
        return image;
    }
    @Override
    public Class<Image> getEncoderClass() {
        return Image.class;
    }
}
