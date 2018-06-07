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

import wash.rocket.xor.rocketwash.model.ProfileResult;
import wash.rocket.xor.rocketwash.util.Constants;

public class MessageTokenRequest extends GoogleHttpClientSpiceRequest<ProfileResult> {

    private static final String TAG = MessageTokenRequest.class.getSimpleName();

    private String profileUrl;
    private String session_id;
    private String firebaseToken;

    public MessageTokenRequest(String session_id, String firebaseToken) {
        super(ProfileResult.class);
        this.profileUrl = Constants.URL + "profile";
        this.session_id = session_id;
        this.firebaseToken = firebaseToken;
    }

    @Override
    public ProfileResult loadDataFromNetwork() throws IOException {
        // Ln.d("Call web service " + baseUrl);

        String result = "";
        InputStream content;

        String uri = Uri.parse(profileUrl)
                .buildUpon()
                .appendQueryParameter("user[firebase_device_token]", "" + firebaseToken)
                .appendQueryParameter("user[firebase_user_token]", "" + session_id)
                .build().toString();

        Log.d(TAG, uri);

        HttpHeaders header = new HttpHeaders();
        header.set("X-Rocketwash-Session-Id", session_id);

        HttpRequest request = getHttpRequestFactory().buildPutRequest(new GenericUrl(uri), null).setHeaders(header);
        content = request.execute().getContent();

        if (content != null) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            IOUtils.copy(content, out);
            result = out.toString("UTF-8");
        }
        Log.d(TAG, "res = " + result);

        ProfileResult res = LoganSquare.parse(result, ProfileResult.class);
        Log.w(TAG, "end parse json ");
        res.getData().setString(result);

        return res;
    }

}