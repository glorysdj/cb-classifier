package cn.azure.chatbot.classifier;

import io.vertx.core.Launcher;

public class App {
    public static void main(String[] args) {
        System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.SLF4JLogDelegateFactory");
        if (args.length == 0) {
            Launcher.main(new String[]{"run", "cn.azure.chatbot.classifier.MainVerticle"});
        } else {
            Launcher.main(args);
        }
    }
}
