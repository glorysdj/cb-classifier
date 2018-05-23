package cn.azure.chatbot.classifier;

import cn.azure.chatbot.classifier.models.SearchRequest;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import jersey.repackaged.com.google.common.base.MoreObjects;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MainVerticle extends AbstractVerticle {
    private final static String SERVICE_NAME = "classifier";
    private final static String VERSION = "v1";

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ssX").create();

    @Override
    public void start(Future<Void> startFuture) {
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.get("/").handler(ctx -> ctx.response()
                .putHeader("Access-Control-Allow-Origin", "*")
                .putHeader("Content-Type", "application/json; charset=utf-8")
                .end("OK"));
        router.get("/app/info").handler(ctx -> ctx.response()
                .putHeader("Access-Control-Allow-Origin", "*")
                .putHeader("Content-Type", "application/json; charset=utf-8")
                .putHeader("Content-Type", "application/json; charset=utf-8")
                .end("{\"version\": \"" + VERSION + "\", \"name\": \"" + SERVICE_NAME + "\"}"));
        router.post("/test").blockingHandler(ctx -> endWith(ctx, Classifier.classifyString(ctx.getBodyAsString())));
        // CORS and XSRF hack
        router.options("/api/classify").handler(req -> {
            req.response()
                    .putHeader("Access-Control-Allow-Origin", "*")
                    .putHeader("Access-Control-Allow-Methods", "*")
                    .putHeader("Access-Control-Allow-Headers", "*")
                    .end();
        });
        router.post("/api/classify").blockingHandler(req -> {
            SearchRequest payload = bodyAs(req, SearchRequest.class);
            List<String> tags = Classifier.classifyString(payload.getQuestion().getContent()).stream()
                    .map(s -> "category/" + s)
                    .collect(Collectors.toList());
            log.debug("Classification('%s') => '%s'", payload.getQuestion().getContent(), tags);
            payload.setTags(tags);
            endWith(req, payload);
        });

        String configURL = MoreObjects.firstNonNull(System.getenv("CONFIG_URL"), System.getProperty("config.url", ""));
        ConfigRetrieverOptions options = new ConfigRetrieverOptions();
        if (!configURL.isEmpty()) {
            try {
                URL url = new URL(configURL);
                String host = url.getHost();
                int port = url.getPort();
                String schema = url.getProtocol();
                ConfigStoreOptions httpStore = new ConfigStoreOptions()
                        .setType("http")
                        .setConfig(new JsonObject()
                                .put("host", url.getHost())
                                .put("port", url.getDefaultPort())
                                .put("path", url.getFile())
                                .put("ssl", url.getProtocol().equalsIgnoreCase("https"))
                        );
                options = options.addStore(httpStore);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        } else {
            options = options.setIncludeDefaultStores(true);
        }
        log.info("Loading config...");
        ConfigRetriever retriever = ConfigRetriever.create(vertx, options);
        retriever.getConfig(
                ar -> {
                    if (ar.failed()) {
                        log.error("Failed to load config, shutting down...");
                        vertx.close();
                    } else {
                        JsonObject config = ar.result();
                        log.info("Config loaded.");
                        vertx.executeBlocking(future -> {
                            log.info("Loading models...");
                            try {
                                prepareModels(config);
                                future.complete();
                            } catch (Exception e) {
                                e.printStackTrace();
                                future.fail(e);
                            }
                        }, r -> {
                            if (r.failed()) {
                                log.error("Failed to load models");
                            } else {
                                log.info("Models loaded.");
                                int port = config.getInteger("http.port", 8080);
                                log.info("Starting listening on {}", port);
                                vertx.createHttpServer()
                                        .requestHandler(router::accept)
                                        .listen(port, result -> {
                                            if (result.succeeded()) {
                                                startFuture.complete();
                                            } else {
                                                startFuture.fail(result.cause());
                                            }
                                        });
                            }
                        });
                    }
                }
        );
    }

    private <T> T bodyAs(@NotNull RoutingContext ctx, Class<T> c) {
        return gson.fromJson(ctx.getBodyAsString(), c);
    }

    private <T> void endWith(@NotNull RoutingContext ctx, T t) {
        ctx.response()
                .putHeader("Access-Control-Allow-Origin", "*")
                .putHeader("Content-Type", "application/json; charset=utf-8")
                .putHeader("Content-Type", "application/json; charset=utf-8")
                .end(gson.toJson(t));
    }

    private File downloadFile(String url) {
        if (url.startsWith("http")) {
            try {
                File f = File.createTempFile("prefix", null);
                log.info("Downloading {}...", url);
                FileUtils.copyURLToFile(new URL(url), f);
                log.info("Downloading completed.");
                return f;
            } catch (IOException e) {
                e.printStackTrace();
                log.error("Failed to download model from {}, shutting down...", url);
                vertx.close();
                throw new RuntimeException(e);
            }

        } else {
            return new File(url);
        }
    }

    private void prepareModels(JsonObject config) {
        String ftm = config.getString("fastText.model", "./cc.zh.300.bin");
        String model = config.getString("bigdl.model", "./faqmodel.bigdl");
        String weights = config.getString("bigdl.weight", "./faqmodel.bin");

        Classifier.initModel(downloadFile(ftm).getPath(),
                downloadFile(model).getPath(),
                downloadFile(weights).getPath(),
                config.getJsonArray("categories").stream().map(Object::toString).collect(Collectors.toList()),
                config.getInteger("tag.count", 3));
    }
}
