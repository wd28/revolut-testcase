package revolut.wd28;

import org.junit.Assert;
import org.junit.Test;
import revolut.wd28.datastore.Datastore;
import revolut.wd28.datastore.beans.RatesBean;
import revolut.wd28.datastore.model.Currency;

import javax.ws.rs.NotFoundException;

public class RateTest extends CommonTestSetup {
    @Test
    public void getSimpleRate() {
        Datastore.setRate(Currency.EUR, 0.95);
        RatesBean rates = target.path("rate/target/EUR").request().get(RatesBean.class);
        Assert.assertEquals(0.95, rates.getRates().get(Currency.USD), 0.01);
    }

    @Test
    public void getCrossRate() {
        Datastore.setRate(Currency.EUR, 0.9);
        Datastore.setRate(Currency.GBP, 0.7);
        RatesBean rates = target.path("rate/target/EUR").request().get(RatesBean.class);
        Assert.assertEquals(0.9 / 0.7, rates.getRates().get(Currency.GBP), 0.01);
    }

    @Test(expected = NotFoundException.class)
    public void getRateForNonexistentCurrency() {
        target.path("rate/target/TWO").request().get(RatesBean.class);
    }

}
