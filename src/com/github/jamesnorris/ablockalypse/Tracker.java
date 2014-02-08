package com.github.jamesnorris.ablockalypse;

public class Tracker {
    private String application, version, reportLink;
    private int totalFatality, maxFatality = 100;

    public Tracker(String application, String version, String reportLink, int maxFatality) {
        this.application = application;
        this.version = version;
        this.reportLink = reportLink;
        this.maxFatality = maxFatality;
    }

    public void error(String reason, int fatality) {
        error(reason, fatality, null);
    }

    public void error(String reason, int fatality, Exception ex) {
        totalFatality += fatality;
        System.err.println("An aspect of " + application + " is broken, please report it at: \n" + reportLink + " \n" + "--------------------------[ERROR REPORT]--------------------------\n" + "VERSION: " + version + " \n" + "BREAK REASON: " + reason + " \n" + "------------------------------------------------------------------\n" + "ERROR STACKTRACE: \n");
        ex.printStackTrace();
        System.out.println("------------------------------------------------------------------\n" + "FATALITY: " + fatality + " \n" + "TOTAL FATALITY: " + totalFatality + " / " + maxFatality + " \n" + "---------------------------[END REPORT]---------------------------\n");
        if (totalFatality >= maxFatality) {
            System.err.print(application + " has reached the maximum fatality level allowed. It will now kill itself to prevent more serious or recurring errors.\n");
        }
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

    protected String stackTraceToString(StackTraceElement[] elements) {
        String total = "";
        for (StackTraceElement element : elements) {
            total += element.toString() + "\n";
        }
        return total;
    }
}
