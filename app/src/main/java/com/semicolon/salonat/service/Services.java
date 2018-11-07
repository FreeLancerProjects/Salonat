package com.semicolon.salonat.service;

import com.semicolon.salonat.models.BankAccountModel;
import com.semicolon.salonat.models.Country_City_Model;
import com.semicolon.salonat.models.MyReservationModel;
import com.semicolon.salonat.models.ResponsModel;
import com.semicolon.salonat.models.SalonModel;
import com.semicolon.salonat.models.ServiceModel;
import com.semicolon.salonat.models.SocialContactModel;
import com.semicolon.salonat.models.Terms_Conditions;
import com.semicolon.salonat.models.UserModel;
import com.semicolon.salonat.models.VoteModel;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface Services {
    @GET("AboutApp/SocialMedia")
    Call<SocialContactModel> getContacts();

    @GET("AboutApp/Banks")
    Call<List<BankAccountModel>> getBanks();

    @GET("AboutApp/TermsAndConditions")
    Call<Terms_Conditions> getTerms_Condition();

    @FormUrlEncoded
    @POST("AboutApp/ContactUs")
    Call<ResponsModel> sendProblemViaContact(@Field("name") String name,
                                             @Field("email") String email,
                                             @Field("subject") String subject,
                                             @Field("message") String message);
    @GET("AppUser/Logout/{user_id}")
    Call<ResponsModel> logout(@Path("user_id") String user_id);

    @FormUrlEncoded
    @POST("AppUser/UpdateLocation/{user_id}")
    Call<ResponsModel> updateLocation(@Path("user_id") String user_id,
                                      @Field("user_google_lat") double user_google_lat,
                                      @Field("user_google_long") double user_google_long
                                      );
    @FormUrlEncoded
    @POST("AppUser/UpdateTokenId/{user_id}")
    Call<ResponsModel> UpdateTokenId(@Path("user_id") String user_id,
                                     @Field("user_token_id") String user_token_id);
    @Multipart
    @POST("AppUser/SignUp")
    Call<UserModel> SignUp(@Part("user_pass") RequestBody user_pass,
                           @Part("user_phone") RequestBody user_phone,
                           @Part("user_country") RequestBody user_country,
                           @Part("user_email") RequestBody user_email,
                           @Part("user_full_name") RequestBody user_full_name,
                           @Part("user_token_id") RequestBody user_token_id,
                           @Part("user_google_lat") RequestBody user_google_lat,
                           @Part("user_google_long") RequestBody user_google_long,
                           @Part("user_city") RequestBody user_city,
                           @Part("user_address") RequestBody user_address,
                           @Part MultipartBody.Part user_photo
                           );

    @FormUrlEncoded
    @POST("AppUser/Login")
    Call<UserModel> SignIn(@Field("user_phone") String user_phone,
                           @Field("user_pass") String user_pass
                           );

    @FormUrlEncoded
    @POST("AppUser/RestMyPass")
    Call<ResponsModel> resetPassword(@Field("user_email") String user_email);

    @Multipart
    @POST("AppUser/Profile/{user_id}")
    Call<UserModel> UpdateProfileImage(@Path("user_id") String user_id,
                                       @Part("user_phone") RequestBody user_phone,
                                       @Part("user_country") RequestBody user_country,
                                       @Part("user_email") RequestBody user_email,
                                       @Part("user_full_name") RequestBody user_full_name,
                                       @Part("user_city") RequestBody user_city,
                                       @Part("user_address") RequestBody user_address,
                                       @Part MultipartBody.Part user_photo
                                       );

    @Multipart
    @POST("AppUser/Profile/{user_id}")
    Call<UserModel> UpdateProfileData(@Path("user_id") String user_id,
                                       @Part("user_phone") RequestBody user_phone,
                                       @Part("user_country") RequestBody user_country,
                                       @Part("user_email") RequestBody user_email,
                                       @Part("user_full_name") RequestBody user_full_name,
                                       @Part("user_city") RequestBody user_city,
                                       @Part("user_address") RequestBody user_address
    );

    @FormUrlEncoded
    @POST("AppUser/UpdatePass/{user_id}")
    Call<UserModel> UpdatePassword(@Path("user_id") String user_id,
                                   @Field("user_old_pass") String user_old_pass,
                                   @Field("user_new_pass") String user_new_pass
                                   );

    @GET("Api/AllSalons")
    Call<List<SalonModel>> getAllSalons();

    @GET("Api/OneSalonService/{id_salon}/{in_type}")
    Call<List<ServiceModel>> getServices(@Path("id_salon") String id_salon,
                                         @Path("in_type") String in_type
                                         );

    @GET("Api/CustomerOpinions/{id_salon}/{in_type}")
    Call<List<VoteModel>> getClientsVotes(@Path("id_salon") String id_salon,@Path("in_type") String in_type);

    @FormUrlEncoded
    @POST("Api/Resevation/{id_salon}")
    Call<ResponsModel> book(@Path("id_salon") String id_salon,
                            @Field("reservation_cost") String reservation_cost,
                            @Field("reservation_date") String reservation_date,
                            @Field("reservation_time") String reservation_time,
                            @Field("reservation_address") String reservation_address,
                            @Field("reservation_google_lat") String reservation_google_lat,
                            @Field("reservation_google_long") String reservation_google_long,
                            @Field("reservation_place") String reservation_place,
                            @Field("reservation_sevice[]") List<String> serviceIdsList

                            );

    @GET("AboutApp/Countries")
    Call<List<Country_City_Model>> getCountry_City();

    @GET("Api/MyResevation/{user_id}")
    Call<List<MyReservationModel>> getMyReservations(@Path("user_id") String user_id);

}

