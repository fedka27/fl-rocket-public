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

public class NearestWashServiceRequest extends GoogleHttpClientSpiceRequest<WashServiceResult> {

    private String baseUrl;
    private double lat;
    private double lon;
    private int distance;
    private int page;
    private String id_session;

    public NearestWashServiceRequest(double lat, double lon, int distance, int page, String id_session) {
        super(WashServiceResult.class);
        this.baseUrl = "http://test.rocketwash.me/v2/service_locations/nearest";
        this.lat = lat;
        this.lon = lon;
        this.distance = distance;
        this.page = page;
        this.id_session = id_session;
    }

    @Override
    public WashServiceResult loadDataFromNetwork() throws IOException {

        String uri = Uri.parse(baseUrl)
                .buildUpon()
                .appendQueryParameter("latitude", "" + lat)
                .appendQueryParameter("longitude", "" + lon)
                .appendQueryParameter("distance", "" + distance)
                .appendQueryParameter("page", "" + page)
                .build().toString();

        Log.d("loadDataFromNetwork", "uri = " + uri);

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

        Log.d("loadDataFromNetwork", " res = " + result);

        //JsonNode json = new ObjectMapper().readTree(result);
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.readValue(result, getResultType());

        //request.setParser( new JacksonFactory().createJsonObjectParser() );
        //request.setParser( new GsonFactory().createJsonObjectParser());
        //request.setParser(  );
        //return request.execute().parseAs(getResultType());
    }


}
