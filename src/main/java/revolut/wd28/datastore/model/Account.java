package revolut.wd28.datastore.model;

import jersey.repackaged.com.google.common.base.Preconditions;
import jersey.repackaged.com.google.common.collect.ImmutableList;
import jersey.repackaged.com.google.common.collect.ImmutableMap;
import jersey.repackaged.com.google.common.collect.Lists;
import jersey.repackaged.com.google.common.collect.Maps;
import revolut.wd28.datastore.beans.AccountBean;

import java.util.List;
import java.util.Map;
import java.util.UUID;


public class Account {
    private final UUID id;
    private final String name;

    //NB: never use double as money in production system concerned with actual payments
    private final Map<Currency, Double> amounts = Maps.newEnumMap(Currency.class);
    private final List<Transaction> transactions = Lists.newArrayList();

    public Account(String name) {
        id = UUID.randomUUID();
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public synchronized AccountBean getAccountDetails() {
        return new AccountBean(name, id, ImmutableMap.copyOf(amounts));
    }

    public synchronized double getAmount(Currency currency) {
        if (amounts.containsKey(currency)) {
            return amounts.get(currency);
        } else {
            return 0;
        }
    }

    public synchronized boolean withdraw(Currency currency, double amount) {
        Preconditions.checkArgument(amount > 0);
        if (getAmount(currency) < amount) {
            return false;
        } else {
            amounts.put(currency, getAmount(currency) - amount);
            return true;
        }
    }

    public synchronized void add(Currency currency, double amount) {
        Preconditions.checkArgument(amount > 0);
        amounts.put(currency, getAmount(currency) + amount);
    }

    public synchronized List<Transaction> getLastTransactions(int limit) {
        int start = Math.max(0, transactions.size() - limit);
        return ImmutableList.copyOf(transactions.subList(start, transactions.size()));
    }

    public synchronized void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }
}
