$package("chis.application.fhr.script")

$import("app.modules.list.SimpleListView")

chis.application.fhr.script.FamilyContractBaseModule = function(cfg) {
	cfg.autoFieldWidth = false
	Ext.apply(this, app.modules.common)
	this.contractId = "";
	cfg.showButtonOnTop = true;
	chis.application.fhr.script.FamilyContractBaseModule.superclass.constructor
			.apply(this, [cfg])
	this.on("winShow", this.onWinShow, this)
}
Ext.extend(chis.application.fhr.script.FamilyContractBaseModule,
		app.modules.list.SimpleListView, {
			initPanel : function() {
				var panel = new Ext.Panel({
							border : false,
							frame : true,
							layout : 'border',
							height : 400,
							buttonAlign : "center",
							tbar : this.createButtons(),
							items : [{
										layout : "fit",
										split : true,
										collapsed : false,
										title : '',
										region : 'north',
										autoHeight : true,
										items : this.getBaseForm()
									}, {
										layout : "fit",
										split : true,
										title : '',
										region : 'center',
										items : this.getServiceList()
									}]

						});
				this.panel = panel
				return panel;

			},
			doSave : function() {
				//签约之前校验成员基本信息数据完整性
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.familyRecordService",
							serviceAction : "checkpeople",
							method : "execute",
							familyId : this.initDataId
						});
				if(result.json && result.json.tsmsg && result.json.tsmsg.length > 0){
					Ext.Msg.alert("提示",result.json.tsmsg+"请先完善");
					return;
				}
				var formData = this.form1.getFormData()
				if (!formData)
					return
				if(!formData.FC_Repre){
					formData.FC_Repre=this.form1.FC_Repre;
					this.form1.FC_Repre="";
				}
				var listData = this.list1.getListData()
				util.rmi.jsonRequest({
							serviceId : "chis.familyRecordService",
							pkey : this.contractId,
							familyId : this.initDataId,
							formData : formData,
							gridData : listData,
							serviceAction : "saveContractBase",
							method : "execute"
						}, function(code, msg, json) {
							this.grid.el.unmask()
							if (code > 300) {
								this.processReturnMsg(code, msg);
								return;
							}
							this.contractId = json.body.pkey;
							if (json.body.same == false) {
								alert("家庭成员的签约日期不可以修改！")
							}
							this.fireEvent("save");
							if (this.op == 'create') {
								this.op = "update"
							}
						}, this)
			},
			setButtonDisable : function(status) {
				var btns = this.panel.getTopToolbar().items;

				// if (this.exContext.control && !this.exContext.control.update)
				// {
				//			
				// btns.item(0).disable();
				// return;
				// }
				if (btns && btns.item(0)) {
					if (status) {
						btns.item(0).disable();
					} else {
						btns.item(0).enable();
					}
				}
			},
			getBaseForm : function() {
				var cfg = {}
				cfg.autoLoadSchema = false
				cfg.autoLoadData = false;
				cfg.showButtonOnTop = false
				cfg.isCombined = true
				cfg.mainApp = this.mainApp
				var cls = "chis.application.fhr.script.FamilyContractBaseFormView"
				var module
				if (!module) {
					$import(cls)
					module = eval("new " + cls + "(cfg)");
					this.form1 = module;
					var form = module.initPanel()
					this.form = form
					module.on("loadData", this.onFormLoadData, this);
					return form;
				}

			},
			getServiceList : function() {
				var cfg = {}
				cfg.autoLoadSchema = false
				cfg.autoLoadData = false;
				cfg.showButtonOnTop = true
				cfg.isCombined = true
				cfg.mainApp = this.mainApp
				cfg.FC_Repre = this.FC_Repre
				var cls = "chis.application.fhr.script.FamilyContractServiceList"
				var module
				if (!module) {
					$import(cls)
					module = eval("new " + cls + "(cfg)");
					this.list1 = module;
					var grid = module.initPanel()
					this.grid = grid
					return grid;
				}

			},
			getContractDate : function() {
				return this.form.getForm().findField("FC_Begin").getValue()
						.format("Y-m-d")
			},
			doNew : function() {
				this.contractId = "";
				this.form1.initDataId = this.contractId;
				this.form1.familyId = this.initDataId;
				this.setSaveBtnStatus(true);
				this.form1.doNew();
				this.list1.FC_Id = this.contractId;
				this.list1.selectRecord = null;
				this.list1.initDataId = this.initDataId;
				this.list1.requestData.FC_Id = "-";
				this.list1.requestData.cnd = ['eq', ['$', 'FC_Id'],
						['s', this.contractId]]
				this.list1.refresh();
			},
			loadData : function() {
				this.list1.selectRecord = null;
				this.list1.initDataId = this.initDataId;
				this.list1.FC_Id = this.contractId;
				this.list1.requestData.cnd = ['eq', ['$', 'FC_Id'],
						['s', this.contractId]]
				this.list1.refresh();
				this.form1.initDataId = this.contractId
				this.form1.familyId = this.initDataId;
				this.form1.loadData();
			},
			onFormLoadData : function() {
				var stopDate = this.form1.form.getForm()
						.findField("FC_Stop_Date").getValue();
				var endDate = this.form1.form.getForm().findField("FC_End")
						.getValue();
				var today = new Date();
				var en = true;
				if (stopDate || endDate.getTime() + 86400000 < today.getTime()) {
					en = false
				}
				this.setSaveBtnStatus(en);
			},
			setSaveBtnStatus : function(s) {
				this.setButtonDisable(!s);
			},
			doContactCard : function() {
				if (!this.contractId) {
					Ext.Msg.alert("提示", "请先保存签约记录。");
					return;
				}
				var url = "resources/chis.prints.template.";
				if(this.mainApp.deptId.indexOf("320481")==0 ||this.mainApp.deptId.indexOf("320111")==0){
					url+= "familyContractBaseLy";
				}else{
					url+= "familyContractBase";
				}
				url+= ".print?type=1&recordId="+this.contractId+"&temp=" + new Date().getTime()
				var win = window.open(url,"","height="+ (screen.height - 100)+ ", width="+ (screen.width - 10)
						+ ", top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no")

				if (Ext.isIE6) {
					win.print()
				} else {
					win.onload = function() {
						win.print()
					}
				}
			},
			doPrint : function() {
				if (!this.contractId) {
					Ext.Msg.alert("提示", "请先保存签约记录。");
					return;
				}
				var url = "resources/chis.prints.template.";
				if(this.mainApp.deptId.indexOf("320481")==0 ||this.mainApp.deptId.indexOf("320111")==0){
					url+= "familyContractBaseLy";
				}else{
					url+= "familyContractBase";
				}
				url+= ".print?type=1&recordId="+this.contractId+"&temp=" + new Date().getTime()
				var win = window.open(url,"","height="+ (screen.height - 100)+ ", width="+ (screen.width - 10)
						+ ", top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no")

				if (Ext.isIE6) {
					win.print()
				} else {
					win.onload = function() {
						win.print()
					}
				}
			}
		})