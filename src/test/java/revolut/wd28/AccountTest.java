package revolut.wd28;

import org.junit.Assert;
import org.junit.Test;
import revolut.wd28.datastore.beans.AccountBean;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.UUID;

public class AccountTest extends CommonTestSetup {
    @Test
    public void searchByName() {
        AccountBean first = target.queryParam("name", "John Smith").path("accounts/create").request().post(Entity.text(""), AccountBean.class);
        AccountBean second = target.queryParam("name", "John Smith").path("accounts/create").request().post(Entity.text(""), AccountBean.class);
        AccountBean third = target.queryParam("name", "Jane Smith").path("accounts/create").request().post(Entity.text(""), AccountBean.class);
        List<AccountBean> allAccounts = target.path("accounts").request(MediaType.APPLICATION_XML_TYPE)
                .get(new GenericType<List<AccountBean>>() { });
        List<AccountBean> johnAccounts = target.path("accounts").queryParam("name", "John Smith").
                request(MediaType.APPLICATION_XML_TYPE).get(new GenericType<List<AccountBean>>() { });
        Assert.assertEquals(3, allAccounts.size());
        Assert.assertEquals(2, johnAccounts.size());
    }

    @Test
    public void testAccountCreated() {
        String accountName = "John Smith";
        AccountBean account = target.queryParam("name", accountName).path("accounts/create").request().post(Entity.text(""), AccountBean.class);
        Assert.assertEquals(accountName, account.getName());
        account = target.path("accounts/create").request().post(Entity.text(""), AccountBean.class);
        Assert.assertNotNull(account);
        Assert.assertEquals("", account.getName());
    }

    @Test
    public void testAccountReceived() {
        AccountBean account = target.queryParam("name", "John Smith").path("accounts/create")
                .request(MediaType.APPLICATION_JSON_TYPE).post(Entity.text(""), AccountBean.class);
        List<AccountBean> allAccounts = target.path("accounts").request(MediaType.APPLICATION_XML_TYPE)
                .get(new GenericType<List<AccountBean>>() {
                });
        Assert.assertTrue("account list doesn't has newly created account", allAccounts.contains(account));
        AccountBean receivedAccount = target.path("accounts/" + account.getUuid()).request().get(AccountBean.class);
        Assert.assertEquals("account received first and second time are not identical", account, receivedAccount);
    }

    @Test(expected = NotFoundException.class)
    public void testIncorrectAccountException() {
        //fake id
        UUID uuid = UUID.randomUUID();
        AccountBean account = target.path("accounts/" + uuid).request().get(AccountBean.class);
        System.out.println(account);
    }

    @Test
    public void testTwoAccounts() {
        AccountBean first = target.queryParam("name", "John Smith").path("accounts/create").request().post(Entity.text(""), AccountBean.class);
        AccountBean second = target.queryParam("name", "John Smith").path("accounts/create").request().post(Entity.text(""), AccountBean.class);
        AccountBean third = target.queryParam("name", "Jane Smith").path("accounts/create").request().post(Entity.text(""), AccountBean.class);
        Assert.assertNotEquals(first, second);
        Assert.assertNotEquals(first, third);
    }
}
