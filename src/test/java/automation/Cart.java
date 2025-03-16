package automation;

import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import java.util.Map;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class Cart extends Simulation {

    private HttpProtocolBuilder httpProtocol = http
            .baseUrl("https://fakestoreapi.com/");

    private static ChainBuilder getAllCarts =
            exec(
                    http("Retrieve a list of all available carts")
                            .get("carts")
                            .check(bodyString().saveAs("responseBody"))
                            .check(status().is(200))
            );

    private static ChainBuilder addANewCart =
            exec(
                    http("Create a new cart.")
                            .post("carts")
                            .header("Content-Type", "application/json")
                            .body(ElFileBody("bodies/postCarts.json"))
                            .check(bodyString().saveAs("responseBody"))
                            .check(status().is(200))
            );

    private static ChainBuilder updateACart =
            exec(
                    http("Update an existing cart by ID")
                            .put("carts/1")
                            .header("Content-Type", "application/json")
                            .body(ElFileBody("bodies/putCarts.json"))
                            .check(bodyString().saveAs("responseBody"))
                            .check(status().is(200))
            );

    private static ChainBuilder deleteACart =
            exec(
                    http("Delete a specific cart by ID")
                            .delete("carts/1")
                            .check(bodyString().saveAs("responseBody"))
                            .check(status().is(200))
            );


    private final ScenarioBuilder openModelScn = scenario("Open Model - Load Test - Carts")
            .exec(getAllCarts)
            .pace(5)
            .exec(addANewCart)
            .pace(5)
            .exec(updateACart)
            .pace(5)
            .exec(deleteACart);

    private final ScenarioBuilder closedModelScn = scenario("Closed Model - Load Test - Carts")
            .exec(getAllCarts)
            .pace(5)
            .exec(addANewCart)
            .pace(5)
            .exec(updateACart)
            .pace(5)
            .exec(deleteACart);

    {
        setUp(
                openModelScn.injectOpen(// Ejecuta las peticiones con atOnceUsers (todos los usuarios a la vez)
                        atOnceUsers(3)),
                closedModelScn.injectClosed(// Ejecuta las peticiones con rampConcurrentUsers (usuarios aumentan linealmente)
                        rampConcurrentUsers(2).to(10).during(10))
        ).protocols(httpProtocol).maxDuration(30);
    }


}
