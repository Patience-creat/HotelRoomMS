

import hotel.dao.RoomManArray;
import hotel.dao.RoomManList;
import hotel.dao.RoomManMap;
import hotel.dao.RoomManMySQL;
import hotel.entity.Room;
import hotel.service.RoomService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

/**
 * 酒店客房管理系统 —— Swing 图形界面
 * <p>
 * 启动时会弹出对话框选择数据存储方式（Array / List / Map / MySQL），
 * 之后进入主界面进行客房增删改查等操作。
 */
public class HotelRoomswing extends JFrame {

    // ======================== 组件 ========================

    private RoomService roomService;

    private final JTable roomTable = new JTable();
    private final DefaultTableModel tableModel;

    private final JLabel statusLabel = new JLabel("就绪");
    private final JLabel countLabel  = new JLabel("共 0 条记录");

    private final JTextField queryField = new JTextField(12);

    // 状态选项（与 Room.getStatusStr() 保持一致）
    private static final String[] STATUS_NAMES = {"空闲", "入住", "打扫", "维修"};

    // ======================== 构造 ========================

    public HotelRoomswing() {
        super("酒店客房管理系统");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 620);
        setLocationRelativeTo(null);

        // ---- 表格模型 ----
        tableModel = new DefaultTableModel(
                new Object[]{"房间号", "楼层", "状态"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 表格不可直接编辑
            }
        };
        roomTable.setModel(tableModel);
        roomTable.setRowHeight(28);
        java.awt.Font headerFont = roomTable.getTableHeader().getFont();
        if (headerFont == null) {
            headerFont = UIManager.getFont("TableHeader.font");
        }
        if (headerFont != null) {
            roomTable.getTableHeader().setFont(headerFont.deriveFont(Font.BOLD));
        }
        roomTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // ---- 组装界面 ----
        initUI();

