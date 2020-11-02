$package("phis.application.cic.script")

$import("phis.script.SimpleList")

$styleSheet("phis.resources.css.app.biz.style")

phis.application.cic.script.ClinicHistoryList = function(cfg) {
	cfg.enableRowBody = true;
	cfg.headerGroup = true;
	cfg.modal = true;
	this.serverParams = {
		serviceAction : cfg.serviceAction
	}
	phis.application.cic.script.ClinicHistoryList.superclass.constructor.apply(
			this, [cfg])
}
var lastRowIndex = null;
var clinicAll_ctx = null;
var CF_MZHM = null;
Ext.extend(phis.application.cic.script.ClinicHistoryList,
		phis.script.SimpleList, {

			// showWinOnly:function(){
			// this.addPanelToWin();
			// },
			initPanel : function(sc) {
				Ext.apply(this, this.loadSystemParams({
									privates : ['QYMZDZBL']
								}));
				var grid = phis.application.cic.script.ClinicHistoryList.superclass.initPanel
						.call(this, sc);
				this.requestData.schema = "phis.application.cic.schemas.YS_MZ_JZLS";
				if (this.QYMZDZBL + "" == '1') {
					this.requestData.serviceAction = "queryOMRHistoryList";
					this.requestData.schema = "phis.application.cic.schemas.YS_MZ_JZLS_EMR";
					this.initCnd = [
							'and',
							['eq', ['$', 'e.YDLBBM'], ['i', 17]],
							['ne', ['$', 'd.BLZT'], ['i', 9]],
							[
									'eq',
									['$', 'a.JGID'],
									[
											"$",
											"'"
													+ this.mainApp['phisApp'].deptId
													+ "'"]]];
					this.requestData.cnd = [
							'and',
							['eq', ['$', 'e.YDLBBM'], ['i', 17]],
							['ne', ['$', 'd.BLZT'], ['i', 9]],
							[
									'eq',
									['$', 'a.JGID'],
									[
											"$",
											"'"
													+ this.mainApp['phisApp'].deptId
													+ "'"]]];
				} else {
					// 判断是否是EMRView内部打开
					this.initCnd = ['eq', ['$', 'a.JGID'],
							["$", "'" + this.mainApp['phisApp'].deptId + "'"]]
					this.requestData.cnd = ['eq', ['$', 'a.JGID'],
							["$", "'" + this.mainApp['phisApp'].deptId + "'"]]
				}
				clinicAll_ctx = this;
				return grid;
			},
			expansion : function(cfg) {
				var radiogroup = [];
				var itemName = ['全院', '本科室', '本人'];
				this.gltj = 1;
				for (var i = 1; i < 4; i++) {
					radiogroup.push({
						xtype : "radio",
						checked : i == 1,
						boxLabel : itemName[i - 1],
						inputValue : i,
						name : "jzls",
						listeners : {
							check : function(group, checked) {
								if (checked) {
									var gltj = group.inputValue;
									this.gltj = gltj
									if (gltj == 1) {
										if (this.QYMZDZBL + "" == '1') {
											this.initCnd = [
													'and',
													['eq', ['$', 'e.YDLBBM'],
															['i', 17]],
													['ne', ['$', 'd.BLZT'],
															['i', 9]],
													[
															'eq',
															['$', 'a.JGID'],
															[
																	"$",
																	"'"
																			+ this.mainApp['phisApp'].deptId
																			+ "'"]]];
											this.requestData.cnd = [
													'and',
													['eq', ['$', 'e.YDLBBM'],
															['i', 17]],
													['ne', ['$', 'd.BLZT'],
															['i', 9]],
													[
															'eq',
															['$', 'a.JGID'],
															[
																	"$",
																	"'"
																			+ this.mainApp['phisApp'].deptId
																			+ "'"]]];
										} else {
											this.initCnd = [
													'eq',
													['$', 'a.JGID'],
													[
															"$",
															"'"
																	+ this.mainApp['phisApp'].deptId
																	+ "'"]];
											this.requestData.cnd = [
													'eq',
													['$', 'a.JGID'],
													[
															"$",
															"'"
																	+ this.mainApp['phisApp'].deptId
																	+ "'"]];
										}
										this.doCndQuery()
									} else if (gltj == 2) {
										if (this.QYMZDZBL + "" == '1') {
											this.initCnd = [
													'and',
													['eq', ['$', 'e.YDLBBM'],
															['i', 17]],
													['ne', ['$', 'd.BLZT'],
															['i', 9]],
													[
															'eq',
															['$', 'a.KSDM'],
															[
																	"$",
																	"'"
																			+ this.mainApp['phis'].departmentId
																			+ "'"]],
													[
															'eq',
															['$', 'a.JGID'],
															[
																	"$",
																	"'"
																			+ this.mainApp['phisApp'].deptId
																			+ "'"]]];
											this.requestData.cnd = [
													'and',
													['eq', ['$', 'e.YDLBBM'],
															['i', 17]],
													['ne', ['$', 'd.BLZT'],
															['i', 9]],
													[
															'eq',
															['$', 'a.KSDM'],
															[
																	"$",
																	"'"
																			+ this.mainApp['phis'].departmentId
																			+ "'"]],
													[
															'eq',
															['$', 'a.JGID'],
															[
																	"$",
																	"'"
																			+ this.mainApp['phisApp'].deptId
																			+ "'"]]];
										} else {
											this.initCnd = [
													'and',
													[
															'eq',
															['$', 'a.KSDM'],
															[
																	"$",
																	"'"
																			+ this.mainApp['phis'].departmentId
																			+ "'"]],
													[
															'eq',
															['$', 'a.JGID'],
															[
																	"$",
																	"'"
																			+ this.mainApp['phisApp'].deptId
																			+ "'"]]];
											this.requestData.cnd = [
													'and',
													[
															'eq',
															['$', 'a.KSDM'],
															[
																	"$",
																	"'"
																			+ this.mainApp['phis'].departmentId
																			+ "'"]],
													[
															'eq',
															['$', 'a.JGID'],
															[
																	"$",
																	"'"
																			+ this.mainApp['phisApp'].deptId
																			+ "'"]]];
										}
										this.doCndQuery()
									} else if (gltj == 3) {
										if (this.QYMZDZBL + "" == '1') {
											this.initCnd = [
													'and',
													['eq', ['$', 'e.YDLBBM'],
															['i', 17]],
													['ne', ['$', 'd.BLZT'],
															['i', 9]],
													[
															'eq',
															['$', 'a.YSDM'],
															[
																	"$",
																	"'"
																			+ this.mainApp.uid
																			+ "'"]],
													[
															'eq',
															['$', 'a.JGID'],
															[
																	"$",
																	"'"
																			+ this.mainApp['phisApp'].deptId
																			+ "'"]]];
											this.requestData.cnd = [
													'and',
													['eq', ['$', 'e.YDLBBM'],
															['i', 17]],
													['ne', ['$', 'd.BLZT'],
															['i', 9]],
													[
															'eq',
															['$', 'a.YSDM'],
															[
																	"$",
																	"'"
																			+ this.mainApp.uid
																			+ "'"]],
													[
															'eq',
															['$', 'a.JGID'],
															[
																	"$",
																	"'"
																			+ this.mainApp['phisApp'].deptId
																			+ "'"]]];
										} else {
											this.initCnd = [
													'and',
													[
															'eq',
															['$', 'a.YSDM'],
															[
																	"$",
																	"'"
																			+ this.mainApp.uid
																			+ "'"]],
													[
															'eq',
															['$', 'a.JGID'],
															[
																	"$",
																	"'"
																			+ this.mainApp['phisApp'].deptId
																			+ "'"]]];
											this.requestData.cnd = [
													'and',
													[
															'eq',
															['$', 'a.YSDM'],
															[
																	"$",
																	"'"
																			+ this.mainApp.uid
																			+ "'"]],
													[
															'eq',
															['$', 'a.JGID'],
															[
																	"$",
																	"'"
																			+ this.mainApp['phisApp'].deptId
																			+ "'"]]];
										}
										this.doCndQuery()
									}
								}
							},
							scope : this
						}
					})
				}

				// this.radiogroup = radiogroup;
				var tbar = cfg.tbar;
				delete cfg.tbar;
				cfg.tbar = [];
				cfg.tbar.push([radiogroup, "-", tbar]);
				// var label = new Ext.form.Label({
				// html : "<div id='YK_FKCL_1' align='center'
				// style='color:blue'>合计
				// 付款金额:0.00 已付金额:0.00 未付金额:0.00</div>"
				// })
				// cfg.bbar = [];
				// cfg.bbar.push(label);
			},
			doNew : function() {
				MyMessageTip.msg("注意", '测试 测试 测试!', true);
			},
			onDblClick : function() {

			},
			onRowClick : function(record, rowIndex) {
				if (lastRowIndex != null && lastRowIndex != rowIndex) {
					if (typeof lastRowIndex == 'number') {
						row = this.grid.view.getRow(lastRowIndex);
						this.collapseRow(row)
					}
				}
				lastRowIndex = rowIndex;
				this.toggleRow(rowIndex)
			},
			getRowClass : function(record, rowIndex, rp, ds) { // rp =
																// rowParams
				rp.body = "<div id=recordHistry_All_" + record.get("JZXH")
						+ record.get("BLBH") + " class='rtmain'></div>";
				return 'x-grid3-row-collapsed';
			},
			toggleRow : function(row) {
				if (typeof row == 'number') {
					row = this.grid.view.getRow(row);
				}
				this[Ext.fly(row).hasClass('x-grid3-row-collapsed')
						? 'expandRow'
						: 'collapseRow'](row);
			},
			expandRow : function(row) {
				if (typeof row == 'number') {
					row = this.grid.view.getRow(row);
				}
				var record = this.grid.store.getAt(row.rowIndex);
				if (document.getElementById("recordHistry_All_"
						+ record.get("JZXH") + record.get("BLBH")).innerHTML == "") {
					document.getElementById("recordHistry_All_"
							+ record.get("JZXH") + record.get("BLBH")).innerHTML = clinicAll_ctx
							.getHtml(record);
				}
				Ext.fly(row).replaceClass('x-grid3-row-collapsed',
						'x-grid3-row-expanded');
			},
			collapseRow : function(row) {
				if (typeof row == 'number') {
					row = this.grid.view.getRow(row);
				}
				var record = this.grid.store.getAt(row.rowIndex);
				var body = Ext.fly(row).child('tr:nth(1) div.x-grid3-row-body',
						true);
				Ext.fly(row).replaceClass('x-grid3-row-expanded',
						'x-grid3-row-collapsed');
			},
			getHtml : function(record) {
				if (this.QYMZDZBL + "" == '1') {
					var type = "HTML";
					var url = ClassLoader.appRootOffsetPath
							+ 'resources/phis/resources/phisUrlProxy/FileContent.outputStream?BLBH='
							+ record.data.BLBH + '&type=' + type;
					var html = util.rmi.loadXML({
								url : url,
								httpMethod : "get"
							});
					var tpl = new Ext.XTemplate(html);
					this.tpl = tpl;
					return this.tpl.apply({});
				} else {
					var body = this.initClinicRecord(record);
					if (body == null)
						return "";
					var data = {};
					if (body.ms_bcjl) {
						for (prop in body.ms_bcjl) {
							if (body.ms_bcjl[prop] == null
									|| body.ms_bcjl == 'null') {
								body.ms_bcjl[prop] = "";
							}
						}
						Ext.apply(data, body.ms_bcjl);
					} else {
						data.KS = 0;
						data.YT = 0;
						data.HXKN = 0;
						data.OT = 0;
						data.FT = 0;
						data.FX = 0;
						data.PZ = 0;
						data.QT = 0;
					}
					data.ms_brzd = this.getDiagnosisHtml(body.ms_brzd);
					data.measures = this.getMeasuresHtml(body.measures,
							body.disposal);

					if (!this.tpl) {
						var tpl = new Ext.XTemplate(
								'<div class="Rlist">',
								'<div style="width:100%; overflow-x:hidden;overflow-y:hidden;z-index:0;">',
								'<table cellpadding="1" cellspacing="0" class="BL_table" style="height:auto" id="L_table">',
								'<tr>',
								'<th width="15%">主诉：</th><td colspan="2">{ZSXX}</td>',
								'</tr>',
								'<tr>',
								'<th>现病史：</th><td colspan="2">{XBS}</td>',
								'</tr>',
								'<tr>',
								'<th>既往史：</th><td colspan="2">{JWS}</td>',
								'</tr>',
								'<tr>',
								'<th >&nbsp;</th><td colspan="2"><label>T:{T}℃</label><label>P:{P}次/分</label><label>R:{R}次/分</label><label>BP:{SSY}</label>/<label>{SZY}mmHg</label><label>H:{H}cm</label><label>W:{W}kg</label><label>BMI:{BMI}</label></td>',
								'</tr>',
								'<tr>',
								'<th>临床表现：</th><td colspan="2"><label><input type="checkbox" <tpl if="KS==1">checked</tpl> >&nbsp咳嗽</label><label><input type="checkbox" <tpl if="YT===1">checked</tpl> >&nbsp咽痛</label><label><input type="checkbox" <tpl if="HXKN===1">checked</tpl> >&nbsp呼吸困难</label><label><input type="checkbox"  <tpl if="OT===1">checked</tpl>>&nbsp呕吐</label><label><input type="checkbox"  <tpl if="FT===1">checked</tpl>>&nbsp腹痛</label><label><input type="checkbox" <tpl if="FX===1">checked</tpl>>&nbsp腹泻</label><label><input type="checkbox"  <tpl if="PZ===1">checked</tpl>>&nbsp皮疹</label><label><input type="checkbox"  <tpl if="QT===1">checked</tpl>>&nbsp其他</label></td>',
								'</tr>',
								'<tr>',
								'<th>体格检查：</th><td colspan="2">{TGJC}</td>',
								'</tr>',
								'<tr>',
								'<th>辅助检查：</th><td colspan="2">{FZJC}</td>',
								'<tr>',
								' <th>初步诊断：</th><td colspan="2">',
								'<div id="recordHistry_clinicDiv">{ms_brzd}</div>',
								'</td>',
								'</tr>',
								'<tr><th>处理措施：</th><td colspan="2">',
								'<div id="recordHistry_measuresDiv">{measures}</div></td>',
								'</tr>',
								'<tr>',
								'<th>健康教育：</th><td colspan="2">{JKJYNR}</td>',
								'</tr>',
								'<tr>',
								'<th>处理意见：</th><td colspan="2">{BQGZ}</td>',
								'</tr>', '</table>', '</div></div>');
						this.tpl = tpl;
					}
					return this.tpl.apply(data);
				}
			},
			initClinicRecord : function(record) {
				// 载入病历信息
				var resData = phis.script.rmi.miniJsonRequestSync({
							serviceId : "clinicManageService",
							serviceAction : "loadClinicInfo",
							body : {
								"clinicId" : record.get("JZXH"),
								"type" : "5"
							}
						});
				return resData.code > 200 ? null : resData.json;
			},
			getDeepStr : function(deep) {
				var s = "";
				for (var j = 0; j < deep; j++) {
					s += "&nbsp;&nbsp;&nbsp;&nbsp;";
				}
				return s;
			},
			getDiagnosisHtml : function(records) {
				var html = '<table cellpadding="0" cellspacing="0" border="0" class="BL_ul">'
				if (records != null && records.length > 0) {
					var num = 1;
					for (var i = 0; i < records.length; i++) {
						var r = records[i];
						html += '<tr><td width="50px">'
								+ (r.DEEP == 0 ? num + '.' : '&nbsp')
								+ '</td><td colSpan="2">'
								+ this.getDeepStr(r.DEEP)
								+ r.ZDMC
								+ (r.ZZBZ == 1
										? '<img src="'
												+ ClassLoader.appRootOffsetPath
												+ 'resources/phis/resources/css/app/biz/images/zhu.png" title="主诊断" />'
										: '') + '</td><td colSpan="4">'
								+ (r.FZBZ == 1 ? '复诊' : '初诊') + '</td></tr>'
						if (r.DEEP == 0)
							num++;
					}
				}
				html += '</table>';
				return html;
			},
			// 处理措施
			getMeasuresHtml : function(measures, disposal) {
				var html = '<table cellpadding="0" cellspacing="0" border="0" class="BL_ul">'
				var num = 1;
				// var isHerbs = false;
				if (measures != null && measures.length > 0) {
					for (var i = 0; i < measures.length; i++) {
						var r = measures[i];
						html += "<tr>";
						if (num == 1) {
							// if (r.TYPE == 3) {
							// isHerbs = true;
							// }
							html += '<td width="50px">' + num + '.</td>';
							num++;
						} else {
							if (r.CFSB != measures[i - 1].CFSB) {
								html += '<td width="50px">' + num + '.</td>';
								num++;
								// isHerbs = true;
							} else {
								html += '<td width="50px">&nbsp;</td>';
							}
						}
						if (r.TYPE == "3") {
							html += '<td>' + r.YPMC + '</td><td colspan="6">'
									+ r.YPSL + (r.YFDW ? r.YFDW : '')
									+ '</td></tr>';
							// 判断是否需要添加尾部信息
							if (i + 1 >= measures.length
									|| r.CFSB != measures[i + 1].CFSB) {
								html += '<td colspan="7" align="center">帖数：'
										+ (r.CFTS ? r.CFTS : '')
										+ '&nbsp;&nbsp;&nbsp;&nbsp;用法：'
										+ (r.GYTJ_text ? r.GYTJ_text : '')
										// + '&nbsp;&nbsp;&nbsp;&nbsp;服法：' +
										// (r.YPZS_text?r.YPZS_text:'')
										+ '&nbsp;&nbsp;&nbsp;&nbsp;'
										+ (r.YPYF_text ? r.YPYF_text : '')
										+ '</td>';
							}
						} else {
							html += '<td>' + r.YPMC;
							if (r.YFGG) {
								html += "/" + r.YFGG + '&nbsp;';
							}
							// 判断是否皮试
							if (r.PSPB == 1) {

								if (r.PSJG) {
									var psjg_text = "<font color='#FFFA4C'>未知</font>";
									if (r.PSJG == 1) {
										psjg_text = "<font color='red'>阳性</font>";
									} else if (r.PSJG == -1) {
										psjg_text = "<font color='green'>阴性</font>";
									}
									html += ' (' + psjg_text + ')';
								} else {
									html += '<img id="jzxh_'
											+ r.SBXH
											+ '" src="resources/phis/resources/css/app/biz/images/pi.png" width="21" height="21"  style="cursor:pointer;" onClick="openSkinTestWin('
											+ r.SBXH + ',' + r.YPXH + ')"/>';
								}
								html += '</td>';
							}
							html += '<td>' + r.YPYF_text + '</td>' + '<td>'
									+ r.YCJL + (r.JLDW ? r.JLDW : '') + '</td>'
									+ '<td>' + r.YPSL + (r.YFDW ? r.YFDW : '')
									+ '</td>' + '<td>' + r.GYTJ_text + '</td>';
							html += '<td>&nbsp</td></tr>';
						}
					}
				}
				if (disposal != null && disposal.length > 0) {
					for (var i = 0; i < disposal.length; i++) {
						var r = disposal[i];
						html += "<tr>";
						html += '<td width="50px">' + num + '.</td>';
						html += '<td colspan="2">' + r.FYMC + '</td>'
								+ '<td colspan="5">' + r.YLSL
								+ (r.FYDW ? r.FYDW : '') + '</td>';
						html += "</tr>";
						num++;
					}
				}
				html += '</table>';
				return html;
			},
			doPrintCF : function() {
				var r = this.getSelectedRecord();
				CF_MZHM = r.get("MZHM");
				if (!r) {
					MyMessageTip.msg("提示", "请先选中要打印的就诊历史记录!", true);
					return;
				}
				var resData = phis.script.rmi.miniJsonRequestSync({
							serviceId : "clinicManageService",
							serviceAction : "loadCF01",
							body : {
								"clinicId" : r.get("JZXH")
							}
						});
				var cf01s = resData.json.cf01s;
				if (!cf01s || cf01s.length == 0) {
					MyMessageTip.msg("提示", "没有可以打印的处方信息!", true);
					return;
				}

				var module = this.createModule("prescriptionEntryList",
						this.refCFMX);
				module.cfsbs = cf01s;
				if (this.printCFWin) {
					this.printCFWin.show();
					return;
				}
				this.printCFWin = module.getWin();
				this.printCFWin.add(module.initPanel());
				this.printCFWin.setWidth(1024);
				this.printCFWin.setHeight(535);
				this.printCFWin.show();

			},
			doPrintCZ : function() {
				var r = this.getSelectedRecord();
				if (!r) {
					MyMessageTip.msg("提示", "请先选中要打印的就诊历史记录!", true);
					return;
				}
				var resData = phis.script.rmi.miniJsonRequestSync({
							serviceId : "clinicDisposalEntryService",
							serviceAction : "getCZYJXH",
							body : {
								"jzxh" : r.get("JZXH")
							}
						});
				if (resData.json.YJXH.length == 0) {
					MyMessageTip.msg("提示", "没有可以打印的处置信息!", true);
					return;
				}
				var module = this.createModule("czprint", this.refCZMX)
				module.yjxh = [];
				for (var i = 0; i < resData.json.YJXH.length; i++) {
					module.yjxh.push(resData.json.YJXH[i].YJXH);
				}
				if (r.get("JZXH")) {
					module.jzxh = r.get("JZXH");
				} else {
					module.jzxh = 0;
				}
				if (r.get("BRBH")) {
					module.brid = r.get("BRBH");// 病人信息部分的打印就传brid过去
				} else {
					module.brid = 0;
				}
				module.initPanel();
				module.doPrint();

			},
			doPrintBL : function() {
				var r = this.getSelectedRecord();
				if (!r) {
					MyMessageTip.msg("提示", "请先选中要打印的就诊历史记录!", true);
					return;
				}
				if(this.QYMZDZBL+""=='1'){
					var type = "HTML";
					var url = ClassLoader.appRootOffsetPath
					+ 'resources/phis/resources/phisUrlProxy/FileContent.outputStream?BLBH=' + r.data.BLBH + '&type='
					+ type;
					var html = util.rmi.loadXML({url:url,httpMethod:"get"});
					var LODOP=getLodop();
					LODOP.PRINT_INIT("打印控件");
					LODOP.SET_PRINT_PAGESIZE("0","","","");
					LODOP.ADD_PRINT_HTM("0","0","100%","100%",html);
					LODOP.SET_PRINT_MODE ("PRINT_PAGE_PERCENT","Full-Width");
					//预览
					LODOP.PREVIEW();
					return;
				}
				var module = this.createModule("patientMedicalRecords",
						"phis.application.cic.CIC/CIC/CIC01010101");
				if (!r.get("JZXH")) {
					MyMessageTip.msg("提示", "没有可以打印的病历信息!", true);
					return;
				}
				var BRXM = "";
				if (r.get("BRXM")) {
					BRXM = r.get("BRXM");// 病人姓名
				}
				var XB = "";
				if (r.get("BRXB")) {
					XB = r.get("BRXB");// 病人性别
				}
				var NL = "";
				if (r.get("CSNY")) {
					NL = r.get("CSNY");// 病人年龄
				}
				var MZHM = "";
				if (r.get("MZHM")) {
					MZHM = r.get("MZHM");// 门诊号码
				}
				if (!BRXM) {
					module.BRXM = null;
				} else {
					module.BRXM = encodeURIComponent(BRXM);
				}
				if (!XB) {
					module.XB = null;
				} else {
					module.XB = XB
				}
				if (!NL) {
					module.NL = null;
				} else {
					module.NL = NL
				}
				if (!MZHM) {
					module.MZHM = null;
				} else {
					module.MZHM = MZHM
				}
				if (r.get("JZXH")) {
					module.CLINICID = r.get("JZXH");
				} else {
					module.CLINICID = 0;
				}
				if (r.get("BRBH")) {
					module.BRID = r.get("BRBH");
				} else {
					module.BRID = 0;
				}
				module.initPanel();
				module.doPrint();
			},
			doCndQuery : function(button, e, addNavCnd) { // ** modified by
															// yzh ,
				var initCnd = this.initCnd
				var itid = this.cndFldCombox.getValue()
				var items = this.schema.items
				var it
				for (var i = 0; i < items.length; i++) {
					if (items[i].id == itid) {
						it = items[i]
						break
					}
				}

				if (!it) {
					return;
				}
				this.resetFirstPage()
				var f = this.cndField;
				var v = f.getValue()

				if (v == null || v == "") {
					this.queryCnd = null;
					this.requestData.cnd = initCnd
					this.refresh()
					return
				}
				if (f.getXType() == "datefield") {
					v = v.format("Y-m-d")
				}
				if (f.getXType() == "datetimefield") {
					v = v.format("Y-m-d H:i:s")
				}
				var refAlias = it.refAlias || "a"
				var cnd = ['eq', ['$', refAlias + "." + it.id]]
				if (it.dic) {
					if (it.dic.render == "Tree") {
						var node = this.cndField.selectedNode
						if (!node.isLeaf()) {
							cnd[0] = 'like'
							cnd.push(['s', v + '%'])
						} else {
							cnd.push(['s', v])
						}
					} else {
						cnd.push(['s', v])
					}
				} else {
					switch (it.type) {
						case 'int' :
							cnd.push(['i', v])
							break;
						case 'double' :
						case 'bigDecimal' :
							cnd.push(['d', v])
							break;
						case 'string' :
							cnd[0] = 'like'
							cnd.push(['s', v + '%'])
							break;
						case "date" :
							if (v.format) {
								v = v.format("Y-m-d")
							}
							cnd[1] = [
									'$',
									"str(" + refAlias + "." + it.id
											+ ",'yyyy-MM-dd')"]
							cnd.push(['s', v])
							break;
						case 'datetime' :
						case 'timestamp' :
							if (v.format) {
								v = v.format("Y-m-d H:i:s")
							}
							cnd[1] = [
									'$',
									"str(" + refAlias + "." + it.id
											+ ",'yyyy-MM-dd')"]
							cnd.push(['s', v])
							break;
					}
				}
				this.queryCnd = cnd
				if (initCnd) {
					cnd = ['and', initCnd, cnd]
				}
				// alert(cnd)
				if(cnd[2][1][1]=="b.MZHM"){
					cnd[2][1] = ["$", "d.cardNo"];
				}else if(cnd[2][1][1]=="str(a.KSSJ,'yyyy-MM-dd')"){
					cnd[2][1] = ["$", "to_char(a.KSSJ,'yyyy-MM-dd')"];
				}
				this.requestData.cnd = cnd
				this.refresh()
			},
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
				// field.destroy()
				field.hide();
				var f = this.midiComponents[it.id]
				if (!f) {
					if (it.dic) {
						it.dic.src = this.entryName + "." + it.id
						it.dic.defaultValue = it.defaultValue
						it.dic.width = 150
						f = this.createDicField(it.dic)
					} else {
						f = this.createNormalField(it)
					}
					f.on("specialkey", this.onQueryFieldEnter, this)
					this.midiComponents[it.id] = f
				} else {
					f.on("specialkey", this.onQueryFieldEnter, this)
					// f.rendered = false
					f.show();
				}
				this.cndField = f
				tbarItems[6] = f
				tbar.doLayout()
			},
			getCndBar:function(items){
				var schema = "";
				if (!schema) {
					var re = util.schema.loadSync("phis.application.cic.schemas.YS_MZ_JZLS_EMR_CX1")
					if (re.code == 200) {
						schema = re.schema;
					} else {
						this.processReturnMsg(re.code, re.msg, this.initPanel)
						return;
					}
				}
				this.schema = schema;
				var items = this.schema.items
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
				var queryBtn = new Ext.Toolbar.SplitButton({
							text : '',
							iconCls : "query",
							notReadOnly : true, // ** add by yzh **
							menu : new Ext.menu.Menu({
										items : {
											text : "高级查询",
											iconCls : "common_query",
											handler : this.doAdvancedQuery,
											scope : this
										}
									})
						})
				this.queryBtn = queryBtn
				queryBtn.on("click", this.doCndQuery, this);
				return [combox, '-', cndField, '-', queryBtn]
			}
		});