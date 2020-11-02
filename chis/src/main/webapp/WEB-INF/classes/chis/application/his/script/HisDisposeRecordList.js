$package("chis.application.his.script")

$import("chis.script.BizSimpleListView");
chis.application.his.script.HisDisposeRecordList = function(cfg) {
	cfg.showRowNumber = true;
	this.serverParams = {
		serviceAction : cfg.serviceAction
	}
	chis.application.his.script.HisDisposeRecordList.superclass.constructor
			.apply(this, [cfg]);
},

Ext.extend(chis.application.his.script.HisDisposeRecordList,
		chis.script.BizSimpleListView, {

			getCndBar : function(items) {
				var cndbars = [];
				var radioGroup = this.getRadioGroup();
				return cndbars.concat(radioGroup);
			},
			getRadioGroup : function() {
				var radio = [];
				var c3 = {
					boxLabel : "处置",
					inputValue : "3",
					name : "dispose",
					clearCls : true
				};
				radio.push(c3);
				var c1 = {
					boxLabel : "检验",
					inputValue : "1",
					name : "dispose",
					clearCls : true
				};
				radio.push(c1);
				var c2 = {
					boxLabel : "检查",
					inputValue : "2",
					name : "dispose",
					clearCls : true
				};
				radio.push(c2);
				var radioGroup = new Ext.form.RadioGroup({
							width : 180,
							disabled : false,
							allowBlank : false,
							value : "3",
							items : radio,
							listeners : {
								change : this.radioGroupChange,
								scope : this
							}
						});
				this.radioGroup = radioGroup;
				return radioGroup;
			},
			setActiveOne : function() {
				if(this.radioGroup){
					this.radioGroup.setValue("3");
				}
			},
			radioGroupChange : function(field, newValue, oldValue) {
				if (newValue == null) {
					return;
				}
				if (!this.recordIds||this.recordIds.length==0) {
					return;
				}
				if (newValue.inputValue == "3") {
					this.initCnd = this.requestData.cnd = [
							'and',
							['in', ['$', 'c.JZXH'],this.recordIds],
							['ne', ['$', 'c.DJLY'], ['s', '8']],
							['ne', ['$', 'c.DJLY'], ['s', '9']]];
				}
				if (newValue.inputValue == "2") {
					this.initCnd = this.requestData.cnd = [
							'and',
							['in', ['$', 'c.JZXH'],this.recordIds],
							['eq', ['$', 'c.DJLY'], ['s', '9']]];
				}
				if (newValue.inputValue == "1") {
					this.initCnd = this.requestData.cnd = [
							'and',
							['in', ['$', 'c.JZXH'],this.recordIds],
							['eq', ['$', 'c.DJLY'], ['s', '8']]];
				}
				this.loadData();
			}
		});