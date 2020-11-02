/**
 * 家庭档案-->问题列表-->新建或查看表单页面
 * 
 * @author tianj
 */
$package("chis.application.fhr.script");

$import("chis.script.BizTableFormView");

chis.application.fhr.script.JsdqyxxForm = function(cfg) {
	chis.application.fhr.script.JsdqyxxForm.superclass.constructor.apply(this, [cfg]);
}

Ext.extend(chis.application.fhr.script.JsdqyxxForm, chis.script.BizTableFormView, {
});