package idv.logistic.logisticnote.dao.wage.dao;

import java.util.List;

import idv.logistic.logisticnote.dao.product.LogisticNoteDaoAdapter;
import idv.logistic.logisticnote.dao.wage.model.Wage;

public class WageDao extends LogisticNoteDaoAdapter<Wage> {
    @Override
    public void add(Wage wage) {

    }

    @Override
    public Wage get(Object id) {
        return null;
    }

    @Override
    public Wage getBy(String sqlSnippet, String queryType, Object... queryBys) {
        return null;
    }


    @Override
    public List<Wage> getAllBy(String sqlSnippet, String queryType, Object... queryBys) {
        return null;
    }

    @Override
    public void update(Wage wage, Object id) {

    }

    @Override
    public void delete(Object id) {

    }
}
