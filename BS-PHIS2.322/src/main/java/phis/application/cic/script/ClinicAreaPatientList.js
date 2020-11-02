$package("phis.application.cic.script")

$import("phis.script.SimpleList", "phis.application.cic.script.EMRView",
		"phis.application.cic.script.PEMRView")

phis.application.cic.script.ClinicAreaPatientList = function(cfg) {
	cfg.autoLoadData = false;// acitve时已经会处方查询操作
	phis.application.cic.script.ClinicAreaPatientList.superclass.constructor
			.apply(this, [cfg])
}

Ext.extend(phis.application.cic.script.ClinicAreaPatientList,
		phis.script.SimpleList, {
			doTest : function() {
				var logon = new app.desktop.plugins.LogonWin({
							forConfig : false,
							deep : false,
							uid : "5031",
							mainApp : this.mainApp
						})
				logon.getWin().show();
			},
			onReady : function() {
				phis.application.cic.script.ClinicAreaPatientList.superclass.onReady
						.call(this)
				var res = phis.script.rmi.miniJsonRequestSync({
							serviceId : "clinicManageService",
							serviceAction : "loadSystemParams",
							body : {
								privates : ['YXWGHBRJZ', "QYMZDZBL"]
							}
						});
				if (res && res.json.body) {
					if (res.json.body.YXWGHBRJZ != "1") {
						// var newBtn = this.grid.getTopToolbar().items.item(7);
						var newBtn = this.grid.getTopToolbar().find('cmd',
								'save')[0].disable();
						newBtn.hide();
					}
				}
				this.sysParams = res.json.body;
			},
			onContextMenu : function(grid, rowIndex, e) {
				if (e) {
					e.stopEvent()
				}
				if (this.disableContextMenu) {
					return
				}
				this.grid.getSelectionModel().selectRow(rowIndex);
				this.onRowClick();
				var cmenu = this.midiMenus['gridContextMenu']
				if (!cmenu) {
					var items = [];
					var actions = this.actions
					// update by caijy for no actions' list
					if (!actions || actions.length == 0) {
						return;
					}
					for (var i = 0; i < actions.length; i++) {
						var action = actions[i];
						var it = {}
						it.cmd = action.id
						it.ref = action.ref
						it.iconCls = action.iconCls || action.id
						it.script = action.script
						it.text = action.name
						it.handler = this.doAction
						it.scope = this
						items.push(it)
					}
					cmenu = new Ext.menu.Menu({
								items : items
							})
					this.midiMenus['gridContextMenu'] = cmenu
				}
				// @@ to set menuItem disable or enable according to buttons of
				// toptoolbar.
				var toolBar = this.grid.getTopToolbar();
				if (toolBar) {
					for (var i = 0; i < this.actions.length; i++) {
						var btn = toolBar.find("cmd", this.actions[i].id);
						if (!btn || btn.length == 0) {
							continue;
						}
						if (btn[0].disabled) {
							cmenu.items.itemAt(i).disable();
						} else {
							cmenu.items.itemAt(i).enable();
						}

					}
				}
				if (this.sysParams.YXWGHBRJZ != "1") {
					cmenu.items.itemAt(2).hide();
				}
				cmenu.showAt([e.getPageX() + 5, e.getPageY() + 5])
			},
			doSave : function() {
				// ymPrompt.alert({
				// message : '右下角弹出',
				// title : '右下角弹出'+new Date(),
				// winPos : 'rb',
				// showMask : false,
				// useSlide : true
				// })

				// this.bclLock("【处方录入】业务已锁定该病人<br>锁定人：张医生<br>锁定时间：2014-08-08
				// 14:32:31");

				if (!this.mainApp['phis'].reg_departmentId) {
					MyMessageTip.msg("提示", "请先设置科室信息", true);
					return;
				}
				var r = this.getSelectedRecord();
				var empiId = r.get("EMPIID");
				// var clinicId = r.get("JZHM");
				var brid = r.get("BRID");
				// 1、远程请求，判断就诊信息有效性
				phis.script.rmi.jsonRequest({
							serviceId : "clinicManageService",
							serviceAction : "saveInitClinicInfo",
							body : r.data
						}, function(code, msg, json) {
							if (code < 300) {
								if (this.sysParams.QYMZDZBL + "" == '1') {
									this.emrViewShow(r.data);
								} else {
									var clinicId = json.JZXH;
									var age = json.age;
									var m = new phis.application.cic.script.EMRView(
											{
												empiId : empiId,
												clinicId : clinicId,
												brid : brid,
												age : age
											})
									m.setMainApp(this.mainApp);
									m.on("close", function() {
												this.mainApp.locked = false;
												this.refresh();
											}, this);
									m.getWin().show()
									this.mainApp.locked = true;
								}
							} else {
								this.processReturnMsg(code, msg);
							}
						}, this)
			},
			emrViewShow : function(data) {
				// var r = this.getSelectedRecord();
				var empiId = data.EMPIID;
				// var clinicId = r.get("JZHM");
				var brid = data.BRID;
				// 1、远程请求，判断就诊信息有效性
				// alert(r.data.toSource())
				phis.script.rmi.jsonRequest({
							serviceId : "clinicManageService",
							serviceAction : "saveInitClinicInfo",
							body : data
						}, function(code, msg, json) {
							if (code < 300) {
								var clinicId = json.JZXH;// 就诊序号
								var age = json.age;
								var m = new phis.application.cic.script.PEMRView(
										{
											empiId : empiId,
											clinicId : clinicId,
											brid : brid,
											age : age,
											ghxh : data.SBXH
										})
								m.initModules = null;
								m.on("close", function() {
											this.mainApp.locked = false;
											this.refresh();
										}, this);
								var exContext = {};
								exContext.brxx = data;
								Ext.applyIf(m.exContext, exContext);
								m.setMainApp(this.mainApp);
								m.getWin().show()
								m.getWin().maximize()
							} else {
								this.processReturnMsg(code, msg);
							}
						}, this)
			},
			onDblClick : function(grid, index, e) {
				if (this.sysParams.YXWGHBRJZ != "1") {
					this.doUpdatePerson();
				} else {
					this.doSave();
				}
			},
			doNewPerson : function(item, e) {
				// 判断启用模式 1卡号,2门诊号码
				var pdms = phis.script.rmi.miniJsonRequestSync({
					serviceId : "clinicChargesProcessingService",
					serviceAction : "checkCardOrMZHM"
						// cardOrMZHM : data.cardOrMZHM
					});
				if (pdms.code > 300) {
					this.processReturnMsg(pdms.code, r.msg, this.doNewPerson);
					return;
				} else {
					if (!pdms.json.cardOrMZHM) {
						Ext.Msg.alert("提示", "该卡号门诊号码判断不存在!");
						return;
					}
				}
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
					m.on("onEmpiReturn", this.checkRecordExist, this);
					m.on("close", this.active, this);
					this.midiModules["healthRecordModule"] = m;
				}
				m.EMPIID = null;
				var win = m.getWin();
				win.setPosition(250, 100);
				win.show();
				// 1卡号
				var form = m.midiModules[m.entryName].form.getForm();
				if (pdms.json.cardOrMZHM == 1) {
					form.findField("MZHM").setDisabled(true);
				}
				// 2门诊号码
				if (pdms.json.cardOrMZHM == 2) {
					form.findField("cardNo").setValue(form.findField("MZHM")
							.getValue());
					form.findField("personName").focus(true, 200);
				}
			},
			checkRecordExist : function(data) {
				if (this.sysParams.YXWGHBRJZ != "1") {
					return;
				}
				if (!this.mainApp['phis'].reg_departmentId) {
					MyMessageTip.msg("提示", "请先设置科室信息", true);
					return;
				}
				phis.script.rmi.jsonRequest({
							serviceId : "clinicManageService",
							serviceAction : "saveInitClinicInfo",
							body : {
								EMPIID : data.empiId,
								BRXZ : data.BRXZ
							}
						}, function(code, msg, json) {
							if (code < 300) {
								this.refresh();
								if (this.sysParams.QYMZDZBL + "" == '1') {
									data.EMPIID = json.empiId;
									data.BRID = json.BRID;
									data.SBXH = null;
									this.emrViewShow(data);
								} else {
									var clinicId = json.JZXH;
									var m = new phis.application.cic.script.EMRView(
											{
												empiId : json.empiId,
												clinicId : clinicId,
												brid : json.BRID
											})
									m.setMainApp(this.mainApp);
									m.getWin().show()
								}
							} else {
								if (msg == '病人未挂号!') {
									this.refresh();
								} else {
									this.processReturnMsg(code, msg);
								}
							}
						}, this);
			},
			doUpdatePerson : function() {
				// 判断启用模式 1卡号,2门诊号码
				var pdms = phis.script.rmi.miniJsonRequestSync({
					serviceId : "clinicChargesProcessingService",
					serviceAction : "checkCardOrMZHM"
						// cardOrMZHM : data.cardOrMZHM
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
				var r = this.getSelectedRecord();
				if (r == null) {
					return;
				}
				var empiId = r.get("EMPIID");
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
					m.on("onEmpiReturn", this.checkRecordExist, this);
					m.on("close", this.active, this);
					this.midiModules["healthRecordModule"] = m;
				}
				m.EMPIID = empiId;
				var win = m.getWin();
				win.setPosition(250, 100);
				win.show();
				// 1卡号
				var form = m.midiModules[m.entryName].form.getForm();
				if (pdms.json.cardOrMZHM == 1) {
					form.findField("MZHM").setDisabled(true);
				}
				// 2门诊号码
				if (pdms.json.cardOrMZHM == 2) {
					form.findField("cardNo").setValue(form.findField("MZHM")
							.getValue());
					form.findField("personName").focus(true, 202);
				}
			},
			onStoreBeforeLoad : function() {
				phis.application.cic.script.ClinicAreaPatientList.superclass.onStoreBeforeLoad
						.call(this);
				this.requestData.serviceAction = "queryList"
			},
			doMpi : function() {
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : "empiService",
							serviceAction : "testMpi"
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.doInvalid);
				} else {
					alert("测试完成！")
				}
			},
			// 打开患者健康档案信息
			doDyhzxx : function() {
				var r = this.getSelectedRecord();
				var body = {};
				body["jzksdm"] = this.mainApp['phis'].reg_departmentId;
				body["empiId"] = r.get("EMPIID");
				body["personName"] = r.get("BRXM");
				// 打开前判断是否有患者信息
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : "interfaceForWDService",
							serviceAction : "validateExistQueryInfo",
							body : body
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg);
					return;
				}
				var csxx = ret.json.body;// 后台返回的参数信息
				// var csxx = {
				// jgid : '491203063360121',
				// kh : '102828372',
				// klx : 2,
				// xm : '张三',
				// jzksdm : '015',
				// agentid : '1010001',
				// ip : '10.1.44.12',
				// mac : '20-7C-8F-73-FD-10'
				// };
				var myForm = document.createElement("form");
				myForm.method = "post";
				myForm.action = "http://10.21.96.133/query/outlink/outQueryAction.action";
				var myInput = document.createElement("input");
				myInput.setAttribute("name", "yljgdm");
				myInput.setAttribute("value", csxx.jgid);
				myInput.setAttribute("hidden", true);
				myForm.appendChild(myInput);
				var myInput2 = document.createElement("input");
				myInput2.setAttribute("name", "daszjgdm");
				myInput2.setAttribute("value", csxx.jgid);
				myInput2.setAttribute("hidden", true);
				myForm.appendChild(myInput2);
				var myInput3 = document.createElement("input");
				myInput3.setAttribute("name", "kh");
				myInput3.setAttribute("value", csxx.kh);
				myInput3.setAttribute("hidden", true);
				myForm.appendChild(myInput3);
				var myInput4 = document.createElement("input");
				myInput4.setAttribute("name", "ktype");
				myInput4.setAttribute("value", csxx.klx);
				myInput4.setAttribute("hidden", true);
				myForm.appendChild(myInput4);
				var myInput5 = document.createElement("input");
				myInput5.setAttribute("name", "xm");
				myInput5.setAttribute("value", csxx.xm);
				myInput5.setAttribute("hidden", true);
				myForm.appendChild(myInput5);
				var myInput6 = document.createElement("input");
				myInput6.setAttribute("name", "jzksdm");
				myInput6.setAttribute("value", csxx.jzksdm);
				myInput6.setAttribute("hidden", true);
				myForm.appendChild(myInput6);
				var myInput7 = document.createElement("input");
				myInput7.setAttribute("name", "agentid");
				myInput7.setAttribute("value", csxx.agentid);
				myInput7.setAttribute("hidden", true);
				myForm.appendChild(myInput7);
				var myInput8 = document.createElement("input");
				myInput8.setAttribute("name", "agentip");
				myInput8.setAttribute("value", csxx.ip);
				myInput8.setAttribute("hidden", true);
				myForm.appendChild(myInput8);
				var myInput9 = document.createElement("input");
				myInput9.setAttribute("name", "agentmac");
				myInput9.setAttribute("value", csxx.mac);
				myInput9.setAttribute("hidden", true);
				myForm.appendChild(myInput9);
				var win = window.open("", "newwin");
				win.document.body.appendChild(myForm);
				myForm.submit();
			},
			active : function() {
				try {
					this.refresh();
				} catch (e) {
				}
				// phis.application.cic.script.ClinicAreaPatientList.superclass.active
				// .call(this)
			},
			// add by caijy for 重写为了防止查询框选择后导致就诊按钮出来,详见BUG1662
			onCndFieldSelect : function(item, record, e) {
				var tbar = this.grid.getTopToolbar()
				var tbarItems = tbar.items.items
				var itid = record.data.value
				var items = this.schema.items
				var it
				for (var i = 0; i < items.length; i++) {
					if (items[i].id == itid) {
						it = items[i]
						break
					}
				}
				var field = this.cndField
				field.hide()
				// field.destroy()
				var f = this.midiComponents[it.id]
				if (!f) {
					if (it.dic) {
						it.dic.src = this.entryName + "." + it.id
						it.dic.defaultValue = it.defaultValue
						it.dic.width = this.cndFieldWidth || 200
						f = this.createDicField(it.dic)
					} else {
						f = this.createNormalField(it)
					}
					this.doAfterCreateQueryField(f, it);
					f.on("specialkey", this.onQueryFieldEnter, this)
					this.midiComponents[it.id] = f
				} else {
					f.show()
					// f.rendered = false
				}
				tbarItems[2] = f
				tbar.doLayout()
				this.cndField = f
				if (this.sysParams.YXWGHBRJZ != "1") {
					var newBtn = this.grid.getTopToolbar().find('cmd', 'save')[0]
							.disable();
					// var newBtn = this.grid.getTopToolbar().items.item(7);
					newBtn.hide();
				}
			},
			getCndBar : function(items) {
				var fields = [];
				if (!this.enableCnd) {
					return []
				}
				var selected = null;
				var defaultItem = null;
				for (var i = 0; i < items.length; i++) {
					var it = items[i]
					if (!it.queryable || it.queryable == 'false') {
						continue
					}
					if (it.selected == "true") {
						selected = it.id;
						defaultItem = it;
					}
					fields.push({
								// change "i" to "it.id"
								value : it.id,
								text : it.alias
							})
				}
				if (fields.length == 0) {
					return [];
				}
				var store = new Ext.data.JsonStore({
							fields : ['value', 'text'],
							data : fields
						});
				var combox = null;
				if (fields.length > 1) {
					combox = new Ext.form.ComboBox({
								store : store,
								valueField : "value",
								displayField : "text",
								value : selected,
								mode : 'local',
								triggerAction : 'all',
								emptyText : '选择查询字段',
								selectOnFocus : true,
								width : 100
							});
					combox.on("select", this.onCndFieldSelect, this)
					this.cndFldCombox = combox
				} else {
					combox = new Ext.form.Label({
								text : fields[0].text
							});
					this.cndFldCombox = new Ext.form.Hidden({
								value : fields[0].value
							});
				}

				var cndField;
				if (defaultItem) {
					if (defaultItem.dic) {
						defaultItem.dic.src = this.entryName + "." + it.id
						defaultItem.dic.defaultValue = defaultItem.defaultValue
						defaultItem.dic.width = 150
						cndField = this.createDicField(defaultItem.dic)
					} else {
						cndField = this.createNormalField(defaultItem)
					}
				} else {
					cndField = new Ext.form.TextField({
								width : 150,
								selectOnFocus : true,
								name : "dftcndfld"
							})
				}
				this.cndField = cndField
				cndField.on("specialkey", this.onQueryFieldEnter, this)
				var queryBtn = new Ext.Toolbar.Button({
					text : '',
					iconCls : "query",
					notReadOnly : true
						// ** add by yzh **
						// menu : new Ext.menu.Menu({
						// items : {
						// text : "高级查询",
						// iconCls : "common_query",
						// handler : this.doAdvancedQuery,
						// scope : this
						// }
						// })
					})
				this.queryBtn = queryBtn
				queryBtn.on("click", this.doCndQuery, this);
				return [combox, '-', cndField, '-', queryBtn]
			}
		})
