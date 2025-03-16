package automation;

import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;


import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class Carts extends Simulation {

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_JSON = "application/json";

    private final Map<CharSequence, String> commonsHeaders = Map.of(
            CONTENT_TYPE, APPLICATION_JSON
    );

    private HttpProtocolBuilder httpProtocol = http
            .baseUrl("https://fakestoreapi.com/");


    private final ScenarioBuilder scn = scenario("Carts")
            .exec(
                    http("Get all carts")
                            .get("carts")
                            .check(bodyString().saveAs("responseBody"))
                            .check(status().is(200))
            )
            .pause(1)
            .exec(
                    http("Create a new cart")
                            .post("carts")
                            .headers(commonsHeaders)
                            .body(ElFileBody("bodies/postCarts.json"))
                            .check(bodyString().saveAs("responseBody"))
                            .check(status().is(200))
            )
            .pause(1)
            .exec(
                    http("Update a cart")
                            .put("carts/1")
                            .headers(commonsHeaders)
                            .body(ElFileBody("bodies/putCarts.json"))
                            .check(bodyString().saveAs("responseBody"))
                            .check(status().is(200))
            )
            .pause(1)
            .exec(
                    http("Delete a cart")
                            .delete("carts/1")
                            .check(bodyString().saveAs("responseBody"))
                            .check(status().is(200))
            )
            .pause(1);

    {
        setUp(
         scn.injectOpen( // Ejecución del modelo abierto: Ejecuta 3 peticiones con atOnceUsers (todos los usuarios a la vez)
                 atOnceUsers(3)).protocols(httpProtocol),
         scn.injectClosed( // Ejecución del modelo cerrado: Ejecuta 2 a 10 peticiones con rampConcurrentUsers (usuarios aumentan linealmente)
                 rampConcurrentUsers(2).to(10).during(10)).protocols(httpProtocol)
        ).maxDuration(30);


    }

}
