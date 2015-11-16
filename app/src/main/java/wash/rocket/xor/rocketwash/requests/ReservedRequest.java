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

import wash.rocket.xor.rocketwash.model.ReservedResult;
import wash.rocket.xor.rocketwash.util.Constants;

public class ReservedRequest extends GoogleHttpClientSpiceRequest<ReservedResult> {

    private String session_id;
    private String url;

    public ReservedRequest(String session_id) {
        super(ReservedResult.class);
        this.url = Constants.URL + "reservations";
        this.session_id = session_id;
    }

    @Override
    public ReservedResult loadDataFromNetwork() throws IOException {
        // Ln.d("Call web service " + baseUrl);

        Uri.Builder bld = Uri.parse(url).buildUpon();
        String uri = bld.build().toString();
        Log.d("ReservationRequest", " uri = " + uri);

        HttpHeaders header = new HttpHeaders();
        header.set("X-Rocketwash-Session-Id", session_id);

        HttpRequest request = getHttpRequestFactory().buildGetRequest(new GenericUrl(uri)).setHeaders(header);

        String result = "";
        InputStream content = request.execute().getContent();

        if (content != null) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            IOUtils.copy(content, out);
            result = out.toString("UTF-8");
        }

        Log.d("ReservationRequest", " res = " + result);

        //ObjectMapper mapper = new ObjectMapper();
       // mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
       // mapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);

        ReservedResult res = LoganSquare.parse(result, ReservedResult.class);
        //ReservedResult res = mapper.readValue(result, getResultType());

        return res;
    }

}