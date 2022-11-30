package org.acme;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import org.springframework.stereotype.Service;

import javax.enterprise.context.ApplicationScoped;
import javax.print.attribute.standard.PrinterURI;
import javax.swing.plaf.synth.SynthPasswordFieldUI;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;

import io.quarkus.cache.CacheResult;

@Service
@ApplicationScoped
public class Search {
    private final Logger LOG = LoggerFactory.getLogger(Search.class);
    private static final int MAX_BEFORE_WRITING = 15000;
    private static final int MAX_HOLIDAY_LENGTH = 28;
    private LocalDate earliestDepartureDate;
    private LocalDate latestDepartureDate;

    private void SetDates(String earliestDepartureDate, String latestDepartureDate, int holidayDays) {
            this.earliestDepartureDate = LocalDate.parse(earliestDepartureDate);
            this.latestDepartureDate = LocalDate.parse(latestDepartureDate);
    }

    @CacheResult(cacheName = "search")
    public List<Offer> searchHotelsWithOffers(String departureAirport, int countAdults, int countChildren,
            int days, String earliestDepartureDate, String latestDepartureDate) {
        try {
            return readOffersData(departureAirport, countAdults, countChildren,
                    days, earliestDepartureDate, latestDepartureDate);
        } catch (FileNotFoundException e) {
            LOG.error("File not found: " + e.getMessage());
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }
        return new ArrayList<Offer>();
    }

    @CacheResult(cacheName = "offers")
    public List<Offer> readOffersData(String departureAirport, int countAdults, int countChildren,
            int days, String earliestDepartureDate, String latestDepartureDate)
            throws IOException {
        SetDates(earliestDepartureDate, latestDepartureDate, days);
        List<Offer> offers = new ArrayList<Offer>();
        //NEW CODE
        File file = new File("splitted_files/offers_" + days + "days.csv");
        try {
            FileReader reader = new FileReader(file);
            BufferedReader br = new BufferedReader(reader);
            br.readLine();
            String line;
            LOG.info("start");
            while ((line = br.readLine()) != null) {
                List<String> split = Splitter.on(',').trimResults(CharMatcher.is('"')).splitToList(line);
                 if (checkDate(split.get(1)) &&
                        Integer.parseInt(split.get(3)) == countAdults &&
                        Integer.parseInt(split.get(4)) == countChildren &&
                        split.get(10).equals(departureAirport)) {
                    offers.add(new Offer(Integer.parseInt(split.get(0)), split.get(1), split.get(2),
                            Integer.parseInt(split.get(5)), split.get(8), split.get(9), split.get(12), split.get(13),
                            split.get(14), Boolean.parseBoolean(split.get(15)), split.get(16)));
                 }
            }
            br.close();
            LOG.info("end");
            reader.close();

        } catch (FileNotFoundException e) {
            LOG.error("File not found: " + e.getMessage());
        }
        return offers;
    }

    private boolean checkDate(String departureDate) {
            LocalDate date1 = LocalDate.parse(departureDate.substring(0, 10));
            if (date1.isBefore(this.earliestDepartureDate) || date1.isAfter(this.latestDepartureDate)) {
                return false;
            } else {
                return true;
            }
    }

    private int getDaysBetweenDates(String departureDate, String returnDate) {
        LocalDate date1 = LocalDate.parse(departureDate);
        LocalDate date2 = LocalDate.parse(returnDate);
        return (int) ChronoUnit.DAYS.between(date1, date2);
    }

    public int splittingOffersByDays() {
        try {
            File file = FileUtils.toFile(this.getClass().getClassLoader().getResource("offers.csv"));
            String[] fileBuffers = new String[MAX_HOLIDAY_LENGTH];
            Arrays.fill(fileBuffers, "");
            int[] counters = new int[MAX_HOLIDAY_LENGTH];
            Arrays.fill(counters, 0);
        
            FileReader reader = new FileReader(file);
            BufferedReader br = new BufferedReader(reader);
            br.readLine();
            String line;
            LOG.info("Start offers.csv File Splitting");
            while ((line = br.readLine()) != null) {
                List<String> split = Splitter.on(',').trimResults(CharMatcher.is('"')).splitToList(line);
                int index = getDaysBetweenDates(split.get(1).substring(0, 10), split.get(2).substring(0, 10)) - 1;
                if (counters[index] < MAX_BEFORE_WRITING) {
                    fileBuffers[index] += line + "\n";
                    counters[index]++;
                } else {
                    FileWriter writer = new FileWriter("splitted_files\\offers_" + (index + 1) + "days.csv", true);
                    writer.write(fileBuffers[index]);
                    fileBuffers[index] = "";
                    counters[index] = 0;
                    writer.close();
                }
            }
            for (int i : counters){
                if (i != 0) {
                    FileWriter writer = new FileWriter("splitted_files\\offers_" + (i + 1) + "days.csv", true);
                    writer.write(fileBuffers[i]);
                    writer.close();
                }
            }
            br.close();
            reader.close();

            LOG.info("End Offers File Splitting");
            return 1;

        } catch (FileNotFoundException e) {
            LOG.error("File not found: " + e.getMessage());
        } catch (IOException e) {
            LOG.error("IOException: " + e.getMessage());
        }
        return -1;
    }

    public Hotel searchHotel(int id) {
        return parseHotelData().stream()
                .filter(h -> h.getId() == id)
                .findAny()
                .orElse(new Hotel());
    }

    @CacheResult(cacheName = "hotels-data")
    public List<Hotel> parseHotelData() {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("hotels.csv");
        CsvParserSettings settings = new CsvParserSettings();
        BeanListProcessor<Hotel> rowProcessor = new BeanListProcessor<>(Hotel.class);
        settings.setHeaderExtractionEnabled(true);
        settings.setProcessor(rowProcessor);
        settings.selectFields("id", "name", "latitude", "longitude", "category_stars");
        CsvParser parser = new CsvParser(settings);
        parser.parseAllRecords(inputStream);
        return rowProcessor.getBeans();
    }
}
