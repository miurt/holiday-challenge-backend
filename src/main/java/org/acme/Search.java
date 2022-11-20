package org.acme;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import org.springframework.stereotype.Service;

import javax.enterprise.context.ApplicationScoped;

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
    private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-dd-MM");
    private Date earliestDepartureDate;
    private Date latestDepartureDate;
    private int holidayDays;

    private void SetDates(String earliestDepartureDate, String latestDepartureDate, int holidayDays) {
        try {
            this.earliestDepartureDate = formatter.parse(earliestDepartureDate);
            this.latestDepartureDate = formatter.parse(latestDepartureDate);
        } catch (ParseException e) {
            LOG.error("Parsing Exception" + e.getMessage());
        }
        this.holidayDays = holidayDays;
    }

    @CacheResult(cacheName = "search")
    public List<Offer> searchHotelsWithOffers(String departureAirport, int countAdults, int countChildren,
            int days, String earliestDepartureDate, String latestDepartureDate) {
        try {
            return readOffersData(departureAirport, countAdults, countChildren,
                    days, earliestDepartureDate, latestDepartureDate);
        } catch (FileNotFoundException e) {
            LOG.error("File not found" + e.getMessage());
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
        File file = FileUtils.toFile(this.getClass().getClassLoader().getResource("offers.csv"));
        try {
            FileReader reader = new FileReader(file);
            BufferedReader br = new BufferedReader(reader);
            br.readLine();
            String line;
            LOG.info("start");
            while ((line = br.readLine()) != null) {
                List<String> split = Splitter.on(',').trimResults(CharMatcher.is('"')).splitToList(line);
                 if (checkDates(split.get(1), split.get(2)) &&
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
            LOG.error("File not found" + e.getMessage());
        }
        return offers;
    }

    private boolean checkDates(String departureDate, String returnDate) {
        try {
            Date date1 = formatter.parse(departureDate);
            if (date1.before(this.earliestDepartureDate) || date1.after(this.latestDepartureDate)) {
                return false;
            } else {
                Date date2 = formatter.parse(returnDate);
                if ((int) ((date2.getTime() - date1.getTime()) / (1000 * 60 * 60 * 24)) > this.holidayDays) {
                    return false;
                } else {
                    return true;
                }
            }
        } catch (ParseException e) {
            LOG.error("Parsing Exception" + e.getMessage());
        }
        return false;
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
