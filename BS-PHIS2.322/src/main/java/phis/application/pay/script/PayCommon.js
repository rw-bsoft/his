$package("phis.application.pay.script")
$import("phis.script.rmi.jsonRequest", "phis.script.rmi.miniJsonRequestSync","phis.script.common", "phis.script.util.DateUtil")
// 提交订单
function SubmitOrder(paydata,jsmodule) {
	JSMODULE = jsmodule;//定义结算窗口对象
	ORDERQUEREY ={};
	ORDERQUEREY.IP = paydata.IP;
	ORDERQUEREY.ORGANIZATIONCODE = paydata.ORGANIZATIONCODE;
	ORDERQUEREY.COMPUTERNAME = paydata.COMPUTERNAME;
	ORDERQUEREY.APIURL = getPayApiUrl("orderQuery");
	paydata.APIURL = getPayApiUrl("trade_pay");
	var payapi = phis.script.rmi.miniJsonRequestSync({
		serviceId : "phis.mobilePaymentService",
		serviceAction : "payOrder",
		body : paydata
	});
	if(payapi.code >=300){
		MyMessageTip.msg("提示",  "支付异常！" + payapi.msg, true);
		JSMODULE.running = false;
		return;
	}else{
		if(payapi.json.body.hospno==null){
			if(payapi.json.body.order.indexOf('"code":"200"') != -1){
				Ext.Msg.alert("提示",  "支付异常！请与病人确认微信或支付宝是否已成功扣款，如已扣款请手动退款！订单号："+JSON.parse(payapi.json.body.order).hospNo);
				JSMODULE.running = false;
				return;
			}
			MyMessageTip.msg("提示",  "支付异常！" + payapi.json.body.order, true);
			JSMODULE.running = false;
			return;
		}
		ORDERQUEREY.HOSPNO = payapi.json.body.hospno;
		startOrderQueryTask();
	}
}
//开始循环获取订单状态
function startOrderQueryTask(){
	JSMODULE.runningTask = true;
	Ext.TaskMgr.stopAll();
	requestcount = 9;//循环请求订单状态查询接口9次
	tempCount = 0;
	if(!JSMODULE.task_orderquery){
		JSMODULE.task_orderquery = {
        	run:OrderQuery,
       		interval:3000,
       		scope : JSMODULE
		};
	}
	Ext.TaskMgr.start(JSMODULE.task_orderquery);
}
function stopReadTask(){
	Ext.TaskMgr.stopAll();
	JSMODULE.runningTask = false;
}
//订单查询
function OrderQuery() {
	if(ORDERQUEREY == null || ORDERQUEREY.HOSPNO== null || ORDERQUEREY.HOSPNO==""){		
		stopReadTask();
		MyMessageTip.msg("提示",  "未找到订单流水号，订单查询失败！", true);
		JSMODULE.running = false;
		return;
	}
	if(typeof(tempCount) == 'undefined'){
		tempCount = 0;
	}
	if(tempCount==requestcount-1){		
			stopReadTask();
			Ext.Msg.confirm("请确认", "查询订单交易结果超时，要继续请求查询吗？如订单已正常结算，请务必点击‘否’，以免多次结算！", function(btn) {// 先提示是否删除
				if (btn == 'yes') {
					tempCount = 0;
					Ext.TaskMgr.start(this.task_orderquery);
				}
				else{
					//取消继续后，修改订单状态为 3订单失败
					tempCount = 0;
					stopReadTask();
					updateOrderStatus("3",ORDERQUEREY);
					JSMODULE.running = false;
					return;
				}
			}, this);
	}
	tempCount+=1;
	if(ORDERQUEREY != null && ORDERQUEREY.HOSPNO!=null && ORDERQUEREY.HOSPNO!=""){
		var payapi = phis.script.rmi.miniJsonRequestSync({
			serviceId : "phis.mobilePaymentService",
			serviceAction : "orderQuery",
			body : ORDERQUEREY
		});
		if(payapi.code >=300){
			if(tempCount==requestcount-1){
				stopReadTask();
				//更新订单状态为：2订单异常
				updateOrderStatus("2",ORDERQUEREY);
				JSMODULE.running = false;
				return;
			}
		}else{
			if(payapi.json.body.order.indexOf('"code":"200"') != -1 ){
				stopReadTask();
				//更新订单状态为：1订单完成，需考虑是否在后台直接更新掉
				updateOrderStatus("1",ORDERQUEREY);
				JSMODULE.doCommit2();
			}
			/*else if(tempCount==requestcount-1){
				alert(tempCount);
				stopReadTask();
				//更新订单状态为：2订单异常
				updateOrderStatus("2",ORDERQUEREY);
				JSMODULE.running = false;
				return;
			}*/
		}		
	}
}

