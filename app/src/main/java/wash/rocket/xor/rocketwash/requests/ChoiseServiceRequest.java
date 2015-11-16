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

import wash.rocket.xor.rocketwash.model.ChoiceServiceResult;
import wash.rocket.xor.rocketwash.util.Constants;

public class ChoiseServiceRequest extends GoogleHttpClientSpiceRequest<ChoiceServiceResult> {

    private String baseUrl;

    private int id_service;
    private String id_session;
    private int car_model_id;

    public ChoiseServiceRequest(int id_service, int car_model_id, String id_session) {
        super(ChoiceServiceResult.class);
        this.baseUrl = Constants.URL + "services";
        this.id_service = id_service;
        this.car_model_id = car_model_id;
        this.id_session = id_session;
    }

    @Override
    public ChoiceServiceResult loadDataFromNetwork() throws IOException {

        String uri = Uri.parse(baseUrl)
                .buildUpon()
                .appendQueryParameter("service_location_id", "" + id_service)
                .appendQueryParameter("car_model_id", "" + car_model_id)
                .build().toString();

        Log.d("ChoiceServiceResult", "uri = " + uri);

        HttpHeaders header = new HttpHeaders();
        header.set("X-Rocketwash-Session-Id", id_session);
        //header.set("Accept", "application/json");
        HttpRequest request = getHttpRequestFactory().buildGetRequest(new GenericUrl(uri)).setHeaders(header);

        String result = ""; //request.execute().parseAsString();

        InputStream content = request.execute().getContent();
        if (content != null) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            IOUtils.copy(content, out);
            result = out.toString("UTF-8");
        }

        //Log.d("loadDataFromNetwork", " res = " + result);
        //JsonNode json = new ObjectMapper().readTree(result);
        //ObjectMapper mapper = new ObjectMapper();
        //mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //return mapper.readValue(result, getResultType());

        Log.d("ChoiceServiceResult", " res = " + result);
        Log.w("ChoiceServiceResult", " start parse json ");

        //ObjectMapper mapper = new ObjectMapper();
        //mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //WashServiceResult res = mapper.readValue(result, getResultType());

        ChoiceServiceResult res = LoganSquare.parse(result, ChoiceServiceResult.class);
        Log.w("ChoiceServiceResult", " end parse json ");

        return res;
    }


}
