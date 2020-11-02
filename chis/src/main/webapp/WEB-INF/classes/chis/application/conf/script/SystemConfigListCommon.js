/**
 * 公共文件(表单页面公共函数)
 * 
 * @author : yaozh
 */
$package("chis.application.conf.script")

chis.application.conf.script.SystemConfigListCommon = {

	/**
	 * 控制列表按钮权限
	 */
	resetButtonsReadOnly : function() {
		if (!this.grid) {
			return
		}
		if (this.showButtonOnTop) {
			var btns = this.grid.getTopToolbar().items;
			if (!btns) {
				return;
			}
			var n = btns.getCount()
			for (var i = 0; i < n; i++) {
				var btn = btns.item(i)
				if (btn.type != "button"  ||  btn.prop.notReadOnly) {
					continue;
				}
				var status = this.readOnly;
				if (status == null) {
					return;
				}
				this.changeButtonStatus(btn, !status);
			}
		} else {
			var btns = this.grid.buttons
			if (!btns) {
				return;
			}
			for (var i = 0; i < btns.length; i++) {
				var btn = btns[i]
				if (btn.prop.notReadOnly) {
					continue;
				}
				var status = this.readOnly;
				if (status == null) {
					return;
				}
				this.changeButtonStatus(btn, !status);
			}
		}
	}
}