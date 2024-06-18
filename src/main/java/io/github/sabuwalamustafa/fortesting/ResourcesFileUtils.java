package io.github.sabuwalamustafa.fortesting;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.sabuwalamustafa.interfaces.IFileUtils;
import io.github.sabuwalamustafa.utils_utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

class ResourcesFileUtils implements IFileUtils {
    private static ResourcesFileUtils INSTANCE;

    private ResourcesFileUtils() {
    }

    public static ResourcesFileUtils getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ResourcesFileUtils();
        }
        return INSTANCE;
    }

    @Override public String readIt(String resourceFilePathStr)
            throws IOException {
        StringBuilder contentBuilder = new StringBuilder();
        InputStream inputStream = getInputStream(
                ResourcesFileUtils.class, resourceFilePathStr);
        if (inputStream != null) {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    contentBuilder.append(line);
                    contentBuilder.append("\n");
                }
            } // todo: maybe better exception handling
        }
        return contentBuilder.toString();
    }

    @Override public JsonNode readJson(String resourceFilePathStr)
            throws IOException {
        String jsonString = readIt(resourceFilePathStr);
        return utils_utils.parseJsonStr(jsonString);
    }

    @Override public void write(String filePathStr, String content)
            throws IOException {
        return;
    }

    @Override public void write(String filePathStr, String content,
            boolean createIfAbsent) throws IOException {
        return;
    }

    @Override public void append(String filePathStr, String content)
            throws IOException {
        return;
    }

    @Override public void append(String filePathStr, String content,
            boolean createIfAbsent) throws IOException {
        return;
    }

    @Override public void createDirRecursively(String directoryPathStr)
            throws IOException {
        return;
    }

    @Override public void createFileIfMissing(String filePathStr)
            throws IOException {
        return;
    }

    @Override public void removeIfPresent(String filePathStr)
            throws IOException {
        return;
    }

    @Override public boolean fileExists(String filePathStr) throws IOException {
        return false; //todo
    }

    @Override public List<String> getAllFiles(String dir) {
        return null; //todo
    }

    public InputStream getInputStream(Class myClass,
            String resourceFilePathStr) {
        return ResourcesFileUtils.class.getClassLoader().getResourceAsStream(
                resourceFilePathStr);
    }
}
