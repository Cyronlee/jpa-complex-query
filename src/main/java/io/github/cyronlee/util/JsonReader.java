package io.github.cyronlee.util;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class JsonReader {

    private JsonReader() {
    }

    public static String readJson(String fileName) throws URISyntaxException, IOException {
        return Files.lines(Paths.get(getUrl(fileName).toURI()))
                .parallel()
                .collect(Collectors.joining());
    }

    public static <T> T readObject(String fileName, Class<T> cls) throws IOException {
        URL url = getUrl(fileName);
        return getObjectMapper().readValue(url, cls);
    }

    public static <T> List<T> readList(String fileName, Class<T> cls) throws IOException {
        URL url = getUrl(fileName);
        ObjectMapper mapper = getObjectMapper();
        JavaType javaType = mapper.getTypeFactory().constructCollectionType(List.class, cls);
        return mapper.readValue(url, javaType);
    }

    private static ObjectMapper getObjectMapper() {
        return new ObjectMapper().registerModule(new SimpleModule()
                .addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ISO_LOCAL_DATE_TIME)));
    }

    private static URL getUrl(String fileName) {
        final ClassLoader loader = ClassLoader.getSystemClassLoader();
        return Objects.requireNonNull(loader.getResource(fileName));
    }
}
