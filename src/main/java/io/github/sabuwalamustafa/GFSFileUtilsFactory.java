package io.github.sabuwalamustafa;

import com.google.auth.Credentials;

import java.io.IOException;

public class GFSFileUtilsFactory {
    // todo: return optional<>
    public static GFSFileUtils getGFSFileUtils(Credentials credentials) {
        String bucketName = "sabuwalabucket0";
        GFSFileUtils gfsFileUtils = null;
        try {
            gfsFileUtils = GFSFileUtils.getInstance(bucketName, credentials);
        } catch (IOException e) {
            // todo: log
        }
        return gfsFileUtils;
    }
}
