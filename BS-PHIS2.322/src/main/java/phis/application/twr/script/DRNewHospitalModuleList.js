$package("phis.application.twr.script")
$import("phis.script.SimpleList")
phis.application.twr.script.DRNewHospitalModuleList = function(cfg) {
	phis.application.twr.script.DRNewHospitalModuleList.superclass.constructor
			.apply(this, [ cfg ])

}
Ext.extend(phis.application.twr.script.DRNewHospitalModuleList,
		phis.script.SimpleList, {
//			expansion : function(cfg) {
//				var radiogroup = [];
//				var itemName = [ '全部','待审核','已审核','全部','待入院','已入院','已出院' ];
//				this.jczxzt = 1;
//				for ( var i = 1; i < 4; i++) {
//					radiogroup.push({
//						xtype : "radio",
//						checked : i == 1,
//						boxLabel : itemName[i - 1],
//						inputValue : i,
//						name : "jczxzt",
//						listeners : {
//							check : function(group, checked) {
//								if (checked) {
//									var jczxzt = group.inputValue;
//									this.jczxzt = jczxzt;
//									this.doQuery();
//								}
//							},
//							scope : this
//						}
//					})
//				}
//				this.radiogroup = radiogroup;
//				var tbar = cfg.tbar;
//				delete cfg.tbar;
//				cfg.tbar = [];
//				cfg.tbar.push([ this.radiogroup, tbar ]);
//			},
			loadData : function() {
				this.clear(); // ** add by yzh , 2010-06-09 **
				recordIds = [];
				this.requestData.serviceId = "phis.referralService";
				this.requestData.serviceAction = "queryZyZzInfo";
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
			doNew:function(){
				var HFList = this.createModule("HFList","phis.application.cic.CIC/CIC/CIC28");
				HFList.exContext = this.exContext;
				HFList.opener = this;
				var win = HFList.getWin();
				win.setHeight(575);
				win.add(HFList.initPanel());
				win.show();
			},
			doCancel : function() {
// var r = this.getSelectedRecord()
// if (r == null) {
// return
// }
// this.grid.el.mask("正在取消数据...", "x-mask-loading");
// phis.script.rmi.jsonRequest({
// serviceId : "referralService",
// serviceAction : "registerDevicerCancel",
// body : {
// "EMPIID":r.data.EMPIID,
// "YUYUESQDBH":r.data.JIANCHASQDH
// }
// }, function(code, msg, json) {
// this.grid.el.unmask()
// if (code > 300) {
// MyMessageTip.msg("提示", msg, true)
// return
// }else{
// MyMessageTip.msg("提示", "取消成功!", true)
// }
// }, this)// jsonRequest
			},
			doLook:function(){
				var r = this.getSelectedRecord()
				if (r == null) {
					MyMessageTip.msg("提示", "请选择记录!", true)
					return
				}
				this.winModule = this.createModule(
						"winModule", this.winModule);
				this.winModule.zhuanzhendh=r.data.ZHUANZHENDH,
				this.winModule.initPanel();
				var win = this.winModule.getWin();
				win.add(this.winModule.initPanel());
				win.setWidth(960);
				win.setHeight(600);
				win.show();
				win.center();
				this.winModule.loadData();
				
				
			}
			
		})