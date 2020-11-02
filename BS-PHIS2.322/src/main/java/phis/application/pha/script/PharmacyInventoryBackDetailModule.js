$package("phis.application.pha.script");

$import("phis.application.pha.script.PharmacyMySimpleDetailModule");

phis.application.pha.script.PharmacyInventoryBackDetailModule= function(cfg) {
	cfg.title = "调拨申请单";
	phis.application.pha.script.PharmacyInventoryBackDetailModule.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(phis.application.pha.script.PharmacyInventoryBackDetailModule,
		phis.application.pha.script.PharmacyMySimpleDetailModule, {
			doNew : function() {
				phis.application.pha.script.PharmacyInventoryBackDetailModule.superclass.doNew
						.call(this);
				this.list.remoteDicStore.baseParams = {
					"tag" : "dbty",
					"mbyf" : this.selectValue
				}
			},
			doLoad : function(initDataBody) {
				this.list.remoteDicStore.baseParams = {
					"tag" : "dbty",
					"mbyf" : this.selectValue
				}
				this.form.initDataBody = initDataBody;
				this.form.op = this.op;
				this.form.loadData();
				this.list.op = this.op;
				this.list.requestData.cnd = ['and',
						['eq', ['$', 'SQYF'], ['l', initDataBody.SQYF]],
						['eq', ['$', 'SQDH'], ['i', initDataBody.SQDH]]];
				this.list.loadData();
			},
			afterLoad : function(entryName, body) {
				this.panel.items.items[0].setTitle("NO: " + body.SQDH);
			},
			getListData : function() {
				var ck02 = new Array();
				var count = this.list.store.getCount();
				for (var i = 0; i < count; i++) {
					if (this.list.store.getAt(i).data["YPXH"] != ''
							&& this.list.store.getAt(i).data["YPXH"] != null
							&& this.list.store.getAt(i).data["YPXH"] != 0
							&& this.list.store.getAt(i).data["YPCD"] != ''
							&& this.list.store.getAt(i).data["YPCD"] != 0
							&& this.list.store.getAt(i).data["YPCD"] != null
							&& this.list.store.getAt(i).data["YPSL"] != ''
							&& this.list.store.getAt(i).data["YPSL"] != 0
							&& this.list.store.getAt(i).data["YPSL"] != null) {
						if (this.list.store.getAt(i).data.LSJE < -99999999.99) {
							MyMessageTip.msg("提示",
									"第" + (i + 1) + "行零售金额超过最大值", true);
							this.panel.el.unmask();
							return;
						}
						if (this.list.store.getAt(i).data.JHJE < -99999999.99) {
							MyMessageTip.msg("提示",
									"第" + (i + 1) + "行进货金额超过最大值", true);
							this.panel.el.unmask();
							return;
						}
						ck02.push(this.list.store.getAt(i).data);
					}
				}
				return ck02;
			}
		});