package org.jtalks.jcommune.web.validation;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author Eugeny Batov
 */
public class AvatarWeightValidator implements ConstraintValidator<AvatarWeight, MultipartFile> {

    private int MAX_AVATAR_SIZE = 66560;

    @Override
    public void initialize(AvatarWeight avatarWeight) {
        //nothing to do
    }

    /**
     * Check that file's weight no more
     *
     * @param multipartFile mage that user want upload as avatar
     * @return
     */
    @Override
    public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext constraintValidatorContext) {
        if (multipartFile.getOriginalFilename().equals("")) {
            return true;
        }
        return multipartFile.getSize() < MAX_AVATAR_SIZE;
    }
}
