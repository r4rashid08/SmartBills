package com.affable.smartbills.Network;

import android.util.Log;

import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by mRashid on 21/09/2020.
 */
public abstract class ServerCallback<T> implements Callback<T> {

    public abstract void onFailure(ServerError restError);

    public abstract void onSuccess(Response<T> response);

    public abstract void onResponse(Response<T> response);

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        String gson = new Gson().toJson(response.body());
        Log.e("testing", "response 33: "+new Gson().toJson(response.body()) );
        onResponse(response);
        if (response.code() == 200){
            onSuccess(response);
        } else {
            ServerError serverError = new ServerError();
//            serverError.setMessage(baseModel.getErrors());
//            serverError.setStatus(baseModel.getStatus());
            serverError.setMessage("Something went wrong"); //TODO appropriate message needs to be set.
            onFailure(serverError);
        }
//        if (response.isSuccessful()) {
//            if(response.body() instanceof BaseModel) {
//                BaseModel baseModel = (BaseModel) response.body();
//                if (baseModel.isSuccessFull()) {
//                    onSuccess(response);
//                } else {
//                    ServerError serverError = new ServerError();
//                    serverError.setMessage(baseModel.getErrors());
//                    serverError.setStatus(baseModel.getStatus());
//                    onFailure(serverError);
//                }
//            }
//        } else {
//            ServerError serverError = parseError(response);
//            if (serverError == null) {
//                serverError = new ServerError();
//                serverError.setMessage("Something went wrong");
//                serverError.setStatus(500);
//            } else if (serverError.getStatus() == 500) {
//                if (!TextUtils.isEmpty(serverError.getServerResponse())) {
//                    serverError.setMessage(serverError.getServerResponse());
//                } else {
//                    serverError.setMessage("Something went wrong");
//                }
//            }
//            onFailure(serverError);
//        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        ServerError error = new ServerError();
        error.setMessage(t.getMessage());
        error.setStatus(500);
        onFailure(error);
    }

    private ServerError parseError(Response<?> response) {
//        ServerError error = new ServerError();
//        ExceptionParser exceptionParser = new ExceptionParser(response);
//        String bodyString = exceptionParser.getBodyAsString();
//
//        if (!TextUtils.isEmpty(bodyString)) {
//            Gson gson = new Gson();
//            try {
//                error = gson.fromJson(bodyString, ServerError.class);
//            } catch (Exception e) {
//                error.setMessage(e.getMessage());
//                error.setStatus(500);
//            } finally {
//                error.setServerResponse(bodyString);
//            }
//        }

//        return error;
        return null;
    }
}

