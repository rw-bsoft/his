$package("phis.application.emr.script")

$import("phis.script.SimpleModule")
phis.application.emr.script.EMRClinicDefaultTemplateModule = function(cfg) {
	phis.application.emr.script.EMRClinicDefaultTemplateModule.superclass.constructor
			.apply(this, [cfg]);
}

Ext.extend(phis.application.emr.script.EMRClinicDefaultTemplateModule,
		phis.script.SimpleModule, {
			initPanel : function(sc) {
				var panel = new Ext.Panel({
							border : false,
							frame : true,
							layout : 'border',
							tbar : this.createButtons(),
							items : [{
										layout : "fit",
										region : 'west',
										width : 280,
										items : this.getLList()
									}, {
										layout : "fit",
										region : 'center',
										items : this.getRList()
									}]
						});
				this.panel = panel;
				// this.panel.on("beforeclose", this.onBeforeclose, this);
				// this.panel.on("afterrender", this.onReady, this);
				return panel;
			},
			getLList : function() {
				var module = this
						.createModule("refLListEMRClinicDefaultTemplateModule",
								this.refLList);
				// module.on("treeClick", this.onBeforeTreeClick, this);
				this.llist = module;
				this.llist.opener = this;
				var lGrid = module.initPanel();
				this.llist.requestData.cnd = [
						'and',
						['eq', ['$', 'OUTPATIENTCLINIC'], ['s', '1']],
						['like', ['$', 'ORGANIZCODE'],
								['s', this.mainApp['phisApp'].deptId + '%']]];
				lGrid.on("rowclick", this.onRowClick, this);
				lGrid.on("loadData", this.onListLoadData, this);
				this.lGrid = lGrid;
				return lGrid;
			},
			getRList : function() {
				var module = this
						.createModule("refRListEMRClinicDefaultTemplateModule",
								this.refRList);
				this.rlist = module;
				this.rlist.opener = this;
				var rGrid = module.initPanel();
				this.rGrid = rGrid;
				// lGrid.on("rowclick", this.onRowBeforeClick, this);
				module.on("loadData", this.onRLoadData, this);
				this.rlist.requestData.cnd = ['and',
						['eq', ['$', 'INOROUTTYPE'], ['i', 1]],
						['eq', ['$', 'NOTAVAILABLE'], ['i', 0]]];
				return rGrid;
			},
			onListLoadData : function(store) {
				// 如果第一次打开页面，默认模拟选中第一行
				if (this.rlist) {
					this.rlist.clear();
				}
				if (store.getCount() > 0) {
					if (!this.initDataId) {
						this.rlist.fireEvent("rowclick", this.rlist, 0);
					}
				} else {
				}
			},
			onRowClick : function() {
				var r = this.llist.getSelectedRecord();
				if (!r) {
					return
				}
				console.debug(r)
//				var ret = phis.script.rmi.miniJsonRequestSync({
//							serviceId : "phis.emrManageService",
//							serviceAction : "getKSDYinfo",
//							body : r.data
//						});
//				if (ret.code > 300) {
//					this.processReturnMsg(ret.code, ret.msg);
//					return;
//				} else {
//					
//				}
				// this.rlist.requestData.serviceId = "phis.emrManageService";
				// this.rlist.requestData.serviceAction = "loadKsmbdy";
				this.rlist.requestData.cnd = ['and',
						['eq', ['$', 'INOROUTTYPE'], ['i', 1]],
						['eq', ['$', 'NOTAVAILABLE'], ['i', 0]]];
				this.rlist.loadData();
			},
			onRLoadData : function(store) {
				this.rlist.clearSelect();
				var records = new Array();
				// console.debug(store)
				store.each(function(r) {
							// if(r.json.MBDY == 1){
							// records.push(r);
							// }
						});
				this.rlist.sm.selectRecords(records, true);
			},
			doSave : function() {
			}
		});
