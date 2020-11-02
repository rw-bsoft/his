$import('phis.application.cic.script.InjuryReportCard');
/**
 * 简单诊断录入
 * 
 * @type
 */
var diaginput_ctx = null;
phis.application.cic.script.ClinicDiagnosisEasyInputModule = {
	renderDiagInput : function(records) {
		if (records && records.length > 0) {
			for (var i = 0; i < records.length; i++) {
				this.createRemoteDicField(records[i]);
				this.createDicField(records[i]);
			}
		}

		if (document.getElementById("DIV_ZDLR_NEW")
				&& !document.getElementById("DIV_ZDLR_NEW").innerHTML) {
			this.createRemoteDicField({
						JLBH : 'NEW'
					});
			this.createDicField({
						JLBH : 'NEW'
					})
		}
		// 增加事件监控
		diaginput_ctx = this;
		var addImgs = document.getElementsByName("addDiag");
		for (var i = 0; i < addImgs.length; i++) {
			var img = addImgs[i];
			img.removeEventListener("click", diaginput_ctx.onAddClick)
			img.addEventListener("click", diaginput_ctx.onAddClick);
		}
		var delImgs = document.getElementsByName("deleteDiag");
		for (var i = 0; i < delImgs.length; i++) {
			var img = delImgs[i];
			img.removeEventListener("click", diaginput_ctx.onDelClick)
			img.addEventListener("click", diaginput_ctx.onDelClick);
		}
	},
	onAddClick : function() {
		if (diaginput_ctx.diagnosisChange) {
			diaginput_ctx.doSaveDiagnosis(true);
		} else {
			if (!document.getElementById("ZDLR_NEW")) {
				diaginput_ctx.doNewDiag();
			}
		}
	},
	doNewDiag : function() {
		var tab = document.getElementById("diagnosisTable").tBodies.item(0);
		// 添加行
		var newTR = tab.insertRow(tab.rows.length);
		var num = newTR.rowIndex;
		if (num >5) {
			MyMessageTip.msg('提示', '诊断最多只能录入5条!', true);
			return;
		}
		var imageUrl = '&nbsp;&nbsp;<img name="deleteDiag" id="diag_NEW" src=\''
				+ ClassLoader.appRootOffsetPath
				+ 'resources/phis/resources/css/app/desktop/images/shared/icons/fam/delete.gif\' style="cursor:pointer;" title=\'删除\' />';
		var html = "<td >"
				+ (num + 1)
				+ "</td><td><div id='DIV_ZDLR_NEW'></div></td><td><td><div id='DIV_FZBZ_NEW'></div></td><td>"
				+ imageUrl + "</td>";

		newTR.innerHTML = html;
		this.renderDiagInput();
	},
	onDelClick : function(e) {
		var id = e.target.id.split("_")[1]; // 主键,NEW为新建
		var d = document.getElementById("ZDLR_" + id);
		var obj = Ext.getCmp("ZDLR_" + id);
		if (!id || id.indexOf("NEW") >= 0) {
			if (!d.value) {
				return;
			}
		}
		var r = {};
		Ext.apply(r.data = d.zdxx);
		Ext.Msg.show({
			title : '确认删除诊断[' + d.value + ']吗?',
			msg : '删除操作将无法恢复，是否继续?',
			modal : true,
			width : 300,
			buttons : Ext.MessageBox.OKCANCEL,
			multiline : false,
			fn : function(btn, text) {
				if (btn == "ok") {
					if (id.indexOf("NEW") >= 0) {
						d.value = "";
						delete d.zdxx;
						obj.setValue("");
						obj.focus(100);
					} else {
						phis.script.rmi.jsonRequest({
									serviceId : "clinicDiagnossisService",
									serviceAction : "removeDiagnossis",
									body : {
										"jlbh" : id
									}
								}, function(code, msg, json) {
									if (code > 300) {
										this.processReturnMsg(code, msg)
										return
									}
									
									if(r.data.JLBH == this.CRBBGK_JLBH){//add by lizhi 2018-01-30传染病报告卡是否需填写
										this.needSaveCrb = false;
									}
									if (!this.SFQYGWXT) {
										var publicParam = {
											"commons" : ['SFQYGWXT']
										}
										this.SFQYGWXT = this
												.loadSystemParams(publicParam).SFQYGWXT;
										this.SFQYGWXT = true;
									}
									if (this.SFQYGWXT == '1'
											&& this.mainApp.chisActive) {
										// 如果要删除掉的疾病存在疾病类别
										if (r.data.JBPB) {
											var jblbs = {
												'01' : '0202',
												'02' : '0203',
												'09' : '0298',
												'10' : '0204',
												'11' : '0205',
												'12' : '0206',
												'13' : '0207',
												'14' : '0208',
												'15' : '0209',
												'16' : '0210',
												'17' : '0211',
												'18' : '0212',
												'19' : '0213',
												'20' : '0214'
											}
											var brempiid = this.exContext.empiData.empiId;
											var nowDate = Date.getServerDate();

											var delRecords = [];

											// 疾病判别存在多选的情况 如'01,02','高血压,糖尿病'
											var rjbbms = r.data.JBPB.split(',');
											var rjbmcs = r.data.JBPB_text
													.split(',');

											// 循环所有记录查看是否还存在此种疾病，如果过存在则不删除
											var zdlr_list = document
													.getElementsByName("CIC_ZDLR");
											for (var k = 0; k < rjbbms.length; k++) {
												var flag = true;
												for (var i = 0; i < zdlr_list.length; i++) {
													var id = zdlr_list[i].id
															.split("_")[1];
													var d = document
															.getElementById("ZDLR_"
																	+ id);
													if (!d)
														continue;
													var grecord = d.zdxx || {};
													if (grecord.JLBH
															&& grecord.JLBH != r.data.JLBH) {

														// 疾病判别存在多选的情况
														// 如'01,02','高血压,糖尿病'
														var jbbms = grecord.JBPB
																.split(',');
														var jbmcs = grecord.JBPB_text
																.split(',');
														for (var j = 0; j < jbbms.length; j++) {
															if (rjbbms[k] == jbbms[j]
																	&& rjbmcs[k] == jbmcs[j]) {
																flag = false;
															}

														}
													}
												}
												if (flag) {
													var gmywlb = '0299';
													var gmywlbtext = '其他';
													if (jblbs
															.hasOwnProperty(rjbbms[k]))
														gmywlb = jblbs[rjbbms[k]];
													if (rjbmcs[k]) {
														gmywlbtext = rjbmcs[k];
													}
													var delRecord = {
														empiId : brempiid,
														pastHisTypeCode : '02',
														ysid : this.mainApp.uid,
														diseaseCode : gmywlb,
														diseaseText : gmywlbtext
													}
													delRecords.push(delRecord);
												}
											}
											if (delRecords.length > 0) {
												// 删除本人当天操作的个人既往史

												var comreq1 = util.rmi
														.miniJsonRequestSync({
															serviceId : "chis.CommonService",
															serviceAction : "delPastHistory",
															body : {
																empiId : brempiid,
																record : delRecords
															}
														});
												if (comreq1.code != 200) {
													this.processReturnMsg(
															comreq1.code,
															comreq1.msg);
													return;
												} else {
												}

											}

										}
									}
									// 与公卫业务联动结束
									this.initClinicRecord("2"); // 刷新诊断页面
								}, this)// jsonRequest
					}
					this.emrview.refreshEMRNavTree();
				}
			},
			scope : diaginput_ctx
		});
	},
	setBackInfo : function(obj, record) {

		var zdlr_list = document.getElementsByName("CIC_ZDLR");
		// var fzbz_list = document.getElementsByName("CIC_FZBZ");
		for (var i = 0; i < zdlr_list.length; i++) {
			var o = {};
			var id = zdlr_list[i].id.split("_")[1];
			var d = document.getElementById("ZDLR_" + id);
			if (d.value) {
				if (d.zdxx) {
					if (obj.id != zdlr_list[i].id && record.get("JBXH")
							&& record.get("JBXH") == d.zdxx['ZDXH']) {
						MyMessageTip.msg("提示", "\"" + record.get("MSZD")
										+ "\"已存在，请勿重复录入！", true);
						return;
					}
				}
			}
		}
		var o = {};
		o['ZDXH'] = record.data['JBXH'];
		o['ZDMC'] = record.data['MSZD'];
		o['ICD10'] = record.data['JBBM'];
		var JBPB = record.data['JBPB'];
		o['JBPB'] = JBPB || "";
		obj.el.dom.zdxx = o;
		obj.setValue(record.get("MSZD"));
		obj.collapse();
		obj.triggerBlur();
		var id = obj.id.split("_")[1];
		Ext.get("FZBZ_" + id).focus(100);
		var jbbgk = record.get('JBBGK');
		if (jbbgk == '09' && phis.application.cic.script.InjuryReportCard.can(this.exContext.ids.empiId,record.get('MSZD'))) {// 伤害报卡
			Ext.Msg.show({
				title : '请确认',
				msg : '这个诊断是伤病诊断，需要填写伤病报告卡，是否继续?',
				modal : true,
				width : 300,
				buttons : Ext.MessageBox.OKCANCEL,
				multiline : false,
				fn : function(btn, text) {
					if (btn == "ok") {
						var report = new phis.application.cic.script.InjuryReportCard(
								{
									name : '伤病报告卡录入',
									width : 1000,
									height : 560,
									empiid : this.exContext.ids.empiId,
									opener : this,
									jbxh:record.get('MSZD')
								});
						report.on('cancel', function() {
									delete obj.el.dom.zdxx;
									obj.setValue("");
									obj.focus(100);
								}, this);
						report.getWin().show();
						report.getWin().center();
					} else {
						delete obj.el.dom.zdxx;
						obj.setValue("");
						obj.focus(100);
					}
				},
				scope : this
			})
		}
		if (!this.CDMZKFSJD) {
			var CDMZKFSJD = this.loadSystemParams({
						privates : ['CDMZKFSJD']
					}).CDMZKFSJD.split(",");
			this.CDMZKFSJD = {
				fromMouth : parseInt(CDMZKFSJD[0]),
				toMouth : parseInt(CDMZKFSJD[1])
			}
			var CDMZKS = this.loadSystemParams({
						privates : ['CDMZKS']
					}).CDMZKS;
			this.CDMZKFSJD["CDMZKS"] = CDMZKS;
		}
		var mouth = parseInt(this.mainApp.serverDate.substring(5, 7));
		var cdmz = record.get('CDMZZD');
		if (this.mainApp.reg_departmentId != this.CDMZKFSJD.CDMZKS && cdmz == 1
				&& mouth >= this.CDMZKFSJD.fromMouth
				&& mouth <= this.CDMZKFSJD.toMouth) {// 伤害报卡
			Ext.Msg.show({
						title : '系统提示',
						msg : '该诊断只能到肠道门诊开',
						buttons : Ext.Msg.OK,
						fn : function() {
							delete obj.el.dom.zdxx;
							obj.setValue("");
							obj.focus(100);
						},
						closable : false,
						scope : this
					});
		}
	},

	doSaveDiagnosis : function(addNewDiag) {
		// 判断诊断数据
		var data = [];
		var zdlr_list = document.getElementsByName("CIC_ZDLR");
		// var fzbz_list = document.getElementsByName("CIC_FZBZ");
		this.crbFlag = 1;//add by lizhi 2018-01-17传染病识别
		this.icd10 = null;//add by lizhi 2018-01-31 传染病诊断icd10码
		for (var i = 0; i < zdlr_list.length; i++) {
			var o = {};
			var id = zdlr_list[i].id.split("_")[1];
			if (id.indexOf("NEW") >= 0) {
				o['FZBZ'] = Ext.getCmp("FZBZ_" + id).getValue() || 0;
				o['ZDLB'] = 1;
				o['CFLX'] = 1;
				o['ZDYS'] = this.mainApp.uid;
				o['FBRQ'] = Date.getServerDate();
			} else {
				o['JLBH'] = id;
				o['FZBZ'] = Ext.getCmp("FZBZ_" + id).getValue() || 0;
			}
			var d = document.getElementById("ZDLR_" + id);
			if (d.value) {
				if (d.zdxx) {
					o['ZDXH'] = d.zdxx['ZDXH'];
					o['ZDMC'] = d.zdxx['ZDMC'];
					o['ICD10'] = d.zdxx['ICD10'];
					o['JBPB'] = d.zdxx['JBPB'];
				}
				o['DEEP'] = 0;
				o['ZDSJ'] = d.zdxx['ZDSJ'] || Date.getServerDateTime();
				if(o['JBPB'] == '09'){//add by lizhi 2018-01-17传染病识别
					this.crbFlag = 2;
					this.icd10 = o['ICD10'];
				}
				data.push(o);
			}
		}
		if (!this.diagnosisChange){
			if(this.CRBBGK_JLBH && this.needSaveCrb){
				this.CRBBGK(this.CRBBGK_JLBH,this.icd10);
			}
			return;
		}
		if (data.length == 0) {
			return;
		}

		// update by caijy for诊断只能录入3条,需求by 刘文佳
		if (data.length > 5) {
			MyMessageTip.msg('提示', '诊断最多只能录入5条!', true);
			return;
		}
		this.CRBBGK_JLBH = null;
		this.needSaveCrb = false;
		var jzxh = this.exContext.ids.clinicId;
		var brid = this.exContext.ids.brid;
		phis.script.rmi.jsonRequest({
					serviceId : "clinicDiagnossisService",
					serviceAction : "saveDiagnossis",
					body : {
						"jzxh" : jzxh,
						"brid" : brid,
						"dignosisList" : data
					}
				}, function(code, msg, json) {
					if (code > 300) {
						this.processReturnMsg(code, msg)
						return false;
					}
					this.diagnosisChange = false;
					if (addNewDiag) {
						this.addNewDiagSign = true // 此处通过标志位控制新增
					}
					this.initClinicRecord("2");
					if (json.JBPB) {
						var JBPB = json.JBPB.JBPB;
						// 公卫业务
						this.CHISJDRWTS(JBPB);
						debugger;
						if (this.crbFlag == 2 && json.JBPB.MS_BRZD_JLBH!="") {//add by lizhi 2018-01-17传染病识别
							this.CRBBGK_JLBH = json.JBPB.MS_BRZD_JLBH;
							this.CRBBGK(json.JBPB.MS_BRZD_JLBH,this.icd10);
						}
					}
					this.fireEvent("onDiagnosisSave");
					this.emrview.refreshEMRNavTree();
				}, this)// jsonRequest

		// 与公卫业务联动开始，疾病添加到个人既往史中
		// 获取 是否启用公卫系统的参数
		if (!this.SFQYGWXT) {
			var publicParam = {
				"commons" : ['SFQYGWXT']
			}
			this.SFQYGWXT = this.loadSystemParams(publicParam).SFQYGWXT;
		}
		// 如果存在公卫系统，将疾病保存到公卫个人既往史中
		if (this.SFQYGWXT == '1' && this.mainApp.chisActive) {
			var jblbs = {
				'01' : '0202',
				'02' : '0203',
				'09' : '0298',
				'10' : '0204',
				'11' : '0205',
				'12' : '0206',
				'13' : '0207',
				'14' : '0208',
				'15' : '0209',
				'16' : '0210',
				'17' : '0211',
				'18' : '0212',
				'19' : '0213',
				'20' : '0214'
			}
			var brempiid = this.exContext.empiData.empiId;
			var nowDate = (new Date()).format('Y-m-d');
			var jbsRecords = [];// 需要保存的疾病史记录集
			// var jblbHistory={};
			var jbmcHistory = {};
			// 每条记录循环
			//var ssize = store.getCount();
			for (var i = 0; i < data.length; i++) {
				var grecord = data[i];
				// 如果存在疾病判别 (过滤掉没有选则疾病类别的疾病)
				if (grecord.JBPB) {
					// 疾病判别存在多选的情况 如'01,02','高血压,糖尿病'
					var jbbms = grecord.JBPB.split(',');
					var jbmcs = grecord.JBPB_text.split(',');

					for (var j = 0; j < jbbms.length; j++) {
						var gmywlb = '0299';
						var gmywlbtext = '其他';
						if (jblbs.hasOwnProperty(jbbms[j]))
							gmywlb = jblbs[jbbms[j]];
						if (jbmcs[j]) {
							gmywlbtext = jbmcs[j];
						}
						var jbsRecord = {
							empiId : brempiid,
							pastHisTypeCode_text : '疾病史',
							pastHisTypeCode : '02',
							methodsCode : '',
							protect : '',
							diseaseCode : gmywlb,
							diseaseText : gmywlbtext,
							vestingCode : '',
							startDate : '',
							endDate : '',
							confirmDate : nowDate,
							recordUnit : this.mainApp.deptId,
							recordUser : this.mainApp.uid,
							recordDate : nowDate,
							lastModifyUser : this.mainApp.uid,
							lastModifyUnit : this.mainApp.deptId,
							lastModifyDate : nowDate
						}
						if (!jbmcHistory.hasOwnProperty(gmywlbtext)) {
							jbsRecords.push(jbsRecord);
							jbmcHistory[gmywlbtext] = gmywlbtext;
						}
					}
				}

			}

			if (jbsRecords.length > 0) {
				var comreq1 = util.rmi.miniJsonRequestSync({
							serviceId : "chis.healthRecordService",
							serviceAction : "savePastHistoryHis",
							schema : 'chis.application.hr.schemas.EHR_PastHistory',
							op : 'create',
							body : {
								empiId : brempiid,
								record : jbsRecords,
								delPastId : []
							}
						});
				if (comreq1.code != 200) {
					this.processReturnMsg(comreq1.code, comreq1.msg);
					return;
				} else {
				}

			}

		}
		// 与公卫业务联动结束

		return true;
	},
	CRBBGK : function(MS_BRZD_JLBH,ICD10){
		var param=phis.script.common.loadSystemParams({"privates" : ['CRBBGK']}).CRBBGK;
		if(param!="1"){
			return;
		}
		var _this = this;
		this.exContext.args.MS_BRZD_JLBH = MS_BRZD_JLBH || '';
		this.exContext.args.ICD10 = ICD10 || '';
		//根据门诊诊断记录编号查询是否已保存传染病报告卡
		var resData = phis.script.rmi.miniJsonRequestSync({
					serviceId : "clinicManageService",
					serviceAction : "queryIdrReport",
					body : {
						"MS_BRZD_JLBH" : MS_BRZD_JLBH,
						"EMPIID" : this.exContext.empiData.empiId,
						"ICD10" : ICD10
					}
				});
		if(resData.code > 200){
			return;
		}
		if(resData.json.result){
			var isSaved = resData.json.result.isSaved;
			if(typeof(isSaved)!="undefined" && !isSaved){
				_this.needSaveCrb = true;
				Ext.Msg.show({
					title : '确认',
					msg : '该诊断为传染病诊断，是否填写传染病报告卡？',
					modal : false,
					width : 300,
					buttons : Ext.MessageBox.YESNO,
					multiline : false,
					fn : function(btn, text) {
						if (btn == "yes") {
							_this.doPopBgkWin();
						}
					},
					scope : this
				});
			}
		}
	},
	doPopBgkWin : function(){
		this.midiModules["crbBgkModule"]=null;
		var crbBgkmodule= this.createModule("crbBgkModule", "phis.application.cic.CIC/CIC/CIC0403");
		this.crbModule = crbBgkmodule;
		Ext.apply(this.crbModule.exContext, this.exContext);
		crbBgkmodule.opener = this;
		var win = crbBgkmodule.getWin();
		win.add(crbBgkmodule.initPanel());
		win.setWidth(1000);
		win.setHeight(550);
		win.show();
		win.center();
	},
	CHISJDRWTS : function(JBPB) {// 社区建档案任务提示,执行
		if (this.exContext.ids.manaUnitId) {
			if (this.exContext.ids.manaUnitId.substring(0, 9) != '310112051') {// 非古美中心不提示
				return;
			}
		}

		var GXY = false;
		var TNB = false;
		var THQ = false;
		if (JBPB.indexOf('01') >= 0) {
			GXY = true;
			// MyMessageTip.msg("提示", "请完善高血压相关业务", true);
		}
		if (JBPB.indexOf('02') >= 0) {
			TNB = true;
			// MyMessageTip.msg("提示", "请完善糖尿病相关业务", true);
		}
		var types = ["03", "04", "05", "06", "07", "08"];
		var isTHQ = false;
		for (var ti = 0, tiLen = types.length; ti < tiLen; ti++) {
			var bm = types[ti];
			if (JBPB.indexOf(bm) >= 0) {
				THQ = true;
				isTHQ = true;
				break;
			}
		}
		if (isTHQ) {
			// MyMessageTip.msg("提示", "请完善 肿瘤问卷 业务", true);
		}
		// if (json.JBPB.JBBGK) {
		// if (json.JBPB.JBBGK.indexOf("06") >= 0) {
		// MyMessageTip.msg("提示", "请完善疾病报告卡相关业务",
		// true);
		// }
		// }
		if (!this.SFQYGWXT) {
			var publicParam = {
				"commons" : ['SFQYGWXT']
			}
			this.SFQYGWXT = this.loadSystemParams(publicParam).SFQYGWXT;
		}
		if (this.mainApp.chisActive && this.SFQYGWXT) {
			// 判断病人诊断，是否要创建高血压，糖尿病档案及做肿瘤问题
			util.rmi.miniJsonRequestAsync({
						serviceId : "chis.chisRecordFilter",
						serviceAction : "getCDMsgInfo",
						method : "execute",
						body : {
							empiId : this.exContext.empiData.empiId,
							JZXH : '' + this.exContext.ids.clinicId,
							GXY : GXY,
							TNB : TNB,
							THQ : THQ
						}
					}, function(code, msg, json) {
						if (code < 300) {
							var body = json.body;
							if (body) {
								this.openMyNodes = body.openNodes || '';
								var isOpenTHQ = body.isOpenTHQ || false;
								this.empiId = body.empiId;
								if (isOpenTHQ) {
									var CHISTHQModule = this
											.createModule("CHIS_THQ_Win",
													"chis.application.diseasemanage.DISEASEMANAGE/TR/THQM");
									if (!this.exContext.args) {
										this.exContext.args = {};
									}
									this.exContext.args.masterplateTypes = body.mtList;
									this.exContext.args.empiId = this.exContext.ids.empiId;
									this.exContext.args.MS_BRZD_JLBH = this.JLBH
											|| "";
									this.exContext.args.JZXH = this.exContext.ids.clinicId
											|| "";
									Ext.apply(CHISTHQModule.exContext,
											this.exContext);
									CHISTHQModule.initPanel();
									var CHIS_THQ_Win = CHISTHQModule.getWin();
									CHISTHQModule.on("chisSave", 
										function() {
											this.emrview.refreshEMRNavTree();
										}
									, this);
									CHIS_THQ_Win.show();
								}
								var rsMsgList = body.rsMsgList;
								var len = rsMsgList.length;
								if (len > 0) {
									var ts = "";
									ts = "<table id='chisJDTS_Table'><tbody style='font-size:15px;font-weight:bold;color:#ff0000;'>";
									for (var i = 0; i < len; i++) {
										ts += "<tr><td>" + rsMsgList[i]
												+ "</td></tr>";
									}
									ts += "</tbody></table>";
									ymPrompt.confirmInfo({
												message : ts,
												title : "您有待办社区业务",
												winPos : 'c',
												okTxt : "执行",
												cancelTxt : "退出",
												scope : this,
												hashcode : "jcbryztztx",
												showMask : false,
												useSlide : true,
												width : 300,
												height : 160,
												handler : this.CHISJDTSMyBtnClick,
												autoClose : false
											});
								}
							}
						} else {
							this.processReturnMsg(code, msg);
						}
					}, this);
		}
	},
	CHISJDTSMyBtnClick : function(sign){
		if (sign == "cancel" || sign == "close") {
			ymPrompt.doHandler('doClose', true);
		} else if (sign == "ok") {
			this.onOpenPagesOfCHIS(this.openMyNodes,this.empiId);
		}
	},
	createRemoteDicField : function(r) {
		var mds_reader = this.getRemoteDicReader();
		// store远程url
		// var url = "http://127.0.0.1:8080/BS-PHIS/" + this.remoteUrl
		var url = ClassLoader.serverAppUrl || "";
		var comboJsonData = {
			serviceId : "phis.searchService",
			serviceAction : "loadDicData",
			method : "execute",
			className : "MedicalDiagnosisZdlr",
			ZXLB : "1"
			// ,pageSize : this.pageSize || 25,
			// pageNo : 1
		}
		var proxy = new Ext.data.HttpProxy({
					url : url + '*.jsonRequest',
					method : 'POST',
					jsonData : comboJsonData
				});
		var mdsstore = new Ext.data.Store({
					proxy : proxy,
					reader : mds_reader
				});
		proxy.on("loadexception", function(proxy, o, response, arg, e) {
					if (response.status == 200) {
						var json = eval("(" + response.responseText + ")")
						if (json) {
							var code = json["code"]
							var msg = json["msg"]
							MyMessageTip.msg("提示", msg, true)
						}
					} else {
						MyMessageTip.msg("提示", "貌似网络不是很给力~请重新尝试!", true)
					}
				}, this)
		// this.remoteDicStore = mdsstore;
		Ext.apply(mdsstore.baseParams, this.queryParams);
		var resultTpl = new Ext.XTemplate(
				'<tpl for=".">',
				'<div class="search-item">',
				'<table cellpadding="0" cellspacing="0" border="0" class="search-item-table">',
				'<tr>' + this.remoteTpl + '</tr>', '</table>', '</div>',
				'</tpl>');
		var _ctx = this;
		var remoteField = new Ext.form.ComboBox({
					id : ("ZDLR_" + r.JLBH),
					name : "CIC_ZDLR",
					width : 140,
					selectedClass : this.selectedClass||'x-remoteField-selected',
					store : mdsstore,
					selectOnFocus : true,
					typeAhead : false,
					loadingText : '搜索中...',
					pageSize : 10,
					hideTrigger : true,
					minListWidth : this.minListWidth || 280,
					tpl : resultTpl,
					minChars : 2,
					enableKeyEvents : true,
					style : "border-style:none;background-image:none;",
					lazyInit : false,
					value : r.ZDMC,
					// hiddenName : ("ZDXH_" + r.JLBH),
					// hiddenValue : r.ZDXH,
					itemSelector : 'div.search-item',
					// renderTo : ("DIV_ZDLR_" + r.JLBH),
					onSelect : function(record) { // override default onSelect
						// to do
						this.bySelect = true;
						_ctx.setBackInfo(this, record);
						// this.hasFocus = false;// add by yangl 2013.9.4
						// 解决新增行搜索时重复调用setBack问题
					}
				});
		remoteField.render("DIV_ZDLR_" + r.JLBH)
		remoteField.on("focus", function() {
					remoteField.innerList.setStyle('overflow-y', 'hidden');
				}, this);
		remoteField.on("keyup", function(obj, e) {// 实现数字键导航
					var key = e.getKey();
					if (key == e.ENTER && !obj.isExpanded()) {
						// 是否是字母
						if (key == e.ENTER) {
							if (!obj.isExpanded()) {
								// 是否是字母
								var patrn = /^[a-zA-Z.]+$/;
								if (patrn.exec(obj.getValue())) {
									// 防止查询不出数据或者按回车速度过快导致上次输入结果直接被调入
									obj.getStore().removeAll();
									obj.lastQuery = "";
									if (obj.doQuery(obj.getValue(), true) !== false) {
										e.stopEvent();
										return;
									}
								}
							}
							_ctx.focusFieldAfter(obj.index);
							return;
						}
						var patrn = /^[a-zA-Z.]+$/;
						if (patrn.exec(obj.getValue())) {
							// 防止查询不出数据或者按回车速度过快导致上次输入结果直接被调入
							obj.getStore().removeAll();
							obj.lastQuery = "";
							if (obj.doQuery(obj.getValue(), true) !== false) {
								e.stopEvent();
								return;
							}
						}
					}
					if ((key >= 48 && key <= 57) || (key >= 96 && key <= 105)) {
						var searchTypeValue = _ctx.cookie
								.getCookie(_ctx.mainApp.uid + "_searchType");
						if (searchTypeValue != 'BHDM') {
							if (obj.isExpanded()) {
								if (key == 48 || key == 96)
									key = key + 10;
								key = key < 59 ? key - 49 : key - 97;
								var record = this.getStore().getAt(key);
								obj.bySelect = true;
								_ctx.setBackInfo(obj, record);
							}
						}
					}
					// 支持翻页
					if (key == 37) {
						obj.pageTb.movePrevious();
					} else if (key == 39) {
						obj.pageTb.moveNext();
					}
					// 删除事件 8
					if (key == 8) {
						if (obj.getValue().trim().length == 0) {
							if (obj.isExpanded()) {
								obj.collapse();
							}
						}
					}
				})
		if (remoteField.store) {
			remoteField.store.load = function(options) {
				Ext.apply(comboJsonData, options.params);
				Ext.apply(comboJsonData, mdsstore.baseParams);
				options = Ext.apply({}, options);
				this.storeOptions(options);
				if (this.sortInfo && this.remoteSort) {
					var pn = this.paramNames;
					options.params = Ext.apply({}, options.params);
					options.params[pn.sort] = this.sortInfo.field;
					options.params[pn.dir] = this.sortInfo.direction;
				}
				try {
					return this.execute('read', null, options); // <-- null
					// represents
					// rs. No rs for
					// load actions.
				} catch (e) {
					this.handleException(e);
					return false;
				}
			}
		}
		remoteField.on("change", this.dataChange, this);
		remoteField.el.dom.zdxx = r;
		remoteField.isSearchField = true;
		return remoteField;
	},
	getRemoteDicReader : function() {
		return new Ext.data.JsonReader({
					root : 'disease',
					totalProperty : 'count'
				}, [{
							name : 'numKey'
						}, {
							name : 'JBXH'
						}, {
							name : 'MSZD'

						}, {
							name : 'JBBM'

						}, {
							name : 'JBPB'

						}, {
							name : 'JBPB_text'

						}, {
							name : 'SFSB'

						}, {
							name : 'JBBGK'
						}, {
							name : 'CDMZZD'
						}]);
	},
	createDicField : function(r) {
		var dic = {
			id : "phis.dictionary.treatmentstatus"
		}
		var cmbCfg = {
			id : ("FZBZ_" + r.JLBH),
			name : "CIC_FZBZ",
			store : util.dictionary.SimpleDicFactory.getStore(dic),
			valueField : "key",
			displayField : "text",
			searchField : dic.searchField || "mCode",
			hideTrigger : true,
			editable : true,
			minChars : 2,
			selectOnFocus : true,
			triggerAction : dic.remote ? "query" : "all",
			pageSize : dic.pageSize,
			width : 60,
			listWidth : dic.listWidth,
			value : dic.defaultValue
		}
		cmbCfg.style = "border-style:none;background-image:none;";
		var combox = new util.widgets.MyCombox(cmbCfg)
		combox.render("DIV_FZBZ_" + r.JLBH);
		combox.on("focus", this.onFocus, combox);
		combox.on("blur", this.onBlur, combox);
		combox.on("change", this.dataChange, this);
		combox.zdxx = r;
		var defaultIndex = 0;
		if (r.FZBZ > 0) {
			defaultIndex = r.FZBZ;
		}
		combox.store.on("load", function() {
					if (this.getCount() == 0)
						return;
					if (defaultIndex) {
						if (isNaN(defaultIndex)
								|| this.getCount() <= defaultIndex)
							defaultIndex = 0;
						combox.setValue(this.getAt(defaultIndex).get('key'));
						defaultIndex = null;
					}
				})
		combox.store.load()
		return combox;
	},
	onFocus : function(f) {
		this.trigger.setDisplayed(true);
	},
	onBlur : function(f) {
		this.trigger.setDisplayed(false);
	},
	dataChange : function(f, newV, oldV) {
		// MyMessageTip.msg(f.el.dom.zdxx['ZDMC']+"--" + newV + "--" + oldV)
		if (f.el.dom.zdxx && f.el.dom.zdxx['ZDMC'] != newV) {
			f.setValue(f.el.dom.zdxx['ZDMC']);
		}
		this.diagnosisChange = true;
	}

}