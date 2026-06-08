package hotel.dao;

import hotel.entity.Room;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class RoomManArray implements IRoomMan {
    private static final int MAX = 100;
    private int count = 0;
    private Room[] rooms = new Room[MAX];

    @Override
    public boolean addRoom(Room room) {
        if (count >= MAX) {
            System.out.println(" 添加失败：房间数量已达上限！");
            return false;
        }
        if (queryRoomByNo(room.getRoomNo()) != null) {
            System.out.println(" 添加失败：房号已存在！");
            return false;
        }
        rooms[count++] = room;
        System.out.println(" 添加成功！");
        return true;
    }

    @Override
    public boolean deleteRoom(String roomNo) {
        int index = -1;
        for (int i = 0; i < count; i++) {
            if (rooms[i].getRoomNo().equals(roomNo)) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            System.out.println(" 删除失败：房号不存在！");
            return false;
        }
        // 数组前移覆盖
        for (int i = index; i < count - 1; i++) {
            rooms[i] = rooms[i + 1];
        }
        rooms[--count] = null;
        System.out.println(" 删除成功！");
        return true;
    }

    @Override
    public boolean updateRoom(Room room) {
        Room target = queryRoomByNo(room.getRoomNo());
        if (target == null) {
            System.out.println(" 修改失败：房号不存在！");
            return false;
        }
        target.setFloor(room.getFloor());
        target.setRoomStatus(room.getRoomStatus());
        System.out.println(" 修改成功！");
        return true;
    }

    @Override
    public Room queryRoomByNo(String roomNo) {
        for (int i = 0; i < count; i++) {
            if (rooms[i].getRoomNo().equals(roomNo)) {
                return rooms[i];
            }
        }
        return null;
    }

    @Override
    public List<Room> queryAllRooms() {
        List<Room> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            list.add(rooms[i]);
        }
        return list;
    }

    // 文本方式保存到文件（不用序列化）
    @Override
    public void roomsToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter("rooms.txt"))) {
            for (int i = 0; i < count; i++) {
                Room r = rooms[i];
                // 格式：房间号-楼层-状态
                pw.println(r.getRoomNo() + "-" + r.getFloor() + "-" + r.getRoomStatus());
            }
            System.out.println(" 数据已保存到 rooms.txt");
        } catch (IOException e) {
            System.out.println(" 保存失败：" + e.getMessage());
        }
    }

    // 从文本文件读取（不用序列化）
    @Override
    public void roomsFromFile() {
        File file = new File("rooms.txt");
        if (!file.exists()) {
            System.out.println("!  数据文件不存在，跳过读取");
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            count = 0;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("-");
                if (parts.length == 3) {
                    String roomNo = parts[0];
                    int floor = Integer.parseInt(parts[1]);
                    int status = Integer.parseInt(parts[2]);
                    rooms[count++] = new Room(roomNo, floor, status);
                }
            }
            System.out.println(" 已从 rooms.txt 读取数据");
        } catch (IOException e) {
            System.out.println(" 读取失败：" + e.getMessage());
        }
    }
}