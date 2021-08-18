package com.example.arcore_navigation_ce;

//import com.aut.navigation.NeshanOutDoor.PlacesService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class ServiceModule {

    public ServiceModule() {

    }

    // Dagger will only look for methods annotated with @Provides
//    @Provides
//    @Singleton
//    // Application reference must come from AppModule.class
//    SharedPreferences providesSharedPreferences(Application application) {
//        return PreferenceManager.getDefaultSharedPreferences(application);
//    }


    @Provides
    @Singleton
    public static Retrofit provideRetrofit(String url) {

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();


        return retrofit;
    }

    //---------------------Place Service-------------------------------------

    @Provides
    @Singleton
    public static PlacesService providePlaceService() {
        return createService(PlacesService.class,"https://api.neshan.org");
    }


    private static <T> T createService(Class<T> service, String url)
    {
        return provideRetrofit(url)
                .create(service);
    }

//    @Provides
//    @Singleton
//    PlaceProviders providePlaceProviders(Observable<Place> placeObservable){
//        return ;
//    }
}