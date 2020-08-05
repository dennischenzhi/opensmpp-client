package com.tp.sms.opensmpp.client.connector;

import org.smpp.Data;
import org.smpp.Session;
import org.smpp.TCPIPConnection;
import org.smpp.WrongSessionStateException;
import org.smpp.pdu.BindRequest;
import org.smpp.pdu.BindResponse;
import org.smpp.pdu.BindTransciever;

import java.io.IOException;

public class SmppConnection {
    public static String systemId = "cloudsmpp";
    public static String password = "cloud123";
    public static String host = "192.168.29.40";
    public static int port = 2775;

    public static Session InitConnection() throws IOException, WrongSessionStateException {
        Session session =null;

        try {
            //byte value = (new Integer(52)).byteValue();
            final BindRequest request = new BindTransciever();
            request.setSystemId(systemId);
            request.setPassword(password);
            request.setInterfaceVersion((byte)52);
            //request.setCommandId(0x00000009);
            //request.setCommandStatus(0x00);
            //request.setAddressRange((byte) 0,(byte) 0,null);


            TCPIPConnection connection =
                    new TCPIPConnection(host, port);
            //connection.setReceiveTimeout(BIND_TIMEOUT);
            session = new Session(connection);
            System.out.println("Send bind request..."+request.debugString());
            BindResponse response = session.bind(request);

            if (response.getCommandStatus() == Data.ESME_ROK) {
                System.out.println("Bind Succ ");
            } else {
                System.out.println("Bind failed, code " + response.getCommandStatus());
            }
            System.out.println("Bind response " + response.debugString());
            System.out.println("Bind response body "+response.getBody().toString());
            //System.out.println("Bind response system Id "+response.getSystemId());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            System.out.println(ex.toString());
        }

        return session;
    }
}
