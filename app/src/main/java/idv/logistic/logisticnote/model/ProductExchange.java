package idv.logistic.logisticnote.model;

import java.sql.Timestamp;

public class ProductExchange {

    private Integer id;
    private String no;
    private String ShelfNoFrom;
    private String shelfNoTo;
    private Timestamp setup_date;
    private Timestamp update_date;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getShelfNoFrom() {
        return ShelfNoFrom;
    }

    public void setShelfNoFrom(String shelfNoFrom) {
        ShelfNoFrom = shelfNoFrom;
    }

    public String getShelfNoTo() {
        return shelfNoTo;
    }

    public void setShelfNoTo(String shelfNoTo) {
        this.shelfNoTo = shelfNoTo;
    }

    public Timestamp getSetup_date() {
        return setup_date;
    }

    public void setSetup_date(Timestamp setup_date) {
        this.setup_date = setup_date;
    }

    public Timestamp getUpdate_date() {
        return update_date;
    }

    public void setUpdate_date(Timestamp update_date) {
        this.update_date = update_date;
    }
}
