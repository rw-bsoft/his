$package("phis.application.yb.script")
$import("util.schema.SchemaLoader", "phis.script.widgets.MyMessageTip",
		"phis.script.util.DateUtil", "phis.script.rmi.jsonRequest",
		"phis.script.rmi.miniJsonRequestSync",
		"phis.script.rmi.miniJsonRequestAsync");
/**
 * 使用说明：MedicareCommonMethod.JS是整个医保接口函数声明的模块，要开始做一个医保接口的时候，需要实现doYbjddk后面
 * 的所有接口函数，实现后需要在本文件的initYBServer配置好要调用的模块，业务模块每次调用医保接口的时候会调用initYBServer方法
 * 动态装载要调用相应的医保的JS实现模块，具体参考MedicareCommonMethod4SGYB.JS的实现。
 * ---write by candy 2016.8.3
 */
phis.application.yb.script.MedicareCommonMethod = {
		/**
		 * 向服务端请求数据，并返回请求结果
		 */
		requestServer : function(ffm, body) {
			var ret = phis.script.rmi.miniJsonRequestSync({
						serviceId : "medicareService",
						serviceAction : ffm,
						body : body
					});
			if (ret.code > 300) {
				this.processReturnMsg(ret.code, ret.msg);
				return;
			}
			return ret;
		},
			/**
		 * 初始化医保服务方法,每增加一个医保需要在此方法内声明要调用的接口文件
		 * @param BRXZ
		 */		
		initYBServer : function(brxz){
			var ret=this.requestServer("queryYbbrxz",brxz);				
			var iXZDL = parseInt(ret.json.body);
			var sYBSvr = "";
			//alert("BRXZ=" + brxz +"  XZDL=" + iXZDL);		
			switch(iXZDL){
				//1 韶关医保
				case 1: {
						sYBSvr = "phis.application.yb.sgyb.script.MedicareCommonMethod4SGYB";
						break;
					}
				//2 肇庆医保
				case 2: {
						sYBSvr = "phis.application.yb.zqyb.script.MedicareCommonMethod4ZQYB";				
						break;
				}
				//3 省异地结算
				case 3: {
						sYBSvr = "phis.application.yb.sgyb.script.MedicareCommonMethod4SYDJS";
						break;
				}
				//4 东莞医保
				case 3: {
						sYBSvr = "phis.application.yb.sgyb.script.MedicareCommonMethod4DGYB";
						break;
				}
				default: sYBSvr ="";
			}
			//动态引入对应医保的JS模块
			if(sYBSvr.length > 10){
				$import(sYBSvr);					
				Ext.apply(this,eval(sYBSvr));
			}
	},	
		//取得病人医保性质分类
		getYbbrxz : function(brxz) {
			var ret = phis.script.rmi.miniJsonRequestSync({
						serviceId : "medicareService",
						serviceAction : "queryYbbrxz",
						body : brxz
					});
			if (ret.code > 300) {
				this.processReturnMsg(ret.code, ret.msg);
				return null;
			}
			return ret.json.body;
	},
		/*
		 * 打开结算找零前如果没有读卡判断病人是否是医保病人,如果是则不让结算 
		 * 必须读完卡才能结算(根据实际需求确定是否需要该判断)
		 */
		getSfYb : function(brxz) {
			//alert("取医保性质");
			var ybbrxz = this.getYbbrxz(brxz);// 查询当前病人信息 是否是医保
			if (ybbrxz == null) {
				return;
			}
			if (ybbrxz > 0) {
				// 如果病人性质是医保,并且没读卡, 则不能完成(具体判断已当地医保流程而定)
//				MyMessageTip.msg("提示", "医保病人未读卡,请先读卡", true);
//				return;
			}
			return 1;
	},
		//医保相关的公共数据对象	
		ybbhxx : null,  //病人基本信息
		ybcfxx : null,   //门诊处方信息
		ybyzxx : null,  //住院医嘱明细信息
		ybfyxx : null,  //医保费用信息  YBTCZF: 医保统筹支付（对应HIS的QTYS）  YBZHZF:医保帐户支付  YBZJJE: 医保总计金额    
		                         // YBZFJE: 自负金额HISZJJE:业务总计金额  HISZFJE:业务自费金额  HISXJJE: 业务现金金额  HBWC: 货币误差
		ybkxx : null,  //医保读卡信息
		ybfpxx : null, //传回给主业务模块，需要打印发票的地方调用些对象打印医保相关的内容
		
		//以下为医保接口实现单元必须要实现的接口方法
		
		//---挂号部分开始---
	    //通用医保读卡，给建档界面调用
		doYbjddk:function(){
		
	},
		//清除医保缓存信息
		clearYbxx : function() {
			this.ybbhxx  = null;
			this.ybcfxx  = null;
			this.ybyzxx  =null;
			this.ybfyxx  = null;
			this.ybkxx  = null;
	},
	//医保挂号读卡
	doYbghdk:function(){
			var form = this.form.getForm();
			var BRXM = form.findField("BRXM");
			var xm = BRXM.getValue();
			var ret = phis.script.rmi.miniJsonRequestSync({
						serviceId : "YBService",
						serviceAction : "doQueryMzghXmlByPath",
						xm : xm
					});
			if (ret.code > 300) {
				this.processReturnMsg(ret.code, ret.msg);
				return null;
			}
			var JZKH = form.findField("JZKH");//卡号
			JZKH.setValue(ret.json.TBR);
	},
		//读卡界面点击确认后回调方法
		onQr_mzghdk : function(ybxx) {
			this.ybkxx = ybxx;
	},
	    //医保挂号预结算
		doYbghyjs : function() {	
			var ret = {};
			
			ret["result"] = true;
			ret["msg"] = "成功";
			return ret;
	},
		//医保挂号确认结算
		doYbghjs : function() {
			var ret = {};
			
			ret["result"] = true;
			ret["msg"] = "成功";
			return ret;
	},
		// 医保退号
		doYbghth : function() {
			var ret = {};
			
			ret["result"] = true;
			ret["msg"] = "成功";
			return ret;
	},
		//退号界面确定后的回调函数
		onQr_Ghth : function(ybxx) {
			this.ybkxx = ybxx;
	},
	
		//---门诊收费部分开始---
	    //医保门诊收费读卡
		doYbmzsfdk:function(){
			
	},
		// 读卡返回 自动调用病人结算信息
		onQr_mzsfdk : function(ybxx) {
			this.ybkxx = ybxx;
	},
		// 医保门诊收费预结算
		doYbmzyjs : function() {
			var ret = {};
			
			ret["result"] = true;
			ret["msg"] = "成功";
			return ret;	
	},
		//医保门诊收费确认结算
		doYbmzjs:function(){
			var ret = {};
			
			ret["result"] = true;
			ret["msg"] = "成功";
			return ret;
	},
		//门诊收费保存数据过程中发生异常，调用医保费用冲销功能
		doYbmzsfcx: function(){
			var ret = {};
			
			ret["result"] = true;
			ret["msg"] = "成功";
			return ret;
	},
	    //门诊收费退款读卡
		doYbmztkdk:function(){
			
	},	
		// 门诊收费发票作废读卡成功
		onQr_mzfpzf : function(ybxx) {
			this.ybkxx = ybxx;
	},
	
		//---医保接口方法,入院登记部分开始---	    
		//医保入院登记读卡
		doYbrydjdk:function(){
				
	},
		// 住院登记读卡确认回调函数
		onQr_rydj : function(ybxx) {
			this.ybkxx = ybxx;
	},
		// 医保入院登记确认
		doYbZydj : function() {
			var ret = {};
			
			ret["result"] = true;
			ret["msg"] = "成功";
			return ret;
	},
	
		//取消入院登记读卡
		doYbqxdjdk:function(){
		
	},
		//取消入院登记确认回调函数
		onQr_qxrydj : function(ybxx) {
			this.ybkxx = ybxx;
	},
		//医保取消入院登记
		doYbqxrydj:function(){
			var ret = {};
			
			ret["result"] = true;
			ret["msg"] = "成功";
			return ret;
	},		

	
		//--医保接口方法,住院结算部分开始--	
		//医保住院结算读卡
		doYbzyjsdk:function(){
		
	},	
		onQr_zyjs : function(ybxx) {
			this.ybkxx = ybxx;
	},
		// 住院预结算
		doYbzyYjs : function() {
			var ret = {};
			
			ret["result"] = true;
			ret["msg"] = "成功";
			return ret;
	},
		//住院正式结算
		doYbzyjs:function(){
			var ret = {};
			
			ret["result"] = true;
			ret["msg"] = "成功";
			return ret;
	},
		//保存数据过程中发生异常，医保冲销费用
		doYbzyjscx: function(){
			var ret = {};
			
			ret["result"] = true;
			ret["msg"] = "成功";
			return ret;
	},
	
		//住院退款发票作废读卡
		doYbzytkdk:function(){
			
	},
		// 发票作废
		onQr_zyfpzf : function(ybxx) {
			this.ybkxx = ybxx;
	},
		// 住院发票作废
		doYbzyFPzf : function() {
			var ret = {};
			
			ret["result"] = true;
			ret["msg"] = "成功";
			return ret;
	},
	
		//自费转医保,成功返回true,失败返回false
		doZfzyb:function(body){
		var ret = phis.script.rmi.miniJsonRequestSync({
						serviceId : "medicareService",
						serviceAction : "queryYbRydjcs",
						body:body
					});
			if (ret.code > 300) {
				this.processReturnMsg(ret.code, ret.msg);
				return false;
			}
			var rydjcs=ret.json.body;
			//医保调用入院登记代码
			//....
			//如果失败 返回false
			//return false;
			//如果成功,更新zy_brry表
			//body["ZFZYB"]=1//自费转医保
			return true;
	},
		//医保转自费,成功返回true,失败返回false
		doYbzzf:function(body){
			var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : "medicareService",
							serviceAction : "queryYbZyxzzhcs",
							body:body
						});
			if (ret.code > 300) {
				this.processReturnMsg(ret.code, ret.msg);
				return false;
			}
			var xzzhcs=ret.json.body;
			//医保转换代码
			//....
			//如果失败 返回false
			//return false;
			//如果成功,更新zy_brry表
			//body["YBZZF"]=1//医保转自费
			return true;
	}
}