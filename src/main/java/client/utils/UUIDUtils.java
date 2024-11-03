package client.utils;


import java.util.UUID;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UUIDUtils {
    public static UUID uuidFromString(String stringUUID) throws RuntimeException
    {
        try
        {
            String withDashes = stringUUID.replaceFirst(
                    "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)",
                    "$1-$2-$3-$4-$5");

            return UUID.fromString(withDashes);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
