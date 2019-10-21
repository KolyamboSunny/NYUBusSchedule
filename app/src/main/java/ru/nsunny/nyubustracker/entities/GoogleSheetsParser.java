package ru.nsunny.nyubustracker.entities;

import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import ru.nsunny.nyubustracker.Config;


public class GoogleSheetsParser {

    public GoogleSheetsParser(){
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory factory = JacksonFactory.getDefaultInstance();
    }

    public void populateBusSchedule(List<Route> nyuRoutes) throws Exception{
        for(Route route: nyuRoutes){
            populateBusSchedule(route);
        }
    }
    public void populateBusSchedule(Route nyuRoute) throws Exception{
        // normally sheet names represents day when the schedule is active
        List<String> sheetNames = requestSheetNames(nyuRoute.googleSheetsLink);
        for(String sheetName: sheetNames){
            Schedule retrievedFromSheet = parseScheduleSheet(requestBusTable(nyuRoute,sheetName), nyuRoute.routeName);
            nyuRoute.addScheduleForDay(sheetName,retrievedFromSheet);
        }
    }
    private List<String> requestSheetNames(String routeSheetId) throws InterruptedException,ExecutionException{
        // Create the FutureTask with Callable
        FutureTask<List<String>> request = new FutureTask(new SheetNamesRetriever(routeSheetId));

        // As it implements Runnable, create Thread
        // with FutureTask
        Thread t = new Thread(request);
        t.start();

        return request.get();
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

    private ValueRange requestBusTable(Route route, String sheetname) throws InterruptedException,ExecutionException{
        // Create the FutureTask with Callable
        FutureTask<ValueRange> request = new FutureTask(new SheetContentRetriever(route.googleSheetsLink,sheetname));

        // As it implements Runnable, create Thread
        // with FutureTask
        Thread t = new Thread(request);
        t.start();

        return request.get();
    }

    private static abstract class GoogleRetriever implements Callable {
        private static final HttpTransport transport = AndroidHttp.newCompatibleTransport();
        private static final JsonFactory factory = JacksonFactory.getDefaultInstance();
        protected static final Sheets sheetsService = new Sheets.Builder(transport, factory, null)
                .setApplicationName("NYUBusTracker")
                .build();
    }

    private static class SheetContentRetriever extends GoogleRetriever {
        private final String spreadsheetId;
        private String sheetToRetrieve;

        public SheetContentRetriever(String spreadsheetId, String sheetToRetrieve){
            this.sheetToRetrieve = sheetToRetrieve;
            this.spreadsheetId = spreadsheetId;
        }

        @Override
        public ValueRange call() throws IOException{
            String sheetName = sheetToRetrieve;
            ValueRange result = this.sheetsService.spreadsheets().values()
                    .get(spreadsheetId, sheetToRetrieve)
                    .setKey(Config.google_api_key)
                    .execute();

            int numRows = result.getValues() != null ? result.getValues().size() : 0;
            Log.d("SUCCESS.", "retrieved " + numRows + " rows from sheet " + sheetName);

            return result;
        }
    }
    private static class SheetNamesRetriever extends GoogleRetriever {
        private final String spreadsheetId;

        public SheetNamesRetriever(String spreadsheetId){
            this.spreadsheetId = spreadsheetId;
        }

        @Override
        public List<String> call() throws IOException{
            Spreadsheet sp = sheetsService.spreadsheets().get(spreadsheetId).setKey(Config.google_api_key).execute();
            List<Sheet> sheets = sp.getSheets();

            List<String> sheetNames = new ArrayList<>();
            for (Sheet sheet : sheets){
                // skip hidden sheets: they probably did it for a reason!
                if (sheet.getProperties().getHidden()==null || !sheet.getProperties().getHidden()) {
                    String title = sheet.getProperties().getTitle();
                    sheetNames.add(title);
                }
            }
            return sheetNames;
        }
    }
}
