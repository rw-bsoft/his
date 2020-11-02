$package("phis.application.war.script");

$import("phis.script.SimpleModule");

phis.application.war.script.DoctorAdviceExecuteModule_t = function(cfg) {
	this.exContext = {};
	cfg.mutiSelect = true;
	cfg.mutiSelect = this.mutiSelect = true;
	phis.application.war.script.DoctorAdviceExecuteModule_t.superclass.constructor.apply(
			this, [cfg]);
	this.on("winShow", this.onWinShow, this);
	this.on("close", this.onWinClose, this);// add by yangl 关闭时抛出事件
}
Ext.extend(phis.application.war.script.DoctorAdviceExecuteModule_t,
		phis.script.SimpleModule, {
			onWinShow : function() {
				if (this.patientGrid) {
					this.doRefresh();
				}
			},
			initPanel : function() {
				// 判断是否有病区
				if (!this.mainApp['phis'].wardId) {
					MyMessageTip.msg("提示", "当前不存在病区，请先选择病区信息!", true);
					return;
				}
				if (this.panel)
					return this.panel;
				var panel = new Ext.Panel({
							border : false,
							frame : true,
							layout : 'border',
							defaults : {
								border : false
							},
							height : 500,
							items : [{
										title : "病人信息",
										layout : "fit",
										border : false,
										split : true,
										region : 'west',
										width : 270,
										items : this.getPatientList()
									}, {
										title : "医嘱明细",
										layout : "fit",
										border : false,
										split : true,
										region : 'center',
										items : this.getDetailsList()
									}],
							tbar : this.createButtons()
						});
				this.panel = panel;
				this.panel.on("afterrender", this.onReady, this)
				// 如果是子窗口就显示关闭按钮，否则不显示
				/*if (this.initDataId) {
					al_zyh = this.initDataId;
				} else {
					var btns = this.panel.getTopToolbar();
					var btn = btns.find("cmd", "close");
					btn[0].hide();
				}*/
				return panel;
			},
			onReady : function() {
				if (!this.initDataId) {
					this.doRefresh();
				}
			},
			doAction : function(item, e) {
				var cmd = item.cmd
				var ref = item.ref

				if (ref) {
					this.loadRemote(ref, item)
					return;
				}
				var script = item.script
				if (cmd == "create") {
					if (!script) {
						script = this.createCls
					}
					this.loadModule(script, this.entryName, item)
					return
				}
				if (cmd == "update" || cmd == "read") {
					var r = this.getSelectedRecord()
					if (r == null) {
						return
					}
					if (!script) {
						script = this.updateCls
					}
					this.loadModule(script, this.entryName, item, r)
					return
				}
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
			getPatientList : function() {
				this.patientList = this.createModule("patientList",
						this.refDocAdvPatientList);
				this.patientList.on("selectRecord", this.onSelectRecord, this);
				return this.patientList.initPanel();
				//this.patientList.on("loadData", this.onListLoadData, this);
			},
//			onListLoadData : function(store) {
//				// 如果第一次打开页面，默认选中第一行
//				if (this.initDataId) {
//					this.patientList.ZYH = this.initDataId;
//				}
//				if (this.detailsList) {
//					this.detailsList.clear();
//				}
//				if (store.getCount() > 0) {
//					/** 2013-08-20 修改2236bug 注释if (!this.initDataId) {代码**/
////					if (!this.initDataId) {
//						this.patientGrid.fireEvent("rowclick",
//								this.patientGrid, 0);
////					}
//				} else {
//					this.patientList.ZYH = "";
//				}
//			},
			onSelectRecord : function(patientGrid, rowIndex, e) {
				this.detailsList.clearSelect();
				this.detailsList.clear();
				var records=this.patientList.getSelectedRecords();
				var length=records.length;
				if(length==0){
				return;}
				var zyhs=new Array();
				for(var i=0;i<length;i++){
				var r=records[i];
				zyhs.push(r.get("ZYH"));
				}
				this.panel.el.mask("正在查询数据...","x-mask-loading")
				var resData = phis.script.rmi.miniJsonRequestSync({
							serviceId : "doctorAdviceExecuteService",
							serviceAction : "queryFHSFXM",
							zyhs:zyhs
						});
				var code = resData.code;
				var msg = resData.msg;
				var json = resData.json;
				if (code >= 300) {
					this.processReturnMsg(code, msg);
					this.panel.el.unmask();
					return false;
				}
				if (json.body == 1) {
					if (json.count > 0) {
						MyMessageTip.msg("提示", "发现未复核医嘱，请复核!", true);
					}
				}
				this.detailsList.ZYH = 0;
				this.detailsList.ZYHS=zyhs;
				this.detailsList.loadData(0);
			},
			onAfterLoadData:function(){
			this.panel.el.unmask()
			},
			getDetailsList : function() {
				this.detailsList = this.createModule("detailsList",
						this.refDocAdvDetailsList);
				// this.detailsList.on("loadData", this.detailListLoadData,
				// this)
				this.detailsList.on("afterLoadData",this.onAfterLoadData,this)
				this.detailsList.mutiSelect = true;
				return this.detailsList.initPanel();
			},
			// detailListLoadData : function() {
			// var l = this.detailsList.store.getCount();
			// this.getYzzh(this.detailsList.store);
			// if (l == 0) {
			// var r = this.patientList.getSelectedRecord();
			// this.patientList.store.remove(r);
			// this.patientList.grid.getView().refresh();// 刷新行号
			// if (this.patientList.store.getCount() > 0) {
			// this.patientList.selectRow(0);
			// this.onListRowClick(this.patientList, 0);
			// }
			// }
			// },
			// 刷新
			doRefresh : function() {
				this.detailsList.clear();
				this.patientList.clear();
				var al_zyh = "0";// 病人住院号
				if (this.initDataId) {
					al_zyh = this.initDataId;
				}
				//this.panel.el.mask("正在刷新,时间可能很长,请耐心等待...", "x-mask-loading");
				this.patientList.loadData(al_zyh);
				/** 2013-08-20 修改2236bug 注释以下代码,通过RowClick事件onListRowClick方法**/
//				if (al_zyh != 0) {
//					this.detailsList.loadData(al_zyh);
//				}
				/** end**/
				//this.panel.el.unmask();
			},
			// 确认
			doConfirm : function() {
				var records = this.detailsList.getSelectedRecords();
				if (records.length == 0) {
					Ext.Msg.alert("提示", "未选中任何记录");
					return;
				}
				var body = {};
				body['bq'] = this.mainApp['phis'].wardId;
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : "configLogisticsInventoryControlService",
							serviceAction : "verificationWPJFBZ",
							body : body
						});
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg, this.doConfirm);
					return;
				}
				var bodys = [];
				for (var i = 0; i < records.length; i++) {
					var body = {};
					// body["FYRQ"] = records[i].json.QRSJ; // 费用日期
					body["FYMC"] = records[i].json.YZMC; // 费用名称
					body["FYSL"] = records[i].json.YCSL * records[i].json.FYCS;// 费用数量
					body["FYDJ"] = records[i].json.YPDJ;// 费用单价
					body["ZJJE"] = records[i].json.FYCS * records[i].json.YPDJ
							* records[i].json.YCSL;// 总计金额
					body["YSGH"] = records[i].json.YSGH;// 医生工号
					body["SRGH"] = records[i].json.CZGH;// 输入工号
					body["FYKS"] = records[i].json.BRKS;// 费用
					body["JLXH"] = records[i].json.JLXH;// 医嘱序号
					body["ZYH"] = records[i].json.ZYH;// 住院号
					body["FYXH"] = records[i].json.YPXH;// 费用序号
					body["YPCD"] = records[i].json.YPCD;// 药品产地
					body["XMLX"] = records[i].json.XMLX;// 项目类型
					body["YPLX"] = records[i].json.YPLX;// 药品类型
					body["BRXZ"] = records[i].json.BRXZ;// 病人性质
					body["ZXKS"] = records[i].json.ZXKS;// 执行科室
					body["YZXH"] = records[i].json.YZXH;// 医嘱序号
					bodys[i] = body;
				}
				this.panel.el.mask("正在确认数据,时间可能很长,请耐心等待...", "x-mask-loading");
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.serviceActionqr,
							body : bodys
						});
				this.panel.el.unmask();
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.doSave);
					return;
				}
				// 执行完操作 去掉钩钩。
				this.detailsList.clearSelect();
				this.onSelectRecord();
				this.doRefresh();
				if(ret.json.RES_MESSAGE){
					MyMessageTip.msg("提示", ret.json.RES_MESSAGE, true);
				}else{
					MyMessageTip.msg("提示", "确认成功", true);
				}
			},
			// 关闭
			doClose : function() {
				var win = this.getWin();
				if (win)
					win.hide();
			},
			onWinClose : function() {
				this.fireEvent("doSave");
			},
			getYzzh : function(store) {
				yzzh = 1;
				var store = store;
				var n = store.getCount()
				var YZZHs = [];
				for (var i = 0; i < n; i++) {
					if (i == 0) {
						var r = store.getAt(i)
						yzzh = r.get('YZZH') % 2 + 1;
						YZZHs.push(yzzh)
					} else {
						var r1 = store.getAt(i - 1)
						var r = store.getAt(i)
						if (r1.get('YZZH') == r.get('YZZH')) {
							YZZHs.push(yzzh)
						} else {
							YZZHs.push(++yzzh)
						}
					}
				}
				for (var i = 0; i < YZZHs.length; i++) {
					var r = store.getAt(i);
					r.set('YZZH', YZZHs[i]);
				}
			}

		});
