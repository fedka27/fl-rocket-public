package wash.rocket.xor.rocketwash.requests;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;

import java.io.IOException;

import roboguice.util.temp.Ln;
import wash.rocket.xor.rocketwash.model.CarsMakesResult;

public class CarsMakesRequest extends GoogleHttpClientSpiceRequest<CarsMakesResult> {

    private String baseUrl;

    public CarsMakesRequest(String param) {
        super(CarsMakesResult.class);
        this.baseUrl = "http://test.rocketwash.me/v2/car_makes";
    }

    @Override
    public CarsMakesResult loadDataFromNetwork() throws IOException {
        Ln.d("Call web service " + baseUrl);
        HttpRequest request = getHttpRequestFactory().buildGetRequest(new GenericUrl(baseUrl));
        request.setParser(new JacksonFactory().createJsonObjectParser());
        return request.execute().parseAs(getResultType());
    }

}