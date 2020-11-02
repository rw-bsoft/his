$package("phis.application.cfg.script")

$import("phis.script.SelectList","phis.script.SimpleForm")

phis.application.cfg.script.SubstancesSelectList = function(cfg) {
	cfg.autoLoadData = false;
	cfg.disablePagingTbr = true;
	cfg.entryName="phis.application.cfg.schemas.WL_WZZD_SCCJ";
	phis.application.cfg.script.SubstancesSelectList.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(phis.application.cfg.script.SubstancesSelectList,
		phis.script.SelectList, {
			loadData : function(DXXH) {
				this.clear(); 
				if(!DXXH){
					return ;
				}
				this.requestData.serviceId = "phis.configManufacturerForWZService";
				this.requestData.serviceAction = "manufacturerForSCCJQuery";
				this.requestData.cnd = DXXH;
				
				if (this.store) {
					if (this.disablePagingTbr) {
						this.store.load()
					} else {
						var pt = this.grid.getBottomToolbar()
						if (this.requestData.pageNo == 1) {
							pt.cursor = 0;
						}
						pt.doLoad(pt.cursor)
					}
				}
				this.resetButtons();
			},
			loadDataCheck : function(){
				var bodys = {};
				if(!this.ZJXH){
					return false;
				}
				if(!this.CJXH){
					return false;
				}
				bodys["ZJXH"] = this.ZJXH;
				bodys["CJXH"] = this.CJXH;
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : "configManufacturerForWZService",
							method:"execute",
							serviceAction : "queryCheckWZ",
							body : bodys
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.doLoadReport);
				}
				return ret.json.ret;
			},
			onStoreLoadData : function(store, records, ops) {
				this.clearSelect();
				var rows = [];
				var body = this.loadDataCheck();
				for(var i = 0 ;i < store.getCount();i++){
					var num = 0;
					for(var j= 0 ;j < body.length;j++){
						if(body[j].WZXH == store.getAt(i).get("WZXH")){
        					rows.push(i);
						}
					}
				}
			   this.grid.selModel.selectRows(rows, true);
			}, 
			//对照
			doCompar : function(){
				var records = this.getSelectedRecords();
				if (records.length < 1) {
					Ext.Msg.alert("提示", "请选择物资信息！");
					return;
				}
				if(!this.ZJXH){
					Ext.Msg.alert("提示", "请选择证件信息！");
					return ;
				}
				if(!this.CJXH){
					Ext.Msg.alert("提示", "请选择证件信息！");
					return ;
				}
				var body = {};
				body["WL_WZXX"] = [];
				for (var i = 0; i < records.length; i++) {
					var record = records[i];
					record.data.ZJXH = this.ZJXH;
					record.data.CJXH = this.CJXH;
					body["WL_WZXX"].push(record.data);
				}
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : "configManufacturerForWZService",
							method:"execute",
							serviceAction : "saveCompar",
							body : body
						});
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg, this.onBeforeSave);
					return;
				} else {
					Ext.Msg.alert("提示", "对照成功！");
					this.loadData(this.CJXH);
				}
				
				
				
			}
			
		});