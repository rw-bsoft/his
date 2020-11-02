$package("chis.application.hy.script.first");

$import("chis.script.BizSimpleListView",
		"chis.application.hy.script.first.HypertensionFirstCheckForm",
		"chis.application.hy.script.first.HypertensionFirstForm",
		"chis.application.mpi.script.ExpertQuery");

chis.application.hy.script.first.HypertensionFirstCheckList = function(cfg) {
	cfg.serviceId = "chis.hypertensionFirstService";
	cfg.hypertensionFirstEntryName = "chis.application.hy.schemas.MDC_HypertensionFirst";
	cfg.hypertensionFirstDetialEntryName = "chis.application.hy.schemas.MDC_HypertensionFirstDetail";
	cfg.empiEntryName = "chis.application.mpi.schemas.MPI_DemographicInfo";
	chis.application.hy.script.first.HypertensionFirstCheckList.superclass.constructor
			.apply(this, [cfg]);
};

Ext.extend(chis.application.hy.script.first.HypertensionFirstCheckList,
		chis.script.BizSimpleListView, {

			doEstimate : function() {
				util.rmi.jsonRequest({
							serviceId : "chis.yearEstimateService",
							serviceAction : "yearEstimate",
							method:"execute",
							estimateType : "hypertension",
							schema : "chis.application.hy.schemas.MDC_HypertensionRecord",
							vistSchema : "chis.application.hy.schemas.MDC_HypertensionVisit",
							instanceType : "1"
						}, function(code, msg, json) {
							if (code > 300) {
								this.processReturnMsg(code, msg,
										this.doEstimate);
								return;
							}
							if (code == 200) {

							}
						}, this);
			},
			doCheck : function() {
				var r = this.getSelectedRecord();
				if (!r)
					return;
				var hyptFirstId = r.get("recordNumber");
				var empiId = r.get("empiId");
				var diagnosisType = r.get("diagnosisType");
				this.openCheckForm(hyptFirstId, empiId, diagnosisType);
			},
			doFirst : function(empiId) {
				if (!empiId)
					return
				var firstForm = this.midiModules["firstForm"];
				if (!firstForm) {
					var firstForm = new chis.application.hy.script.first.HypertensionFirstForm(
							{
								mainApp : this.mainApp
							});
					firstForm.on("save", function() {
								this.refresh();
							}, this);
					this.midiModules["firstForm"] = firstForm;
				}
				var win = firstForm.getWin();
				firstForm.setEmpiId(empiId);
				win.show();
			},
			openCheckForm : function(hyptFirstId, empiId, diagnosisType) {
				var checkForm = this.midiModules["checkForm"];
				if (!checkForm) {
					var checkForm = new chis.application.hy.script.first.HypertensionFirstCheckForm(
							{
								hypertensionFirstId : hyptFirstId,
								empiId : empiId,
								diagnosisType : diagnosisType,
								mainApp : this.mainApp
							});
					checkForm.on("save", function() {
								this.refresh();
							}, this);
					this.midiModules["checkForm"] = checkForm;
				} else {
					checkForm.setArgs(hyptFirstId, empiId,diagnosisType);
				}

				var win = checkForm.getWin();
				win.show();
			},
			doBrush : function() {
				var expertQuery = this.midiModules["EMPI.ExpertQuery"];
				if (!expertQuery) {
					expertQuery = new chis.application.mpi.script.ExpertQuery({
							// entryName:this.entryName,
							// height:450
							});
					expertQuery.on("empiSelected", function(empi) {
						if (!empi)
							return;
						var empiId = empi.get("empiId");
						this.empiId = empiId;
						util.rmi.jsonRequest({
							serviceId : this.serviceId,
							schema : this.hypertensionFirstDetialEntryName,
							empiId : empiId,
							empiSchema : this.empiEntryName,
							hypertensionFirstSchema : this.hypertensionFirstEntryName,
							serviceAction : "ifHypertensionFirst",
							method:"execute",
						}, function(code, msg, json) {
							if (code > 300) {
								this.processReturnMsg(code, msg, this.onBrush);
								return;
							}
							var hypertensionFirst = json["hypertensionFirst"];
							if (hypertensionFirst == "1") {
								this.doFirst(empiId);
								return;
							}
							var hypertensionFirstCheck = json["hypertensionFirstCheck"];
							if (hypertensionFirstCheck == "1") {
								var hypertensionFirstId = json["hypertensionFirstId"];
								this.openCheckForm(hypertensionFirstId, empiId);
							}
						}, this);

					}, this);
					this.midiModules["EMPI.ExpertQuery"] = expertQuery;
				}
				expertQuery.getWin().show();
			}
		});