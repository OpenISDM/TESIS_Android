package sinica.earth.tesis.rest.service;



import java.util.ArrayList;

import retrofit2.Call;

import retrofit2.http.GET;
import retrofit2.http.Query;
import sinica.earth.tesis.EarthquakeEvents;

/**
 * Created by JanSu on 7/19/16.
 */
public interface TESISApiService {

    @GET("getid.php")
    Call<ArrayList<String>> getId(
            @Query("start") String start,
            @Query("end") String end);

    @GET("processdatamobile.php")
    Call<EarthquakeEvents> getEarthquakeData(
            @Query("firstid") String firstId,
            @Query("secondid") String secondId);
}
