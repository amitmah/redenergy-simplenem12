// Copyright Red Energy Limited 2017

package com.au.redenergy.model;

import org.apache.commons.lang3.StringUtils;

/**
 * Represents the KWH energy unit in SimpleNem12
 */
public enum EnergyUnit {

    KWH;

    public static final boolean isValidEnergyUnit(String energyUnit) {
        for (EnergyUnit unit : EnergyUnit.values()) {
            if (StringUtils.equals(unit.toString(), energyUnit)) {
                return true;
            }
        }
        return false;
    }
}