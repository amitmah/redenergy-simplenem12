// Copyright Red Energy Limited 2017

package com.au.redenergy.model;

import org.apache.commons.lang3.StringUtils;

/**
 * Represents meter read quality in SimpleNem12
 */
public enum Quality {

    A,
    E;

    public static final boolean isValidQuality(String inputQuality) {
        for(Quality quality : Quality.values()) {
            if(StringUtils.equals(quality.toString(), inputQuality)) {
                return true;
            }
        }
        return false;
    }

}
