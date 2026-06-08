package hotel.entity;

public class Room {
    private int roomId;
    private String roomNo;
    private int floor;
    private int roomStatus; 


    public Room(String roomNo, int floor, int roomStatus) {
        this.roomNo = roomNo;
        this.floor = floor;
        this.roomStatus = roomStatus;
    }

  
    public String getStatusStr(){
        switch (roomStatus){
            case 0: return "空闲";
            case 1: return "入住";
            case 2: return "打扫";
            case 3: return "维修";
            default: return "未知状态";
        }
    }

     public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public int getRoomStatus() {
        return roomStatus;
    }

    public void setRoomStatus(int roomStatus) {
        this.roomStatus = roomStatus;
    }


    @Override
    public String toString() {
        return "客房{" +
                "房间号='" + roomNo + '\'' +
                ", 楼层=" + floor +
                ", 状态=" + getStatusStr() +
                '}';
    }
}