package hotel.util;

import java.sql.*;

/**
 * 数据库连接工具类
 *
 * 提供统一的数据库连接管理，集中存放连接参数，
 * 其他 DAO 类通过 DBUtil.getConnection() 获取连接即可。
 *
 * 使用示例：
 *   Connection conn = DBUtil.getConnection();
 *   // ... 执行 SQL ...
 *   DBUtil.closeConnection(conn, stmt, rs);
 */
public class DBUtil {

    // ========================
    //  数据库连接配置
    //  如需修改，直接改下面的常量值
    // ========================
    private static final String DRIVER   = "com.mysql.cj.jdbc.Driver";
    private static final String URL      = "jdbc:mysql://localhost:3306/ke?useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&characterEncoding=utf8";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "123456";

    /**
     * 静态代码块：注册 JDBC 驱动（类加载时执行一次）
     */
    static {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL 驱动加载失败：" + e.getMessage(), e);
        }
    }

    /**
     * 获取数据库连接
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    // ========================
    //  关闭资源（三个重载版本）
    // ========================

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try { conn.close(); } catch (SQLException ignored) {}
        }
    }

    public static void closeConnection(Connection conn, Statement stmt) {
        if (stmt != null) {
            try { stmt.close(); } catch (SQLException ignored) {}
        }
        closeConnection(conn);
    }

    public static void closeConnection(Connection conn, Statement stmt, ResultSet rs) {
        if (rs != null) {
            try { rs.close(); } catch (SQLException ignored) {}
        }
        closeConnection(conn, stmt);
    }

    // ========================
    //  测试入口
    // ========================

    /**
     * 直接运行这个类可以测试数据库能否正常连接
     */
    public static void main(String[] args) {
        System.out.println("===== 测试数据库连接 =====");

        try (Connection conn = getConnection()) {
            System.out.println("✅ 数据库连接成功！");
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT VERSION()")) {
                if (rs.next()) {
                    System.out.println("MySQL 版本：" + rs.getString(1));
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ 连接失败：" + e.getMessage());
            System.err.println("请检查：");
            System.err.println("  1. MySQL 服务是否已启动");
            System.err.println("  2. 数据库地址/用户名/密码是否正确");
            System.err.println("  3. 数据库 ke 是否已创建（执行 resource/room.sql）");
        }
    }
}
