$package("phis.application.cfg.script")
/**
 * ��ҩ;��ά��from zhangyq 2012.05.25
 * 
 */
$import("phis.script.SimpleForm", "phis.script.cookie.CookieOperater")

phis.application.cfg.script.ConfigHibitsForm = function(cfg) {
	cfg.colCount = 1;
	cfg.width = 400;
	this.entryName = "phis.application.cfg.schemas.SYS_UserHabits";
	this.cookie = util.cookie.CookieOperater;
	phis.application.cfg.script.ConfigHibitsForm.superclass.constructor.apply(
			this, [cfg])
	this.on("winShow", this.onWinShow, this);
}
Ext.extend(phis.application.cfg.script.ConfigHibitsForm, phis.script.SimpleForm,
		{
			onReady : function() {
				var searchTypeField = this.form.getForm()
						.findField("searchType");
				var isfirst = true;
				searchTypeField.store.on("load", function() {
							var searchTypeField = this.form.getForm()
									.findField("searchType");
							if (isfirst && searchTypeField.store.getCount() > 0) {
								searchTypeField.setValue(this.cookie
										.getCookie(this.mainApp.uid
												+ "_searchType")
										|| "PYDM");
							}
							isfirst = false;
						}, this);

				var matchTypeField = this.form.getForm().findField("matchType");
				var isfirst_m = true;
				matchTypeField.store.on("load", function() {
							var matchTypeField = this.form.getForm()
									.findField("matchType");
							if (isfirst_m
									&& matchTypeField.store.getCount() > 0) {
								matchTypeField.setValue(this.cookie
										.getCookie(this.mainApp.uid
												+ "_matchType")
										|| "ALL");
							}
							isfirst_m = false;
						}, this);
			},
			doSave : function() {
				var searchTypeField = this.form.getForm()
						.findField("searchType");
				var searchTypeValue = searchTypeField.getValue();
				if (searchTypeValue == "") {
					searchTypeValue = "PYDM";
				}
				searchTypeField.setValue(searchTypeValue);
				this.cookie.setCookie(this.mainApp.uid + "_searchType",
						searchTypeValue);

				var matchTypeField = this.form.getForm().findField("matchType");
				var matchTypeValue = matchTypeField.getValue();
				if (matchTypeValue == "") {
					matchTypeValue = "ALL";
				}
				matchTypeField.setValue(matchTypeValue);
				this.cookie.setCookie(this.mainApp.uid + "_matchType",
						matchTypeValue);
				MyMessageTip.msg("提示", "用户习惯设置成功!", true);
				this.doCancel();
			},
			onWinShow : function() {
				var searchTypeField = this.form.getForm()
						.findField("searchType");
				var searchTypeValue = this.cookie.getCookie(this.mainApp.uid
						+ "_searchType");
				if (searchTypeValue) {
					searchTypeField.setValue(searchTypeValue);
				}
			},
			getWin : function() {
				var win = this.win
				if (!win) {
					win = new Ext.Window({
								title : this.title || this.name,
								width : this.width,
								autoHeight : this.autoHeight || true,
								iconCls : 'icon-form',
								bodyBorder : false,
								closeAction : 'hide',
								shim : true,
								layout : "fit",
								plain : true,
								autoScroll : false,
								// minimizable: true,
								maximizable : true,
								constrain : true,
								shadow : false,
								buttonAlign : 'center',
								modal : true,
								items : this.initPanel()
							})
					win.on({
								beforehide : this.confirmSave,
								beforeclose : this.confirmSave,
								scope : this
							})
					win.on("show", function() {
								this.fireEvent("winShow")
							}, this)
					win.on("close", function() {
								this.fireEvent("close", this)
							}, this)
					win.on("hide", function() {
								this.fireEvent("close", this)
							}, this)
					win.on("restore", function(w) {
								this.form.onBodyResize()
								this.form.doLayout()
								this.win.doLayout()
							}, this)

					var renderToEl = this.getRenderToEl()
					if (renderToEl) {
						win.render(renderToEl)
					}
					this.win = win
				}
				this.afterCreateWin(win);
				return win;
			}
		});