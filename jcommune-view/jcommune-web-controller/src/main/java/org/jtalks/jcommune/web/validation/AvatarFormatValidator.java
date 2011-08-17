package org.jtalks.jcommune.web.validation;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author Eugeny Batov
 */
public class AvatarFormatValidator implements ConstraintValidator<AvatarFormat, MultipartFile> {
    @Override
    public void initialize(AvatarFormat avatarFormat) {
        //nothing to do
    }

    /**
     * Check that file has extension jpg, png or gif.
     *
     * @param multipartFile image that user want upload as avatar
     * @return true if image has extension jpg, png or gif
     */
    @Override
    public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext constraintValidatorContext) {
        if (multipartFile.getOriginalFilename().equals("")) {
            return true;
        }
        String contentType = multipartFile.getContentType();
        return contentType.equals("image/jpeg") || contentType.equals("image/png") || contentType.equals("image/gif");
    }
}
