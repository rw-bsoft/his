$package("app.bia.hc")
$import("chis.script.BizTableFormView")

chis.application.hc.script.HealthExaminationForm = function(cfg) {
	cfg.labelAlign = "left";
	cfg.colCount = 4;
	cfg.showButtonOnTop = true;
	cfg.autoFieldWidth = false
	cfg.fldDefaultWidth = 115;
	cfg.labelWidth = 90;
	cfg.autoLoadSchema = false;
	cfg.autoLoadData = false;
	cfg.isCombined = true;
	cfg.showButtonOnTop = true;
	chis.application.hc.script.HealthExaminationForm.superclass.constructor.apply(this, [cfg]);
	this.on("loadData",this.onLoadData,this);
}

Ext.extend(chis.application.hc.script.HealthExaminationForm, chis.script.BizTableFormView, {
			onReady : function() {
				this.other = ["fundus", "breathSound", "abdominAltend",
						"adbominAlmass", "liverBig", "splenomegaly",
						"dullness", "vulva", "vaginal", "cervix", "palace",
						"attachment", "heartMurmur"]
				chis.application.hc.script.HealthExaminationForm.superclass.onReady.call(this)

				this.arr = ["skin", "sclera", "lymphnodes", "breast", "dre",
						"rales"]
				this.arrValue = {
					skin:7,
					sclera:4,
					lymphnodes:4,
					breast:7,
					dre:5,
					rales:4
				};
				var form = this.form.getForm();
				for (var i = 0; i < this.arr.length; i++) {
					var field = form.findField(this.arr[i])
					if (field) {
						field.on("select", this.onOther, this);
						field.on("blur", this.onOther, this);
					}
				}
				for (var i = 0; i < this.other.length; i++) { // yub
					var field = form.findField(this.other[i]);
					if (field) {
						field.on("select", this.OnotherDiseasesone, this);
						field.on("blur", this.OnotherDiseasesone, this);
					}
				}
			},

			onOther : function(comb, r, index) {
				var value = comb.getValue();
				var combName = comb.name;
				var form = this.form.getForm();
				var other = form.findField(this.getDesc(combName))
				if (other)
					if (value.indexOf(this.arrValue[combName]) != -1) {
						other.enable();
					} else {
						other.reset();
						other.disable();
					}
			},

			OnotherDiseasesone : function(field) { // yub
				var value = field.getValue();
				var fieldName = field.name
				var desc = this.form.getForm().findField(fieldName + "Desc");
				if (desc) {
					if (value.indexOf("2") != -1) { // 有
						desc.enable()
					} else {
						desc.reset()
						desc.disable()
					}
				}
			},

			getDesc : function(f) { // yub
				return f + "Desc";
			},

			onLoadData : function(entryName, body) {
				var form = this.form.getForm();
				for (var i = 0; i < this.arr.length; i++) {
					var field = form.findField(this.arr[i]);
					if(field){
						this.onOther(field);
					}
				}
				for (var i = 0; i < this.other.length; i++) { // yub
					var field = form.findField(this.other[i]);
					if (field) {
						this.OnotherDiseasesone(field);
					}
				}
			},

			getSaveRequest : function(saveData) {
				saveData.healthCheck = this.exContext.args.healthCheck;
				return saveData;
			},

			getLoadRequest : function() {
				return {
					"healthCheck" : this.exContext.args.healthCheck
				}
			}
		})