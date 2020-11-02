$package("phis.application.hos.script")

$import("phis.script.SimpleList")

phis.application.hos.script.HospitalAntibacterialApplyList = function(cfg) {
	cfg.autoLoadData = false;
	cfg.modal = true;
	cfg.removeByFiled = "YPMC";
	phis.application.hos.script.HospitalAntibacterialApplyList.superclass.constructor
			.apply(this, [cfg]);
}

Ext.extend(phis.application.hos.script.HospitalAntibacterialApplyList,
		phis.script.SimpleList, {
			onReady : function() {
				phis.application.hos.script.HospitalAntibacterialApplyList.superclass.onReady
						.call(this);
				if (this.win) {
					this.on("winShow", this.onWinShow, this);
				} else {
					this.onWinShow();
				}
			},
			/**
			 * 打开申请界面（新增或者修改）
			 * 
			 * @param {}
			 *            op: create update
			 */
			doOpenApplyModule : function(op) {
				var initDataId = null;
				if (op == "update") {
					var r = this.getSelectedRecord();
					initDataId = r.get("SQDH");
				}
				var form = this.createModule("antibacterialApplyListForm",
						this.refAntibacterialApplyForm);
				form.render = (this.brxx ? "apply" : "hdr") + this.suffix;
				form.brxx = this.brxx || this.exContext.empiData;
				form.initDataId = initDataId;
				form.opener = this;
				form.openBy = this.openBy
				if (this.openBy == 'fsb') {
					form.fixLoadCfg = function(cfg) {
						cfg.openBy = "fsb";
					}
				}
				form.on("save", this.doRefresh, this);
				if (!this.applyFormWin) {
					this.applyFormWin = form.getWin();
					this.applyFormWin.add(form.initPanel());
				}
				this.applyFormWin.show();
			},
			onWinShow : function() {
				var jzlx = this.openBy == 'fsb' ? 6 : 1;
				this.initCnd = [
						'and',
						[
								'and',
								['eq', ['$', 'a.JZXH'], ['d', this.initDataId]],
								['eq', ['$', 'a.JZLX'], ['i', jzlx]]]
						];
						//['ne', ['$', 'a.ZFBZ'], ['i', 1]]]
				this.requestData.cnd = [
						'and',
						[
								'and',
								['eq', ['$', 'a.JZXH'], ['d', this.initDataId]],
								['eq', ['$', 'a.JZLX'], ['i', jzlx]]],
						];
						//['ne', ['$', 'a.ZFBZ'], ['i', 1]]];
				this.loadData();
			},
			saveStatus : function(op) {
				var r = this.getSelectedRecord();
				if (!r) {
					MyMessageTip.msg("提示", "请先选择需要操作的抗菌药物信息!", true)
					return;
				}
				var body = {
					SQDH : r.get("SQDH")
				}
				if (op == "commit") {
					body.DJZT = 1;
				} else {
					body.ZFBZ = 1;
				}
				if (op == "commit" && r.get("DJZT") > 0) {
					MyMessageTip.msg("提示", "该记录已提交，请勿重复提交!", true)
					return;
				}
				if (op == "writeoff" && r.get("SPJG") > 0) {
					MyMessageTip.msg("提示", "该记录已审核，不能作废!", true)
					return;
				}
				if (op == "calloff"&&r.get("ZFBZ") == 0) {
					MyMessageTip.msg("提示", "操作有误!", true);
					return;
				}
				if (op == "calloff"&&r.get("ZFBZ") == 1) {
					//MyMessageTip.msg("提示", "该记录已作废，请勿重复作废!", true);
					body.ZFBZ = 0;
				}
				this.mask("正在保存数据...")
				phis.script.rmi.jsonRequest({
							serviceId : "hospitalAntibacterialService",
							serviceAction : "saveAntibactril",
							body : body
						}, function(code, msg, json) {
							this.unmask();
							this.saving = false;
							if (code > 300) {
								this.processReturnMsg(code, msg);
								return;
							}
							MyMessageTip.msg("提示", "保存成功!", true)
							this.doRefresh();
						}, this)
			},
			totalHJYL : function(value, params, r, row, col, store) {
				return parseFloat(r.get("RZYL") * r.get("YYLC"), 2).toFixed(2)
			},
			onDblClick : function() {
				this.doModify();
			},
			doCommit : function() {
				var r = this.getSelectedRecord();
				if (r.get("ZFBZ") == 1) {
					MyMessageTip.msg("提示", "该记录已作废，不能提交!", true);
					return;
				}
				this.saveStatus("commit");
			},
			doWriteoff : function() {
				var r = this.getSelectedRecord();
//				if (r.get("DJZT") >= 1) {
//					MyMessageTip.msg("提示", "该记录已提交，不能作废!", true);
//					return;
//				}
				this.saveStatus("writeoff");
			},
			doCalloff : function() {
				this.saveStatus("calloff");
			},
			doRefresh : function() {
				this.loadData();
			},
			doAdd : function() {
				this.doOpenApplyModule("create");
			},
			doModify : function() {
				var r = this.getSelectedRecord();
				if (r.get("ZFBZ") == 1) {
					MyMessageTip.msg("提示", "该记录已作废，不能修改!", true);
					return;
				}
				if (!r)
					return;
				this.doOpenApplyModule("update");
			},
			doRemove : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					return
				}
				if (r.get("DJZT") >= 1) {
					MyMessageTip.msg("提示", "该记录已提交，不能删除!", true);
					return;
				}
				var title = r.id;
				if (this.isCompositeKey) {
					title = "";
					for (var i = 0; i < this.schema.keys.length; i++) {
						title += r.get(this.schema.keys[i])
					}
				}
				if (this.removeByFiled && r.get(this.removeByFiled)) {
					title = r.get(this.removeByFiled);
				}
				Ext.Msg.show({
							title : '确认删除记录[' + title + ']',
							msg : '删除操作将无法恢复，是否继续?',
							modal : true,
							width : 300,
							buttons : Ext.MessageBox.OKCANCEL,
							multiline : false,
							fn : function(btn, text) {
								if (btn == "ok") {
									this.processRemove(r);
								}
							},
							scope : this
						})
			},
			doClose : function() {
				this.win.hide();
			},
			doPrint : function() {
				var r = this.getSelectedRecord();
				if (!r) {
					MyMessageTip.msg("提示", "请选择需要操作的信息!", true);
					return;
				}
				if (r.get("ZFBZ") == 1) {
					MyMessageTip.msg("提示", "该记录已作废，不能打印!", true);
					return;
				}
				var initDataId = r.get("SQDH");
				if (!initDataId || initDataId == "" || initDataId == null) {
					MyMessageTip.msg("提示", "请选择需要操作的信息!", true);
					return;
				}
				var html = this.getApplyHtml(initDataId);
				this.doPrintReady(html);
			},
			doPrintReady : function(html) {
				var LODOP = getLodop();
				LODOP.PRINT_INIT("打印控件");
				LODOP.ADD_PRINT_TABLE(30, 10, 700, 500, html);
				LODOP.SET_PRINT_MODE("PRINT_PAGE_PERCENT", "Full-Width");
				LODOP.PREVIEW();
			},
			getApplyHtml : function(initDataId) {
				var data = "";
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : "phis.hospitalAntibacterialService",
							serviceAction : "queryList",
							body : initDataId,
							JZLX : (this.openBy == 'fsb' ? 6 : 1)
						});
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg, this.onBeforeSave);
					return;
				} else {
					if (!r.json.body) {
						Ext.Msg.alert("提示", "无记录!");
						return;
					}
					var data = r.json.body;
				}
				if (data == "" || data == null) {
					Ext.Msg.alert("提示", "无记录!");
					return;
				}
				var tpl = new Ext.Template(
						'<table width="620" height="433" style="BORDER-COLLAPSE: collapse;font-size:14px;text-indent:2px;margin-left:30px;" borderColor="#000000" cellPadding="1"  align="center" border="1">',
						'<caption style="text-align:right;margin-bottom:2px;">',
						'<span  style="font-family:宋体;font-weight: bold;font-size: 24px;margin-right:50px;">抗菌药物使用申请表</span>',
						'<span style="font-family:宋体;font-weight: bold;font-size: 18px; color:#FF0000; margin-left:100px;"></span>',// '+
						// data["status"]
						// + '
						'</caption>',
						'<tr>',
						'<td width="66" height="29">科&nbsp;&nbsp;室</td>',
						'<td width="118">' + data["BRKS"] + '</td>',
						'<td width="63">病&nbsp;&nbsp;区</td>',
						'<td colspan="2"> ' + data["BRBQ"] + '</td>',
						'<td width="78">住院号</td>',
						'<td colspan="2">' + data["ZYHM"] + '</td>',
						'</tr>',
						'<tr>',
						'<td height="28">姓&nbsp;&nbsp;名</td>',
						'<td>' + data["BRXM"] + '</td>',
						'<td>性&nbsp;&nbsp;别</td>',
						'<td colspan="2">' + data["BRXB"] + '</td>',
						'<td>年&nbsp;&nbsp;龄</td>',
						'<td colspan="2">' + data["AGE"] + '</td>',
						'</tr>',
						'<tr>',
						'<td height="29">床位号</td>',
						// 床位号码是不是可以输入，并通过此调出病人
						'<td>' + data["BRCH"] + '</td>',
						'<td>主治医生</td>',
						'<td colspan="5">' + data["ZRYS"] + '</td>',
						'</tr>',
						'<tr>',
						'<td height="30">诊&nbsp;&nbsp;断</td>',
						'<td colspan="7">' + data["JCZD"] + '</td>',
						'</tr>',
						'<tr>',
						'<td rowspan="2">申请使用抗菌药物</td>',
						'<td height="30" colspan="2">药品名称</td>',
						'<td width="61">规格</td>',
						'<td width="60">日用量</td>',
						'<td>日剂量</td>',
						'<td width="69">用药天数</td>',
						'<td width="53">总剂量</td>',
						'</tr>',
						'<tr>',
						'<td height="30" colspan="2">' + data["YPMC"] + '</td>',
						'<td>' + data["YFGG"] + '</td>',
						'<td>' + data["MRYL"] + '</td>',
						'<td>' + data["RZYL"] + '</td>',
						'<td>' + data["YYLC"] + '</td>',
						'<td>' + data["HJYL"] + '</td>',
						'</tr>',
						'<tr>',
						'<td rowspan="3">申请原因</td>',
						'<td height="50" colspan="7"><div style="float:left"><span id="'
								+ '_GRBQYZZ">'
								+ data["GRBQYZZ"]
								+ '</span></div><div style="float:left"><span id="'
								+ '_MYGNDX">' + data["MYGNDX"] + '</span></td>',
						'</tr>',
						'<tr>',
						'<td colspan="7"><div style="float:left"><div id="'
								+ '_SQYWMG" >'
								+ data["SQYWMG"]
								+ '</div><span style="float:left">（检验报告单：</span><div style="float:left" id="'
								+ '_JYBGH">' + data["JYBGH"]
								+ ')</div></div></div></td>', '</tr>', '<tr>',
						'<td height="54" colspan="7"><div style="float:left"><span id="'
								+ '_QTYY" >' + data["QTYY"]
								+ '</span></div><div style="float:left">（请'
								+ data["HZKS"] + '专科会诊意见）：</div>',
						'<div style="clear:both;" id="' + '_QTYYXS" >'
								+ data["QTYYXS"] + '</div></td>', '</tr>',
						'<tr>', '<td height="36">申请医师</td>',
						'<td colspan="3"><div id="' + '_SQYS">' + data["SQYS"]
								+ '</div></td>', '<td>申请日期</td>',
						'<td colspan="3"><div id="' + '_SQKZR">' + data["SQRQ"]
								+ '</div></td>', '</tr>', '<tr>',
						'<td height="32">审批结果</td>',
						'<td colspan="3"><div id="' + '_SPJG">' + data["SPJG"]
								+ '</div></td>', '<td>审批用量</td>',
						'<td colspan="3"><div id="' + '_SPYL" >' + data["SPYL"]
								+ '</div></td>', '</tr>', '</table><br />')
				this.applyHtml = tpl.html;
				return tpl.html;
			}
		});