package hotel.dao;

import hotel.entity.Room;
import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RoomManMap implements IRoomMan {
    private Map<String, Room> roomMap = new LinkedHashMap<>();

    public RoomManMap() {
        // 构造器预加载一些房间数据
        roomMap.put("101", new Room("101", 1, 0));
        roomMap.put("102", new Room("102", 1, 1));
        roomMap.put("201", new Room("301", 2, 0));
    }

    @Override
    public boolean addRoom(Room room) {
        if (roomMap.containsKey(room.getRoomNo())) {
            System.out.println(" 添加失败：房号已存在！");
            return false;
        }
        roomMap.put(room.getRoomNo(), room);
        System.out.println(" 添加成功！");
        return true;
    }

    @Override
    public boolean deleteRoom(String roomNo) {
        if (roomMap.remove(roomNo) != null) {
            System.out.println(" 删除成功！");
            return true;
        }
        System.out.println(" 删除失败：房号不存在！");
        return false;
    }

    @Override
    public boolean updateRoom(Room room) {
        if (!roomMap.containsKey(room.getRoomNo())) {
            System.out.println(" 修改失败：房号不存在！");
            return false;
        }
        roomMap.put(room.getRoomNo(), room);
        System.out.println(" 修改成功！");
        return true;
    }

    @Override
    public Room queryRoomByNo(String roomNo) {
        return roomMap.get(roomNo);
    }

    @Override
    public List<Room> queryAllRooms() {
        return new ArrayList<>(roomMap.values());
    }

    @Override
    public void roomsToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter("rooms.txt"))) {
            for (Room r : roomMap.values()) {
                pw.println(r.getRoomNo() + "-" + r.getFloor() + "-" + r.getRoomStatus());
            }
            System.out.println(" 数据已保存到 rooms.txt");
        } catch (IOException e) {
            System.out.println(" 保存失败：" + e.getMessage());
        }
    }

    @Override
    public void roomsFromFile() {
        File file = new File("rooms.txt");
        if (!file.exists()) {
            System.out.println("! 数据文件不存在，跳过读取");
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            roomMap.clear();
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("-");
                if (parts.length == 3) {
                    String roomNo = parts[0];
                    int floor = Integer.parseInt(parts[1]);
                    int status = Integer.parseInt(parts[2]);
                    roomMap.put(roomNo, new Room(roomNo, floor, status));
                }
            }
            System.out.println(" 已从 rooms.txt 读取数据");
        } catch (IOException e) {
            System.out.println(" 读取失败：" + e.getMessage());
        }
    }
}
