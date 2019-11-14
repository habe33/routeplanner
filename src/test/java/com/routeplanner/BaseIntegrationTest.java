package com.routeplanner;

import com.routeplanner.service.DataService;
import com.routeplanner.utils.StatusName;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.annotation.Resource;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

@SpringBootTest
@ActiveProfiles("test")
public class BaseIntegrationTest {

    @Resource
    private DataService dataService;

    @Resource
    protected TestRepository repository;

    private final static String CSV_PATH = "C:/Temp/airport-codes-test.csv";

    protected void createGraph() {
        writeTestCsv();
        dataService.generateGraph();
        await().atMost(15, TimeUnit.SECONDS).until(isRunning());
    }

    private void writeTestCsv() {
        try (FileWriter csvWriter = new FileWriter(CSV_PATH)) {
            write(csvWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void write(FileWriter csvWriter) throws IOException {
        csvWriter.append("ident,type,name,elevation_ft,continent,iso_country,iso_region,municipality,gps_code,iata_code,local_code,coordinates\n")
                .append("UKOO,large_airport,Odessa International Airport,172,EU,UA,UA-51,Odessa,UKOO,ODS,,\"30.67650032043457, 46.42679977416992\"\n")
                .append("ULLI,large_airport,Pulkovo Airport,78,EU,RU,RU-SPE,St. Petersburg,ULLI,LED,,\"30.262500762939453, 59.80030059814453\"\n")
                .append("UMMS,large_airport,Minsk National Airport,670,EU,BY,BY-MI,Minsk,UMMS,MSQ,,\"28.030700683594, 53.882499694824\"\n")
                .append("UNKL,large_airport,Yemelyanovo Airport,942,EU,RU,RU-KYA,Krasnoyarsk,UNKL,KJA,,\"92.493301, 56.172901\"\n")
                .append("UNNT,large_airport,Tolmachevo Airport,365,AS,RU,RU-NVS,Novosibirsk,UNNT,OVB,,\"82.650703430176, 55.012599945068\"\n");
        csvWriter.flush();
    }

    private Callable<Boolean> isRunning() {
        return () -> AppStatus.getStatus().equals(StatusName.RUNNING.name());
    }
}
