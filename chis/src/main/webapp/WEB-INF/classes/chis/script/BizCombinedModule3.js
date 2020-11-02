/**
 * 组合模块公共页面，支持 左边简单module，右边两个模块"左右"/"上下" 布局格式的组合
 * 
 * @author : yaozh
 */
$package("chis.script")
$import("chis.script.BizCombinedModule2")
chis.script.BizCombinedModule3 = function(cfg) {
	this.westWidth = 255 // ** 左边项的宽度
	chis.script.BizCombinedModule3.superclass.constructor.apply(this, [cfg])

}
Ext.extend(chis.script.BizCombinedModule3, chis.script.BizCombinedModule2, {

			/**
			 * 获取面板上的各项
			 * 
			 * @return {}
			 */
			getPanelItems : function() {
				var firstItem = this.getFirstItem();
				var secondItem = this.getSecondItem();
				var thirdItem = this.getThirdItem();
				var items = [{
							layout : "fit",
							border : false,
							frame : false,
							split : true,
							title : '',
							region : "west",
							width : this.westWidth,
							heigth : this.height,
							collapsible : this.itemCollapsible,
							items : firstItem
						}, {
							layout : "border",
							border : false,
							frame : true,
							split : true,
							title : '',
							region : "center",
							items : [{
										collapsible : this.itemCollapsible,
										layout : "fit",
										border : false,
										frame : false,
										split : true,
										title : '',
										region : this.layOutRegion,
										width : this.itemWidth || this.width,
										height : this.itemHeight || this.height,
										items : secondItem
									}, {
										layout : "fit",
										border : false,
										frame : false,
										split : true,
										title : '',
										region : "center",
										items : thirdItem
									}]
						}]
				return items;
			},
			keyManageFunc : function(keyCode, keyName) {
				for (var i = 0; i < this.actions.length; i++) {
					var m = this.midiModules[this.actions[i].id]
					if (m) {
						if (m.keyManageFunc) {
							m.keyManageFunc(keyCode, keyName);
						} else {
							if (m.btnAccessKeys) {
								var btn = m.btnAccessKeys[keyCode];
								if (btn && btn.disabled) {
									continue;
								}
							}
							if (!m.btnAccessKeys[keyCode]) {
								continue;
							}
							m.doAction(m.btnAccessKeys[keyCode]);
						}
					}
				}
			},

			/**
			 * 获取第三个面板
			 * 
			 * @return {}
			 */
			getThirdItem : function() {
				var firstModule = this.midiModules[this.actions[0].id];
				var buttonIndex = 0;
				if (firstModule) {
					buttonIndex = firstModule.actions.length;
				}
				var secondModule = this.midiModules[this.actions[1].id];
				if (secondModule) {
					buttonIndex = buttonIndex + secondModule.actions.length;
				}
				return this.getCombinedModule(this.actions[2].id,
						this.actions[2].ref, buttonIndex);
			}

		})