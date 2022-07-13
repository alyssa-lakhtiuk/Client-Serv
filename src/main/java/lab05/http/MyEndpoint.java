package lab05.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.regex.Pattern;

@Data
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MyEndpoint {

    private final Pattern urlPattern;
    private final CustomHandler httpHandler;
    private final BiFunction<String, Pattern, Map<String, String>> paramsExtractor;

    public static MyEndpoint of(final String pattern, final CustomHandler httpHandler,
                                final BiFunction<String, Pattern, Map<String, String>> paramsExtractor) {
        return new MyEndpoint(Pattern.compile(pattern), httpHandler, paramsExtractor);
    }

    public boolean matches(final String uri) {
        return urlPattern.matcher(uri).matches();
    }

    public HttpHandler handler() {
        return httpExchange -> {
            final Map<String, String> params = paramsExtractor.apply(httpExchange.getRequestURI().toString(), urlPattern);
            httpHandler.handle(httpExchange, params);
        };
    }

    public interface CustomHandler {
        void handle(HttpExchange exchange, Map<String, String> pathParams);
    }

}