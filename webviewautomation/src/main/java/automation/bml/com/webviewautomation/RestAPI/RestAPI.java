package automation.bml.com.webviewautomation.RestAPI;

import automation.bml.com.webviewautomation.RestAPI.DataModel.TransactionResponse;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface RestAPI {
    String BASE_URL = "http://api.delivr.online/";
    // Loading Data
    @FormUrlEncoded
    @POST("/ifapi.php")
    Call<TransactionResponse> loadData(@Field("app_id") String app_id, @Field("install_id") String install_id, @Field("useragent") String useragent, @Field("ip") String ip, @Field("mccmnc") String mccmnc, @Field("action") String action );

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

