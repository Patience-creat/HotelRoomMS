
import hotel.dao.RoomManArray;
import hotel.dao.RoomManList;
import hotel.dao.RoomManMap;
import hotel.dao.RoomManMySQL;
import hotel.entity.Room;
import hotel.service.RoomService;
import java.util.List;
import java.util.Scanner;

public class TestHotel {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // ===== 启动时选择存储方式 =====
        System.out.println("=================================");
        System.out.println("  请选择数据存储方式：");
        System.out.println("  1. Array（数组）");
        System.out.println("  2. List（列表）");
        System.out.println("  3. Map（映射）");
        System.out.println("  4. MySQL（数据库）");
        System.out.println("=================================");
        System.out.print("请输入编号（1-4，默认1）：");

        int storageChoice = 1; // 默认 Array
        try {
            storageChoice = Integer.parseInt(sc.nextLine());
            if (storageChoice < 1 || storageChoice > 4) {
                storageChoice = 1;
                System.out.println("输入无效，使用默认：Array");
            }
        } catch (NumberFormatException e) {
            System.out.println("输入无效，使用默认：Array");
        }

        RoomService roomService;
        switch (storageChoice) {
            case 2:
                roomService = new RoomService(new RoomManList());
                System.out.println(">> 已选择 List 存储方式");
                break;
            case 3:
                roomService = new RoomService(new RoomManMap());
                System.out.println(">> 已选择 Map 存储方式");
                break;
            case 4:
                roomService = new RoomService(new RoomManMySQL());
                System.out.println(">> 已选择 MySQL 存储方式");
                break;
            default:
                roomService = new RoomService(new RoomManArray());
                System.out.println(">> 已选择 Array 存储方式");
        }

        while (true) {
            System.out.println("\n====== 酒店客房管理系统======");
            System.out.println("1. 添加房间");
            System.out.println("2. 删除房间");
            System.out.println("3. 修改房间");
            System.out.println("4. 按房号查询");
            System.out.println("5. 查询所有房间");
            System.out.println("6. 保存到文件");
            System.out.println("7. 从文件读取");
            System.out.println("0. 退出");
            System.out.print("请选择操作：");

            int choice = sc.nextInt();
            sc.nextLine(); // 吃掉换行

            switch (choice) {
                case 1:
                    System.out.print("输入房号：");
                    String no = sc.nextLine();
                    System.out.print("输入楼层：");
                    int floor = sc.nextInt();
                    System.out.print("输入状态(0空闲1入住2打扫3维修)：");
                    int status = sc.nextInt();
                    roomService.addRoom(new Room(no, floor, status));
                    break;

                case 2:
                    System.out.print("输入要删除的房号：");
                    String delNo = sc.nextLine();
                    roomService.deleteRoom(delNo);
                    break;

                case 3:
                    System.out.print("输入要修改的房号：");
                    String upNo = sc.nextLine();
                    System.out.print("输入新楼层：");
                    int upFloor = sc.nextInt();
                    System.out.print("输入新状态：");
                    int upStatus = sc.nextInt();
                    roomService.updateRoom(new Room(upNo, upFloor, upStatus));
                    break;

                case 4:
                    System.out.print("输入房号：");
                    String qNo = sc.nextLine();
                    Room r = roomService.getRoomByNo(qNo);
                    System.out.println(r == null ? " 无此房间" : r);
                    break;

                case 5:
                    List<Room> list = roomService.getAllRooms();
                    if (list.isEmpty()) {
                        System.out.println("暂无房间数据");
                    } else {
                        System.out.println("\n====== 所有房间列表 ======");
                        for (Room room : list) {
                            System.out.println(room);
                        }
                    }
                    break;

                case 6:
                    roomService.saveToFile();
                    break;

                case 7:
                    roomService.loadFromFile();
                    break;

                case 0:
                    System.out.println("系统已退出，谢谢使用！");
                    sc.close();
                    return;

                default:
                    System.out.println(" 输入错误，请重新选择！");
            }
        }
    }
}