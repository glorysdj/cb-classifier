package cn.azure.chatbot.classifier;

import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
    private static final Logger log = LoggerFactory.getLogger(App.class);
    public static void main(String[] args) {
        Vertx vertx= Vertx.vertx();
        vertx.deployVerticle(new MainVerticle(), event -> {
            if(event.succeeded()) {
                log.info("Application started.");
            } else {
                log.error("Could not start application");
                event.cause().printStackTrace();
            }
        });
    }
}
