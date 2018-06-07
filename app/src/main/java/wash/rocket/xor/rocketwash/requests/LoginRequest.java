package wash.rocket.xor.rocketwash.requests;

import android.net.Uri;
import android.util.Log;

import com.bluelinelabs.logansquare.LoganSquare;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.util.IOUtils;
import com.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import roboguice.util.temp.Ln;
import wash.rocket.xor.rocketwash.model.LoginResult;
import wash.rocket.xor.rocketwash.util.Constants;

public class LoginRequest extends GoogleHttpClientSpiceRequest<LoginResult> {
    private static final String TAG = LoginRequest.class.getSimpleName();

    private String baseUrl;
    private String phone;
    private String pin;

    public LoginRequest(String phone, String pin) {
        super(LoginResult.class);
        this.baseUrl = Constants.URL + "session/sign_in";
        this.phone = phone;
        this.pin = pin;
    }

    @Override
    public LoginResult loadDataFromNetwork() throws IOException {
        Ln.d("Call web service " + baseUrl);

        String uri = Uri.parse(baseUrl)
                .buildUpon()
                .appendQueryParameter("phone", phone.replace("+", ""))
                .appendQueryParameter("pin", pin)
                .build().toString();

        Log.d("loadDataFromNetwork", "uri = " + uri);

        HttpRequest request = getHttpRequestFactory().buildPostRequest(new GenericUrl(uri), null);

        InputStream content = request.execute().getContent();
        String result = "";

        if (content != null) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            IOUtils.copy(content, out);
            result = out.toString("UTF-8");
        }

        Log.d("LoginResult", "result = " + result);

        LoginResult res = LoganSquare.parse(result, LoginResult.class);

        Log.w("LoginResult", " end parse json ");

        return res;

    }

}