//订单交易成功后更新订单状态
function updateOrderStatus(status,orderdata){
	orderdata.STATUS = status;
	var payapi = phis.script.rmi.miniJsonRequestSync({
		serviceId : "phis.mobilePaymentService",
		serviceAction : "updateOrderStatus",
		body : orderdata
	});
	if(payapi.code >=300){
		MyMessageTip.msg("提示",  payapi.msg, true);
	}
}
String.prototype.myReplace=function(f,e){
    var reg=new RegExp(f,"g");    
    return this.replace(reg,e); 
}
function generateHospno(ywlx,voucherno){
	return ywlx+Date.getServerDateTime().myReplace('-','').myReplace(':','').myReplace(' ','')+voucherno;
}
//退款
function refund(refunddata,jsmodule){
	JSMODULE = jsmodule;//定义结算窗口对象	
	REFUNDQUEREY ={};
	REFUNDQUEREY.PAYSERVICE = refunddata.PAYSERVICE;
	REFUNDQUEREY.IP = refunddata.IP;
	REFUNDQUEREY.ORGANIZATIONCODE = refunddata.ORGANIZATIONCODE;
	REFUNDQUEREY.COMPUTERNAME = refunddata.COMPUTERNAME;
	REFUNDQUEREY.HOSPNO_ORG = refunddata.HOSPNO_ORG;//退款查询接口：原付款订单号
	REFUNDQUEREY.APIURL = getPayApiUrl("refundQuery");		
	refunddata.APIURL = getPayApiUrl("refund");
	var payapi = phis.script.rmi.miniJsonRequestSync({
		serviceId : "phis.mobilePaymentService",
		serviceAction : "refund",
		body : refunddata
	});
	if(payapi.code >=300){
		MyMessageTip.msg("提示",  "退款异常！" + payapi.msg, true);
		JSMODULE.running = false;
		return;
	}else{
		if(payapi.json.body.hospno==null){
			if(payapi.json.body.order.indexOf('"code":"200"') != -1){
				Ext.Msg.alert("提示",  "退款异常！请与病人确认微信或支付宝是否已收到退款，如未退款请手动退款！订单号："+JSON.parse(payapi.json.body.order).hospNo);
				JSMODULE.running = false;
				return;
			}
			MyMessageTip.msg("提示",  "退款异常！" + payapi.json.body.order, true);
			JSMODULE.running = false;
			return;
		}
		REFUNDQUEREY.HOSPNO = payapi.json.body.hospno;//退款单号
		startRefundTask();
	}
}
//开始循环获取退款订单状态
function startRefundTask(){
	Ext.TaskMgr.stopAll();
	requestcount = 9;//循环请求订单状态查询接口9次
	tempCount = 0;
	if(!this.task_refund){
		this.task_refund = {
        	run:RefundQuery,
       		interval:3000,
       		scope : JSMODULE
		};
	}
	Ext.TaskMgr.start(this.task_refund);
}
//订单查询
function RefundQuery() {
	if(REFUNDQUEREY == null || REFUNDQUEREY.HOSPNO== null || REFUNDQUEREY.HOSPNO==""){		
		stopReadTask();
		MyMessageTip.msg("提示",  "未找到退款单流水号，订单查询失败！", true);
		JSMODULE.running = false;
		return;
	}
	if(typeof(tempCount) == 'undefined'){
		tempCount = 0;
	}
	if(tempCount==requestcount){	
			stopReadTask();
			Ext.Msg.confirm("请确认", "查询退款单交易结果超时，要继续请求查询吗？如订单已正常结算，请务必点击‘否’，以免多次结算！", function(btn) {// 先提示是否删除
				if (btn == 'yes') {
					tempCount = 0;
					Ext.TaskMgr.start(this.task_refund);
				}
				else{
					//取消继续后，修改订单状态为 3订单失败
					tempCount = 0;
					stopReadTask();
					updateOrderStatus("3",REFUNDQUEREY);
					JSMODULE.running = false;
					return;
				}
			}, this);
	}
	tempCount+=1;
	if(REFUNDQUEREY != null && REFUNDQUEREY.HOSPNO!=null && REFUNDQUEREY.HOSPNO!=""){
		var payapi = phis.script.rmi.miniJsonRequestSync({
			serviceId : "phis.mobilePaymentService",
			serviceAction : "refundQuery",
			body : REFUNDQUEREY
		});
		if(payapi.code >=300){
			if(tempCount==requestcount-1){
				stopReadTask();
				//更新订单状态为：2订单异常
				updateOrderStatus("2",REFUNDQUEREY);
				JSMODULE.running = false;
				return;
			}
		}else{		
			if(payapi.json.body.order.indexOf('"code":"200"') != -1 ){
				stopReadTask();
				//更新订单状态为：1订单完成，需考虑是否在后台直接更新掉
				updateOrderStatus("1",REFUNDQUEREY);
				MyMessageTip.msg("提示", "扫码付和APP支付的订单退费成功后费用将按支付通道返还，无需退还现金！", true);
				this.doCommit3();
			}
			else if(tempCount==requestcount-1){
				stopReadTask();
				//更新订单状态为：2订单异常
				updateOrderStatus("2",REFUNDQUEREY);
				JSMODULE.running = false;
				return;
			}
		}		
	}
}

