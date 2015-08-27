package org.jtalks.jcommune.test.model

import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic

/**
 * @author Mikhail Stryzhonok
 */
class Section {
    String name = randomAlphabetic(80)
    String description = randomAlphabetic(255)
}
