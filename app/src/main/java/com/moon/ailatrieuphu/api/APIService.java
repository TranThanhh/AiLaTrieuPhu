package com.moon.ailatrieuphu.api;

import com.moon.ailatrieuphu.model.CauHoi;
import com.moon.ailatrieuphu.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface APIService {
    //-----USER-----
    //check exists email, nickname
    @POST("/user/check")
    Call<String> checkUserExists(@Body User user);

    //register
    @POST("/user/addUser")
    Call<Integer> addUser(@Body User user);

    //login
    @POST("/user/login")
    Call<User> getUserLogin(@Body User user);

    //change password
    @PUT("/user/changePass")
    Call<String> updatePassword(@Body User user);

    //Change Score
    @PUT("/user/modifyScore")
    Call<String> modifyScore(@Body User user);

    //----------------------------------CAU HOI----------------------------------------------
    //GET List cau hoi theo idLoaiCH
    @GET("/cauhoi/list2")
    Call<List<CauHoi>> getCauHoiByIdLoaiCH(@Query("idLoaiCH") int idLoaiCH);

    //-----------------------------------ADMIN----------------------------------------------
    //Get list all cauhoi.
    @GET("/admin/cauhoi/all")
    Call<List<CauHoi>> getAllCauHoi();
    //add new CauHoi
    @POST("/admin/cauhoi/one")
    Call<String> addCauHoi(@Body CauHoi cauhoi);
    //get list user.
    @GET("/admin/user/all-player")
    Call<List<User>> getAllPlayer();
    //get list moderator
    @GET("/admin/user/all-moderator")
    Call<List<User>> getAllModerator();
    //update role a user.
    @PUT("/admin/user/one/rolelevel")
    Call<String> updateRoleLevel(@Query("idUser") int idUser,@Query("roleLevel") int roleLevel, @Query("updateTime") String updateTime);
    //delete a user.
    @DELETE("/admin/user/one")
    Call<String> deleteUser(@Query("idUser") int idUser);
    //cout cauhois of user.
    @POST("/admin/user/size-of-cauhoi")
    Call<Integer> countCauHoiOfUser(@Query("idUser") int idUser);
}
