package wash.rocket.xor.rocketwash.requests;

import android.net.Uri;
import android.util.Log;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.IOUtils;
import com.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import wash.rocket.xor.rocketwash.model.PinResult;

public class PinRequest extends GoogleHttpClientSpiceRequest<PinResult> {

    private String baseUrl;
    private String phone;

    public PinRequest(String phone) {
        super(PinResult.class);
        this.baseUrl = "http://test.rocketwash.me/v2/user_actions/request_pin";
        this.phone = phone;
    }

    @Override
    public PinResult loadDataFromNetwork() throws IOException {
        String uri = Uri.parse(baseUrl)
                .buildUpon()
                .appendQueryParameter("phone", phone)
                .build().toString();
        Log.d("loadDataFromNetwork", "uri = " + uri);
        HttpRequest request = getHttpRequestFactory().buildPostRequest(new GenericUrl(uri), null);
        request.setParser(new JacksonFactory().createJsonObjectParser());

        InputStream content = request.execute().getContent();
        String result = "";

        if (content != null) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            IOUtils.copy(content, out);
            result = out.toString("UTF-8");
        }

        Log.d("PinRequest", "result = " + result);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);



        return mapper.readValue(result, getResultType());

       // return request.execute().parseAs(getResultType());
    }

}
