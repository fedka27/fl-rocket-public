package wash.rocket.xor.rocketwash.requests;

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
import wash.rocket.xor.rocketwash.model.CarsMakesResult;
import wash.rocket.xor.rocketwash.util.Constants;

public class CarsMakesRequest extends GoogleHttpClientSpiceRequest<CarsMakesResult> {

    private String baseUrl;

    public CarsMakesRequest() {
        super(CarsMakesResult.class);
        this.baseUrl = Constants.URL + "car_makes";
    }

    @Override
    public CarsMakesResult loadDataFromNetwork() throws IOException {
        Ln.d("Call web service " + baseUrl);
        HttpRequest request = getHttpRequestFactory().buildGetRequest(new GenericUrl(baseUrl));

        String result = "";
        InputStream content = request.execute().getContent();
        if (content != null) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            IOUtils.copy(content, out);
            result = out.toString("UTF-8");
        }

        //request.setParser(new JacksonFactory().createJsonObjectParser());
        //return request.execute().parseAs(getResultType());

        Log.d("CarsMakesResult", " res = " + result);
        Log.w("CarsMakesResult", " start parse json ");

        //ObjectMapper mapper = new ObjectMapper();
        //mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //WashServiceResult res = mapper.readValue(result, getResultType());

        CarsMakesResult res = LoganSquare.parse(result, CarsMakesResult.class);

        Log.w("CarsMakesResult", " end parse json ");

        return res;

    }

}