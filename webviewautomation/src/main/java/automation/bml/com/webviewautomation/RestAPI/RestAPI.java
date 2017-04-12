package automation.bml.com.webviewautomation.RestAPI;

import automation.bml.com.webviewautomation.RestAPI.DataModel.TransactionRequest;
import automation.bml.com.webviewautomation.RestAPI.DataModel.TransactionResponse;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RestAPI {
    String BASE_URL = "http://api.delivr.online/ifapi.php";
    // Login

    @POST("")
    Call<TransactionResponse> loadData(@Body TransactionRequest user);

    // API instance class
    class Factory {
        private static RestAPI service;

        public static RestAPI getInstance() {
            if (service == null) {
                Retrofit retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(BASE_URL).build();
                service = retrofit.create(RestAPI.class);
                return service;
            } else {
                return service;
            }
        }
    }
}

