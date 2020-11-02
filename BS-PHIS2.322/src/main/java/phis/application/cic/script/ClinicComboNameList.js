$package("phis.application.cic.script")

$import("phis.script.SimpleList")

phis.application.cic.script.ClinicComboNameList = function(cfg) {
	
	phis.application.cic.script.ClinicComboNameList.superclass.constructor
			.apply(this, [cfg])
}

Ext.extend(phis.application.cic.script.ClinicComboNameList,
		phis.script.SimpleList, {
			expansion : function(cfg) {
				var radioGroup = new Ext.form.RadioGroup({
							width:180,
							items : [
							{boxLabel : '个人常用项目',inputValue:'1',name: 'project'},
							{boxLabel : '个人组套',inputValue:'2',name: 'project'}
							]
						});
				var tbar = cfg.tbar;
				cfg.tbar = [];
				cfg.tbar.push(radioGroup,['-'],tbar);
			}
		})
			