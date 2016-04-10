package revolut.wd28.datastore;

import jersey.repackaged.com.google.common.base.Preconditions;
import jersey.repackaged.com.google.common.collect.Collections2;
import jersey.repackaged.com.google.common.collect.ImmutableList;
import jersey.repackaged.com.google.common.collect.Maps;
import revolut.wd28.datastore.beans.AccountBean;
import revolut.wd28.datastore.beans.RatesBean;
import revolut.wd28.datastore.model.Account;
import revolut.wd28.datastore.model.Currency;
import revolut.wd28.datastore.model.RateSet;
import revolut.wd28.datastore.model.Transaction;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public class Datastore {
    private final static Map<UUID, Account> accounts = Maps.newConcurrentMap();
    private final static RateSet rates = new RateSet();

    public static AccountBean createAccount(String name) {
        Account newAccount = new Account(name);
        accounts.put(newAccount.getId(), newAccount);
        return newAccount.getAccountDetails();
    }

    public static AccountBean getAccountDetails(UUID accountId) throws NullPointerException {
        Account account = Preconditions.checkNotNull(accounts.get(accountId), "No account with id %s", accountId);
        return account.getAccountDetails();
    }

    public static Collection<AccountBean> getAccounts(String name) {
        return Collections2.transform(
                Collections2.filter(ImmutableList.copyOf(accounts.values()), (Account a) -> name == null || name.equals(a.getName())),
                Account::getAccountDetails);
    }

    public static Collection<Transaction> getTransactions(UUID account, int limit) {
        return Preconditions.checkNotNull(accounts.get(account)).getLastTransactions(limit);
    }

    public static void add(UUID accountId, Currency currency, double amount) {
        Account account = Preconditions.checkNotNull(accounts.get(accountId));
        account.add(currency, amount);
    }

    public static Transaction transfer(UUID from, UUID to, Currency fromCcy, Currency toCcy, double sourceAmount,
                                       double targetAmount, String comment) {
        Account sourceAccount = Preconditions.checkNotNull(accounts.get(from), "No user with id %s", String.valueOf(from));
        Account targetAccount = Preconditions.checkNotNull(accounts.get(to), "No user with id %s", String.valueOf(to));
        Preconditions.checkArgument(sourceAmount > 0, "source amount should be greater than zero, is %s", sourceAmount);
        Preconditions.checkArgument(targetAmount > 0, "source amount should be greater than zero, is %s", targetAmount);
        Transaction transaction = new Transaction();
        transaction.setTime(LocalDateTime.now());
        transaction.setFrom(from);
        transaction.setTo(to);
        transaction.setSourceCurrency(fromCcy);
        transaction.setTargetCurrency(toCcy);
        transaction.setSourceAmount(sourceAmount);
        transaction.setTargetAmount(targetAmount);
        transaction.setComment(comment);
        if (sourceAccount.withdraw(fromCcy, sourceAmount)) {
            transaction.setSucceessful(true);
            targetAccount.add(toCcy, targetAmount);
            sourceAccount.addTransaction(transaction);
            targetAccount.addTransaction(transaction);
        } else {
            transaction.setSucceessful(false);
            transaction.setComment("Failed - not enough money on account");
            sourceAccount.addTransaction(transaction);
            throw new IllegalArgumentException("Not enough money at " + from);
        }
        return transaction;
    }

    public static Transaction transferUsingRate(UUID from, UUID to, Currency fromCcy, Currency toCcy,
                                                double targetAmount, double rate, String comment) {
        return transfer(from, to, fromCcy, toCcy, targetAmount / rate, targetAmount, comment);
    }

    public static Transaction transferUsingRate(UUID from, UUID to, Currency fromCcy, Currency toCcy,
                                                double targetAmount, String comment) {
        return transferUsingRate(from, to, fromCcy, toCcy, targetAmount, getRate(fromCcy, toCcy), comment);
    }

    public static double getRate(Currency fromCcy, Currency toCcy) {
        return rates.getRate(fromCcy, toCcy);
    }

    public static void setRate(Currency currency, double rate) {
        rates.setUsdRate(currency, rate);
    }

    public static RatesBean getRates(Currency targetCurrency) {
        return rates.getRatesForTarget(targetCurrency);
    }

}
