import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.lang.Nullable;
import org.xerial.snappy.Snappy;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class SnappyRedisSerializer implements RedisSerializer<Object>
{

    private final ObjectMapper objectMapper;

    public SnappyRedisSerializer(ObjectMapper objectMapper)
    {
        this.objectMapper = objectMapper;
    }

    @Override
    public byte[] serialize(@Nullable Object value) throws SerializationException
    {
        try
        {
            final String jsonString = objectMapper.writeValueAsString(value);
            return Snappy.compress(jsonString, StandardCharsets.UTF_8);
        } catch (Exception e)
        {
            throw new SerializationException(e.getMessage(), e);
        }
    }

    @Override
    public Object deserialize(byte[] bytes) throws SerializationException
    {
        try
        {
            if (Snappy.isValidCompressedBuffer(bytes))
            {
                final String jsonString =  Snappy.uncompressString(bytes, StandardCharsets.UTF_8);
                return objectMapper.readValue(jsonString, Object.class);
            }
            return objectMapper.readValue(bytes, Object.class);
        } catch (IOException e)
        {
            throw new SerializationException(e.getMessage(), e);
        }
    }
}
