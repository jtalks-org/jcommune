package org.jtalks.jcommune.model.entity;

import org.apache.commons.lang.RandomStringUtils;
import org.joda.time.DateTimeUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.testng.Assert.assertEquals;

/**
 * @author Dmitry S. Dolzhenko
 */
public class TopicDraftTest {

    private static Validator validator;

    @BeforeMethod
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void validationOfFilledDraftShouldPassSuccessfully() {
        TopicDraft draft = ObjectsFactory.getDefaultTopicDraft();

        Set<ConstraintViolation<TopicDraft>> constraintViolations = validator.validate(draft);

        assertEquals(constraintViolations.size(), 0);
    }

    @Test
    public void validationOfEmptyDraftShouldFail() {
        TopicDraft draft = ObjectsFactory.getDefaultTopicDraft();

        draft.setTitle(null);
        draft.setContent(null);
        draft.setPollTitle(null);
        draft.setPollItemsValue(null);

        Set<ConstraintViolation<TopicDraft>> constraintViolations = validator.validate(draft);

        assertEquals(constraintViolations.size(), 1);
    }

    @Test
    public void validationOfDraftWithoutTitleShouldPassSuccessfully() {
        TopicDraft draft = ObjectsFactory.getDefaultTopicDraft();

        draft.setTitle(null);

        Set<ConstraintViolation<TopicDraft>> constraintViolations = validator.validate(draft);

        assertEquals(constraintViolations.size(), 0);
    }

    @Test
    public void validationOfDraftWithoutContentShouldPassSuccessfully() {
        TopicDraft draft = ObjectsFactory.getDefaultTopicDraft();

        draft.setContent(null);

        Set<ConstraintViolation<TopicDraft>> constraintViolations = validator.validate(draft);

        assertEquals(constraintViolations.size(), 0);
    }

    @Test
    public void validationOfDraftWithoutPollTitleShouldPassSuccessfully() {
        TopicDraft draft = ObjectsFactory.getDefaultTopicDraft();

        draft.setPollTitle(null);

        Set<ConstraintViolation<TopicDraft>> constraintViolations = validator.validate(draft);

        assertEquals(constraintViolations.size(), 0);
    }

    @Test
    public void validationOfDraftWithoutPollItemsValueShouldPassSuccessFull() {
        TopicDraft draft = ObjectsFactory.getDefaultTopicDraft();

        draft.setPollItemsValue(null);

        Set<ConstraintViolation<TopicDraft>> constraintViolations = validator.validate(draft);

        assertEquals(constraintViolations.size(), 0);
    }

    @Test
    public void validationOfDraftWithWrongNumberOfPollItemsShouldFail() {
        TopicDraft draft = ObjectsFactory.getDefaultTopicDraft();

        draft.setPollItemsValue(RandomStringUtils.random(15));

        Set<ConstraintViolation<TopicDraft>> constraintViolations = validator.validate(draft);

        assertEquals(constraintViolations.size(), 1);
    }

    @Test
    public void validationOfDraftWithoutTopicStarterShouldFail() {
        TopicDraft draft = ObjectsFactory.getDefaultTopicDraft();

        draft.setTopicStarter(null);

        Set<ConstraintViolation<TopicDraft>> constraintViolations = validator.validate(draft);

        assertEquals(constraintViolations.size(), 1);
    }

    @Test
    public void validationOfDraftWithoutLastSavedDateShouldFail() {
        TopicDraft draft = ObjectsFactory.getDefaultTopicDraft();

        draft.setLastSaved(null);

        Set<ConstraintViolation<TopicDraft>> constraintViolations = validator.validate(draft);

        assertEquals(constraintViolations.size(), 1);
    }

    @Test
    public void updateLastSavedTimeShouldUpdateLastSavedFiled() throws Exception {
        TopicDraft draft = ObjectsFactory.getDefaultTopicDraft();

        DateTimeUtils.setCurrentMillisFixed(DateTimeUtils.currentTimeMillis() + 1000);

        draft.updateLastSavedTime();

        assertEquals(draft.getLastSaved().getMillis(), DateTimeUtils.currentTimeMillis());

        DateTimeUtils.setCurrentMillisSystem();
    }
}
