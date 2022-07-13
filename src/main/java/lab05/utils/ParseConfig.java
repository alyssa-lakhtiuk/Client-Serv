package lab05.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lab05.entity.Config;

import java.io.File;

public class ParseConfig {
    private static Config config;
    public static Config getConfig() {
        return config;
    }

    private static final String filePath = "src/main/resources/config.yaml";
    public static void YamlParser() throws Exception {
        File file = new File(filePath);
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        config = objectMapper.readValue(file, Config.class);
    }
}
