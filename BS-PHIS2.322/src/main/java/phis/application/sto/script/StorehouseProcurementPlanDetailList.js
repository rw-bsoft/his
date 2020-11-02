/**
 * 采购计划详细list
 * 
 * @author caijy
 */
$package("phis.application.sto.script");

$import("phis.application.sto.script.StorehouseMySimpleDetailList");

phis.application.sto.script.StorehouseProcurementPlanDetailList = function(cfg) {
	cfg.remoteUrl = "Medicines";
	cfg.remoteTpl = '<td width="12px" style="background-color:#deecfd">{numKey}.</td><td width="100px">{YPMC}</td><td width="70px">{YFGG}</td><td width="20px">{YFDW}</td><td width="80px">{CDMC}</td><td width="70px">{LSJG}</td>';
	cfg.minListWidth = 600;
	cfg.queryParams = {
		"tag" : "cszc"
	};
	cfg.count = 12;
	cfg.toColumnNum = 1;// 换行到下一行的哪列
	cfg.labelText = false;// 底部合计
	phis.application.sto.script.StorehouseProcurementPlanDetailList.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(phis.application.sto.script.StorehouseProcurementPlanDetailList,
		phis.application.sto.script.StorehouseMySimpleDetailList, {
			doJshj : function() {
			},
			expansion : function(cfg) {
			},
			getRemoteDicReader : function() {
				return new Ext.data.JsonReader({
							root : 'mds',
							// 类里面总数的参数名
							totalProperty : 'count',
							id : 'mdssearch'
						}, [{
									name : 'numKey'
								}, {
									name : 'YPCD'
								}, {
									name : 'YPXH'
								}, {
									name : 'CDMC'
								}, {
									name : 'LSJG'
								}, {
									name : 'JHJG'
								}, {
									name : 'YPMC'
								}, {
									name : 'YFGG'
								}, {
									name : 'YFDW'
								}, {
									name : 'KWBM'
								}, {
									name : 'KCSL'
								}, {
									name : 'GCSL'
								}, {
									name : 'DCSL'
								}]);
			},
			// 数据回填
			setBackInfo : function(obj, record) {
				// 将选中的记录设置到行数据中
				var cell = this.grid.getSelectionModel().getSelectedCell();
				var row = cell[0];
				var col = cell[1];
				var griddata = this.grid.store.data;
				var ypcd = record.get("YPCD");
				var ypxh = record.get("YPXH");
				for (var i = 0; i < griddata.length; i++) {
					if (griddata.itemAt(i).get("YPCD") == ypcd
							&& griddata.itemAt(i).get("YPXH") == ypxh
							&& i != row
							&& (griddata.itemAt(i).get("YPXQ") == ""
									|| griddata.itemAt(i).get("YPXQ") == null || griddata
									.itemAt(i).get("YPXQ") == undefined)
							&& (griddata.itemAt(i).get("YPPH") == ""
									|| griddata.itemAt(i).get("YPPH") == null || griddata
									.itemAt(i).get("YPPH") == undefined)) {
						MyMessageTip.msg("提示", "该药品已存在,请修改此药品", true);
						this.remoteDic.lastQuery = "";
						//this.remoteDic.clearValue();
						return;
					}
				}
				var body = {
					"YPXH" : record.get("YPXH"),
					"YPCD" : record.get("YPCD")
				};
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.queryKcslActionId,
							body : body
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.doCommit);
					return;
				}
				obj.collapse();
				var rowItem = griddata.itemAt(row);
				rowItem.set('KCSL', ret.json.body.YKKC);
				rowItem.set('YPCD', record.get("YPCD"));
				rowItem.set('YPXH', record.get("YPXH"));
				rowItem.set('CDMC', record.get("CDMC"));
				rowItem.set('YPMC', record.get("YPMC"));
				rowItem.set('GJJG', record.get("JHJG"));
				rowItem.set('BZLJ', record.get("LSJG"));
				rowItem.set('YPGG', record.get("YFGG"));
				rowItem.set('YPDW', record.get("YFDW"));
				rowItem.set('GCSL', record.get("GCSL"));
				rowItem.set('DCSL', record.get("DCSL"));
				if (rowItem.get("JHSL") != null && rowItem.get("JHSL") != ""
						&& rowItem.get("JHSL") != 0) {
					rowItem.set('GJJE', (record.get("LSJG") * rowItem
									.get("JHSL")).toFixed(4));
					this.doJshj();
				} else {
					rowItem.set('LSJE', 0);
					rowItem.set('JHJE', 0);
				}
				// obj.setValue(record.get("YPMC"));
				this.remoteDic.lastQuery = "";
				// this.remoteDic.clearValue();//注释掉防止第二次输入,全部为空
				obj.setValue(record.get("YPMC"));
				obj.triggerBlur();
				this.isEdit = true;
				this.grid.startEditing(row, 7);
			},
			onBeforeCellEdit : function(it, record, field, v) {
				if (this.isRead&&this.op!="sp") {
					return false;
				}
				var cell = this.grid.getSelectionModel().getSelectedCell();
				if(!cell){
				return true;
				}
				var row = cell[0];
				var col = cell[1];
				if (this.op == "create" || this.op == "update") {
					if (it.id == "SPSL") {
						if(record.get("YPXH")==null||record.get("YPXH")==""){
						this.grid.startEditing(row, 1);
						}else{
						if(this.store.getCount()>row+1){
						this.grid.startEditing(row+1, 1);
						}else{
						this.doCreate();
						}
						}
						return false;
					}
				}
				if (this.op == "sp"){
					if(it.id=="SPSL"){
					return true;
					}else{
					return false;}
//				if (it.id == "JHSL") {
//				this.grid.startEditing(row, col+2);
//				return false;
//				}
				}
					return true;
			},// 数量操作后
			onAfterCellEdit : function(it, record, field, v) {
				if(it.id=="JHSL"){
				record.set("GJJE",(record.get("GJJG") * v).toFixed(4))
				}
			},
			loadData:function(){
			this.requestData.serviceId=this.fullserviceId;
			this.requestData.serviceAction=this.serviceAction;
			phis.application.sto.script.StorehouseProcurementPlanDetailList.superclass.loadData.call(this)
			},
			//备注回车新增一行
			doCreateRow:function(){
				var storeCount=this.store.getCount();
				if(storeCount==0){
					this.doCreate();
				}else{
				var row=storeCount-1;
				if(this.store.getAt(row).get("YPXH")==null||this.store.getAt(row).get("YPXH")==""||this.store.getAt(row).get("YPXH")==0||this.store.getAt(row).get("YPXH")==undefined){
				this.grid.startEditing(row, 1);
				}else{
				this.doCreate();
				}
				}
			}
		});