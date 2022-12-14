package ru.netology.nmedia.database


import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DbHelper(
    context: Context,
    dbVersion: Int,
    dbName: String,
    private val DDLs: Array<String>
) : SQLiteOpenHelper(context, dbName, null, dbVersion) {

    override fun onCreate(db: SQLiteDatabase) {
        DDLs.forEach {
            db.execSQL(it)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
        //super.onDowngrade(db, oldVersion, newVersion)
    }
}