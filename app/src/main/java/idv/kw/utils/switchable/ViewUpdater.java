package idv.kw.utils.switchable;

public interface ViewUpdater<T> {

    void updateText(T t, boolean isTrue);

    T getData();

}
