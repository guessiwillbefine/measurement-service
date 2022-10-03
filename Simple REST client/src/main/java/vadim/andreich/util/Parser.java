package vadim.andreich.util;

import org.slf4j.event.Level;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    private final Scanner scan;
    private final String SEPARATOR = "  ";

    public enum Logs {
        FULL(0), TIME(1), LOGLEVEL(2), METHOD(3), MESSAGE(4);
        private final int groupId;

        Logs(int groupId) {
            this.groupId = groupId;
        }

        public static List<Logs> parse(List<String> values) {
            List<Logs> logs = new ArrayList<>();
            for (String s : values) {
                try {
                    logs.add(Logs.valueOf(s));
                } catch (IllegalArgumentException exception) {
                    throw exception;
                }
            }
            return logs;
        }
    }

    private Parser(String path) throws FileNotFoundException {
        try {
            FileReader fileReader = new FileReader(path);
            scan = new Scanner(fileReader);
        } catch (FileNotFoundException e) {
            throw e;
        }
    }

    public static Parser createParser(String pathToLogs) throws FileNotFoundException {
        return new Parser(pathToLogs);
    }

    public String getParsedLogs(Level level, List<String> logFilters) {
        return parse(level, Logs.parse(logFilters));
    }
    public String getParsedLogs(Level level, Logs...logFilters) {
        return parse(level, List.of(logFilters));
    }

    private String parse(Level level, List<Logs> logFilters) {
        StringBuilder logs = new StringBuilder();
        while (scan.hasNext()) {
            logs.append(scan.nextLine());
        }
        if (logFilters.size() == 0) return logs.toString();
        Pattern pattern = Pattern.compile(String.format("([\\d+ А-Яа-я\\.:,\\w]+)  (\\[ %s ]) (\\[ METHOD ]: [\\w\\.]+) (\\[ Text : [\\w ,° \\]]+])", level.toString()));
        Matcher matcher = pattern.matcher(logs);
        StringBuilder parsedLogs = new StringBuilder();
        while (matcher.find()) {
            logFilters.forEach(l -> parsedLogs.append(matcher.group(l.groupId)).append(SEPARATOR));
            parsedLogs.append('\n');
        }
        return parsedLogs.toString();
    }
}
