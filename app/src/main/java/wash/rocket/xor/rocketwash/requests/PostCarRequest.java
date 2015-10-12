package wash.rocket.xor.rocketwash.requests;

import android.net.Uri;
import android.util.Log;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;

import java.io.IOException;

import wash.rocket.xor.rocketwash.model.PostCarResult;

public class PostCarRequest extends GoogleHttpClientSpiceRequest<PostCarResult> {

    private String baseUrl;
    private int id_carBrand;
    private int id_carModel;
    private String id_session;

    public PostCarRequest(int id_carBrand, int id_carModel, String id_session) {
        super(PostCarResult.class);
        this.baseUrl = "http://test.rocketwash.me/v2/cars";
        this.id_carBrand = id_carBrand;
        this.id_carModel = id_carModel;
        this.id_session = id_session;
    }

    @Override
    public PostCarResult loadDataFromNetwork() throws IOException {
        String uri = Uri.parse(baseUrl)
                .buildUpon()
                .appendQueryParameter("car_make_id", "" + id_carBrand)
                .appendQueryParameter("car_model_id", "" + id_carModel)
                .build().toString();
        Log.d("loadDataFromNetwork", "uri = " + uri);
        HttpHeaders header = new HttpHeaders();
        header.set("X-Rocketwash-Session-Id", id_session);
      //  header.set("Accept", "application/json");
        HttpRequest request = getHttpRequestFactory().buildPostRequest(new GenericUrl(uri), null).setHeaders(header);
        request.setParser(new JacksonFactory().createJsonObjectParser());
        return request.execute().parseAs(getResultType());
    }

}
