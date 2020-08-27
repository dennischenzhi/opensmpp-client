package com.tp.sms.opensmpp.client.service;

import com.google.common.base.CharMatcher;
import org.apache.commons.codec.binary.Hex;
import org.jsmpp.util.HexUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smpp.*;
import org.smpp.pdu.*;
import org.smpp.util.Queue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;

public class OpenSmppClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(OpenSmppClient.class);

    static BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
//    private String systemId = "cloudsmpp";
//    private String password = "cloud123";
//    private String host = "192.168.29.40";
//    private int port = 2775;
//    private String oa = "Test";
//    private String da = "85259846556";
//    private String content = "SMPP Client By Java";

    private String systemId = "TPTLCMI01";
    private String password = "4GeppuHQ";
    private String host = "223.118.36.5";
    private int port = 2775;
    private String oa = "Test";
    private String da = "8618851152111";
    //private String da = "85262142167";
    private String content = "短信测试中文简体 短信測試中文繁體";

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

    private String getParam(String prompt, String defaultValue) {
        String value = "";
        String promptFull = prompt;
        promptFull += defaultValue == null ? "" : " [" + defaultValue + "] ";
        System.out.print(promptFull);
        try {
            value = keyboard.readLine();
        } catch (IOException e) {
            LOGGER.info(e.getMessage());
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
        systemId = getParam("systemId", systemId);
        password = getParam("password", password);
        host= getParam("host", host);
        port= Integer.valueOf(getParam("port", String.valueOf(port)));
        oa = getParam("oa", oa);
        da = getParam("da", da);
        content = getParam("content", content);
        //private String systemId = "cloudsmpp";

        //private String password = "cloud123";
        //private String host = "192.168.29.40";
        //private int port = 2775;
        //private String oa = "Test";
        //private String da = "85259846556";


        Session session = InitConnection();
        SubmitSM  request = new SubmitSM();
        request.setSourceAddr((byte) 5,(byte)0,oa);
        request.setDestAddr((byte) 1,(byte)1,da);


//        if(!isAscii(content)){
//            request.setShortMessage(encodeChinese(content));
//        }else{
//            request.setShortMessage(content);
//        }

        //byte[] bytes= content.getBytes("UTF-16BE");

        request.setRegisteredDelivery((byte)1);
        request.setDataCoding((byte)8);
        request.setShortMessage(content,"UTF-16BE");
        //request.setShortMessage(HexUtil.convertStringToHexString(content));

        LOGGER.info("SubmitSM debug: "+request.debugString());
        SubmitSMResp  submitSMResp  = session.submit(request);

        LOGGER.info("submitSMResp messageId: "+submitSMResp.getMessageId());
        LOGGER.info("submitSMResp debug: "+submitSMResp.debugString());


        if(session!=null)
            session.close();

    }

    private boolean isAscii(String pContent) {
        if (CharMatcher.ascii().matchesAllOf(pContent)) {
            return true;
        } else {
            return false;
        }
    }

    private String encodeChinese(String pContent) throws UnsupportedEncodingException {

        String s = pContent;
        //this.logger.info(s);
        String hexString = null;
        try {
            hexString = new String(Hex.encodeHexString(s.getBytes("UTF-8")));
        } catch (UnsupportedEncodingException e) {
            LOGGER.error(e.getMessage());
            throw e;
        }
        return hexString;
    }

}
