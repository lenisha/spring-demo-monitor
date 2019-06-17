package com.microsoft.demoai;


public class HelloUtils {

    public static void slow()  {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            // e.printStackTrace();
        }
    }

    public static void trackexception()  {
        try {
            throw new NullPointerException();
        } catch (Exception ex) {
            //telemetryClient.trackException(ex);
            ex.printStackTrace();
        }
    }

}