        // ---- 窗口关闭前询问 ----
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int opt = JOptionPane.showConfirmDialog(
                        HotelRoomswing.this, "确定要退出系统吗？", "退出确认",
                        JOptionPane.YES_NO_OPTION);
                if (opt == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });
    }

    // ======================== 界面组装 ========================

    private void initUI() {
        // 主面板采用 BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(6, 6));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // ----- 顶部：操作工具栏 -----
        mainPanel.add(createToolBar(), BorderLayout.NORTH);

        // ----- 中间：房间表格（带滚动条） -----
        JScrollPane scrollPane = new JScrollPane(roomTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // ----- 底部：查询 & 状态栏 -----
        mainPanel.add(createBottomBar(), BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    /** 顶部工具栏 */
    private JToolBar createToolBar() {
        JToolBar bar = new JToolBar();
        bar.setFloatable(false);
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
                new EmptyBorder(4, 4, 4, 4)
        ));

        bar.add(createButton("添加房间", e -> addRoom()));
        bar.add(createButton("修改房间", e -> editRoom()));
        bar.add(createButton("删除房间", e -> deleteRoom()));
        bar.addSeparator(new Dimension(10, 0));

        bar.add(createButton("查询房间", e -> queryRoom()));
        bar.add(createButton("刷新列表", e -> refreshTable()));
        bar.addSeparator(new Dimension(10, 0));

        bar.add(createButton("保存文件", e -> saveToFile()));
        bar.add(createButton("读取文件", e -> loadFromFile()));

        return bar;
    }

    /** 底部栏：查询框 + 统计信息 */
    private JPanel createBottomBar() {
        JPanel panel = new JPanel(new BorderLayout(8, 0));
        panel.setBorder(new EmptyBorder(6, 2, 2, 2));

        // 左：查询
        JPanel queryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        queryPanel.add(new JLabel("房号:"));
        queryField.addActionListener(e -> queryRoom());
        queryPanel.add(queryField);
        JButton qBtn = new JButton("查询");
        qBtn.addActionListener(e -> queryRoom());
        queryPanel.add(qBtn);
        panel.add(queryPanel, BorderLayout.WEST);

        // 中：状态提示
        statusLabel.setForeground(new Color(0x33, 0x66, 0x99));
        panel.add(statusLabel, BorderLayout.CENTER);

        // 右：记录数
        countLabel.setForeground(Color.GRAY);
        panel.add(countLabel, BorderLayout.EAST);

        return panel;
    }

    /** 快捷创建按钮 */
    private JButton createButton(String text, java.awt.event.ActionListener listener) {
        JButton btn = new JButton(text);
        btn.addActionListener(listener);
        return btn;
    }

    // ======================== 存储方式选择对话框 ========================

    /** 显示启动选择对话框（模态），返回 true 表示已成功初始化 */
    public boolean chooseStorageAndInit() {
        String[] options = {"Array（数组）", "List（列表）", "Map（映射）", "MySQL（数据库）"};
        int choice = JOptionPane.showOptionDialog(
                this,
                "请选择数据存储方式：",
                "系统启动",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        switch (choice) {
            case 1:
                roomService = new RoomService(new RoomManList());
                setStatus("已选择 List 存储方式");
                break;
            case 2:
                roomService = new RoomService(new RoomManMap());
                setStatus("已选择 Map 存储方式");
                break;
            case 3:
                roomService = new RoomService(new RoomManMySQL());
                setStatus("已选择 MySQL 存储方式");
                break;
            default:
                roomService = new RoomService(new RoomManArray());
                setStatus("已选择 Array 存储方式");
        }

        // 加载数据到表格
        refreshTable();
        return true;
    }

    // ======================== CRUD 操作 ========================

    /** 添加房间 */
    private void addRoom() {
        RoomDialog dialog = new RoomDialog(this, "添加房间", null);
        dialog.setVisible(true);

        Room room = dialog.getRoom();
        if (room != null) {
            boolean ok = roomService.addRoom(room);
            if (ok) {
                setStatus("添加房间 " + room.getRoomNo() + " 成功");
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(this, "添加失败，房号可能已存在！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /** 修改房间（需要先选中一行） */
    private void editRoom() {
        int row = roomTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "请先在表格中选择要修改的房间！", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String roomNo = (String) tableModel.getValueAt(row, 0);
        Room old = roomService.getRoomByNo(roomNo);
        if (old == null) {
            JOptionPane.showMessageDialog(this, "该房间不存在或已被删除！", "错误", JOptionPane.ERROR_MESSAGE);
            refreshTable();
            return;
        }

        RoomDialog dialog = new RoomDialog(this, "修改房间", old);
        dialog.setVisible(true);

        Room updated = dialog.getRoom();
        if (updated != null) {
            boolean ok = roomService.updateRoom(updated);
            if (ok) {
                setStatus("修改房间 " + roomNo + " 成功");
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(this, "修改失败，房号不存在！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /** 删除房间 */
    private void deleteRoom() {
        int row = roomTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "请先在表格中选择要删除的房间！", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String roomNo = (String) tableModel.getValueAt(row, 0);

        int confirm = JOptionPane.showConfirmDialog(this,
                "确定要删除房间 " + roomNo + " 吗？", "删除确认",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        boolean ok = roomService.deleteRoom(roomNo);
        if (ok) {
            setStatus("删除房间 " + roomNo + " 成功");
            refreshTable();
        } else {
            JOptionPane.showMessageDialog(this, "删除失败，房号不存在！", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** 查询房间（按房号） */
    private void queryRoom() {
        String roomNo = queryField.getText().trim();
        if (roomNo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入房号！", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Room room = roomService.getRoomByNo(roomNo);
        if (room == null) {
            JOptionPane.showMessageDialog(this, "未找到房间 " + roomNo, "查询结果", JOptionPane.INFORMATION_MESSAGE);
            setStatus("未找到房间 " + roomNo);
            return;
        }

        // 在表格中高亮该行
        setStatus("找到房间 " + room);
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if (roomNo.equals(tableModel.getValueAt(i, 0))) {
                roomTable.setRowSelectionInterval(i, i);
                roomTable.scrollRectToVisible(roomTable.getCellRect(i, 0, true));
                return;
            }
        }
    }

    /** 刷新表格（从 Service 重新加载全部数据） */
    private void refreshTable() {
        // 清空
        tableModel.setRowCount(0);

        List<Room> list = roomService.getAllRooms();
        if (list != null) {
            for (Room r : list) {
                tableModel.addRow(new Object[]{
                        r.getRoomNo(),
                        r.getFloor(),
                        r.getStatusStr()
                });
            }
        }
        countLabel.setText("共 " + tableModel.getRowCount() + " 条记录");
        setStatus("列表已刷新");
    }

    // ======================== 文件操作 ========================

    private void saveToFile() {
        roomService.saveToFile();
        setStatus("数据已保存到文件");
    }

    private void loadFromFile() {
        roomService.loadFromFile();
        refreshTable();
        setStatus("已从文件读取数据");
    }

    // ======================== 状态栏 ========================

    private void setStatus(String msg) {
        statusLabel.setText(msg);
    }

    // ======================== 启动入口 ========================

    public static void main(String[] args) {
        // 设置系统外观
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // 使用默认外观
        }

        SwingUtilities.invokeLater(() -> {
            HotelRoomswing ui = new HotelRoomswing();
            ui.setVisible(true);
            ui.chooseStorageAndInit();
        });
    }

    // ============================================================
    //  内部类 —— 添加/修改房间对话框
    // ============================================================

    private static class RoomDialog extends JDialog {

        private final JTextField roomNoField  = new JTextField(12);
        private final JComboBox<String> floorCombo = new JComboBox<>(new String[]{"1", "2", "3", "4", "5", "6"});
        private final JComboBox<String> statusCombo = new JComboBox<>(STATUS_NAMES);

        private Room result = null;
        private boolean editMode;

        /**
         * @param owner  父窗口
         * @param title  标题
         * @param room   编辑模式传入已有房间对象；添加模式传 null
         */
        RoomDialog(Frame owner, String title, Room room) {
            super(owner, title, true);
            this.editMode = (room != null);
            initDialog();

            if (room != null) {
                // 编辑模式：预填数据，房号不可改
                roomNoField.setText(room.getRoomNo());
                roomNoField.setEditable(false);
                floorCombo.setSelectedItem(String.valueOf(room.getFloor()));
                statusCombo.setSelectedIndex(Math.max(0, Math.min(room.getRoomStatus(), STATUS_NAMES.length - 1)));
            }
        }

        private void initDialog() {
            setSize(380, 220);
            setLocationRelativeTo(getOwner());
            setResizable(false);

            JPanel panel = new JPanel(new GridBagLayout());
            panel.setBorder(new EmptyBorder(12, 12, 8, 12));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(4, 4, 4, 4);

            // 房号
            gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
            panel.add(new JLabel("房间号："), gbc);
            gbc.gridx = 1; gbc.weightx = 1;
            panel.add(roomNoField, gbc);

            // 楼层
            gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
            panel.add(new JLabel("楼 层："), gbc);
            gbc.gridx = 1; gbc.weightx = 1;
            panel.add(floorCombo, gbc);

            // 状态
            gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
            panel.add(new JLabel("状 态："), gbc);
            gbc.gridx = 1; gbc.weightx = 1;
            panel.add(statusCombo, gbc);

            // 按钮
            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 6));
            JButton okBtn     = new JButton("确定");
            JButton cancelBtn = new JButton("取消");

            okBtn.addActionListener(e -> onOk());
            cancelBtn.addActionListener(e -> dispose());
            getRootPane().setDefaultButton(okBtn);

            btnPanel.add(okBtn);
            btnPanel.add(cancelBtn);

            gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; gbc.weightx = 1;
            panel.add(btnPanel, gbc);

            setContentPane(panel);
        }

        private void onOk() {
            String roomNo = roomNoField.getText().trim();

            if (roomNo.isEmpty()) {
                JOptionPane.showMessageDialog(this, "房间号不能为空！", "输入错误", JOptionPane.ERROR_MESSAGE);
                roomNoField.requestFocus();
                return;
            }
            // 添加模式下检查房号是否已存在（业务层会做，但可以提前提示）
            if (!editMode && roomNo.length() > 10) {
                JOptionPane.showMessageDialog(this, "房间号过长（最多10个字符）！", "输入错误", JOptionPane.ERROR_MESSAGE);
                roomNoField.requestFocus();
                return;
            }

            int floor;
            try {
                floor = Integer.parseInt((String) floorCombo.getSelectedItem());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "楼层输入无效！", "输入错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int status = statusCombo.getSelectedIndex();

            result = new Room(roomNo, floor, status);
            dispose();
        }

        /** 返回用户填写的房间对象（点击确定后有效），点击取消返回 null */
        Room getRoom() {
            return result;
        }
    }
}
