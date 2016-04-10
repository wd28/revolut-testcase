package revolut.wd28.resources;

import revolut.wd28.datastore.Datastore;
import revolut.wd28.datastore.beans.AccountBean;
import revolut.wd28.datastore.model.Transaction;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.UUID;

@Path("/accounts")
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class Account {
    @POST
    @Path("/create")
    public AccountBean createAccount(@QueryParam("name") String name) {
        return Datastore.createAccount(name == null ? "" : name);
    }

    @GET
    public Collection<AccountBean> getAllAccounts(@QueryParam("name") String name) {
        return Datastore.getAccounts(name);
    }

    @GET
    @Path("/{accountid}")
    public AccountBean getAccountInfo(@PathParam("accountid") UUID accountId) {
        return Datastore.getAccountDetails(accountId);
    }

    @GET
    @Path("/{accountid}/transactions")
    public Collection<Transaction> getTransactions(@PathParam("accountid") UUID accountId, @QueryParam("limit") Integer limit) {
        return Datastore.getTransactions(accountId, limit == null ? 100 : limit);
    }
}
