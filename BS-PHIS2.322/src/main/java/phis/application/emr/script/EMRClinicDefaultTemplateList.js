$package("phis.application.emr.script")

$import("phis.script.SimpleList")

phis.application.emr.script.EMRClinicDefaultTemplateList = function(cfg) {
	cfg.disablePagingTbr=true;
	phis.application.emr.script.EMRClinicDefaultTemplateList.superclass.constructor.apply(this, [cfg])
}

Ext.extend(phis.application.emr.script.EMRClinicDefaultTemplateList,
		phis.script.SimpleList, {
//			loadData : function() {
//				this.clear();
//				recordIds = [];
//				this.requestData.serviceId = "phis.emrManageService";
//				this.requestData.serviceAction ="loadTemplateList";
//				if (this.store) {
//					if (this.disablePagingTbr) {
//						this.store.load()
//					} else {
//						var pt = this.grid.getBottomToolbar()
//						if (this.requestData.pageNo == 1) {
//							pt.cursor = 0;
//						}
//						pt.doLoad(pt.cursor)
//					}
//				}
//				this.resetButtons();
//			},
			doRefresh : function() {
				this.refresh();
			}
//			,
//			doSave : function() {
//				var data = [];
//				var store = this.grid.getStore();
//				var n = store.getCount()
//				for ( var i = 0; i < n; i++) {
//					var r = store.getAt(i);
//					data.push(r.data);
//				}
//				this.grid.el.mask("正在保存数据...", "x-mask-loading")
//				var ret = phis.script.rmi.miniJsonRequestSync({
//					serviceId : "phis.emrManageService",
//					serviceAction : "updateEmrTemplatesType",
//					body : data
//				});
//				this.grid.el.unmask()
//				if (ret.code > 300) {
//					this.processReturnMsg(ret.code, ret.msg);
//					return;
//				} else {
//					var r = this.grid.getSelectionModel();
//					var s = this.opener.rlist.getSelectedRecords();
//					var rdata = [];
//					if(r){
//						var rd = r.selection.record.data;
//						var n = this.opener.rlist.sm.selections.length;
//						if(n>0){
//							for ( var i = 0; i < n; i++) {
//								rdata.push(s[i].data);
//								rdata[i].TEMPLATE_ID = rd.CHTCODE;
//								rdata[i].T_TABLE_NAME = rd.TABLENAME;
//							}
//						}else{
//							rdata.push(rd);
//						}
//						var retr = phis.script.rmi.miniJsonRequestSync({
//							serviceId : "phis.emrManageService",
//							serviceAction : "saveKsTemplateDy",
//							body : rdata
//						});
//						if (retr.code > 300) {
//							this.processReturnMsg(retr.code, retr.msg);
//							return;
//						}
//					}
//					MyMessageTip.msg("提示", "保存成功!", true);
//				}
//			}
		})
