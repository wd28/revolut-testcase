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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

public class TransferTest extends CommonTestSetup {
    @Test
    public void simpleTransferTest() {
        AccountBean first = target.path("accounts/create").request().post(Entity.text(""), AccountBean.class);
        AccountBean second = target.path("accounts/create").request().post(Entity.text(""), AccountBean.class);
        Datastore.add(UUID.fromString(first.getUuid()), Currency.EUR, 100);
        WebTarget path = target.path("transfer/from/" + first.getUuid() + "/to/" + second.getUuid());
        WebTarget webTarget = path.queryParam("sourceCurrency", "EUR").queryParam("targetCurrency", "EUR");
        webTarget.queryParam("targetAmount", 20).request().post(Entity.text(""), Transaction.class);
        webTarget.queryParam("targetAmount", 15).request().post(Entity.text(""), Transaction.class);
        webTarget.queryParam("targetAmount", 30).request().post(Entity.text(""), Transaction.class);
        first = target.path("accounts/" + first.getUuid()).request().get(AccountBean.class);
        second = target.path("accounts/" + second.getUuid()).request().get(AccountBean.class);
        Assert.assertEquals(35d, first.getAmounts().get(Currency.EUR), 1e-6);
        Assert.assertEquals(65d, second.getAmounts().get(Currency.EUR), 1e-6);
    }

    @Test
    public void multiCurrencyTest() {
        AccountBean first = target.path("accounts/create").request().post(Entity.text(""), AccountBean.class);
        AccountBean second = target.path("accounts/create").request().post(Entity.text(""), AccountBean.class);
        Datastore.add(UUID.fromString(first.getUuid()), Currency.EUR, 100);
        Datastore.setRate(Currency.EUR, 0.5);
        WebTarget path = target.path("transfer/from/" + first.getUuid() + "/to/" + second.getUuid());
        WebTarget webTarget = path.queryParam("sourceCurrency", "EUR");
        webTarget.queryParam("targetCurrency", "EUR").queryParam("targetAmount", 20).request()
                .post(Entity.text(""), Transaction.class);
        webTarget.queryParam("targetCurrency", "USD").queryParam("targetAmount", 15).queryParam("rate", 1d).request()
                .post(Entity.text(""), Transaction.class);
        webTarget.queryParam("targetCurrency", "USD").queryParam("targetAmount", 30).request().post(Entity.text(""), Transaction.class);
        first = target.path("accounts/" + first.getUuid()).request().get(AccountBean.class);
        second = target.path("accounts/" + second.getUuid()).request().get(AccountBean.class);
        //mind the spread
        Assert.assertEquals(50d, first.getAmounts().get(Currency.EUR), 0.1);
        Assert.assertEquals(20d, second.getAmounts().get(Currency.EUR), 1e-6);
        Assert.assertEquals(45d, second.getAmounts().get(Currency.USD), 1e-6);
    }

    @Test(expected = BadRequestException.class)
    public void tooLargeAmountTest() {
        AccountBean first = target.path("accounts/create").request().post(Entity.text(""), AccountBean.class);
        AccountBean second = target.path("accounts/create").request().post(Entity.text(""), AccountBean.class);
        Datastore.add(UUID.fromString(first.getUuid()), Currency.EUR, 100);
        target.path("transfer/from/" + first.getUuid() + "/to/" + second.getUuid())
                .queryParam("sourceCurrency", "EUR").queryParam("targetCurrency", "EUR")
                .queryParam("targetAmount", 200).request().post(Entity.text(""), Transaction.class);

    }

    @Test
    public void concurrentTransactionTest() throws ExecutionException, InterruptedException, TimeoutException {
        AccountBean first = target.path("accounts/create").request().post(Entity.text(""), AccountBean.class);
        AccountBean second = target.path("accounts/create").request().post(Entity.text(""), AccountBean.class);
        AccountBean third = target.path("accounts/create").request().post(Entity.text(""), AccountBean.class);
        Datastore.add(UUID.fromString(first.getUuid()), Currency.EUR, 1010);
        WebTarget webTarget = target.queryParam("sourceCurrency", "EUR")
                .queryParam("targetCurrency", "EUR").queryParam("targetAmount", 1d);
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        List<Callable<Object>> tasks = new ArrayList<>(1000);
        for(int i = 0; i < 500; i++) {
            tasks.add(() -> webTarget.path("/transfer/from/" + first.getUuid() + "/to/" + second.getUuid()).request().post(Entity.text("")));
            tasks.add(() -> webTarget.path("/transfer/from/" + first.getUuid() + "/to/" + third.getUuid()).request().post(Entity.text("")));
        }
        List<Future<Object>> futures = executorService.invokeAll(tasks);
        for (Future<Object> future : futures) {
            future.get(100, TimeUnit.MILLISECONDS);
        }
        double firstAccount = target.path("accounts/" + first.getUuid()).request().get(AccountBean.class)
                .getAmounts().get(Currency.EUR);
        double secondAccount = target.path("accounts/" + second.getUuid()).request().get(AccountBean.class)
                .getAmounts().get(Currency.EUR);
        double thirdAccount = target.path("accounts/" + third.getUuid()).request().get(AccountBean.class)
                .getAmounts().get(Currency.EUR);
        Assert.assertEquals(10d, firstAccount, 1e-6);
        Assert.assertEquals(500d, secondAccount, 1e-6);
        Assert.assertEquals(500d, thirdAccount, 1e-6);
    }
}
