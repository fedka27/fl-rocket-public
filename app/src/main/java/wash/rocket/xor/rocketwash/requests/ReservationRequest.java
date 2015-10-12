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
import java.util.ArrayList;

import wash.rocket.xor.rocketwash.model.ChoiseService;
import wash.rocket.xor.rocketwash.model.ReservationResult;

public class ReservationRequest extends GoogleHttpClientSpiceRequest<ReservationResult> {


    private String session_id;
    private int carwash_id;
    private int car_id;
    private ArrayList<ChoiseService> list;
    private String url;
    private String time_from;


    public ReservationRequest(String session_id, int carwash_id, int car_id, ArrayList<ChoiseService> list, String time_from  ) {
        super(ReservationResult.class);
        this.url = "http://test.rocketwash.me/v2/reservations";

        this.session_id = session_id;
        this.carwash_id = carwash_id;
        this.car_id = car_id;
        this.list = list;
        this.time_from = time_from;
    }

    @Override
    public ReservationResult loadDataFromNetwork() throws IOException {
        // Ln.d("Call web service " + baseUrl);


        Uri.Builder bld = Uri.parse(url)
                .buildUpon()
                //.appendQueryParameter("session_id", "" + session_id)
                .appendQueryParameter("carwash_id", "" + carwash_id)
                .appendQueryParameter("car_id", "" + car_id)
                .appendQueryParameter("time_from", "" + time_from);

        if (list != null)
        {
            for (int i = 0; i < list.size();i++)
            {
                if (list.get(i).isCheck() )
                    bld.appendQueryParameter("services[][id]", "" + list.get(i).getId() );
            }
        }

        String uri = bld.build().toString();

        Log.d("ReservationRequest", " uri = " + uri);


        HttpHeaders header = new HttpHeaders();
        header.set("X-Rocketwash-Session-Id", session_id);

        HttpRequest request = getHttpRequestFactory().buildPostRequest(new GenericUrl(uri), null).setHeaders(header);

        String result = "";
        InputStream content = request.execute().getContent();

        if (content != null) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            IOUtils.copy(content, out);
            result = out.toString("UTF-8");
        }

        Log.d("ReservationRequest", " res = " + result);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);

        ReservationResult res = mapper.readValue(result, getResultType());

        return res;
    }

}