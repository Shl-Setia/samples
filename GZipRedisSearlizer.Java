import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GZipRedisSearlizer implements RedisSerializer<Object> {

    private final ObjectMapper objectMapper;


    public GZipRedisSearlizer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public byte[] serialize(Object o) throws SerializationException {
        try {
            String jsonString = objectMapper.writeValueAsString(o);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(jsonString.length());
            GZIPOutputStream gzip = new GZIPOutputStream(bos);
            gzip.write(jsonString.getBytes());
            gzip.close();
            byte[] compressed = bos.toByteArray();
            bos.close();
            return compressed;
        } catch (Exception e) {
            throw new SerializationException(e.getMessage(), e);
        }

    }

    @Override
    public Object deserialize(byte[] compressed) throws SerializationException {

        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(compressed);
            GZIPInputStream gis = new GZIPInputStream(bis);
            byte[] bytes = IOUtils.toByteArray(gis);
            String jsonString = new String(bytes, StandardCharsets.UTF_8);
            return objectMapper.readValue(jsonString, Object.class);

        } catch (Exception e) {
            throw new SerializationException(e.getMessage(), e);
        }
    }
}
