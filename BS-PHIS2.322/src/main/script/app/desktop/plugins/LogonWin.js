$package("app.desktop.plugins")
$import("app.viewport.Logon")

app.desktop.plugins.LogonWin = function(config) {
	this.forConfig = false
	this.requestDefineDeep = 3
	app.desktop.plugins.LogonWin.superclass.constructor.apply(this, [config]);
}

Ext.extend(app.desktop.plugins.LogonWin, app.viewport.Logon, {
	init : function() {

	},

	getWin : function() {
		if (this.win) {
			return this.win;
		}
		var win = new Ext.Window({
			autoWidth : true,
			frame : false,
			border : false,
			autoHeight : true,
			resizable : false,
			modal : true,
			shim : true,
			closable : false,
			html : '<div class="login2">'
					+ '<table cellpadding="0" cellspacing="0" border="0" >'
					+ '<tr>'
					+ '<td width="15%">用户名:</td><td class="inputs2"><img src="resources/phis/css/images/user.png" class="ico2" /><label>'
					+ this.mainApp.uname
					+ '</label></td>'
					+ '</tr>'
					+ '<tr>'
					+ '<td>密&nbsp;&nbsp;&nbsp;码:</td><td class="inputs2"><img src="resources/phis/css/images/key.png" class="ico2" /><input id="password" name="password" type="password" style="width:185px;height:26px" /></td>'
					+ '</tr>'
					+ '<tr>'
					+ '<td>角&nbsp;&nbsp;&nbsp;色:</td><td class="inputs2"><img src="resources/phis/css/images/jiao.png" class="ico2" /><label>'
					+ this.mainApp.jobtitle
					+ '</label></td>'
					+ '</tr>'
					+ '<tr>'
					+ '<td>机&nbsp;&nbsp;&nbsp;构:</td><td class="inputs2"><img src="resources/phis/css/images/ji.png" class="ico2" /><label>'
					+ this.mainApp.dept
					+ '</label></td>'
					+ '</tr>'
					+ '<tr>'
					+ '<td>&nbsp;</td><td><label><a id="logonIn" class="loginBtn2"></a></label></td>'
					+ '</tr></table></div>'
		})

		win.doLayout();
		win.on("afterrender", this.initCmp, this)
		win.on("close", this.doCanel, this);
		win.on("show", this.onWinShow, this)
		this.win = win;
		return win

	},

	initCmp : function(win) {
		// var user = Ext.fly("logonuser")
		// if (this.mainApp) {
		// this.uid = this.mainApp.uid
		// }
		// if (this.uid) {
		// user.dom.value = this.uid
		// }

		var pws = Ext.fly("password")
		pws.dom.value = ""
		pws.dom.focus()
		pws.on("keypress", function(e) {
					if (e.getKey() == e.ENTER) {
						this.doLogon();
					}
				}, this)
		var logon = Ext.fly("logonIn")
		logon.on("click", this.doLogon, this)

	},

	onWinShow : function() {
		var pws = Ext.fly("password")
		pws.dom.value = ""
		pws.focus(20);
		if (this.mainApp) {
			this.mainApp.desktop.fireEvent("winLock")
		}
	},

	doLogon : function() {
		var uid = this.mainApp.uid// Ext.fly("logonuser").dom.value
		var pwd = Ext.fly("password").dom.value
		var urt = ""
		if (this.mainApp) {
			urt = this.mainApp.urt
		}
		if (!uid || !pwd || !urt) {
			return;
		}
		var key = this.getPublicKey();
		if (key) {
			pwd = util.codec.RSAUtils.encryptedString(key, pwd)
		}
		this.doLoadAppDefines(uid, pwd, urt)
		// var json = util.rmi.miniJsonRequestSync({
		// url : 'logon/myRoles',
		// uid : uid,
		// pwd : pwd
		// })
		// if (json.code == 200) {
		// var res = util.rmi.miniJsonRequestSync({
		// url:'logon/extInfo',
		// body: userDomain
		// })
		// this.doLoadAppDefines(urt)
		// }

	},

	doLoadAppDefines : function(uid, pwd, urt, forceLogon) {
		util.rmi.miniJsonRequestAsync({
					url : "logon/myApps?urt=" + urt + "&uid=" + uid + "&pwd="
							+ pwd + "&deep=" + this.requestDefineDeep
							+ "&forceLogon=" + (forceLogon || 0),
					httpMethod : "POST"
				}, function(code, msg, json) {
					if (code == 200) {
						this.win.hide()
						this.mainApp.desktop.initLogininfo();
						this.fireEvent("appsDefineLoaded")
					} else if (code == 404) {
						this.messageDialog("错误", "用户不存在或已禁用",
								Ext.MessageBox.ERROR)
					} else if (code == 501) {
						this.messageDialog("错误", "密码错误", Ext.MessageBox.ERROR)
					} else if (code == 502) {
						Ext.Msg.alert("提示", "登录失败：当前用户已在别处登录!");
					} else if (code == 503) {
						Ext.Msg.confirm("提示", "当前用户已在别处登录，是否强制登录？", function(
										btn) {
									if (btn == 'yes') {
										this.doLoadAppDefines(uid, pwd, urt, 1)
									}
								}, this);
					}
				}, this)
	},

	doCanel : function() {
		if (this.mainApp) {
			this.mainApp.desktop.fireEvent("winUnlock")
		}
		this.fireEvent("logonCancel")
		return true;
	}

})
