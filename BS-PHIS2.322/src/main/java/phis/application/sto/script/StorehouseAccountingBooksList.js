/**
 * 会计账簿list
 * 
 * @author caijy
 */
$package("phis.application.sto.script");

$import("phis.script.SimpleList");

phis.application.sto.script.StorehouseAccountingBooksList = function(cfg) {
	phis.application.sto.script.StorehouseAccountingBooksList.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(phis.application.sto.script.StorehouseAccountingBooksList,
		phis.script.SimpleList, {
			initPanel : function(sc) {
				if (this.mainApp['phis'].storehouseId == null
						|| this.mainApp['phis'].storehouseId == ""
						|| this.mainApp['phis'].storehouseId == undefined) {
					Ext.Msg.alert("提示", "未设置登录药库,请先设置");
					return null;
				}
				// 进行是否初始化验证
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.initializationServiceActionID
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.initPanel);
					return null;
				}
				return phis.application.sto.script.StorehouseAccountingBooksList.superclass.initPanel
						.call(this, sc)
			},
			loadData : function() {
				this.dateF=null;
				this.dateT=null;
				this.pydm=null;
				var body = {};
				var dateF = this.dateFrom.getValue();
				var dateT = this.dateTo.getValue();
				if (!dateF || !dateT) {
					Ext.MessageBox.alert("提示", "请输入月结日期");
					return
				}
				if (dateF != null && dateT != null && dateF != ""
						&& dateT != "" && dateF > dateT) {
					Ext.MessageBox.alert("提示", "开始时间不能大于结束时间");
					return;
				}
				this.dateF=dateF;
				this.dateT=dateT;
				body["QSSJ"] = dateF;
				body["ZZSJ"] = dateT;
				var pydm = this.pydmText.getValue();
				if (pydm && pydm != null && pydm != "") {
					body["PYDM"] = pydm.toUpperCase();
					this.pydm=pydm;
				}
				this.requestData.serviceId = this.fullserviceId;
				this.requestData.serviceAction = this.serviceAction;
				this.requestData.body = body;
				phis.application.sto.script.StorehouseAccountingBooksList.superclass.loadData
						.call(this)
			},
			getCndBar : function() {
				var l_kjrq = new Ext.form.Label({
							"text" : "会计时间"
						})
				this.dateFrom = new Ext.ux.form.Spinner({
							fieldLabel : '结账日期开始',
							name : 'dateFrom',
							value : new Date().format('Y-m'),
							strategy : {
								xtype : "month"
							},
							width : 100
						})
				this.dateTo = new Ext.ux.form.Spinner({
							fieldLabel : '结账日期结束',
							name : 'dateTo',
							value : new Date().format('Y-m'),
							strategy : {
								xtype : "month"
							},
							width : 100
						})
				var l_pydm = new Ext.form.Label({
							"text" : "拼音代码"
						})
				this.pydmText = new Ext.form.TextField();
				return [l_kjrq, "-", this.dateFrom, "-", this.dateTo, "-",
						l_pydm, "-", this.pydmText];
			},
			doRefresh : function() {
				this.loadData();
			},
			doPrint : function() {
				//var dateF = this.dateFrom.getValue();
				//var dateT = this.dateTo.getValue();
				if (!this.dateF || !this.dateT) {
					//Ext.MessageBox.alert("提示", "请输入月结日期");
					return
				}
//				if (dateF != null && dateT != null && dateF != ""
//						&& dateT != "" && dateF > dateT) {
//					Ext.MessageBox.alert("提示", "开始时间不能大于结束时间");
//					return;
//				}
				var url = "resources/phis.prints.jrxml.StorehouseAccountingBooks.print?type=1&QSSJ="
						+ this.dateF + "&ZZSJ=" + this.dateT;
			//	var pydm = this.pydmText.getValue();
				if (this.pydm && this.pydm != null && this.pydm != "") {
					url += "&PYDM=" + this.pydm.toUpperCase()
				}
				var LODOP = getLodop();
				LODOP.PRINT_INIT("打印控件");
				LODOP.SET_PRINT_PAGESIZE("0", "", "", "");
				LODOP.ADD_PRINT_HTM("0", "0", "100%", "100%", util.rmi.loadXML(
								{
									url : url,
									httpMethod : "get"
								}));
				LODOP.SET_PRINT_MODE("PRINT_PAGE_PERCENT", "Full-Width");
				LODOP.PREVIEW();
			},
			doDetail : function() {
				var r = this.getSelectedRecord();
				if (!r) {
					return;
				}
				//先查询开始时间是否有月结
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.querySfyjAction,
							body:{"KSSJ":this.dateF,"YPXH":r.get("YPXH"),"YPCD":r.get("YPCD")}
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.initPanel);
					return ;
				}
				var module = this.createModule("detailModule", this.refModule);
				var win = module.getWin();
				win.add(module.initPanel());
				win.show()
				win.center()
				if (!win.hidden) {
					module.dateF=this.dateF;
					module.dateT=this.dateT;
					module.YPXH=r.get("YPXH");
					module.YPCD=r.get("YPCD");
					module.doNew();
					module.doQuery();
				}
			}
		});