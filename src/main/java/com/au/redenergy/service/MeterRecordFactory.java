package com.au.redenergy.service;

import com.au.redenergy.model.EnergyUnit;
import com.au.redenergy.model.MeterRead;
import com.au.redenergy.model.MeterVolume;
import com.au.redenergy.model.Quality;

import java.math.BigDecimal;

public final class MeterRecordFactory {

    public static final MeterRead getMeterRead(String[] splits) {
        String nmi = splits[1];
        EnergyUnit energyUnit = EnergyUnit.valueOf(splits[2]);
        return new MeterRead(nmi, energyUnit);
    }

    public  static final MeterVolume getMeterVolume(String[] splits) {
        BigDecimal volume = new BigDecimal(splits[2]);
        Quality quality = Quality.valueOf(splits[3]);
        return new MeterVolume(volume, quality);
    }

}
