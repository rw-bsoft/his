$package("chis.application.fhr.script")
$import("app.modules.list.EditorListView",
		"chis.application.fhr.script.FamilyContractServiceForm")
chis.application.fhr.script.FamilyContractServiceList = function(cfg) {
	this.disablePagingTbr = true;
	cfg.aotuLoadData = false;
	this.entryName = "chis.application.fhr.schemas.EHR_FamilyContractService";
	// this.actions = [{
	// id : "addServiceRecord",
	// name : "添加服务记录",
	// iconCls : "create"
	// }]
	chis.application.fhr.script.FamilyContractServiceList.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(chis.application.fhr.script.FamilyContractServiceList,
		app.modules.list.EditorListView, {
			getCndBar : function() {
				return [];
			},
//			setButtonDisable : function(status) {
//				var btns = this.grid.getTopToolbar().items;
//				if (status) {
//					btns.item(0).disable();
//				} else {
//					btns.item(0).enable();
//				}
//
//			},
			onRowClick : function() {
				this.storeRecord = this.grid.getStore();
				var r = this.getSelectedRecord();
				this.selectRecord = r
			},
			onDblClick : function() {
				this.selectEmpiId = this.selectRecord.get("FS_EmpiId")
				var initentryName="";
				if(this.mainApp.deptId.indexOf("320481")==0 ||this.mainApp.deptId.indexOf("320111")==0){
					initentryName="chis.application.fhr.schemas.EHR_FamilyContractServiceDetail_ly";
				}else{
					initentryName="chis.application.fhr.schemas.EHR_FamilyContractServiceDetail";
				}
				var serviceModule = new chis.application.fhr.script.FamilyContractServiceForm(
						{
							empiId : this.selectEmpiId,
							record : this.selectRecord,
							entryName : initentryName,
							title : "服务项目明细",
							modal : true,
							autoLabelWidth : true,
							fldDefaultWidth : 600,
							autoFieldWidth : true,
							width : 800,
							empiId : this.selectEmpiId,
							mainApp : this.mainApp
						})
				serviceModule.on("save", this.setListData, this);
				var win = serviceModule.getWin();
				win.setPosition(150, 100);
				win.show();

			},
			setListData : function(data) {
				var rAll;
				for (var i = 0; i < this.storeRecord.getCount(); i++) {
					rAll = this.grid.getStore().getAt(i);
					if (rAll.get("FS_EmpiId") == this.selectEmpiId) {
						rAll.set("FS_PersonGroup", data["personGroup"]);
						rAll.set("FS_Kind", data["serviceContent"])
						rAll.set("FS_Disease", data["disease"])
					}
				}
			},
			getListData : function() {
				var array = []
				for (var i = 0; i < this.store.getCount(); i++) {
					var r = this.store.getAt(i)
					array.push(r.data)
				}
				return array;
			},
			loadData : function() {
				this.requestData.serviceId = "chis.familyRecordService";
				this.requestData.serviceAction = "familyQuery";
				this.requestData.initDataId = this.initDataId
				this.requestData.FC_Id = this.FC_Id;
				chis.application.fhr.script.FamilyContractServiceList.superclass.loadData
						.call(this)
			}
			// getStore : function(items) {
			//				
			//
			// }
		});