package hotel.service;

import hotel.dao.IRoomMan;
import hotel.entity.Room;
import java.util.List;

public class RoomService {
    private IRoomMan roomDao;

    public RoomService(IRoomMan roomDao) {
        this.roomDao=roomDao;
    }

    public boolean addRoom(Room room) {
        return roomDao.addRoom(room);
    }

    public boolean deleteRoom(String roomNo) {
        return roomDao.deleteRoom(roomNo);
    }

    public boolean updateRoom(Room room) {
        return roomDao.updateRoom(room);
    }

    public Room getRoomByNo(String roomNo) {
        return roomDao.queryRoomByNo(roomNo);
    }

    public List<Room> getAllRooms() {
        return roomDao.queryAllRooms();
    }

    // 下面两个是新加的
    public void saveToFile() {
        roomDao.roomsToFile();
    }

    public void loadFromFile() {
        roomDao.roomsFromFile();
    }
}