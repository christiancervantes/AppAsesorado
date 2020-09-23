package com.example.appasesorado.Remote;

import com.example.appasesorado.Modelos.FCMResponse;
import com.example.appasesorado.Modelos.FCMSendData;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public
interface IFCMService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAqf226pc:APA91bGVqC6sXkzobSyAMh_UdRj2-DTj0cyKBsF8iwq27aqNQjjOIakonfLReRWYnx1Y22x0N59VgYT5mRx3-bbG6VlFSDzcPzq_nUOUdMbQPP_VNFnkJGr4gWBC17iYe8xhmbAlr7sa"

    })
    @POST("fcm/send")
    Observable<FCMResponse> sendNotification(@Body FCMSendData body);
}
