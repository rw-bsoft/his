$package("phis.application.sto.script")

$import("phis.application.sto.script.StorehouseMySimpleDetailList")

phis.application.sto.script.StorehouseCheckOutCommitDetailList = function(cfg) {
	cfg.labelText=" 零售合计:0  进货合计:0 ";
	phis.application.sto.script.StorehouseCheckOutCommitDetailList.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.sto.script.StorehouseCheckOutCommitDetailList,
		phis.application.sto.script.StorehouseMySimpleDetailList, {
			onAfterCellEdit : function(it, record, field, v) {
				if (v == "" || v == undefined) {
					v = 0;
				}
				if (it.id == "SFSL") {
					if (v > parseFloat(record.get("KCSL"))) {
						MyMessageTip.msg("提示", "实发数量不能大于库存数量", true);
						record.set("SFSL", parseFloat(record.get("KCSL")));
						return;
					}
					record.set("JHJE", (parseFloat(v) * parseFloat(record
									.get("JHJG"))).toFixed(4));
					record.set("LSJE", (parseFloat(v) * parseFloat(record
									.get("LSJG"))).toFixed(4));
				}
					this.doJshj();
			},
			onBeforeCellEdit : function(it, record, field, v) {
				if (this.isRead||this.dyfs==6) {
					return false;
				}
				return true;
			},
			onDblClick : function() {
				if(this.isRead){
				return;}
				var record = this.getSelectedRecord()
				if (record == null) {
					return
				}
				if(record.get("SFSL")<0){
				return;
				}
				this.checkRecord=record;
				this.list = this.createModule("list", this.refList);
				this.list.on("checkData", this.onCheckData, this);
				this.list.on("close", function(){
					this.getWin().hide();
				}, this);
				var m = this.list.initPanel();
				var win = this.getWin();
				win.add(m);
				win.show()
				win.center()
				if (!win.hidden) {
					this.list.requestData.cnd = [
							'and',
							['eq', ['$', 'JGID'], ['$', '%user.manageUnit.id']],
							[
									'and',
									['eq', ['$', 'YPXH'],
											['l', record.get("YPXH")]],
									['eq', ['$', 'YPCD'],
											['l', record.get("YPCD")]]]];
					this.list.loadData();
				}
			},
			onCheckData : function(sbxh, kcsl,ypph,ypxq,jhjg) {
				this.getWin().hide();
				var n = this.store.getCount();
				for (var i = 0; i < n; i++) {
				if(sbxh==this.store.getAt(i).data.KCSB){
					MyMessageTip.msg("提示", "选择的药品批次已经存在!", true);
					return;
				}
				}
				if(kcsl<this.checkRecord.get("SFSL")){
				MyMessageTip.msg("提示", "该批次药品库存不足,不能出库!", true);
				return;
				}
				this.checkRecord.set("KCSL",kcsl);
				this.checkRecord.set("KCSB",sbxh);
				this.checkRecord.set("YPPH",ypph);
				this.checkRecord.set("YPXQ",ypxq);
				this.checkRecord.set("JHJG",jhjg);
				this.checkRecord.set("JHJE", (parseFloat(jhjg) * parseFloat(this.checkRecord.get("SFSL"))).toFixed(4));
				this.doJshj();
				this.isEdit=true;
			},
			doJshj : function() {
				if(!this.label){
				return;}
				var n = this.store.getCount()
				var hjje = 0;
				var lsje = 0;
				for (var i = 0; i < n; i++) {
					var r = this.store.getAt(i);
					if(r.get("YPXH")==null||r.get("YPXH")==""||r.get("YPXH")==undefined){
					continue;
					}
					hjje += parseFloat(r.get("JHJE"));
					lsje += parseFloat(r.get("LSJE"));
				}
				this.label.setText("合计   零售金额:"+ lsje.toFixed(4) + " 进货金额:" + hjje.toFixed(4));
			}
		})