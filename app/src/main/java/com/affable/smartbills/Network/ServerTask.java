package com.affable.smartbills.Network;

import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by mRashid on 21/09/2020.
 */
public class ServerTask {

    private static ServerTask serverTask = null;

    private ServerAPI serverAPI;
    private Retrofit retrofit;
    private OkHttpClient.Builder httpClient;

    private String credentials;

    public static ServerTask getInstance() {
        if (serverTask == null) {
            serverTask = new ServerTask();

        }

        return serverTask;
    }

    private ServerTask() {

        LoggingInterceptor loggingInterceptor = new LoggingInterceptor();

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);


        httpClient = new OkHttpClient.Builder();
      //  httpClient.addInterceptor(loggingInterceptor);
        httpClient.addInterceptor(new BasicAuthInterceptor("microuser","Micro@usr22"));
        httpClient.readTimeout(60, TimeUnit.SECONDS);
        httpClient.connectTimeout(60, TimeUnit.SECONDS);


        // add logging as last interceptor
        httpClient.addInterceptor(logging);  // <-- this is the important line!

        retrofit = new Retrofit.Builder()
                .baseUrl(UrlUtils.SERVER_DOMAIN)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

    }

    public <S> S createService(Class<S> serviceClass) {

        return retrofit.create(serviceClass);
    }


    public class BasicAuthInterceptor implements Interceptor {

        private String credentials;

        public BasicAuthInterceptor(String user, String password) {
            this.credentials = Credentials.basic(user, password);
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Request authenticatedRequest = request.newBuilder()
                    .header("Authorization", credentials).build();
            return chain.proceed(authenticatedRequest);
        }

    }

    public ServerAPI getServices() {
        if (serverAPI == null) {
            serverAPI = createService(ServerAPI.class);
        }

        return serverAPI;
    }

    private static class LoggingInterceptor implements Interceptor {
        @NotNull
        @Override
        public Response intercept(Chain chain) throws IOException {



            Request request = chain.request();

           String  credentials = Credentials.basic("microuser", "Micro@usr22");



                String requestLog = String.format("Sending request %s on %s%n%s",
                        request.url(), chain.connection(), request.headers());

                if (request.method().compareToIgnoreCase("post") == 0) {
                    requestLog = "\n" + requestLog + "\n" + bodyToString(request);
                }

                if (request.method().compareToIgnoreCase("put") == 0) {
                    requestLog = "\n" + requestLog + "\n" + bodyToString(request);
                }

                Log.d("TAG", "request" + "\n" + requestLog);

                Response response = chain.proceed(request);

                String bodyString = Objects.requireNonNull(response.body()).string();

                Log.d("TAG", "\nResponse Body : " + bodyString);

                return response.newBuilder()
                        .header("Authorization",credentials)
                        .body(ResponseBody.create(Objects.requireNonNull(response.body()).contentType(), bodyString))
                        .build();
            }

    }

    private static String bodyToString(final Request request) {
        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            Objects.requireNonNull(copy.body()).writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }
}
