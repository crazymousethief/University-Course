package pers.crazymousethief.bigsort.distributed.proxy;

import pers.crazymousethief.bigsort.distributed.SocketBlock;
import pers.crazymousethief.bigsort.distributed.node.NodeBlock;
import pers.crazymousethief.bigsort.io.util.Helper;

import java.beans.JavaBean;
import java.io.*;
import java.net.ServerSocket;
import java.util.*;

public class Proxy {
    private static final ArrayList<SocketBlock> socketBlocks = new ArrayList<>();
    private static final Map<SocketBlock, NodeBlock> socketBlockMap = new HashMap<>();

    public static void main(String[] args) throws IOException {
        new Thread(() -> {
            try (var server = new ServerSocket(17325)) {
                while (true) {
                    SocketBlock socketBlock = new SocketBlock(server.accept());
                    socketBlocks.add(socketBlock);
                    socketBlockMap.put(socketBlock, new NodeBlock());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        var reader = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            printf("> ");
            var orders = reader.readLine().split(" ");
            var argList = new String[orders.length - 1];
            System.arraycopy(orders, 1, argList, 0, orders.length - 1);
            if (command(orders[0], argList)) break;
        }

        System.exit(0);
    }

    private static boolean command(String order, String... args) {
        boolean exit = false;
        switch (order) {
            case "":
                break;
            case "ls":
                refresh();
                for (var i = 0; i < socketBlocks.size(); i++) {
                    var socketBlock = socketBlocks.get(i);
                    printfln("%d %s %s", i, socketBlock.getSocket().getRemoteSocketAddress(), socketBlockMap.get(socketBlock).getState());
                }
                break;
            case "put":
                if (args.length != 2) {
                    printfln("Unexpected arguments");
                } else {
                    var fileName = args[0];
                    var socketBlock = socketBlocks.get(Integer.parseInt(args[1]));
                    new Thread(() -> {
                        try {
                            var input = new BufferedInputStream(new FileInputStream(fileName));
                            var output = new BufferedOutputStream(socketBlock.getOutputStream());
                            byte[] buffer = new byte[1024];
                            int length;
                            while ((length = input.read(buffer)) != -1) {
                                output.write(buffer, 0, length);
                            }
                            output.flush();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }).start();
                }
                break;
            case "exit":
                exit = true;
                break;
            default:
                printfln("Unknown command");
                break;
        }

        return exit;
    }

    private static void refresh() {
        socketBlocks.removeIf((socketBlock) -> {
            try {
                socketBlock.getSocket().sendUrgentData(0xFF);
            } catch (Exception e) {
                socketBlockMap.remove(socketBlock);
                return true;
            }
            return false;
        });
        for (SocketBlock socketBlock : socketBlocks) {
            socketBlockMap.replace(socketBlock, getState(socketBlock));
        }
    }

    private static NodeBlock getState(SocketBlock socketBlock) {
        try {
            var writer = socketBlock.getOutputStreamWriter();
            writer.write("STATE\n");
            writer.flush();
            return (NodeBlock) socketBlock.getObjectInputStream().readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void printf(String order, Object... args) {
        System.out.printf(order, args);
    }

    private static void printfln(String order, Object... args) {
        printf(order.concat("\n"), args);
    }
}
