package org.jtalks.poulpe.service.transactional;

import java.util.List;

import org.jtalks.poulpe.model.dao.TopicTypeDao;
import org.jtalks.poulpe.model.entity.TopicType;
import org.jtalks.poulpe.service.TopicTypeService;

public class TransactionalTopicTypeService extends
        AbstractTransactionalEntityService<TopicType, TopicTypeDao> implements TopicTypeService {

    @Override
    public List<TopicType> getAll() {
        return dao.getAll();
    }

    @Override
    public void deleteTopicType(TopicType topicType) {
        dao.delete(topicType.getId());
    }

    @Override
    public void saveTopicType(TopicType topicType) {
        dao.saveOrUpdate(topicType);
    }
}
