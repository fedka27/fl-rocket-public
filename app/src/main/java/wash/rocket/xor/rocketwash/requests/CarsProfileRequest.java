package wash.rocket.xor.rocketwash.requests;

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

import roboguice.util.temp.Ln;
import wash.rocket.xor.rocketwash.model.CarsProfileResult;

public class CarsProfileRequest extends GoogleHttpClientSpiceRequest<CarsProfileResult> {

    private String baseUrl;
    private String session_id;

    public CarsProfileRequest(String session_id) {
        super(CarsProfileResult.class);
        this.baseUrl = "http://test.rocketwash.me/v2/cars";
        this.session_id = session_id;
    }

    @Override
    public CarsProfileResult loadDataFromNetwork() throws IOException {
        Ln.d("Call web service " + baseUrl);

        HttpHeaders header = new HttpHeaders();
        header.set("X-Rocketwash-Session-Id", session_id);
        HttpRequest request = getHttpRequestFactory().buildGetRequest(new GenericUrl(baseUrl)).setHeaders(header);


        String result = "";
        InputStream content = request.execute().getContent();
        if (content != null) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            IOUtils.copy(content, out);
            result = out.toString("UTF-8");
        }

        Log.d("loadDataFromNetwork", " res = " + result);
        //JsonNode json = new ObjectMapper().readTree(result);
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.readValue(result, getResultType());

    }

}