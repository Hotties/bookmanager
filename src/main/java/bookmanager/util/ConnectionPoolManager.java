package bookmanager.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Properties;

public class ConnectionPoolManager {

    private static HikariDataSource dataSource;

    public static void init() {
        Properties prop = new Properties();
        try (InputStream is = ConnectionPoolManager.class.getClassLoader().getResourceAsStream("propVariables.txt")) {
            if (is == null) {
                // 파일이 없을 경우를 대비한 처리
                throw new RuntimeException("propVariables.txt 파일을 찾을 수 없습니다.");
            }
            prop.load(is);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("프로퍼티 파일 로딩 실패", e);
        }

        HikariConfig config = new HikariConfig();
        // Null 체크를 더 안전하게 수행
        String url = prop.getProperty("url");
        if (url == null) {
            throw new RuntimeException("프로퍼티 파일에 'url' 속성이 정의되지 않았습니다.");
        }
        config.setJdbcUrl(url);
        config.setUsername(prop.getProperty("userName"));
        config.setPassword(prop.getProperty("pwd"));

        config.setMaximumPoolSize(10);
        config.setMinimumIdle(5);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
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
