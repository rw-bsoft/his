/**
 * 体弱儿档案整合模块页面
 * 
 * @author : yaozh
 */
$package("chis.application.cdh.script.debility.record")
$import("chis.script.BizCombinedTabModule")
chis.application.cdh.script.debility.record.DebilityChildrenRecordModule = function(cfg) {
	cfg.autoLoadData = false
	chis.application.cdh.script.debility.record.DebilityChildrenRecordModule.superclass.constructor
			.apply(this, [cfg])
	this.itemWidth = 150
	this.on("winShow", this.onWinShow, this)
	this.on("loadModule", this.onLoadModule, this)
}
Ext.extend(chis.application.cdh.script.debility.record.DebilityChildrenRecordModule,
		chis.script.BizCombinedTabModule, {

			initPanel : function() {
				var panel = chis.application.cdh.script.debility.record.DebilityChildrenRecordModule.superclass.initPanel
						.call(this)
				this.panel = panel;
				this.list = this.midiModules[this.otherActions.id];
				this.list.on("loadData", this.onLoadGridData, this)
				this.list.grid.on("rowClick", this.onRowClick, this)
				return panel;
			},

			onLoadModule : function(moduleId, module) {
				if (moduleId == this.actions[0].id) {
					this.recordModule = module;
					module.on("doCreate", this.onDoCreate, this);
					module.on("save", this.onSave, this);
					module.on("close", this.onClose, this);
					module.on("closeFlag", this.onCloseFlag, this);
					module.on("processButton", this.processButton, this);
				} else {
					this.visitModule = module;
					module.on("controlButton", this.controlVisitModuleButton, this);
				}
			},
			controlVisitModuleButton:function(){
				if(this.recordModule.form.getTopToolbar().items.item(0).disabled == true){
					this.visitModule.midiModules[this.visitModule.actions[0].id].form.getTopToolbar().items.item(0).disable()
				}
				if(this.recordModule.form.getTopToolbar().items.item(0).disabled == false){
					this.visitModule.midiModules[this.visitModule.actions[0].id].form.getTopToolbar().items.item(0).enable() 
				}
			}
			,
			onCloseFlag : function(key) {
				if (this.visitModule) {
					this.visitModule.exContext.args.closeFlag = key
				}
			},

			onSave : function(entryName, op, json, data) {
				this.exContext.args.debilityRecordId = data.recordId
				this.exContext.args.data = null
				this.list.loadData()
				this.changeSubItemDisabled(false, this.actions[0].id);
				this.fireEvent("save", entryName, op, json, data)
				this.refreshEhrTopIcon();
			},

			onClose : function() {
				if (this.visitModule) {
					this.visitModule.exContext.args.closeFlag = "y"
				}
				this.list.loadData()
			},

			onDoCreate : function() {
				var store = this.list.store
				for (i = 0; i < store.getCount(); i++) {
					if (store.getAt(i).get("closeFlag") != 'y'
							&& store.getAt(i).get("status") != '1') {
						return false;
					}
				}
				this.list.selectedIndex = store.getCount();
				this.changeSubItemDisabled(true, this.actions[0].id);
				this.recordModule.doNew()
				var data = this.exContext.args.data;
				var debilityReason = this.recordModule.form.getForm()
						.findField("debilityReason");
				if (data) {
					debilityReason.setValue(data.debilityReason);
				} else {
					debilityReason.setValue();
				}
			},

			loadData : function() {
				this.list.selectedIndex = 0
				this.list.requestData.cnd = ['eq', ['$', 'a.empiId'],
						['s', this.exContext.empiData.empiId]]
				this.list.loadData()
			},

			onLoadGridData : function(store) {
				
				var index = 0;
				var selectRecord = this.exContext.args.debilityRecordId;
				if (selectRecord) {
					index = this.list.store.find("recordId", selectRecord);
				}
				if (this.exContext.args.data) {
					var exc = {
						"phrId" : this.exContext.args.data.phrId,
						"empiId" : this.exContext.args.data.empiId,
						"recordId" : null,
						"closeFlag" : null,
						"formDatas" : null
					}
					Ext.apply(this.exContext.args, exc);
					this.changeSubItemDisabled(true, this.actions[0].id);
					this.activeModule(0);
					this.recordModule.doCreate()
				} else {
					if (store.getCount() == 0) {
						var exc = {
							"phrId" : this.exContext.ids["CDH_HealthCard.phrId"],
							"empiId" : this.exContext.empiData.empiId,
							"recordId" : null,
							"closeFlag" : null,
							"formDatas" : null
						}
						Ext.apply(this.exContext.args, exc);
						this.changeSubItemDisabled(true, this.actions[0].id);
						this.activeModule(0);
						this.recordModule.doCreate();
						return;
					}
					var r = store.getAt(index);
					this.list.selectedIndex = index;
					this.proccess(r);
				}
			},

			onRowClick : function(grid, index, e) {
				var r = grid.getStore().getAt(index)
				this.list.selectedIndex = index
				this.proccess(r)
			},

			proccess : function(r) {
				if (!r) {
					return;
				}
				var data = this.castListDataToForm(r.data, this.list.schema)
				this.changeSubItemDisabled(false, this.actions[0].id);
				var exc = {
					"phrId" : r.get("phrId"),
					"empiId" : r.get("empiId"),
					"recordId" : r.get("recordId"),
					"closeFlag" : r.get("closeFlag"),
					"formDatas" : data
				}
				Ext.apply(this.exContext.args, exc);
				this.activeModule(0);
			},

			processButton : function() {
				if (!this.recordModule.form.getTopToolbar()) {
					return;
				}

				var btns = this.recordModule.form.getTopToolbar().items;
				if (btns) {
					var n = btns.getCount()
					for (var i = 0; i < n; i++) {
						if (i != 1) {
							var btn = btns.item(i)
							btn.disable()
						}
					}
				}
			},

			castListDataToForm : function(data, schema) {
				var formData = chis.application.cdh.script.debility.record.DebilityChildrenRecordModule.superclass.castListDataToForm
						.call(this, data, schema);
				var control = data["_actions"];
				this.exContext.control = control;
				return formData;
			},

			getWin : function() {
				var win = this.win;
				var closeAction = "hide";
				if (!win) {
					win = new Ext.Window({
								id : this.id,
								title : this.title,
								constrain : true,
								width : this.width,
								height : this.height || 500,
								iconCls : 'icon-grid',
								shim : true,
								layout : "fit",
								animCollapse : true,
								closeAction : closeAction,
								constrainHeader : true,
								minimizable : true,
								maximizable : true,
								autoScroll : true,
								shadow : false,
								modal : true,
								items : this.initPanel()
							})
					var renderToEl = this.getRenderToEl();
					if (renderToEl) {
						win.render(renderToEl);
					}
					win.on("show", function() {
								this.win.doLayout();
								this.fireEvent("winShow", this);
							}, this)
					win.on("close", function() {
								this.fireEvent("close", this);
							}, this)
					win.on("hide", function() {
								this.fireEvent("close", this);
							}, this)
					this.win = win
				}
				win.instance = this;
				return win;
			},

			onWinShow : function() {
				this.loadData();
			}
		});