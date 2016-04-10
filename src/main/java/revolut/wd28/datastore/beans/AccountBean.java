package revolut.wd28.datastore.beans;

import revolut.wd28.datastore.model.Currency;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Map;
import java.util.UUID;

@XmlRootElement
public class AccountBean {
    private String name;
    private String uuid;
    private Map<Currency, Double> amounts;

    public AccountBean(String name, UUID uuid, Map<Currency, Double> amounts) {
        this.name = name;
        this.uuid = uuid.toString();
        this.amounts = amounts;
    }

    public AccountBean() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Map<Currency, Double> getAmounts() {
        return amounts;
    }

    public void setAmounts(Map<Currency, Double> amounts) {
        this.amounts = amounts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        AccountBean that = (AccountBean) o;

        if (!name.equals(that.name))
            return false;
        if (!uuid.equals(that.uuid))
            return false;
        return amounts.equals(that.amounts);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + uuid.hashCode();
        result = 31 * result + amounts.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "AccountBean{" +
                "name='" + name + '\'' +
                ", uuid='" + uuid + '\'' +
                ", amounts=" + amounts +
                '}';
    }
}
