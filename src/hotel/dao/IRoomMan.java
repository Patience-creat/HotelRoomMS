package hotel.dao;

import java.util.List;
import hotel.entity.Room;

public interface IRoomMan {
    boolean addRoom(Room room);
    boolean deleteRoom(String roomNo);
    boolean updateRoom(Room room);
    Room queryRoomByNo(String roomNo);
    List<Room> queryAllRooms();
    void roomsToFile();
    void roomsFromFile();
}