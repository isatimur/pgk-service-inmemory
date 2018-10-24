package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.inject.Inject;
import com.typesafe.config.Config;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import play.cache.AsyncCacheApi;
import play.cache.NamedCache;
import play.libs.ws.WSClient;
import play.mvc.Controller;
import play.mvc.Result;
import scala.concurrent.ExecutionContext;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * RedisController. Created at 10/24/2018 1:42 PM by @author Timur Isachenko
 * @isatimur | † be patient and test it! †
 */

@Api(value = "Cache Controller. Делает много важного и интересного", produces = "application/json", consumes = "application/json")
public class RedisController extends Controller {

    private final WSClient wsClient;
    private final Config config;
    @NamedCache("local")
    private final AsyncCacheApi asyncCacheApi;
    private final ExecutionContext executionContext;

    @Inject
    public RedisController(final WSClient wsClient, final Config config, final AsyncCacheApi asyncCacheApi, final ExecutionContext executionContext) {
        this.wsClient = wsClient;
        this.config = config;
        this.asyncCacheApi = asyncCacheApi;
        this.executionContext = executionContext;
    }

    @ApiOperation(value = "заглушка для docker", hidden = true)
    public Result index() {
        return ok();
    }

    @ApiOperation(value = "get")
    public CompletionStage<Result> get(@ApiParam(value = "key", required = true) String key) {
        CompletionStage<Object> result = asyncCacheApi.get(key);
        return result.thenApply(v -> {
            if (v instanceof ObjectNode) {
                return ok((JsonNode) v);
            } else if (v instanceof String) {
                return ok((String) v);
            }
            return ok("Empty result");
        });

    }

    @ApiOperation(value = "set", consumes = "application/json")
    @ApiImplicitParams(
            {@ApiImplicitParam(
                    name = "body",
                    dataType = "String",
                    required = true,
                    paramType = "body",
                    value = "Данные для сохранения в Redis"
            )}
    )
    public CompletionStage<Result> set(@ApiParam(value = "key", required = true) String key) {
        JsonNode value = request().body().asJson();
        if (value == null) {
            return CompletableFuture.supplyAsync(() -> notAcceptable("body"));
        }
        if (value instanceof TextNode) {
            String resultString = ((TextNode) value).asText();
            return asyncCacheApi.set(key, resultString).thenApply(done -> created("Created"));
        } else if (value instanceof ObjectNode) {
            ObjectNode resultObject = ((ObjectNode) value);
            return asyncCacheApi.set(key, resultObject).thenApply(done -> created("Created"));
        }

        return asyncCacheApi.set(key, value).thenApply(done -> created("Created"));
    }

    @ApiOperation(value = "remove")
    public CompletionStage<Result> remove(@ApiParam(value = "key", required = true) String key) {
        return asyncCacheApi.remove(key).thenCompose(done -> remove("Removed"));
    }

}
