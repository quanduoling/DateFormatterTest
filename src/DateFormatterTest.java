import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class DateFormatterTest {

    class SimpleDateFormatterThreadTest {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        public void exec(int end) {
            IntStream.range(0, end)
            .mapToObj(i -> {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, i);
                return cal.getTime();
            })
            .parallel()
            .forEach(d -> {
                System.out.println(d + ":" + sdf.format(d));
            });
        }
    }

    class DateTimeFormatterThreadTest {
        DateTimeFormatter df = DateTimeFormatter.ISO_LOCAL_DATE;

        public void exec(int end) {
            IntStream.range(0, end)
            .mapToObj(i -> LocalDateTime.now().plusDays(i))
            .parallel()
            .forEach(d -> {
                System.out.println(d + ":" + df.format(d));
            });
        }
    }

    abstract class SpeedTest {
        LocalDateTime start;
        void start() {
            start = LocalDateTime.now();
        }
        void end() {
            LocalDateTime end = LocalDateTime.now();
            Duration d = Duration.between(start, end);
            System.out.println(getClass().getName() + " time:" + d);
        }
        void exec(int repeat) {
            start();
            IntStream.range(0, repeat)
            .mapToObj(run())
            .collect(Collectors.toList());
            end();
        }
        abstract IntFunction<String> run();
    }

    class SimpleDateFormatterSpeedTest extends SpeedTest {
        @Override
        IntFunction<String> run() {
            return i -> {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                return sdf.format(new Date());
            };
        }
    }

    class DateTimeFormatterSpeedTest extends SpeedTest {
        DateTimeFormatter df = DateTimeFormatter.ISO_LOCAL_DATE;
        @Override
        IntFunction<String> run() {
            return i -> df.format(LocalDateTime.now());
        }
    }

    class StringFormatterSpeedTest extends SpeedTest {
        @Override
        IntFunction<String> run() {
            return i -> String.format("%tF", new Date());
        }
    }

    public static void main(String[] args) {
        new DateFormatterTest().go();
    }

    public void go() {
        int end = 10;
        new SimpleDateFormatterThreadTest().exec(end);
        new DateTimeFormatterThreadTest().exec(end);

        int repeat = 100;
        new SimpleDateFormatterSpeedTest().exec(repeat);
        new DateTimeFormatterSpeedTest().exec(repeat);
        new StringFormatterSpeedTest().exec(repeat);
    }
}
