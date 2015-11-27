package wash.rocket.xor.rocketwash.requests;

import android.net.Uri;
import android.util.Log;

import com.bluelinelabs.logansquare.LoganSquare;
import com.google.api.client.util.IOUtils;
import com.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import wash.rocket.xor.rocketwash.model.ChoiceService;
import wash.rocket.xor.rocketwash.model.ReservationResult;
import wash.rocket.xor.rocketwash.util.Constants;

public class ReservationRequest extends GoogleHttpClientSpiceRequest<ReservationResult> {


    private String session_id;
    private int carwash_id;
    private int car_id;
    private ArrayList<ChoiceService> list;
    private String url;
    private String time_from;


    public ReservationRequest(String session_id, int carwash_id, int car_id, ArrayList<ChoiceService> list, String time_from) {
        super(ReservationResult.class);
        this.url = Constants.URL + "reservations";

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
                        //.appendQueryParameter("time_from", "" + time_from.replace("+", "%2B").replace(":", "%3A"));
                .appendQueryParameter("time_from", "" + time_from);

        String s = "";

        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).isCheck()) {
                    bld.appendQueryParameter("services[][id]", "" + list.get(i).getId());
                    bld.appendQueryParameter("services[][count]", "" + 1);
                    //s = s  + "&" + String.format("services[][id]=%d&services[][count]=%d", list.get(i).getId(), 1 );
                    //%5D%3D
                    //s = s  + "%26" + "services%5B%5D%5Bid%5D%3D"+list.get(i).getId()+"%26services%5B%5D%5Bcount%5D%3D1";
                    //s = s  + "&" + "services%5B%5D%5Bid%5D="+list.get(i).getId()+"&services%5B%5D%5Bcount%5D=1";
                }
            }
        }

        //bld.appendQueryParameter("time_from", "" + time_from.replace("+", "%2B"));
        Uri ur = bld.build();
        //String uri = bld.build().toString();
        String uri = ur.toString();
        Log.d("ReservationRequest", " uri = " + uri);

        // URL url = new URL(URLDecoder.decode(uri, "UTF-8"));
        // uri = url.toString() + s;
        // Log.d("ReservationRequest", " uri = " + url.toString() + s);

        //HttpHeaders header = new HttpHeaders();
        //header.set("X-Rocketwash-Session-Id", session_id);
        //GenericUrl u = new GenericUrl(uri);
        //Log.d("ReservationRequest", " u = " + u.toString());
        //HttpRequest request = getHttpRequestFactory().buildPostRequest(u, null).setHeaders(header);

        String result = "";

        HttpPost httpGet = new HttpPost(uri);
        HttpClient client = new DefaultHttpClient();
        httpGet.addHeader("X-Rocketwash-Session-Id", session_id);
        HttpResponse response = client.execute(httpGet);
        //response = client.execute(httpGet);
        //response = client.execute(httpGet);
        //InputStream content = request.execute().getContent();
        InputStream content = response.getEntity().getContent();
        if (content != null) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            IOUtils.copy(content, out);
            result = out.toString("UTF-8");
        }

        Log.d("ReservationRequest", " res = " + result);

        //ObjectMapper mapper = new ObjectMapper();
        //mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //mapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
        //ReservationResult res = mapper.readValue(result, getResultType());
        ReservationResult res = LoganSquare.parse(result, ReservationResult.class);

        return res;
    }

}