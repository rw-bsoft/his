$package("phis.application.cic.script")

$import("phis.script.SimpleList")

$styleSheet("phis.resources.css.app.biz.style")
phis.application.cic.script.ClinicEmrHistoryForEMRList = function(cfg) {
	cfg.enableRowBody = true;
	cfg.headerGroup = true;
	cfg.modal = true;
	this.serverParams = {
		serviceAction : cfg.serviceAction
	}
	phis.application.cic.script.ClinicEmrHistoryForEMRList.superclass.constructor
			.apply(this, [ cfg ])
}
var lastRowIndex = null;
var clinicPerson_ctx = null;
Ext
		.extend(
				phis.application.cic.script.ClinicEmrHistoryForEMRList,
				phis.script.SimpleList,
				{

					// showWinOnly:function(){
					// this.addPanelToWin();
					// },
					initPanel : function(sc) {
						// 判断是否是EMRView内部打开
						var grid = phis.application.cic.script.ClinicEmrHistoryForEMRList.superclass.initPanel
								.call(this, sc);
						if (this.exContext != null
								&& this.exContext.ids != null) {
							this.initCnd = [
									'and',
									[ 'eq', [ '$', 'a.BRBH' ],
											[ 'd', this.exContext.ids.brid ] ],
									[ 'eq', [ '$', 'e.YDLBBM' ], [ 'i', 17 ] ],
									[ 'ne', [ '$', 'd.BLZT' ], [ 'i', 9 ] ] ];
							this.requestData.cnd = [
									'and',
									[ 'eq', [ '$', 'a.BRBH' ],
											[ 'd', this.exContext.ids.brid ] ],
									[ 'eq', [ '$', 'e.YDLBBM' ], [ 'i', 17 ] ],
									[ 'ne', [ '$', 'd.BLZT' ], [ 'i', 9 ] ] ];
							grid.getColumnModel().setHidden(3, true);
							grid.getColumnModel().setHidden(4, true);
							grid.getColumnModel().setHidden(5, true);
							grid.getColumnModel().setHidden(6, true);
						} else {
							this.initCnd = [
									'eq',
									[ '$', 'a.JGID' ],
									[
											"$",
											"'"
													+ this.mainApp['phisApp'].deptId
													+ "'" ] ]
							this.requestData.cnd = [
									'eq',
									[ '$', 'a.JGID' ],
									[
											"$",
											"'"
													+ this.mainApp['phisApp'].deptId
													+ "'" ] ]
						}
						clinicPerson_ctx = this;
						return grid;
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
						rp.body = "<div id=recordHistry_Person_"
								+ record.get("BLBH") + " class='rtmain'></div>";
						return 'x-grid3-row-collapsed';
					},
					toggleRow : function(row) {
						if (typeof row == 'number') {
							row = this.grid.view.getRow(row);
						}
						this[Ext.fly(row).hasClass('x-grid3-row-collapsed') ? 'expandRow'
								: 'collapseRow'](row);
					},
					expandRow : function(row) {
						if (typeof row == 'number') {
							row = this.grid.view.getRow(row);
						}
						var record = this.grid.store.getAt(row.rowIndex);
						if (document.getElementById("recordHistry_Person_"
								+ record.get("BLBH")).innerHTML == "") {
							document.getElementById("recordHistry_Person_"
									+ record.get("BLBH")).innerHTML = clinicPerson_ctx
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
						var body = Ext.fly(row).child(
								'tr:nth(1) div.x-grid3-row-body', true);
						Ext.fly(row).replaceClass('x-grid3-row-expanded',
								'x-grid3-row-collapsed');
					},
					getHtml : function(record) {
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
						for ( var j = 0; j < deep; j++) {
							s += "&nbsp;&nbsp;&nbsp;&nbsp;";
						}
						return s;
					},
					getDiagnosisHtml : function(records) {
						var html = '<table cellpadding="0" cellspacing="0" border="0" class="BL_ul">'
						if (records != null && records.length > 0) {
							var num = 1;
							for ( var i = 0; i < records.length; i++) {
								var r = records[i];
								html += '<tr><td width="50px">'
										+ (r.DEEP == 0 ? num + '.' : '&nbsp')
										+ '</td><td colSpan="2">'
										+ this.getDeepStr(r.DEEP)
										+ r.ZDMC
										+ (r.ZZBZ == 1 ? '<img src="'
												+ ClassLoader.appRootOffsetPath
												+ 'resources/phis/resources/css/app/biz/images/zhu.png" title="主诊断" />'
												: '') + '</td><td colSpan="4">'
										+ (r.FZBZ == 1 ? '复诊' : '初诊')
										+ '</td></tr>'
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
							for ( var i = 0; i < measures.length; i++) {
								var r = measures[i];
								html += "<tr>";
								if (num == 1) {
									// if (r.TYPE == 3) {
									// isHerbs = true;
									// }
									html += '<td width="50px">' + num
											+ '.</td>';
									num++;
								} else {
									if (r.CFSB != measures[i - 1].CFSB) {
										html += '<td width="50px">' + num
												+ '.</td>';
										num++;
										// isHerbs = true;
									} else {
										html += '<td width="50px">&nbsp;</td>';
									}
								}
								if (r.TYPE == "3") {
									html += '<td>' + r.YPMC
											+ '</td><td colspan="6">' + r.YPSL
											+ (r.YFDW ? r.YFDW : '')
											+ '</td></tr>';
									// 判断是否需要添加尾部信息
									if (i + 1 >= measures.length
											|| r.CFSB != measures[i + 1].CFSB) {
										html += '<td colspan="7" align="center">帖数：'
												+ (r.CFTS ? r.CFTS : '')
												+ '&nbsp;&nbsp;&nbsp;&nbsp;用法：'
												+ (r.GYTJ_text ? r.GYTJ_text
														: '')
												// +
												// '&nbsp;&nbsp;&nbsp;&nbsp;服法：'
												// + (r.YPZS_text ? r.YPZS_text
												//														: '')
												+ '&nbsp;&nbsp;&nbsp;&nbsp;'
												+ (r.YPYF_text ? r.YPYF_text
														: '') + '</td>';
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
													+ r.SBXH + ',' + r.YPXH
													+ ')"/>';
										}
										html += '</td>';
									}
									html += '<td>' + r.YPYF_text + '</td>'
											+ '<td>' + r.YCJL
											+ (r.JLDW ? r.JLDW : '') + '</td>'
											+ '<td>' + r.YPSL
											+ (r.YFDW ? r.YFDW : '') + '</td>'
											+ '<td>'
											+ (r.GYTJ_text ? r.GYTJ_text : '')
											+ '</td>';
									html += '<td>&nbsp</td></tr>';
								}
							}
						}
						if (disposal != null && disposal.length > 0) {
							for ( var i = 0; i < disposal.length; i++) {
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
					}
				});