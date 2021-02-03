package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistentTransactionDAO implements TransactionDAO {

    private final Db_helper dbhelper;

    public PersistentTransactionDAO(Db_helper dbhelper) {
        this.dbhelper = dbhelper;
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        Transaction transaction = new Transaction(date,accountNo,expenseType,amount);
        if(accountNo != null){
            dbhelper.enterTransaction(transaction);
        }
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        return dbhelper.getAllTransactions();
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        return dbhelper.getAllTransactionsLimited(limit);
    }
}