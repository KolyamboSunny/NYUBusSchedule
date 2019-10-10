package ru.nsunny.nyubustracker.entities;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;

import java.io.IOException;
import java.util.concurrent.*;
import java.util.*;

import android.util.Log;

import ru.nsunny.nyubustracker.Config;


public class GoogleSheetsParser {
    private Sheets sheetsService;

    public GoogleSheetsParser(){
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory factory = JacksonFactory.getDefaultInstance();
        sheetsService = new Sheets.Builder(transport, factory, null)
                .setApplicationName("NYUBusTracker")
                .build();
    }

    public List<Schedule> parseBusSchedule(){
        String sheet_mn_th = "Mon-Thurs";
        String sheet_fri = "Fri";
        String sheet_wk = "Weekend";

        List<Schedule> result = new ArrayList<Schedule>();
        try {
            result.add(parseScheduleSheet(requestBusTable(sheet_mn_th),"RouteA_"+sheet_mn_th));
            result.add(parseScheduleSheet(requestBusTable(sheet_fri),"RouteA_"+sheet_fri));
            result.add(parseScheduleSheet(requestBusTable(sheet_wk),"RouteA_"+sheet_wk));

        }catch (Exception e){
            Log.d("FAILED.","Could not retrieve sheet from Google Sheets",e);
        }
        return result;
    }

    private Schedule parseScheduleSheet(ValueRange sheet,String routeName){
        //retrieve range values and convert them to strings
        List<List<Object>> rawValues = sheet.getValues();
        List<List<String>> strings = new ArrayList<>();
        for (List<Object> rawRow : rawValues) {
            List<String> stringRow = new ArrayList<>(rawRow.size());
            for (Object object : rawRow)
                stringRow.add(Objects.toString(object, null));
            strings.add(stringRow);
        }

        //get and remove line with addresses from schedule
        List<String> addresses = strings.get(0);
        strings.remove(0);

        //parse the remaining schedule as time values
        List<List<ScheduleTime>> times = new ArrayList<List<ScheduleTime>>();
        for(List<String> stringRow: strings){
            List<ScheduleTime> timeRow = new ArrayList<ScheduleTime>();
            for(String rawTime : stringRow)
                timeRow.add(new ScheduleTime(rawTime));
            times.add(timeRow);
        }

        Schedule schedule = new Schedule(routeName,addresses,times);
        return schedule;
    }

    private ValueRange requestBusTable(String sheetname) throws InterruptedException,ExecutionException{
        // Create the FutureTask with Callable
        FutureTask<ValueRange> request = new FutureTask(new GoogleSheetsRetriever(sheetname));

        // As it implements Runnable, create Thread
        // with FutureTask
        Thread t = new Thread(request);
        t.start();

        return request.get();
    }

    private static class GoogleSheetsRetriever implements Callable {
        private static final HttpTransport transport = AndroidHttp.newCompatibleTransport();
        private static final JsonFactory factory = JacksonFactory.getDefaultInstance();
        private static final Sheets sheetsService = new Sheets.Builder(transport, factory, null)
                .setApplicationName("NYUBusTracker")
                .build();

        private String sheetToRetrieve;
        public GoogleSheetsRetriever(String sheetToRetrieve){
            this.sheetToRetrieve = sheetToRetrieve;
        }
        @Override
        public ValueRange call() throws IOException{
            String sheetName = sheetToRetrieve;
            ValueRange result = sheetsService.spreadsheets().values()
                    .get(Config.spreadsheet_id, sheetToRetrieve)
                    .setKey(Config.google_api_key)
                    .execute();

            int numRows = result.getValues() != null ? result.getValues().size() : 0;
            Log.d("SUCCESS.", "retrieved " + numRows + " rows from sheet " + sheetName);

            return result;
        }
    }
}
