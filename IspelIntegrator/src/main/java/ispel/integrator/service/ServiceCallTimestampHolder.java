package ispel.integrator.service;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ServiceCallTimestampHolder {

	private static final ThreadLocal<Long> holder = new ThreadLocal<Long>();
	private static final ThreadLocal<DateFormat> format = new ThreadLocal<DateFormat>() {
		protected DateFormat initialValue() {
			DateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return f;
		};
	};
    private static final ThreadLocal<DateFormat> formatYYMMDD = new ThreadLocal<DateFormat>() {
        protected DateFormat initialValue() {
            DateFormat f = new SimpleDateFormat("yyyymmdd");
            return f;
        }

        ;
    };
    private static final ThreadLocal<DateFormat> formatHHMMSS = new ThreadLocal<DateFormat>() {
        protected DateFormat initialValue() {
            DateFormat f = new SimpleDateFormat("HHmmss");
            return f;
        }

        ;
    };

	public static void setTimestamp(long timestamp) {
		holder.set(timestamp);
	}

	public static long getAsLong() {
		Long timestamp = holder.get();
		if (timestamp == null) {
			throw new IllegalStateException("Service call timestamp null");
		} else {
			return timestamp;
		}
	}

    public static String getAsYYMMDD() {
        Long timestamp = holder.get();
        if (timestamp == null) {
            throw new IllegalStateException("Service call timestamp null");
        } else {
            return formatYYMMDD.get().format(new Date(timestamp));
        }
    }

    public static String getAsHHMMSS() {
        Long timestamp = holder.get();
        if (timestamp == null) {
            throw new IllegalStateException("Service call timestamp null");
        } else {
            return formatHHMMSS.get().format(new Date(timestamp));
        }
    }

	public static String getAsDateTime() {
		return format.get().format(new Date(getAsLong()));
	}

	public static Timestamp getAsTimestamp() {
		return new Timestamp(getAsLong());
	}
}
