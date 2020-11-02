$package("chis.application.fhr.script")
$import("chis.script.BizSelectListView")
chis.application.fhr.script.FieldMaintainAddList = function(cfg) {
	cfg.pageSize = 0;
	chis.application.fhr.script.FieldMaintainAddList.superclass.constructor
			.apply(this, [cfg]);
	this.enableCnd = false;
	this.disablePagingTbr = false;
};
Ext.extend(chis.application.fhr.script.FieldMaintainAddList,
		chis.script.BizSelectListView, {

			initPanel : function(schema) {
				var grid = app.modules.list.SelectListView.superclass.initPanel
						.call(this, schema);
				this.grid = grid;
				if (this.win) {
					this.win.on("show", function() {
								if (this.sm) {
									this.sm.clearSelections();
								}
							}, this);
				}
				return grid;
			},
			
			doSave:function(){
				var records=this.getSelectedRecords();
				if(!records||records.length==0){
					return;
				}
				var fieldIds="";
				if(records.length>0){
					fieldIds=this.getFieldIds(records);
				}	
				util.rmi.jsonRequest({
							serviceId : "chis.templateService",
							serviceAction : "saveSelectField",
							method:"execute",
							fieldIds:fieldIds,
							masterplateId:this.masterplateId
						}, function(code, msg, json) {
							if (code > 300) {
								this.processReturnMsg(code, msg);
								return;
							}
							this.refresh();
							this.fireEvent("cancel",this);
						}, this);
			},
			getFieldIds:function(records){
				var fieldIds="";
				for(var i=0;i<records.length;i++){
					var r=records[i];
					var fieldId=r.id;
					fieldIds=fieldIds+fieldId+","
				}
				fieldIds=fieldIds.substring(0,fieldIds.length-1);
				return fieldIds;
			}

			
		})