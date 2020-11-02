$package("phis.application.cfg.script")

/**
 * 医院库房维护from zhangyq 2012.5.25
 * 
 */
$import("phis.script.TableForm", "phis.script.cookie.CookieOperater")

phis.application.cfg.script.ConfigModuleShortkeyForm = function(cfg) {
	cfg.colCount = 1;
	this.cookie = util.cookie.CookieOperater;
	phis.application.cfg.script.ConfigModuleShortkeyForm.superclass.constructor
			.apply(this, [cfg])
	this.on("winShow", this.onWinShow, this);
}
Ext.extend(phis.application.cfg.script.ConfigModuleShortkeyForm,
		phis.script.TableForm, {
			onReady : function() {
				phis.application.cfg.script.ConfigModuleShortkeyForm.superclass.onReady
						.call(this)
			},
			onWinShow : function() {
				var f = this.form.getForm();
				this.oldKey = null;
				var quickInfo = this.cookie.getCookie(this.mainApp.uid + "_"
						+ this.mainApp.jobtitleId + "_" + this.curPanel._mId);
				if (quickInfo) {
					quickInfo = Ext.decode(quickInfo);
					f.findField("moduleId").setValue(quickInfo._mId);
					f.findField("moduleName").setValue(quickInfo.title);
					f.findField("shortcutKey").setValue(quickInfo.shortcutKey);
					f.findField("shortcutKey").focus(false, 200);
					this.oldKey = quickInfo.shortcutKey;
				} else {
					f.findField("moduleId").setValue(this.curPanel._mId);
					f.findField("moduleName").setValue(this.curPanel.title);
					f.findField("shortcutKey").setValue(null);
					f.findField("shortcutKey").focus(false, 200);
				}
			},
			doSave : function() {
				var f = this.form.getForm();
				var _mId = f.findField("moduleId").getValue();
				var title = f.findField("moduleName").getValue();
				var shortcutKey = f.findField("shortcutKey").getValue();
				if (!shortcutKey) {
					MyMessageTip.msg("提示", "请输入需要绑定的快捷键!", true);
					return;
				}
				var info = {};
				info._mId = _mId;
				info.title = title;
				info.shortcutKey = shortcutKey;
				// 删除原有的绑定信息
				if (this.oldKey) {
					this.cookie.delCookie(this.mainApp.uid + "_"
							+ this.mainApp.jobtitleId + "_" + this.oldKey);
				}
				var otherInfo = this.cookie.getCookie(this.mainApp.uid + "_"
						+ this.mainApp.jobtitleId + "_" + shortcutKey);
				otherInfo = Ext.decode(otherInfo);
				if (otherInfo && otherInfo._mId != _mId) {// 已经被其它模块绑定
					Ext.Msg.alert("提示", "当前快捷键\"" + shortcutKey + "\"已被【"
									+ otherInfo.title + "】模块绑定，请使用其它快捷键!");
					return;
				}
				this.cookie.setCookie(this.mainApp.uid + "_"
								+ this.mainApp.jobtitleId + "_"
								+ this.curPanel._mId, Ext.encode(info));
				this.cookie.setCookie(this.mainApp.uid + "_"
								+ this.mainApp.jobtitleId + "_" + shortcutKey,
						Ext.encode(info));
				MyMessageTip.msg("提示", "当前模块已成功绑定快捷键!", true);
				this.win.hide();
			}

		})