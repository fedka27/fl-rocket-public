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

import wash.rocket.xor.rocketwash.model.ReserveCancelResult;
import wash.rocket.xor.rocketwash.util.Constants;

public class ReserveCancelRequest extends GoogleHttpClientSpiceRequest<ReserveCancelResult> {

    private String session_id;
    private String url;
    private int id;
    private int organization_id;

    public ReserveCancelRequest(String session_id, int id, int organization_id) {
        super(ReserveCancelResult.class);
        this.url = Constants.URL + "reservations/%d";
        this.session_id = session_id;
        this.id = id;
        this.organization_id = organization_id;
    }

    @Override
    public ReserveCancelResult loadDataFromNetwork() throws IOException {
        // Ln.d("Call web service " + baseUrl);

        Uri.Builder bld = Uri.parse(String.format(url, id)).buildUpon();
        String uri = bld
                .appendQueryParameter("organization_id", "" + organization_id)
                .build().toString();
        Log.d("ReservationRequest", " uri = " + uri);

        HttpHeaders header = new HttpHeaders();
        header.set("X-Rocketwash-Session-Id", session_id);

        HttpRequest request = getHttpRequestFactory().buildDeleteRequest(new GenericUrl(uri)).setHeaders(header);

        String result = "";
        InputStream content = request.execute().getContent();

        if (content != null) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            IOUtils.copy(content, out);
            result = out.toString("UTF-8");
        }

        Log.d("ReserveCancelResult", " res = " + result);

        //ObjectMapper mapper = new ObjectMapper();
        //mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //mapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
        //ReserveCancelResult res = mapper.readValue(result, getResultType());

        ReserveCancelResult res = LoganSquare.parse(result, ReserveCancelResult.class);

        return res;
    }

}