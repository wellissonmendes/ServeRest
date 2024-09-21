import com.github.javafaker.Faker;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.json.JSONObject;

import static io.restassured.RestAssured.given;

public class ApiTest {

    private static final Logger logger = LoggerFactory.getLogger(ApiTest.class);
    private ApiClient apiClient;
    private Faker faker;

    private String userId;
    private String userName;
    private String userEmail;
    private String userPassword;
    private String productName;
    private int price;
    private String description;
    private int quantity;
    private String token;

    @BeforeEach
    public void setUp() {
        apiClient = new ApiClient();
        faker = new Faker();

        // Gerar dados para o usuário
        userName = faker.name().fullName();
        userEmail = faker.internet().emailAddress();
        userPassword = faker.internet().password();

        // Gerar dados para o produto
        productName = "Produto " + faker.commerce().productName();
        price = faker.number().numberBetween(1, 200);
        description = faker.commerce().promotionCode();
        quantity = faker.number().numberBetween(1, 100);
    }

    @Test
    public void testarCriarEVerificarUsuario() {
        JSONObject userJson = new JSONObject();
        userJson.put("nome", userName);
        userJson.put("email", userEmail);
        userJson.put("password", userPassword);
        userJson.put("administrador", "true");

        // Criar usuário
        Response criarUsuarioResposta = apiClient.criarUsuario(userJson.toString());
        Assertions.assertEquals(201, criarUsuarioResposta.statusCode(), "Falha na criação do usuário");

        // Extrair ID do usuário criado
        userId = criarUsuarioResposta.jsonPath().getString("_id");

        // Verificar se o usuário foi criado corretamente
        Response obterUsuarioResposta = apiClient.obterUsuario(userId);
        Assertions.assertEquals(200, obterUsuarioResposta.statusCode(), "Usuário não encontrado");
        Assertions.assertEquals(userName, obterUsuarioResposta.jsonPath().getString("nome"), "Nome do usuário está incorreto");
    }

    @Test
    public void testarCriarEVerificarProduto() {
        // Realizar login para obter o token
        realizarLogin();

        String productJson = "{\n" +
                "  \"nome\": \""+productName+"\",\n" +
                "  \"preco\": "+price+",\n" +
                "  \"descricao\": \""+description+"\",\n" +
                "  \"quantidade\": "+quantity+"\n" +
                "}";

        Response criarProdutoResposta = apiClient.criarProduto(productJson, token);
        Assertions.assertEquals(201, criarProdutoResposta.statusCode(), "Falha na criação do produto");

        // Extrair ID do produto criado
        String productId = criarProdutoResposta.jsonPath().getString("_id");

        // Verificar se o produto foi criado corretamente
        Response obterProdutoResposta = apiClient.obterProduto(productId);
        Assertions.assertEquals(200, obterProdutoResposta.statusCode(), "Produto não encontrado");
        Assertions.assertEquals(productName, obterProdutoResposta.jsonPath().getString("nome"), "Nome do produto está incorreto");
    }

    @Test
    public void realizarLogin() {
        // Criar produto
        String loginJson = "{ \"email\": \"fulano@qa.com\", \"password\": \"teste\" }";
        Response resposta = apiClient.realizarLogin(loginJson);

        // Extrair o token da resposta JSON
        token = resposta.jsonPath().getString("authorization");

        // Exibir o token
        System.out.println("Token gerado: " + token);
    }

}
