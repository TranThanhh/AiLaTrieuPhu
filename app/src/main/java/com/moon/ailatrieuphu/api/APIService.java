package com.moon.ailatrieuphu.api;

import com.moon.ailatrieuphu.model.CauHoi;
import com.moon.ailatrieuphu.model.Diem;
import com.moon.ailatrieuphu.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface APIService {
    //-----USER-----
    //check exists email, nickname
    @POST("user/check")
    Call<String> checkUserExists(@Body User user);

    //register
    @POST("user/addUser")
    Call<Integer> addUser(@Body User user);

    //login
    @POST("user/login")
    Call<User> getUserLogin(@Body User user);

    //change password
    @PUT("user/changePass")
    Call<String> updatePassword(@Body User user);

    //-----SCORE-----
    //set default score
    @POST("diem/setScore")
    Call<String> setScore(@Body Diem diem);

    //Change Score
    @PUT("diem/modifyScore")
    Call<String> modifyScore(@Body Diem diem);

    //Get HighScore
    @GET("diem/diemcao")
    Call<Integer> getHighScore(@Query("idUser") int idUser);

    //-----CAU HOI-----
    //GET List cau hoi theo idLoaiCH
    @GET("cauhoi/list2")
    Call<List<CauHoi>> getCauHoiByIdLoaiCH(@Query("idLoaiCH") int idLoaiCH);
    @POST("dapan/dapandung")
    //Check dap an dung
    Call<String> getDapAnDung(@Query("idCauHoi") int idCauHoi,@Query("dapanUser") String dapanUser);
//    @GET("/dapan")
//    Call<String> getDapAn(@Query("idCauHoi") int idCauHoi);
    //Get list all cauhoi.
    @GET("/cauhoi/list")
    Call<List<CauHoi>> getAllCauHoi();
    //add new CauHoi
    @POST("/cauhoi/one")
    Call<String> addCauHoi(@Body CauHoi cauhoi);

}
