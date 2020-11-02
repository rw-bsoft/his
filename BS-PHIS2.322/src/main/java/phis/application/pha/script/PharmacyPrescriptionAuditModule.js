$package("phis.application.pha.script")
$import("phis.script.SimpleModule")

phis.application.pha.script.PharmacyPrescriptionAuditModule = function(cfg) {
	cfg.autoLoadData = false;
	phis.application.pha.script.PharmacyPrescriptionAuditModule.superclass.constructor.apply(this, [cfg]);
}

Ext.extend(phis.application.pha.script.PharmacyPrescriptionAuditModule,
		phis.script.SimpleModule, {
			initPanel : function(sc) {
				if(this.mainApp['phis'].pharmacyId==null||this.mainApp['phis'].pharmacyId==""||this.mainApp['phis'].pharmacyId==undefined){
					Ext.Msg.alert("提示","未设置登录药房,请先设置");
					 return null;
				} 
				//初始化判断 
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.queryInitActionId
						}); 
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.initPanel);
					return null;
				} 
				//判断是否启用门诊审核
				var ret=phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.queryEnableActionId
						}); 
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg,this.createButton);
					return;
				} 
				 var enable=ret.json.enable;
				this.enableAutit = enable == '1' ? true : false;
				if(!this.enableAutit) {
					Ext.Msg.alert("提示","未启用门诊审方标志"); 
					  return;
				}
				if (this.panel) {
					return this.panel;
				}
				var panel = new Ext.Panel({
					border : false,
					frame : true,
					layout : 'border',
					defaults : {
						border : false
					},
					tbar : this.createButton(),
					items : [{
						layout : "fit",
						border : false,
						split : true,
						region : 'west',
						collapseMode : 'mini',
						width : 380,
						items : this.getPrescriptionList()
					},{
						layout : "fit",
						border : false,
						split : true,
						region : 'center',
						items : this.getAuditModule()
					}]
				});
				this.panel = panel;
				return panel;
			},
			createButton : function() {
				var tbar = [];
				
				var actions = this.actions;
				for (var i = 0; i < actions.length; i++) {
					var action = actions[i];
					var btn = {}
					btn.id = action.id;
					btn.disabled = !this.enableAutit;
					if(action.id == 'refresh') {
						btn.disabled = false;
					}
					btn.accessKey = "F1", btn.cmd = action.id
					btn.text = action.name, btn.iconCls = action.iconCls
							|| action.id
					btn.handler = this.doAction;
					btn.name = action.id;
					btn.scope = this;
					tbar.push(btn);
					tbar.push('-');
				}
				return tbar;
			},
			getPrescriptionList : function() { 
				this.prescriptionList = this.createModule("prescription_list", this.refPrescriptionList);
				this.prescriptionList.on("prescriptionClick",this.prescriptionClick, this);
				this.prescriptionList.on("radioChange", this.radioClick, this);
				return this.prescriptionList.initPanel();
			},
			getAuditModule : function() {
				this.prescriptionModule = this.createModule("prescription_audit_list", this.refAuditList);
				return this.prescriptionModule.initPanel();
			},
			radioClick : function(checkedValue) {
				checkedValue=0;
				var tbar = this.panel.getTopToolbar();
//				tbar.getComponent('allAdopt').setDisabled(checkedValue != 0);
				tbar.getComponent(2)[checkedValue !=0 ? "hide":"show"]();
				tbar.getComponent(3)[checkedValue !=0 ? "hide":"show"]();
			},
			prescriptionClick : function(data) {
				if(data == null ) {
					this.getPrescriptionForm().doNew();
					this.getAuditDetailList().clear();
					this.getAuditDetailList().setCountInfo();
					return
				}
				this.reconfigureByCflx(data['CFLX'], this.getAuditDetailList().grid.getColumnModel());	//草药明细与中西药不同
				this.getAuditDetailList().loadData(data);
				this.getPrescriptionForm().initDataId = data['CFSB'];
				this.getPrescriptionForm().loadData(data);
			},
			//根据处方类型重配列表表头定义
			reconfigureByCflx : function(cflx, colModel) {
				if (cflx == 3) {
					colModel.setColumnHeader(9, "每帖数量");
					colModel.setHidden(colModel.getIndexById('YPZS_STR'), false);
					colModel.setHidden(colModel.getIndexById('YYTS'), true);
				} else {
					colModel.setColumnHeader(9, "总量");
					colModel.setHidden(colModel.getIndexById('YPZS_STR'), true);
					colModel.setHidden(colModel.getIndexById('YYTS'), false);
				}
			},
			doAllAdopt : function() {
				this.getAuditDetailList().allAdopt(this);
			},
			doRefresh : function() {
				this.prescriptionList.loadData();
			},
			doSave : function() {
				this.getAuditDetailList().save(this);
			},
			//返回处方审核详细列表
			getAuditDetailList : function() {
				return this.prescriptionModule.getAuditDetailList();
			},
			getPrescriptionForm : function() {
				return this.prescriptionModule.getPrescriptionForm();
			}
		});