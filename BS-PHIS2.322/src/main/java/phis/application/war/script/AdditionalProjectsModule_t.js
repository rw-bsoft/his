$package("phis.application.war.script");

$import("phis.script.SimpleModule","util.helper.Helper","phis.script.widgets.MyMessageTip");
phis.application.war.script.AdditionalProjectsModule_t = function(cfg) {
	this.printurl = util.helper.Helper.getUrl();
	this.westWidth = cfg.westWidth || 250;
	this.showNav = true;
	this.height = 450;
	this.commitType = "patientXMTab"; //提交类型，有按项目提交projectCommitTab 和按病人提交patientCommitTab  在Applications.xml中配置
	phis.application.war.script.AdditionalProjectsModule_t.superclass.constructor.apply(
			this, [cfg]);
	this.on("winShow", this.onWinShow, this);
	this.on("close", this.onWinClose, this);// 关闭时抛出事件
},

Ext.extend(phis.application.war.script.AdditionalProjectsModule_t,
		phis.script.SimpleModule, {
	onWinShow : function() {
		if (this.listmodule) {
			this.refreshData();
		}
	},
	initPanel : function() {
		if(!this.mainApp['phis'].wardId){
			MyMessageTip.msg("提示", "当前不存在病区，请先选择病区信息!", true);
			return;
		}
		if (this.panel) {
			return this.panel;
		}
		this.actTab = "patientXMTab";
		this.projectInitFlag = false;//项目提交初始化标志
		this.patientInitFlag = false;//病人提交初始化标志
		var tbar = [];
		//获取Applications.xml中配置的按钮(如打印、保存、取消等)
		var actions = this.actions;
		for (var i = 0; i < actions.length; i++) {
			var action = actions[i];
			var btn = {};
			btn.id = action.id;
			btn.accessKey = "F1", btn.cmd = action.id;
			btn.text = action.name, btn.iconCls = action.iconCls
					|| action.id;
			//添加doAction点击事件,调用doAction方法
			btn.handler = this.doAction;
			btn.name = action.id;
			btn.notReadOnly = action.properties.notReadOnly;
			if (action.properties.notReadOnly == "true"){//设置为启用标志
				btn.disabled = false;
			}else{//设置为禁用标志
				btn.disabled = true;
			}
			btn.scope = this;
			tbar.push(btn);
		}
		//创建一个Panel，该Panel中包含phis.script.med.WardProjectTabModule
		var panel = new Ext.Panel({
			border : false,
			frame : true,
			layout : 'border',
			width : this.width,
			height : this.height,
			tbar : tbar,
			items : [ {
						layout : "fit",
						split : true,
						title : '',
						region : 'center',
						width : 280,
						items : this.getList()
					}]
		});
		this.panel = panel;
		// 如果是子窗口就显示关闭按钮，否则不显示
		if (this.initDataId) {
			al_zyh = this.initDataId;
		} else {
			var btns = this.panel.getTopToolbar();
			var btn = btns.find("cmd", "close");
			btn[0].hide();
		}
		return panel;
	},
	doAction : function(item, e) {
		if(item.id == "determine"){//确定按钮执行
			this.determine();
		}else if(item.id == "refresh"){//刷新按钮
			this.refresh();
		}else if(item.id == "close"){
			this.doClose();
		}else if(item.id == "print"){
			this.doPrint();
		}
	},
	/**
	 * 确认按钮
	 */
	determine : function(){
		Ext.Msg.confirm("请确认", "是否打印?", function(btn1) {
								if (btn1 == 'yes') {
								this.doPrint(1);
								this.doDetermine();
								}else{
								this.doDetermine();	
								}
								},this)
		 
		
	},
	doDetermine:function(){
	var ctx_ = this.midiModules["getList"].midiModules[this.midiModules["getList"].tab.getActiveTab().id];
		 //var ctx2_ = this.midiModules["getList"].midiModules["projectTab"];
		 var records = ctx_.detailsList.getSelectedRecords();
			var body = {};
			body['bq'] = this.mainApp['phis'].wardId;
			var r = phis.script.rmi.miniJsonRequestSync({
						serviceId : "configLogisticsInventoryControlService",
						serviceAction : "verificationWPJFBZ",
						body : body
					});
			if (r.code > 300) {
				this.processReturnMsg(r.code, r.msg, this.determine);
				return;
			}
			var bodys = [];
			for (var i = 0; i < records.length; i++) {
				var body = {};
				// body["FYRQ"] = records[i].json.QRSJ; // 费用日期
				body["FYMC"] = records[i].json.YZMC; // 费用名称
				body["FYSL"] = records[i].json.YCSL * records[i].json.FYCS;// 费用数量
				body["FYDJ"] = records[i].json.YPDJ;// 费用单价
				body["ZJJE"] = records[i].json.FYCS * records[i].json.YPDJ * records[i].json.YCSL;// 总计金额
				body["YSGH"] = records[i].json.YSGH;// 医生工号
				body["SRGH"] = records[i].json.CZGH;// 输入工号
				body["FYKS"] = records[i].json.BRKS;// 费用
				body["JLXH"] = records[i].json.JLXH;// 医嘱序号
				body["ZYH"] = records[i].json.ZYH;// 住院号
				body["FYXH"] = records[i].json.YPXH;// 费用序号
				body["YPCD"] = records[i].json.YPCD;// 药品产地
				body["XMLX"] = records[i].json.XMLX;// 项目类型
				body["YPLX"] = records[i].json.YPLX;// 药品类型
				body["BRXZ"] = records[i].json.BRXZ;// 病人性质
				body["ZXKS"] = records[i].json.ZXKS;// 执行科室
				body["YZXH"] = records[i].json.YZXH;// 医嘱序号
				body["OK"] = 1;// 是否选择
				/** 2013-08-13 add by gejj 判断病人欠费功能使用**/
				body["JE"] = records[i].json.FYCS * records[i].json.YPDJ * records[i].json.YCSL;// 总计金额
				body["YPXH"] = records[i].json.YPXH;// 费用序号
				/** 2013-08-13 end**/
				bodys[i] = body;
				if (records[i].json.FHGH==this.mainApp.uid){
					MyMessageTip.msg("提示","【"+records[i].json.YZMC+"的】"+"复核护士和执行护士不能是同一个人！",true);
					return;
				}
			}
			var body2 = [];
			var r = phis.script.rmi.miniJsonRequestSync({
						serviceId : "doctorAdviceExecuteService",
						serviceAction : "additionProjectsFeeQuery",
						cnds : bodys,
						cnd : this.initDataId
					});
			if(r.code < 300){
			    var resbody = r.json.body;
				for (var i = 0; i < resbody.length; i++) {
					var body = {};
					body["FYMC"] = resbody[i].YZMC; // 费用名称
					body["FYSL"] = resbody[i].YCSL * resbody[i].FYCS;// 费用数量
					body["FYDJ"] = resbody[i].YPDJ;// 费用单价
					body["ZJJE"] = resbody[i].FYCS * resbody[i].YPDJ * resbody[i].YCSL;// 总计金额
					body["YSGH"] = resbody[i].YSGH;// 医生工号
					body["SRGH"] = resbody[i].CZGH;// 输入工号
					body["FYKS"] = resbody[i].BRKS;// 费用
					body["JLXH"] = resbody[i].JLXH;// 医嘱序号
					body["ZYH"] =  resbody[i].ZYH;// 住院号
					body["FYXH"] = resbody[i].FYXH;// 费用序号
					body["YPXH"] = resbody[i].FYXH;// 费用序号
					body["YPCD"] = 0;// 药品产地
					body["XMLX"] = resbody[i].XMLX;// 项目类型
					body["YPLX"] = resbody[i].YPLX;// 药品类型
					body["BRXZ"] = resbody[i].BRXZ;// 病人性质
					body["ZXKS"] = resbody[i].ZXKS;// 执行科室
					body["YZXH"] = resbody[i].YZZH;// 医嘱序号
					body["QRSJ"] = resbody[i].QRSJ;// 医嘱序号
					body["LSBZ"] = resbody[i].LSBZ;// 医嘱序号
					body["JE"] = resbody[i].FYCS * resbody[i].YPDJ * resbody[i].YCSL;// 总计金额
					body2[i] = body;
				}
			}
			if(bodys.length == 0 && body2.length == 0){
				Ext.Msg.alert("提示", "未选择记录,不需要执行!");
				return;
			}
			ctx_.panel.el.mask("正在确认数据,时间可能很长,请耐心等待...", "x-mask-loading");
			var ret = phis.script.rmi.miniJsonRequestSync({
						serviceId : "doctorAdviceExecuteService",
						serviceAction : "saveConfirm",
						body : bodys,
						body2:body2
					});
			ctx_.panel.el.unmask();
			if (ret.code > 300) {
				ctx_.processReturnMsg(ret.code, ret.msg, ctx_.doSave);
				this.refresh();
				return;
			}
			// 执行完操作 去掉钩钩。
			ctx_.detailsList.clearSelect();
			this.refresh();
			//this.listmodule.tab.activate(0);//重新激活病区项目执行页
			if(ret.json.RES_MESSAGE){
				MyMessageTip.msg("提示", ret.json.RES_MESSAGE, true);
			}else{
				MyMessageTip.msg("提示", "确认成功", true);
			}
	},
	refresh : function(){
		this.refreshData();
	},
	/**
	 * 确认成功，重写刷新数据
	 */
	refreshData : function(){
			if(this.initDataId){
			    this.midiModules["getList"].midiModules["projectPatientTab"].initDataId = this.initDataId;
			    this.midiModules["getList"].midiModules["patientXMTab"].initDataId = this.initDataId;
			}
			if(this.midiModules["getList"].midiModules["patientXMTab"]){
			this.midiModules["getList"].midiModules["patientXMTab"].doRefresh();
			}
			if(this.midiModules["getList"].midiModules["projectPatientTab"]){
			this.midiModules["getList"].midiModules["projectPatientTab"].doRefresh();
			}
//			if(this.midiModules["getList"].midiModules["projectTab"]){
//			this.midiModules["getList"].midiModules["projectTab"].doRefresh();
//			}
	},
	/**
	 * 创建模型ID为WAR1401的模型
	 * @returns
	 */
	getList : function() {
		this.listmodule = this.createModule("getList", this.refList);
		this.listmodule.on("beforetabchange",this.commitTypeChange,this);
		this.listmodule.initDataId = this.initDataId; 
		return this.listmodule.initPanel();

	},
	commitTypeChange: function(type){
		this.actTab = type;
		if(type == "projectTab"){
			this.proTab = true;//定义费用医嘱附加计价单Tab页已被加载过
			 var ctx_ = this.midiModules["getList"].midiModules["patientXMTab"];
			 //var ctx2_ = this.midiModules["getList"].midiModules["projectTab"];
			 var records = ctx_.detailsList.getSelectedRecords();
			 if(this.initDataId){
				    this.midiModules["getList"].midiModules["projectTab"].initDataId = this.initDataId;
			 }
			 var bodys = [];
			 if(records){
				 for (var i = 0; i < records.length; i++) {
					var body = {};
					body["YZXH"] = records[i].json.YZXH;// 医嘱序号
					bodys[i] = body;
				} 
			 }
			
			this.midiModules["getList"].midiModules["projectTab"].bodys = bodys;
			this.midiModules["getList"].midiModules["projectTab"].loadData();
		}else{
			if(this.proTab){//费用医嘱附加计价单Tab被激活说明需要刷新
				this.refresh();
			}
		}
	},
	// 关闭
	doClose : function() {
		var win = this.getWin();
		if (win)
			win.hide();
	},
	onWinClose : function() {
		this.fireEvent("doSave");
	},
	//打印
	doPrint:function(tag){
	 var ctx_ = this.midiModules["getList"].midiModules[this.midiModules["getList"].tab.getActiveTab().id];
	 var records = ctx_.detailsList.getSelectedRecords();
			var body = {};
			body['bq'] = this.mainApp['phis'].wardId;
			var r = phis.script.rmi.miniJsonRequestSync({
						serviceId : "configLogisticsInventoryControlService",
						serviceAction : "verificationWPJFBZ",
						body : body
					});
			if (r.code > 300) {
				this.processReturnMsg(r.code, r.msg, this.determine);
				return;
			}
		var jlxhs="";
		var length=records.length;
		if(length==0){
		MyMessageTip.msg("提示","请选择打印记录",true);
		return;
		}
		for(var i=0;i<length;i++){
		if(i!=(length-1)){
		jlxhs+=records[i].get("JLXH")+",";
		}else{
		jlxhs+=records[i].get("JLXH")
		}
		}
		var url = "resources/phis.prints.jrxml.AdditionalProjects.print?type=1&JLXHS="
						+  jlxhs;
				var LODOP = getLodop();
				LODOP.PRINT_INIT("打印控件");
				LODOP.SET_PRINT_PAGESIZE("0", "", "", "");
				LODOP.ADD_PRINT_HTM("0", "0", "100%", "100%", util.rmi.loadXML(
								{
									url : url,
									httpMethod : "get"
								}));
				LODOP.SET_PRINT_MODE("PRINT_PAGE_PERCENT", "Full-Width");
				if(tag==1){
					LODOP.PRINT();
				}else{
				LODOP.PREVIEW();
				}
			
	}
	
})