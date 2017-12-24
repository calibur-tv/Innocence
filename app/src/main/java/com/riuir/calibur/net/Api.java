package com.riuir.calibur.net;

import com.riuir.calibur.data.DramaListResp;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface Api {

    @POST("drama/list")
    Observable<DramaListResp> dramaList(@Body Map<String, Object> argsMap);

    @POST("drama/list")
    Call<DramaListResp> dramaList2(@Body Map<String, Object> argsMap);
}