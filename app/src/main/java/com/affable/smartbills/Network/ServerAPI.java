package com.affable.smartbills.Network;




import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by mRashid on 21/09/2020.
 */

public interface ServerAPI {






    @FormUrlEncoded
    @POST(UrlUtils.createUser)
    Call<UserInfo>  insertUser(

            @Field("compname") String companyName,
            @Field("address") String address ,
            @Field("vat") String  vat,
            @Field("mobile") String mobile,
            @Field("email") String email,
            @Field("accname") String accountName
    );





}
