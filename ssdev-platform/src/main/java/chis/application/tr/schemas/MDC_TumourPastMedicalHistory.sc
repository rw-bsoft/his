<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.tr.schemas.MDC_TumourPastMedicalHistory" alias="肿瘤既往史">
	<item id="TPMHID" alias="既往史编号" type="string" length="16" not-null="1" generator="assigned" pkey="true" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="empiId" alias="empiId" type="string" length="32" display="0"/>
	<item id="checkDate" alias="检查日期" type="date" not-null="true" maxValue="%server.date.date"/>
	<item id="checkType" alias="检查方式" type="string" length="1" not-null="true">
		<dic>
			<item key="1" text="B超"/>
			<item key="2" text="钡剂灌肠"/>
			<item key="3" text="CT"/>
			<item key="4" text="螺旋CT模拟肠境"/>
			<item key="5" text="MRI"/>
			<item key="6" text="PET"/>
		</dic>
	</item>
	<item id="reachPart" alias="到达部位" type="string" length="1" not-null="true" display="2">
		<dic>
			<item key="1" text="乙状结肠"/>
			<item key="2" text="回盲部"/>
		</dic>
	</item>
	<item id="lesionPart" alias="病变部位" type="string" length="2" display="2">
		<dic>
			<item key="1" text="无"/>
			<item key="2" text="阑尾"/>
			<item key="3" text="盲肠"/>
			<item key="4" text="升结肠"/>
			<item key="5" text="肝曲"/>
			<item key="6" text="横结肠"/>
			<item key="7" text="脾曲"/>
			<item key="8" text="降结肠"/>
		</dic>
	</item>
	<item id="distance" alias="直肠肿瘤距肛距离(cm)" type="int" length="4" display="2"/>
	<item id="tumourSize" alias="肿瘤大小(最大直径cm)" type="int" length="4" display="2"/>
	<item id="biopsy" alias="活检" type="string" length="1" display="2">
		<dic id="chis.dictionary.yesOrNo" />
	</item>
	<item id="biopsyResult" alias="活检结果" type="string" length="2" display="2">
		<dic>
			<item key="01" text="肠炎"/>
			<item key="02" text="炎性息肉"/>
			<item key="03" text="增生性息肉"/>
			<item key="04" text="错构瘤性息肉"/>
			<item key="05" text="管状腺瘤"/>
			<item key="06" text="绒毛状腺瘤"/>
			<item key="07" text="混合性腺瘤"/>
			<item key="08" text="伴中、重度异型增生的其它病变"/>
			<item key="09" text="证实为癌"/>
			<item key="99" text="其他"/>
		</dic>
	</item>
	<item id="withCancer" alias="伴癌变" type="string" length="1" display="2">
		<dic>
			<item key="1" text="是(低级别瘤变、高级别瘤变)"/>
			<item key="2" text="否"/>
		</dic>
	</item>
	<item id="polypsShape" alias="息肉形状" type="string" length="1" display="2">
		<dic>
			<item key="1" text="隆起形"/>
			<item key="2" text="扁平形"/>
			<item key="3" text="凹陷形"/>
		</dic>
	</item>
	<item id="pedicle" alias="有无蒂" type="string" length="1" display="2">
		<dic id="chis.dictionary.haveOrNot"/>
	</item>
	<item id="bleeding" alias="出血情况" type="string" length="1" display="2">
		<dic id="chis.dictionary.haveOrNot"/>
	</item>
	<item id="biopsyDate" alias="活检报告日期" type="date" display="2"/>
	<item id="adenocarcinomaProcessing" alias="腺癌处理" type="string" length="1" display="2">
		<dic>
			<item key="1" text="未处理"/>
			<item key="2" text="肠镜切除"/>
			<item key="3" text="手术切除"/>
			<item key="4" text="其他"/>
		</dic>
	</item>
	<item id="surgeryType" alias="手术方式" type="string" length="1" display="2">
		<dic>
			<item key="1" text="根治"/>
			<item key="2" text="姑息切除"/>
			<item key="3" text="其他"/>
		</dic>
	</item>
	<item id="auxiliaryTreatment" alias="辅助治疗方式" type="string" length="1" display="2">
		<dic>
			<item key="1" text="无"/>
			<item key="2" text="术前放化疗"/>
			<item key="3" text="术后放化疗"/>
			<item key="4" text="腹腔化疗"/>
			<item key="5" text="术后化疗"/>
		</dic>
	</item>
	<item id="malignantTumorTypes" alias="恶性肿瘤类型" type="string" length="2" display="2">
		<dic>
			<item key="01" text="高分化腺癌"/>
			<item key="02" text="中分化腺癌"/>
			<item key="03" text="低分化腺癌"/>
			<item key="04" text="粘液腺癌"/>
			<item key="05" text="印戒细胞癌"/>
			<item key="06" text="未分化癌"/>
			<item key="07" text="腺鳞癌"/>
			<item key="08" text="鳞状细胞癌"/>
			<item key="09" text="类癌"/>
			<item key="99" text="其他"/>
		</dic>
	</item>
	<item id="stagesGist" alias="分期依据" type="string" length="2" display="10">
		<dic>
			<item key="C" text="C(临床分期)"/>
			<item key="P" text="P(术后分期)"/>
			<item key="Y" text="Y(放疗后分期)"/>
		</dic>
	</item>
	<item id="T" alias="TNM分期(T)" type="string" length="2" display="10">
		<dic id="chis.dictionary.TumourT" />
	</item>
	<item id="N" alias="TNM分期(N)" type="string" length="2" display="10">
		<dic id="chis.dictionary.TumourN" />
	</item>
	<item id="M" alias="TNM分期(M)" type="string" length="2" display="10">
		<dic id="chis.dictionary.TumourM" />
	</item>
	<item id="surgeryUnit" alias="手术机构" type="string" length="20" display="2">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"  />
	</item>
	<item id="qualityControlCode" alias="质控流水号" type="string" length="20" display="2"/>
	<item id="qualityControlResult" alias="质控结果" type="string" length="2" display="2">
		<dic>
			<item key="1" text="合格"/>
			<item key="2" text="不合格"/>
		</dic>
	</item>
	<item id="radiotherapyUnit" alias="放疗机构" type="string" length="20" display="2">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" />
	</item>
	<item id="firstRemindTime" alias="第一次提醒" type="datetime" display="2"/>
	<item id="radiotherapyType" alias="方式1" type="string" length="50" display="2"/>
	<item id="chemotherapyUnit" alias="化疗机构" type="string" length="20" display="2">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"  />
	</item>
	<item id="SecondRemindTime" alias="第二次提醒" type="datetime" display="2"/>
	<item id="chemotherapyType" alias="方式2" type="string" length="50" display="2"/>
	
	<item id="createUser" alias="录入人" type="string" length="20" update="false"  defaultValue="%user.userId" fixed="true" display="0">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" keyNotUniquely="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="createUnit" alias="录入机构" type="string" length="20" update="false" width="320" defaultValue="%user.manageUnit.id" fixed="true" colspan="2" display="0">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="createDate" alias="录入时间" type="datetime"  update="false" defaultValue="%server.date.date" fixed="true"  maxValue="%server.date.date" display="0">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改单位" type="string" length="20"
		width="180" display="1" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" 
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		fixed="true" defaultValue="%user.userId" display="1">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" keyNotUniquely="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime" defaultValue="%server.date.date"
		fixed="true" display="1">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
</entry>
