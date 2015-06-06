import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.function.IntFunction;
import java.util.stream.IntStream;


public class DateFormatterTest {

    class SimpleDateFormatThreadTest {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        public void exec(int count) {
            IntStream.range(0, count)
            .mapToObj(i -> {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.YEAR, i);
                cal.add(Calendar.MONTH, i);
                cal.add(Calendar.DATE, i);
                cal.add(Calendar.HOUR_OF_DAY, i);
                cal.add(Calendar.MINUTE, i);
                cal.add(Calendar.SECOND, i);
                return cal.getTime();
            })
            .map(d -> d + ":" + sdf.format(d))
            .parallel()
            .forEach(System.out::println);
        }
    }

    class DateTimeFormatterThreadTest {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss");

        public void exec(int count) {
            IntStream.range(0, count)
            .mapToObj(i -> LocalDateTime.now()
				.plusYears(i)
				.plusMonths(i)
				.plusDays(i)
				.plusHours(i)
				.plusMinutes(i)
				.plusSeconds(i)
			)
            .map(d -> d + ":" + df.format(d))
            .parallel()
            .forEach(System.out::println);
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
            .forEach(x -> {});
            end();
        }
        abstract IntFunction<String> run();
    }

    class SimpleDateFormatSpeedTest extends SpeedTest {
        @Override
        IntFunction<String> run() {
            return i -> {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                return sdf.format(new Date());
            };
        }
    }

    class DateTimeFormatterSpeedTest extends SpeedTest {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss");
        @Override
        IntFunction<String> run() {
            return i -> df.format(LocalDateTime.now());
        }
    }

    class StringFormatterSpeedTest extends SpeedTest {
        @Override
        IntFunction<String> run() {
            return i -> String.format("%tF %<tT", new Date());
        }
    }

    public static void main(String[] args) {
        new DateFormatterTest().go();
    }

    public void go() {
        int count = 20;
        new SimpleDateFormatThreadTest().exec(count);
        new DateTimeFormatterThreadTest().exec(count);

        int repeat = 100;
        new SimpleDateFormatSpeedTest().exec(repeat);
        new DateTimeFormatterSpeedTest().exec(repeat);
        new StringFormatterSpeedTest().exec(repeat);
    }
}
