package hotel.dao;

import hotel.entity.Room;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomManMySQL implements IRoomMan {

    private static final String URL = "jdbc:mysql://localhost:3306/ke?useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "123456";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("! MySQL 驱动加载失败：" + e.getMessage());
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public RoomManMySQL() {
        // 构造时测试连接
        try (Connection conn = getConnection()) {
            System.out.println(">> MySQL 数据库连接成功");
        } catch (SQLException e) {
            System.out.println("! MySQL 连接失败：" + e.getMessage());
        }
    }

    @Override
    public boolean addRoom(Room room) {
        String sql = "INSERT INTO rooms (room_no, floor, room_status) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, room.getRoomNo());
            ps.setInt(2, room.getFloor());
            ps.setInt(3, room.getRoomStatus());
            ps.executeUpdate();
            System.out.println(" 添加成功！");
            return true;
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println(" 添加失败：房号已存在！");
        } catch (SQLException e) {
            System.out.println(" 添加失败：" + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean deleteRoom(String roomNo) {
        String sql = "DELETE FROM rooms WHERE room_no = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, roomNo);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println(" 删除成功！");
                return true;
            }
            System.out.println(" 删除失败：房号不存在！");
        } catch (SQLException e) {
            System.out.println(" 删除失败：" + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean updateRoom(Room room) {
        String sql = "UPDATE rooms SET floor = ?, room_status = ? WHERE room_no = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, room.getFloor());
            ps.setInt(2, room.getRoomStatus());
            ps.setString(3, room.getRoomNo());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println(" 修改成功！");
                return true;
            }
            System.out.println(" 修改失败：房号不存在！");
        } catch (SQLException e) {
            System.out.println(" 修改失败：" + e.getMessage());
        }
        return false;
    }

    @Override
    public Room queryRoomByNo(String roomNo) {
        String sql = "SELECT * FROM rooms WHERE room_no = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, roomNo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return resultSetToRoom(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println(" 查询失败：" + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Room> queryAllRooms() {
        List<Room> list = new ArrayList<>();
        String sql = "SELECT * FROM rooms ORDER BY room_no";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(resultSetToRoom(rs));
            }
        } catch (SQLException e) {
            System.out.println(" 查询失败：" + e.getMessage());
        }
        return list;
    }

    @Override
    public void roomsToFile() {
        // MySQL 方式下，保存到文件相当于导出到 SQL 脚本
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM rooms ORDER BY room_no");
             PrintWriter pw = new PrintWriter(new FileWriter("rooms_mysql.txt"))) {
            while (rs.next()) {
                pw.println(rs.getString("room_no") + "-"
                         + rs.getInt("floor") + "-"
                         + rs.getInt("room_status"));
            }
            System.out.println(" 数据已导出到 rooms_mysql.txt");
        } catch (SQLException | IOException e) {
            System.out.println(" 导出失败：" + e.getMessage());
        }
    }

    @Override
    public void roomsFromFile() {
        File file = new File("rooms_mysql.txt");
        if (!file.exists()) {
            System.out.println("! 数据文件不存在，跳过读取");
            return;
        }
        String sql = "INSERT IGNORE INTO rooms (room_no, floor, room_status) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             BufferedReader br = new BufferedReader(new FileReader(file))) {
            // 清空原有数据
            try (Statement clear = conn.createStatement()) {
                clear.executeUpdate("TRUNCATE TABLE rooms");
            }
            String line;
            int count = 0;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("-");
                if (parts.length == 3) {
                    ps.setString(1, parts[0]);
                    ps.setInt(2, Integer.parseInt(parts[1]));
                    ps.setInt(3, Integer.parseInt(parts[2]));
                    ps.executeUpdate();
                    count++;
                }
            }
            System.out.println(" 已从 rooms_mysql.txt 导入 " + count + " 条数据");
        } catch (SQLException | IOException e) {
            System.out.println(" 导入失败：" + e.getMessage());
        }
    }

    private Room resultSetToRoom(ResultSet rs) throws SQLException {
        Room room = new Room(rs.getString("room_no"), rs.getInt("floor"), rs.getInt("room_status"));
        room.setRoomId(rs.getInt("room_id"));
        return room;
    }
}
