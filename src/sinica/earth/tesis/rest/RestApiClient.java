package sinica.earth.tesis.rest;


import android.util.Log;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;
import sinica.earth.tesis.rest.service.TESISApiService;

/**
 *
 */
public class RestApiClient {

    private static final String BASE_URL = "http://tesis.earth.sinica.edu.tw/common/php/";

    private TESISApiService mTESISApiService;

    final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .build();

    public RestApiClient() {

        Log.i("api", "ApiClient");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        mTESISApiService = retrofit.create(TESISApiService.class);

    }

    public TESISApiService getTESISApiService() {

        return mTESISApiService;
    }


}

