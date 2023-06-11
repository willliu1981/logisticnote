package idv.logistic.logisticnote.dao.product;

import java.util.List;

import idv.kw.dao.Dao;
import idv.kw.dao.Query;

public abstract class LogisticNoteDaoAdapter<T> implements Dao<T> {
    @Override
    public List<T> getAll() {
        return getAll(Query.NONE);
    }


    @Override
    public List<T> getAll(Object id) {
        if (id.equals(Query.NONE)) {
            return getAllBy("","", "");
        } else {
            return getAllBy("where id=?", "int", id);
        }


    }
}
