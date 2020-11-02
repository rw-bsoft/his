$package("phis.application.reg.script");

$import("phis.script.TableForm", "phis.script.widgets.MyMessageTip",
		"app.desktop.Module");

phis.application.reg.script.RegistrationDeptForm = function(cfg) {
	this.showButtonOnTop = false;
	cfg.colCount = 3;
	phis.application.reg.script.RegistrationDeptForm.superclass.constructor.apply(this,
			[cfg]);
	this.on("winShow", this.winShow);
	this.showButtonOnTop = true;
}
Ext.extend(phis.application.reg.script.RegistrationDeptForm,
		phis.script.TableForm, {
			initPanel : function() { 
				this.getParameter();
				var panel = phis.application.reg.script.RegistrationDeptForm.superclass.initPanel
						.call(this);
				// 添加转入科室选择事件
				this.form.getForm().findField("ZRKS").on("select",
						this.onSelect, this);
				return panel;
			},
			/**
			 * 获取配置的参数信息(DQGHRQ和DQZBLB其中DQGHRQ与杨力确认为获取当前周几)
			 */
			getParameter : function() {
				var result = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.serviceAction
						});
				if (result.code == 200) {
					this.dqghrq = result.json.dqghrq;
					this.dqzblb = result.json.dqzblb;
//					this.ghkssfpb = result.json.ghkssfpb
				} else {
					this.processReturnMsg(result.code, result.msg);
				}
			},
			createDicField : function(dic) {
				if (dic.id == "phis.dictionary.department_td") {
//					if (this.ghkssfpb == '1') {
						dic.filter = "['and',"
								+ "['eq',['$','item.properties.JGID'],['s','"
								+ this.mainApp['phisApp'].deptId + "']],"
								+ "['eq',['$','item.properties.GHRQ'],['i',"
								+ parseInt(this.dqghrq) + "]],"
								+ "['eq',['$','item.properties.ZBLB'],['i',"
								+ parseInt(this.dqzblb) + "]]]";
//					} else {
//						dic.id = "department_reg";
//						dic.filter = "['eq',['$map',['s','JGID']],['s','"
//								+ this.mainApp['phisApp'].deptId + "']]";
//					}
				}
				var cls = "util.dictionary.";
				if (!dic.render) {
					cls += "Simple";
				} else {
					cls += dic.render;
				}
				cls += "DicFactory";

				$import(cls);
				var factory = eval("(" + cls + ")");
				var field = factory.createDic(dic);
				return field;
			},
			onSelect : function(f, v) {
				if (!this.ksdm) {
					MyMessageTip.msg("提示", "未选择已挂科室,请先输入卡号查询!", true);
					this.form.getForm().findField("ZRKS").setValue('');
					return;
				}
				var result = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.checkAction,
							body : v.data.key
						});
				if (result.code == 200) {
					var zlf = result.json.zlf;
					var ghf = result.json.ghf;
					if ((parseFloat(zlf) + (ghf)) != (parseFloat(this.zlf) + parseFloat(this.ghf))) {
						MyMessageTip.msg("提示", "科室费用不一致不能转科!", true);
						this.form.getForm().findField("ZRKS").setValue('');
						return
					}
					this.zrdm = v.data.key;
				} else {
					this.processReturnMsg(result.code, result.msg);
				}
			},
			doTurnDeptQuery : function() {
				if (!this.text.getValue()) {
					MyMessageTip.msg("提示", "请输入卡号!", true);
					return;
				}
				if (this.tdwin) {
					var module = this.midiModules["refRegisteredDeptGHList"];
					module.refreshData();
				} else {
					// this.form.getForm().findField("YSDM").getStore().load();
					var module = this.createModule("refRegisteredDeptGHList",
							this.refRegisteredDeptGHList);
					module.opener = this;
					this.tdwin = module.getWin();
					this.tdwin.setTitle(module.name);
					this.tdwin.setWidth(600);
					this.tdwin.add(module.initPanel());
				}
//				this.tdwin.show();
			},
			doCommit : function() {
				var body = {};

				if (!this.ksdm) {
					MyMessageTip.msg("提示", "未选择已挂科室,请先输入卡号查询!", true);
					return;
				}
				if (!this.zrdm) {
					MyMessageTip.msg("提示", "未选择转入科室!", true);
					return;
				}
				if (this.ksdm == this.zrdm) {
					MyMessageTip.msg("提示", "转入科室与原科室相同!", true);
					return;
				}
				body.XGKS = this.ksdm;
				body.KSDM = this.zrdm;
				body.SBXH = this.sbxh;
				body.GHSJ = this.form.getForm().findField("GHSJ").getValue();
				body.ZBLB = this.opener.opener.ZBLB;
				body.GHRQ=this.dqghrq;
				body.ZBLB=this.dqzblb;
				var result = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.cmmmitAction,
							body : body
						});
				if (result.code == 200) {
					MyMessageTip.msg("提示", "转科成功!", true);
					this.opener.opener.KSList.SBXH = result.json.SBXH;
					this.fireEvent("settlement", this);
					this.clearData();
					this.win.hide();
				} else {
					this.processReturnMsg(result.code, result.msg);
				}
			},
			expansion : function(cfg) {// 扩展
				this.text = new Ext.form.TextField({
							xtype : 'textfield',
							id : "carNo",
							fieldLabel : "姓名"
						});
				this.text.on("specialkey", function(text, e) {
							var key = e.getKey();
							if (key == e.ENTER) {
								this.doTurnDeptQuery();
							}
						}, this);
				cfg.tbar=[];
				cfg.tbar.push('卡号:');
				cfg.tbar.push(this.text);
				var btn = {};
				btn.id = 'turnDeptQuery';
				btn.text = '查询', btn.iconCls = 'query';
				// 添加点击事件,调用doTurnDeptQuery方法
				btn.handler = this.doTurnDeptQuery;
				btn.name = 'turnDeptQuery';
				btn.scope = this;
				cfg.tbar.push(btn);
				var btn2 = {};
				btn2.id = 'turnDeptConfim';
				btn2.text = '确认', btn2.iconCls = 'commit';
				// 添加点击事件,调用doCommit方法
				btn2.handler = this.doCommit;
				btn2.name = 'turnDeptConfim';
				btn2.scope = this;
				cfg.tbar.push(btn2);
			},
			getInputValue : function() {
				return this.text.getValue();
			},
			/**
			 * 往表单中添加值
			 * 
			 * @param data
			 */
			setGHDJ : function(data) {
				var tmp = this.text.getValue();
				this.clearData();
				if (!data) {
					MyMessageTip.msg("提示", "未找到挂号记录，可能已经退号!", true);
					return;
				}
				if (data.JZZT != 0) {
					MyMessageTip.msg("提示", "该挂号记录已经就诊不能转科!", true);
					return;
				}
				if (data.ZJFY) {
					if (data.ZJFY > 0) {
						MyMessageTip.msg("提示", "转前挂号是专家号，不能进行转科处理!", true);
						return;
					}
				}
				this.text.setValue(tmp);
				var form = this.form.getForm();
				form.findField("JZHM").setValue(data.JZHM);
				form.findField("BRXM").setValue(data.BRXM);
				form.findField("XGKS").setValue(data.GHKS);
				form.findField("GHSJ").setValue(data.GHSJ);
				form.findField("JZZT").setValue(data.JZZT_TEXT);
				form.findField("ZRKS").setValue('');
				if (data.YGXM) {
					form.findField("YGXM").setValue(data.YGXM);
				}

				this.ksdm = data.KSDM;
				this.sbxh = data.SBXH;
				this.brid = data.BRID;
				this.zlf = data.ZLF;
				this.ghf = data.GHF;
				this.zjfy = data.ZJFY;
			},
			/**
			 * 清空表单中的数据与定义在缓存中的各个变量
			 */
			clearData : function() {
				this.opener.doNew();
				this.text.setValue('');
				var form = this.form.getForm();
				form.findField("JZHM").setValue('');
				form.findField("BRXM").setValue('');
				form.findField("XGKS").setValue('');
				form.findField("GHSJ").setValue('');
				form.findField("JZZT").setValue('');
				form.findField("ZRKS").setValue('');
				form.findField("YGXM").setValue('');
				this.zrdm = '';
				this.ksdm = '';
				this.sbxh = '';
				this.brid = '';
				this.zlf = '';
				this.ghf = '';
				this.zjfy = '';
				this.text.focus(false,250);
			},
			winShow : function() {
				if (this.form) {
					var filter, id;
//					if (this.ghkssfpb == '1') {
						id = "phis.dictionary.department_td";
						filter = "['and',"
								+ "['eq',['$','item.properties.JGID'],['s','"
								+ this.mainApp['phisApp'].deptId + "']],"
								+ "['eq',['$','item.properties.GHRQ'],['i',"
								+ parseInt(this.dqghrq) + "]],"
								+ "['eq',['$','item.properties.ZBLB'],['i',"
								+ parseInt(this.dqzblb) + "]]]";
//					} else {
//						id = "department_reg";
//						filter = "['eq',['$map',['s','JGID']],['s','"
//								+ this.mainApp['phisApp'].deptId + "']]"
//					}
					var field = this.form.getForm().findField("ZRKS");
					field.store.removeAll();
					field.store.proxy = new Ext.data.HttpProxy({
								method : "GET",
								url : util.dictionary.SimpleDicFactory.getUrl({
											id : id,
											filter : filter
										})
							});
				}
			}
		});