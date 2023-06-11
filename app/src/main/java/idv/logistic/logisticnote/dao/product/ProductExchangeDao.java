package idv.logistic.logisticnote.dao.product;

import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import idv.logistic.logisticnote.database.ResultTank;
import idv.kw.database.connection.DBConnection;
import idv.logistic.logisticnote.model.ProductExchange;

public class ProductExchangeDao extends LogisticNoteDaoAdapter<ProductExchange> {

    private Connection conn = DBConnection.getConnection();

    @Override
    public void add(ProductExchange productExchange) {
        String sql = "insert into product_exchange (no,shelf_no_from,shelf_no_to,setup_date,update_date) values(?,?,?,?,?)";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, productExchange.getNo());
            stmt.setString(2, productExchange.getShelfNoFrom());
            stmt.setString(3, productExchange.getShelfNoTo());
            stmt.setString(4, productExchange.getSetup_date().toString());
            stmt.setString(5, productExchange.getUpdate_date().toString());

            stmt.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ProductExchange getByNo(String no) {

        return getBy("where no=?", "string", no);
    }

    @Override
    public ProductExchange get(Object id) {


        return getBy("where id=?", "int", id);
    }


    @Override
    public ProductExchange getBy(String sqlSnippet, String queryType, Object... queryBys) {
        String querySql = "select * from product_exchange " + sqlSnippet;
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
                ProductExchange productExchange = new ProductExchange();
                productExchange.setId(resultSet.getInt("id"));
                productExchange.setNo(resultSet.getString("no"));
                productExchange.setShelfNoFrom(resultSet.getString("shelf_no_from"));
                productExchange.setShelfNoTo(resultSet.getString("shelf_no_to"));
                productExchange.setSetup_date(Timestamp.valueOf(resultSet.getString("setup_date")));
                productExchange.setUpdate_date(Timestamp.valueOf(resultSet.getString("update_date")));

                return productExchange;
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public ResultTank<ProductExchange> getTankByNo(Object no) {
        List<ProductExchange> list = getAllByNo(no);
        ResultTank<ProductExchange> tank = new ResultTank<>(list);
        return tank;
    }

    public ResultTank<ProductExchange> getTankByID(Object id) {
        List<ProductExchange> list = getAll(id);
        ResultTank<ProductExchange> tank = new ResultTank<>(list);
        return tank;
    }

    public List<ProductExchange> getAllByNo(Object no) {
        return getAllBy("where no=?", "string", no);
    }


    @Override
    public List<ProductExchange> getAllBy(String sqlSnippet, String queryType, Object... queryBys) {

        String querySql = "select * from product_exchange " + sqlSnippet;
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
            List<ProductExchange> list = new ArrayList<>();
            while (resultSet.next()) {
                ProductExchange productExchange = new ProductExchange();
                productExchange.setId(resultSet.getInt("id"));
                productExchange.setNo(resultSet.getString("no"));
                productExchange.setShelfNoFrom(resultSet.getString("shelf_no_from"));
                productExchange.setShelfNoTo(resultSet.getString("shelf_no_to"));
                productExchange.setSetup_date(Timestamp.valueOf(resultSet.getString("setup_date")));
                productExchange.setUpdate_date(Timestamp.valueOf(resultSet.getString("update_date")));

                list.add(productExchange);
            }
            if (!list.isEmpty()) {
                return list;
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void update(ProductExchange product, Object id) {
        String querySql = "update product_exchange set shelf_no_from=?,shelf_no_to=? where id=?";

        Connection conn = DBConnection.getConnection();
        try {
            PreparedStatement stmt = conn.prepareStatement(querySql);
            stmt.setString(1, product.getShelfNoFrom());
            stmt.setString(2, product.getShelfNoTo());
            stmt.setInt(3, Integer.parseInt(id.toString()));
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Object id) {
        String querySql = "delete from product_exchange where id=?";

        Connection conn = DBConnection.getConnection();
        try {
            PreparedStatement stmt = conn.prepareStatement(querySql);
            stmt.setInt(1, Integer.parseInt(id.toString()));
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
