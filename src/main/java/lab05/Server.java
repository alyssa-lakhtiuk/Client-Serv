package lab05;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.Authenticator;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpPrincipal;
import com.sun.net.httpserver.HttpServer;
import lab05.entity.base.Category;
import lab05.entity.base.Product;
import lab05.entity.base.User;
import lab05.entity.UserInputCredential;
import lab05.http.MyEndpoint;
import lab05.http.ErrorResponse;
import lab05.http.LoginResponse;
import lab05.http.SuccessResponse;
import lab05.utils.JWTgenerator;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Server {

    private final HttpServer server;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final String AUTHORIZATION_HEADER = "Authorization";

    private static final HttpPrincipal ANONYMOUS_USER = new HttpPrincipal("anonymous", "anonymous");

    private static final String dbFile = "storeDB";
    private DBConnection con;
    private final List<MyEndpoint> endpoints;

    public Server(int port) throws IOException {
        con = new DBConnection(dbFile);
        //// Only for the first run of the server
        con.getUserDao().insert(new User("admin",
                DigestUtils.md5Hex("password"),
                "admin"));

        con.getCategoryDao().insert(new Category(
                "food",
                "tasty eco food"));
        con.getCategoryDao().insert(new Category(
                "food2",
                "tasty eco food2"));

        for (int i = 0; i < 7; i++) {
            con.getProductDao().insert(new Product(
                    1,
                    "product" + i,
                    i + 10,
                    (i + 20) * 0.75,
                    "description",
                    "maker"));
        }

        // all routes
        this.endpoints = new ArrayList<MyEndpoint>();
        endpoints.add(MyEndpoint.of("\\/login",
                this::loginHandler, (a, b) -> new HashMap<>()));
        endpoints.add(MyEndpoint.of("\\/api\\/product",
                this::putProductHandler, (a, b) -> new HashMap<>()));
        endpoints.add(MyEndpoint.of("\\/api\\/category",
                this::putCategoryHandler, (a, b) -> new HashMap<>()));
        endpoints.add(MyEndpoint.of("^\\/api\\/product\\/(\\d+)$",
                this::productByIdHandler, this::getProductParamId));
        endpoints.add(MyEndpoint.of("^\\/api\\/category\\/(\\d+)$",
                this::categoryByIdHandler, this::getCategoryParamId));


        this.server = HttpServer.create();
        server.bind(new InetSocketAddress(port), 0);
        server.createContext("/", this::rootHandler)
                .setAuthenticator(new MyAuthenticator());
        server.start();
    }

    public void stop() {
        this.server.stop(1);
    }

    private void rootHandler(final HttpExchange exchange) throws IOException {
        final String uri = exchange.getRequestURI().toString();

        final Optional<MyEndpoint> alsoEndpoint = endpoints.stream()
                .filter(endpoint -> endpoint.matches(uri))
                .findFirst();
        if (alsoEndpoint.isPresent()) {
            alsoEndpoint.get().handler().handle(exchange);
        } else {
            handlerNotFound(exchange);
        }
    }

    // product handlers
    private void productByIdHandler(final HttpExchange exchange, final Map<String, String> pathParams) {
        try (final InputStream inputStream = exchange.getRequestBody(); final OutputStream os = exchange.getResponseBody()) {
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            String method = exchange.getRequestMethod();
            if (!exchange.getPrincipal().getRealm().equals("admin")) {
                writeResponse(exchange, 403, ErrorResponse.of("No permission"));
                return;
            }
            final int productId = Integer.parseInt(pathParams.get("productId"));
            if (method.equals("POST")) {
                // update product
                try (final InputStream requestBody = exchange.getRequestBody()) {
                    final Product productReceived = OBJECT_MAPPER.readValue(requestBody, Product.class);
                    Product product = con.getProductDao().getById(productId);
                    if (product != null) {
                        String name = productReceived.getName();
                        if (name != null) {
                            product.setName(name);
                        }
                        double price = productReceived.getPrice();
                        if (price > 0) {
                            product.setPrice(price);
                        } else if (price < 0) {
                            writeResponse(exchange, 409, ErrorResponse.of("Wrong input"));
                            return;
                        }
                        int amount = productReceived.getQuantity();
                        if (amount > 0) {
                            product.setQuantity(amount);
                        } else if (amount < 0) {
                            writeResponse(exchange, 409, ErrorResponse.of("Wrong input"));
                            return;
                        }
                        String description = productReceived.getDescription();
                        if (description != null) {
                            product.setDescription(description);
                        }
                        String manufacturer = productReceived.getMaker();
                        if (manufacturer != null) {
                            product.setMaker(manufacturer);
                        }
                        Integer group_id = productReceived.getCategoryId();
                        if (group_id > 0) {
                            product.setCategoryId(group_id);
                        } else if (group_id < 0) {
                            writeResponse(exchange, 409, ErrorResponse.of("Wrong input"));
                            return;
                        }
                        int updated = con.getProductDao().update(product, product.getProductId());
                        if (updated > 0) {
                            exchange.sendResponseHeaders(204, -1);
                        } else {
                            writeResponse(exchange, 404, ErrorResponse.of("Can't update product"));
                        }
                    } else {
                        writeResponse(exchange, 404, ErrorResponse.of("No such product"));
                    }
                }
            } else {
                final Product product = con.getProductDao().getById(productId);
                if (method.equals("GET")) {
                    if (product != null) {
                        writeResponse(exchange, 200, product);
                    } else {
                        writeResponse(exchange, 404, ErrorResponse.of("No such product"));
                    }
                } else if (method.equals("DELETE")) {
                    if (product != null) {
                        int deleted = con.getProductDao().delete(productId);

                        if (deleted == productId) {
                            exchange.sendResponseHeaders(204, -1);
                        } else {
                            writeResponse(exchange, 404, ErrorResponse.of("Deletion failed"));
                        }
                    } else {
                        writeResponse(exchange, 404, ErrorResponse.of("No such product"));
                    }
                } else {
                    writeResponse(exchange, 404, ErrorResponse.of("Invalid method type"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void putProductHandler(final HttpExchange exchange, final Map<String, String> pathParams) {
        try (final InputStream requestBody = exchange.getRequestBody()) {
            exchange.getResponseHeaders().add("Content-Type", "application/json");

            String method = exchange.getRequestMethod();

            if (!exchange.getPrincipal().getRealm().equals("admin")) {
                writeResponse(exchange, 403, ErrorResponse.of("No permission"));
                return;
            }

            if (method.equals("PUT")) {
                final Product product = OBJECT_MAPPER.readValue(requestBody, Product.class);

                if (product != null) {
                    if (product.getQuantity() >= 0 && product.getPrice() > 0 && product.getCategoryId() > 0) {

                        con.getProductDao().insert(product);
                        writeResponse(exchange, 201,
                                SuccessResponse.of("Successfully created product!",
                                        con.getProductDao().getByName(product.getName()).getProductId()));
                    } else {
                        writeResponse(exchange, 409,
                                ErrorResponse.of("Wrong input"));
                    }
                } else {
                    writeResponse(exchange, 409,
                            ErrorResponse.of("Wrong input"));
                }
            } else {
                writeResponse(exchange, 404,
                        ErrorResponse.of("Invalid method type"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void putCategoryHandler(final HttpExchange exchange, final Map<String, String> pathParams) {
        try (final InputStream requestBody = exchange.getRequestBody()) {
            exchange.getResponseHeaders().add("Content-Type", "application/json");

            String method = exchange.getRequestMethod();

            if (!exchange.getPrincipal().getRealm().equals("admin")) {
                writeResponse(exchange, 403, ErrorResponse.of("No permission"));
                return;
            }

            if (method.equals("PUT")) {
                final Category category = OBJECT_MAPPER.readValue(requestBody, Category.class);

                if (category != null) {
                    if (!Objects.equals(category.getCategoryName(), "")){
                        con.getCategoryDao().insert(category);

                        writeResponse(exchange, 201,
                                SuccessResponse.of("Successfully created product!",
                                        con.getCategoryDao().getByName(category.getCategoryName()).getCategoryId()));
                    } else {
                        writeResponse(exchange, 409,
                                ErrorResponse.of("Wrong input"));
                    }
                } else {
                    writeResponse(exchange, 409,
                            ErrorResponse.of("Wrong input"));
                }
            } else {
                writeResponse(exchange, 404,
                        ErrorResponse.of("Invalid method type"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void categoryByIdHandler(final HttpExchange exchange, final Map<String, String> pathParams) {
        try (final InputStream inputStream = exchange.getRequestBody(); final OutputStream os = exchange.getResponseBody()) {
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            String method = exchange.getRequestMethod();
            if (!exchange.getPrincipal().getRealm().equals("admin")) {
                writeResponse(exchange, 403, ErrorResponse.of("No permission"));
                return;
            }
            final int categoryId = Integer.parseInt(pathParams.get("categoryId"));
            if (method.equals("POST")) {
                // update category
                try (final InputStream requestBody = exchange.getRequestBody()) {

                    final Category categoryReceived = OBJECT_MAPPER.readValue(requestBody, Category.class);
                    Category category = con.getCategoryDao().getById(categoryId);
                    if (category != null) {
                        String name = categoryReceived.getCategoryName();
                        if (name != null) {
                            category.setCategoryName(name);
                        }
                        String description = categoryReceived.getDescription();
                        if (description != null) {
                            category.setDescription(description);
                        }
                        int updated = con.getCategoryDao().update(category, categoryId);
                        if (updated >= 0) {
                            exchange.sendResponseHeaders(204, -1);
                        } else {
                            writeResponse(exchange, 404, ErrorResponse.of("Can't update category"));
                        }
                    } else {
                        writeResponse(exchange, 404, ErrorResponse.of("No such category"));
                    }
                }
            } else {
                final Category category = con.getCategoryDao().getById(categoryId);
                if (method.equals("GET")) {
                    if (category != null) {
                        writeResponse(exchange, 200, category);
                    } else {
                        writeResponse(exchange, 404, ErrorResponse.of("No such category"));
                    }
                } else if (method.equals("DELETE")) {
                    if (category != null) {
                        int deleted = con.getCategoryDao().delete(categoryId);

                        if (deleted == categoryId) {
                            exchange.sendResponseHeaders(204, -1);
                        } else {
                            writeResponse(exchange, 404, ErrorResponse.of("Deletion failed"));
                        }
                    } else {
                        writeResponse(exchange, 404, ErrorResponse.of("No such category"));
                    }
                } else {
                    writeResponse(exchange, 404, ErrorResponse.of("Invalid method type"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private Map<String, String> getProductParamId(final String uri, final Pattern pattern) {
        final Matcher matcher = pattern.matcher(uri);
        matcher.find();

        return new HashMap<String, String>() {{
            put("productId", matcher.group(1));
        }};
    }

    private Map<String, String> getCategoryParamId(final String uri, final Pattern pattern) {
        final Matcher matcher = pattern.matcher(uri);
        matcher.find();

        return new HashMap<String, String>() {{
            put("categoryId", matcher.group(1));
        }};
    }

    private void loginHandler(final HttpExchange exchange, final Map<String, String> pathParams) {
        try (final InputStream requestBody = exchange.getRequestBody()) {

            final UserInputCredential userCredential = OBJECT_MAPPER.readValue(requestBody, UserInputCredential.class);
            final User user = con.getUserDao().getByLogin(userCredential.getLogin());
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            if (user != null) {
                if (user.getPassword().equals(DigestUtils.md5Hex(userCredential.getPassword()))) {
                    final LoginResponse loginResponse = LoginResponse.of(
                            JWTgenerator.createJWT(user));
                    writeResponse(exchange, 200, loginResponse);
                } else {
                    writeResponse(exchange, 401, ErrorResponse.of("invalid password"));
                }
            } else {
                writeResponse(exchange, 401, ErrorResponse.of("unknown user"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handlerNotFound(final HttpExchange exchange) {
        try {
            exchange.sendResponseHeaders(404, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void writeResponse(final HttpExchange exchange, final int statusCode, final Object response) throws IOException {
        final byte[] bytes = OBJECT_MAPPER.writeValueAsBytes(response);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        exchange.getResponseBody().write(bytes);
    }

    private class MyAuthenticator extends Authenticator {

        @Override
        public Result authenticate(final HttpExchange httpExchange) {
            final String token = httpExchange.getRequestHeaders().getFirst(AUTHORIZATION_HEADER);

            if (token != null) {
                try {
                    final String username = JWTgenerator.parseJWT(token).getSubject();
                    final User user = con.getUserDao().getByLogin(username);

                    if (user != null) {
                        return new Success(new HttpPrincipal(username, user.getRole()));
                    } else {
                        return new Retry(401);
                    }

                } catch (Exception e) {
                    return new Failure(403);
                }
            }
            return new Success(ANONYMOUS_USER);
        }
    }

}