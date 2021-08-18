package com.example.arcore_navigation_ce;

//import com.aut.navigation.Outdoor.Routes;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PlacesService {

    @Headers("Api-Key: service.02vim17PxAA3WLoYqCq2zpIO1x3yeYLenYbD6TdQ")
    @GET("v3/direction?")
    Observable<Root> getRoute(@Query("origin") String origin, @Query("type") String type, @Query("destination") String destination);

//    @GET("api/InsuranceRfps/life/{id}/questions/unanswered")
//    Observable<List<LifeQuestions>> getQuestionsUnAnswered(@Path("id") String id);
//
//    @POST("api/InsuranceRfps/track")
//    Observable<LifeInsStates> getTrack(@Body RequestBody jsonBody);
//
//    @POST("api/InsuranceRfps/answer")
//    Observable<List<LifeQuestions>> getAnswer(@Body RequestBody jsonBody);
}
