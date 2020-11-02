$package("phis.script.widgets")
var msgCt;
MyMessageTip = function() {
	function createBox(t, s) {
		return [
				'<div class="msg_his">',
				'<div class="x-box-tl"><div class="x-box-tr"><div class="x-box-tc"></div></div></div>',
				'<div class="x-box-ml"><div class="x-box-mr"><div class="x-box-mc" style="font-size=14px;line-height:150%"><h3>',
				t,
				'</h3>',
				s,
				'</div></div></div>',
				'<div class="x-box-bl"><div class="x-box-br"><div class="x-box-bc"></div></div></div>',
				'</div>'].join('');
	}
	return {
		msg : function(title, message, autoHide, pauseTime) {
			if (!msgCt) {
				msgCt = Ext.DomHelper.insertFirst(document.body, {
					id : 'msg-div22',
					style : 'position:absolute;top:5px;width:300px;margin-right:auto; margin-left:auto;z-index:20000;'
				}, true);

			}
			msgCt.alignTo(document, 't-t');
			// 给消息框右下角增加一个关闭按钮
			message += '<br>'
					+ '<a style="margin-left:230px;cursor:pointer;color:#FF8E40;" title="鼠标右键单击关闭" onclick="MyMessageTip.onContextmenu();" >关闭</a>'
			var m = Ext.DomHelper.append(msgCt, {
						html : createBox(title, message)
					}, true);
			m.slideIn('t');
			if (!Ext.isEmpty(autoHide) && autoHide == true) {
				if (Ext.isEmpty(pauseTime)) {
					pauseTime = 5;
				}
				m.pause(pauseTime).ghost("tr", {
							remove : true
						});
			}
			// 增加右键直接关闭提示信息功能
			m.on("contextmenu", this.onContextmenu, m);
		},
		hide : function(v) {
			var msg = Ext
					.get(v.parentElement.parentElement.parentElement.parentElement.parentElement.parentElement);
			msg.ghost("tr", {
						remove : true
					});
		},
		onContextmenu : function(e) {
			if (e) {
				e.stopEvent()
			}
			var msgs = Ext.get("msg-div22").query("div.msg_his")
			for (var i = 0; i < msgs.length; i++) {
				Ext.fly(msgs[i]).ghost("tr", {
							remove : true
						});
			}
			return;
		}
	};
}();