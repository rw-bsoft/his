$package("com.bsoft.phis.checkapply")
$import("com.bsoft.phis.SimpleModule")

com.bsoft.phis.checkapply.CheckApplyExchangedApplicationModule_CIC2 = function(
		cfg) {
	com.bsoft.phis.checkapply.CheckApplyExchangedApplicationModule_CIC2.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(com.bsoft.phis.checkapply.CheckApplyExchangedApplicationModule_CIC2,
		com.bsoft.phis.SimpleModule, {
			initPanel : function() {
				var panel = new Ext.Panel({
					border : false,
					frame : true,
					layout : 'border',
					defaults : {
						border : false
					},
					items : [{
								layout : "fit",
								border : false,
								split : true,
								region : 'center',
								height : 300,
								width : "60%",
								items : this
										.getCheckApplyExchangedApplicationList()
							}, {
								layout : "fit",
								border : false,
								split : true,
								region : 'east',
								height : 300,
								width : "40%",
								items : this
										.getCheckApplyExchangedApplicationDetailsList()
							}],
					tbar : this.getTbar()
				});
				this.panel = panel;
				return panel;
			},
			getTbar : function() {
				var tbar = [];
				tbar.push(new Ext.form.Label({
							text : "日期:"
						}));
				tbar.push(new Ext.form.DateField({
							id : 'dateFrom2',
							name : 'dateFrom',
							value : new Date(),
							width : 100,
							allowBlank : false,
							altFormats : 'Y-m-d',
							format : 'Y-m-d',
							emptyText : '请选择'
						}));
				tbar.push(new Ext.form.Label({
							text : " - "
						}));
				tbar.push(new Ext.form.DateField({
							id : 'dateTo2',
							name : 'dateTo',
							value : new Date(),
							width : 100,
							allowBlank : false,
							altFormats : 'Y-m-d',
							format : 'Y-m-d',
							emptyText : '请选择'
						}));
				var ztStore = new Ext.data.SimpleStore({
							fields : ['value', 'text'],
							data : [[0, '未执行'], [1, '已执行']]
						});
				var combox = new Ext.form.ComboBox({
							id : "zt2",
							store : ztStore,
							width : 80,
							valueField : "value",
							displayField : "text",
							editable : false,
							selectOnFocus : true,
							triggerAction : 'all',
							mode : 'local',
							value : 0
						})
				tbar.push(combox);
				tbar.push({
							xtype : "button",
							text : "查询",
							iconCls : "query",
							scope : this,
							handler : this.doQuery
						});
				tbar.push({
							xtype : "button",
							text : "删除",
							iconCls : "common_cancel",
							scope : this,
							handler : this.doRemove
						});
				tbar.push({
							xtype : "button",
							text : "打印申请单",
							iconCls : "print",
							scope : this,
							handler : this.doPrint
						});
				tbar.push({
							xtype : "button",
							text : "取消执行",
							iconCls : "remove",
							scope : this,
							handler : this.doCacelExe
						});
				return tbar;
			},
			getCheckApplyExchangedApplicationList : function() {
				this.checkApplyExchangedApplicationList = this.createModule(
						"checkApplyExchangedApplicationList",
						this.refCheckApplyExchangedApplicationList);
				this.checkApplyExchangedApplicationList.opener = this;
				this.checkApplyExchangedApplicationGrid = this.checkApplyExchangedApplicationList
						.initPanel();
				return this.checkApplyExchangedApplicationGrid;
			},
			getCheckApplyExchangedApplicationDetailsList : function() {
				this.checkApplyExchangedApplicationDetailsList = this
						.createModule(
								"checkApplyExchangedApplicationDetailsList",
								this.refCheckApplyExchangedApplicationDetailsList);
				this.checkApplyExchangedApplicationDetailsList.opener = this;
				this.checkApplyExchangedApplicationDetailsGrid = this.checkApplyExchangedApplicationDetailsList
						.initPanel();
				return this.checkApplyExchangedApplicationDetailsGrid;
			},
			doRemove : function() {
				var list = this.checkApplyExchangedApplicationList;
				if (list.getSelectedRecord() == undefined) {
					Ext.Msg.alert("提示", "请选中要删除的项目")
					return;
				}
				if (list.getSelectedRecord().data.ZXPB == 1) {
					Ext.Msg.alert("提示", "该申请单已执行，删除失败");
					return;
				}
				var yjxh = list.getSelectedRecord().data.SQDH;
				Ext.Msg.show({
					title : '删除确认',
					msg : '删除该申请单将同时删除该申请单上的所有检查项目，确认删除?',
					modal : false,
					width : 300,
					buttons : Ext.MessageBox.OKCANCEL,
					multiline : false,
					fn : function(btn, text) {
						if (btn == "ok") {
							var r = util.rmi.miniJsonRequestSync({
										serviceId : "checkApplyService",
										serviceAction : "removeCheckApplyProject",
										body : {
											lb : 1,// 1表示门诊
											zt : 1,//1表示删除
											yjxh : yjxh
										}
									});
							if (r.code > 300) {
								this.processReturnMsg(r.code, r.msg);
								return;
							} else {
								if (r.json.code == 200) {
									MyMessageTip.msg("提示", "删除成功!", true);
									this.initClinicRecord();
									this.checkApplyExchangedApplicationList
											.refresh();
								} else {
									Ext.Msg.alert("提示", "该申请单已收费，删除失败");
								}
							}
						}
					},
					scope : this
				});

			},
			doCacelExe : function(){
				var list = this.checkApplyExchangedApplicationList;
				if (list.getSelectedRecord() == undefined) {
					Ext.Msg.alert("提示", "请选中要取消执行的项目")
					return;
				}
				if (list.getSelectedRecord().data.ZXPB == 0) {
					Ext.Msg.alert("提示", "取消执行只针对已执行的申请单");
					return;
				}
				var yjxh = list.getSelectedRecord().data.SQDH;
				Ext.Msg.show({
					title : '取消执行确认',
					msg : '取消执行只针对作废发票的特殊作用，是否继续?',
					modal : false,
					width : 300,
					buttons : Ext.MessageBox.OKCANCEL,
					multiline : false,
					fn : function(btn, text) {
						if (btn == "ok") {
							var r = util.rmi.miniJsonRequestSync({
										serviceId : "checkApplyService",
										serviceAction : "removeCheckApplyProject",
										body : {
											lb : 1,// 1表示门诊
											zt : 2,//2表示取消执行
											yjxh : yjxh
										}
									});
							if (r.code > 300) {
								this.processReturnMsg(r.code, r.msg);
								return;
							} else {
								if (r.json.code == 200) {
									MyMessageTip.msg("提示", "取消执行成功!", true);
									this.initClinicRecord();
									this.checkApplyExchangedApplicationList
											.refresh();
								} else {
									Ext.Msg.alert("提示", "取消执行失败");
								}
							}
						}
					},
					scope : this
				});
			},
			doQuery : function() {
				var dateFrom = Ext.getDom("dateFrom2").value;
				var dateTo = Ext.getDom("dateTo2").value;
				if (!dateFrom || !dateTo) {
					return
				}
				if (dateFrom != null && dateTo != null && dateFrom != ""
						&& dateTo != "" && dateFrom > dateTo) {
					Ext.MessageBox.alert("提示", "开始时间不能大于结束时间");
					return;
				}
				this.checkApplyExchangedApplicationList.refresh();
			},
			doPrint : function() {
				var list = this.checkApplyExchangedApplicationList;
				if (list.getSelectedRecord() == undefined) {
					Ext.Msg.alert("提示", "请选中要打印的申请单")
					return;
				}
				var record = list.getSelectedRecord();
				var sslx = record.get("SSLX");
				var sqdh = record.get("SQDH");
				var brid = this.opener.opener.exContext.ids.brid;
				var age = this.opener.opener.exContext.ids.age;
				var yllb = 1;//门诊
				var url = this.printurl+".print?pages=";
				// 根据所属类型跳到指定打印界面
				if (sslx == 1) {
					url+="CheckApplyBillForECG";
				} else if (sslx == 2) {
					url+="CheckApplyBillForRAD";
				} else if (sslx == 3) {
					url+="CheckApplyBillForBC";
				}
				url += "&execJs="+ this.getExecJs()+"&brid="+brid+"&sqdh="+sqdh+"&age="+age+"&yllb="+yllb;
					window
							.open(
									url,
									"",
									"height="
											+ (screen.height - 100)
											+ ", width="
											+ (screen.width - 10)
											+ ", top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no")
			},
			getExecJs : function() {
				return "jsPrintSetup.setPrinter('rb');"
			},
			initClinicRecord : function() {
				// 载入病历信息
				util.rmi.jsonRequest({
							serviceId : "clinicManageService",
							serviceAction : "loadClinicInfo",
							body : {
								"brid" : this.opener.opener.exContext.ids.brid,
								"clinicId" : this.opener.opener.exContext.ids.clinicId,
								"type" : "3"
							}
						}, function(code, msg, json) {
							if (code < 300) {
								var measures = json.measures;
								var disposal = json.disposal;
								document.getElementById("measuresDiv").innerHTML = this
										.getMeasuresHtml(measures, disposal);
							} else {
								this.processReturnMsg(code, msg);
							}
						}, this);
			},
			getMeasuresHtml : function(measures, disposal) {
				var html = '<table cellpadding="0" cellspacing="0" border="0" class="BL_ul">'
				var num = 1;
				var isHerbs = false;
				if (measures != null && measures.length > 0) {
					for (var i = 0; i < measures.length; i++) {
						var r = measures[i];
						html += "<tr>";
						if (num == 1) {
							if (r.TYPE == 3) {
								isHerbs = true;
							}
							html += '<td>' + num + '.</td>';
							num++;
						} else {
							if (r.TYPE == 3 && !isHerbs) {
								html += '<td>' + num + '.</td>';
								num++;
								isHerbs = true;
							} else {
								html += '<td>&nbsp;</td>';
							}
						}
						if (r.TYPE == "3") {
							html += '<td>' + r.YPMC + '</td><td colspan="6">'
									+ r.YPSL + r.YFDW + '</td></tr>';
							// 判断是否需要添加尾部信息
							if (i + 1 >= measures.length
									|| r.CFSB != measures[i + 1].CFSB
									|| r.YPZH != measures[i + 1].YPZH) {
								html += '<td colspan="7" align="center">帖数：'
										+ r.CFTS
										+ '&nbsp;&nbsp;&nbsp;&nbsp;用法：'
										+ r.GYTJ_text
										// + '&nbsp;&nbsp;&nbsp;&nbsp;服法：'
										// + r.YPZS_text
										+ '&nbsp;&nbsp;&nbsp;&nbsp;'
										+ r.YPYF_text + '</td>';
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
											+ '" src="resources/css/app/biz/images/pi.png" width="21" height="21"  style="cursor:pointer;" onClick="openSkinTestWin('
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
						html += '<td>' + num + '.</td>';
						html += '<td>' + r.FYMC + '</td>' + '<td colspan="6">'
								+ r.YLSL + r.FYDW + '</td>';
						html += "</tr>";
						num++;
					}
				}
				html += '</table>';
				return html;
			}
		})
