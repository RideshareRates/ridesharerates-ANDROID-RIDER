package com.rideshare.app.acitivities;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GoogleDirectionsClient {
    private static Retrofit retrofit;

    public static Retrofit getClient() {
        if (retrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .addInterceptor(logging)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl("https://maps.googleapis.com/")  // fixed base URL for Google APIs
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }

//        public static Retrofit getClient() {
//            if (retrofit == null) {
//                // Logging interceptor (for debug)
//                HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
//                logging.setLevel(HttpLoggingInterceptor.Level.BODY);
//
//                // Custom header interceptor
//                OkHttpClient client = new OkHttpClient.Builder()
//                        .connectTimeout(60, TimeUnit.SECONDS)
//                        .writeTimeout(60, TimeUnit.SECONDS)
//                        .readTimeout(60, TimeUnit.SECONDS)
//                        .addInterceptor(chain -> {
//                            okhttp3.Request original = chain.request();
//                            okhttp3.Request request = original.newBuilder()
//                                    .header("X-Ios-Bundle-Identifier", "com.rideshare.app") // Custom header
//                                    .method(original.method(), original.body())
//                                    .build();
//                            return chain.proceed(request);
//                        })
//                        .addInterceptor(logging)
//                        .build();
//
//                retrofit = new Retrofit.Builder()
//                        .baseUrl("https://maps.googleapis.com/")  // Base URL
//                        .addConverterFactory(GsonConverterFactory.create())
//                        .client(client)
//                        .build();
//            }
//            return retrofit;
//        }

    }
