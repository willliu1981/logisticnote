package idv.logistic.logisticnote.database;

import java.util.ArrayList;
import java.util.List;

public class ResultTank<T> {


    public ResultTank() {
    }

    public ResultTank(List<T> list) {
        this.list = list;
    }

    List<T> list = new ArrayList<>();

    public T getLatest() {
        return list.get(list.size() - 1);
    }

    public void setTank(List<T> list) {
        this.list = list;
    }

    public boolean isEmpty() {
        if (this.list == null || this.list.isEmpty()) {
            return true;
        } else {
            return false;
        }

    }

    @Override
    public String toString() {
        return "ResultTank{" +
                "list size=" + list.size() +
                '}';
    }
}
