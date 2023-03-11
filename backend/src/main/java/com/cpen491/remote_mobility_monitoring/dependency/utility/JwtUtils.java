package com.cpen491.remote_mobility_monitoring.dependency.utility;

import com.cpen491.remote_mobility_monitoring.dependency.exception.InvalidAuthorizationException;
import com.cpen491.remote_mobility_monitoring.function.schema.Const;
import com.cpen491.remote_mobility_monitoring.function.schema.auth.JwtPayload;
import com.google.gson.Gson;

import java.util.Base64;
import java.util.Map;

public class JwtUtils {
    private static final Base64.Decoder decoder = Base64.getUrlDecoder();

    public static String getAuthorizationHeader(Map<String, String> headers) {
        String authorization = headers.get(Const.AUTHORIZATION_NAME1);
        if (authorization == null) {
            authorization = headers.get(Const.AUTHORIZATION_NAME2);
            if (authorization == null) {
                throw new InvalidAuthorizationException();
            }
        }
        return authorization;
    }

    public static String decodeAndGetId(String jwt, String idPrefix, Gson gson) {
        String payload = decodePayload(jwt);
        JwtPayload jwtPayload = gson.fromJson(payload, JwtPayload.class);
        return idPrefix + jwtPayload.getSub();
    }

    public static String decodePayload(String jwt) {
        String[] parts = jwt.split("\\.");
        if (parts.length < 3) {
            throw new InvalidAuthorizationException();
        }
        return new String(decoder.decode(parts[1]));
    }
}
