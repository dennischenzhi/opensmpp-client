package com.tp.sms.opensmpp.client.service;

import org.smpp.*;
import org.smpp.pdu.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class MultiTaskClient {

    static BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
    private String systemId = "cloudsmpp";
    private String password = "cloud123";
    private String host = "192.168.29.40";
    private int port = 2775;
    private String oa = "Test";
    private String da = "85259846556";
    private String content = "SMPP Client By Java";

    public void sendMulti() throws PDUException, NotSynchronousException, TimeoutException, WrongSessionStateException, IOException {
        for(int i=0;i<10;i++){
            sendSMS();
        }
    }

    public Session InitConnection() throws IOException, WrongSessionStateException {
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

    private String getParam(String prompt, String defaultValue) {
        String value = "";
        String promptFull = prompt;
        promptFull += defaultValue == null ? "" : " [" + defaultValue + "] ";
        System.out.print(promptFull);
        try {
            value = keyboard.readLine();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            //event.write(e, "");
            //debug.write("Got exception getting a param. " + e);
        }
        if (value.compareTo("") == 0) {
            return defaultValue;
        } else {
            return value;
        }
    }

    public void sendSMS() throws IOException, WrongSessionStateException, TimeoutException, PDUException, NotSynchronousException {
        //systemId = getParam("systemId", systemId);
        //password = getParam("password", password);
        //host= getParam("host", host);
        //port= Integer.valueOf(getParam("port", String.valueOf(port)));
        //oa = getParam("oa", oa);
        //da = getParam("da", da);
        //content = getParam("content", content);
        //private String systemId = "cloudsmpp";

        //private String password = "cloud123";
        //private String host = "192.168.29.40";
        //private int port = 2775;
        //private String oa = "Test";
        //private String da = "85259846556";


        Session session = InitConnection();
        SubmitSM request = new SubmitSM();
        request.setSourceAddr((byte) 5, (byte) 0, oa);
        request.setDestAddr((byte) 1, (byte) 1, da);
        request.setShortMessage(content);

        SubmitSMResp submitSMResp = session.submit(request);

        System.out.println("submitSMResp messageId: " + submitSMResp.getMessageId());
        System.out.println("submitSMResp debug: " + submitSMResp.debugString());
        PDU pdu = session.receive(300000);

        if (pdu instanceof DeliverSM) {
            DeliverSM received = (DeliverSM) pdu;
            if (received.getEsmClass() == 0) {                                                          // new message
                System.out.println("RECEIVE NEW MESSAGE:" + received.debugString());
                String MSG_SENDER = received.getSourceAddr().getAddress();
                String SHORT_MSG = received.getShortMessage();
            } else {                                                                                    // delivry Repport
                System.out.println("RECEIVE NEW DELIVERED REPORT:" + received.debugString());
                String MSG_ID = (new BigInteger(received.getReceiptedMessageId(), 16)) + "";
                int MSG_STATUS = received.getMessageState();
            }
        } else {
            System.out.println("----------------- FF pdu: " + pdu.debugString());
        }

        if (session != null)
            session.close();

    }
}
