package io.github.sabuwalamustafa;

import com.google.auth.Credentials;
import io.github.sabuwalamustafa.filesystemhandlers.GFSFileSystemHandler;

import java.io.IOException;

public class GFSFileSystemHandlerFactory {
    // todo: return optional<>
    public static GFSFileSystemHandler getGFSFileSystemHandler(
            Credentials credentials) {
        String bucketName = "sabuwalabucket0";
        GFSFileSystemHandler gfsFileSystemHandler = null;
        try {
            gfsFileSystemHandler = GFSFileSystemHandler.getInstance(bucketName,
                                                                    credentials);
        } catch (IOException e) {
            // todo: log
        }
        return gfsFileSystemHandler;
    }
}
