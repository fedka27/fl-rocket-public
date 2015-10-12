package wash.rocket.xor.rocketwash.requests;

import android.net.Uri;
import android.util.Log;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.util.IOUtils;
import com.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import wash.rocket.xor.rocketwash.model.EmptyUserResult;

public class CreateEmptyUserRequest extends GoogleHttpClientSpiceRequest<EmptyUserResult> {

    private String baseUrl;

    public CreateEmptyUserRequest() {
        super(EmptyUserResult.class);
        this.baseUrl = "http://test.rocketwash.me/v2/user_actions/create_empty_user";
    }

    @Override
    public EmptyUserResult loadDataFromNetwork() throws IOException {
        String uri = Uri.parse(baseUrl).buildUpon().build().toString();
        Log.d("loadDataFromNetwork", "uri = " + uri);
        //HttpRequest request = getHttpRequestFactory().buildPostRequest(new GenericUrl(uri), null);
        //request.setParser(new JacksonFactory().createJsonObjectParser());
        //return request.execute().parseAs(getResultType());

        HttpRequest request = getHttpRequestFactory().buildPostRequest(new GenericUrl(uri), null);
        String result = ""; //request.execute().parseAsString();

        InputStream content = request.execute().getContent();
        if (content != null) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            IOUtils.copy(content, out);
            result = out.toString("UTF-8");
        }

        Log.d("CreateEmptyUserRequest", " res = " + result);

        //JsonNode json = new ObjectMapper().readTree(result);
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.readValue(result, getResultType());

    }
}
