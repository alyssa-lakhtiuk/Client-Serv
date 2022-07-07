package lab04.entity.daos;

import java.util.List;

public interface IDao<T> {

    T getById(int id);

    List<T> getAll();

    int insert(T t);

    int update(T t, int id);

    int delete(int id);

    void drop();
}
