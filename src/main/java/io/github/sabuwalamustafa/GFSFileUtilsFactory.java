package io.github.sabuwalamustafa;

import java.io.IOException;

public class GFSFileUtilsFactory {
    // todo: return optional<>
    public static GFSFileUtils getGFSFileUtils(String googleKeyFilePath) {
        String bucketName = "sabuwalabucket0";
        GFSFileUtils gfsFileUtils = null;
        try {
            gfsFileUtils = GFSFileUtils.getInstance(bucketName,
                                                    googleKeyFilePath);
        } catch (IOException e) {
            // todo: log
        }
        return gfsFileUtils;
    }
}
