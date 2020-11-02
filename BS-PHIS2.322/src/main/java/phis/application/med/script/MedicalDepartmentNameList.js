$package("phis.application.med.script");
$import("phis.script.SimpleList", "phis.script.widgets.MyMessageTip",
		"phis.script.util.DateUtil");
phis.application.med.script.MedicalDepartmentNameList = function(cfg) {
//    this.cnds="['eq', ['$', 'a.JGID'],['s', this.mainApp['phisApp'].deptId]]";
	
	phis.application.med.script.MedicalDepartmentNameList.superclass.constructor.apply(this,
			[cfg]);
},

Ext.extend(phis.application.med.script.MedicalDepartmentNameList, phis.script.SimpleList,
		{
			onDblClick : function(grid, index, e) {
				var actions = this.actions
				if (!actions) {
					return;
				}
				this.selectedIndex = index
				var item = {};
				for (var i = 0; i < actions.length; i++) {
					var action = actions[i]
					var cmd = action.id
					if (cmd == "update" || cmd == "read") {
						item.text = action.name
						item.cmd = action.id
						item.ref = action.ref
						item.script = action.script
						if (cmd == "update") {
							break
						}
					}
				}
				if (item.cmd) {
					this.doAction(item, e)
				}
				this.opener.test();
			}

		});