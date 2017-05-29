package com.au.redenergy.validator;

import com.au.redenergy.exception.IllegalInputFileException;
import com.au.redenergy.model.EnergyUnit;
import com.au.redenergy.model.Quality;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

//This class is used to validate if the file is in correct format
public final class Nem12FileValidator {
    public boolean isValidNem12File(List<String> lines) throws IllegalInputFileException {

        String firstLine = lines.stream()
                .filter(line -> StringUtils.isNotBlank(line))
                .findFirst()
                .orElse(StringUtils.EMPTY);
        String lastLine = lines.stream()
                .filter(line -> StringUtils.isNotBlank(line))
                .reduce((a, b) -> b)
                .orElse(StringUtils.EMPTY);

        //check if the file starts with record type 100 and ends with record type 900
        if (!(StringUtils.equals(firstLine, "100") && StringUtils.equals(lastLine, "900"))){
            throw new IllegalInputFileException("A valid MeterRecords file should start with first line as " +
                    "keyword '100' and last line as keyword: '900'");
        }

        //verify that for each record type 200 there are 1 or more records of type 300 in the following lines
        for (int i = 0; i < lines.size()-1; i++) {
            if(lines.get(i).startsWith("200") && !(lines.get(i+1).startsWith("300"))) {
                throw new IllegalInputFileException("Invalid MeterRead File. Missing MeterVolume Record. \n" +
                        "Every MeterRead record should be followed by one or more MeterVolume Records.  " +
                        "Valid record types are as following: \n" +
                        "\n 1. start of File: 100 \n" +
                        "\n 2. new MeterRead: e.g '200,1023456789,KWH'\n" +
                        "\n 3. new MeterVolume for above meterRead: e.g '300,20170529,-14.05,A'" +
                        "\n 4. end of file: 900 ");
            }
            String line = lines.get(i);
            if(line.startsWith("200")){
                validateMeterReadRecord(line, i);
            }

            if(line.startsWith("300")) {
                validateMeterVolumeRecord(line, i);
            }

        }
        return true;
    }

    private static void validateMeterVolumeRecord(String line, int lineNumber) {
        String[] splits = line.split(",");

        if(splits.length !=4) {
            String errorMessage = String.format("record at line no: %d is invalid. expected length is 4 " +
                            "but was %d. Correct format for a MeterVolume Record is as follows: \n " +
                            "'300,{date in format yyyyMMdd},{MeterVolume -Valid Decimal value.}," +
                            "{reading quality (A or E)}. \n e.g: 300,20170529,-14.05,A",
                            lineNumber, splits.length);
            throw new IllegalInputFileException(errorMessage);
        }
        //validate fields
        boolean isValidDate = true;
        boolean isValidVolume = true;
        boolean isValidQuality = true;
        try {
            LocalDate.parse(splits[1], DateTimeFormatter.ofPattern("yyyyMMdd"));
        } catch (Exception e) {
            isValidDate = false;
        }
        //validate volume
        if(!NumberUtils.isNumber(splits[2])) {
            isValidVolume = false;
        }
        //validate quality
        if(!Quality.isValidQuality(splits[3])) {
            isValidQuality = false;
        }

        //if any of the fields were corrupt, throw exception
        if(!isValidDate || !isValidVolume || !isValidQuality) {
            String errorMessage = String.format("record at line no: %d is invalid. " +
                    "Correct format for a MeterVolume Record is as follows: \n " +
                    "'200,{date in format yyyyMMdd},{MeterVolume -Valid Decimal value.}," +
                    "{reading quality (A or E)}. " +
                    "\n e.g: 300,20170529,-14.05,A",
                    lineNumber);
            throw new IllegalInputFileException(errorMessage);
        }
    }

    private static void validateMeterReadRecord(String line, int lineNumber) {
        String[] splits = line.split(",");
        //validate line length
        if(splits.length != 3) {
            String errorMessage = String.format("record at line no: %d is invalid. expected length " +
                            "for a MeterRead record  is 4 " +
                            "but was %d. Correct format for a MeterRead Record is as follows: \n " +
                            "'200,{NMI (10 characters max length.)},{Energy Unit (KWH)}' " +
                            "\n e.g: 200,1023456789,KWH",
                            lineNumber, splits.length);
            throw new IllegalInputFileException(errorMessage);
        }

        //validate record fields
        boolean isValidNMI = true;
        boolean isValidEnergyUnit = true;
        if(StringUtils.length(splits[1]) != 10) {
            isValidNMI = false;
        }
        if(!EnergyUnit.isValidEnergyUnit(splits[2])) {
            isValidEnergyUnit = false;
        }

        if(!isValidNMI || !isValidEnergyUnit) {
            String errorMessage = String.format("record at line no: %d is invalid. " +
                            "Correct format for a MeterRead Record is as follows: \n" +
                    "'200,{NMI (10 characters max length.)},{Energy Unit (KWH)}' " +
                            "\n e.g: 200,1023456789,KWH",
                    lineNumber);
            throw new IllegalInputFileException(errorMessage);
        }
    }
}
