package org.aryan.Codec;

import org.aryan.Entity.Artist;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

public class ArtistCodecProvider implements CodecProvider {

    public ArtistCodecProvider() {}

        @Override
        public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
//            System.out.println("ArtistCodecProvider: Checking for class: " + clazz.getName());

            if (clazz == Artist.class) {
//                System.out.println("ArtistCodecProvider: Providing ArtistCodec for class: " + clazz.getName());
                return (Codec<T>) new ArtistCodec(registry);
            }
//            System.out.println("ArtistCodecProvider: No codec found for class: " + clazz.getName());
            return null;
        }
}
