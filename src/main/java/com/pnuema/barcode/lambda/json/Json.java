package com.pnuema.barcode.lambda.json;

import com.squareup.moshi.Moshi;

import java.io.IOException;

public final class Json {
    private static Moshi moshi;

    private static Moshi getMoshi() {
        if (moshi == null) {
            moshi = new Moshi.Builder().build();
        }

        return moshi;
    }

    public static <T> String toJson(Class<T> clazz, T object) {
        return getMoshi().adapter(clazz).toJson(object);
    }

    public static <T> T fromJson(Class<T> clazz, String object) throws IOException {
        return getMoshi().adapter(clazz).fromJson(object);
    }

    public static String getFormattedError(Exception ex) {
        return "{\"errorMessage\":\"" + ex.getMessage() + "\"}";
    }
}
