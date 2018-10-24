package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.typesafe.config.Config;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import play.api.cache.redis.CacheAsyncApi;
import play.libs.ws.WSClient;
import play.mvc.Controller;
import play.mvc.Result;
import scala.compat.java8.FutureConverters;
import scala.concurrent.ExecutionContext;
import scala.concurrent.duration.Duration;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

/**
 * RedisController. Created at 10/24/2018 1:42 PM by @author Timur Isachenko
 * @isatimur | † be patient and test it! †
 */

@Api(value = "Cache Controller. Делает много важного и интересного", produces = "application/json", consumes = "application/json")
public class RedisController extends Controller {

    private final WSClient wsClient;
    private final Config config;
    private final CacheAsyncApi cacheAsyncApi;
    private final ExecutionContext executionContext;

    @Inject
    public RedisController(final WSClient wsClient, final Config config, final CacheAsyncApi cacheAsyncApi, final ExecutionContext executionContext) {
        this.wsClient = wsClient;
        this.config = config;
        this.cacheAsyncApi = cacheAsyncApi;
        this.executionContext = executionContext;
    }

    @ApiOperation(value = "заглушка для docker", hidden = true)
    public Result index() {
        return ok();
    }

    @ApiOperation(value = "get")
    @ApiImplicitParams(
            {@ApiImplicitParam(
                    name = "body",
                    dataType = "JsonNode",
                    required = true,
                    paramType = "body",
                    value = "Получение данных из Redis-а"
            )}
    )
    public CompletionStage get(String key) {
        return FutureConverters.toJava(cacheAsyncApi.get(key, scala.reflect.ClassTag$.MODULE$.apply(JsonNode.class)));
    }

    @ApiOperation(value = "set")
    @ApiImplicitParams(
            {@ApiImplicitParam(
                    name = "body",
                    dataType = "JsonNode",
                    required = true,
                    paramType = "body",
                    value = "Данные для сохранения в Redis"
            )}
    )
    public CompletionStage set(String key, JsonNode json) {
        return FutureConverters.toJava(cacheAsyncApi.set(key, json, Duration.create(10000l, TimeUnit.SECONDS)));
    }

    @ApiOperation(value = "remove")
    @ApiImplicitParams(
            {@ApiImplicitParam(
                    name = "body",
                    dataType = "JsonNode",
                    required = true,
                    paramType = "body",
                    value = "Удаление по ключу из Redis-а"
            )}
    )
    public CompletionStage remove(String key) {
        return FutureConverters.toJava(cacheAsyncApi.remove(key));
    }

}
