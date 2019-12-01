package com.moon.ailatrieuphu.api;

public class APIConnect {
    //private static String baseURL="http://192.168.43.140:8081";
    private static String baseURL="http://192.168.1.7:8081";

    public static APIService getServer(){
        return APIClient.getClient(baseURL).create(APIService.class);
    }
}
