/**
 * 初始账册界面
 * 
 * @author caijy
 */
$package("phis.application.sto.script");

$import("phis.script.SimpleModule");

phis.application.sto.script.StorehousePaymentModule = function(cfg) { 
	cfg.exContext = {};
	cfg.autoLoadData = false;  
	phis.application.sto.script.StorehousePaymentModule.superclass.constructor.apply( this, [cfg]);
}
Ext.extend(phis.application.sto.script.StorehousePaymentModule, phis.script.SimpleModule, {
			initPanel : function() {
				if (this.mainApp['phis'].storehouseId == null
						|| this.mainApp['phis'].storehouseId == ""
						|| this.mainApp['phis'].storehouseId == undefined) {
					Ext.Msg.alert("提示", "未设置登录药库,请先设置");
					return null;
				}
				// 进行是否初始化验证
				 var ret = phis.script.rmi.miniJsonRequestSync({
				 			serviceId : this.serviceId,
				 		    serviceAction : this.queryServiceAction
				 	});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.initPanel);
					return null;
				}
				if (this.panel) {
					return this.panel;
				}
				var panel = new Ext.Panel({
							border : false,
							width : this.width,
							height : this.height,
							frame : true,
							layout : 'border',
							defaults : {
								border : false
							},
							items : [{
										layout : "fit",
										border : false,
										split : true,
										title : '',
										region : 'north',
										width : 960,
										height : 250,
										items : this.getNorthList()
									}, {
										layout : "fit",
										border : false,
										split : true,
										title : '',
										region : 'center',
										width : 960,
										items : this.getCenterList()
									}],
							tbar : (this.tbar || []).concat(this
									.createButtons())
						});
				this.panel = panel;
				return panel;
			},
			doRefresh : function() {
				this.northList.refresh();
				this.centerList.clear();
			},
			getNorthList : function() {
				this.northList = this.createModule("northList",
						this.refNorthList);
				this.northGrid = this.northList.initPanel();
				this.northList.on("loadData", this.doNorthListLoad, this);
				this.northGrid.on("rowClick", this.doNorthGridClick, this);
				return this.northList.initPanel();
			},
			getCenterList : function() {
				this.centerList = this.createModule("centerList",
						this.refCenterList);
				this.centerGrid = this.centerList.initPanel();
				this.centerList.opener = this
				this.centerList.on("loadData", this.doCenterListLoad, this);
				this.centerGrid.on("rowClick", this.doCenterGridClick, this);
				return this.centerList.initPanel();
			},
			doNorthListLoad : function(store) {
				if (store.getCount() == 0) {
				} else {
					var r = store.getAt(0);
					if (!r)
						return;
					this.centerList.requestData.DWXH = r.data.DWXH;
					this.centerList.loadData();
				}
			},
			doCenterListLoad : function(store) {
				if (store.getCount() == 0) {
				} else {
					var r = store.getAt(0); 
					if (!r)
						return;
					this.body = {};
					this.body.YSDH = r.data.YSDH;
					this.body.RKFS = r.data.RKFS;
					this.body.FPHM = r.data.FPHM;
				}
			},
			doNorthGridClick : function(northGrid, rowIndex, e) {
				var r = northGrid.store.getAt(rowIndex);
				if (!r)
					return;
				this.centerList.requestData.DWXH = r.data.DWXH;
				this.centerList.loadData();
			},
			doCenterGridClick : function(northGrid, rowIndex, e) {
				var r = northGrid.store.getAt(rowIndex);
				if (!r)
					return;
				this.body = {};
				this.body.YSDH = r.data.YSDH;
				this.body.RKFS = r.data.RKFS;
				this.body.FPHM = r.data.FPHM;
			},
			doAction : function(item, e) { 
				var cmd = item.cmd   
				var script = item.script    
				cmd = cmd.charAt(0).toUpperCase() + cmd.substr(1)   
				if (script) {
					$require(script, [function() {
								eval(script + '.do' + cmd
										+ '.apply(this,[item,e])')
							}, this])
				} else {
					var action = this["do" + cmd]
					if (action) { 
						action.apply(this, [item, e]) 
					}
				}
			},
			doPrintSum : function() {
				this.northList.doPrint();
			},
			doPrintDet : function() {
				this.centerList.doPrint();
			},
			doPayment : function() {  
				if(!this.body || !this.body.RKFS){
					Ext.MessageBox.alert('提示', '没有可付款单据，不能进行付款处理!');
					return;
				}
				var module = this.createModule("refPaymentList",this.refPaymentList);
				module.on("paymentSuccessful", this.doRefresh,this);
				var win = module.getWin();
				win.add(module.initPanel());
				module.radiogroup.setValue(1)
				module.requestData.body = this.body;
				module.refresh();
				win.show();
			}
		});