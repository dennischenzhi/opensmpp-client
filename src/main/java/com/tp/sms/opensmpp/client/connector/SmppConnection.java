package com.tp.sms.opensmpp.client.connector;

import com.tp.sms.opensmpp.client.service.MultiTaskClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smpp.Data;
import org.smpp.Session;
import org.smpp.TCPIPConnection;
import org.smpp.WrongSessionStateException;
import org.smpp.pdu.BindRequest;
import org.smpp.pdu.BindResponse;
import org.smpp.pdu.BindTransciever;

import java.io.IOException;

public class SmppConnection {
    public static String systemId = "testcz";
    public static String password = "19850416";
    public static String host = "47.90.120.242";
    public static int port = 2775;

    private static final Logger LOGGER = LoggerFactory.getLogger(SmppConnection.class);
    
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
            LOGGER.info("Send bind request..."+request.debugString());
            BindResponse response = session.bind(request);

            if (response.getCommandStatus() == Data.ESME_ROK) {
                LOGGER.info("Bind Succ ");
            } else {
                LOGGER.info("Bind failed, code " + response.getCommandStatus());
            }
            LOGGER.info("Bind response " + response.debugString());
            LOGGER.info("Bind response body "+response.getBody().toString());
            //LOGGER.info("Bind response system Id "+response.getSystemId());
        } catch (Exception ex) {
            LOGGER.info(ex.getMessage());
            LOGGER.info(ex.toString());
        }

        return session;
    }
}
