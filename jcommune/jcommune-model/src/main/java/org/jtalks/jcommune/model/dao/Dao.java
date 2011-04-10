package org.jtalks.jcommune.model.dao;

import java.util.List;
import org.jtalks.jcommune.model.entity.Persistent;

/**
 *
 * @author Temdegon
 */
public interface Dao<T extends Persistent> {

    void saveOrUpdate(T persistent);

    void delete(Long id);

    void delete(T persistent);

    T get(Long id);

    List<T> getAll();
}
