/**
 * 新生儿访视记录表单页面
 * 
 * @author : yaozh
 */
$package("chis.application.mhc.script.babyVisit")
$import("chis.script.BizTableFormView","chis.script.util.helper.Helper")
chis.application.mhc.script.babyVisit.BabyVisitRecordForm = function(cfg) {
	cfg.autoFieldWidth = false
	cfg.fldDefaultWidth = 120
	cfg.labelWidth = 100
	cfg.colCount = 4
	chis.application.mhc.script.babyVisit.BabyVisitRecordForm.superclass.constructor.apply(
			this, [cfg])
	this.on("beforeCreate", this.beforeCreate, this);
	this.on("loadData", this.onLoadData, this);

}
Ext.extend(chis.application.mhc.script.babyVisit.BabyVisitRecordForm, chis.script.BizTableFormView,
		{

			beforeCreate : function() {
				this.data.pregnantId = this.exContext.args.pregnantId;
				this.data.babyId = this.exContext.args.babyId;
				this.setWidgetStatus();
			},
			
			onLoadData:function(){
				var nextVisitDate = this.form.getForm().findField("nextVisitDate");
				var createDate =this.data["createDate"] ;
				var d = Date.parseDate(createDate.substr(0,10), "Y-m-d")
				nextVisitDate.setMinValue(chis.script.util.helper.Helper.getOneDayAfterDate(d));
				nextVisitDate.validate();
			},
			
			doNew:function(){
				chis.application.mhc.script.babyVisit.BabyVisitRecordForm.superclass.doNew.call(this);
				var nextVisitDate = this.form.getForm().findField("nextVisitDate");
				var now = Date.parseDate(this.mainApp.serverDate, "Y-m-d");
				nextVisitDate.setMinValue(now);
				nextVisitDate.validate();
			},

			onReady : function() {
				chis.application.mhc.script.babyVisit.BabyVisitRecordForm.superclass.onReady
						.call(this)

				var form = this.form.getForm();
				var face = form.findField("face")
				this.face = face
				face.on("select", this.onFace, this)
				face.on("blur", this.onFace, this)

				var bregmaStatus = form.findField("bregmaStatus")
				this.bregmaStatus = bregmaStatus
				bregmaStatus.on("select", this.onBregmaStatus, this)
				bregmaStatus.on("blur", this.onBregmaStatus, this)
				
				
				var eye = form.findField("eye")
				this.eye = eye
				eye.on("select", this.onEye, this)
				eye.on("blur", this.onEye, this)
				

				var ear = form.findField("ear")
				this.ear = ear
				ear.on("select", this.onEar, this)
				ear.on("blur", this.onEar, this)

				var nose = form.findField("nose")
				this.nose = nose
				nose.on("select", this.onNose, this)
				nose.on("blur", this.onNose, this)

				var mouse = form.findField("mouse")
				this.mouse = mouse
				mouse.on("select", this.onMouse, this)
				mouse.on("blur", this.onMouse, this)

				var heartlung = form.findField("heartlung")
				this.heartlung = heartlung
				heartlung.on("select", this.onHeartlung, this)
				heartlung.on("blur", this.onHeartlung, this)

				var abdominal = form.findField("abdominal")
				this.abdominal = abdominal
				abdominal.on("select", this.onAbdominal, this)
				abdominal.on("blur", this.onAbdominal, this)

				var limbs = form.findField("limbs")
				this.limbs = limbs
				limbs.on("select", this.onLimbs, this)
				limbs.on("blur", this.onLimbs, this)

				var neck = form.findField("neck")
				this.neck = neck
				neck.on("select", this.onNeck, this)
				neck.on("blur", this.onNeck, this)

				var skin = form.findField("skin")
				this.skin = skin
				skin.on("select", this.onSkin, this)
				skin.on("blur", this.onSkin, this)

				var anal = form.findField("anal")
				this.anal = anal
				anal.on("select", this.onAnal, this)
				anal.on("blur", this.onAnal, this)

				var genitalia = form.findField("genitalia")
				this.genitalia = genitalia
				genitalia.on("select", this.onGenitalia, this)
				genitalia.on("blur", this.onGenitalia, this)

				var spine = form.findField("spine")
				this.spine = spine
				spine.on("select", this.onSpine, this)
				spine.on("blur", this.onSpine, this)

				var umbilical = form.findField("umbilical")
				this.umbilical = umbilical
				umbilical.on("select", this.onUmbilical, this)
				umbilical.on("blur", this.onUmbilical, this)

				var referral = form.findField("referral")
				this.referral = referral
				referral.on("select", this.onReferral, this)
				referral.on("blur", this.onReferral, this)

			},

			initFormData : function(data) {
				chis.application.mhc.script.babyVisit.BabyVisitRecordForm.superclass.initFormData
						.call(this, data)
				this.setWidgetStatus()
			},

			setWidgetStatus : function() {
				this.onFace()
				this.onBregmaStatus()
				this.onEar()
				this.onEye()
				this.onNose()
				this.onMouse()
				this.onHeartlung()
				this.onAbdominal()
				this.onLimbs()
				this.onNeck()
				this.onSkin()
				this.onAnal()
				this.onGenitalia()
				this.onSpine()
				this.onUmbilical()
				this.onReferral()
			},

			onFace : function() {
				var form = this.form.getForm();
				if (this.face.getValue() == "9") {
					form.findField("faceOther").enable()
				} else {
					form.findField("faceOther").disable()
					form.findField("faceOther").setValue("")
				}
			},

			onBregmaStatus : function() {
				var form = this.form.getForm();
				if (this.bregmaStatus.getValue() == "4") {
					form.findField("otherStatus").enable()
				} else {
					form.findField("otherStatus").disable()
					form.findField("otherStatus").setValue("")
				}
			},
			
			onEye : function() {
				var form = this.form.getForm();
				if (this.eye.getValue() == "10") {
					form.findField("eyeAbnormal").enable()
				} else {
					form.findField("eyeAbnormal").disable()
					form.findField("eyeAbnormal").setValue("")
				}
			},

			onEar : function() {
				var form = this.form.getForm();
				if (this.ear.getValue() == "7") {
					form.findField("earAbnormal").enable()
				} else {
					form.findField("earAbnormal").disable()
					form.findField("earAbnormal").setValue("")
				}
			},

			onNose : function() {
				var form = this.form.getForm();
				if (this.nose.getValue() == "2") {
					form.findField("noseAbnormal").enable()
				} else {
					form.findField("noseAbnormal").disable()
					form.findField("noseAbnormal").setValue("")
				}
			},

			onMouse : function() {
				var form = this.form.getForm();
				if (this.mouse.getValue() == "14") {
					form.findField("mouseAbnormal").enable()
				} else {
					form.findField("mouseAbnormal").disable()
					form.findField("mouseAbnormal").setValue("")
				}
			},

			onHeartlung : function() {
				var form = this.form.getForm();
				if (this.heartlung.getValue() == "2") {
					form.findField("heartLungAbnormal").enable()
				} else {
					form.findField("heartLungAbnormal").disable()
					form.findField("heartLungAbnormal").setValue("")
				}
			},

			onAbdominal : function() {
				var form = this.form.getForm();
				if (this.abdominal.getValue() == "5") {
					form.findField("abdominalabnormal").enable()
				} else {
					form.findField("abdominalabnormal").disable()
					form.findField("abdominalabnormal").setValue("")
				}
			},

			onLimbs : function() {
				var form = this.form.getForm();
				if (this.limbs.getValue() == "2") {
					form.findField("limbsAbnormal").enable()
				} else {
					form.findField("limbsAbnormal").disable()
					form.findField("limbsAbnormal").setValue("")
				}
			},

			onNeck : function() {
				var form = this.form.getForm();
				if (this.neck.getValue() == "y") {
					form.findField("neck1").enable()
				} else {
					form.findField("neck1").disable()
					form.findField("neck1").setValue("")
				}
			},

			onSkin : function() {
				var form = this.form.getForm();
				if (this.skin.getValue() == "99") {
					form.findField("skinAbnormal").enable()
				} else {
					form.findField("skinAbnormal").disable()
					form.findField("skinAbnormal").setValue("")
				}
			},

			onAnal : function() {
				var form = this.form.getForm();
				if (this.anal.getValue() == "2") {
					form.findField("analAbnormal").enable()
				} else {
					form.findField("analAbnormal").disable()
					form.findField("analAbnormal").setValue("")
				}
			},

			onGenitalia : function() {
				var form = this.form.getForm();
				if (this.genitalia.getValue() == "2") {
					form.findField("genitaliaAbnormal").enable()
				} else {
					form.findField("genitaliaAbnormal").disable()
					form.findField("genitaliaAbnormal").setValue("")
				}
			},

			onSpine : function() {
				var form = this.form.getForm();
				if (this.spine.getValue() == "2") {
					form.findField("spineAbnormal").enable()
				} else {
					form.findField("spineAbnormal").disable()
					form.findField("spineAbnormal").setValue("")
				}
			},

			onUmbilical : function() {
				var form = this.form.getForm();
				if (this.umbilical.getValue() == "9") {
					form.findField("umbilicalOther").enable()
				} else {
					form.findField("umbilicalOther").disable()
					form.findField("umbilicalOther").setValue("")
				}
			},

			onReferral : function() {
				var form = this.form.getForm();
				if (this.referral.getValue() == "y") {
					form.findField("referralReason").enable()
					form.findField("referralUnit").enable()
				} else {
					form.findField("referralReason").disable()
					form.findField("referralReason").setValue("")
					form.findField("referralUnit").disable()
					form.findField("referralUnit").setValue("")
				}
			},

			doCancel : function() {
				this.fireEvent("cancel", this);
			}

		});