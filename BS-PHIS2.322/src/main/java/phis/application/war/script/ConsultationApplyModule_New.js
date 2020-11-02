$package("phis.application.war.script");

$import("phis.script.SimpleModule");

phis.application.war.script.ConsultationApplyModule_New = function(cfg) {

	cfg.width = 700;
	cfg.height = 1200;
	phis.application.war.script.ConsultationApplyModule_New.superclass.constructor
			.apply(this, [cfg]);
	this.on("winShow", this.onWinShow, this);
};

Ext.extend(phis.application.war.script.ConsultationApplyModule_New,
		phis.script.SimpleModule, {

			onWinShow : function() {
				if (this.form) {
					// this.module.initDataId = this.zyh;
					this.module.loadData();
				}

			},
			initPanel : function() {
				var panel = new Ext.Panel({
							border : false,
							frame : false,
							layout : 'form',
							height : 390,
							tbar : ['', this.initConditionButtons()],
							items : [{
										layout : "fit",
										region : 'north',
										height : 390,
										items : this.getForm()
									}]
						});
				this.panel = panel
				return panel
			},
			
			initConditionButtons : function() {
				var flag = false;
				var items = []
				items.push({
							xtype : "button",
							text : "确认",
							iconCls : "arrow_switch",
							scope : this,
							handler : this.doSure,
							disabled : flag
						})
				items.push({
					xtype : "button",
					text : "取消",
					iconCls : "common_cancel",
					scope : this
						// handler : this.doClose
					})
				return items
			},
			getForm : function() {

				var module = this.midiModules['consultationApply_NewForm'];
				if (!module) {
					module = this.createModule("consultationApply_NewForm",
							"WAR1303");
					module.exContext = this.exContext;
					this.module = module;
					var form = module.initPanel();
					this.form = form;
				}
				//this.module.doNew();

				return form;
			},
			doSure : function() {
				var form = this.form.getForm();
				var ryrq = this.exContext.empiData.RYRQ;
				var val = form.findField("HZSJ").getValue();
				var indexOf = val.indexOf(":");
				var bb = val.substring(0, indexOf-3);

				var hzmd = form.findField("HZMD").getValue();// 会诊目的
				var yqdx = form.findField("YQDX").getValue();// 邀请对象
				var zlqk=form.findField("BQZL").getValue();

				


	
				var flag = compareDate(bb, ryrq);
                if (val == ""||val==null) {
					Ext.Msg.alert("提示","会诊日期不能为空");
					return;
				}
				if (flag == -1) {
					Ext.Msg.alert("提示","会诊日期不能小于入院日期");
					return;
				}
				if (hzmd == '' || hzmd == null) {
					Ext.Msg.alert("提示","会诊目的不能为空");
					return;
				}
				
				if (zlqk == '' || zlqk == null) {
					Ext.Msg.alert("提示","治疗情况不能为空");
					return;
				}
				if (yqdx == '' || yqdx == null) {
					Ext.Msg.alert("提示","会诊者不能为空");
					return;
				}
				console.debug("this.exContext.empiData:{}",this.exContext.empiData);
				var body = {};
				body = {
					"zyh" : this.exContext.empiData.ZYH,
					"sqks" : this.exContext.empiData.BRKS,
					"sqys" : this.exContext.empiData.ZSYS,
					//病人病区
					"brbq": this.exContext.empiData.BRBQ,
					//病人床号
					"brch":this.exContext.empiData.BRCH,
					"sqsj" : form.findField("SQSJ").getValue(),
					"hzmd" : hzmd,
					"hzsj" : form.findField("HZSJ").getValue(),
					"yqdx" : yqdx,
					"bqzl" : zlqk,
					"jjbz" : form.findField("JJBZ").getValue()
				}
				/** 保存会诊新增记录* */
				var rr = phis.script.rmi.miniJsonRequestSync({
							serviceId : "sqService",
							serviceAction : "saveorupdate",
							body : body
						});
				if (rr.code > 300) {
					this.processReturnMsg(r.code, r.msg);
					return
				} else {
					MyMessageTip.msg("提示", "添加会诊记录成功！", true);
					this.doClose();
					// add by yangl 转为抛出事件
					this.fireEvent("doSave");
					this.opener.loadData();
				}
			}

		});
function compareDate(checkStartDate, checkEndDate) {
	var arys1 = new Array();
	var arys2 = new Array();
	if (checkStartDate != null && checkEndDate != null) {
		arys1 = checkStartDate.split('-');
		var sdate = new Date(arys1[0], parseInt(arys1[1] - 1), arys1[2]);
		arys2 = checkEndDate.split('-');
		var edate = new Date(arys2[0], parseInt(arys2[1] - 1), arys2[2]);

		if (sdate > edate) {
			return 0;
		} else {

			return -1;
		}
	}
}