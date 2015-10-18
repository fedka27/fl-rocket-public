package wash.rocket.xor.rocketwash.requests;

import android.net.Uri;
import android.util.Log;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.util.IOUtils;
import com.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import wash.rocket.xor.rocketwash.model.RemoveFavoriteResult;

public class RemoveFavoriteRequest extends GoogleHttpClientSpiceRequest<RemoveFavoriteResult> {

    private String baseUrl;
    private int id;
    private String session_id;

    public RemoveFavoriteRequest(String session_id, int id) {
        super(RemoveFavoriteResult.class);
        this.baseUrl = "http://test.rocketwash.me/v2/favourites/";
        this.id = id;
        this.session_id = session_id;
    }

    @Override
    public RemoveFavoriteResult loadDataFromNetwork() throws IOException {
        String uri = Uri.parse(baseUrl +  id)
                .buildUpon()
                //.appendQueryParameter("id", "" +id)
                .build().toString();
        Log.d("loadDataFromNetwork", "uri = " + uri);

        HttpHeaders header = new HttpHeaders();
        header.set("X-Rocketwash-Session-Id", session_id);

        HttpRequest request = getHttpRequestFactory().buildDeleteRequest(new GenericUrl(uri)).setHeaders(header);
        //request.setParser(new JacksonFactory().createJsonObjectParser());
        // return request.execute().parseAs(getResultType());

        InputStream content = request.execute().getContent();
        String result = "";

        if (content != null) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            IOUtils.copy(content, out);
            result = out.toString("UTF-8");
        }

        Log.d("RemoveFavoriteRequest", "result = " + result);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);

        return mapper.readValue(result, getResultType());
    }

}
