/**
 * 新生儿随访的记录列表
 * 
 * @author :zhouw
 */
$package("chis.application.cdh.script")

$import("chis.script.BizSimpleListView")

chis.application.cdh.script.newBorn.ToMHCBabyVisitRecordListView = function(cfg) {
	cfg.pageSize = -1;
	chis.application.cdh.script.newBorn.ToMHCBabyVisitRecordListView.superclass.constructor
			.apply(this, [cfg]);
	this.enableCnd = false;
	this.disablePagingTbr = true;

}

Ext.extend(chis.application.cdh.script.newBorn.ToMHCBabyVisitRecordListView,
		chis.script.BizSimpleListView, {
			loadData : function(data) {
				if (!data) {
					return;
				}
				this.requestData.cnd = this.requestData.cnd
						|| ["eq", ["$", "babyId"], ["s", data]];
				chis.application.cdh.script.newBorn.ToMHCBabyVisitRecordListView.superclass.loadData
						.call(this);
			},
			doImportIn : function() {
				var r = this.getSelectedRecord().data;// 选中随访的记录
				if (r == null) {
					return;
				}
				var v = this.reData;// 基本信息
				if (!v) {
					return;
				}
				// 组装数据：基本信息和随访记录
				// values["empiId"] = this.exContext.empiData.empiId;
				// values["babyBirth"] = this.exContext.empiData.birthday;
				// values["babySex"] = this.exContext.empiData.sexCode;
				// values["babyIdCard"] = this.exContext.empiData.idCard;
				// values["babyName"] = this.exContext.empiData.personName;
				var data = {};
				// data["babyIdCard"] = v[""];
				data["certificateNo"] = v["certificateNo"];
				data["babyAddress"] = v["babyAddress"];
				data["fatherName"] = v["fatherName"];
				data["fatherJob"] = v["fatherJob"];
				// alert(Ext.encode(v["fatherJob"]))
				data["fatherPhone"] = v["fatherPhone"];
				data["fatherBirth"] = v["fatherBirth"];
				data["motherName"] = v["motherName"];
				data["motherJob"] = v["motherJob"];
				data["motherPhone"] = v["motherPhone"];
				data["motherBirth"] = v["motherBirth"];
				data["gestation"] = v["gestation"];
				data["pregnancyDisease"] = v["pregnancyDisease"];
				data["otherDisease"] = v["otherDisease"];
				data["deliveryUnit"] = v["deliveryUnit"];
				// data["empiId"] = v[""];
				data["birthStatus"] = v["birthStatus"];
				data["otherStatus"] = v["otherStatus"];
				data["asphyxia"] = v["asphyxia"];
				data["apgar1"] = v[""];
				data["apgar5"] = v[""];
				data["apgarNew"] = v[""];
				data["malforMation"] = v["malforMation"];
				data["hearingTest"] = v["hearingTest"];
				data["illnessScreening"] = v["illnessScreening"];
				data["otherIllness"] = v["otherIllness"];
				data["weight"] = v["weight"];
				data["weightNow"] = r["weight"];
				data["length"] = v["length"];
				data["feedWay"] = r["feedWay"];
				data["eatNum"] = r["eatNum"];
				data["eatCount"] = r["eatCount"];
				data["vomit"] = r["vomit"];
				data["stoolStatus"] = r["stoolStatus"];
				data["stoolTimes"] = r["stoolTimes"];
				data["temperature"] = r["temperature"];
				data["pulse"] = r["pulse"];
				data["respiratoryFrequency"] = r["respiratoryFrequency"];
				data["face"] = r["face"];
				data["faceOther"] = r["faceOther"];
				data["jaundice"] = r["jaundice"];
				data["bregmaTransverse"] = r["bregmaTransverse"];
				data["bregmaLongitudinal"] = r["bregmaLongitudinal"];
				data["bregmaStatus"] = r["bregmaStatus"];
				data["otherStatus1"] = r["otherStatus"];
				data["eye"] = r["eye"];
				data["eyeAbnormal"] = r["eyeAbnormal"];
				data["limbs"] = r["limbs"];
				data["limbsAbnormal"] = r["limbsAbnormal"];
				data["ear"] = r["ear"];
				data["earAbnormal"] = r["earAbnormal"];
				data["neck"] = r["neck"];
				data["neck1"] = r["neck1"];
				data["nose"] = r["nose"];
				data["noseAbnormal"] = r["noseAbnormal"];
				data["skin"] = r["skin"];
				data["skinAbnormal"] = r["skinAbnormal"];
				data["mouse"] = r["mouse"];
				data["mouseAbnormal"] = r["mouseAbnormal"];
				data["anal"] = r["anal"];
				data["analAbnormal"] = r["analAbnormal"];
				data["heartlung"] = r["heartlung"];
				data["heartLungAbnormal"] = r["heartLungAbnormal"];
				data["genitalia"] = r["genitalia"];
				data["genitaliaAbnormal"] = r["genitaliaAbnormal"];
				data["abdominal"] = r["abdominal"];
				data["abdominalabnormal"] = r["abdominalabnormal"];
				data["spine"] = r["spine"];
				data["spineAbnormal"] = r["spineAbnormal"];
				data["umbilical"] = r["umbilical"];
				data["umbilicalOther"] = r["umbilicalOther"];
				data["referral"] = r["referral"];
				data["referralUnit"] = r["referralUnit"];
				data["referralReason"] = r["referralReason"];
				data["guide"] = r["guide"];
				data["visitDate"] = r["visitDate"];
				data["nextVisitAddress"] = r["nextVisitAddress"];
				data["nextVisitDate"] = r["nextVisitDate"];
				// data["visitDoctor"] = r["visitDoctor"];
				var doctorKey = {};
				doctorKey["visitDoctor"] = r["visitDoctor"];
				var doctorText = this.findText(doctorKey);
				Ext.apply(data, doctorText)
				this.fireEvent("importIn", data);
			},
			// 返回visitDoctor_text
			findText : function(data) {
				if (!data) {
					return;
				}
				// 可能会去数据库取数据
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.childrenHealthRecordService",
							serviceAction : "findInfo",
							method : "execute",
							body : data
						});
				var info = result.json.body;
				if (info) {
					return info;
				}

			}

		});