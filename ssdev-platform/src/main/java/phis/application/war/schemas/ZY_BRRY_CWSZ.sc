<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="ZY_BRRY"  alias="病人入院">
	<item id="ZYH" alias="住院号" type="long" display="0" length="18" not-null="1" generator="assigned" pkey="true"/>
	<item id="JGID" alias="机构ID" type="string" display="0" length="20" not-null="1"/>
	<item id="BRID" alias="病人ID" type="long" display="0" length="18" not-null="1"/>
	<item id="EMPIID" alias="病人ID" type="string" display="0" length="18" not-null="1"/>
	<item id="BRBQ" alias="病案病区" type="long" length="18" display="0"/>
	<item id="BAHM" alias="病案号码" length="10" display="0"/>
	<item id="ZYHM" alias="住院号" length="10"  not-null="1"/>
	<item id="BRCH" alias="床号" length="12"/>
	<item id="BRCH_SHOW" alias="显示床号" length="12"/>
	<item id="BRXM" alias="姓名" length="20" not-null="1"/>
	<item id="BRXB" alias="性别" type="int" length="4" not-null="1" fixed="true">
		<dic id="phis.dictionary.gender"/>
	</item>
	<item id="BRKS" alias="科室" type="long" length="18" not-null="1" fixed="true">
		<dic id="phis.dictionary.department"/>
	</item>
	<item id="BRXZ" alias="性质" type="long" length="18" not-null="1">
		<dic id="phis.dictionary.patientProperties_ZY"/>
	</item>
	<item id="ZSYS" alias="主任医师" length="10">
		<dic id="phis.dictionary.doctor" />
	</item>
	<item id="ZZYS" alias="主治医师" length="10"/>
	<item id="HLJB" alias="护理级别" type="string" length="4" />
	<item id="ZKZT" alias="转科状态" type="int" length="4" />
	<item id="BRQK" alias="病人情况" type="int" length="4" />
	<item id="YSDM" alias="饮食代码" length="12" display="0"/>
	<item id="CYPB" alias="病人状态" type="int" length="2" not-null="1" />
	<item id="RYRQ" alias="入院日期" type="date" not-null="1" />
	<item id="CSNY" alias="出身年月"  type="date"/>
	<item id="AGE" alias="年龄"  virtual="true"/>
	<item id="JBMC" alias="疾病名称"  virtual="true"/>
	<item id="FJHM" alias="房间号码" length="10" fixed="true"/>
	<item id="CWKS" alias="床位科室" type="long" length="18" not-null="1" fixed="true"/>
	<item id="KSDM" alias="床位病区" type="long" length="18" not-null="1" fixed="true"/>
	<item id="CWXB" alias="床位性别" type="int" length="4" not-null="1" fixed="true"/>
	<item id="CWFY" alias="床位费用" type="double" length="6" precision="2" not-null="1" fixed="true"/>
	<item id="ICU" alias="icu费用" type="double" length="8" precision="2" not-null="1" fixed="true"/>
	<item id="JCPB" alias="床位类别" type="int" length="1" not-null="1" dispaly="0" fixed="true"/>
	<item id="ZYH" alias="住院号" type="long" length="18" display="0"/>
	<item id="YEWYH" alias="婴儿唯一号" type="long" length="18" display="0"/>
	<item id="ZDYCW" alias="自定义床位" type="int" length="1" display="0"/>
	<item id="XKYZ" alias="新开医嘱"  virtual="true"/>
	<item id="XTYZ" alias="新停医嘱"  virtual="true"/>
	<item id="CYZ" alias="出院证"  virtual="true"/>
	<item id="GMYW" alias="过敏药物"  virtual="true"/>
	<item id="GMYW_SIGN" alias="过敏药物标志"  virtual="true"/>
	<item id="CYRQ" alias="出院日期"  virtual="true"/>
	<item id="RYRQS" alias="出院日期"  virtual="true"/>
	<item id="RYNL" alias="入院年龄"  virtual="true"/>
	<item id="JSCS" alias="结算次数"  virtual="true"/>
</entry>
