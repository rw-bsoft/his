$package("phis.application.ivc.script")

$import("phis.script.TableForm")

phis.application.ivc.script.ClinicFeeForm = function(cfg) {
	cfg.remoteUrl = 'Disease';
	cfg.remoteTpl = '<td width="12px" style="background-color:#deecfd">{numKey}.</td><td width="100px">{JBMC}</td></td>';
	cfg.ZD1 = "";
	cfg.ZD2 = "";
	cfg.ZD3 = "";
	phis.application.ivc.script.ClinicFeeForm.superclass.constructor.apply(
			this, [cfg])
}
Ext.extend(phis.application.ivc.script.ClinicFeeForm, phis.script.TableForm, {
	onReady : function() {
		var form = this.form.getForm();
		var CFTS = form.findField("CFTS");
		CFTS.hide();
		var YBMC = form.findField("YBMC");
		YBMC.hide();
		var dic_ks;
		var items = this.schema.items;
		for (var i = 0; i < items.length; i++) {
			var it = items[i]
			if (it.id == 'KSDM') {
				dic_ks = it.dic;
			}
		}
		var ksdm = this.form.getForm().findField("KSDM");
		var ysdm = this.form.getForm().findField("YSDM");
		var ybmc = this.form.getForm().findField("YBMC");
		
		ybmc.setDisabled(true);
		ksdm.getStore().on("load", function() {
			if (this.ksdm) {
				ksdm.setValue(this.ksdm);
			}
			if (this.opener.module && this.opener.module.list
					&& ksdm.getValue()) {
				this.opener.module.list.setKSDM(ksdm.getValue());
			}
		}, this);
		ksdm.on("select", function() {
					if (this.opener.module && this.opener.module.list) {
						this.opener.module.list.setKSDM(ksdm.getValue());
					}
				}, this);
		ysdm.on("select", function() {
			var ysId = ysdm.getValue();
			this.opener.module.list.setYSDM(ysId);
			phis.script.rmi.jsonRequest({
						serviceId : "changeDoctorOrDepartmentService",
						serviceAction : "findKsdmByYsdm",
						body : {
							ysId : ysId
						}
					}, function(code, msg, json) {
						var arr1 = json.ksdm;
						this.ksdm = ksdm.getValue();
						ksdm.setValue("");
						ksdm.store.removeAll();
						var filters1 = "['and',['in',['$','item.properties.ID','i'],"
								+ arr1
								+ "],['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]]";
						dic_ks.id = "phis.dictionary.department_leaf";
						dic_ks.filter = filters1;
						ksdm.store.proxy = new util.dictionary.HttpProxy({
									method : "GET",
									url : util.dictionary.SimpleDicFactory
											.getUrl(dic_ks)
								})
						ksdm.store.load();
					}, this)
		}, this);
		phis.application.ivc.script.ClinicFeeForm.superclass.onReady.call(this);
	},
	doJZKHChange : function(f) {
		var store = this.opener.module.list.grid.getStore();
		if (store.getCount() > 0) {
			var r = store.getAt(0);
			if (r.data.YPXH) {
				if (this.opener.MZXX.mxsave) {
					Ext.Msg.confirm("确认", "当前收费明细已发生变化,是否预保存当前信息?", function(
									btn) {
								if (btn == 'yes') {
									this.opener.doSave();
									this.doJZKHChange(f);
								} else {
									this.opener.MZXX.mxsave = false;
									this.doJZKHChange(f);
								}
							}, this);
					return;
				}
			}
		}
		var body = {
			serviceId : "clinicChargesProcessingService",
			serviceAction : "queryPerson",
			logonName : this.mainApp.logonName,
			useBy : 'charges'// 收费处调用
			// add by yangl 疫苗建档用
		}
		if (!f.MZHM) {
			body.JZKH = f.getValue();
		} else {
			body.MZHM = f.MZHM
		}
		var r = phis.script.rmi.miniJsonRequestSync(body);
		if (r.code > 300) {
			this.processReturnMsg(r.code, r.msg, this.onBeforeSave);
			return;
		} else {
			if (!r.json.body) {
				Ext.Msg.alert("提示", "该卡号不存在!", function() {
							f.focus(false, 100);
						});
				return;
			}
			if (this.MZXX) {// 没有取消操作时，需要清空原有锁定
				this.opener.unlock();
			}
			this.MZXX = r.json.body;
//			this.opener.initYBServer(this.MZXX.BRXZ);  //初始化医保接口
			/******add by zhangyq 根据病人挂号科室获取药房信息******/
			if(this.MZXX.GHKS){
				var resData = phis.script.rmi.miniJsonRequestSync({
							serviceId : "publicService",
							serviceAction : "loadSystemParams",
							body : {
								privates : ['YS_MZ_FYYF_'+this.MZXX.GHKS+'_XY', 'YS_MZ_FYYF_'+this.MZXX.GHKS+'_ZY',
										'YS_MZ_FYYF_'+this.MZXX.GHKS+'_CY']
							}
						});
				this.exContext.systemParams = resData.json.body;
				if((this.exContext.systemParams['YS_MZ_FYYF_'+this.MZXX.GHKS+'_XY']+'')=='null'||(this.exContext.systemParams['YS_MZ_FYYF_'+this.MZXX.GHKS+'_ZY']+'')=='null'||(this.exContext.systemParams['YS_MZ_FYYF_'+this.MZXX.GHKS+'_CY']+'')=='null'){
					Ext.Msg.alert("提示", "请先设置挂号科室["+this.MZXX.KSMC+"]的发药药房!");
				}
				this.opener.module.exContext.systemParams.YS_MZ_FYYF_XY = ((this.exContext.systemParams['YS_MZ_FYYF_'+this.MZXX.GHKS+'_XY']+'')=="null"?"0":this.exContext.systemParams['YS_MZ_FYYF_'+this.MZXX.GHKS+'_XY']);
				this.opener.module.exContext.systemParams.YS_MZ_FYYF_ZY = ((this.exContext.systemParams['YS_MZ_FYYF_'+this.MZXX.GHKS+'_ZY']+'')=="null"?"0":this.exContext.systemParams['YS_MZ_FYYF_'+this.MZXX.GHKS+'_ZY']);
				this.opener.module.exContext.systemParams.YS_MZ_FYYF_CY = ((this.exContext.systemParams['YS_MZ_FYYF_'+this.MZXX.GHKS+'_CY']+'')=="null"?"0":this.exContext.systemParams['YS_MZ_FYYF_'+this.MZXX.GHKS+'_CY']);
			}
			/******add by zhangyq 根据病人挂号科室获取药房信息******/
			this.BLLX = r.json.body.BLLX;
			this.opener.clearYbxx();
			if (!r.json.body.GHGL) {
				if (r.json.body.chooseGhks) {
					// add by yangl 增加业务锁
					var p = {};
					p.YWXH = '1003';
					p.BRID = this.MZXX.BRID;
					p.BRXM = this.MZXX.BRXM;
					if (!this.opener.bclLock(p))
						return;
					this.showKsxzWin();
					return;
				}
				// add by yangl 挂号模式没有挂号直接终止调入
				Ext.Msg.alert("提示", "未挂号病人不能收费，请先挂号!");
				return;
			} else {
				// add by yangl 增加业务锁
				var p = {};
				p.YWXH = '1003';
				p.BRID = this.MZXX.BRID;
				p.BRXM = this.MZXX.BRXM;
				if (!this.opener.bclLock(p))
					return;
				var form = this.form.getForm();
				var MZGL = form.findField("MZGL");
				var FPHM = form.findField("FPHM");
				var BRXZ = form.findField("BRXZ");
				var BRXM = form.findField("BRXM");
				if (this.MZXX.BRXZ) {
					BRXZ.setValue(this.MZXX.BRXZ);
				} else {
					BRXZ.setValue(6);
				}
				if (this.MZXX.JZKH) {
					MZGL.setValue(this.MZXX.JZKH);
				} else {
					MZGL.setValue();
				}
				if (this.initDataId) {
					FPHM.setValue(this.initDataId);
				}
				BRXM.setValue(this.MZXX.BRXM);
				var xzks = phis.script.rmi.miniJsonRequestSync({
							serviceId : "clinicChargesProcessingService",
							serviceAction : "checkSFSFXZKS"
						});
				if (xzks.code > 300) {
					this.processReturnMsg(xzks.code, xzks.msg);
					return;
				}
				if (xzks.json.SFSFXZKS == 0) {
					this.doCommit();
				} else {
					if (f.KSDM) {
						this.doCommit(f.KSDM);
					} else {
						this.doOpenKs();
					}
				}
				this.form.getForm().findField("MZGL").disable();
				this.form.getForm().findField("YSDM").enable();
				this.form.getForm().findField("KSDM").enable();
				
			/**************************add by lizhi 以下是查询医保挂号信息**************************/
				var ybmc  = this.form.getForm().findField("YBMC");
				var ksdm = this.form.getForm().findField("KSDM");//挂号科室
				if(this.MZXX.BRXZ==3000){//医保病种
					ybmc.enable();
					ybmc.show();
					var resData = phis.script.rmi.miniJsonRequestSync({
								serviceId : "yBService",
								serviceAction : "queryYbghxx",
								body : this.MZXX
					});
					if (resData.code > 300) {
						this.processReturnMsg(resData.code, resData.msg);
						return;
					}
					if(resData.json.BZM){
						ybmc.setValue(resData.json.BZM);
					}else{
						ybmc.setValue(20);
					}
					if(resData.json.KSM){
						ksdm.setValue(resData.json.KSM);
					}
				}else if(this.MZXX.BRXZ==2000){//南京金保
					if(f.YBMC)
						ybmc.setValue(f.YBMC);
					else{
						ybmc.setValue(20);
					}
					ybmc.enable();
					ybmc.show();
				}
				else{
					ybmc.disable();
					ybmc.hide();
					ybmc.setValue();
				}
			/**************************add by lizhi 查询医保挂号信息结束**************************/
			}

		}

	},

	loadData : function() {
		if (!this.MZXX)
			return;
		var form = this.form.getForm();
		var MZGL = form.findField("MZGL");
		var FPHM = form.findField("FPHM");
		var BRXZ = form.findField("BRXZ");
		var BRXM = form.findField("BRXM");
		if (this.MZXX.BRXZ) {
			BRXZ.setValue(this.MZXX.BRXZ);
		} else {
			BRXZ.setValue(6);
		}
		if (this.MZXX.JZKH) {
			MZGL.setValue(this.MZXX.JZKH);
		} else {
			MZGL.setValue();
		}
		if (this.initDataId) {
			FPHM.setValue(this.initDataId);
		}
		BRXM.setValue(this.MZXX.BRXM);
		this.ZD1 = "";
		this.ZD2 = "";
		this.ZD3 = "";
	},
	showKsxzWin : function() {
		if (!this.reg_form) {
			var GHKS = this.createDicField({
				id : "phis.dictionary.department_reg",
				filter : "['eq',['$','item.properties.JGID']],['$','%user.manageUnit.id']]"
			})
			this.Field = GHKS;
			this.reg_form = new Ext.FormPanel({
						frame : true,
						labelWidth : 75,
						defaults : {
							width : '95%'
						},
						shadow : true,
						items : [{
									fieldLabel : "挂号科室",
									name : "GHKS",
									items : GHKS
								}]
					})
		} else {
			this.Field.setValue();
		}
		if (!this.chiswin) {
			var win = new Ext.Window({
				layout : "form",
				title : '请选择挂号科室',
				width : 330,
				height : 110,
				resizable : true,
				modal : true,
				constrainHeader : true,
				shim : true,
				buttonAlign : 'center',
				closable : false,
				buttons : [{
					text : '确定',
					handler : function() {
						var text = this.Field.getValue();
						// 挂号科室信息
						this.chiswin.hide();
						this.MZXX.GHKS = text;
						var res = phis.script.rmi.miniJsonRequestSync({
									serviceId : "clinicChargesProcessingService",
									serviceAction : "saveGhmx",
									body : this.MZXX
								});
						if (res.code > 300) {
							Ext.Msg.alert("提示", res.msg);
							return;
						}
						this.MZXX.GHGL = res.json.GHGL
						var form = this.form.getForm();
						var MZGL = form.findField("MZGL");
						var FPHM = form.findField("FPHM");
						var BRXZ = form.findField("BRXZ");
						var BRXM = form.findField("BRXM");
						if (this.MZXX.BRXZ) {
							BRXZ.setValue(this.MZXX.BRXZ);
						} else {
							BRXZ.setValue(6);
						}
						if (this.MZXX.JZKH) {
							MZGL.setValue(this.MZXX.JZKH);
						} else {
							MZGL.setValue();
						}
						if (this.initDataId) {
							FPHM.setValue(this.initDataId);
						}
						BRXM.setValue(this.MZXX.BRXM);
						this.doCommit(this.MZXX.GHGL);
						this.form.getForm().findField("MZGL").disable();
						this.form.getForm().findField("YSDM").enable();
						this.form.getForm().findField("KSDM").enable();
					},
					scope : this
				}, {
					text : '取消',
					handler : function() {
						this.chiswin.hide();
					},
					scope : this
				}]
			})
			this.chiswin = win
			this.chiswin.add(this.reg_form);
		}
		this.chiswin.show();
		this.Field.focus(false, 50);
	},
	doENTER : function(field, isDk, ybkxx) {
		this.opener.clearYbxx();
		// 判断启用模式 1卡号,2门诊号码
		var pdms = phis.script.rmi.miniJsonRequestSync({
					serviceId : "clinicChargesProcessingService",
					serviceAction : "checkCardOrMZHM"
				});
		if (pdms.code > 300) {
			this.processReturnMsg(pdms.code, r.msg, this.onBeforeSave);
			return;
		} else {
			if (!pdms.json.cardOrMZHM) {
				Ext.Msg.alert("提示", "该卡号门诊号码判断不存在!");
				return;
			}
		}
		if (field.getValue() == '') {
			this.opener.doNewPerson();
		} else {
			// 去查询处方单
			this.doJZKHChange(field);
			if (isDk) {
				this.opener.ybkxx = ybkxx;
			}
		}
	},
	showModule : function() {
		var m = this.midiModules["healthRecordModule"];
		if (!m) {
			$import("phis.application.pix.script.EMPIInfoModule");
			m = new phis.application.pix.script.EMPIInfoModule({
						entryName : "phis.application.pix.schemas.MPI_DemographicInfo",
						title : "个人基本信息查询",
						height : 450,
						modal : true,
						mainApp : this.mainApp
					});
			m.on("onEmpiReturn", this.opener.newPerson, this);
			this.midiModules["healthRecordModule"] = m;
		}
		var win = m.getWin();
		win.show();
	},
	// 展示单据前先查询科室
	doOpenKs : function() {
		var r = phis.script.rmi.miniJsonRequestSync({
					serviceId : "clinicChargesProcessingService",
					serviceAction : "queryKs",
					body : this.MZXX
				});
		if (r.code > 300) {
			this.processReturnMsg(r.code, r.msg, this.onBeforeSave);
			return;
		}
		if (r.json.num == 0) {
			this.doCommit();
		} else {
			if (r.json.num != -1) {
				this.doCommit(r.json.num);
				return;
			}
			var module = this.createModule("ksxzList", this.refList)
			module.on("commit", this.onKScommit, this);
			module.on("cancel", this.onKSCancel, this);
			var win = module.getWin();
			win.add(module.initPanel())
			win.center();
			win.show();
			module.requestData.serviceId = "phis.clinicChargesProcessingService";
			module.requestData.serviceAction = "queryGHKS";
			module.requestData.body = this.MZXX;
			module.loadData();
		}
	},
	onKScommit : function(jzxh) {
		this.doCommit(jzxh)
	},
	onKSCancel : function() {
		this.opener.MZXX = this.MZXX;
		this.fireEvent("qx", this)
	},
	doCommit : function(jzxh) {
		var from = this.form.getForm();
		var BRXM = from.findField("BRXM");
		var MZGL = from.findField("MZGL");
		var BRXZ = from.findField("BRXZ");
		var parentFrom = this.opener.formModule.form.getForm();
		var KSDM = from.findField("KSDM");
		parentFrom.findField("MZGL").setValue(MZGL.getValue());
		parentFrom.findField("BRXM").setValue(BRXM.getValue());
		parentFrom.findField("BRXZ").setValue(BRXZ.getValue());
		if (jzxh) {
			this.MZXX["KSJZXH"]=jzxh;
		}
		this.getWin().hide();
		var r = phis.script.rmi.miniJsonRequestSync({
					serviceId : "clinicChargesProcessingService",
					serviceAction : "queryDJ",
					body : this.MZXX
				});
		if (r.code > 300) {
			this.processReturnMsg(r.code, r.msg, this.onBeforeSave);
			return;
		} else {
			this.MZXX.ZDXH = r.json.ZDXH
			parentFrom.findField("JBMC").setValue(r.json.JBMC);
			this.MZXX.ICD10=r.json.ICD10
			if (r.json.count > 0) {
				this.opener.cfsb = [];
				this.opener.module.list.clear();
				this.opener.module.list.totalCount();
				this.opener.showCFD(this.MZXX);
			} else {
				this.opener.cfsb = [];
				this.opener.MZXX = this.MZXX;
				this.opener.setCFD([]);
			}
		}
	},
	getRemoteDicReader : function() {
		return new Ext.data.JsonReader({
					root : 'disease',
					totalProperty : 'count',
					id : 'mdssearch_a'
				}, [{
							name : 'numKey'
						}, {
							name : 'JBXH'
						}, {
							name : 'JBMC'

						}, {
							name : 'ICD10'

						}]);
	},
	setBackInfo : function(obj, record) {
		if (!this.MZXX) {
			MyMessageTip.msg("提示", "请先载入病人信息", true);
			return;
		}
		obj.collapse();
		obj.setValue(record.get("JBMC"));
		this.MZXX["ZDXH"] = record.get("JBXH");
		this.MZXX["ICD10"]= record.get("ICD10");
		if(this.opener && this.opener.MZXX ){
			this.opener.MZXX.ZDXH = record.get("JBXH");
			this.opener.MZXX.ICD10 = record.get("ICD10");
		}
	},
	getWin : function() {
		var win = this.win
		if (!win) {
			win = new Ext.Window({
						id : this.id,
						title : "审批编号录入",
						width : 800,
						height : 300,
						iconCls : 'icon-grid',
						shim : true,
						layout : "fit",
						animCollapse : true,
						closeAction : "hide",
						constrainHeader : true,
						constrain : true,
						minimizable : true,
						maximizable : true,
						shadow : false,
						modal : this.modal || false
					})
			var renderToEl = this.getRenderToEl()
			if (renderToEl) {
				win.render(renderToEl)
			}
			win.on("show", function() {
						this.fireEvent("winShow")
					}, this)
			win.on("add", function() {
						this.win.doLayout()
					}, this)
			win.on("beforeclose", function() {
						return this.fireEvent("beforeclose", this);
					}, this);
			win.on("beforehide", function() {
						return this.fireEvent("beforeclose", this);
					}, this);
			win.on("close", function() {
						this.fireEvent("close", this)
					}, this)
			win.on("hide", function() { //add by yzh 2010-06-24
						this.fireEvent("hide", this)
					}, this)
			this.win = win
		}
		return win;
	},
	setData : function(data) {
		this.setCFLX(data.CFLX);
		var form = this.form.getForm();
		var YSDM = form.findField("YSDM");
		var KSDM = form.findField("KSDM");
		var CFTS = form.findField("CFTS");
		if (data.YSDM) {
			if (data.YSDM != YSDM.getValue()) {
				YSDM.setValue(data.YSDM);
			}
		} else {
			YSDM.setValue();
		}
		if (data.KSDM) {
			KSDM.setValue(data.KSDM);
		} else {
			KSDM.setValue();
		}
		CFTS.setValue(data.CFTS);
		if ((data.DJLY + "") == "6") {
			YSDM.enable();
			KSDM.enable();
			CFTS.enable();
		} else {
			YSDM.disable();
			KSDM.disable();
			CFTS.disable();
		}
	},
	setCFLX : function(type) {
		var form = this.form.getForm();
		var CFTS = form.findField("CFTS");
		CFTS.hide();
		if (type == 3) {
			CFTS.show();
		} else {
			CFTS.hide();
		}
	}
})