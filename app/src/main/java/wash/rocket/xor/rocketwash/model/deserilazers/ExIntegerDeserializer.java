package wash.rocket.xor.rocketwash.model.deserilazers;

import android.text.TextUtils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class ExIntegerDeserializer extends JsonDeserializer<Integer> {

    @Override
    public Integer deserialize(JsonParser parser, DeserializationContext context)
            throws IOException {
        String floatString = parser.getText();

        if (TextUtils.isEmpty(floatString))
            return 0;
        if (floatString.contains("null")) {
            return 0;
        }
        return Integer.valueOf(floatString);
    }


}
