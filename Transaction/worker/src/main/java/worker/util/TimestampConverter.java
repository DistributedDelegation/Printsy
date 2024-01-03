package worker.util;

import com.google.protobuf.Timestamp;

import java.util.Date;

public class TimestampConverter {

    private static Timestamp toProtoTimestamp(Date date) {
        long millis = date.getTime();
        return Timestamp.newBuilder()
                .setSeconds(millis / 1000)
                .setNanos((int) ((millis % 1000) * 1000000))
                .build();
    }

    public static Date fromProtoTimestamp(Timestamp timestamp) {
        long millis = timestamp.getSeconds() * 1000 + timestamp.getNanos() / 1000000;
        return new Date(millis);
    }
}
