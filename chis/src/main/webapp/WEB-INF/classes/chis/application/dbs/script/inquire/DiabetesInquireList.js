$package("chis.application.dbs.script.inquire")
$import("util.Accredit", "app.modules.form.TableFormView",
		"chis.script.BizSimpleListView")

chis.application.dbs.script.inquire.DiabetesInquireList = function(cfg) {
	this.entryName = "chis.application.dbs.schemas.MDC_DiabetesInquire"
	cfg.listServiceId = "chis.diabetesService";
	chis.application.dbs.script.inquire.DiabetesInquireList.superclass.constructor.apply(this, [cfg])
    
}
Ext.extend(chis.application.dbs.script.inquire.DiabetesInquireList, chis.script.BizSimpleListView, {
			onRowClick : function(grid, rowIndex, e) {
				this.selectedIndex = rowIndex
			},
			loadData : function(){
			     this.requestData.empiId = this.exContext.ids.empiId;
                 this.requestData.serviceAction = "getDiabetesInquire";	
                 chis.application.dbs.script.inquire.DiabetesInquireList.superclass.loadData.call(this)
			},
			
			onStoreBeforeLoad : function(store, op) {
			},
			onStoreLoadData : function(store, records, ops) {
				this.fireEvent("loadData", store)
				if (records.length == 0) {
					return
				}
				if (!this.selectedIndex) {
					this.selectRow(0)
				} else {
					this.selectRow(this.selectedIndex);
					this.selectedIndex = 0;
				}
			}
		});