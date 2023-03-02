package com.tutorialapi.server;


import org.eclipse.jetty.server.*;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.eclipse.jetty.http.HttpScheme.HTTPS;
import static org.eclipse.jetty.http.HttpVersion.HTTP_1_1;

public class TutorialApiServer {
    public static final Logger LOGGER = LoggerFactory.getLogger(TutorialApiServer.class);
    public static void main(String[] args) throws Exception{
        LOGGER.info("start the app");

        HttpConfiguration httpsConfiguration = new HttpConfiguration();
        httpsConfiguration.setSecureScheme(HTTPS.asString());
        httpsConfiguration.setSecurePort(8443);
        httpsConfiguration.addCustomizer(new SecureRequestCustomizer());
        httpsConfiguration.setSendServerVersion(false); //disable the server info in header
        httpsConfiguration.setSendXPoweredBy(false);//disable the PoweredBy info in header

        HttpConnectionFactory httpsConnectionFactory = new HttpConnectionFactory(httpsConfiguration);

        SslContextFactory.Server sslContextFactory = new SslContextFactory.Server();
        sslContextFactory.setKeyStorePath("tutorialapi-server/src/main/resources/certs/tutorialapi.p12");
        sslContextFactory.setKeyStoreType("PKCS12");
        sslContextFactory.setKeyStorePassword("changeit");
        sslContextFactory.setKeyManagerPassword("changeit");
        sslContextFactory.setTrustAll(true);

        SslConnectionFactory sslConnectionFactory = new SslConnectionFactory(sslContextFactory,HTTP_1_1.asString());

        Server server = new Server();

        ServerConnector httpsConnector = new ServerConnector(server,sslConnectionFactory,httpsConnectionFactory);
        httpsConnector.setName("secure-HTTP");
        httpsConnector.setPort(httpsConfiguration.getSecurePort());

        server.addConnector(httpsConnector);
        server.start();
        server.join();
    }
}
