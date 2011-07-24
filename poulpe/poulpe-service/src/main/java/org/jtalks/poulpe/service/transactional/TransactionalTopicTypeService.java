package org.jtalks.poulpe.service.transactional;

import java.util.Collection;
import java.util.List;

import org.jtalks.poulpe.model.dao.TopicTypeDao;
import org.jtalks.poulpe.model.entity.TopicType;
import org.jtalks.poulpe.service.TopicTypeService;
import org.jtalks.poulpe.service.exceptions.NotUniqueException;

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
    public void saveTopicType(TopicType topicType) throws NotUniqueException {
        String title = topicType.getTitle(); 
        if (title == null || title.isEmpty()) {
            throw new IllegalArgumentException();
        }
        
        if (dao.isTopicTypeNameExists(title)) {
            throw new NotUniqueException("Not unique title of topic type: " + title);
        }
        
        dao.saveOrUpdate(topicType);
    }

    @Override
    public boolean isTopicTypeNameExists(String topicTypeName) {
        return dao.isTopicTypeNameExists(topicTypeName);
    }

    @Override
    public void deleteTopicTypes(Collection<TopicType> topicTypes) {
        for (TopicType topicType: topicTypes) {
            deleteTopicType(topicType);
        }
    }
    
}
