$package("phis.application.cic.script");
$import("phis.script.SimpleModule", "util.helper.Helper");

phis.application.cic.script.ClinicHospitalAppointmentModule = function(cfg) {
	phis.application.cic.script.ClinicHospitalAppointmentModule.superclass.constructor
			.apply(this, [ cfg ]);
};
Ext.extend(phis.application.cic.script.ClinicHospitalAppointmentModule,
		phis.script.SimpleModule, {
			initPanel : function() {
				var panel = new Ext.Panel({
					border : false,
					frame : true,
					layout : 'border',
					defaults : {
						border : false
					},
					width : 800,
					height : 450,
					tbar : [ '', this.initConditionButtons() ],
					items : [ {
						layout : "fit",
						region : 'north',
						height : 80,
						items : this.getForm()
					}, {
						layout : "fit",
						region : 'center',
						items : this.getKSList()
					} ]
				});
				this.panel = panel;
				return panel;
			},
			initConditionButtons : function() {
				var items = []
				items.push({
					xtype : "button",
					text : "生成",
					iconCls : "common_add",
					scope : this,
					handler : this.doAdvance
				})
				items.push({
					xtype : "button",
					text : "取消",
					iconCls : "common_remove",
					scope : this,
					handler : this.doCancelAdvance
				})
				items.push({
					xtype : "button",
					text : "关闭",
					iconCls : "common_cancel",
					scope : this,
					handler : this.doClose
				})
				return items
			},
			getForm : function() {
				this.yyform = this.createModule("refFormyy", this.refForm);
				this.yyform.birthday = this.brxx.birthday;
				this.yyform.jzxh = this.jzxh;
				this.yyform.on("loadList",this.onLoadList,this);
				return this.yyform.initPanel();
			},
			getKSList : function() {
				this.yylist = this.createModule("refListyy", this.refList);
				return this.yylist.initPanel();
			},
			loadData : function(BRID) {
				this.yyform.initDataId = BRID;
				this.yyform.loadData();
				this.yylist.loadData();
			},
			doAdvance : function() {
				var r = this.yylist.getSelectedRecord()
				if (r == null) {
					MyMessageTip.msg("提示", "请选择需要预约的科室!", true);
					return
				}
				var yyks=r.data.YYKS;
				var body = {
					"BRID" : this.brxx.BRID,
					"YYKS" : r.data.YYKS,
					"CSNY" : this.brxx.birthday,
					"YYRQ" : new Date(this.yyform.form.getForm().findField("YYRQ").getValue()).format('Y-m-d')
				};
				this.panel.el.mask("正在保存...", "x-mask-loading");
				var r = phis.script.rmi.miniJsonRequestSync({
					serviceId : this.serviceId,
					serviceAction : this.serviceActionSave,
					body : body
				});
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg, this.onBeforeSave);
					this.panel.el.unmask();
					return false;
				} else {
					MyMessageTip.msg("提示", "通知成功!", true);
					this.panel.el.unmask();
					this.yylist.loadData(new Date(this.yyform.form.getForm().findField(
					"YYRQ").getValue()).format('Y-m-d'));
					this.doPrint(yyks);
				}
			},
			doCancelAdvance : function() {
				this.panel.el.mask("正在取消...", "x-mask-loading");
				var r = phis.script.rmi.miniJsonRequestSync({
					serviceId : this.serviceId,
					serviceAction : this.serviceActionUpdate,
					"BRID" : this.brxx.BRID
				});
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg, this.onBeforeSave);
					this.panel.el.unmask();
					return false;
				} else {
//					MyMessageTip.msg("提示", "取消成功!", true);
					this.yylist.loadData(new Date(this.yyform.form.getForm().findField(
					"YYRQ").getValue()).format('Y-m-d'));
				}
				this.panel.el.unmask();
			},
			doClose : function() {
				var win = this.getWin();
				if (win) {
					win.hide();
				}
			},
			onLoadList:function(yyrq){
				this.yylist.loadData(yyrq);
			},
			doPrint : function(yyks) {
				this.printurl = util.helper.Helper.getUrl();
				var pages = "phis.prints.jrxml.HospitalAppointment";
				var url = "resources/" + pages + ".print?silentPrint=1"
				url += "&temp=" + new Date().getTime()+ "&brid="+ this.brxx.BRID + "&yyks="+ yyks+ "&yyrq="
						+ new Date(this.yyform.form.getForm().findField("YYRQ").getValue()).format('Y-m-d') 
						+ "&jzxh="+this.jzxh + "&flag=true";
				var LODOP = getLodop();
				LODOP.PRINT_INIT("打印控件");
				LODOP.SET_PRINT_PAGESIZE("0", "", "", "");
				LODOP.ADD_PRINT_HTM("0", "0", "100%", "100%", util.rmi.loadXML({url : url,httpMethod : "get"}));
				LODOP.SET_PRINT_MODE("PRINT_PAGE_PERCENT", "Full-Width");
				// 预览
				LODOP.PREVIEW();
			}
		});