$package("phis.application.twr.script");

$import("phis.script.TableForm");

phis.application.twr.script.TwoWayReferralClinicFormLook = function(cfg) {
	this.colCount = cfg.colCount = 4;
	phis.application.twr.script.TwoWayReferralClinicFormLook.superclass.constructor
			.apply(this, [ cfg ]);
}
Ext.extend(phis.application.twr.script.TwoWayReferralClinicFormLook,
		phis.script.TableForm, {
			loadData : function() {
				var form = this.form.getForm();
				var brinfo = phis.script.rmi.miniJsonRequestSync({
					serviceId : "referralService",
					serviceAction : "queryMzScInfo",
					"ZHUANZHENDH" : this.zhuanzhendh
				});
				var br = brinfo.json.body;
				if (form) {
					if (br.BINGRENXM) {
						form.findField("BRXM").setValue(br.BINGRENXM);
					}
					if (br.BINGRENXB) {
						this.brxb = br.BINGRENXB;
						if (br.BINGRENXB == 0) {
							form.findField("BRXB").setValue("未知的性别");
						} else if (br.BINGRENXB == 1) {
							form.findField("BRXB").setValue("男");
						} else if (br.BINGRENXB == 2) {
							form.findField("BRXB").setValue("女");
						} else if (br.BINGRENXB == 9) {
							form.findField("BRXB").setValue("未说明的性别");
						}
					}
					if (br.BINGRENNL) {
						form.findField("NL").setValue(br.BINGRENNL);
					}
					if (br.BINGRENSFZH) {
						form.findField("SFZH").setValue(br.BINGRENSFZH);
					}
					if (br.BINGRENLXDH) {
						form.findField("LXDH").setValue(br.BINGRENLXDH);
					}
					if (br.BINGRENLXDZ) {
						form.findField("LXDZ").setValue(br.BINGRENLXDZ);
					}
					if (br.JBMC) {
						form.findField("ZZZD").setValue(br.JBMC);
					}
					if (br.ZHUANZHENYY) {
						form.findField("ZZYY").setValue(br.ZHUANZHENYY);
					}
					if (br.BINQINGMS) {
						form.findField("BQMS").setValue(br.BINQINGMS);
					}
					if (br.ZHUANZHENZYSX) {
						form.findField("ZLJG").setValue(br.ZHUANZHENZYSX);
					}
					if (br.SHENQINGJGMC) {
						form.findField("SQJG").setValue(br.SHENQINGJGMC);
					}
					if (br.SHENQINGJGLXDH) {
						form.findField("JGDH").setValue(br.SHENQINGJGLXDH);
					}
					if (br.SHENQINGYS) {
						form.findField("SQYS").setValue(br.SHENQINGYS);
					}
					if (br.SHENQINGYSDH) {
						form.findField("YSDH").setValue(br.SHENQINGYSDH);
					}
				}
			}
		})