$package("phis.application.cic.script")

$import("phis.script.common", "phis.script.SimpleModule")

phis.application.cic.script.ClinicDiagnosisCenterQuickInputModule = function(cfg) {
	this.serviceId = cfg.serviceId;
	this.queryServiceAction = cfg.queryServiceAction;
	phis.application.cic.script.ClinicDiagnosisCenterQuickInputModule.superclass.constructor
			.apply(this, [cfg])
}

Ext.extend(phis.application.cic.script.ClinicDiagnosisCenterQuickInputModule,phis.script.SimpleModule, {
			initPanel : function(sc) {
				var panel = new Ext.Panel({
							border : false,
							frame : true,
							layout : 'border',
							width : this.width,
							height : this.height,
							items : [{
										layout : "fit",
										split : true,
										title : '疾病',
										region : 'north',
										height : 280,
										items : this.getJBList()
									}, {
										layout : "fit",
										split : true,
										title : '证侯',
										region : 'center',
										items : this.getZHList()
									}]
						});

				this.panel = panel;
				return panel
			},
			getJBList : function() {
				this.jblist = this.createModule("jblist", this.refJBList);
				this.jblist.oper = this;
				this.jblist.disablePagingTbr = false;
				this.jblist.grid = this.jblist.initPanel();
				this.jblist.on("loadData", this.onListLoadData, this);
				this.jblist.grid.on("rowClick", function(grid, index, e) {
									this.doZHListRefresh(grid.getStore().getAt(index));
								}, this)
				this.jblist.grid.on("rowdblclick", function(grid, index, e) {
							this.fireEvent("quickInput", "zdmc", grid.getStore().getAt(index));
						}, this)
				return this.jblist.grid;
			},
			getZHList : function() {
				this.zhlist = this.createModule("zhlist", this.refZHList);
				this.zhlist.oper = this;
				this.zhlist.disablePagingTbr = false;
				this.zhlist.grid = this.zhlist.initPanel();
				this.zhlist.grid.on("rowdblclick", function(grid, index, e) {
							this.fireEvent("quickInput", "zh", grid.getStore().getAt(index));
						}, this)
				return this.zhlist.grid;
			},
			doZHListRefresh : function(record) {
				var body = {};
				var zhbs = [];
				body["JBBS"] = record.get("JBBS");
				var r = util.rmi.miniJsonRequestSync({
							serviceId : "phis.clinicDiagnossisService",
							serviceAction : "querySymptom",
							body : body
						});
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg, this.onBeforeSave);
					return;
				} else {
					Ext.each(r.json.ZHBS, function() {
								zhbs.push(this["ZHBS"])
							})
				}
				if(zhbs == ""){
					zhbs.push("-1");
				}
				this.zhlist.requestData.cnd = ['in', ['$', 'ZHBS'], zhbs];
				this.zhlist.refresh();
			},
			onListLoadData : function(store) {
				if (this.zhlist) {
					this.zhlist.clear();
				}
				if (store.getCount() > 0) {
					this.jblist.grid.fireEvent("rowclick",this.jblist.grid, 0);
				} 
			}
		})