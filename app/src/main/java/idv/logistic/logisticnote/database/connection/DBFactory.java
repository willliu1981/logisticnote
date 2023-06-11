package idv.logistic.logisticnote.database.connection;

import android.content.Context;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DBFactory {
    private static DBFactory factory = new DBFactory();

    private String dbName;
    private String packageName;
    private Context context;

    private DBFactory() {

    }

    public static DBFactory config() {


        return factory;
    }

    public DBFactory setDBName(String dbname) {
        this.dbName = dbname;
        return this;
    }

    public DBFactory setContext(Context context) {
        this.context = context;
        return this;
    }

    public DBFactory setPackageName(String packageName) {
        this.packageName = packageName;
        return this;
    }

    public static String getPackageName() {
        return factory.packageName;
    }

    public static String getDBName() {
        return factory.dbName;
    }

    public static Context getContext() {
        return factory.context;
    }

    public static String getDBAbsolutePath() {
        String rtPath = null;
        if (factory.context != null) {
            String strPath = factory.context.getFilesDir().getPath();
            File file = factory.context.getFileStreamPath(DBFactory.getDBName());
            if (!file.exists()) {
                Toast.makeText(DBFactory.getContext(), "start copy db...", Toast.LENGTH_SHORT).show();

                try {
                    copyAssetsFileTo("log.db", strPath, DBFactory.getDBName());
                    Toast.makeText(DBFactory.getContext(), "copy db finish", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                //Toast.makeText(DBFactory.getContext(), "db is exists", Toast.LENGTH_SHORT).show();
            }
            rtPath = file.getAbsolutePath();
        } else {
            rtPath = "/data/user/0/" + DBFactory.getPackageName() + "/files/" + DBFactory.getDBName();

        }
        return rtPath;
    }


    public static void copyAssetsFileTo(String fromAssetsFilename, String toPath, String toFileName) throws IOException {

        OutputStream myOutput = new FileOutputStream(toPath + "/" + toFileName);
        byte[] buffer = new byte[1024];
        int length;
        InputStream myInput = DBFactory.getContext().getAssets().open(fromAssetsFilename);
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }
        myInput.close();
        myOutput.flush();
        myOutput.close();

    }

}
