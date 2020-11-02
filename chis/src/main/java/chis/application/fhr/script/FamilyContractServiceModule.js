$package("chis.application.fhr.script")
$import("app.modules.list.EditorListView",
		"chis.application.fhr.script.FamilyContractServiceForm")
chis.application.fhr.script.FamilyContractServiceModule = function(cfg) {
	cfg.aotuLoadData = false;
	chis.application.fhr.script.FamilyContractServiceModule.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(chis.application.fhr.script.FamilyContractServiceModule,
		app.modules.list.EditorListView, {
			getCndBar : function() {
				return [];
			},
			setButtonDisable : function(status) {
				var btns = this.grid.getTopToolbar().items;
				if (status) {
					btns.item(0).disable();
				} else {
					btns.item(0).enable();
				}

			},
			onRowClick : function() {
				this.storeRecord = this.grid.getStore();
				var r = this.getSelectedRecord();
				this.selectRecord = r
			},
			onDblClick : function() {
				this.selectEmpiId = this.selectRecord.get("FS_EmpiId")
				var serviceModule = new chis.application.fhr.script.FamilyContractServiceForm(
						{
							empiId : this.selectEmpiId,
							record : this.selectRecord,
							entryName : "chis.application.fhr.schemas.EHR_FamilyContractServiceDetail",
							title : "服务项目明细",
							modal : true,
							autoLabelWidth : true,
							fldDefaultWidth : 600,
							autoFieldWidth : true,
							width : 800,
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
				return chis.application.fhr.script.FamilyContractServiceModule.superclass.loadData
						.call(this)
			}
			// getStore : function(items) {
			//				
			//
			// }
		});