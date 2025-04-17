package HealiumWithDB;

import java.sql.*;
import java.util.Properties;

public class ElementSnapshotDB {
    private final Connection connection;
    
    public ElementSnapshotDB(String dbUrl, String user, String password) throws SQLException {
        Properties props = new Properties();
        props.setProperty("user", user);
        props.setProperty("password", password);
        this.connection = DriverManager.getConnection(dbUrl, props);
    }
    
    public void saveSnapshot(String elementId, String testName, String locatorType, 
                           String locatorValue, String domState, String attributesJson) throws SQLException {
        String sql = "INSERT INTO element_snapshots " +
                     "(element_id, test_name, original_locator_type, original_locator_value, " +
                     "dom_state, element_attributes) " +
                     "VALUES (?, ?, ?, ?, ?, ?::jsonb) " +
                     "ON CONFLICT (element_id) DO UPDATE SET " +
                     "updated_at = CURRENT_TIMESTAMP";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, elementId);
            stmt.setString(2, testName);
            stmt.setString(3, locatorType);
            stmt.setString(4, locatorValue);
            stmt.setString(5, domState);
            stmt.setString(6, attributesJson);
            stmt.executeUpdate();
        }
    }
    
    public void updateHealedLocator(String elementId, String healedLocatorType, 
                                  String healedLocatorValue) throws SQLException {
        String sql = "UPDATE element_snapshots " +
                     "SET healed_locator_type = ?, healed_locator_value = ?, updated_at = CURRENT_TIMESTAMP " +
                     "WHERE element_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, healedLocatorType);
            stmt.setString(2, healedLocatorValue);
            stmt.setString(3, elementId);
            stmt.executeUpdate();
        }
    }
    
    public ResultSet findSnapshotsByTest(String testName) throws SQLException {
        String sql = "SELECT * FROM element_snapshots WHERE test_name = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, testName);
        return stmt.executeQuery();
    }
    
    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}