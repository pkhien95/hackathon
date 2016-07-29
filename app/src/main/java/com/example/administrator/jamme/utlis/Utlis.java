package com.example.administrator.jamme.utlis;

import java.util.LinkedHashMap;

public class Utlis {
    public static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
    public static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    public static final String BASE_URL = "http://192.168.56.1:8080";
    public static final String BASE_URL_AUTOCOMPLETE = "https://maps.googleapis.com";
    public static final String API_BROWSER = "AIzaSyBozYSCQjajM8XyVTOOQ5lpAvLCzpKSdrM";
    public static final String FIREBASE_URL = "https://android-chat.firebaseio-demo.com";

    public static final String INTENT_HOME_DETAIL_KEY = "intent_home_detail";

    public final static LinkedHashMap<Integer, String> eventMap = new LinkedHashMap<Integer, String>();
    static {
        eventMap.put(0, "Tín hiệu SOS");
        eventMap.put(1, "Kẹt xe");
        eventMap.put(2, "Tai nạn");
        eventMap.put(3, "Khác");
    };

}