function getFKFS(QRCODE,iszy) {
    if (QRCODE == null) {
        Ext.Msg.alert("提示", "付款码不能为空！", function () {
        });
        return;
    }
 	//判断发起的支付是微信支付还是支付宝支付
    var wx = [10, 11, 12, 13, 14, 15];// 微信付款码串首两位数字范围
    var zfb = [25, 26, 27, 28, 29, 30];// 支付宝付款码串首两位数字范围
    var sqms = QRCODE.substring(0, 2);//截取付款码首两位
    for (var i = 0; i < wx.length; i++) {
        if (sqms == wx[i]) {
            //document.getElementsByName("_ZFFS")[0].value = "正在使用微信支付";
        	if(iszy && iszy==1){
        		return 52;
        	}
            return 32;//对应表gy_fkfs中字段fkfs
        }
    }
    for (var o = 0; o < zfb.length; o++) {
        if (sqms == zfb[o]) {
            //document.getElementsByName("_ZFFS")[0].value = "正在使用支付宝支付";
        	if(iszy && iszy==1){
        		return 53;
        	}
            return 33;//对应表gy_fkfs中字段fkfs
        }
    }
    return;
}

function getPaytype(QRCODE) {//1支付宝 2微信
 	//判断发起的支付是微信支付还是支付宝支付
    var wx = [10, 11, 12, 13, 14, 15];// 微信付款码串首两位数字范围
    var zfb = [25, 26, 27, 28, 29, 30];// 支付宝付款码串首两位数字范围
    var sqms = QRCODE.substring(0, 2);//截取付款码首两位
    for (var i = 0; i < wx.length; i++) {
        if (sqms == wx[i]) {
            //document.getElementsByName("_ZFFS")[0].value = "正在使用微信支付";
            return "2";
        }
    }
    for (var o = 0; o < zfb.length; o++) {
        if (sqms == zfb[o]) {
            //document.getElementsByName("_ZFFS")[0].value = "正在使用支付宝支付";
            return "1";
        }
    }
    return 0;
}

//获取聚合支付平台接口地址
function getPayApiUrl(apiname) {
	var apiurl = "http://10.2.202.15:8081/payRegion/rest/pay/";
	//var apiurl = "http://223.111.7.25:10001/payRegion/rest/pay/";
/*	var params = this.loadSystemParams({
		"commons" : ['PAY_URL']
	})
	if (params.PAY_URL && params.PAY_URL!="null" && params.PAY_URL!=""){
		apiurl = params.PAY_URL;
	}*/
	switch(apiname)
	{
		case "trade_pay"://支付接口
		  	apiurl += "trade_pay";
		  break;
		case "orderQuery"://支付查询接口
		  	apiurl += "orderquery";
		  break;
		case "refund"://退款接口
		  	apiurl += "refund";
		  break;
		case "refundQuery"://退款查询接口
		  	apiurl += "refundQuery";
		  break;
		case "cancel"://撤销订单接口
		  	apiurl += "cancel";
		  break;
		case "closeorder"://关闭订单接口
		  	apiurl += "closeorder";
		  break;
		default:
	}
    return apiurl;
}

