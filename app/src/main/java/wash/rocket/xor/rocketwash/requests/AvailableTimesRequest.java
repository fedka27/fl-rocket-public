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

import wash.rocket.xor.rocketwash.model.AvailableTimesResult;

public class AvailableTimesRequest extends GoogleHttpClientSpiceRequest<AvailableTimesResult> {

    private String baseUrl;
    private String session_id;
    private int id;
    private String time_range_start;
    private String time_range_end;
    private int services_duration;


    public AvailableTimesRequest(String session_id, int id, String time_range_start, String time_range_end, int services_duration) {
        super(AvailableTimesResult.class);
        this.baseUrl = "http://test.rocketwash.me/v2/service_locations/%d/available_times";
        this.session_id = session_id;
        this.id = id;
        this.time_range_start = time_range_start;
        this.time_range_end = time_range_end;
        this.services_duration = services_duration;
    }

    @Override
    public AvailableTimesResult loadDataFromNetwork() throws IOException {

        String uri = Uri.parse(String.format(baseUrl, id))
                .buildUpon()
                .appendQueryParameter("time_range_start", time_range_start)
                .appendQueryParameter("time_range_end", time_range_end)
                .appendQueryParameter("services_duration", "" + services_duration)
                .build().toString();

        Log.d("loadDataFromNetwork", "uri = " + uri);

        HttpHeaders header = new HttpHeaders();
        header.set("X-Rocketwash-Session-Id", session_id);
        //header.set("Accept", "application/json");
        HttpRequest request = getHttpRequestFactory().buildGetRequest(new GenericUrl(uri)).setHeaders(header);

        String result = ""; //request.execute().parseAsString();

        InputStream content = request.execute().getContent();
        if (content != null) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            IOUtils.copy(content, out);
            result = out.toString("UTF-8");
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
        return mapper.readValue(result, getResultType());
    }

}
