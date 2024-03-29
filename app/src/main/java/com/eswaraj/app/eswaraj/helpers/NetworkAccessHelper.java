package com.eswaraj.app.eswaraj.helpers;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.eswaraj.app.eswaraj.base.BaseClass;

import javax.inject.Inject;

public class NetworkAccessHelper extends BaseClass {
    @Inject
    Context applicationContext;
    @Inject
    RequestQueue requestQueue;

    public void submitNetworkRequest(String requestTag, Request request) {
        requestQueue.cancelAll(requestTag);
        requestQueue.add(request);
        request.setTag(requestTag);
    }
}
