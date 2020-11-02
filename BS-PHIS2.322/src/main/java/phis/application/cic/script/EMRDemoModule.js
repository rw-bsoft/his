$package("phis.application.cic.script")

$import("phis.script.SimpleModule")
phis.application.cic.script.EMRDemoModule = function(cfg) {
	this.isModify = false;
	// this.mKey = "test";
	phis.application.cic.script.EMRDemoModule.superclass.constructor.apply(this, [cfg]);
}
var emr_this;
function doTest() {
	alert(1)
	// emr_this.doShowWin();

}

Ext.extend(phis.application.cic.script.EMRDemoModule, phis.script.SimpleModule, {
	initPanel : function() {
		// 判断是否有权限
		if (!this.checkEmrPermission()) {
			Ext.Msg.alert("错误", "对不起，您没有权限操作此功能，请与管理员联系!")
			return;
		}
		this.mKey = "E1";
		Ext.useShims = true;
		if (this.panel)
			return this.panel;
		var panel = new Ext.Panel({
					// id : this.mKey,
					border : false,
					html : this.getHtml(),
					frame : true,
					// autoScroll : true,
					tbar : this.createMyButtons()
				});
		this.panel = panel;
		panel.on("afterrender", this.onReady, this)
		emr_this = this;
		return panel;
	},
	doShowWin : function() {
		var module = this.createModule("ttt", "WAR030102");
		var win = module.getWin();
		win.add(module.initPanel());
		win.setHeight(400);
		win.show();
	},
	onReady : function() {
		var emr = this.emr = document.getElementById(this.mKey);
		if (!this.emr) {
			Ext.Msg.alert("提示", "检查到您尚未安装电子病历插件，请安装后重新尝试!")
			return;
		}
		// function addEvent(oElement, sEvent, func) {
		// if (oElement.attachEvent) {
		// oElement.attachEvent(sEvent, func);
		// } else {
		// sEvent = sEvent.substring(2, sEvent.length);
		// oElement.addEventListener(sEvent, func, false);
		// }
		// }
		if (Ext.isIE) {
			this.emr.attachEvent("BsButtonClick", function() {
						alert(emr.StrReturnData)
						var retObj = eval("(" + emr.StrReturnData + ")");
						alert(retObj.FunName)
					});
		} else {
			this.emr.addEventListener("BsButtonClick", function() {
						alert(1)
					});
		}
		// addEvent(document.getElementById("E1"), "BsButtonClick", function() {
		// alert(1)
		// });

		//this.doNewEMR();
		// this.doImportEMR();
		// if (this.emr.BsInitLoader) {
		// this.emr.BsInitLoader();
		// }
		// this.panel.getEl().on("contextmenu", function(e) {
		// MyMessageTip.msg("右键", "测试");
		// e.stopEvent()
		// })
		// this.showWin();
		// this.ActiveXKiller(this.mKey)
	},
	checkEmrPermission : function() {
		return true;
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
	getHtml : function() {
		// codebase='http://172.16.170.13:8080/PHIS/PEMRWORD.CAB'<input
		// type='text' id='hx' name='hx' height=15 width=120 />
		var s = '';
		if (Ext.isIE) {
			return s
					+ "<OBJECT id='"
					+ this.mKey
					+ "' name='"
					+ this.mKey
					+ "' classid='clsid:FFAA1970-287B-4359-93B5-644F6C8190BB' codebase='http://172.16.170.13:8080/PHIS/EMRWORD.CAB#version=2,3,0,1' align=center hspace=0 vspace=0 ></OBJECT>"
		} else {
			return s
					+ '<object	id="'
					+ this.mKey
					+ '" TYPE="application/x-itst-activex" event_onclick="doTest" WIDTH="1024" HEIGHT="450" clsid="{FFAA1970-287B-4359-93B5-644F6C8190BB}"></OBJECT>';
		}
	},
	doDownLoad : function() {
		window.open(ClassLoader.appRootOffsetPath + "component/ActiveX.exe");
	},
	doPrint : function() {
		this.emr.FunActiveXInterface("BsInvokeMenuItem", 'printTB', 'new');
	},
	doShowVersion : function() {
		var s = this.emr.FunActiveXInterface("BsGetFileVersion", '', '');
		// alert(s);
		// alert(this.emr.StrReturnData)
		this.emr.height = 300;
	},
	doSave : function() {
		this.emr.FunActiveXInterface('BsSaveWordData', 1, '');
		var resData = phis.script.rmi.miniJsonRequestSync({
					serviceId : "emrManageService",
					serviceAction : "saveTemplateData",
					body : {
						"data" : this.emr.WordData
					}
				});
		alert("保存成功!");
	},
	// 载入后自动调用
	doNewEMR : function() {
		var resData = phis.script.rmi.miniJsonRequestSync({
					serviceId : "emrManageService",
					serviceAction : "loadTemplateData",
					body : {
						"step" : 1
					}
				});
		this.emr.FunActiveXInterface('BsNewDocument', resData.json.docText, '');
		this.emr.BsButtonClick = this.handle
		// this.emr.addEventListener('BsButtonClick', doTest, false);

	},
	handle : function(oEvnet) {
		alert(1)
	},
	doImportEMR : function() {
		// 弹出病历模版选择
		// if (!this.node.BLBH) {
		// Ext.Msg.show({
		// title : '提示信息',
		// msg : '未能检索到指定类别的病历，是否现在就要增加一份?',
		// modal : true,
		// width : 300,
		// buttons : Ext.MessageBox.YESNO,
		// multiline : false,
		// fn : function(btn, text) {
		// if (btn == "yes") {
		// // 打开模版选择界面
		// this.openTemplateChooseWin();
		// }
		// },
		// scope : this
		// })
		// } else {
		// 载入指定病历
		var resData = phis.script.rmi.miniJsonRequestSync({
					serviceId : "emrManageService",
					serviceAction : "loadTemplateData",
					body : {
						"step" : 3,
						"CHTCODE" : 13
					}
				});

		this.emr.FunActiveXInterface("BsLoadTemplateDate",
				resData.json.gbkText, resData.json.uft8Text);
		// }
	},
	openTemplateChooseWin : function(node) {
		var tplModule = this.createModule("refTplChooseModule",
				this.refTplChooseModule);
		tplModule.node = this.node;
		var tpl_win = tplModule.getWin();
		tpl_win.setHeight(400);
		tpl_win.setWidth(300);
		this.tpl_win = tpl_win;
		tpl_win.show();
	},
	doLoadTemplate : function() {

	},
	doSetIll : function() {
		var r = this.emr.FunActiveXInterface("BsIllrecNewPara", 'Illrc_1_001',
				'测试段落');
		alert(r)
	},
	doLoadEMR : function() {
		// var tempDoc;
		var resData = phis.script.rmi.miniJsonRequestSync({
					serviceId : "emrManageService",
					serviceAction : "loadTemplateData",
					body : {
						"step" : 2,
						"BLBH" : 10304930,
						"CHTCODE" : 10000689
					}
				});
		// document.getElementById('hx').value=tempDoc
		this.emr
				.FunActiveXInterface("BsLoadCaseDocData", resData.json.BLNR, '');
		this.emr.FunActiveXInterface("BsLoadCaseDocXML",
				resData.json.XMLTEXTPAT, '');
	},
	doLoadRefItems : function() {
		var r = this.emr.FunActiveXInterface("BsGetReference", '', '');
		var s = this.emr.StrReturnData;
		if (r !== 0) {
			Ext.Msg.alert("加载引用元素出错!");
			return;
		}
		var s = "[" + s + "]";
		var arrObj = eval(s);
		var resData = phis.script.rmi.miniJsonRequestSync({
					serviceId : "emrManageService",
					serviceAction : "loadRefItems",
					body : {
						"items" : arrObj,
						"ZYH" : 2012078,
						"JZXH" : 123,
						"BRKS" : 31
					}
				});
		var refItems = resData.json.retItems;
		var refStr = Ext.encode(refItems);
		refStr = refStr.substring(1, refStr.length - 1);
		refStr = refStr.replace(/\"/g, "");
		this.emr.FunActiveXInterface("BsDoReference", '', refStr);
	},
	doIsEmpty : function() {
		var r = this.emr.FunActiveXInterface("BsCheckWordEmpty", '', '');
		alert(r)
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
	doLoadBlbh : function() {
		var resData = phis.script.rmi.miniJsonRequestSync({
					serviceId : "emrManageService",
					serviceAction : "loadBlbh"
				});
		alert(resData.json.BLBH)
	},
	doAction : function(item, e) {
		var cmd = item.cmd
		var script = item.script
		cmd = cmd.charAt(0).toUpperCase() + cmd.substr(1)
		if (script) {
			$require(script, [function() {
								eval(script + '.do' + cmd
										+ '.apply(this,[item,e])')
							}, this])
		} else {
			var action = this["do" + cmd]
			if (action) {
				action.apply(this, [item, e])
			}
		}
	},

	doUserFul : function() {
		var ufList = this.createModule("refUserFulList", this.refUserFulList,
				this.exContext);
		ufList.emr = this.emr;
		var uf_win = ufList.getWin();
		uf_win.add(ufList.initPanel());
		uf_win.setHeight(550);
		uf_win.setWidth(240);
		uf_win.setPosition(2000, 100);
		this.uf_win = uf_win;
		uf_win.show();
	},
	doAddTemp : function() {
		var BLBH = null;
		if (this.exContext != null && this.exContext.empiData != null) {
			BLBH = this.exContext.empiData.BLBH;
		}
		if (BLBH == null) {
			Ext.Msg.alert("提示", "病历不存在，请先保存病历!")
			return;
		}
		var s = this.emr.FunActiveXInterface("BsGetSelectionText", '', '');
		var txtData = this.emr.StrReturnData;

		s = this.emr.FunActiveXInterface("BsCheckSelectEmpty", '', '');
		if (s == 1) {
			Ext.Msg.alert("提示", "未选择内容，请选择!");
			return;
		}

		var limitLen = this.getUserFulLimitLength();
		if (txtData.length > limitLen) {
			Ext.Msg.alert("提示", "选择的内容长度超过常用语限制的长度，请重选!");
			return;
		}

		s = this.emr.FunActiveXInterface("BsCopySelectXmlRec", '', '');
		var textData = this.emr.WordData;
		var xmlData = this.emr.WordXML;

		var ufForm = this.createModule("refUserFulForm", this.refUserFulForm,
				this.exContext);
		ufForm.doNew();
		ufForm.txtData = textData;
		ufForm.xmlData = xmlData;
		var uff_win = ufForm.getWin();
		uff_win.add(ufForm.initPanel());
		this.uff_win = uff_win;
		uff_win.show();
	},
	getUserFulLimitLength : function() {
		// var r = phis.script.rmi.miniJsonRequestSync({
		// serviceId : "",
		// serviceAction : ""
		// });
		// if (r.code > 300) {
		// this.processReturnMsg(r.code, r.msg, this.getUserFulLimitLength);
		// return
		// }
		// if (r.json.body) {}
		return 1000;
	},

	doDataBox : function() {
		var BLBH = null;
		if (this.exContext != null && this.exContext.empiData != null) {
			BLBH = this.exContext.empiData.BLBH;
		}
		if (BLBH == null) {
			Ext.Msg.alert("提示", "病历不存在，请先保存病历!")
			return;
		}
		var dataBox = this.createModule("refDataBox", this.refDataBox,
				this.exContext);
		dataBox.emr = this.emr;
		var db_win = dataBox.getWin();
		db_win.setHeight(600);
		db_win.setWidth(1000);
		this.db_win = db_win;
		db_win.show();
	}
});