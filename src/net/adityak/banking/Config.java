package net.adityak.banking;

public class Config {
    public static final String VERSION_NAME = "1.0";

    public static final String MYSQL_URL = "jdbc:mysql://localhost/payment_services";
    public static final String MYSQL_USERNAME = "wepay";
    public static final String MYSQL_PASSWORD = "wepay@123";

    public static final int RMI_PORT = 5084;
    public static final String RMI_NAME = "wepay";
    public static final String KAFKA_SERVER = "172.25.90.226:9092";
}
