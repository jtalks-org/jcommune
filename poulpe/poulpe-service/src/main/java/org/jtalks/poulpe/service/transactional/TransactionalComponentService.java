/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jtalks.poulpe.service.transactional;

import java.util.List;
import org.jtalks.poulpe.model.dao.ComponentDao;
import org.jtalks.poulpe.model.entity.Component;
import org.jtalks.poulpe.service.ComponentService;


/**
 *
 * @author Pavel Vervenko
 */
public class TransactionalComponentService extends AbstractTransactionalEntityService<Component, ComponentDao>
        implements ComponentService {

    /**
     * Create new instance of the service.
     * @param dao need it for CRUD operations
     */
    public TransactionalComponentService(ComponentDao dao) {
        this.dao = dao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Component> getAll() {
        return dao.getAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteComponent(Component component) {
        dao.delete(component.getId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveComponent(Component component) {
        dao.saveOrUpdate(component);
    }
}
