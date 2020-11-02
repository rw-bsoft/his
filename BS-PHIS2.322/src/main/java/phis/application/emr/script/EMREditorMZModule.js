$package("phis.application.emr.script")

$import("phis.script.SimpleModule")
phis.application.emr.script.EMREditorMZModule = function(cfg) {
	// this.mKey = "test";
	this.cacheMbXsmc = {};
	this.cachePermission = {};
	phis.application.emr.script.EMREditorMZModule.superclass.constructor.apply(
			this, [cfg]);
}
var emr_this = emr_this || {}
Ext.extend(phis.application.emr.script.EMREditorMZModule,
		phis.script.SimpleModule, {
			initPanel : function() {
				this.BLBH = this.node.BLBH;
				this.bl01 = {};
				this.bl02 = {};
				this.blOpenTime = null;// 记录病程打开时间
				if (this.panel)
					return this.panel;
				var url = ClassLoader.appRootOffsetPath
						+ 'resources/phis/resources/phisUrlProxy/'
						+ (Ext.isIE ? 'emrocx_ie.html' : 'emrocx_ff.html');
				var panel = new Ext.Panel({
					// id : this.mKey,
					frame : true,
					border : false,
					html : '<div id="emrOcxContainer_'
							+ this.mKey
							+ '"><IFRAME id='
							+ this.mKey
							+ ' name="emrOcxFrame" width="100%" height="700" src="'
							+ url + '?mKey=' + this.mKey
							+ '" frameborder=0  scrolling=no  ></IFRAME></div>',
					// autoScroll : true,
					tbar : new Ext.Toolbar({
								enableOverflow : true,
								items : [this.createButtons()]
							})
				});
				this.panel = panel;
				panel.on("afterrender", this.onReady, this);
				panel.on("beforeclose", this.panelClose, this);
				panel.on("bodyresize", this.afterbodyResize, this)
				// panel.on("destroy", this.closeActive, this);
				emr_this[this.mKey] = this;
				return panel;
			},
			afterbodyResize : function() {
				this.emr.height = this.panel.getHeight() - 50;
				this.emr.width = this.panel.getWidth() - 20;
			},
			panelClose : function() {
				if (!Ext.isIE) {
					var lodop = getLodop();
					if (lodop) {
						lodop.PRINT_INIT();
					}
				}
				return this.beforeClose();
			},
			showWin : function() {
				var module = this.createModule("ttt", "WAR030102");
				module.on("hide", this.showEmr, this);
				var win = module.getWin();
				win.add(module.initPanel());
				win.setHeight(400);
				this.hideEmr();
				win.show();
			},
			onReady : function() {
				// if (this.node.ReadOnly) {
				// var items = this.panel.getTopToolbar().items;
				// for (var i = 0; i < items.length; i++) {
				// if (i != 12) {
				// items.item(i).hide();
				// }
				// }
				// }
				var _ctx = this;
				var iframe = Ext.isIE ? document.frames[this.mKey] : document
						.getElementById(this.mKey);
				if (iframe.attachEvent) {
					iframe.attachEvent("onload", function() {
								_ctx.initEmrActiveX(iframe);
							});
				} else {
					iframe.onload = function() {
						_ctx.initEmrActiveX(iframe);
					};
				}
				this.getHasClinicRecord();
			},
			getHasClinicRecord : function() {
				this.hasClinicRecord = false;
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : "clinicManageService",
							serviceAction : "getHasClinicRecord",
							body : {
								"brid" : this.exContext.ids.brid,
								"clinicId" : this.exContext.ids.clinicId,
								"empiId" : this.exContext.ids["empiId"]
							}
						});
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg);
					return false;
				} else {
					this.hasClinicRecord = r.json.hasClinicRecord
				}
			},
			beforeEnd : function(str) {
				if (!this.emr)
					return true;
				if (this.noNeedSave) {
					this.noNeedSave = false;
					return true;
				}
				// if (this.saving) {
				// MyMessageTip.msg("提示", "正在保存数据，不允许关闭病历/病程!", true);
				// return false;
				// }
				// if (this.emrLoading) {
				// MyMessageTip.msg("提示", "正在加载数据，不允许关闭病历/病程!", true);
				// return false;
				// }
				if (!this.BLBH)
					return true;
				if (!this.backup) {// 删除备份
					this.emr.FunActiveXInterface('BsEditAutoData', '1',
							this.BLBH + '#' + this.mainApp.uid)
				}
				if (this.emr.FunActiveXInterface("BsCheckWordDataChange", '',
						'')
						|| this.modifySign) {
					Ext.Msg.show({
								title : '提示信息',
								msg : '当前病历/病程数据发生变化，是否保存？',
								modal : true,
								width : 300,
								animEl : 'emrOcxContainer_' + this.mKey,
								buttons : Ext.MessageBox.YESNO,
								multiline : false,
								fn : function(btn, text) {
									this.showEmr();
									this.closeTitle = true;
									if (btn == 'yes') {
										if (this.doSave()) {
											if (str == "ZG") {
												this.opener.opener
														.doClinicFinish(2);
											} else if (str == "JS") {
												this.opener.opener
														.openReferralAppointment();
											}
										} else {
											this.closeTitle = false;
											return;
										}
									} else {
										this.noNeedSave = true;
										if (str == "ZG") {
											this.opener.opener
													.doClinicFinish(2);
										} else if (str == "JS") {
											this.opener.opener
													.openReferralAppointment();
										}
									}
									// this.unlockEmr();
									// if (this.runner) {
									// this.runner.stopAll();
									// }
								},
								scope : this
							})
					return false;
				}
				// if (this.runner) {
				// this.runner.stopAll();
				// }
				// this.unlockEmr();
				return true;
			},
			beforeClose : function(closeHdr) {
				if (!this.emr)
					return true;
				if (!this.BLBH)
					return true;
				if (!this.backup) {// 删除备份
					var _this = this;
					var deferFunction = function() {
						_this.emr.FunActiveXInterface('BsEditAutoData', '1',
								this.BLBH + '#' + this.mainApp.uid)
					}
					deferFunction.defer(1000);
				}
				if (this.runner) {
					this.runner.stopAll();
				}
				this.unlockEmr();
				var _this = this;
				var deferFunction = function() {
					_this.emr.FunActiveXInterface("BsCloseDocument", '', '');
				}
				deferFunction.defer(1000);
				return true;
			},
			loadModuleCfg : function(id) {
				var result = phis.script.rmi.miniJsonRequestSync({
							url : 'app/loadModule',
							id : id
						})
				if (result.code != 200) {
					if (result.msg == "NotLogon") {
						this.mainApp.logon(this.loadModuleCfg, this, [id])
					}
					return null;
				}
				var m = result.json.body
				Ext.apply(m, m.properties)
				return m
			},
			initEmrActiveX : function(iframe) {
				// alert(this.panel.getHeight())
				this.emr = Ext.isIE
						? iframe.document.getElementById("emrOcx")
						: iframe.contentWindow.document
								.getElementById("emrOcx");
				try {
					if (!this.emr
							|| this.emr.FunActiveXInterface("BsGetFileVersion",
									'', '')) {
						this.hideEmr();
						Ext.Msg.show({
									title : '提示',
									msg : "检查到您尚未安装电子病历插件,是否需要下载安装程序？",
									modal : true,
									width : 300,
									buttons : {
										yes : '代理插件',
										no : '电子病历插件',
										cancel : '取消'
									},
									multiline : false,
									fn : function(btn, text) {
										if (btn == "yes") {
											this.doDownLoadTwo();
										} else if (btn == "no") {
											this.doDownLoad();
										}
										if (this.closable) {
											this.opener.closeCurrentTab();
										} else {
											this.emr.FunActiveXInterface(
													"BsEmptyDoc", '', '');
										}
									},
									scope : this
								})
						// Ext.Msg.confirm("提示", "检查到您尚未安装电子病历插件,是否需要下载安装程序？",
						// function(btn) {
						// this.showEmr();
						// if (btn == 'yes') {
						// this.doDownLoad();
						// }
						// this.opener.closeCurrentTab();
						// }, this)
						// this.alert("提示", "检查到您尚未安装电子病历插件，请安装后重新尝试!")
						return;
					}
				} catch (e) {
					this.hideEmr();
					Ext.Msg.show({
								title : '提示',
								msg : "检查到您尚未安装电子病历插件,是否需要下载安装程序？",
								modal : true,
								width : 300,
								buttons : {
									yes : '代理插件',
									no : '电子病历插件',
									cancel : '取消'
								},
								multiline : false,
								fn : function(btn, text) {
									if (btn == "yes") {
										this.doDownLoadTwo();
									} else if (btn == "no") {
										this.doDownLoad();
									}
									if (this.closable) {
										this.opener.closeCurrentTab();
									} else {
										this.emr.FunActiveXInterface(
												"BsEmptyDoc", '', '');
									}
								},
								scope : this
							})
					// this.alert("提示", "检查到您尚未安装电子病历插件，请安装后重新尝试!")
					return;
				}
				this.panel.focus();
				// 检查版本
				this.emr.FunActiveXInterface("BsGetFileVersion", '', '');
				var version = this.emr.StrReturnData;
				if (!version
						|| version != this.exContext.systemParams.EMRVERSION) {
					this.hideEmr();
					Ext.Msg.confirm("提示", "检查到新的电子病历插件,是否升级插件？<br />当前版本号:"
									+ version + ",最新版本号:"
									+ this.exContext.systemParams.EMRVERSION,
							function(btn) {
								this.showEmr();
								if (btn == 'yes') {
									this.doDownLoad();
								}
								if (this.closable) {
									this.opener.closeCurrentTab();
								} else {
									this.emr.FunActiveXInterface("BsEmptyDoc",
											'', '');
								}
							}, this)
					// this.alert("提示", "检查到您尚未安装电子病历插件，请安装后重新尝试!")
					return;
				}
				var _ctx = this;
				if (Ext.isIE) {
					this.emr.attachEvent("OnClick", function() {
								_ctx.emrCallBackFunc();
							});
				}else {
					this.emr.FunActiveXInterface("BsSetUserData",'1','')
				}
				this.doNewEMR();
				this.emr.height = this.panel.getHeight() - 50;
				this.emr.width = this.panel.getWidth() - 20;
				this.doImportEMR();
				if (this.exContext.systemParams.BCJG > 0) {
					this.start = false;
					var task = {
						run : function() {
							_ctx.autoSave();
						},
						interval : 1000 * 60
								* parseInt(this.exContext.systemParams.BCJG)
					}
					this.runner = new Ext.util.TaskRunner();
					this.runner.start(task);
				}
				// if (!this.msgCt) {
				// this.msgCt = Ext.DomHelper.insertFirst(this.panel.body, {
				// id : 'charge-div',
				// style : 'position:absolute;top:0px;right:5px;margin:0
				// auto;z-index:20001;'
				// }, true);
				// }
				// this.m = Ext.DomHelper.append(this.msgCt, {
				// html : "<img src='images/charge.png' />"
				// }, true);
			},
			/**
			 * 锁定病历编辑器
			 * 
			 * @param {}
			 *            blbh
			 */
			lockEmr : function() {
				if (isNaN(this.BLBH))
					return
				var resData = phis.script.rmi.miniJsonRequestSync({
							serviceId : "emrManageService",
							serviceAction : "saveEmrLockManage",
							body : {
								"BLBH" : this.BLBH,
								"type" : "lock"
							}
						});
				if (resData.code > 200) {
					if (resData.msg == "NotLogon") {
						this.hideEmr()
						this.doNotLogon(this.showEmr)
						if (this.lockRunner) {
							this.lockRunner.stopAll();
						}
						return false;
					}
					if (resData.json.unsign) {
						this.signByLock = false;
					} else {
						this.signByLock = true;
					}
					MyMessageTip.msg("提示", resData.msg, true);
					this.hasLock = false;
					return false;
				}
				if (!this.lockRunner) {
					var _ctx = this;
					var task = {
						run : function() {
							_ctx.lockEmr(this.BLBH);
						},
						interval : 1000 * 60 * 5
					}
					this.lockRunner = new Ext.util.TaskRunner();
					this.lockRunner.start(task);
				}
				this.signByLock = true;
				this.hasLock = true;
				return true;
			},
			/**
			 * 解锁
			 * 
			 * @param {}
			 *            blbh
			 */
			unlockEmr : function() {
				if (isNaN(this.BLBH))
					return
				var resData = phis.script.rmi.miniJsonRequestSync({
							serviceId : "emrManageService",
							serviceAction : "saveEmrLockManage",
							body : {
								"BLBH" : this.BLBH,
								"type" : "unlock"
							}
						});
				if (resData.code > 200) {
					if (resData.msg == "NotLogon") {
						this.hideEmr()
						this.doNotLogon(this.showEmr)
						if (this.lockRunner) {
							this.lockRunner.stopAll();
						}
						return false;
					}
					MyMessageTip.msg("提示", resData.msg, true);
					return false;
				}
				if (this.lockRunner) {
					this.lockRunner.stopAll();
					this.lockRunner = null;
				}
				this.signByLock = true;
				this.hasLock = false;
				return true;
			},
			doAutoSave : function() {
				this.autoSave();
			},
			autoSave : function() {
				if (!this.start) {
					this.start = true;
					return;
				}
				if (!this.editing)
					return;
				if (this.emr.FunActiveXInterface("BsCheckWordDataChange", '',
						'')
						|| this.modifySign) {
					if (this.getEmrBlxx()) {
						this.emr.FunActiveXInterface("BsEditAutoData", '1',
								this.BLBH + '#' + this.mainApp.uid)
						this.emr
								.FunActiveXInterface("BsAutoSaveData",
										this.node.BLLX + '#' + this.BLBH + "#"
												+ this.mainApp.uid,
										this.bl01.MBBH + "")
						this.bl01.BFSJ = new Date().format('Y-m-d H:i:s');
						this.bl01.LBMC = this.node.LBMC;
						this.bl01.Node = this.node;
						this.emr.FunActiveXInterface("BsAutoSaveData", '9#'
										+ this.BLBH + "#" + this.mainApp.uid,
								Ext.encode(this.bl01))
						this.modifySign = 1;
					}
				}
//				MyMessageTip.msg("提示", "本地备份成功!", true);
			},
			emrCallBackFunc : function() {
				var retObj = eval("(" + this.emr.StrReturnData + ")");
				var retType = retObj.FunName;
				var valueA = retObj.ValueA;
				var valueB = retObj.ValueB;
				switch (retType) {
					case "EditRecord" :
						this.doBeforeEditor();
						break;
					case "SetCurParaCation" :
						this.doChangeParaCation(valueA);
						break;
					case "EditTextElement" :
						this.doEditTextElement(valueA, valueB);
						break;
					case "printselect" :
					case "AddPrintRecord" :
						this.doPrint(retType, valueA, valueB);
						break;
					case "referenywsj" :
						this.opener.showEmrUserDataBox(this);
						break;
					case "UsefulLanguage" :
						this.opener.showUserFul(this);
						break;
					case "Insertyxbds" :
						this.doInsertYxbds();
						break;
					case "SaveRecordMedicalPic" :
						this.doSaveYxbds(valueA, valueB);
						break;
					case "EditMedicalSymbol" :
						this.doEitorYxbds(valueB);
						break;
					case "EditLookupElement" :
						this.showDicInfo(valueA, valueB, this);
						break;
					case "signsuper" :
						this.doSigned();
						break;
					case "SaveAsCommonUse" :
						this.doAddTemp();
						break;
					case "SaveAsPersonalTemp" :
						this.doSaveAsPersonal();
						break;
				}

			},
			showDicInfo : function(paraA, paraB, ctx) {
				this.paraA = paraA;
				this.paraB = paraB;
				var entryName;
				if (paraB) {
					entryName = paraB.toString().split("|")[0];
				}
				if (!entryName) {
					return;
				}
				if (entryName == "MS_BRZD") {
					this.openCardLayoutWin(1);
				} else if (entryName == "MS_CF01") {
					this.openCardLayoutWin(2);
				} else if (entryName == "MS_YJ01") {
					this.openCardLayoutWin(3);
				} else if (entryName == "HER_HEALTHRECIPERECORD_MZ") {
					this.openCardLayoutWin(4);
				} else {
					this.opener.showDicInfo(valueA, valueB, ctx);
				}
			},
			onImportRecipe : function(JKCFRecords) {
				if (this.opener.opener) {
					this.opener.opener.JKCFRecords = {};
				}
				// this.opener.opener.isModify = true;
				var jkcfStr = '';
				var num = 1;
				if (JKCFRecords != null && JKCFRecords.length > 0) {
					for (var i = 0; i < JKCFRecords.length; i++) {
						var r = JKCFRecords[i];
						var diagnoseId = parseInt(r.diagnoseId);
						if (this.opener.opener) {
							this.opener.opener.JKCFRecords[diagnoseId] = r;
						}
						jkcfStr += num + '.' + r.healthTeach;
						num++;
						if (i + 1 < JKCFRecords.length) {
							jkcfStr += '\n';
						}
					}
					var red = this.emr.FunActiveXInterface('BsDocSearchElem',
							'element..健康处方', "门诊健康处方#健康处方录入");
					if (red == 1) {
						this.setElementValue(jkcfStr, this.emr.StrReturnData);
					}
				} else {
					if (this.opener.opener) {
						this.opener.opener.JKCFRecords = {};
					}
					var red = this.emr.FunActiveXInterface('BsDocSearchElem',
							'element..健康处方', "门诊健康处方#健康处方录入");
					if (red == 1) {
						this
								.setElementValue("{健康处方录入}",
										this.emr.StrReturnData);
					}
				}
			},
			openCardLayoutWin : function(num) {
				if (num == 4) {
					var module = this.createModule("refRecipeImportModule",
							this.refRecipeImportModule);
					module.exContext = this.exContext;
					module.on("importRecipe", this.onImportRecipe, this);
					module.fromId = "record";
					this.recipeImportModule = module;
					module.ZZDICD10 = this.ZZDICD10;
					module.ZZDMC = this.ZZDMC;
					if (this.opener.opener) {
						module.JKCFRecords = this.opener.opener.JKCFRecords;
					}
					var win = module.getWin();
					win.setHeight(500);
					win.setWidth(1000);
					win.show();
					return;
				}
				/**
				 * modified by gaof 2013-9-23 修改需求：根据系统参数QYCFCZQZTJ
				 * 未录入诊断，不允许录入处方处置
				 */
				if (num == 2 || num == 3) {
					var body = {};
					body["JZXH"] = this.exContext.ids.clinicId;
					var r = phis.script.rmi.miniJsonRequestSync({
								serviceId : "clinicManageService",
								serviceAction : "queryIsAllowed",
								body : body
							});
					if (r.code > 300) {
						this.processReturnMsg(r.code, r.msg);
						return false;
					} else {
						if (r.json.isAllowed == 0) {
							Ext.MessageBox.alert("提示", "请先录入诊断");
							return;
						}
					}
				}

				var firstOpen = false;
				var goText, backText, title;
				if (num == 1) {
					goText = "处方录入";
					backText = "处置录入"
					title = "诊断录入"
				} else if (num == 2) {
					goText = "处置录入";
					backText = "诊断录入"
					title = "处方录入"
				} else if (num == 3) {
					goText = "处方录入";
					backText = "诊断录入"
					title = "处置录入"
				}
				title = title + '-' + '【' + this.mainApp.departmentName + '】';
				this.panel.el.mask("载入中...");
				if (!this.cardWin) {
					this.cardWin = new Ext.Window({
								title : title,
								width : 1024,
								layout : 'card',
								activeItem : num - 1,
								iconCls : 'icon-grid',
								shim : true,
								animCollapse : true,
								constrain : true,
								resizable : false,
								closeAction : "hide",
								constrainHeader : true,
								minimizable : false,
								maximizable : true,
								shadow : false,
								modal : true,
								defaults : {
									border : false
								},
								bbar : [{
											id : 'move-prev',
											text : backText,
											handler : this.backTab,
											iconCls : "backward",
											scale : "small",
											scope : this
										}, '->', {
											id : 'move-next',
											text : goText,
											scope : this,
											iconCls : "forward",
											scale : "small",
											handler : this.goTab
										}],
								items : [this.openDiagnosisWin(),
										this.openPrescriptionWin(),
										this.openDisposalWin()],
								scope : this
							})
					this.cardWin.on("show", function() {
								this.fireEvent("winShow");
							}, this);
					this.cardWin.on("beforeshow", function() {
								this.fireEvent("beforeWinShow");
							}, this);
					this.cardWin.on("close", function() {
								this.fireEvent("close", this);
							}, this);
					this.cardWin.on("beforeclose", function() {
								return this.fireEvent("beforeclose", this);
							}, this);
					this.cardWin.on("beforehide", function() {
								return this.beforeCardWinClose();
							}, this);
					this.cardWin.on("hide", function() {
								this.fireEvent("close", this);
							}, this);
					firstOpen = true;
					this.midiWins['cardWin'] = this.cardWin;
				} else {
					this.cardWin.setTitle(title);
					this.cardWin.getLayout().setActiveItem(num - 1);
					var prev = Ext.getCmp('move-prev');
					var next = Ext.getCmp('move-next');
					prev.setText(backText);
					next.setText(goText);
				}
				this.cardWin.show();
				if (firstOpen) {
					// this.cardWin.setWidth(1024);
					// this.cardWin.setHeight(600);
					// this.cardWin.center();
					this.cardWin.maximize();
				}
				id = this.cardWin.getLayout().activeItem.id;
				this.midiModules[id].onWinShow();
				this.panel.el.unmask();
				this.lock(id, this.midiModules[id]);
			},
			lock : function(id, m) {
				if (id == 'clinicPrescriptionEntry') {
					var p = {};
					p.YWXH = '1001';
					p.BRID = this.exContext.ids.brid;
					m.bclLock(p, m.complexPanel.el)
				} else if (id == 'clinicopenDisposalEntry') {
					var p = {};
					p.YWXH = '1002';
					p.BRID = this.exContext.ids.brid;
					m.bclLock(p, m.panel.el)
				}
			},
			unlock : function(id, m) {
				if (id == 'clinicPrescriptionEntry') {
					var p = {};
					p.YWXH = '1001';
					p.BRID = this.exContext.ids.brid;
					m.bclUnlock(p, m.complexPanel.el);
				} else if (id == 'clinicopenDisposalEntry') {
					var p = {};
					p.YWXH = '1002';
					p.BRID = this.exContext.ids.brid;
					m.bclUnlock(p, m.panel.el);
				}
			},
			goTab : function() {
				/**
				 * modified by gaof 2013-9-23 修改需求：根据系统参数QYCFCZQZTJ
				 * 未录入诊断，不允许录入处方处置
				 */
				var id = this.cardWin.getLayout().activeItem.id;
				// alert(id + ":" + this.currModuleId)
				// if (id == this.currModuleId)// 如果是当前，则不切换
				// return;
				if (id == "clinicDiagnosisEntry") {
					var body = {};
					body["JZXH"] = this.exContext.ids.clinicId;
					var r = phis.script.rmi.miniJsonRequestSync({
								serviceId : "clinicManageService",
								serviceAction : "queryIsAllowed",
								body : body
							});
					if (r.code > 300) {
						this.processReturnMsg(r.code, r.msg);
						return false;
					} else {
						if (r.json.isAllowed == 0) {
							var store = this.midiModules["clinicDiagnosisEntry"].list.grid
									.getStore();
							if (store.getCount() > 0) {
								var r = store.getAt(0);
								if (r.get("ICD10") == null
										|| r.get("ICD10") == ""
										|| r.get("ICD10") == 0) {
									Ext.MessageBox.alert("提示", "请先录入诊断");
									return;
								} else {
									Ext.MessageBox.alert("提示", "请先保存诊断");
									return;
								}
							} else {
								Ext.MessageBox.alert("提示", "请先录入诊断");
								return;
							}
						}
					}
				}

				if (!this.midiModules[id].beforeclose()) {
					return;
				}

				var prev = Ext.getCmp('move-prev');
				var next = Ext.getCmp('move-next');
				if (id == 'clinicDiagnosisEntry') {
					prev.setText("诊断录入");
					next.setText("处置录入");
					this.cardWin.setTitle("处方录入" + '-' + '【'
							+ this.mainApp.departmentName + '】');
					this.cardWin.getLayout().setActiveItem(1);
				}
				if (id == 'clinicPrescriptionEntry') {
					prev.setText("诊断录入");
					next.setText("处方录入");
					this.cardWin.setTitle("处置录入" + '-' + '【'
							+ this.mainApp.departmentName + '】');
					this.cardWin.getLayout().setActiveItem(2);
				}
				if (id == 'clinicopenDisposalEntry') {
					prev.setText("诊断录入");
					next.setText("处置录入");
					this.cardWin.setTitle("处方录入" + '-' + '【'
							+ this.mainApp.departmentName + '】');
					this.cardWin.getLayout().setActiveItem(1);
				}
				this.unlock(id, this.midiModules[id]);
				id = this.cardWin.getLayout().activeItem.id;
				this.currModuleId = id;
				this.midiModules[id].onWinShow();
				this.cardWin.instance = this.midiModules[id];
				// BCL lock
				this.lock(id, this.midiModules[id]);
			},
			backTab : function() {
				/**
				 * modified by gaof 2013-9-23 修改需求：根据系统参数QYCFCZQZTJ
				 * 未录入诊断，不允许录入处方处置
				 */
				var id = this.cardWin.getLayout().activeItem.id;
				if (id == "clinicDiagnosisEntry") {
					var body = {};
					body["JZXH"] = this.exContext.ids.clinicId;
					var r = phis.script.rmi.miniJsonRequestSync({
								serviceId : "clinicManageService",
								serviceAction : "queryIsAllowed",
								body : body
							});
					if (r.code > 300) {
						this.processReturnMsg(r.code, r.msg);
						return false;
					} else {
						if (r.json.isAllowed == 0) {
							var store = this.midiModules["clinicDiagnosisEntry"].list.grid
									.getStore();
							if (store.getCount() > 0) {
								var r = store.getAt(0);
								if (r.get("ICD10") == null
										|| r.get("ICD10") == ""
										|| r.get("ICD10") == 0) {
									Ext.MessageBox.alert("提示", "请先录入诊断");
									return;
								} else {
									Ext.MessageBox.alert("提示", "请先保存诊断");
									return;
								}
							} else {
								Ext.MessageBox.alert("提示", "请先录入诊断");
								return;
							}
						}
					}
				}

				if (!this.midiModules[id].beforeclose()) {
					return;
				}
				var prev = Ext.getCmp('move-prev');
				var next = Ext.getCmp('move-next');
				if (id == 'clinicDiagnosisEntry') {
					prev.setText("诊断录入");
					next.setText("处方录入");
					this.cardWin.setTitle("处置录入");
					this.cardWin.getLayout().setActiveItem(2);
				}
				if (id == 'clinicPrescriptionEntry') {
					prev.setText("处置录入");
					next.setText("处方录入");
					this.cardWin.setTitle("诊断录入");
					this.cardWin.getLayout().setActiveItem(0);
				}
				if (id == 'clinicopenDisposalEntry') {
					prev.setText("处置录入");
					next.setText("处方录入");
					this.cardWin.setTitle("诊断录入");
					this.cardWin.getLayout().setActiveItem(0);
				}
				this.lock(id);
				id = this.cardWin.getLayout().activeItem.id;
				this.midiModules[id].onWinShow();
			},
			openDiagnosisWin : function() {
				// if (!this.exContext || !this.exContext.ids
				// || !this.exContext.ids["empiId"]
				// || !this.exContext.ids["clinicId"]) {
				// Ext.Msg.alert("错误", "无法获取就诊信息！请检查数据是否正确.");
				// return;
				// }
				// this.panel.getEl().mask("业务模块载入中...");
				var module = this.createModule("clinicDiagnosisEntry",
						this.refDiagnosisEntryModule);
				module.opener = this;
				module.on("close", this.onWinClose, this);
				module.on("beforeclose", this.onBeforeWinClose, module);
				module.on("doSave", this.onClinicSave, this);
				module.exContext = this.exContext;
				module.on("doRemove", this.onClinicRemove, this);
				var p = module.initPanel();
				p.id = 'clinicDiagnosisEntry';
				return p;
				// if (!this.clinicDiagnosisEntry_win) {

				// var win = module.getWin();
				// this.clinicDiagnosisEntry_win = win;
				// }
				// this.clinicDiagnosisEntry_win.show();
				// this.panel.getEl().unmask();
			},
			openPrescriptionWin : function() {
				// 处方编辑
				if (!this.exContext || !this.exContext.ids
						|| !this.exContext.ids["empiId"]
						|| !this.exContext.ids["clinicId"]) {
					Ext.Msg.alert("错误", "无法获取就诊信息！请检查数据是否正确.");
					return;
				}
				var module = this.createModule("clinicPrescriptionEntry",
						this.refPrescriptionEntryModule, {
							id : "card-1"
						});
				module.opener = this;
				module.exContext = this.exContext;
				if (module.getClinicInitParams() == -1) {
					return;
				}
				if (this.exContext.systemParams.YS_MZ_FYYF_XY == -1
						|| this.exContext.systemParams.YS_MZ_FYYF_ZY == -1
						|| this.exContext.systemParams.YS_MZ_FYYF_CY == -1
						|| this.exContext.systemParams.YS_MZ_FYYF_XY == ""
						|| this.exContext.systemParams.YS_MZ_FYYF_ZY == ""
						|| this.exContext.systemParams.YS_MZ_FYYF_CY == "") {
					MyMessageTip.msg("错误", "请先设置门诊发药药房!", true);
					this.panel.getEl().unmask();
					return;
				}
				module.on("close", this.onWinClose, this);
				module.on("doSave", this.onClinicSave, this);
				var p = module.initPanel();
				p.id = 'clinicPrescriptionEntry';
				return p;
				// this.on("beforeclose", this.onBeforeWinClose, this);
				// if (!this.clinicPrescriptionEntry_win) {
				// var win = module.getWin();
				// win.setTitle('处方录入-' + '【' +
				// this.mainApp.departmentName
				// + '】');
				// this.clinicPrescriptionEntry_win = win;
				// }
				// this.clinicPrescriptionEntry_win.show();
				// this.clinicPrescriptionEntry_win.maximize();
				// this.panel.getEl().unmask();
			},
			openDisposalWin : function() {
				// 处置编辑
				if (!this.exContext || !this.exContext.ids
						|| !this.exContext.ids["empiId"]
						|| !this.exContext.ids["clinicId"]) {
					Ext.Msg.alert("错误", "无法获取就诊信息！请检查数据是否正确.");
					return;
				}
				// this.panel.getEl().mask("业务模块载入中...");
				var module = this.createModule("clinicopenDisposalEntry",
						this.refDisposalEntryModule);
				module.opener = this;
				module.exContext = this.exContext;
				module.on("close", this.onWinClose, this);
				module.on("doSave", this.onClinicSave, this);
				// if (!this.clinicopenDisposalEntry_win) {
				// var win = module.getWin();
				// this.clinicopenDisposalEntry_win = win;
				// }
				// this.clinicopenDisposalEntry_win.show();
				// this.panel.getEl().unmask();
				var p = module.initPanel();
				p.id = 'clinicopenDisposalEntry';
				return p;
			},
			Backfill : function(comFrom) {
				if (comFrom == "MS_BRZD") {
					var r = this.emr.FunActiveXInterface('BsDocSearchElem',
							'element..门急诊诊断', "门诊诊断#诊断录入");
					if (r == 1) {
						var resData = phis.script.rmi.miniJsonRequestSync({
									serviceId : "clinicManageService",
									serviceAction : "loadClinicInfo",
									body : {
										"brid" : this.exContext.ids.brid,
										"clinicId" : this.exContext.ids.clinicId,
										"type" : '2'
									}
								});
						if (resData.code >= 300) {
							this.processReturnMsg(resData.code, resData.msg);
						}
						var brzds = resData.json.ms_brzd;
						var num = 1;
						var zdStr = "";
						for (var i = 0; i < brzds.length; i++) {
							var row = brzds[i];
							if (i == 0) {
								zdStr = row.DEEP == 0 ? num + '.' : ' ';
								zdStr += row.ZDMC;
								if(row.CFLX==2){
									zdStr += '  '+row.ZHMC;
								}
							} else {
								zdStr += "\n";
								zdStr += row.DEEP == 0 ? num + '.' : ' ';
								for (var j = 0; j < parseInt(row.DEEP); j++) {
									zdStr += "   ";
								}
								zdStr += row.ZDMC;
								if(row.CFLX==2){
									zdStr += ' '+row.ZHMC;
								}
							}
							if (row.DEEP == 0)
								num++;
						}
						if (zdStr.length == 0) {
							zdStr = "{诊断录入}";
						}
						this.brzds = brzds;
						this.setElementValue(zdStr, this.emr.StrReturnData);
						// this.opener.opener.refreshEMRNavTree();
					}
				} else if (comFrom == "MS_CF01") {
					var r = this.emr.FunActiveXInterface('BsDocSearchElem',
							'element..门急诊用药', "门诊用药#处方录入");
					if (r == 1) {
						var resData = phis.script.rmi.miniJsonRequestSync({
									serviceId : "clinicManageService",
									serviceAction : "loadClinicInfo",
									body : {
										"brid" : this.exContext.ids.brid,
										"clinicId" : this.exContext.ids.clinicId,
										"type" : '3'
									}
								});
						if (resData.code >= 300) {
							this.processReturnMsg(resData.code, resData.msg);
						}
						var measures = resData.json.measures;
						// var disposal = resData.json.disposal;
						var cfStr = '';
						var num = 1;
						// var isHerbs = false;
						if (measures != null && measures.length > 0) {
							for (var i = 0; i < measures.length; i++) {
								var r = measures[i];
								if (num == 1) {
									cfStr += num + '.';
									num++;
								} else {
									if (r.CFSB != measures[i - 1].CFSB) {
										cfStr += num + '.';
										num++;
									} else {
										cfStr += '  ';
									}
								}
								cfStr += r.YPMC + "(" + r.YCJL
										+ (r.JLDW ? r.JLDW : '') + ") ";
								cfStr += r.YPSL + (r.YFDW ? r.YFDW : '');
								if (r.YFGG) {
									cfStr += '/' + r.YFGG + ' ';
								}
								cfStr += r.YPYF_text + ' ' + r.GYTJ_text;
								if (i + 1 < measures.length) {
									cfStr += '\n';
								}
							}
						}
						if (cfStr.length == 0) {
							cfStr = "{处方录入}";
						}
						this.setElementValue(cfStr, this.emr.StrReturnData);
						// this.opener.opener.refreshEMRNavTree();
					}
				} else if (comFrom == "MS_YJ01") {
					var r = this.emr.FunActiveXInterface('BsDocSearchElem',
							'element..处理措施', "门诊处置#处置录入");
					if (r == 1) {
						var resData = phis.script.rmi.miniJsonRequestSync({
									serviceId : "clinicManageService",
									serviceAction : "loadClinicInfo",
									body : {
										"brid" : this.exContext.ids.brid,
										"clinicId" : this.exContext.ids.clinicId,
										"type" : '3'
									}
								});
						if (resData.code >= 300) {
							this.processReturnMsg(resData.code, resData.msg);
						}
						// var measures = resData.json.measures;
						var disposal = resData.json.disposal;
						var czStr = '';
						var num = 1;
						if (disposal != null && disposal.length > 0) {
							for (var i = 0; i < disposal.length; i++) {
								var r = disposal[i];
								czStr += num + '.' + r.FYMC + ' ' + r.YLSL
										+ r.FYDW;
								num++;
								if (i + 1 < disposal.length) {
									czStr += '\n';
								}
							}
						}
						if (czStr.length == 0) {
							czStr = "{处置录入}";
						}
						this.setElementValue(czStr, this.emr.StrReturnData);
						// this.opener.opener.refreshEMRNavTree();
					}
				} else if (comFrom == "HER_HEALTHRECIPERECORD_MZ") {
					var red = this.emr.FunActiveXInterface('BsDocSearchElem',
							'element..健康处方', "门诊健康处方#健康处方录入");
					if (red == 1) {
						var resData = phis.script.rmi.miniJsonRequestSync({
									serviceId : "clinicManageService",
									serviceAction : "loadClinicInfo",
									body : {
										"brid" : this.exContext.ids.brid,
										"clinicId" : this.exContext.ids.clinicId,
										"type" : '5'
									}
								});
						if (resData.code >= 300) {
							this.processReturnMsg(resData.code, resData.msg);
						}
						// var measures = resData.json.measures;
						var JKCFRecords = resData.json.ms_bcjl.JKCFRecords;
						var jkcfStr = '';
						var num = 1;
						if (JKCFRecords != null && JKCFRecords.length > 0) {
							for (var i = 0; i < JKCFRecords.length; i++) {
								var r = JKCFRecords[i];
								var diagnoseId = parseInt(r.diagnoseId);
								if (this.opener.opener) {
									this.opener.opener.JKCFRecords[diagnoseId] = r;
								}
								jkcfStr += num + '.' + r.healthTeach;
								num++;
								if (i + 1 < JKCFRecords.length) {
									jkcfStr += '\n';
								}
							}
						}
						this.setElementValue(jkcfStr, this.emr.StrReturnData);
					}
				}
			},
			beforeCardWinClose : function() {
				var id = this.cardWin.getLayout().activeItem.id;
				// 关闭时取消业务锁
				if (this.midiModules[id].beforeclose()) {
					this.unlock(id, this.midiModules[id]);
					return true;
				} else {
					return false;
				}
				// return this.midiModules[id].beforeclose();
			},
			onClinicSave : function(busType) {
				if (busType == 2) {
					this.Backfill("MS_BRZD");
					if (this.opener.opener) {
						this.opener.opener.refreshEMRNavTree();
					}
				} else if (busType == 3) {
					this.Backfill("MS_CF01");
					this.Backfill("MS_YJ01");
				}
			},
			onClinicRemove : function() {
				this.Backfill("MS_BRZD");
				if (this.opener.opener) {
					this.opener.opener.refreshEMRNavTree();
				}
			},
			doInsertYxbds : function() {
				var yxbds = this.cacheYxbds();
				if (!yxbds || yxbds.length == 0) {
					MyMessageTip.msg("提示", "请先维护医学表达式!", true);
					return;
				}
				var instr = "";
				for (var i = 0; i < yxbds.length; i++) {
					instr += this.exContext.empiData.BRKS_text + "#"
							+ yxbds[i].BDSMC + "#" + yxbds[i].BDSNR + "#,";
				}
				this.emr.FunActiveXInterface('BsCurrentPos', '0', '');
				this.emr.FunActiveXInterface('BsEditMedicPic', '0#'
								+ this.emr.StrReturnData, this.doLoadYxbdsbh()
								+ '#' + instr);
			},
			doEitorYxbds : function(bdsbh) {
				this.emr.FunActiveXInterface('BsCurrentPos', '0', '');
				this.emr.FunActiveXInterface('BsEditMedicPic', '1#'
								+ this.emr.StrReturnData, bdsbh + "#"
								+ this.doLoadYxbdsBl(bdsbh));
			},
			doLoadYxbdsBl : function(bdsbh) {
				if (this.EMR_YXBDS_BL) {
					for (var i = 0; i < this.EMR_YXBDS_BL.length; i++) {
						if (this.EMR_YXBDS_BL[i].BLBDSBH == bdsbh) {
							return this.EMR_YXBDS_BL[i].BDSNR;
						}
					}
				}
				var resData = phis.script.rmi.miniJsonRequestSync({
							serviceId : "emrManageService",
							serviceAction : "loadYxbds",
							body : {
								"BLBDSBH" : bdsbh,
								"queryType" : "2"
							}
						});
				if (resData.code > 200) {
					MyMessageTip.msg("提示", resData.msg, true);
					this.emrLoading = false;
					return null;
				}
				return resData.json.body.BDSNR;
			},
			// 获取当前科室的 医学表达式
			cacheYxbds : function() {
				if (!this.chchedYxbds) {
					var resData = phis.script.rmi.miniJsonRequestSync({
								serviceId : "emrManageService",
								serviceAction : "loadYxbds",
								body : {
									"SSKS" : this.mainApp['phis'].departmentId,
									"queryType" : "1"
								}
							});
					if (resData.code > 200) {
						MyMessageTip.msg("提示", resData.msg, true);
						this.emrLoading = false;
						return false;
					}
					this.chchedYxbds = resData.json.body;
				}
				return this.chchedYxbds;
			},
			doSaveYxbds : function(key, data) {
				if (!this.EMR_YXBDS_BL) {
					this.EMR_YXBDS_BL = [];
				}
				for (var i = 0; i < this.EMR_YXBDS_BL.length; i++) {
					if (this.EMR_YXBDS_BL[i].BLBDSBH == key) {
						this.EMR_YXBDS_BL[i].BDSNR = data;
						return;
					}
				}
				this.EMR_YXBDS_BL.push({
							BLBDSBH : key,
							BDSNR : data,
							BLBH : this.BLBH,
							ZYMZ : 1,
							SYBZ : 1
						})
				// phis.script.rmi.jsonRequest({
				// serviceId : "emrManageService",
				// serviceAction : "saveYxbds",
				// body : {
				// BLBDSBH : key,
				// BLBH : this.BLBH,
				// BDSNR : data,
				// ZYMZ : 1,
				// SYBZ : 1
				// }
				// }, function(code, msg, json) {
				// if (code >= 300) {
				// this.processReturnMsg(code, msg);
				// return;
				// }
				// }, this);
			},
			doChangeParaCation : function(paraKey) {
				if (this.node.BLLX != 1) {
					MyMessageTip.msg("提示", "当前病历不能修改标题!", true);
					return;
				}
				var blbh = this.getCurBlbh();
				if (!blbh || this.BLBH != blbh || !this.editing) {
					MyMessageTip.msg("提示", "当前病历不能修改标题!", true);
					return;
				}
				var moduleCfg = this.loadModuleCfg(this.refParaWin);
				var cfg = {
					showButtonOnTop : true,
					border : false,
					frame : false,
					autoLoadSchema : false,
					isCombined : true,
					exContext : {}
				};
				Ext.apply(cfg, moduleCfg);
				var cls = moduleCfg.script;
				if (!cls) {
					return;
				}
				$import(cls);
				var paraModule = eval("new " + cls + "(cfg)");
				paraModule.setMainApp(this.mainApp);
				var blbh = paraKey.split("_1_")[1];
				var bl01;
				for (var i = 0; i < this.cacheBl01List.length; i++) {
					if (this.cacheBl01List[i].BLBH == blbh) {
						bl01 = this.cacheBl01List[i];
						break;
					}
				}
				if (!this.cacheMbXsmc['MBXX_' + blbh]) {
					var res = phis.script.rmi.miniJsonRequestSync({
								serviceId : "emrManageService",
								serviceAction : "getMbXsmc",
								body : {
									MBBH : this.bl01.MBBH
								}
							});
					this.cacheMbXsmc['MBXX_' + blbh] = res.json.MBXX;
				}
				paraModule.bl01 = bl01;
				paraModule.MBXX = this.cacheMbXsmc['MBXX_' + blbh];
				paraModule.on("close", this.showEmr, this);
				paraModule.on("changeParaCation", this.setParaCation, this);
				var win = paraModule.getWin();
				win.setWidth(600);
				win.setHeight(145);
				this.hideEmr();
				win.show();

			},
			setParaCation : function(doctor, recordTime, title) {
				this.emr.FunActiveXInterface('BsChangeParaCaption',
						'element..Illrc_1_' + this.BLBH, title);
				this.bl01.BLMC = title.replace(/\\r\n/g, "").trim();
				this.bl01.SSYS = doctor;
				this.changeParaCation = true;
				this.bl01.JLSJ_new = recordTime;
			},
			doEditTextElement : function(valueA, valueB) {
				if (valueB == '入院诊断元素') {

				} else if (valueB == '确认诊断元素') {

				} else {
					// 签名元素
					if (this.checkElemExist(valueB)) {
						this.doSigned(null, null, valueB);
					}
				}
			},
			/**
			 * 判断接口元素是否存在
			 * 
			 * @param {}
			 *            eleName 接口元素名称
			 */
			checkElemExist : function(eleName) {
				if (!this.cacheElements) {
					var result = phis.script.rmi.miniJsonRequestSync({
								serviceId : "emrManageService",
								serviceAction : "loadJkys"
							});
					if (result.code > 200) {
						MyMessageTip.msg("提示", result.msg, true);
						return false;
					}
					this.cacheElements = result.json.body;
				}
				for (var i = 0; i < this.cacheElements.length; i++) {
					if (this.cacheElements[i].GLYS
							&& this.cacheElements[i].GLYS.indexOf("," + eleName
									+ ",") >= 0)
						return true;
				}
				return false;
			},
			/**
			 * SXQX 代表书写权限 CKQX 代表查看权限 SYQX 代表审阅权限 DYQX 代表打印权限 特殊
			 * 参数为JSJB时，返回当前医生的角色级别
			 * 
			 * @param {}
			 *            op
			 * @return {Boolean}
			 */
			checkEmrPermission : function(op, blbh) {
				if (this.node.ReadOnly)
					return true;
				if (this.cachePermission[blbh]) {
					return this.cachePermission[blbh][op]
				}
				var bl01 = this.getBl01ByBlbh(blbh);
				var result = phis.script.rmi.miniJsonRequestSync({
							serviceId : "emrManageService",
							serviceAction : "checkPermission",
							body : {
								BLLB : bl01.MBLB
							}
						});
				if (result.code > 200) {
					MyMessageTip.msg("提示", result.msg, true);
					return false;
				}
				this.cachePermission[blbh] = result.json.emrPermissions;
				return this.cachePermission[blbh][op]
			},
			ActiveXKiller : function(AcitveXObjectID) {
				var ce = document.getElementById(this.mKey);
				if (ce) {
					var cce = ce.children;
					for (var i = 0; i < cce.length; i = i + 1) {
						if (cce[i].id == AcitveXObjectID) {
							ce.removeChild(cce[i]);
						}
					}
				}
			},
			doDownLoadTwo : function() {
				window
						.open(ClassLoader.appRootOffsetPath
								+ "resources/phis/resources/component/ffactivex-setup-r39.exe");
			},
			doDownLoad : function() {
				window.open(ClassLoader.appRootOffsetPath
						+ "resources/phis/resources/component/ActiveX.exe");
			},
			doPrintBtn : function() {
				this.doPrint("printAll");
			},
			doPrint : function(printType, paraA, paraB) {
				if (this.emr.FunActiveXInterface("BsCheckWordDataChange", '',
						'')
						|| this.modifySign) {
					MyMessageTip.msg("提示", "有修改未保存，请先保存后再打印!", true);
					return;
				}
				var blbh = this.BLBH;
				if (this.node.BLLX == 1) {
					blbh = this.getCurBlbh();
					if (!blbh) {
						MyMessageTip.msg("提示", "对不起，没有需要打印的内容信息!", true);
						return;
					}
				}
				if (!this.checkEmrPermission('DYQX', blbh)) {
					MyMessageTip.msg("提示", "对不起，您没有打印该病历的权限!", true);
					return;
				}
				if (this.node.BLLX == 1) {
					if (!this.emr.FunActiveXInterface('BsCheckParaContent',
							'element..Illrc_1_' + blbh, '')) {
						MyMessageTip.msg("提示", "当前病程为空，不允许保存!", true);
						return false;
					}
				} else {
					if (this.emr
							.FunActiveXInterface('BsCheckWordEmpty', '', '')) {
						MyMessageTip.msg("提示", "当前病历为空，不允许保存!", true);
						return false;
					}
				}
				// 判断是否加载后台
				this.loadEmrHT(blbh);
				if (this.exContext.systemParams.DYQWZXJY > 0) {
					if (this.node.BLLX == 1) {
						this.emr.FunActiveXInterface("BsCheckComplete",
								'element..Illrc_1_' + blbh, '')
					} else {
						this.emr.FunActiveXInterface("BsCheckComplete", '', '')
					}
					if (this.emr.StrReturnData) {
						MyMessageTip.msg("提示", "当前病历填写不完整，不能打印!", true);
						return;
					}
				}
				this.opener.showEmrPrint(this, printType, paraA, paraB, blbh);
			},
			/**
			 * 获取当前光标所在的病历编号
			 */
			getCurBlbh : function() {
				this.emr.FunActiveXInterface('BsGetCurrentParakey', '', '');
				if (this.emr.StrReturnData.indexOf("SpacePara") >= 0) {
					MyMessageTip.msg("提示", "对不起，不能操作空段落!", true);
					return false;
				}
				return this.emr.StrReturnData.split("_1_")[1];
			},
			alert : function(title, msg) {
				this.hideEmr();
				Ext.Msg.alert(title, msg, function() {
							this.showEmr()
						}, this);
			},
			doShowVersion : function() {
				var s = this.emr
						.FunActiveXInterface("BsGetFileVersion", '', '');
				alert(s);
				alert(this.emr.StrReturnData)
			},
			doNew : function() {
				// 判断是否有新建权限
				// if (!this.checkEmrPermission('SXQX')) {
				// this.hideEmr();
				// this.alert("错误", "对不起，您没有新建当前病历的权限!");
				// return;
				// }
				// 判断是否修改
				if (this.node.BLLX == 1) {// 病程
					// 为空或者未修改
					if (this.emr
							.FunActiveXInterface("BsCheckWordEmpty", '', '')) {
						this.doImportEMR();
					} else {
						if (this.emr.FunActiveXInterface(
								"BsCheckWordDataChange", '', '')
								|| this.modifySign) {
							this.hideEmr();
							Ext.Msg.confirm("提示", "病程【" + this.bl01.BLMC
											+ "】已修改，是否保存？", function(btn) {
										this.showEmr();
										if (btn == 'yes') {
											// this.BLBH = blbh;
											if (!this.doSave()) {
												return;
											}
											this.openTemplateChooseWin();
										} else {
											var blbh = this.BLBH;
											if (this.node.BLLX == 1) {
												if (this.cacheBl01List
														&& this.cacheBl01List.length > 0) {
													blbh = this.cacheBl01List[0].BLBH;
												} else {
													this.emr
															.FunActiveXInterface(
																	"BsEmptyDoc",
																	'', '')
													this.BLBH = null;
													this.doImportEMR();
													return;
												}
											}
											// 清空痕迹
											this.emr.FunActiveXInterface(
													'BsDocClearTrace', '', '')
											this.modifySign = 0;
											this.doLoadBcjl(blbh, true);
											this.lastSign = "1";
											this.openTemplateChooseWin();
										}
									}, this)
						} else {
							this.openTemplateChooseWin();
						}
					}
					return;
				}
				// 判断是否单一文档
				// 是否是空文档
				if (this.node.DYWD == 1) {
					// 判断数据库是否存在（防止多人操作）
					if (!this.emr.FunActiveXInterface("BsCheckWordEmpty", '',
							'')) {
						this.alert("提示", "单一文档，不能新建多份!")
						return;
					}
					this.openTemplateChooseWin();
				} else {
					if (this.emr
							.FunActiveXInterface("BsCheckWordEmpty", '', '')) {
						this.openTemplateChooseWin();
					} else {
						// 打开一份新文档
						// 设置一个新建的唯一编号
						var newNode = {};
						Ext.apply(newNode, this.node);
						if (this.node.LBMC) {
							newNode.text = newNode.LBMC;
						}
						newNode.BLBH = "new_" + new Date().getTime();
						this.opener.openEmrEditorModule(newNode);
					}

				}
			},
			// 获取病历编号
			doLoadBlbh : function() {
				var resData1 = phis.script.rmi.miniJsonRequestSync({
							serviceId : "emrManageService",
							serviceAction : "loadKey",
							body : {
								schema : "EMR_BL01"
							}
						});
				var resData2 = phis.script.rmi.miniJsonRequestSync({
							serviceId : "emrManageService",
							serviceAction : "loadKey",
							body : {
								schema : "OMR_BL01"
							}
						});
				return (parseFloat(resData1.json.key) > parseFloat(resData2.json.key))
						? resData1.json.key
						: resData2.json.key
			},
			doLoadYxbdsbh : function() {
				var resData = phis.script.rmi.miniJsonRequestSync({
							serviceId : "emrManageService",
							serviceAction : "loadKey",
							body : {
								schema : "EMR_YXBDS_BL"
							}
						});
				return resData.json.key
			},
			getBl01ByBlbh : function(blbh) {
				if (this.node.BLLX == 1) {
					if (this.ifNew) {
						return {};
					}
					if (!this.cacheBl01List)
						return null;
					for (var i = 0; i < this.cacheBl01List.length; i++) {
						if (this.cacheBl01List[i].BLBH == blbh) {
							return this.cacheBl01List[i];
						}
					}
					return null;
				} else {
					return this.bl01;
				}

			},
			getBl02ByBlbh : function(blbh) {
				if (this.node.BLLX == 1) {
					if (this.cacheBl02Map) {
						return this.cacheBl02Map["BLBH_" + blbh]
					} else {
						return {};
					}
				} else {
					return this.bl02;
				}
			},
			// 设置当前操作的病历信息
			setCurrBlxx : function(blbh) {
				this.bl01 = this.getBl01ByBlbh(blbh);
				this.bl02 = this.getBl02ByBlbh[blbh];
				if (!this.bl01)
					this.bl01 = {};
				if (!this.bl02)
					this.bl02 = {};
			},
			// 获得病历01内容，供本地缓存
			getEmrBlxx : function() {
				if (this.node.BLLX == 1 && !this.ifNew) {
					this.setCurrBlxx(this.BLBH);
				}
				// alert(Ext.encode(this.exContext.ids));
				if (this.ifNew && !this.recoveredBl01) {
					this.bl01.BLMC = this.BLMC;
					this.bl01.BRBH = this.exContext.empiData.MZHM;
					this.bl01.BRID = this.exContext.ids.brid;
					this.bl01.BLLX = this.node.BLLX;
					this.bl01.BLLB = this.node.key;
					this.bl01.DLLB = 0;
					this.bl01.DLJ = '-';
					this.bl01.JLSJ = this.JLRQ || new Date();
					this.bl01.CJKS = this.mainApp['phis'].departmentId;
					this.bl01.SSYS = this.mainApp.uid;
					this.bl01.SXYS = this.mainApp.uid;
					this.bl01.BLZT = 0;
					this.bl01.SYBZ = 0;
					this.bl01.BLYM = 0;
					this.bl01.YMJL = 0;
					this.bl01.JGID = this.mainApp['phisApp'].deptId;
					this.bl01.JZXH = this.exContext.ids.clinicId;
					this.bl01.BLBH = this.BLBH;
					this.bl01.ifNew = true;
				} else {
					if (!this.bl01) {
						this.bl01 = {};
					} else {
						this.bl01.ifNew = false;
					}
				}
				this.bl01.DKSJ = this.bjOpenTime;
				this.bl01.BRXM = this.exContext.empiData.personName;
				this.bl01.BRKS = this.mainApp['phis'].departmentId;
				this.bl01.BRCH = '0';
				this.bl01.BRBQ = '0';
				this.bl01.BRNL = '10';// this.exContext.empiData.AGE;
				this.bl01.BRZD = '无';
				// 页眉页脚
				if (this.ifNewYmyj) {
					this.emr.FunActiveXInterface('BsSaveWordData', 2, '');
					this.bl01.YMYJ = 1;
					this.bl01.ymysText = this.ymysText_new;
					this.bl02.YMYJNR = this.emr.WordData;
					this.bl02.YMYJHT = this.emr.WordXML;
				} else {
					delete this.bl01.YMYJ;
					delete this.bl01.ymysText;
					delete this.bl02.YMYJNR;
					delete this.bl02.YMYJHT;
				}
				// bl02
				this.bl02.BLBH = this.BLBH;
				this.bl02.JGID = this.mainApp['phisApp'].deptId;
				// if (!this.bl02.BLNR || !this.bl02.BLHT) {
				// this.alert("错误", "获取病历内容错误:请尝试重新打开或联系管理员!");
				// return false;
				// }
				return true;
			},
			// 获取审阅记录
			getReadRecord : function(blbh) {
				if (isNaN(blbh)) {
					blbh = null;
				}
				var result = phis.script.rmi.miniJsonRequestSync({
							serviceId : "emrManageService",
							serviceAction : "loadReadRecord",
							body : {
								BLBH : blbh
							}
						});
				if (result.code > 200) {
					MyMessageTip.msg("提示", result.msg, true);
					return false;
				}
				return result.json.records;
			},
			// 签名
			checkReadRecord : function(blbh) {
				if (!blbh) {
					blbh = this.BLBH;
				}
				var records = this.getReadRecord(blbh);
				if (!records || records.length == 0) {// 不存在审阅记录
					var bl01 = this.getBl01ByBlbh(blbh);
					if (!bl01) {
						MyMessageTip.msg("提示", "对不起，无法获取当前病程信息!", true);
						return false;
					}
					if (bl01.SSYS == this.mainApp.uid
							|| bl01.SXYS == this.mainApp.uid) {
						return true
					} else {
						MyMessageTip.msg("提示", "对不起，您没有编辑该病历的权限!", true);
						return false;
					}
					return (bl01.SSYS == this.mainApp.uid || bl01.SXYS == this.mainApp.uid)
				} else {
					if (this.exContext.systemParams.YXXJYSXG > 0) {// 允许下级医生修改
						for (var i = 0; i < records.length; i++) {
							if (records[i].SYYS == this.mainApp.uid)
								return true;
						}
					}
					if (!this.checkEmrPermission('SYQX', blbh)) {
						MyMessageTip.msg("提示", "对不起，您没有审阅该病历的权限!", true);
						return false;
					}
					var jsjb = this.checkEmrPermission('JSJB', blbh);
					var maxLevel = -1;
					var maxSyys;
					for (var i = 0; i < records.length; i++) {
						if (records[i].QXJB > maxLevel) {
							maxLevel = records[i].QXJB;
							maxSyys = records[i].SYYS;
						}
					}
					if (jsjb && jsjb > maxLevel) {
						return true
					}
					if (jsjb == maxLevel && maxSyys == this.mainApp.uid)
						return true;
				}
				MyMessageTip.msg("提示", "对不起，上级医师已签名，您没有权限编辑该病历/病程!", true);
				return false;
			},
			doBeforeEditor : function() {
				var blbh = this.BLBH;
				if (this.node.ReadOnly) {
					return;
				}
				if (this.node.BLLX == 1) {
					// 获取当前光标位的BLBH
					this.emr.FunActiveXInterface('BsCurrentPos', '0', '');
					var curPos = this.emr.StrReturnData;
					blbh = this.getCurBlbh();
					if (!blbh) {
						MyMessageTip.msg("提示", "无法获取当前病程信息!", true);
						return;
					}
					// 判断当前是否有编辑的
					if (this.BLBH == blbh && this.editing) {
						MyMessageTip.msg("提示", "当前病历已是编辑状态!", true);
						return;
					}
					// 判断权限
					if (!this.checkEmrPermission('SXQX', blbh)) {
						MyMessageTip.msg("提示", "对不起，您没有编辑该病历的权限!", true);
						return;
					}
					if (this.emr.FunActiveXInterface('BsCheckWordDataChange',
							'', '')
							|| this.modifySign) {
						this.hideEmr();
						Ext.Msg.confirm("提示", "病程【" + this.bl01.BLMC
										+ "】已修改，是否保存？", function(btn) {
									this.showEmr();
									if (btn == 'yes') {
										// this.BLBH = blbh;
										if (this.doSave()) {
											this.emr.FunActiveXInterface(
													'BsCurrentPos', '1', curPos
															+ '');
											this.doBeforeEditor();
										}
									}
								}, this)
						return;
					} else {
						this.emr.FunActiveXInterface('BsSetParaReadOnly',
								'element..Illrc_1_' + this.BLBH, 1);
					}
				}
				// 判断审阅权限
				if (!this.checkReadRecord(blbh)) {
					this.emr.FunActiveXInterface('BsDocClearTrace', '', '')
					this.modifySign = 0;
					return;
				}
				if (this.node.BLLX == 1) {
					// 判断要编辑的病程是否被其它医生修改
					var result = phis.script.rmi.miniJsonRequestSync({
								serviceId : "emrManageService",
								serviceAction : "checkEditor",
								body : {
									BLBH : blbh,
									DKSJ : this.blOpenTime
								}
							});
					if (result.code > 200) {
						MyMessageTip.msg("提示", result.msg, true);
						return;
					}
					if (result.json.hasEditor) {
						this.hideEmr()
						Ext.Msg.confirm("提示", "该病程已被其它医生修改，是否重新打开？", function(
										btn) {
									this.showEmr();
									if (btn == 'yes') {
										this.doLoadBcjl(blbh, true);
										this.doEditor();
									}
								}, this)
						return;
					}
				} else {
					if (this.editing)
						return;
				}
				this.BLBH = blbh;
				this.doEditor(curPos);
			},
			// 加载病历后台
			loadEmrHT : function(blbh) {
				if (!this.loadedEmrHt) {
					this.loadedEmrHt = {};
				}
				if (this.loadedEmrHt[blbh])
					return true;
				var bl01 = this.bl01;
				var bl02 = this.bl02;
				if (this.node.BLLX == 1) {
					if (!this.ifNew) {
						bl01 = this.getBl01ByBlbh(blbh);
						bl02 = this.getBl02ByBlbh(blbh);
					}
				}
				var mbbh = bl01.MBBH
				var resData = phis.script.rmi.miniJsonRequestSync({
							serviceId : "emrManageService",
							serviceAction : "loadEmrEditorData",
							body : {
								BLLX : this.node.BLLX,
								CHTCODE : mbbh
							}
						});
				if (resData.code > 200) {
					MyMessageTip.msg("提示", resData.msg, true);
					return null;;
				}
				if (this.node.BLLX == 1) {
					this.emr.FunActiveXInterface('BsLoadIllRecXml', blbh + '#'
									+ resData.json.gbkText, bl02.BLHT || '');
				} else {
					var blht = bl02.BLHT || '';
					if (this.recoveredBl01) {
						this.emr.FunActiveXInterface("BsEditAutoData", '0',
								this.BLBH + '#' + this.mainApp.uid);
						blht = this.emr.WordXML;
					}
					this.emr.FunActiveXInterface('BsLoadCaseDocXml',
							resData.json.gbkText, blht);
				}
				this.loadedEmrHt[blbh] = true;
				return resData;
			},
			doEditor : function(curPos) {
				var resData = this.loadEmrHT(this.BLBH);
				if (!resData) {
					return;
				}
				if (this.node.ReadOnly) {
					return;
				}
				if (!this.lockEmr(this.BLBH)) {
					this.emr.FunActiveXInterface('BsDocClearTrace', '', '')
					this.modifySign = 0;
					return;
				}
				if (this.node.BLLX == 1) {
					this.emr.FunActiveXInterface('BsSetDocView', 'ReadOnly', 0);
					this.emr.FunActiveXInterface('BsSetParaReadOnly',
							'element..Illrc_1_' + this.BLBH, 0);
				} else {
					this.emr.FunActiveXInterface('BsSetDocView', 'ReadOnly', 0);
				}
				// 清除痕迹
				if (!this.ifNew && !this.recoveredBl01) {
					this.emr.FunActiveXInterface('BsDocClearTrace', '', '')
					this.modifySign = 0;
				}
				if (curPos) {
					this.emr.FunActiveXInterface('BsCurrentPos', '1', curPos
									+ '');
				} else {
					if (this.node.BLLX == 1) {
						this.emr.FunActiveXInterface('BsDocGoto',
								'element..Illrc_1_' + this.BLBH, '2');
					}
				}
				if (!this.ifNew && this.node.BLLX == 1) {
					this.setCurrBlxx(this.BLBH);
				}
				this.editing = true;
				if (!this.recoveredBl01) {
					this.getEmrBlxx(this.BLBH);
				}
				// 设置tab标题
				var tab = this.opener.mainTab.getActiveTab();
				tab.setTitle(this.bl01.BLMC + "-编辑");
			},
			/**
			 * 
			 * @param {}
			 *            a
			 * @param {}
			 *            b
			 * @param {}
			 *            noMsg 不弹出提示信息
			 * @param {}
			 *            focusToSave 强制保存
			 * @return {Boolean}
			 */
			doTgjc : function() {
				this.emr.FunActiveXInterface('BsDocGetElemvalue',
						'element..体格检查', "#收缩压");
				var ssy = this.emr.StrReturnData;
				this.emr.FunActiveXInterface('BsDocGetElemvalue',
						'element..体格检查', "#舒张压");
				var szy = this.emr.StrReturnData;
				if (!isNaN(ssy) && !isNaN(szy)) {
					if (parseInt(ssy) < parseInt(szy)) {
						MyMessageTip.msg("提示", "收缩压必须大于舒张压!", true);
						return false;
					}
				}
				if (this.exContext.ids.age >= 35 && !this.hasClinicRecord) {
					if (isNaN(ssy) || isNaN(szy)) {
						MyMessageTip.msg("提示", "病人超过35岁,必须测量血压！", true);
						return false;
					} else {
						// 把血压插入初诊表MDC_Hypertension_FCBP中
						var body = {
							empiId : this.exContext.ids.empiId,
							constriction : ssy,
							diastolic : szy,
							isFZ : this.hasClinicRecord,
							age : this.exContext.ids.age,
							op : "create"
						};
						util.rmi.jsonRequest({
									serviceId : "chis.chisRecordFilter",
									serviceAction : "saveHyperFCBP",
									method : "execute",
									body : body
								}, function(code, msg, json) {
									if (code > 300) {
										MyMessageTip.msg("提示", "测量血压保存失败！",
												true);
									}
								});
					}
				}
				return true;
			},
			doSave : function(a, b, noMsg, gotoSign) {
				if (!this.doTgjc()) {
					return false;
				}
				// 空文档
				if (!gotoSign) {
					if (this.node.BLLX == 1) {
						if (!this.emr.FunActiveXInterface('BsCheckParaContent',
								'element..Illrc_1_' + this.BLBH, '')) {
							MyMessageTip.msg("提示", "当前病程为空，不允许保存!", true);
							return false;
						}
					} else {
						if (this.emr.FunActiveXInterface('BsCheckWordEmpty',
								'', '')) {
							MyMessageTip.msg("提示", "当前病历为空，不允许保存!", true);
							return false;
						}
					}
					// 判断是否修改
					if (!this.emr.FunActiveXInterface('BsCheckWordDataChange',
							'', '')
							&& !this.modifySign) {
						this.unlockEmr();
						this.emr.FunActiveXInterface('BsSetDocView',
								'ReadOnly', 1)
						// 删除痕迹
						this.emr.FunActiveXInterface('BsDocClearTrace', '', '')
						// 删除备份
						this.emr.FunActiveXInterface('BsEditAutoData', '1',
								this.BLBH + '#' + this.mainApp.uid)
						this.editing = false;
						this.hasSigned = false;
						this.saving = false;
						MyMessageTip.msg("提示", "当前病历没有发生修改，无需保存!", true);
						if (!this.closeTitle) {
							var tab = this.opener.mainTab.getActiveTab();
							if (this.node.BLLX == 1) {
								tab.setTitle("病程记录类");
							} else {
								tab.setTitle(this.bl01.BLMC);
							}
						}
						return false;
					} else {
						this.modifySign = 1;
					}
					if (this.editing && !this.hasLock) {
						MyMessageTip.msg("提示",
								"保存失败:当前病历/病程的锁定状态丢失，可能已被管理员强制解锁!", true);
						return false;
					}
					if (this.exContext.systemParams.QMYZXJY > 0) {// 签名一致性判断
						var result = phis.script.rmi.miniJsonRequestSync({
									serviceId : "emrManageService",
									serviceAction : "loadReadRecord",
									body : {
										BLBH : isNaN(this.BLBH)
												? null
												: this.BLBH
									}
								});
						if (result.code > 200) {
							if (resData.msg == "NotLogon") {
								this.hideEmr()
								this.doNotLogon(this.showEmr)
								return false;
							}
							if (resData.msg == "ConnectionError"
									|| resData.msg == "ParseResponseError") {
								this.hideEmr();
								Ext.Msg.confirm("提示", "网络连接异常，是否保存到本地？",
										function(btn) {
											this.showEmr();
											if (btn == 'yes') {
												this.autoSave();
												MyMessageTip.msg("提示",
														"本地保存成功!", true);
												this.backup = true;
											}
										}, this)
							}
							MyMessageTip.msg("提示", resData.msg, true);
							return false;
						}
						var records = result.json.records;
						for (var i = 0; i < records.length; i++) {
							// 判断是否一致
							this.emr.FunActiveXInterface('BsDocGetElemvalue',
									'element..' + records[i].DLLJ, "#"
											+ records[i].QMYS);
							var r = this.emr.StrReturnData;
							if (!r) {
								MyMessageTip.msg("提示", "保存失败："
												+ records[i].QMYS + "签名丢失!",
										true);
								return false;
							}
							if (r != records[i].QMYSZ) {
								MyMessageTip.msg("提示", "保存失败："
												+ records[i].QMYS + "签名不一致!",
										true);
								return false;
							}
						}
					}
				}
				if (!this.getEmrBlxx())
					return;
				var openBlbh = -1;
				if (this.node.BLLX == 1 && this.changeParaCation) {// 修改标题
					var newTime = this.bl01.JLSJ_new;
					if (typeof this.bl01.JLSJ_new == "string") {
						newTime = Date.parseDate(this.bl01.JLSJ_new,
								'Y-m-d H:i:s');
					}
					var oldTime = this.bl01.JLSJ;
					if (typeof this.bl01.JLSJ == "string") {
						oldTime = Date.parseDate(this.bl01.JLSJ, 'Y-m-d H:i:s');
					}
					if (newTime.getTime() < oldTime.getTime()) {
						openBlbh = this.BLBH;
					} else if (newTime.getTime() > oldTime.getTime()) {
						for (var i = 0; i < this.cacheBl01List.length; i++) {
							if (this.cacheBl01List[i].BLBH == this.BLBH) {
								if (i + 1 < this.cacheBl01List.length) {
									var nextBl01 = this.cacheBl01List[i + 1];
									var nextTime = Date.parseDate(
											nextBl01.JLSJ, 'Y-m-d H:i:s');
									if (newTime.getTime() == nextTime.getTime()) {// 相同时间取BLBH小的
										openBlbh = nextBl01.BLBH > this.BLBH
												? this.BLBH
												: nextBl01.BLBH;
									} else if (newTime.getTime() > nextTime
											.getTime()) {
										openBlbh = nextBl01.BLBH;
									} else {
										openBlbh = this.BLBH;
									}
								}
								break;
							}
						}
					}
				}
				if (this.saving) {
					MyMessageTip.msg("提示", "正在保存数据...等耐心等待!", true);
					return;
				}
				this.saving = true;
				this.bl03 = {};
				var body = {
					Node : this.node,
					BL01 : this.bl01,
					BL02 : this.bl02,
					BL03 : this.bl03,
					EMR_YXBDS_BL : this.EMR_YXBDS_BL,
					openBlbh : openBlbh
				};
				if (this.hasSigned) {
					body.SYJL = this.SYJL;
				}
				if (this.node.BLLX == 1) {
					// 获取修改痕迹
					this.emr.FunActiveXInterface('BsGetDocText',
							'element..Illrc_1_' + this.BLBH, '');
					this.bl02.HJNR = this.emr.StrReturnData;
					this.emr
							.FunActiveXInterface('BsSaveWordData', 1, this.BLBH);
					// 获取页码和页眉距离
					this.emr
							.FunActiveXInterface('BsDocGetParakeyLines', '', '');
					this.bl01.ParaLineInfo = eval('[' + this.emr.StrReturnData
							+ ']')
				} else {
					this.emr.FunActiveXInterface('BsGetDocText', 'element', '');
					this.bl02.HJNR = this.emr.StrReturnData;
					this.emr.FunActiveXInterface('BsSaveWordData', 0,
							this.bl01.MBBH);
				}
				if (!this.emr.WordData) {
					this.saving = false;
					MyMessageTip.msg("提示", "未知错误：获取病历数据失败!", true);
					return false;
				}
				this.bl01.DKSJ = this.blOpenTime;
				this.bl02.BLNR = this.emr.WordData;
				this.bl02.BLHT = this.emr.WordXML;
				this.emr.FunActiveXInterface('BsGetRecordTempHTMLString',
						this.emr.WordData, 'emrImage/');
				this.bl03.HTML = this.emr.WordData;
				this.bl03.IMAGE = eval("["
						+ this.emr.WordXML.replace(/\n/g, "") + "]");
				this.emr.FunActiveXInterface('BsGetRecordStandardXml',
						this.bl02.BLNR, '0');
				this.bl03.XML = this.emr.StrReturnData;
				this.bl03.ZYMZ = 1;
				this.bl03.BLBH = this.BLBH;
				this.bl03.JGID = this.mainApp['phisApp'].deptId;
				var resData = phis.script.rmi.miniJsonRequestSync({
							serviceId : "emrManageService",
							serviceAction : "saveOmrData",
							body : body
						});
				if (resData.code > 200) {
					if (resData.msg == "NotLogon") {
						this.hideEmr()
						this.doNotLogon(this.showEmr)
						this.saving = false;
						return
					} else if (resData.msg == "ConnectionError"
							|| resData.msg == "ParseResponseError") {
						this.hideEmr();
						Ext.Msg.confirm("提示", "网络连接异常，是否保存到本地？", function(btn) {
									this.showEmr();
									if (btn == 'yes') {
										this.autoSave();
										MyMessageTip.msg("提示", "本地保存成功!", true);
										this.backup = true;
									}
								}, this)
					} else {
						MyMessageTip.msg("提示", resData.msg, true);
					}
					this.saving = false;
					return false;
				}
				if (this.opener.opener) {
					this.opener.opener.doSave();
				}
				this.SYJL = null;
				if (this.ifNew || this.changeParaCation) {
					// 重载左侧树节点
					this.opener.loadEmrTreeNode();
				}
				if (this.ifNew) {
					if (this.node.BLLX == 1) {
						if (!this.cacheBl01List) {
							this.cacheBl01List = [];
						}
						if (!this.cacheBl02Map) {
							this.cacheBl02Map = {};
						}
						var bl01Tmp = {}
						var bl02Tmp = {};
						Ext.apply(bl01Tmp, this.bl01);
						Ext.apply(bl02Tmp, this.bl02);
						this.cacheBl01List.push(bl01Tmp);
						// alert(Ext.encode(bl01Tmp))
						this.cacheBl02Map['BLBH_' + this.BLBH] = bl02Tmp;
					} else {
						if (this.opener.opener) {
							this.opener.opener.activeModules["phis.application.cic.CIC/CIC/CIC29"
									+ this.BLBH] = true;
							if (this.opener.opener.BLSY == "phis.application.cic.CIC/CIC/CIC29"
									+ this.node.key) {
								this.opener.opener.BLSY = "phis.application.cic.CIC/CIC/CIC29"
										+ this.BLBH;
							}
						}
						this.panel.mKey = "phis.application.cic.CIC/CIC/CIC29"
								+ this.BLBH;
						this.panel.key = "phis.application.cic.CIC/CIC/CIC29"
								+ this.BLBH;
						this.opener.midiModules["phis.application.cic.CIC/CIC/CIC29"
								+ this.BLBH] = this.opener.midiModules["phis.application.cic.CIC/CIC/CIC29"
								+ this.node.key];
						delete this.opener.midiModules["phis.application.cic.CIC/CIC/CIC29"
								+ this.node.key];
						if (this.opener.opener) {
							delete this.opener.opener.activeModules["phis.application.cic.CIC/CIC/CIC29"
									+ this.node.key];
						}
					}
				}
				if (openBlbh > 0) {
					this.alert("提示", "修改标题后病程顺序发生变化，病程将重新载入.");
					this.doUpdateYmxx(resData.json.reloadBlbh);
				} else if (resData.json.reload) {
					this.alert("提示", "当前病程被其他医生修改，病程将重新载入.");
					this.doUpdateYmxx();
				}
				if (!noMsg) {
					MyMessageTip.msg("提示",
							(this.hasSigned ? "签名成功!" : "保存成功!"), true);
				}
				// 释放病历锁
				this.unlockEmr();
				// 设置文档只读
				this.emr.FunActiveXInterface('BsSetDocView', 'ReadOnly', 1)
				// 删除痕迹
				this.emr.FunActiveXInterface('BsDocClearTrace', '', '')
				this.modifySign = 0;
				// 删除备份
				this.emr.FunActiveXInterface('BsEditAutoData', '1', this.BLBH
								+ '#' + this.mainApp.uid)
				if (!this.closeTitle) {
					var tab = this.opener.mainTab.getActiveTab();
					if (this.node.BLLX == 1) {
						tab.setTitle("病程记录类");
					} else {
						tab.setTitle(this.bl01.BLMC);
					}
				}
				// 关闭自动备份
				// .......待补全
				this.blOpenTime = Date.getServerDateTime();
				this.ifNew = false;
				this.ifNewYmyj = false;
				this.editing = false;
				this.hasSigned = false;
				this.saving = false;
				this.changeParaCation = false;
				this.backup = false;
				this.EMR_YXBDS_BL = [];
				return true;
			},
			// 载入后自动调用
			doNewEMR : function() {
				this.emrLoading = true;
				var resData = phis.script.rmi.miniJsonRequestSync({
							serviceId : "emrManageService",
							serviceAction : "loadMZTemplateData",
							body : {
								"step" : 1
							}
						});
				if (resData.code > 200) {
					MyMessageTip.msg("提示", resData.msg, true);
					this.emrLoading = false;
					return false;
				}
				this.emr.FunActiveXInterface('BsNewDocument',
						resData.json.docText, '');
				if (this.exContext.systemParams.TZYS
						&& this.exContext.systemParams.TZYS != 1) {
					this.emr.FunActiveXInterface('BsSetDocView', 'IMENavigate',
							'0');
				}
				this.emrLoading = false;
			},
			doImportEMR : function() {
				// 根据病历类型弹出选择界面
				// 弹出病历模版选择
				if (!this.BLBH || ((this.BLBH + "").indexOf("new_") != -1)) {
					if (!this.BLBH) {
						if (this.node.YDLBBM.indexOf('17') >= 0) {
							var resData = phis.script.rmi.miniJsonRequestSync({
								serviceId : "emrManageService",
								serviceAction : "loadMZBlmb",
								SSKS : this.mainApp['phis'].departmentId,
								schema : "phis.application.emr.schemas.V_EMR_BLMB_MZ",
								KSDM : this.mainApp['phis'].departmentId,
								BLLB : this.node.key
							});
							if (resData.code <= 200) {
								if (resData.json.totalCount == 1) {
									var data = resData.json.body[0];
									data.mblb = 1;
									data.YSXM = this.mainApp.uname;
									this.doReLoadTemplate(data);
									this.showEmr();
									return;
								}
							}
						}
						// 判断是否有权限
						// if (!this.checkEmrPermission('SXQX', this.node.key))
						// {
						// this.hideEmr()
						// Ext.Msg.alert("错误", "对不起，您没有新建当前病历的权限!", function() {
						// this.showEmr();
						// this.opener.closeCurrentTab();
						// }, this);
						// return;
						// }
						this.hideEmr()
						Ext.Msg.show({
									title : '提示信息',
									msg : '未能检索到指定类别的病历，是否现在就要增加一份?',
									modal : true,
									width : 350,
									buttons : Ext.MessageBox.YESNO,
									multiline : false,
									fn : function(btn, text) {
										this.showEmr();
										if (btn == "yes") {
											// 打开模版选择界面
											this.openTemplateChooseWin();
										}else{
											this.doClose();
										}
									},
									scope : this
								})
					} else {
						this.openTemplateChooseWin();
					}
				} else {
					// 载入指定病历
					// 判断是否有权限
					// if (!this.checkEmrPermission('CKQX')) {
					// this.alert("错误", "对不起，您没有查看当前病历的权限!");
					// if (this.node.BLLX == 1) {
					// this.opener.closeCurrentTab();
					// }
					// return;
					// }
					this.emrLoading = true;
					var resData;
					if (!this.recoveredBl01 || this.node.BLLX == 1) {
						if (this.recoveredBl01 && this.recoveredBl01.ifNew) {
							this.lastSign = 1;
						}
						resData = phis.script.rmi.miniJsonRequestSync({
									serviceId : "emrManageService",
									serviceAction : "loadMZTemplateData",
									body : {
										"step" : 2,
										"BLLX" : this.node.BLLX,
										"BLLB" : this.node.key,
										"ZYH" : this.exContext.ids.clinicId,
										"BLBH" : this.BLBH,
										"lastSign" : this.lastSign
									}
								});
						if (resData.code > 200) {
							this.emrLoading = false;
							MyMessageTip.msg("提示", resData.msg, true);
							return;
						}
					}
					delete this.lastSign;
					if (this.node.BLLX == 1) {
						var bl01Array = resData.json.BL01List;
						if (bl01Array.length <= 0) {
							if (!this.recoveredBl01) {
								MyMessageTip.msg("提示", "未找到有效的病程数据，可能数据已被删除!",
										true);
								return;
							} else {
								// 新建病程的恢复
								this.node = this.recoveredBl01.Node;
								this.doLoadTemplate(this.recoveredBl01);
							}
						}
						this.emr.FunActiveXInterface("BsSetDocFirstPageNum",
								(bl01Array[0].BLYM - 1 > 0 ? bl01Array[0].BLYM
										- 1 : 0)
										+ "", '');
						this.printFirstPage = bl01Array[0].BLYM - 1;
						this.emr.FunActiveXInterface("BsSplitHdrftrWorddata",
								resData.json.BL02_YMYJ.BLNR, '');
						this.cacheBl01List = bl01Array;
						this.cacheBl02Map = resData.json.BL02Map;
						for (var i = 0; i < bl01Array.length; i++) {
							if (i == 0 && bl01Array[i].YMJL > 0) {
								// 空段落
								this.emr.FunActiveXInterface(
										"BsLoadSpaceFromLines",
										bl01Array[i].YMJL, '');
							}
							if (this.recoveredBl01) {
								if (this.recoveredBl01.BLBH == bl01Array[i].BLBH) {
									this.emr.FunActiveXInterface(
											"BsEditAutoData", '0',
											this.recoveredBl01.BLBH + "#"
													+ this.mainApp.uid);
									this.emr.FunActiveXInterface(
											"BsLoadIllRecData",
											this.emr.WordData,
											this.recoveredBl01.BLBH);
									this.modifySign = 1;
									// alert(this.emr.WordData)
									continue;
								}
							}
							var blnr = resData.json.BL02Map['BLBH_'
									+ bl01Array[i].BLBH].BLNR;
							this.emr.FunActiveXInterface("BsLoadIllRecData",
									blnr, bl01Array[i].BLBH);
						}
						if (this.recoveredBl01 && this.recoveredBl01.ifNew) {
							this.emr.FunActiveXInterface("BsEditAutoData", '0',
									this.recoveredBl01.BLBH + "#"
											+ this.mainApp.uid);
							this.emr.FunActiveXInterface("BsLoadIllRecData",
									this.emr.WordData, this.recoveredBl01.BLBH);
							this.BLBH = this.recoveredBl01.BLBH;
							this.node = this.recoveredBl01.Node;
							this.bl01 = this.recoveredBl01;
							this.ifNew = true;
							this.modifySign = 1;
						}
						// 页眉数据
						if (!this.recoveredBl01) {
							this.setCurrBlxx(this.BLBH);
						}
						this.emr.FunActiveXInterface("BsSendMyHeaderFooter",
								resData.json.ymysText, '');
						this.ymysText = resData.json.ymysText;
						this.emr.FunActiveXInterface("BsDocGoto",
								'element..Illrc_1_' + this.BLBH, '2');
						this.blOpenTime = Date.getServerDateTime();
						this.emr.FunActiveXInterface("BsIllRecDataEnd", '', '');
					} else {
						this.emr.FunActiveXInterface("BsSetDocFirstPageNum",
								'0', '');
						if (this.recoveredBl01) {
							if (this.recoveredBl01.ifNew) {
								this.ifNew = true;
							}
							this.emr.FunActiveXInterface("BsEditAutoData", '0',
									this.BLBH + "#" + this.mainApp.uid);
							this.emr.FunActiveXInterface("BsLoadCaseDocData",
									this.emr.WordData, '');
							this.bl01 = this.recoveredBl01;
							this.emr.FunActiveXInterface('BsEditAutoData', '1',
									this.BLBH + "#" + this.mainApp.uid);
							this.modifySign = 1;
						} else {
							this.emr.FunActiveXInterface("BsLoadCaseDocData",
									resData.json.BL02.BLNR, '');
							this.bl01 = resData.json.BL01;
							this.bl02 = resData.json.BL02;
						}
					}
					if (!this.recoveredBl01) {
						this.emr.FunActiveXInterface("BsDocClearTrace", '', '');
						this.modifySign = 0;
					}
					// resData.json.XMLTEXTPAT, '');
					// 设置是否禁用外部拷贝
					// 是否编辑
					// if (this.bl01.SXYS == this.mainApp.uid
					// || this.bl01.SSYS == this.mainApp.uid) {
					// this.doEditor();
					// }
					if (this.needBlht) {
						this.loadEmrHT(this.BLBH)
					}
					this.doBeforeEditor();
					this.emrLoading = false;
					if (this.exContext.systemParams.XSBL
							&& this.exContext.systemParams.XSBL != 100) {
						this.emr.FunActiveXInterface("BsSetDocView", 'SetZoom',
								this.exContext.systemParams.XSBL + "");
					}
					this.emr.FunActiveXInterface("BsSetPasteControl",
							(this.exContext.systemParams.SFJYWBKB == 1
									? '1'
									: "0"), '');
				}
			},
			/**
			 * 病程记录快速打开特殊处理
			 * 
			 * @param {}
			 *            blbh 要打开的病程的BLBH
			 * @param {}
			 *            forceReload 是否强制重新加载
			 */
			doLoadBcjl : function(blbh, forceReload, unsave) {
				// 判断是否已经载入了需要打开的病程
				if (this.node.BLLX != 1 || !blbh) {
					return;
				}
				var r = this.emr.FunActiveXInterface("BsCheckParaTree",
						'Illrc_1_' + blbh, '');
				if (r && !forceReload) {
					this.emr.FunActiveXInterface("BsDocGoto",
							'element..Illrc_1_' + blbh, '2');
				} else {
					// 判断当前是否修改
					var tab = this.opener.mainTab.getActiveTab();
					tab.setTitle("病程记录类");
					if ((this.emr.FunActiveXInterface("BsCheckWordDataChange",
							'', '') || this.modifySign)
							&& !unsave) {
						this.hideEmr();
						Ext.Msg.show({
									title : '提示信息',
									msg : '当前病历/病程数据发生变化，是否保存？',
									modal : true,
									width : 300,
									animEl : 'emrOcxContainer_' + this.mKey,
									buttons : Ext.MessageBox.YESNO,
									multiline : false,
									fn : function(btn, text) {
										this.showEmr();
										if (btn == 'yes') {
											if (!this.doSave()) {
												return;
											}
										}
										// 根据BLBH重新加载
										this.emr.FunActiveXInterface(
												"BsEmptyDoc", '', '');
										this.loadedEmrHt = {};
										this.BLBH = blbh;
										this.doImportEMR();
										this.blOpenTime = Date
												.getServerDateTime();// 获取服务器时间
									},
									scope : this
								});
					} else {
						// 根据BLBH重新加载
						this.emr.FunActiveXInterface("BsEmptyDoc", '', '');
						this.loadedEmrHt = {};
						this.BLBH = blbh;
						this.doImportEMR();
						this.blOpenTime = Date.getServerDateTime();// 获取服务器时间
					}
				}
			},
			doUpdateYmxx : function(blbh) {
				// 删除痕迹
				this.emr.FunActiveXInterface('BsDocClearTrace', '', '')
				this.modifySign = 0;
				this.BLBH = blbh || this.cacheBl01List[0].BLBH
				this.doLoadBcjl(this.BLBH, true);
				this.emr.FunActiveXInterface('BsDocGetParakeyLines', '', '');
				var ParaLineInfo = eval('[' + this.emr.StrReturnData + ']')
				phis.script.rmi.jsonRequest({
							serviceId : "emrManageService",
							serviceAction : "updateYmxx",
							body : {
								ParaLineInfo : ParaLineInfo
							}
						}, function(code, msg, json) {
							if (code >= 300) {
								// this.processReturnMsg(code, msg);
								return;
							}
						}, this);
			},
			doOpenPlate : function() {
				// 编辑状态
				var blbh = this.BLBH;
				if (this.node.BLLX == 1) {
					blbh = this.getCurBlbh();
					if (!blbh) {
						MyMessageTip.msg("提示", "无效的病历/病程信息，无法加载模版!", true);
						return;
					}
				}
				if (this.BLBH != blbh || !this.editing) {
					MyMessageTip.msg("提示", "当前病历/病程不可编辑，无法重新加载模版!", true);
					return;
				}
				if (this.emr.FunActiveXInterface("BsCheckWordEmpty", '', '')) {
					this.doImportEMR();
					return;
				}
				var result = phis.script.rmi.miniJsonRequestSync({
							serviceId : "emrManageService",
							serviceAction : "checkSigned",
							body : {
								BLBH : this.BLBH
							}
						});
				if (result.code > 200) {
					MyMessageTip.msg("提示", result.msg, true);
					return;
				}
				if (result.json.hasReview) {
					MyMessageTip.msg("提示", "前病历已经有签名信息，不允许再切换模板!", true);
					return;
				}
				if (this.node.BLLX == 1) {
					var blbh = this.getCurBlbh();
					if (!blbh) {
						MyMessageTip.msg("提示", "无法获取当前病历/病程信息!", true);
						return;
					}
					this.emr.FunActiveXInterface("BsDocGoto",
							'element..Illrc_1_' + blbh, '2')
				}
				this.hideEmr();
				Ext.Msg.show({
							title : '提示信息',
							msg : '当前文档已经有数据了，是否需要继续引入模板?',
							modal : true,
							width : 300,
							buttons : Ext.MessageBox.YESNO,
							multiline : false,
							fn : function(btn, text) {
								this.showEmr();
								if (btn == "yes") {
									this.openTemplateChooseWin('modify');
								}
							},
							scope : this
						})
			},
			openTemplateChooseWin : function(sign) {
				var moduleCfg = this.loadModuleCfg(this.refTplChooseModule);
				var cfg = {
					showButtonOnTop : true,
					border : false,
					frame : false,
					autoLoadSchema : false,
					isCombined : true,
					exContext : {}
				};
				Ext.apply(cfg, moduleCfg);
				var cls = moduleCfg.script;
				if (!cls) {
					return;
				}
				$import(cls);
				var tplModule = eval("new " + cls + "(cfg)");
				tplModule.setMainApp(this.mainApp);
				tplModule.on("loadTemplate", this.doReLoadTemplate, this);
				tplModule.on("close", this.showEmr, this);
				tplModule.node = this.node;
				tplModule.opener = this;
				tplModule.exContext = this.exContext;
				tplModule.ifNew = !(sign == 'modify');
				var tpl_win = tplModule.getWin();
				tpl_win.setWidth(900);
				tpl_win.setHeight(450);
				this.tpl_win = tpl_win;
				// this.midiWins[this.node.BLBH || this.node.key] = tpl_win;
				this.hideEmr();
				this.tpl_win.show();
			},
			doReLoadTemplate : function(data) {
				this.doLoadTemplate(data);
				this.modifySign = 1;
			},
			doLoadTemplate : function(data) {
				this.emrLoading = true;
				// 前台获取病历编号
				if (!this.BLBH || (this.BLBH + '').indexOf("new_") != -1) {
					this.ifNew = true;
					this.BLBH = this.doLoadBlbh();
					// this.node.BLBH = this.BLBH;
					this.setCurrBlxx(this.BLBH);
					this.getEmrBlxx()
				} else {
					this.changeParaCation = true;
				}
				this.bl01.MBLB = data.MBLB;
				this.bl01.MBBH = data.MBBH;
				this.bl01.HYBZ = data.HYBZ;
				// 是否病程，是否需要载入页眉页脚
				if (this.node.BLLX == 1) {
					if (this.emr
							.FunActiveXInterface("BsCheckWordEmpty", '', '')) {
						// 载入页面页脚
						// alert(Ext.encode(this.node))
						// alert(Ext.encode(this.exContext.empiData))
						var resData = phis.script.rmi.miniJsonRequestSync({
									serviceId : "emrManageService",
									serviceAction : "loadMZTemplateData",
									body : {
										"step" : 3,
										"BLLB" : this.node.key,
										"MBLB" : '1',// 1 病历模版 2 个人模版
										"KSDM" : this.mainApp['phis'].departmentId,
										"ZYH" : this.exContext.ids.clinicId,
										"BLBH" : this.BLBH
									}
								});
						if (resData.code > 200) {
							this.alert("警告", resData.msg);
							// this.emrLoading = false;
							// this.emr.FunActiveXInterface("BsEmptyDoc", '',
							// '');
							if (this.closable) {
								this.opener.closeCurrentTab();
							} else {
								this.emr.FunActiveXInterface("BsEmptyDoc", '',
										'');
							}
							return;
						}
						// 载入页面页脚
						this.emr.FunActiveXInterface(
								"BsIllrecLoadHdrftrTemplate",
								resData.json.gbkText, resData.json.uft8Text)
						// 载入页眉元素
						this.ymysText = resData.json.ymysText;
						this.ifNewYmyj = true;
					}
					// 病程的时间和医生
					this.JLRQ = data.JLRQ.format('Y-m-d H:i:s');
					this.YSXM = data.YSXM;
					if (this.changeParaCation) {
						this.bl01.SSYS = this.mainApp.uid;
						this.bl01.JLSJ_new = data.JLRQ;
					}
				} else {
					this.JLRQ = data.JLRQ;
				}
				// 设置病历名称
				if (data.XSMC) {
					var title = '';
					// if (this.node.BLLX == 1) {
					// title = data.title;
					// }
					if (this.node.XSMC == "模板名称") {
						title = data.MBMC;
					} else if (data.XSMC == "科室名称") {
						title = "全科";
					} else {
						var xsmc = data.XSMC.split("+");
						for (var i = 0; i < xsmc.length; i++) {
							if (xsmc[i].indexOf("记录日期") >= 0) {
								var fmt = xsmc[i].substring(xsmc[i]
												.indexOf('{')
												+ 1, xsmc[i].indexOf('}'))
								title += data.JLRQ.format('Y-m-d H:i');
							} else if (xsmc[i].indexOf("类别名称") >= 0) {
								title += data.MBMC;
							} else if (xsmc[i].indexOf("医生姓名") >= 0) {
								title += data.YSXM;
							} else if (xsmc[i].indexOf("换行符") >= 0) {
								title += '\r\n';
							} else {
								title += xsmc[i].replace(/\'/g, "");
							}
						}
					}
					if (title) {
						this.BLMC = title.replace(/\\r\n/g, "").trim();
						this.bl01.BLMC = this.BLMC;
					}
					if (this.node.BLLX == 1) {
						// 段落键冲突问题，add by yangl
						var r = this.emr.FunActiveXInterface("BsCheckParaTree",
								'Illrc_1_' + this.BLBH, '');
						if (this.ifNew && !r) {
							// 换页
							if (this.cacheBl01List
									&& this.cacheBl01List.length > 0) {
								if (data.HYBZ == 1) {
									title = '' + title;
								} else {
									// 前一份的HYBZ
									var lastBl01 = this.cacheBl01List[this.cacheBl01List.length
											- 1];
									if (lastBl01
											&& (lastBl01.HYBZ == 2 || lastBl01.HYBZ == 3)) {
										title = '' + title;
									}
								}
							}
							// 调用设置段落建
							// alert('Illrc_1_' + this.node.BLBH)
							this.emr.FunActiveXInterface("BsIllrecNewPara",
									'Illrc_1_' + this.BLBH, title);
						} else {
							this.emr.FunActiveXInterface("BsChangeParaCaption",
									'element..Illrc_1_' + this.BLBH, title);
						}
					} else {
						// if(!this.node.SJH){
						var tab = this.opener.mainTab.getActiveTab();
						tab.setTitle(this.BLMC);
						// }
					}
					this.emrLoading = false;

				}
				// 载入模版数据
				var resData = phis.script.rmi.miniJsonRequestSync({
							serviceId : "emrManageService",
							serviceAction : "loadMZTemplateData",
							body : {
								"step" : 3,
								"MBLB" : data.mblb,// 1 病历模版 2 个人模版
								"CHTCODE" : (data.mblb == 2
										? data.PTID
										: data.MBBH),
								"BLLX" : this.node.BLLX
							}
						});
				if (resData.code > 200) {
					this.alert("警告", resData.msg);
					return;
				}
				if (this.node.BLLX == 1) {
					this.emr.FunActiveXInterface("BsIllrecAddTemplate",
							data.MBBH + "#" + resData.json.gbkText,
							resData.json.uft8Text);
				} else {
					this.emr.FunActiveXInterface("BsLoadTemplateDate",
							resData.json.gbkText, resData.json.uft8Text);
				}
				if (!this.loadedEmrHt) {
					this.loadedEmrHt = {};
				}
				this.loadedEmrHt[this.BLBH] = true;
				// 引用元素加载
				this.updateRef();
				// 页眉元素加载
				if (this.node.BLLX == 1) {
					if (this.ifNew) {
						var resData = phis.script.rmi.miniJsonRequestSync({
									serviceId : "emrManageService",
									serviceAction : "loadMZTemplateData",
									body : {
										"step" : 4,
										"ZYH" : this.exContext.ids.clinicId,
										"BLLB" : this.node.key,
										"KSDM" : this.mainApp['phis'].departmentId,
										"BLBH" : this.BLBH
									}
								});
						if (resData.code > 200) {
							this.alert("警告", resData.msg);
							// this.opener.closeCurrentTab();
							// return;
						} else {
							this.ymysText_new = resData.json.ymysText_new;
							this.ymysText = resData.json.ymysText;
							// alert(resData.json.ymysText)
							this.emr.FunActiveXInterface(
									"BsSendMyHeaderFooter",
									resData.json.ymysText, '');
						}
					}
					this.emr.FunActiveXInterface("BsIllRecDataEnd", '', '');
					if (!this.ifNew && !this.changeParaCation) {
						this.emr.FunActiveXInterface("BsDocClearTrace", '', '');
						this.modifySign = 0;
					}
				}
				var res = phis.script.rmi.miniJsonRequestSync({
							serviceId : "emrManageService",
							serviceAction : "loadPrtSetup",
							body : {
								BLLB : this.node.key
							}
						})
				if (res.code >= 300) {
					MyMessageTip.msg("提示", res.msg, true);
					return;
				}
				if (res.json.body.prtSetup) {
					this.emr.FunActiveXInterface("BsLoadPrtsetup",
							res.json.body.prtSetup, '');// 加载打印参数
				}
				// 光标定位
				this.doEditor();
				if (this.exContext.systemParams.XSBL != 100) {
					this.emr.FunActiveXInterface("BsSetDocView", 'SetZoom',
							this.exContext.systemParams.XSBL + "");
				}
				this.emr
						.FunActiveXInterface("BsSetPasteControl",
								(this.exContext.systemParams.SFJYWBKB == 1
										? '1'
										: "0"), '');
				this.Backfill("MS_BRZD");
				this.Backfill("MS_CF01");
				this.Backfill("MS_YJ01");
				this.Backfill("HER_HEALTHRECIPERECORD_MZ");
			},
			updateRef : function() {
				var r = this.emr.FunActiveXInterface("BsGetReference", '', '');
				var s = this.emr.StrReturnData;
				if (r !== 0) {
					this.alert("提示", "获取引用元素出错!");
					return;
				}
				var s = "[" + s + "]";
				var arrObj = eval(s);
				var resData = phis.script.rmi.miniJsonRequestSync({
							serviceId : "emrManageService",
							serviceAction : "loadRefItems",
							body : {
								"items" : arrObj,
								"ZYH" : this.exContext.ids.clinicId,
								"BRKS" : this.exContext.empiData.BRKS,
								"MZSY" : 1
							}
						});
				if (resData.code > 200) {
					this.alert("警告", resData.msg);
					return;
				}
				var refItems = resData.json.retItems;
				var refStr = Ext.encode(refItems);
				refStr = refStr.substring(1, refStr.length - 1);
				refStr = refStr.replace(/\"/g, "");
				this.emr.FunActiveXInterface("BsDoReference", '', refStr);
				// 病程引病历
				this.emr.FunActiveXInterface("BsGetParaRefence",
						(this.node.BLLX == 1
								? 'element..Illrc_1_' + this.BLBH
								: ''), '');
				s = this.emr.StrReturnData;
				s = "[" + s + "]";
				arrObj = eval(s);
				resData = phis.script.rmi.miniJsonRequestSync({
							serviceId : "emrManageService",
							serviceAction : "loadParaRefItems",
							body : {
								"items" : arrObj,
								"ZYH" : this.exContext.ids.clinicId
							}
						});
				if (resData.code > 200) {
					this.alert("警告", resData.msg);
					return;
				}
				for (var i = 0; i < arrObj.length; i++) {
					if (resData.json.body.refBlnrArray[i]) {
						var pfStr = Ext.encode(arrObj[i]);
						pfStr = pfStr.substring(1, pfStr.length - 1);
						pfStr = pfStr.replace(/\"/g, "");
						this.emr.FunActiveXInterface("BsDoParaRefence", pfStr,
								resData.json.body.refBlnrArray[i]);
					}
				}
			},
			// 更新引用元素
			doUpdateRef : function() {
				var blbh = this.BLBH;
				if (this.node.BLLX == 1) {
					blbh = this.getCurBlbh();
					if (!blbh)
						return;
				}
				if (!this.editing || blbh != this.BLBH) {
					MyMessageTip.msg("提示", "病历/病程不能编辑，不能进行更新引用元素操作!", true);
					return;
				}
				this.updateRef();
			},
			doRemove : function() {
				if (!this.editing) {
					MyMessageTip.msg("提示", "删除失败：当前没有可编辑的病历/病程信息!", true);
					return;
				}
				if (this.emrLoading) {
					MyMessageTip.msg("提示", "病历/病程数据载入中,不允许删除，请稍后重试", true);
					return;
				}
				if (this.node.BLLX == 1) {
					var blbh = this.getCurBlbh();
					if (!blbh)
						return;
					if (this.BLBH != blbh) {
						MyMessageTip
								.msg("提示", "删除失败：选择的病程不可编辑，请先编辑后再删除.", true);
						return;
					}
				}
				this.hideEmr();
				Ext.Msg.show({
					title : '确认删除病历/病程【' + (this.bl01.BLMC || this.BLMC) + '】?',
					msg : '当前操作只删除病历，不删除已保存的诊疗信息。是否确认删除？',
					modal : true,
					width : 300,
					buttons : Ext.MessageBox.YESNO,
					multiline : false,
					fn : function(btn, text) {
						this.showEmr();
						if (btn == "yes") {
							if (this.ifNew) {
								if (this.node.BLLX == 1) {
									if (!this.cacheBl01List
											|| this.cacheBl01List.length == 0) {
										if (this.closable) {
											this.opener.closeCurrentTab();
										} else {
											this.emr.FunActiveXInterface(
													"BsEmptyDoc", '', '');
										}
									} else {
										var nextBlbh = this.cacheBl01List[0].BLBH;
										this.editing = false;
										this.ifNew = false;
										this.doLoadBcjl(nextBlbh, true, true);
									}
								} else {
									if (this.closable) {
										this.opener.closeCurrentTab();
									} else {
										this.emr.FunActiveXInterface(
												"BsEmptyDoc", '', '');
									}
								}
								return;
							}
							var body = {
								BLLX : this.node.BLLX,
								BLBH : this.BLBH,
								JZXH : this.exContext.ids.clinicId,
								BLLB : this.node.key
							};

							if (this.emr.FunActiveXInterface(
									'BsCheckWordDataChange', '', '')
									|| this.modifySign) {
								if (!this.getEmrBlxx())
									return;
								body.Node = this.node;
								body.BL01 = this.bl01;
								body.BL02 = this.bl02;
							}
							var resData = phis.script.rmi.miniJsonRequestSync({
										serviceId : "emrManageService",
										serviceAction : "removeOmrData",
										body : body
									});
							// phis.script.rmi.jsonRequest({
							// serviceId : "emrManageService",
							// serviceAction : "removeEmrData",
							// body : body
							// }, function(code, msg, json) {
							var code = resData.code;
							var msg = resData.msg;
							var json = resData.json;
							if (code >= 300) {
								this.processReturnMsg(code, msg);
								return;
							}
							this.opener.loadEmrTreeNode();
							if (json.removeAll) {
								if (this.closable) {
									this.opener.closeCurrentTab();
									return;
								} else {
									this.emr.FunActiveXInterface("BsEmptyDoc",
											'', '');
								}
							}
							MyMessageTip.msg("提示", "删除成功!", true);
							// if(this.opener.BLSY == this.mKey){
							// this.opener.BLSY = null;
							// }
							if (this.cacheBl01List) {
								var nextBlbh = this.cacheBl01List[0].BLBH;
								if (nextBlbh == this.BLBH) {
									nextBlbh = this.cacheBl01List[1].BLBH;
								}
								this.editing = false;
								this.ifNew = false;
								this.doLoadBcjl(nextBlbh, true, true);
							}
						}
					},
					scope : this
				})
			},
			doShowEmrDetail : function() {
				var blbh = this.BLBH;
				if (this.node.BLLX == 1) {
					blbh = this.getCurBlbh();
					if (!blbh)
						return;
				}
				var moduleCfg = this.loadModuleCfg(this.refEmrDetailModule);
				var cfg = {
					showButtonOnTop : true,
					border : false,
					frame : false,
					autoLoadSchema : false,
					isCombined : true,
					exContext : {}
				};
				Ext.apply(cfg, moduleCfg);
				var cls = moduleCfg.script;
				if (!cls) {
					return;
				}
				$import(cls);
				var module = eval("new " + cls + "(cfg)");
				module.setMainApp(this.mainApp);
				module.on("close", this.showEmr, this);
				module.opener = this;
				module.exContext = this.exContext;
				module.BLBH = blbh;
				var win = module.getWin();
				this.hideEmr();
				win.setHeight(470);
				win.setWidth(650);
				win.show();
			},
			// 签名
			doSigned : function(f, e, elemName) {
				if (this.emr.FunActiveXInterface("BsCheckWordEmpty", '', '')) {
					MyMessageTip.msg("提示", "当前文档为空，不允许签名!", true);
					return;
				}
				if (!this.signByLock) {
					MyMessageTip.msg("提示", "当前病历已被锁定，不允许签名!", true);
					return;
				}
				var blbh = this.BLBH;
				this.emr.FunActiveXInterface('BsCurrentPos', '0', '');
				var curPos = this.emr.StrReturnData;
				this.curPos = curPos;
				if (!this.hasSigned) {
					if (this.node.BLLX == 1) {
						blbh = this.getCurBlbh();
						if (!blbh)
							return;
						// 判断当前是否有编辑的
						if (this.BLBH != blbh && this.editing) {
							MyMessageTip.msg("提示", "病程【"
											+ (this.bl01.BLMC || this.BLMC)
											+ "】正在编辑，请先保存该病程!", true);
							return;
						}
					}
					if (this.ifNew) {
						this.getEmrBlxx();
					}
					if (!this.checkEmrPermission('SYQX', blbh)) {
						MyMessageTip.msg("提示", "对不起，您没有权限书写签名!", true);
						return;
					}
					if (this.mainApp.uid == this.bl01.SXYS
							|| this.mainApp.uid == this.bl01.SSYS
							|| this.bl01.BLZT == 1) {
						var res = phis.script.rmi.miniJsonRequestSync({
									serviceId : "emrManageService",
									serviceAction : "loadReadRecord",
									body : {
										BLBH : isNaN(blbh) ? null : blbh
									}
								});
						if (res.code > 200) {
							MyMessageTip.msg("提示", res.msg, true);
							return;
						}
						var records = res.json.records;
						var maxQxjb = -1;
						for (var i = 0; i < records.length; i++) {
							if (records[i].SYQX == this.mainApp.YLJS) {
								MyMessageTip.msg("提示", "对不起，该医疗角色已签名不能重复签名!",
										true);
								return;
							}
							if (maxQxjb < records[i].QXJB) {
								maxQxjb = records[i].QXJB
							}
						}
						if (this.checkEmrPermission("JSJB", blbh) <= maxQxjb) {
							MyMessageTip.msg("提示", "对不起，您的医疗角色没有权限书写签名!", true);
							return;
						}
						// 完整性检测
						if (this.exContext.systemParams.QZWZXJY > 0) {
							if (this.node.BLLX == 1) {
								this.emr.FunActiveXInterface("BsCheckComplete",
										'element..Illrc_1_' + this.BLBH, '')
							} else {
								this.emr.FunActiveXInterface("BsCheckComplete",
										'', '')
							}
							this.loadEmrHT(this.BLBH);
							if (this.emr.StrReturnData) {
								this.hideEmr()
								Ext.Msg.show({
									title : '提示信息',
									msg : '当前病历存在未填写元素，是否继续签名?',
									modal : true,
									width : 300,
									buttons : Ext.MessageBox.YESNO,
									multiline : false,
									fn : function(btn, text) {
										this.showEmr();
										if (btn == "yes") {
											var result = phis.script.rmi
													.miniJsonRequestSync({
														serviceId : "emrManageService",
														serviceAction : "signed",
														body : {
															op : "create",
															elemName : elemName
														}
													});
											if (result.code > 200) {
												MyMessageTip.msg("提示",
														result.msg, true);
												return;
											}
											this.loadEmrHT(blbh);
											this.BLBH = blbh;
											this.elemName = elemName;
											this.showPswWin(function() {
												if (!this.elemName) {
													this.emr
															.FunActiveXInterface(
																	'BsCurrentPos',
																	'1',
																	this.curPos);
													this.emr
															.FunActiveXInterface(
																	'BsDocAddSignElem',
																	result.json.eleName,
																	result.json.eleValue
																			+ this.mainApp.uname);
												} else {
													this.emr
															.FunActiveXInterface(
																	'BsSetElemValue',
																	this.mainApp.uname,
																	'');
												}
												this.emr.FunActiveXInterface(
														'BsGetCurrentParakey',
														'', '');
												this.SYJL = {};
												this.SYJL.QXJB = this
														.checkEmrPermission(
																'JSJB', blbh);
												this.SYJL.BLBH = this.BLBH;
												this.SYJL.SYQX = this.mainApp.YLJS;
												this.SYJL.JGID = this.mainApp['phisApp'].deptId;
												this.SYJL.DLLJ = this.emr.StrReturnData;
												this.SYJL.QMYS = result.json.eleName;
												this.SYJL.YSMRZ = result.json.eleValue;
												this.SYJL.QMYSZ = result.json.eleValue
														+ this.mainApp.uname;
												this.SYJL.ZJBJ = this.elemName
														? 0
														: 1;
												this.hasSigned = true;
												this.doSave();
											})
										} else {
											var elements = eval("["
													+ this.emr.StrReturnData
													+ "]");
											if (elements.length > 0) {
												if (this.checkEmrPermission(
														'SXQX', blbh)) {
													this.doEditor();
												}
												this.opener.showUnSetElements(
														elements, this);
											}
										}
									},
									scope : this
								})
								return false;
							}
						}
						var result = phis.script.rmi.miniJsonRequestSync({
									serviceId : "emrManageService",
									serviceAction : "signed",
									body : {
										op : "create",
										elemName : elemName
									}
								});
						if (result.code > 200) {
							MyMessageTip.msg("提示", result.msg, true);
							return;
						}
						this.loadEmrHT(blbh);
						this.BLBH = blbh;
						this.elemName = elemName;
						this.showPswWin(function() {
									if (!this.elemName) {
										this.emr.FunActiveXInterface(
												'BsCurrentPos', '1',
												this.curPos);
										this.emr.FunActiveXInterface(
												'BsDocAddSignElem',
												result.json.eleName,
												result.json.eleValue
														+ this.mainApp.uname);
									} else {
										this.emr.FunActiveXInterface(
												'BsSetElemValue',
												this.mainApp.uname, '');
									}
									this.emr.FunActiveXInterface(
											'BsGetCurrentParakey', '', '');
									this.SYJL = {};
									this.SYJL.QXJB = this.checkEmrPermission(
											'JSJB', blbh);
									this.SYJL.BLBH = this.BLBH;
									this.SYJL.SYQX = this.mainApp.YLJS;
									this.SYJL.JGID = this.mainApp['phisApp'].deptId;
									this.SYJL.DLLJ = this.emr.StrReturnData;
									this.SYJL.QMYS = result.json.eleName;
									this.SYJL.YSMRZ = result.json.eleValue;
									this.SYJL.QMYSZ = result.json.eleValue
											+ this.mainApp.uname;
									this.SYJL.ZJBJ = this.elemName ? 0 : 1;
									this.hasSigned = true;
									this.doSave();
								})
						// this.emr.FunActiveXInterface('BsCurrentPos', 1,
						// curPos);

					} else {
						MyMessageTip.msg("提示", "对不起，您没有权限签名该病历!", true);
						return;
					}

				} else {
					MyMessageTip.msg("提示", "对不起，该医疗角色已签名不能重复签名!", true);
					return;
				}

			},
			doUnSigned : function() {
				if (this.emr.FunActiveXInterface("BsCheckWordEmpty", '', '')) {
					MyMessageTip.msg("提示", "当前文档为空，不允许签名!", true);
					return;
				}
				var blbh = this.BLBH;
				if (this.node.BLLX == 1) {
					this.emr.FunActiveXInterface('BsGetCurrentParakey', '', '');
					if (this.emr.StrReturnData.indexOf("SpacePara") >= 0) {
						MyMessageTip.msg("提示", "对不起，不能操作空段落!", true);
						return;
					}
					blbh = this.emr.StrReturnData.split("_1_")[1];
				}
				if (!this.editing || this.BLBH != blbh) {
					MyMessageTip.msg("提示", "请点击编辑后再清除签名!", true);
					return;
				}
				var curPos = this.emr.FunActiveXInterface('BsCurrentPos', '0',
						'');
				// 判断是否需要保存
				if (this.emr.FunActiveXInterface('BsCheckWordDataChange', '',
						'')
						|| this.modifySign) {
					this.hideEmr();
					Ext.Msg.confirm("提示", (this.node.BLLX == 1 ? "病程" : "病历")
									+ "【" + this.bl01.BLMC + "】已修改，是否保存？",
							function(btn) {
								this.showEmr();
								if (btn == 'yes') {
									// this.BLBH = blbh;
									if (this.doSave()) {
										this.emr.FunActiveXInterface(
												'BsCurrentPos', '1', curPos
														+ '');
										this.editing = true;
										this.doUnSigned();
									}
								}
							}, this)
					return;
				}
				var result = phis.script.rmi.miniJsonRequestSync({
							serviceId : "emrManageService",
							serviceAction : "signed",
							body : {
								BLBH : blbh,
								op : "remove"
							}
						});
				if (result.code > 200) {
					MyMessageTip.msg("提示", result.msg, true);
					return;
				}
				if (result.json.records.totalCount == 1) {
					this.hideEmr();
					Ext.Msg.confirm("提示", "确认要清除签名信息吗？", function(btn) {
								this.showEmr();
								if (btn == 'yes') {
									this
											.doRemoveSign(result.json.records.body[0]);
								}
							}, this)
				} else if (result.json.records.totalCount > 1) {
					this.showReadRecordWin();
				} else {
					MyMessageTip.msg("提示", "未找到有效的签名信息，无需取消签名!", true);
					return;
				}
			},
			doRemoveSign : function(data) {
				var sign = this.emr.FunActiveXInterface('BsDocSearchElem',
						"element.." + data.DLLJ, '#' + data.QMYS);
				if (sign < 1) {
					MyMessageTip.msg("提示", "取消签名失败，请联系管理员!", true);
					return;
				}
				var pos = this.emr.StrReturnData;
				if (data.ZJBJ) {
					var r = this.emr.FunActiveXInterface('BsDocDelElem',
							"element.." + data.DLLJ, '#' + data.QMYS);
					if (r > 0) {
						MyMessageTip.msg("提示", "取消签名失败，请联系管理员!", true);
						return;
					}
				} else {
					this.emr.FunActiveXInterface('BsSetElemValue', '{'
									+ data.QMYS + '}', pos + '');
				}
				var result = phis.script.rmi.miniJsonRequestSync({
							serviceId : "emrManageService",
							serviceAction : "removeSigned",
							body : {
								JLXH : data.JLXH,
								BLBH : data.BLBH,
								JZXH : this.exContext.ids.clinicId,
								BLMC : this.bl01.BLMC
							}
						});
				if (result.code > 200) {
					MyMessageTip.msg("提示", result.msg, true);
					return;
				}
				this.doSave(null, null, true, 'focusSave');
				MyMessageTip.msg("提示", "取消签名成功!", true);
			},
			doSaveAsFile : function() {
				this.emr.FunActiveXInterface('BsDocSaveAs', this.bl01.BLMC
								+ Date.getServerDate(), '3')
			},
			doSaveAsPersonal : function() {
				if (this.emr.FunActiveXInterface('BsCheckWordEmpty', '', '')) {
					MyMessageTip.msg("提示", "当前病历/病程为空，不允许保存为个人模板!", true);
					return;
				}
				var blbh = this.BLBH;
				if (this.node.BLLX == 1) {
					blbh = this.getCurBlbh();
					if (!blbh)
						return;
				}
				var records = this.getReadRecord(blbh);
				if (records && records.length > 0) {// 不存在审阅记录
					MyMessageTip.msg("提示", "已签名的病历/病程不允许保存为个人模板!", true);
					return;
				}
				this.hideEmr();
				Ext.Msg.confirm("提示", "是否将"
								+ (this.node.BLLX == 1 ? "病程" : "病历") + "【"
								+ this.bl01.BLMC + "】保存为个人模版？", function(btn) {
							this.showEmr();
							if (btn == 'yes') {
								this.hideEmr();
								this.opener.showEmrPersonalForm(this, blbh);
							}
						}, this)
			},
			doAddTemp : function() {
				var BLBH = this.BLBH;
				if (this.node.BLLX == 1) {
					BLBH = this.getCurBlbh();
					if (!BLBH)
						return;
				}
				var s = this.emr.FunActiveXInterface("BsGetSelectionText", '',
						'');
				var txtData = this.emr.StrReturnData;

				s = this.emr.FunActiveXInterface("BsCheckSelectEmpty", '', '');
				if (s == 1) {
					this.alert("提示", "未选择内容，请选择!");
					return;
				}

				var limitLen = this.getUserFulLimitLength();
				if (txtData.length > limitLen) {
					this.alert("提示", "选择的内容长度超过常用语限制的长度，请重选!");
					return;
				}
				this.hideEmr();
				s = this.emr.FunActiveXInterface("BsCopySelectXmlRec", '', '');
				var textData = this.emr.WordData;
				var xmlData = this.emr.WordXML;

				var ufForm = this.createModule("refUserFulForm",
						"phis.application.war.WAR/WAR/WAR91");
				ufForm.doNew();
				ufForm.KSDM = this.mainApp['phis'].departmentId;
				ufForm.opener = this;
				ufForm.txtData = textData;
				ufForm.xmlData = xmlData;
				var uff_win = ufForm.getWin();
				uff_win.show();
			},
			getUserFulLimitLength : function() {
				// var r = phis.script.rmi.miniJsonRequestSync({
				// serviceId : "",
				// serviceAction : ""
				// });
				// if (r.code > 300) {
				// this.processReturnMsg(r.code, r.msg,
				// this.getUserFulLimitLength);
				// return
				// }
				// if (r.json.body) {}
				return 1000;
			},
			showReadRecordWin : function() {

			},
			doShowModify : function() {
				var blbh = this.BLBH;
				if (this.node.BLLX == 1) {
					blbh = this.getCurBlbh();
					if (!blbh)
						return;
				}
				this.hideEmr();
				this.opener.showModifyRecord(blbh, this);
			},
			loadAppoint : function(type, data) {
				if (type == 3) {
					var node = {
						key : data.BLLB,
						BLBH : data.BLBH,
						text : "患者病史-" + data.BLMC,
						BLLX : data.BLLX,
						BLLB : data.BLLB,
						MBLB : data.MBLB,
						ReadOnly : true
					};
					this.opener.openEmrEditorModule(node);
				} else {
					this.emr.FunActiveXInterface('BsCurrentPos', '0', '');
					this.emr.FunActiveXInterface('BsDocDirectAddtext', data,
							this.emr.StrReturnData + "#");
				}
			},
			setElementValue : function(fieldValue, startIndex) {
				this.showEmr();
				var r = this.emr.FunActiveXInterface('BsSetElemValue',
						fieldValue, startIndex);
			},
			doRevRecords : function() {
				var blbh = this.BLBH;
				if (this.node.BLLX == 1) {
					blbh = this.getCurBlbh();
					if (!blbh) {
						return;
					}
				}
				var moduleCfg = this
						.loadModuleCfg("phis.application.cic.CIC/CIC/CIC38");
				var cfg = {
					showButtonOnTop : true,
					border : false,
					frame : false,
					autoLoadSchema : false,
					isCombined : true,
					exContext : this.exContext
				};
				Ext.apply(cfg, moduleCfg);
				var cls = moduleCfg.script;
				if (!cls) {
					return;
				}
				$import(cls);
				var module = eval("new " + cls + "(cfg)");
				module.setMainApp(this.mainApp);
				module.opener = this;
				module.BLBH = blbh;
				var win = module.getWin();
				win.maximize();
				win.show();
			},
			doClose : function() {
				if (!this.beforeClose())
					return;
				// 判断是否修改
				if (this.closable) {
					this.opener.closeCurrentTab();
					return;
				} else {
					var _this = this;
					var deferFunction = function() {
						_this.emr.FunActiveXInterface("BsEmptyDoc", '', '');
					}
					deferFunction.defer(1000);
				}
			},
			createMyButtons : function() {
				var actions = this.actions
				var buttons = []
				if (!actions) {
					return buttons
				}
				var f1 = 112
				for (var i = 0; i < actions.length; i++) {
					var action = actions[i];
					if (action.hide) {
						continue
					}
					var btn = {
						accessKey : f1 + i,
						text : action.name + "(F" + (i + 1) + ")",
						ref : action.ref,
						target : action.target,
						cmd : action.delegate || action.id,
						iconCls : action.iconCls || action.id,
						enableToggle : (action.toggle == "true"),
						scale : action.scale || "small",
						// ** add by yzh **
						notReadOnly : action.notReadOnly,

						script : action.script,
						handler : this.doAction,
						scope : this
					}
					buttons.push(btn)
				}
				return buttons

			},
			showPswWin : function(callback) {
				this.hideEmr();
				if (!this.form) {
					this.form = new Ext.FormPanel({
								frame : true,
								labelWidth : 75,
								labelAlign : 'top',
								defaults : {
									width : '95%'
								},
								defaultType : 'textfield',
								shadow : true,
								items : [{
											fieldLabel : '请输入密码',
											name : 'psw',
											inputType : 'password'
										}]
							})
				} else {
					var form = this.form.getForm()
					this.Field = form.findField("psw");
					this.Field.setValue();
				}
				// this.Field.setValue();
				if (!this.chiswin) {
					var win = new Ext.Window({
						layout : "form",
						title : '请输入...',
						width : 300,
						height : 130,
						resizable : true,
						modal : true,
						iconCls : 'x-logon-win',
						constrainHeader : true,
						shim : true,
						// items:this.form,
						buttonAlign : 'center',
						closable : false,
						buttons : [{
							text : '确定',
							handler : function() {
								var psw = this.Field.getValue();
								var res = phis.script.rmi.miniJsonRequestSync({
											serviceId : "emrMedicalRecordsService",
											serviceAction : "queryUser",
											uid : this.mainApp.uid,
											psw : psw
										});
								var code = res.code;
								var msg = res.msg;
								if (code >= 300) {
									this.processReturnMsg(code, msg,
											this.doSignature);
									return;
								}
								if (res.json.body) {
									Ext.MessageBox.alert('提示', res.json.body,
											function() {
												// this.psw =
												// form.findField("psw");
												this.Field.focus(true, 50);
											}, this);
									return;
								}
								this.chiswin.hide();
								this.showEmr();
								if (typeof callback == "function") {
									callback.apply(this, [])
								}
							},
							scope : this
						}, {
							text : '取消',
							handler : function() {
								this.chiswin.hide();
								this.showEmr();
							},
							scope : this
						}]
					})
					this.chiswin = win
					this.chiswin.add(this.form);
				}
				this.chiswin.show();
				var form = this.form.getForm()
				this.Field = form.findField("psw");
				this.Field.focus(false, 50);
			},
			hideEmr : function() {// 隐藏Iframe
				if (Ext.isIE) {
					var iframe = document.getElementById("emrOcxContainer_"
							+ this.mKey);
					iframe.style.display = "none";
				} else {
					var iframe = document.getElementById(this.mKey);
					iframe.height = 0;
				}
			},
			showEmr : function() {// 显示Iframe
				if (Ext.isIE) {
					var iframe = document.getElementById("emrOcxContainer_"
							+ this.mKey);
					iframe.style.display = "";
				} else {
					var iframe = document.getElementById(this.mKey);
					iframe.height = this.panel.getHeight() - 50;
				}
			}

		});