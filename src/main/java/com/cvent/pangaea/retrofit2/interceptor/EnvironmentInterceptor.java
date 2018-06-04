package com.cvent.pangaea.retrofit2.interceptor;

import com.cvent.pangaea.MultiEnvAware;
import com.cvent.pangaea.filter.EnvironmentIdentifierFilter;
import com.cvent.pangaea.util.EnvironmentUtil;
import java.io.IOException;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Interceptor.Chain;
import okhttp3.Request;
import okhttp3.Response;

/**
 * This is request interceptor for Retrofit2Clients so that environment is appended as query param in all the requests.
 * It takes environment either from ThreadLocal set in {@link EnvironmentIdentifierFilter} or passed in the constructor.
 */
public class EnvironmentInterceptor implements Interceptor {

    private String environment;

    public EnvironmentInterceptor() {
    }

    public EnvironmentInterceptor(String environment) {
        this.environment = environment;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        if (environment != null && !environment.isEmpty()) {
            HttpUrl url = request.url().newBuilder().addQueryParameter(MultiEnvAware.ENVIRONMENT, environment).build();
            request = request.newBuilder().url(url).build();
        } else if (EnvironmentUtil.getEnvironment() != null
                           && !EnvironmentUtil.getEnvironment().isEmpty()) {
            HttpUrl url = request.url().newBuilder()
                    .addQueryParameter(MultiEnvAware.ENVIRONMENT, EnvironmentUtil.getEnvironment()).build();
            request = request.newBuilder().url(url).build();

        }

        return chain.proceed(request);
    }

}
