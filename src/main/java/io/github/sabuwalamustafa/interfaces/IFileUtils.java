package io.github.sabuwalamustafa.interfaces;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.List;

public interface IFileUtils {
    public String readIt(String filePathStr) throws IOException;

    public JsonNode readJson(String filePathStr) throws IOException;

    public void write(String filePathStr, String content) throws IOException;

    public void write(String filePathStr, String content,
            boolean createIfAbsent) throws IOException;

    public void append(String filePathStr, String content) throws IOException;

    public void append(String filePathStr, String content,
            boolean createIfAbsent) throws IOException;

    public void createDirRecursively(String directoryPathStr) throws IOException;

    public void createFileIfMissing(String filePathStr) throws IOException;

    public void removeIfPresent(String filePathStr) throws IOException;

    public boolean fileExists(String filePathStr) throws IOException;

    List<String> getAllFiles(String dir);
}
