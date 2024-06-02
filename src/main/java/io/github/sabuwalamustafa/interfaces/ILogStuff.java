package io.github.sabuwalamustafa.interfaces;

public interface ILogStuff {
    public void logIt(String... strsToLog);

    public void datedLogIt(String... strsToLog);

    public void datedLogIt(Integer noOfBlankLines, String... strsToLog);
}
