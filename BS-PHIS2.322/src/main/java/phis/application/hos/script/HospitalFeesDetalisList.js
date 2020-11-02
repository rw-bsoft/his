$package("phis.application.hos.script")

$import("phis.script.SimpleList")

phis.application.hos.script.HospitalFeesDetalisList = function(cfg) {
	cfg.autoLoadData = false;
	cfg.disablePagingTbr = true;
	cfg.height = 181
	cfg.showRowNumber = false;
	phis.application.hos.script.HospitalFeesDetalisList.superclass.constructor
			.apply(this, [ cfg ]);
}

Ext
		.extend(
				phis.application.hos.script.HospitalFeesDetalisList,
				phis.script.SimpleList,
				{
					loadData : function() {
						var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : "hospitalPatientSelectionService",
							serviceAction : "getSelectionDetailsList",
							body : this.data
						});
						if (ret.code > 300) {
							this.processReturnMsg(ret.code, ret.msg);
							return null;
						}
						this.details = ret.json.body;
						this.requestData.serviceId = "phis.hospitalPatientSelectionService";
						this.requestData.serviceAction = "getSelectionFeesDetailsList";
						// alert(Ext.encode(this.data));
						this.requestData.body = this.data;
						this.requestData.openBy = this.openBy;
						phis.application.hos.script.HospitalFeesDetalisList.superclass.loadData
								.call(this);
					},
					doCancel : function() {
						this.opener.getWin().hide();
					},
					onRowClick : function(grid, index, e) {
						this.selectedIndex = index;
						var r = this.store.getAt(index);
						var data = [];
						for ( var i = 0; i < this.details.length; i++) {
							if(this.details[i].YSGH == 'null' || this.details[i].YSGH == null){
								this.details[i].YSGH = "";
							}
							if(this.details[i].FYKS == 'null' || this.details[i].FYKS == null){
								this.details[i].FYKS = "";
							}
							if(this.details[i].ZXKS == 'null' || this.details[i].ZXKS == null){
								this.details[i].ZXKS = "";
							}
							if (this.openBy == "sfxm"
									&& r.data.FYRQ == this.details[i].FYRQ
									&& r.data.FYXM == this.details[i].FYXM
									&& r.data.FYKS == this.details[i].FYKS
									&& r.data.ZXKS == this.details[i].ZXKS
									&& r.data.YSGH == this.details[i].YSGH) {
								this.opener.DetailsList.requestData.schema = "phis.application.hos.schemas.ZY_FYMX_RQMX";
								var zyhcnd = ['eq',['$','ZYH'],['i',r.data.ZYH]];
								var jscscnd = ['eq',['$','JSCS'],['i',r.data.JSCS]];
								var zxkscnd = ['or',['isNull',['$','ZXKS']],['eq',['$','ZXKS'],['i',0]]];
								if(r.data.ZXKS){
									zxkscnd = ['eq',['$','ZXKS'],['i',r.data.ZXKS]];
								}
								var ysghcnd = ['isNull',['$','YSGH']];
								if(r.data.YSGH){
									ysghcnd = ['eq',['$','YSGH'],['s',r.data.YSGH]];
								}
								var fykscnd = ['isNull',['$','FYKS']];
								if(r.data.FYKS){
									fykscnd = ['eq',['$','FYKS'],['i',r.data.FYKS]];
								}
								var fyxmcnd = ['eq',['$','FYXM'],['i',r.data.FYXM]];
								this.opener.DetailsList.requestData.cnd = ['and',zyhcnd,jscscnd,zxkscnd,ysghcnd,fykscnd,fyxmcnd];
								this.opener.DetailsList.requestData.pageSize = null;
								this.opener.DetailsList.requestData.pageNo = null;
								data.push(this.details[i]);
								
							}else if(this.openBy == "mxxm"
								&& r.data.FYXH == this.details[i].FYXH
								&& r.data.FYMC == this.details[i].FYMC){
								this.opener.DetailsList.requestData.schema = "phis.application.hos.schemas.ZY_FYMX_MX";
								this.opener.DetailsList.requestData.cnd = ['and',['eq',['$','ZYH'],['i',r.data.ZYH]],['eq',['$','JSCS'],['i',r.data.JSCS]],['eq',['$','FYXH'],['i',r.data.FYXH]],['eq',['$','FYMC'],['s',r.data.FYMC]]];
								this.opener.DetailsList.requestData.pageSize = null;
								this.opener.DetailsList.requestData.pageNo = null;
								data.push(this.details[i]);
							}
						}
						this.opener.detaileLoadData(data);
					},
					onStoreLoadData:function(store,records,ops){
						this.fireEvent("loadData", store) // ** 不管是否有记录，都fire出该事件
						if(records.length == 0){
							return
						}
						this.totalCount = store.getTotalCount()
						if (!this.selectedIndex || this.selectedIndex >= records.length) {
							this.selectRow(0)
							this.selectedIndex = 0;
						}
						else{
							this.selectRow(this.selectedIndex);
						}
						this.onRowClick(null,0);
					}
				});