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

        //JSONObject obj = new JSONObject();
        //obj.put("");
        //HttpRequest request = getHttpRequestFactory().buildGetRequest(new GenericUrl(baseUrl), ByteArrayContent.fromString("application/json", obj.toString()));

        //baseUrl = baseUrl + "phone=" + URLEncoder.encode(phone, "utf-8") + "&pin=" + pin;

        String uri = Uri.parse(baseUrl)
                .buildUpon()
                //.appendQueryParameter("phone", URLEncoder.encode(phone, "utf-8"))
                //.appendQueryParameter("pin", URLEncoder.encode(pin, "utf-8"))
                .appendQueryParameter("phone", phone.replace("+", ""))
                .appendQueryParameter("pin", pin)
                .build().toString();

        Log.d("loadDataFromNetwork", "uri = " + uri);

        HttpRequest request = getHttpRequestFactory().buildPostRequest(new GenericUrl(uri), null);
        //buildGetRequest(new GenericUrl(baseUrl));
        //request.setParser(new JacksonFactory().createJsonObjectParser());
        //HttpResponse f = request.execute();
        //Log.d("responce", "" + f.toString());
        //return f.parseAs(getResultType());

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