function updateFPHM(MZXH,FPHM) {
    var saveFPHM = phis.script.rmi.miniJsonRequestSync({
        serviceId : "PayService",
        serviceAction : "updateFPHM",
        FPHM : FPHM,
        MZXH : MZXH
    });
}
function P7001_request (JZKH,BRXM,FKFS,_YSJE,_SQM,IP,TYPE,JSCS){

    var P7001URL = phis.script.rmi.miniJsonRequestSync({
        serviceId : "PayService",
        serviceAction : "orderSendPostURL",
        orderType : "P7001",
        JZKH : JZKH,
        BRXM : BRXM,
        ZFFS : FKFS,
        XJJE : _YSJE,
        autoCode : _SQM,
        IP : IP,
        TYPE : TYPE,
        JSCS : JSCS
    });

    var urlParam=P7001URL.json.URL;
    var MZXHParam=P7001URL.json.MZXH;


    var P7001URL1 = phis.script.rmi.miniXmlRequestSync({
        url:urlParam
    });
    var P7001URL_ = phis.script.rmi.miniJsonRequestSync({
        serviceId : "PayService",
        serviceAction : "saveOrderMap",
        orderType : "P7001",
        JZKH : JZKH,
        BRXM : BRXM,
        ZFFS : FKFS,
        XJJE : _YSJE,
        autoCode : _SQM,
        IP : IP,
        TYPE : TYPE,
        JSCS : JSCS,
        retnrnXml:P7001URL1.json,
        MZXH: MZXHParam
    });

    if((P7001URL_.json.code == "0000" && P7001URL_.json.status == 1)){
        return P7001URL_;
    }else if(P7001URL_.json.code == "9999"||P7001URL_.code == 500){
        return P7001URL_;
    }else {
        var sleep_=0
        while (sleep_<20){
            sleep_++;
            sleep(2000)
            var P2005URL_ = phis.script.rmi.miniJsonRequestSync({
                serviceId : "PayService",
                serviceAction : "orderSendPostURL",
                orderType : "P2005",
                JZKH : JZKH,
                BRXM : BRXM,
                ZFFS : FKFS,
                XJJE : _YSJE,
                autoCode : _SQM,
                IP : IP,
                TYPE : TYPE,
                JSCS : JSCS,
                retnrnXml:P7001URL1.json,
                MZXH: MZXHParam
            });
            var P2005URL_json = phis.script.rmi.miniXmlRequestSync({
                url:P2005URL_.json.URL
            });

            var P2005saveOrderMap = phis.script.rmi.miniJsonRequestSync({
                serviceId : "PayService",
                serviceAction : "saveOrderMap",
                orderType : "P2005",
                JZKH : JZKH,
                BRXM : BRXM,
                ZFFS : FKFS,
                XJJE : _YSJE,
                autoCode : _SQM,
                IP : IP,
                TYPE : TYPE,
                JSCS : JSCS,
                retnrnXml:P2005URL_json.json,
                MZXH: MZXHParam
            });
            var PayStatus=P2005saveOrderMap.json.PayStatus;
            if(P2005saveOrderMap.code!=500){
                if(PayStatus=="0"){//0 未支付
                }else if(PayStatus=="1"){//1 支付成功
                    return P2005saveOrderMap;
                }else if(PayStatus=="2"){//2 支付失败
                    return P2005saveOrderMap;
                }else if(PayStatus=="-1"){//-1 关闭或撤销
                    return P2005saveOrderMap;
                }else{
                    return P2005saveOrderMap;
                }
            }
            if(sleep_==20){
                data_P7002(_SQM,MZXHParam)
                return P2005saveOrderMap;
            }

        }
    }
}
// 取消关闭订单
function data_P7002 (autoCode,MZXH) {
    var P7002URL_ = phis.script.rmi.miniJsonRequestSync({
        serviceId : "PayService",
        serviceAction : "orderSendPostURL",
        orderType : "P7002",
        autoCode : autoCode,
        MZXH: MZXH
    });
    var P7002URL_json = phis.script.rmi.miniXmlRequestSync({
        url:P7002URL_.json.URL
    });
    var P7002saveOrderMap = phis.script.rmi.miniJsonRequestSync({
        serviceId : "PayService",
        serviceAction : "saveOrderMap",
        orderType : "P7002",
        autoCode : autoCode,
        retnrnXml:P7002URL_jsoRefundn.json,
        MZXH: MZXH,
        id:P7002URL_.json.id
    });
}
////下载对账文件
function data_P7003 (BalanceDate) {
    var P7003URL_ = phis.script.rmi.miniJsonRequestSync({
        serviceId : "PayService",
        serviceAction : "orderSendPostURL",
        orderType : "P7003",
        BalanceDate : BalanceDate
    });
    var P7003URL_json = phis.script.rmi.miniXmlRequestSync({
        url:P7003URL_.json.URL
    });
}
function sleep(numberMillis) {
    var now = new Date();
    var exitTime = now.getTime() + numberMillis;
    while (true) {
        now = new Date();
        if (now.getTime() > exitTime)
            return;
    }
}