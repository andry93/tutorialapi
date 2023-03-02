package com.tutorialapi.server;


import org.eclipse.jetty.server.HttpConfiguration;

import static org.eclipse.jetty.http.HttpScheme.HTTPS;

public class TutorialApiServer {
    public static void main(String[] args) {
        System.out.println("Helllloo.......");

        HttpConfiguration httpsConfiguration = new HttpConfiguration();
        httpsConfiguration.setSecureScheme(HTTPS.asString());
        //httpsConfiguration.setSecurePort(Integer.valueOf(HTTPS.asString()));

    }
}
