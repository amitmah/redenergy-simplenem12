package com.au.redenergy;

import com.au.redenergy.exception.IllegalInputFileException;
import com.au.redenergy.validator.Nem12FileValidator;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Arrays;
import java.util.List;

public class Nem12FileValidatorTest {
    @Rule
    public final ExpectedException exception = ExpectedException.none();

    private static final List<String> INVALID_EOF = Arrays.asList("100", "200,1023456789,KWH", "300,20170529,-12.34,A", "100");
    private static final List<String> INVALID_SOF = Arrays.asList("200", "200,1023456789,KWH", "300,20170529,-12.34,A", "900");
    private static final List<String> INVALID_NMI = Arrays.asList("100", "200,102ee456789,KWH", "300,20170529,-12.34,A", "900");
    private static final List<String> INVALID_UNIT = Arrays.asList("100", "200,1023456789,AWH", "300,20170529,-12.34,A", "900");
    private static final List<String> INVALID_METERVOLUME_DATE = Arrays.asList("100", "200,1023456789,KWH", "300,2A170529,-12.34,A", "900");
    private static final List<String> INVALID_METERVOLUME_VOLUME = Arrays.asList("100", "200,1023456789,KWH", "300,20170529,-AZ2.34,A", "900");
    private static final List<String> INVALID_METERVOLUME_QUALITY = Arrays.asList("100", "200,1023456789,KWH", "300,20170529,-12.34,P", "900");
    private static final List<String> MISSING_METERVOLUME= Arrays.asList("100", "200,1023456789,KWH", "900");
    private static final List<String> VALID_METERREAD_FILE= Arrays.asList(
            "100",
            "200,1023456789,KWH",
            "300,20170529,-12.34,A",
            "300,20170528,312.34,A",
            "200,1123456789,KWH",
            "300,20170529,-12.34,A",
            "300,20170528,412.34,A",
            "900");



    @Test
    public void shouldThrowIllegalFileExceptionWhenInvalidStartOfFile() throws IllegalInputFileException {
        String cause = "A valid MeterRecords file should start with first line as keyword '100' and last line as keyword: '900'";
        exception.expectMessage(cause);
        new Nem12FileValidator().isValidNem12File(INVALID_SOF);
    }

    @Test
    public void shouldThrowIllegalFileExceptionWhenInvalidEndOfFile() throws IllegalInputFileException {
        String cause = "A valid MeterRecords file should start with first line as keyword '100' and last line as keyword: '900'";
        exception.expectMessage(cause);
        new Nem12FileValidator().isValidNem12File(INVALID_EOF);
    }
    //
    @Test
    public void shouldThrowIllegalFileExceptionWhenInvalidNMIProvided() throws IllegalInputFileException {
        String cause = "record at line no: 1 is invalid. Correct format for a MeterRead Record is as follows: \n" +
                "'200,{NMI (10 characters max length.)},{Energy Unit (KWH)}' \n" +
                " e.g: 200,1023456789,KWH";
        exception.expectMessage(cause);
        new Nem12FileValidator().isValidNem12File(INVALID_NMI);
    }
    @Test
    public void shouldThrowIllegalFileExceptionWhenInvalidEnergyUnitProvided() throws IllegalInputFileException {
        String cause = "record at line no: 1 is invalid. Correct format for a MeterRead Record is as follows: \n" +
                "'200,{NMI (10 characters max length.)},{Energy Unit (KWH)}' \n" +
                " e.g: 200,1023456789,KWH";
        exception.expectMessage(cause);
        new Nem12FileValidator().isValidNem12File(INVALID_UNIT);
    }
    @Test
    public void shouldThrowIllegalFileExceptionWhenInvalidMeterVolumeDateProvided() throws IllegalInputFileException {
        String cause = "record at line no: 2 is invalid. Correct format for a MeterVolume Record is as follows: \n" +
                " '200,{date in format yyyyMMdd},{MeterVolume -Valid Decimal value.},{reading quality (A or E)}. \n" +
                " e.g: 300,20170529,-14.05,A";
        exception.expectMessage(cause);
        new Nem12FileValidator().isValidNem12File(INVALID_METERVOLUME_DATE);
    }
    @Test
    public void shouldThrowIllegalFileExceptionWhenInvalidMeterVolumeProvided() throws IllegalInputFileException {
        String cause = "record at line no: 2 is invalid. Correct format for a MeterVolume Record is as follows: \n" +
                " '200,{date in format yyyyMMdd},{MeterVolume -Valid Decimal value.},{reading quality (A or E)}";
        exception.expectMessage(cause);
        new Nem12FileValidator().isValidNem12File(INVALID_METERVOLUME_VOLUME);
    }
    @Test
    public void shouldThrowIllegalFileExceptionWhenInvalidMeterVolumeQualityProvided() throws IllegalInputFileException {
        String cause = "record at line no: 2 is invalid. Correct format for a MeterVolume Record is as follows: \n" +
                " '200,{date in format yyyyMMdd},{MeterVolume -Valid Decimal value.},{reading quality (A or E)}";
        exception.expectMessage(cause);
        new Nem12FileValidator().isValidNem12File(INVALID_METERVOLUME_QUALITY);
    }
    @Test
    public void shouldThrowIllegalFileExceptionWhenNoMeterVolumeProvided() throws IllegalInputFileException {
        String cause = "Every MeterRead record should be followed by one or more MeterVolume Records.  Valid record types are as following: \n" +
                "\n" +
                " 1. start of File: 100 \n" +
                "\n" +
                " 2. new MeterRead: e.g '200,1023456789,KWH'\n" +
                "\n" +
                " 3. new MeterVolume for above meterRead: e.g '300,20170529,-14.05,A'\n" +
                " 4. end of file: 900 ";
        exception.expectMessage(cause);
        new Nem12FileValidator().isValidNem12File(MISSING_METERVOLUME);
    }
    @Test
    public void shouldNotThrowExceptionWhenValidFileProvided() throws IllegalInputFileException {
        new Nem12FileValidator().isValidNem12File(VALID_METERREAD_FILE);
    }
}