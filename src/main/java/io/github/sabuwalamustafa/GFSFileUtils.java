package io.github.sabuwalamustafa;


import com.fasterxml.jackson.databind.JsonNode;
import com.google.auth.Credentials;
import com.google.cloud.storage.*;
import io.github.sabuwalamustafa.interfaces.IFileUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/*
 * Note: Instance of this class will work for files in the given bucketName
 * only.
 * */
public class GFSFileUtils implements IFileUtils {
    private static Map<String, GFSFileUtils> INSTANCE_MAP = new HashMap<>();
    private final String bucketName;
    private final Storage storage;

    private GFSFileUtils(String bucketName, Credentials credentials)
            throws IOException {
        this.bucketName = bucketName;
        //todo: Needs google cloud key file on machine running this code.
        this.storage = StorageOptions.newBuilder()
                                     .setCredentials(credentials)
                                     .build()
                                     .getService();
        // todo: .setCredentials(ServiceAccountCredentials.fromJson
        //  (GenericJson fileContents, OAuth2Utils.HTTP_TRANSPORT_FACTORY))
    }

    public static GFSFileUtils getInstance(String bucketName,
            Credentials credentials) throws IOException {
        if (!INSTANCE_MAP.containsKey(bucketName)) {
            INSTANCE_MAP.put(bucketName,
                             new GFSFileUtils(bucketName, credentials));
        }
        return INSTANCE_MAP.get(bucketName);
    }

    @Override public String readIt(String filePathStr) throws IOException {
        Blob blob = storage.get(BlobId.of(bucketName, filePathStr));
        return new String(blob.getContent(), StandardCharsets.UTF_8);
    }

    @Override public JsonNode readJson(String filePathStr) throws IOException {
        String jsonString = readIt(filePathStr);
        return utils_utils.parseJsonStr(jsonString);
    }

    @Override public void write(String filePathStr, String content)
            throws IOException {
        BlobId blobId = BlobId.of(bucketName, filePathStr);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        storage.create(blobInfo, content.getBytes(StandardCharsets.UTF_8));
    }

    @Override public void write(String filePathStr, String content,
            boolean shouldCreateIfMissing) throws IOException {
        if (shouldCreateIfMissing) {
            createFileIfMissing(filePathStr);
        }
        write(filePathStr, content);
    }

    @Override public void append(String filePathStr, String content)
            throws IOException {
        Blob blob = storage.get(BlobId.of(bucketName, filePathStr));
        String existingContent = new String(blob.getContent(),
                                            StandardCharsets.UTF_8);
        String updatedContent = existingContent + content;
        write(filePathStr, updatedContent); // Reuses the writeContent method
    }

    @Override public void append(String filePathStr, String content,
            boolean shouldCreateIfMissing) throws IOException {
        if (shouldCreateIfMissing) {
            createFileIfMissing(filePathStr);
        }
        append(filePathStr, content);
    }

    public void createDirRecursively(String directoryPathStr)
            throws IOException {
        createFileIfMissing(directoryPathStr);
    }

    @Override public void createFileIfMissing(String filePathStr)
            throws IOException {
        if (fileExists(filePathStr)) {
            return;
        }
        BlobId blobId = BlobId.of(bucketName, filePathStr);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        storage.create(blobInfo, "".getBytes(StandardCharsets.UTF_8));
    }

    @Override public void removeIfPresent(String filePathStr)
            throws IOException {
        BlobId blobId = BlobId.of(bucketName, filePathStr);
        boolean deleted = storage.delete(blobId);
        // todo: log `deleted` value and proper msg
    }

    @Override public boolean fileExists(String filePathStr) throws IOException {
        BlobId blobId = BlobId.of(bucketName, filePathStr);
        Blob blob = storage.get(blobId);
        return blob != null && blob.exists();
    }

    // `dir` is not relevant in case of GFS.
    @Override public List<String> getAllFiles(String dir) {
        // todo: Add check that dir is null or empty string
        List<String> allFiles = new ArrayList<>();
        Bucket bucket = storage.get(bucketName);
        if (bucket == null) {
            //todo: log
            return allFiles;
        }
        for (Blob blob : bucket.list().iterateAll()) {
            allFiles.add(blob.getName());
        }
        return allFiles;
    }

    // todo: take bucket name as input
    public List<String> getAllFilesInBucket() {
        return getAllFiles("");
    }

    public List<String> getAllFilesInBucket(String prefix) {
        return getAllFilesInBucket().stream().filter(fileName -> {
            return fileName.startsWith(prefix);
        }).collect(Collectors.toList());
    }
}
