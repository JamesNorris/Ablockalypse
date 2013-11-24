package com.github.jamesnorris.ablockalypse;

import java.util.regex.Pattern;

public class ErrorTracker {
    private String application, version, reportLink;
    private int totalFatality, maxFatality = 100;

    public ErrorTracker(String application, String version, String reportLink, int maxFatality) {
        this.application = application;
        this.version = version;
        this.reportLink = reportLink;
        this.maxFatality = maxFatality;
    }

    public void crash(String reason, int fatality) {
        crash(reason, fatality, null);
    }

    public void crash(String reason, int fatality, Exception ex) {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        totalFatality += fatality;
        String raw = getRawErrorReport();
        raw.replaceAll(Pattern.quote("@application"), application);
        raw.replaceAll(Pattern.quote("@reportlink"), reportLink);
        raw.replaceAll(Pattern.quote("@version"), version);
        raw.replaceAll(Pattern.quote("@breakreason"), reason);
        raw.replaceAll(Pattern.quote("@crashtrace"), stackTraceElements[0] + "\n" + stackTraceElements[1] + "\n" + stackTraceElements[2]);
        if (ex != null) {
            raw.replaceAll(Pattern.quote("@errorreport"), ex.getMessage());
        }
        raw.replaceAll(Pattern.quote("@fatalitylevel"), fatality + "");
        raw.replaceAll(Pattern.quote("@totalfatality"), totalFatality + "");
        raw.replaceAll(Pattern.quote("@maxfatality"), maxFatality + "");
        if (totalFatality >= maxFatality) {
            raw += application + " has reached the maximum fatality level allowed. It will now kill itself to prevent more serious or recurring errors.\n";
        }
        System.out.println(raw);
    }

    public int getMaxFatality() {
        return maxFatality;
    }

    public String getReportLink() {
        return reportLink;
    }

    public int getTotalFatality() {
        return totalFatality;
    }

    public void setMaxFatality(int maxFatality) {
        this.maxFatality = maxFatality;
    }

    private String getRawErrorReport() {
        return "An aspect of @application is broken, please report it at: \n" + "@reportlink \n" + "--------------------------[ERROR REPORT]--------------------------\n"
                + "VERSION: @version \n" + "BREAK REASON: @breakreason \n" + "CRASH TRACE: @crashtrace \n" + "------------------------------------------------------------------\n"
                + "ERROR REPORT: @errorreport \n" + "------------------------------------------------------------------\n" + "FATALITY: @fatalitylevel \n"
                + "TOTAL FATALITY: @totalfatality / @maxfatality \n" + "\n---------------------------[END REPORT]---------------------------\n";
    }
}
