package idv.logistic.logisticnote.dao.product;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import idv.kw.database.connection.DBConnection;
import idv.logistic.logisticnote.model.Shelf;
import idv.logistic.logisticnote.model.Type;

public class ShelfDao extends LogisticNoteDaoAdapter<Shelf> {
    //private Connection conn = DBConnection.getConnection();

    Context context;

    public ShelfDao() {
    }

    public ShelfDao(Context context) {
        this.context = context;
    }

    @Override
    public void add(Shelf shelf) {
        String sql = "insert into shelf (no,type,capacity,update_date) values(?,?,?,?)";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, shelf.getNo());
            stmt.setString(2, shelf.getType().getCode());
            stmt.setInt(3, shelf.getCapacity());
            stmt.setString(4, shelf.getUpdate_date().toString());

            stmt.execute();

            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public Shelf get(Object id) {
        return getBy("where id=?", "int", id);
    }

    public Shelf getByNo(String no) {

        return getBy("where no=?", "string", no);
    }

    @Override
    public Shelf getBy(String sqlSnippet, String queryType, Object... queryBys) {
        String querySql = "select * from shelf " + sqlSnippet;
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(querySql);

            if (!sqlSnippet.equals("")) {
                String[] arrQueryType = queryType.split(",");
                for (int i = 0; i < queryBys.length; i++) {
                    if (arrQueryType[i].equalsIgnoreCase("int")) {
                        stmt.setInt(i + 1, Integer.parseInt(queryBys[i].toString()));
                    } else if (arrQueryType[i].equalsIgnoreCase("string")) {
                        stmt.setString(i + 1, String.valueOf(queryBys[i]));
                    }
                }
            }

            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                Shelf shelf = new Shelf();
                shelf.setId(resultSet.getInt("id"));
                shelf.setNo(resultSet.getString("no"));
                shelf.setType(Type.getType(resultSet.getString("type")));
                shelf.setCapacity(resultSet.getInt("capacity"));
                shelf.setUpdate_date(Timestamp.valueOf(resultSet.getString("update_date")));

                return shelf;
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    @Override
    public List<Shelf> getAllBy(String sqlSnippet, String queryType, Object... queryBys) {
        return null;
    }

    @Override
    public void update(Shelf shelf, Object id) {
        String sql = "update shelf set no=?,type=?,capacity=?,update_date=? where id=?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, shelf.getNo());
            stmt.setString(2, shelf.getType().getCode());
            stmt.setInt(3, shelf.getCapacity());
            stmt.setString(4, shelf.getUpdate_date().toString());
            stmt.setString(5, id.toString());

            stmt.execute();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void delete(Object id) {

    }
}
