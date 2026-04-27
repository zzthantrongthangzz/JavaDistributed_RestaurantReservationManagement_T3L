package connect;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;

public class DBConnect {
    private static Driver driver;

    public static void initConnection() {
        // Thay đổi password "123456" thành mật khẩu Neo4j của bạn
        driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "sapassword"));
    }

    public static Session getSession() {
        if (driver == null) {
            initConnection();
        }
        return driver.session();
    }

    public static void close() {
        if (driver != null) {
            driver.close();
        }
    }
}