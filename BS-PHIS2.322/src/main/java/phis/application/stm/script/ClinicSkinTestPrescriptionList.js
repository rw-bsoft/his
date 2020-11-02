$package("phis.application.stm.script")

$import("phis.script.SimpleList")

phis.application.stm.script.ClinicSkinTestPrescriptionList = function(cfg) {
	cfg.disablePagingTbr = true;
	cfg.selectOnFocus = true;
	cfg.autoLoadData = false;
	cfg.showRowNumber = true;
	cfg.sortable = false;
	cfg.minListWidth = 510;
	this.serviceId = "clinicManageService";
	phis.application.stm.script.ClinicSkinTestPrescriptionList.superclass.constructor
			.apply(this, [cfg])
	this.on("loadData", this.afterLoadData, this);
}

Ext.extend(phis.application.stm.script.ClinicSkinTestPrescriptionList,
		phis.script.SimpleList, {
			onEnterKey : function() {
				Ext.EventObject.stopEvent();
			},
			afterLoadData : function(store) {
				this.setCountInfo();
			},
			expansion : function(cfg) {
				// 底部 统计信息,未完善
				var label = new Ext.form.Label({
					html : "<div id='cfmx_tjxx_"
							+ this.render
							+ "' align='center' style='color:blue'>统计信息：&nbsp;&nbsp;&nbsp;&nbsp;"
							+ "合计金额：0.00&nbsp;&nbsp;￥&nbsp;&nbsp;&nbsp;&nbsp;"
							+ "自负金额：0.00&nbsp;&nbsp;￥</div>"
				})
				cfg.bbar = [];
				cfg.bbar.push(label);
			},
			setCountInfo : function() {
				var totalMoney = 0;
				var selfMoney = 0;
				var cfts = 1;
				for (var i = 0; i < this.store.getCount(); i++) {
					var r = this.store.getAt(i);
					var ypsl = parseFloat(r.get("YPSL"));
					var ypdj = parseFloat(r.get("YPDJ"));
					var zfbl = parseFloat(r.get("ZFBL"));
					totalMoney += parseFloat(ypsl * ypdj * cfts);
					selfMoney += parseFloat(ypsl * ypdj * zfbl * cfts);
					if (isNaN(totalMoney)) {
						totalMoney = 0;
					}
					if (isNaN(selfMoney)) {
						selfMoney = 0;
					}
				}
				document.getElementById("cfmx_tjxx_" + this.render).innerHTML = "统计信息：&nbsp;&nbsp;&nbsp;&nbsp;"
						+ "合计金额："
						+ parseFloat(totalMoney).toFixed(2)
						+ "&nbsp;&nbsp;￥&nbsp;&nbsp;&nbsp;&nbsp;"
						+ "自负金额："
						+ parseFloat(selfMoney).toFixed(2) + "&nbsp;&nbsp;￥";
				return totalMoney;
			},
			reloadYPZH : function() {
				var count = this.store.getCount();
				var ypzh = 0;
				var lastYPZH = -1;
				for (var i = 0; i < count; i++) {
					var now_ypzh = this.store.getAt(i).get("YPZH_SHOW");
					if (now_ypzh != lastYPZH) {
						ypzh++;
						this.store.getAt(i).set("YPZH_SHOW", ypzh);
						lastYPZH = now_ypzh;
					} else {
						this.store.getAt(i).set("YPZH_SHOW", ypzh);
					}
				}

			},
			loadData : function() {
				// 添加了phis.
				this.requestData.serviceId = "phis.configLogisticsInventoryControlService";
				this.requestData.serviceAction = "queryCfmx";
				this.clear(); // ** add by yzh , 2010-06-09 **
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
			},
			showColor : function(v, params, data) {
				var YPZH = data.get("YPZH_SHOW") % 2 + 1;
				var PSPB = data.get("PSPB");
				var PSJG = data.get("PSJG");
				var JYLX = data.get("JYLX");
				switch (YPZH) {
					case 1 :
						params.css = "x-grid-cellbg-1";
						break;
					case 2 :
						params.css = "x-grid-cellbg-2";
						break;
					case 3 :
						params.css = "x-grid-cellbg-3";
						break;
					case 4 :
						params.css = "x-grid-cellbg-4";
						break;
					case 5 :
						params.css = "x-grid-cellbg-5";
						break;
				}
				if (PSPB > 0) {
					if (PSJG == 1) {
						return "<h2 style='color:red'>阳</h2>";
					}
					if (PSJG == -1) {
						return "<h2 style='color:blue'>阴</h2>";
					}
					return "<h2 style='color:red'>皮</h2>"
				}
				return "";
			},
			totalYPSL : function(v, params, data) {
				return v == null
						? '0'
						: ('<span style="font-size:14px;color:black;">药品记录:&#160;'
								+ v + '</span>');
			}
		});