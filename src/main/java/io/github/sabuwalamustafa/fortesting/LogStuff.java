package io.github.sabuwalamustafa.fortesting;

import com.sabu.at.DateTimeUtils;
import io.github.sabuwalamustafa.interfaces.IFilePathsProvider;
import io.github.sabuwalamustafa.interfaces.IFileUtils;
import io.github.sabuwalamustafa.interfaces.ILogStuff;

import java.io.IOException;

public class LogStuff implements ILogStuff {
    private static LogStuff INSTANCE;
    private IFileUtils fileUtils;
    private IFilePathsProvider filePathsProvider;
    private String currentLogFilePathStr;

    private LogStuff(IFileUtils fileUtils, IFilePathsProvider filePathsProvider) {
        this.fileUtils = fileUtils;
        this.filePathsProvider = filePathsProvider;
    }

    public static LogStuff getInstance(IFileUtils fileUtils, IFilePathsProvider filePathsProvider) {
        if (INSTANCE == null) {
            INSTANCE = new LogStuff(fileUtils, filePathsProvider);
        }
        return INSTANCE;
    }


    public void logIt(String... strsToLog) {
        log(strsToLog, 0);
    }

    public void datedLogIt(String... strsToLog) {
        log(strsToLog, 0, true);
    }

    public void datedLogIt(Integer noOfBlankLines, String... strsToLog) {
        log(strsToLog, noOfBlankLines, true);
    }

    private void log(String[] strsToLog, int noOfBlankLines) {
        log(strsToLog, noOfBlankLines, false);
    }

    private void log(String[] strsToLog, int noOfBlankLines, boolean addDate) {
        StringBuilder content = new StringBuilder();
        if (addDate) {
            content.append(DateTimeUtils.readableTimestamp()).append(" ");
        }
        for (String str : strsToLog) {
            content.append(str).append(" ");
        }
        basicLogIt(content.toString());
        logBlankLines(noOfBlankLines);
    }

    private void logBlankLines(int noOfBlankLines) {
        for (int i = 0; i < noOfBlankLines; i++) {
            basicLogIt("\n");
        }
    }

    private void basicLogIt(String content) {
        // TODO: Run it in a separate thread
        updateFileConfigs();
        try {
            fileUtils.append(currentLogFilePathStr, content, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateFileConfigs() {
        currentLogFilePathStr = filePathsProvider.getCurrentLogFilePath();
    }
}
