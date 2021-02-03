package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PersistentAccountDAO implements AccountDAO {
    private final Db_helper dbhelper;

    public PersistentAccountDAO(Db_helper dbhelper) {
        this.dbhelper = dbhelper;
    }


    @Override
    public List<String> getAccountNumbersList() {
        List<String> accountNos = new ArrayList<>();
        List<Account> accounts = dbhelper.getAllAccounts();
        if(accounts.size()>0){
            for(Account a:accounts){
                accountNos.add(a.getAccountNo());
            }
            return accountNos;
        }
        return accountNos;

    }

    @Override
    public List<Account> getAccountsList() {
        return dbhelper.getAllAccounts();
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        return dbhelper.getSingleAccount(accountNo);
    }

    @Override
    public void addAccount(Account account) {
        dbhelper.addAccount(account);
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        dbhelper.deleteAccount(accountNo);
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        if(accountNo == null){
            throw new InvalidAccountException("Account number invalid");
        }

        Account account = dbhelper.getSingleAccount(accountNo);
        double balance = account.getBalance();
        if(expenseType == ExpenseType.INCOME){
            account.setBalance(balance+amount);
        }else if (expenseType == ExpenseType.EXPENSE){
            account.setBalance(balance-amount);

        }
        if(account.getBalance()<0 ){
            throw new InvalidAccountException("Not enough credits to withdraw");
        }

        else{
            dbhelper.updateAccount(account);
        }
    }
}
