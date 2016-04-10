package revolut.wd28.datastore.model;

import jersey.repackaged.com.google.common.collect.Maps;
import revolut.wd28.datastore.beans.RatesBean;

import java.util.Map;

public class RateSet {
    private final static double tinySpread = 1e-5; //0.5 bps
    /*ignore major pairs, do a simple cross rate via USD and add a tiny spread
      this is USDXXX rate, ignore actual currency pairing*/
    private final Map<Currency, Double> usdRates = Maps.newEnumMap(Currency.class);

    public RateSet() {
        for (Currency currency : Currency.values()) {
            usdRates.put(currency, 1d);
        }
    }

    public synchronized double getRate(Currency source, Currency target) {
        if (source == target) {
            return 1d;
        } else {
            return usdRates.get(target) / usdRates.get(source) * (1 + tinySpread);
        }
    }

    public synchronized RatesBean getRatesForSource(final Currency source) {
        RatesBean bean = new RatesBean();
        bean.setSourceCurrency(source);
        bean.setRates(Maps.newEnumMap(Maps.transformEntries(usdRates, (key, value) -> getRate(source, key))));
        return bean;
    }

    public synchronized RatesBean getRatesForTarget(final Currency target) {
        RatesBean bean = new RatesBean();
        bean.setTargetCurrency(target);
        bean.setRates(Maps.newEnumMap(Maps.transformEntries(usdRates, (key, value) -> getRate(key, target))));
        return bean;
    }

    public synchronized void setUsdRate(Currency currency, double rate) {
        usdRates.put(currency, rate);
    }
}
