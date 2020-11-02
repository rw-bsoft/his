$package("phis.application.reg.script");
$import("phis.script.SimpleList","util.helper.Helper","phis.application.reg.script.FsyyManageForm",
		"phis.script.Phisinterface");

phis.application.reg.script.FsyyManageList=function(cfg) {
	cfg.autoLoadData=false;
	this.serverParams = {
		serviceAction : cfg.queryActionId
	};
	cfg.initCnd=['in',['$','a.HYZT'],['2','4']];
	Ext.apply(this,phis.script.Phisinterface);
	phis.application.reg.script.FsyyManageList.superclass.constructor.apply(this,[cfg]);
	this.printurl=util.helper.Helper.getUrl();
};
Ext.extend(phis.application.reg.script.FsyyManageList,phis.script.SimpleList,{
	doTakeTheNumber:function(){
		//前台取消此功能，只在app端取号
		var r=this.getSelectedRecord();
		if(!r) return MyMessageTip.msg("提示","请选择一条记录!",true);
		var data=r.data;
		if(data.HYZT!="2") return MyMessageTip.msg("提示","非已预约状态不能取号!",true);
		var qhform =this.createModule("yyqhform","phis.application.reg.REG/REG/REG0901")
		var win=qhform.getWin();
		win.add(qhform.initPanel())
		win.show();
		data.HJJE=data.GHF+data.ZLF;
		qhform.initFormData(r.data);
	}
	,doRetreat:function(){
		debugger;
		var r=this.getSelectedRecord();
		if(!r) return MyMessageTip.msg("提示","请选择一条记录!",true);
		var data=r.data;
		if(data.HYZT!="4") return MyMessageTip.msg("提示","非已预约状态不能退号!",true);
		var re=phis.script.rmi.miniJsonRequestSync({
				serviceId : "phis.registeredManagementService",
				serviceAction : "saveRetreatYyxx",
				HYXH:data.HYXH
			});
		if(re.code>300){
			MyMessageTip.msg("友情提示",re.msg,true);
		}else{
			if(data.XJJE>0)
			MyMessageTip.msg("友情提示","不要忘了退款"+data.XJJE+"圆给病人哦!",true);
		}
		this.refresh();
	}
	,doTranNh:function(){
		var r=this.getSelectedRecord();
		if(!r) return MyMessageTip.msg("提示","请选择一条记录!",true);
		var HYXH=r.data.HYXH;
		if(HYXH!=null && typeof(HYXH)=="number"){
			var re=phis.script.rmi.miniJsonRequestSync({
				serviceId : "phis.registeredManagementService",
				serviceAction : "saveTurnToNh",
				HYXH:HYXH
			});
			if(re.json.turnMsg.length>0){
				MyMessageTip.msg("提示",re.json.turnMsg,true);
			}else{
				MyMessageTip.msg("提示","转换成功",true);
			}
		}
		this.refresh();
	}
	,doTranYb:function(){
		debugger;
		var r=this.getSelectedRecord();
		if(!r) return MyMessageTip.msg("提示","请选择一条记录!",true);
		if(this.mainApp.serverDate!=r.data.HYSJ.substring(0,10)){
			return MyMessageTip.msg("提示","只能转换当天的号!",true)
		}
		var HYXH=r.data.HYXH;
		//先执行读卡程序
		var body={};
		body.USERID=this.mainApp.uid;
		//获取业务周期号
		var ret=phis.script.rmi.miniJsonRequestSync({
				serviceId : "phis.NjjbService",
				serviceAction : "getywzqh",
				body:body
			});
		if (ret.code<=300){
			var ywzqh=ret.json.YWZQH;
			this.addPKPHISOBJHtmlElement();
			this.drinterfaceinit();
			var str=this.buildstr("2100",ywzqh,"");
			var drre=this.drinterfacebusinesshandle(str);
			var arr=drre.split("^");
			if(arr[0]=="0"){
				var canshu=arr[2].split("|")
				var data={};
				data.SHBZKH=canshu[0];//社会保障卡号
				data.DWBH=canshu[1];//单位编号
				data.DWMC=canshu[2];//单位名称
				data.SFZH=canshu[3];//身份证号
				data.XM=canshu[4];//姓名
				data.XB=canshu[5];//性别
				data.YLRYLB=canshu[6];//医疗人员类别
				data.YDRYBZ=canshu[7];//异地人员标志
				data.TCQH=canshu[8];//统筹区号
				data.DQZHYE=canshu[9];//当前帐户余额
				data.ZYZT=canshu[10];//在院状态
				data.BNZYCS=canshu[11];//本年住院次数
				data.DYXSBZ=canshu[12];//待遇享受标志
				data.DYBXSYY=canshu[13];//待遇不享受原因
				data.BZDJQK=canshu[14];//病种登记情况
				data.YBMMZG=canshu[15];//医保门慢资格
				data.YBMMBZ=canshu[16];//医保门慢病种
				data.YBMJZG=canshu[17];//医保门精资格
				data.YBMJBZ=canshu[18];//医保门精病种
				data.YBMAZG=canshu[19];//医保门艾资格
				data.YBMABZ=canshu[20];//医保门艾病种
				data.YBBGGRSZG=canshu[21];//医保丙肝干扰素资格
				data.YBBGGRSBZ=canshu[22];//医保丙肝干扰素病种
				data.YBMZXYBZG=canshu[23];//医保门诊血友病资格
				data.YBMZXYBBZ=canshu[24];//医保门诊血友病病种
				data.YBMTZG=canshu[25];//医保门特资格
				data.YBMTBZ=canshu[26];//医保门特病种
				data.YBTYZG=canshu[27];//医保特药资格
				data.YBTYBZ=canshu[28];//医保特药病种
				data.YBTYMCBM=canshu[29];//医保特药名称编码
				data.JMMDZG=canshu[30];//居民门大资格
				data.JMMDBZ=canshu[31];//居民门大病种
				data.JMMZXYBZG=canshu[32];//居民门诊血友病资格
				data.JMMZXYBBZ=canshu[33];//居民门诊血友病病种
				data.JMTYZG=canshu[34];//居民特药资格
				data.JMTYBZ=canshu[35];//居民特药病种
				data.JMTYMCBM=canshu[36];//居民特药名称编码
				data.NMGMDZG=canshu[37];//农民工门大资格
				data.NMGMDBZ=canshu[38];//农民工门大病种
				data.NMGTYZG=canshu[39];//农民工特药资格
				data.NMGTYBZ=canshu[40];//农民工特药病种
				data.NMGTYMCBM=canshu[41];//农民工特药名称编码
				data.NFXSZGMZTC=canshu[42];//能否享受职工门诊统筹
				data.SYSPLX=canshu[43];//生育审批类型
				data.FSYY=canshu[44];//封锁原因
				data.MMSYKBJE=canshu[45];//门慢剩余可报金额
				data.MTFZZLSYKBJE=canshu[46];//门特辅助治疗剩余可报金额
				data.GSDYZG=canshu[47];//工伤待遇资格
				data.GSDYBZ=canshu[48];//工伤待遇病种
				data.GSZDJL=canshu[49];//工伤诊断结论
				data.DKSYKBJE=canshu[50];//大卡剩余可报金额
				data.MTSYKBJE=canshu[51];//门统剩余可报金额
				data.YBJCZG=canshu[52];//医保家床资格
				//验证卡与病人是否对应
				var ck=phis.script.rmi.miniJsonRequestSync({
						serviceId : "phis.registeredManagementService",
						serviceAction : "checkPerson",
						HYXH:HYXH,
						SFZH:data.SFZH
					});
				if(ck.code<=300){
					if(ck.json.checkre.CHECK==2){
						return MyMessageTip.msg("提示：","所刷卡的身份证和病人预约时的身份证不匹配，不能转换！",true);
					}else{
						if(ck.json.checkre.CHECK==1){
							//执行保存医保信息
							var req=phis.script.rmi.miniJsonRequestSync({
								serviceId : "phis.NjjbService",
								serviceAction : "savenjjbkxx",
								body:data
							});
							if(req.code>300){
								MyMessageTip.msg("提示：",req.msg,true);
							}
						}
						var yyxx={};
						yyxx.HYXH=HYXH;
						yyxx.KSDM=ck.json.checkre.KSDM;
						yyxx.LXDH=ck.json.checkre.LXDH;
						//弹出选择医保登记信息界面
						var ybdjmodule = this.createModule("ybdjmodule","phis.application.reg.REG/REG/REG0111");
						var panel=ybdjmodule.initPanel();
						ybdjmodule.on("saveTran",this.refresh,this);
						ybdjmodule.ybkxx=data;
						ybdjmodule.yyxx=yyxx;
						var win = ybdjmodule.getWin();
						win.add(panel);
						win.show();
					}
				}else{
					return MyMessageTip.msg("提示：",ck.msg,true);
				}
			}else{
				return MyMessageTip.msg("提示：","金保返回错误:"+drre+",HIS系统提示读卡器可能没连接或没插卡！",true);
			}
		}else{
			return MyMessageTip.msg("提示：",ret.msg,true);
		}
	}
	,doCndQuery:function(){
		var initCnd = this.initCnd
		var itid = this.cndFldCombox.getValue()
		var items = this.schema.items
		var it
		for (var i = 0; i < items.length; i++) {
			if (items[i].id == itid) {
				it = items[i]
				break
			}
		}
		if(!it){
			return;
		}
		this.resetFirstPage()
		var f = this.cndField;
		var v = f.getValue()
		var rawV = f.getRawValue();
		var xtype = f.getXType();
		if ((Ext.isEmpty(v) || Ext.isEmpty(rawV)) && (xtype !== "MyRadioGroup" && xtype !== "MyCheckboxGroup")) {
			this.queryCnd = null;
			this.requestData.cnd = initCnd
			this.refresh()
			return
		}
		if(f.getXType() == "datefield"){
			v = v.format("Y-m-d")
		}
		if(f.getXType() == "datetimefield"){
			v = v.format("Y-m-d H:i:s")
		}
		//替换'，解决拼sql语句查询的时候报错
		v=v.replace(/'/g, "''")
		var refAlias = it.refAlias || "a"
		var cnd = ['eq',['$',refAlias + "." + it.id]]
		if(it.dic){
			var expType=this.getCndType(it.type)
			if(it.dic.render == "Tree"){
				var node =  this.cndField.selectedNode;
				if(!node || node.isLeaf()){
					cnd.push([expType,v]);
				}else{
					cnd[0] = 'like'
					cnd.push([expType,v + '%'])
				}
			}
			else{
				cnd.push([expType,v])
			}
		}
		else{
			switch(it.type){
				case 'int':
					cnd.push(['i',v])
					break;
				case 'double':
				case 'bigDecimal':
					cnd.push(['d',v])
					break;
				case 'string':
					cnd[0] = 'like'
					cnd.push(['s',v + '%'])
					break;
				case "date":
					if(v.format){
				       v = v.format("Y-m-d")
				    }
					cnd[1] = ['$', "str(" + refAlias + "." + it.id + ",'yyyy-MM-dd')"]
					cnd.push(['s',v])
					break;
				case 'datetime':
				case 'timestamp':
					if (it.xtype == "datefield") {
						if(v.format){
							v = v.format("Y-m-d")
						}
						cnd[1] = ['$',"str(" + refAlias + "." + it.id + ",'yyyy-MM-dd')"]
						cnd.push(['s', v])
					} else {
						if(v.format){
							v = v.format("Y-m-d H:i:s")
						}
						cnd[0]="gt";
						cnd[1] = ['$',"str(" + refAlias + "." + it.id + ",'yyyy-MM-dd HH24:mi:ss')"]
						cnd.push(['s', v])
					}
					break;
			}
		}
		this.queryCnd = cnd
		if(initCnd){
			cnd = ['and',initCnd,cnd]
		}
		this.requestData.cnd = cnd
		this.refresh();
	}
});