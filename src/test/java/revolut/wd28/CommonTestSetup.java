package revolut.wd28;

import com.sun.net.httpserver.HttpServer;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.moxy.json.MoxyJsonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.After;
import org.junit.Before;
import revolut.wd28.mappers.IllegalArgumentExceptionMapper;
import revolut.wd28.mappers.NullPointerExceptionMapper;
import revolut.wd28.resources.Account;
import revolut.wd28.resources.Rate;
import revolut.wd28.resources.Transfer;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.concurrent.Executors;

public abstract class CommonTestSetup {
    HttpServer server;

    protected WebTarget target = ClientBuilder.newBuilder().register(MoxyJsonFeature.class).build().target("http://localhost:9998");

    @Before
    public void setUp() {
        URI baseUri = UriBuilder.fromUri("http://localhost/").port(9998).build();

        ResourceConfig config = new ResourceConfig(MoxyJsonFeature.class, Rate.class,
                Account.class, Transfer.class, NullPointerExceptionMapper.class, IllegalArgumentExceptionMapper.class);
        server = JdkHttpServerFactory.createHttpServer(baseUri, config, false);
        server.setExecutor(Executors.newFixedThreadPool(5));
        server.start();
    }


    @After
    public void tearDown() {
        server.stop(0);
    }
}
