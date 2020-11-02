$package("phis.application.twr.script")
$import("phis.script.SimpleList")
phis.application.twr.script.TwoWayReferralApplicationList = function(cfg) {
	phis.application.twr.script.TwoWayReferralApplicationList.superclass.constructor
			.apply(this, [cfg])

}
Ext.extend(phis.application.twr.script.TwoWayReferralApplicationList,
		phis.script.SimpleList, {
			loadData : function() {
				this.clear();
				recordIds = [];
				this.requestData.serviceId = "phis.referralService";
				this.requestData.serviceAction = "queryZzInfo";
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
			doNew : function() {
				var RFList = this.createModule("RFList",
						"phis.application.cic.CIC/CIC/CIC23");
				RFList.exContext = this.exContext;
				RFList.opener = this;
				var win = RFList.getWin();
				win.add(RFList.initPanel());
				win.show();
			},
			doCancel : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					return
				}
				// console.debug(r.data)
				this.grid.el.mask("正在取消数据...", "x-mask-loading");
				phis.script.rmi.jsonRequest({
							serviceId : "referralService",
							serviceAction : "registerCancel",
							body : {
								"JIUZHENKLX" : "3",
								"JIUZHENKH" : "",
								"BINGRENSFZH" : r.data.BINGRENSFZH,
								"BINGRENXM" : r.data.BINGRENXM,
								"YEWULX" : "1",
								"QUHAOMM" : r.data.QUHAOMM,
								"ZHUANRUYYDM" : r.data.ZHUANRUYYDM,
								"GUAHAOXH" : r.data.GUAHAOXH,
								"ZHUANRUKSDM" : r.data.ZHUANRUKSDM,
								"YISHENGDM" : r.data.YISHENGDM,
								"ZHUANZHENRQ" : r.data.ZHUANZHENRQ
							}
						}, function(code, msg, json) {
							this.grid.el.unmask()
							if (code > 300) {
								MyMessageTip.msg("提示", msg, true)
								return
							} else {
								MyMessageTip.msg("提示", "取消成功!", true)
								this.refresh();
							}
						}, this)// jsonRequest
			},
			doLook : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					MyMessageTip.msg("提示", "请选择记录!", true)
					return
				}
				this.winModule = this.createModule("winModule", this.winModule);
				this.winModule.zhuanzhendh = r.data.ZHUANZHENDH;
				this.winModule.quhaomm = r.data.QUHAOMM;
//				this.winModule.initPanel();
				var win = this.winModule.getWin();
				win.add(this.winModule.initPanel());
				win.setWidth(960);
				win.setHeight(600);
				win.show();
				win.center();
			}
			// ,
			// expansion : function(cfg) {
			// var radiogroup = [];
			// var itemName = ['全部', '未就诊', '已就诊'];
			// this.jzzt = 1;
			// for (var i = 1; i < 4; i++) {
			// radiogroup.push({
			// xtype : "radio",
			// checked : i == 1,
			// boxLabel : itemName[i - 1],
			// inputValue : i,
			// name : "jzzt",
			// listeners : {
			// check : function(group, checked) {
			// if (checked) {
			// var jzzt = group.inputValue;
			// this.jzzt = jzzt;
			// this.doQuery();
			// }
			// },
			// scope : this
			// }
			// })
			// }
			// this.radiogroup = radiogroup;
			// var tbar = cfg.tbar;
			// delete cfg.tbar;
			// cfg.tbar = [];
			// cfg.tbar.push([this.radiogroup, tbar]);
			// }
		})