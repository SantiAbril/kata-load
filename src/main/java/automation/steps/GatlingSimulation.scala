package com.mycompany.automation.simulation

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class FakeStoreSimulation extends Simulation {

  // Definir el protocolo HTTP
  val httpProtocol = http
    .baseUrl("https://fakestoreapi.com") // URL base
    .acceptHeader("application/json") // Aceptamos JSON como respuesta
    .contentTypeHeader("application/json") // Enviamos JSON como cuerpo de las peticiones

  // Definir las peticiones
  val scn = scenario("FakeStoreScenario")

    // Petición GET - Obtener productos
    .exec(http("GET Products")
      .get("/products") // URL para obtener todos los productos
      .check(status.is(200)) // Verificar que la respuesta sea exitosa (status 200)
    )
    .pause(1) // Pausar 1 segundo entre las peticiones

    // Petición POST - Crear un nuevo producto
    .exec(http("POST Product")
      .post("/products")
      .body(RawFileBody("bodyPost.json")).asJson // Body leído desde un archivo JSON
      .check(status.is(201)) // Verificar que el producto fue creado con éxito (status 201)
    )
    .pause(1)

    // Petición PUT - Actualizar un producto
    .exec(http("PUT Product")
      .put("/products/1") // Reemplazar con el ID real del producto a actualizar
      .body(RawFileBody("bodyPost.json")).asJson
      .check(status.is(200)) // Verificar que la respuesta sea exitosa (status 200)
    )
    .pause(1)

    // Petición DELETE - Eliminar un producto
    .exec(http("DELETE Product")
      .delete("/products/1") // Reemplazar con el ID real del producto a eliminar
      .check(status.is(200)) // Verificar que el producto fue eliminado con éxito (status 200)
    )
    .pause(1)

  // **Modelo Abierto**: Inyectar usuarios y ejecutar las peticiones
  setUp(
    scn.inject(
      atOnceUsers(10), // Enviar 10 usuarios al mismo tiempo (modelo abierto)
      rampUsers(10) during (30.seconds) // Aumentar gradualmente a 10 usuarios durante 30 segundos
    )
  ).protocols(httpProtocol)

  // **Modelo Cerrado**: Enviar un número fijo de usuarios con un tiempo de duración específico
  // setUp(
  //   scn.inject(
  //     constantUsersPerSec(5) during (10.seconds) // 5 usuarios por segundo durante 10 segundos
  //   )
  // ).protocols(httpProtocol)
}
