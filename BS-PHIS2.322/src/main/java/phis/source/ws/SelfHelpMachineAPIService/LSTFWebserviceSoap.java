/**
 * LSTFWebserviceSoap.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package phis.source.ws.SelfHelpMachineAPIService;

public interface LSTFWebserviceSoap extends java.rmi.Remote {

    /**
     * 测试方法
     */
    public String helloWorld() throws java.rmi.RemoteException;

    /**
     * 启航交易
     */
    public String trans(String client_id, String AESKey, String payType, String txnType, String QRCodeNo, String COMM_HIS, String je, String body, String oldCOMM_SN, String oldCOMM_HIS, String oldHostSer) throws java.rmi.RemoteException;
}
