package org.aryan.Main;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;

@QuarkusMain
public class Main {
    public static void main(String[] args) {
        Quarkus.run(MainQuarkus.class,args);
    }

    public static class MainQuarkus implements QuarkusApplication {
        @Override
        public int run(String... args) throws Exception {
            System.out.println("System UP");
            Quarkus.waitForExit();
            return 0;
        }
    }
}
