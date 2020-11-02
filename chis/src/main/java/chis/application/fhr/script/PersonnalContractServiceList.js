$package("chis.application.fhr.script")
$import("app.modules.list.EditorListView",
		"chis.application.fhr.script.FamilyContractServiceForm")
chis.application.fhr.script.PersonnalContractServiceList = function(cfg) {
	this.disablePagingTbr = true;
	cfg.autoLoadData = false;
	this.entryName = "chis.application.fhr.schemas.EHR_PersonContractService";
	chis.application.fhr.script.PersonnalContractServiceList.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(chis.application.fhr.script.PersonnalContractServiceList,
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
//				this.selectEmpiId = this.selectRecord.get("FS_EmpiId")
				var serviceModule = new chis.application.fhr.script.FamilyContractServiceForm(
						{
							empiId : this.exContext.empiData["empiId"],
							record : this.selectRecord,
							entryName : "chis.application.fhr.schemas.EHR_FamilyContractServiceDetail",
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
			doSave : function(){
			    var listData = this.getListData()
//				this.grid.el.mask("正在载入数据....", "x-mask-loading")
				var r = util.rmi.miniJsonRequestSync({
							serviceId : "chis.healthRecordService",
							serviceAction : "savePsersonnalContract",
							method: "execute",
							body : listData					
						});
				if (r.code > 300) {
					MyMessageTip.msg("提示", r.msg, true);
					return;
				}
				this.loadData();
				this.refresh();
			},
			onSave : function(){
				var count = store.getTotalCount();
				var btns = this.grid.getTopToolbar().items;
				var bts = btns.items[0];
				if (!btns) {
					return;
				}
				if(count>0){		
					bts.disable();
				}else{
				bts.enable();
				}
			},
			setListData : function(data) {
				var rAll;
				for (var i = 0; i < this.storeRecord.getCount(); i++) {
					    rAll = this.grid.getStore().getAt(i);
					    rAll.set("personName",this.exContext.empiData["personName"]);
					    rAll.set("FS_EmpiId",this.exContext.empiData["empiId"]);
						rAll.set("FS_PersonGroup", data["personGroup"]);
						rAll.set("FS_Kind", data["serviceContent"]);
						rAll.set("FS_Disease", data["disease"]);
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
				this.requestData.cnd = ['eq',['$','a.FS_EmpiId'],['s',this.exContext.empiData["empiId"]]];
//				this.requestData.FC_Id = this.FC_Id;
				chis.application.fhr.script.PersonnalContractServiceList.superclass.loadData
						.call(this);
				//检验数据
			},
			onStoreLoadData : function(store,record,ops){
				var count = store.getTotalCount();
				var btns = this.grid.getTopToolbar().items;
				var bts = btns.items[0];
				if (!btns) {
					return;
				}
				if(count>0){		
					bts.disable();
				}else{
				bts.enable();
				}
			},
			// getStore : function(items) {
			//				
			//
			// }
			doRemove : function() {
//				
//				var today = new Date();
////				if (this.FC_CreateDate.getTime() + 86400000 < today.getTime()) {
////					alert("签约超过一天，不能删除该记录")
////					return;
////				}
			    var r = this.getSelectedRecord()
			    if (r == null) {
					return
			    }
				Ext.Msg.show({
							title : '确认删除此记录',
							msg : '删除操作将无法恢复，是否继续?',
							modal : true,
							width : 300,
							buttons : Ext.MessageBox.OKCANCEL,
							multiline : false,
							fn : function(btn, text) {
								if (btn == "ok") {
									this.processRemove();
								}
							},
							scope : this
						})
			},
			processRemove : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					return
				}
				if (!this.fireEvent("beforeRemove", this.entryName, r)) {
					return;
				}
				this.mask("正在删除数据...")
				util.rmi.jsonRequest({
							serviceId : "chis.healthRecordService",
							pkey : r.id,
							FC_Id : r.data.FC_Id,
							FS_EmpiId : r.data.FS_EmpiId,
							serviceAction : "remove",
							method : "execute"
						}, function(code, msg, json) {
							this.unmask()
							if (code < 300) {
								this.store.remove(r)
								this.fireEvent("remove", this.entryName,
										'remove', json, r.data)
										debugger
								var count = this.store.getTotalCount();
								var btns = this.grid.getTopToolbar().items;
								var bts = btns.items[0];
								if (!btns) {
									return;
								}
								if(count>1){		
									bts.disable();
								}else{
								bts.enable();
								}
							} else {
								this.processReturnMsg(code, msg, this.doRemove)
							}
						}, this)
			}
		});