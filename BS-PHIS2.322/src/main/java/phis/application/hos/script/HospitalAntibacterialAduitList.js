$package("phis.application.hos.script")

$import("phis.script.SimpleList")

phis.application.hos.script.HospitalAntibacterialAduitList = function(cfg) {
	cfg.autoLoadData = false;
	phis.application.hos.script.HospitalAntibacterialAduitList.superclass.constructor
			.apply(this, [cfg]);
}

Ext.extend(phis.application.hos.script.HospitalAntibacterialAduitList,
		phis.script.SimpleList, {
			expansion : function(cfg) {
				var radiogroup = [{
							xtype : "radio",
							boxLabel : '住  院',
							inputValue : 1,
							name : "JZLX",
							width : 60,
							checked : true,
							listeners : {
								check : this.afterCheck,
								scope : this
							}
						}, {
							xtype : "radio",
							boxLabel : '家  床',
							name : "JZLX",
							width : 60,
							inputValue : 6,
							listeners : {
								check : this.afterCheck,
								scope : this
							}
						}];

				var tbar = cfg.tbar;
				delete cfg.tbar;
				cfg.tbar = [];
				cfg.tbar.push([radiogroup, "-", tbar]);
			},
			afterCheck : function(radio, checked) {
				if (checked) {
					var jzlx = radio.inputValue;
					this.grid.getColumnModel().setColumnHeader(4,
							(jzlx > 1 ? "家床号码" : "住院号码"));
					this.grid.getColumnModel().setHidden(1, (jzlx > 1));
					this.grid.getColumnModel().setHidden(2, (jzlx > 1));
					this.grid.getColumnModel().setHidden(7, (jzlx > 1));
					var brks = this.opener.form.form.getForm()
							.findField("BRKS");
					brks.setDisabled((jzlx > 1));
					var r = this.cndFldCombox.store.getAt(0);
					r.set("text", (jzlx > 1 ? "家床号码" : "住院号码"))
					this.cndFldCombox.setRawValue((jzlx > 1 ? "家床号码" : "住院号码"))
					this.requestData.schema = (jzlx > 1
							? "phis.application.fsb.schemas.AMQC_SYSQ01"
							: "phis.application.war.schemas.AMQC_SYSQ01");
					// this.requestData.body = {
					// jzlx : jzlx,
					// departmentId : this.mainApp['phis'].reg_departmentId
					// }
					this.JZLX = jzlx;
					this.opener.doQuery();
				}
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
				tbarItems[5] = f
				tbar.doLayout(true)
				this.cndField = f
			},
			doOpenApplyModule : function(op) {
				var r = this.getSelectedRecord();
				if (!r) {
					MyMessageTip.msg("提示", "请选择需要操作的信息!", true);
					return;
				}
				if (op == 'aduit' && r.get("DJZT") < 1) {
					MyMessageTip.msg("提示", "不能审批未提交的抗菌药物申请单!", true);
					return
				}
				if (r.get("SPJG") > 0)
					op = "look";
				var form = this.createModule("antibacterialAduitForm",
						this.refAntibacterialApplyForm);
				form.initDataId = r.get("SQDH");
				form.action = op;
				var ctx = this;
				form.fixLoadCfg = function(cfg) {
					cfg.openBy = ctx.JZLX == 6 ? "fsb" : "";
				}
				form.on("save", this.loadData, this);
				if (!this.AduitFormWin) {
					this.AduitFormWin = form.getWin();
					this.AduitFormWin.add(form.initPanel());
				}
				this.AduitFormWin.show();
			},
			doPrint : function() {
				var r = this.getSelectedRecord();
				if (!r) {
					MyMessageTip.msg("提示", "请选择需要操作的信息!", true);
					return;
				}
				var initDataId = r.get("SQDH");
				if (!initDataId || initDataId == "" || initDataId == null) {
					MyMessageTip.msg("提示", "请选择需要操作的信息!", true);
					return;
				}
				var html = this.getApplyHtml(initDataId);
				this.doPrintReady(html);
				// form.on("ready", this.doPrintReady(data), this);
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
							"JZLX":this.JZLX
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
				if (data["YFGG"] == null || data["YFGG"] == "null") {
					data["YFGG"] = "";
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
						'<td colspan="5">' + data["ZSYS"] + '</td>',
						'</tr>',
						'<tr>',
						'<td height="30">诊&nbsp;&nbsp;断</td>',
						'<td colspan="7">' + data["MQZD"] + '</td>',
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
			},
			onDblClick : function() {
				this.doLook();
			},
			doLook : function() {
				this.doOpenApplyModule("look")
			},
			doAduit : function() {
				this.doOpenApplyModule("aduit")
			},
			totalHJYL : function(value, params, r, row, col, store) {
				return parseFloat(r.get("RZYL") * r.get("YYLC"), 2)
			}
		});