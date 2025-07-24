package bookmanager.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Properties;

public class ConnectionPoolManager {

    private static HikariDataSource dataSource;

    public static void init() {
        FileInputStream fis = null;
        Properties prop = null;
        try {
            fis = new FileInputStream("propVariables.txt");

            prop = new Properties();
            prop.load(fis);
        } catch (Exception e) {
            e.printStackTrace();
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(Objects.requireNonNull(prop).getProperty("url"));
        config.setUsername(Objects.requireNonNull(prop).getProperty("userName"));
        config.setPassword(Objects.requireNonNull(prop).getProperty("pwd"));
        config.setMaximumPoolSize(10); // 최대 풀 사이즈
        config.setMinimumIdle(5); // 최소 유휴 커넥션 수
        config.setConnectionTimeout(30000); // 커넥션 획득 최대 대기 시간 (밀리초)
        config.setIdleTimeout(600000); // 유휴 커넥션 유지 최대 시간 (밀리초)
        config.setMaxLifetime(1800000); // 커넥션 최대 수명 (밀리초)
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        dataSource = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void close(){
        dataSource.close();
    }


}
