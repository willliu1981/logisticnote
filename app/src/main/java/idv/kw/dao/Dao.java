package idv.kw.dao;

import java.util.List;

public interface Dao<T> {

    void add(T t);

    T get(Object id);

    T getBy(String sqlSnippet, String queryType, Object... queryBys);

    List<T> getAll(Object id);


    List<T> getAll();


    List<T> getAllBy(String sqlSnippet, String queryType, Object... queryBys);

    void update(T t, Object id);

    void delete(Object id);
}
