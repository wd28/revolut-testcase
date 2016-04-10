package revolut.wd28;


import com.sun.net.httpserver.HttpServer;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.moxy.json.MoxyJsonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import revolut.wd28.datastore.Datastore;
import revolut.wd28.datastore.model.Currency;
import revolut.wd28.mappers.IllegalArgumentExceptionMapper;
import revolut.wd28.mappers.NullPointerExceptionMapper;
import revolut.wd28.resources.Account;
import revolut.wd28.resources.Rate;
import revolut.wd28.resources.Transfer;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.UUID;

public class AsStandAloneApplication {
    public static void main(String[] args) {
        URI baseUri = UriBuilder.fromUri("http://localhost/").port(9998).build();

        ResourceConfig config = new ResourceConfig(MoxyJsonFeature.class, Rate.class, Account.class, Transfer.class, NullPointerExceptionMapper.class, IllegalArgumentExceptionMapper.class);
        UUID testId = UUID.fromString(Datastore.createAccount("John Smith").getUuid());
        Datastore.add(testId, Currency.EUR, 100);
        Datastore.setRate(Currency.EUR, 0.88);
        Datastore.setRate(Currency.GBP, 0.71);
        Datastore.setRate(Currency.AUD, 1.32);

        HttpServer server = JdkHttpServerFactory.createHttpServer(baseUri, config);
    }
}
