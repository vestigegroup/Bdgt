package com.example.chanakyabharwaj.bdgt;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chanakya.bharwaj on 24/01/16.
 */

public class ExpenseDBHelper extends SQLiteOpenHelper {
    private static ExpenseDBHelper dbInstance;

    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "Expense.db";

    public static synchronized ExpenseDBHelper getInstance(Context context) {
        if (dbInstance == null) {
            dbInstance = new ExpenseDBHelper(context.getApplicationContext());
        }
        return dbInstance;
    }

    private ExpenseDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ExpenseContract.SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(ExpenseContract.SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void addExpense(Expense expense) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(ExpenseContract.ExpenseEntry.COLUMN_NAME_EXPENSE_AMOUNT, expense.amount.toString());
            values.put(ExpenseContract.ExpenseEntry.COLUMN_NAME_EXPENSE_CATEGORY, expense.category);
            values.put(ExpenseContract.ExpenseEntry.COLUMN_NAME_EXPENSE_DESCRIPTION, expense.description);
            values.put(ExpenseContract.ExpenseEntry.COLUMN_NAME_EXPENSE_DATE, expense.date.getTimeInMillis());

            db.insertOrThrow(ExpenseContract.ExpenseEntry.TABLE_NAME, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(ExpenseDBHelper.class.getSimpleName(), "Error while trying to add expense to database");
        } finally {
            db.endTransaction();
        }
    }

    public int updateExpense(Expense expense) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ExpenseContract.ExpenseEntry.COLUMN_NAME_EXPENSE_AMOUNT, expense.amount.toString());
        values.put(ExpenseContract.ExpenseEntry.COLUMN_NAME_EXPENSE_CATEGORY, expense.category);
        values.put(ExpenseContract.ExpenseEntry.COLUMN_NAME_EXPENSE_DESCRIPTION, expense.description);
        values.put(ExpenseContract.ExpenseEntry.COLUMN_NAME_EXPENSE_DATE, expense.date.getTimeInMillis());

        return db.update(ExpenseContract.ExpenseEntry.TABLE_NAME, values, ExpenseContract.ExpenseEntry._ID + " = ?",
                new String[]{String.valueOf(expense._id)});
    }

    public ArrayList<Expense> getAllExpenses() {
        SQLiteDatabase db = this.getWritableDatabase();

        String[] allColumns = {ExpenseContract.ExpenseEntry._ID,
                ExpenseContract.ExpenseEntry.COLUMN_NAME_EXPENSE_AMOUNT,
                ExpenseContract.ExpenseEntry.COLUMN_NAME_EXPENSE_CATEGORY,
                ExpenseContract.ExpenseEntry.COLUMN_NAME_EXPENSE_DESCRIPTION,
                ExpenseContract.ExpenseEntry.COLUMN_NAME_EXPENSE_DATE};

        ArrayList<Expense> expenses = new ArrayList<Expense>();

        Cursor cursor = db.query(ExpenseContract.ExpenseEntry.TABLE_NAME,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Expense expense = cursorToExpense(cursor);
            expenses.add(expense);
            cursor.moveToNext();
        }

        cursor.close();
        return expenses;
    }

    public Expense getExpenseById(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        String[] allColumns = {ExpenseContract.ExpenseEntry._ID,
                ExpenseContract.ExpenseEntry.COLUMN_NAME_EXPENSE_AMOUNT,
                ExpenseContract.ExpenseEntry.COLUMN_NAME_EXPENSE_CATEGORY,
                ExpenseContract.ExpenseEntry.COLUMN_NAME_EXPENSE_DESCRIPTION,
                ExpenseContract.ExpenseEntry.COLUMN_NAME_EXPENSE_DATE};

        Cursor cursor = db.query(ExpenseContract.ExpenseEntry.TABLE_NAME,
                allColumns, ExpenseContract.ExpenseEntry._ID + " = ?", new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        return cursorToExpense(cursor);
    }

    public int deleteExpense(Expense expense) {
        SQLiteDatabase db = this.getWritableDatabase();

        String[] allColumns = {ExpenseContract.ExpenseEntry._ID,
                ExpenseContract.ExpenseEntry.COLUMN_NAME_EXPENSE_AMOUNT,
                ExpenseContract.ExpenseEntry.COLUMN_NAME_EXPENSE_CATEGORY,
                ExpenseContract.ExpenseEntry.COLUMN_NAME_EXPENSE_DESCRIPTION,
                ExpenseContract.ExpenseEntry.COLUMN_NAME_EXPENSE_DATE};

        return db.delete(ExpenseContract.ExpenseEntry.TABLE_NAME, ExpenseContract.ExpenseEntry._ID + "=" + expense._id, null);
    }

    public ArrayList<String> getAllCategories() {
        SQLiteDatabase db = this.getWritableDatabase();

        String[] categoryColumn = {ExpenseContract.ExpenseEntry.COLUMN_NAME_EXPENSE_CATEGORY};

        ArrayList<String> categories = new ArrayList<String>();

        Cursor cursor = db.query(true, ExpenseContract.ExpenseEntry.TABLE_NAME,
                categoryColumn, null, null, ExpenseContract.ExpenseEntry.COLUMN_NAME_EXPENSE_CATEGORY, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            categories.add(cursor.getString(cursor.getColumnIndex(ExpenseContract.ExpenseEntry.COLUMN_NAME_EXPENSE_CATEGORY)));
            cursor.moveToNext();
        }

        cursor.close();
        return categories;
    }

    private Expense cursorToExpense(Cursor cursor) {
        Expense expense = new Expense();
        expense.setId(cursor.getInt(cursor.getColumnIndex(ExpenseContract.ExpenseEntry._ID)));
        expense.setAmount(cursor.getString(cursor.getColumnIndex(ExpenseContract.ExpenseEntry.COLUMN_NAME_EXPENSE_AMOUNT)));
        expense.setDescription(cursor.getString(cursor.getColumnIndex(ExpenseContract.ExpenseEntry.COLUMN_NAME_EXPENSE_DESCRIPTION)));
        expense.setCategory(cursor.getString(cursor.getColumnIndex(ExpenseContract.ExpenseEntry.COLUMN_NAME_EXPENSE_CATEGORY)));
        expense.setDate(cursor.getLong(cursor.getColumnIndex(ExpenseContract.ExpenseEntry.COLUMN_NAME_EXPENSE_DATE)));
        return expense;
    }
}