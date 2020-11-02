$package("phis.application.twr.script");

$import("phis.script.TableForm");

phis.application.twr.script.DRExamineApplicationFormLook = function(cfg) {
	cfg.showButtonOnTop = true;
	this.colCount = cfg.colCount = 4;
	phis.application.twr.script.DRExamineApplicationFormLook.superclass.constructor
			.apply(this, [cfg]);
	this.brid = '';
	this.mzhm = '';
	this.csny = '';
	this.brxb = '';
}
Ext.extend(phis.application.twr.script.DRExamineApplicationFormLook,
		phis.script.TableForm, {
			loadData : function() {
				var form = this.form.getForm();
				var brinfo = phis.script.rmi.miniJsonRequestSync({
							serviceId : "referralService",
							serviceAction : "queryJcScInfoo",
							"JIANCHASQDH" : this.jianchasqdh
						});
				var br = brinfo.json.body;
				if (form) {
					if (br.BRID) {
						this.brid = br.BRID;
					}
					if (br.MZHM) {
						this.mzhm = br.MZHM;
					}
					if (br.BINGRENXM) {
						form.findField("BRXM").setValue(br.BINGRENXM);
					}
					if (br.BRXB) {
						this.brxb = br.BRXB;
						if (br.BRXB == 0) {
							form.findField("BRXB").setValue("未知的性别");
						} else if (br.BRXB == 1) {
							form.findField("BRXB").setValue("男");
						} else if (br.BRXB == 2) {
							form.findField("BRXB").setValue("女");
						} else if (br.BRXB == 9) {
							form.findField("BRXB").setValue("未说明的性别");
						}
					}
					if (br.AGE) {
						form.findField("NL").setValue(br.AGE);
					}
					if (br.BINGRENSFZH) {
						form.findField("SFZH").setValue(br.BINGRENSFZH);
					}
					if (br.LXDH) {
						form.findField("LXDH").setValue(br.LXDH);
					}
					if (br.LXDZ) {
						form.findField("LXDZ").setValue(br.LXDZ);
					}
					if (br.CSNY) {
						this.csny = br.CSNY;
					}
					if (br.JBMC) {
						form.findField("BRZD").setValue(br.JBMC);
					}
					if (br.BINGRENTZ) {
						form.findField("BRTZ").setValue(br.BINGRENTZ);
					}
					if (br.SONGJIANKSMC) {
						form.findField("SQJG").setValue(br.SONGJIANKSMC);
					}
					if (br.SONGJIANYS) {
						form.findField("SQYS").setValue(br.SONGJIANYS);
					}
					if (br.JIANCHAYYMC) {
						form.findField("JIANCHAYYMC").setValue(br.JIANCHAYYMC);
					}
					if (br.BINGQINGMS) {
						form.findField("BQMS").setValue(br.BINGQINGMS);
					}
					if (br.JIANCHAXMMC) {
						form.findField("JIANCHAXMMC").setValue(br.JIANCHAXMMC);
					}
					if (br.JIANCHATS) {
						form.findField("JCSM").setValue(br.JIANCHATS);
					}
				}
			}
		})