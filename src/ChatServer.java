import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.Vector;

public class ChatServer {
    static Vector ClientSockets;
    static Vector LoginNames;

    ChatServer() throws IOException{
        ServerSocket server = new ServerSocket(5217);
        ClientSockets = new Vector();
        LoginNames = new Vector();

        while(true){
            Socket client = server.accept();
            AcceptClient acceptClient = new AcceptClient(client);
        }
    }

    public static void main(String[] args) throws IOException {
        ChatServer mChatServer = new ChatServer();
    }

    class AcceptClient extends Thread {
        Socket ClientSocket;
        DataInputStream dIn;
        DataOutputStream dOut;
        AcceptClient(Socket client) throws IOException {
            this.ClientSocket = client;
            dIn = new DataInputStream(ClientSocket.getInputStream());
            dOut = new DataOutputStream(ClientSocket.getOutputStream());

            String LoginName = dIn.readUTF();

            LoginNames.add(LoginName);
            ClientSockets.add(ClientSocket);

            start();
        }

        @Override
        public void run() {
            while(true){
                try {
                    String msgFromClient = dIn.readUTF();
                    StringTokenizer stringTokenizer = new StringTokenizer(msgFromClient);
                    String LoginName = stringTokenizer.nextToken();
                    String MsgType = stringTokenizer.nextToken();
                    String msg = "";
                    int lo = -1;
                    while(stringTokenizer.hasMoreTokens()){
                        msg = msg + " " +stringTokenizer.nextToken();
                    }
                    if(MsgType.equals("LOGIN")) {
                        for (int i = 0; i < LoginNames.size(); i++) {
                            Socket pSocket = (Socket) ClientSockets.elementAt(i);
                            DataOutputStream mDataOutputStream = new DataOutputStream(pSocket.getOutputStream());
                            mDataOutputStream.writeUTF(LoginName + " Has Logged In");
                        }
                    } else if(MsgType.equals("LOGOUT")){
                            for (int i = 0; i < LoginNames.size(); i++) {
                                if(LoginName == LoginNames.elementAt(i)){
                                    lo = i;
                                }
                                Socket pSocket = (Socket) ClientSockets.elementAt(i);
                                DataOutputStream mDataOutputStream = new DataOutputStream(pSocket.getOutputStream());
                                mDataOutputStream.writeUTF(LoginName + " Has Logged Out");
                            }
                            if(lo >= 0){
                                LoginNames.removeElementAt(lo);
                                ClientSockets.removeElementAt(lo);
                            }
                    } else {
                            for (int i = 0; i < LoginNames.size(); i++) {
                                Socket pSocket = (Socket) ClientSockets.elementAt(i);
                                DataOutputStream mDataOutputStream = new DataOutputStream(pSocket.getOutputStream());
                                mDataOutputStream.writeUTF(LoginName + " : " + msg);
                            }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
