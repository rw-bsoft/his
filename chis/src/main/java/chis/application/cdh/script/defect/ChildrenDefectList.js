/**
 * 儿童出生缺陷监测列表页面
 * 
 * @author : yaozh
 */
$package("chis.application.cdh.script.defect")
$import("chis.script.BizSimpleListView")
chis.application.cdh.script.defect.ChildrenDefectList = function(cfg) {
	chis.application.cdh.script.defect.ChildrenDefectList.superclass.constructor
			.apply(this, [cfg]);
}

Ext.extend(chis.application.cdh.script.defect.ChildrenDefectList,
		chis.script.BizSimpleListView, {

			doRemove : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					return
				}
				Ext.Msg.show({
							title : '确认删除记录[' + r.id + ']',
							msg : r.get("personName")+ '的儿童出生缺陷记录将删除，是否确定要删除该档案?',
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
      
			doCreateDefect : function(item, e) {
				var m = this.midiModules["childrenDefectRecord"];
				if (!m) {
					$import("chis.application.cdh.script.base.ChildInfoForm")
					m = new chis.application.cdh.script.base.ChildInfoForm({
								entryName : "chis.application.mpi.schemas.MPI_ChildBaseInfo",
								title : "儿童基本信息查询",
								height : 450,
								width : 780,
								modal : true,
								mainApp : this.mainApp,
								isDeadRegist : false
							})
					m.on("save", this.onAddDefect, this)
					this.midiModules["childrenDefectRecord"] = m;
				}
				var win = m.getWin();
				win.setPosition(250, 100);
				win.show();
			},
      
			onAddDefect : function(entryName, op, json, data) {
				var empiId = data.empiId;
				this.empiId = empiId;
				this.loadModule();
			},
      
			doModify : function() {
				var r = this.grid.getSelectionModel().getSelected()
				this.empiId = r.get("empiId");
				this.loadModule();
			},
      
			loadModule : function() {
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.childrenHealthRecordService",
							serviceAction : "checkHealthCardExists",
							method:"execute",
							body : {
								"empiId" : this.empiId
							}
						});
				var childrenRecordExists = false;
				if (result.code == 200) {
					if (result.json) {
						childrenRecordExists = result.json.body.recordExists;
					}
				}				
				$import("chis.script.EHRView")
				var module = this.midiModules["ChildrenDefectRecord_EHRView"]
				if (!module) {
					var modules = ['H_01', 'H_05'];
					if (childrenRecordExists==true||childrenRecordExists=="true") {
						modules = ['H_05'];
					}
					var module = new chis.script.EHRView({
								initModules : modules,
								empiId : this.empiId,
								closeNav : true,
								activeTab : 0,
								mainApp : this.mainApp
							})
					this.midiModules["ChildrenDefectRecord_EHRView"] = module
					module.on("save", this.refresh, this);
				} else {
					module.exContext.ids["empiId"]  = this.empiId
					module.refresh();
				}
				module.getWin().show()
			},
      
			onDblClick : function() {
				this.doModify();
			}

//			refresh : function() {
//				if (this.store)
//					this.store.load()
//			}
		});