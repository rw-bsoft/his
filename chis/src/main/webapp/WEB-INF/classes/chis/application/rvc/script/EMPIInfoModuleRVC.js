$package("chis.application.rvc.script");

$import("chis.application.mpi.script.EMPIInfoModule");

chis.application.rvc.script.EMPIInfoModuleRVC = function(cfg) {
	chis.application.rvc.script.EMPIInfoModuleRVC.superclass.constructor
			.apply(this, [cfg]);
}

Ext.extend(chis.application.rvc.script.EMPIInfoModuleRVC,
		chis.application.mpi.script.EMPIInfoModule, {
			doSave : function() {
				var demoInfoView = this.midiModules[this.entryName];
				if (!demoInfoView.validate()) {
					return;
				}
				var empiForm = demoInfoView.form.getForm();
				var sexCode = empiForm.findField("sexCode").getValue();
				var sexCode_text = empiForm.findField("sexCode").getRawValue();
				var birthday = empiForm.findField("birthday").getValue();
				var age = demoInfoView.getAgeFromServer(birthday);
				var rvcAge;
				if (sexCode == 1) {
					rvcAge = 60;
					sexCode_text = "男性";
				} else if (sexCode == 2) {
					rvcAge = 55;
					sexCode_text = "女性";
				} else {
					rvcAge = 55;
					sexCode_text = sexCode_text + "性別的";
				}
				if (age < rvcAge) {
					Ext.Msg.alert("提示", sexCode_text + "离休干部年龄必须大于等于" + rvcAge
									+ "岁");
					return;
				}
				chis.application.rvc.script.EMPIInfoModuleRVC.superclass.doSave
						.call(this);
			}

		});