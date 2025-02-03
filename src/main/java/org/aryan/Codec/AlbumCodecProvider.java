package org.aryan.Codec;

import org.aryan.Entity.Album;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

public class AlbumCodecProvider implements CodecProvider {

    @Override
    public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
//        System.out.println("AlbumCodecProvider: Checking for class: " + clazz.getName());

        if (clazz == Album.class) {
//            System.out.println("AlbumCodecProvider: Providing ArtistCodec for class: " + clazz.getName());

            return (Codec<T>) new AlbumCodec(registry);
        }
//        System.out.println("AlbumCodecProvider: No codec found for class: " + clazz.getName());

        return null;
    }
}
