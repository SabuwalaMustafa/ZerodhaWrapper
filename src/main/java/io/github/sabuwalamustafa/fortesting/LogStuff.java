package io.github.sabuwalamustafa.fortesting;

import com.sabu.at.DateTimeUtils;
import io.github.sabuwalamustafa.interfaces.IFileUtils;
import io.github.sabuwalamustafa.interfaces.ILogStuff;
import io.github.sabuwalamustafa.utils_utils;

import java.io.IOException;

class LogStuff implements ILogStuff {
    private static LogStuff INSTANCE;
    String logsBaseFolderPath;
    private IFileUtils fileUtils;
    private String currentLogFilePathStr;
    private boolean disableLogging;

    private LogStuff(IFileUtils fileUtils, String logsBaseFolder) {
        this.fileUtils = fileUtils;
        this.logsBaseFolderPath = logsBaseFolder;
        this.disableLogging = logsBaseFolder == null;
    }

    public static LogStuff getInstance(IFileUtils fileUtils,
            String logsBaseFolder) {
        if (INSTANCE == null) {
            INSTANCE = new LogStuff(fileUtils, logsBaseFolder);
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
        if (disableLogging) {
            return;
        }
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
        if(disableLogging) return;
        // TODO: Run it in a separate thread
        updateFileConfigs();
        try {
            fileUtils.append(currentLogFilePathStr, content, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateFileConfigs() {
        currentLogFilePathStr = getCurrentLogFilePath();
    }

    private String getCurrentLogFilePath() {
        // todo: use String.format()
        String fileName = "logs_" + DateTimeUtils.readableTimestampTillHours()
                          + ".txt";
        return utils_utils.buildPath(false, logsBaseFolderPath, fileName);
    }
}
