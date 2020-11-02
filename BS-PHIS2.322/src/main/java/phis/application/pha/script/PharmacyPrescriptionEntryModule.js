/**
 * 门诊收费处理
 * 
 * @author caijy
 */
$package("phis.application.pha.script");

$import("phis.script.SimpleModule");

phis.application.pha.script.PharmacyPrescriptionEntryModule = function(cfg) {
	this.noDefaultBtnKey = true;
	phis.application.pha.script.PharmacyPrescriptionEntryModule.superclass.constructor
			.apply(this, [cfg]);
	/**
	 * 监听快捷键 shortcutKeyFunc common.js有默认实现类
	 * 如有特殊需求要重写，需要重新定义监听的方法名称，否则会被common中的默认方法覆盖
	 */
	this.on("shortcutKey", this.shortcutKeyFunc, this);
}

Ext.extend(phis.application.pha.script.PharmacyPrescriptionEntryModule,
		phis.script.SimpleModule, {
			initPanel : function() {
				if (this.panel) {
					return this.panel;
				}
				if (!this.mainApp['phis'].pharmacyId) {
					MyMessageTip.msg("错误", "请先设置当前登录药房!", true);
					return;
				}
				// 判断是否设置发药药房
				var body = {
					"privates" : ['YYJGTS', 'YSZJS', 'XSFJJJ', 'HQFYYF',
							'QYJYBZ', 'QYTJBGBZ']
				}
				if (!this.exContext) {
					this.exContext = {};
				}

				this.exContext.systemParams = this.loadSystemParams(body);
				this.exContext.systemParams.YS_MZ_FYYF_XY = this.mainApp['phis'].pharmacyId;
				this.exContext.systemParams.YS_MZ_FYYF_ZY = this.mainApp['phis'].pharmacyId;
				this.exContext.systemParams.YS_MZ_FYYF_CY = this.mainApp['phis'].pharmacyId;

				var panel = new Ext.Panel({
							border : false,
							width : this.width,
							height : this.height,
							// frame : true,
							layout : 'border',
							defaults : {
								border : false
							},
							buttonAlign : 'center',
							items : [{
										layout : "fit",
										border : false,
										region : 'north',
										height : 95,
										items : this.getForm()
									}, {
										layout : "fit",
										border : false,
										region : 'center',
										items : this.getList()
									}]
						});
				this.panel = panel;
				return panel;
			},
			getForm : function() {
				var form = this.createModule("refForm", this.refForm);
				this.form = form;
				this.form.opener = this;
				var m = this.form.initPanel();
				var MZHM = m.form.findField('JZKH');
				MZHM.on("change", this.onMzhmChange, this)
				var cfts = m.form.findField("CFTS");
				if (cfts) {
					cfts.un("specialkey", form.onFieldSpecialkey, form);
					cfts.on("specialkey", function(cfts, e) {
								var key = e.getKey();
								if (key == e.ENTER) {
									var n = this.list.store.getCount();
									this.list.grid.startEditing(n - 1, 3);
								}
							}, this);
					cfts.on("change", function() {
								this.list.setCFTS(cfts.getValue());
							}, this);
				}
				var ysdm = m.form.findField("YSDM");
				if (ysdm) {
					ysdm.un("specialkey", form.onFieldSpecialkey, form);
					ysdm.on("specialkey", function(ysdm, e) {
								var key = e.getKey();
								if (key == e.ENTER) {
									if (ysdm.getValue()) {
										var ksdm = m.form.findField("KSDM");
										ksdm.focus();
									}
								}
							}, this);
				}
				var ksdm = m.form.findField("KSDM");
				if (ksdm) {
					ksdm.un("specialkey", form.onFieldSpecialkey, form);
					ksdm.on("specialkey", function(ksdm, e) {
								var key = e.getKey();
								if (key == e.ENTER) {
									if (ksdm.getValue()) {
										if (this.list.type == 3) {
											var cfts = m.form.findField("CFTS");
											cfts.focus(true);
										} else {
											var n = this.list.store.getCount();
											if (n > 0) {
												this.list.grid.startEditing(n
																- 1, 3);
											}else{
												this.list.doXY();
											}
										}
									}
								}
							}, this);
				}
				m.form.findField('CFTS').hide();
				return m;
			},
			onMzhmChange : function(f, v) {
				if (!v) {
					MyMessageTip.msg("提示", "请输入有效的卡号!", true);
					return;
				}
				this.doLoadMzxx(v);
			},
			afterOpen : function() {
				this.form.form.getForm().findField("JZKH").focus(true);
			},
			doLoadMzxx : function(v) {
				// 根据门诊号码获取处方信息
				this.panel.el.mask("正在载入数据...", "x-mask-loading")
				phis.script.rmi.jsonRequest({
							serviceId : "clinicManageService",
							serviceAction : "loadCfxx",
							body : {
								JZKH : v
							}
						}, function(code, msg, json) {
							this.panel.el.unmask()
							if (code > 300) {
								this.processReturnMsg(code, msg);
								return
							}
							// 缓存CF01返回结果
							this.Brxx = json.BRXX;
							this.list.MZXX = this.Brxx;
							this.form.initFormData(this.Brxx)
							this.form.form.getForm().findField("JZKH")
									.setDisabled(true);
							// this.cacheCF01List = json.CF01List;
							// 刷新list
							this.list.requestData.cnd = ['and',[
									'and',
									[
											'and',
											['eq', ['$', 'c.BRID'],
													['l', this.Brxx.BRID]],
											['eq', ['$', 'c.JGID'],
													['s', this.mainApp['phisApp'].deptId]]],
									[
											'and',
											[
													'and',
													['eq', ['$', 'c.DJLY'],
															['i', 2]],
													['eq', ['$', 'c.ZFPB'],
															['i', 0]]],
											['isNull', ['$', 'c.FPHM']]]],
											['eq', ['$', 'c.YFSB'],
												['i', this.mainApp['phis'].pharmacyId]]];
							this.list.loadData();
						}, this)// jsonRequest
			},
			getList : function() {
				this.list = this.midiModules['refList'];
				if (!this.list) {
					var module = this.createModule("refList", this.refList);
					module.exContext = this.exContext;
					module.opener = this;
					var list = module.initPanel();
					// 默认隐藏每帖数量
					// list.getColumnModel().setHidden(
					// list.getColumnModel().getIndexById('YPZS'), true);
					this.list = module;
				}
				return list;
			},
			F1 : function() {
				this.form.doReset()
			},
			F2 : function() {
				this.form.doClose();
			},
			alt_1 : function() {
				this.list.doXY();
			},
			alt_2 : function() {
				this.list.doZY();
			},
			alt_3 : function() {
				this.list.doCY();
			},
			alt_C : function() {
				this.list.doInsert();
			},
			alt_G : function() {
				this.list.doNewGroup();
			},
			alt_R : function() {
				this.list.doRemove();
			},
			alt_D : function() {
				this.list.doDelGroup();
			},
			alt_Z : function() {
				this.list.doSave();
			}
		});