$package("chis.application.hy.script.baseline");

$import("chis.script.BizFieldSetFormView");

chis.application.hy.script.baseline.HyBaselineForm = function(cfg){
	cfg.showButtonOnTop = true;
	cfg.labelAlign = "left";
	cfg.labelWidth = 190;
	cfg.autoFieldWidth = true;
	cfg.fldDefaultWidth = 120;
	cfg.colCount = 4;
	chis.application.hy.script.baseline.HyBaselineForm.superclass.constructor.apply(this,[cfg]);
	this.saveServiceId = "chis.hypertensionBaselineService";
	this.saveAction = "saveHyBaseline";
	this.on("doNew", this.onDoNew, this);
	this.on("save",this.onSave,this);
};

Ext.extend(chis.application.hy.script.baseline.HyBaselineForm,chis.script.BizFieldSetFormView,{
	doNew : function() {
		chis.application.hy.script.baseline.HyBaselineForm.superclass.doNew.call(this);
		var form = this.form.getForm();		
		if (this.exContext.ids.empiId) {
			this.data["empiId"] = this.exContext.ids.empiId;
			var empiId = this.exContext.ids.empiId;
			var phrId = this.exContext.ids.phrId;	
			var form = this.form.getForm();		
			form.findField("empiId").setValue(empiId);
			form.findField("phrId").setValue(phrId);
			var result = util.rmi.miniJsonRequestSync({
				serviceId : this.saveServiceId,
				serviceAction : "getHealthCheckInfo",
				method : "execute",
				body : {
					empiId : empiId
				}
			});
			if(result.code < 300){
				var healthCheckInfoList = result.json.healthCheckInfo;
				if(healthCheckInfoList.length > 0){
					var healthCheckInfo = healthCheckInfoList[0];
					form.findField("BASAJZYM").setValue(healthCheckInfo.BASAJZYM);
					form.findField("DMDZDBDGC").setValue(healthCheckInfo.DMDZDBDGC);
					form.findField("GMDZDBDGC").setValue(healthCheckInfo.GMDZDBDGC);
					form.findField("GYSZ").setValue(healthCheckInfo.GYSZ);
					if(healthCheckInfo.JYNSFJJ != null){
						form.findField("JYNSFJJ").setValue(healthCheckInfo.JYNSFJJ);
					}
					form.findField("KFXT").setValue(healthCheckInfo.KFXT);
					form.findField("KSYJNL").setValue(healthCheckInfo.KSYJNL);
					if(healthCheckInfo.SFJJ != null){
						form.findField("SFJJ").setValue(healthCheckInfo.SFJJ);
					}
					form.findField("SFJJYJJJJNL").setValue(healthCheckInfo.SFJJYJJJJNL);
					form.findField("SG").setValue(healthCheckInfo.SG);
					form.findField("TMDASAJZYM").setValue(healthCheckInfo.TMDASAJZYM);
					form.findField("TZ").setValue(healthCheckInfo.TZ);
					form.findField("TZZS").setValue(healthCheckInfo.TZZS);
					if(healthCheckInfo.XDT != null){
						form.findField("XDT").setValue(healthCheckInfo.XDT);
					}
					if(healthCheckInfo.XDTYC != null){
						form.findField("XDTYC").setValue(healthCheckInfo.XDTYC);
					}
					form.findField("XJ").setValue(healthCheckInfo.XJ);
					form.findField("XJG").setValue(healthCheckInfo.XJG);
					form.findField("XL").setValue(healthCheckInfo.XL);
					form.findField("XN").setValue(healthCheckInfo.XN);
					form.findField("XNSD").setValue(healthCheckInfo.XNSD);
					form.findField("XYZK").setValue(healthCheckInfo.XYZK);
					form.findField("XYZKJYKSNL").setValue(healthCheckInfo.XYZKJYKSNL);
					form.findField("XYZKJYMTJZ").setValue(healthCheckInfo.XYZKJYMTJZ);
					form.findField("XYZKJYNL").setValue(healthCheckInfo.XYZKJYNL);
					if(healthCheckInfo.YJQK != null){
						form.findField("YJQK").setValue(healthCheckInfo.YJQK);
					}
					form.findField("YJQKORMTJL").setValue(healthCheckInfo.YJQKORMTJL);
					if(healthCheckInfo.YJZL != null){
						form.findField("YJZL").setValue(healthCheckInfo.YJZL);
					}
					form.findField("YJZLQT").setValue(healthCheckInfo.YJZLQT);
					form.findField("ZDGC").setValue(healthCheckInfo.ZDGC);
				}		
				var medicineInfoList = result.json.medicineInfo;
				for(var n=0; n<medicineInfoList.length;n++){
					var medicineInfo = medicineInfoList[n];		
					form.findField("YW"+(n+1)+"MC").setValue(medicineInfo.YWMC);
					form.findField("YW"+(n+1)+"YF").setValue(medicineInfo.YWYF);
					form.findField("YW"+(n+1)+"JL").setValue(medicineInfo.YWJL);
					form.findField("YW"+(n+1)+"SJ").setValue(medicineInfo.YWSJ);
					if(medicineInfo.YWYCX != null){
						form.findField("YW"+(n+1)+"YCX").setValue(medicineInfo.YWYCX);		
					}			
				}
			}
		}		
		var GXY = form.findField("GXY");
		GXY.on("select", this.onGXYSelect, this);
		
		var SFFYJYY = form.findField("SFFYJYY");
		SFFYJYY.on("select", this.onSFFYJYYSelect, this);
		
		var TNB = form.findField("TNB");
		TNB.on("select", this.onTNBSelect, this);
		
		var GZXZ = form.findField("GZXZ");
		GZXZ.on("select", this.onGZXZSelect, this);
		
		var JWYWGXB = form.findField("JWYWGXB");
		JWYWGXB.on("select", this.onJWYWGXBSelect, this);
		
		var JWYWNZZ = form.findField("JWYWNZZ");
		JWYWNZZ.on("select", this.onJWYWNZZSelect, this);
		
		var YWQTJB = form.findField("YWQTJB");  
		YWQTJB.on("select", this.onYWQTJBSelect, this);

		var XYZK = form.findField("XYZK");  
		XYZK.on("change", this.onXYZKSelect, this);
		
		var YJQK = form.findField("YJQK");  
		YJQK.on("select", this.onYJQKSelect, this);
		
		var SFJJ = form.findField("SFJJ");   
		SFJJ.on("select", this.onSFJJSelect, this);
		
		var SG = form.findField("SG");   
		SG.on("change", this.calculateBMI, this);
		
		var TZ = form.findField("TZ");   
		TZ.on("change", this.calculateBMI, this);
		
		var XJG = form.findField("XJG");
		XJG.on("change", this.calculateeGFR, this);
		
		var XDT = form.findField("XDT");
		XDT.on("change", this.onXDTSelect, this);
		
		var SFYY = form.findField("SFYY");
		SFYY.on("change", this.onSFYYSelect, this);
	
		var JXXJGS = form.findField("JXXJGS");
		JXXJGS.on("select",this.onJXXJGSSelect,this);

		var BWDXJT = form.findField("BWDXJT"); 
		BWDXJT.on("select",this.onBWDXJTSelect,this);

		var XYCJ = form.findField("XYCJ"); 
		XYCJ.on("select",this.onXYCJSelect,this);

		var XSZYZL = form.findField("XSZYZL"); 
		XSZYZL.on("select",this.onXSZYZLSelect,this);

		var NZZ = form.findField("NZZ"); 
		NZZ.on("select",this.onNZZSelect,this);
		
		var NLJBS = form.findField("NLJBS"); 
		NLJBS.on("change",this.onNLJBSSelect,this);
		
		var BLSJJLJYN = form.findField("BLSJJLJYN"); 
		BLSJJLJYN.on("change",this.onBLSJJLJYNSelect,this);
	},	
	//高血压关联控制
	onGXYSelect : function(){
		debugger;
		var form = this.form.getForm();
		var GXY = form.findField("GXY");
		var GXYZDSJ = form.findField("GXYZDSJ"); 
		var SFFYJYY = form.findField("SFFYJYY");
		var KSYYSJ = form.findField("KSYYSJ"); 
		if(GXY.getValue() == "1"){
			GXYZDSJ.allowBlank = false;
			GXYZDSJ.enable();
			SFFYJYY.allowBlank = false;
			SFFYJYY.enable()
			KSYYSJ.allowBlank = false;
			KSYYSJ.enable();
		}else{
			GXYZDSJ.disable();
			GXYZDSJ.allowBlank = true;
			SFFYJYY.setValue("2");
			SFFYJYY.disable();
			SFFYJYY.allowBlank = true;
			KSYYSJ.disable();
			KSYYSJ.allowBlank = true;
			GXYZDSJ.reset();
			KSYYSJ.reset();
		}
	},
	
	//是否服用降压药关联
	onSFFYJYYSelect:function(){
		debugger;
		var form = this.form.getForm();
		var SFFYJYY = form.findField("SFFYJYY");
		var KSYYSJ = form.findField("KSYYSJ"); 
		if(SFFYJYY.getValue() == "1"){
			KSYYSJ.enable();
			KSYYSJ.allowBlank = false;
		}else{
			KSYYSJ.disable();
			KSYYSJ.allowBlank = true;
			KSYYSJ.reset();
		}	
	},
	
	//糖尿病关联控制
	onTNBSelect : function(){
		debugger;
		var form = this.form.getForm();
		var TNB = form.findField("TNB");
		var TNBZDSJ = form.findField("TNBZDSJ"); 
		var SFFYJTY = form.findField("SFFYJTY");
		if(TNB.getValue() == "1"){
			TNBZDSJ.allowBlank = false;
			TNBZDSJ.enable()
			SFFYJTY.allowBlank = false;
			SFFYJTY.enable();
		}else{
			TNBZDSJ.disable();
			TNBZDSJ.allowBlank = true;
			SFFYJTY.setValue("2");
			SFFYJTY.disable();
			SFFYJTY.allowBlank = true;
			TNBZDSJ.reset();
		}
	},
	
	//高血脂症关联控制
	onGZXZSelect : function(){
		debugger;
		var form = this.form.getForm();
		var GZXZ = form.findField("GZXZ");
		var GZXZZDSJ = form.findField("GZXZZDSJ"); 
		var GZXZSFYY = form.findField("GZXZSFYY");
		if(GZXZ.getValue() == "1"){
			GZXZZDSJ.allowBlank = false;
			GZXZZDSJ.enable();
			GZXZSFYY.allowBlank = false;
			GZXZSFYY.enable();
		}else{
			GZXZZDSJ.disable();
			GZXZZDSJ.allowBlank = true;
			GZXZSFYY.setValue("2");
			GZXZSFYY.disable();
			GZXZSFYY.allowBlank = true;
			GZXZZDSJ.reset();	
		}
	},
	
	//既往有无冠心病关联控制
	onJWYWGXBSelect : function(){
		debugger;
		var form = this.form.getForm();
		var JWYWGXB = form.findField("JWYWGXB");
		var GXBZDSJ = form.findField("GXBZDSJ"); 
		if(JWYWGXB.getValue() == "1"){
			GXBZDSJ.allowBlank = false;
			GXBZDSJ.enable();
		}else{
			GXBZDSJ.reset();
			GXBZDSJ.disable();
			GXBZDSJ.allowBlank = true;
		}
		
	},
	
	//既往有无脑卒中关联控制
	onJWYWNZZSelect : function(){
		debugger;
		var form = this.form.getForm();
		var JWYWNZZ = form.findField("JWYWNZZ");
		var NZZZDSJ = form.findField("NZZZDSJ"); 
		if(JWYWNZZ.getValue() == "1"){
			NZZZDSJ.allowBlank = false;
			NZZZDSJ.enable();
		}else{
			NZZZDSJ.reset();	
			NZZZDSJ.disable();
			NZZZDSJ.allowBlank = true;
		}	
	},
	
	//有无其他系统疾病关联控制
	onYWQTJBSelect : function(){
		debugger;
		var form = this.form.getForm();
		var YWQTJB = form.findField("YWQTJB");
		var YWQTJBJT = form.findField("YWQTJBJT"); 
		if(YWQTJB.getValue() == "1"){
			YWQTJBJT.allowBlank = false;
			YWQTJBJT.enable();		
		}else{
			YWQTJBJT.reset();
			YWQTJBJT.disable();
			YWQTJBJT.allowBlank = true;
		}	
	},
	
	//吸烟联动
	onXYZKSelect : function(){
		debugger
		var form = this.form.getForm();
		var XYZK = form.findField("XYZK");
		var XYZKJYKSNL = form.findField("XYZKJYKSNL");
		var XYZKJYMTJZ = form.findField("XYZKJYMTJZ");
		var XYZKJYNL = form.findField("XYZKJYNL");
		if(XYZK.getValue() == "1"){
			XYZKJYKSNL.disable();
			XYZKJYMTJZ.disable();
			XYZKJYNL.disable();	
			
			XYZKJYKSNL.allowBlank = true;
			XYZKJYMTJZ.allowBlank = true;
			XYZKJYNL.allowBlank = true;
			
			XYZKJYKSNL.reset();
			XYZKJYMTJZ.reset();
			XYZKJYNL.reset();

		}else if(XYZK.getValue() == "2"){
			XYZKJYKSNL.enable();
			XYZKJYMTJZ.enable();
			XYZKJYNL.enable();		
			
			XYZKJYKSNL.allowBlank = false;
			XYZKJYMTJZ.allowBlank = false;
			XYZKJYNL.allowBlank = false;
			
		}else{
			XYZKJYKSNL.enable();
			XYZKJYMTJZ.enable();
			XYZKJYKSNL.allowBlank = false;
			XYZKJYMTJZ.allowBlank = false;			
			XYZKJYNL.disable();	
			XYZKJYNL.reset();
		}	
	},
	
	//饮酒联动
	onYJQKSelect : function(){
		debugger
		var form = this.form.getForm();
		var YJQK = form.findField("YJQK");
		var YJQKORMTJL = form.findField("YJQKORMTJL");
		var SFJJ = form.findField("SFJJ");
		var SFJJYJJJJNL = form.findField("SFJJYJJJJNL");
		var KSYJNL = form.findField("KSYJNL");
		var JYNSFJJ = form.findField("JYNSFJJ");
		var YJZL = form.findField("YJZL");
		var YJZLQT = form.findField("YJZLQT");
		if(YJQK.getValue() == "1"){
			YJQKORMTJL.disable();
			SFJJ.disable();
			SFJJYJJJJNL.disable();	
			KSYJNL.disable();
			JYNSFJJ.disable();
			YJZL.disable();	
			YJZLQT.disable();	
			
			YJQKORMTJL.allowBlank = true;
			SFJJ.allowBlank = true;
			SFJJYJJJJNL.allowBlank = true;	
			KSYJNL.allowBlank = true;
			JYNSFJJ.allowBlank = true;
			YJZL.allowBlank = true;	
			YJZLQT.allowBlank = true;	
			
			YJQKORMTJL.reset();
			SFJJ.reset();
			SFJJYJJJJNL.reset();
			KSYJNL.reset();
			JYNSFJJ.reset();
			YJZL.reset();
			YJZLQT.reset();	
		}else{
			YJQKORMTJL.enable();
			SFJJ.enable();
			SFJJYJJJJNL.enable();	
			KSYJNL.enable();
			JYNSFJJ.enable();
			YJZL.enable();	
			YJZLQT.enable();	
			
			YJQKORMTJL.allowBlank = false;
			SFJJ.allowBlank = false;
			SFJJYJJJJNL.allowBlank = false;	
			KSYJNL.allowBlank = false;
			JYNSFJJ.allowBlank = false;
			YJZL.allowBlank = false;	
		}	
	},
	
	//戒酒联动
	onSFJJSelect : function(){
		debugger
		var form = this.form.getForm();
		var SFJJ = form.findField("SFJJ");
		var SFJJYJJJJNL = form.findField("SFJJYJJJJNL");

		if(SFJJ.getValue() == "2"){
			SFJJYJJJJNL.enable();
			SFJJYJJJJNL.allowBlank = false;
		}else{
			SFJJYJJJJNL.disable();
			SFJJYJJJJNL.allowBlank = true;
			SFJJYJJJJNL.reset();
		}	
	},
	
	//BMI计算联动
	calculateBMI : function(){
		debugger
		var form = this.form.getForm();
		var SG = form.findField("SG").getValue();
		var TZ = form.findField("TZ").getValue();
		var TZZS = form.findField("TZZS");
		if(SG != null && SG != 0){
			var Bmi = TZ/(Math.pow(SG/100, 2));
			TZZS.setValue(Bmi);
		}
	},	
	
	//BMI计算联动
	calculateeGFR : function(){
		debugger
		var form = this.form.getForm();
		var age = this.exContext.empiData.age;
		var sexCode = this.exContext.empiData.sexCode;
		var SXQLGL = form.findField("SXQLGL"); 
		var XJG = form.findField("XJG").getValue(); 
		var eGFR = 0;
		if(sexCode == 2){
			eGFR = (186*(Math.pow((XJG/88.4),-1.154))*(Math.pow(age,-0.203))*1.227*0.742).toFixed(2);
		}else{
			eGFR = (186*(Math.pow((XJG/88.4),-1.154))*(Math.pow(age,-0.203))*1.227).toFixed(2);
		}
		SXQLGL.setValue(eGFR);
	},	
	
	onXDTSelect : function(){
		debugger
		var form = this.form.getForm();
		var XDT = form.findField("XDT");
		var XDTYC = form.findField("XDTYC");

		if(XDT.getValue() == "1"){
			XDTYC.disable();
			XDTYC.allowBlank = true;
			XDTYC.reset();
		}else if(XDT.getValue() == "2"){
			XDTYC.enable();
			XDTYC.allowBlank = false;
		}else{
			XDTYC.enable();
			XDTYC.allowBlank = true;
			XDTYC.reset();
		}	
	},
	
	onSFYYSelect : function(){
		debugger
		var form = this.form.getForm();
		var SFYY = form.findField("SFYY");
		var JDFY = form.findField("JDFY");
		var SFYYYES = form.findField("SFYYYES");
		
		var YW1MC = form.findField("YW1MC");
		var YW1YF = form.findField("YW1YF");
		var YW1JL = form.findField("YW1JL");
		var YW1SJ = form.findField("YW1SJ");
		var YW1YCX = form.findField("YW1YCX");
		
		var YW2MC = form.findField("YW2MC");
		var YW2YF = form.findField("YW2YF");
		var YW2JL = form.findField("YW2JL");
		var YW2SJ = form.findField("YW2SJ");
		var YW2YCX = form.findField("YW2YCX");
		
		var YW3MC = form.findField("YW3MC");
		var YW3YF = form.findField("YW3YF");
		var YW3JL = form.findField("YW3JL");
		var YW3SJ = form.findField("YW3SJ");
		var YW3YCX = form.findField("YW3YCX");
		
		var YW4MC = form.findField("YW4MC");
		var YW4YF = form.findField("YW4YF");
		var YW4JL = form.findField("YW4JL");
		var YW4SJ = form.findField("YW4SJ");
		var YW4YCX = form.findField("YW4YCX");
		
		var YW5MC = form.findField("YW5MC");
		var YW5YF = form.findField("YW5YF");
		var YW5JL = form.findField("YW5JL");
		var YW5SJ = form.findField("YW5SJ");
		var YW5YCX = form.findField("YW5YCX");
		
		var YW6MC = form.findField("YW6MC");
		var YW6YF = form.findField("YW6YF");
		var YW6JL = form.findField("YW6JL");
		var YW6SJ = form.findField("YW6SJ");
		var YW6YCX = form.findField("YW6YCX");

		if(SFYY.getValue() == "1"){
			YW1MC.enable();
			YW1YF.enable();
			YW1JL.enable();
			YW1SJ.enable();
			YW1YCX.enable();
					
			YW2MC.enable();
			YW2YF.enable();
			YW2JL.enable();
			YW2SJ.enable();
			YW2YCX.enable();
					
			YW3MC.enable();
			YW3YF.enable();
			YW3JL.enable();
			YW3SJ.enable();
			YW3YCX.enable();
					
			YW4MC.enable();
			YW4YF.enable();
			YW4JL.enable();
			YW4SJ.enable();
			YW4YCX.enable();
					
			YW5MC.enable();
			YW5YF.enable();
			YW5JL.enable();
			YW5SJ.enable();
			YW5YCX.enable();
					
			YW6MC.enable();
			YW6YF.enable();
			YW6JL.enable();
			YW6SJ.enable();
			YW6YCX.enable();
			
			JDFY.enable();
			SFYYYES.enable();
			
			YW1MC.allowBlank = false;
			YW1YF.allowBlank = false;
			YW1JL.allowBlank = false;
			YW1SJ.allowBlank = false;
			YW1YCX.allowBlank = false;
			JDFY.allowBlank = false;
			SFYYYES.allowBlank = false;
		}else{
			YW1MC.disable();
			YW1YF.disable();
			YW1JL.disable();
			YW1SJ.disable();
			YW1YCX.disable();
					
			YW2MC.disable();
			YW2YF.disable();
			YW2JL.disable();
			YW2SJ.disable();
			YW2YCX.disable();
					
			YW3MC.disable();
			YW3YF.disable();
			YW3JL.disable();
			YW3SJ.disable();
			YW3YCX.disable();
					
			YW4MC.disable();
			YW4YF.disable();
			YW4JL.disable();
			YW4SJ.disable();
			YW4YCX.disable();
					
			YW5MC.disable();
			YW5YF.disable();
			YW5JL.disable();
			YW5SJ.disable();
			YW5YCX.disable();
					
			YW6MC.disable();
			YW6YF.disable();
			YW6JL.disable();
			YW6SJ.disable();
			YW6YCX.disable();
			SFYYYES.disable();
			JDFY.disable();
			
			YW1MC.reset();
			YW1YF.reset();
			YW1JL.reset();
			YW1SJ.reset();
			YW1YCX.reset();
					
			YW2MC.reset();
			YW2YF.reset();
			YW2JL.reset();
			YW2SJ.reset();
			YW2YCX.reset();
					
			YW3MC.reset();
			YW3YF.reset();
			YW3JL.reset();
			YW3SJ.reset();
			YW3YCX.reset();
					
			YW4MC.reset();
			YW4YF.reset();
			YW4JL.reset();
			YW4SJ.reset();
			YW4YCX.reset();
					
			YW5MC.reset();
			YW5YF.reset();
			YW5JL.reset();
			YW5SJ.reset();
			YW5YCX.reset();
					
			YW6MC.reset();
			YW6YF.reset();
			YW6JL.reset();
			YW6SJ.reset();
			YW6YCX.reset();
			
			SFYYYES.reset();
			JDFY.reset();
			
			YW1MC.allowBlank = true;
			YW1YF.allowBlank = true;
			YW1JL.allowBlank = true;
			YW1SJ.allowBlank = true;
			YW1YCX.allowBlank = true;
			JDFY.allowBlank = true;
			SFYYYES.allowBlank = true;
		}
	},
	
	onJXXJGSSelect : function(){
		debugger
		var form = this.form.getForm();
		var JXXJGS = form.findField("JXXJGS");
		var JXXJGSYES = form.findField("JXXJGSYES");
		var JXXJGSFBRQ = form.findField("JXXJGSFBRQ");
		if(JXXJGS.getValue() == "1"){
			JXXJGSYES.enable();
			JXXJGSFBRQ.enable();
			JXXJGSYES.allowBlank = false;
			JXXJGSFBRQ.allowBlank = false;
		}else{
			JXXJGSYES.disable();
			JXXJGSFBRQ.disable();
			JXXJGSYES.allowBlank = true;
			JXXJGSFBRQ.allowBlank = true;
			JXXJGSYES.reset();
			JXXJGSFBRQ.reset();
		}	
	},
	
	onBWDXJTSelect : function(){
		debugger
		var form = this.form.getForm();
		var BWDXJT = form.findField("BWDXJT");
		var BWDXJTYES = form.findField("BWDXJTYES");
		var BWDXJTFBRQ = form.findField("BWDXJTFBRQ");
		if(BWDXJT.getValue() == "1"){
			BWDXJTYES.enable();
			BWDXJTFBRQ.enable();
			BWDXJTYES.allowBlank = false;
			BWDXJTFBRQ.allowBlank = false;
		}else{
			BWDXJTYES.disable();
			BWDXJTFBRQ.disable();
			BWDXJTYES.allowBlank = true;
			BWDXJTFBRQ.allowBlank = true;
			BWDXJTYES.reset();
			BWDXJTFBRQ.reset();
		}	
	},
	
	onXSZYZLSelect : function(){
		debugger
		var form = this.form.getForm();
		var XSZYZL = form.findField("XSZYZL");
		var XSFJ = form.findField("XSFJ");
		var XSZYZLFBRQ = form.findField("XSZYZLFBRQ");
		if(XSZYZL.getValue() == "1"){
			XSFJ.enable();
			XSZYZLFBRQ.enable();
			XSFJ.allowBlank = false;
			XSZYZLFBRQ.allowBlank = false;
		}else{
			XSFJ.disable();
			XSZYZLFBRQ.disable();
			XSFJ.allowBlank = true;
			XSZYZLFBRQ.allowBlank = true;
			XSFJ.reset();
			XSZYZLFBRQ.reset();
		}	
	},
	
	onXYCJSelect : function(){
		debugger
		var form = this.form.getForm();
		var XYCJ = form.findField("XYCJ");
		var XYCJRGY = form.findField("XYCJRGY");
		var XYCJRQ = form.findField("XYCJRQ");
		if(XYCJ.getValue() == "1"){
			XYCJRGY.enable();
			XYCJRQ.enable();
			XYCJRGY.allowBlank = false;
			XYCJRQ.allowBlank = false;
		}else{
			XYCJRGY.disable();
			XYCJRQ.disable();
			XYCJRGY.allowBlank = true;
			XYCJRQ.allowBlank = true;
			XYCJRGY.reset();
			XYCJRQ.reset();
		}	
	},
	
	onNZZSelect : function(){
		debugger
		var form = this.form.getForm();
		var NZZ = form.findField("NZZ");
		var NZZSFZY = form.findField("NZZSFZY");
		var NZZFBRQ = form.findField("NZZFBRQ");
		var NZZSFZYZDYJ = form.findField("NZZSFZYZDYJ");
		var NZZFL = form.findField("NZZFL");
		if(NZZ.getValue() == "1"){
			NZZSFZY.enable();
			NZZFBRQ.enable();
			NZZSFZYZDYJ.enable();
			NZZFL.enable();
			NZZSFZY.allowBlank = false;
			NZZFBRQ.allowBlank = false;
			NZZSFZYZDYJ.allowBlank = false;
			NZZFL.allowBlank = false;
		}else{
			NZZSFZY.disable();
			NZZFBRQ.disable();
			NZZSFZYZDYJ.disable();
			NZZFL.disable();
			NZZSFZY.allowBlank = true;
			NZZFBRQ.allowBlank = true;
			NZZSFZYZDYJ.allowBlank = true;
			NZZFL.allowBlank = true;
			NZZSFZY.reset();
			NZZFBRQ.reset();
			NZZSFZYZDYJ.reset();
			NZZFL.reset();
		}	
	},
	
	onNLJBSSelect : function(){
		debugger
		var form = this.form.getForm();
		var NLJBS = form.findField("NLJBS");
		var NLJBSZDRQ = form.findField("NLJBSZDRQ");
		var NLJBSJBZD = form.findField("NLJBSJBZD");
		if(NLJBS.getValue() == "1"){
			NLJBSZDRQ.enable();
			NLJBSJBZD.enable();
			NLJBSZDRQ.allowBlank = false;
			NLJBSJBZD.allowBlank = false;
		}else{
			NLJBSZDRQ.disable();
			NLJBSJBZD.disable();
			NLJBSZDRQ.allowBlank = true;
			NLJBSJBZD.allowBlank = true;
			NLJBSZDRQ.reset();
			NLJBSJBZD.reset();
		}	
	},
	
	onBLSJJLJYNSelect : function(){
		debugger
		var form = this.form.getForm();
		var BLSJJLJYN = form.findField("BLSJJLJYN");
		var BLSJMC1 = form.findField("BLSJMC1");
		var BLSJFSSJ1 = form.findField("BLSJFSSJ1");
		var BLSJJSSJ1 = form.findField("BLSJJSSJ1");
		var BLSJYZCD1 = form.findField("BLSJYZCD1");
		var BLSJSFJDFS1 = form.findField("BLSJSFJDFS1");
		var BLSJCL1 = form.findField("BLSJCL1");
		var BLSJYWJL1 = form.findField("BLSJYWJL1");
		var BLSJJYYWGX1 = form.findField("BLSJJYYWGX1");
		var BLSJZG1 = form.findField("BLSJZG1");
		
		var BLSJMC2 = form.findField("BLSJMC2");
		var BLSJFSSJ2 = form.findField("BLSJFSSJ2");
		var BLSJJSSJ2 = form.findField("BLSJJSSJ2");
		var BLSJYZCD2 = form.findField("BLSJYZCD2");
		var BLSJSFJDFS2 = form.findField("BLSJSFJDFS2");
		var BLSJCL2 = form.findField("BLSJCL2");
		var BLSJYWJL2 = form.findField("BLSJYWJL2");
		var BLSJJYYWGX2 = form.findField("BLSJJYYWGX2");
		var BLSJZG2 = form.findField("BLSJZG2");
		
		var BLSJMC3 = form.findField("BLSJMC3");
		var BLSJFSSJ3 = form.findField("BLSJFSSJ3");
		var BLSJJSSJ3 = form.findField("BLSJJSSJ3");
		var BLSJYZCD3 = form.findField("BLSJYZCD3");
		var BLSJSFJDFS3 = form.findField("BLSJSFJDFS3");
		var BLSJCL3 = form.findField("BLSJCL3");
		var BLSJYWJL3 = form.findField("BLSJYWJL3");
		var BLSJJYYWGX3 = form.findField("BLSJJYYWGX3");
		var BLSJZG3 = form.findField("BLSJZG3");		
		
		if(BLSJJLJYN.getValue() == "1"){
			BLSJMC1.enable();
			BLSJFSSJ1.enable();
			BLSJJSSJ1.enable();
			BLSJYZCD1.enable();
			BLSJSFJDFS1.enable();
			BLSJCL1.enable();
			BLSJYWJL1.enable();
			BLSJJYYWGX1.enable();
			BLSJZG1.enable();
			
			BLSJMC2.enable();
			BLSJFSSJ2.enable();
			BLSJJSSJ2.enable();
			BLSJYZCD2.enable();
			BLSJSFJDFS2.enable();
			BLSJCL2.enable();
			BLSJYWJL2.enable();
			BLSJJYYWGX2.enable();
			BLSJZG2.enable();
					
			BLSJMC3.enable();
			BLSJFSSJ3.enable();
			BLSJJSSJ3.enable();
			BLSJYZCD3.enable();
			BLSJSFJDFS3.enable();
			BLSJCL3.enable();
			BLSJYWJL3.enable();
			BLSJJYYWGX3.enable();
			BLSJZG3.enable();
					
			BLSJMC1.allowBlank = false;
			BLSJFSSJ1.allowBlank = false;
			BLSJJSSJ1.allowBlank = false;
			BLSJYZCD1.allowBlank = false;
			BLSJSFJDFS1.allowBlank = false;
			BLSJCL1.allowBlank = false;
			BLSJYWJL1.allowBlank = false;
			BLSJJYYWGX1.allowBlank = false;
			BLSJZG1.allowBlank = false;
		}else{
			BLSJMC1.disable();
			BLSJFSSJ1.disable();
			BLSJJSSJ1.disable();
			BLSJYZCD1.disable();
			BLSJSFJDFS1.disable();
			BLSJCL1.disable();
			BLSJYWJL1.disable();
			BLSJJYYWGX1.disable();
			BLSJZG1.disable();
			
			BLSJMC2.disable();
			BLSJFSSJ2.disable();
			BLSJJSSJ2.disable();
			BLSJYZCD2.disable();
			BLSJSFJDFS2.disable();
			BLSJCL2.disable();
			BLSJYWJL2.disable();
			BLSJJYYWGX2.disable();
			BLSJZG2.disable();
					
			BLSJMC3.disable();
			BLSJFSSJ3.disable();
			BLSJJSSJ3.disable();
			BLSJYZCD3.disable();
			BLSJSFJDFS3.disable();
			BLSJCL3.disable();
			BLSJYWJL3.disable();
			BLSJJYYWGX3.disable();
			BLSJZG3.disable();

			BLSJMC1.allowBlank = true;
			BLSJFSSJ1.allowBlank = true;
			BLSJJSSJ1.allowBlank = true;
			BLSJYZCD1.allowBlank = true;
			BLSJSFJDFS1.allowBlank = true;
			BLSJCL1.allowBlank = true;
			BLSJYWJL1.allowBlank = true;
			BLSJJYYWGX1.allowBlank = true;
			BLSJZG1.allowBlank = true;
			
			BLSJMC1.reset();
			BLSJFSSJ1.reset();
			BLSJJSSJ1.reset();
			BLSJYZCD1.reset();
			BLSJSFJDFS1.reset();
			BLSJCL1.reset();
			BLSJYWJL1.reset();
			BLSJJYYWGX1.reset();
			BLSJZG1.reset();
			
			BLSJMC2.reset();
			BLSJFSSJ2.reset();
			BLSJJSSJ2.reset();
			BLSJYZCD2.reset();
			BLSJSFJDFS2.reset();
			BLSJCL2.reset();
			BLSJYWJL2.reset();
			BLSJJYYWGX2.reset();
			BLSJZG2.reset();
					
			BLSJMC3.reset();
			BLSJFSSJ3.reset();
			BLSJJSSJ3.reset();
			BLSJYZCD3.reset();
			BLSJSFJDFS3.reset();
			BLSJCL3.reset();
			BLSJYWJL3.reset();
			BLSJJYYWGX3.reset();
			BLSJZG3.reset();
		}	
	},
	

	doAdd : function() {
		this.fireEvent("add");
	},
	
	onDoNew : function() {
		this.data.empiId = this.exContext.ids.empiId;
		this.data.phrId = this.exContext.ids.phrId;
	},
	
	onReady : function() {
		chis.application.hy.script.baseline.HyBaselineForm.superclass.onReady.call(this);
		var form = this.form.getForm();
	},
	
	onSave:function(schmema,op,body){
		if(body.code < 300){
			var form = this.form.getForm();
			var items = form.items.items;
			var empiData = this.exContext.empiData;
//			empiData.idCard = "140522198909012015";
//			empiData.personName = "张三";
//			
//			empiData.idCard = "342623199012206136";
//			empiData.personName = "李四";
			var pdata = "{\"idCard\":\""+empiData.idCard+"\"," +
						"\"name\":\""+empiData.personName+"\"," +
						"\"sexcode\":\""+empiData.sexCode_text+"\"," +
						"\"birthday\":\""+empiData.birthday+"\"," +
						"\"age\":\""+empiData.age+"\"," +
						"\"education\":\""+empiData.educationCode_text+"\"," +
						"\"nation\":\""+empiData.nationCode_text+"\"," +
						"\"address\":\""+empiData.address+"\"," +
						"\"phone\":\""+empiData.mobileNumber+"\"," +
						"\"contactPeople\":\""+empiData.contact+"\"," +
						"\"iecPhone\":\""+empiData.contactPhone+"\",";
			
			var YYSSY = "";
			var YYSZY = "";
			var YESSY = "";
			var YESZY = "";
			var ZYSSY = "";
			var ZYSZY = "";
			var ZESSY = "";
			var ZESZY = "";
			
			for(var n=0;n<items.length;n++){					
				if(typeof items[n].items != "undefined"){
					var subItems = items[n].items.items;
					var str = "";
					for(var i=0; i<subItems.length;i++){
						if(subItems[i].checked){
							str += subItems[i].boxLabel + ",";
						}
					}
					str = str.substring(0, str.length-1);
					pdata += "\""+items[n].name+"\":\""+str+"\",";	
				}else{
					if(typeof items[n].lastSelectionText != "undefined"){
						pdata += "\""+items[n].name+"\":\""+items[n].lastSelectionText+"\",";	
					}else{
						if(items[n].name == "YYSSY"){
							YYSSY = items[n].value
						}else if(items[n].name == "YYSZY"){
							YYSZY = items[n].value
						}else if(items[n].name == "YESSY"){
							YESSY = items[n].value
						}else if(items[n].name == "YESZY"){
							YESZY = items[n].value
						}else if(items[n].name == "ZYSSY"){
							ZYSSY = items[n].value
						}else if(items[n].name == "ZYSZY"){
							ZYSZY = items[n].value
						}else if(items[n].name == "ZESSY"){
							ZESSY = items[n].value
						}else if(items[n].name == "ZESZY"){
							ZESZY = items[n].value
						}else{
							pdata += "\""+items[n].name+"\":\""+items[n].value+"\",";	
						}					
					}					
				}
			}
			pdata += "\"YSZDY\":\""+YYSSY+"/"+YYSZY+"\",";	
			pdata += "\"YSZDE\":\""+YESSY+"/"+YESZY+"\",";	
			pdata += "\"ZSZDY\":\""+ZYSSY+"/"+ZYSZY+"\",";	
			pdata += "\"ZSZDE\":\""+ZESSY+"/"+ZESZY+"\",";	
			
			pdata = pdata.replace("\"YW1MC\"","\"DRUGS\": [{\"YWMC\"");
			
			pdata = pdata.substring(0,pdata.length-1);
			pdata += "}";
			debugger;			
			pdata = pdata.replace("\"YW1MC\"","\"DRUGS\": [{\"YWMC\"");
			pdata = pdata.replace(",\"YW2MC\"","},{\"YWMC\"");
			pdata = pdata.replace(",\"YW3MC\"","},{\"YWMC\"");
			pdata = pdata.replace(",\"YW4MC\"","},{\"YWMC\"");
			pdata = pdata.replace(",\"YW5MC\"","},{\"YWMC\"");
			pdata = pdata.replace(",\"YW6MC\"","},{\"YWMC\"");
			pdata = pdata.replace(",\"JXXJGS\"","}],\"JXXJGS\"");
			pdata = pdata.replace(/YW1/g,"YW");
			pdata = pdata.replace(/YW2/g,"YW");
			pdata = pdata.replace(/YW3/g,"YW");
			pdata = pdata.replace(/YW4/g,"YW");
			pdata = pdata.replace(/YW5/g,"YW");
			pdata = pdata.replace(/YW6/g,"YW");
			
			pdata = pdata.replace("\"BLSJMC1\"","\"BLSJS\":[{\"BLSJMC\"");
			pdata = pdata.replace(",\"BLSJMC2\"","},{\"BLSJMC\"");
			pdata = pdata.replace(",\"BLSJMC3\"","},{\"BLSJMC\"");
			pdata = pdata.replace(",\"manaUnitId\"","}],\"manaUnitId\"");
			
			pdata = pdata.replace("BLSJFSSJ1","BLSJFSSJ");
			pdata = pdata.replace("BLSJFSSJ2","BLSJFSSJ");
			pdata = pdata.replace("BLSJFSSJ3","BLSJFSSJ");
			
			pdata = pdata.replace("BLSJJSSJ1","BLSJJSSJ");
			pdata = pdata.replace("BLSJJSSJ2","BLSJJSSJ");
			pdata = pdata.replace("BLSJJSSJ3","BLSJJSSJ");
			
			pdata = pdata.replace("BLSJYZCD1","BLSJYZCD");
			pdata = pdata.replace("BLSJYZCD2","BLSJYZCD");
			pdata = pdata.replace("BLSJYZCD3","BLSJYZCD");
			
			pdata = pdata.replace("BLSJSFJDFS1","BLSJSFJDFS");
			pdata = pdata.replace("BLSJSFJDFS2","BLSJSFJDFS");
			pdata = pdata.replace("BLSJSFJDFS3","BLSJSFJDFS");
			
			pdata = pdata.replace("BLSJCL1","BLSJCL");
			pdata = pdata.replace("BLSJCL2","BLSJCL");
			pdata = pdata.replace("BLSJCL3","BLSJCL");
			
			pdata = pdata.replace("BLSJYWJL1","BLSJYWJL");
			pdata = pdata.replace("BLSJYWJL2","BLSJYWJL");
			pdata = pdata.replace("BLSJYWJL3","BLSJYWJL");
			
			pdata = pdata.replace("BLSJJYYWGX1","BLSJJYYWGX");
			pdata = pdata.replace("BLSJJYYWGX2","BLSJJYYWGX");
			pdata = pdata.replace("BLSJJYYWGX3","BLSJJYYWGX");
			
			pdata = pdata.replace("BLSJZG1","BLSJZG");
			pdata = pdata.replace("BLSJZG2","BLSJZG");
			pdata = pdata.replace("BLSJZG3","BLSJZG");
			
			pdata = pdata.replace("createDate","createdate");
			var result = util.rmi.miniJsonRequestSync({
				serviceId : this.saveServiceId,
				serviceAction : "getJxdcUrl_HTTPPOST",
				method : "execute",
				body : {
					pdata : pdata,
					empiId :empiData.empiId
				}
			});
			MyMessageTip.msg("提示", "保存成功!", true);
		}else{
			MyMessageTip.msg("提示", "保存失败!", true);
		}	
	}
});