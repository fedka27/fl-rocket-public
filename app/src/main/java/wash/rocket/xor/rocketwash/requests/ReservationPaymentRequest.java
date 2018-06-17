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

import wash.rocket.xor.rocketwash.model.ReservationPaymentResult;
import wash.rocket.xor.rocketwash.util.Constants;

public class ReservationPaymentRequest extends GoogleHttpClientSpiceRequest<ReservationPaymentResult> {
    private static final String TAG = ReservationPaymentRequest.class.getSimpleName();

    private String session_id;
    private int organization_id;
    private long transaction_id;
    private String url;


    public ReservationPaymentRequest(String session_id,
                                     int reservation_id,
                                     int organization_id,
                                     long transaction_id) {
        super(ReservationPaymentResult.class);
        this.url = Constants.URL + "reservations/" + reservation_id;

        this.session_id = session_id;
        this.organization_id = organization_id;
        this.transaction_id = transaction_id;
    }

    @Override
    public ReservationPaymentResult loadDataFromNetwork() throws IOException {

        Uri.Builder bld = Uri.parse(url)
                .buildUpon()
                .appendQueryParameter("organization_id", "" + organization_id)
                .appendQueryParameter("tinkoff_transaction_id", "" + transaction_id);

        Uri ur = bld.build();

        String uri = ur.toString();
        Log.d(TAG, " uri = " + uri);

        String result = "";

        HttpPost httpGet = new HttpPost(uri);
        HttpClient client = new DefaultHttpClient();
        httpGet.addHeader("X-Rocketwash-Session-Id", session_id);
        HttpResponse response = client.execute(httpGet);

        InputStream content = response.getEntity().getContent();
        if (content != null) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            IOUtils.copy(content, out);
            result = out.toString("UTF-8");
        }

        Log.d(TAG, " res = " + result);

        return LoganSquare.parse(result, ReservationPaymentResult.class);
    }

}