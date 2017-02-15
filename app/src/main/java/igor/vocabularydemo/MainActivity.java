package igor.vocabularydemo;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity implements OnClickListener {

    final String LOG_TAG = "myLogs";

    Button btnAdd, btnRead, btnClear, btnUpd, btnDel;
    EditText etRussian, etEnglish, etID;

    DBHelper dbHelper;

    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(this);

        btnRead = (Button) findViewById(R.id.btnRead);
        btnRead.setOnClickListener(this);

        btnClear = (Button) findViewById(R.id.btnClear);
        btnClear.setOnClickListener(this);

        btnUpd = (Button) findViewById(R.id.btnUpd);
        btnUpd.setOnClickListener(this);

        btnDel = (Button) findViewById(R.id.btnDel);
        btnDel.setOnClickListener(this);

        etRussian = (EditText) findViewById(R.id.etRussian);
        etEnglish = (EditText) findViewById(R.id.etEnglish);
        etID = (EditText) findViewById(R.id.etID);

        // создаем объект для создания и управления версиями БД
        dbHelper = new DBHelper(this);
    }

    public void onClick(View v) {

        // создаем объект для данных
        ContentValues cv = new ContentValues();

        // получаем данные из полей ввода
        String russian = etRussian.getText().toString();
        String english = etEnglish.getText().toString();
        String sample = etID.getText().toString();

        // подключаемся к БД
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        switch (v.getId()) {
            case R.id.btnAdd:
                Log.d(LOG_TAG, "Insert in mytable:");
                // подготовим данные для вставки в виде пар: наименование столбца -
                // значение
                cv.put("russian", russian);
                cv.put("english", english);
                // вставляем запись и получаем ее ID
                long rowID = db.insert("mytable", null, cv);
                Log.d(LOG_TAG, "row inserted, ID = " + rowID);
                break;
            case R.id.btnRead:
                Log.d(LOG_TAG, " Rows in mytable:");
                // делаем запрос всех данных из таблицы mytable, получаем Cursor
                Cursor cursor = db.query("mytable", null, null, null, null, null, null);

                // ставим позицию курсора на первую строку выборки
                // если в выборке нет строк, вернется false
                if (cursor.moveToFirst()) {

                    // определяем номера столбцов по имени в выборке
                    int idColIndex = cursor.getColumnIndex("id");
                    int russianColIndex = cursor.getColumnIndex("russian");
                    int englishColIndex = cursor.getColumnIndex("english");

                    do {
                        // получаем значения по номерам столбцов и пишем все в лог
                        Log.d(LOG_TAG,
                                "ID = " + cursor.getInt(idColIndex) + ", russian: "
                                        + cursor.getString(russianColIndex) + ", english: "
                                        + cursor.getString(englishColIndex));
                        // переход на следующую строку
                        // а если следующей нет (текущая - последняя), то false -
                        // выходим из цикла
                    } while (cursor.moveToNext());
                } else
                    Log.d(LOG_TAG, "0 rows");
                cursor.close();
                break;
            case R.id.btnClear:
                Log.d(LOG_TAG, "Clear mytable:");
                // удаляем все записи
                int clearCount = db.delete("mytable", null, null);
                Log.d(LOG_TAG, "deleted rows count = " + clearCount);
                break;
            case R.id.btnUpd:
                if (sample.equalsIgnoreCase("")) {
                    break;
                }
                Log.d(LOG_TAG, " Update mytable:");
                // подготовим значения для обновления
                cv.put("russian", russian);
                cv.put("english", english);
                // обновляем по english
                int updCount = db.update("mytable", cv, "english = ?",
                        new String[] { sample });
                Log.d(LOG_TAG, "updated rows count = " + updCount);
                break;
            case R.id.btnDel:
                if (sample.equalsIgnoreCase("")) {
                    break;
                }
                Log.d(LOG_TAG, "Delete from mytable:");
                // удаляем по english
                int delCount = db.delete("mytable", "english = ?",  new String[] { sample } );
                Log.d(LOG_TAG, "deleted rows count = " + delCount);
                break;
        }
        // закрываем подключение к БД
        dbHelper.close();
    }

    class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            // конструктор суперкласса
            super(context, "myDB", null, 1);
        }

        public void onCreate(SQLiteDatabase db) {
            Log.d(LOG_TAG, "onCreate database");
            // создаем таблицу с полями
            db.execSQL("create table mytable ("
                    + "id integer primary key autoincrement,"
                    + "russian text,"
                    + "english text" + ");");
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

}
