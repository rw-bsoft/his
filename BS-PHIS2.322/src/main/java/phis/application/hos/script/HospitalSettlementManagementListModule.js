$package("phis.application.hos.script")

$import("phis.script.SimpleModule")

phis.application.hos.script.HospitalSettlementManagementListModule = function(cfg) {
//	cfg.autoLoadData = false;
//	cfg.disablePagingTbr = true;
//	cfg.sortable = false;// 不能排序
	phis.application.hos.script.HospitalSettlementManagementListModule.superclass.constructor
			.apply(this, [cfg]);
}

Ext.extend(phis.application.hos.script.HospitalSettlementManagementListModule,
		phis.script.SimpleModule, {
	initPanel : function() {
		if (this.panel) {
			return this.panel;
		}
		var panel = new Ext.Panel({
					border : false,
					autoScroll : true,
					width : this.width,
					height : this.height,
					frame : false,
					layout : 'border',
					defaults : {
						border : false
					},
					items : [{
								layout : "fit",
								border : false,
								split : true,
								region : 'center',
//								autoScroll : false,
								autoHeight : true,
								width : 300,
								items : this.getList2()
							}, {
								layout : "fit",
								border : false,
								split : true,
								region : 'west',
								width : 300,
//								autoScroll : false,
								autoHeight : true,
								items : this.getList1()
							}]
				});

		this.panel = panel;
//		this.panel.on("afterrender", this.onReady, this)
		return panel;
	},
//	onReady : function(){},
	getList1 : function() {
		var module = this.createModule("refList1",
				this.refList1);
//		module.on("setSettle", this.setSettle, this)
		this.list1 = module;
		module.opener = this;
		return module.initPanel();
	},
	getList2 : function() {
		var module = this.createModule("refList2",
				this.refList2);
//		module.on("setSettle", this.setSettle, this)
		this.list2 = module;
		module.opener = this;
		return module.initPanel();
	},
	loadData : function(){
		this.list1.ldata = this.ldata;
		this.list2.ldata = this.ldata;
		this.list1.loadData();
		this.list2.loadData();
	},
//			loadData : function() {
//				this.requestData.serviceId = "phis.hospitalPatientSelectionService";
//				this.requestData.serviceAction = "getSelectionList";
//				this.requestData.body = this.ldata;
//				phis.application.hos.script.HospitalSettlementManagementListModule.superclass.loadData
//						.call(this);
//			},
//			onStoreLoadData : function(store, records, ops) {
//				if (records.length == 0) {
//					document.getElementById("ZTJS_FYZK_HJ1" + this.openBy).innerHTML = "";
//					this.fireEvent("noRecord", this);
//					return
//				}
//				// var store = this.grid.getStore();
//				var n = store.getCount();
//				var ZJJE = 0;
//				for (var i = 0; i < n; i++) {
//					var r = store.getAt(i);
//					ZJJE += r.get("ZJJE");
//					ZJJE += r.get("ZJJE2");
//				}
//				var numStr = ['零', '壹', '贰', '叁', '肆', '伍', '陆', '柒', '捌', '玖'];
//				var rmbStr = ['分', '角', '', '元', '拾', '佰', '千', '万', '拾', '佰',
//						'千']
//				ZJJE = parseFloat(ZJJE).toFixed(2);
//				var ZJJEStr = ZJJE + "";
//				var l = ZJJEStr.length;
//				var RMB = "";
//				for (var i = 0; i < l; i++) {
//					if('-'==ZJJEStr.charAt(i)){
//							RMB += '负';
//					}else{
//						if (l - i != 3) {
//							RMB += numStr[ZJJEStr.charAt(i)];
//							RMB += rmbStr[l - i - 1];
//						}
//					}
//				}
//				if(ZJJE>0){
//					this.fireEvent("setSettle", false);
//				}else{
//					this.fireEvent("setSettle", true);
//				}
//				document.getElementById("ZTJS_FYZK_HJ1" + this.openBy).innerHTML = " 合 计 金 额 ："
//						+ ZJJE + "<br/>人民币大写：" + RMB;
//			},
			expansion : function(cfg) {
				cfg.enableHdMenu = false;// 不显示menu
				cfg.enableColumnHide = false;// 不显示menu
				cfg.enableDragDrop = false;// 不能拖动
				cfg.enableColumnMove = false;// 不能拖动
				// 底部 统计信息,未完善
				var label = new Ext.form.Label({
					html : "<div id='ZTJS_FYZK_HJ1"
							+ this.openBy
							+ "' align='left' style='color:blue'> 合 计 金 额 ：<br/>人民币大写：</div>"
				})
				cfg.bbar = [];
				cfg.bbar.push(label);
			},
			labelClear : function() {
				document.getElementById("ZTJS_FYZK_HJ1" + this.openBy).innerHTML = " 合 计 金 额 ：<br/>人民币大写：";
			}
		});