import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.restassured.RestAssured.given;

public class ApiClient {

    private static final Logger logger = LoggerFactory.getLogger(ApiClient.class);
    private String authToken; // Campo para armazenar o token

    static {
        RestAssured.baseURI = "https://serverest.dev";
    }

    public void setAuthToken(String token) {
        this.authToken = token; // Método para definir o token
    }

    // Criação de um usuário
    public Response criarUsuario(String userJson) {
        logger.info("Criando usuário com o seguinte payload: {}", userJson);

        Response response = given()
                .contentType(ContentType.JSON)
                .body(userJson)
                .when()
                .post("/usuarios")
                .then()
                .extract()
                .response();

        return response;
    }

    // Verificação de usuário
    public Response obterUsuario(String userId) {
        logger.info("Consultando usuário com ID: {}", userId);

        Response response = given()
                .contentType(ContentType.JSON)
                .when()
                .get("/usuarios/" + userId)
                .then()
                .extract()
                .response();

        return response;
    }

    // Criação de um produto
    public Response criarProduto(String productJson, String token) {
        logger.info("Criando produto com o seguinte payload: {}", productJson);

        Response response = given()
                .contentType(ContentType.JSON)
                .header("Authorization", authToken) // Usar o token armazenado
                .body(productJson)
                .when()
                .post("/produtos")
                .then()
                .extract()
                .response();

        return response;
    }

    // Verificação de produto
    public Response obterProduto(String productId) {
        logger.info("Consultando produto com ID: {}", productId);

        Response response = given()
                .contentType(ContentType.JSON)
                .when()
                .get("/produtos/" + productId)
                .then()
                .extract()
                .response();

        return response;
    }

    // Realização de login
    public Response realizarLogin(String loginJson) {
        logger.info("Relizando login com o seguinte payload: {}", loginJson);

        Response response = given()
                .contentType(ContentType.JSON)
                .body(loginJson)
                .when()
                .post("/login")
                .then()
                .statusCode(200) // Verificar se o status da resposta é 200 OK
                .extract()
                .response();

        // Armazenar o token após o login
        this.authToken = response.jsonPath().getString("authorization");

        return response;
    }

}
