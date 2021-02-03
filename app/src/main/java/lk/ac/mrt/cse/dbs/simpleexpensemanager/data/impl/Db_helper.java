package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class Db_helper extends SQLiteOpenHelper {
    public static final String db = "180234U.db";
    public static final int VERSION = 4;

    //tables
    private static final String ACCOUNT = "account";
    private static final String LOGS = "transactions";

    //account table cols
    private static final String ACCOUNT_NO = "accountNo";
    private static final String BANK_NAME = "bankName";
    private static final String ACCOUNT_HOLDER_NAME = "accountHolderName";
    private static final String BALANCE = "balance";

    //transaction table cols

    private static final String DATE = "date";
    private static final String ACCOUNT_NUMBER = "accountNo";
    private static final String EXPENSE_TYPE = "expenseType";
    private static final String AMOUNT = "amount";

    private final Context context;


    public Db_helper(@Nullable Context context) {
        super(context, db, null, VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String AccountTable= "CREATE TABLE " + ACCOUNT
                + "(" + ACCOUNT_NO + " TEXT(50) PRIMARY KEY," + BANK_NAME + " TEXT(50),"
                + ACCOUNT_HOLDER_NAME +" TEXT(30)," + BALANCE + " REAL" + ")";

        // transaction create table statement

        final String LogTable= "CREATE TABLE " + LOGS
                + "(" + DATE + " DATE," + ACCOUNT_NUMBER + " TEXT(50),"
                + EXPENSE_TYPE +" TEXT(30)," + AMOUNT + " REAL" + ", FOREIGN KEY (" + ACCOUNT_NUMBER +") REFERENCES "+ ACCOUNT + "(" + ACCOUNT_NUMBER +"))";


        db.execSQL(AccountTable);
        db.execSQL(LogTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(" DROP TABLE IF EXISTS " + ACCOUNT);
        db.execSQL(" DROP TABLE IF EXISTS " + LOGS);
        onCreate(db);
    }

    public Account getSingleAccount(String acco){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM "+ ACCOUNT +" WHERE "+ ACCOUNT_NO +" =?",new String[]{acco});
        Account account = null;
        if (res.getCount() != 0) {
            while (res.moveToNext()) {
                String accountNo = res.getString(0);
                String bankName = res.getString(1);
                String accountHolderName = res.getString(2);
                double balance = res.getDouble(3);
                account = new Account(accountNo, bankName, accountHolderName, balance);
            }
        }
        return account;
    }


    public List<Account> getAllAccounts() {
        List<Account> accounts = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + ACCOUNT, null);

        if (res.getCount() == 0) {
            return accounts;
        }
        while (res.moveToNext()) {
            String accountNo = res.getString(0);
            String bankName = res.getString(1);
            String accountHolderName = res.getString(2);
            double balance = res.getDouble(3);
            Account temp = new Account(accountNo, bankName, accountHolderName, balance);
            accounts.add(temp);

        }
        return accounts;

    }

    public  Boolean addAccount(Account account){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ACCOUNT_NO,account.getAccountNo());
        values.put(BANK_NAME,account.getBankName());
        values.put(ACCOUNT_HOLDER_NAME,account.getAccountHolderName());
        values.put(BALANCE,account.getBalance());

        long res = db.insert(ACCOUNT,null , values);
        if(res==-1){
            return false;
        }
        return true;


    }

    public Boolean updateAccount(Account account){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ACCOUNT_NO,account.getAccountNo());
        values.put(BANK_NAME,account.getBankName());
        values.put(ACCOUNT_HOLDER_NAME,account.getAccountHolderName());
        values.put(BALANCE,account.getBalance());

        long res = db.update(ACCOUNT, values, ACCOUNT_NO +"=?",new String[]{account.getAccountNo()} );
        if(res==-1){
            return false;
        }
        return true;

    }





    public boolean deleteAccount(String accNO){
        SQLiteDatabase db = this.getWritableDatabase();
        long res = db.delete(ACCOUNT, ACCOUNT_NO +"= "+accNO,null);
        if (res> 0){
            return true;
        }
        return false;

    }


    // transaction table handling



    public  List<Transaction> getAllTransactions() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM "+ LOGS,null);

        List<Transaction> transactions=new ArrayList<>();
        DateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
        if (res.getCount() >= 0) {

            while (res.moveToNext()) {

                try {
                    String accountNo = res.getString(1);
                    ExpenseType expenseType = ExpenseType.valueOf(res.getString(2));
                    Date date = format.parse(res.getString(0));

                    double amount = res.getDouble(3);
                    transactions.add(new Transaction(date, accountNo, expenseType, amount));
                } catch (ParseException e) {
                    e.printStackTrace();
                }


            }
        }
        return transactions;


    }

    public  List<Transaction> getAllTransactionsLimited(int limit)  {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM "+ LOGS + " LIMIT " + limit,null);

        List<Transaction> transactions=new ArrayList<>();
        DateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
        if (res.getCount() >= 0) {

            while (res.moveToNext()) {
                System.out.print(res);

                try {
                    String accountNo = res.getString(1);
                    ExpenseType expenseType = ExpenseType.valueOf(res.getString(2));
                    Date date = format.parse(res.getString(0));
                    double amount = res.getDouble(3);
                    transactions.add(new Transaction(date, accountNo, expenseType, amount));

                } catch (ParseException e) {
                    e.printStackTrace();
                }


            }
        }
        return transactions;

    }

    public boolean enterTransaction(Transaction transaction){



        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        System.out.println(transaction.getDate());
        values.put(DATE,transaction.getDate().toString());
        values.put(ACCOUNT_NUMBER,transaction.getAccountNo());
        values.put(EXPENSE_TYPE,transaction.getExpenseType().toString());
        values.put(AMOUNT,transaction.getAmount());


        long res = db.insert(LOGS,null,values);
        if(res == -1){
            return false;
        }
        return true;
    }
}
