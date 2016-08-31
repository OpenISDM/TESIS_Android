package sinica.earth.tesis.rest;


import android.util.Log;

import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;
import sinica.earth.tesis.rest.service.TESISApiService;

/**
 *
 */
public class RestApiClient {

    private static final String BASE_URL = "http://tesis.earth.sinica.edu.tw/common/php/";

    private TESISApiService mTESISApiService;

    public RestApiClient() {

        Log.i("api", "Apicleint");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mTESISApiService = retrofit.create(TESISApiService.class);

    }

    public TESISApiService getTESISApiService(){

        return mTESISApiService;
    }


}

