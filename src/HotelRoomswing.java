

import hotel.dao.RoomManArray;
import hotel.dao.RoomManList;
import hotel.dao.RoomManMap;
import hotel.dao.RoomManMySQL;
import hotel.entity.Room;
import hotel.service.RoomService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

/**
 * 酒店客房管理系统 Swing 图形界面
 */
public class HotelRoomswing extends JFrame implements ActionListener {
    // 业务层
    private RoomService roomService;

    // 顶部面板 - 选择存储方式
    private JComboBox<String> cbxStorage;
    private JButton btnInitStorage;

    // 中部输入面板
    private JTextField txtRoomNo, txtFloor, txtStatus;

    // 功能按钮
    private JButton btnAdd, btnDel, btnUpdate, btnQueryOne, btnQueryAll, btnSaveFile, btnLoadFile;

    // 底部文本域 - 展示数据/日志
    private JTextArea taContent;

    public HotelRoomswing() {
        // 1. 窗口基础设置
        setTitle("酒店客房管理系统（Swing版）");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int result = JOptionPane.showConfirmDialog(
                        HotelRoomswing.this,
                        "确定要退出酒店客房管理系统吗？",
                        "退出确认",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );
                if (result == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });
        setLocationRelativeTo(null); // 居中
        setLayout(new BorderLayout(10, 10));

        // 2. 初始化组件
        initTopPanel();
        initInputPanel();
        initBtnPanel();
        initContentArea();

