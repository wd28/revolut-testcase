package revolut.wd28;

import org.junit.Assert;
import org.junit.Test;
import revolut.wd28.datastore.Datastore;
import revolut.wd28.datastore.beans.AccountBean;
import revolut.wd28.datastore.model.Currency;
import revolut.wd28.datastore.model.Transaction;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import java.util.List;
import java.util.UUID;

public class TransactionTest extends CommonTestSetup {
    @Test
    public void testTransactionHistory() {
        AccountBean first = target.path("accounts/create").request().post(Entity.text(""), AccountBean.class);
        AccountBean second = target.path("accounts/create").request().post(Entity.text(""), AccountBean.class);
        Datastore.add(UUID.fromString(first.getUuid()), Currency.EUR, 100);
        WebTarget path = target.path("transfer/from/" + first.getUuid() + "/to/" + second.getUuid());
        WebTarget webTarget = path.queryParam("sourceCurrency", "EUR").queryParam("targetCurrency", "EUR");
        webTarget.queryParam("targetAmount", 20).request().post(Entity.text(""), Transaction.class);
        webTarget.queryParam("targetAmount", 15).request().post(Entity.text(""), Transaction.class);
        webTarget.queryParam("targetAmount", 30).request().post(Entity.text(""), Transaction.class);
        try {
            webTarget.queryParam("targetAmount", 50).request().post(Entity.text(""), Transaction.class);
        } catch (BadRequestException e) {
            //ignore - this is expected
        }
        List<Transaction> lastTransaction = target.path("/accounts/" + first.getUuid() + "/transactions")
                .queryParam("limit", 1).request().get(new GenericType<List<Transaction>>() { });
        Assert.assertEquals(1, lastTransaction.size());
        Assert.assertEquals(false, lastTransaction.get(0).isSucceessful());
        List<Transaction> secondUserTrasnactions = target.path("/accounts/" + second.getUuid() + "/transactions")
                .request().get(new GenericType<List<Transaction>>() { });
        Assert.assertEquals(3, secondUserTrasnactions.size());
        Assert.assertEquals(true, secondUserTrasnactions.get(2).isSucceessful());
    }
}
