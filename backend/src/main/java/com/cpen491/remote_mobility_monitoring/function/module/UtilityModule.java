package com.cpen491.remote_mobility_monitoring.function.module;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class UtilityModule {
    @Provides
    @Singleton
    public static Gson gson() {
        return new GsonBuilder().serializeNulls().create();
    }
}
