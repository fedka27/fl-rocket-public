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

import wash.rocket.xor.rocketwash.model.WashServiceResult;

public class FavoritesWashServiceRequest extends GoogleHttpClientSpiceRequest<WashServiceResult> {

    private String baseUrl;

    private int page;
    private String id_session;

    public FavoritesWashServiceRequest(String id_session, int pshr) {
        super(WashServiceResult.class);
        this.baseUrl = "http://test.rocketwash.me/v2/favourites";
        this.page = page;
        this.id_session = id_session;
    }

    @Override
    public WashServiceResult loadDataFromNetwork() throws IOException {

        String uri = Uri.parse(baseUrl)
                .buildUpon()
                .appendQueryParameter("page", "" + page)
                .build().toString();

        Log.d("loadDataFromNetwork", "uri = " + uri);

        HttpHeaders header = new HttpHeaders();
        header.set("X-Rocketwash-Session-Id", id_session);

        HttpRequest request = getHttpRequestFactory().buildGetRequest(new GenericUrl(uri)).setHeaders(header);
        String result = ""; //request.execute().parseAsString();

        InputStream content = request.execute().getContent();
        if (content != null) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            IOUtils.copy(content, out);
            result = out.toString("UTF-8");
        }

        Log.d("loadDataFromNetwork", " res = " + result);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.readValue(result, getResultType());
    }


}
