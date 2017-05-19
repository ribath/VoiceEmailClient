package com.chtl.mainflow;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

/**
 * Created by Ribath on 4/30/2017.
 */

public class DBHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "NupuITDB";
    private static final int DATABASE_VERSION = 1;
    private Dao<SentMail, String> ContactClassDao = null;

    public DBHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, SentMail.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, SentMail.class, true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Dao<SentMail, String> getSentMailDao() throws SQLException {
        if (ContactClassDao == null)
        {
            ContactClassDao = getDao(SentMail.class);
        }
        return ContactClassDao;
    }
}
