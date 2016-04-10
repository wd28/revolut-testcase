package revolut.wd28.resources;

import revolut.wd28.datastore.Datastore;
import revolut.wd28.datastore.model.Currency;
import revolut.wd28.datastore.model.Transaction;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.UUID;

@Path("/transfer/from/{from}/to/{to}")
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class Transfer {
    @POST
    public Transaction transfer(@PathParam("from") UUID from,
                                @PathParam("to") UUID to,
                                @QueryParam("sourceCurrency") Currency sourceCurrency,
                                @QueryParam("targetCurrency") Currency targetCurrency,
                                @QueryParam("targetAmount") double targetAmount,
                                @QueryParam("rate") Double rate,
                                @QueryParam("comment") String comment) {
        if (rate == null) {
            return Datastore.transferUsingRate(from, to, sourceCurrency, targetCurrency, targetAmount, comment);
        } else {
            return Datastore.transferUsingRate(from, to, sourceCurrency, targetCurrency, targetAmount, rate, comment);
        }
    }
}
