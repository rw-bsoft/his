$package("phis.application.yb.sgyb.script")
$import("util.schema.SchemaLoader", "phis.script.widgets.MyMessageTip",
		"phis.script.util.DateUtil", "phis.script.rmi.jsonRequest",
		"phis.script.rmi.miniJsonRequestSync",
		"phis.script.rmi.miniJsonRequestAsync", "phis.script.widgets.ymPrompt","phis.application.yb.script.PHISActiveXUtils",
		"phis.script.widgets.PrintWin","phis.application.yb.script.MedicareUtils");

phis.application.yb.sgyb.script.MedicareCommonMethod4SGYB ={
	
	  //---挂号部分开始---
	  //通用医保读卡，给建档界面调用
		doYbjddk:function(){
			
	},
		//清除医保信息
		clearYbxx : function() {
			this.ybbhxx  = null;
			this.ybcfxx  = null;
			this.ybyzxx  =null;
			this.ybfyxx  = null;
			this.ybkxx  = null;
	},
	  	//医保挂号读卡
		doYbghdk:function(){	
			//alert("病号信息" +Ext.encode(this.ybbhxx));			
	       var ybModule = this.createModule("ybghModule","phis.application.yb.YB/YB/YB01");	
	       ybModule.ybbhxx = this.ybbhxx;
	       ybModule.on("qr", this.onQr_mzghdk, this);
	       ybModule.opener = this;
			var win = ybModule.getWin();	 
			win.setTitle("韶关医保读卡");
			win.add(ybModule.initPanel());
			win.show();			
		    
	},
		//读卡界面点击确认后回调方法
		onQr_mzghdk : function(ybxx) {		
			this.ybkxx = ybxx;
	},
		  //医保挂号预结算
		doYbghyjs : function() {	
			var ret = {};
			//alert("预结算病号信息" +Ext.encode(this.ybbhxx));		
			//组装费用，写入request文件
			var requestFile = "cardno=" +this.ybkxx.ICKH+"|"
										+ "id0000=" +this.ybkxx.SBKH+"|"
			                            + "ghksmc="+this.ybbhxx.KSMC + "|" 
			                            + "ghfy00=" +this.ybbhxx.ZLJE+"|"
			                            + "ryzd00=";
			$PhisActiveXObjectUtils.initHtmlElement();
			var o = $PhisActiveXObjectUtils.getObject();
			//o.DeleteRpyFile();  //先删除应答文件
			var bsucess = o.WriteFile("mzgh",requestFile);
			if (parseInt(bsucess) != 1){
				Ext.MessageBox.alert("写入挂号请求文件失败，请联系管理员！");
				return;
			}			
			Ext.MessageBox.alert("提示框","操作员点了[否]按钮，本次交易取消xxx！");			
			Ext.MessageBox.confirm("挂号请求","写入挂号请求文件成功,请等待医保有结果后点【是】按钮，系统才能取得医保返回参数。",function(btn){								
				if(btn!="yes"){
					Ext.MessageBox.alert("提示","操作员点了[否]按钮，本次交易取消！");
					return
				}				   
					var ybjsxx = phis.application.yb.script.MedicareUtils.String2Json(o.ReadFile("mzgh"));
					//alert("返回JS对象：" + Ext.encode(ybjsxx));
					if(ybjsxx.success !="TRUE"){
						Ext.MessageBox.alert("提示","医保返回挂号失败，原因:" + ybjsxx.bnghyy);
						return;
					}					
					this.ybkxx["YBLSH"] = ybjsxx.ghlsh0;
					this.ybkxx["GHRQ"] = ybjsxx.ghrq00;
					this.ybkxx["GHSJ"] = ybjsxx.ghsj00;					
					this.ybkxx["BRXM"] = ybjsxx.xming0;
					this.ybkxx["BRXB"] = ybjsxx.xbie00;
					this.ybkxx["BRNL"] = ybjsxx.brnl00;
					this.ybkxx["ICKH"] = ybjsxx.cardno;
					this.ybkxx["SBKH"] = ybjsxx.id0000;
					this.ybkxx["DWMC"] = ybjsxx.dwmc00;
					this.ybkxx["ICKZT"] = ybjsxx.icztmc;
					this.ybkxx["GZZT"] = ybjsxx.gzztmc;
					this.ybkxx["DQMC"] = ybjsxx.dqmc00;
					this.ybkxx["FZXMC"] = ybjsxx.fzxmc0;
					this.ybkxx["ZHYE"] = parseFloat(ybjsxx.grzhye);
					this.ybkxx["CZRY"] = ybjsxx.sfrxm0;
					
					this.ybfyxx = {};
					this.ybfyxx["YBTCZF"] = parseFloat(ybjsxx.ghfy00,2);  // 医保统筹支付
					this.ybfyxx["YBZHZF"] = 0.0; //医保帐户支付
					this.ybfyxx["YBZJJE"] = 0.0;   //医保总计金额
					this.ybfyxx["YBZFJE"] = 0.0;  //自负金额
					this.ybfyxx["HISZJJE"] = 0.0;  //业务总计金额
					this.ybfyxx["HISZFJE"] = 0.0;  //业务自费金额
					this.ybfyxx["HISXJJE"] = 0.0;  //业务现金金额
					this.ybfyxx["HBWC"] = 0.0;  //货币误差		
					Ext.apply(this.ybkxx,this.ybfyxx);		//复制费用信息	
					
					var module = this.midiModules["rjsModule"];
					if (!module) {
						module = this.createModule("rjsModule", this.refJsForm);
						this.midiModules["rjsModule"] = module;
						module.opener = this;
						module.on("settlement", this.doNew, this);
					}
					//this.GHXX.BLJE = this.form.getForm().findField('BLJE').getValue();
					var win = module.getWin();
					module.setData(this.GHXX, this.ybfyxx);
					win.show();
			},this);
	},
		//医保挂号结算
		doYbghjs : function() {
			var ret = {};
			alert("结算医保卡信息" +Ext.encode(this.ybkxx));
			alert("结算病号信息" +Ext.encode(this.ybkxx));
			//alert("结算费用信息" +Ext.encode(this.ybfyxx));
			
			ret["result"] = true;
			ret["msg"] = "成功";
			return ret;
	},
		// 医保退号
		doYbghth : function() {
			var ret = {};
			alert("挂号退号病号信息" +Ext.encode(this.ybbhxx));
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
			 var ybModule = this.createModule("ybghModule","phis.application.yb.YB/YB/YB01");	
		       ybModule.ybbhxx = this.ybbhxx;
		       ybModule.on("qr", this.onQr_mzsfdk, this);
				var win = ybModule.getWin();	 
				win.setTitle("韶关医保读卡");
				win.add(ybModule.initPanel());
				win.show();
	},
		// 读卡返回 自动调用病人结算信息
		onQr_mzsfdk : function(data) {
			this.ybkxx = 	data;
	},
		// 医保门诊预结算
		doYbmzyjs : function() {
			debugger
			var ret = {};		
			alert("门诊预结算病号信息" +Ext.encode(this.ybbhxx)); 	
			//门诊收费
			var requestFile = "cardno=" +  this.ybkxx.ICKH+"|" +  
	                                   "mzlsh0=" +  this.ybbhxx.MZHM+ "|" + 
	                                   "bqbm00=" + "|"+ //病种编码暂时未获取
	                                   "cfxms0=" + this.ybcfxx.length;
			$PhisActiveXObjectUtils.initHtmlElement();
			var o = $PhisActiveXObjectUtils.getObject();
			o.WriteFile("mzsf",requestFile);
			
			//门诊处方明细
			var cfxx = "";
			for(var i=0; i<this.ybcfxx.length; i++){
				var row = this.ybcfxx[i];
				for(var field in row){
					cfxx = cfxx + "{" + field + ":" + row[field] + "}";				
					//alert("key:"+key+";  value:"+row[field]);
					} 		
			}		
			alert("处方信息：" + cfxx);
			
			var requestMxFile ="";
			for(var i=0; i<this.ybcfxx.length; i++){
				requestMxFile ="医保编号" + "|" + //医院收费项目在医保中心的编号
										 "Y" + "|" +            //是否医保项目
										 this.ybcfxx[i].GBMC + "|" +//医院收费项目在医保中心的发票项目名称
										  this.ybcfxx[i].YPMC + "|" +//医院收费项目在医保中心的名称
										  this.ybcfxx[i].YFGG + "|" +//医院收费项目在医保中心的规格
										  this.ybcfxx[i].YFDW + "|" +//医院收费项目在医保中心的单位
										  this.ybcfxx[i].YPDJ + "|" +//医院收费项目在医保中心的单价
										  this.ybcfxx[i].YPSL + "|" +//数量
										  this.ybcfxx[i].HJJE + "|" +  //金额
										  this.ybcfxx[i].YSDM_text   //医生姓名
										 ;
			}
			o.WriteFile("mzsfmx",requestMxFile);
			//alert("门诊预结算病号信息" +Ext.encode(this.ybbhxx));		
			//alert("门诊处方信息" + Ext.encode(this.ybcfxx));
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
		alert("门诊退号病号信息" +Ext.encode(this.ybbhxx));
	},	
	// 门诊收费发票作废读卡成功
	onQr_mzfpzf : function(ybxx) {
		this.ybkxx = ybxx;
	},
	
	//---医保接口方法,入院登记部分开始---	    
	//医保入院登记读卡
	doYbrydjdk:function(){
		alert("入院登记病号信息" +Ext.encode(this.ybbhxx));
	},
	// 住院登记读卡确认回调函数
	onQr_rydj : function(ybxx) {
		this.ybkxx = ybxx;
	},
	// 医保入院登记确认
	doYbZydj : function() {
		var ret = {};
		alert("入院登记病号信息" +Ext.encode(this.ybbhxx));
		
		ret["result"] = true;
		ret["msg"] = "成功";
		return ret;
	},
	
	//取消入院登记读卡
	doYbqxdjdk:function(){
		alert("取消登记病号信息" +Ext.encode(this.ybbhxx));
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
		alert("住院结算读卡病号信息" +Ext.encode(this.ybbhxx));
	},	
	onQr_zyjs : function(ybxx) {
		this.ybkxx = ybxx;
	},
	// 住院预结算
	doYbzyYjs : function() {
		var ret = {};
		alert("住院预结算病号信息" +Ext.encode(this.ybbhxx));
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