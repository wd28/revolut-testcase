package revolut.wd28.resources;

import revolut.wd28.datastore.Datastore;
import revolut.wd28.datastore.beans.RatesBean;
import revolut.wd28.datastore.model.Currency;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/rate")
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class Rate {
    @GET
    @Path("/target/{targetCurrency}")
    public RatesBean getRate(@PathParam("targetCurrency") final Currency targetCurrency) {
        return Datastore.getRates(targetCurrency);
    }
}
