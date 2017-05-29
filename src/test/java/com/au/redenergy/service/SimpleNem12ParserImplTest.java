package com.au.redenergy.service;

import com.au.redenergy.model.EnergyUnit;
import com.au.redenergy.model.MeterRead;
import com.au.redenergy.model.MeterVolume;
import com.au.redenergy.model.Quality;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.core.Is.is;
public class SimpleNem12ParserImplTest {
    private File file;
    private MeterRead meterRead1;
    private MeterRead meterRead2;
    private Collection<MeterRead> meterReads;
    private MeterVolume meterVolume;
    private MeterVolume meterVolume1;
    private MeterVolume meterVolume2;
    private MeterVolume meterVolume3;
    private MeterVolume meterVolume4;
    private MeterVolume meterVolume5;
    private MeterVolume meterVolume6;
    private MeterVolume meterVolume7;
    private MeterVolume meterVolume8;
    private MeterVolume meterVolume9;
    private MeterVolume meterVolume10;
    private MeterVolume meterVolume11;
    private MeterVolume meterVolume12;

    @Before
    public void init() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        file = new File(classLoader.getResource("SimpleNem12.csv").getFile());

        meterRead1 = new MeterRead("6123456789", EnergyUnit.KWH);
        meterVolume = new MeterVolume(new BigDecimal("-50.8"), Quality.A);
        meterRead1.addVolume("20161113", meterVolume);
        meterVolume1 = new MeterVolume(new BigDecimal("23.96"), Quality.A);
        meterRead1.addVolume("20161114", meterVolume1);
        meterVolume2 = new MeterVolume(new BigDecimal("32.0"), Quality.A);
        meterRead1.addVolume("20161115", meterVolume2);
        meterVolume3 = new MeterVolume(new BigDecimal("-33"), Quality.A);
        meterRead1.addVolume("20161116", meterVolume3);
        meterVolume4 = new MeterVolume(new BigDecimal("0"), Quality.A);
        meterRead1.addVolume("20161117", meterVolume4);
        meterVolume5 = new MeterVolume(new BigDecimal("0"), Quality.E);
        meterRead1.addVolume("20161118", meterVolume5);
        meterVolume6 = new MeterVolume(new BigDecimal("-9"), Quality.A);
        meterRead1.addVolume("20161119", meterVolume6);

        meterRead2 = new MeterRead("6987654321", EnergyUnit.KWH);
        meterVolume7 = new MeterVolume(new BigDecimal("-3.8"), Quality.A);
        meterRead1.addVolume("20161215", meterVolume7);
        meterVolume8 = new MeterVolume(new BigDecimal("0"), Quality.A);
        meterRead1.addVolume("20161216", meterVolume8);
        meterVolume9 = new MeterVolume(new BigDecimal("3.0"), Quality.E);
        meterRead1.addVolume("20161217", meterVolume9);
        meterVolume10 = new MeterVolume(new BigDecimal("-12.8"), Quality.A);
        meterRead1.addVolume("20161218", meterVolume10);
        meterVolume11 = new MeterVolume(new BigDecimal("23.43"), Quality.E);
        meterRead1.addVolume("20161219", meterVolume11);
        meterVolume12 = new MeterVolume(new BigDecimal("4.5"), Quality.A);
        meterRead1.addVolume("20161221", meterVolume12);
        meterReads = new SimpleNem12ParserImpl().parseSimpleNem12(file);
    }

    @Test
    public void shouldReturnTwoMeterReadRecords() throws Exception {
        assertThat(meterReads.size(), is(2));
    }

    @Test
    public void shouldReturnAccurateMeterReads() throws Exception {
        assertThat(meterReads, contains(meterRead1,meterRead2));
    }

    @Test
    public void shouldReturnAccurateMeterVolumes() throws  Exception {
        List<MeterRead> meterReadList = meterReads.stream().collect(Collectors.toList());
        meterReads.stream().forEach(meterRead -> {
            if(StringUtils.equals(meterRead.getNmi(), "6123456789")) {
                Map<LocalDate, MeterVolume> volumeMap = meterRead.getVolumes();
                assertThat(volumeMap.size(), is(7));
                assertThat(volumeMap.keySet(), contains(
                        getLocalDate("20161113"),
                        getLocalDate("20161114"),
                        getLocalDate("20161115"),
                        getLocalDate("20161116"),
                        getLocalDate("20161117"),
                        getLocalDate("20161118"),
                        getLocalDate("20161119")));
                assertThat(meterRead.getVolumes().values(), containsInAnyOrder(meterVolume,
                        meterVolume1, meterVolume2, meterVolume3, meterVolume4, meterVolume5, meterVolume6));
            } else {
                assertThat(meterRead.getVolumes().size(), is(6));
                assertThat(meterRead.getVolumes().keySet(), contains(
                        getLocalDate("20161215"),
                        getLocalDate("20161216"),
                        getLocalDate("20161217"),
                        getLocalDate("20161218"),
                        getLocalDate("20161219"),
                        getLocalDate("20161221")));
                assertThat(meterRead.getVolumes().values(), containsInAnyOrder(
                        meterVolume7, meterVolume8, meterVolume9, meterVolume10,
                        meterVolume11, meterVolume12));
            }
        });

    }
    private LocalDate getLocalDate(String dateString) {
        return LocalDate.parse(dateString, DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

}