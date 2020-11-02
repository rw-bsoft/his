$package("chis.application.fhr.script")

$import("chis.script.BizCombinedModule2")

chis.application.fhr.script.FamilyContractBase = function(cfg) {
	cfg.aotuLoadData = false;
	cfg.itemWidth = 235;
	cfg.height = 400;
	chis.application.fhr.script.FamilyContractBase.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(chis.application.fhr.script.FamilyContractBase,
		chis.script.BizCombinedModule2, {
			initPanel : function() {
				var panel = chis.application.fhr.script.FamilyContractBase.superclass.initPanel
						.call(this);
				this.panel = panel;
				this.list = this.midiModules[this.actions[0].id];
				this.list.on("loadData", this.onListLoadData, this);
				this.list.on("loadNoData", this.onListLoadData, this);
				this.list.father = this;
				this.grid = this.list.grid;
				this.grid.on("rowclick", this.onRowClick, this)
				this.form = this.midiModules[this.actions[1].id];
				this.form.on("save", this.loadData, this);
				return panel;
			},

			// refreshList : function() {
			// this.loadGridData();
			// },
			loadData : function() {
				//不存在initDataId 根据empiId去查
				if(!this.exContext.args.initDataId)
				{
					this.exContext.args.initDataId=this.exContext.ids.familyId;
				}
				this.list.requestData.cnd = ['eq', ['$', 'F_Id'],
						['s', this.exContext.args.initDataId]]
				this.list.loadData();
			},
			onListLoadData : function(store) {
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.familyRecordService",
							serviceAction : "loadContractControl",
							method : "execute",
							body : {
								familyId : this.exContext.args.initDataId
							}
						})
				if (result.code > 300) {
					this.processReturnMsg(result.code, result.msg);
					return
				}
				this.exContext.control = result.json.body;
				this.form.exContext.control = result.json.body;
				if (!store || store.getCount() == 0) {
					this.form.op = "create"
					this.form.contractId = "";
					this.form.initDataId = this.exContext.args.initDataId;
					this.form.setButtonDisable(true);
					this.form.doNew()
					return
				}
				var r = this.list.getSelectedRecord();
				this.FC_CreateDate = Date.parseDate(r.get("FC_CreateDate"),
						"Y-m-d")
				this.list.FC_CreateDate = this.FC_CreateDate
				// this.loadFormData(r)
				var today = new Date();
				var bool = true
				for (var i = 0; i < store.getCount(); i++) {
					var r1 = store.getAt(i)
					var stopDate = null;
					if (r1.get("FC_Stop_Date")) {
						stopDate = Date.parseDate(r1.get("FC_Stop_Date"),
								"Y-m-d")
					}
					var endDate = Date.parseDate(r1.get("FC_End"), "Y-m-d")
					if (!stopDate
							&& endDate.getTime() + 86400000 > today.getTime()) {
						bool = false
					}
				}
				if (this.list) {
					this.list.setBtnStatus(bool, "new");
				}
				this.onRowClick()
			},
			onRowClick : function(grid, index, e) {
				// this.selectedIndex = index
				// var r = grid.store.getAt(index)
				var r = this.list.getSelectedRecord();
				if (!r) {
					return;
				}
				var contractDate, contractDate1, maxContract = r
						.get("FC_Begin");
				for (var k = 0; k < this.list.grid.store.getCount(); k++) {
					contractDate = this.list.grid.store.getAt(k)
							.get("FC_Begin");
					if (contractDate > maxContract)
						maxContract = contractDate
				}
				if (r.get("FC_Begin") == maxContract) {
					this.form.setButtonDisable(false);
					this.list.setButtonDisable(false);
				} else {
					this.form.setButtonDisable(true);
					this.list.setButtonDisable(true);
				}
				this.loadFormData(r)

			},
			loadFormData : function(r) {
				this.form.op = "update"
				this.form.contractId = r.get("FC_Id")
				this.form.initDataId = this.exContext.args.initDataId;
				this.form.loadData()
			},
			getWin : function() {
				var win = this.win;
				var closeAction = "hide";
				if (!win) {
					win = new Ext.Window({
								id : this.id,
								title : this.title,
								width : this.width,
								iconCls : 'icon-grid',
								shim : true,
								layout : "fit",
								animCollapse : true,
								closeAction : closeAction,
								constrainHeader : true,
								minimizable : true,
								maximizable : true,
								shadow : false,
								items : this.initPanel()
							});
					win.on("show", function() {
								this.fireEvent("winShow")
							}, this);
					win.on("close", function() {
								this.fireEvent("close", this)
							}, this);
					win.on("hide", function() {
								this.fireEvent("close", this)
							}, this);
					var renderToEl = this.getRenderToEl();
					if (renderToEl) {
						win.render(renderToEl);
					}
					this.win = win;
				}
				win.instance = this;
				return win;
			},
			onWinShow : function() {
				this.loadData()
			}
		});