$package("phis.application.fsb.script")
$import("phis.script.EditorList")
phis.application.fsb.script.JCBusiList = function(cfg) {
	cfg.minListWidth = 400;
	cfg.autoLoadData = true;
	cfg.listServiceId = "medicalTechnicalSectionService";
	cfg.disablePagingTbr = true;
	cfg.mutiSelect = true;// 添加勾选框
// this.serviceId = "wardPatientManageService";
	phis.application.fsb.script.JCBusiList.superclass.constructor.apply(this,[cfg])
	this.on("loadData",this.onLoadData,this);
}
var recordIds = new Array();
Ext.extend(phis.application.fsb.script.JCBusiList, phis.script.EditorList,
		{
	
			onStoreLoadData : function(store, records, ops) {
				recordIds = [];
				if (records.length == 0) {
					this.fireEvent("loadData", store)
					return
				}
				/*
				 * if (!this.selectedIndex || this.selectedIndex >=
				 * records.length) { this.selectRow(0) } else {
				 * this.selectRow(this.selectedIndex); this.selectedIndex = 0; }
				 */
				var selRecords = [];
				
				for (var id in this.selects) {
					var r = store.getById(id);
					if (r) {
						if (!this.containsArray(recordIds, r.get("YJXH")))
							recordIds.push(r.get("YJXH"));
					}
				}
				
				this.grid.getSelectionModel().selectRecords(selRecords);
				
				this.fireEvent("loadData", store) // ** 不管是否有记录，都fire出该事件
				var datas = [];
				for (var i=0; i<store.getCount(); i++ ) {
						var r = store.getAt(i)
						datas.push(r.data);
				}
				var resData = phis.script.rmi.miniJsonRequestSync({
					serviceId :this.listServiceId,
					serviceAction : "queryJcMX",
					bodys : datas
				});
				if (resData.code > 300) {
					this.processReturnMsg(resData.code, resData.msg);
					return;
				}
				this.listDatas = resData.json.body;
			},
			onLoadData : function(){
				this.onRowClick();
			},
			expansion : function(cfg) {
				cfg.sm.on("rowselect", this.rowSelect, this);
				cfg.sm.on("rowdeselect", this.rowdeSelect, this);
			},
			rowSelect : function(e, rowIndex, record) {
				if (!this.containsArray(recordIds, record.get("YJXH")))
					recordIds.push(record.get("YJXH"));
				this.dList.loadData(recordIds);
			},
			rowdeSelect : function(e, rowIndex, record) {
				if (this.containsArray(recordIds, record.get("YJXH"))) {
					this.RemoveArray(recordIds, record.get("YJXH"));
				}
				this.dList.loadData(recordIds);
			},
			containsArray : function(array, attachId) {
				for (var i = 0; i < array.length; i++) {
					if (array[i] == attachId) {
						return true;
						break;
					}
				}
				return false;
			},
			RemoveArray : function(array, attachId) {
				for (var i = 0, n = 0; i < array.length; i++) {
					if (array[i] != attachId) {
						array[n++] = array[i]
					}
				}
				array.length -= 1;
			},
			init: function(){
					// 是否已编辑
					this.editor = false;
					// 选择框
					this.addEvents({
								"select" : true
							});
					if (this.mutiSelect) {
						this.selectFirst = false;
					}
					this.selects = {};
				    this.singleSelect = {}
					 
					this.serverParams = {serviceAction:"queryJCList",zxks:this.mainApp['phis'].MedicalId,jgid:this.mainApp['phisApp'].deptId}
					phis.application.fsb.script.JCBusiList.superclass.init.call(this)
			},
			initPanel : function(sc) {
				var grid = phis.application.fsb.script.JCBusiList.superclass.initPanel.call(this, sc);
				grid.onEditorKey = function(field, e) {
					if (field.needFocus) {
						field.needFocus = false;
						ed = this.activeEditor;
						if (!ed) {
							ed = this.lastActiveEditor;
						}
						this.startEditing(ed.row, ed.col);
						return;
					}
					this.selModel.onEditorKey(field, e);
				}
				var sm = grid.getSelectionModel();
				// 重写onEditorKey方法，实现Enter键导航功能
				var panel = new Ext.Panel({
					layout : 'border',
					tbar : this.createButtons(),
					items : [{
								layout : "fit",
								border : false,
								region : 'center',
								items : grid
							},
					         {
				        	    layout : "fit",
								border : false,
								region : 'south',
								height: 200,
								items :  this.getDList()
				         }]
				});
				return panel ;
			},
			onRowClick : function() {
				this.opener.opener.panel.getTopToolbar().find("cmd", "delete")[0].setDisabled(false);
				var r = this.getSelectedRecord();
// var yjxh ;
				if(r){
// yjxh = r.get("YJXH");
				}else{
					this.dList.store.removeAll();
					return ;
				}
// this.dList.listServiceId = this.listServiceId;
// this.dList.requestData["serviceAction"] = "getMzList_Proj";
// this.dList.requestData["jgid"]=this.mainApp['phisApp'].deptId;
// this.dList.requestData["yjxh"] = yjxh ;
// this.dList.loadData();
				/**
				 * 2013-06-20 gejj start
				 * 修改bug1775医技管理--医技项目执行：已收费的记录，【修改】按钮需不可用。
				 * 修改描述:在发票号码不为空，说明已经收费过，则不能修改("修改"按钮禁用)
				 * 在发票号码为空，说明已经未收费过，则可以修改("修改"按钮启用)
				 */
				
				var djzt = r.get("DJZT");
				if(djzt){// 发票号码不为空，说明已经收费过，则不能修改("修改"按钮禁用)
// this.opener.opener.panel.getTopToolbar().items.items[3].setDisabled(true);
					this.opener.opener.panel.getTopToolbar().find("cmd", "modify")[0].setDisabled(true);
					this.opener.opener.panel.getTopToolbar().find("cmd", "goback")[0].setDisabled(false);
				}else{// 发票号码为空，说明已经未收费过，则可以修改("修改"按钮启用)
// this.opener.opener.panel.getTopToolbar().items.items[3].setDisabled(false);
					this.opener.opener.panel.getTopToolbar().find("cmd", "modify")[0].setDisabled(false);
					this.opener.opener.panel.getTopToolbar().find("cmd", "goback")[0].setDisabled(true);
				}
				if(recordIds.length>1){
					this.opener.opener.panel.getTopToolbar().find("cmd", "delete")[0].setDisabled(true);
					this.opener.opener.panel.getTopToolbar().find("cmd", "modify")[0].setDisabled(true);
					this.opener.opener.panel.getTopToolbar().find("cmd", "goback")[0].setDisabled(true);
				}
				/** 2013-06-20 gejj end */
			},
			getDList : function(){
				var module = this.createModule("jc_yj02list",
						'phis.application.med.MED/MED/MED010106');
				if (module) {
					module.exContext = this.exContext;
					module.opener = this;
					module.openBy = this.openBy;
					this.dList = module;
					return module.initPanel();
				}
			},
			getRowClass : function(record, rowIndex, rowParams, store) {
				if (record.get("SYBZ") == 1) {
					return "x-grid-record-gray";
				}
				if ((record.get("QRSJ") && record.get("QRSJ").length > 0)) {
					return "x-grid-record-smoke";
				}
				return ""
			} ,
			dateFormat : function(value, params, r, row, col, store) {
				if (row > 0
						&& col < 5
						&& store.getAt(row - 1).get("YZZH_SHOW") == r
								.get("YZZH_SHOW")) {
					return "";
				}
				return Ext.util.Format.date(Date
								.parseDate(value, "Y-m-d H:i:s"), 'Y.m.d H:i')
			}, 
			showColor : function(v, params, data) {
				var YZZH = data.get("YZZH_SHOW") % 2 + 1;
				switch (YZZH) {
					case 1 :
						params.css = "x-grid-cellbg-1";
						break;
					case 2 :
						params.css = "x-grid-cellbg-2";
						break;
					case 3 :
						params.css = "x-grid-cellbg-3";
						break;
					case 4 :
						params.css = "x-grid-cellbg-4";
						break;
					case 5 :
						params.css = "x-grid-cellbg-5";
						break;
				}
				return "";
			} ,
// getCM : function(items) {
// var cm = phis.application.med.script.JCBusiList.superclass.getCM.call(this,
// items);
// var sm = this.sm;
// sm.on("rowselect", function(sm, rowIndex, record) {
// this.selects[record.id] = record;
// /*var selectList = this.changeSelect();
// this.dList.listServiceId = this.listServiceId;
// this.dList.requestData["serviceAction"] = "queryMX";
// this.dList.requestData["bodys"] = selectList ;
// this.dList.loadData();*/
// }, this);
// sm.on("rowdeselect", function(sm, rowIndex, record) {
// delete this.selects[record.id];
// /*var selectList = this.changeSelect();
// this.dList.listServiceId = this.listServiceId;
// this.dList.requestData["serviceAction"] = "queryMX";
// this.dList.requestData["bodys"] = selectList ;
// this.dList.loadData();*/
// }, this);
// return cm;
// },
			
			changeSelect : function(){
				var selectList = [];
				var store = this.grid.getStore();
				var n = store.getCount()
				for (var i=0; i<n; i++ ) {
					if(this.grid.getSelectionModel().isSelected(i)){
						var r = store.getAt(i)
						selectList.push(r.data);
					}
				}
				return selectList;
			},
			// 清空
			clearSelect : function() {
				this.selects = {};
				this.singleSelect = {};
				this.sm.clearSelections();
			},
			// 获取选中记录
			getSelectedRecords : function() {
				var records = [];
				var selects = this.grid.getSelectionModel().getSelections();
				for(var i=0; i<selects.length; i++){
					records.push(selects[i]);
				}
				return records;
			}
		});
