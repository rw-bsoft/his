$package("phis.application.war.script")
$import("app.desktop.Module", "util.widgets.MyRadioGroup",
		"util.dictionary.TreeDicFactory", "util.helper.Helper")

phis.application.war.script.OrderCardsInjectionCard = function(cfg) {
	this.printurl = util.helper.Helper.getUrl();
	phis.application.war.script.OrderCardsInjectionCard.superclass.constructor.apply(
			this, [cfg])
}
Ext.extend(phis.application.war.script.OrderCardsInjectionCard, app.desktop.Module, {

	initPanel : function() {
		this.frameId = "SimplePrint_frame_phis.prints.jrxml.OrderCardsInjectionCard";
		this.conditionFormId = "SimplePrint_form_phis.prints.jrxml.OrderCardsInjectionCard";
		this.mainFormId = "SimplePrint_mainform_phis.prints.jrxml.OrderCardsInjectionCard";

		var panel = new Ext.Panel({
			id : this.mainFormId,
			width : this.width,
			height : this.height,
			tbar : {},
			html : "<iframe id='"
					+ this.frameId
					+ "' border=0 width='100%' height='100%' onload='simplePrintMask(\"phis.prints.jrxml.OrderCardsInjectionCard\")'></iframe>"
		})
		this.panel = panel

		// 获取组件都触发了哪些事件
		// Ext.util.Observable.capture(prompt, function(e, node) {
		// console.debug(e, node,34)
		// })
		return panel
	},
	/**
	 * 依据激活的医嘱卡片类型,即yzlb的值 发送对应卡片报表请求
	 * 
	 * @param {}
	 *            arr_zyh 选中医嘱病人的住院号数组
	 * @param {}
	 *            yzlb 值2:口服卡,3:注射卡,4:静滴卡,0:临时医嘱
	 */
	doActivePanel : function(arr_zyh, yzlb, typeValue, orderTypeValue) { 
		// wardName 病区名称
		// wardId 病区代码
		 if(arr_zyh=="" || arr_zyh==null){
		 	document.getElementById(this.frameId).src = '';
			 return
		 }
		var serviceId = "phis.prints.jrxml.OrderCardsInjectionCard";
		if (typeValue == 2) {
			serviceId = "phis.prints.jrxml.OrderCardsMouthCardZX";// 因口服卡、注射卡、静滴卡的执行单格式相同
												// 所以采用同一Service
		}
		var pages=serviceId;
		 var url="resources/"+pages+".print?yzlb=" + yzlb + "&orderTypeValue=" + orderTypeValue
				+ "&arr_zyh=" + arr_zyh.toString() + "&wardName="
				+ encodeURI(encodeURI(this.mainApp['phis'].wardName)) + "&wardId="
				+ this.mainApp['phis'].wardId+"&dysj=" + document.getElementsByName("zykpdydateFrom")[0].value;
		Ext.getCmp(this.mainFormId).el.mask("正在生成报表...", "x-mask-loading")

		document.getElementById(this.frameId).src = url
	}
})

simplePrintMask = function(printId) {
	Ext.getCmp("SimplePrint_mainform_" + printId).el.unmask()
}