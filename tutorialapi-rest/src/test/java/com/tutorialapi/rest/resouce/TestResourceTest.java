package com.tutorialapi.rest.resouce;

import com.tutorialapi.rest.ApiApplication;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.logging.LogManager;

public class TestResourceTest extends JerseyTest {
    //public static final Logger LOGGER = LoggerFactory.getLogger(TestResourceTest.class);
    static {
        LogManager.getLogManager().reset();
    }
    @Override
    protected Application configure() {
        return new ApiApplication();
    }

    @Test
    public void test() {
        Response response = target("/test/subpath").request().get();
        Assertions.assertEquals(200,response.getStatus());
        Assertions.assertEquals("Hello",response.readEntity(String.class));

        Assertions.assertEquals("*",response.getHeaderString("Access-Control-Allow-Origin"));
        Assertions.assertEquals("GET, PUT, POST, DELETE, HEAD, OPTIONS, PATCH",response.getHeaderString("Access-Control-Allow-Methods"));

        //LOGGER.info("Response Header : {}",response.getHeaders());
    }
}
