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
    @POST("/users-check")
    Call<String> checkUserExists(@Body User user);

    //register
    @POST("/users")
    Call<Integer> addUser(@Body User user);

    //login
    @POST("/users-login")
    Call<User> getUserLogin(@Body User user);

    //change password
    @PUT("/users/password")
    Call<String> updatePassword(@Body User user);

    //Change Score
    @PUT("/users/score")
    Call<String> modifyScore(@Body User user);

    //----------------------------------CAU HOI----------------------------------------------
    //GET List cau hoi theo idLoaiCH
    @GET("/cauhois/loai")
    Call<List<CauHoi>> getCauHoiByIdLoaiCH(@Query("idLoaiCH") int idLoaiCH);

    //-----------------------------------ADMIN----------------------------------------------
    //Get list all cauhoi.
    @GET("/admin/cauhois")
    Call<List<CauHoi>> getAllCauHoiActive();
    //add new CauHoi
    @POST("/admin/cauhois")
    Call<String> addCauHoi(@Body CauHoi cauHoiNew);
    // edit cauhoi.
    @PUT("/admin/cauhois")
    Call<String> updateCauHoi(@Body CauHoi cauHoiEdit);
    //delete cauhoi.
    @DELETE("/admin/cauhois")
    Call<String> deleteCauHoi(@Query("idCauHoi") int idCauHoi);

    //get list user.
    @GET("/admin/users-player")
    Call<List<User>> getAllPlayer();
    //get list moderator
    @GET("/admin/users-moderator")
    Call<List<User>> getAllModerator();
    //update role a user.
    @PUT("/admin/users/role-level")
    Call<String> updateRoleLevel(@Query("idUser") int idUser,@Query("roleLevel") int roleLevel, @Query("updateTime") String updateTime);
    //delete a user.
    @DELETE("/admin/users")
    Call<String> deleteUser(@Query("idUser") int idUser);
    //cout cauhois of user.
    @POST("/admin/users/size-of-cauhoi")
    Call<Integer> countCauHoiOfUser(@Query("idUser") int idUser);
}