        // 默认使用 Array 存储
        roomService = new RoomService(new RoomManArray());
    }

    // 顶部：选择存储方式
    private void initTopPanel() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        topPanel.setBorder(BorderFactory.createTitledBorder("数据存储方式选择"));

        JLabel lab = new JLabel("存储类型：");
        cbxStorage = new JComboBox<>();
        cbxStorage.addItem("1 - Array 数组");
        cbxStorage.addItem("2 - List 列表");
        cbxStorage.addItem("3 - Map 映射");
        cbxStorage.addItem("4 - MySQL 数据库");
        cbxStorage.setSelectedIndex(0);

        btnInitStorage = new JButton("切换并初始化");
        btnInitStorage.addActionListener(this);

        topPanel.add(lab);
        topPanel.add(cbxStorage);
        topPanel.add(btnInitStorage);
        add(topPanel, BorderLayout.NORTH);
    }

    // 中间：房间信息输入区域
    private void initInputPanel() {
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("房间信息"));
        inputPanel.setPreferredSize(new Dimension(300, 120));

        inputPanel.add(new JLabel("房号："));
        txtRoomNo = new JTextField();
        inputPanel.add(txtRoomNo);

        inputPanel.add(new JLabel("楼层："));
        txtFloor = new JTextField();
        inputPanel.add(txtFloor);

        inputPanel.add(new JLabel("状态(0空闲/1入住/2打扫/3维修)："));
        txtStatus = new JTextField();
        inputPanel.add(txtStatus);

        add(inputPanel, BorderLayout.WEST);
    }

    // 右侧：功能按钮区
    private void initBtnPanel() {
        JPanel btnPanel = new JPanel(new GridLayout(7, 1, 10, 10));
        btnPanel.setBorder(BorderFactory.createTitledBorder("操作功能"));
        btnPanel.setPreferredSize(new Dimension(120, 0));

        btnAdd = new JButton("添加房间");
        btnDel = new JButton("删除房间");
        btnUpdate = new JButton("修改房间");
        btnQueryOne = new JButton("按房号查询");
        btnQueryAll = new JButton("查询全部");
        btnSaveFile = new JButton("保存到文件");
        btnLoadFile = new JButton("从文件读取");

        // 绑定监听
        btnAdd.addActionListener(this);
        btnDel.addActionListener(this);
        btnUpdate.addActionListener(this);
        btnQueryOne.addActionListener(this);
        btnQueryAll.addActionListener(this);
        btnSaveFile.addActionListener(this);
        btnLoadFile.addActionListener(this);

        btnPanel.add(btnAdd);
        btnPanel.add(btnDel);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnQueryOne);
        btnPanel.add(btnQueryAll);
        btnPanel.add(btnSaveFile);
        btnPanel.add(btnLoadFile);

        add(btnPanel, BorderLayout.EAST);
    }

    // 底部/中心：数据展示文本域
    private void initContentArea() {
        taContent = new JTextArea();
        taContent.setEditable(false);
        taContent.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(taContent);
        scrollPane.setBorder(BorderFactory.createTitledBorder("数据展示 & 操作日志"));
        add(scrollPane, BorderLayout.CENTER);
    }

    // 清空输入框
    private void clearInput() {
        txtRoomNo.setText("");
        txtFloor.setText("");
        txtStatus.setText("");
    }

    // 追加日志信息
    private void appendLog(String msg) {
        taContent.append(msg + "\n");
        // 滚动到最后一行
        taContent.setCaretPosition(taContent.getDocument().getLength());
    }

    // 事件监听
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        // 1. 切换存储方式
        if (source == btnInitStorage) {
            int idx = cbxStorage.getSelectedIndex();
            switch (idx) {
                case 0:
                    roomService = new RoomService(new RoomManArray());
                    appendLog("✅ 已切换为：Array 数组存储");
                    break;
                case 1:
                    roomService = new RoomService(new RoomManList());
                    appendLog("✅ 已切换为：List 列表存储");
                    break;
                case 2:
                    roomService = new RoomService(new RoomManMap());
                    appendLog("✅ 已切换为：Map 映射存储");
                    break;
                case 3:
                    roomService = new RoomService(new RoomManMySQL());
                    appendLog("✅ 已切换为：MySQL 数据库存储");
                    break;
            }
            clearInput();
            return;
        }

        String roomNo = txtRoomNo.getText().trim();
        int floor, status;

        // 2. 添加房间
        if (source == btnAdd) {
            try {
                floor = Integer.parseInt(txtFloor.getText().trim());
                status = Integer.parseInt(txtStatus.getText().trim());
                if (roomNo.isEmpty()) {
                    appendLog("❌ 房号不能为空！");
                    return;
                }
                Room room = new Room(roomNo, floor, status);
                boolean ok = roomService.addRoom(room);
                if (ok) {
                    appendLog("✅ 房间【" + roomNo + "】添加成功");
                    clearInput();
                } else {
                    appendLog("❌ 添加失败：房间【" + roomNo + "】已存在！");
                }
            } catch (NumberFormatException ex) {
                appendLog("❌ 楼层、状态必须为数字！");
            }
        }

        // 3. 删除房间
        else if (source == btnDel) {
            if (roomNo.isEmpty()) {
                appendLog("❌ 请输入要删除的房号！");
                return;
            }
            boolean deleted = roomService.deleteRoom(roomNo);
            if (deleted) {
                appendLog("✅ 房间【" + roomNo + "】删除成功");
                clearInput();
            } else {
                appendLog("❌ 删除失败：房间【" + roomNo + "】不存在！");
            }
        }

        // 4. 修改房间
        else if (source == btnUpdate) {
            try {
                floor = Integer.parseInt(txtFloor.getText().trim());
                status = Integer.parseInt(txtStatus.getText().trim());
                if (roomNo.isEmpty()) {
                    appendLog("❌ 房号不能为空！");
                    return;
                }
                Room room = new Room(roomNo, floor, status);
                boolean updated = roomService.updateRoom(room);
                if (updated) {
                    appendLog("✅ 房间【" + roomNo + "】修改完成");
                    clearInput();
                } else {
                    appendLog("❌ 修改失败：房间【" + roomNo + "】不存在！");
                }
            } catch (NumberFormatException ex) {
                appendLog("❌ 楼层、状态必须为数字！");
            }
        }

        // 5. 按房号查询
        else if (source == btnQueryOne) {
            if (roomNo.isEmpty()) {
                appendLog("❌ 请输入查询房号！");
                return;
            }
            Room room = roomService.getRoomByNo(roomNo);
            if (room == null) {
                appendLog("❌ 未查询到房号：" + roomNo);
            } else {
                appendLog("📋 查询结果：" + room);
            }
        }

        // 6. 查询所有房间
        else if (source == btnQueryAll) {
            List<Room> roomList = roomService.getAllRooms();
            appendLog("-------- 全部房间列表 --------");
            if (roomList.isEmpty()) {
                appendLog("暂无房间数据");
            } else {
                for (Room r : roomList) {
                    appendLog(r.toString());
                }
            }
        }

        // 7. 保存到文件
        else if (source == btnSaveFile) {
            roomService.saveToFile();
            appendLog("✅ 数据已保存到 rooms.txt");
        }

        // 8. 从文件读取
        else if (source == btnLoadFile) {
            java.io.File file = new java.io.File("rooms.txt");
            if (!file.exists()) {
                appendLog("❌ 文件 rooms.txt 不存在，请先保存数据！");
            } else {
                roomService.loadFromFile();
                appendLog("✅ 已从 rooms.txt 加载数据");
                // 加载后自动显示全部房间
                List<Room> roomList = roomService.getAllRooms();
                appendLog("-------- 当前房间列表 --------");
                if (roomList.isEmpty()) {
                    appendLog("暂无房间数据");
                } else {
                    for (Room r : roomList) {
                        appendLog(r.toString());
                    }
                }
            }
        }
    }

    // 程序入口（替代原控制台 TestHotel）
    public static void main(String[] args) {
        // Swing 建议在事件线程中启动
        SwingUtilities.invokeLater(() -> {
            new HotelRoomswing().setVisible(true);
        });
    }
}