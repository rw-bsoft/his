$package("phis.application.war.script")

$import("phis.script.SimpleList")

phis.application.war.script.ConsultationManageList = function(cfg) {

	this.printurl = util.helper.Helper.getUrl();
	phis.application.war.script.ConsultationManageList.superclass.constructor.apply(
			this, [cfg])
	this.selectFirst = false;
}

Ext.extend(phis.application.war.script.ConsultationManageList,
		phis.script.SimpleList, {
			/*
			 * init : function() { this.addEvents({ "gridInit" : true,
			 * "beforeLoadData" : true, "loadData" : true, "loadSchema" : true })
			 * this.requestData = { serviceId : this.serviceId, serviceAction :
			 * this.serviceAction, schema : this.entryName, ksType : 1, dicValue :
			 * this.dicValue, body : { zyh : this.exContext.empiData.ZYH, hzbr :
			 * this.exContext.empiData.BRXM }, pageSize : this.pageSize > 0 ?
			 * this.pageSize : 0, pageNo : 1 } if (this.serverParams) {
			 * Ext.apply(this.requestData, this.serverParams) } if
			 * (this.autoLoadSchema) { this.getSchema(); } }
			 */// 刚打开页面时候默认选中数据,这时候判断下分配和转床状态
			onStoreLoadData : function(store, records, ops) {

				var val = this.exContext.alias;

				if (val == 2) {
					// 会诊病人
					this.setButtonsState(['refresh'], false);
					this.setButtonsState(['create'], false);
					this.setButtonsState(['update'], false);
					this.setButtonsState(['submit'], false);
					this.setButtonsState(['close2'], false);
					this.setButtonsState(['thbq'], false);
					this.setButtonsState(['remove'], false);
					this.setButtonsState(['print'], false);
					this.setButtonsState(['close'], false);
					this.setButtonsState(['openXML'], true);
				}
			},
			setButtonsState : function(m, enable) {
				var btns;
				var btn;
				if (this.showButtonOnTop) {
					btns = this.grid.getTopToolbar();
				} else {
					btns = this.grid.buttons;
				}

				if (!btns) {
					return;
				}
				if (this.showButtonOnTop) {
					for (var j = 0; j < m.length; j++) {
						if (!isNaN(m[j])) {
							btn = btns.items.item(m[j]);
						} else {
							btn = btns.find("cmd", m[j]);
							btn = btn[0];
						}
						if (btn) {
							(enable) ? btn.enable() : btn.disable();
						}
					}
				} else {
					for (var j = 0; j < m.length; j++) {
						if (!isNaN(m[j])) {
							btn = btns[m[j]];
						} else {
							for (var i = 0; i < this.actions.length; i++) {
								if (this.actions[i].id == m[j]) {
									btn = btns[i];
								}
							}
						}
						if (btn) {
							(enable) ? btn.enable() : btn.disable();
						}
					}
				}
			},
			loadData : function() {

				this.requestData.serviceId = "phis."+this.sqService;
				this.requestData.serviceAction = this.serviceAction;
				this.requestData.body = {
					zyh : this.exContext.empiData.ZYH,
					hzbr : this.exContext.empiData.BRXM,
					alias : this.exContext.alias
				};
				phis.application.war.script.ConsultationManageList.superclass.loadData
						.call(this);
			},
			openModule : function(cmd, r, xy) {
				var module = this.midiModules["PatientManage"]
				module.cmd = cmd;
				if (module) {
					var win = module.getWin()
					win.setTitle(module.title)
					win.show()
					var xy = win.getPosition();
					win.setPagePosition(xy[0], 50);
					if (!win.hidden) {
						if (cmd == "sure" || cmd == "update"
								|| cmd == "cancel2" || cmd == "home") {
							module.on('update', this.loadData, this);
							module.loadData()
							module.initButton();
						} else if (cmd == "create") {
							module.doNew();
							module.initFormDoNew();
						}
					}
				}
			},
			// 提交
			doSubmit : function() {
				var r = this.getSelectedRecord();
				if (!r) {
					MyMessageTip.msg("提示", "请先选择需要提交的会诊申请!", true);
					return;
				}
				if (r.data.TJBZ == 1) {
					MyMessageTip.msg("提示", "当前选中的会诊申请已经提交!", true);
					return;
				}
				if (r.data.JSBZ == 1) {
					MyMessageTip.msg("提示", "当前选中的会诊申请已结束,不允许提交!", true);
					return;
				}
				Ext.Msg.confirm("提示", "确认提交此会诊申请吗？", function(btn, text) {
					if (btn == "yes") {
						phis.script.rmi.jsonRequest({
									serviceId : this.sqService,
									serviceAction : "sub",
									body : {
										SQXH : r.data.SQXH
									}
								}, function(code, msg, json) {
									// this.panel.el.unmask()
									if (code < 300) {
										MyMessageTip.msg("提示", "提交申请成功!", true);
										this.loadData();
									} else {
										this.processReturnMsg(code, msg,
												this.doRemove)
									}
								}, this)
					}
				}, this);

			},
			// 退回
			doThbq : function() {
				var r = this.getSelectedRecord();
				if (!r) {
					MyMessageTip.msg("提示", "请先选择需要退回的会诊申请!", true);
					return;
				}
				if (r.data.TJBZ == 0) {
					MyMessageTip.msg("提示", "当前选中的会诊申请未提交,不能退回!", true);
					return;
				}
				if (r.data.JSBZ == 1) {
					MyMessageTip.msg("提示", "当前选中的会诊申请已结束,不允许退回!", true);
					return;
				}

				var scope = this;
				phis.script.rmi.jsonRequest({
							serviceId : "wardConsultationYService",
							serviceAction : "query",
							body : {
								"SQXH" : r.data.SQXH
							}
						}, function(code, msg, json) {
							var data = json.recode;
							if (data != null && data.HZYJ != null
									&& data.HZYJ.length != 0) {
								MyMessageTip.msg("提示",
										"当前选中的会诊申请已填写会诊意见,不允许退回!", true);
							} else {
								Ext.Msg.confirm("提示", "确认退回此会诊申请吗？", function(
										btn, text) {
									if (btn == "yes") {
										phis.script.rmi.jsonRequest({
													serviceId : scope.sqService,
													serviceAction : "back",
													body : {
														SQXH : r.data.SQXH
													}
												}, function(code, msg, json) {
													// this.panel.el.unmask()
													if (code < 300) {
														MyMessageTip.msg("提示",
																"退回成功!", true);
														scope.loadData();
													} else {
														scope.processReturnMsg(
																code, msg,
																scope.doRemove)
													}
												}, scope)
									}
								}, scope)
							}
						})
			},
			// 刷新
			doRefresh : function() {
				this.refresh();

			},
			// 删除
			doRemove : function() {
				var r = this.getSelectedRecord();
				if (!r) {
					MyMessageTip.msg("提示", "请先选择需要删除的会诊申请!", true);
					return;
				}
				var tjbz = r.data.TJBZ;
				if (tjbz == 1) {
					MyMessageTip.msg("提示", "已经提交的会诊申请 不能删除!", true);
					return;
				}

				Ext.Msg.confirm("提示", "确认删除此会诊申请吗？", function(btn, text) {
							if (btn == "yes") {
								phis.script.rmi.jsonRequest({
											serviceId : this.sqService,
											serviceAction : "remove",
											body : {
												SQXH : r.data.SQXH
											}
										}, function(code, msg, json) {
											// this.panel.el.unmask()
											if (code < 300) {
												MyMessageTip.msg("提示", "删除成功!",
														true);
												this.loadData();
											} else {
												this.processReturnMsg(code,
														msg, this.doRemove)
											}
										}, this)
							}
						}, this)

			},

			// 打印
			doPrint : function() {
				if (!this.getSelectedRecord()) {
					MyMessageTip.msg("提示", "请先选中需要打印的会诊记录!", true);
					return;
				}

				var r = this.getSelectedRecord().data;
				var BRXM = encodeURIComponent(encodeURIComponent(this.exContext.empiData.BRXM));// 病人姓名0
				var BRXB = encodeURIComponent(encodeURIComponent(this.exContext.empiData.BRXB_text));// 病人性别1
				var HZSJ = r.HZSJ;// 会诊时间 2
				var SQXH = r.SQXH;// 申请单号 3
				var BRCH = this.exContext.empiData.BRCH;// 病人床号 4
				var ZYHM = this.exContext.empiData.ZYHM;// 住院号码 5
				var ZDMC = encodeURIComponent(encodeURIComponent(this.exContext.empiData.JBMC));// 当前诊断
				// 6
				var SQKS = encodeURIComponent(encodeURIComponent(r.SQKS_text));// 申请科室 7
				var SQYS = encodeURIComponent(encodeURIComponent(r.SQYS_text));// 申请医师 8
				var HZMD = encodeURIComponent(encodeURIComponent(r.HZMD));// 会诊目的 9
				var BQZL = encodeURIComponent(encodeURIComponent(r.BQZL));// 患者治疗情况 10
				var SQSJ = r.SQSJ;// 申请时间 11
				var BRYL = this.exContext.empiData.AGE;// 病人年龄 12
				var YQDX = encodeURIComponent(encodeURIComponent(r.YQDX_text));// 会诊者姓名13
				var JJBZ = r.JJBZ;// 紧急标识 0: 一般, 1: 24小时, 2: 紧急 14

				var record = [];
				record.push(BRXM);
				record.push(BRXB);
				record.push(HZSJ);
				record.push(SQXH);
				record.push(BRCH);
				record.push(ZYHM);
				record.push(ZDMC);
				record.push(SQKS);
				record.push(SQYS);
				record.push(HZMD);
				record.push(BQZL);
				record.push(BRYL);
				record.push(SQSJ);
				record.push(YQDX);
				record.push(JJBZ);
				var pages="phis.prints.jrxml.ConsultationManageList";
				 var url="resources/"+pages+".print?record="+ record;
			/*	window
						.open(
								url,
								"",
								"height="
										+ (screen.height - 100)
										+ ", width="
										+ (screen.width - 10)
										+ ", top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no")
				*/
				 var LODOP=getLodop();
					LODOP.PRINT_INIT("打印控件");
					LODOP.SET_PRINT_PAGESIZE("0","","","");
					//预览LODOP.PREVIEW();
					//预览LODOP.PRINT();
					LODOP.ADD_PRINT_HTM("0","0","100%","100%",util.rmi.loadXML({url:url,httpMethod:"get"}));
					LODOP.SET_PRINT_MODE ("PRINT_PAGE_PERCENT","Full-Width");
					LODOP.PREVIEW();// 预览
			},
			// // 关闭
			// doClose : function() {
			// this.getWin().hide();
			//
			// },
			// 结束会诊
			doClose2 : function() {

				var r = this.getSelectedRecord();
				if (!r) {
					MyMessageTip.msg("提示", "请先选择需要结束的会诊申请!", true);
					return;
				}
				Ext.Msg.confirm("提示", "确认结束此会诊申请吗？", function(btn, text) {
					if (btn == "yes") {
						phis.script.rmi.jsonRequest({
									serviceId : this.sqService,
									serviceAction : "stop",
									body : {
										SQXH : r.data.SQXH
									}
								}, function(code, msg, json) {
									// this.panel.el.unmask()
									if (code < 300) {
										MyMessageTip.msg("提示", "结束会诊成功!", true);
										this.loadData();
									} else {
										this.processReturnMsg(code, msg,
												this.doRemove)
									}
								}, this)
					}
				}, this);
			},
			loadModule : function(cls, entryName, item, r) {

				cls = "phis.application.war.script.ConsultationApplyForm_New";
				if (this.loading) {
					return
				}
				var cmd = item.cmd
				var cfg = {}
				cfg._mId = this.grid._mId // 增加module的id
				cfg.title = this.title + '-' + item.text
				cfg.entryName = entryName
				cfg.op = cmd
				cfg.exContext = {}
				Ext.apply(cfg.exContext, this.exContext)

				if (cmd != 'create') {
					if (this.isCompositeKey) {
						var pkeys = this.schema.pkeys;
						var initDataBody = {};
						for (var i = 0; i < pkeys.length; i++) {
							initDataBody[pkeys[i]] = r.get(pkeys[i])
						}
						cfg.initDataBody = initDataBody;
					} else {

						cfg.initDataId = r.data.SQXH;
					}
					cfg.exContext[entryName] = r;
				}
				if (this.saveServiceId) {
					cfg.saveServiceId = this.saveServiceId;
				}
				var m = this.midiModules["PatientManage"]
				if (!m) {
					this.loading = true
					$require(cls, [function() {
										this.loading = false
										cfg.autoLoadData = false;
										var module = eval("new " + cls
												+ "(cfg)")
										module.on("save", this.onSave, this)
										module.on("close", this.active, this)
										module.opener = this
										module.setMainApp(this.mainApp)
										this.midiModules["PatientManage"] = module
										this.fireEvent("loadModule", module)
										this.openModule(cmd, r)
									}, this])
				} else {
					Ext.apply(m, cfg)
					this.openModule(cmd, r)
				}
			},
			/*
			 * doClose:function(){ // var module=this.emrview; //
			 * module.getWin().hide(); var module =
			 * this.emrview.midiModules['C04'];
			 * 
			 * module.getWin().hide(); // this.getWin().hide(); },
			 */
			doOpenXML : function() {
				var r = this.getSelectedRecord();

				if (r == null || r == "")
					return;
				var module = this.createModule("consultationIdeaForm",
						"phis.application.war.WAR/WAR/WAR1305");

				module.exContext = this.exContext;

				module.exContext.data = r.data;

				var win = module.getWin();
				win.add(module.initPanel());
				module.doNew();
				module.initFormDoNew();
				win.show();

			}

		});