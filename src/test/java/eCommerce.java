import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import com.jayway.jsonpath.JsonPath;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static io.restassured.RestAssured.*;
import static org.junit.Assert.*;
@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class eCommerce {

    static private String base_url = "https://webapi.segundamano.mx";
    static private String username = "alfonsoqa1@mailinator.com";
    static private String password = "12345";
    static private String access_token;
    static private String account_id;
    static private String encode1;
    static private String encode2;
    static private String uuid;
    static private String refresh_access_token;
    static private String access_token_up;
    static private String address_id;
    static private String newToken;
    static private String id_alert;


    private String getToken() {
        RestAssured.baseURI = String.format("%s/nga/api/v1.1/private/accounts", base_url);
        Response resp = given()
                .log().all()
                .queryParam("lang", "es")
                .auth().preemptive().basic("alfonsoqa1@mailinator.com", "12345")
                .post();

        String body_response = resp.getBody().asString();
        System.out.print("Body response: " + body_response);

        access_token = JsonPath.read(body_response, "$.access_token");
        System.out.print("The token: " + access_token);

        uuid = JsonPath.read(body_response, "$.account.uuid");
        System.out.println("uuid" + uuid);

        String datos = uuid + ":" + access_token;
        String encodeAu = Base64.getEncoder().encodeToString(datos.getBytes());
        return encodeAu;
    }


    @Test
    public void t01_get_categorias() {
        //Configurar URI
        RestAssured.baseURI = String.format("%s/nga/api/v1/public/categories/insert", base_url);
        //hacer el request y guardarlo en response

        Response response = given()
                .log()
                .all()
                .queryParam("lang", "es")
                .get();

        String body_response = response.getBody().asString();
        String headers_response = response.getHeaders().toString();
        //String body_p = response.prettyPrint();
        System.out.print("The Body response: " + body_response);
        System.out.print("The Headers response: " + headers_response);
        //System.out.println("Body response: " + body_p);

        // esto para que es
        assertEquals(200, response.getStatusCode());
        assertNotNull(body_response);
        assertTrue(body_response.contains("categories"));

    }

    @Test
    public void t02_obtener_token() {
        RestAssured.baseURI = String.format("%s/nga/api/v1.1/private/accounts", base_url);
        String token_basic = "dmVudGFzNDcyODM3Njg3QG1haWxpbmF0b3IuY29tOjEyMzQ1";

        Response resp = given()
                .log().all()
                .queryParam("lang", "es")
                .header("Authorization", "Basic " + token_basic)
                .post();

        String body_response = resp.getBody().asString();
        String headers_response = resp.getHeaders().toString();
        //String body_p = response.prettyPrint();
        System.out.print("Body response: " + body_response);
        System.out.print("Headers response: " + headers_response);
        //System.out.println("Body response: " + body_p);

        assertEquals(200, resp.getStatusCode());
        assertNotNull(body_response);
        assertTrue(body_response.contains("access_token"));
    }

    @Test
    public void t03_obtener_token_con_Basic_Auth_email_pass() {
        RestAssured.baseURI = String.format("%s/nga/api/v1.1/private/accounts", base_url);

        Response resp = given()
                .log().all()
                .queryParam("lang", "es")
                .auth().preemptive().basic("alfonsoqa@mailinator.com", "12345")
                .post();
        String body_response = resp.getBody().asString();
        String headers_response = resp.getHeaders().toString();
        System.out.print("Body response: " + body_response);
        assertEquals(200, resp.getStatusCode());
        assertNotNull(body_response);
        assertTrue(body_response.contains("access_token"));


        access_token = JsonPath.read(body_response, "$.access_token");
        System.out.print("The token: " + access_token);

        System.out.println("account_id: " + JsonPath.read(body_response, "$.account.account_id"));
        System.out.println("The uuid: " + JsonPath.read(body_response, "$.account.uuid"));

        account_id = JsonPath.read(body_response, "$.account.account_id");
        uuid = JsonPath.read(body_response, "$.account.uuid");

    }

    @Test
    public void t04_editar_datos_usuario() {
        RestAssured.baseURI = String.format("%s/nga/api/v1/%s", base_url, account_id);

        String body2 = "{\"account\":{\"name\":\"Alfonso Estrada\",\"phone\":\"5576743185\",\"professional\":false}}";

        Response resp = given()
                .log().all()
                .header("Authorization", "tag:scmcoord.com,2013:api " + access_token)
                .header("Content-Type", "application/json")
                .body(body2)
                .patch();

        String body_response = resp.getBody().asString();
        String headers_response = resp.getHeaders().toString();
        // Esta línea es para el json
        String body_pre = resp.prettyPrint();
        //System.out.println("Body response: " + body_response);
        System.out.println("Below is the pretty response format");
        String body_p2 = resp.prettyPrint();

        System.out.println("Headers response: " + headers_response);

        assertEquals(200, resp.getStatusCode());
        assertNotNull(body_response);
        assertTrue(body_response.contains("account"));
    }

    @Test
    public void t05_get_all_municipios() {
        RestAssured.baseURI = String.format("%s/nga/api/v1.1/public/regions?depth=1&from=region:29&lang=es", base_url);

        //Crear Objeto
        Response response = given()
                .log()
                .all()
                .queryParam("lang", "es")
                .get();

        // Solo imprimo el status Code
        System.out.println("Status Code 200 is OK " + response.getStatusCode());

        // Para validar muchas cosas mejor crear una variable
        String body_response = response.getBody().asString();
        String headers_response = response.getHeaders().toString();
        //String body_p = response.prettyPrint();
        System.out.println("Body Response is " + body_response);
        System.out.print("The Headers response: " + headers_response);
        //System.out.println("Body Response Pretty " + body_p);

        assertEquals(200, response.getStatusCode());
        assertNotNull(body_response);
        assertTrue(body_response.contains("all_label"));
    }

    @Test
    public void t06_get_cartera() {
        RestAssured.baseURI = String.format("%s/tokens/v1/public/balance/detail/%s", base_url, uuid);
        Response response = given()
                .log()
                .all()
                .auth().preemptive().basic(uuid, access_token)
                .header("Accept","application/json, text/plain, */*")
                .get();

        System.out.println("Status Code 200 is OK " + response.getStatusCode());

        String body_response = response.getBody().asString();
        String headers_response = response.getHeaders().toString();
        System.out.println("Body Response is " + body_response);
        System.out.print("The Headers response: " + headers_response);

        assertEquals(200, response.getStatusCode());
        assertNotNull(body_response);
        assertTrue(body_response.contains("balance"));
    }


    @Test
    public void t07_get_messages() {
        RestAssured.baseURI = String.format("%s/nga/api/v1/api/hal/%s/conversations/", base_url, uuid);

        Response resp = given()
                .log()
                .all()
                .queryParam("lang", "es")
                .header("Authorization", "tag:scmcoord.com,2013:api " + access_token)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json, text/plain, */*")
                .get();

        String body_response = resp.getBody().asString();
        String headers_response = resp.getHeaders().toString();
        String body_p = resp.prettyPrint();
        System.out.println("Below is the pretty response format");
        String body_p2 = resp.prettyPrint();
        System.out.println("Headers response: " + headers_response);

        assertEquals(200, resp.getStatusCode());
        assertNotNull(body_response);
        assertTrue(body_response.contains("conversation"));
    }

    @Test
    public void t08_get_count() {
        RestAssured.baseURI = String.format("%s/nga/api/v1/api/users/%s/counter", base_url, uuid);

        Response resp = given()
                .log()
                .all()
                .queryParam("lang", "es")
                .header("Authorization ", "tag:scmcoord.com,2013:api " + access_token)
                .header("Origin", "https://www.segundamano.mx")
                .get();

        /*String body_response = resp.getBody().asString();
        String headers_response = resp.getHeaders().toString();
        System.out.print("The Body response: " + body_response);*/

        //assertEquals(200,resp.getStatusCode());
        // assertNotNull(body_response);
        // assertTrue(body_response.contains("unread"));
    }

    @Test
    public void t00_Encode() {
        RestAssured.baseURI = String.format("%s/nga/api/v1.1/private/accounts", base_url);
        String credentials = username + ':' + password;
        encode1 = Base64.getEncoder().encodeToString(credentials.getBytes());
        System.out.println("La base 64 es: " + encode1);

        Response response = given()
                .log().all()
                .queryParam("lang", "es")
                .header("Authorization", "Basic " + encode1)
                .post();

        String body_response = response.getBody().asString();
        System.out.print("Body response: " + body_response);


        refresh_access_token = JsonPath.read(body_response, "$.access_token");
        System.out.println("The new token: " + refresh_access_token);

        String credentials2 = uuid + ':' + refresh_access_token;
        encode2 = Base64.getEncoder().encodeToString(credentials2.getBytes());
        String body_encode = response.prettyPrint();
        System.out.println();
        System.out.println("El tokenUp es: " + encode2);

        byte[] bytesDecodificados = Base64.getDecoder().decode(encode1);
        String cadenaDecodificada = new String(bytesDecodificados);
        System.out.println("decodificado es: " + cadenaDecodificada);
    }

    @Test
    public void t09_create_an_announce() {
        String newToken = getToken();
        System.out.println("Test que regresa el token "+ newToken);
        RestAssured.baseURI = String.format("%s/v2/accounts/%s/up", base_url, uuid);


        String body = "{\n" +
                "    \"images\":\"6972822811.jpg\",\n" +
                "    \"category\":\"2122\",\n" +
                "    \"subject\":\"Postman_Prueba 07\",\n" +
                "    \"body\":\"Prueba de subir la información de un anuncio desde Postman para verlo en segundamano\",\n" +
                "    \"price\":\"10\",\n" +
                "    \"region\":\"29\",\n" +
                "    \"municipality\":\"1976\",\n" +
                "    \"area\":\"22552\",\n" +
                "    \"phone_hidden\":\"true\",\n" +
                "    \"show_phone\":\"false\",\n" +
                "    \"contact_phone\":\"1234567890\"\n" +
                "}";

        Response response = given()
                .log().all()
                .header("Authorization","Basic "+ newToken)
                .header("Content-Type"," application/json")
                .header("x-source"," PHOENIX_DESKTOP")
                .header("Accept","application/json, text/plan, */*")
                .body(body)
                .post();

        String body_response = response.getBody().asString();
        System.out.print("Body response: " + body_response);
        //String body_p2 = response.prettyPrint();

        assertEquals(200, response.getStatusCode());
        assertNotNull(body_response);
        assertTrue(body_response.contains("ad_id"));
    }

    @Test
    public void t10_create_address() {
        RestAssured.baseURI = String.format("%s/addresses/v1/create", base_url);
        Response response = given()
                .log().all()
                .auth().preemptive().basic(uuid, access_token)
                .header("Content-type", "application/x-www-form-urlencoded")
                .formParam("contact", "Pamela de Los Angeles")
                .formParam("phone", "5576743185")
                .formParam("rfc", "EAMJ930412A44")
                .formParam("zipCode", "80010")
                .formParam("exteriorInfo", "25")
                .formParam("interiorInfo", "5")
                .formParam("region", "29")
                .formParam("municipality", "1976")
                .formParam("area", "60683")
                .formParam("alias", "Casa de Pam")
                .post();

        String body_response = response.getBody().asString();
        System.out.println("El body Responde: " + body_response);
        assertEquals(201, response.getStatusCode());
        assertNotNull(body_response);
        assertTrue(body_response.contains("addressID"));
        assertNotNull(body_response);


        address_id = JsonPath.read(body_response, "$.addressID");
        System.out.print("The address id is: " + address_id);
    }

    @Test
    public void t11_create_alerts_vsa() {
        RestAssured.baseURI = String.format("%s/alerts/v1/private/account/%s/alert", base_url, uuid);

        String body = "{\n" +
                "    \"ad_listing_service_filters\":{\n" +
                "        \"region\":\"29\",\n" +
                "        \"municipality_multi\":\"1976\",\n" +
                "        \"category_lv0\":\"2000\",\n" +
                "        \"category_lv1\":\"2020\",\n" +
                "        \"category_lv2\":\"2021\",\n" +
                "        \"gearbox\":\"2\",\n" +
                "        \"owners\":\"2\",\n" +
                "        \"with_shop_2\":\"0\"\n" +
                "    }\n" +
                "}";

        Response response = given().log().all()
                .auth().preemptive().basic(uuid, access_token)
                .header("accept", "application/json, text/plain")
                .header("Content-Type","application/json")
                .body(body)
                .post();

        String body_response = response.getBody().asString();
        System.out.println("El body Responde: " + body_response);
        String body_p5 = response.prettyPrint();


        id_alert = JsonPath.read(body_response, "$.data.alert.id");
        System.out.print("Este es el id de la Alerta: " + id_alert);
    }

    @Test
    public void t12_get_address() {
        RestAssured.baseURI = String.format("%s/addresses/v1/get/", base_url);
        Response response = given().log().all()
                .auth().preemptive().basic(uuid, access_token)
                .get();


        String body_response = response.getBody().asString();
        System.out.println("El body Responde: " + body_response);
        String body_p2 = response.prettyPrint();


        assertEquals(200, response.getStatusCode());
        assertNotNull(body_response);
        assertTrue(body_response.contains("addresses"));
        assertTrue(body_response.contains(address_id));
    }

    @Test
    public void t13_put_address() {
        RestAssured.baseURI = String.format("%s/addresses/v1/modify/%s", base_url, address_id);
        Response response = given().log().all()
                .auth().preemptive().basic(uuid, access_token)
                .header("Content-type", "application/x-www-form-urlencoded")
                .formParam("contact", "Carolina Echavaria Modificada")
                .formParam("phone", "5576743185")
                .formParam("rfc", "EAMJ930412A44")
                .formParam("zipCode", "80010")
                .formParam("exteriorInfo", "25")
                .formParam("interiorInfo", "5")
                .formParam("region", "29")
                .formParam("municipality", "1976")
                .formParam("area", "60683")
                .formParam("alias", "Casa de la CAro moficidada")
                .put();

        String body_response = response.getBody().asString();
        System.out.println("El body Responde: " + body_response);
        String body_p2 = response.prettyPrint();

        assertEquals(200, response.getStatusCode());
        assertNotNull(body_response);
        assertTrue(body_response.contains(address_id));

        assertTrue(body_response.contains("{\"message\":\""+ address_id + " modified correctly\"}"));
    }

    @Test
    public void t14_get_favorit() {
        RestAssured.baseURI = String.format("%s/favorites/v1/private/accounts/%s", base_url, uuid);

        Response response = given().log().all()
                .auth().preemptive().basic(uuid, access_token)
                .header("origin","https://www.segundamano.mx")
                .header("referer","https://www.segundamano.mx/")
                .get();


        String body_response = response.getBody().asString();
        System.out.println("El body Responde: " + body_response);
        String body_p2 = response.prettyPrint();

        assertEquals(200, response.getStatusCode());
        assertNotNull(body_response);
        assertTrue(body_response.contains("data"));
        assertTrue(body_response.contains("list_ids"));
    }

    @Test
    public void t15_get_alerts() {
        RestAssured.baseURI = String.format("%s/alerts/v1/private/account/%s/alert", base_url, uuid);

        Response response = given().log().all()
                .auth().preemptive().basic(uuid, access_token)
                .header("accept", "application/json, text/plain, */*")
                .get();


        String body_response = response.getBody().asString();
        System.out.println("El body Responde: " + body_response);
        String body_p2 = response.prettyPrint();
    }

    @Test
    public void t16_delete_address() {
        RestAssured.baseURI = String.format("%s/addresses/v1/delete/%s", base_url, address_id);

        Response response = given().log().all()
                .auth().preemptive().basic(uuid, access_token)
                .delete();

        String body_response = response.getBody().asString();
        System.out.println("El body Responde: " + body_response);
        String body_p2 = response.prettyPrint();

        assertEquals(200, response.getStatusCode());
        assertNotNull(body_response);
        assertTrue(body_response.contains(address_id));

        assertTrue(body_response.contains("{\"message\":\""+ address_id + " deleted correctly\"}"));
    }

    @Test
    public void t17_delete_alert() {
        RestAssured.baseURI = String.format("%s/alerts/v1/private/account/%s/alert/%s", base_url, uuid, id_alert);

        Response response = given().log().all()
                .auth().preemptive().basic(uuid, access_token)
                .header("accept","application/json, text/plain, */*")
                .delete();

        String body_response = response.getBody().asString();
        System.out.println("El body Responde: " + body_response);
        String body_p3 = response.prettyPrint();

        assertEquals(200, response.getStatusCode());
        assertNotNull(body_response);
        System.out.println("El Id de la alerta es:" + id_alert);

        assertTrue(body_response.contains("{\n" +
                "    \"data\": {\n" +
                "        \"status\": \"ok\"\n" +
                "    }\n" +
                "}"));
    }


    @Test
    public void t18_update_password() {
        RestAssured.baseURI = String.format("%s/nga/api/v1/%s", base_url, account_id);

        String body = "{\n" +
                "    \"account\":{\n" +
                "        \"password\":\"andromeda93\"\n" +
                "    }\n" +
                "}";

        Response resp = given()
                .log().all()
                .header("Authorization", "tag:scmcoord.com,2013:api " + access_token)
                .header("Content-Type", "application/json")
                .header("Accept","application/json, text/plain, */*")
                .body(body)
                .patch();

        String body_response = resp.getBody().asString();
        String headers_response = resp.getHeaders().toString();
        String body_pre = resp.prettyPrint();
        System.out.println("Below is the pretty response format");
        String body_p2 = resp.prettyPrint();
/*
        System.out.println("Headers response: " + headers_response);

        assertEquals(200, resp.getStatusCode());
        assertNotNull(body_response);
        assertTrue(body_response.contains("account"));*/
    }


}