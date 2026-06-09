package hotel.dao;

import hotel.entity.Room;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class RoomManList implements IRoomMan {
    private List<Room> roomList = new ArrayList<>();

    public RoomManList() {
        // 构造器可以预加载一些房间数据
        roomList.add(new Room("101", 1, 0));
        roomList.add(new Room("102", 1, 1));
        roomList.add(new Room("201", 2, 0));
    }

    @Override
    public boolean addRoom(Room room) {
        if (queryRoomByNo(room.getRoomNo()) != null) {
            System.out.println(" 添加失败：房号已存在！");
            return false;
        }
        roomList.add(room);
        System.out.println(" 添加成功！");
        return true;
    }

    @Override
    public boolean deleteRoom(String roomNo) {
        Room r = queryRoomByNo(roomNo);
        if (r == null) {
            System.out.println(" 删除失败：房号不存在！");
            return false;
        }
        roomList.remove(r);
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
        for (Room r : roomList) {
            if (r.getRoomNo().equals(roomNo)) {
                return r;
            }
        }
        return null;
    }

    @Override
    public List<Room> queryAllRooms() {
        return new ArrayList<>(roomList);
    }

    @Override
    public void roomsToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter("rooms.txt"))) {
            for (Room r : roomList) {
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
            roomList.clear();
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("-");
                if (parts.length == 3) {
                    String roomNo = parts[0];
                    int floor = Integer.parseInt(parts[1]);
                    int status = Integer.parseInt(parts[2]);
                    roomList.add(new Room(roomNo, floor, status));
                }
            }
            System.out.println(" 已从 rooms.txt 读取数据");
        } catch (IOException e) {
            System.out.println(" 读取失败：" + e.getMessage());
        }
    }
}
