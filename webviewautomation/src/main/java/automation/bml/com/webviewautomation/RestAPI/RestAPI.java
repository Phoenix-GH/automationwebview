package automation.bml.com.webviewautomation.RestAPI;

import automation.bml.com.webviewautomation.RestAPI.DataModel.TransactionResponse;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface RestAPI {
    String BASE_URL = "http://api.delivr.online/";
    // Loading data
    @FormUrlEncoded
    @POST("/ifapi.php")
    Call<TransactionResponse> loadData(@Field("app_id") String app_id, @Field("install_id") String install_id, @Field("useragent") String useragent, @Field("ip") String ip, @Field("mccmnc") String mccmnc, @Field("action") String action );

    // Updating data
    @FormUrlEncoded
    @POST("/ifapi.php")
    Call<String> updateData(@Field("action") String action, @Field("transaction_id") String transaction_id, @Field("status") String status);

    // Uploading screenshots
    @FormUrlEncoded
    @POST("/ifapi-screenshot.php")
    Call<String> postScreenShot(@Field("image") MultipartBody.Part image);
}

