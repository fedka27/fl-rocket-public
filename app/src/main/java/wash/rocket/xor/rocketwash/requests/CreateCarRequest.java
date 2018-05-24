package wash.rocket.xor.rocketwash.requests;

import android.net.Uri;
import android.util.Log;

import com.bluelinelabs.logansquare.LoganSquare;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.util.IOUtils;
import com.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import wash.rocket.xor.rocketwash.model.PostCarResult;
import wash.rocket.xor.rocketwash.util.Constants;

public class CreateCarRequest extends GoogleHttpClientSpiceRequest<PostCarResult> {

    private String baseUrl;
    private int id_carBrand;
    private int id_carModel;
    private String id_session;
    private String tag;

    public CreateCarRequest(int id_carBrand, int id_carModel, String tag, String id_session) {
        super(PostCarResult.class);
        this.baseUrl = Constants.URL + "cars";
        this.id_carBrand = id_carBrand;
        this.id_carModel = id_carModel;
        this.id_session = id_session;
        this.tag = tag;
    }

    @Override
    public PostCarResult loadDataFromNetwork() throws IOException {
        String uri = Uri.parse(baseUrl)
                .buildUpon()
                .appendQueryParameter("car_make_id", "" + id_carBrand)
                .appendQueryParameter("car_model_id", "" + id_carModel)
                .appendQueryParameter("tag", tag)
                .build().toString();
        Log.d("loadDataFromNetwork", "uri = " + uri);
        HttpHeaders header = new HttpHeaders();
        header.set("X-Rocketwash-Session-Id", id_session);
        //  header.set("Accept", "application/json");
        HttpRequest request = getHttpRequestFactory().buildPostRequest(new GenericUrl(uri), null).setHeaders(header);

        InputStream content = request.execute().getContent();
        String result = "";

        if (content != null) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            IOUtils.copy(content, out);
            result = out.toString("UTF-8");
        }

        Log.d("PostCarResult", "result = " + result);
        return LoganSquare.parse(result, PostCarResult.class);

        // request.setParser(new JacksonFactory().createJsonObjectParser());
        // return request.execute().parseAs(getResultType());
    }

}
