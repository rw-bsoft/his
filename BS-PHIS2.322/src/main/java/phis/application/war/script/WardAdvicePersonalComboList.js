$package("phis.application.war.script")

/**
 * 处方组套维护list zhangyq 2012.05.25
 */
$import("phis.script.SimpleList")

phis.application.war.script.WardAdvicePersonalComboList = function(cfg) {
	cfg.showRowNumber = true;
	// 根据ksdm查询列表的条件暂时不加
	cfg.cnds = [
			'and',
			[
					'and',
					[
							'and',
							['and', ['eq', ['$', 'ZTLB'], ['i', 1]],
									['eq', ['$', 'SSLB'], ['i', 2]]],
							['eq', ['$', 'JGID'], ["$", '%user.manageUnit.id']]],
					['eq', ['$', 'KSDM'], ["$", '%user.prop.wardId']]],
			['eq', ['$', 'SFQY'], ['i', 1]]];
	cfg.initCnd = [
			'and',
			[
					'and',
					[
							'and',
							['and', ['eq', ['$', 'ZTLB'], ['i', 1]],
									['eq', ['$', 'SSLB'], ['i', 2]]],
							['eq', ['$', 'JGID'], ["$", '%user.manageUnit.id']]],
					['eq', ['$', 'KSDM'], ["$", '%user.prop.wardId']]],
			['eq', ['$', 'SFQY'], ['i', 1]]];

	phis.application.war.script.WardAdvicePersonalComboList.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.war.script.WardAdvicePersonalComboList,
		phis.script.SimpleList, {
			openModule : function(cmd, r, xy) {
				phis.application.war.script.AdvicePersonalComboNameList.superclass.openModule
						.call(this, cmd, r, xy)
				var module = this.midiModules[cmd];
				module.SSLB = this.SSLB;
				module.ZTLB = this.ZTLB;
			},
			onRenderer : function(value, metaData, r) {
				var SFQY = r.get("SFQY");
				var src = (SFQY == 1) ? "yes" : "no";
				return "<img src='" + ClassLoader.appRootOffsetPath
						+ "resources/phis/resources/images/" + src + ".png'/>";
			},
			setButtonsState : function(m, enable) {
				var btns;
				var btn;
				if (this.showButtonOnTop) {
					btns = this.grid.getTopToolbar();
				} else {
					btns = this.grid.buttons;
				}

				if (!btns) {
					return;
				}
				if (this.showButtonOnTop) {
					for (var j = 0; j < m.length; j++) {
						if (!isNaN(m[j])) {
							btn = btns.items.item(m[j]);
						} else {
							btn = btns.find("cmd", m[j]);
							btn = btn[0];
						}
						if (btn) {
							(enable) ? btn.enable() : btn.disable();
						}
					}
				} else {
					for (var j = 0; j < m.length; j++) {
						if (!isNaN(m[j])) {
							btn = btns[m[j]];
						} else {
							for (var i = 0; i < this.actions.length; i++) {
								if (this.actions[i].id == m[j]) {
									btn = btns[i];
								}
							}
						}
						if (btn) {
							(enable) ? btn.enable() : btn.disable();
						}
					}
				}
			}

		})
