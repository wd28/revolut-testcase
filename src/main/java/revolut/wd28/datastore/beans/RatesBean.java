package revolut.wd28.datastore.beans;

import revolut.wd28.datastore.model.Currency;

import java.util.Map;

public class RatesBean {
    private Currency sourceCurrency;
    private Currency targetCurrency;
    private Map<Currency, Double> rates;

    public Currency getSourceCurrency() {
        return sourceCurrency;
    }

    public void setSourceCurrency(Currency sourceCurrency) {
        this.sourceCurrency = sourceCurrency;
    }

    public Currency getTargetCurrency() {
        return targetCurrency;
    }

    public void setTargetCurrency(Currency targetCurrency) {
        this.targetCurrency = targetCurrency;
    }

    public Map<Currency, Double> getRates() {
        return rates;
    }

    public void setRates(Map<Currency, Double> rates) {
        this.rates = rates;
    }
}
