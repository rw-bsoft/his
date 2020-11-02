/**
 * 采购入库详细list
 * 
 * @author caijy
 */
$package("phis.application.sto.script");

$import("phis.application.sto.script.StorehouseMySimpleDetailList");

phis.application.sto.script.StorehouseCheckInDetailList = function(cfg) {
	cfg.remoteUrl = "Medicines";
	cfg.remoteTpl = '<td width="12px" style="background-color:#deecfd">{numKey}.</td><td width="100px">{YPMC}</td><td width="70px">{YFGG}</td><td width="20px">{YFDW}</td><td width="80px">{CDMC}</td><td width="70px">{LSJG}</td><td width="70px">{JHJG}</td>';
	cfg.minListWidth = 600;
	cfg.queryParams = {
		"tag" : "cszc"
	};
	//cfg.columnNum=3;//换行
	cfg.count=12;
	cfg.toColumnNum=1;//换行到下一行的哪列
	cfg.labelText=" 零售合计:0  进货合计:0  差价合计: 0";
	phis.application.sto.script.StorehouseCheckInDetailList.superclass.constructor.apply(
			this, [cfg]);
}
Ext.extend(phis.application.sto.script.StorehouseCheckInDetailList,
				phis.application.sto.script.StorehouseMySimpleDetailList, {
	setBackInfo : function(obj, record) {
				// 将选中的记录设置到行数据中
				obj.collapse();
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
				//Ext.EventObject.stopEvent();// 停止事件
				var rowItem = griddata.itemAt(row);
				rowItem.set('YPCD', record.get("YPCD"));
				rowItem.set('YPXH', record.get("YPXH"));
				rowItem.set('CDMC', record.get("CDMC"));
				rowItem.set('YPMC', record.get("YPMC"));
				rowItem.set('LSJG', record.get("LSJG"));
				rowItem.set('JHJG', record.get("JHJG"));
				rowItem.set('YPGG', record.get("YFGG"));
				rowItem.set('YPDW', record.get("YFDW"));
				rowItem.set('YFBZ', record.get("YFBZ"));
				rowItem.set('PFJG', record.get("PFJG"));
				rowItem.set('DJFS', record.get("DJFS"));
				rowItem.set('DJGS', record.get("DJGS"));
				rowItem.set('BZLJ', record.get("BZLJ"));
				rowItem.set('RKSL', 0);
				if (rowItem.get("RKSL") != null && rowItem.get("RKSL") != ""
						&& rowItem.get("RKSL") != 0) {
					rowItem.set('LSJE', (record.get("LSJG") * rowItem
									.get("RKSL")).toFixed(4));
					rowItem.set('JHHJ', (record.get("JHJG") * rowItem
									.get("RKSL")).toFixed(4));
				} else {
					rowItem.set('LSJE', 0);
					rowItem.set('JHJE', 0);
				}
				obj.setValue(record.get("YPMC"));
				obj.triggerBlur();
				this.remoteDic.lastQuery = "";
				//this.remoteDic.clearValue();//注释掉防止第二次输入,全部为空
				this.grid.startEditing(row, 6);
			},// 数量操作后
			onAfterCellEdit : function(it, record, field, v) {
				var cell = this.grid.getSelectionModel().getSelectedCell();
				var row = cell[0];
				if (it.id == "YPMC") {
					if (v == null || v == "" || v == 0) {
						record.set('YPCD', "");
						record.set('YPXH', "");
						record.set('CDMC', "");
						record.set('LSJG', 0);
						record.set('JHJG', 0);
						record.set('YPGG', "");
						record.set('YPDW', "");
						record.set('PFJG', 0);
						record.set('LSJE', 0);
						record.set('JHHJ', 0);
					}
				}
				if (it.id == "YPPH") {
					if (v.length > 20) {
						MyMessageTip.msg("提示", "批号长度不能超过20位", true);
						v = v.substring(0, 20);
						record.set("YPPH", v);
					}
				}
				if (it.id == "RKSL") {

					// if(!record.get("YPXH")){return;}
					var pfje = 0;
					var jhje = 0;
					var lsje = 0;
					var cjje = 0;
					if (((v != null && v != ""&& v != undefined) || v == 0)
							&& record.get("YPXH") != undefined
							&& record.get("YPXH") != "") {
						pfje = (v * record.get("PFJG")).toFixed(4);
						jhje = (v * record.get("JHJG")).toFixed(4);
						lsje = (v * record.get("LSJG")).toFixed(4);
						cjje = (lsje - jhje).toFixed(4);
					}
					record.set("JHHJ", jhje);
					record.set("PFJE", pfje);
					record.set("LSJE", lsje);
					record.set("CJHJ", cjje);
					if (!this.editRecords) {
						this.editRecords = [];
					}
					this.editRecords.push(record.data);
					this.doJshj();
					if (v < 0) {
						if (record.get("YPXH") == undefined
								|| record.get("YPXH") == "") {
							MyMessageTip.msg("提示", "没选药品入库数量不能为负!", true);
							record.set('RKSL', 0);
							return;
						}else if(record.get("JHSBXH")){
							MyMessageTip.msg("提示", "计划单明细入库数量不能为负!", true);
							record.set('RKSL', 0);
							record.set('JHJE', 0);
							record.set('LSJE', 0);
							record.set('CJHJ', 0);
							this.doJshj();
							return;
						} else {
							this.checkRecord = record;
							this.list = this.createModule("list", this.refList);
							this.list.on("checkData", this.onCheckData, this);
							this.list.on("hide", this.onClose, this);
							var m = this.list.initPanel();
							var win = this.list.getWin();
							win.add(m);
							win.show()
							win.center()
							if (!win.hidden) {
								this.list.requestData.cnd = [
										'and',
										['eq', ['$', 'JGID'],
												['$', '%user.manageUnit.id']],
										[
												'and',
												[
														'eq',
														['$', 'YPXH'],
														[
																'l',
																record
																		.get("YPXH")]],
												[
														'eq',
														['$', 'YPCD'],
														[
																'l',
																record
																		.get("YPCD")]]]];
								this.list.loadData();
								return;
							}
						}

					}

				}
				if (it.id == "YPXQ") {
					var today = new Date().format('Ymd');
					//这样转成数字比较是因为ie8不支持date的gettime
					var d=(v.substring(0,10)).split('-');
					var date = d[0]+d[1]+d[2];
					if (today > date) {
						Ext.Msg.alert("提示", "药品已过期，请确认效期是否填写正确");
					}else if(today == date){
						Ext.Msg.alert("提示", "药品即将过期，请确认效期是否填写正确");
					}
				}
				if (it.id == "HGSL") {
					var rksl = parseFloat(record.get("RKSL"));
					if (v > rksl) {
						Ext.Msg.alert("提示", "合格数量不能超过入库数量");
						record.set("HGSL", rksl);
						record.set("CPSL", 0);
					} else {
						record.set("CPSL", rksl - v);
					}
				}
				if (it.id == "JHJG") {
					var jhhj=0;
					if (((v != null && v != ""&& v != undefined) || v == 0)
							&& record.get("YPXH") != undefined
							&& record.get("YPXH") != "") {
							jhhj=(parseFloat(v) * parseFloat(record
									.get("RKSL"))).toFixed(4);
							}
					record.set("JHHJ", jhhj);
					record.set("CJHJ", record.get("LSJE")- record.get("JHHJ"));
					if((parseInt(record.get("DJFS"))==1||parseInt(record.get("DJFS"))==2)&&(record.get("DJGS")+"").indexOf("实际进价")!=-1){//如果是公式定价并且按进价来计算
					var body={"JHJG":v,"LSJG":record.get("BZLJ"),"DJGS":record.get("DJGS")}
					var r = phis.script.rmi.miniJsonRequestSync({
							serviceId :"storehouseManageService",
							serviceAction : "jsLsjg",
							body : body
						});
					if (r.code > 300) {
						MyMessageTip.msg("提示", "零售价格计算失败,请检查定价公式是否正确", true);
						record.set("LSJG",0);
						record.set("LSJE",0);
						return;
					}
					var lsjg=parseFloat(r.json.lsjg)
					record.set("LSJG",lsjg);
					record.set("LSJE",(lsjg * parseFloat(record.get("RKSL"))).toFixed(4))
					}
					this.doJshj();
				}
				if(it.id=="LSJG"){
				if (((v != null && v != ""&& v != undefined) || v == 0)
							&& record.get("YPXH") != undefined
							&& record.get("YPXH") != "") {
								record.set("LSJE",(parseFloat(v) * parseFloat(record
									.get("RKSL"))).toFixed(4));
								record.set("CJHJ", record.get("LSJE")- record.get("JHHJ"));
							}
					this.doJshj();
				}
			},
			doCreate : function(item, e) {
				phis.application.sto.script.StorehouseMySimpleDetailList.superclass.doCreate
						.call(this);
				var store = this.grid.getStore();
				var n = store.getCount() - 1
				if(n>0){
				store.getAt(n).set("FPHM",store.getAt(n-1).get("FPHM"));
				}
				this.grid.startEditing(n, 1);
			},
			// 处理库存界面返回的SBXH
			onCheckData : function(sbxh, kcsl,ypph,ypxq,jhjg,lsjg) {
				var count=this.store.getCount();
				var cell = this.grid.getSelectionModel().getSelectedCell();
				var row = cell[0];
				for(var i=0;i<count;i++){
				if(this.store.getAt(i).get("KCSB")==sbxh&&i!=row){
				MyMessageTip.msg("提示","第"+(i+1)+"行相同批次的药品已经存在,请修改数量!", true);
				this.store.remove(this.checkRecord);
				this.doCreate();
				this.getWin().hide();
				return;
				}
				}
				this.checkRecord.set("KCSB", sbxh);
				this.checkRecord.set("YPPH", ypph);
				this.checkRecord.set("YPXQ", ypxq);
				this.checkRecord.set("JHJG", jhjg);
				this.checkRecord.set("LSJG", lsjg);
				if (kcsl < -this.checkRecord.get("RKSL")) {
					MyMessageTip.msg("提示", "库存不够!", true);
					this.checkRecord.set("RKSL", -kcsl);
					this.checkRecord.set("LSJE", (parseFloat(this.checkRecord.get("LSJG")) * parseFloat(this.checkRecord.get("RKSL"))).toFixed(4));
				}
				this.checkRecord.set("JHHJ", (parseFloat(jhjg) * parseFloat(this.checkRecord.get("RKSL"))).toFixed(4));
				this.checkRecord.set("LSJE", (parseFloat(lsjg) * parseFloat(this.checkRecord.get("RKSL"))).toFixed(4));
				this.doJshj();
				this.onClose();
			},
			onClose : function() {
				// alert(this.checkRecord.get("KCSB"))
				if (this.checkRecord.get("KCSB") == null
						|| this.checkRecord.get("KCSB") == 0
						|| this.checkRecord.get("KCSB") == undefined) {
					MyMessageTip.msg("提示", "未选择库存,库存数量不能为负", true);
					this.checkRecord.set("RKSL", 0);
					this.checkRecord.set("JHHJ", 0);
					this.checkRecord.set("PFJE", 0);
					this.checkRecord.set("LSJE", 0);
				}
				//this.getWin().hide();
				this.doJshj();
			},
			onBeforeCellEdit : function(it, record, field, v) {
				if (it.id == "HGSL") {
					if (parseFloat(record.get("HGSL")) <= 0) {
						return false;
					}
				}
				if (it.id == "TYPE") {
					if (parseFloat(record.get("CPSL")) == 0) {
						return false;
					}
				}
//				if(it.id=="LSJG"){
//				if(record.get("DJFS")!=2&&record.get("DJFS")!=3){
//				return false;
//				}
//				}
				if(it.id=="YPMC"){
				if(record.get("JHSBXH")&&record.get("JHSBXH")!=null){
				MyMessageTip.msg("提示", "采购单引入药品不能修改", true);
				return false;
				}
				}
				return true;
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
			,
			doJshj : function() {
				if(!this.label){
				return;}
				var n = this.store.getCount()
				var hjje = 0;
				var lsje = 0;
				var cjje = 0;
				for (var i = 0; i < n; i++) {
					var r = this.store.getAt(i);
					if(r.get("YPXH")==null||r.get("YPXH")==""||r.get("YPXH")==undefined||r.get("RKSL")==null||r.get("RKSL")==""||r.get("RKSL")==undefined){
					continue;
					}
					hjje += parseFloat(r.get("JHHJ"));
					lsje += parseFloat(r.get("LSJE"));
					cjje += parseFloat(r.get("CJHJ"));
				}
				this.label.setText("零售合计:"+ lsje.toFixed(4) + "  进货合计:" + hjje.toFixed(4)+"  差价合计:" + cjje.toFixed(4));
			}
});