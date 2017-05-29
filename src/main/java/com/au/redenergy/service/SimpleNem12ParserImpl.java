package com.au.redenergy.service;

import com.au.redenergy.exception.IllegalInputFileException;
import com.au.redenergy.model.MeterRead;
import com.au.redenergy.model.MeterVolume;
import com.au.redenergy.validator.Nem12FileValidator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SimpleNem12ParserImpl implements SimpleNem12Parser {
    @Override
    public Collection<MeterRead> parseSimpleNem12(File simpleNem12File) throws IOException, IllegalInputFileException {
        List<String> lines = Files.lines(Paths.get(simpleNem12File.getPath())).collect(Collectors.toList());
        new Nem12FileValidator().isValidNem12File(lines);
        Set<MeterRead> meterReads = new HashSet<>();

        MeterRead meterRead = null;
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            String[] splits = line.split(",");
            switch (splits[0]) {
                case "200":
                    meterRead = MeterRecordFactory.getMeterRead(splits);
                    meterReads.add(meterRead);
                    break;

                case "300":
                    //build MeterVolume records for above meterRead record
                    MeterVolume meterVolume = MeterRecordFactory.getMeterVolume(splits);
                    meterRead.addVolume(splits[1], meterVolume);
                    break;

                case "100":
                case "900":
                    //100 and 900 are markers of start and end of the file. Nothing to do.
                    break;
            }
        }
        return meterReads;
    }
}
