package com.tutorialapi.server;


import com.tutorialapi.rest.ApiApplication;
import com.tutorialapi.server.config.ConfigKey;
import com.tutorialapi.server.config.SystemKey;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.servlet.ServletProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;

import static org.eclipse.jetty.http.HttpScheme.HTTPS;
import static org.eclipse.jetty.http.HttpVersion.HTTP_1_1;
import static org.eclipse.jetty.servlet.ServletContextHandler.NO_SESSIONS;

public class TutorialApiServer {
    public static final Logger LOGGER = LoggerFactory.getLogger(TutorialApiServer.class);
    public static final String ROOT_CONTEXT = "/";
    public static final String API_PATTERN = "/api/*";

    private static Server createJettyServer(int port,Config config) throws IOException
    {
        HttpConfiguration httpsConfiguration = new HttpConfiguration();
        httpsConfiguration.setSecureScheme(HTTPS.asString());
        httpsConfiguration.setSecurePort(port);
        httpsConfiguration.addCustomizer(new SecureRequestCustomizer());
        httpsConfiguration.setSendServerVersion(false); //disable the server info in header
        httpsConfiguration.setSendXPoweredBy(false);//disable the PoweredBy info in header

        HttpConnectionFactory httpsConnectionFactory = new HttpConnectionFactory(httpsConfiguration);

        SslContextFactory.Server sslContextFactory = new SslContextFactory.Server();
        sslContextFactory.setKeyStorePath(config.getString(ConfigKey.SERVER_KEYSTORE_FILE.getKey()));
        sslContextFactory.setKeyStoreType(config.getString(ConfigKey.SERVER_KEYSTORE_TYPE.getKey()));
        sslContextFactory.setKeyStorePassword(config.getString(ConfigKey.SERVER_KEYSTORE_PASSWORD.getKey()));
        sslContextFactory.setKeyManagerPassword(config.getString(ConfigKey.SERVER_KEYSTORE_PASSWORD.getKey()));
        sslContextFactory.setTrustAll(true);

        SslConnectionFactory sslConnectionFactory = new SslConnectionFactory(sslContextFactory,HTTP_1_1.asString());

        Server server = new Server();

        ServerConnector httpsConnector = new ServerConnector(server,sslConnectionFactory,httpsConnectionFactory);
        //httpsConnector.setName("secure-HTTP");
        httpsConnector.setPort(httpsConfiguration.getSecurePort());

        server.addConnector(httpsConnector);

        ServletContextHandler servletContextHandler = new ServletContextHandler(NO_SESSIONS);
        servletContextHandler.setContextPath(ROOT_CONTEXT);
        servletContextHandler.setBaseResource(Resource.newResource(config.getString(ConfigKey.SERVER_WEB_CONTENT.getKey())));
        servletContextHandler.addServlet(DefaultServlet.class,ROOT_CONTEXT);

        server.setHandler(servletContextHandler);

        ServletHolder apiServletHolder = servletContextHandler.addServlet(ServletContainer.class,API_PATTERN);
        apiServletHolder.setInitParameter(ServletProperties.JAXRS_APPLICATION_CLASS, ApiApplication.class.getName());

        return server;
    }
    public static void main(String[] args) throws Exception{
        int port = Integer.parseInt(Optional.ofNullable(System.getProperty(SystemKey.PORT.getKey())).orElse(SystemKey.PORT.getDefaultValue()));
        String mode = Optional.ofNullable(System.getProperty(SystemKey.MODE.getKey())).orElse(SystemKey.MODE.getDefaultValue());

        String url = String.format("https://raw.githubusercontent.com/andry93/tutorialapi/main/system-%s.properties",mode);
        Config config = ConfigFactory.parseURL(new URL(url));


        Server server = createJettyServer(port,config);


        LOGGER.info("server starting on port: {}",port);
        server.start();
        server.join();
    }
}
